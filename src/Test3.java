import java.io.*;
import java.util.Arrays;
import jxl.*;
//import jxl.write.*;
//import java.util.Collections;

public class Test3 {	
	
	public static void main(String[] args) throws Exception{

		double Rc1 = 20.0;
		double Rc2 = 60.0;
		double Rs = 5.0;
		double P = 5.0;
		double C0 = 0.9;
		double l = 100.0;
		int bit = 32;
		//int turn = 200;
		int testRound = 1500;
		String autoOpenMode = "ON";
		
		//建立SINK 和 ARC NODE
		Sink sink = new Sink(Rc1,Rc2);
		RS nodes[] = new RS[1000];
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
		
		
		
    	try{
    		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(new File("C:/test1/outputTest3.xls"));
    		jxl.write.WritableSheet ws1 = wwb.createSheet("Test Sheet 1", 0);
    		ws1.addCell(new jxl.write.Label(0, 0, "The Node"));
    		ws1.addCell(new jxl.write.Label(1, 0, "State"));
    		ws1.addCell(new jxl.write.Label(2, 0, "Backofftime"));
    		ws1.addCell(new jxl.write.Label(3, 0, "deliver HOP time"));
			ws1.addCell(new jxl.write.Label(4, 0, "X"));
			ws1.addCell(new jxl.write.Label(5, 0, "Y"));
			ws1.addCell(new jxl.write.Label(6, 0, "My CHN"));
			ws1.addCell(new jxl.write.Label(7, 0, "上游CHN"));
			ws1.addCell(new jxl.write.Label(8, 0, "HOP COUNT"));
			ws1.addCell(new jxl.write.Label(9, 0, "Channel"));
			ws1.addCell(new jxl.write.Label(10, 0, "My NCHN數量"));
			ws1.addCell(new jxl.write.Label(11, 0, "跟 My CHN 的距離"));
			ws1.addCell(new jxl.write.Label(12, 0, "門檻值"));
			ws1.addCell(new jxl.write.Label(13, 0, "此CHN的啟動節點數"));
			ws1.addCell(new jxl.write.Label(14, 0, "Nh"));
			ws1.addCell(new jxl.write.Label(17, 0, "Power"));
			ws1.addCell(new jxl.write.Label(18, 0, "當過CHN次數"));
			ws1.addCell(new jxl.write.Label(19, 0, "啟動次數"));
			
			
			for(int k=1;k<=testRound;k++){
				
				System.out.println("round : " + String.valueOf(k));
				
				//PHASE 1
				sink.CHNSelection();
				
				if(k == testRound){
					//test
					for(int i=0;i<nodes.length;i++){
						if(nodes[i].myCHN==null){
							System.out.print(nodes[i]);
							System.out.print(".");
							System.out.print(nodes[i].nodeState);
							System.out.print(" : null\n");
						}
						else
							System.out.println(nodes[i]);
					}
				}
				
				Arrays.sort(nodes);
				for(int i=0;i<nodes.length;i++)
					nodes[i].CHN();
				for(int i=0;i<nodes.length;i++)
					nodes[i].JOIN();
				
				//PHASE 2
				sink.routeSetup();
				sink.HOP();
				{
					int i=0,CHNAmount=0;
					for(int j=0;j<nodes.length;j++){
						if(nodes[j].nodeState == ARCNode.CHN)
							CHNAmount++;
					}
					while(true){
						Arrays.sort(nodes);
						for(;i<CHNAmount;i++){
							if(nodes[i].deliverHOPTime == 100.0)
								break;
							nodes[i].HOP();
						}
						if(i == CHNAmount)
							break;
					}
				}
				
				/*
				if(k == testRound){
					//test
					for(int i=0;i<nodes.length;i++){
						if(nodes[i].nodeState == ARCNode.NCHN)
							System.out.println(nodes[i].toString() +"." +String.valueOf(nodes[i].clusterChannel)+ " 的上游: " 
						+ nodes[i].myCHN.toString() + "." +String.valueOf(nodes[i].myCHN.clusterChannel) +" "+ String.valueOf(nodes[i].myCHN.nodeState));
					}
				}
				*/
				
				//PAHSE 4
				sink.steadyPhase();
				for(int i=0;i<nodes.length;i++)
					if(nodes[i].nodeState == ARCNode.CHN || nodes[i].nodeState == ARCNode.ACTIVE)
						//for(int j=0;j<turn;j++)
							nodes[i].reportData();
				
				for(int i=0;i<nodes.length;i++){
					if(nodes[i].power.checkPower(bit));
					else{
						nodes[i].nodeState = ARCNode.DEAD;
						nodes[i].deliverHOPTime = 100.0;
						nodes[i].valueForCompare = 100.0;
					}
				}
			}
			
			
			
			
			
			
			{
				int a=0,b=0;
				double Cu=0,Cd=0;
				for(int i=0;i<nodes.length;i++){
					if(nodes[i].nodeState == ARCNode.CHN)
						a++;
					else if(nodes[i].nodeState == ARCNode.ACTIVE)
						b++;
				}
				ws1.addCell(new jxl.write.Label(15, 2, "Active NCHN總數"));
				ws1.addCell(new jxl.write.Label(15, 3, "CHN總數"));
				ws1.addCell(new jxl.write.Label(15, 4, "實際覆蓋率上限"));
				ws1.addCell(new jxl.write.Label(15, 5, "實際覆蓋率下限"));
				ws1.addCell(new jxl.write.Number(16, 2, b));
				ws1.addCell(new jxl.write.Number(16, 3, a));
				Cu =1 
						- (1 - a * Math.PI * Rs * Rs / l / l ) 
						* (Math.pow( 1- Math.PI * Rs * Rs / l / l, (double)b));
				Cd =1 
						- (1 - a * Math.PI * Rs * Rs / (l + Rs) / (l + Rs)) 
						* (Math.pow( 1 - Math.PI * Rs * Rs / l / l, (double)b));
				ws1.addCell(new jxl.write.Number(16, 4, Cu));
				ws1.addCell(new jxl.write.Number(16, 5, Cd));
				System.out.print("CHN 個數 : ");
				System.out.print(a);
				System.out.print("\n");
				System.out.print("Active NCHN 個數 : ");
				System.out.print(b);
				System.out.print("\n");
				System.out.print("實際覆蓋率上限 : ");
				System.out.print(Cu);
				System.out.print("\n");
				System.out.print("實際覆蓋率下限 : ");
				System.out.print(Cd);
				System.out.print("\n");
				
			}
    		for(int i=0;i<nodes.length;i++){
    			ws1.addCell(new jxl.write.Label(0, i+1, nodes[i].toString()));
    			if(nodes[i].nodeState == ARCNode.CHN)
    				ws1.addCell(new jxl.write.Label(1, i+1, "CHN"));
    			else if(nodes[i].nodeState == ARCNode.ACTIVE)
    				ws1.addCell(new jxl.write.Label(1, i+1, "ACTIVE"));
    			else if(nodes[i].nodeState == ARCNode.SLEEP)
    				ws1.addCell(new jxl.write.Label(1, i+1, "SLEEP"));
    			else
    				ws1.addCell(new jxl.write.Number(1, i+1, nodes[i].nodeState));
    			ws1.addCell(new jxl.write.Number(2, i+1, nodes[i].backofftime));
    			ws1.addCell(new jxl.write.Number(3, i+1, nodes[i].deliverHOPTime));
    			ws1.addCell(new jxl.write.Number(4, i+1, nodes[i].getPositionX()));
    			ws1.addCell(new jxl.write.Number(5, i+1, nodes[i].getPositionY()));
    			ws1.addCell(new jxl.write.Number(17, i+1, 5 - nodes[i].power.getPower()));
    			ws1.addCell(new jxl.write.Number(18, i+1, nodes[i].executeCHNRound));
    			ws1.addCell(new jxl.write.Number(19, i+1, nodes[i].executeRound));
    			if(nodes[i].myCHN != null){
    				ws1.addCell(new jxl.write.Label(6, i+1, nodes[i].myCHN.toString()));
        			//ws1.addCell(new jxl.write.Label(6, i+1, String.valueOf(Math.pow(nodes[i].getPositionX()-nodes[i].myCHN.getPositionX(),2)+Math.pow(nodes[i].getPositionY()-nodes[i].myCHN.getPositionY(), 2))));
    				if(nodes[i].nodeState == ARCNode.ACTIVE)
    					ws1.addCell(new jxl.write.Label(9, i+1, String.valueOf(nodes[i].clusterChannel) + " : " + String.valueOf(nodes[i].indexOfTimeSlot)));
    				else
    					ws1.addCell(new jxl.write.Number(9, i+1, nodes[i].clusterChannel));
    				ws1.addCell(new jxl.write.Number(11, i+1, Math.sqrt(Math.pow(nodes[i].getPositionX()-nodes[i].myCHN.getPositionX(),2)+Math.pow(nodes[i].getPositionY()-nodes[i].myCHN.getPositionY(), 2))));
    			}
    			else{
    				ws1.addCell(new jxl.write.Label(6, i+1, "CHN"));
    				if(nodes[i].upstreamCHN == null)
    					ws1.addCell(new jxl.write.Label(7, i+1, "SINK"));
    				else
    					ws1.addCell(new jxl.write.Label(7, i+1, nodes[i].upstreamCHN.toString()));
    				ws1.addCell(new jxl.write.Number(8, i+1, nodes[i].hopCount));
    				ws1.addCell(new jxl.write.Number(9, i+1, nodes[i].clusterChannel));
    				ws1.addCell(new jxl.write.Number(10, i+1, nodes[i].myNCHN));
    				ws1.addCell(new jxl.write.Number(12, i+1, nodes[i].Km));
    				ws1.addCell(new jxl.write.Number(13, i+1, nodes[i].myActiveNCHN));
    				ws1.addCell(new jxl.write.Number(14, i+1, nodes[i].Nh));
    			}
    		}
    		
    		/*
    		for(int i=0;i<sink.neighborNodeWithRc2.length;i++){
    			ws2.addCell(new jxl.write.Label(0, i, nodes[i].toString()));
    			ws2.addCell(new jxl.write.Label(1, i, String.valueOf(nodes[i].getPositionX())));
    			ws2.addCell(new jxl.write.Label(2, i, String.valueOf(nodes[i].getPositionX())));
    		}
    		*/
    		
    		/*	
    		for(int i=0;i<nodes[0].neighborNodeWithRc2.length;i++){
    			ws3.addCell(new jxl.write.Label(0, i, nodes[i].toString()));
    			ws3.addCell(new jxl.write.Label(1, i, String.valueOf(nodes[i].getPositionX())));
    			ws3.addCell(new jxl.write.Label(2, i, String.valueOf(nodes[i].getPositionX())));
    		}
    		*/
    		
    		
    		
    		
    		//寫入Exel工作表
    		wwb.write();
    		
    		//關閉Excel工作薄物件
    		wwb.close();
    		
    		for(int ii=0;ii<nodes.length;ii++){
    			if(nodes[ii].nodeState == ARCNode.CHN){
    				System.out.print(nodes[ii]);
    				System.out.print(" : ");
    				System.out.print(nodes[ii].totalNeiborCHN);
    				System.out.print(" ");
    				System.out.print(nodes[ii].totalNeiborNode);
    				System.out.print("\n");
    			}
    		}
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	//執行完畢後，直接開啟生成EXCEL檔案
    	if(autoOpenMode == "ON")
    		Runtime.getRuntime().exec("cmd.exe /C \"C:\\test1\\outputTest3.xls\""); 
    		
	}
}
