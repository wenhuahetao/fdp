package com.fline.hadoop.data.client;

import org.apache.hadoop.conf.Configuration;

import com.fline.hadoop.data.common.HDFSOperator;
import com.fline.hadoop.data.core.StaticConfiguration;

public class HDFSDataManager extends HDFSOperator {
	public HDFSDataManager(Configuration conf) throws Exception {
		super(conf);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("hadoop.home.dir", StaticConfiguration.HADOOP_HOME);
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://fdp-master/");
		HDFSDataManager operator = new HDFSDataManager(conf);
		// operator.uploadFile("./pom.xml", "/user/zhongliang/pom.xml", true,
		// null);
		System.out.println(operator.showFileContent("/user/public/123456.txt"));
		// System.out.println(operator.showFileContent("/user/public/hetao/a.txt"));
		// operator.uploadFile("C:\\Users\\zhongliang\\Desktop\\a.txt",
		// "/user/public/hetao/a.txt", false, null);
		operator.close();
		// String[] fileinfos = operator.selectFileInfos("/user/zhongliang/");
		// String[] fileinfos =
		// operator.selectFileStatusWithPattern("/user/zhongliang/",
		// ".*/.*/pom.xml");
		// for (String s : fileinfos) {
		// System.out.println(s);
		// }
		// operator.download2local("/user/public/hetao1/20150815.txt", "./");
		// operator.copyTo("/user/zhongliang/t1/small_multicar_input.csv",
		// "/user/zhongliang/small_multicar_input2.csv");
		// operator.uploadZipFile("./testinput/small_multicar_input.zip",
		// "/user/zhongliang/t1", null);
	}
}
