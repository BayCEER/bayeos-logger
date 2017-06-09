package bayeos.frame.callback;

import java.util.Date;

import bayeos.binary.ByteArray;
import bayeos.frame.DateAdapter;

public class DateCallBack extends ReadCallBack<Date>{		
	@Override
	public void onData(byte[] data) {
		super.onData(data);
		value = DateAdapter.getDate(ByteArray.fromByteUInt32(data));
	}	
}
