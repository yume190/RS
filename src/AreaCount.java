import java.util.ArrayList;

public class AreaCount {
	//總格子量 = len * wid
	private int scope[][];
	//左右格子數
	private int len;
	//上下格子數
	private int wid;
	//格子長和寬的單位
	private double scale;
	//
	//private int optimizationFlag;
	
	//the bound of the circle (up down left right)
	//圓的邊界 (上下左右)
	private int upBound;
	private int downBound;
	private int leftBound;
	private int rightBound;
	
	private Point centerOfNewCircle;
	
	AreaCount(int length,int width,double scale){
		this.scale = scale;
		len = (int)(length/this.scale);
		wid = (int)(width/this.scale);
		this.scope = new int[len][wid];
	}
	
	public double getCoverageRate(){
		return (double)this.getCoverageAmount() / this.getScopeAmount();
	}
	
	public int getScopeAmount(){
		return this.len * this.wid;
	}
	
	public int getCoverageAmount(){
		int sum = 0;
		for(int a = 0;a < this.scope.length;a++){
			for(int b = 0;b < this.scope[a].length;b++){
				if(this.scope[a][b] == 0);
				else
					sum += 1;
			}
		}
		return sum;
	}
	
	public Integer[] getCoverageAmountDetail(){
		ArrayList<Integer> sum = new ArrayList<Integer>();
		sum.add(0);
		for(int a = 0;a < this.scope.length;a++){
			for(int b = 0;b < this.scope[a].length;b++){
				if(this.scope[a][b] > sum.size() - 1)
					for(int count = 0;count <= this.scope[a][b] - sum.size() + 1;count++)
						sum.add(0);
				sum.set(this.scope[a][b],sum.get(this.scope[a][b])+1);
			}
		}
		//for(int a = 0;a <sum.size();a++)
			//System.out.println("The coverage [" + a + "] : " + sum.get(a));
		return sum.toArray(new Integer[sum.size()]);
	}
	
	/**
	 * 清空全部格子資料
	 */
	public void flush(){
		this.scope = new int[len][wid];
	}
	
	////////////////////////////////////////////////////////////////
	public void show(){
		//this.scope[0][9]=1;
		/*
		System.out.println(this.upBound);
		System.out.println(this.downBound);
		System.out.println(this.leftBound);
		System.out.println(this.rightBound);
		*/
		for(int a = this.scope.length-1; a >= 0 ;a--){
			for(int b = 0;b < this.scope[a].length;b++){
				System.out.print(scope[b][a]);
			}
			System.out.println();
		}
	}
	
	public void addCircle(double xx,double yy,double rad){
		double centerX = xx / scale;
		double centerY = yy / scale;
		double radius = rad / scale;
		this.centerOfNewCircle = new Point(xx,yy);
		setBound(centerX,centerY,radius);
		paintCircle(centerX,centerY,radius);
	}
	
	private void setBound(double centerX,double centerY,double radius){
		setUpBound(centerX,centerY,radius);
		setDownBound(centerX,centerY,radius);
		setLeftBound(centerX,centerY,radius);
		setRightBound(centerX,centerY,radius);
	}
	
	//at 10 * 10 area
	//put a circle (5,5) and radius is 5
	//then y + r > wid
	//5 + 5 > 10
	//bound -> 9
	private void setUpBound(double centerX,double centerY,double radius){
		if ((int)(Math.floor(centerY + radius)) > wid - 1)
			upBound = wid - 1;
		else
			upBound = (int)(Math.floor(centerY + radius));
	}

	private void setDownBound(double centerX,double centerY,double radius){
		if ((int)(Math.floor(centerY - radius)) < 0)
			downBound = 0;
		else
			downBound= (int)(Math.floor(centerY -  radius));
	}
	
	private void setLeftBound(double centerX,double centerY,double radius){
		if ((int)(Math.floor(centerX - radius)) < 0)
			leftBound = 0;
		else
			leftBound= (int)(Math.floor(centerX - radius));
	}
	
	private void setRightBound(double centerX,double centerY,double radius){
		if ((int)(Math.floor(centerX + radius)) > len - 1)
			rightBound = len - 1;
		else
			rightBound = (int)(Math.floor(centerX + radius));
	}
	
	private void paintCircle(double centerX,double centerY,double radius){
		paintCircleLU(centerX,centerY,radius);
		paintCircleRU(centerX,centerY,radius);
		paintCircleLD(centerX,centerY,radius);
		paintCircleRD(centerX,centerY,radius);
	}
	
	private void paintCircleLU(double centerX,double centerY,double radius){
		for(int yStart = upBound;yStart >= (int)centerY;yStart--){
			//////////////////////////////////-1
			for(int xStart = (int)centerX - 1;xStart >= leftBound;xStart--){
				//if(inCircle(this.getLeftUpPoint(xStart, yStart),radius))
				if(inCircle(this.getCenterPoint(xStart, yStart),radius))
					this.scope[xStart][yStart] += 1;
				//else
					//break;
			}
		}
	}
	private void paintCircleRU(double centerX,double centerY,double radius){
		for(int yStart = upBound;yStart >= (int)centerY;yStart--){
			for(int xStart = (int)centerX;xStart <= rightBound;xStart++){
				//if(inCircle(this.getRightUpPoint(xStart, yStart),radius))
				if(inCircle(this.getCenterPoint(xStart, yStart),radius))
					this.scope[xStart][yStart] += 1;
				//else
					//break;
			}
		}
	}
	private void paintCircleLD(double centerX,double centerY,double radius){
		for(int yStart = downBound;yStart < (int)centerY;yStart++){
			for(int xStart = (int)centerX-1;xStart >= leftBound;xStart--){
				//if(inCircle(this.getLeftDownPoint(xStart, yStart),radius))
				if(inCircle(this.getCenterPoint(xStart, yStart),radius))
					this.scope[xStart][yStart] += 1;
				//else
					//break;
			}
		}
	}
	private void paintCircleRD(double centerX,double centerY,double radius){
		for(int yStart = downBound;yStart < (int)centerY;yStart++){
			for(int xStart = (int)centerX;xStart <= rightBound;xStart++){
				//if(inCircle(this.getRightDownPoint(xStart, yStart),radius))
				if(inCircle(this.getCenterPoint(xStart, yStart),radius))
					this.scope[xStart][yStart] += 1;
				//else
					//break;
			}
		}
	}
	
	private Point getLeftUpPoint(double xx, double yy){
		return new Point(xx*scale,yy*scale+scale);
	}
	
	private Point getLeftDownPoint(double xx, double yy){
		return new Point(xx*scale,yy*scale);
	}
	
	private Point getRightUpPoint(double xx, double yy){
		return new Point(xx*scale+scale,yy*scale+scale);
	}
	
	private Point getRightDownPoint(double xx, double yy){
		return new Point(xx*scale+scale,yy*scale);
	}
	
	private Point getCenterPoint(double xx, double yy){
		return new Point(xx * scale + 0.5 * scale,yy * scale + 0.5 * scale);
	}
	
	private boolean inCircle(Point zone,double c){
		double a = Math.pow(this.centerOfNewCircle.getX() - zone.getX() ,2);
		double b = Math.pow(this.centerOfNewCircle.getY() - zone.getY() ,2);
		c = c * this.scale;
		c = c * c;
		if(a + b <= c)
			return true;
		else
			return false;
	}
	
	public class Point{
		
		private double x;
		private double y;
		
		Point(double xx, double yy){
			x = xx;
			y = yy;
		}
		
		public double getX(){
			return x;
		}
		
		public double getY(){
			return y;
		}
		
	}
}