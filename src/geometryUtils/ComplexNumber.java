package geometryUtils;

public class ComplexNumber {
	
	public double realPart;
	public double imgPart;
	
	public ComplexNumber(double realPart, double imgPart) {
		this.realPart = realPart;
		this.imgPart = imgPart;
	}
	public ComplexNumber() {
		this.realPart = 0;
		this.imgPart = 0;
	}
	
	public ComplexNumber add(ComplexNumber with) {
		return new ComplexNumber(realPart + with.realPart, imgPart + with.imgPart);
	}
	public ComplexNumber times(ComplexNumber with) {
		double real = this.realPart*with.realPart - this.imgPart*with.imgPart;
		double img = this.realPart*with.imgPart + this.imgPart*with.realPart;
		return new ComplexNumber(real, img);
	}
	public double getModule() {
		return Math.sqrt(realPart*realPart + imgPart*imgPart);
	}
	public String toString() {
		String answer =  "" + realPart + " ";
		if(imgPart>=0)
			answer += "+";
		answer += imgPart +"i";
		return answer;
	}

}
