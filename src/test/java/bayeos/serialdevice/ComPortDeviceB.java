package bayeos.serialframe;

import java.io.IOException;

import org.apache.log4j.Logger;

import bayeos.serialdevice.ISerialDevice;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class ComPortDevice implements ISerialDevice {
			
	private SerialPort port;
	private int available;
	private int timeout = 10000;
	
	private static final Logger log = Logger.getLogger(ComPortDevice.class);

	public void open(String portName) throws IOException{
		port = new SerialPort(portName);
				
		try {
			port.openPort();
			port.setParams(SerialPort.BAUDRATE_38400,
			        SerialPort.DATABITS_8,
			        SerialPort.STOPBITS_1,
			        SerialPort.PARITY_NONE);
						
			port.addEventListener(new SerialPortEventListener() {
				@Override
				public void serialEvent(SerialPortEvent event) {
						if (event.isRXCHAR()) {							
							available = event.getEventValue();
							log.debug("Available:" + available);
						}
					
				}
			});

		} catch (SerialPortException e) {
			throw new IOException(e);
		}
		
	}
	
	public void close() throws IOException{
		try {
			port.closePort();
		} catch (SerialPortException e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public int read() throws IOException {		
		try {
			byte[] b = port.readBytes(1,timeout);
			if (available> 0) {
				available--;
			} 	
			return 0xff & b[0];			
		} catch (SerialPortException | SerialPortTimeoutException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public void write(byte[] data) throws IOException {
			try {
				port.writeBytes(data);
			} catch (SerialPortException e) {
				throw new IOException(e.getMessage());
			}
		
	}
	

	@Override
	public int available() throws IOException {
		return available;
	}
	
	

}
