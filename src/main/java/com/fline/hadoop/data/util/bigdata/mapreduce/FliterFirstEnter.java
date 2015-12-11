package com.fline.hadoop.data.util.bigdata.mapreduce;

import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.mapreduce.Job;

public class FliterFirstEnter {

	private static final String sourceTable = "BDHP_FIRSTENTERRECORD";
	private static final String targetTable = "BDHP_ERROR_FIRSTENTERRECORD";

	public static final byte[] columnSet_firstEnter = Bytes.toBytes("fed"); // analysis
	// result
	// for
	// first
	// enter
	public static final byte[] columnSet_lastEnter = Bytes.toBytes("mcd"); // analysis
	// condition
	// for
	// multi-carid
	// analysis
	public static final byte[] column_ID_firstEnter = Bytes.toBytes("id");
	public static final byte[] column_lt_lastEnter = Bytes.toBytes("lt");
	public static final byte[] column_ch_lastEnter = Bytes.toBytes("ch");
	public static final byte[] columnSet_multiCar = Bytes.toBytes("info"); // multi-car
	// 's
	// column
	// set
	public static final byte[] column_Content_multiCar = Bytes.toBytes("c");

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		conf.addResource(FliterFirstEnter.class.getClassLoader()
				.getResourceAsStream("./core-site.xml"));
		conf.addResource(FliterFirstEnter.class.getClassLoader()
				.getResourceAsStream("./hbase-site.xml"));
		conf.addResource(FliterFirstEnter.class.getClassLoader()
				.getResourceAsStream("./hdfs-site.xml"));
		Job job = new Job(conf, "FliterFirstEnter");
		job.setJarByClass(FliterFirstEnter.class);
		Scan scan = new Scan();
		scan.setCaching(1024);
		scan.setCacheBlocks(false);
		TableMapReduceUtil.initTableMapperJob(sourceTable, scan, Mapper1.class,
				BytesWritable.class, BytesWritable.class, job);
		TableMapReduceUtil
				.initTableReducerJob(targetTable, Reducer1.class, job);
		boolean b = job.waitForCompletion(true);
		if (!b) {
			throw new IOException("error");
		}
	}

	public static class Mapper1 extends
			TableMapper<BytesWritable, BytesWritable> {

		public void map(ImmutableBytesWritable row, Result value,
				Context context) throws IOException, InterruptedException {
			// byte[] idbytes = value.getValue(columnSet_firstEnter,
			// column_ID_firstEnter);
			String rowkeystr = Bytes.toString(value.getRow());
			byte[] idbytes = rowkeystr.split("_")[1].getBytes();
			context.write(new BytesWritable(idbytes),
					new BytesWritable(value.getRow()));
		}
	}

	public static class Reducer1 extends
			TableReducer<BytesWritable, BytesWritable, ImmutableBytesWritable> {

		private HTable deleteHTable = null;

		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {
			deleteHTable = new HTable(context.getConfiguration(),
					Bytes.toBytes("BDHP_FIRSTENTERRECORD"));
		}

		public void reduce(BytesWritable key, Iterable<BytesWritable> values,
				Context context) throws IOException, InterruptedException {
			byte[] caridbytes = key.copyBytes();
			// if ((caridbytes.length > 4 && caridbytes[0] == (byte) 0xE6
			// && caridbytes[1] == (byte) 0xB5
			// && caridbytes[2] == (byte) 0x99 && caridbytes[3] == (byte) 0x44)
			// || (caridbytes.length < 8 && (caridbytes[0] & 0x80) > 0)
			// || (caridbytes.length < 5)) {
			if (caridbytes.length < 8) {
				for (BytesWritable val : values) {
					long time = Long.valueOf(new String(val.getBytes())
							.split("_")[0]);
					byte[] keybytes = Bytes.add(key.copyBytes(),
							Bytes.toBytes(time));
					Put put = new Put(keybytes);
					put.add(Bytes.toBytes("info"), Bytes.toBytes("c"),
							val.copyBytes());
					context.write(null, put);
					deleteHTable.delete(new Delete(val.copyBytes()));
				}
			} else {
				long minTime = Long.MAX_VALUE;
				HashSet<Long> deleteTimes = new HashSet<Long>();
				for (BytesWritable val : values) {
					long time = Long.valueOf(new String(val.getBytes())
							.split("_")[0]);
					deleteTimes.add(time);
					if (time < minTime) {
						minTime = time;
					}
				}
				deleteTimes.remove(minTime);
				for (Long time : deleteTimes) {
					byte[] keybytes = Bytes.add(key.copyBytes(),
							Bytes.toBytes(time));
					Put put = new Put(keybytes);
					byte[] rowkey = Bytes.add(Bytes.toBytes(time + "_"),
							key.copyBytes());
					put.add(Bytes.toBytes("info"), Bytes.toBytes("c"), rowkey);
					context.write(null, put);
					deleteHTable.delete(new Delete(rowkey));
				}
			}
		}

		@Override
		public void cleanup(Context context) throws IOException,
				InterruptedException {
			deleteHTable.close();
		}
	}
}
