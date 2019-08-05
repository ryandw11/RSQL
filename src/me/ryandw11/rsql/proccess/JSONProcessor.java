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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

/**
 * Handles the processing of JSON.
 * @author Ryandw11
 *
 */
public class JSONProcessor {
	private String filename;
	public JSONProcessor(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Process the objects then save it to the specified json file.
	 * @param objs The list of objects.
	 */
	public void proccessJSON(List<Object> objs) {
		File file = new File(filename);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Get the class related to the list of objects.
		Class<?> clazz = objs.get(0).getClass();
		// Setup GSON with a custom serializer that prevents ints from displaying as doubles in the json.
		Gson gson = new GsonBuilder().setPrettyPrinting().
		        registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {   
		            @Override
		            public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
		                if(src == src.longValue())
		                    return new JsonPrimitive(src.longValue());          
		                return new JsonPrimitive(src);
		            }}).create();
		// Gets the current data from the json file.
		JSONTables jst = (JSONTables) gson.fromJson(this.getJSONString(file), new TypeToken<JSONTables>() {}.getType());
		Map<String, List<Object>> current;
		try {
			current = jst.getObjectList();
		}catch (NullPointerException ex) {
			current = new HashMap<String, List<Object>>();
		}
		// If the table already exists.
		if(current.containsKey(clazz.getSimpleName()))
			current.remove(clazz.getSimpleName());
		// Add the table to the list of tables.
		current.put(clazz.getSimpleName(), objs);
		// Convert the list of tables back to json.
		String json = gson.toJson(new JSONTables().setObjectList(current));
		// Write and save it to the file.
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
	
	/**
	 * Get the objects stored from the JSON.
	 * @param clazz The class you want data for.
	 * @return A list of objects that contain the data. (Returns null if the file is not found!)
	 */
	public List<Object> getJSON(Class<?> clazz) {
		// Get the file and see if it exts.
		File file = new File(filename);
		if(!file.exists()) return null;
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		// Loads in the json from the file with help from apache's IOUtils class.
		String json = "";
		try {
			json = IOUtils.toString(is, "UTF-8");
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Gets GSON with the custom serializer.
		Gson gson = new GsonBuilder().setPrettyPrinting().
		        registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {   
		            @Override
		            public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
		                if(src == src.longValue())
		                    return new JsonPrimitive(src.longValue());          
		                return new JsonPrimitive(src);
		            }}).create();
		// Gets the tables from the json.
		JSONTables jst = (JSONTables) gson.fromJson(json, JSONTables.class);
		// Gets the list of tables.
		Map<String, List<Object>> obs = jst.getObjectList();
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
					f.set(obj, ((LinkedTreeMap<?, ?>) o).get(f.getName()));//ob.get(i)
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// JSON has a tendecy to confuse ints as doubles. This fixes that and converts the double back to an int.
					if(((LinkedTreeMap<?, ?>) o).get(f.getName()).getClass().getSimpleName().equals("Double")
							&& f.getType().getTypeName().equals("int")) {
						try {
							f.set(obj, (int) Math.round((double) ((LinkedTreeMap<?, ?>) o).get(f.getName())));
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
	
	/**
	 * Get the text from the json file.
	 * @param f The file to get the text from.
	 * @return A string of text.
	 */
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
