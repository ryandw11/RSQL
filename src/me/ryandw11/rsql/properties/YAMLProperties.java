package me.ryandw11.rsql.properties;

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
	
	public String getFile() {
		return file;
	}

}
