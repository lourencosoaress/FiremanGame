package element;

import utils.Direction;
import utils.Point2D;

public class Plane extends GameElement implements Movable {

	public Plane(Point2D position) {
		super(position);
	}

	@Override
	public void move(Direction direction) {
		Point2D newPosition = getPosition().plus(direction.asVector());
		setPosition(newPosition);
	}

	public Point2D getPosition() {
		return super.getPosition();
	}

	public boolean inEdge() {
		return getPosition().getY() <= 0;
	}

	@Override
	public String getName() {
		return "plane";
	}

	@Override
	public int getLayer() {
		return 5;
	}

}
