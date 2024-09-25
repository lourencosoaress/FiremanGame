package element;

import utils.Direction;
import utils.Point2D;

public class Water extends GameElement {

	private String name = "water_down";
	private boolean waterOn = false;

	public Water(Point2D position) {
		super(position);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWater(boolean waterOn) {
		this.waterOn = waterOn;
	}

	public boolean getWater() {
		return waterOn;
	}

	public void waterHose(Direction direction) {
		switch (direction) {
		case UP:
			setName("water_up");
			break;
		case DOWN:
			setName("water_down");
			break;
		case LEFT:
			setName("water_left");
			break;
		case RIGHT:
			setName("water_right");
			break;
		}
	}

	@Override
	public int getLayer() {
		return 3;
	}
}
