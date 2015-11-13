package bayeos.frame.callback;

import bayeos.frame.FrameParser;
import bayeos.frame.FrameParserException;
import bayeos.serialframe.AbstractReadCallBack;


public class FrameCallBack<T> extends AbstractReadCallBack<T> {	
	FrameParser parser = new FrameParser();			
	@Override
	public void onData(byte[] data) {			
		try {
			super.onData(data);			
			parser.parse(data);				
		} catch (FrameParserException e){
			onError(e.getMessage());			
		}
	}		
}


