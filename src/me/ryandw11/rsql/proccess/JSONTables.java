package me.ryandw11.rsql.proccess;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JSONTables {
	public Map<String, List<Object>> rsql;
	
	@JsonIgnore
	public JSONTables setObjectList(Map<String, List<Object>> objs) {
		this.rsql = objs;
		return this;
	}
	
	@JsonIgnore
	public Map<String, List<Object>> getObjectList(){
		return rsql;
	}

}
