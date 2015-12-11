package com.fline.hadoop.data.util.bigdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.impl.YarnClientImpl;
import com.fline.hadoop.data.common.HDFSOperator;
import com.hetao.util.FileOperate;

public class JobInfo_Query {
	
	final static String buildDir = "/var/db/fdp/";
	public final static String DISK_HISTORY_TXT = "DISK_HISTORY.txt";
	public final static String DISK_TODAY_TXT = "DISK_TODAY.txt";
	public final static String MR_HISTORY_TXT = "MR_HISTORY.txt";
	public final static String MR_TODAY_TXT = "MR_TODAY.txt";
	
	private static JobInfo_Query defaultInstance = null;

	private YarnClient yarnclient = new YarnClientImpl();

	public JobInfo_Query(Configuration conf) {
		yarnclient.init(conf);
		yarnclient.start();
	}

	public static JobInfo_Query getDefaultInstance() {
		if (defaultInstance == null) {
			Configuration conf = new Configuration();
			try {
				conf.addResource(new FileInputStream(JobInfo_Query.class
						.getClassLoader().getResource("").getPath()
						+ "mapred-site.xml"));
				conf.addResource(new FileInputStream(JobInfo_Query.class
						.getClassLoader().getResource("").getPath()
						+ "yarn-site.xml"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			defaultInstance = new JobInfo_Query(conf);
		}
		return defaultInstance;
	}
	
	public String queryStateInfo(String applicationID) throws Exception {
		String[] splits = applicationID.split("_");
		if (splits.length != 3) {
			return null;
		} else {
			long systime = Long.valueOf(splits[1]);
			int jobid = Integer.valueOf(splits[2]);
			ApplicationId id = ApplicationId.newInstance(systime, jobid);
			ApplicationReport report = yarnclient.getApplicationReport(id);

			return report.getApplicationType() + "," + report.getYarnApplicationState().toString() + ","
					+ report.getFinalApplicationStatus().toString() + ","
					+ report.getStartTime() + "," + report.getFinishTime(); 
											
		}
	}
	
	public String queryState(String applicationID) throws Exception {
		String[] splits = applicationID.split("_");
		if (splits.length != 3) {
			return null;
		} else {
			long systime = Long.valueOf(splits[1]);
			int jobid = Integer.valueOf(splits[2]);
			ApplicationId id = ApplicationId.newInstance(systime, jobid);
			ApplicationReport report = yarnclient.getApplicationReport(id);

			return report.getYarnApplicationState().toString() + ","
					+ report.getFinalApplicationStatus().toString() + ","
					+ report.getProgress(); // return whether killed ,
											// successed, failed.
		}
	}

	public static List<String> readFile(String path){
		path = buildDir + path; 
		BufferedReader bufReader = null;
		List<String> List = new ArrayList<>(); 
		try {
			bufReader = new BufferedReader(new FileReader(path));
			String line = null;
			while( (line = bufReader.readLine())!=null){
				List.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bufReader != null ) {
				try {
					bufReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return List;
	}
	
	/**
	 * 查看今天的磁盘容量情况
	 */
	public void writeTodayDisk2File(){
		BufferedWriter bufWriter = null;
		try { 
			bufWriter = new BufferedWriter(new FileWriter(buildDir + DISK_TODAY_TXT, true));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time = sdf.format(new Date());
			Long[] diskUsed = HDFSOperator.getDefaultInstance().getDiskUsed4Array();
			HDFSOperator.getDefaultInstance().close();
			bufWriter.write(time + ",used," + diskUsed[0] + ",all," + diskUsed[1] + "\n");
			bufWriter.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (bufWriter != null) {
				try {
					bufWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void writeHistoryDisk2File(){
		BufferedWriter bufWriter = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		try { 
			String path = buildDir + DISK_HISTORY_TXT;
			bufWriter = new BufferedWriter(new FileWriter(path));
			System.out.println("***********path: " + path);
			Long[] diskUsed = HDFSOperator.getDefaultInstance().getDiskUsed4Array();
			System.out.println("***********diskUsed: " + diskUsed[0] + "********" + diskUsed[1]);
			HDFSOperator.getDefaultInstance().close();
			bufWriter.write(time + ",used," + diskUsed[0] + ",all," + diskUsed[1] + "\n");
			bufWriter.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (bufWriter != null) {
				try {
					bufWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void writeToday2File(){
		BufferedWriter bufWriter = null;
		try { 
			String path = buildDir + MR_TODAY_TXT;
			bufWriter = new BufferedWriter(new FileWriter(path, true));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time = sdf.format(new Date());
			Map<String, Long[]> queryAllUsedSource = queryAllUsedSource(time);
			if(queryAllUsedSource.size() == 0){
				bufWriter.write(time + ":" +  0 + "," +  0  + "\n");
				bufWriter.flush();
			}else{
				for (String key : queryAllUsedSource.keySet()) {
					bufWriter.write(key + ":" +  queryAllUsedSource.get(key)[0] + "," +  queryAllUsedSource.get(key)[1]  + "\n");
					bufWriter.flush();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (bufWriter != null) {
				try {
					bufWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void writeHistory2File(String path){
		BufferedWriter bufWriter = null;
		try { 
			if(path == null || "".equals(path)){
				path = buildDir + MR_HISTORY_TXT;
			}
			FileOperate.mkdir(path);
			bufWriter = new BufferedWriter(new FileWriter(path));
			Map<String, Long[]> queryAllUsedSource = queryAllUsedSource(null);
			Object[] key = queryAllUsedSource.keySet().toArray(); 
			Arrays.sort(key);  
			for (int i = 0; i < key.length; i++) {
				bufWriter.write(key[i] + ":" +  queryAllUsedSource.get(key[i])[0] + "," +  queryAllUsedSource.get(key[i])[1]  + "\n");
				bufWriter.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (bufWriter != null) {
				try {
					bufWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String queryApplicationNodes(String applicationID) throws Exception {
		String[] splits = applicationID.split("_");
		if (splits.length != 3) {
			return null;
		} else {
			long systime = Long.valueOf(splits[1]);
			int jobid = Integer.valueOf(splits[2]);
			ApplicationId id = ApplicationId.newInstance(systime, jobid);
			ApplicationReport report = yarnclient.getApplicationReport(id);
			ApplicationResourceUsageReport resourceReport = report
					.getApplicationResourceUsageReport();
			resourceReport.getUsedResources().getMemory();
			resourceReport.getUsedResources().getVirtualCores();
			return report.getYarnApplicationState().toString() + ","
					+ report.getFinalApplicationStatus().toString() + ","
					+ report.getProgress(); // return whether killed ,
											// successed, failed.
		}
	}

	public Map<String, Long[]> queryAllUsedSource(String today) throws Exception {
		List<ApplicationReport> applications = yarnclient.getApplications();
		Map<String, Long[]> map = new HashMap<String, Long[]>();
		for (ApplicationReport applicationReport : applications) {
			String[] tmpsplits = applicationReport
					.getApplicationResourceUsageReport().toString()
					.split("\\s+");
			long finishTime = applicationReport.getFinishTime();
			Date date = new Date(finishTime);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time = sdf.format(date);
			if(today != null && !time.equals(today)){
				Long[] time2long = time2long(sdf.format(new Date()));
				if(finishTime > time2long[1] || finishTime < time2long[0]){
					continue;
				}
			}
			Long[] vmArr = map.get(time);
			if (vmArr == null) {
				vmArr = new Long[2];
				vmArr[0] = Long.parseLong(tmpsplits[tmpsplits.length - 1]);
				vmArr[1] = Long.parseLong(tmpsplits[tmpsplits.length - 3]);
			} else {
				vmArr[0] += Long.parseLong(tmpsplits[tmpsplits.length - 1]);
				vmArr[1] += Long.parseLong(tmpsplits[tmpsplits.length - 3]);
			}
			map.put(time, vmArr);
		}
		return map;
	}
	
	public static Long[] time2long(String time) {
		try {
			Long[] lTime = new Long[2];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:ms");
			long startTime = sdf.parse(time + " 00:00:00").getTime();
			long endTime = sdf.parse(time + " 23:59:59").getTime();
			lTime[0] = startTime;
			lTime[1] = endTime;
			return lTime;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String queryUsedSource(String applicationID) throws Exception {
		String[] splits = applicationID.split("_");
		if (splits.length != 3) {
			return null;
		} else {
			long systime = Long.valueOf(splits[1]);
			int jobid = Integer.valueOf(splits[2]);
			ApplicationId id = ApplicationId.newInstance(systime, jobid);
			ApplicationReport appreport = yarnclient.getApplicationReport(id);
			String[] tmpsplits = appreport.getApplicationResourceUsageReport()
					.toString().split("\\s+");
			return "{vcores_sec:" + tmpsplits[tmpsplits.length - 1]
					+ ",memory_sec:" + tmpsplits[tmpsplits.length - 3] + "}";
		}
	}

	public void close() throws Exception {
		if (yarnclient != null) {
			yarnclient.stop();
			yarnclient.close();
		}
	}

	public static void main(String[] args) throws Exception {
		
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// Configuration conf = new Configuration();
		// conf.addResource(new FileInputStream(JobInfo_Query.class
		// .getClassLoader().getResource("").getPath()
		// + "mapred-site.xml"));
		// conf.addResource(new FileInputStream(JobInfo_Query.class
		// .getClassLoader().getResource("").getPath()
		// + "mapred-site.xml"));
		// YarnClient yarnclient = new YarnClientImpl();
		// yarnclient.init(conf);
		// yarnclient.start();
		// ApplicationId id = ApplicationId.newInstance(1446690210857l, 76);
		// List<ApplicationAttemptReport> reports = yarnclient
		// .getApplicationAttempts(id);
		// ApplicationReport appreport = yarnclient.getApplicationReport(id);
		// System.out.println(appreport.getApplicationResourceUsageReport()
		// .toString());
		JobInfo_Query.getDefaultInstance().queryAllUsedSource(null);
//		JobInfo_Query.getDefaultInstance().writeHistory2File();
		// System.out.println(JobInfo_Query.getDefaultInstance().queryUsedSource(
		// "application_1446690210857_0081"));
		// JobInfo_Query.getDefaultInstance().close();
		// appreport.getApplicationResourceUsageReport().getUsedResources().get
		// for (ApplicationAttemptReport report : reports) {
		// System.out.println(report.getYarnApplicationAttemptState());
		// }
		// yarnclient.close();
	}
}
