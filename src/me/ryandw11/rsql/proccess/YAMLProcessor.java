package me.ryandw11.rsql.proccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import test.DoubleTrouble;

import java.util.LinkedHashMap;

public class YAMLProcessor {
	
	public void processYAML(List<Object> objs) {
		File file = new File("test.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Class<?> clazz = objs.get(0).getClass();
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		JSONTables jt;
		try {
			jt = mapper.readValue(file, JSONTables.class);
		} catch (IOException e) {
			jt = new JSONTables();
			jt.setObjectList(new HashMap<String, List<Object>>());
//			e.printStackTrace();
		}
		
		if(jt.getObjectList() == null) jt.setObjectList(new HashMap<String, List<Object>>());
		Map<String, List<Object>> current = jt.getObjectList();
		if(current.containsKey(clazz.getSimpleName()))
			current.remove(clazz.getSimpleName());
		
		current.put(clazz.getSimpleName(), objs);
		try {
			mapper.writeValue(file, new JSONTables().setObjectList(current));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Object> getYAML(Class<?> clazz) {
		File file = new File("test.yml");
		if(!file.exists()) return null;
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		JSONTables jt;
		try {
			jt = mapper.readValue(file, JSONTables.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Map<String, List<Object>> obs = jt.getObjectList();
		// Gets the list of objects from within the specified table.
		List<Object> ob = obs.get(clazz.getSimpleName());
		// The final list of objects that will be returned.
		List<Object> output = new ArrayList<>();
		for(Object o : ob) {
			Object obj = null;
			// Constructs a new object.
			// TODO attempt with null constructor.
			try {
				Constructor<?> ctor = clazz.getConstructor();
				obj = ctor.newInstance();
			} catch (NoSuchMethodException | SecurityException | InstantiationException | 
					IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			// loop through the global fields.
			for(Field f : clazz.getFields()) {
				// Set the fields to the data provided from the LinkedTreeMap
				try {
					f.set(obj, ((LinkedHashMap<?,?>) o).get(f.getName()));//ob.get(i)
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// JSON has a tendecy to confuse ints as doubles. This fixes that and converts the double back to an int.
					if(((LinkedHashMap<?,?>) o).get(f.getName()).getClass().getSimpleName().equals("Double")
							&& f.getType().getTypeName().equals("int")) {
						try {
							f.set(obj, (int) Math.round((double) ((LinkedHashMap<?,?>) o).get(f.getName())));
						} catch (IllegalArgumentException | IllegalAccessException e1) {
							e1.printStackTrace();
						}
					} else
						e.printStackTrace();
				}
			}
			// Add the new object to the list of final objects.
			output.add(obj);
		}
		// Return the list of final objects.
		return output;
		
	}

}
