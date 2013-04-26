
public class Energy {
	/**node的能量*/
	private double power;
	/**以Rc1為傳輸距離時，傳輸1bit所需的能量*/
	private double transmitPower1;
	/**以Rc2為傳輸距離時，傳輸1bit所需的能量*/
	private double transmitPower2;
	/**接收1bit所需的能量*/
	private double hearing = 50.0 / 1000000000.0;
	private double efs = 10.0 / 1000000000000.0;
	private double etr = 0.0013 / 1000000000000.0;
	//87.7058
	private double d0 = Math.sqrt(10.0 / 0.0013);
	public ARCNode owner;
	
	/**
	 *給予node的初始能量，及Rc1 Rc2
	 *初始化其以Rc1 或 Rc2的傳輸能量
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
	 * 回傳TRUE為還活著，
	 * 回傳FALSE為節點已死亡
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
