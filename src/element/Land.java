package element;

import utils.Point2D;

public class Land extends GameElement {

	public Land(Point2D position) {
		super(position);
	}
	
	@Override
	public String getName() {
		return "land";
	}

	@Override
	public int getLayer() {
		return 0;
	}
}
