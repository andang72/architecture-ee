package architecture.ee.component.platform;

import java.io.File;

public class DiskUsage {

	private String absolutePath;
	private long totalSpace;
	private long freeSpace;
	private long usableSpace;

	public DiskUsage() {
		super();
	}

	private DiskUsage(File root) {
		this.absolutePath = root.getAbsolutePath();
		this.totalSpace = root.getTotalSpace();
		this.freeSpace = root.getFreeSpace();
		this.usableSpace = root.getUsableSpace();

	}

	public static class Builder {
		public static DiskUsage build(File file) {
			return new DiskUsage(file);
		}
	}

	/**
	 * @return absolutePath
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public long getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(long totalSpace) {
		this.totalSpace = totalSpace;
	}

	public long getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(long freeSpace) {
		this.freeSpace = freeSpace;
	}

	public long getUsableSpace() {
		return usableSpace;
	}

	public void setUsableSpace(long usableSpace) {
		this.usableSpace = usableSpace;
	}

}
