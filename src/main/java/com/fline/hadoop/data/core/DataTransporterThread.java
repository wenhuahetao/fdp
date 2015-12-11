package com.fline.hadoop.data.core;

import org.apache.sqoop.model.MSubmission;

import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.fline.hadoop.data.util.bigdata.JobInfo_Query;

public class DataTransporterThread implements Runnable {
	private String externaljobid;
	private ProgressSource source;

	public DataTransporterThread(MSubmission submission,
			DataProgressListener listener) {
		this.source = new ProgressSource();
		this.source.addProgressListener(listener);
		this.externaljobid = submission.getExternalJobId();
	}

	public DataTransporterThread(String applicationid,
			DataProgressListener listener) {
		this.source = new ProgressSource();
		this.source.addProgressListener(listener);
		this.externaljobid = applicationid;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				String stateinfo = JobInfo_Query.getDefaultInstance()
						.queryState(externaljobid);
				String[] splits = stateinfo.split(",");
				String state = splits[0];
				String finalstate = splits[1];
				float progress = Float.valueOf(splits[2]);
				if (state == null) {
					break;
				} else if (state.equalsIgnoreCase("FINISHED")
						|| finalstate.equalsIgnoreCase("KILLED")) {
					source.setStat(finalstate);
					source.changeProgressPercent(progress);
					break;
				} else {
					source.setStat(finalstate);
					source.changeProgressPercent(progress);
				}
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		DataProgressListener listener = new DataProgressListener() {
			@Override
			public void handleEvent(ProgressEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() instanceof ProgressSource) {
					System.out.println(((ProgressSource) e.getSource())
							.getStat());
				}
			}
		};
		DataTransporterThread thread = new DataTransporterThread(
				"application_1439181059765_0106", listener);
		new Thread(thread).start();
		JobInfo_Query.getDefaultInstance().close();

	}
}
