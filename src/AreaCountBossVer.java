import java.util.ArrayList;

public class AreaCountBossVer {
	//總格子量 = len * wid
	private int scope[][];
	//左右格子數
	private int len;
	//上下格子數
	private int wid;
	
	//the bound of the circle (up down left right)
	//圓的邊界 (上下左右)
	private int upBound;
	private int downBound;
	private int leftBound;
	private int rightBound;
	
	AreaCountBossVer(int length,int width){
		len = length;
		wid = width;
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
		double centerX = (int)xx ;
		double centerY = (int)yy ;
		double radius = (int)rad ;
		
		setBound(centerX,centerY,radius);
		paintCircle(centerX,centerY,radius);
	}
	
	private void setBound(double centerX,double centerY,double radius){
		setUpBound(centerX,centerY,radius);
		setDownBound(centerX,centerY,radius);
		setLeftBound(centerX,centerY,radius);
		setRightBound(centerX,centerY,radius);
	}
	

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
		for(int xStart = leftBound;xStart <= rightBound;xStart++){
			for(int yStart = downBound;yStart <= upBound;yStart++){
				double a,b,c;
				a = Math.pow(centerX - xStart, 2);
				b = Math.pow(centerY - yStart, 2);
				c = Math.sqrt(a + b);
				//System.out.println("C before : " + c);
				//c = Math.round(c);
				//System.out.println("C after : " + c);
				if(inCircle(c,radius)){
					this.scope[xStart][yStart] += 1;
				}
			}
		}
	}
	
	private boolean inCircle(double c,double radius){
		if(c <= radius)
			return true;
		else
			return false;
	}
}
