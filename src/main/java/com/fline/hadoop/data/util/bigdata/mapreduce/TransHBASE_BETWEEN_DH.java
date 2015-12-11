package com.fline.hadoop.data.util.bigdata.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
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

/**
 * Trans hbase data between different hbase cluster.
 * 
 * @author zhongliang
 * 
 */
public class TransHBASE_BETWEEN_DH {
	public TransHBASE_BETWEEN_DH(String outputHBASE_ZOOKEEPERLIST,
			String outputHBASE_ZNODE, String tablename) {

	}

	public static void main(String[] args) throws Exception {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		Configuration conf = new Configuration();
		conf.addResource(FliterFirstEnter.class.getClassLoader()
				.getResourceAsStream("./core-site.xml"));
		conf.addResource(FliterFirstEnter.class.getClassLoader()
				.getResourceAsStream("./hbase-site.xml"));
		conf.addResource(FliterFirstEnter.class.getClassLoader()
				.getResourceAsStream("./hdfs-site.xml"));
		conf.addResource(FliterFirstEnter.class.getClassLoader()
				.getResourceAsStream("./yarn-site.xml"));

		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "TransHBASE_BETWEEN_DH - lvbb_latest_location");
		job.setJarByClass(TransHBASE_BETWEEN_DH.class);
		Scan scan = new Scan();
		scan.setCaching(1024);
		scan.setCacheBlocks(false);
		TableMapReduceUtil.initTableMapperJob("lvbb_latest_location", scan,
				Mapper1.class, BytesWritable.class, BytesWritable.class, job);
		TableMapReduceUtil.initTableReducerJob(
				"lvbb_latest_location_TestOutput", Reducer1.class, job);
		boolean b = job.waitForCompletion(true);
		if (!b) {
			throw new IOException("error");
		}
	}

	public static class Mapper1 extends
			TableMapper<BytesWritable, BytesWritable> {

		public void map(ImmutableBytesWritable row, Result value,
				Context context) throws IOException, InterruptedException {
			BytesWritable outkey = new BytesWritable(value.getRow());
			BytesWritable outvalue = new BytesWritable(value.getValue(
					Bytes.toBytes("bs"), Bytes.toBytes("d")));
			context.write(outkey, outvalue);
		}
	}

	public static class Reducer1 extends
			TableReducer<BytesWritable, BytesWritable, ImmutableBytesWritable> {

		public void reduce(BytesWritable key, Iterable<BytesWritable> values,
				Context context) throws IOException, InterruptedException {
			byte[] keybytes = key.copyBytes();
			for (BytesWritable value : values) {
				Put put = new Put(keybytes);
				put.add(Bytes.toBytes("bs"), Bytes.toBytes("d"),
						value.copyBytes());
				context.write(null, put);
			}
		}
	}
}
