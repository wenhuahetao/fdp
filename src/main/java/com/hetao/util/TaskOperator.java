package com.hetao.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TaskOperator {

	final static String JOB = "";
	final static String JOBATTEMPTS = "/jobattempts";
	final static String TASKS = "/tasks";
	final static String COUNTERS = "/counters";
	final static String ATTEMPTS = "/attempts";
	final static String SEARCH_TASK_COUNTER = "/collection/searchTaskCounter";
	
	final static String buildDir = "/var/db/fdp/";
	
	public static Map<String,Map<String,Object>> jobsMap = new HashMap<String,Map<String,Object>>();
	
	//作业资源消耗总量
	public static String searchResourceCounter(String url,String appId) {
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("applicationId","application_"+appId);
		String json = HttpClientUtil.getHttp(url + SEARCH_TASK_COUNTER, paramMap);
		return json;
	}
	
	//作业运行的节点数
	public static int searchNode4Count(String url,String jobid) {
		Map<String, List<String>> searchTaskAttempts = searchTaskAttempts(url,jobid);
		return searchTaskAttempts.size();
	}
	
	//异常节点数
	public static String searchNode4Error(String url,String jobid) {
		Map<String, List<String>> searchTaskAttempts = searchTaskAttempts(url,jobid);
		Map<String, List<String>> errorTaskAttempts = new HashMap<String,List<String>>();
		for (String key : searchTaskAttempts.keySet()) {
			List<String> list = searchTaskAttempts.get(key);
			boolean errorFlag = false;
			for (String info : list) {
				String[] infoSplit = info.split(",");
				if(infoSplit[1] != null && !infoSplit[1].equals("SUCCEEDED")){
					errorFlag = true;
				}
			}
			if(errorFlag){
				errorTaskAttempts.put(key, list);
			}
		}
		String json = JSONObject.fromObject(errorTaskAttempts).toString();
		return json;
	}
	
	//查询节点排名
	public static String searchNode4Ranking(String url,String jobid) {
		Map<String,Long> rankMap = new HashMap<String,Long>();
		Map<String, List<String>> searchTaskAttempts = searchTaskAttempts(url,jobid);
		for (String key : searchTaskAttempts.keySet()) {
			List<String> list = searchTaskAttempts.get(key);
			Long elapsedTime = 0L;
			for (String info : list) {
				String[] infoSplit = info.split(",");
				if(infoSplit[0] != null){
					elapsedTime += Long.parseLong(infoSplit[0]);
				}
			}
			rankMap.put(key, elapsedTime);
		}
        List<Map.Entry<String,Long>> list = new ArrayList<Map.Entry<String,Long>>(rankMap.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Long>>() {
            //升序排序
            public int compare(Entry<String, Long> o1,
                    Entry<String, Long> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
       
        String json = JSONArray.fromObject(list).toString();
        return json;
	}
	
	//获取每个作业节点信息
	public static Map<String,List<String>> searchTaskAttempts(String url,String jobid) {
		Map<String,String> taskMap = searchTask(url,jobid);
		Map<String,List<String>> taskAttemptMap = new HashMap<String,List<String>>();
		System.out.println(taskMap.size());
		for (String key : taskMap.keySet()) {
			String httpUrl = url + "_" + jobid + TASKS;
			httpUrl += "/" + key + "/attempts";
			String http = HttpClientUtil.getHttp(httpUrl, new HashMap<String, String>());
			JSONObject jsonObj = JSONObject.fromObject(http);
			if(jsonObj != null){
				JSONObject jo = null;
				if(jsonObj.get("taskAttempts") != null && !"null".equals(jsonObj.get("taskAttempts").toString())){
					jo = (JSONObject) jsonObj.get("taskAttempts");
				}
				if(jo != null){
					JSONArray ja = (JSONArray) jo.get("taskAttempt");
					for (int i = 0; i < ja.size(); i++) {
						JSONObject taskAttempt = (JSONObject) ja.get(i);
						String id = (String) taskAttempt.get("nodeHttpAddress");
						StringBuffer infoBuff = new StringBuffer();
						infoBuff.append(taskAttempt.get("elapsedTime")).append(",").append(taskAttempt.get("state")).append(",").append(taskAttempt.get("type"));
						List<String> taskAttemptList = taskAttemptMap.get(id);
						if(taskAttemptList==null){
							taskAttemptList = new ArrayList<String>();
						}else{
							taskAttemptList = taskAttemptMap.get(id);
						}
						taskAttemptList.add(infoBuff.toString());
						taskAttemptMap.put(id, taskAttemptList);
					}
				}
			}
		}
		return taskAttemptMap;
	}
	
	//获取每个作业节点信息 资源消耗总量 
	public static String searchTaskCounter(String url,String jobid) {
		String http = HttpClientUtil.getHttp(url + "_" + jobid + COUNTERS, new HashMap<String, String>());
		JSONObject jsonObj = JSONObject.fromObject(http);
		JSONObject jo = (JSONObject) jsonObj.get("jobCounters");
		JSONArray ja = (JSONArray) jo.get("counterGroup");
		for (int i = 0; i < ja.size(); i++) {
			JSONObject taskAttempt = (JSONObject) ja.get(i);
			String counterGroupName = (String) taskAttempt.get("counterGroupName");
			if("org.apache.hadoop.mapreduce.JobCounter".equals(counterGroupName)){
				JSONArray counterArr = (JSONArray) taskAttempt.get("counter");
				Long vm = 0L;
				Long mm = 0L;
				for (int j = 0; j < counterArr.size(); j++) {
					JSONObject counter = (JSONObject) counterArr.get(j);
					String name = (String) counter.get("name");
					
					if("VCORES_MILLIS_MAPS".equals(name)){
						Long totalCounterValue = Long.parseLong(counter.get("totalCounterValue").toString());
						vm += totalCounterValue/1000;
					}
					if("VCORES_MILLIS_REDUCES".equals(name)){
						Long totalCounterValue = Long.parseLong(counter.get("totalCounterValue").toString());
						vm += totalCounterValue/1000;
					}
					if("MB_MILLIS_MAPS".equals(name)){
						Long totalCounterValue = Long.parseLong(counter.get("totalCounterValue").toString());
						mm += totalCounterValue/1000;
					}
					if("MB_MILLIS_REDUCES".equals(name)){
						Long totalCounterValue = Long.parseLong(counter.get("totalCounterValue").toString());
						mm += totalCounterValue/1000;
					}
				}
				return  mm + " MB-seconds, " + vm  + " vcore-seconds";
			}
		}
		return null;
	}
	
	//统计作业耗时（每个Task） 
	public static Map<String,String> searchTask(String url,String jobid) {
		String http = HttpClientUtil.getHttp(url + "_" + jobid + TASKS, new HashMap<String, String>());
		JSONObject jsonObj = JSONObject.fromObject(http);
		JSONObject jo = (JSONObject) jsonObj.get("tasks");
		JSONArray ja = (JSONArray) jo.get("task");
		Map<String,String> map = new HashMap<String,String>();
		for (int i = 0; i < ja.size(); i++) {
			JSONObject task = (JSONObject) ja.get(i);
			String id = (String) task.get("id");
			StringBuffer infoBuff = new StringBuffer();
			infoBuff.append(task.get("elapsedTime")).append(",").append(task.get("type")).append(",").append(task.get("state"));
			map.put(id, infoBuff.toString());
		}
		return map;
	}
	
	public static int searchJobattempts(String url,String jobid) {
		String http = HttpClientUtil.getHttp(url + "_" + jobid + JOBATTEMPTS, new HashMap<String, String>());
		JSONObject jsonObj = JSONObject.fromObject(http);
		JSONObject jo = (JSONObject) jsonObj.get("jobAttempts");
		JSONArray ja = (JSONArray) jo.get("jobAttempt");
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jobattempt = ja.getJSONObject(i);
			String nodeId = (String) jobattempt.get("nodeId");
			System.out.println(jobattempt.get("nodeId")); 
		}
		return ja.size();
	}
	
	public static String searchTask4json(String url,String jobid) {
		Map<String,String> map = searchTask(url,jobid);
		String json = JSONObject.fromObject(map).toString();
		return json; 
	}

	public static String searchJob(String url,String jobid) {
		String http = HttpClientUtil.getHttp(url + "_" + jobid + JOB, new HashMap<String, String>());
		JSONObject jsonObj = JSONObject.fromObject(http);
		JSONObject jo = (JSONObject) jsonObj.get("job");
		StringBuffer infoBuffer = new StringBuffer();
		Map<String,String> map = new HashMap<String,String>();
		infoBuffer.append(jo.get("state"));
		map.put(jo.get("id").toString(), infoBuffer.toString());
		String json = JSONObject.fromObject(map).toString();
		return json;
	}
	
	public static JSONArray listJobs(String url){
		String http = HttpClientUtil.getHttp(url.substring(0,url.lastIndexOf("/")), new HashMap<String, String>());
		JSONObject jsonObj = JSONObject.fromObject(http);
		JSONObject jo = (JSONObject) jsonObj.get("jobs");
		JSONArray ja = (JSONArray) jo.get("job");
		return ja;
	}
	
	public static String searchJobs(String url,String bigUrl){
		String readFile = readFile();
		System.out.println("********readFile: " + readFile);
		if(readFile != null && !"".equals(readFile)){
			System.out.println("********readFile: " + readFile);
			Map<String,Map<String,Object>> jsonObj = JSONObject.fromObject(readFile);
			for (String key : jsonObj.keySet()) {
				if(!jobsMap.containsKey(key)){
					jobsMap.put(key, jsonObj.get(key));
				}
			}
		}
		JSONArray ja = listJobs(url);
		for (int i = 0; i < ja.size(); i++) {
			Map<String,Object> jobMap= new HashMap<String,Object>();
			JSONObject jobattempt = ja.getJSONObject(i);
			String jobId = (String) jobattempt.get("id");
			jobId = jobId.replace("job_", "");
			jobMap.put("searchJob", searchJob(url, jobId)); 
			jobMap.put("searchNode4Error", searchNode4Error(url, jobId));
			jobMap.put("searchNode4Count", searchNode4Count(url, jobId));
			jobMap.put("searchNode4Ranking", searchNode4Ranking(url, jobId));
			jobMap.put("searchResourceCounter", searchResourceCounter(bigUrl, jobId));
			jobsMap.put(jobId, jobMap);
		}
		String mapInfo = JSONObject.fromObject(jobsMap).toString();
		System.out.println("mapInfo: " + mapInfo);
		write2file(mapInfo);
		return mapInfo;
	}
	
	public static String readFile(){
		String	path = buildDir + "yarn-task";
		BufferedReader bufReader = null;
		StringBuffer sb = new StringBuffer();
		try {
			bufReader = new BufferedReader(new FileReader(path));
			String line = null;
			while( (line = bufReader.readLine())!=null){
				sb.append(line);
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
		return sb.toString();
	}
	
	public static void write2file(String args) {
		String path = buildDir + "yarn-task";
		BufferedWriter bufWriter = null;
		try { 
			bufWriter = new BufferedWriter(new FileWriter(path, true));
			bufWriter.write(args);
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
	
	public static void main(String[] args) {
		String URL = "http://114.215.249.44:8084/fline_hdfs";  
		String JOB_URL = "http://114.215.249.44:19888/ws/v1/history/mapreduce/jobs/job";
		System.out.println(searchJobs(JOB_URL,URL));
	}
}
