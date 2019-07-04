package architecture.ee.component.platform;

public class SystemInfo {

	private String date;
	private String time;
	private String javaVersion;
	private String javaVendor;
	private String jvmVersion;
	private String jvmVendor;
	private String jvmImplementationVersion;
	private String javaRuntime;
	private String javaVm;
	private String userName;
	private String systemLanguage;
	private String systemTimezone;
	private String operatingSystem;
	private String operatingSystemArchitecture;
	private String fileSystemEncoding;
	private String jvmInputArguments;
	private String workingDirectory;
	private String tempDirectory;
	private int availableProcessors;

	public SystemInfo() {

	}

	/**
	 * @return availableProcessors
	 */
	public int getAvailableProcessors() {
		return availableProcessors;
	}

	/**
	 * @param availableProcessors
	 *            설정할 availableProcessors
	 */
	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getJavaVendor() {
		return javaVendor;
	}

	public void setJavaVendor(String javaVendor) {
		this.javaVendor = javaVendor;
	}

	public String getJvmVersion() {
		return jvmVersion;
	}

	public void setJvmVersion(String jvmVersion) {
		this.jvmVersion = jvmVersion;
	}

	public String getJvmVendor() {
		return jvmVendor;
	}

	public void setJvmVendor(String jvmVendor) {
		this.jvmVendor = jvmVendor;
	}

	public String getJvmImplementationVersion() {
		return jvmImplementationVersion;
	}

	public void setJvmImplementationVersion(String jvmImplementationVersion) {
		this.jvmImplementationVersion = jvmImplementationVersion;
	}

	public String getJavaRuntime() {
		return javaRuntime;
	}

	public void setJavaRuntime(String javaRuntime) {
		this.javaRuntime = javaRuntime;
	}

	public String getJavaVm() {
		return javaVm;
	}

	public void setJavaVm(String javaVm) {
		this.javaVm = javaVm;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSystemLanguage() {
		return systemLanguage;
	}

	public void setSystemLanguage(String systemLanguage) {
		this.systemLanguage = systemLanguage;
	}

	public String getSystemTimezone() {
		return systemTimezone;
	}

	public void setSystemTimezone(String systemTimezone) {
		this.systemTimezone = systemTimezone;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public String getOperatingSystemArchitecture() {
		return operatingSystemArchitecture;
	}

	public void setOperatingSystemArchitecture(String operatingSystemArchitecture) {
		this.operatingSystemArchitecture = operatingSystemArchitecture;
	}

	public String getFileSystemEncoding() {
		return fileSystemEncoding;
	}

	public void setFileSystemEncoding(String fileSystemEncoding) {
		this.fileSystemEncoding = fileSystemEncoding;
	}

	public String getJvmInputArguments() {
		return jvmInputArguments;
	}

	public void setJvmInputArguments(String jvmInputArguments) {
		this.jvmInputArguments = jvmInputArguments;
	}

	public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public String getTempDirectory() {
		return tempDirectory;
	}

	public void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

}
