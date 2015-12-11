package com.fline.hadoop.data.common.analysis;

import java.util.Map;

public interface ContentReader {
	// if no more data, then return null;
	public byte[] getNextBytes() throws Exception;

	/**
	 * use the params to create a reader, then getNextBytes can be used.
	 * 
	 * @param params
	 *            configuration.
	 * @return whether reader is ready.
	 */
	public boolean setupReader(Map<String, String> params);

	/**
	 * get the file size.
	 * 
	 * @return file size
	 */
	public double getTotalSize();

	public void close() throws Exception;
}
