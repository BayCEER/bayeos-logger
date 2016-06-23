package bayeos.logger;

import java.io.IOException;
import java.io.OutputStream;

import bayeos.binary.ByteArray;


public class BulkWriter {
	
	OutputStream out;
	
		
	private long lastBulkPosition = -1;
	
	public BulkWriter(OutputStream out){		
		this.out = out;
	}
	
		
	/*	Function to parse data in format and write bytes to out
	 * 
	 *  [0xa][(unsigned long)  0][.... Binary Bytes  --]
		[0xa][(unsigned long) 95][.... Binary Bytes  --]
		[0xa][(unsigned long)190][.... Binary Bytes  --]
		[0xa][(unsigned long)285][.... Binary Bytes  --]
		[0xa][(unsigned long)380][.... Binary Bytes]
	*/ 	
	
	public void write(byte[] data) throws IOException {
		
		if (data.length < 5){
			// Empty bulk received.
			return;
		}
		
		if (data[0]!=0xa){
			throw new IOException("Bulk format exception, wrong start byte");
		}
	 
		long pos = ByteArray.fromByteInt32(data,1);		
		if (pos > lastBulkPosition){			
			for(int i=5;i<data.length;i++){
				out.write(data[i]);
			}
			lastBulkPosition = pos;			
			
		} else {
			throw new IOException("Bulk position not ascending.");
		}

		
	
		
		
		
	}
}
