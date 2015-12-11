package com.fline.hadoop.data.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;

import com.hetao.util.ResourceUtils;

public class StaticConfiguration {
	private static String logDir = null;
	private static String sqoopShell = null;
	private static String flumeConfigDir = null;
	private static String oozieurl = null;
	private static String oozieConfigDir = null;
	private static String hadoopconfdir = null;
	private static int serverport = 0;
	private static int SQOOP_CONNECOTRID_JDBC = 4;
	private static int SQOOP_CONNECTORID_KAFKA = 2;
	private static int SQOOP_CONNECTORID_HDFS = 3;
	private static int SQOOP_CONNECTORID_FEIXIANHBASE = 10;
	private static int SQOOP_CONNECTORID_FEIXIANSOLR = 6;
	private static String SQOOP_SERVER_URL = "";
	private static String HBASE_ZK_URLS = "";
	private static String HBASE_ZK_NODE = "";
	private static Configuration conf = new Configuration();

	private static boolean inited = false;

	public static String HADOOP_HOME = "N:/hadoop-common-2.2.0-bin-master";

	static {
		System.setProperty("hadoop.home.dir", HADOOP_HOME);
	}

	public static void loadConfigs(String configpath) throws Exception {
		Properties p = new Properties();
		FileInputStream fis = new FileInputStream(new File(configpath));
		p.load(fis);
		fis.close();
		logDir = p.getProperty("log.dir");
		sqoopShell = p.getProperty("sqoop.shell");
		if (sqoopShell == null) {
			throw new Exception(
					"sqoop.shell has not been set... in conf/server.properties");
		}
		flumeConfigDir = p.getProperty("flume.config.dir");
		serverport = Integer.valueOf(p.getProperty("server.port", "10501"));
		oozieurl = p.getProperty("oozie.url", "http://localhost:11000/oozie");
		oozieConfigDir = p.getProperty("oozie.userconfdir");
		hadoopconfdir = p.getProperty("hadoopconfdir", "./conf");
		SQOOP_SERVER_URL = p.getProperty("sqoop.serverurl",
				"http://121.40.99.124:12000/sqoop/");
		HBASE_ZK_URLS = p.getProperty("hbase.zk.urls", ResourceUtils.getRemoteLinuxIP() + ":2181");
		HBASE_ZK_NODE = p.getProperty("hbase.zk.node", "/hbase-unsecure");
		if (new File(hadoopconfdir).exists() == false) {
			throw new Exception("hadoop conf dir does not exist. path = "
					+ hadoopconfdir);
		}
		if (oozieConfigDir == null) {
			throw new Exception(
					"oozie.userconfdir has not been set... in conf/server.properties");
		}
		inited = true;
	}

	public static String getOozieConfigDir() {
		return oozieConfigDir;
	}

	public static boolean isInited() {
		return inited;
	}

	public static String getLogDir() {
		return logDir;
	}

	public static String getSqoopShell() {
		return sqoopShell;
	}

	public static String getFlumeConfigDir() {
		return flumeConfigDir;
	}

	public static int getServerPort() {
		return serverport;
	}

	public static Configuration getConf() {
		return conf;
	}

	public static String getOozieurl() {
		return oozieurl;
	}

	public static int getServerport() {
		return serverport;
	}

	public static int getSQOOP_CONNECOTRID_JDBC() {
		return SQOOP_CONNECOTRID_JDBC;
	}

	public static int getSQOOP_CONNECTORID_KAFKA() {
		return SQOOP_CONNECTORID_KAFKA;
	}

	public static int getSQOOP_CONNECTORID_HDFS() {
		return SQOOP_CONNECTORID_HDFS;
	}

	public static int getSQOOP_CONNECTORID_FEIXIANHBASE() {
		return SQOOP_CONNECTORID_FEIXIANHBASE;
	}

	public static int getSQOOP_CONNECTORID_FEIXIANSOLR() {
		return SQOOP_CONNECTORID_FEIXIANSOLR;
	}

	public static String getSQOOP_SERVER_URL() {
		return SQOOP_SERVER_URL;
	}

	public static String getHBASE_ZK_URLS() {
		return HBASE_ZK_URLS;
	}

	public static String getHBASE_ZK_NODE() {
		return HBASE_ZK_NODE;
	}
}
