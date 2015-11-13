package bayeos.frame;

import bayeos.exception.CodeException;

public class FrameParserException extends CodeException {
	public FrameParserException(int code, String msg) {
		super(code,msg);
	}
}
