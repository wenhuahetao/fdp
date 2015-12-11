package com.fline.hadoop.data.util.bigdata;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.FacetParams;

import com.hetao.bean.SolrDocBean;
import com.hetao.util.ResourceUtils;

public class Solr_Query {
	private HttpSolrClient dbclient = null;
	private HttpSolrClient metaclient = null;

	public Solr_Query(String solrmasterurl) {
		dbclient = new HttpSolrClient(solrmasterurl + "core_for_searchDB");
		metaclient = new HttpSolrClient(solrmasterurl + "core_for_Meta");
	}

	/**
	 * 
	 * @return all tables from rdb in solr.
	 * @throws Exception
	 */
	public String[] getTableInfos() throws Exception {
		SolrQuery params = new SolrQuery("*:*").setFacet(true).addFacetField(
				"rdbtablename");
		QueryResponse response = metaclient.query(params);
		List<Count> counters = response.getFacetField("rdbtablename")
				.getValues();
		if (counters == null) {
			return null;
		} else {
			String[] ret = new String[counters.size()];
			for (int i = 0; i < counters.size(); i++) {
				ret[i] = counters.get(i).getName();
			}
			return ret;
		}
	}

	/**
	 * get fields in solr with rdbtablename
	 * 
	 * @param tablename
	 * @return
	 * @throws Exception
	 */
	public String[] getFields(String tablename) throws Exception {
		SolrQuery params = new SolrQuery("rdbtablename:" + tablename);
		QueryResponse response = metaclient.query(params);
		SolrDocumentList list = response.getResults();
		HashSet<String> ret = new HashSet<String>();
		for (int i = 0; i < list.size(); i++) {
			String cols = (String) list.get(i).get("rdbtablecols");
			if (cols != null) {
				String[] splits = cols.split(",");
				for (String col : splits) {
					ret.add(col);
				}
			}
		}
		return ret.toArray(new String[ret.size()]);
	}

	public SolrDocumentList query(String querystr) throws Exception {
		SolrQuery params = new SolrQuery(querystr);
		QueryResponse response = dbclient.query(params);
		return response.getResults();
	}

	/**
	 * 
	 * @param datasourceType
	 *            1=file,2=db
	 * @param starttime
	 *            to query data between starttime and endtime . <=0 means do not
	 *            use it.
	 * @param endtime
	 *            to query data between starttime and endtime . <=0 means will
	 *            not use it.
	 * @param label
	 *            user label.
	 * @param tablename
	 *            if datasourceType == 2, then it must be set.
	 * @param queryKeyWord
	 *            query keyword.
	 * @param username
	 *            if == null, then not used.
	 * @return
	 * @throws Exception
	 */
	public SolrDocumentList query(int datasourceType, long starttime,
			long endtime, String label, String tablename, String queryKeyWord,
			String username) throws Exception {
		SolrQuery filterQuery = new SolrQuery();
		if (queryKeyWord == null || queryKeyWord.length() <= 0) {
			throw new Exception("queryKeyWord could not be empty");
		}
		if (datasourceType == 1) {
			// file search
			filterQuery.setQuery("filecontent:" + queryKeyWord
					+ " || filename:" + queryKeyWord);
		} else if (datasourceType == 2) {
			if (tablename == null || tablename.length() <= 0) {
				throw new Exception(
						"datasourceType = 2, means query db data, tablename could not be empty");
			} else {
				String[] cols = getFields(tablename);
				if (cols == null || cols.length <= 0) {
					throw new Exception("no cols in table " + tablename);
				} else {
					filterQuery.setQuery("sourceType:2 && rdbtablename:"
							+ tablename);
					if (cols.length >= 1) {
						StringBuilder querybuilder = new StringBuilder();
						querybuilder.append(cols[0]);
						querybuilder.append(':');
						querybuilder.append(queryKeyWord);
						for (int i = 1; i < cols.length; i++) {
							querybuilder.append(" || ");
							querybuilder.append(cols[i]);
							querybuilder.append(':');
							querybuilder.append(queryKeyWord);
						}
						filterQuery.addFilterQuery(querybuilder.toString());
					}
				}
			}
		} else {
			throw new Exception("unsupport datasourceType = " + datasourceType);
		}
		if (starttime > 0 && endtime > 0) {
			filterQuery.addFilterQuery("createdTime:[" + starttime + " TO "
					+ endtime + "]");
		}
		if (label != null && label.length() > 0) {
			filterQuery.addFilterQuery("label:" + label);
		}
		if (username != null && username.length() > 0) {
			filterQuery.addFilterQuery("username:" + username);
		}
		QueryResponse response = dbclient.query(filterQuery);
		return response.getResults();
	}

