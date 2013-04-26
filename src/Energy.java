
public class Energy {
	/**node����q*/
	private double power;
	/**�HRc1���ǿ�Z���ɡA�ǿ�1bit�һݪ���q*/
	private double transmitPower1;
	/**�HRc2���ǿ�Z���ɡA�ǿ�1bit�һݪ���q*/
	private double transmitPower2;
	/**����1bit�һݪ���q*/
	private double hearing = 50.0 / 1000000000.0;
	private double efs = 10.0 / 1000000000000.0;
	private double etr = 0.0013 / 1000000000000.0;
	//87.7058
	private double d0 = Math.sqrt(10.0 / 0.0013);
	public ARCNode owner;
	
	/**
	 *����node����l��q�A��Rc1 Rc2
	 *��l�ƨ�HRc1 �� Rc2���ǿ��q
	 */
	Energy(double inPower,double Rc1,double Rc2){
		power = inPower;
		if(Rc1 < d0)
			transmitPower1 = hearing + Rc1 * Rc1 * efs;
		else
			transmitPower1 = hearing + Rc1 * Rc1 * Rc1 * Rc1 * etr;
		if(Rc2 < d0)
			transmitPower2 = hearing + Rc2 * Rc2 * efs;
		else
			transmitPower2 = hearing + Rc2 * Rc2 * Rc2 * Rc2 * etr;
	}
	
	public void hear(int bit){
		power -= bit * hearing; 
	}
	
	public void transmitWithP1(int bit){
		power -= bit * transmitPower1;
	}
	
	public void transmitWithP2(int bit){
		power -= bit * transmitPower2;
	}
	
	/**
	 * �^��TRUE���٬��ۡA
	 * �^��FALSE���`�I�w���`
	 */
	public boolean checkPower(int bit){
		if(this.power < bit * this.transmitPower2){
			//this.owner.nodeState = ARCNode.DEAD;
			return false;
		}
		return true;
	}
	
	public double getPower(){
		return power;
	}
}
