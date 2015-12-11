package com.fline.hadoop.data.util.bigdata;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.log4j.Logger;

/**
 * This class is ongly used for handling bdhp data. This class's design must be
 * considerd if serializable.. for spark streaming... by zhongliang.zhu at
 * 2015.06.05
 * 
 * code update: 2015.08.26, update `addMultiCarRecord` and `addFirstEnterRecord`
 * , add new function named as flushPuts. In this way, program will call
 * addMultiCarRecord or addFirstEnterRecord to add data to this object, and call
 * flushPuts to send data to hbase. This update will fix the region too busy
 * problem.
 */
public class Hbase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient Configuration conf = null;
	private transient HTable table = null;
	private transient List<Put> puts = null;

	private String tableName = null;
	private ArrayList<String> confkeys = null;
	private ArrayList<String> confvalues = null;

	private static Logger logger = Logger.getLogger(Hbase.class); // log4j

	public Hbase(Configuration conf, String tableName) {
		this.tableName = tableName;
		Iterator<Entry<String, String>> iter = conf.iterator();
		confkeys = new ArrayList<String>();
		confvalues = new ArrayList<String>();
		while (iter.hasNext()) {
			Entry<String, String> next = iter.next();
			confkeys.add(next.getKey());
			confvalues.add(next.getValue());
		}
	}

	public void constructConf() throws IOException {
		if (conf == null) {
			conf = new Configuration();
			int minLen = Math.min(confkeys.size(), confvalues.size());
			for (int i = 0; i < minLen; i++) {
				conf.set(confkeys.get(i), confvalues.get(i));
			}
		}

		if (table == null) {
			logger.warn("begin to init htable , tablename = " + tableName);
			this.table = new HTable(conf, Bytes.toBytes(tableName));
			// create list<put> at the same time of creating table.
			puts = new ArrayList<Put>();
		}
	}

	public void flushPuts() throws Exception {
		if (puts.size() > 0) {
			table.put(puts);
			table.flushCommits();
			puts.clear();
		}
	}

	public void addFirstEnterRecord(String rowkey, String carid, long time,
			String channelid) throws Exception {
		logger.info("begin to addFirstEnterRecord ... carid = " + carid);
		constructConf();
		Put put = new Put(Bytes.toBytes(rowkey));
		put.add(UserHTableDescription.columnSet_firstEnter,
				UserHTableDescription.column_ID_firstEnter,
				Bytes.toBytes(carid));
		put.add(UserHTableDescription.columnSet_lastEnter,
				UserHTableDescription.column_lt_lastEnter, Bytes.toBytes(time));
		put.add(UserHTableDescription.columnSet_lastEnter,
				UserHTableDescription.column_ch_lastEnter,
				Bytes.toBytes(channelid));
		table.put(put);
		// puts.add(put);
	}

	public void deleteFirstEnterRecord(String rowkey) throws Exception {
		constructConf();
		List<Delete> list = new ArrayList<Delete>();
		Delete del = new Delete(Bytes.toBytes(rowkey));
		list.add(del);
		table.delete(list);
	}

	public void deleteFirstEnterRecords(List<String> rowkeys) throws Exception {
		constructConf();
		List<Delete> list = new ArrayList<Delete>();
		for (String rowkey : rowkeys) {
			Delete del = new Delete(Bytes.toBytes(rowkey));
			list.add(del);
		}
		table.delete(list);
	}

	/**
	 * rowkey is designed by other program. update time + carid 's currenttime &
	 * channelid
	 */
	public void updateLastEnterRecord(String rowkey, long currenttime,
			String channelid) throws Exception {
		constructConf();
		Put put = new Put(Bytes.toBytes(rowkey));
		put.add(UserHTableDescription.columnSet_lastEnter,
				UserHTableDescription.column_lt_lastEnter,
				Bytes.toBytes(currenttime));
		put.add(UserHTableDescription.columnSet_lastEnter,
				UserHTableDescription.column_ch_lastEnter,
				Bytes.toBytes(channelid));
		table.put(put);
	}

	/**
	 * start_rowkey & end_rowkey = time .strcat ( carinfo )
	 */
	public ArrayList<String> searchFirstEnterRecord(String start_rowkey,
			String end_rowkey) throws Exception {
		// prepare condition & scan.
		constructConf();
		ArrayList<String> ret = new ArrayList<String>();
		Scan scan = new Scan();
		scan.setStartRow(Bytes.toBytes(start_rowkey));
		scan.setStopRow(Bytes.toBytes(end_rowkey));

		// handle result.
		ResultScanner rs = table.getScanner(scan);
		for (Result r : rs) {
			byte[] idbytes = r.getValue(
					UserHTableDescription.columnSet_firstEnter,
					UserHTableDescription.column_ID_firstEnter);
			ret.add(Bytes.toString(idbytes));
		}
		return ret;
	}

	public HashMap<String, Long> loadHistoryFirstEnterRecord() throws Exception {
		// scan first enter record table...
		constructConf();
		Scan scan = new Scan();
		ResultScanner rs = table.getScanner(scan);

		HashMap<String, Long> retmap = new HashMap<String, Long>();
		List<String> multi_rowkeys = new ArrayList<String>();

		for (Result r : rs) {
			byte[] idbytes = r.getValue(
					UserHTableDescription.columnSet_firstEnter,
					UserHTableDescription.column_ID_firstEnter);
			byte[] row = r.getRow();
			String rowkey = Bytes.toString(row);
			try {
				long time = Long.valueOf(rowkey.split("_")[0]);
				String carid = Bytes.toString(idbytes);
				if (retmap.get(carid) == null) {
					retmap.put(carid, time);
				} else {
					if (time - retmap.get(carid) < 0) {
						// to delete data in hbase. because the record is error
						// , it is not the first enter record.
						multi_rowkeys.add(rowkey);
					}
				}
			} catch (Exception e) {
				logger.error(e + "\trowkey = " + rowkey);
				if (rowkey != null) {
					multi_rowkeys.add(rowkey);
				}
				continue;
			}
		}
		if (multi_rowkeys.size() > 0) {
			logger.info("Hbase find multi record in first enter records.... begin to clear , please wait... multi_records.size = "
					+ multi_rowkeys.size());
			this.deleteFirstEnterRecords(multi_rowkeys);
		} else {
			logger.info("no multi_rowkeys found in first enter records...");
		}
		return retmap;
	}

	public void addMultiCarRecord(String infocontent, long time, String carid)
			throws Exception {
		constructConf();
		byte[] timekey = Bytes.toBytes(time);
		byte[] caridkey = Bytes.toBytes(carid);
		byte[] rowkey = new byte[timekey.length + caridkey.length];
		System.arraycopy(timekey, 0, rowkey, 0, timekey.length);
		System.arraycopy(caridkey, 0, rowkey, timekey.length, caridkey.length);
		Put put = new Put(rowkey);
		put.add(UserHTableDescription.columnSet_multiCar,
				UserHTableDescription.column_Content_multiCar,
				Bytes.toBytes(infocontent));
		table.put(put);
		// puts.add(put);
	}

	public Long queryLastEnterTime(String carid) throws Exception {
		constructConf();
//		ResultScanner rs = table.getScanner(new Scan());
//		for (Result r : rs) {
//			byte[] rowkey = r.getRow();
//			if (rowkey.length > 8) {
//				System.out.println(rowkey.length);
//			}
//		}
		byte[] caridkey = Bytes.toBytes(carid);
		Get get = new Get(caridkey);
		Result r = table.get(get);
		byte[] value = r.getValue(Bytes.toBytes("bs"), Bytes.toBytes("tm"));
		if (value == null) {
			return null;
		} else {
			return Bytes.toLong(value);
		}
	}

	public void writeMultiCarTrainResult(HashMap<String, Integer> road2time)
			throws Exception {
		constructConf();
		Set<String> keys = road2time.keySet();
		List<Put> puts = new ArrayList<Put>();
		for (String key : keys) {
			Put put = new Put(Bytes.toBytes(key));
			put.add(Bytes.toBytes("c"), Bytes.toBytes("tm"),
					Bytes.toBytes(road2time.get(key)));
			puts.add(put);
		}
		table.put(puts);
	}

	public HashMap<String, Integer> loadMultiCarTrainResult() throws Exception {
		constructConf();
		Scan scan = new Scan();
		ResultScanner rs = table.getScanner(scan);

		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		for (Result r : rs) {
			byte[] rowbytes = r.getRow();
			byte[] timebytes = r.getValue(Bytes.toBytes("c"),
					Bytes.toBytes("tm"));
			ret.put(Bytes.toString(rowbytes), Bytes.toInt(timebytes));
			System.out.println(Bytes.toString(rowbytes) + ","
					+ Bytes.toInt(timebytes));
		}
		return ret;
	}

	public Pair<byte[][], byte[][]> getStartAndEndRowKeys() throws IOException {
		constructConf();
		return table.getStartEndKeys();
	}

	public ResultScanner getResultScann(byte[] start_rowkey, byte[] stop_rowkey) {
		Scan scan = new Scan();
		ResultScanner rs = null;
		try {
			scan.setStartRow(start_rowkey);
			scan.setStopRow(stop_rowkey);
			rs = table.getScanner(scan);
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Hbase getDefaultSXJT_FirstEnter_Instance() {
		String tableName = "BDHP_FIRSTENTERRECORD";
		Configuration conf = new Configuration();
		conf.addResource("/home/hadoop/hbase-0.98.7-hadoop2/conf/core-site.xml");
		conf.addResource("/home/hadoop/hbase-0.98.7-hadoop2/conf/hbase-site.xml");
		conf.set("hbase.zookeeper.quorum",
				"hadoop.hm:2181,hadoop.hm.bak1:2181,hadoop.slave.s6:2181");
		conf.set("zookeeper.znode.parent", "/hbase");
		Hbase hbase = new Hbase(conf, tableName);
		return hbase;
	}

	public static void main(String[] args) throws Exception {
		// String tableName = "BDHP_FIRSTENTERRECORD";
		// String tableName = "BDHP_MULTICARTRAIN";
		String tableName = "BDHP_FIRSTENTERRECORD_CAR2TIME";
		Configuration conf = new Configuration();
		// conf.addResource("/home/hadoop/hbase-0.98.7-hadoop2/conf/core-site.xml");
		// conf.addResource("/home/hadoop/hbase-0.98.7-hadoop2/conf/hbase-site.xml");
		conf.set("hbase.zookeeper.quorum",
				"hadoop.hm:2181,hadoop.hm.bak1:2181,hadoop.slave.s6:2181");
		Hbase hbase = new Hbase(conf, tableName);
		Long time = hbase.queryLastEnterTime("浙Afffff");
		System.out.println(time);
		// hbase.loadMultiCarTrainResult();
		// HashMap<String, Long> history = hbase.aloadHistoryFirstEnterRecord();
		// System.out.println(history);
		// hbase.deleteFirstEnterRecord("0_caridTest");
		/**PlateAnalyseMprService.javac
		 * Pair<byte[][], byte[][]> keypairs = hbase.getStartAndEndRowKeys();
		 * byte[][] first = keypairs.getFirst(); byte[][] second =
		 * keypairs.getSecond();
		 * 
		 * for (int i = 0 ; i < first.length ;i++) {
		 * System.out.println(Bytes.toStringBinary(first[i]) + "\t" +
		 * Bytes.toStringBinary(second[i])); } StringKafkaProducer producer =
		 * new StringKafkaProducer(
		 * "hadoop.slave.h6:6667,hadoop.slave.h7:6667,hadoop.slave.h8:6667",
		 * "test"); ResultScanner rs = hbase.getResultScann(first[0],
		 * second[0]); for (Result r : rs) { String carid = null; Long timestamp
		 * = null; byte[] row = r.getRow(); if (row.length < 20) continue ;
		 * carid = Bytes.toStringBinary(row, 4, 8); timestamp =
		 * Bytes.toLong(row, 12, 8); Cell channelCell =
		 * r.getColumnLatestCell(Bytes.toBytes("f"), Bytes.toBytes("b"));
		 * 
		 * if (carid != null && carid.length() != 0) {
		 * System.out.println("len - "+ row.length + "\tcarid = " + carid
		 * +"\ttimestamp = " + timestamp +"\t"+ Bytes.toStringBinary(row) + "\t"
		 * + Bytes.toString(channelCell.getValue())); producer.sendMessage(carid
		 * + "\t" + timestamp + "\t" + Bytes.toString(channelCell.getValue()));
		 * } // Thread.sleep(1000); } if (rs != null) rs.close();
		 * producer.close();
		 */

		/**
		 * byte[] normal_column = Bytes.toBytes("n"); byte[] pkd =
		 * Bytes.toBytes("pkd"); byte[] pn = Bytes.toBytes("pn"); byte[] bi =
		 * Bytes.toBytes("bi"); byte[] bn = Bytes.toBytes("bn"); byte[] speed =
		 * Bytes.toBytes("vsp"); if (args[0].equals("import_bytime")) { String
		 * starttime = args[1]; String endtime = args[2]; ResultScanner rs =
		 * hbase.getResultScann(Bytes.toBytes(starttime),
		 * Bytes.toBytes(endtime)); StringKafkaProducer producer = new
		 * StringKafkaProducer(
		 * "hadoop.slave.h6:6667,hadoop.slave.h7:6667,hadoop.slave.h8:6667",
		 * "test"); for (Result r : rs) { byte[] pkdvalue =
		 * r.getValue(normal_column, pkd); byte[] pnvalue =
		 * r.getValue(normal_column, pn); byte[] bivalue =
		 * r.getValue(normal_column, bi); byte[] bnvalue =
		 * r.getValue(normal_column, bn); byte[] speedvalue =
		 * r.getValue(normal_column, speed);
		 * producer.sendMessage(Bytes.toString(pnvalue) + " " +
		 * Bytes.toString(pkdvalue) + " " + Bytes.toString(bivalue) + " " +
		 * Bytes.toShort(bnvalue) + " " + Bytes.toShort(speedvalue));
		 * 
		 * } producer.close(); }
		 */
	}

}
