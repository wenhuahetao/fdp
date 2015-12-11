package com.fline.hadoop.data.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.impl.YarnClientImpl;
import org.apache.hadoop.yarn.exceptions.YarnException;

public class Test {

	public static void main(String[] args) throws YarnException, IOException {
		double aa = Double.parseDouble(new DecimalFormat("######").format(0.00122222));
		System.out.println(aa);
		
		
		Configuration conf = new Configuration();
		conf.addResource(new FileInputStream(DataTransporter.class
				.getClassLoader().getResource("").getPath()
				+ "hbase-site.xml"));
		YarnClient yarnClient = new YarnClientImpl();
		yarnClient.init(conf);
		yarnClient.start();
		ApplicationId appId = ApplicationId.newInstance(1446690210857L, 0066);
		ApplicationReport applicationReport = yarnClient.getApplicationReport(appId);
		System.out.println(applicationReport.getFinalApplicationStatus());
		
	}
}
