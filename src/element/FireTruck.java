package element;

import utils.Direction;
import utils.Point2D;

public class FireTruck extends GameElement implements Movable {

	private String name = "firetruck";

	public FireTruck(Point2D position) {
		super(position);
	}

	@Override
	public void move(Direction direction) {
		boolean hasMoved = false;

		while (hasMoved == false) {
			Point2D newPosition = getPosition().plus(direction.asVector());
			if (canMoveTo(newPosition)) {
				switch (direction) {
				case UP:
					break;
				case DOWN:
					break;
				case LEFT:
					setName("firetruck_left");
					break;
				case RIGHT:
					setName("firetruck_right");
					break;
				}
				setPosition(newPosition);
				hasMoved = true;
			} else
				break;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point2D getPosition() {
		return super.getPosition();
	}

	@Override
	public int getLayer() {
		return 4;
	}

}
