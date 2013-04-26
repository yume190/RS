
public class Message {
	
	public static final int SINK = -1;
	public static final int BROADCAST = -2;
	
	public static final String PHASE1 = "PHASE1";
	public static final String PHASE2 = "PHASE2";
	public static final String PHASE3 = "PHASE3";
	public static final String PHASE4 = "PHASE4";
	
	public static final String HOP = "HOP";
	public static final String CHN = "CHN";
	public static final String JOIN = "JOIN";
	public static final String THRESHOLD = "THRESHOLD";
	public static final String JOIN2 = "JOIN2";
	public static final String TIMESLOT = "TIMESLOT";
	public static final String REPORTDATA = "REPORTDATA";
	public static final String PERMISSION = "PERMISSION";
	public static final String COMPLETION = "COMPLETION";
	
	public static final String ALL = "ALL";
	public static final String RC1 = "RC1";
	public static final String RC2 = "RC2";
	
	public static final int SINKCHANNEL = -1;
	public static final int PRIMARYCHANNEL = -2;
	
	public String command;
	public int channel;
	public String range;
	public int source;
	public int destination;
	public ARCNode sourceNode;
	//public int useChannel;
	public double nowTime;
	//public int slot;
	/**Message �غc�l �ݿ�J �ӷ�NODE �ت��aNODE �ت� �ϥ��W�D �ǿ�d��*/
	Message(int inSource,int inDestination,String inCommand,int inChannel,String inRange){
		source = inSource;
		destination = inDestination;
		command = inCommand;
		channel = inChannel;
		range = inRange;
	}
	/**Message �غc�l �ݿ�J �ӷ�NODE �ت��aNODE �ت� �ϥ��W�D �ǿ�d�� �ӷ�NODE������*/
	Message(int inSource,int inDestination,String inCommand,int inChannel,String inRange,ARCNode inSourceNode){
		source = inSource;
		destination = inDestination;
		command = inCommand;
		channel = inChannel;
		range = inRange;
		this.sourceNode = inSourceNode;
	}
	
	/*
	Message(int inSource,int inDestination,String inCommand,int inChannel,String inRange,ARCNode inSourceNode,int inSlot){
		source = inSource;
		destination = inDestination;
		command = inCommand;
		channel = inChannel;
		range = inRange;
		this.sourceNode = inSourceNode;
		slot = inSlot;
	}
	*/
	/**Message �غc�l �ݿ�J �ӷ�NODE �ت��aNODE �ت� �ϥ��W�D �ǿ�d�� �ӷ�NODE������ �ǰe�ʥ]���ɶ�*/
	Message(int inSource,int inDestination,String inCommand,int inChannel,String inRange,ARCNode inSourceNode,double inNowTime){
		source = inSource;
		destination = inDestination;
		command = inCommand;
		channel = inChannel;
		range = inRange;
		this.sourceNode = inSourceNode;
		this.nowTime = inNowTime;
	}
	/*
	Message(int inSource,int inDestination,String inCommand,int inChannel,String inRange,ARCNode inSourceNode){
		source = inSource;
		destination = inDestination;
		command = inCommand;
		channel = inChannel;
		range = inRange;
		sourceNode = inSourceNode;
	}
	*/
}
