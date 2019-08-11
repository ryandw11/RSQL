package me.ryandw11.rsql.properties.subproperties;

import java.io.File;

import me.ryandw11.rsql.properties.Properties;
import me.ryandw11.rsql.properties.RProperties;

public class YAMLProperties implements RProperties{
	
	private String file;
	public YAMLProperties() {
		file = "example.yml";
	}
	
	@Override
	public Properties getProperty() {
		return Properties.YAML;
	}
	
	public YAMLProperties setFile(String s) {
		this.file = s;
		return this;
	}
	
	public File getFile() {
		return new File(file);
	}
	
	public String getName() {
		return file;
	}

}
