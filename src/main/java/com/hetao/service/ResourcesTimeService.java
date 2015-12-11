package com.hetao.service;

import java.util.Date;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.fline.hadoop.data.util.bigdata.JobInfo_Query;
import com.hetao.util.TaskOperator;

@Component
public class ResourcesTimeService {
	
	@Autowired private MessageSource coreAppSetting;

	/**
	 * 每天 23 点 59 分 执行定时任务 
	 * 写入当天磁盘情况
	 * @throws Exception
	 */
	@Scheduled(cron="0 59 23 * * ? ")
	public void executeDisk() throws Exception {
		System.out.println(new Date() + "----DISK：0 59 23 * * ? begin");
		JobInfo_Query.getDefaultInstance().writeHistoryDisk2File();
//		JobInfo_Query.getDefaultInstance().writeTodayDisk2File();
		System.out.println(new Date() + "----DISK：0 59 23 * * ? end");
	}
	
	/**
	 * 每天 23 点 59 分 执行定时任务 
	 * 写入当天资源情况
	 * @throws Exception
	 */
	@Scheduled(cron="0 58 23 * * ? ")
	public void executeMR() throws Exception {
		System.out.println(new Date() + "----MR ：0 58 23 * * ? begin");
		JobInfo_Query.getDefaultInstance().writeHistory2File(null);
//		JobInfo_Query.getDefaultInstance().writeToday2File();
		System.out.println(new Date() + "----MR ：0 58 23 * * ? end");
	}
	
	/**
	 * 每天 22 点 58 分 执行定时任务 
	 * 写入yarn作业
	 * @throws Exception
	 */
	@Scheduled(cron="0 58 22 * * ? ")
	public void executeTask() throws Exception {
		
		System.out.println(new Date() + "----yarn task ：0 58 22 * * ?  begin");
		
		TaskOperator.searchJobs(getAppSetting("job_url"), getAppSetting("project_url"));
		
		System.out.println(new Date() + "----yarn task ：0 58 22 * * ?  end");
	}
	
	
	public String getAppSetting(String key) {
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
