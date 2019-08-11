package me.ryandw11.rsql.properties.subproperties;

import java.io.File;

import me.ryandw11.rsql.properties.Properties;
import me.ryandw11.rsql.properties.RProperties;

public class JSONProperties implements RProperties {
	
	private String file;

	@Override
	public Properties getProperty() {
		return Properties.JSON;
	}
	
	public JSONProperties setFile(String f) {
		this.file = f;
		return this;
	}
	
	public File getFile() {
		return new File(file);
	}
	
	public String getName() {
		return file;
	}
	
	public JSONProperties() {
		this.file = "example.json";
	}

}
