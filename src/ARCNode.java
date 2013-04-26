
public class ARCNode extends Node implements Comparable<Object>{
	
	int turn = 1;
	
	/** 
	 * 代表節點的狀態
	 */
	protected int nodeState=0;
	public static final int NODE = 0;
	public static final int CHN = 1;
	public static final int NCHN = 2;
	public static final int ACTIVE = 3;
	public static final int SLEEP = 4;
	public static final int DEAD = 5;
	
	/**
	 * 紀錄目前的PHASE
	 */
	protected int phase;
	public static final int CHNSELECTION = 1;
	public static final int ROUTESETUP = 2;
	public static final int NCHNACTIVATION = 3;
	public static final int STEADYPAHSE = 4;
	
	/**目前回合數*/
	protected int currentRound;
	/**執行過幾次回合數*/
	protected int executeRound=0;
	/**當過幾次CHN回合數*/
	protected int executeCHNRound=0;
	
	
	/**叢集首收到的節點數*/
	protected int clusterNodeAmount;	
	
	/**要求的覆蓋率*/
	protected double requireCoverage;
	protected double L;
	protected double W;
	protected int N;
	protected int bit;
	
	protected double Km;
	protected double C1;
	protected double Nh;
	
	protected int T1=7;
	protected int T2=3;
	protected double backofftime;
	/**發送HOP封包的時間*/
	protected double deliverHOPTime;
	protected double valueForCompare;
	protected double Tb = 2.0;
	
