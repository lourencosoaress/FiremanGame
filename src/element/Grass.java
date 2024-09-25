package element;

import utils.Point2D;

public class Grass extends GameElement implements Burnable {

	private String name = "grass";
	private int time = 3;
	private boolean isBurnt = false;

	// Mato Rasteiro
	public Grass(Point2D p) {
		super(p);
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean getIsBurnt() {
		return isBurnt;
	}

	@Override
	public int getLayer() {
		return 0;
	}

	@Override
	public void burned() {
		if (isBurnt == true)
			name = "burntgrass";
	}

	@Override
	public int getTime() {
		return time;
	}

	@Override
	public void setTime(int time){
		this.time = time;
	}
	
	@Override
	public void burning() {
		setTime(time - 1);
		if (getTime() == 0)
			isBurnt = true;
		burned();
	}
}
