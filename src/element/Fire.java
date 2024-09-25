package element;

import utils.Direction;
import utils.Point2D;

public class Fire extends GameElement implements Movable {

	public Fire(Point2D p) {
		super(p);
	}

	public Point2D getPosition() {
		return super.getPosition();
	}

	@Override
	public String getName() {
		return "fire";
	}

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public void move(Direction direction) {
	}
}
