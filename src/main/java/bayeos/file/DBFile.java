package bayeos.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.SortedSet;
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
			public void onMessage(String origin, Date timeStamp, String message) {
				System.out.println("#\"Message\":\"" + message + "\"");
			}
						
			@Override
			public void onError(String origin, Date timeStamp, String message) {				
				System.out.println("#\"Error\":\"" + message + "\"");
			}
			
			@Override
			public void onNewOrigin(String origin) {
				System.out.println("#\"New Origin\":\"" + origin + "\"");			
			}
			
			@Override
			public void onNewChannels(String origin, SortedSet<Integer> channels) {
				System.out.println("#\"New Channels:" + channels +  " for Origin\":\"" + origin + "\"");
			}
			
			@Override
			public void onDataFrame(String origin, Date timeStamp, Hashtable<Integer, Float> values, Integer rssi) {
				System.out.println(df.format(getTimeStamp())  + getCSV(values));
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

	@SuppressWarnings("unused")
	private String getCSV(Hashtable<Integer, Float> values) {
		StringBuilder sb = new StringBuilder();
		Integer max = 0;
		for (Integer nr : values.keySet()) {
			if (max <= nr)
				max = nr;
		}
		for (int i = 1; i <= max; i++) {
			sb.append(',');
			Float v = values.get(i);
			if (v != null) {
				sb.append(v);
			}
		}
		return sb.toString();
	}

}
