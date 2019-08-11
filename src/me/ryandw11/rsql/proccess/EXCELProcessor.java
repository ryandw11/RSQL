package me.ryandw11.rsql.proccess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import me.ryandw11.rsql.properties.subproperties.ExcelProperties;

/**
 * Handle the Excel process for saving data.
 * @author Ryandw11
 *
 */
public class ExcelProcessor {
	
	private ExcelProperties ep;
	public ExcelProcessor(ExcelProperties ep) {
		this.ep = ep;
	}
	
	@SuppressWarnings({ "unchecked", "resource" })
	public void processExcel(List<Object> obs) {
		Class<?> clazz = obs.get(0).getClass();
		XSSFWorkbook workbook = new XSSFWorkbook();
		if(!ep.getFile().exists())
			workbook = new XSSFWorkbook();
		else {		
			try {
				FileInputStream fin = new FileInputStream(ep.getFile()); 
				workbook = new XSSFWorkbook(fin);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		XSSFSheet sheet;
		if(workbook.getSheet(clazz.getSimpleName()) == null)
         sheet = workbook.createSheet(clazz.getSimpleName());
		else
			sheet = workbook.getSheet(clazz.getSimpleName());
        int rowNum = 0;
        
        for(Object o : obs) {
        	Row row = sheet.createRow(rowNum++);
        	
        	int colNum = 0;
        	for(Field f : clazz.getFields()) {
        		Cell cell = row.createCell(colNum++);		
        		try {
        			Class<?> fclazz = f.get(o).getClass();
        			if(fclazz == Integer.class)
        				cell.setCellValue((int) f.get(o));
        			if (fclazz == String.class) 
        				cell.setCellValue((String) f.get(o));
        			if (fclazz == Double.class)
        				cell.setCellValue((double) f.get(o));
        			if(fclazz == Float.class) 
        				cell.setCellValue((double) f.get(o));
        			if(fclazz == List.class || fclazz == ArrayList.class || fclazz.getSimpleName().equals("ArrayList")) {
        				ParameterizedType listType = (ParameterizedType) f.getGenericType();
    			        Class<?> clz = (Class<?>) listType.getActualTypeArguments()[0];
    			        if(clz == String.class) {
    			        	cell.setCellValue(this.generateList((List<String>) f.get(o)));
    			        }
        			}
        		}catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
        	}
        }
        try {
    		FileOutputStream outputStream = new FileOutputStream(ep.getFile());
    		workbook.write(outputStream);
    		workbook.close();
    	}catch(FileNotFoundException e) {
    		
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
	}
	
	@SuppressWarnings("deprecation")
	public List<Object> getExcel(Class<?> clazz){
		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook();
		if(!ep.getFile().exists())
			return null;
		else {		
			try {
				FileInputStream fin = new FileInputStream(ep.getFile()); 
				workbook = new XSSFWorkbook(fin);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		XSSFSheet sheet;
		if(workbook.getSheet(clazz.getSimpleName()) == null)
			return null;
		else
			sheet = workbook.getSheet(clazz.getSimpleName());
		
		Iterator<Row> iterator = sheet.iterator();
		
		List<Object> output = new ArrayList<>();
		
		 while (iterator.hasNext()) {
			 Row currentRow = iterator.next();
             Iterator<Cell> cellIterator = currentRow.iterator();
             
             int i = 0;
             Object obj = null;
         	try {
  				Constructor<?> ctor = clazz.getConstructor();
  				obj = ctor.newInstance();
  			} catch (NoSuchMethodException | SecurityException | InstantiationException | 
  						IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
  				e.printStackTrace();
  			}
             while (cellIterator.hasNext()) {
            	Cell currentCell = cellIterator.next();
            	
            	try {
            		if(currentCell.getCellTypeEnum() == CellType.STRING) {
            			String s = currentCell.getStringCellValue();
            			if(this.isList(s.replace("'", "")))
            				clazz.getFields()[i].set(obj, this.parseList(s.replace("'", "")));
            			else
            				clazz.getFields()[i].set(obj, s);
            		}
            		if(currentCell.getCellTypeEnum() == CellType.NUMERIC) {
            			double d = currentCell.getNumericCellValue();
            			if(clazz.getFields()[i].getType() == int.class) {
            				clazz.getFields()[i].set(obj, (int) Math.round(d));
            			}
            			else if(clazz.getFields()[i].getType() == Float.class) {
            				clazz.getFields()[i].set(obj, (float) d);
            			}
            			else if(clazz.getFields()[i].getType() == Double.class) {
            				clazz.getFields()[i].set(obj, (double) d);
            			}
            			else {
            				clazz.getFields()[i].set(obj, d);
            			}
            		}
            	} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
					e.printStackTrace();
				}
            	i++;
             }
             output.add(obj);
		 }
		 return output;
	}
	
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
