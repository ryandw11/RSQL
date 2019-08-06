package me.ryandw11.rsql.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelProperties implements RProperties {
	
	private XSSFWorkbook workbook;
	private File file;

	@Override
	public Properties getProperty() {
		return Properties.EXCEL;
	}
	
	public ExcelProperties(String file) {
		this.file = new File(file);
		if(!this.file.exists())
			workbook = new XSSFWorkbook();
		else {		
			try {
				FileInputStream fin = new FileInputStream(file); 
				workbook = new XSSFWorkbook(fin);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public XSSFWorkbook getWorkbook() {
		return workbook;
	}
	
	public File getFile() {
		return file;
	}

}
