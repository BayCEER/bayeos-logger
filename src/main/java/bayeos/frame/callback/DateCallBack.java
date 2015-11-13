package bayeos.frame.callback;

import java.util.Date;

import bayeos.binary.ByteArray;
import bayeos.frame.DefaultFrameHandler;
import bayeos.frame.DateAdapter;



public class DateCallBack extends FrameCallBack<Date>{		
	public DateCallBack() {	
		parser.setFrameHandler(new DefaultFrameHandler() {
			@Override
			public void onResponse(byte type, byte[] data) {				
				value = DateAdapter.getDate(ByteArray.fromByteUInt32(data));
			}						
		});
	}
}
