//package com.fline.hadoop.data.util.bigdata.mapreduce;
//
//import java.io.IOException;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.client.Put;
//import org.apache.hadoop.hbase.client.Result;
//import org.apache.hadoop.hbase.client.Scan;
//import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
//import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
//import org.apache.hadoop.hbase.mapreduce.TableMapper;
//import org.apache.hadoop.hbase.mapreduce.TableReducer;
//import org.apache.hadoop.hbase.util.Bytes;
//import org.apache.hadoop.io.LongWritable;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapreduce.Job;
//
//import com.fline.hadoop.data.core.StaticConfiguration;
//
//public class TransFirstEnter {
//
//	private static final String sourceTable = "BDHP_FIRSTENTERRECORD";
//	private static final String targetTable = "BDHP_FIRSTENTERRECORD_CAR2TIME";
//
//	public static final byte[] columnSet_firstEnter = Bytes.toBytes("fed"); // analysis
//	// result
//	// for
//	// first
//	// enter
//	public static final byte[] columnSet_lastEnter = Bytes.toBytes("mcd"); // analysis
//	// condition
//	// for
//	// multi-carid
//	// analysis
//	public static final byte[] column_ID_firstEnter = Bytes.toBytes("id");
//	public static final byte[] column_lt_lastEnter = Bytes.toBytes("lt");
//	public static final byte[] column_ch_lastEnter = Bytes.toBytes("ch");
//	public static final byte[] columnSet_multiCar = Bytes.toBytes("info"); // multi-car
//	// 's
//	// column
//	// set
//	public static final byte[] column_Content_multiCar = Bytes.toBytes("c");
//
//	/**
//	 * @param args
//	 * @throws IOException
//	 * @throws ClassNotFoundException
//	 * @throws InterruptedException
//	 */
//	public static void main(String[] args) throws IOException,
//			InterruptedException, ClassNotFoundException {
//		// TODO Auto-generated method stub
//		System.setProperty("hadoop.home.dir", StaticConfiguration.HADOOP_HOME);
//		Configuration conf = new Configuration();
//		conf.addResource(TransFirstEnter.class.getClassLoader()
//				.getResourceAsStream("./core-site.xml"));
//		conf.addResource(TransFirstEnter.class.getClassLoader()
//				.getResourceAsStream("./hbase-site.xml"));
//		conf.addResource(TransFirstEnter.class.getClassLoader()
//				.getResourceAsStream("./hdfs-site.xml"));
//		Job job = new Job(conf, "TransFirstEnter");
//		job.setJarByClass(TransFirstEnter.class);
//		Scan scan = new Scan();
//		scan.setCaching(1024);
//		scan.setCacheBlocks(false);
//		TableMapReduceUtil.initTableMapperJob(sourceTable, scan, Mapper1.class,
//				Text.class, LongWritable.class, job);
//		TableMapReduceUtil
//				.initTableReducerJob(targetTable, Reducer1.class, job);
//		boolean b = job.waitForCompletion(true);
//		if (!b) {
//			throw new IOException("error");
//		}
//	}
//
//	public static class Mapper1 extends TableMapper<Text, LongWritable> {
//		private Text outkey = new Text();
//		private LongWritable outvalue = new LongWritable();
//
//		public void map(ImmutableBytesWritable row, Result value,
//				Context context) throws IOException, InterruptedException {
//			byte[] rowbytes = row.get();
//			String rowkey = Bytes.toString(rowbytes);
//			byte[] idbytes = value.getValue(columnSet_firstEnter,
//					column_ID_firstEnter);
//			try {
//				long time = Long.valueOf(rowkey.split("_")[0]);
//				String carid = Bytes.toString(idbytes);
//				outkey.set(carid);
//				outvalue.set(time);
//				context.write(outkey, outvalue);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public static class Reducer1 extends
//			TableReducer<Text, LongWritable, ImmutableBytesWritable> {
//		byte[] family = Bytes.toBytes("bs");
//		byte[] colname = Bytes.toBytes("tm");
//
//		public void reduce(Text key, Iterable<LongWritable> values,
//				Context context) throws IOException, InterruptedException {
//			long minTime = Long.MAX_VALUE;
//			for (LongWritable val : values) {
//				if (val.get() < minTime) {
//					minTime = val.get();
//				}
//			}
//			Put put = new Put(Bytes.toBytes(key.toString()));
//			put.add(family, colname, Bytes.toBytes(minTime));
//			context.write(null, put);
//		}
//	}
//}
