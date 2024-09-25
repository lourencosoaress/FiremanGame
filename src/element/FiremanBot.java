package element;

import utils.Direction;
import utils.Point2D;

public class FiremanBot extends GameElement implements Movable {

	private String name = "firemanbot";

	public FiremanBot(Point2D position) {
		super(position);
	}

	public Point2D getPosition() {
		return super.getPosition();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Direction getRandDir() {
		Direction randDir = Direction.random();
		return randDir;
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
					setName("firemanbot_left");
					break;
				case RIGHT:
					setName("firemanbot_right");
					break;
				}
				setPosition(newPosition);
				hasMoved = true;
			} else
				break;
		}
	}

	@Override
	public int getLayer() {
		return 4;
	}
}
