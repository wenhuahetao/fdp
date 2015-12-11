package com.hetao.constroller;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.fline.hadoop.data.util.bigdata.Solr_Query;
import com.hetao.bean.SolrDocBean;
import com.hetao.service.SolrService;
import com.hetao.util.EncryptionDecryption;
import com.hetao.util.Page;
import com.hetao.util.ResourceUtils;

@Controller
@RequestMapping("/solr")
public class SolrConstroller {
	@Autowired private SolrService solrService;
	
	/**
	 * queryFromSolr4Stats
	 */
	@RequestMapping(value = "/queryFromSolr4Stats", method = RequestMethod.GET)
	@ResponseBody
	protected JSONObject queryFromSolr4Stats(String queryTerms,String facetFields,String statsFields,String rows) throws Exception {
		EncryptionDecryption des = new EncryptionDecryption("wf");
		String queryStr = URLDecoder.decode(des.decrypt(queryTerms), "UTF-8");
		Map<String,Object> modelMap= new HashMap<String,Object>();
		Map<String, FieldStatsInfo> contents = solrService.statsComponentStatic(queryStr,facetFields, statsFields);
		modelMap.put("contents", contents);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	/**
	 * queryFromSolr
	 */
	@RequestMapping(value = "/queryFromSolr4Page", method = RequestMethod.GET)
	@ResponseBody
	protected JSONObject queryFromSolr4Page(String queryTerms,String queryFields,String page, String rows,
			String sortField,String order) throws Exception {
		EncryptionDecryption des = new EncryptionDecryption("wf");
		String queryStr = URLDecoder.decode(des.decrypt(queryTerms), "UTF-8");
		Map<String,Object> modelMap= new HashMap<String,Object>();
		Page contents = solrService.queryFromSolr(queryStr, queryFields.split(","), page,rows, sortField, order);
		modelMap.put("contents", contents);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	/**
	 * queryFromSolr
	 */
	@RequestMapping(value = "/queryFromSolr", method = RequestMethod.GET)
	@ResponseBody
	protected JSONObject queryFromSolr(String queryTerms,String queryFields,String rows,
			String sortField,String order) throws Exception {
		EncryptionDecryption des = new EncryptionDecryption("wf");
		String queryStr = URLDecoder.decode(des.decrypt(queryTerms), "UTF-8");
		Map<String,Object> modelMap= new HashMap<String,Object>();
		List<Map<String,String>> contents = solrService.queryFromSolr(queryStr, queryFields.split(","), rows, sortField, order);
		modelMap.put("contents", contents);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	
	/**
	 * queryByFacet
	 */
	@RequestMapping(value = "/queryByFacet", method = RequestMethod.GET)
	@ResponseBody
	protected JSONObject queryByFacet(String queryTerms,String facetField,String rows) throws Exception {
		EncryptionDecryption des = new EncryptionDecryption("wf");
		String queryStr = URLDecoder.decode(des.decrypt(queryTerms), "UTF-8");
		Map<String,Object> modelMap= new HashMap<String,Object>();
		List<Map<String,String>> contents = solrService.queryByFacet(queryStr, facetField, rows);
		modelMap.put("contents", contents);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	
	/**
	 * list
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response, ModelMap modelMap,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "rows", required = false, defaultValue = "5") Integer rows) throws Exception {
		List<String> list=solrService.getTableInfos(ResourceUtils.getSolrMasterUrl());
		modelMap.put("DBlist",list);
		return new ModelAndView("/search", modelMap);
	}
	
	
	@RequestMapping(value = "/ajaxJump", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject ajaxGetHbaseFamily(HttpServletRequest request) throws Exception {
		long st ;
		long et ;
		String solrmasterurl = ResourceUtils.getSolrMasterUrl();
		List<SolrDocBean> list_solr=new ArrayList<SolrDocBean>();
		Map<String,Object> modelMap= new HashMap<String,Object>();
		String flag=request.getParameter("flag");
		String flagtime=request.getParameter("flagtime");
		String filename=request.getParameter("filename");
		String tablename=request.getParameter("tablename");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
		//判断是否选择了日期
		if("0".endsWith(flagtime)){
			st = -1;
		    et = -1; 
		}
		else{
			//勾选了checkbox没有选时间
			//sdf.parse(request.getParameter("st").toString()).getTime();
			if("".equals(request.getParameter("st").toString()))st=-1;
			else st = sdf.parse(request.getParameter("st").toString()).getTime(); 
			if("".equals(request.getParameter("et").toString()))et=-1;
			else et = sdf.parse(request.getParameter("et").toString()).getTime(); 
			
		}
		//数据表是否为空
		if("".equals(tablename)||tablename==null){
			tablename=null;
		}
		//判断是文件还是数据表
		if("1".equals(flag)){
			
			list_solr=solrService.query(solrmasterurl,Integer.parseInt(flag), st, et,
					null, null, filename, null);
			modelMap.put("listsolr",list_solr);
		}else if("2".equals(flag)){
			// 获取tablename对应的所有列
			String[] cols=solrService.cols(solrmasterurl, tablename);
			modelMap.put("cols",cols);
			//获取数据
			Solr_Query q = new Solr_Query(solrmasterurl);
			SolrDocumentList list = q.query(2, st, et, null, tablename, filename, null);
			modelMap.put("listsolr",list);
		}
		long endMili=System.currentTimeMillis();
		//总耗时
		double allMili=(Math.round(endMili-startMili)*1000)/1000000.00;
		modelMap.put("allMili", allMili);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
}
