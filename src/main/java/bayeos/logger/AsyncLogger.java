package bayeos.logger;

import static bayeos.frame.FrameConstants.Command;
import static bayeos.frame.FrameConstants.CommandResponse;
import static bayeos.logger.LoggerConstants.BC_GET_READ_POS;
import static bayeos.logger.LoggerConstants.BufferCommand;
import static bayeos.logger.LoggerConstants.DM_FULL;
import static bayeos.logger.LoggerConstants.DM_NEW;
import static bayeos.logger.LoggerConstants.GetBatteryStatus;
import static bayeos.logger.LoggerConstants.GetName;
import static bayeos.logger.LoggerConstants.GetSamplingInt;
import static bayeos.logger.LoggerConstants.GetTime;
import static bayeos.logger.LoggerConstants.GetTimeOfNextFrame;
import static bayeos.logger.LoggerConstants.GetVersion;
import static bayeos.logger.LoggerConstants.ModeStop;
import static bayeos.logger.LoggerConstants.SetName;
import static bayeos.logger.LoggerConstants.SetSamplingInt;
import static bayeos.logger.LoggerConstants.SetTime;
import static bayeos.logger.LoggerConstants.StartBulkData;
import static bayeos.logger.LoggerConstants.StartData;
import static bayeos.logger.LoggerConstants.StartLiveData;
import static bayeos.logger.LoggerConstants.StopData;
import static bayeos.logger.LoggerConstants.StopLiveData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import bayeos.binary.ByteArray;
import bayeos.frame.DateAdapter;
import bayeos.serialdevice.ISerialDevice;
import bayeos.serialdevice.SerialDevice;
import bayeos.serialframe.AsyncSerialFrameDevice;

public class AsyncLogger implements ILogger {
	
