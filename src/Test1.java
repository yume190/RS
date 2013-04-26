import java.io.*;
import java.util.Arrays;
import jxl.*;

public class Test1 {
	public static void main(String[] args) throws Exception{

		double Rc1 = 20.0;
		double Rc2 = 60.0;
		double Rs = 5.0;
		double P = 5.0;
		double C0 = 0.9;
		double l = 100.0;
		int bit = 32;
		String autoOpenMode = "O";
		//int testA=100;
		//int testB=360;
		
		double averageCHN=0;
		double averageActiveNCHN=0;
		
		//建立SINK 和 ARC NODE
		Sink sink = new Sink(Rc1,Rc2);
		RS nodes[] = new RS[1000];
		//原本版本
		
		for(int i=0;i<nodes.length;i++){
			nodes[i] = new RS(l*Math.random(),l*Math.random(),Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
			nodes[i].self = nodes[i];
			nodes[i].power.owner = nodes[i];
		}
		
		
		//測試之一
		/*
		for(int i=0;i<testA;i++){
			nodes[i] = new ARCNode(60*Math.random(),60*Math.random(),Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
			nodes[i].self = nodes[i];
			nodes[i].power.owner = nodes[i];
			nodes[i].nodeState = ARCNode.DEAD;
		}
		for(int i=testA;i<360;i++){
			nodes[i] = new ARCNode(60*Math.random(),60*Math.random(),Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
			nodes[i].self = nodes[i];
			nodes[i].power.owner = nodes[i];
		}
		for(int i=360;i<testB;i++){
			double a=100*Math.random(),b=100*Math.random();
			while(true){
				a=100*Math.random();
				b=100*Math.random();
				if(a>60 || b>60){
					nodes[i] = new ARCNode(a,b,Rs,Rc1,Rc2,0,C0,i,l,l,nodes.length,bit);
					nodes[i].self = nodes[i];
					nodes[i].power.owner = nodes[i];
					nodes[i].nodeState = ARCNode.DEAD;
					break;
				}
			}
		}
		for(int i=testB;i<nodes.length;i++){
			double a=100*Math.random(),b=100*Math.random();
			while(true){
				a=100*Math.random();
				b=100*Math.random();
				if(a>60 || b>60){
					nodes[i] = new ARCNode(a,b,Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
					nodes[i].self = nodes[i];
					nodes[i].power.owner = nodes[i];
					break;
				}
			}
		}
		*/
		
		//測試之二
		/*
		for(int i=0;i<testA;i++){
			nodes[i] = new ARCNode(l*Math.random(),l*Math.random(),Rs,Rc1,Rc2,0,C0,i,l,l,nodes.length,bit);
			nodes[i].self = nodes[i];
			nodes[i].power.owner = nodes[i];
			nodes[i].nodeState = ARCNode.DEAD;
		}
		for(int i=testA;i<nodes.length;i++){
			nodes[i] = new ARCNode(l*Math.random(),l*Math.random(),Rs,Rc1,Rc2,P,C0,i,l,l,nodes.length,bit);
			nodes[i].self = nodes[i];
			nodes[i].power.owner = nodes[i];
		}*/
		
		
		//建立鄰近關係
		sink.ALLNodes = nodes;
		sink.searchNeighborNode();
		for(int i=0;i<nodes.length;i++){
			nodes[i].searchNeighborNode(nodes);
		}
		
		try{
			//構建Workbook物件, 唯讀Workbook物件
    		//創建可寫入的Excel工作薄
    		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(new File("C:/test1/outputC.xls"));
    		
    		//創建Excel工作表(Sheet)
    		jxl.write.WritableSheet ws1 = wwb.createSheet("Test Sheet 1", 0);
    		
    		//1.添加Label對象
    		ws1.addCell(new jxl.write.Label(0, 0, "Round"));
    		ws1.addCell(new jxl.write.Label(1, 0, "實際覆蓋率上限"));
    		ws1.addCell(new jxl.write.Label(2, 0, "實際覆蓋率下限"));
    		ws1.addCell(new jxl.write.Label(3, 0, "CHN總數"));
    		ws1.addCell(new jxl.write.Label(4, 0, "Active NCHN總數"));
    		
    		for(int round=1;round<=100;round++){
				//PHASE 1
				sink.CHNSelection();
				Arrays.sort(nodes);
				for(int i=0;i<nodes.length;i++)
					nodes[i].CHN();
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
			 		
				sink.steadyPhase();
				
				{
					int a=0,b=0;
					double Cu=0,Cd=0;
					for(int i=0;i<nodes.length;i++){
						if(nodes[i].nodeState == ARCNode.CHN)
							a++;
						else if(nodes[i].nodeState == ARCNode.ACTIVE)
							b++;
					}
					averageCHN+=a;
					averageActiveNCHN+=b;
					ws1.addCell(new jxl.write.Number(0, round, round));
					ws1.addCell(new jxl.write.Number(3, round, a));
					ws1.addCell(new jxl.write.Number(4, round, b));
					Cu =1 
							- (1 - a * Math.PI * Rs * Rs / l / l ) 
							* (Math.pow( 1- Math.PI * Rs * Rs / l / l, (double)b));
					Cd =1 
							- (1 - a * Math.PI * Rs * Rs / (l + Rs) / (l + Rs)) 
							* (Math.pow( 1 - Math.PI * Rs * Rs / l / l, (double)b));
					ws1.addCell(new jxl.write.Number(1, round, Cu));
					ws1.addCell(new jxl.write.Number(2, round, Cd));
					
				}
    		}
    		ws1.addCell(new jxl.write.Number(3, 101, averageCHN/100));
			ws1.addCell(new jxl.write.Number(4, 101, averageActiveNCHN/100));
			System.out.println("平均CHN數量 ： " + String.valueOf(averageCHN/100));
			System.out.println("平均Active NCHN數量 ： " + String.valueOf(averageActiveNCHN/100));
			System.out.println((averageCHN+averageActiveNCHN)/100);
			System.out.println(1-Math.pow(1 - (Math.PI * Rs * Rs / l / l), (averageCHN+averageActiveNCHN)/100));
			
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
    		Runtime.getRuntime().exec("cmd.exe /C \"C:\\test1\\outputC.xls\""); 
		
	}
}
