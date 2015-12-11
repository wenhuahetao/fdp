package com.fline.hadoop.data.common.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgressSource {

	private List<ProgressListener> listeners = null;
	private double percent = 0d;
	private double changeGap = 0.05d;
	private double lastpercent = 0d;
	private String stat = null;
	private ExecutorService es = Executors.newFixedThreadPool(10);

	public ProgressSource() {
		this.stat = "inited progress source";
		this.listeners = new ArrayList<ProgressListener>();
	}

	public void addProgressListener(ProgressListener listener) {
		this.listeners.add(listener);
	}

	public void changeProgressPercent(double percent) {
		if (percent - lastpercent < changeGap) {

		} else {
			this.percent = percent;
			this.percent = this.percent > 1.0d ? 1.0d : this.percent;
			final ProgressSource eventsource = this;
			for (final ProgressListener pl : listeners) {
				// new Thread
				Runnable r = new Runnable() {
					public void run() {
						pl.handleEvent(new ProgressEvent(eventsource));
					}
				};
				es.submit(r);
			}
		}
	}

	public void addProgressPercent(double percent) {
		changeProgressPercent(this.percent + percent);
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public double getPercent() {
		return percent;
	}
}
