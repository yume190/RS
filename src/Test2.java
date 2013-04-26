import java.io.*;
import java.util.Arrays;
import jxl.*;

public class Test2 {
	public static void main(String[] args) throws Exception{

		double Rc1 = 20.0;
		double Rc2 = 60.0;
		double Rs = 5.0;
		double P = 5.0;
		double C0 = 0.9;
		double l = 100.0;
		int bit = 32;
		int turn = 200;
		String autoOpenMode = "ON";
		ARCNode testNode;
		
		int aa=0;
		double CHNs=Math.log10(1 - C0) / Math.log10(1 - Math.PI * Rc1 * Rc1 / l / l) /0.7;
		double NODEs=Math.log10(1 - C0) / Math.log10(1 - Math.PI * Rs * Rs / l / l);
		
		double averageCHN=0;
		double averageActiveNCHN=0;
		
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
		
		testNode = nodes[0];
		
		try{
			//構建Workbook物件, 唯讀Workbook物件
    		//創建可寫入的Excel工作薄
    		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(new File("C:/test1/outputActive.xls"));
    		
    		//創建Excel工作表(Sheet)
    		jxl.write.WritableSheet ws1 = wwb.createSheet("Test Sheet 1", 0);
    		jxl.write.WritableSheet ws2 = wwb.createSheet("Test Sheet 2", 1);
    		
    		//1.添加Label對象
    		ws1.addCell(new jxl.write.Label(0, 0, "Round"));
    		ws1.addCell(new jxl.write.Label(1, 0, "實際覆蓋率上限"));
    		ws1.addCell(new jxl.write.Label(2, 0, "實際覆蓋率下限"));
    		ws1.addCell(new jxl.write.Label(3, 0, "CHN 總數"));
    		ws1.addCell(new jxl.write.Label(4, 0, "Active NCHN 總數"));
    		ws1.addCell(new jxl.write.Label(5, 0, "存活節點百分比"));
    		
    		for(int round=1;round<=2500;round++){
    			
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
				System.out.println("round : " + String.valueOf(round) + "PAHSE2 HOP START");
				sink.HOP();
				{
					int i=0,CHNAmount=0;
					for(int j=0;j<nodes.length;j++){
						if(nodes[j].nodeState == ARCNode.CHN)
							CHNAmount++;
					}
					System.out.println("round : " + String.valueOf(round) + "CHNAmount : " + String.valueOf(CHNAmount));
					int count=0;
					while(true){
						Arrays.sort(nodes);
						for(;i<CHNAmount;i++){
							System.out.print(nodes[i].nodeState);
							System.out.println(". round : " + String.valueOf(round) + " CHNAmount : " + String.valueOf(CHNAmount) + " i : " + String.valueOf(i) + "hop time" +String.valueOf(nodes[i].deliverHOPTime));
							System.out.println("round : " + String.valueOf(round) + " CHNAmount : " + String.valueOf(CHNAmount) + " i+1 : " + String.valueOf(i+1) + "hop time" +String.valueOf(nodes[i+1].deliverHOPTime));
							if(nodes[i].deliverHOPTime == 100.0)
								break;
							nodes[i].HOP();
						}
						if(i == CHNAmount)
							break;
						
						
						
						
						
						if(count == 5/*CHNAmount*/){
							jxl.write.WritableWorkbook wwb2 = Workbook.createWorkbook(new File("C:/test1/outputError.xls"));
							jxl.write.WritableSheet ws4 = wwb2.createSheet("Test Sheet 1", 0);
							
							ws4.addCell(new jxl.write.Label(0, 0, "The Node"));
				    		ws4.addCell(new jxl.write.Label(1, 0, "State"));
				    		ws4.addCell(new jxl.write.Label(2, 0, "Backofftime"));
				    		ws4.addCell(new jxl.write.Label(3, 0, "deliver HOP time"));
							ws4.addCell(new jxl.write.Label(4, 0, "X"));
							ws4.addCell(new jxl.write.Label(5, 0, "Y"));
							ws4.addCell(new jxl.write.Label(6, 0, "My CHN"));
							ws4.addCell(new jxl.write.Label(7, 0, "上游CHN"));
							ws4.addCell(new jxl.write.Label(8, 0, "HOP COUNT"));
							ws4.addCell(new jxl.write.Label(9, 0, "Channel"));
							ws4.addCell(new jxl.write.Label(10, 0, "My NCHN數量"));
							ws4.addCell(new jxl.write.Label(11, 0, "跟 My CHN 的距離"));
							ws4.addCell(new jxl.write.Label(12, 0, "門檻值"));
							ws4.addCell(new jxl.write.Label(13, 0, "此CHN的啟動節點數"));
							ws4.addCell(new jxl.write.Label(14, 0, "Nh"));
							ws4.addCell(new jxl.write.Label(17, 0, "Power"));
							
							for(int p=0;p<nodes.length;p++){
				    			ws4.addCell(new jxl.write.Label(0, p+1, nodes[p].toString()));
				    			if(nodes[p].nodeState == ARCNode.CHN)
				    				ws4.addCell(new jxl.write.Label(1, p+1, "CHN"));
				    			else if(nodes[p].nodeState == ARCNode.ACTIVE)
				    				ws4.addCell(new jxl.write.Label(1, p+1, "ACTIVE"));
				    			else if(nodes[p].nodeState == ARCNode.SLEEP)
				    				ws4.addCell(new jxl.write.Label(1, p+1, "SLEEP"));
				    			else
				    				ws4.addCell(new jxl.write.Number(1, p+1, nodes[p].nodeState));
				    			ws4.addCell(new jxl.write.Number(2, p+1, nodes[p].backofftime));
				    			ws4.addCell(new jxl.write.Number(3, p+1, nodes[p].deliverHOPTime));
				    			ws4.addCell(new jxl.write.Number(4, p+1, nodes[p].getPositionX()));
				    			ws4.addCell(new jxl.write.Number(5, p+1, nodes[p].getPositionY()));
				    			ws4.addCell(new jxl.write.Number(17, p+1, 5 - nodes[p].power.getPower()));
				    			if(nodes[p].myCHN != null){
				    				ws4.addCell(new jxl.write.Label(6, i+1, nodes[p].myCHN.toString()));
				        			//ws1.addCell(new jxl.write.Label(6, i+1, String.valueOf(Math.pow(nodes[i].getPositionX()-nodes[i].myCHN.getPositionX(),2)+Math.pow(nodes[i].getPositionY()-nodes[i].myCHN.getPositionY(), 2))));
				    				if(nodes[p].nodeState == ARCNode.ACTIVE)
				    					ws4.addCell(new jxl.write.Label(9, p+1, String.valueOf(nodes[p].clusterChannel) + " : " + String.valueOf(nodes[p].indexOfTimeSlot)));
				    				ws4.addCell(new jxl.write.Number(11, p+1, Math.sqrt(Math.pow(nodes[p].getPositionX()-nodes[p].myCHN.getPositionX(),2)+Math.pow(nodes[p].getPositionY()-nodes[p].myCHN.getPositionY(), 2))));
				    			}
				    			else{
				    				ws4.addCell(new jxl.write.Label(6, p+1, "CHN"));
				    				if(nodes[p].upstreamCHN == null)
				    					ws4.addCell(new jxl.write.Label(7, p+1, "SINK"));
				    				else
				    					ws4.addCell(new jxl.write.Label(7, p+1, nodes[p].upstreamCHN.toString()));
				    				ws4.addCell(new jxl.write.Number(8, p+1, nodes[p].hopCount));
				    				ws4.addCell(new jxl.write.Number(9, p+1, nodes[p].clusterChannel));
				    				ws4.addCell(new jxl.write.Number(10, p+1, nodes[p].myNCHN));
				    				ws4.addCell(new jxl.write.Number(12, p+1, nodes[p].Km));
				    				ws4.addCell(new jxl.write.Number(13, p+1, nodes[p].myActiveNCHN));
				    				ws4.addCell(new jxl.write.Number(14, p+1, nodes[p].Nh));
				    			}
				    		}
							wwb2.write();
							wwb2.close();
							break;
						}
											
						
						
						
						
						
						
						
						count++;
					}
				}
				
				//test
				/*for(int i=0;i<nodes.length;i++){
					if(nodes[i].nodeState == ARCNode.NCHN)
						System.out.println(nodes[i].toString() +"." +String.valueOf(nodes[i].clusterChannel)+ " 的上游: " 
					+ nodes[i].myCHN.toString() + "." +String.valueOf(nodes[i].myCHN.clusterChannel) +" "+ String.valueOf(nodes[i].myCHN.nodeState));
				}*/
				
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
				
			 		
				{
					int a=0,b=0,c=0,d=0;
					double Cu=0,Cd=0,CC=0;
					for(int i=0;i<nodes.length;i++){
						if(nodes[i].nodeState == ARCNode.CHN)
							a++;
						else if(nodes[i].nodeState == ARCNode.ACTIVE)
							b++;
						else if(nodes[i].nodeState == ARCNode.DEAD)
							c++;
						
						
						
						if(nodes[i].nodeState == ARCNode.CHN){
							if(nodes[i].hopCount != 10000)
								d++;
						}
						else if(nodes[i].nodeState == ARCNode.ACTIVE){
							if(nodes[i].myCHN.hopCount != 10000)
								d++;
						}
						
						
					}
					averageCHN+=a;
					averageActiveNCHN+=b;
					
					////////////////////////////////////////////////////////////////////
					aa=d;
					
					ws1.addCell(new jxl.write.Number(0, round, round));
					ws1.addCell(new jxl.write.Number(3, round, a));
					ws1.addCell(new jxl.write.Number(4, round, b));
					Cu = 1 
							- (1 - a * Math.PI * Rs * Rs / l / l ) 
							* (Math.pow( 1- Math.PI * Rs * Rs / l / l, (double)b));
					Cd = 1 
							- (1 - a * Math.PI * Rs * Rs / (l + Rs) / (l + Rs)) 
							* (Math.pow( 1 - Math.PI * Rs * Rs / l / l, (double)b));
					CC = 1
							-Math.pow( 1 -  Math.PI * Rs * Rs / l / l ,d);
					//ws1.addCell(new jxl.write.Number(1, round, Cu));
					ws1.addCell(new jxl.write.Number(2, round, Cd));
					ws1.addCell(new jxl.write.Number(1, round, CC));
					ws1.addCell(new jxl.write.Number(5, round, ((double)nodes.length - c) / nodes.length));
					
				}
				
				if(testNode.nodeState == ARCNode.ACTIVE)
					ws2.addCell(new jxl.write.Label(0, round, "ACTIVE"));
				else if(testNode.nodeState == ARCNode.CHN)
					ws2.addCell(new jxl.write.Label(0, round, "CHN"));
				else if(testNode.nodeState == ARCNode.SLEEP)
					ws2.addCell(new jxl.write.Label(0, round, "SLEEP"));
				else
					ws2.addCell(new jxl.write.Number(0, round, testNode.nodeState));
    			ws2.addCell(new jxl.write.Number(1, round, testNode.power.getPower()));
    			ws2.addCell(new jxl.write.Number(2, round, testNode.clusterChannel));
    			//ws2.addCell(new jxl.write.Number(3, round, testNode.power.getPower()));
				
    		}
    		//ws1.addCell(new jxl.write.Number(3, 101, averageCHN/100));
			//ws1.addCell(new jxl.write.Number(4, 101, averageActiveNCHN/100));
    		ws2.addCell(new jxl.write.Label(0, 0, "Node"));
    		ws2.addCell(new jxl.write.Label(1, 0, "Power"));
    		
    		/*
    		for(int i=0;i<nodes.length;i++){
    			ws2.addCell(new jxl.write.Label(0, i+1, nodes[i].toString()));
    			ws2.addCell(new jxl.write.Number(1, i+1, nodes[i].power.getPower()));
    		}*/
    		
    		
			System.out.println("平均CHN數量 ： " + String.valueOf(averageCHN/2000));
			System.out.println("平均Active NCHN數量 ： " + String.valueOf(averageActiveNCHN/2000));
			System.out.println((averageCHN+averageActiveNCHN)/2000);
			
    		//寫入Exel工作表
    		wwb.write();
    		
    		//關閉Excel工作薄物件
    		wwb.close();
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
		
		//執行完畢後，直接開啟生成EXCEL檔案
    	if(autoOpenMode == "ON")
    		Runtime.getRuntime().exec("cmd.exe /C \"C:\\test1\\outputActive.xls\""); 
		
	}
}
