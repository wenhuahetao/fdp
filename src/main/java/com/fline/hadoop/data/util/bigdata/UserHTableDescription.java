package com.fline.hadoop.data.util.bigdata;

import org.apache.hadoop.hbase.util.Bytes;

public class UserHTableDescription {
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
}
