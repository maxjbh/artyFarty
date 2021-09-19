
public class Angle {
	//Simple class for angles. By default we'll take degrees.
	private Unit unit;
	private double size;
	/*
	 * Creates an angle in degrees, worth 0.
	 */
	public Angle() {
		unit = Unit.DEG;
		this.size = 0;
	}
	/*
	 * Creates an angle in degrees.
	 */
	public Angle(double size) {
		unit = Unit.DEG;
		this.size = size;
	}
	/*
	 * Do Angle.Unit.(DEG or RAD) to fetch a unit type.
	 */
	public Angle(Unit unitType, double size) {
		unit = unitType;
		this.size = size;
	}
	public enum Unit{
		RAD, DEG;
	}
	/* 
	 * Converts the angle to given unit type, does nothing if unit is unchanged.
	 */
	public Angle convertTo(Unit unitType) {
		Angle answer = new Angle(unit, size);
		if(!(unit.equals(unitType))) {
			if(unit.equals(Unit.DEG)) {
				answer.size = (size*Math.PI)/180.00;
				answer.unit = Unit.RAD;
			}
			else {
				answer.size = (size*180.00)/Math.PI;
				answer.unit = Unit.DEG;
			}
		}
		return answer;
	}
	/*
	 * Return the size of the angle
	 */
	public double gSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}

}
