package test;

import me.ryandw11.rsql.orm.Column;
import me.ryandw11.rsql.orm.Table;

@Table
public class ExampleTable {
	@Column
	public int id = 20;
	
	@Column
	public String name = "Cool Stuff";
	
	public ExampleTable(Object o) {}
}
