package com.fline.hadoop.data.util.rdb;

import java.util.HashMap;
import java.util.List;

public interface RDB_ORM {
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
	 * @param partitioninfo
	 * @return rdb configuration map for creating DataSource
	 */
	public HashMap<String, String> createRDBDescription(String tablename,
			String[] fields, String partitioninfo);

	public void close();
}
