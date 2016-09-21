package bayeos.frame;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public interface FrameEventHandler {
	
	void newOrigin(String origin);
	void newChannels(String origin, List<String> channels);
	void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi);
	void message(String origin, Date timeStamp, String message);
	void error(String origin, Date timeStamp, String message);
	void binary(long pos, byte[] value);

}