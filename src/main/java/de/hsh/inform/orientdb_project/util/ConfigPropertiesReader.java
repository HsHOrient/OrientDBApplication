package de.hsh.inform.orientdb_project.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigPropertiesReader {
	
	public String dbuser;
	public String dbpass;
	public String dbname;
	public String dbhost;
	public String filename;
	public boolean limitedImport;
	public int importLimit;
	
	public ConfigPropertiesReader() {
		Properties props = null;
		try {
			props = this.readProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.dbuser = props.getProperty("dbuser");
		this.dbpass = props.getProperty("dbpass");
		this.dbname = props.getProperty("dbname");
		this.dbhost = props.getProperty("dbhost");
		this.filename = props.getProperty("filename");
		this.limitedImport = Boolean.valueOf(props.getProperty("limitedImport"));
		this.importLimit = Integer.valueOf(props.getProperty("importLimit"));
	}

	private Properties readProperties() throws IOException {
		String propFileName = "config.properties";
		Properties properties = null;
		InputStream inputStream = null;
		try {
			properties = new Properties();
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if(inputStream != null) {
				properties.load(inputStream);
			} else {
				throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath! Make sure to use src/resources as source folder!");
			}
		} catch (Exception e) {
			System.err.println("Exception: " + e);
		} finally {
			inputStream.close();
		}
		return properties;
	}
}
