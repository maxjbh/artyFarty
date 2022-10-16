package geometryUtils;

public class Coord2Ddouble {
	private double x;
	private double y;
	/*
	 * By default 0,0.
	 */
	public Coord2Ddouble() {
		this.x = 0;
		this.y = 0;
	}
	public Coord2Ddouble(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public double gX() {
		return x;
	}
	public double gY() {
		return y;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
}
