package bayeos.serialdevice;

import java.io.IOException;

import org.apache.log4j.Logger;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class ComPortDevice implements ISerialDevice {
			
	private SerialPort port;
			
	private static final Logger log = Logger.getLogger(ComPortDevice.class);

	public void open(String portName) throws IOException{
		log.debug("Open " + portName);
		port = new SerialPort(portName);
				
		try {
			port.openPort();
			port.setParams(SerialPort.BAUDRATE_38400,
			        SerialPort.DATABITS_8,
			        SerialPort.STOPBITS_1,
			        SerialPort.PARITY_NONE);
									

		} catch (SerialPortException e) {
			throw new IOException(e);
		}		
	}
	
	public void close() {
		try {
			if (port.purgePort(SerialPort.PURGE_RXCLEAR|SerialPort.PURGE_TXCLEAR)) {
				log.debug("Port purged.");
			};			
			if (port.closePort()) {
				log.debug("Port closed.");
			} else {
				log.error("Failed to close port");
			};			
			try {
				// Wait until port is really closed
				Thread.sleep(200);
			} catch (InterruptedException e) {			
			}
		} catch (SerialPortException e) {
			log.error(e.getMessage());
		}
	}

	
	public void openLastPort() throws IOException {
		String[] names = SerialPortList.getPortNames();		
		if (names.length>0) {
			open(names[names.length-1]);
		} else {
			throw new IOException("No serial port found.");
			
		}
	}
	
	
	@Override
	public int read() throws IOException {		
		try {		
			if (port.isOpened()) {
				byte[] b = port.readBytes(1);
				return 0xff & b[0];	
			} else {
				throw new IOException("Port already closed.");
			}
			 			
		} catch (SerialPortException e) {
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
	

	
	
	

}