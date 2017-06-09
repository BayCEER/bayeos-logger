package bayeos.logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import bayeos.frame.DateAdapter;
import bayeos.frame.FrameConstants;


public class BulkReader {
	
	
	InputStream in;
	
	long maxStart;
	long minStart;
	
	long bytesRead = 0 ;

	public BulkReader(InputStream in) {
		this.in = in;		
	}

	public byte[] readData() throws IOException {
				
		
		byte[] head = new byte[5];		
		int b = in.read(head);
		
		if (b == -1) return null;
		bytesRead = bytesRead + b;
		
		long d = ((head[3] & 0xFFL) << 24)	+ ((head[2] & 0xFFL) << 16)	+ ((head[1] & 0xFFL) << 8)	+ (head[0] & 0xFFL);															
		int payLength = head[4] & 0xff;		
						
		if (payLength > 0){			
			maxStart = (d>maxStart||maxStart==0)?d:maxStart;				
			minStart = (d<minStart||minStart==0)?d:minStart;											
			byte pl[] = new byte[5+payLength];
			pl[0] = FrameConstants.TimestampFrame;
			pl[1] = head[0];
			pl[2] = head[1];
			pl[3] = head[2];
			pl[4] = head[3];	 
			for(int z=0;z<payLength;z++){
				pl[z+5] = (byte) in.read();
				bytesRead = bytesRead + 1 ;
			}		
			return pl; 									
		} else {
			return null;
		}
		
		
	}

	public long getBytesRead(long bytes) {
		if (bytesRead < bytes){
			return bytesRead; 
		} else {
			return bytes;
		}
	}

	public Date getMinStart() {
			return DateAdapter.getDate(minStart); 
	}

	public Date getMaxStart() {
		   return DateAdapter.getDate(maxStart);
	}

}
