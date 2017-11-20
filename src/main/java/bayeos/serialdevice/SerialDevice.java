package bayeos.serialdevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialDevice implements ISerialDevice {

	InputStream in;
	OutputStream out;
	
	public SerialDevice(InputStream in, OutputStream out) {
		super();
		this.in = in;
		this.out = out;
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public void write(byte[] data) throws IOException {
		out.write(data);		
	}

	@Override
	public int available() throws IOException {
		return in.available();
		
	}
	
	
	
	
	
}
