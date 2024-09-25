package element;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import gui.*;
import observer.*;
import utils.Direction;
import utils.Point2D;

public class GameEngine implements Observer {

	public static final int GRID_HEIGHT = 10;
	public static final int GRID_WIDTH = 10;

	private ImageMatrixGUI gui;
	private List<ImageTile> tileList;
	private Fireman fireman;
	private FiremanBot bot;
	private List<Bulldozer> bulldozers;
	private List<FireTruck> firetrucks;

	private Bulldozer currentBulldozer;

	private FireTruck currentFireTruck;

	private Plane plane;
	private PrintWriter writer;
	private boolean inB = false;
	private boolean inT = false;
	private boolean planeCalled = false;
	private char[][] map = new char[GRID_WIDTH][GRID_HEIGHT];
	private String filename;
	private final String gameName = "Fireman - The Game";
	private List<String> levels = new ArrayList<>();
	private List<String> moreList;
	private List<Integer> fireColumns;
	private List<Fire> fireList;
	private int levelIndex = 0;
	private List<FiremanBot> bots;

	public GameEngine() throws IOException {
		levelFiles();
		setFilename(levels.get(levelIndex));

		gui = ImageMatrixGUI.getInstance();
		gui.setName(gameName);
		gui.setSize(GRID_HEIGHT, GRID_WIDTH);
		gui.registerObserver(this);
		gui.go();

		tileList = new ArrayList<>();
		bots = new ArrayList<>();

		bulldozers = new ArrayList<>();
		firetrucks = new ArrayList<>();
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void levelFiles() {
		// levels = new ArrayList<>(Arrays.asList("level1.txt", "level2.txt", "level3.txt", "level4.txt", "level5.txt", "level6.txt"));
		levels = new ArrayList<>(Arrays.asList("level2.txt", "level5.txt", "level6.txt"));
	}

	public void readFile() {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			for (int i = 0; i < GRID_HEIGHT; ++i) {
				String fileLine = sc.nextLine();
				for (int j = 0; j < GRID_WIDTH; ++j) {
					char data = fileLine.charAt(j);
					map[i][j] = data;
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public char getChar(int i, int j) {
		return map[i][j];
	}

	public void skipLines(Scanner s, int n) {
		for (int i = 0; i < n; i++) {
			if (s.hasNextLine())
				s.nextLine();
		}
	}

	public void readMoreFile() {
		moreList = new ArrayList<>();
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			skipLines(sc, GRID_HEIGHT);
			if (sc.hasNextLine()) {
				while (sc.hasNextLine()) {
					moreList.add(sc.nextLine());
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public GameElement convertToMore(String str, int x, int y) {
		switch (str) {
		case "Fireman":
			fireman = new Fireman(new Point2D(x, y));
			return fireman;
		case "Fire":
			return new Fire(new Point2D(x, y));
			case "Bulldozer":
				Bulldozer newBulldozer = new Bulldozer(new Point2D(x, y));
				bulldozers.add(newBulldozer); // Add to the bulldozer list
				return newBulldozer;
			case "FireTruck":
				FireTruck newFireTruck = new FireTruck(new Point2D(x, y));
				firetrucks.add(newFireTruck); // Add to the firetruck list
				return newFireTruck;
		case "FiremanBot":
			bot = new FiremanBot(new Point2D(x, y));
			bots.add(bot);
			return bot;
		}
		return null;
	}

	public GameElement convertToTerrain(int y, int x) {
		switch (getChar(y, x)) {
		case 'p':
			return new Pine(new Point2D(x, y));
		case 'e':
			return new Eucaliptus(new Point2D(x, y));
		case 'm':
			return new Grass(new Point2D(x, y));
		case '_':
			return new Land(new Point2D(x, y));
		case 'a':
			return new Abies(new Point2D(x, y));
		case 'b':
			return new FuelBarrel(new Point2D(x, y));
		}
		return null;
	}

	@Override
	public void update(Observed source) {
		gui.setStatusMessage("Level: " + (levelIndex + 1));

		int key = gui.keyPressed();

		try {
			win();
		} catch (IOException e) {
			e.printStackTrace();
		}

		handleKeyPress(key);
		turnOffWaters();
		flying();
		updateBurning();
		handleBotMovement();
		handleFiremanMovement(key);

		gui.update();
	}

	private void handleKeyPress(int key) {
		if (key == 0x1B) {
			end();
			System.exit(0);
		}

		if (key == KeyEvent.VK_ENTER) {
			handleEnterKey();
		}

		if (key == KeyEvent.VK_P && !planeCalled) {
			callPlane();
		}
	}

	private void handleEnterKey() {
		if (inB && currentBulldozer != null) {
			exitBulldozer();
		}

		if (inT && currentFireTruck != null) {
			exitFireTruck();
		}
	}

	private void exitFireTruck() {
		if (!inT || currentFireTruck == null) {
			return;
		}

		fireman.setPosition(currentFireTruck.getPosition());
		gui.addImage(fireman);
		tileList.add(fireman);
		inT = false;
		currentFireTruck = null;
	}

	private boolean canEnterBulldozer() {
		for (Bulldozer bd : bulldozers) {
			if (bd.getPosition().equals(fireman.getPosition())) {
				return true;
			}
		}
		return false;
	}

	private void callPlane() {
		planeCalled = true;
		plane = new Plane(new Point2D(fireColumn(), GRID_WIDTH + 1));
		gui.addImage(plane);
		tileList.add(plane);
	}

	private void handleBotMovement() {
		for (FiremanBot bot : bots) {
			Direction randDir = bot.getRandDir();
			Point2D newPosition = bot.getPosition().plus(randDir.asVector());

			if (!isElement(newPosition, "fire", 2)) {
				bot.move(randDir);
			} else {
				waterGun(randDir, newPosition);
				Fire fire = (Fire) getElement(newPosition, 2);
				gui.removeImage(fire);
				tileList.remove(fire);
			}
		}
	}

	private void handleFiremanMovement(int key) {
		if (!Direction.isDirection(key)) return;

		Direction direction = Direction.directionFor(key);
		Point2D newPosition = fireman.getPosition().plus(direction.asVector());

		setFireList();
		backdraft();

		if (!inB && !inT) {
			moveFireman(newPosition, direction);
		} else if (inB) {
			moveBulldozer(direction, currentBulldozer);
		} else if (inT && currentFireTruck != null && !firetrucks.isEmpty()) {
			moveFiretruck(direction, currentFireTruck);
		}
	}

	private void moveFireman(Point2D newPosition, Direction direction) {
		if (!isElement(newPosition, "fire", 2)) {
			fireman.move(direction);

			for (Bulldozer bd : bulldozers) {
				if (bd.getPosition().equals(newPosition) && canEnterBulldozer()) {
					enterBulldozer(bd);
					break;
				}
			}

			for (FireTruck truck : firetrucks) {
				if (truck.getPosition().equals(newPosition)) {
					enterFireTruck(truck);
					break;
				}
			}
		} else {
			extinguishFire(direction, newPosition);
		}
	}

		private void enterBulldozer(Bulldozer bd) {
			if (inB) {
				gui.setStatusMessage("Already in a bulldozer!");
				return;
			}

			gui.removeImage(fireman);
			tileList.remove(fireman);
			currentBulldozer = bd;
			inB = true;
		}

	private void exitBulldozer() {
		if (!inB || currentBulldozer == null) {
			return;
		}

		fireman.setPosition(currentBulldozer.getPosition());
		gui.addImage(fireman);
		tileList.add(fireman);
		inB = false;
		currentBulldozer = null;
	}

	private void enterFireTruck(FireTruck ft) {
		if (inT) {
			gui.setStatusMessage("Already in a fire truck!");
			return;
		}

		gui.removeImage(fireman);
		tileList.remove(fireman);
		currentFireTruck = ft;
		inT = true;
	}

	private void extinguishFire(Direction direction, Point2D newPosition) {
		waterGun(direction, newPosition);
		Fire fire = (Fire) getElement(newPosition, 2);
		gui.removeImage(fire);
		tileList.remove(fire);
	}

	private void moveBulldozer(Direction direction, Bulldozer bd) {
		Point2D newPosition2 = bd.getPosition().plus(direction.asVector());
		if (bd.canMoveTo(newPosition2) && !isElement(newPosition2, "fire", 2)) {
			bd.move(direction);
			transformIntoLand(newPosition2);
		}
	}

	private void moveFiretruck(Direction direction, FireTruck truck) {
		Point2D firetruckNewPosition = truck.getPosition().plus(direction.asVector());

		if (truck.canMoveTo(firetruckNewPosition)) {
			if (isElement(firetruckNewPosition, "fire", 2)) {
				extinguishFireAtPosition(direction, truck.getPosition());
				extinguishFiresBehindAndSides(truck.getPosition(), direction);
			} else {
				truck.move(direction);
			}
		}
	}

	public double getProb(ImageTile i) {
		if (i.getName() == "grass")
			return 0.15;
		else if (i.getName() == "eucaliptus")
			return 0.1;
		else if (i.getName() == "pine")
			return 0.05;
		else if (i.getName() == "abies")
			return 0.05;
		else if (i.getName() == "fuelbarrel")
			return 0.9;
		return 0;
	}

	public boolean willBurn(double d) {
		double e = Math.random();
		return e <= d && e > 0;
	}

	public void setFireList() {
		fireList = new ArrayList<Fire>();
		for (ImageTile it : tileList) {
			if (it.getName() == "fire")
				fireList.add((Fire) it);
		}
	}

	public void setFire(Point2D p) {
		Fire f = new Fire(p);
		gui.addImage(f);
		tileList.add(f);
	}

	public void backdraft() {
		for (Fire f1 : fireList) {
			List<Point2D> gps = f1.getPosition().getNeighbourhoodPoints();
			for (Point2D p : gps) {
				// Check if the fire can move to the position and that the fireman is not there
				if (f1.canMoveTo(p) && !p.equals(fireman.getPosition()) && !isElement(p, "fire", 2)) {
					// Check for burnable ImageTiles in layer 0
					ImageTile img = getElement(p, 0); // For burnable items

					// Check if there is a burnable ImageTile present at the position
					if (img != null && isBurnable(img)) {
						// For other burnable types
						if (willBurn(getProb(img))) {
							setFire(p); // Trigger fire for other burnable types
						}
					} else {
						// If no burnable item is found, check for FuelBarrel in layer 1
						ImageTile fuelBarrelImg = getElement(p, 1); // For FuelBarrel

						if (fuelBarrelImg instanceof FuelBarrel) {
							FuelBarrel barrel = (FuelBarrel) fuelBarrelImg;
							// Check if the barrel is not burnt and if it will burn
							if (!barrel.getIsBurnt() && willBurn(getProb(barrel))) {
								setFire(p); // Trigger fire if not burnt
							}
						}
					}
				}
			}
		}
	}




	public boolean isBurnable(ImageTile it) {
		if (it instanceof FuelBarrel) {
			FuelBarrel barrel = (FuelBarrel) it;
			return !barrel.getIsBurnt(); // Don't burn if already burnt
		}
		// Check other burnable types
		return (it.getName() == "grass" || it.getName() == "eucaliptus" ||
				it.getName() == "pine" || it.getName() == "abies");
	}


	public void transformIntoLand(Point2D p) {
		if (getElement(p, 0).getName() != "land") {
			Land land = new Land(p);

			GameElement toRemove1 = getElement(p, 1);
			GameElement toRemove0 = getElement(p, 0);

			if (toRemove1 != null) {
				tileList.remove(toRemove1);
				gui.removeImage(toRemove1);
			}

			if (toRemove0 != null) {
				tileList.remove(toRemove0);
				gui.removeImage(toRemove0);
			}

			tileList.add(land);
			gui.addImage(land);
		}
	}


	private void extinguishFiresBehindAndSides(Point2D position, Direction direction) {
		List<Point2D> neighbourPoints = position.getNeighbourhoodPoints();

		for(Point2D point : neighbourPoints) {
			extinguishFireAtPosition(direction, point);
		}
	}

	private void extinguishFireAtPosition(Direction direction, Point2D position) {
		if (isElement(position, "fire", 2)) {
			Fire fire = (Fire) getElement(position, 2);
			waterGun(direction, position);
			gui.removeImage(fire);
			tileList.remove(fire);
		}
	}

	public void waterGun(Direction direction, Point2D p) {
		Water water = new Water(p);
		gui.addImage(water);
		tileList.add(water);
		water.setWater(true);
		water.waterHose(direction);
	}

	public void turnOffWaters() {
		for (int i = 0; i < tileList.size(); i++) {
			ImageTile it = tileList.get(i);
			if (it.getName() == "water_up" || it.getName() == "water_down" || it.getName() == "water_left"
					|| it.getName() == "water_right") {
				Water w = (Water) it;
				if (w.getWater() == true) {
					tileList.remove(w);
					gui.removeImage(w);
				}
			}
		}
	}

	public void flying() {
		if (planeCalled == true) {

			plane.move(Direction.UP);
			plane.move(Direction.UP);

			if (isElement(plane.getPosition(), "fire", 2) == true) {
				waterGun(Direction.DOWN, plane.getPosition());
				gui.removeImage((Fire) getElement(plane.getPosition(), 2));
				tileList.remove((Fire) getElement(plane.getPosition(), 2));
			}

			if (plane.inEdge()) {
				planeCalled = false;
				gui.removeImage(plane);
				tileList.remove(plane);
			}
		}
	}

	public GameElement getElement(Point2D p, int layer) {
		GameElement elem = null;
		for (ImageTile it : tileList) {
			if (it.getPosition().equals(p) && it.getLayer() == layer)
				elem = (GameElement) it;
		}
		return elem;
	}

	public void updateBurning() {
		for (ImageTile it : tileList) {
			if (isElement(it.getPosition(), "explosion", 1)) {
				Burnable b = (Burnable) getElement(it.getPosition(), 1);
				b.burned();
			} else if (isBurnable(it) && isElement(it.getPosition(), "fire", 2)) {
				if (it instanceof FuelBarrel) {
					FuelBarrel fuelBarrel = (FuelBarrel) it;
					fuelBarrel.burning();
					if (fuelBarrel.getIsBurnt()) {
						fuelBarrel.burned();
					}
				} else {
					Burnable b = (Burnable) it;
					b.burning();
					if (b.getIsBurnt()) {
						b.burned();
					}
				}
			}
		}
	}

	public int fireColumn() {
		fireColumns = new ArrayList<>();
		for (ImageTile it : tileList) {
			if (it.getName() == "fire") {
				fireColumns.add(it.getPosition().getX());
			}
		}
		return getPopularElement(fireColumns);
	}

	public int getPopularElement(List<Integer> a) {
		int count = 1, n;
		int popular = a.get(0);
		int temp = 0;
		for (int i = 0; i < (a.size() - 1); i++) {
			temp = a.get(i);
			n = 0;
			for (int j = 1; j < a.size(); j++) {
				if (temp == a.get(j))
					n++;
			}
			if (n > count) {
				popular = temp;
				count = n;
			}
		}
		return popular;
	}

	public boolean isElement(Point2D p, String name, int layer) {
		for (ImageTile it : tileList) {
			if (it.getName() == name && it.getPosition().getX() == p.getX() && it.getPosition().getY() == p.getY()
					&& it.getLayer() == layer) {
				return true;
			}
		}
		return false;
	}

	public boolean allFireCeased() {
		for (ImageTile it : tileList) {
			if (isElement(it.getPosition(), "fire", 2))
				return false;
		}
		return true;
	}

	public boolean inBulldozer(Point2D p) {
		for (Bulldozer bd : bulldozers) {
			if (bd.getPosition().equals(p)) {
				return true; // Fireman is in a bulldozer
			}
		}
		return false;
	}

	public boolean inFiretruck(Point2D p) {
		for (FireTruck truck : firetrucks) {
			if (truck.getPosition().equals(p)) {
				return true; // Fireman is in a firetruck
			}
		}
		return false;
	}

	public void end() {
		gui.setMessage("GAME ENDED");
		System.exit(0);
	}

	public void win() throws IOException {
		if (allFireCeased()) {
			gui.setMessage("Map " + (levelIndex + 1) + " Finished! ");

			gameFinished();
			levelIndex++;

			setFilename(levels.get(levelIndex));
			gui.clearImages();
			tileList.clear();

			bot = null;
			start();
		}
	}

	public void gameFinished() {
		if (levelIndex + 1 == levels.size()) {
			gui.setMessage("GAME FINISHED! CONGRATULATIONS");
			System.exit(0);
		}
	}

	public void start() throws IOException {
		createTerrain();
		createMoreStuff();
		sendImagesToGUI();
	}

	private void createTerrain() throws IOException {
		readFile();
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_HEIGHT; x++) {
				GameElement ge = convertToTerrain(y, x);
				tileList.add(ge);
			}
		}
	}

	private void createMoreStuff() {
		readMoreFile();
		for (String str : moreList) {
			String[] content = str.split(" ");
			GameElement ge = convertToMore(content[0], Integer.valueOf(content[1]), Integer.valueOf(content[2]));
			tileList.add(ge);
		}
	}

	private void sendImagesToGUI() {
		gui.addImages(tileList);
	}
}