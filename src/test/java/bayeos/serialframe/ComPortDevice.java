package bayeos.serialframe;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bayeos.serialdevice.ISerialDevice;

public class ComPortDevice implements ISerialDevice {
	
	private String port;
	private int baudrate;		 
	private InputStream in;
	private OutputStream out;
	
	private RXTXPort comm;

	public ComPortDevice(String port, int baudrate) {
		super();
		this.port = port;
		this.baudrate = baudrate;
	}
	
	public ComPortDevice(String port) {
		this(port,38400);
	}
	
		
	public void write(byte[] data) throws IOException {		
		out.write(data);
	}

	public boolean open()  {		
		CommPortIdentifier ident;
		try {
			ident = CommPortIdentifier.getPortIdentifier(port);
			
			comm = ident.open("SerialPort", 2000);
			comm.enableReceiveTimeout(10000);
			comm.setSerialPortParams(baudrate, SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			comm.notifyOnDataAvailable(true);
		
			in = comm.getInputStream();
			out = comm.getOutputStream();
		} catch (NoSuchPortException|PortInUseException|UnsupportedCommOperationException e) {			
			e.printStackTrace();
			return false;
		} 
		System.out.println("Device on port:" + port + " opened.");
		return true;
	}

	public void close() {	
		if (comm!=null){
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			comm.close();
		}
	}

	public int read() throws IOException {		
		return in.read();		
	}

	

	

}
