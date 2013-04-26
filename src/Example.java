
public class Example {
	public static void main (String[] args)  {
		AreaCount AC = new AreaCount(10,10,1);
	
		AC.addCircle(5, 4, 3);
		AC.show();
	
		System.out.println("The coverage rate :" + AC.getCoverageRate());
		System.out.println("The coverage :" + AC.getCoverageAmount());
		for(int a = 0;a < AC.getCoverageAmountDetail().length;a++){
			System.out.println("The coverage [" + a + "] : " + AC.getCoverageAmountDetail()[a]);
		}
		
		AreaCountBossVer AC2 = new AreaCountBossVer(10,10);
 		AC2.addCircle(5, 4, 3);
		AC2.show();
		System.out.println("The coverage rate :" + AC2.getCoverageRate());
		System.out.println("The coverage :" + AC2.getCoverageAmount());
		for(int a = 0;a < AC2.getCoverageAmountDetail().length;a++){
			System.out.println("The coverage [" + a + "] : " + AC2.getCoverageAmountDetail()[a]);
		}
	}
}
