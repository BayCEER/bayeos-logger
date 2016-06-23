package bayeos.frame.callback;
/**
 * A callback interface to get response back from serial frame device 
 * @author oliver
 *
 */
public interface SerialCallback<T>
{			
	public void onData(byte[] data);
	public void onError(String msg);
	public void onAck(byte value);	
	
	public T getValue();	
	public boolean hasError();
	public String getErrorMsg();
	public byte getAck();
	
	public boolean isRunning();
}

