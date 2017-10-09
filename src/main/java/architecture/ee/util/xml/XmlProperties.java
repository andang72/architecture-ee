/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package architecture.ee.util.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.xml.XmlEscapers;
/**
 * Provides the the ability to use simple XML property files. Each property is
 * in the form X.Y.Z, which would map to an XML snippet of:
 * <pre>
 * &lt;X&gt;
 *     &lt;Y&gt;
 *         &lt;Z&gt;someValue&lt;/Z&gt;
 *     &lt;/Y&gt;
 * &lt;/X&gt;
 * </pre>
 * The XML file is passed in to the constructor and must be readable and
 * writable. Setting property values will automatically persist those value
 * to disk. The file encoding used is UTF-8.
 *
 * @author Derek DeMoro
 * @author Iain Shigeoka
 */
public class XmlProperties {

	private static final Logger log = LoggerFactory.getLogger(XmlProperties.class);

	private static final String ENCRYPTED_ATTRIBUTE = "encrypted";

	/**
	 * Copies the inFile to the outFile.
	 *
	 * @param inFile
	 *            The file to copy from
	 * @param outFile
	 *            The file to copy to
	 * @throws IOException
	 *             If there was a problem making the copy
	 */
	private static void copy(File inFile, File outFile) throws IOException {
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
			fin = new FileInputStream(inFile);
			fout = new FileOutputStream(outFile);
			copy(fin, fout);
		} finally {
			try {
				if (fin != null)
					fin.close();
			} catch (IOException e) {
				// do nothing
			}
			try {
				if (fout != null)
					fout.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	/**
	 * Copies data from an input stream to an output stream
	 *
	 * @param in
	 *            the stream to copy data from.
	 * @param out
	 *            the stream to copy data to.
	 * @throws IOException
	 *             if there's trouble during the copy.
	 */
	private static void copy(InputStream in, OutputStream out) throws IOException {
		// Do not allow other threads to intrude on streams during copy.
		synchronized (in) {
			synchronized (out) {
				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1)
						break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	}
	
	private File file;

	private Document document;

	/**
	 * Parsing the XML file every time we need a property is slow. Therefore, we
	 * use a Map to cache property values that are accessed more than once.
	 */
	private Map<String, String> propertyCache = new HashMap<String, String>();

	/**
	 * Creates a new XMLPropertiesTest object.
	 *
	 * @param file
	 *            the file that properties should be read from and written to.
	 * @throws IOException
	 *             if an error occurs loading the properties.
	 */
	public XmlProperties(File file) throws IOException {
		this.file = file;
		if (!file.exists()) {
			// Attempt to recover from this error case by seeing if the
			// tmp file exists. It's possible that the rename of the
			// tmp file failed the last time Jive was running,
			// but that it exists now.
			File tempFile;
			tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
			if (tempFile.exists()) {
				// log.error(L10NUtils.format("002157", file.getName()));
				tempFile.renameTo(file);
			}
			// There isn't a possible way to recover from the file not
			// being there, so throw an error.
			else {
				throw new FileNotFoundException(); // L10NUtils.format("002151",
													// file.getName()));
			}
		}
		// Check read and write privs.
		if (!file.canRead()) {
			throw new IOException(); // L10NUtils.format("002152",
										// file.getName()));
		}
		if (!file.canWrite()) {
			throw new IOException(); // L10NUtils.format("002153",
										// file.getName()));
		}

		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
			buildDoc(reader);
		} catch (Exception e) {
			// log.error(L10NUtils.format("002154", file.getName(),
			// e.getMessage()));
			throw new IOException(e.getMessage());
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}
		}

		// FileReader reader = new FileReader(file);
		// buildDoc(reader);
	}

	/**
	 * Loads XML properties from a stream.
	 *
	 * @param in
	 *            the input stream of XML.
	 * @throws IOException
	 *             if an exception occurs when reading the stream.
	 */
	public XmlProperties(InputStream in) throws IOException {
		Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		buildDoc(reader);
	}

	/**
	 * Creates a new XMLPropertiesTest object.
	 *
	 * @param fileName
	 *            the full path the file that properties should be read from and
	 *            written to.
	 * @throws IOException
	 *             if an error occurs loading the properties.
	 */
	public XmlProperties(String fileName) throws IOException {
		this(new File(fileName));
	}

	/**
	 * Builds the document XML model up based the given reader of XML data.
	 * 
	 * @param in
	 *            the input stream used to build the xml document
	 * @throws java.io.IOException
	 *             thrown when an error occurs reading the input stream.
	 */
	private void buildDoc(Reader in) throws IOException {
		try {
			SAXReader xmlReader = new SAXReader();
			xmlReader.setEncoding("UTF-8");
			document = xmlReader.read(in);
		} catch (Exception e) {
			log.error("Error reading XML properties", e);
			throw new IOException(e.getMessage());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Deletes the specified property.
	 *
	 * @param name
	 *            the property to delete.
	 */
	public synchronized void deleteProperty(String name) {
		// Remove property from cache.
		propertyCache.remove(name);

		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy.
		Element element = document.getRootElement();
		for (int i = 0; i < propName.length - 1; i++) {
			element = element.element(propName[i]);
			// Can't find the property so return.
			if (element == null) {
				return;
			}
		}
		// Found the correct element to remove, so remove it...
		element.remove(element.element(propName[propName.length - 1]));
		// .. then write to disk.
		saveProperties();

		// Generate event.
		Map<String, Object> params = Collections.emptyMap();

		// PropertyEventDispatcher.dispatchEvent(name,
		// PropertyEventDispatcher.EventType.xml_property_deleted, params);
	}

	/**
	 * Returns the value of the attribute of the given property name or
	 * <tt>null</tt> if it doesn't exist. Note, this
	 *
	 * @param name
	 *            the property name to lookup - ie, "foo.bar"
	 * @param attribute
	 *            the name of the attribute, ie "id"
	 * @return the value of the attribute of the given property or <tt>null</tt>
	 *         if it doesn't exist.
	 */
	public String getAttribute(String name, String attribute) {
		if (name == null || attribute == null) {
			return null;
		}
		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy.
		Element element = document.getRootElement();
		for (String child : propName) {
			element = element.element(child);
			if (element == null) {
				// This node doesn't match this part of the property name which
				// indicates this property doesn't exist so return empty array.
				break;
			}
		}
		if (element != null) {
			// Get its attribute values
			return element.attributeValue(attribute);
		}
		return null;
	}

	/**
	 * Return all values who's path matches the given property name as a String
	 * array, or an empty array if the if there are no children. This allows you
	 * to retrieve several values with the same property name. For example,
	 * consider the XML file entry:
	 * 
	 * <pre>
	 * &lt;foo&gt;
	 *     &lt;bar&gt;
	 *         &lt;prop&gt;some value&lt;/prop&gt;
	 *         &lt;prop&gt;other value&lt;/prop&gt;
	 *         &lt;prop&gt;last value&lt;/prop&gt;
	 *     &lt;/bar&gt;
	 * &lt;/foo&gt;
	 * </pre>
	 * 
	 * If you call getProperties("foo.bar.prop") will return a string array
	 * containing {"some value", "other value", "last value"}.
	 *
	 * @param name
	 *            the name of the property to retrieve
	 * @return all child property values for the given node name.
	 */
	public Iterator getChildProperties(String name) {
		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy,
		// stopping one short.
		Element element = document.getRootElement();
		for (int i = 0; i < propName.length - 1; i++) {
			element = element.element(propName[i]);
			if (element == null) {
				// This node doesn't match this part of the property name which
				// indicates this property doesn't exist so return empty array.
				return Collections.EMPTY_LIST.iterator();
			}
		}
		// We found matching property, return values of the children.
		Iterator iter = element.elementIterator(propName[propName.length - 1]);
		ArrayList<String> props = new ArrayList<String>();
		while (iter.hasNext()) {
			props.add(((Element) iter.next()).getText());
		}
		return props.iterator();
	}

	public Collection<String> getChildrenNames(String parent) {
		String propNames[] = parsePropertyName(parent);
		Element element = document.getRootElement();
		for (String propName : propNames) {
			element = element.element(propName);
			if (element == null)
				return Collections.emptyList();
		}

		List<Element> children = element.elements();
		int childCount = children.size();
		List<String> childrenNames = new ArrayList<String>(childCount);

		for (Element e : children) {
			childrenNames.add(e.getName());
		}

		return childrenNames;
	}

	/**
	 * Return all children property names of a parent property as a String
	 * array, or an empty array if the if there are no children. For example,
	 * given the properties <tt>X.Y.A</tt>, <tt>X.Y.B</tt>, and <tt>X.Y.C</tt>,
	 * then the child properties of <tt>X.Y</tt> are <tt>A</tt>, <tt>B</tt>, and
	 * <tt>C</tt>.
	 *
	 * @param parent
	 *            the name of the parent property.
	 * @return all child property values for the given parent.
	 */
	public String[] getChildrenProperties(String parent) {
		String[] propName = parsePropertyName(parent);
		// Search for this property by traversing down the XML heirarchy.
		Element element = document.getRootElement();
		for (String aPropName : propName) {
			element = element.element(aPropName);
			if (element == null) {
				// This node doesn't match this part of the property name which
				// indicates this property doesn't exist so return empty array.
				return new String[] {};
			}
		}
		// We found matching property, return names of children.
		List children = element.elements();
		int childCount = children.size();
		String[] childrenNames = new String[childCount];
		for (int i = 0; i < childCount; i++) {
			childrenNames[i] = ((Element) children.get(i)).getName();
		}
		return childrenNames;
	}

	private void getElementNames(List<String> list, Element e, String name) {
		if (e.elements().isEmpty()) {
			list.add(name);
		} else {
			List<Element> children = e.elements();
			for (Element child : children) {
				getElementNames(list, child,
						(new StringBuilder()).append(name).append('.').append(child.getName()).toString());
			}
		}
	}

	/**
	 * Return all values who's path matches the given property name as a String
	 * array, or an empty array if the if there are no children. This allows you
	 * to retrieve several values with the same property name. For example,
	 * consider the XML file entry:
	 * 
	 * <pre>
	 * &lt;foo&gt;
	 *     &lt;bar&gt;
	 *         &lt;prop&gt;some value&lt;/prop&gt;
	 *         &lt;prop&gt;other value&lt;/prop&gt;
	 *         &lt;prop&gt;last value&lt;/prop&gt;
	 *     &lt;/bar&gt;
	 * &lt;/foo&gt;
	 * </pre>
	 * 
	 * If you call getProperties("foo.bar.prop") will return a string array
	 * containing {"some value", "other value", "last value"}.
	 *
	 * @param name
	 *            the name of the property to retrieve
	 * @return all child property values for the given node name.
	 */
	public String[] getProperties(String name) {
		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy,
		// stopping one short.
		Element element = document.getRootElement();
		for (int i = 0; i < propName.length - 1; i++) {
			element = element.element(propName[i]);
			if (element == null) {
				// This node doesn't match this part of the property name which
				// indicates this property doesn't exist so return empty array.
				return new String[] {};
			}
		}
		// We found matching property, return names of children.
		Iterator iter = element.elementIterator(propName[propName.length - 1]);
		List<String> props = new ArrayList<String>();
		String value;
		while (iter.hasNext()) {
			// Empty strings are skipped.
			value = ((Element) iter.next()).getTextTrim();
			if (!"".equals(value)) {
				props.add(value);
			}
		}
		String[] childrenNames = new String[props.size()];
		return props.toArray(childrenNames);
	}

	/**
	 * Returns the value of the specified property.
	 *
	 * @param name
	 *            the name of the property to get.
	 * @return the value of the specified property.
	 */
	public synchronized String getProperty(String name) {
		String value = propertyCache.get(name);
		if (value != null) {
			return value;
		}

		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy.
		Element element = document.getRootElement();
		for (String aPropName : propName) {
			element = element.element(aPropName);
			if (element == null) {
				// This node doesn't match this part of the property name which
				// indicates this property doesn't exist so return null.
				return null;
			}
		}
		// At this point, we found a matching property, so return its value.
		// Empty strings are returned as null.
		value = element.getTextTrim();
		if ("".equals(value)) {
			return null;
		} else {
			// Add to cache so that getting property next time is fast.
			propertyCache.put(name, value);
			return value;
		}
	}

	public Collection<String> getPropertyNames() {
		List<String> propNames = new LinkedList<String>();

		Element element = document.getRootElement();
		List<Element> elements = element.elements();
		if (elements.size() == 0)
			return Collections.emptyList();
		for (Element child : elements) {
			getElementNames(propNames, child, child.getName());
		}
		return propNames;
	}

	/**
	 * Returns an array representation of the given Jive property. Jive
	 * properties are always in the format "prop.name.is.this" which would be
	 * represented as an array of four Strings.
	 *
	 * @param name
	 *            the name of the Jive property.
	 * @return an array representation of the given Jive property.f
	 */
	private String[] parsePropertyName(String name) {
		List<String> propName = new ArrayList<String>(5);
		// Use a StringTokenizer to tokenize the property name.
		StringTokenizer tokenizer = new StringTokenizer(name, ".");
		while (tokenizer.hasMoreTokens()) {
			propName.add(tokenizer.nextToken());
		}
		return propName.toArray(new String[propName.size()]);
	}

	/**
	 * Saves the properties to disk as an XML document. A temporary file is used
	 * during the writing process for maximum safety.
	 */
	private synchronized void saveProperties() {
		boolean error = false;
		// Write data out to a temporary file first.
		File tempFile = null;
		Writer writer = null;
		try {
			tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
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
				copy(tempFile, file);
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

	public void setProperties(Map<String, String> propertyMap) {
		for (String propertyName : propertyMap.keySet()) {
			String propertyValue = propertyMap.get(propertyName);
			setProperty(propertyName, propertyValue);
		}
	}

	/**
	 * Sets a property to an array of values. Multiple values matching the same
	 * property is mapped to an XML file as multiple elements containing each
	 * value. For example, using the name "foo.bar.prop", and the value string
	 * array containing {"some value", "other value", "last value"} would
	 * produce the following XML:
	 * 
	 * <pre>
	 * &lt;foo&gt;
	 *     &lt;bar&gt;
	 *         &lt;prop&gt;some value&lt;/prop&gt;
	 *         &lt;prop&gt;other value&lt;/prop&gt;
	 *         &lt;prop&gt;last value&lt;/prop&gt;
	 *     &lt;/bar&gt;
	 * &lt;/foo&gt;
	 * </pre>
	 *
	 * @param name
	 *            the name of the property.
	 * @param values
	 *            the values for the property (can be empty but not null).
	 */
	public void setProperties(String name, List<String> values) {
		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy,
		// stopping one short.
		Element element = document.getRootElement();
		for (int i = 0; i < propName.length - 1; i++) {
			// If we don't find this part of the property in the XML heirarchy
			// we add it as a new node
			if (element.element(propName[i]) == null) {
				element.addElement(propName[i]);
			}
			element = element.element(propName[i]);
		}
		String childName = propName[propName.length - 1];
		// We found matching property, clear all children.
		List<Element> toRemove = new ArrayList<Element>();
		Iterator iter = element.elementIterator(childName);
		while (iter.hasNext()) {
			toRemove.add((Element) iter.next());
		}
		for (iter = toRemove.iterator(); iter.hasNext();) {
			element.remove((Element) iter.next());
		}
		// Add the new children.
		for (String value : values) {
			Element childElement = element.addElement(childName);
			if (value.startsWith("<![CDATA[")) {
				Iterator it = childElement.nodeIterator();
				while (it.hasNext()) {
					Node node = (Node) it.next();
					if (node instanceof CDATA) {
						childElement.remove(node);
						break;
					}
				}
				childElement.addCDATA(value.substring(9, value.length() - 3));
			} else {
				childElement.setText(XmlEscapers.xmlContentEscaper().escape(value));// StringEscapeUtils.escapeXml(value));
			}
		}
		saveProperties();

		// Generate event.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("value", values);

		// PropertyEventDispatcher.dispatchEvent(name,
		// PropertyEventDispatcher.EventType.xml_property_set, params);

	}

	/**
	 * Sets the value of the specified property. If the property doesn't
	 * currently exist, it will be automatically created.
	 *
	 * @param name
	 *            the name of the property to set.
	 * @param value
	 *            the new value for the property.
	 */
	public synchronized void setProperty(String name, String value) {
		
		
		if (!XmlEscapers.xmlContentEscaper().escape(name).equals(name)) {
			throw new IllegalArgumentException();// L10NUtils.getMessage("002155"));
		}
		if (name == null) {
			return;
		}
		if (value == null) {
			value = "";
		}

		// Set cache correctly with prop name and value.
		propertyCache.put(name, value);

		String[] propName = parsePropertyName(name);
		// Search for this property by traversing down the XML heirarchy.
		Element element = document.getRootElement();
		for (String aPropName : propName) {
			// If we don't find this part of the property in the XML heirarchy
			// we add it as a new node
			if (element.element(aPropName) == null) {
				element.addElement(aPropName);
			}
			element = element.element(aPropName);
		}
		// Set the value of the property in this node.
		if (value.startsWith("<![CDATA[")) {
			Iterator it = element.nodeIterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				if (node instanceof CDATA) {
					element.remove(node);
					break;
				}
			}
			element.addCDATA(value.substring(9, value.length() - 3));
		} else {
			element.setText(value);
		}
		// Write the XML properties to disk
		saveProperties();

		// Generate event.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("value", value);

		// PropertyEventDispatcher.dispatchEvent(name,
		// PropertyEventDispatcher.EventType.xml_property_set, params);
	}
}