package com.hetao.constroller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.hadoop.hbase.util.Bytes;
import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.client.DataTransporter;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.fline.hadoop.data.util.rdb.MysqlDB;
import com.google.gson.Gson;
import com.hetao.bean.DbInfo;
import com.hetao.bean.FamilyBean;
import com.hetao.bean.FamilyBean.FamilyCols;
import com.hetao.bean.TableInfo;
import com.hetao.page.Pager;
import com.hetao.service.HbaseService;
import com.hetao.util.EncryptionDecryption;
import com.hetao.util.ResourceUtils;


@Controller
@RequestMapping("/hbase")
public class HbaseConstroller {

	private Gson gson = new Gson();
	@Autowired private HbaseService hbaseService;
	static Map<String,String> mrOneMap = new HashMap<String,String>();
	static String mrkey = "";
	/**
	 * list
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response, ModelMap modelMap,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "rows", required = false, defaultValue = "5") Integer rows) throws Exception {
		// table list
		List<String> tableList = hbaseService.listTable();
		if(tableList!=null && tableList.size()>0){
			//  datas : default of table
			TableInfo tableInfo = hbaseService.listDataByTable(tableList.get(0));
			List list = tableInfo.getHbaselist();
			Pager pager = new Pager(page,rows,list);
			int countPage = list.size() % rows == 0  ? (list.size() / rows) : (list.size() / rows + 1);
			
			Map<String, Set<String>> familyColums = tableInfo.getFamilyColums();
			Set<String> familys = familyColums.keySet();
			int totalLength = 0;
			Map<String, Integer> sizeMap = new HashMap<String,Integer>();
			for (String family : familys) {
				Set<String> colums = familyColums.get(family);
				int length = colums.size();
				sizeMap.put(family, length);
				totalLength+=length;
				
			}
			float width = totalLength!=0 ? 85/totalLength : 20;
			modelMap.put("width", width);
			modelMap.put("sizeMap", sizeMap);
			
			modelMap.put("total", list.size());
			modelMap.put("countPage", countPage);
			modelMap.put("pageIndex", page);
			
			modelMap.put("tablename", tableList.get(0));
			modelMap.put("tableList", tableList);
			modelMap.put("familyColums", tableInfo.getFamilyColums());
			modelMap.put("hbaselist", pager.getPagerList());
		}
		return new ModelAndView("/hbase_index", modelMap);
	}
	/**
	 * list
	 */
	@RequestMapping(value = "/queryList", method = RequestMethod.GET)
	public ModelAndView queryList(HttpServletRequest request,HttpServletResponse response, ModelMap modelMap,
			@RequestParam(value = "tableName", required = true) String tableName,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "rows", required = false, defaultValue = "5") Integer rows) throws Exception {
		// table list
		//  datas : default of table
		List<String> tableList = hbaseService.listTable();
		if(null==tableName || "".equals(tableName)){
			if(null!=tableList&&tableList.size()>0){
				tableName = tableList.get(0);
			}
		}
		TableInfo tableInfo = hbaseService.listDataByTable(tableName);
		List list = tableInfo.getHbaselist();
		Pager pager = new Pager(page,rows,list);
		int countPage = list.size() % rows == 0  ? (list.size() / rows) : (list.size() / rows + 1);
		
		Map<String, Set<String>> familyColums = tableInfo.getFamilyColums();
		Set<String> familys = familyColums.keySet();
		int totalLength = 0;
		Map<String, Integer> sizeMap = new HashMap<String,Integer>();
		for (String family : familys) {
			Set<String> colums = familyColums.get(family);
			int length = colums.size();
			sizeMap.put(family, length);
			totalLength+=length;
			
		}
		float width = totalLength!=0?85/totalLength:20;
		modelMap.put("width", width);
		modelMap.put("sizeMap", sizeMap);
		
		modelMap.put("total", list.size());
		modelMap.put("countPage", countPage);
		modelMap.put("pageIndex", page);
		modelMap.put("tablename", tableName);
		modelMap.put("tableList", tableList);
		modelMap.put("familyColums", tableInfo.getFamilyColums());
		modelMap.put("hbaselist", pager.getPagerList());
		return new ModelAndView("/hbase_index", modelMap);
	}
	
	
	@RequestMapping(value = "/ajaxGetFamilyColums", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject ajaxGetFamilyColums(HttpServletRequest request) throws Exception {
		Map<String,Object> modelMap= new HashMap<String,Object>();
		String hbasetablename = request.getParameter("tablename");
		//  datas : default of table
		TableInfo tableInfo = hbaseService.listDataByTable(hbasetablename);
		modelMap.put("familyColums", tableInfo.getFamilyColums());
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	@RequestMapping(value = "/ajaxGetHbaseFamily", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject ajaxGetHbaseFamily(HttpServletRequest request) throws Exception {
		Map<String,Object> modelMap= new HashMap<String,Object>();
		String hbasetablename = request.getParameter("hbasetablename");
		//  datas : default of table
		TableInfo tableInfo = hbaseService.listDataByTable(hbasetablename);
		modelMap.put("tableInfo", tableInfo);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	@RequestMapping(value = "/ajaxGetHbaseTable", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject ajaxGetHbaseTable(HttpServletRequest request) throws Exception {
		Map<String,Object> modelMap= new HashMap<String,Object>();
		List<String> tableList = hbaseService.listTable();
		modelMap.put("tableList", tableList);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	@RequestMapping(value = "/ajaxGetDateBase", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject ajaxGetDateBase(HttpServletRequest request
			) throws Exception {
		Map<String,Object> modelMap= new HashMap<String,Object>();
		DbInfo db = new DbInfo();
		String dbtablename = request.getParameter("dbname");
		String dbtype = request.getParameter("dbtype");
		db.setDbtype(dbtype);
		db.setDbname(dbtablename);
		String url = request.getParameter("url");
		db.setUrl(url);
		String username = request.getParameter("username");
		db.setUsername(username);
		String password = request.getParameter("passwd");
		db.setPasswd(password);
		List<String> tables = hbaseService.getDataBaseTable(db);
		//check table fields
		int availableIndex = 0;
		for (String table : tables) {
			List<String[]> fields = hbaseService.getDataBaseField(db,table);
			if(fields == null || fields.size()==0) {
				availableIndex++;
			} else {
				break;
			}
		}
		modelMap.put("tables", tables);
		modelMap.put("defaulttable", tables.get(availableIndex));
		List<String[]> list = hbaseService.getDataBaseField(db,tables.get(availableIndex));
		modelMap.put("fields",list );
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	
	@RequestMapping(value = "/ajaxGetDBField", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject ajaxGetDBField(HttpServletRequest request) throws Exception {
		Map<String,Object> modelMap= new HashMap<String,Object>();
		String dbtablename = request.getParameter("dbtablename");
		DbInfo db = new DbInfo();
		String dbtype = request.getParameter("dbtype");
		db.setDbtype(dbtype);
		db.setDbname(request.getParameter("dbname"));
		String url = request.getParameter("url");
		db.setUrl(url);
		String username = request.getParameter("username");
		db.setUsername(username);
		String password = request.getParameter("passwd");
		db.setPasswd(password);
		List<String[]> fields = hbaseService.getDataBaseField(db,dbtablename);
		if(fields == null) {
			modelMap.put("fields", "");
		} else {
			modelMap.put("fields", fields);
		}
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	@RequestMapping(value = "/ajaxGetBaseCount", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject ajaxGetBaseCount(HttpServletRequest request) throws Exception {
		Map<String,Object> modelMap= new HashMap<String,Object>();
		String dbtablename = request.getParameter("dbtablename");
		DbInfo db = new DbInfo();
		String dbtype = request.getParameter("dbtype");
		db.setDbtype(dbtype);
		db.setDbname(request.getParameter("dbname"));
		String url = request.getParameter("url");
		db.setUrl(url);
		String username = request.getParameter("username");
		db.setUsername(username);
		String password = request.getParameter("passwd");
		db.setPasswd(password);
//		String ip, int port, String dbname, String username,String passwd
		int count = new MysqlDB(url,3306,db.getDbname(),db.getUsername(),db.getPasswd()).countTableSize(dbtablename);
		modelMap.put("countDb", count);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	@RequestMapping(value = "/listXzzd", method = RequestMethod.GET)
	public ModelAndView listXzzd(HttpServletRequest request,HttpServletResponse response, ModelMap modelMap) throws Exception {
		String xzcols = request.getParameter("xzcols");
		String rodom = request.getParameter("rodom");
		String type = request.getParameter("type");
		if(StringUtils.isNotEmpty(xzcols) && xzcols.length()>0){
			xzcols = xzcols.substring(1);
		}
		
		String xzzdType;
		if("3".equals(type)){
			xzzdType = "/xzzd_radio";
			rodom = rodom.replace("hcheckbox_", "");
			
		}else{
			xzzdType = "/xzzd";
		}
		modelMap.put("fields", xzcols.split(","));
		modelMap.put("rodom", rodom);
		modelMap.put("type", type);
		return new ModelAndView(xzzdType, modelMap);
	}
	
	@RequestMapping(value = "/createTable", method = RequestMethod.POST)
	public ModelAndView createTable(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "tabname") String tabname, @RequestParam(value = "familyname") String familyname) throws Exception {
		hbaseService.createTable(tabname, familyname.split(","));
		return new ModelAndView("redirect:/hbase/list");
	}
	
	@RequestMapping(value = "/hbaseImportDetail", method = RequestMethod.POST)
	protected void hbaseImportDetail(String mrOnekey,String dbtype,String connectionurl,String username,String password,String dbName,
			String rdbcolumns,String linenum,String hbasetable,String rowkeyparam,
			String partitioncolumn,String rdbtablename,String familyParms,String familys,String hbasecolumns,String port){
		mrkey = mrOnekey;
		String progress = "UNDEFINED" + "," + "0" + ","+ System.currentTimeMillis();
		mrOneMap.put(mrkey, progress);
		Map<String,Object> parms = hbaseParms(dbtype, connectionurl, username, password, dbName, rdbcolumns, linenum, hbasetable, rowkeyparam, partitioncolumn, rdbtablename,familyParms,familys,hbasecolumns);
		try {
			String newConnectionUrl = "";
			String driver = parms.get("driver").toString();
			if(driver.contains("mysql")) {
				newConnectionUrl = "jdbc:mysql://"+connectionurl+":"+port+"/"+dbName;
			} else {
				newConnectionUrl = "jdbc:oracle:thin:@" + connectionurl + ":" + port + ":" + dbName;
			}
			DataProgressListener listener = new DataProgressListener() {
				@Override
				public void handleEvent(ProgressEvent e) {
					if (e.getSource() instanceof ProgressSource) {
						String status = ((ProgressSource) e.getSource()).getStat();
						double percent = ((ProgressSource) e.getSource()).getPercent();
						System.out.println(status+ "\t" + percent);
						StringBuffer progress = new StringBuffer();
						if(mrOneMap.containsKey(mrkey)){
							progress.append(status+",");
							progress.append(percent);
							if("SUCCEEDED".equals(status)){	
								progress.append(","+System.currentTimeMillis());
							}else{
								progress.append(",0");
							}
							mrOneMap.put(mrkey,progress.toString());
						}
					}
				}
			};
			hbasecolumns = parms.get("hbasecolumns").toString();
			String solrmasterurl = parms.get("solrmasterurl").toString();
			String label = parms.get("label").toString();
			
			hbasetable = parms.get("hbasetable").toString();
			partitioncolumn = parms.get("partitioncolumn").toString();
			
			System.out.println("connectionurl:"+connectionurl);
			System.out.println("dbname:"+dbName);
			System.out.println("driver:"+driver);
			System.out.println("username&passwd=>"+username+":"+password);
			System.out.println("rdbtablename:"+rdbtablename);
			System.out.println("rdbcolumns:"+rdbcolumns);
			System.out.println("linenum:"+Integer.parseInt(linenum));
			System.out.println("partitioncolumn:"+partitioncolumn);
			System.out.println("hbasetable:"+hbasetable);
			System.out.println("hbasecolumns:"+hbasecolumns);
			System.out.println("rowkeyparam:"+rowkeyparam); 
			System.out.println("solrmasterurl:"+solrmasterurl);
			System.out.println("label:"+label);
			
			DataTransporter.transRDB2HBASEWithIndexOnSolr(newConnectionUrl, driver, username, password, rdbtablename, rdbcolumns, partitioncolumn, Integer.parseInt(linenum), hbasetable, hbasecolumns, rowkeyparam, solrmasterurl, label,listener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Monitor data import
	 */
	@RequestMapping(value = "/searchProgress", method = RequestMethod.GET)
	@ResponseBody
	protected JSONObject searchProgress(@RequestParam(value = "mrOnekey") final String mrOnekey) throws Exception {
		Map<String,Object> modelMap= new HashMap<String,Object>();
		if(mrOneMap.containsKey(mrOnekey)){
			String pregress = mrOneMap.get(mrOnekey);
			modelMap.put("contents",pregress);
			System.out.println("######mrOnekey is :" + pregress);
		}else{
			System.out.println("#####mrOnekey is null");
			String progress = "UNDEFINED" + "," + "0" + ","+ System.currentTimeMillis();
			modelMap.put("contents",progress);
		}
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	private Map<String,Object> hbaseParms(String dbtype,String connectionurl,String username,String password,String dbname,
			String rdbcolumns,String linenum,String hbasetable,String rowkeyparam,
			String partitioncolumn,String rdbtablename,String familyParms,String familyArr,String hbasecolumns){
		Map<String,Object> parms = new HashMap<String,Object>();
		String driver = "com.mysql.jdbc.Driver";
		if("oracle".equals(dbtype)){
			driver = "oracle.jdbc.driver.OracleDriver";
		}
		if(StringUtils.isEmpty(partitioncolumn)){
			partitioncolumn = "id";
		}
		if(StringUtils.isEmpty(hbasetable)){
			hbasetable = rdbtablename;
		}
		if(StringUtils.isNotEmpty(familyParms)){
			String hc = "";
			FamilyBean familyBean = gson.fromJson(familyParms, FamilyBean.class);
			List<String> fmailyList = new ArrayList<String>();
			for (FamilyCols familyCols : familyBean.getFamilyCols()) {
				if(StringUtils.isNotEmpty(familyCols.getFamily_td()) && StringUtils.isNotEmpty(familyCols.getFamily_cols_td())){
					fmailyList.add(familyCols.getFamily_td());
					hc = hc + "," + familyCols.getFamily_td()+":"+familyCols.getFamily_cols_td();
				}
			}
			hbaseService.createTable(hbasetable, (fmailyList.toArray(new String[fmailyList.size()])));
			hbasecolumns = hc.substring(1);
		}else{
			hbaseService.createTable(hbasetable, familyArr.split(","));
		}
		
//		partitioncolumn = "id";
		String solrmasterurl = ResourceUtils.getSolrMasterUrl();
		String label = "testNewTable";
		parms.put("hbasecolumns", hbasecolumns);
		parms.put("solrmasterurl", solrmasterurl);
		parms.put("label", label);
		parms.put("driver", driver);
		parms.put("hbasetable", hbasetable);
		parms.put("partitioncolumn", partitioncolumn);
		return parms;
	}
	
	@RequestMapping(value = "/hbaseImport", method = RequestMethod.POST)
	public ModelAndView hbaseImport(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "dbtype") String dbtype, 
			@RequestParam(value = "connectionurl") String connectionurl, 
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,@RequestParam(value = "dbname") String dbname,
			@RequestParam(value = "rdbcolumns") String rdbcolumns,@RequestParam(value = "linenum") String linenum,
			@RequestParam(value = "hbasetable") String hbasetable,@RequestParam(value = "rowkeyparam") String rowkeyparam,
			@RequestParam(value = "partitioncolumn") String partitioncolumn,@RequestParam(value = "rdbtablename") String rdbtablename) throws Exception {
		mrkey = "HBASE_"+System.currentTimeMillis();
		String familyParms = request.getParameter("familyParms");
		String port = "3306";
		hbaseImportDetail(mrkey, dbtype, connectionurl, username, password, dbname, rdbcolumns, linenum, hbasetable, rowkeyparam, partitioncolumn, rdbtablename, familyParms,"","", port);
		return new ModelAndView("redirect:/hbase/list");
	}
	
//	@RequestMapping(value = "/hbaseImport", method = RequestMethod.POST)
//	public ModelAndView hbaseImport(HttpServletRequest request,HttpServletResponse response,
//			@RequestParam(value = "dbtype") String dbtype, 
//			@RequestParam(value = "connectionurl") String connectionurl, 
//			@RequestParam(value = "username") String username,
//			@RequestParam(value = "password") String password,@RequestParam(value = "dbname") String dbname,
//			@RequestParam(value = "rdbcolumns") String rdbcolumns,@RequestParam(value = "linenum") String linenum,
//			@RequestParam(value = "hbasetable") String hbasetable,@RequestParam(value = "rowkeyparam") String rowkeyparam,
//			@RequestParam(value = "partitioncolumn") String partitioncolumn,@RequestParam(value = "rdbtablename") String rdbtablename) throws Exception {
//		
//		String familyParms = request.getParameter("familyParms");
//		FamilyBean familyBean = gson.fromJson(familyParms, FamilyBean.class);
//		String driver = "com.mysql.jdbc.Driver";
//		if("oracle".equals(dbtype)){
//			driver = "oracle.jdbc.driver.OracleDriver";
//		}
//		String hc = "";
//		List<String> fmailyList = new ArrayList<String>();
// 		
//		for (FamilyCols familyCols : familyBean.getFamilyCols()) {
//			if(StringUtils.isNotEmpty(familyCols.getFamily_td()) && StringUtils.isNotEmpty(familyCols.getFamily_cols_td())){
//				fmailyList.add(familyCols.getFamily_td());
//				hc = hc + "," + familyCols.getFamily_td()+":"+familyCols.getFamily_cols_td();
//			}
//		}
//		if(StringUtils.isEmpty(partitioncolumn)){
//			partitioncolumn = "id";
//		}
//		if(StringUtils.isEmpty(hbasetable)){
//			hbasetable = rdbtablename;
//		}
//		hbaseService.createTable(hbasetable, (fmailyList.toArray(new String[fmailyList.size()])));
//		String hbasecolumns = hc.substring(1);
////		partitioncolumn = "id";
//		String solrmasterurl = ResourceUtils.getSolrMasterUrl();
//		String label = "testNewTable";
//		hbaseService.transRDB2HBASEWithIndexOnSolr(connectionurl, dbname, driver, username, password, rdbtablename, rdbcolumns, partitioncolumn, Integer.parseInt(linenum), hbasetable, hbasecolumns, rowkeyparam, solrmasterurl, label);
//		System.out.println("connectionurl:"+connectionurl);
//		System.out.println("dbname:"+dbname);
//		System.out.println("driver:"+driver);
//		System.out.println("username&passwd=>"+username+":"+password);
//		System.out.println("rdbtablename:"+rdbtablename);
//		System.out.println("rdbcolumns:"+rdbcolumns);
//		System.out.println("linenum:"+Integer.parseInt(linenum));
//		System.out.println("partitioncolumn:"+partitioncolumn);
//		System.out.println("hbasetable:"+hbasetable);
//		System.out.println("hbasecolumns:"+hbasecolumns);
//		System.out.println("rowkeyparam:"+rowkeyparam); 
//		System.out.println("solrmasterurl:"+solrmasterurl);
//		System.out.println("label:"+label);
//		return new ModelAndView("redirect:/hbase/list");
//	}
	
	/**
	 * delete
	 * @throws Exception 
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	protected void delete(@RequestParam(value = "tablename") final String tablename,
			@RequestParam(value = "rowkey") final String rowkey) throws Exception {
		EncryptionDecryption des = new EncryptionDecryption("wf");
		hbaseService.delete(tablename, des.hexStr2ByteArr(rowkey));
	}
	
	/**
	 *  update
	 * @throws Exception 
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	protected void update(@RequestParam(value = "tablename") final String tablename,
			@RequestParam(value = "colnames") final String colnames,
			@RequestParam(value = "line") final String line,
			@RequestParam(value = "rowkey") final String rowkey) throws Exception {
		hbaseService.update(tablename, colnames, line, Bytes.toBytes(rowkey));
	}
	
	/**
	 *  Query, enter the range of rowkey and table name, function to return to the range of all rowkey
	 * @throws Exception 
	 */
	@RequestMapping(value = "/searchData", method = RequestMethod.GET)
	@ResponseBody
	protected JSONObject searchData(@RequestParam(value = "tablename") final String tablename,
			@RequestParam(value = "startRowkey") final String startRowkey,
			@RequestParam(value = "endRowkey") final String endRowkey) throws Exception {
		System.out.println(tablename);
		System.out.println(startRowkey);
		System.out.println(endRowkey);
		List<Object> dataList = hbaseService.searchData(tablename, Bytes.toBytes(startRowkey), Bytes.toBytes(endRowkey));
		System.out.println("====================================");
		Map<String,Object> modelMap= new HashMap<String,Object>();
		modelMap.put("contents",dataList);
		JSONObject jo = JSONObject.fromObject(modelMap);
		System.out.println("=========="+jo.toString()+"================");
		return jo;
	}
	
	/**
	 * Query rowkey range of all data
	 */
	@RequestMapping(value = "/searchContents", method = RequestMethod.GET)
	@ResponseBody
	protected JSONObject searchContents(@RequestParam(value = "tablename") final String tablename,
			@RequestParam(value = "startRowkey") final String startRowkey,
			@RequestParam(value = "endRowkey") final String endRowkey) throws Exception {
		List<Object[]> contents = hbaseService.searchContents(tablename, Bytes.toBytes(startRowkey), Bytes.toBytes(endRowkey));
		Map<String,Object> modelMap= new HashMap<String,Object>();
		modelMap.put("contents",contents);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	/**
	 * Query specifying the rowkey corresponding to the data
	 */
	@RequestMapping(value = "/searchContent", method = RequestMethod.GET)
	@ResponseBody
	protected JSONObject searchContent(@RequestParam(value = "tablename") final String tablename,
			@RequestParam(value = "rowkey") final String rowkey) throws Exception {
		EncryptionDecryption des = new EncryptionDecryption("wf");
		Object[] content = hbaseService.searchContent(tablename, des.hexStr2ByteArr(rowkey));
		Map<String,Object> modelMap= new HashMap<String,Object>();
		modelMap.put("contents",content);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
}
