package com.zakiis.file.model.inner;

public class Access {
	
	public static Access READ_ONLY = new Access(true, false);
	public static Access READ_WRITE = new Access(true, true);
	
	public Access() {}
	
	private Access(boolean canRead, boolean canWrite) {
		this.canRead = canRead;
		this.canWrite = canWrite;
	}

	boolean canRead;
	boolean canWrite;
	
	public boolean isCanRead() {
		return canRead;
	}
	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}
	public boolean isCanWrite() {
		return canWrite;
	}
	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}
}
