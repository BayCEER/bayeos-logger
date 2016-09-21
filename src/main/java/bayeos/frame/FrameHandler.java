package bayeos.frame;

import java.util.Date;
import java.util.Hashtable;

public interface FrameHandler {
	void onDataFrame(byte type, Hashtable<String, Float> values) ;  // 0x1
	void onCommand(byte type, byte[] value) ;						// 0x2
	void onResponse(byte type, byte[] value); 						// 0x3
	void onMessage(String message);									// 0x4
	void onError(String message);									// 0x5
	void onRoute(int myId, int panId);								// 0x6
	void onDelay(long millis);										// 0x7
	void onRouteRssi(int myId, int panId, int rssi);				// 0x8
	void onTimeStamp(long time);									// 0x9
	void onBinary(long pos, byte[] value);							// 0xa
	void onOriginFrame(String origin);								// 0xb
	void onMillisecond(Date time);									// 0xc
	void onRoute(String origin);									// 0xd
 
	void endOfFrame();
	void startOfFrame();	
	
}
