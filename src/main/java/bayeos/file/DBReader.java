package bayeos.file;

import static bayeos.frame.FrameConstants.TimestampFrame;

import java.io.IOException;
import java.io.InputStream;


import bayeos.binary.ByteArray;

public class DBReader {
	

		
	InputStream in;
	long bytesRead = 0 ;
	
	public long getBytesRead() {
		return bytesRead;
	}

	public DBReader(InputStream in){
		this.in = in;		
	}
	
	
	public byte[] next() throws IOException  {		
		byte[] head = new byte[5];
		int b = in.read(head);		
		if (b != 5){
			return null;
		} else {			
			bytesRead = bytesRead + b;						
			int payLength = ByteArray.fromByteUInt8(head[4]);
			
			
			if (payLength > 0){
				byte pl[] = new byte[5+payLength];
				pl[0] = TimestampFrame;
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
		
	}
}
