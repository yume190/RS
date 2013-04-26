import java.util.ArrayList;
import java.util.Arrays;
public class RS extends ARCNode{
	
	double totalNeedNode = 0;
	double sigma = 0;
	/**叢集首所需收到的節點數*/
	static double clusterNodeNeed;
	/**叢集首自己與下游累積節點數*/
	protected int clusterNodePassThrouthAmount;
	
	protected int T1=1;
	protected int T2=6;
	protected int T3=3;
	
	/**NCHN在TIMER停止前，維護的CHN LIST*/
	ArrayList<ARCNode> CHNListA = new ArrayList<ARCNode>();
	/**NCHN在TIMER停止後，維護的CHN LIST*/
	ArrayList<ARCNode> CHNListB = new ArrayList<ARCNode>();
	/**此CHN的下游CHN LIST*/
	ArrayList<ARCNode> downStreamList = new ArrayList<ARCNode>();
	
	RS(double inPositionX,
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
		super(inPositionX,inPositionY,inSensingRange,inInnerClusterRange,inInterClusterRange,inPower,inRequireCoverage,inSerialNumber,inL,inW,inN,inBit);
		//clusterNodeNeed = Math.floor(Math.log10(1 - this.requireCoverage) / Math.log10(1 - Math.PI * this.sensingRange * this.sensingRange / Math.PI / this.innerClusterRange / this.innerClusterRange));
		//clusterNodeNeed = Math.floor(Math.log10(1 - this.requireCoverage) / Math.log10(1 - Math.PI * this.sensingRange * this.sensingRange / 10000));
		//clusterNodeNeed = (clusterNodeNeed - 20) / 20;
		
		double totalNeedCluster=0;
		totalNeedCluster = Math.log10(1 - this.requireCoverage) / Math.log10(1 - Math.PI * this.innerClusterRange * this.innerClusterRange / this.L / this.W);
		totalNeedNode = Math.log10(1 - this.requireCoverage) / Math.log10(1 - Math.PI * this.sensingRange * this.sensingRange / this.L / this.W);
		clusterNodeNeed = (totalNeedNode - totalNeedCluster) / totalNeedCluster; 
		clusterNodeNeed = Math.ceil(clusterNodeNeed);
		
		this.clusterNodePassThrouthAmount = 0;
		this.currentRound = 0;
	}
	
	
	public void JOIN(){
		
		/*
		System.out.print(this.self);
		System.out.print(" ");
		System.out.print(this.nodeState);
		System.out.print(" join \n");
		*/
		
		if(this.nodeState == NCHN){
			
			ARCNode listA[] = this.CHNListA.toArray(new ARCNode[0]);
			for(int aa = 0 ; aa < listA.length ; aa++){
				listA[aa].valueForCompare = Math.pow(this.getPositionX() - listA[aa].getPositionX(), 2)
										  + Math.pow(this.getPositionY() - listA[aa].getPositionY(), 2);
			}
			//System.out.print("CHNListA ");
			//System.out.println(CHNListA);
			if(listA.length != 0)
				Arrays.sort(listA);
			for(int aa = 0 ; aa < listA.length ; aa++){
				if(this.nodeState == NCHN){
					///////////////////////////////////////////////////////////////////////////
					/*
					System.out.print(this.self);
					System.out.print(" join ");
					System.out.print(listA[aa]);
					System.out.print("\n");
					*/
					Message m = transmit(this.serialNumber,listA[aa].serialNumber,Message.JOIN, Message.PRIMARYCHANNEL,Message.RC1,self);
					for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
						this.neighborNodeWithRc1[i].receive(m);
					}
					this.power.transmitWithP1(bit);
				}
			}
			
			ARCNode listB[] = this.CHNListB.toArray(new ARCNode[0]);
			for(int aa = 0 ; aa < listB.length ; aa++){
				listB[aa].valueForCompare = Math.pow(this.getPositionX() - listB[aa].getPositionX(), 2)
										  + Math.pow(this.getPositionY() - listB[aa].getPositionY(), 2);
			}
			Arrays.sort(listB);
			for(int aa = 0 ; aa < listB.length ; aa++){
				if(this.nodeState == NCHN){
					///////////////////////////////////////////////////////////////////////////
					/*
					System.out.print(this.self);
					System.out.print(" join ");
					System.out.print(listB[aa]);
					System.out.print("\n");
					*/
					Message m = transmit(this.serialNumber,listB[aa].serialNumber,Message.JOIN, Message.PRIMARYCHANNEL,Message.RC1,self);
					for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
						this.neighborNodeWithRc1[i].receive(m);
					}
					this.power.transmitWithP1(bit);
				}
			}
		}
	}
	
	public void Permission(Message r){
		if(this.nodeState == CHN){
			Message m = transmit(this.serialNumber,r.sourceNode.serialNumber,Message.PERMISSION, Message.PRIMARYCHANNEL,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
	}
	
	public void Completion(){
		if(this.nodeState == CHN){
			Message m = transmit(this.serialNumber,Message.BROADCAST,Message.COMPLETION, Message.PRIMARYCHANNEL,Message.RC1,self);
			for (int i = 0;i < this.neighborNodeWithRc1.length;i++){
				this.neighborNodeWithRc1[i].receive(m);
			}
			this.power.transmitWithP1(bit);
		}
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
				case Message.PHASE4 :
					this.receivePHASE4(m);
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
				case Message.PERMISSION :
					this.receivePermission(m);
					break;
				case Message.COMPLETION :
					this.receiveCompletion(m);
					break;
			}
		}
	}
	
	protected void receivePHASE1(Message m){
		
		this.clusterChannel = 12;
		
		this.downStreamList.removeAll(downStreamList);
		
		this.CHNListA.removeAll(CHNListA);
		this.CHNListB.removeAll(CHNListB);
		
		this.sigma += this.clusterNodePassThrouthAmount / (this.clusterNodePassThrouthAmount + clusterNodeNeed);
		this.clusterNodePassThrouthAmount = 0;
		
		this.currentRound += 1;
		this.myActiveNCHN = 0;
		this.backofftime =  this.T1 * (double)this.executeRound / (double)this.currentRound
						 +	this.T2 * this.sigma /(double)(this.executeRound+1)
						 +	this.T3 * Math.random();
		
		/*
		System.out.print(" T1 :");
		System.out.print(this.T1);
		System.out.print(" T2 :");
		System.out.print(this.T2);
		System.out.print(" T3 :");
		System.out.print(this.T3);
		System.out.print("\n");
		*/
		
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
	
	protected void receiveCHN(Message m){
		if(this.nodeState == NODE){
			this.nodeState = NCHN;
			CHNListA.add(m.sourceNode);
		}
		else if(this.nodeState == NCHN){
			if(this.backofftime > m.sourceNode.backofftime)
				CHNListA.add(m.sourceNode);
			else
				CHNListB.add(m.sourceNode);
		}
		this.power.hear(bit);
	}
	
	protected void receiveJOIN(Message m){
		if(this.nodeState == CHN){
			if(this.serialNumber == m.destination){
				if(this.myActiveNCHN > clusterNodeNeed){
					this.Completion();
				}
				
				else if(this.myActiveNCHN < clusterNodeNeed){
					this.Permission(m);
					this.myActiveNCHN++;
					if(this.myActiveNCHN == clusterNodeNeed - 2)
						this.Completion();
				}
			}
			this.power.hear(bit);
		}
		else if(this.nodeState == NCHN)
			this.power.hear(bit);
		else;
	}
	
	protected void receivePermission(Message m){
		if(this.nodeState == NCHN){
			if(this.serialNumber == m.destination){
				/////////////////////////////////////////////////////////////
				/*
				System.out.print(m.sourceNode);
				System.out.print(" permission ");
				System.out.print(this.self);
				System.out.print("\n");
				*/
				this.nodeState = ACTIVE;
				this.executeRound++;
				this.myCHN = m.sourceNode;
				this.indexOfTimeSlot = m.sourceNode.myActiveNCHN;
			}
		}
		this.power.hear(bit);
	}
	
	protected void receiveCompletion(Message m){
		if(this.nodeState == NCHN){
			//System.out.print(this.self);
			//System.out.print(" remove ");
			//System.out.println(m.sourceNode);
			//System.out.print(this.CHNListA);
			//System.out.print(">");
			this.CHNListA.remove(m.sourceNode);
			//System.out.println(this.CHNListA);
			//System.out.print(this.CHNListB);
			//System.out.print(">");
			this.CHNListB.remove(m.sourceNode);
			//System.out.println(this.CHNListB);
		}
		this.power.hear(bit);
	}
	
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
			else if(this.nodeState == ACTIVE){
				this.power.hear(bit);
			}
		}
		else{
			if(this.nodeState == ACTIVE){
				this.power.hear(bit);
			}
			else if(this.nodeState == CHN){
				if(this.deliverHOPTime == 100.0){
					this.deliverHOPTime = m.nowTime + Math.random() + Tb;
					this.valueForCompare = this.deliverHOPTime;
					this.hopCount = m.sourceNode.hopCount + 1;
					this.upstreamCHN = m.sourceNode;
					((RS)this.upstreamCHN).downStreamList.add(this.self);
				}
				if(m.nowTime <= this.deliverHOPTime){
					this.channels[m.sourceNode.clusterChannel] = false;
					this.totalNeiborNode += m.sourceNode.myNCHN + 1;
					this.totalNeiborCHN++;
				}
				else{
					this.totalNeiborNode += m.sourceNode.myNCHN + 1;
					this.totalNeiborCHN++;
				}
				this.power.hear(bit);
			}
		}
	}
	
	protected void receivePHASE4(Message m){
		if(this.nodeState == NCHN)
			this.nodeState = SLEEP;
		else if(this.nodeState == ACTIVE)
			this.clusterChannel = this.myCHN.clusterChannel;
		else if(this.nodeState == CHN){
			this.clusterNodePassThrouthAmount += 1 + this.myActiveNCHN + this.counting(this.downStreamList.toArray(new ARCNode[0]));
		}
		this.power.hear(bit);
	}
	
	private int counting(ARCNode[] node){
		int sum = 0;
		for(int ii = 0;ii < node.length;ii++){
			if(((RS)node[ii]).downStreamList.size() == 0)
				sum += 1 + ((RS)node[ii]).myActiveNCHN;
			else
				sum += 1 + ((RS)node[ii]).myActiveNCHN + ((RS)node[ii]).counting(((RS)node[ii]).downStreamList.toArray(new ARCNode[0])); 
		}
		return sum;
	}
}
