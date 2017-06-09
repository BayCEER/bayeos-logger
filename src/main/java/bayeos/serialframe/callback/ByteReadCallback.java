package bayeos.frame.callback;

public class ByteReadCallback extends ReadCallBack<byte[]> {
	@Override
	public void onData(byte[] data) {		
		super.onData(data);		
		this.value = data;
	}
}
