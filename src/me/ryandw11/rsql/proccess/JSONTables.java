package me.ryandw11.rsql.proccess;

import java.util.List;
import java.util.Map;

public class JSONTables {
	public Map<String, List<Object>> rsql;
	
	public JSONTables setObjectList(Map<String, List<Object>> objs) {
		this.rsql = objs;
		return this;
	}
	
	public Map<String, List<Object>> getObjectList(){
		return rsql;
	}

}
