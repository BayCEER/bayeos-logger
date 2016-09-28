package bayeos.binary;

public class CheckSum {
	int sum = 0;
	
	public int oneByte(){				
		return 0xff - (sum & 0xff);
	}
	
	public int twoByte(){		
		return 0xffff - (sum & 0xffff);		
		 		
	}						
	
	
	public void addByte(byte b){
	  sum+=(b & 0xff);
	}
	
	public void addBytes(byte[] values){
		for (byte i : values) {
			addByte(i);
		}
	}	
}