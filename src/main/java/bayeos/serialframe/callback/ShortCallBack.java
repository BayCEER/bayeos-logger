package bayeos.frame.callback;

import bayeos.binary.ByteArray;

public class ShortCallBack extends ReadCallBack<Short>{
	
	@Override
	public void onData(byte[] data) {
		super.onData(data);
		value = ByteArray.fromByteInt16(data);		
	}	
}
