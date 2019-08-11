package me.ryandw11.rsql.properties.subproperties;

import java.io.File;

import me.ryandw11.rsql.properties.Properties;
import me.ryandw11.rsql.properties.RProperties;

public class SQLProperties implements RProperties{

	private String name = "sample";
	
	@Override
	public Properties getProperty() {
		return Properties.SQL;
	}
	
	public SQLProperties setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public File getFile() {
		return new File(name + ".db");
	}

}
