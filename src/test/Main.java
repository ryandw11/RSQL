package test;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import me.ryandw11.rsql.RSQL;
import me.ryandw11.rsql.properties.JSONProperties;
import me.ryandw11.rsql.properties.RProperties;
import me.ryandw11.rsql.properties.SQLProperties;

public class Main {

	public static void main(String[] args) {
		RProperties p = new SQLProperties().setName("Example");
//		RProperties p = new JSONProperties();
		RSQL rsql = new RSQL(p);
		ExampleTable ex2 = new ExampleTable(new Object());
		ex2.id = 2;
		ex2.name = "Test";
		rsql.process(Arrays.asList(new ExampleTable(new Object()), ex2));
		
		List<Object> obj = rsql.get(ExampleTable.class);
//		System.out.println(obj);
		List<ExampleTable> ex = new ArrayList<>();
		for(Object o : obj) {
			ex.add((ExampleTable) o);
		}
		System.out.println(ex.get(1).name);

	}

}
