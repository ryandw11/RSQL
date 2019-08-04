package me.ryandw11.rsql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ryandw11.rsql.orm.Column;
import me.ryandw11.rsql.orm.Table;
import me.ryandw11.rsql.proccess.JSONProcessor;
import me.ryandw11.rsql.properties.Properties;
import me.ryandw11.rsql.properties.RProperties;
import me.ryandw11.rsql.properties.SQLProperties;

/**
 * To handle SQL interaction for a special project.
 * @author Ryandw11
 *
 */
public class RSQL {
	
	private Properties type;
	private RProperties op;
	
	public RSQL(RProperties op) {
		this.type = op.getProperty();
		this.op = op;
	}
	
	public void process(List<Object> o) {
		if(type == Properties.SQL) {
			proccessSQL(o);
		}
		if(type == Properties.JSON) {
			JSONProcessor jspro = new JSONProcessor();
			jspro.proccessJSON(o);
		}
	}
	
	public List<Object> get(Class<?> clazz){
		if(type == Properties.SQL) {
			return this.getSQL(clazz);
		}
		if(type == Properties.JSON) {
			JSONProcessor jspro = new JSONProcessor();
			return jspro.getJSON(clazz);
		}
		return null;
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
			
			ResultSet rs = statement.executeQuery("select * from " + name);
			while(rs.next()) {
				Constructor<?> ctor = clazz.getConstructor(Object.class);
				Object obj = ctor.newInstance(new Object());
				for(Field f : fo) {
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
		return "string";
	}

}
