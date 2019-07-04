package architecture.ee.util.maven;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MavenVersion implements Serializable {
	/**
	 * Timestamp of the file.
	 */
	private String fileTimestamp;
	/**
	 * Artifact version.
	 */
	private String version;
	/**
	 * Artifact group.
	 */
	private String group;
	/**
	 * Artifact id.
	 */
	private String artifact;

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return group + '/' + artifact + ' ' + version + " file: " + fileTimestamp;
	}

	/**
	 * <p>
	 * Getter for the field <code>fileTimestamp</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFileTimestamp() {
		return fileTimestamp;
	}

	void setFileTimestamp(String date) {
		this.fileTimestamp = date;
	}

	/**
	 * <p>
	 * Getter for the field <code>version</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getVersion() {
		return version;
	}

	void setVersion(String version) {
		this.version = version;
	}

	/**
	 * <p>
	 * Getter for the field <code>group</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getGroup() {
		return group;
	}

	void setGroup(String group) {
		this.group = group;
	}

	/**
	 * <p>
	 * Getter for the field <code>artifact</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getArtifact() {
		return artifact;
	}

	void setArtifact(String artifact) {
		this.artifact = artifact;
	}
}