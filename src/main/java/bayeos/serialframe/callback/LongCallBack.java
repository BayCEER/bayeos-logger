package bayeos.frame.callback;

import bayeos.binary.ByteArray;

public class LongCallBack extends ReadCallBack<Long>{		
	@Override
	public void onData(byte[] data) {
		super.onData(data);
		value = ByteArray.fromByteUInt32(data);
	}	
}
