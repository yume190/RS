
public class Sink {
	
	private double positionX;
	private double positionY;
	private double Rc1;
	private double Rc2;
	
	public ARCNode ALLNodes[];
	public ARCNode neighborNodeWithRc1[];
	public ARCNode neighborNodeWithRc2[];
	
	Sink(double inRc1,double inRc2){
		setPositionX(0.0);
		setPositionY(0.0);
		Rc1 = inRc1;
		Rc2 = inRc2;
	}

	private void setPositionX(double positionX) {
		this.positionX = positionX;
	}

	public double getPositionX() {
		return positionX;
	}

	private void setPositionY(double positionY) {
		this.positionY = positionY;
	}

	public double getPositionY() {
		return positionY;
	}
	
	/**發送PHASE 1 開始訊息給所有nodes*/
	public void CHNSelection(){
		Message m = transmit(Message.SINK,Message.BROADCAST,Message.PHASE1,Message.SINKCHANNEL,Message.ALL);
		for (int i = 0;i < ALLNodes.length;i++){
			ALLNodes[i].receive(m);
		}
	}
	/**發送PHASE 2 開始訊息給所有nodes*/
	public void routeSetup(){
		Message m = transmit(Message.SINK,Message.BROADCAST,Message.PHASE2,Message.SINKCHANNEL,Message.ALL);
		for (int i = 0;i < ALLNodes.length;i++){
			ALLNodes[i].receive(m);
		}
	}
	/**發送PHASE 3 開始訊息給所有nodes*/
	public void NCHNActivation(){
		Message m = transmit(Message.SINK,Message.BROADCAST,Message.PHASE3,Message.SINKCHANNEL,Message.ALL);
		for (int i = 0;i < ALLNodes.length;i++){
			ALLNodes[i].receive(m);
		}
	}
	/**發送PHASE 4 開始訊息給所有nodes*/
	public void steadyPhase(){
		Message m = transmit(Message.SINK,Message.BROADCAST,Message.PHASE4,Message.SINKCHANNEL,Message.ALL);
		for (int i = 0;i < ALLNodes.length;i++){
			ALLNodes[i].receive(m);
		}
	}
	/**發送HOP給距離RC2的所有節點*/
	public void HOP(){
		Message m = transmit(Message.SINK,Message.BROADCAST,Message.HOP,Message.PRIMARYCHANNEL,Message.RC2);
		for (int i = 0;i < this.neighborNodeWithRc2.length;i++){
			this.neighborNodeWithRc2[i].receive(m);
		}
	}
	private Message transmit(int s,int d,String command,int channel,String range){
		return new Message(s,d,command,channel,range);
	}
	/**搜尋鄰近距離RC1 RC2的所有節點
	  *並分別記錄於 neighborNodeWithRc1 neighborNodeWithRc2
	  */
	public void searchNeighborNode(){
		
		int count11 = 0, count12 = 0, count21 = 0, count22= 0;
		
		for(int i=0;i<ALLNodes.length;i++){
			if(Math.pow(ALLNodes[i].getPositionX(),2)+Math.pow(ALLNodes[i].getPositionY(),2)<=Math.pow(Rc1,2))
				count11++;
			if(Math.pow(ALLNodes[i].getPositionX(),2)+Math.pow(ALLNodes[i].getPositionY(),2)<=Math.pow(Rc2,2))
				count21++;
		}
		
		neighborNodeWithRc1 = new ARCNode[count11];
		neighborNodeWithRc2 = new ARCNode[count21];
		
		for(int i=0;i<ALLNodes.length;i++){
			if(Math.pow(ALLNodes[i].getPositionX(),2)+Math.pow(ALLNodes[i].getPositionY(),2)<=Math.pow(Rc1,2))
				neighborNodeWithRc1[count12++] = ALLNodes[i];
			if(Math.pow(ALLNodes[i].getPositionX(),2)+Math.pow(ALLNodes[i].getPositionY(),2)<=Math.pow(Rc2,2))
				neighborNodeWithRc2[count22++] = ALLNodes[i];
		}
	}
	
}
