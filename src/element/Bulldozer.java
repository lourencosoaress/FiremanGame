package element;

import utils.Direction;
import utils.Point2D;

public class Bulldozer extends GameElement implements Movable {

	private String name = "bulldozer";
	private boolean occupied;

	public Bulldozer(Point2D p) {
		super(p);
		this.occupied = false;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public Point2D getPosition() {
		return super.getPosition();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getLayer() {
		return 4;
	}

	public void setName(String name){
		this.name = name;
	}

	@Override
	public void move(Direction direction) {
		boolean hasMoved = false;

		while (hasMoved == false) {
			Point2D newPosition = getPosition().plus(direction.asVector());
			if (canMoveTo(newPosition)) {
				switch (direction) {
				case UP:
					setName("bulldozer_up");
					break;
				case DOWN:
					setName("bulldozer_down");
					break;
				case LEFT:
					setName("bulldozer_left");
					break;
				case RIGHT:
					setName("bulldozer_right");
					break;
				}
				setPosition(newPosition);
				hasMoved = true;
			} else break;
		}
	}
}
