package bayeos.frame.callback;

import bayeos.frame.DefaultFrameHandler;

public class StringCallBack extends FrameCallBack<String>{		
	public StringCallBack() {	
		parser.setFrameHandler(new DefaultFrameHandler() {
			@Override
			public void onResponse(byte type, byte[] data) {				
				value = new String(data);
			}						
		});
	}
}
