package bayeos.serialframe;

public interface SerialFrameHandler {		
	void onData(byte apiType , byte[] payload);	
	void onError(String msg);
}
