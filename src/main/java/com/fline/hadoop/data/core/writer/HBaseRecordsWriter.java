package com.fline.hadoop.data.core.writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource.HBaseOutputDataSource;

public class HBaseRecordsWriter extends AbstractRecordsWriter {

	HTable table = null;
	String rowkeyGenratedWay = null;
	String rowkeyParams = null;
	String[] items = null;
	HashMap<Integer, byte[][]> index2familyAndColumn = null;
	private static Logger LOG = Logger.getLogger(HBaseRecordsWriter.class);

	public HBaseRecordsWriter(OutputDataSource outputdatasource)
			throws Exception {
		super(outputdatasource);
		Map<String, String> config = outputdatasource.getDataSourceConfig();
		String tablename = config
				.get(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME);
		String columnsMap = config
				.get(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP);
		rowkeyGenratedWay = config
				.get(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY);
		rowkeyParams = config
				.get(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS);

		String zookeeperlist = config
				.get(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST);
		String zookeepernode = config.get(HBaseDataSource.CONFIG_HBASE_ZKNODE);

		items = columnsMap.split(",");
		index2familyAndColumn = new HashMap<Integer, byte[][]>();
		for (int i = 0; i < items.length; i++) {
			String[] splits = items[i].split(":");
			if (splits.length != 2) {
				throw new Exception(
						"HBASE LOADER's columnsMap error. columnsMap=family1:column1,...");
			}
			index2familyAndColumn.put(
					i,
					new byte[][] { Bytes.toBytes(splits[0]),
							Bytes.toBytes(splits[1]) });
		}

		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum", zookeeperlist);
		conf.set("zookeeper.znode.parent", zookeepernode);
		table = new HTable(conf, Bytes.toBytes(tablename));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void writeRecords(List<String[]> records) {
		// TODO Auto-generated method stub
		List<Put> puts = new ArrayList<Put>();
		for (String[] record : records) {
			if (items.length != record.length) {
				LOG.warn("HbaseLoader load record.length = " + record.length
						+ ", but columnsMap.length = " + items.length
						+ ". quit record.");
				continue;
			} else {
				try {
					byte[] rowkey = generateRowKey(record, rowkeyGenratedWay,
							rowkeyParams);
					Put put = new Put(rowkey);
					Object[] formateddata = format(record);
					for (int i = 0; i < formateddata.length; i++) {
						if (formateddata[i] == null) {
							continue;
						}
						byte[][] familyAndColumn = index2familyAndColumn.get(i);
						if (formateddata[i] instanceof Integer) {
							put.add(familyAndColumn[0], familyAndColumn[1],
									Bytes.toBytes((Integer) formateddata[i]));
						} else if (formateddata[i] instanceof Double) {
							put.add(familyAndColumn[0], familyAndColumn[1],
									Bytes.toBytes((Double) formateddata[i]));
						} else if (formateddata[i] instanceof Long) {
							put.add(familyAndColumn[0], familyAndColumn[1],
									Bytes.toBytes((Long) formateddata[i]));
						} else {
							put.add(familyAndColumn[0], familyAndColumn[1],
									Bytes.toBytes(String
											.valueOf(formateddata[i])));
						}
					}
					puts.add(put);
				} catch (Exception e) {
					LOG.error("handle record error.", e);
				}
			}
		}
		try {
			table.put(puts);
			table.flushCommits();
		} catch (Exception e) {
			LOG.error(e);
		}
		LOG.debug("upload docs successfully");
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e);
		}
	}

	private byte[] generateRowKey(Object[] records, String generatedWay,
			String rowkeyParams) throws Exception {
		if (generatedWay.equals("normal")) {
			String[] splits = rowkeyParams.split(",");
			byte[][] rowkeyparts = new byte[splits.length][];
			int byteslen = 0;
			for (int i = 0; i < splits.length; i++) {
				Object value = records[Integer.valueOf(splits[i])];
				if (value instanceof Integer) {
					rowkeyparts[i] = Bytes.toBytes((Integer) value);
				} else if (value instanceof Long) {
					rowkeyparts[i] = Bytes.toBytes((Long) value);
				} else if (value instanceof byte[]) {
					rowkeyparts[i] = (byte[]) value;
				} else {
					rowkeyparts[i] = Bytes.toBytes(String.valueOf(value));
				}
				byteslen += rowkeyparts[i].length;
			}
			int index = 0;
			byte[] rowkey = new byte[byteslen];
			for (byte[] rowkeypart : rowkeyparts) {
				System.arraycopy(rowkeypart, 0, rowkey, index,
						rowkeypart.length);
				index += rowkeypart.length;
			}
			return rowkey;
		} else {
			throw new Exception("unsupported RowKeyGeneratedWay");
		}
	}

	@Override
	public void writeObjRecords(List<Object[]> records) {
		// TODO Auto-generated method stub
		List<Put> puts = new ArrayList<Put>();
		for (Object[] record : records) {
			if (items.length != record.length) {
				LOG.warn("HbaseLoader load record.length = " + record.length
						+ ", but columnsMap.length = " + items.length
						+ ". quit record.");
				continue;
			} else {
				try {
					byte[] rowkey = generateRowKey(record, rowkeyGenratedWay,
							rowkeyParams);
					Put put = new Put(rowkey);
					for (int i = 0; i < record.length; i++) {
						if (record[i] == null) {
							continue;
						}
						byte[][] familyAndColumn = index2familyAndColumn.get(i);
						if (record[i] instanceof Integer) {
							put.add(familyAndColumn[0], familyAndColumn[1],
									Bytes.toBytes((Integer) record[i]));
						} else if (record[i] instanceof Double) {
							put.add(familyAndColumn[0], familyAndColumn[1],
									Bytes.toBytes((Double) record[i]));
						} else if (record[i] instanceof Long) {
							put.add(familyAndColumn[0], familyAndColumn[1],
									Bytes.toBytes((Long) record[i]));
						} else if (record[i] instanceof byte[]) {
							put.add(familyAndColumn[0], familyAndColumn[1],
									(byte[]) record[i]);
						} else {
							put.add(familyAndColumn[0], familyAndColumn[1],
									Bytes.toBytes(String.valueOf(record[i])));
						}
					}
					puts.add(put);
				} catch (Exception e) {
					LOG.error("handle record error.", e);
				}
			}
		}
		try {
			table.put(puts);
			table.flushCommits();
		} catch (Exception e) {
			LOG.error(e);
		}
		LOG.debug("upload docs successfully");
	}
}
