package bayeos.frame;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public abstract class DefaultFrameHandler implements FrameHandler {
	
	
	private String origin;
	private Long timestamp;
	private Integer rssi;	
	private Map<String,Set<Integer>> originMap = new HashMap<>();
	
	private String defaultOrigin;
	
	public DefaultFrameHandler() {		
		this.timestamp = new Date().getTime();
		this.rssi = null;
	}
	
	public DefaultFrameHandler(String defaultOrigin){
		this();
		this.origin = defaultOrigin;
		this.defaultOrigin = defaultOrigin;
	}
	
	
	public void resetHandler(){
		this.origin = defaultOrigin; 
		this.timestamp = new Date().getTime();
	}
		

	// Calculated by handler 
	public String getOrigin() {
		return origin;
	}		

	// Calculated by handler
	public Date getTimeStamp() {
		return new Date(timestamp);
	}

	// Calculated by handler
	public Map<String, Set<Integer>> getOriginMap(){		
		return originMap;
	}

	
	public Integer getRssi() {
		return rssi;
	}
	
	@Override
	public void onDelay(long millis) {
		timestamp = timestamp - millis;
	}

	@Override
	public void onOriginFrame(String origin) {
		this.origin = origin;			    
	}
	
	
	@Override
	public void onRoute(int myId, int panId) {
		StringBuilder b = new StringBuilder();
		b.append(origin).append("/XBee").append(panId).append(":")
				.append(myId);
		origin = b.toString();			
	};
	
	@Override
	public void onRouteRssi(int myId, int panId, int rssi) {
		onRoute(myId, panId);
		this.rssi = rssi;		
	}
	
	@Override
	public void onMillisecond(Date time) {
		timestamp = time.getTime();				
	} 
	
	
	@Override
	public void onTimeStamp(long time) {
		this.timestamp = time;	
	}
	
	@Override
	public void onMessage(String message) {
		addOriginKey();
	}
	
	@Override
	public void onCommand(byte type, byte[] value) {
		addOriginKey();
	}
	
	@Override
	public void onDataFrame(byte type, Hashtable<Integer, Float> values) {
		addOriginKey();
		originMap.get(getOrigin()).addAll(values.keySet());		
	}
	
	@Override
	public void onDataFrameEnd() {
		resetHandler();		
	}

	private void addOriginKey() {
		if (!originMap.containsKey(getOrigin())){
			originMap.put(getOrigin(), new HashSet<Integer>(10));	
		}
	}
	
	@Override
	public void onError(String message) {	
		addOriginKey();
	}

	@Override
	public void onResponse(byte type, byte[] value) {
		addOriginKey();
	}
	
	

			
	
}
