package bayeos.frame.callback;

import bayeos.binary.ByteArray;
import bayeos.frame.DefaultFrameHandler;

public class ShortCallBack extends FrameCallBack<Short>{		
	public ShortCallBack() {	
		parser.setFrameHandler(new DefaultFrameHandler() {
			@Override
			public void onResponse(byte type, byte[] data) {				
				value = ByteArray.fromByteInt16(data);
			}						
		});
	}
}
