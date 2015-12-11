package com.fline.hadoop.data.util.bigdata.mapreduce;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;

/**
 * PlateAnalysis Input args[0] = outputTableName args[1] = starttime args[2] =
 * endtime
 * 
 * @author zhongliang
 * 
 */
public class PlateAnalyseCombineMpr {
	public static void main(String[] args) throws Exception {
		long endtime = System.currentTimeMillis() / 60 / 60 / 1000 * 60 * 60
				* 1000;
		if (args.length >= 1) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH");
			endtime = sdf.parse(args[0]).getTime() / 60 / 60 / 1000 * 60 * 60
					* 1000;
		}
		long starttime = endtime - 60 * 60 * 1000;

		String outputTableName = "BDHP_MULTIANALYSIS_TEMP_"
				+ (endtime / 60 / 60 / 1000);
		// long starttime = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
		// long endtime = System.currentTimeMillis();
		System.out.println(PlateAnalyseCombineMpr.class.getClassLoader()
				.getResource("") + "/core-site.xml");
		System.out.println(PlateAnalyseCombineMpr.class.getClassLoader()
				.getResource("./"));
		Configuration conf = new Configuration();
		// conf.addResource(PlateAnalyseCombineMpr.class.getClassLoader()
		// .getResourceAsStream("./core-site.xml"));
		conf.addResource(PlateAnalyseCombineMpr.class.getClassLoader()
				.getResourceAsStream("./hbase-site.xml"));
		// conf.addResource(PlateAnalyseCombineMpr.class.getClassLoader()
		// .getResourceAsStream("./hdfs-site.xml"));
		// conf.addResource(PlateAnalyseCombineMpr.class.getClassLoader()
		// .getResourceAsStream("./yarn-site.xml"));
		// conf.addResource(PlateAnalyseCombineMpr.class.getClassLoader()
		// .getResourceAsStream("./mapred-site.xml"));
		conf.set("bdhp_plateAnalyse_color_enable", "true");
		conf.set("bdhp_plateAnalyse_type_enable", "true");
		conf.set("bdhp_plateAnalyse_bayonet_enable", "true");
		Class.forName(Collect_Mapper.class.getName());
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "bdhp_plateAnalyse_" + outputTableName);
		job.setNumReduceTasks(20);
		job.setJarByClass(PlateAnalyseCombineMpr.class);
		Scan scan = new Scan();
		scan.setCaching(1024);

		System.currentTimeMillis();

		scan.setStartRow(Bytes.toBytes(starttime));
		scan.setStopRow(Bytes.toBytes(endtime));
		scan.setCacheBlocks(false);
		TableMapReduceUtil.initTableMapperJob("BDHP_BASEPICRECORD", scan,
				Collect_Mapper.class, Text.class, Text.class, job);
		HBaseAdmin hbaseadmin = new HBaseAdmin(conf);

		if (hbaseadmin.tableExists(Bytes.toBytes(outputTableName)) == false) {
			HColumnDescriptor cdes = new HColumnDescriptor(Bytes.toBytes("f"));
			HTableDescriptor tabledes = new HTableDescriptor(
					TableName.valueOf(Bytes.toBytes(outputTableName)));
			tabledes.addFamily(cdes);
			hbaseadmin.createTable(tabledes);
		}
		hbaseadmin.close();
		TableMapReduceUtil.initTableReducerJob(outputTableName,
				Count_Reducer.class, job);

		boolean b = job.waitForCompletion(true);
		if (!b) {
			throw new IOException("error");
		}
	}

	public static class Collect_Mapper extends TableMapper<Text, Text> {

		private Text key = new Text();
		private Text val = new Text();
		private String delemiter = " @_@ ";

		private static byte[] normalFamily = Bytes.toBytes("n");
		private static byte[] carinfoFamily = Bytes.toBytes("ci");
		private static byte[] vt = Bytes.toBytes("vt");
		private static byte[] vc = Bytes.toBytes("vc");
		private static byte[] pn = Bytes.toBytes("pn");
		private static byte[] bi = Bytes.toBytes("bi");
		private static byte[] pkd = Bytes.toBytes("pkd");

		@Override
		public void map(ImmutableBytesWritable rowkey, Result value,
				Context context) throws IOException, InterruptedException {
			String vehicletype = Bytes.toString(value
					.getValue(normalFamily, vt));
			String vehiclecolor = Bytes.toString(value.getValue(carinfoFamily,
					vc));
			@SuppressWarnings("unused")
			String bayonetid = Bytes.toString(value.getValue(normalFamily, bi));
			Long pastkkdate_str = Bytes.toLong(value
					.getValue(normalFamily, pkd));
			String plateno = Bytes.toString(value.getValue(normalFamily, pn));
			if (vehiclecolor == null) {
				vehiclecolor = "";
			}
			if (pastkkdate_str == null) {
				pastkkdate_str = 0L;
			}
			if (plateno == null) {
				plateno = "";
			}
			if ((!plateno.equals("0")) && (!plateno.equals(""))) {
				key.set(plateno.trim());
				val.set(vehiclecolor + delemiter + vehicletype + delemiter
						+ pastkkdate_str);
				context.write(key, val);
			}
		}
	}

	public static class Count_Reducer extends
			TableReducer<Text, Text, ImmutableBytesWritable> {
		private HashSet<String> colorSet = new HashSet<String>();
		private HashSet<String> typeSet = new HashSet<String>();

		private HashSet<Long> passTimeSet = new HashSet<Long>();

		private int type_count = 0;
		private int color_count = 0;
		private String family = "f";
		private String delemiter = " @_@ ";
		private static boolean color_enable = false;
		@SuppressWarnings("unused")
		private static boolean bayonet_enable = false;
		private static boolean type_enable = false;

		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {
			color_enable = Boolean.parseBoolean(context.getConfiguration().get(
					"bdhp_plateAnalyse_color_enable"));
			bayonet_enable = Boolean.parseBoolean(context.getConfiguration()
					.get("bdhp_plateAnalyse_bayonet_enable"));
			type_enable = Boolean.parseBoolean(context.getConfiguration().get(
					"bdhp_plateAnalyse_type_enable"));
		}

		@Override
		protected void reduce(Text plateNo, Iterable<Text> texts,
				Context context) throws IOException, InterruptedException {
			colorSet.clear();
			typeSet.clear();
			passTimeSet.clear();
			for (Text value : texts) {
				String valuestr = value.toString();
				String[] splits = valuestr.split(delemiter);
				if (splits.length >= 2) {
					// if (!StringUtils.isEmpty(splits[0]) &&
					// !splits[0].equals("null")) {
					colorSet.add(splits[0]);
					// }
					// if (!StringUtils.isEmpty(splits[1]) &&
					// !splits[1].equals("null")) {
					typeSet.add(splits[1]);
					passTimeSet.add(Long.parseLong(splits[2]));
					// }
				} else {
					throw new IOException("Length < 2");
				}
			}
			color_count = colorSet.size();
			type_count = typeSet.size();
			if (type_count > 1 || color_count > 1) {
				String plate_no = plateNo.toString().trim();
				Put put = new Put(Bytes.toBytes(plate_no));
				String colorrks = "";
				String typerks = "";
				String times = "";
				if (color_enable && color_count > 1) {
					put.add(family.getBytes(), "a".getBytes(),
							Bytes.toBytes(color_count));
					for (String color : colorSet) {
						colorrks += color + delemiter;
					}
					put.add(family.getBytes(), "d".getBytes(),
							Bytes.toBytes(colorrks));
				}
				if (type_enable && type_count > 1) {
					put.add(family.getBytes(), "b".getBytes(),
							Bytes.toBytes(type_count));
					for (String type : typeSet) {
						typerks += type + delemiter;
					}
					put.add(family.getBytes(), "e".getBytes(),
							Bytes.toBytes(typerks));
				}

				if (passTimeSet.size() > 0) {
					for (Long time : passTimeSet) {
						times += time + delemiter;
					}
					put.add(family.getBytes(), "k".getBytes(),
							Bytes.toBytes(times));
				}

				if ((color_enable && color_count > 1)
						|| (type_enable && type_count > 1)) {
					context.write(null, put);
				}
			}
		}
	}

	public static class Saver {
		private long date;
		private String rk;

		public long getDate() {
			return date;
		}

		public void setDate(long date) {
			this.date = date;
		}

		public String getRk() {
			return rk;
		}

		public void setRk(String rk) {
			this.rk = rk;
		}

		public Saver(long date, String rk) {
			super();
			this.date = date;
			this.rk = rk;
		}

		public Saver() {
		}
	}
}
