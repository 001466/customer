package com.ec.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

public class PropertiesUtils {

	private  static final Logger  LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

	private Properties prop = new Properties();
	private File       file;
	public PropertiesUtils(String resource) {
		super();
		this.file = new File(resource);
		try {
			
			if(!this.file.exists()){
				File dir = new File(this.file.getAbsolutePath().substring(0, this.file.getAbsolutePath().lastIndexOf(File.separator)));
				if (!dir.exists())
					dir.mkdirs();
				this.file.createNewFile();
			}
			
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			this.prop.load(in);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	public String get(String key) {
		return prop.getProperty(key);
	}
	
	public boolean containsKey(String key) {
			return prop.containsKey(key);
	}

	public Set<Entry<Object, Object>> values() {
		return prop.entrySet();
	}

	public void put(String key, String value) {
		FileOutputStream outputFile=null;
		try {
			prop.setProperty(key, value);
			outputFile = new FileOutputStream(this.file);
			prop.store(outputFile, "modify "+key);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
		}finally{
			try {
				outputFile.close();
				outputFile.flush();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	public void remove(String key) {
		FileOutputStream outputFile=null;
		try {
			prop.remove(key);
			outputFile = new FileOutputStream(this.file);
			prop.store(outputFile, "remove "+key);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
		} finally{
			try {
				outputFile.close();
				outputFile.flush();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	

	public static void main(String[] args) {
		PropertiesUtils p = new PropertiesUtils("strategy.properties");
		for (Entry<Object, Object> e : p.values()) {
			System.out.println(e);
		}

	}
}
