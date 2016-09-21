package bayeos.frame;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class DefaultFrameHandler implements FrameHandler, FrameEventHandler {

	private String origin;
	private Long timestamp;
	private Integer rssi;
	private Map<String, List<String>> originMap = new Hashtable<>();

	private String defaultOrigin;

	public DefaultFrameHandler() {
		startOfFrame();
	}

	public DefaultFrameHandler(String defaultOrigin) {
		this.defaultOrigin = defaultOrigin;
		startOfFrame();
	}

	public void startOfFrame() {
		this.origin = this.defaultOrigin;
		this.timestamp = new Date().getTime();
		this.rssi = null;
	}

	public String getOrigin() {
		return origin;
	}

	public Date getTimeStamp() {
		return new Date(timestamp);
	}

	public Map<String, List<String>> getOriginMap() {
		return originMap;
	}

	public Integer getRssi() {
		return rssi;
	}
	
	@Override
	public void endOfFrame() {
		startOfFrame();		
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
		StringBuffer b = new StringBuffer(origin);
		b.append("/XBee").append(panId).append(":").append(myId);
		this.origin = b.toString();
	};
	
	@Override
	public void onRoute(String origin) {
		StringBuffer b = new StringBuffer(this.origin);
		b.append("/").append(origin);
		this.origin = b.toString();						
	};
	
	@Override
	public void onBinary(long pos, byte[] value){
		binary(pos, value);
	}
	

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
		message(getOrigin(), getTimeStamp(), message);
	}

	@Override
	public void onError(String message) {
		addOriginKey();
		error(getOrigin(), getTimeStamp(), message);
	}

	@Override
	public void onResponse(byte type, byte[] value) {
		addOriginKey();
	}

	@Override
	public void onCommand(byte type, byte[] value) {
		addOriginKey();
	}

	@Override
	public void onDataFrame(byte type, Hashtable<String, Float> values) {
		addOriginKey();		
		String origin = getOrigin();		
		List<String> oldKeys = originMap.get(origin);		
		List<String> newKeys = new ArrayList<String>();
		
		for(String channelNr: values.keySet()){
			if (!oldKeys.contains(channelNr)){
				newKeys.add(channelNr);				
			}
		}
				
		if (newKeys.size()>0){
			originMap.get(origin).addAll(newKeys);
			newChannels(origin,newKeys);
		}
						
		dataFrame(origin, getTimeStamp(), values, getRssi());
		startOfFrame();		
	}

	
	private void addOriginKey() {
		if (!originMap.containsKey(getOrigin())) {
			originMap.put(getOrigin(), new ArrayList<String>());
			newOrigin(getOrigin());
		}
	}

	
	@Override
	public void newOrigin(String origin) {

	}

	@Override
	public void newChannels(String origin, List<String> channels) {

	}

	@Override
	public void dataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {

	}

	@Override
	public void message(String origin, Date timeStamp, String message) {

	}

	@Override
	public void error(String origin, Date timeStamp, String message) {

	}
	
	@Override
	public void binary(long pos, byte[] value){
		
	}

}
