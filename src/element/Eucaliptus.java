package element;

import utils.Point2D;

public class Eucaliptus extends GameElement implements Burnable {

	private String name = "eucaliptus";
	private int time = 5;
	private boolean isBurnt = false;

	public Eucaliptus(Point2D p) {
		super(p);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getLayer() {
		return 0;
	}
	
	@Override
	public boolean getIsBurnt() {
		return isBurnt;
	}

	@Override
	public void burned() {
		if (isBurnt == true)
			name = "burnteucaliptus";
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
