package element;

import utils.Direction;
import utils.Point2D;

public class Fireman extends GameElement implements Movable {

	private String name = "fireman";

	public Fireman(Point2D position) {
		super(position);
	}

	public Point2D getPosition() {
		return super.getPosition();
	}

	@Override
	public void move(Direction direction) {
		boolean hasMoved = false;

		while (hasMoved == false) {
			Point2D newPosition = getPosition().plus(direction.asVector());
			if (canMoveTo(newPosition)) {
				switch (direction) {
					case LEFT:
						setName("fireman_left");
						break;
					case RIGHT:
						setName("fireman_right");
						break;
				}
				setPosition(newPosition);
				hasMoved = true;
			} else
				break;
		}
	}

	public void setPosition(Point2D position) {
		super.setPosition(position);
	}

	@Override
	public int getLayer() {
		return 4;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}
}
