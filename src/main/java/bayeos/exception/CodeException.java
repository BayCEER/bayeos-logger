package bayeos.exception;


/**
 * Exception class with user defined code number    
 * @author oliver
 *
 */
public class CodeException extends Exception {
	String msgFormat = "Error %d: %s";
	int code = 0;
	String msg;
	
	public CodeException(int code, String msg) {		
		super();
		this.msg = msg;
		this.code = code;
	}
	
	@Override
	public String getMessage() {
		return String.format(msgFormat, code,this.msg);
	}

	public int getCode() {
		return code;
	}
	
	
	
}
