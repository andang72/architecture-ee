package architecture.ee.util.maven;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.apache.commons.io.IOUtils;

import architecture.ee.util.NumberUtils;
import architecture.ee.util.StringUtils;

public class MavenVersionReader {
	
	/**
	 * <p>readVersionFromJar.</p>
	 *
	 * @param f a {@link java.io.File} object.
	 * @return MavenVersion
	 */
	public static final MavenVersion readVersionFromJar(File f){
		if (!f.exists())
			throw new IllegalArgumentException("File doesn't exists");
		try{
			JarFile jar = new JarFile(f);
			Enumeration<JarEntry> entries = jar.entries();
			while(entries.hasMoreElements()){
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith("META-INF") && entry.getName().endsWith("/pom.properties")){
					String myName = entry.getName();
					JarInputStream input = new JarInputStream(new FileInputStream(f));
					JarEntry fromStream = null;
					while(fromStream==null || (!fromStream.getName().equals(myName))){
						fromStream = input.getNextJarEntry();
					}
					int size = (int)fromStream.getSize();
					if (size>0 ) {
						byte[] b = new byte[size];
					    input.read(b);
					    String content = new String(b);
					    return readVersionFromString(content, f.lastModified());
				    }
					if (size == -1){
						StringBuilder b = new StringBuilder();
						int c;
						while( (c = input.read())!=-1){
							b.append((char)c);
						}
						return readVersionFromString(b.toString(), f.lastModified());

					}
				}
				
			}
			
			return null;
		}catch(IOException e){
			throw new IllegalArgumentException("Couldn't parse jar file", e);
		}

		
	}
	/**
	 * <p>readVersionFromString.</p>
	 *
	 * @param content a {@link java.lang.String} object.
	 * @param timestamp a long.
	 * @return  MavenVersion object
	 */
	public static final MavenVersion readVersionFromString(String content, long timestamp){
		String[] lines = StringUtils.tokenize(StringUtils.removeChar(content, '\r'), '\n');
		Map<String, String> properties = new HashMap<>();
		for (String line: lines){
			if (line!=null)
				line = line.trim();
			if (line==null || line.isEmpty() || line.startsWith("#") )
				continue;
			String[] tokens = StringUtils.tokenize(line, '=');
			if (tokens!=null && tokens.length==2)
				properties.put(tokens[0], tokens[1]);
		}
		MavenVersion ret = new MavenVersion();
		ret.setArtifact(properties.get("artifactId"));
		ret.setVersion(properties.get("version"));
		ret.setGroup(properties.get("groupId"));
		ret.setFileTimestamp(NumberUtils.makeISO8601TimestampString(timestamp));
		return ret;
	}
		
	/**
	 * <p>readVersionFromFile.</p>
	 *
	 * @param f a {@link java.io.File} object.
	 * @return a {@link architecture.ee.util.maven.MavenVersion} object.
	 */
	public static final MavenVersion readVersionFromFile(File f){
		if (!f.exists())
			throw new IllegalArgumentException("File doesn't exists");
		try{
			
			FileReader reader = new FileReader(f); 
			String content = IOUtils.toString(reader);
			//String content = IOUtils.readFileAtOnceAsString(f);
			
			return readVersionFromString(content,f.lastModified());
		}catch(IOException e){
			throw new IllegalArgumentException("Couldn't parse file", e);
		}
	}
	
	/**
	 * <p>findVersionInDirectory.</p>
	 *
	 * @param dir a {@link java.io.File} object.
	 * @return a {@link architecture.ee.util.maven.MavenVersion} object.
	 */
	public static final MavenVersion findVersionInDirectory(File dir){
		if (!dir.exists())
			throw new IllegalArgumentException("Directory "+dir.getAbsolutePath()+" doesn't exists.");
		if (!dir.isDirectory())
			throw new IllegalArgumentException("Directory "+dir.getAbsolutePath()+" is not a directory.");
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.getName().equals("pom.properties")) {
				return readVersionFromFile(file);
			}
		}

		for (File aDir : files) {
			if (!aDir.isDirectory())
				continue;
			MavenVersion v = findVersionInDirectory(aDir);
			if (v != null)
				return v;
		}
		
		return null;
		
		
	}
	
	/**
	 * <p>findJarInDirectory.</p>
	 *
	 * @param dir a {@link java.io.File} object.
	 * @param artifactName a {@link java.lang.String} object.
	 * @return a {@link architecture.ee.util.maven.MavenVersion} object.
	 */
	public static final MavenVersion findJarInDirectory(File dir, String artifactName){
		if (!dir.exists())
			throw new IllegalArgumentException("Directory "+dir.getAbsolutePath()+" doesn't exists.");
		if (!dir.isDirectory())
			throw new IllegalArgumentException("Directory "+dir.getAbsolutePath()+" is not a directory.");
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.getName().startsWith(artifactName)) {
				return readVersionFromJar(file);
			}
		}
		return null;
		
		
	}

	/**
	 * <p>main.</p>
	 *
	 * @param args a {@link java.lang.String} object.
	 */
	public static void main(String... args) {

	}
}
