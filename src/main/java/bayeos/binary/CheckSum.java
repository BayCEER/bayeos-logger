package bayeos.binary;

public class CheckSum {
	int sum = 0;
	
	public int oneByte(){				
		return 0xff - (0xff & sum);
	}
	
	public int twoByte(){		
		return 0xffff - (0xffff & sum );		
		 		
	}						
	
	
	public void addByte(byte b){
	  sum+=b;
	}
	
	public void addBytes(byte[] values){
		for (byte i : values) {
			addByte(i);
		}
	}	
}