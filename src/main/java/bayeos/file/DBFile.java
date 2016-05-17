package bayeos.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import bayeos.frame.DefaultFrameHandler;
import bayeos.frame.FrameParser;
import bayeos.frame.FrameParserException;

public class DBFile {

	private static final String TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
	private static final String TIME_ZONE = "GMT+1";
	private static final SimpleDateFormat df;

	static {
		df = new SimpleDateFormat(TIME_FORMAT);
		df.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
	}

	private String origin;

	public DBFile(String origin) {
		this.origin = origin;
	}

	public static void main(String[] args) {
		try {
			FileInputStream in = new FileInputStream(new File(args[0]));
			new DBFile(args[1]).read(in);
			in.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void read(InputStream in) throws IOException {
		DBReader r = new DBReader(in);
		DefaultFrameHandler myHandler = new DefaultFrameHandler(origin) {
			
			@Override
			public void message(String origin, Date timeStamp, String message) {
				System.out.println("#\"Message\":\"" + message + "\"");
			}
						
			@Override
			public void error(String origin, Date timeStamp, String message) {				
				System.out.println("#\"Error\":\"" + message + "\"");
			}
			
			@Override
			public void newOrigin(String origin) {
				System.out.println("#\"New Origin\":\"" + origin + "\"");			
			}
			
			@Override
			public void newChannels(String origin, List<String> channels) {
				System.out.println("#\"New Channels:" + channels +  " for Origin\":\"" + origin + "\"");
			}
			
			@Override
			public void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {
				System.out.println(df.format(getTimeStamp())  + ":" +  values.toString() );
			}

		};
		FrameParser parser = new FrameParser(myHandler);
		byte[] data = null;
		while ((data = r.next()) != null) {
			try {
				parser.parse(data);
			} catch (FrameParserException e) {
				System.err.println(e.getMessage());
			}
		}
		System.out.println(myHandler.getOriginMap());
	}

	

}