	/**
	 * update docs, fieldname must be all in oldvalue.
	 * 
	 * @param fieldName
	 * @param oldvalue
	 * @param newvalue
	 * @throws Exception
	 */
	public void updateSolrIndex(String fieldName[], String oldvalue[],
			String newvalue[]) throws Exception {
		if (fieldName.length != oldvalue.length
				|| fieldName.length != newvalue.length) {
			throw new Exception(
					"fieldName.length must equals oldvalue.length and newvalue.length");
		}
		StringBuilder querystr = new StringBuilder();
		querystr.append(fieldName[0]);
		querystr.append(':');
		querystr.append("\"");
		querystr.append(oldvalue[0]);
		querystr.append("\"");
		for (int i = 1; i < fieldName.length; i++) {
			querystr.append(" && ");
			querystr.append(fieldName[i]);
			querystr.append(':');
			querystr.append("\"");
			querystr.append(oldvalue[i]);
			querystr.append("\"");
		}
		dbclient.deleteByQuery(querystr.toString());
		SolrInputDocument doc = new SolrInputDocument();
		for (int i = 0; i < fieldName.length; i++) {
			doc.addField(fieldName[i], newvalue[i]);
		}
		dbclient.add(doc);
		dbclient.commit();
	}

	public void updateSolrIndex(String oldhdfspath,
			Map<String, Object> newvalues) throws Exception {
		deleteDoc(oldhdfspath);
		SolrInputDocument doc = new SolrInputDocument();
		Set<String> keys = newvalues.keySet();
		for (String key : keys) {
			doc.addField(key, newvalues.get(key));
		}
		dbclient.add(doc);
		dbclient.commit();
	}

	public void addDoc(Map<String, Object> newvalues) throws Exception {
		SolrInputDocument doc = new SolrInputDocument();
		Set<String> keys = newvalues.keySet();
		for (String key : keys) {
			doc.addField(key, newvalues.get(key));
		}
		dbclient.add(doc);
		dbclient.commit();
	}

	public void deleteDoc(String hdfspath) throws Exception {
		dbclient.deleteByQuery("hdfspath:\"" + hdfspath + "\"");
		dbclient.commit();
	}

	public void close() throws IOException {
		dbclient.close();
		metaclient.close();
	}

	public void clear() throws Exception {
		dbclient.deleteByQuery("*:*");
		dbclient.commit();
		metaclient.deleteByQuery("*:*");
		metaclient.commit();
	}
	
