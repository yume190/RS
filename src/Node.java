
public class Node {
	
	protected double positionX;
	protected double positionY;
	protected double sensingRange;
	protected double innerClusterRange;
	protected double interClusterRange;
	
	protected int serialNumber;
	
	protected Energy power;
	/**Node���غc�l �ݭn��J X Y�y�� �P���Z�� �O�������q�Z�� �O�������q�Z�� ��l��q �ǦC��*/
	Node(double inPositionX,
			double inPositionY,
			double inSensingRange,
			double inInnerClusterRange,
			double inInterClusterRange,
			double inPower,
			int inSerialNumber
			){
		positionX = inPositionX;
		positionY = inPositionY;
		sensingRange = inSensingRange;
		innerClusterRange = inInnerClusterRange;
		interClusterRange = inInterClusterRange;
		serialNumber = inSerialNumber;
	
		power = new Energy(inPower,this.innerClusterRange,this.interClusterRange);
	}	
	
	public double getPositionX(){
		return positionX;
	}
	
	public double getPositionY(){
		return positionY;
	}
	
}
