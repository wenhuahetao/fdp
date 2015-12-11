package com.fline.hadoop.data.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataManagerPool {
	private static ExecutorService pool = Executors.newCachedThreadPool();

	public static void addDataTransporterUnit(Runnable r) {
		pool.submit(r);
	}
}
