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
package architecture.ee.jdbc.sqlquery.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;

import architecture.ee.component.VariableMapImpl;
import architecture.ee.jdbc.sqlquery.builder.BuilderException;
import architecture.ee.jdbc.sqlquery.builder.xml.XmlSqlSetBuilder;
import architecture.ee.jdbc.sqlquery.factory.SqlQueryFactory;
import architecture.ee.service.Repository;
import architecture.ee.util.StringUtils;

public class DirectoryScanner {
	
	protected static class DirectoryListener implements FileAlterationListener {

		private SqlQueryFactory sqlQueryFactory;
		
		
		public DirectoryListener(SqlQueryFactory sqlQueryFactory) {
			this.sqlQueryFactory = sqlQueryFactory;
		}

		protected void buildFromFile(File file) throws BuilderException {
			try {
				XmlSqlSetBuilder builder = new XmlSqlSetBuilder(new FileInputStream(file), sqlQueryFactory.getConfiguration(), file.toURI().toString(), null);
				builder.parse();
			} catch (IOException e) {
				throw new BuilderException("build faild", e);
			}
		}

		public void onDirectoryChange(File directory) {			
		}

		public void onDirectoryCreate(File directory) {		
		}

		public void onDirectoryDelete(File directory) {
		}

		public void onFileChange(File file) {
			log.debug("change {}", file.toURI().toString());
			buildFromFile(file);
		}

		public void onFileCreate(File file) {
			log.debug("new {}", file.toURI().toString());
			buildFromFile(file);
		}

		public void onFileDelete(File file) {	
			log.debug("remove {}", file.toURI().toString());
			if (sqlQueryFactory.getConfiguration().isResourceLoaded(file.toURI().toString())) {
				sqlQueryFactory.getConfiguration().removeLoadedResource(file.toURI().toString());
			}
		}		
		

		public void onStart(FileAlterationObserver observer) {			
		}				
		
		public void onStop(FileAlterationObserver observer) {	
			
		}		
		
	}
	
	private static Logger log = LoggerFactory.getLogger(DirectoryScanner.class);
	
	private static final int DEFAULT_POOL_INTERVAL_MILLIS = 10000;
	
	private int pollIntervalMillis = DEFAULT_POOL_INTERVAL_MILLIS;
	
	private SqlQueryFactory sqlQueryFactory;
	
	private FileAlterationMonitor monitor; 
	
	private ResourceLoader resourceLoader;
	
	@Autowired( required = true)
	private Repository repository;

	
	
	private String directory;

	public DirectoryScanner() {
		this.resourceLoader = new FileSystemResourceLoader();	
	}

	protected void buildFromDirectory(File file) throws BuilderException {
		for( File f : FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(sqlQueryFactory.getConfiguration().getSuffix()), FileFilterUtils.trueFileFilter())){			
			if( !sqlQueryFactory.getConfiguration().isResourceLoaded(f.toURI().toString())){
				log.debug("building " + f.toURI().toString());
				try {					
					XmlSqlSetBuilder builder = new XmlSqlSetBuilder(new FileInputStream(f), sqlQueryFactory.getConfiguration(), f.toURI().toString(), null);
					builder.parse();
				} catch (IOException e) {
					throw new BuilderException("build faild", e);
				}	
			}
		}
	}

	public void destroy() throws Exception {	
		
		if( monitor != null)
		{
			log.debug("stopping sqlquery directory scanner...");
			monitor.stop();
			log.debug("stopped sqlquery directory scanner...");
		}
	}

	public String getDirectory() {
		return directory;
	}

	public int getPollIntervalMillis() {
		return pollIntervalMillis;
	}

	public SqlQueryFactory getSqlQueryFactory() {
		return sqlQueryFactory;
	}

	public void initialize() {				
				
		File directoryFile = null ;		
		if( !StringUtils.isNullOrEmpty(directory)){			
			VariableMapImpl impl = new VariableMapImpl(repository.getSetupApplicationProperties());
			String path = impl.expand(directory);
			log.debug("initializing scanner : {} > '{}'" , directory, path);
			if( StringUtils.startsWithIgnoreCase(path, "file:")){					
				try {
					directoryFile = resourceLoader.getResource(path).getFile();
				} catch (IOException e) { /* ignore */ }					
			}else{
				directoryFile = repository.getFile(path);
			}
		}
		log.debug("starting directory scanner : '{}'" , directory, directoryFile);	
		if( directoryFile != null)
			try {
				buildFromDirectory(directoryFile);
				start(directoryFile);
			} catch (Exception e) {
				e.printStackTrace();
			}			
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	
	public void setPollIntervalMillis(int pollIntervalMillis) {
		this.pollIntervalMillis = pollIntervalMillis;
	}
		
	public void setSqlQueryFactory(SqlQueryFactory sqlQueryFactory) {
		this.sqlQueryFactory = sqlQueryFactory;
	}
	
	public void start(File file ) throws Exception {
		log.debug("starting '{0}' file alter observer ...", file.getAbsolutePath());
		if( monitor == null)
		{			
			monitor = new FileAlterationMonitor(pollIntervalMillis);	
			FileAlterationObserver observer = new FileAlterationObserver(file, FileFilterUtils.suffixFileFilter(sqlQueryFactory.getConfiguration().getSuffix()));
			observer.addListener(new DirectoryListener(sqlQueryFactory));
			monitor.addObserver(observer);			
		}
		monitor.start();
		log.debug("started '{0}' file alter observer ...", file.getAbsolutePath());
	}
	
}
