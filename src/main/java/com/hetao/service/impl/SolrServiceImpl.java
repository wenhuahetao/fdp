package com.hetao.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;
import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.client.DataTransporter;
import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.InputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource.SolrOutputDataSource;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.fline.hadoop.data.util.bigdata.Solr_Query;
import com.hetao.bean.SolrDocBean;
import com.hetao.service.SolrService;
import com.hetao.util.Page;
import com.hetao.util.ResourceUtils;

@Service("sorlService")
public class SolrServiceImpl implements SolrService {
	
	final static int AUTO_LABEL_NUM = 6;
	final static String separator = ",";
	final static double SIMILARITY = 0.5;
	
	@Override
	public Map<String, FieldStatsInfo> statsComponentStatic(String queryStr,String facetFields,String statsFields){
		return ResourceUtils.getSolrInstance().statsComponentStatic(queryStr,facetFields,statsFields);
	}
	
	@Override
	public List<Map<String,String>> queryByFacet(String queryTerms,String facetField,String rows){
		return ResourceUtils.getSolrInstance().queryByFacet(queryTerms, facetField, rows);
	}
	
	@Override
	public Page queryFromSolr(String queryTerms,String[] queryFields,String start,String size,String sortField,String order){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		SolrDocumentList solrList = ResourceUtils.getSolrInstance().query(queryTerms.toString(),Integer.parseInt(start),Integer.parseInt(size),sortField,order);
		Page page = new Page();
		for (SolrDocument solrDocument : solrList) {
			Map<String,String> map = new HashMap<String,String>();
			for (int i = 0; i < queryFields.length; i++) {
				if(solrDocument.getFieldValue(queryFields[i]+"_d")==null){
					map.put(queryFields[i], "");
				}else{
					map.put(queryFields[i], solrDocument.getFieldValue(queryFields[i]+"_d").toString());
				}
			}
			list.add(map);
		}
		page.setCount(solrList.getNumFound());
		page.setDatas(list);
		page.setCurrent(Integer.parseInt(start));
		page.setSize(Integer.parseInt(size));
		return page;
	}
	
	@Override
	public List<Map<String,String>> queryFromSolr(String queryTerms,String[] queryFields,String rows,String sortField,String order){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		SolrDocumentList solrList = ResourceUtils.getSolrInstance().query(queryTerms.toString(),rows,sortField,order);
		for (SolrDocument solrDocument : solrList) {
			Map<String,String> map = new HashMap<String,String>();
			for (int i = 0; i < queryFields.length; i++) {
				if(solrDocument.getFieldValue(queryFields[i]+"_d")==null){
					map.put(queryFields[i], "");
				}else{
					map.put(queryFields[i], solrDocument.getFieldValue(queryFields[i]+"_d").toString());
				}
			}
			list.add(map);
		}
		return list;
	}
	
	@Override
	public String showContent(String hdfspath){
		StringBuffer querystr = new StringBuffer();
		if(hdfspath!=null && !"".equals(hdfspath)){
			querystr.append("sourceType:1 AND hdfspath:"+"\""+hdfspath+"\"");
		}
		String sort = "createdTime";
		SolrDocumentList solrList = ResourceUtils.getSolrInstance().query(querystr.toString(),1,sort);
		if(solrList!=null && solrList.size()>0){
			return solrList.get(0).getFieldValue("filecontent").toString();
		}
		return null;
	}
	
	@Override
	public List<SolrDocBean> listAutoLableDoc(String filePath){
		String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
		return listDocByLables(findLabels(fileName),filePath);
	}
	private List<String> findLabels(String filename){
		StringBuffer querystr = new StringBuffer();
		if(filename!=null && !"".equals(filename)){
			querystr.append("sourceType:1 AND filename:"+filename);
		}
		String sort = "createdTime";
		SolrDocumentList solrList = ResourceUtils.getSolrInstance().query(querystr.toString(),1,sort);
		if(solrList!=null && solrList.size()>0){
			return (List<String>) solrList.get(0).getFieldValue("label");
		}
		return null;
	}
	private List<SolrDocBean> listDocByLables(List<String> lables,String filePath){
		List<SolrDocBean> solrList = new ArrayList<SolrDocBean>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String searchParam = "";
		if ((lables != null) && (lables.size() != 0)) {
		      searchParam = searchParam + "label:( ";
		}
		for (int j = 0; j < lables.size(); j++) {
			searchParam = searchParam + lables.get(j);
	        if (j != lables.size() - 1) {
	          searchParam = searchParam + " OR ";
	        }
		}
		searchParam = searchParam + " ) ";
		searchParam = searchParam + "AND hdfspath:(* NOT " + "\""+filePath+ "\"  )";
		SolrDocumentList list = ResourceUtils.getSolrInstance().query(searchParam,AUTO_LABEL_NUM,null);
//		String similaritOne = StringUtils.join(lables.toArray(),separator);
		for (SolrDocument s : list) {
			List<String> lablesTwo = (List<String>) s.getFieldValue("label");
//			String similaritTwo =  StringUtils.join(lablesTwo.toArray(),separator);
//			double similarity = SimilarDegreeByCos.getSimilarDegree(similaritOne, similaritTwo);
			double similarity = getSimilarity(lables,lablesTwo);
			System.out.println("similarity*****"+similarity);
			if(similarity>=SIMILARITY){
				SolrDocBean solrbaen=new SolrDocBean();
				solrbaen.setFilename(s.getFieldValue("filename").toString());
				solrbaen.setFilecontent(s.getFieldValue("filecontent").toString());
				solrbaen.setHdfspath(s.getFieldValue("hdfspath").toString());
				long time=(long) s.getFieldValue("createdTime");
				solrbaen.setCreatedTime(sdf.format(new Date(time)));
				solrList.add(solrbaen);
			}
		}
		
		return solrList;
	}

