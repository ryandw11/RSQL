package me.ryandw11.rsql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.sqlite.SQLiteException;

import me.ryandw11.rsql.exists.RSQLExists;
import me.ryandw11.rsql.orm.Column;
import me.ryandw11.rsql.orm.Table;
import me.ryandw11.rsql.proccess.ExcelProcessor;
import me.ryandw11.rsql.proccess.JSONProcessor;
import me.ryandw11.rsql.proccess.YAMLProcessor;
import me.ryandw11.rsql.properties.Properties;
import me.ryandw11.rsql.properties.RProperties;
import me.ryandw11.rsql.properties.subproperties.ExcelProperties;
import me.ryandw11.rsql.properties.subproperties.JSONProperties;
import me.ryandw11.rsql.properties.subproperties.SQLProperties;

/**
 * To handle SQL interaction for a special project.
 * @author Ryandw11
 *
 */
public class RSQL {
	
	private Properties type;
	private RProperties op;
	
	/**
	 * Defines what storage type is used.
	 * @param op The property defines what storage type is used.
	 */
	public RSQL(RProperties op) {
		this.type = op.getProperty();
		this.op = op;
	}
	
	/**
	 * Process a list of the same table objects.<br>
	 * Tables of other object types will not be changed.
	 * <p><b>If a table current exists for that object, it will be overwritten.</b></p>
	 * @param o The list of objects.
	 */
	public void process(List<Object> o) {
		if(type == Properties.SQL) {
			proccessSQL(o);
		}
		if(type == Properties.JSON) {
			JSONProcessor jspro = new JSONProcessor(((JSONProperties) op).getName());
			jspro.proccessJSON(o);
		}
		if(type == Properties.YAML) {
			YAMLProcessor ympro = new YAMLProcessor(op);
			ympro.processYAML(o);
		}
		if(type == Properties.EXCEL) {
			ExcelProcessor expro = new ExcelProcessor((ExcelProperties) op);
			expro.processExcel(o);
		}
	}
	
	/**
	 * Get an object table from storage. <br>
	 * Will return null if the file does not exist and/or if the data is invalid.
	 * @param clazz The class you want to get data for.
	 * @return The list of objects for the specified class.
	 */
	public List<Object> get(Class<?> clazz){
		if(type == Properties.SQL) {
			return this.getSQL(clazz);
		}
		if(type == Properties.JSON) {
			JSONProcessor jspro = new JSONProcessor(((JSONProperties) op).getName());
			return jspro.getJSON(clazz);
		}
		if(type == Properties.YAML) {
			YAMLProcessor ympro = new YAMLProcessor(op);
			return ympro.getYAML(clazz);
		}
		if(type == Properties.EXCEL) {
			ExcelProcessor expro = new ExcelProcessor((ExcelProperties) op);
			return expro.getExcel(clazz);
		}
		return null;
	}
	
	/**
	 * Check if the RSQL filex exists.
	 * @return If it exists.
	 */
	public boolean exists() {
		RSQLExists rsqle = new RSQLExists();
		return rsqle.exists(op);
	}
	
	/**
	 * Check to see if a table exists
	 * @param c The table class
	 * @return If the table exists.
	 */
	public boolean exists(Class<?> c) {
		RSQLExists rsqle = new RSQLExists();
		return rsqle.exists(op, c, this);
	}
	
