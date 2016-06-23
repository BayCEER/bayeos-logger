package bayeos.frame.callback;

public class StringCallBack extends ReadCallBack<String>{
	@Override
	public void onData(byte[] data) {
		super.onData(data);
	    value = new String(data);
	}
}
