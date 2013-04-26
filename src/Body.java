import java.util.Arrays;

public class Body {
	public static void main(String[] args) throws Exception{

		
		double Rc1 = 20.0;
		double Rc2 = 60.0;
		double Rs = 5.0;
		double P = 5.0;
		double C0 = 0.9;
		double l = 300.0;
		int bit = 32;
		int turn = 200;
		//String autoOpenMode = "ON";
			
		for(int time = 1 ; time <= 5 ; time++){
			
			int aa=0;
			double CHNs=Math.log10(1 - C0) / Math.log10(1 - Math.PI * Rc1 * Rc1 / l / l) /0.7;
			double NODEs=Math.log10(1 - C0) / Math.log10(1 - Math.PI * Rs * Rs / l / l);
			
			//建立SINK 和 ARC NODE
			Sink sink = new Sink(Rc1,Rc2);
			RS nodes[] = new RS[9000];
			
			for(int i=0;i<nodes.length;i++){
				nodes[i] = new RS(l*Math.random(),l*Math.random(),Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
				nodes[i].self = nodes[i];
				nodes[i].power.owner = nodes[i];
			}	
			
			//建立鄰近關係
			sink.ALLNodes = nodes;
			sink.searchNeighborNode();
			for(int i=0;i<nodes.length;i++){
				nodes[i].searchNeighborNode(nodes);
			}

    		for(int round=1;round<=50000;round++){
    			
    			System.out.println("round : " + String.valueOf(round) + "PAHSE1");
				//PHASE 1
				sink.CHNSelection();
				if(aa == 0);
				else
					CHNs = 0.7 * CHNs + 0.3 * (aa) / RS.clusterNodeNeed;
				RS.clusterNodeNeed = Math.ceil((NODEs - CHNs) / CHNs);
				Arrays.sort(nodes);
				for(int i=0;i<nodes.length;i++)
					nodes[i].CHN();
				for(int i=0;i<nodes.length;i++)
					nodes[i].JOIN();
				
				
				System.out.println("round : " + String.valueOf(round) + "PAHSE2");
				//PHASE 2
				sink.routeSetup();
				sink.HOP();
				{
					int i=0,CHNAmount=0;
					//計算CHN總數量
					for(int j=0;j<nodes.length;j++){
						if(nodes[j].nodeState == ARCNode.CHN)
							CHNAmount++;
					}
					System.out.println("round : " + String.valueOf(round) + "  CHNAmount : " + String.valueOf(CHNAmount));
					int count=0;
					while(true){
						Arrays.sort(nodes);
						for(;i<CHNAmount;i++){
							//System.out.print(nodes[i].nodeState);
							System.out.println(". round : " + String.valueOf(round) + " CHNAmount : " + String.valueOf(CHNAmount) + " i : " + String.valueOf(i) + "hop time" +String.valueOf(nodes[i].deliverHOPTime));
							//System.out.println("round : " + String.valueOf(round) + " CHNAmount : " + String.valueOf(CHNAmount) + " i+1 : " + String.valueOf(i+1) + "hop time" +String.valueOf(nodes[i+1].deliverHOPTime));
							if(nodes[i].deliverHOPTime == 100.0){
								//i++;
								break;
							}
							nodes[i].HOP();
						}
						if(i == CHNAmount)
							break;
						if(count == CHNAmount)
							break;
						count++;
					}
				}
				
				System.out.println("round : " + String.valueOf(round) + "PAHSE4");
				//PAHSE 4
				sink.steadyPhase();
				for(int j=0;j<turn;j++)
					for(int i=0;i<nodes.length;i++){
						if(nodes[i].nodeState == ARCNode.CHN || nodes[i].nodeState == ARCNode.ACTIVE)
							nodes[i].reportData();
					}
				//判定死亡
				for(int i=0;i<nodes.length;i++){
					if(nodes[i].power.checkPower(bit));
					else{
						nodes[i].nodeState = ARCNode.DEAD;
						nodes[i].deliverHOPTime = 100.0;
						nodes[i].valueForCompare = 100.0;
					}
				}
    		}
		}
	}
}
