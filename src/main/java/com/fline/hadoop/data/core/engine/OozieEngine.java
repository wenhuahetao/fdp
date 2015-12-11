package com.fline.hadoop.data.core.engine;

import java.util.Properties;

import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;

public class OozieEngine {

	private static String oozie_url = null;

	public static void setOozie_url(String oozie_url) {
		OozieEngine.oozie_url = oozie_url;
	}

	public static void addOozieJob(Properties conf) throws Exception {
		if (oozie_url == null) {
			throw new Exception("Oozie master url could not be null.");
		}
		OozieClient wc = new OozieClient(oozie_url);
		try {
			wc.run(conf);
		} catch (OozieClientException e) {
			e.printStackTrace();
		}
	}
}
