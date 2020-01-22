package bayeos.serialframe;

import static bayeos.serialframe.SerialFrameConstants.ACK_FRAME;
import static bayeos.serialframe.SerialFrameConstants.BREAK_FRAME;
import static bayeos.serialframe.SerialFrameConstants.NACK_FRAME;
import static bayeos.serialframe.SerialFrameConstants.ack_ok;
import static bayeos.serialframe.SerialFrameConstants.api_ack;
import static bayeos.serialframe.SerialFrameConstants.api_data;
import static bayeos.serialframe.SerialFrameConstants.escapeByte;
import static bayeos.serialframe.SerialFrameConstants.frameDelimeter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import bayeos.binary.ByteArray;
import bayeos.binary.CheckSum;
import bayeos.serialdevice.ISerialDevice;
import bayeos.serialdevice.SerialDevice;

public class AsyncSerialFrameDevice {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AsyncSerialFrameDevice.class);

	private boolean stopped = false;
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	
	private Thread readThread;


	// Contains api-type, payload 
	private final BlockingQueue<byte[]> inQueue;

	private final ISerialDevice serial;

	public AsyncSerialFrameDevice(ISerialDevice serial) {

		this.serial = serial;

		this.inQueue = new LinkedBlockingQueue<byte[]>();

		this.readThread = new ReadThread();
		this.readThread.start();

	}

	public AsyncSerialFrameDevice(InputStream in, OutputStream out) {
		this(new SerialDevice(in, out));
	}

	public void stop() {
				
		stopped = true;					
	}

	public Future<Void> writeFrame(byte[] payload) {
		return executor.submit(() -> {
			serial.write(SerialFrameEncoder.encodePayload(api_data, payload));
			if (getFrame(api_ack)[1] == ack_ok) {
				return null;
			} else {
				throw new IOException("Write Frame failed.");
			}
		});
	}

	public Future<byte[]> readFrame() {
		return executor.submit(() -> {
			return getFrame(api_data);
		});
	}

	public Future<byte[]> writeReadFrame(byte[] payload) {
		return executor.submit(() -> {
			serial.write(SerialFrameEncoder.encodePayload(api_data, payload));
			byte[] f = getFrame(api_ack);
			if (f[0] == ack_ok) {
				return getFrame(api_data);
			} else {
				throw new IOException("Write Frame failed.");
			}
		});

	}

	public Future<Void> breakFrame() {
		return executor.submit(() -> {
			serial.write(BREAK_FRAME);
			inQueue.clear();
			return null;
		});
	}

	private byte[] getFrame(byte apiType) throws InterruptedException {
		log.debug("Get " + ((apiType==api_data)?"data":"acknowledge") + " frame");
		byte[] f = inQueue.take();
		log.debug("Took:" + ByteArray.toString(f));
		if (f[0] == apiType) {
			return Arrays.copyOfRange(f, 1, f.length);
		} else {
			// Skip wrong types
			log.warn("Skipping wrong apiType");
			return getFrame(apiType);
		}
	}

	private class ReadThread extends Thread {

		public ReadThread() {
			super("Reader");
		}

		private int readByteEscaped() throws IOException {
			int b = serial.read();
			if (b == escapeByte) {
				b = 0x20 ^ serial.read();
			}
			return b;
		}
		

		@Override
		public void run() {
			while (!stopped) {
				try {
					if (serial.read() == frameDelimeter) {
						log.debug("On next frame");

						// Read length
						int length = readByteEscaped();

						if (length == 0) {
							log.warn("Payload length 0 not allowed.");
							continue;
						}
						// Read api
						int apiType = readByteEscaped();

						CheckSum chkCalculated = new CheckSum();
						chkCalculated.addByte((byte) apiType);

						// Read payload
						byte[] payload = new byte[length + 1];
						payload[0] = (byte) apiType;

						int p = 0;
						for (int i = 0; i < length; i++) {
							p = readByteEscaped();
							payload[i + 1] = (byte) p;
							chkCalculated.addByte((byte) p);
						}

						// Read checksum
						int checkSum = readByteEscaped();

						if (chkCalculated.oneByte() == checkSum) {
							serial.write(ACK_FRAME);
							log.debug("Put payload in queue:" + ByteArray.toString(payload));
							inQueue.put(payload);
						} else {
							serial.write(NACK_FRAME);
							log.warn("Wrong checksum, skipping response frame.");	
							continue;
						}
					}

				} catch (IOException | InterruptedException e) {
					log.error(e.getMessage());
				}
			}
			
			log.debug("Reader stopped");
		}

	};

}
