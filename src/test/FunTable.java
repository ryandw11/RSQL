package test;

import java.util.List;

import me.ryandw11.rsql.orm.Column;
import me.ryandw11.rsql.orm.Table;

@Table
public class FunTable {
	@Column
	public String name;
	@Column
	public List<String> desc;
	
	public FunTable(Object o) {}

}
