package me.ryandw11.rsql.properties;

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

}
