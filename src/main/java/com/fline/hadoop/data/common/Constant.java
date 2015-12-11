package com.fline.hadoop.data.common;

import java.util.HashSet;

public class Constant {
	public static final int NORMAL_FILE_DATASOURCE = 1;
	public static final String NORMAL_FILE_DATASOURCE_NAME = "NORMAL_FILE_DATASOURCE";
	public static final int RDBS_DATASOURCE = 5;
	public static final String RDBS_DATASOURCE_NAME = "org.apache.sqoop.connector.jdbc.GenericJdbcConnector";
	public static final int HBASE_DATASOURCE = 6;
	public static final String HBASE_DATASOURCE_NAME = "com.feixian.sqoop.connector.hbase.HbaseConnector";
	public static final int HDFS_DATASOURCE = 7;
	public static final String HDFS_DATASOURCE_NAME = "org.apache.sqoop.connector.hdfs.HdfsConnector";
	public static final int ES_DATASOURCE = 8;
	public static final String ES_DATASOURCE_NAME = "org.apache.sqoop.connector.es.ESConnector";
	public static final int SOLR_DATASOURCE = 9;
	public static final String SOLR_DATASOURCE_NAME = "org.apache.sqoop.connector.solr.SolrConnector";

	private static HashSet<Integer> localinput = new HashSet<Integer>();
	static {
		// prepare localinputdatasource
		localinput.add(NORMAL_FILE_DATASOURCE);
	}

	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

}
