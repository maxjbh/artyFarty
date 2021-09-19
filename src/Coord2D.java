

public class Coord2D {
	private int x;
	private int y;
	/*
	 * By default 0,0.
	 */
	public Coord2D() {
		this.x = 0;
		this.y = 0;
	}
	public Coord2D(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int gX() {
		return x;
	}
	public int gY() {
		return y;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	//Requires Angle class.
	//Fetches coordinate at alpha , distance pixels away from this point.
	public Coord2D coordsAt(Angle alpha, int distance) {
		Angle temp = alpha.convertTo(Angle.Unit.RAD);
		return new Coord2D(x + (int)(Math.cos(temp.gSize())*distance), y - (int)(Math.sin(temp.gSize())*distance));
	}
	//Returns the coords of this point rotated around pivot by alpha clockwise.
	public Coord2D rotateAround(Coord2D pivot, Angle alpha) {
		int xDiff = pivot.gX() - x;
		int yDiff = pivot.gY() - y;
		
		//Coeficiants
		int xx = -1;
		int xy = 1;
		int yx;
		int yy;
		if(xDiff>=0)
			xx = 1;
		if(yDiff>=0)
			xy = -1;
		if(xx==-1) {
			if(xy==-1) {
				yx=-1; yy=1;
			}else {
				yx=1; yy=1;
			}
		}else {
			if(xy==-1) {
				yx=-1; yy=-1;
			}else {
				yx=1; yy=-1;
			}
		}
		
		//angles
		double xDiffBis = (double)xDiff;
		if(xDiff==0)
			xDiffBis = 0.1;
		Angle beta = new Angle(Angle.Unit.RAD, Math.tan((double)(yDiff/xDiffBis)) );
		beta.convertTo(Angle.Unit.DEG);
		Angle gamma = new Angle(90-beta.gSize());
		alpha.convertTo(Angle.Unit.DEG);
		
		//r
		double r = Math.sqrt((double)(xDiff*xDiff+yDiff*yDiff));
		
		//result's x and y in double type
		double answerX = r*
				(
						-1.0*Math.cos(alpha.gSize())*(double)(xx)*Math.cos(beta.gSize())
					+	Math.sin(alpha.gSize())*(double)(yx)*Math.cos(gamma.gSize())        );
		double answerY = r*
				(
						-1.0*Math.cos(alpha.gSize())*(double)(xy)*Math.sin(beta.gSize())
					+	Math.sin(alpha.gSize())*(double)(yy)*Math.sin(gamma.gSize())        );
		return new Coord2D((int)answerX, (int)answerY);
	}

}
