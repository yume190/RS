import java.io.*;
import java.util.Arrays;
import jxl.*;
//import jxl.write.*;
//import java.util.Collections;

public class TEST {	
	
	public static void main(String[] args) throws Exception{

		double Rc1 = 20.0;
		double Rc2 = 60.0;
		double Rs = 5.0;
		double P = 5.0;
		double C0 = 0.9;
		double l = 100.0;
		int bit = 32;
		int turn = 1;
		//int testA = 400;
		String autoOpenMode = "OFF";
		
		//
		AreaCount AC = new AreaCount(100,100,1);
		AreaCountBossVer AC2 = new AreaCountBossVer(100,100);
		
		//建立SINK 和 ARC NODE
		Sink sink = new Sink(Rc1,Rc2);
		RS nodes[] = new RS[1000];
		
		//原始版本
		
		for(int i=0;i<nodes.length;i++){
			nodes[i] = new RS(l*Math.random(),l*Math.random(),Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
			nodes[i].self = nodes[i];
			nodes[i].power.owner = nodes[i];
		}
		
		
		//測試之一
		/*
		for(int i=0;i<testA;i++){
			nodes[i] = new RS(l*Math.random(),l*Math.random(),Rs,Rc1,Rc2,0,C0,i,l,l,nodes.length,bit);
			nodes[i].self = nodes[i];
			nodes[i].power.owner = nodes[i];
			nodes[i].nodeState = ARCNode.DEAD;
			nodes[i].valueForCompare=10000;
		}
		for(int i=testA;i<nodes.length;i++){
			nodes[i] = new RS(l*Math.random(),l*Math.random(),Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
			nodes[i].self = nodes[i];
			nodes[i].power.owner = nodes[i];
		}
		*/
		
		//建立鄰近關係
		sink.ALLNodes = nodes;
		sink.searchNeighborNode();
		for(int i=0;i<nodes.length;i++){
			nodes[i].searchNeighborNode(nodes);
		}
		
		//PHASE 1
		sink.CHNSelection();
		Arrays.sort(nodes);
		for(int i=0;i<nodes.length;i++){
			nodes[i].CHN();
		}
		for(int i=0;i<nodes.length;i++)
			((RS)nodes[i]).JOIN();
		
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
		//PHASE 3
		sink.NCHNActivation();
		for(int i=0;i<nodes.length;i++){
			nodes[i].Threshold();
		}
		*/
		
		//PAHSE 4
		sink.steadyPhase();
		for(int i=0;i<nodes.length;i++){
			if(nodes[i].nodeState == ARCNode.CHN || nodes[i].nodeState == ARCNode.ACTIVE){
				for(int j=0;j<turn;j++){
					nodes[i].reportData();
					////////////////////////////////////////////////test
					//System.out.println();
				}
			}
		}
		
		////////////////////////////////////////////AC
		for(int i=0;i<nodes.length;i++){
			if(nodes[i].nodeState == ARCNode.CHN || nodes[i].nodeState == ARCNode.ACTIVE){
				AC.addCircle(nodes[i].getPositionX(), nodes[i].getPositionY(), Rs);
				AC2.addCircle(nodes[i].getPositionX(), nodes[i].getPositionY(), Rs);
			}
		}
		System.out.println("The coverage rate Ver.1 : " + AC.getCoverageRate());
		System.out.println("The coverage rate Ver.2 : " + AC2.getCoverageRate());
		
		/*
		for(int i=0;i<nodes.length;i++){
			nodes[i].valueForCompare = nodes[i].backofftime;
		}
		Arrays.sort(nodes);
		*/
		
		/*
		for(int a=0;a<nodes.length;a++){
			if(nodes[a].nodeState == ARCNode.CHN){
				System.out.print(nodes[a]);
				System.out.print(" : \n");
				for(int b=0;b<nodes[a].neighborNodeWithRc2.length;b++){
					if(nodes[a].neighborNodeWithRc2[b].nodeState == ARCNode.CHN){
						System.out.print(nodes[a].neighborNodeWithRc2[b]);
						System.out.print(" : ");
						System.out.print(nodes[a].neighborNodeWithRc2[b].myNCHN);
						System.out.print("\n");
					}
				}
			}
		}
		*/
		
    	try{
    		//構建Workbook物件, 唯讀Workbook物件
    		//Method 1：創建可寫入的Excel工作薄
    		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(new File("C:/test1/output.xls"));

    		//Method 2：將WritableWorkbook直接寫入到輸出流
    		/*
    		OutputStream os = new FileOutputStream(targetfile);
    		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(os);
    		*/
    		
    		//創建Excel工作表(Sheet)
    		jxl.write.WritableSheet ws1 = wwb.createSheet("Test Sheet 1", 0);
    		//jxl.write.WritableSheet ws2 = wwb.createSheet("Test Sheet 2", 0);
    		//jxl.write.WritableSheet ws3 = wwb.createSheet("Test Sheet 3", 0);
    		
    		//1.添加Label對象
    		//jxl.write.Label labelC = new jxl.write.Label(0, 0, "This is a Label cell");
    		//ws1.addCell(labelC);
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
			ws1.addCell(new jxl.write.Label(10, 0, "跟 My CHN 的距離"));
			ws1.addCell(new jxl.write.Label(11, 0, "此CHN的啟動節點數"));
			ws1.addCell(new jxl.write.Label(12, 0, "此CHN的累積節點"));
			ws1.addCell(new jxl.write.Label(13, 0, "Power"));
			ws1.addCell(new jxl.write.Label(14, 0, "當過CHN次數"));
			ws1.addCell(new jxl.write.Label(15, 0, "啟動次數"));
			
			
			{
				int a=0,b=0;
				double Cu=0,Cd=0;
				for(int i=0;i<nodes.length;i++){
					if(nodes[i].nodeState == ARCNode.CHN)
						a++;
					else if(nodes[i].nodeState == ARCNode.ACTIVE)
						b++;
				}
				ws1.addCell(new jxl.write.Label(16, 2, "Active NCHN總數"));
				ws1.addCell(new jxl.write.Label(16, 3, "CHN總數"));
				ws1.addCell(new jxl.write.Label(16, 4, "實際覆蓋率上限"));
				ws1.addCell(new jxl.write.Label(16, 5, "實際覆蓋率下限"));
				ws1.addCell(new jxl.write.Number(17, 2, b));
				ws1.addCell(new jxl.write.Number(17, 3, a));
				Cu =1 
						- (1 - a * Math.PI * Rs * Rs / l / l ) 
						* (Math.pow( 1- Math.PI * Rs * Rs / l / l, (double)b));
				Cd =1 
						- (1 - a * Math.PI * Rs * Rs / (l + Rs) / (l + Rs)) 
						* (Math.pow( 1 - Math.PI * Rs * Rs / l / l, (double)b));
				ws1.addCell(new jxl.write.Number(17, 4, Cu));
				ws1.addCell(new jxl.write.Number(17, 5, Cd));
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
    			ws1.addCell(new jxl.write.Number(3, i+1, ((RS)nodes[i]).deliverHOPTime));
    			ws1.addCell(new jxl.write.Number(4, i+1, nodes[i].getPositionX()));
    			ws1.addCell(new jxl.write.Number(5, i+1, nodes[i].getPositionY()));
    			ws1.addCell(new jxl.write.Number(13, i+1, 5 - nodes[i].power.getPower()));
    			
    			
    			ws1.addCell(new jxl.write.Number(14, i+1, nodes[i].executeCHNRound));
    			ws1.addCell(new jxl.write.Number(15, i+1, nodes[i].executeRound));
    			
    			if(nodes[i].myCHN != null){
    				ws1.addCell(new jxl.write.Label(6, i+1, nodes[i].myCHN.toString()));
        			//ws1.addCell(new jxl.write.Label(6, i+1, String.valueOf(Math.pow(nodes[i].getPositionX()-nodes[i].myCHN.getPositionX(),2)+Math.pow(nodes[i].getPositionY()-nodes[i].myCHN.getPositionY(), 2))));
    				if(nodes[i].nodeState == ARCNode.ACTIVE)
    					ws1.addCell(new jxl.write.Label(9, i+1, String.valueOf(nodes[i].clusterChannel) + " : " + String.valueOf(nodes[i].indexOfTimeSlot)));
    				ws1.addCell(new jxl.write.Number(10, i+1, Math.sqrt(Math.pow(nodes[i].getPositionX()-nodes[i].myCHN.getPositionX(),2)+Math.pow(nodes[i].getPositionY()-nodes[i].myCHN.getPositionY(), 2))));
    			}
    			else{
    				if(nodes[i].nodeState == ARCNode.CHN){
    					ws1.addCell(new jxl.write.Label(6, i+1, "CHN"));
        				if(nodes[i].upstreamCHN == null)
        					ws1.addCell(new jxl.write.Label(7, i+1, "SINK"));
        				else
        					ws1.addCell(new jxl.write.Label(7, i+1, nodes[i].upstreamCHN.toString()));
        				ws1.addCell(new jxl.write.Number(8, i+1, nodes[i].hopCount));
        				ws1.addCell(new jxl.write.Number(9, i+1, nodes[i].clusterChannel));
        				ws1.addCell(new jxl.write.Number(11, i+1, ((RS)nodes[i]).myActiveNCHN));
        				ws1.addCell(new jxl.write.Number(12, i+1, ((RS)nodes[i]).clusterNodePassThrouthAmount));
    				}
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
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	//執行完畢後，直接開啟生成EXCEL檔案
    	if(autoOpenMode == "ON")
    		Runtime.getRuntime().exec("cmd.exe /C \"C:\\test1\\output.xls\""); 
    		
	}
}
