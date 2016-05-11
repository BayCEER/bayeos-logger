package bayeos.frame;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class DefaultFrameHandler implements FrameHandler {

	private String origin;
	private Long timestamp;
	private Integer rssi;
	private Map<String, List<String>> originMap = new Hashtable<>();

	private String defaultOrigin;

	public DefaultFrameHandler() {
		initFrame();
	}

	public DefaultFrameHandler(String defaultOrigin) {
		this.defaultOrigin = defaultOrigin;
		initFrame();
	}

	private void initFrame() {
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
		initFrame();		
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
		b.append(origin).append("/XBee").append(panId).append(":").append(myId);
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
		onMessage(getOrigin(), getTimeStamp(), message);
	}

	@Override
	public void onError(String message) {
		addOriginKey();
		onError(getOrigin(), getTimeStamp(), message);
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
			onNewChannels(origin,newKeys);
		}
						
		onDataFrame(origin, getTimeStamp(), values, getRssi());
		initFrame();		
	}

	
	private void addOriginKey() {
		if (!originMap.containsKey(getOrigin())) {
			originMap.put(getOrigin(), new ArrayList<String>());
			onNewOrigin(getOrigin());
		}
	}

	public void onNewOrigin(String origin) {

	}

	public void onNewChannels(String origin, List<String> channels) {

	}

	public void onDataFrame(String origin, Date timeStamp, Hashtable<String, Float> values, Integer rssi) {

	}

	public void onMessage(String origin, Date timeStamp, String message) {

	}

	public void onError(String origin, Date timeStamp, String message) {

	}

}
