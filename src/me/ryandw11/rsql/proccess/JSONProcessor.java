package me.ryandw11.rsql.proccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class JSONProcessor {
	
	public void proccessJSON(List<Object> objs) {
		File file = new File("test.json");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Class<?> clazz = objs.get(0).getClass(); 
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		JSONTables jst = (JSONTables) gson.fromJson(this.getJSONString(file), JSONTables.class);
		JSONTables jst = (JSONTables) gson.fromJson(this.getJSONString(file), new TypeToken<JSONTables>() {}.getType());
		Map<String, List<Object>> current;
		try {
			current = jst.getObjectList();
		}catch (NullPointerException ex) {
			current = new HashMap<String, List<Object>>();
		}
		System.out.println(current);
		if(current.containsKey(clazz.getSimpleName()))
			current.remove(clazz.getSimpleName());
		current.put(clazz.getSimpleName(), objs);
		
		String json = gson.toJson(new JSONTables().setObjectList(current));
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(json);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Object> getJSON(Class<?> clazz) {
		File file = new File("test.json");
		if(!file.exists()) return null;
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String json = "";
		try {
			json = IOUtils.toString(is, "UTF-8");
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Gson gson = new Gson();
		JSONTables jst = (JSONTables) gson.fromJson(json, JSONTables.class);
		Map<String, List<Object>> obs = jst.getObjectList();
		List<Object> ob = obs.get(clazz.getSimpleName());
		List<Object> output = new ArrayList<>();
		for(Object o : ob) {
			Object obj = null;
			try {
				Constructor<?> ctor = clazz.getConstructor(Object.class);
				obj = ctor.newInstance(new Object());
			} catch (NoSuchMethodException | SecurityException | InstantiationException | 
					IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int i = 0;
			for(Field f : clazz.getFields()) {
				try {
					System.out.println(((LinkedTreeMap<?, ?>) o).get(f.getName()).getClass().getTypeName());
					System.out.println(f.getName() + ":" + f.getType().getName());
					f.set(obj, ((LinkedTreeMap<?, ?>) o).get(f.getName()));//ob.get(i)
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					if(((LinkedTreeMap<?, ?>) o).get(f.getName()).getClass().getTypeName().equals("java.lang.Double")
							&& f.getType().getTypeName().equals("int")) {
						try {
							f.set(obj, Integer.valueOf(((LinkedTreeMap<?, ?>) o).get(f.getName()).toString().split(".")[0]));
						} catch (IllegalArgumentException | IllegalAccessException e1) {
							e1.printStackTrace();
						}
					} else
						e.printStackTrace();
				}
				i++;
			}
			output.add(obj);
		}
		
		return output;
		
	}
	
	private String getJSONString(File f) {
		InputStream is = null;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String json = "";
		try {
			json = IOUtils.toString(is, "UTF-8");
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

}
