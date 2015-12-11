package com.fline.hadoop.data.core.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.ElasticSearchDataSource;
import com.fline.hadoop.data.common.datasource.impl.ElasticSearchDataSource.ESOutputDataSource;

public class ESRecordsWriter extends AbstractRecordsWriter {

	String[] columns = null;
	String index = null;
	String indextype = null;
	TransportClient client = null;
	private static Logger LOG = Logger.getLogger(ESRecordsWriter.class);

	public ESRecordsWriter(OutputDataSource outputdatasource) {
		super(outputdatasource);
		parseMap();
		// TODO Auto-generated constructor stub
	}

	public ESRecordsWriter(Map<String, String> config) {
		super(config);
		parseMap();
	}

	@SuppressWarnings("resource")
	private void parseMap() {
		columns = config.get(ESOutputDataSource.CONFIG_ES_COLUMNNAMES).split(
				",");
		index = config.get(ESOutputDataSource.CONFIG_ES_INDEXNAME);
		indextype = config.get(ESOutputDataSource.CONFIG_ES_INDEXTYPE);
		client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(
						config.get(ElasticSearchDataSource.CONFIG_ES_HOSTNAME),
						Integer.valueOf(config
								.get(ElasticSearchDataSource.CONFIG_ES_HOSTPORT))));
	}

	@Override
	public void writeRecords(List<String[]> records) {
		// TODO Auto-generated method stub
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		for (String[] record : records) {
			if (columns.length != record.length) {
				LOG.warn("ESLoader load record.length = " + record.length
						+ ", but columns.length = " + columns.length
						+ ". quit record.");
				continue;
			} else {
				Map<String, Object> jsondata = new HashMap<String, Object>();
				Object[] formateddata = format(record);
				for (int i = 0; i < columns.length; i++) {
					jsondata.put(columns[i], formateddata[i]);
				}
				bulkRequest.add(client.prepareIndex(index, indextype)
						.setSource(jsondata));
			}
		}
		bulkRequest.execute().actionGet();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		client.close();
	}

	@Override
	public void writeObjRecords(List<Object[]> records) {
		// TODO Auto-generated method stub
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		for (Object[] record : records) {
			if (columns.length != record.length) {
				LOG.warn("ESLoader load record.length = " + record.length
						+ ", but columns.length = " + columns.length
						+ ". quit record.");
				continue;
			} else {
				Map<String, Object> jsondata = new HashMap<String, Object>();
				for (int i = 0; i < columns.length; i++) {
					jsondata.put(columns[i], record[i]);
				}
				bulkRequest.add(client.prepareIndex(index, indextype)
						.setSource(jsondata));
			}
		}
		bulkRequest.execute().actionGet();
	}

}
