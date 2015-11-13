package bayeos.serialframe;

public class ByteReadCallback extends AbstractReadCallBack<byte[]> {
	@Override
	public void onData(byte[] data) {
		
		super.onData(data);
		
		this.value = data;
	}
	
	@Override
	public void onAck(byte value) {
		super.onAck(value);
		
	}

	@Override
	public void onError(String msg) {
		super.onError(msg);
		
	}
	
	
}
