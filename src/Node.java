
public class Node {
	
	protected double positionX;
	protected double positionY;
	protected double sensingRange;
	protected double innerClusterRange;
	protected double interClusterRange;
	
	protected int serialNumber;
	
	protected Energy power;
	/**Node的建構子 需要輸入 X Y座標 感測距離 叢集內溝通距離 叢集間溝通距離 初始能量 序列號*/
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