	/**給CHN用，收到PHASE 1封包時，會做初始化。
	 * true 表示此Channel可用
	 */
	protected boolean[] channels = new boolean[20];
	
	
	/**到達SINK的HOP COUNT數*/
	protected int hopCount;
	/**steady phase 使用的時間槽索引*/
	protected int indexOfTimeSlot;
	/**steady phase 使用的頻道*/
	protected int clusterChannel;
	
	
	/**與此節點距離Rc1的鄰近節點*/
	protected ARCNode neighborNodeWithRc1[];
	/**與此節點距離Rc2的鄰近節點*/
	protected ARCNode neighborNodeWithRc2[];
	/**NCHN的CHN*/
	public ARCNode myCHN;
	/**上游CHN*/
	public ARCNode upstreamCHN;
	/**自己*/
	public ARCNode self;
	/**自己叢集的NCHN總個數*/
	public int myNCHN;
	/**自己叢集的啟動NCHN總個數*/
	public int myActiveNCHN;
	/**鄰近叢集節點總個數*/
	public int totalNeiborNode;
	/**鄰近叢集首總個數*/
	public int totalNeiborCHN;
	/**ARCNode的建構子 需輸入 X Y座標 感測距離 叢集內溝通距離 叢集間溝通距離 初始能量 要求的覆蓋率 序列號 感測範圍長 寬 N 封包長度*/
	ARCNode(double inPositionX,
			double inPositionY,
			double inSensingRange,
			double inInnerClusterRange,
			double inInterClusterRange,
			double inPower,
			double inRequireCoverage,
			int inSerialNumber,
			double inL,
			double inW,
			int inN,
			int inBit){
		super(inPositionX,inPositionY,inSensingRange,inInnerClusterRange,inInterClusterRange,inPower,inSerialNumber);
		requireCoverage = inRequireCoverage;
		serialNumber = inSerialNumber;
		L = inL;
		W = inW;
		N = inN;
		bit = inBit;
	}
	/**CHN發送CHN封包 並且記錄自己曾經執行過 曾經當過CHN*/
	public void CHN(){
		if(this.nodeState == NODE){
			this.nodeState = CHN;
			this.executeCHNRound++;
			this.executeRound++;
			Message m = transmit(this.serialNumber,Message.BROADCAST,Message.CHN, Message.PRIMARYCHANNEL,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	/**CHN發送HOP封包 告之鄰近CHN，此CHN的選擇頻道以及NCHN個數
	 * CHN發送HOP封包 告之鄰近NCHN，此CHN的選擇頻道*/
	public void HOP(){
		if(this.nodeState == CHN){
			for(int i=0;i<this.channels.length;i++){
				if(this.channels[i] == true){
					this.clusterChannel = i;
					break;
				}
			}
			Message m = transmit(this.serialNumber,Message.BROADCAST,Message.HOP, Message.PRIMARYCHANNEL,Message.RC2,self,this.deliverHOPTime);
			for (int i = 0;i < this.neighborNodeWithRc2.length;i++){
				this.neighborNodeWithRc2[i].receive(m);
			}
			this.power.transmitWithP2(bit);
		}
	}
	/**NCHN向CHN發出JOIN 封包，加入CHN的小隊*/
	public void JOIN(){
		if(this.nodeState == NCHN){
			Message m = transmit(this.serialNumber,this.myCHN.serialNumber,Message.JOIN, Message.PRIMARYCHANNEL,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	/**CHN告之此叢集的NCHN啟動的門檻值*/
	public void Threshold(){
		if(this.nodeState == CHN){
			Message m = transmit(this.serialNumber,Message.BROADCAST,Message.THRESHOLD,this.clusterChannel,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	/**當NCHN決定要啟動後 發送JOIN2封包給其CHN*/
	public void JOIN2(){
		if(this.nodeState == ACTIVE){
			Message m = transmit(this.serialNumber,this.myCHN.serialNumber,Message.JOIN2, this.clusterChannel,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	/**CHN收到NCHN的JOIN封包後 回應該NCHN TIMESLOT封包 告知其啟動的time slot*/
	public void TimeSlot(Message r){
		if(this.nodeState == CHN){
			Message m = transmit(this.serialNumber,r.sourceNode.serialNumber,Message.TIMESLOT,this.clusterChannel,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	/**回傳資料直到SINK
	 *    NCHN -> CHN
	 * 
	 *    CHN  -> upstream CHN
	 * or CHN  -> SINK 
	 */
	public void reportData(){
		if(this.power.checkPower(bit)){
			if(this.nodeState == CHN){
				//如果HOP等於1，即上游節點為SINK
				if(this.hopCount == 1){
					Message m = transmit(this.serialNumber,Message.SINK,Message.REPORTDATA,Message.PRIMARYCHANNEL,Message.RC2,self);
					for (int i = 0;i < this.neighborNodeWithRc2.length;i++){
						this.neighborNodeWithRc2[i].receive(m);
					}
					this.power.transmitWithP2(bit*turn);
				}
				else{
					//當CHN無法連結到網路時，hopCount為預設10000
					if(this.hopCount == 10000){
						Message m = transmit(this.serialNumber,Message.SINK,Message.REPORTDATA,Message.PRIMARYCHANNEL,Message.RC2,self);
						for (int i = 0;i < this.neighborNodeWithRc2.length;i++){
							this.neighborNodeWithRc2[i].receive(m);
						}
						this.power.transmitWithP2(bit*turn);
					}
					else{
						Message m = transmit(this.serialNumber,this.upstreamCHN.serialNumber,Message.REPORTDATA,Message.PRIMARYCHANNEL,Message.RC2,self);
						for (int i = 0;i < this.neighborNodeWithRc2.length;i++){
							this.neighborNodeWithRc2[i].receive(m);
						}
						this.power.transmitWithP2(bit*turn);
					}
				}
			}
			else if(this.nodeState == ACTIVE){
				Message m = transmit(this.serialNumber,this.myCHN.serialNumber,Message.REPORTDATA,this.clusterChannel,Message.RC1,self);
				this.myCHN.receive(m);
				this.power.transmitWithP1(bit*turn);
			}
		}
		else{
			this.nodeState = ARCNode.DEAD;
			this.deliverHOPTime = 100.0;
			this.valueForCompare = 100.0;
		}
	}
	
	protected Message transmit(int s,int d,String command,int channel,String range,ARCNode Self){
		return new Message(s,d,command,channel,range,self);
	}
	
	protected Message transmit(int s,int d,String command,int channel,String range,ARCNode Self,double nowTime){
		return new Message(s,d,command,channel,range,self,nowTime);
	}
	
	public void receive(Message m){
		if (this.nodeState == ARCNode.DEAD);
		else{
			switch(m.command){
				case Message.PHASE1 :
					this.receivePHASE1(m);
					break;
				case Message.PHASE2 :
					this.receivePHASE2(m);
					break;
				case Message.PHASE3 :
					this.receivePHASE3(m);
					break;
				case Message.CHN :
					this.receiveCHN(m);
					break;
				case Message.JOIN :
					this.receiveJOIN(m);
					break;
				case Message.JOIN2 :
					this.receiveJOIN2(m);
					break;
				case Message.HOP :
					this.receiveHOP(m);
					break;
				case Message.THRESHOLD :
					this.receiveThreshold(m);
					break;
				case Message.TIMESLOT :
					this.receiveTimeSlot(m);
					break;
				case Message.REPORTDATA :
					this.receiveReportData(m);
					break;
			}
		}
	}
	/** 
	 *  接收到PHASE1 的訊息後
	 *  算出backoff time
	 *  更新狀態為node
	 *  清空 myCHN 以及 upstreamCHN
	 *  歸零myNCHN
	 *  所有頻道可用
	 */
	protected void receivePHASE1(Message m){
		this.backofftime =  this.T1 * (double)this.executeCHNRound/(double)(this.executeRound+1)+this.T2*Math.random();
		this.valueForCompare = this.backofftime;
		this.nodeState = NODE;
		this.phase = ARCNode.CHNSELECTION;
		this.myCHN = null;
		this.upstreamCHN = null;
		this.myNCHN=0;
		for(int i=0;i<this.channels.length;i++)
			this.channels[i] = true;
		this.power.hear(bit);
	}
	/**
	 * 接受到PHASE2 的訊息後
	 * 歸零 鄰近 node CHN
	 */
	protected void receivePHASE2(Message m){
		this.deliverHOPTime = 100.0;
		this.valueForCompare = this.deliverHOPTime;
		this.totalNeiborNode = 0;
		this.totalNeiborCHN = 0;
		//this.clusterChannel = 12;
		this.hopCount = 10000;
		this.power.hear(bit);
	}
	/**
	 * 接受到PHASE3 的訊息後
	 * CHN : 歸零啟動節點
	 *       算出 Nh C1 Km
	 * NCHN: 獲得CHN使用的頻道
	 */
	protected void receivePHASE3(Message m){
		if(this.nodeState == CHN){
			this.myActiveNCHN = 0;
			this.Nh = (double)(this.N * (this.totalNeiborCHN + 1)) 
					/ (double)(this.myNCHN + 1 + this.totalNeiborNode);
			this.C1 = (double)(Nh * Math.PI * this.sensingRange * this.sensingRange) 
					/ (W + 2 * this.sensingRange) / (L + 2 * this.sensingRange);
			if(C1 >= this.requireCoverage)
				this.Km = 0.0;
			else
				this.Km = (1 - Math.pow( (1 - this.requireCoverage) / (1 - this.C1), 1/(this.N - this.Nh))) * L * W / Math.PI / this.sensingRange / this.sensingRange;
		}
		else if(this.nodeState == NCHN)
			this.clusterChannel = this.myCHN.clusterChannel;
		this.power.hear(bit);
	}
	
	protected void receivePHASE4(Message m){}
	/**
	 * NODE: 狀態更新成NCHN
	 *       將來源變成myCHN
	 * NCHN: 如果新訊息來源比較近的話 將新訊息來源變成新的myCHN
	 * 
	 */
	protected void receiveCHN(Message m){
		if(this.nodeState == NODE){
			this.nodeState = NCHN;
			this.myCHN = m.sourceNode;
			this.power.hear(bit);
		}
		else if(this.nodeState == NCHN){
			double a=0,b=0;
			a = Math.pow(this.myCHN.getPositionX() - this.getPositionX(),2) + Math.pow(this.myCHN.getPositionY() - this.getPositionY(),2);
			b = Math.pow(m.sourceNode.getPositionX() - this.getPositionX(),2) + Math.pow(m.sourceNode.getPositionY() - this.getPositionY(),2);
			if(a>b)
				this.myCHN = m.sourceNode;
			this.power.hear(bit);
		}
	}
	/**
	 * CHN : NCHN數量+1 
	 * 
	 */
	protected void receiveJOIN(Message m){
		if(this.nodeState == CHN){
			this.power.hear(bit);
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(this.serialNumber == m.destination)
				this.myNCHN++;
		}
		else if(this.nodeState == NCHN){
			this.power.hear(bit);
		}
	}
	/**
	 * 來源是SINK的話 : CHN : 傳送HOP time 為 0 + random +Tb
	 *                        hop = 1
	 *                        上游節點為SINK (先用null代替) 
	 *                  NCHN: 無
	 * 來源是CHN的話  : CHN : 第一次收到HOP訊息時，產生發送HOP time = 接到HOP封包的時間 + random + Tb
	 *                        假如收到的HOP封包的時間比此node發送HOP訊息還早的話
	 *                            則標記HOP封包所指的頻道 不能用
	 *                            加總鄰近 node CHN 數量
	 *                            如果此節點的HOP COUNT比來源的HOP COUNT+1還大的話
	 *                                更新HOP COUNT
	 *                                將來源節點設為上游
	 *                        假如收到的HOP封包的時間比此node發送HOP訊息還晚的話
	 *                            加總鄰近 node CHN 數量
	 *                  NCHN: 無
	 */
	protected void receiveHOP(Message m){
		if(m.source == Message.SINK){
			if(this.nodeState == CHN){
				this.deliverHOPTime = 0.0 + Math.random() + Tb;
				this.valueForCompare = this.deliverHOPTime;
				this.hopCount = 1;
				//////////////////////////////////////////////////////////////////////////////
				this.upstreamCHN = null;
				//////////////////////////////////////////////////////////////////////////////
				this.power.hear(bit);
			}
			else if(this.nodeState == NCHN){
				this.power.hear(bit);
			}
		}
		else{
			if(this.nodeState == NCHN){
				this.power.hear(bit);
			}
			else if(this.nodeState == CHN){
				if(this.deliverHOPTime == 100.0){
					this.deliverHOPTime = m.nowTime + Math.random() + Tb;
					this.valueForCompare = this.deliverHOPTime;
				}
				if(m.nowTime <= this.deliverHOPTime){
					this.channels[m.sourceNode.clusterChannel] = false;
					this.totalNeiborNode += m.sourceNode.myNCHN + 1;
					this.totalNeiborCHN++;
					if(this.hopCount > m.sourceNode.hopCount + 1){
						this.hopCount = m.sourceNode.hopCount + 1;
						this.upstreamCHN = m.sourceNode;
					}
				}
				else{
					this.totalNeiborNode += m.sourceNode.myNCHN + 1;
					this.totalNeiborCHN++;
				}
				this.power.hear(bit);
			}
		}
	}
	/**
	 * 如果隨機值比門檻值還低
	 *     將狀態更新為 ACTIVE
	 *     執行回合+1
	 *     對隊長發出JOIN2封包
	 * 如果隨機值比門檻值還高
	 *     將狀態更新為 SLEEP
	 */
	protected void receiveThreshold(Message m){
		if(this.clusterChannel == m.sourceNode.clusterChannel){
			if(this.nodeState == NCHN){
				if(Math.random() < m.sourceNode.Km){
					this.nodeState = ACTIVE;
					this.executeRound++;
					this.JOIN2();
				}
				else{
					this.nodeState = SLEEP;
				}
				this.power.hear(bit);
			}
		}
	}
	/**
	 * CHN : 回應Time slot給來源節點 
	 *       啟動節點+1
	 * 
	 */
	protected void receiveJOIN2(Message m){
		if(this.clusterChannel == m.sourceNode.clusterChannel){
			if(this.nodeState == ACTIVE){
				this.power.hear(bit);
			}
			else if(this.nodeState == CHN){
				this.TimeSlot(m);
				this.myActiveNCHN++;
				this.power.hear(bit);
			}
		}
	}
	/**
	 * ACTIVE : 獲得time slot 
	 */
	protected void receiveTimeSlot(Message m){
		if(this.clusterChannel == m.sourceNode.clusterChannel){
			if(this.nodeState == CHN){
				this.power.hear(bit);
			}
			else if(this.nodeState == ACTIVE){
				if(this.serialNumber == m.destination)
					this.indexOfTimeSlot = m.sourceNode.myActiveNCHN;
				this.power.hear(bit);
			}
		}
	}
	
	protected void receiveReportData(Message m){
		if(m.sourceNode.nodeState == ARCNode.CHN){
			//if(m.channel == Message.PRIMARYCHANNEL){
				if(this.nodeState == CHN){
					if(m.destination == this.serialNumber){
						this.reportData();
						this.power.hear(bit*turn);
					}
					else{
						///////////////////////////////////////test
						//System.out.println(String.valueOf(this.self) + " hear : " +String.valueOf(m.sourceNode) + " send to " + String.valueOf(m.destination));
						this.power.hear(bit*turn);
					}
				}
			//}
		}
		else if(m.sourceNode.nodeState == ARCNode.ACTIVE){
			if(m.channel == this.clusterChannel){
				if(this.nodeState == CHN){
					this.reportData();
					this.power.hear(bit*turn);
				}
				//else if(this.nodeState == ACTIVE)
					//this.power.hear(bit*turn);
			}
		}
	}
	
	public int compareTo(Object o1){
        //物件本身與 o1 相比較，如果 return 正值，就表示比 o1 大
        if(this.valueForCompare > ((ARCNode)o1).valueForCompare){
            return 1;
        }else{
            return -1;
        }
    }
	
	public String toString(){
		return "Node" + this.serialNumber;
	}

	public void searchNeighborNode(ARCNode ALLNodes[]){
		
		int count11 = 0, count12 = 0, count21 = 0, count22= 0;
		
		for(int i=0;i<ALLNodes.length;i++){
			if(ALLNodes[i].serialNumber == serialNumber)
				continue;
			if(Math.pow(ALLNodes[i].getPositionX() - getPositionX(),2)
					+ Math.pow(ALLNodes[i].getPositionY() - getPositionY(),2)
					<= Math.pow(innerClusterRange,2))
				count11++;
			if(Math.pow(ALLNodes[i].getPositionX() - getPositionX(),2)
					+ Math.pow(ALLNodes[i].getPositionY() - getPositionY(),2)
					<= Math.pow(interClusterRange,2))
				count21++;
		}
		
		neighborNodeWithRc1 = new ARCNode[count11];
		neighborNodeWithRc2 = new ARCNode[count21];
		
		for(int i=0;i<ALLNodes.length;i++){
			if(ALLNodes[i].serialNumber == serialNumber)
				continue;
			if(Math.pow(ALLNodes[i].getPositionX() - getPositionX(),2)
					+ Math.pow(ALLNodes[i].getPositionY() - getPositionY(),2)
					<= Math.pow(innerClusterRange,2))
				neighborNodeWithRc1[count12++] = ALLNodes[i];
			if(Math.pow(ALLNodes[i].getPositionX() - getPositionX(),2)
					+ Math.pow(ALLNodes[i].getPositionY() - getPositionY(),2)
					<= Math.pow(interClusterRange,2))
				neighborNodeWithRc2[count22++] = ALLNodes[i];
				
		}
	}
}
