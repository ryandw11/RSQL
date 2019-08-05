package test;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import me.ryandw11.rsql.RSQL;
import me.ryandw11.rsql.properties.JSONProperties;
import me.ryandw11.rsql.properties.RProperties;
import me.ryandw11.rsql.properties.SQLProperties;
import me.ryandw11.rsql.properties.YAMLProperties;

public class Main {

	public static void main(String[] args) {
//		RSQL sql = new RSQL(new SQLProperties());
//		RSQL json = new RSQL(new JSONProperties());
//		
//		sql.process(Arrays.asList(new FunTable().setName("Yeet").setDesc("This is [an] description's !", "Yep | it i`s", "Cool huh?")));
//		json.process(Arrays.asList(new FunTable().setName("Yeet").setDesc("This is [an] description's !", "Yep | it i`s", "Yea it is!")));
//		DoubleTrouble dt = new DoubleTrouble();
//		dt.id = 4;
//		dt.idouble = 22.40;
//		json.process(Arrays.asList(dt));
//		
//		System.out.println(((FunTable) sql.get(FunTable.class).get(0)).desc);
//		System.out.println(((FunTable) json.get(FunTable.class).get(0)).desc);
		
		RSQL yaml = new RSQL(new YAMLProperties());
		
		yaml.process(Arrays.asList(new FunTable().setName("Yeet").setDesc("This is [an] description's !", "Yep | it i`s", "Cool huh?")));
		DoubleTrouble dt = new DoubleTrouble();
		dt.id = 4;
		dt.idouble = 22.40;
		yaml.process(Arrays.asList(dt));
		
		System.out.println(((FunTable) yaml.get(FunTable.class).get(0)).name);
		

	}

}
