package pl.agngor.graingrowth;

import java.util.Random;

class pointState { //pixel is or is not a grain
	int act = 0;
	int prev = 0;

	int R = 250;
	int G = 250;
	int B = 250;
	pointState() {
	}

	pointState(pointState pointState) {
		this.prev = pointState.prev;
		this.act = pointState.act;
		this.R = pointState.R;
		this.G = pointState.G;
		this.B = pointState.B;
	}
	pointState(int act, int prev, int integerRGB)
	{
		this.act = act;
		this.prev = prev;

		loadRGB(integerRGB);
	}


	void set(int x) {
		this.prev = x;
		this.act = x;


		this.R = 250;
		this.G = 250;
		this.B = 250;
	}

	void set_random(int x) {
		this.set(x);

		Random r = new Random();

		this.R = r.nextInt(250);
		this.G = r.nextInt(250);
		this.B = r.nextInt(250);
	}

	void recolor(pointState pointState) {
		this.R = pointState.R;
		this.G = pointState.G;
		this.B = pointState.B;
	}

	boolean colorMatch(pointState pointState) {
		return (this.R != 250 || this.G != 250 || this.B != 250) && this.R == pointState.R && this.G == pointState.G && this.B == pointState.B;
	}
	Integer color2int() { //RGB to integer
		int rgb;
		rgb = this.R;
		rgb = (rgb << 8) + this.G;
		rgb = (rgb << 8) + this.B;
		return rgb;
	}
	private void loadRGB(Integer _rgb) {
		int rgb = _rgb;
		this.R = (rgb >> 16) & 0xFF;
		this.G = (rgb >> 8) & 0xFF;
		this.B = rgb & 0xFF;
	}
}
