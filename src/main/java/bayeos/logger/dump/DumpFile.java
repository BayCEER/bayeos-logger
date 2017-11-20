package bayeos.logger.dump;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import bayeos.frame.FrameParserException;
import bayeos.frame.Parser;
import bayeos.logger.BulkReader;

public class DumpFile extends File {
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DumpFile.class);

	private static final String DATE_STRING = "yyyy-MM-dd-HH-mm-ss";
	private static final String FILE_EXTENSION = ".db";
		
	public Date getLastModified() {
		return new Date(lastModified());
	}

	public DumpFile(String pathname) {
		super(pathname);
	}
		
	public DumpFile(String path, String origin) {
		super(path,getFileName(origin));					
	}
	
	private static String getFileName(String origin) {
		StringBuffer f = new StringBuffer(origin);
		f.append("_");
		SimpleDateFormat df = new SimpleDateFormat(DATE_STRING);
		f.append(df.format(new Date()));
		f.append(FILE_EXTENSION);
		return f.toString();
	}


	public String getOrigin() {
		return getName().substring(0, getName().length() - (1 + DATE_STRING.length() + FILE_EXTENSION.length()));
	}
   
	public Long getLength() {
		return length();
	}
	
	
	public Map<String, Object> getInfo() throws IOException {
		
		Map<String,SummaryStatistics> stats = new Hashtable<>(64);
		
		long binFrameCount = 0;
		long errorMessageCount = 0;
		long dataFrameCount = 0;
		long messageCount = 0;
		long corruptFrameCount = 0;
		
		Date minDate = null;
		Date maxDate = null;
		
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(this))) {			
			BulkReader reader = new BulkReader(in);									
			byte[] data = null;
			while ((data = reader.readData()) != null) {
				
				Map<String, Object> frame;
				try {
					frame = Parser.parse(data,new Date(),getOrigin(),null);	
				if (!frame.containsKey("type")) {
					log.error("Incomplete frame:" + frame.toString());
					corruptFrameCount++;
					continue;
				}
				switch (frame.get("type").toString()) {
				case "BinaryFrame":
					binFrameCount++;
					break;
				case "ErrorMessage":
					errorMessageCount++;
					break;
				case "DataFrame":					
					dataFrameCount++;					
					
					Date ts = new Date(((long)(frame.get("ts"))/(1000*1000)));
					
					if (minDate == null && maxDate == null) {
						minDate = ts; maxDate = ts;
					}
					if (ts.after(maxDate)) {
						maxDate = ts;
					}
					if (ts.before(minDate)) {
						minDate = ts;
					}					
					for(Entry<String, Object> d: ((Map<String,Object>)frame.get("value")).entrySet()) {
						String key = frame.get("origin") + "/" + d.getKey();
						if (!stats.containsKey(key)){
							stats.put(key, new SummaryStatistics());							
						}												
						stats.get(key).addValue(((Number)d.getValue()).doubleValue());
					}
					break;
				case "Message":
					messageCount++;
					break;				
				default:
					break;
				}
				
				} catch (FrameParserException e) {
					log.error(e.getMessage());
					corruptFrameCount++;
				}	
			};
		}
		Map<String,Object> ret = new Hashtable();		
		ret.put("DataFrameCount", dataFrameCount);		
		ret.put("CorruptFrameCount", corruptFrameCount);		
		ret.put("BinaryFrameCount", binFrameCount);
		ret.put("ErrorMessageCount", errorMessageCount);		
		ret.put("MessageCount", messageCount);		
		ret.put("DataFrameStats", stats);		
		ret.put("MinDate", minDate);
		ret.put("MaxDate", maxDate);
		
		
		
	return ret;
		
	}

	}
