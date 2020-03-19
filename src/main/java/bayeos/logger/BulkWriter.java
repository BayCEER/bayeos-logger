package bayeos.logger;

import java.io.IOException;
import java.io.RandomAccessFile;

import bayeos.binary.ByteArray;


public class BulkWriter {
			
	RandomAccessFile out;
		
	private long lastBulkPosition = -1;
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BulkWriter.class);
	
	public BulkWriter(RandomAccessFile out){		
		this.out = out;
	}
	
			
	public void write(byte[] data) throws IOException {
				
		/*	Data in format: 		 
		 *  [0xa][(unsigned long)  0][.... Binary Bytes  --]
			[0xa][(unsigned long) 95][.... Binary Bytes  --]
			[0xa][(unsigned long)190][.... Binary Bytes  --]
			[0xa][(unsigned long)285][.... Binary Bytes  --]
			[0xa][(unsigned long)380][.... Binary Bytes]
		*/ 	
							
		if (data.length < 6){
			log.warn("Empty bulk received.");
			return;
		}		
				
		if (data[0]!=0xa){
			throw new IOException("Bulk format exception, wrong start byte");
		}	 
		long pos = ByteArray.fromByteUInt32(data,1);				
		if (pos <= lastBulkPosition) {
			log.warn("Data not in order.");
		}		
		out.seek(pos);
		out.write(data, 5, data.length - 5);
		lastBulkPosition = pos;					
	}
}
