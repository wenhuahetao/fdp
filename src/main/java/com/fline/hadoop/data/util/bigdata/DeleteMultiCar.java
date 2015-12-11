package com.fline.hadoop.data.util.bigdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class DeleteMultiCar {
	HTable table = null;

	public DeleteMultiCar(Configuration conf, String tablename) throws Exception {
		table = new HTable(conf, Bytes.toBytes(tablename));
	}

	public List<byte[]> getDeleteRowKeys() throws Exception {
		List<byte[]> rowkeys = new ArrayList<byte[]>();
		Scan scan = new Scan();
		scan.setStartRow(Bytes.toBytes(System.currentTimeMillis() - 10 * 24
				* 60 * 60 * 1000));
		scan.setStopRow(Bytes.toBytes(System.currentTimeMillis()));
		ResultScanner scanner = table.getScanner(scan);
		byte[] family = Bytes.toBytes("info");
		byte[] col = Bytes.toBytes("c");
		int total = 0;
		for (Result r : scanner) {
			total++;
			String content = Bytes.toString(r.getValue(family, col));
			String[] splits = content.split(",");
			double distance = Double.valueOf(splits[12]);
			double time = Double.valueOf(splits[13]);
			if (distance > 3000 && (time > 30 || time < -30)) {
				rowkeys.add(r.getRow());
			}
		}
		System.out
				.println("rowkeys = " + rowkeys.size() + "\ttotal : " + total);
		return rowkeys;
	}

	public void deleteRecords(List<byte[]> keys) throws Exception {
		List<Delete> deletes = new ArrayList<Delete>();
		for (byte[] key : keys) {
			Delete delete = new Delete(key);
			deletes.add(delete);
		}
		table.delete(deletes);
	}

	public void close() throws Exception {
		table.flushCommits();
		table.close();
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum",
				"hadoop.hm:2181,hadoop.hm.bak1:2181,hadoop.slave.s6:2181");
		DeleteMultiCar delete = new DeleteMultiCar(conf, "BDHP_MULTICARRECORD");
		delete.deleteRecords(delete.getDeleteRowKeys());
		delete.close();
	}
}
