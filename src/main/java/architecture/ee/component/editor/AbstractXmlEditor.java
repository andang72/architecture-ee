package architecture.ee.component.editor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.ee.util.xml.XmlWriter;

public abstract class AbstractXmlEditor {

	protected Logger log = LoggerFactory.getLogger(getClass());

	private File file;

	private Document document;

	public AbstractXmlEditor() {
	}

	public AbstractXmlEditor(File file) {
		this.file = file;
		if (this.file.exists())
			read();
	}

	protected void setFile(File file) {
		this.file = file;
		if (this.file.exists())
			read();
	}

	protected Document getDocument() {
		return document;
	}

	protected abstract void initialize();

	public void read() {
		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
			readDoc(reader);
		} catch (Exception e) {

		}
	}

	private void readDoc(Reader in) throws IOException {
		try {
			SAXReader xmlReader = new SAXReader();
			xmlReader.setEncoding("UTF-8");
			document = xmlReader.read(in);
		} catch (Exception e) {
			log.error("Error reading XML", e);
			throw new IOException(e.getMessage());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public void write() {
		writeDoc(this.document, this.file);
	}

	private void writeDoc(Document document, File file) {
		boolean error = false;
		// Write data out to a temporary file first.
		File tempFile = null;
		Writer writer = null;
		try {
			tempFile = new File(file.getParentFile(), FilenameUtils.getName(file.getName()) + ".tmp");
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "UTF-8"));
			OutputFormat prettyPrinter = OutputFormat.createPrettyPrint();
			XmlWriter xmlWriter = new XmlWriter(writer, prettyPrinter);
			xmlWriter.write(document);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// There were errors so abort replacing the old property file.
			error = true;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e1) {
					log.error(e1.getMessage(), e1);
					error = true;
				}
			}
		}

		// No errors occured, so delete the main file.
		if (!error) {
			// Delete the old file so we can replace it.
			if (!file.delete()) {
				// log.error(L10NUtils.format("002156",
				// file.getAbsolutePath()));
				return;
			}
			// Copy new contents to the file.
			try {
				FileUtils.copyFile(tempFile, file);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				// There were errors so abort replacing the old property file.
				error = true;
			}
			// If no errors, delete the temp file.
			if (!error) {
				tempFile.delete();
			}
		}
	}

}
