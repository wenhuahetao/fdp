package com.fline.hadoop.data.util.bigdata;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource.HBaseOutputDataSource;
import com.fline.hadoop.data.util.DB_ORM;

public class HBaseOperator implements DB_ORM {
	private HBaseAdmin hbaseadmin = null;
	private HashMap<String, List<String[]>> table2description = null;
	private HashMap<String, HTable> tablename2tableisntance = new HashMap<String, HTable>();
	private static HBaseOperator instance = null;

	private static Logger logger = Logger.getLogger(HBaseOperator.class);

	public HBaseOperator() throws Exception {
		Configuration conf = new Configuration();
		conf.addResource(new FileInputStream(this.getClass().getClassLoader()
				.getResource("").getPath()
				+ "hbase-site.xml"));
		System.out.println(this.getClass().getClassLoader().getResource("").getPath());
		init(conf);
		table2description = new HashMap<String, List<String[]>>();
	}

	public void init(Configuration conf) throws Exception {
		hbaseadmin = new HBaseAdmin(conf);
	}

	public static HBaseOperator getInstance() {
		if (instance == null) {
			try {
				instance = new HBaseOperator();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}

	public String[] getTables() {
		table2description.clear();
		tablename2tableisntance.clear();
		try {
			HTableDescriptor[] descriptors = hbaseadmin.listTables();
			for (HTableDescriptor descriptor : descriptors) {
				String tablename = descriptor.getNameAsString();
				HColumnDescriptor[] columnDescriptors = descriptor
						.getColumnFamilies();
				List<String[]> columns = new ArrayList<String[]>();
				for (HColumnDescriptor columnDescriptor : columnDescriptors) {
					String colName = columnDescriptor.getNameAsString(); // family
					columns.add(new String[] { colName });
				}
				table2description.put(tablename, columns);
				tablename2tableisntance.put(
						tablename,
						new HTable(hbaseadmin.getConfiguration(), Bytes
								.toBytes(tablename)));
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return table2description.keySet().toArray(
				new String[table2description.keySet().size()]);
	}

	@Override
	public List<String[]> getFields(String tablename) {
		// TODO Auto-generated method stub
		if (this.table2description.get(tablename) == null) {
			getTables();
		}
		return this.table2description.get(tablename);
	}

	public void update(String tablename, String colnames, String line,
			byte[] rowkey) throws Exception {
		// update("BDHP_X", "col1:family1, col2:family2", "value1,value2")
		HTable instance = tablename2tableisntance.get(tablename);
		if (instance == null) {
			instance = new HTable(hbaseadmin.getConfiguration(),
					Bytes.toBytes(tablename));
			tablename2tableisntance.put(tablename, instance);
		}
		String[] colSplits = colnames.split(",");
		String[] linesplits = line.split(",", colSplits.length);
		Put put = new Put(rowkey);
		for (int i = 0; i < colSplits.length; i++) {
			String[] colAndFamily = colSplits[i].split(":");
			if (colAndFamily.length == 2 && linesplits[i].length() > 0) {
				put.add(Bytes.toBytes(colAndFamily[1]),
						Bytes.toBytes(colAndFamily[0]),
						Bytes.toBytes(linesplits[i]));
			}
		}
		instance.put(put);
	}

	public void delete(String tablename, byte[] rowkey) throws Exception {
		// delete("BDHP_X", rowkey)
		HTable instance = tablename2tableisntance.get(tablename);
		if (instance == null) {
			instance = new HTable(hbaseadmin.getConfiguration(),
					Bytes.toBytes(tablename));
			tablename2tableisntance.put(tablename, instance);
		}
		Delete delete = new Delete(rowkey);
		instance.delete(delete);
	}

	/**
	 * query data rowkeys from startRowkey to endRowkey
	 * 
	 * @param tablename
	 * @param startRowkey
	 * @param endRowkey
	 * @return
	 * @throws Exception
	 */
	public List<Object> searchData(String tablename, byte[] startRowkey,
			byte[] endRowkey) throws Exception {
		HTable instance = getTable(tablename);
		if (instance == null) {
			return null;
		} else {
			Scan scan = new Scan();
			scan.setStartRow(startRowkey);
			scan.setStopRow(endRowkey);
			ResultScanner rs = instance.getScanner(scan);
			List<Object> ret = new ArrayList<Object>();
			for (Result r : rs) {
				ret.add(r.getRow());
			}
			return ret;
		}
	}

	/**
	 * query data contents from startRowkey to endRowkey
	 * 
	 * @param tablename
	 * @param startRowkey
	 * @param endRowkey
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public List<Object[]> searchContents(String tablename, byte[] startRowkey,
			byte[] endRowkey) throws Exception {
		HTable instance = getTable(tablename);
		if (instance == null) {
			return null;
		} else {
			Scan scan = new Scan();
			scan.setStartRow(startRowkey);
			scan.setStopRow(endRowkey);
			ResultScanner rs = instance.getScanner(scan);
			List<Object[]> results = new ArrayList<Object[]>();
			for (Result r : rs) {
				List<Object> result = new ArrayList<Object>();
				result.add(r.getRow());
				for (Cell cell : r.listCells()) {
					String colname = Bytes.toString(cell.getQualifier()) + ":"
							+ Bytes.toString(cell.getFamily());
					result.add(colname + "=" + Bytes.toString(cell.getValue()));
				}
				results.add(result.toArray(new Object[result.size()]));
			}
			return results;
		}
	}

	/**
	 * query data content corresponding to rowkey
	 * 
	 * @param tablename
	 * @param rowkey
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public Object[] searchContent(String tablename, byte[] rowkey)
			throws Exception {
		HTable instance = getTable(tablename);
		if (instance == null) {
			return null;
		} else {
			Get get = new Get(rowkey);
			Result r = instance.get(get);
			List<Object> result = new ArrayList<Object>();
			result.add(r.getRow());
			for (Cell cell : r.listCells()) {
				String colname = Bytes.toString(cell.getQualifier()) + ":"
						+ Bytes.toString(cell.getFamily());
				result.add(colname + "=" + Bytes.toString(cell.getValue()));
			}
			return result.toArray(new Object[result.size()]);
		}
	}

	public HTable getTable(String tablename) {
		HTable instance = tablename2tableisntance.get(tablename);
		if (instance == null) {
			getTables();
			instance = tablename2tableisntance.get(tablename);
		}
		return instance;
	}

	@SuppressWarnings("deprecation")
	public List<Object[]> show(String tablename, int pagenum) throws Exception {
		// show("BDHP_X" , 2)
		HTable instance = tablename2tableisntance.get(tablename);
		if (instance == null) {
			instance = new HTable(hbaseadmin.getConfiguration(),
					Bytes.toBytes(tablename));
			tablename2tableisntance.put(tablename, instance);
		}
		List<Object[]> results = new ArrayList<Object[]>();
		Scan scan = new Scan();
		scan.setFilter(new PageFilter(100));
		ResultScanner rs = instance.getScanner(scan);
		for (Result r : rs) {
			List<Object> result = new ArrayList<Object>();
			result.add(r.getRow());
			for (Cell cell : r.listCells()) {
				String colname = Bytes.toString(cell.getQualifier()) + ":"
						+ Bytes.toString(cell.getFamily());
				result.add(colname + "=" + Bytes.toString(cell.getValue()));
			}
			results.add(result.toArray(new Object[result.size()]));
		}
		return results;
	}

	@SuppressWarnings("deprecation")
	public List<Object[]> showall(String tablename) throws Exception {
		// showall("BDHP_X")
		HTable instance = tablename2tableisntance.get(tablename);
		if (instance == null) {
			instance = new HTable(hbaseadmin.getConfiguration(),
					Bytes.toBytes(tablename));
			tablename2tableisntance.put(tablename, instance);
		}
		List<Object[]> results = new ArrayList<Object[]>();
		ResultScanner rs = instance.getScanner(new Scan());
		for (Result r : rs) {
			List<Object> result = new ArrayList<Object>();
			result.add(r.getRow());
			for (Cell cell : r.listCells()) {
				String colname = Bytes.toString(cell.getQualifier()) + ":"
						+ Bytes.toString(cell.getFamily());
				result.add(colname + "=" + Bytes.toString(cell.getValue()));
			}
			results.add(result.toArray(new Object[result.size()]));
		}
		return results;
	}

	@SuppressWarnings("deprecation")
	public void createTable(String tablename, String[] familyNames)
			throws Exception {
		if (hbaseadmin.tableExists(Bytes.toBytes(tablename)) == false) {
			HTableDescriptor desc = new HTableDescriptor(
					Bytes.toBytes(tablename));
			for (String familyName : familyNames) {
				desc.addFamily(new HColumnDescriptor(Bytes.toBytes(familyName)));
			}
			hbaseadmin.createTable(desc);
		} else {
			List<String[]> fields = getFields(tablename);
			HashSet<String> set = new HashSet<String>();
			for (String[] field : fields) {
				set.add(field[0]);
			}

			for (String familyName : familyNames) {
				if (set.contains(familyName) == false) {
					hbaseadmin.addColumn(tablename, new HColumnDescriptor(
							familyName));
				}
			}
		}
	}

	@Override
	/**
	 * otherparams[0] = HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY
	 * otherparams[1] = HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS
	 */
	public HashMap<String, String> createDBDescription(String tablename,
			String[] fields, String[] otherparams) {
		// TODO Auto-generated method stub
		Configuration conf = hbaseadmin.getConfiguration();
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST,
				conf.get("hbase.zookeeper.quorum"));
		configMap.put(HBaseDataSource.CONFIG_HBASE_ZKNODE,
				conf.get("zookeeper.znode.parent"));
		configMap.put(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME, tablename);
		StringBuilder columnstrbuilder = new StringBuilder();
		if (fields.length > 0) {
			columnstrbuilder.append(fields[0]);
		}
		for (int i = 1; i < fields.length; i++) {
			columnstrbuilder.append(',');
			columnstrbuilder.append(fields[i]);
		}
		configMap.put(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP,
				columnstrbuilder.toString());
		if (otherparams.length > 0) {
			configMap.put(
					HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY,
					otherparams[0]);
		}
		if (otherparams.length > 1) {
			configMap.put(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS,
					otherparams[1]);
		}
		return configMap;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			hbaseadmin.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		HBaseOperator operator = new HBaseOperator();
		String[] tablenames = operator.getTables();
		for (String tablename : tablenames) {
			System.out.println(tablename);
			List<String[]> fields = operator.getFields(tablename);
			for (String[] field : fields) {
				System.out.println("---" + field[0]);
			}
		}
		operator.searchData(
				"hb_lvbb_vehicle",
				Bytes.toBytes(String.valueOf(System.currentTimeMillis() - 24
						* 60 * 60 * 1000)),
				Bytes.toBytes(String.valueOf(System.currentTimeMillis())));
		// operator.createTable("BDHP_BASEPICRECORD", new String[] { "n", "ci"
		// });
		// operator.update("BDHP_BASEPICRECORD", "vt:n,vc:ci,pkd:n,pn:n",
		// "11,21,1,90001", Bytes.toBytes(System.currentTimeMillis()));
		// operator.update("BDHP_BASEPICRECORD", "vt:n,vc:ci,pkd:n,pn:n",
		// "11,22,1,90001", Bytes.toBytes(System.currentTimeMillis()));
		// HashMap<String, String> config = operator.createDBDescription("car",
		// new String[] { "xx:d" }, new String[] { "1", "0" });
		// Set<String> keys = config.keySet();
		// for (String key : keys) {
		// System.out.println(key + "\t" + config.get(key));
		// }
		// List<Object[]> results = operator.show("sqoop_hbase_table", 3);
		// for (Object[] obj : results) {
		// System.out.println(obj[0]);
		// }
		// operator.createTable("TestCreateTableFromAPI", new String[] { "abc",
		// "def" });
		// operator.update("TestCreateTableFromAPI", "AC:abc,DF:def", "T,f",
		// "ROWKEY".getBytes());
		// operator.delete("TestCreateTableFromAPI", "ROWKEY".getBytes());
		// operator.createTable("yyyy", new String[] { "x", "y" });
		operator.close();
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDriver() {
		// TODO Auto-generated method stub
		return null;
	}
}
