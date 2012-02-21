package com.dreasyLib.comm;

import java.io.IOException;
import java.io.InputStream;

import com.dreasyLib.comm.Comm.OnCommEvent;

public class ExtendedInputStream extends InputStream{
	private OnCommEvent commEvent;
	private long length;
	private InputStream is;
	private long bytes=0;
	
	public ExtendedInputStream(InputStream is,OnCommEvent commEvent, long length) {
		this.commEvent = commEvent;
		this.length = length;
		bytes=0;
		this.is = is;
	}


	@Override
	public int read() throws IOException {
		bytes=bytes+1;
		int percent=(int) ((bytes*100/this.length));
		commEvent.OnProcess(percent, "read");
		return is.read();
	}


	@Override
	public int available() throws IOException {
		return is.available();
	}


	@Override
	public void close() throws IOException {
		is.close();
	}


	@Override
	public void mark(int readlimit) {
		is.mark(readlimit);
	}


	@Override
	public boolean markSupported() {
		return is.markSupported();
	}


	@Override
	public int read(byte[] buffer, int offset, int length)
			throws IOException {
		bytes=bytes+length;
		int percent=(int) ((bytes*100/this.length));
		commEvent.OnProcess(percent, "uploading");			
		return is.read(buffer, offset, length);
	}


	@Override
	public int read(byte[] buffer) throws IOException {
		bytes=bytes+buffer.length;
		int percent=(int) ((bytes*100/this.length));
		commEvent.OnProcess(percent, "uploading");		
		return is.read(buffer);
	}


	@Override
	public synchronized void reset() throws IOException {

		is.reset();
	}


	@Override
	public long skip(long byteCount) throws IOException {
		return is.skip(byteCount);
	}
	
	
	
}