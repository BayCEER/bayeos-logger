package bayeos.serialframe;

import static bayeos.serialframe.SerialFrameConstants.ACK_FRAME;
import static bayeos.serialframe.SerialFrameConstants.NACK_FRAME;
import static bayeos.serialframe.SerialFrameConstants.api_ack;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import bayeos.binary.ByteArray;
import bayeos.device.SerialDeviceInterface;
import bayeos.frame.callback.ByteReadCallback;
import bayeos.frame.callback.ReadCallBack;


public class SerialFrameDevice implements SerialFrameInterface {

	private SerialDeviceInterface device;	
	private ReadThread readThread;
	private WriteThread writeThread;

	
	private BlockingQueue<byte[]> qOut = new ArrayBlockingQueue<byte[]>(10);
	
	
	private boolean started = false;
	
	public SerialFrameDevice(final SerialDeviceInterface device) {		
		this.device = device;
		this.readThread = new ReadThread();			
		this.writeThread = new WriteThread();				
		start();
	}
	
	public void close() {
		this.stop();
		this.device.close();
	}
		
	protected class ReadThread extends Thread {
		boolean stopped = false;
		
		private ReadCallBack<?> callback;
		private InputStream in;
		
		SerialFrameParser parser = new SerialFrameParser(new SerialFrameHandler() {
			@Override
			public void onData(byte apiType, byte[] payload) {
				System.out.println("Read [" + apiType + "]:" + ByteArray.toString(payload));
				if (apiType == api_ack) {
					callback.onAck(payload[0]);
				} else {
					callback.onData(payload);								
					try {
						device.write(ACK_FRAME);					
					} catch (IOException e) {
						callback.onError(e.getMessage());
					}						
				}
			}

			@Override
			public void onError(String msg) {
				callback.onError(msg);	
				try {
					device.write(NACK_FRAME);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

		public void setCallback(ReadCallBack<?> callback) {
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
	public void writeFrame(byte apiType, byte[] data) throws IOException {
		try {
			System.out.println("Write [" + apiType + "]:" + ByteArray.toString(data));
			byte[] out = SerialFrameEncoder.encodePayload(apiType, data); 
			qOut.put(out);
		} catch (InterruptedException e) {
			throw new IOException(e.getMessage());
		}		
	}
	
		
	public byte[] readFrame() throws IOException {						
		ByteReadCallback cb = new ByteReadCallback();
		readFrame(cb);
		if (cb.hasError()){
			throw new IOException(cb.getErrorMsg());
		} else {
			return cb.getValue();	
		}
	}
	
	
		
	public void readFrame(ReadCallBack<?> cb)  {
		readFrame(cb, true);
	}
	
		
	@Override
	public void readFrame(ReadCallBack<?> cb, boolean blocking)  {						
		readThread.setCallback(cb);	
		if (blocking){			
			while(!readThread.stopped && cb.isRunning()){
				try {					
					Thread.sleep(50);					
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
