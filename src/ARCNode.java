
public class ARCNode extends Node implements Comparable<Object>{
	
	int turn = 1;
	
	/** 
	 * �N��`�I�����A
	 */
	protected int nodeState=0;
	public static final int NODE = 0;
	public static final int CHN = 1;
	public static final int NCHN = 2;
	public static final int ACTIVE = 3;
	public static final int SLEEP = 4;
	public static final int DEAD = 5;
	
	/**
	 * �����ثe��PHASE
	 */
	protected int phase;
	public static final int CHNSELECTION = 1;
	public static final int ROUTESETUP = 2;
	public static final int NCHNACTIVATION = 3;
	public static final int STEADYPAHSE = 4;
	
	/**�ثe�^�X��*/
	protected int currentRound;
	/**����L�X���^�X��*/
	protected int executeRound=0;
	/**��L�X��CHN�^�X��*/
	protected int executeCHNRound=0;
	
	
	/**�O�������쪺�`�I��*/
	protected int clusterNodeAmount;	
	
	/**�n�D���л\�v*/
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
	/**�o�eHOP�ʥ]���ɶ�*/
	protected double deliverHOPTime;
	protected double valueForCompare;
	protected double Tb = 2.0;
	
	/**��CHN�ΡA����PHASE 1�ʥ]�ɡA�|����l�ơC
	 * true ��ܦ�Channel�i��
	 */
	protected boolean[] channels = new boolean[20];
	
	
	/**��FSINK��HOP COUNT��*/
	protected int hopCount;
	/**steady phase �ϥΪ��ɶ��ѯ���*/
	protected int indexOfTimeSlot;
	/**steady phase �ϥΪ��W�D*/
	protected int clusterChannel;
	
	
	/**�P���`�I�Z��Rc1���F��`�I*/
	protected ARCNode neighborNodeWithRc1[];
	/**�P���`�I�Z��Rc2���F��`�I*/
	protected ARCNode neighborNodeWithRc2[];
	/**NCHN��CHN*/
	public ARCNode myCHN;
	/**�W��CHN*/
	public ARCNode upstreamCHN;
	/**�ۤv*/
	public ARCNode self;
	/**�ۤv�O����NCHN�`�Ӽ�*/
	public int myNCHN;
	/**�ۤv�O�����Ұ�NCHN�`�Ӽ�*/
	public int myActiveNCHN;
	/**�F���O���`�I�`�Ӽ�*/
	public int totalNeiborNode;
	/**�F���O�����`�Ӽ�*/
	public int totalNeiborCHN;
	/**ARCNode���غc�l �ݿ�J X Y�y�� �P���Z�� �O�������q�Z�� �O�������q�Z�� ��l��q �n�D���л\�v �ǦC�� �P���d��� �e N �ʥ]����*/
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
	/**CHN�o�eCHN�ʥ] �åB�O���ۤv���g����L ���g��LCHN*/
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
	/**CHN�o�eHOP�ʥ] �i���F��CHN�A��CHN������W�D�H��NCHN�Ӽ�
	 * CHN�o�eHOP�ʥ] �i���F��NCHN�A��CHN������W�D*/
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
	/**NCHN�VCHN�o�XJOIN �ʥ]�A�[�JCHN���p��*/
	public void JOIN(){
		if(this.nodeState == NCHN){
			Message m = transmit(this.serialNumber,this.myCHN.serialNumber,Message.JOIN, Message.PRIMARYCHANNEL,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	/**CHN�i�����O����NCHN�Ұʪ����e��*/
	public void Threshold(){
		if(this.nodeState == CHN){
			Message m = transmit(this.serialNumber,Message.BROADCAST,Message.THRESHOLD,this.clusterChannel,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	/**��NCHN�M�w�n�Ұʫ� �o�eJOIN2�ʥ]����CHN*/
	public void JOIN2(){
		if(this.nodeState == ACTIVE){
			Message m = transmit(this.serialNumber,this.myCHN.serialNumber,Message.JOIN2, this.clusterChannel,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	/**CHN����NCHN��JOIN�ʥ]�� �^����NCHN TIMESLOT�ʥ] �i����Ұʪ�time slot*/
	public void TimeSlot(Message r){
		if(this.nodeState == CHN){
			Message m = transmit(this.serialNumber,r.sourceNode.serialNumber,Message.TIMESLOT,this.clusterChannel,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	/**�^�Ǹ�ƪ���SINK
	 *    NCHN -> CHN
	 * 
	 *    CHN  -> upstream CHN
	 * or CHN  -> SINK 
	 */
	public void reportData(){
		if(this.power.checkPower(bit)){
			if(this.nodeState == CHN){
				//�p�GHOP����1�A�Y�W��`�I��SINK
				if(this.hopCount == 1){
					Message m = transmit(this.serialNumber,Message.SINK,Message.REPORTDATA,Message.PRIMARYCHANNEL,Message.RC2,self);
					for (int i = 0;i < this.neighborNodeWithRc2.length;i++){
						this.neighborNodeWithRc2[i].receive(m);
					}
					this.power.transmitWithP2(bit*turn);
				}
				else{
					//��CHN�L�k�s��������ɡAhopCount���w�]10000
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
	 *  ������PHASE1 ���T����
	 *  ��Xbackoff time
	 *  ��s���A��node
	 *  �M�� myCHN �H�� upstreamCHN
	 *  �k�smyNCHN
	 *  �Ҧ��W�D�i��
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
	 * ������PHASE2 ���T����
	 * �k�s �F�� node CHN
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
	 * ������PHASE3 ���T����
	 * CHN : �k�s�Ұʸ`�I
	 *       ��X Nh C1 Km
	 * NCHN: ��oCHN�ϥΪ��W�D
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
	 * NODE: ���A��s��NCHN
	 *       �N�ӷ��ܦ�myCHN
	 * NCHN: �p�G�s�T���ӷ�����񪺸� �N�s�T���ӷ��ܦ��s��myCHN
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
	 * CHN : NCHN�ƶq+1 
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
	 * �ӷ��OSINK���� : CHN : �ǰeHOP time �� 0 + random +Tb
	 *                        hop = 1
	 *                        �W��`�I��SINK (����null�N��) 
	 *                  NCHN: �L
	 * �ӷ��OCHN����  : CHN : �Ĥ@������HOP�T���ɡA���͵o�eHOP time = ����HOP�ʥ]���ɶ� + random + Tb
	 *                        ���p���쪺HOP�ʥ]���ɶ���node�o�eHOP�T���٦�����
	 *                            �h�аOHOP�ʥ]�ҫ����W�D �����
	 *                            �[�`�F�� node CHN �ƶq
	 *                            �p�G���`�I��HOP COUNT��ӷ���HOP COUNT+1�٤j����
	 *                                ��sHOP COUNT
	 *                                �N�ӷ��`�I�]���W��
	 *                        ���p���쪺HOP�ʥ]���ɶ���node�o�eHOP�T���ٱߪ���
	 *                            �[�`�F�� node CHN �ƶq
	 *                  NCHN: �L
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
	 * �p�G�H���Ȥ���e���٧C
	 *     �N���A��s�� ACTIVE
	 *     ����^�X+1
	 *     �ﶤ���o�XJOIN2�ʥ]
	 * �p�G�H���Ȥ���e���ٰ�
	 *     �N���A��s�� SLEEP
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
	 * CHN : �^��Time slot���ӷ��`�I 
	 *       �Ұʸ`�I+1
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
	 * ACTIVE : ��otime slot 
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
        //���󥻨��P o1 �ۤ���A�p�G return ���ȡA�N��ܤ� o1 �j
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
