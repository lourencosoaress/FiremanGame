package element;

import gui.ImageTile;
import utils.Point2D;

public abstract class GameElement implements ImageTile {

	// Layers:
	// 0: Land, Pine, Grass, Eucaliptus, Abies
	// 1: FuelBarrel
	// 2: Fire
	// 3: Water
	// 4: Fireman, Bulldozer, FireTruck, FiremanBot
	// 5: Plane
	private Point2D position;

	public GameElement(Point2D position) {
		this.position = position;
	}

	public Point2D getPosition() {
		return position;
	}

	protected void setPosition(Point2D position) {
		this.position = position;
	}
}