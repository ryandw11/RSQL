package me.ryandw11.rsql.properties;

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
	
	public String getFile() {
		return file;
	}
	
	public JSONProperties() {
		this.file = "example.json";
	}

}
