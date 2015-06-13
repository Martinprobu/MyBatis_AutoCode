package com.uniweibo.util.gencode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class CodeGenDao {

	private JdbcTemplate jdbcTemplate;
	
	public static String basePackage;
	
	public static String basePackageSuffix;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public void setBasePackage(String basePackage) {
    	this.basePackage = basePackage;
    }
    
    public void setBasePackageSuffix(String basePackageSuffix) {
    	this.basePackageSuffix = basePackageSuffix;
    }

    public void doExecute(String sql) {
        this.jdbcTemplate.execute(sql);
    }
    
    public int doUpdate(String sql) {
        return this.jdbcTemplate.update(sql);
    }
    
    public List showTables(String sql) {
    	List<Map<String, Object>> resultList = this.jdbcTemplate.queryForList(sql);
    	List<String> list = new ArrayList<String>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	for(int i=0; i<resultList.size(); i++) {
    		map = resultList.get(i);
    		Set<String> keys = map.keySet();
    		String key = keys.iterator().next();
    		list.add(String.valueOf(map.get(key)));
    	}
    	return list;
    }
    
    public List descTable(String tableName) {
    	//id	int(11)	NO	PRI	NULL	auto_increment
    	List<Map<String, Object>> resultList = this.jdbcTemplate.queryForList("desc `" + tableName + "`");
    	Map<String, Object> map = new HashMap<String, Object>();
    	TableDesc table;
    	List<TableDesc> list = new ArrayList<CodeGenDao.TableDesc>();
    	for(int i=0; i<resultList.size(); i++) {
    		map = resultList.get(i);
    		table = new TableDesc(map.get("Field").toString(), map.get("Type").toString(), map.get("Null").toString(), map.get("Key").toString(), String.valueOf(map.get("Default")), String.valueOf(map.get("Extra")));//map.get("Key").toString(), map.get("Default").toString(), map.get("Extra").toString());
    		list.add(table);
    		System.out.println(table.toString());
    	}
    	
    	return list;
    }
    
    class TableDesc {
    	private String field;
    	private String type;
    	private String allowNull;
    	private String key;
    	private String defaultValue;
    	private String extra;
    	
    	public TableDesc() {}
    	
    	public TableDesc(String field, String type, String allowNull, String key, String defaultValue, String extra) {
    		this.field = field;
    		this.type = type;
    		this.allowNull = allowNull;
    		this.key = key;
    		this.defaultValue = defaultValue;
    		this.extra = extra;
    	}
    	
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getAllowNull() {
			return allowNull;
		}
		public void setAllowNull(String allowNull) {
			this.allowNull = allowNull;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getDefaultValue() {
			return defaultValue;
		}
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		public String getExtra() {
			return extra;
		}
		public void setExtra(String extra) {
			this.extra = extra;
		}
    	
		public String toString() {
			return "field=" + this.field + "," +
					"type=" + this.type + "," +
					"null=" + this.allowNull + "," +
					"key=" + this.key + "," +
					"default=" + this.defaultValue + "," +
					"extra=" + this.extra;
		}
    }
}
