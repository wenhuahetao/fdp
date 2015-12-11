package com.fline.hadoop.data.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;

import com.fline.hadoop.data.common.HDFSOperator;

public class RunShellJob {
	static HDFSOperator operator = null;

	static {
		Configuration conf = new Configuration();
		try {
			conf.addResource(new FileInputStream(RunShellJob.class
					.getClassLoader().getResource("").getPath()
					+ "/hdfs-site.xml"));
			operator = new HDFSOperator(conf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String[] searchContent(String hdfspath, String token)
			throws Exception {
		String tmppath = "/tmp/DATAMANAGERtmp" + System.currentTimeMillis();
		operator.download2local(hdfspath, tmppath);
		String cmd = "find " + tmppath + " -exec grep -l \"" + token
				+ "\" {} \\;";
		String result = runJob(cmd);
		return result.split("\n");
	}

	public static String runJob(String shellcmd) {
		try {
			Process pid = Runtime.getRuntime().exec(
					new String[] { "/bin/sh", "-c", shellcmd });
			// String starttime = new
			// Date(System.currentTimeMillis()).toString();
			if (pid != null) {
				pid.waitFor();
				// String endtime = new Date(System.currentTimeMillis())
				// .toString();
				BufferedReader in_reader = new BufferedReader(
						new InputStreamReader(pid.getInputStream()));
				BufferedReader error_reader = new BufferedReader(
						new InputStreamReader(pid.getErrorStream()));
				StringBuilder strbuilder = new StringBuilder();
				// strbuilder.append("RunJob Report:\n");
				// strbuilder.append("Start at:");
				// strbuilder.append(starttime);
				// strbuilder.append('\n');
				// strbuilder.append("End at:");
				// strbuilder.append(endtime);
				// strbuilder.append('\n');
				// strbuilder.append("[OUTPUT RESULT]:");
				while (in_reader.ready()) {
					String line = in_reader.readLine();
					if (line == null)
						break;
					strbuilder.append(line);
					strbuilder.append('\n');
				}
				// strbuilder.append("[ERROR RESULT]:");
				while (error_reader.ready()) {
					String line = error_reader.readLine();
					if (line == null)
						break;
					strbuilder.append(line);
					strbuilder.append('\n');
				}
				error_reader.close();
				in_reader.close();
				return strbuilder.toString();
			} else {
				return "RunShellJob error: create process error... cmd = "
						+ shellcmd;
			}
		} catch (Exception e) {
			return "RunShellJob error. shellcmd = " + shellcmd
					+ ".ErrorMessage:" + e.getMessage();
		}
	}
	public static String executeMapReduceTask(String mapreduceFile, String className, String buildDir,String logPath, String args) throws Exception {
		String parma = "/usr/lib/fdp/mapreduce_exec.sh " + buildDir + " "
				+ mapreduceFile + " " + className + " " + logPath + "  \"" + args + "\"";
		System.out.println(parma);
		return RunShellJob.runJob(parma);
		
	}
	public static String executeShellCmds(String tmpCmdShellFile, String args) throws Exception {
		RunShellJob.runJob("chmod +x " + tmpCmdShellFile);
		return RunShellJob.runJob(tmpCmdShellFile + " " + args);
	}

	public static void main(String[] args) throws Exception {
		/**
		String[] results = RunShellJob
				.searchContent("/user/zhongliang", "goal");
		for (String result : results) {
			System.out.println(result);
		}
		*/
		//System.out.println(RunShellJob.runJob("/home/zhongliang/datamanager/mapreduce_exec.sh ./testDir JT_LinePredict_train.java JT_LinePredict_train \"/user/root/gd_train_data.txt /user/zhongliang/2\" "));
		if (args[0].equals("cmd")) {
			System.out.println(RunShellJob.runJob(args[1]));
		} else if (args[0].equals("mr")) {
			///tmp/fdp/mapreduce.$key.log
//			RunShellJob.executeMapReduceTask("JT_LinePredict_train.java", "JT_LinePredict_train", "./testDir", "/user/root/gd_train_data.txt /user/zhongliang/5");
		} else if (args[0].equals("cmds")) {
			if (args.length == 2) {
				System.out.println(RunShellJob.executeShellCmds(args[1], null));
			} else {
				System.out.println(RunShellJob.executeShellCmds(args[1], args[2]));
			}
		}
	}
}