	private double getSimilarity(List<String> lablesOne,List<String> lablesTwo){
		int i = 0;
		for (String  one : lablesOne) {
			for (String two : lablesTwo) {
				if(one.contains(two) || two.contains(one)){
					i++;
					break;
				}
			}
		}
		double similarity = (i+0.0)/(lablesOne.size()+0.0);
		return similarity;
	}
	
	public static void main(String[] args) {
		String downfilename= "/user/public/1.txt";
		new SolrServiceImpl().listAutoLableDoc(downfilename);
	}
	
	@Override
	public void uploadFile2Solr(String filepath,String schema) {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// File Config
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH,filepath);
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
				FileInputDataSource.LINE_READER);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
				FileInputDataSource.CSV_ANALYZER);
		configMap.put(FileInputDataSource.CONFIG_ANALYZER_SCHEMA, schema);
		configMap.put(FileInputDataSource.CONFIG_ANALZYER_RESULTTYPE, "1,4");
		InputDataSource inputdatasource = InputDataSourceFactory
				.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,
						configMap);
		// solr CONFIG
		HashMap<String, String> solrconfig = new HashMap<String, String>();
		solrconfig.put(SolrDataSource.CONFIG_SOLR_MASTERURL,
				ResourceUtils.getSolrMasterUrl());
		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_COLUMNS, "id,name");
		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_INSTANCE,
				"core_for_test");
		OutputDataSource solr = new SolrOutputDataSource(solrconfig,
				Constant.SOLR_DATASOURCE_NAME);
		DataProgressListener listener = new DataProgressListener() {
			@Override
			public void handleEvent(ProgressEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() instanceof ProgressSource) {
					System.out.println(((ProgressSource) e.getSource())
							.getStat());
				}
			}
		};
		try {
			DataTransporter.createTransJob(inputdatasource, solr, listener);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	@Override
	public List<String> getTableInfos(String solrmasterurl) {
		// TODO Auto-generated method stub
		List<String> list=new ArrayList<String>();
		Solr_Query solr_Query=new Solr_Query(solrmasterurl);
		try {
			String[] ret=solr_Query.getTableInfos();
			for(String s:ret){
				list.add(s);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	@Override
	public List<SolrDocBean> query(String solrmasterurl,int datasourceType, long starttime,
			long endtime, String label, String tablename, String queryKeyWord,
			String username) {
		// TODO Auto-generated method stub
		List<SolrDocBean> list_solr=new ArrayList<SolrDocBean>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SolrDocumentList list=new SolrDocumentList();
		Solr_Query solr_Query=new Solr_Query(solrmasterurl);
		try {
			list=solr_Query.query(datasourceType, starttime, endtime, label, tablename, queryKeyWord, username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (SolrDocument s : list) {
			SolrDocBean solrbaen=new SolrDocBean();
			solrbaen.setFilename(s.getFieldValue("filename").toString());
			solrbaen.setFilecontent(s.getFieldValue("filecontent").toString());
			solrbaen.setHdfspath(s.getFieldValue("hdfspath").toString());
			long time=(long) s.getFieldValue("createdTime");
			solrbaen.setCreatedTime(sdf.format(new Date(time)));
			list_solr.add(solrbaen);
		}
		return list_solr;
	}

	@Override
	public String[] cols(String solrmasterurl, String tablename) {
		Solr_Query solr_Query=new Solr_Query(solrmasterurl);
		String[] cols={};
		try {
			cols = solr_Query.getFields(tablename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cols;
	}
}
