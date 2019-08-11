package me.ryandw11.rsql.properties.subproperties;

import java.io.File;

import me.ryandw11.rsql.properties.Properties;
import me.ryandw11.rsql.properties.RProperties;

public class ExcelProperties implements RProperties {
	
	private File file;

	@Override
	public Properties getProperty() {
		return Properties.EXCEL;
	}
	
	public ExcelProperties() {
		this.file = new File("example.xlsx");
	}
	
	public ExcelProperties setFile(String name) {
		this.file = new File(name);
		return this;
	}
	
	public File getFile() {
		return file;
	}

}