	private void proccessSQL(List<Object> o) {
		SQLProperties sp = (SQLProperties) op;
		Connection connection = null;
		Object an = o.get(0);
		if(an.getClass().getAnnotation(Table.class) == null) return;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + sp.getName() + ".db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			String name = an.getClass().getSimpleName();
			
			statement.executeUpdate("drop table if exists " + name);
			statement.executeUpdate("create table " + name + "(" + this.getTableColumns(an.getClass()) + ")");
			for(Object obj : o) {
				statement.executeUpdate("insert into " + name + " values(" + this.getValues(obj) + ")");
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		finally{
          try{
            if(connection != null)
              connection.close();
          }
          catch(SQLException e){
            System.err.println(e.getMessage());
          }
        }
	}
	
	private List<Object> getSQL(Class<? extends Object> clazz) {
		SQLProperties sp = (SQLProperties) op;
		Connection connection = null;
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + sp.getName() + ".db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			
			String name = clazz.getSimpleName();
			
			List<Object> output = new ArrayList<>();
			
			List<Field> fo = this.getColumns(clazz);
			ResultSet rs;
			try {
				rs = statement.executeQuery("select * from " + name);
			}catch(SQLiteException ex) {
				return null;
			}
			while(rs.next()) {
				Constructor<?> ctor = clazz.getConstructor();
				Object obj = ctor.newInstance();
				for(Field f : fo) {
					if(rs.getObject(f.getName()) instanceof String && this.isList((String) rs.getObject(f.getName())))
						f.set(obj, this.parseList((String) rs.getObject(f.getName())));
					else
						f.set(obj, rs.getObject(f.getName()));
				}
				output.add(obj);
			}
			
			connection.close();
			return output;
		}catch(SQLException e){
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		finally{
          try{
            if(connection != null)
              connection.close();
          }
          catch(SQLException e){
            System.err.println(e.getMessage());
          }
        }
		return null;
	}
	
	private List<Field> getColumns(Class<? extends Object> c){
		List<Field> f = Arrays.asList(c.getFields());
		List<Field> output = new ArrayList<>();
		for(Field fi : f) {
			if(fi.isAnnotationPresent(Column.class))
				output.add(fi);
		}
		
		return output;
	}
	
	private String getTableColumns(Class<? extends Object> c) {
		List<Field> fields = this.getColumns(c);
		String s = "";
		int i = 0;
		for(Field f : fields) {
			s += f.getName() + " " + this.getType(f);
			if(i < fields.size() - 1)
				s += ", ";
			i++;
		}
		return s;
	}
	
	@SuppressWarnings("unchecked")
	private String getValues(Object o) {
		Class<? extends Object> clazz = o.getClass();
		List<Field> columns = this.getColumns(clazz);
		String s = "";
		
		int i = 0;
		for(Field f : columns) {
			try {
				String val = "";
				if(f.get(o) instanceof String)
					val = "'" + f.get(o).toString() + "'";
				else
					val = f.get(o).toString();
				if(f.getType() == List.class) {
					ParameterizedType listType = (ParameterizedType) f.getGenericType();
			        Class<?> clz = (Class<?>) listType.getActualTypeArguments()[0];
			        if(clz == String.class) {
			        	val = this.generateList((List<String>) f.get(o));
			        }
				}
				s += val;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if(i < columns.size() - 1)
				s += ", ";
			i++;
		}
		
		
		
		return s;	
	}
	
	private String getType(Field f) {
		if(f.getType() == String.class) {
			return "string";
		}
		if(f.getType() == int.class) {
			return "integer";
		}
		if(f.getType() == float.class) {
			return "float";
		}
		if(f.getType() == double.class) {
			return "double";
		}
		if(f.getType() == List.class) {
			ParameterizedType listType = (ParameterizedType) f.getGenericType();
	        Class<?> clazz = (Class<?>) listType.getActualTypeArguments()[0];
	        if(clazz == String.class) {
	        	return "string";
	        }
		}
		return "string";
	}
	
	/**
	 * Generates a list to be used in sql.
	 * @param list
	 * @return
	 */
	private String generateList(List<String> list) {
		String output = "'RSQLLIST[";
		int i = 0;
		for(String s : list) {
			output += "`" + s.replace("'", "%$1$%").replace("`", "%$2$%").replace("\"", "%$3$%").replace("|", "%$4$%")
					.replace("[", "%$5$%").replace("]", "%$6$%");
			if(i < list.size() - 1)
				output += "`|";
			i++;
		}
		output += "`]'";
		return output;
	}
	
	/**
	 * Check if a string is a list.
	 * @param s The input string.
	 * @return If it is a list.
	 */
	private boolean isList(String s) {
		return s.startsWith("RSQLLIST");
	}
	
	/**
	 * Parse a generated list.
	 * @param s The input string
	 * @return The list generated from the string.
	 */
	private List<String> parseList(String s){
		String in = s;
		in = in.replace("RSQLLIST", "");
		in = in.replace("[", "").replace("]", "");
		String[] lists = in.split(Pattern.quote("|"));
		if(lists.length < 1) return new ArrayList<>();
		List<String> output = new ArrayList<>();
		for(String st : lists) {
			String process = st.replace("`", "");
			process = process.replace("%$1$%", "'").replace("%$2$%", "`").replace("%$3$%", "\"").replace("%$4$%", "|")
					.replace("%$5$%", "[").replace("%$6$%", "]");
			output.add(process);
		}
		return output;
	}

}
