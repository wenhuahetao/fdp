package com.hetao.service;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FieldStatsInfo;


import com.hetao.bean.SolrDocBean;
import com.hetao.util.Page;

public interface SolrService {
	
	public void uploadFile2Solr(String filepath,String schema);
	public List<String> getTableInfos(String solrmasterurl);
	public List<SolrDocBean> query(String solrmasterurl,int datasourceType, long starttime,
			long endtime, String label, String tablename, String queryKeyWord,
			String username);
	public String[] cols(String solrmasterurl,String tablename);
	
	public List<SolrDocBean> listAutoLableDoc(String filePath);
	
	public String showContent(String hdfspath);
	
	public List<Map<String,String>> queryByFacet(String queryTerms,String facetField,String rows);
	
	public List<Map<String,String>> queryFromSolr(String queryTerms,String[] queryFields,String rows,String sortField,String order);
	
	public Page queryFromSolr(String queryTerms,String[] queryFields,String start,String size,String sortField,String order);
	
	public Map<String, FieldStatsInfo> statsComponentStatic(String queryStr,String facetFields,String statsFields);
}
