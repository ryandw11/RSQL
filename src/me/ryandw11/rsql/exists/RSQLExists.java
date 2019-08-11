package me.ryandw11.rsql.exists;

import java.util.List;

import me.ryandw11.rsql.RSQL;
import me.ryandw11.rsql.properties.RProperties;

public class RSQLExists {
	
	public boolean exists(RProperties op) {
		return op.getFile().exists();
	}
	
	public boolean exists(RProperties op, Class<?> clazz, RSQL rInst) {
		if(!exists(op)) return false;
		
//		Properties p = op.getProperty();
//		if(p == Properties.SQL) {
//			
//		}else {
//			List<Object> obj = rInst.get(clazz);
//			if(obj == null) return false;
//		}
		
		List<Object> obj = rInst.get(clazz);
		if(obj == null) return false;
		
		return true;
	}

}
