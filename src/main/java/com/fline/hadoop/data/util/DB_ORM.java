package com.fline.hadoop.data.util;

import java.util.HashMap;
import java.util.List;

public interface DB_ORM {
	
	public String getDriver();
	
	public String getUrl();
	
	public String[] getTables();

	/**
	 * 
	 * @param tablename
	 *            the table which contains the fields user need.
	 * @return fields definition
	 */
	public List<String[]> getFields(String tablename);

	/**
	 * create a configration for creating DataSouce
	 * 
	 * @param tablename
	 *            rdb table name
	 * @param fields
	 *            selected cols
	 * @param otherparams
	 * @return rdb configuration map for creating DataSource
	 */
	public HashMap<String, String> createDBDescription(String tablename,
			String[] fields, String[] otherparams);

	public void close();
}
