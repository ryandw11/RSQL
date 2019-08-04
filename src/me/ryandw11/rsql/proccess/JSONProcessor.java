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
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

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
		
		Gson gson = new Gson();
		String json = gson.toJson(objs);
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
		System.out.println(json);
		Gson gson = new Gson();
		List<Object> ob = gson.fromJson(json, List.class);
		
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
					f.set(obj, ob.get(i));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					//e.printStackTrace();
				}
				i++;
			}
			output.add(obj);
		}
		
		return output;
		
	}

}