	public List<Map<String,String>> queryByFacet(String queryStr,String facetField,String rows){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		SolrQuery query = new SolrQuery();
		query.setQuery(queryStr);
		query.setRows(Integer.parseInt(rows));
		try {
			query.set("facet", "on");
			query.set("facet.field", facetField);
			query.setFacetLimit(Integer.parseInt(rows));
			query.setFacetMinCount(1);
			query.set(FacetParams.FACET_METHOD, "fcs");

			QueryResponse response = dbclient.query(query, METHOD.POST);
			FacetField categoryField = response.getFacetField(facetField);
			if (categoryField != null) {
				List<Count> counts = categoryField.getValues();
				if (counts != null) {
					for (Count count : counts) {
						Map<String,String> map = new HashMap<String,String>();
						map.put("fieldValue", count.getName());
						map.put("count", count.getCount()+"");
						list.add(map);
						System.out.println("分类名称:" + count.getName() + "  次数:" + count.getCount());
					}
				}
			}
			System.out.println("Records found:" + response.getResults().getNumFound());
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 分页查询
	 * @param querystr
	 * @param start
	 * @param size
	 * @param sort
	 * @param order
	 * @return
	 */
	public SolrDocumentList query(String querystr,int page,int rows,String sort,String order){
		try {
			int start = (page - 1) < 0 ? 0 : (page - 1) * rows;
			
			SolrQuery params = new SolrQuery(querystr);
			if(sort!=null && !"".equals(sort)){
				if("desc".equals(order)){
					params.addSort(sort, SolrQuery.ORDER.desc);
				}else{
					params.addSort(sort, SolrQuery.ORDER.asc);
				}
			}
			if (start >= 0) {
				params.setStart(Integer.valueOf(start));
		    }
		    if (rows >= 0) {
		    	params.setRows(Integer.valueOf(rows));
		    }
			QueryResponse response = dbclient.query(params);
			SolrDocumentList solrList = response.getResults();
			return solrList;
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public SolrDocumentList query(String querystr,String rows,String sort,String order){
		try {
			SolrQuery params = new SolrQuery(querystr);
			if(sort!=null && !"".equals(sort)){
				if("desc".equals(order)){
					params.addSort(sort, SolrQuery.ORDER.desc);
				}else{
					params.addSort(sort, SolrQuery.ORDER.asc);
				}
			}
			if(rows!=null && !"".equals(rows)){
				params.setRows(Integer.parseInt(rows));
			}
			QueryResponse response = dbclient.query(params);
			SolrDocumentList solrList = response.getResults();
			return solrList;
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public SolrDocumentList query(String querystr,int rows,String sort){
		try {
			SolrQuery params = new SolrQuery(querystr);
			if(sort!=null && !"".equals(sort)){
				params.addSort(sort, SolrQuery.ORDER.desc);
			}
			params.setRows(rows);
			QueryResponse response = dbclient.query(params);
			SolrDocumentList solrList = response.getResults();
			return solrList;
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, FieldStatsInfo> statsComponentStatic(String queryStr,String facetFields,String statsFields) {
		Map<String, FieldStatsInfo> fieldStatsInfoMap = null;
		try {
			SolrQuery query = new SolrQuery();
			query.setQuery(queryStr);
			query.setRows(0);
			query.set("stats",true);
			String[] facetFieldArr = facetFields.split(",");
			for (String facetField : facetFieldArr) {
				query.set("stats.facet",facetField);
			}
			String[] statsFieldArr = statsFields.split(",");
			for (String statsField : statsFieldArr) {
				query.set("stats.field",statsField);
			}
			QueryResponse resp = dbclient.query(query);
			fieldStatsInfoMap = resp.getFieldStatsInfo();
			
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fieldStatsInfoMap;
	}
	public static void main(String[] args) throws Exception {
		Solr_Query q = new Solr_Query("http://localhost:8983/solr/");
		String facetFields = "regNo";
		String statsFields = "RegMoney";
		Map<String, FieldStatsInfo> fieldStatsInfoMap = q.statsComponentStatic(facetFields,statsFields,"1");
		for (String key : fieldStatsInfoMap.keySet()) {
			FieldStatsInfo fieldStatsInfo1 = fieldStatsInfoMap.get(key);
			Map<String, List<FieldStatsInfo>> facets = fieldStatsInfo1.getFacets();
			for (String facetFieldKey : facets.keySet()) {
				List<FieldStatsInfo> fieldStatsList = facets.get(facetFieldKey);
				for (FieldStatsInfo fieldStatsInfo : fieldStatsList) {
					String name = fieldStatsInfo.getName();
					if(null != name && !"".equals(name)){
						Object sum = fieldStatsInfo.getSum();
						Long count = fieldStatsInfo.getCount();
						System.out.println("name: " + name + "\t sum : " + sum + "\t count : " + count);
					}
				}
			}
			
		}
		q.close();
	}
}
