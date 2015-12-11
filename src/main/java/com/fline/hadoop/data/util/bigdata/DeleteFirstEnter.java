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

public class DeleteFirstEnter {
	HTable table = null;
	Configuration conf = null;

	public DeleteFirstEnter(Configuration conf, String tablename)
			throws Exception {
		this.conf = conf;
		table = new HTable(conf, Bytes.toBytes(tablename));
	}

	public List<byte[]> getDeleteRowKeys() throws Exception {
		List<byte[]> rowkeys = new ArrayList<byte[]>();
		Scan scan = new Scan();
		ResultScanner scanner = table.getScanner(scan);
		byte[] family = Bytes.toBytes("info");
		byte[] col = Bytes.toBytes("c");
		int total = 0;
		for (Result r : scanner) {
			rowkeys.add(r.getValue(family, col));
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
		HTable basetable = new HTable(conf,
				Bytes.toBytes("BDHP_FIRSTENTERRECORD"));
		basetable.delete(deletes);
		basetable.close();
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
		DeleteFirstEnter delete = new DeleteFirstEnter(conf,
				"BDHP_ERROR_FIRSTENTERRECORD");
		delete.deleteRecords(delete.getDeleteRowKeys());
		delete.close();
	}
}
