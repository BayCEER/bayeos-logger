package bayeos.binary;

public class CheckSum {
	int sum = 0;
	
	public int get(){				
		return 0xff - (0xff & sum);
	}						
	public void addByte(int b){
	  sum+=b;
	}
	
	public void addBytes(int[] values) {
		for (int i : values) {
			addByte(i);
		}		
	 } 
}