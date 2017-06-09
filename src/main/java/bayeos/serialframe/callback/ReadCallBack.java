package bayeos.frame.callback;

public abstract class ReadCallBack<T> implements SerialCallback<T> {
	
	public byte ack;
	public boolean error = false;
	public String errorMsg = "";
	public T value;
	
	public boolean running = true;
	 
	/*
	 * Should be overwritten by subclass
	 * Decode byte to value of type T 
	 * @see bayeos.serialframe.SerialFrameInterface.ReadCallback#onData(byte[])
	 */
	public void onData(byte[] data) {
		// System.out.println("On Data:" + ByteArray.toString(data));
		running = false;
	}
	
	public void onError(String msg) {
		System.out.println("On Error:" +  msg);
		error = true;
		errorMsg = msg;	
		running = false;
	}

	public void onAck(byte value) {
		// System.out.println("On Ack:" + ByteArray.toString(value));
		ack = value;
	}
	
	public byte getAck(){
		return ack;
	}
	
    public T getValue() {
		return value;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public boolean hasError() {
		return error;
	}
	
	@Override
	public synchronized boolean isRunning() {	
		return running;
	}
	
	
	
	

}

