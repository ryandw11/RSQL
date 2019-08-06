package test;

import java.util.Arrays;
import java.util.List;

import me.ryandw11.rsql.orm.Column;
import me.ryandw11.rsql.orm.Table;

@Table
public class FunTable {
	@Column
	public String name;
	@Column
	public List<String> desc;
	
	public FunTable setName(String name) {
		this.name = name;
		return this;
	}
	
	public FunTable setDesc(String... s) {
		desc = Arrays.asList(s);
		return this;
	}

}
