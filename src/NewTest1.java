import java.io.*;
import java.util.Arrays;
import jxl.*;

public class NewTest1 {
	public static void main(String[] args) throws Exception{

		String path = "C:/NewTest1/實驗一_";
		//內層
		int inner = 12;
		//外層
		int outer = 100 - inner;
		
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
		
			int cantMaintain = 0;
			
			int deadFlag = 0;
			int deadRound = 0;
			
			int aa=0;
			double CHNs=Math.log10(1 - C0) / Math.log10(1 - Math.PI * Rc1 * Rc1 / l / l) /0.7;
			double NODEs=Math.log10(1 - C0) / Math.log10(1 - Math.PI * Rs * Rs / l / l);
			
			//建立SINK 和 ARC NODE
			Sink sink = new Sink(Rc1,Rc2);
			RS nodes[] = new RS[9000];
			/*
			for(int i=0;i<nodes.length;i++){
				nodes[i] = new RS(l*Math.random(),l*Math.random(),Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
				nodes[i].self = nodes[i];
				nodes[i].power.owner = nodes[i];
			}
			*/
			
			for(int i=0;i<nodes.length*inner/100;i++){
				double xx,yy;
				while(true){
					double x = Math.random()*l;
					double y = Math.random()*l;
					if(x*x+y*y < 3600){
						xx=x;
						yy=y;
						break;
					}
				}
				nodes[i] = new RS(xx,yy,Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
				nodes[i].self = nodes[i];
				nodes[i].power.owner = nodes[i];
			}
			for(int i=nodes.length*inner/100;i<nodes.length;i++){
				double xx,yy;
				while(true){
					double x = Math.random()*l;
					double y = Math.random()*l;
					if(x*x+y*y > 3600){
						xx=x;
						yy=y;
						break;
					}
				}
				nodes[i] = new RS(xx,yy,Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
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
				//構建Workbook物件, 唯讀Workbook物件
	    		//創建可寫入的Excel工作薄
	    		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(new File(path + String.valueOf(l) + "x" + String.valueOf(l) + "_" + String.valueOf(nodes.length) + "node" +String.valueOf(inner) + "比" + String.valueOf(outer) + "之" + String.valueOf(time) + ".xls"));
	    		//jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(new File(path + "隨機分布之" + String.valueOf(l) + "x" + String.valueOf(l) + "_" + String.valueOf(nodes.length) + "node" + String.valueOf(time) + ".xls"));
	    		
	    		//創建Excel工作表(Sheet)
	    		jxl.write.WritableSheet ws1 = wwb.createSheet("Test Sheet 1", 0);
	    		
	    		//1.添加Label對象
	    		ws1.addCell(new jxl.write.Label(0, 0, "Round"));
	    		ws1.addCell(new jxl.write.Label(1, 0, "實際覆蓋率"));
	    		ws1.addCell(new jxl.write.Label(2, 0, "CHN 總數"));
	    		ws1.addCell(new jxl.write.Label(3, 0, "Active NCHN 總數"));
	    		ws1.addCell(new jxl.write.Label(4, 0, "存活節點百分比"));
	    		ws1.addCell(new jxl.write.Label(5, 0, "平均結點能量"));
	    		
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
						
						////////////////////////////////////////////////////////////////////
						aa=d;
						
						ws1.addCell(new jxl.write.Number(0, round, round));
						ws1.addCell(new jxl.write.Number(2, round, a));
						ws1.addCell(new jxl.write.Number(3, round, b));
						Cu = 1 
								- (1 - a * Math.PI * Rs * Rs / l / l ) 
								* (Math.pow( 1- Math.PI * Rs * Rs / l / l, (double)b));
						Cd = 1 
								- (1 - a * Math.PI * Rs * Rs / (l + Rs) / (l + Rs)) 
								* (Math.pow( 1 - Math.PI * Rs * Rs / l / l, (double)b));
						CC = 1
								-Math.pow( 1 -  Math.PI * Rs * Rs / l / l ,d);
						//ws1.addCell(new jxl.write.Number(1, round, Cu));
						//ws1.addCell(new jxl.write.Number(2, round, Cd));
						ws1.addCell(new jxl.write.Number(1, round, CC));
						ws1.addCell(new jxl.write.Number(4, round, ((double)nodes.length - c) / nodes.length));
						
						if(deadFlag == 0){
							if(c != 0){
								deadFlag = 1;
								deadRound = round;
							}
						}
						
						double averageEnergy = 0;
						for(int z = 0 ; z < nodes.length ; z++){
							averageEnergy += nodes[z].power.getPower() / nodes.length;
						}
						ws1.addCell(new jxl.write.Number(5, round, averageEnergy));
						
						if(round > 10){
							if(CC < C0*0.9)
								cantMaintain++;
							else
								cantMaintain = 0;
						}
						if(cantMaintain == 2){
							
							
							ws1.addCell(new jxl.write.Number(6, round, deadRound));
							
							//寫入Exel工作表
				    		wwb.write();
				    		
				    		//關閉Excel工作薄物件
				    		wwb.close();
				    		
				    		break;
						}
						
					}
	    		}
				
	    		
	    	}
	    	catch (Exception e){
	    		e.printStackTrace();
	    	}
			
			//執行完畢後，直接開啟生成EXCEL檔案
	    	//if(autoOpenMode == "ON")
	    		//Runtime.getRuntime().exec("cmd.exe /C \"C:\\test1\\outputActive.xls\""); 
		
		}
	}
}
