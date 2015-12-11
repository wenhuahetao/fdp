package com.fline.hadoop.data.core.writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource.SolrOutputDataSource;

public class SolrRecordsWriter extends AbstractRecordsWriter {

	String[] columns = null;
	HttpSolrClient client = null;
	private static Logger LOG = Logger.getLogger(SolrRecordsWriter.class);

	public SolrRecordsWriter(OutputDataSource outputdatasource) {
		super(outputdatasource);
		columns = config.get(SolrOutputDataSource.CONFIG_SOLR_COLUMNS).split(
				",");
		client = new HttpSolrClient(
				config.get(SolrDataSource.CONFIG_SOLR_MASTERURL)
						+ config.get(SolrOutputDataSource.CONFIG_SOLR_INSTANCE));
	}

	@Override
	public void writeRecords(List<String[]> records) {
		// TODO Auto-generated method stub
		List<SolrInputDocument> sidocs = new ArrayList<SolrInputDocument>();
		for (String[] record : records) {
			if (columns.length != record.length) {
				LOG.warn("SolrLoader load record.length = " + record.length
						+ ", but sorlcolumns.length = " + columns.length
						+ ". quit record.");
				continue;
			} else {
				SolrInputDocument sidoc = new SolrInputDocument();
				Object[] formateddata = format(record);
				for (int i = 0; i < columns.length; i++) {
					if (columns[i].equals("label")) {
						String[] valuesplits = ((String) formateddata[i])
								.split("\\s+");
						Set<String> values = new HashSet<String>();
						for (String valuesplit : valuesplits) {
							values.add(valuesplit);
						}
						sidoc.addField(columns[i], values);
					} else {
						sidoc.addField(columns[i], formateddata[i]);
					}
				}
				sidocs.add(sidoc);
			}
		}
		LOG.debug("load docs " + sidocs.size());
		try {
			client.add(sidocs);
			client.commit();
		} catch (Exception e) {
			LOG.error(e);
		}
		LOG.debug("upload docs successfully");
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e);
		}
	}

	@Override
	public void writeObjRecords(List<Object[]> records) {
		// TODO Auto-generated method stub
		List<SolrInputDocument> sidocs = new ArrayList<SolrInputDocument>();
		for (Object[] record : records) {
			if (columns.length != record.length) {
				LOG.warn("SolrLoader load record.length = " + record.length
						+ ", but sorlcolumns.length = " + columns.length
						+ ". quit record.");
				continue;
			} else {
				SolrInputDocument sidoc = new SolrInputDocument();
				for (int i = 0; i < columns.length; i++) {
					sidoc.addField(columns[i], record[i]);
				}
				sidocs.add(sidoc);
			}
		}
		LOG.debug("load docs " + sidocs.size());
		try {
			client.add(sidocs);
			client.commit();
		} catch (Exception e) {
			LOG.error(e);
		}
		LOG.debug("upload docs successfully");
	}

}