	public float version = 1.4F;
	private static final int TIMEOUT_SECS = 5;		
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AsyncLogger.class);
	
	private AsyncSerialFrameDevice dev;	
	
	public AsyncLogger(ISerialDevice serial){
		dev = new AsyncSerialFrameDevice(serial);
	}
	
	public AsyncLogger(InputStream in, OutputStream out){
		this(new SerialDevice(in, out));
	}
	
	public void stop() {			
		dev.stop();
	}
				
	@Override
	public String getName() throws IOException {	
		log.debug("Getting name of device");										
		byte[] resp = readCommandResponse(GetName,writeReadFrame(new byte[]{Command,GetName}));		
		return new String(resp);				
	}
	
	@Override
	public void setName(String name) throws IOException {
		log.debug("Set name:" + name);
		ByteBuffer bf = ByteBuffer.allocate(name.length()+2);
		bf.put(Command);
		bf.put(SetName);
		bf.put(name.getBytes());		
		String newName = new String(readCommandResponse(SetName,writeReadFrame(bf.array())));				
		if (!newName.equals(name)) {
			throw new IOException("Expected:" + name + " was:" + newName );
		}		
	}
	
	

	@Override
	public Date getTime() throws IOException {
		log.debug("Getting time of device");		
		byte[] resp = readCommandResponse(GetTime,writeReadFrame(new byte[]{Command,GetTime}));
		return DateAdapter.getDate(ByteArray.fromByteUInt32(resp));
	}


	@Override
	public Date setTime(Date date) throws IOException {
		log.debug("Setting time of device to " + date);
		long secs = DateAdapter.getSeconds(date);		
		ByteBuffer bf = ByteBuffer.allocate(6);
		bf.put(Command);
		bf.put(SetTime);
		bf.put(ByteArray.toByteUInt32(secs));			
		byte[] resp = readCommandResponse(SetTime,writeReadFrame(bf.array()));
		return DateAdapter.getDate(ByteArray.fromByteUInt32(resp));
	}


	@Override
	public int getSamplingInterval() throws IOException {
		log.debug("Getting sampling interval");
		byte[] resp = readCommandResponse(GetSamplingInt,writeReadFrame(new byte[]{Command,GetSamplingInt}));
		return ByteArray.fromByteInt16(resp);
	}


	@Override
	public int setSamplingInterval(int interval) throws IOException {
		ByteBuffer bf = ByteBuffer.allocate(6);
		bf.put(Command);
		bf.put(SetSamplingInt);
		bf.put(ByteArray.toByteInt16((short)interval));
		byte[] resp = readCommandResponse(SetSamplingInt,writeReadFrame(bf.array()));
		return ByteArray.fromByteInt16(resp);
	}


	@Override
	public Date getDateOfNextFrame() throws IOException {
		log.debug("Get date of next frame");		
		byte[] resp = readCommandResponse(GetTimeOfNextFrame,writeReadFrame(new byte[]{Command,GetTimeOfNextFrame}));
		return DateAdapter.getDate(ByteArray.fromByteUInt32(resp));
	}


	@Override
	public long startData(byte dataMode) throws IOException {
		log.debug("Starting data dump.");		
		byte[] resp = readCommandResponse(StartData,writeReadFrame(new byte[]{Command,StartData,dataMode}));
		return ByteArray.fromByteUInt32(resp);
	}


	@Override
	public void stopData(byte stopMode) throws IOException {
		log.debug("Stopping data dump.");	
		breakSocket();		
		readCommandResponse(StopData,writeReadFrame(new byte[]{Command,StopData,(byte)stopMode}));		
	}


	@Override
	public void startLiveData() throws IOException {
		log.debug("Start live data");		
		readCommandResponse(StartLiveData,writeReadFrame(new byte[]{Command,StartLiveData}));						
	}


	@Override
	public void stopLiveData() throws IOException {			
		log.debug("Stop live data");
		if (version >= 1.3F) {				
				byte[] r = readCommandResponse(ModeStop,writeReadFrame(new byte[]{Command,ModeStop}));
				log.debug(ByteArray.toString(r));
		} else {				
				byte[] b = readCommandResponse(StopLiveData,writeReadFrame(new byte[]{Command,StopLiveData}));				
		}
				
	}


	@Override
	public String getVersion() throws IOException {		
		log.debug("Get version");		
		byte[] resp = readCommandResponse(GetVersion,writeReadFrame(new byte[]{Command,GetVersion}));
		if (resp != null) {			
			this.version = Float.valueOf(new String(resp));	
		}		
		return new String(resp);
	}

	@Override
	public Boolean getBatteryStatus() throws IOException {		
		log.debug("getBatteryStatus()");
		if (version >= 1.4F) {			
			byte[] resp = readCommandResponse(GetBatteryStatus,writeReadFrame(new byte[]{Command,GetBatteryStatus}));			
			ByteBuffer bf = ByteBuffer.wrap(resp);
			int mv = Short.toUnsignedInt(bf.getShort());
			int limit = Short.toUnsignedInt(bf.getShort());			
			if (mv == 0 || limit == 0) { 
				return null;
			} else {
				return mv > limit;	
			}					
		} else {
			return null;
		}		
	}


	@Override
	public byte[] readData() throws IOException {
		log.debug("readData");		
		return readFrame();			
	}


	@Override
	public void breakSocket() throws IOException {				
		try {
			Future<Void> f = dev.breakFrame();
			f.get(TIMEOUT_SECS, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new IOException(e.getMessage());
		}
	}


	@Override
	public void stopMode() throws IOException {
		log.debug("stopMode");
		readCommandResponse(ModeStop,writeReadFrame(new byte[]{Command,ModeStop}));		
	}


	@Override
	public long startBulkData(byte dataMode) throws IOException {
		log.debug("startBulkData mode:" +dataMode);
		if (dataMode==DM_FULL){
			byte[] resp = readCommandResponse(StartBulkData,writeReadFrame(new byte[]{Command,StartBulkData}));
			return ByteArray.fromByteUInt32(resp);			
		} else if (dataMode == DM_NEW){			
			byte[] resp = readCommandResponse(BufferCommand,writeReadFrame(new byte[]{Command,BufferCommand,BC_GET_READ_POS}));						
			ByteBuffer bf = ByteBuffer.allocate(6);
			bf.put(Command);
			bf.put(StartBulkData);
			bf.put(resp);														
			byte[] db = readCommandResponse(StartBulkData,writeReadFrame(bf.array()));
			return ByteArray.fromByteUInt32(db);
		} else {
			throw new IOException("Mode not supported."); 	
		}

	}

	@Override
	public byte[] readBulk() throws IOException {
		log.debug("readBulk");
		return readFrame();		
	}

	@Override
	public void sendBufferCommand(byte command) throws IOException {
		log.debug("sendBufferCommand command:" + ByteArray.toString(command));				
		readCommandResponse(BufferCommand,writeReadFrame(new byte[]{Command,BufferCommand,command}));			
	}
	
	private byte[] readFrame() throws IOException {		
		try {
			Future<byte[]> f = dev.readFrame();
			return f.get(TIMEOUT_SECS, TimeUnit.SECONDS);								
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new IOException(e.getMessage());
		}	
	}
	
	
	private byte[] writeReadFrame(byte[] frame) throws IOException {		
		try {
			Future<byte[]> f = dev.writeReadFrame(frame);
			return f.get(TIMEOUT_SECS, TimeUnit.SECONDS);								
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new IOException(e.getMessage());
		}	
	}
	
	private byte[] readCommandResponse(byte command, byte[] resp) throws IOException {
		log.debug("readCommandResponse command:" + ByteArray.toString(command));			
		if (resp.length < 2){
			throw new IOException("Command response:" + ByteArray.toString(resp) + " is too short.");			
		} else {
			if (resp[0] != CommandResponse || resp[1] != command){				
				throw new IOException("Response:"  + ByteArray.toString(resp) + " has wrong type");				
			} else {
				return Arrays.copyOfRange(resp, 2, resp.length);	
			}			
		}
	}	

	
	
}

