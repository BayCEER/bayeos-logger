package bayeos.serialframe;

import static bayeos.serialframe.SerialFrameConstants.ack_ok;
import static bayeos.serialframe.SerialFrameConstants.api_ack;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import bayeos.binary.ByteArray;
import bayeos.device.SerialDeviceInterface;


public class SerialFrameDevice implements SerialFrameInterface {

	private SerialDeviceInterface device;	
	private ReadThread readThread;
	private WriteThread writeThread;

	private BlockingQueue<byte[]> qOut = new LinkedBlockingDeque<>(10);
	
	
	private boolean started = false;
	
	public SerialFrameDevice(final SerialDeviceInterface device) {		
		this.device = device;
		this.readThread = new ReadThread();			
		this.writeThread = new WriteThread();				
		start();
	}
		
	protected class ReadThread extends Thread {
		boolean stopped = false;
		
		private ReadCallback<?> callback;
		private InputStream in;
		
		SerialFrameParser parser = new SerialFrameParser(new SerialFrameHandler() {
			@Override
			public void onData(byte apiType, byte[] payload) {
				if (apiType == api_ack) {
					callback.onAck(payload[0]);
				} else {
					callback.onData(payload);								
					try {
						qOut.put(SerialFrameEncoder.encodePayload(api_ack, new byte[ack_ok]));
					} catch (InterruptedException e) {
						callback.onError(e.getMessage());
					}
				}
			}

			@Override
			public void onError(String msg) {
				callback.onError(msg);				
			}	
			
			
		});	
		
		@Override
		public void run() {
			
			in = new InputStream() {						
				@Override
				public int read() throws IOException {
					return device.read();
				}
			};
						
			while (!stopped) {
				parser.parse(in);				
			}
		}

		public void setCallback(ReadCallback<?> callback) {
			this.callback = callback;
		}

		public void stopThread() {
			stopped = true;
			if (in!=null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	protected class WriteThread extends Thread {
		boolean stopped = false;
				
		@Override
		public void run() {
			while (!stopped) {
				try {
					byte[] b = qOut.take();
					System.out.println("Write [" + b.length + "]:" + ByteArray.toString(b));
					device.write(b);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		public void stopThread() {
			stopped = true;
		}

	}

	@Override
	public void writeFrame(byte apiType, byte[] data) {
		try {
			qOut.put(SerialFrameEncoder.encodePayload(apiType, data));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	public byte[] readFrame() throws IOException {
		
		ByteReadCallback cb = new ByteReadCallback();		
		readThread.setCallback(cb);
		while(!readThread.stopped && cb.isRunning()){
			try {
				Thread.sleep(50);
				
			} catch (InterruptedException e) {
				cb.onError("Call interrupted.");				
			}
		}
		
		if (cb.hasError()){
			throw new IOException(cb.getErrorMsg());
		}  
		return cb.getValue();		
		
	}
	
			
	public void readFrame(ReadCallback<?> cb){
		readFrame(cb, true);
	}
	
		
	@Override
	public void readFrame(ReadCallback<?> cb, boolean blocking)  {						
		readThread.setCallback(cb);	
		if (blocking){
			while(!readThread.stopped && cb.isRunning()){
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}
			
		}
		
	}

	
	@Override
	public void stop() {
		if (started){
			killWriteThread();
			killReadThread();			
			started = false;
		}
		
	}

	private void killReadThread() {
		if (readThread != null) {
			readThread.stopThread();
			readThread = null;
		}

	}

	private void killWriteThread() {
		if (writeThread != null) {
			writeThread.stopThread();
			writeThread = null;
			qOut.clear();
		}
	}

	@Override
	public void start() {
		if (!started){
			writeThread.start();
			readThread.start();
			started = true;										
		}				
	}

	
	

}
