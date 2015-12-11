package com.hetao.util;

import java.util.Locale;

import org.apache.hadoop.conf.Configuration;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.fline.hadoop.data.client.HDFSDataManager;
import com.fline.hadoop.data.util.bigdata.Solr_Query;

/**
 * 
 * @author hetao
 *
 */
public class ResourceUtils {

    private static MessageSource coreAppSetting;
	
    private static HDFSDataManager hdfSDataManager = null;
    
    private static Solr_Query solr_Query = null;
    
    public static String getImageWebUrl(){
    	return getAppSetting("image_web_url");
    }
    
    public static String getJobUrl(){
    	return getAppSetting("job_url");
    }
    
    public static String getProjectUrl(){
    	return getAppSetting("project_url");
    }
    
    public static String getAmbariUrl(){
    	return getAppSetting("ambari_url");
    }
    
    public static String getDefaultfs(){
    	return getAppSetting("fs_defaultfs");
    }
    
    public static String getSolrMasterUrl(){
    	return getAppSetting("solr_master_url");
    }
    
    public static String getRemoteLinuxIP(){
    	return getAppSetting("remote_linux_ip");
    }
    
    public static Solr_Query getSolrInstance() {
        try {
			if (solr_Query == null) {  
				return	new Solr_Query(getSolrMasterUrl());
			 }  
		} catch (Exception e) {
			e.printStackTrace();
		}
      return solr_Query;
   }
  
    public static HDFSDataManager getHDFSDataManager() {
         try {
			if (hdfSDataManager == null) {  
				 Configuration conf = new Configuration();
				 conf = new Configuration();
			     conf.set("fs.defaultFS", getDefaultfs());
			     hdfSDataManager = new HDFSDataManager(conf);
			 }  
		} catch (Exception e) {
			e.printStackTrace();
		}
       return hdfSDataManager;
    }

	public static void initMessageSource(MessageSource caSetting) {
		if (coreAppSetting == null) {
			coreAppSetting = caSetting;
		}
	}

	public static boolean isInit() {
		return coreAppSetting != null && coreAppSetting != null;
	}

	public static HDFSDataManager getHDFSDataManager1(){
		try {
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", getDefaultfs());
			return new HDFSDataManager(conf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String[] getDirs(String key){
		return getAppSetting(key).substring(1, getAppSetting(key).length()).replace("/", ",").split(",");
	}
	
	public static String getAppSetting(String key) {
		String returnValue = "???" + key + "???";
		if (coreAppSetting != null) {
			try {
				returnValue = coreAppSetting.getMessage(key, new Object[0], Locale.CHINESE);
			} catch (NoSuchMessageException e) {
				e.printStackTrace();
			}
		}
		return returnValue;
	}
}
