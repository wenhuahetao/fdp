package com.hetao.constroller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.client.DataTransporter;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.fline.hadoop.data.util.DB_ORM;
import com.fline.hadoop.data.util.FileChecker;
import com.fline.hadoop.data.util.RunShellJob;
import com.fline.hadoop.data.util.bigdata.JobInfo_Query;
import com.fline.hadoop.data.util.rdb.MysqlDB;
import com.hetao.bean.CheckFileBean;
import com.hetao.bean.DbInfo;
import com.hetao.bean.FileInfo;
import com.hetao.bean.TableInfo;
import com.hetao.page.Pager;
import com.hetao.service.HbaseService;
import com.hetao.service.HdfsService;
import com.hetao.util.CheckFileEnum;
import com.hetao.util.CsvTypeEnum;
import com.hetao.util.DateUtil;
import com.hetao.util.EncryptionDecryption;
import com.hetao.util.FileOperate;
import com.hetao.util.ResourceUtils;
import com.hetao.util.TaskOperator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


@Controller
public class CollectionManagerConstroller {

	private static Logger logger = Logger.getLogger(CollectionManagerConstroller.class);  
	
	@Autowired private HdfsService hdfsService;
	@Autowired private HbaseService hbaseService;
	final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static Map<String,Integer> mrMap = new HashMap<String,Integer>();
	static Map<String,String> mrOneMap = new HashMap<String,String>();
	static String mrkey = ""; 
	
	/**
	 * index
	 */
	@RequestMapping(value = {"/","/collection/index"}, method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response, ModelMap modelMap,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "rows", required = false, defaultValue = "10") Integer rows) throws Exception {
		logger.info("begin");
		System.out.println("*********************");
		return new ModelAndView("/fline/collection/index", modelMap);
	}
	
	/**
	 * structureView
	 */
	@RequestMapping(value ="/collection/structureView", method = RequestMethod.GET)
	public ModelAndView structureView() throws Exception {
		return new ModelAndView("fline/collection/structure");
	}
	
	@RequestMapping(value = "/collection/queryList", method = RequestMethod.GET)
	public ModelAndView queryList(HttpServletRequest request,HttpServletResponse response, ModelMap modelMap,
			@RequestParam(value = "tableName", required = true) String tableName,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "rows", required = false, defaultValue = "8") Integer rows,
			@RequestParam(value = "dbtype", required = true) String dbtype,
			@RequestParam(value = "dbname", required = true) String dbname,
			@RequestParam(value = "connectionurl", required = true) String connectionurl,
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password
		) throws Exception {
		
		String tablecount=request.getParameter("tablecount");
		String tabledate_c=request.getParameter("tabledate_c");
		String alltime=request.getParameter("alltime");
		List <String> tablehead=new ArrayList<String>();
		tablehead.add(tablecount);
		tablehead.add(tabledate_c);
		tablehead.add(alltime);
		DbInfo dbInfo = getDbInfo(dbtype,dbname,connectionurl,username,password);
		MysqlDB rdb = new MysqlDB(connectionurl, 3306, dbname, username,password);
		String[] tables = rdb.getTables();
//		for(int i=0;i<tables.length;i++){
//			tables[i]= tables[i];
//		}
		List<String> tablenames= Arrays.asList(tables);
		List <String> deletetable=new ArrayList<String>();
		// table list
		//  datas : default of table
		List<String> tableList = hbaseService.listTable();
		for(String f:tablenames){
			if(!tableList.contains(f)){
				deletetable.add(f);
			}
		}
		tablenames.removeAll(deletetable);
		if(tablenames.size()>0){
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
			modelMap.put("tableList", tablenames);
			modelMap.put("total", list.size());
			modelMap.put("countPage", countPage);
			modelMap.put("pageIndex", page);
			modelMap.put("tablename", tableName);
			modelMap.put("familyColums", tableInfo.getFamilyColums());
			modelMap.put("hbaselist", pager.getPagerList());
			modelMap.put("dbInfo", dbInfo);		
			modelMap.put("tablehead",tablehead);
		}
		return new ModelAndView("fline/collection/structurefinsh", modelMap);
	}
	
	/**
	 * According to the data source and tableName, import table data to HBase
	 */
	@RequestMapping(value ="/collection/importRdbTable2Hbase", method = RequestMethod.POST)
	public void importRdbTable2Hbase(String dbtype,String dbname,String connectionurl,
			String username,String password,String tablename,String mrOnekey){
		try {
			mrkey = mrOnekey;
			String progress = "UNDEFINED" + "," + "0" + ","+ System.currentTimeMillis();
			mrOneMap.put(mrkey, progress);
			rdbDataImport2Hbase(tablename,getDbInfo(dbtype,dbname,connectionurl,username,password));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * According to the data source and tableName, import table data to HBase
	 */
	@RequestMapping(value ="/collection/importMultiTable2Hbase", method = RequestMethod.POST)
	public void importMultiTable2Hbase(String dbtype,String dbname,String connectionurl,
			String username,String password,String tableName,String mrOnekey,String familys,String fileds,String fmField){
		try {
			mrkey = mrOnekey;
			String progress = "UNDEFINED" + "," + "0" + ","+ System.currentTimeMillis();
			mrOneMap.put(mrkey, progress);
			
			DbInfo dbInfo = getDbInfo(dbtype,dbname,connectionurl,username,password);

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
			
				DB_ORM rdm = hbaseService.getDB(dbInfo);
				String url = rdm.getUrl();

				String[] familyArr = familys.split(",");
				String hbaseTableName = "";
				for(int j=0;j<familyArr.length;j++){
					if(!StringUtils.isEmpty(familyArr[j])){
						hbaseTableName += familyArr[j]+ "_";
					}
				}
				String[] filedArr = fileds.split(",");
				
				System.out.println("connectionurl : " + url + "\r" + "driver: " + rdm.getDriver() +  "\r" + "username:" + dbInfo.getUsername());
				System.out.println("password : " + dbInfo.getPasswd() + "\r" + "rdbcolumns: " + tableName +  "\r" + "rdbcolumns:" + fileds);
				System.out.println("partitioncolumn : " + filedArr[0] + "\r" + "linenum:1 " +  "\r" + "hbaseTableName: " + hbaseTableName);
				System.out.println("hbasecolumns : " + fmField + "\r" + "rowkeyparam:timestamp,0,radom16 ");
				hbaseService.createTable(hbaseTableName,familyArr);
				DataTransporter
				.transRDB2HBASEWithIndexOnSolr(
						url,
						rdm.getDriver(),
						dbInfo.getUsername(),
						dbInfo.getPasswd(),
						tableName,
						fileds,
						filedArr[0],
						1,
						hbaseTableName,
						fmField,
						"timestamp,0,radom16", ResourceUtils.getSolrMasterUrl(),
						"testNewTable", listener);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addMultiTable(String tableName,DbInfo dbInfo) throws Exception{
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
		DB_ORM rdm = hbaseService.getDB(dbInfo);
		String url = rdm.getUrl();
		List<String[]> fields = hbaseService.getDataBaseField(rdm, tableName);
		System.out.println("fields:"+fields);
		String filed = "";
		String fm_filed = "";
		for(int j=0;j<fields.size()-1;j++){
			String [] f=fields.get(j);
			filed+=f[0]+",";
			fm_filed+="fm:"+f[0]+",";
		}
		filed+=fields.get(fields.size()-1)[0];
		fm_filed+="fm:"+fields.get(fields.size()-1)[0]+"";
		String[] fm = new String[1];
		fm [0] = "fm"; 
		hbaseService.createTable(tableName,fm);
		System.out.println("url:"+url);
		System.out.println("filed:"+filed);
		System.out.println("filed:"+fields.get(0)[0]);
		System.out.println("fm_filed:"+fm_filed);
		DataTransporter
		.transRDB2HBASEWithIndexOnSolr(
				url,
				rdm.getDriver(),
				dbInfo.getUsername(),
				dbInfo.getPasswd(),
				tableName,
				filed,
				fields.get(0)[0],
				-1,
				tableName,
				fm_filed,
				"timestamp,0,radom16", ResourceUtils.getSolrMasterUrl(),
				"testNewTable", listener);
	}

	/**
	 * According to the data source, Batch import relational database to HBase
	 */
	@RequestMapping(value ="/collection/structurefinshView", method = RequestMethod.POST)
	public ModelAndView structurefinshView(HttpServletRequest request,HttpServletResponse response, ModelMap modelMap,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "rows", required = false, defaultValue = "8") Integer rows,
			@RequestParam(value = "dbtype", required = true) String dbtype,
			@RequestParam(value = "dbname", required = true) String dbname,
			@RequestParam(value = "connectionurl", required = true) String connectionurl,
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password) throws Exception {
		long currentBegin = System.currentTimeMillis();
		DbInfo dbInfo = getDbInfo(dbtype,dbname,connectionurl,username,password);
		MysqlDB rdb = new MysqlDB(connectionurl, 3306, dbname, username,password);
		String[] tables = rdb.getTables();
	
		List<String> tablename_old = Arrays.asList(tables);
		List <String> deletetable = new ArrayList<String>();
		List<String> tablehead = rdbDataImport2Hbase(tablename_old,dbInfo);
	
		long currentEnd = System.currentTimeMillis();
		long totalTime =  (currentEnd - currentBegin)/1000/60;
		tablehead.add(totalTime+"");
		List<String> tableList = hbaseService.listTable();
//		for(int i=0;i<tables.length;i++){
//			tables[i]="hb_"+tables[i];
//		}
		List<String> tablename= Arrays.asList(tables);
		for(String f:tablename){
			if(!tableList.contains(f)){
				deletetable.add(f);
			}
		}
		tablename.removeAll(deletetable);
		if(tablename.size()>0){
		//  datas : default of table
			TableInfo tableInfo = hbaseService.listDataByTable(tablename.get(0));
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
			modelMap.put("tablename", tablename.get(0));
			modelMap.put("tableList", tablename);
			modelMap.put("tablehead", tablehead);
			modelMap.put("familyColums", tableInfo.getFamilyColums());
			modelMap.put("hbaselist", pager.getPagerList());
			modelMap.put("dbInfo", dbInfo);
		}
		return new ModelAndView("fline/collection/structurefinsh", modelMap);
	}

	/**
	 * import one rdb table
	 * @param tableName
	 * @param dbInfo
	 * @throws Exception
	 */
	public void rdbDataImport2Hbase(String tableName,DbInfo dbInfo) throws Exception {
		addHbase4One(tableName,dbInfo);
	}
	
	/**
	 * import many rdb tables
	 * @param tablenames
	 * @param dbInfo
	 * @return
	 * @throws Exception
	 */
	public List<String> rdbDataImport2Hbase (List<String> tablenames,DbInfo dbInfo) throws Exception {
		mrMap.clear();
		mrkey = "HBASE_"+System.currentTimeMillis();
		mrMap.put(mrkey, 0);
		List <String> information = new ArrayList<String>();
		List<String> tables = tablenames;
		int countData = 0;
		for (int i = 0; i < tables.size(); i++) {
			final String tableName = tables.get(i);
			countData += new MysqlDB(dbInfo.getUrl(),3306,dbInfo.getDbname(),dbInfo.getUsername(),dbInfo.getPasswd()).countTableSize(tableName);
			addHbase(tableName,dbInfo);
		}
		int tableSize = tables.size();
		information.add(tableSize+"");
		information.add(countData+"");
		boolean flag = true;
		while(flag){
			if(mrMap.get(mrkey)<tableSize){
				Thread.sleep(10000);
			}else{
				flag = false;
			}
		}
		return information;
	}
	
	private void addHbase4One(String tableName,DbInfo dbInfo) throws Exception{
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
		DB_ORM rdm = hbaseService.getDB(dbInfo);
		String url = rdm.getUrl();
		List<String[]> fields = hbaseService.getDataBaseField(rdm, tableName);
		System.out.println("fields:"+fields);
		String filed = "";
		String fm_filed = "";
		for(int j=0;j<fields.size()-1;j++){
			String [] f=fields.get(j);
			filed+=f[0]+",";
			fm_filed+="fm:"+f[0]+",";
		}
		filed+=fields.get(fields.size()-1)[0];
		fm_filed+="fm:"+fields.get(fields.size()-1)[0]+"";
		String[] fm = new String[1];
		fm [0] = "fm"; 
		hbaseService.createTable(tableName,fm);
		System.out.println("url:"+url);
		System.out.println("filed:"+filed);
		System.out.println("filed:"+fields.get(0)[0]);
		System.out.println("fm_filed:"+fm_filed);
		DataTransporter
		.transRDB2HBASEWithIndexOnSolr(
				url,
				rdm.getDriver(),
				dbInfo.getUsername(),
				dbInfo.getPasswd(),
				tableName,
				filed,
				fields.get(0)[0],
				-1,
				tableName,
				fm_filed,
				"timestamp,0,radom16", ResourceUtils.getSolrMasterUrl(),
				"testNewTable", listener);
	}
	
	private void addHbase(String tableName,DbInfo dbInfo) throws Exception{
		DataProgressListener listener = new DataProgressListener() {
		@Override
		public void handleEvent(ProgressEvent e) {
				if (e.getSource() instanceof ProgressSource) {
					String status = ((ProgressSource) e.getSource()).getStat();
					double percent = ((ProgressSource) e.getSource()).getPercent();
					System.out.println(status+ "\t" + percent);
					if("SUCCEEDED".equals(status)){						
						int mrVal = mrMap.get(mrkey);
						mrMap.put(mrkey, mrVal+1);
					}
				}
			}
		};
		DB_ORM rdm = hbaseService.getDB(dbInfo);
		String url = rdm.getUrl();
		List<String[]> fields = hbaseService.getDataBaseField(rdm, tableName);
		String filed = "";
		String fm_filed = "";
		for(int j=0;j<fields.size()-1;j++){
			String [] f=fields.get(j);
			filed+=f[0]+",";
			fm_filed+="fm:"+f[0]+",";
		}
		filed+=fields.get(fields.size()-1)[0];
		fm_filed+="fm:"+fields.get(fields.size()-1)[0]+"";
		String[] fm = new String[1];
		fm [0] = "fm"; 
		hbaseService.createTable(tableName,fm);
		System.out.println("url:"+url);
		System.out.println("filed:"+filed);
		System.out.println("filed:"+fields.get(0)[0]);
		System.out.println("fm_filed:"+fm_filed);
//		DataTransporter.transRDB2HBASEWithIndexOnSolr(connectionurl, driver, username, password, tablename, rdbcolumns, partitioncolumn, linenum, hbasetable, hbasecolumns, rowkeyparam, solrmasterurl, label, listener)
		DataTransporter
		.transRDB2HBASEWithIndexOnSolr(
				url,
				rdm.getDriver(),
				dbInfo.getUsername(),
				dbInfo.getPasswd(),
				tableName,
				//"id,serial_number,batch_number,occur_date,biz_type,income,pay",
				filed,
				fields.get(0)[0],
				-1,
				tableName,
				//"fm:id,fm:serial_number,fm:batch_number,fm:occur_date,fm:biz_type,fm:income,fm:pay",
				fm_filed,
				"timestamp,0,radom16", ResourceUtils.getSolrMasterUrl(),
				"testNewTable", listener);
	}
	
	private DbInfo getDbInfo(String dbtype,String dbname,String connectionurl,String username,String password){
		DbInfo dbInfo= new DbInfo();
		dbInfo.setDbname(dbname);
		dbInfo.setDbtype(dbtype);
		dbInfo.setPasswd(password);
		dbInfo.setUrl(connectionurl);
		dbInfo.setUsername(username);
		return dbInfo;
	}
	
	/**
	 * unstructureView
	 */
	@RequestMapping(value ="/collection/unstructureView", method = RequestMethod.GET)
	public ModelAndView unstructureView() throws Exception {
		return new ModelAndView("fline/collection/unstructured");
	}
	
	/**
	 * unstructureCheckView
	 */
	@RequestMapping(value ="/collection/unstructureCheckView", method = RequestMethod.GET)
	public ModelAndView unstructureCheckView(ModelMap modelMap,@RequestParam(value = "type") String type) throws Exception {
		modelMap.put("type", type);
		return new ModelAndView("fline/collection/unstructured");
	}
	
	/**
	 * ajaxgettable
	 * @throws Exception
	 */
	@RequestMapping(value = "/collection/ajaxgettable", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject ajaxgettable(HttpServletRequest request)throws Exception{
		String tablename=request.getParameter("tablename");
		String dbtype=request.getParameter("dbtype");
		String dbname=request.getParameter("dbname");
		String connectionurl=request.getParameter("connectionurl");
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		MysqlDB rdb = new MysqlDB(connectionurl, 3306, dbname, username,password);
		String[] tables = rdb.getTables();
		for(int i=0;i<tables.length;i++){
			tables[i]=tables[i];
		}
		List <String> tablenames_data=Arrays.asList(tables);
		List <String> tablenames=new ArrayList<String>();
		List <String> deletetable=new ArrayList<String>();
		// table list
		List<String> tableList = hbaseService.listTable();
		for(String t:tablenames_data){
			if(!tableList.contains(t)){
				deletetable.add(t);
			}
		}
		tablenames_data.removeAll(deletetable);
		for(String f:tablenames_data){
			if(f.indexOf(tablename)>-1){
				tablenames.add(f);
			}
		}
		Map<String,Object> modelMap= new HashMap<String,Object>();
		modelMap.put("tablenames",tablenames);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	/**
	 * unstructurefinshView
	 */
	@RequestMapping(value ="/collection/unstructurefinshView", method = RequestMethod.GET)
	public ModelAndView unstructurefinshView( ModelMap modelMap,@RequestParam(value = "successedCount") String successedCount,
			@RequestParam(value = "total") String total) throws Exception {
		modelMap.put("total", total);
		modelMap.put("successedCount", successedCount);
		return new ModelAndView("fline/collection/unstructurefinsh",modelMap);
	}
	
	
	/**
	 * Remote server upload attachments
	 */
	@RequestMapping(value = "/collection/uploadRemoteFile", method = RequestMethod.POST)
	public ModelAndView uploadRemoteFile(HttpServletRequest request,
			@RequestParam(value = "remoteip") String remoteip,@RequestParam(value = "remotefilepath") String remotefilepath,
			@RequestParam(value = "remoteusername") String remoteusername,@RequestParam(value = "remotepassword") String remotepassword) throws Exception {
		DataProgressListener listener = new DataProgressListener() {
			@Override
			public void handleEvent(ProgressEvent e) {
				if (e.getSource() instanceof ProgressSource) {
					System.out.println(((ProgressSource) e.getSource())
							.getStat()
							+ "\t"
							+ ((ProgressSource) e.getSource()).getPercent());
					
				}
			}
		};
		//"/root/nohup.out",
		// "121.43.146.160", "root", "Fline2015", "/user/zhongliang/",
		// true, "zhongliang-solr", "solrTest",
		// "http://112.33.1.202:8983/solr/", FileChecker.SMARTCN_CHECKER
		String hdfspath = "/user/public/";
		hdfspath += sdf.format(new Date());
		String results = "";
		hdfsService.addDir(hdfspath);
		String uploadusername = "zhongliang-solr";
		String label = "solrTest";
		String solrmasterurl = ResourceUtils.getSolrMasterUrl();
		int checkCode = FileChecker.SMARTCN_CHECKER;
		
		System.out.println("****************************************************");
		System.out.println("remotefilepath:"+remotefilepath);
		System.out.println("remoteip:"+remoteip);
		System.out.println("remoteusername:"+remoteusername);
		System.out.println("remotepassword:"+remotepassword);
		System.out.println("hdfspath:"+hdfspath);
		System.out.println("uploadusername:"+uploadusername);
		System.out.println("label:"+label);
		System.out.println("solrmasterurl:"+solrmasterurl);
		System.out.println("checkCode:"+checkCode);
		System.out.println("****************************************************");
		
		Map<String,Object> modelMap= new HashMap<String,Object>();
		results = DataTransporter.uploadRomoteFile2HDFSWithIndexOnSolr(remotefilepath, remoteip, remoteusername, remotepassword, hdfspath, true, uploadusername, label, solrmasterurl, checkCode, listener);
//		results = "successed/total=2/3";
		String[] resultArr = results.split("/");
		if(resultArr!=null && resultArr.length==3){
			modelMap.put("status", resultArr[0]);
			modelMap.put("successedCount", resultArr[1].split("=")[1]);
			modelMap.put("total", resultArr[2]);
		}
		String parms = "?&type=0&successedCount="+modelMap.get("successedCount")+"&current_name_dir="+hdfspath;
		return new ModelAndView("redirect:/jump"+parms);
	}

	/**
	 * jump to
	 */
	@RequestMapping(value = "/collection/jump", method = RequestMethod.GET)
	public ModelAndView jump(HttpServletRequest request,ModelMap modelMap,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "rows", required = false, defaultValue = "10") Integer rows) throws Exception {
		String dirname = request.getParameter("current_name_dir");
		String successedCount = request.getParameter("successedCount");
		String[] dirs= ResourceUtils.getDirs("dir");
		if(!StringUtils.isEmpty(dirname)){
			List<FileInfo> list = hdfsService.selectFileInfos(dirname);
			if(list!=null){
				Pager pager = new Pager(page,rows,list);
				int countPage = list.size() % rows == 0  ? (list.size() / rows) : (list.size() / rows + 1);
				modelMap.put("fileinfos", pager.getPagerList());
				modelMap.put("countPage", countPage);
				modelMap.put("pageIndex", page);
				modelMap.put("total", list.size());
			}
		}
		dirs = dirname.substring(1, dirname.length()).replace("/", ",").split(",");
		modelMap.put("dir", dirs);
		modelMap.put("dirname", dirname);
		modelMap.put("successedCount", successedCount);
		return new ModelAndView("fline/collection/unstructurefinsh",modelMap);
	}
	
	@RequestMapping(value="/collection/uploadFileGo", method = RequestMethod.POST)
	 public ModelAndView uploadFileGo(@RequestParam(value = "checkCode") int checkCode,
			 @RequestParam(value = "hdfsPath") String hdfsPath,
			 @RequestParam(value = "path") String path,
			 @RequestParam(value = "fileName") String fileName,
			 @RequestParam(value = "lines") String lines,
			 @RequestParam(value = "errorOn") String errorOn,ModelMap modelMap) {
		try {
			ResourceUtils.getSolrInstance().deleteDoc(hdfsPath + "/" + fileName);
			if("true".equals(errorOn)){
				String[] errorLine = lines.split(",");
				// 删除错误行
				hdfsService.writeUTFToFile(path+"/"+fileName,path+"/"+fileName+".temp", errorLine);
				hdfsService.upload(path + "/" + fileName+".temp",hdfsPath + "/" + fileName, checkCode);
			}else{
				hdfsService.upload(path + "/" + fileName,hdfsPath + "/" + fileName, checkCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String parms = "?successedCount=1&current_name_dir=" + hdfsPath;
		return new ModelAndView("redirect:/jump" + parms);
	 }
	
	/**
	  * upload common file
	  */
	 @RequestMapping(value="/collection/uploadFile", method = RequestMethod.POST)
	 public ModelAndView uploadFile(@RequestParam(value = "checkCode") int checkCode,
			 @RequestParam(value = "importfilename", required = false) MultipartFile importfilename, HttpServletRequest request, ModelMap modelMap) {
		 	 String hdfsPath = "/user/public/"+sdf.format(new Date());
	        try {
		        String path = request.getSession().getServletContext().getRealPath("upload");
		        String fileName = importfilename.getOriginalFilename();
		        hdfsService.addDir(hdfsPath);
		        boolean isText = fileName.contains("txt");
		        // utf-8 write into server
		        if(isText){
		        	File targetFile = new File(path,fileName);
				    targetFile.createNewFile();
		        	InputStream inputStream = importfilename.getInputStream();
		        	hdfsService.writeUTFToFile(path+"/"+fileName,inputStream);
		        }else{
		        	File targetFile = new File(path,fileName);
		        	importfilename.transferTo(targetFile);
		        }
		        
		        //文件校验 
		        String checkContent = FileChecker.checkFile(path+"/"+fileName,checkCode);
		        if(checkContent!=null && !"".equals(checkContent)){
						String[] checkFileLines = checkContent.split("\n");
						List<CheckFileBean> checkFileBeanList = new ArrayList<CheckFileBean>();
						String lines = "";
						for (String checkFileLine : checkFileLines) {
							CheckFileBean checkFileBean = new CheckFileBean();
							String[] checkFiles = checkFileLine.split("@");
							if(checkFiles.length>0){
								checkFileBean.setLine(checkFiles[0]);
							}
							if(checkFiles.length>1){
								checkFileBean.setType(checkFiles[1].toUpperCase());
							}
							if(checkFiles.length>2){
								checkFileBean.setContent(checkFiles[2]);
							}
							
							/**
							 * 2@typeError@1@2@1 第2行第1列是整型 与第1列的时间类型不一致
							 */
							if(CheckFileEnum.TYPEERROR.getCode().equals(checkFileBean.getType())){
								StringBuffer checkBuff = new StringBuffer();
								checkBuff.append("第<font color=blue>"+checkFiles[2]+"列</font>是");
								checkBuff.append("<strong>"+returnTypeStr(checkFiles[4])+"</strong>，");
								checkBuff.append("<font color=red>第"+checkFiles[0]+"行该列");
								checkBuff.append("类型错误。</font>");
								checkFileBean.setCheckInfo(checkBuff.toString());
								lines = lines  + "," + checkFiles[0];
							}else if(CheckFileEnum.EMPTYCSV.getCode().equals(checkFileBean.getType()) || CheckFileEnum.EMPTYLINE.getCode().equals(checkFileBean.getType())){
								StringBuffer checkBuff = new StringBuffer();
								checkBuff.append("第"+checkFiles[0]+"行");
								checkBuff.append("有");
								checkBuff.append(CheckFileEnum.valueOf(checkFiles[1].toUpperCase()).getText());
								checkFileBean.setCheckInfo(checkBuff.toString());
								lines = lines  + "," + checkFiles[0];
							}else if(CheckFileEnum.REPEAT.getCode().equals(checkFileBean.getType())){
								/**
								 * linenum@repeat@linenum
								 * 3@repeat@1  表示 第3行和第1行重复
								 */
								StringBuffer checkBuff = new StringBuffer();
								checkBuff.append("第"+checkFiles[0]+"行和"+"第"+checkFiles[2]+"行内容重复");
								checkFileBean.setCheckInfo(checkBuff.toString());
								lines = lines  + "," + checkFiles[0];
							}else if(CheckFileEnum.REPEATCHAR.getCode().equals(checkFileBean.getType())){
								/**
								 * charnum@repeatchar@content
								 * 13@repeatchar@1  第13个字符的位置有重复的"1"
								 */
								StringBuffer checkBuff = new StringBuffer();
								checkBuff.append("第"+checkFiles[0]+"个字符的位置有重复的‘"+checkFiles[2]+"’");
								checkFileBean.setCheckInfo(checkBuff.toString());
							}
							
							checkFileBeanList.add(checkFileBean);
							
						}
						if(!"".equals(lines) && lines.length()>1){
							lines = lines.substring(1);
						}
						modelMap.put("checkFileBeanList", checkFileBeanList);
						modelMap.put("hdfsPath", hdfsPath);
						modelMap.put("path", path);
						modelMap.put("checkCode", checkCode);
						modelMap.put("fileName", fileName);
						modelMap.put("lines", lines);
						if(checkCode==1){
							 String dirname = hdfsPath;
							 String[] dirs = dirname.substring(1, dirname.length()).replace("/", ",").split(",");
							 modelMap.put("dir", dirs);
							 ResourceUtils.getSolrInstance().deleteDoc(hdfsPath+"/"+fileName);
					         hdfsService.upload(path+"/"+fileName,hdfsPath+"/"+fileName,checkCode);
						}
						return new ModelAndView("fline/collection/unstructureFileCheck",modelMap);
		        }
		        ResourceUtils.getSolrInstance().deleteDoc(hdfsPath+"/"+fileName);
	        	hdfsService.upload(path+"/"+fileName,hdfsPath+"/"+fileName,checkCode);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        String parms = "?successedCount=1&type=1&current_name_dir="+hdfsPath;
			return new ModelAndView("redirect:/jump"+parms);
	}
	
	/**
	 * Monitor data import
	 */
	@RequestMapping(value = "/collection/searchProgress", method = RequestMethod.GET)
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
	
	/**
	 * searchTaskCounter
	 */
	@RequestMapping(value = "/collection/searchTaskCounter", method = RequestMethod.GET)
	@ResponseBody
	protected String searchTaskCounter(String applicationId) throws Exception {
		return JobInfo_Query.getDefaultInstance().queryUsedSource(applicationId);
	}
	
	/**
	 * searchCurrentDayMR
	 */
	@RequestMapping(value = "/collection/searchCurrentDayMR", method = RequestMethod.GET)
	@ResponseBody
	protected String searchCurrentDayMR(String time) throws Exception {
		Date date= new Date(Long.parseLong(time));
        String currentDate = sdf.format(date);
		List<String> readFile = JobInfo_Query.readFile(JobInfo_Query.MR_TODAY_TXT);
		for (String line : readFile) {
			String[] lineArr = line.split(":");
			if(currentDate.equals(lineArr[0])){
				return line;
			}
		}
		return "";
	}
	
	/**
	 * listJobIds
	 */
	@RequestMapping(value = "/collection/listJobIds", method = RequestMethod.GET)
	@ResponseBody
	protected String listJobIds() throws Exception {
		Map<String, Map<String, Object>> queryStateMap = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> jobsMap = TaskOperator.jobsMap;
		String mapInfo = JSONObject.fromObject(jobsMap).toString();
		
		if(mapInfo==null || "".equals(mapInfo) || "{}".equals(mapInfo)){
			mapInfo = TaskOperator.searchJobs(ResourceUtils.getJobUrl(), ResourceUtils.getProjectUrl());
		}
		for (String key : TaskOperator.jobsMap.keySet()) {
			String queryStateInfo = JobInfo_Query.getDefaultInstance().queryStateInfo("job_" + key);
			Map<String, Object> jobMap = TaskOperator.jobsMap.get(key);
			jobMap.put("queryState", queryStateInfo);
			queryStateMap.put(key, jobMap);
		}
		String queryStateInfo = JSONObject.fromObject(queryStateMap).toString();
		return queryStateInfo;
	}
	
	/**
	 * searchCurrentMonthMR
	 */
	@RequestMapping(value = "/collection/searchCurrentMonthMR", method = RequestMethod.GET)
	@ResponseBody
	protected String searchCurrentMonthMR(String currentMonthDate) throws Exception {
		List<String> readFile = JobInfo_Query.readFile(JobInfo_Query.MR_TODAY_TXT);
		List<String> monthCPUMr = new ArrayList<String>();
		List<String> monthMemMr = new ArrayList<String>();
		Map<String,Object> modelMap= new HashMap<String,Object>();
		for (String line : readFile) {
			String[] lineArr = line.split(":");
			String month_date = DateUtil.StringToFormat(lineArr[0], DateUtil.MONTH_MODEL);
//			long curentTime = sdf.parse(sdf.format(new Date())).getTime();
//			long tempTime = sdf.parse(lineArr[0]).getTime();
			if(currentMonthDate.equals(month_date)){
				monthCPUMr.add(lineArr[0] + ":" + lineArr[1].split(",")[0]);
				monthMemMr.add(lineArr[0] + ":" + lineArr[1].split(",")[1]);
			}
		}
		modelMap.put("CPU", monthCPUMr);
		modelMap.put("MEM", monthMemMr);
		return JSONObject.fromObject(modelMap).toString();
	}
	
	/**
	 * writeHistory2File
	 */
	@RequestMapping(value = "/collection/writeHistory2File", method = RequestMethod.GET)
	@ResponseBody
	protected void writeHistory2File(String param) throws Exception {
		JobInfo_Query.getDefaultInstance().writeHistory2File(param);
	}
	
	/**
	 * searchCurrentMonthDisk
	 */
	@RequestMapping(value = "/collection/searchCurrentMonthDisk", method = RequestMethod.GET)
	@ResponseBody
	protected String searchCurrentMonthDisk(String currentMonthDate) throws Exception {
		List<String> readFile = JobInfo_Query.readFile(JobInfo_Query.DISK_TODAY_TXT);
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < readFile.size(); i++) {
			String[] lineArr = readFile.get(i).split(",");
			String month_date = DateUtil.StringToFormat(lineArr[0], DateUtil.MONTH_MODEL);
//			long curentTime = sdf.parse(sdf.format(new Date())).getTime();
//			long tempTime = sdf.parse(lineArr[0]).getTime();
			if(currentMonthDate.equals(month_date)){
				lines.add(readFile.get(i));
			}
		}
		return JSONArray.fromObject(lines).toString();
	}
	
	/**
	 * searchCurrentDayDisk
	 */
	@RequestMapping(value = "/collection/searchCurrentDayDisk", method = RequestMethod.GET)
	@ResponseBody
	protected String searchCurrentDayDisk(String time) throws Exception {
		Date date= new Date(Long.parseLong(time));
        String currentDate = sdf.format(date);
		List<String> readFile = JobInfo_Query.readFile(JobInfo_Query.DISK_TODAY_TXT);
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < readFile.size(); i++) {
			String[] lineArr = readFile.get(i).split(",");
			if(currentDate.equals(lineArr[0])){
				if(i>0){
					lines.add(readFile.get(i-1));
				}
				lines.add(readFile.get(i));
				return JSONArray.fromObject(lines).toString();
			}
		}
		return "";
	}
	
	/**
	 * queryTaskMap
	 */
	@RequestMapping(value = "/collection/queryTaskMap", method = RequestMethod.GET)
	@ResponseBody
	protected String queryTaskMap(String excuParma,String jobId) throws Exception {
		Map<String, Map<String, Object>> jobsMap = TaskOperator.jobsMap;
		for (String key : jobsMap.keySet()) {
			if(jobId.equals(key)){
				if(jobsMap.get(key).get(excuParma) == null){
					return "";
				}
				return jobsMap.get(key).get(excuParma).toString();
			}
		}
		System.out.println("*******url: " + ResourceUtils.getJobUrl() + "\r" + ResourceUtils.getProjectUrl());
		TaskOperator.searchJobs(ResourceUtils.getJobUrl(), ResourceUtils.getProjectUrl());
		jobsMap = TaskOperator.jobsMap;
		for (String key : jobsMap.keySet()) {
			if(jobId.equals(key)){
				if(jobsMap.get(key).get(excuParma) == null){
					return "";
				}
				return jobsMap.get(key).get(excuParma).toString();
			}
		}
		return "no result";
	}
	
	/**
	 * runCmdShellJob
	 */
	@RequestMapping(value = "/collection/runCmdShellJob", method = RequestMethod.GET)
	@ResponseBody
	protected String runCmdShellJob(String excuParma) throws Exception {
		EncryptionDecryption des = new EncryptionDecryption("wf");
		System.out.println("**********************excuParma: " + des.decrypt(excuParma));
		return RunShellJob.runJob(des.decrypt(excuParma));
	}
	
	public static boolean uploadMRFile(String webfilePath,String localpath) {
		try {
			Client client = new Client();
			WebResource resource = client.resource(webfilePath);
			byte[] readFileToByteArray = FileUtils.readFileToByteArray(new File(localpath));
			resource.put(String.class, readFileToByteArray);
		} catch (UniformInterfaceException e) {
			e.printStackTrace();
		} catch (ClientHandlerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * runMrWithMkDir
	 */
	@RequestMapping(value = "/collection/runMrWithMkDir", method = RequestMethod.POST)
	@ResponseBody
	protected String runMrWithMkDir() throws Exception {
			String tmp = "/tmp/fdp_shell_execute/" + System.currentTimeMillis();
			String webfilePath =  ResourceUtils.getImageWebUrl() + tmp;
			if(FileOperate.mkdir(webfilePath)){
				return tmp;
			}
			return "error";
	}
	
	/**
	 * runMrShellJob
	 */
	@RequestMapping(value = "/collection/runMrShellJob", method = RequestMethod.POST)
	@ResponseBody
	protected String runMrShellJob(String runDir,String className,String mrkey, String excuParma) throws Exception {
			final StringBuffer result = new StringBuffer();
			final String logPath = "/tmp/fdp/mapreduce."+mrkey+".log";
			String buildDir =  ResourceUtils.getImageWebUrl() + runDir;
			String filePath = buildDir + "/" + className + ".java";
			String runShellInfo = RunShellJob.executeMapReduceTask(filePath, className, buildDir,logPath,excuParma);
			System.out.println("runShellInfo*************:" + runShellInfo);
			System.out.println("*****filePath:"+ filePath + "\r *****buildDir:" + buildDir + 
					"\r *****excuParma:" + excuParma + "***********logPath:" + logPath);
		
			if(runShellInfo == null || "".equals(runShellInfo)){
				return "failed";
			}
			if(runShellInfo.contains("failed")){
				return runShellInfo;
			}
			long start = System.currentTimeMillis();
			long interval = 0;
			try {
				while (interval <= 30*1000) {
					interval = System.currentTimeMillis() - start;
					String readResult = readLog(logPath);
					System.out.println("*********readResult:" + readResult);
					if(readResult == null){
						Thread.sleep(5000);
						if(interval >= 30*1000){
							result.setLength(0);
							result.append("sumbit is ok");
						}
					}else if("Exception".equals(readResult)){
						return readExceptionLog(logPath);
					}else{
						result.setLength(0);
						result.append(readResult);
						interval = 70*1000;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result.toString();
	}
	
	
	private String readExceptionLog(String logPath) {
		File file = new File(logPath);
		BufferedReader bufReader = null;
		StringBuffer exceptionBuff = new StringBuffer();
		try {
			bufReader = new BufferedReader(new FileReader(file));
			String temp = null;
			while ((temp = bufReader.readLine()) != null) {
				exceptionBuff.append(temp);
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					e.getStackTrace();
				}
			}
		}
		return exceptionBuff.toString();
	}
	
	private String readLog(String logPath) {
		File file = new File(logPath);
		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new FileReader(file));
			String temp = null;
			while ((temp = bufReader.readLine()) != null) {
				if(temp.contains("Exception")){
					return "Exception";
				}else if(temp.contains("Running job")){
					return "sumbit is ok";
				}
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException e) {
					e.getStackTrace();
				}
			}
		}
		return null;
	}
	
	private String returnTypeStr(String checkFile){
		 	String typeStr = "";
		 	if(checkFile.equals(FileChecker.CSV_STRING_TYPE+"")){
				typeStr = CsvTypeEnum.valueOf("CSV_STRING_TYPE").getText();
			}else if(checkFile.equals(FileChecker.CSV_NUM_TYPE+"")){
				typeStr = CsvTypeEnum.valueOf("CSV_NUM_TYPE").getText();
			}else if(checkFile.equals(FileChecker.CSV_DATE_TYPE+"")){
				typeStr = CsvTypeEnum.valueOf("CSV_DATE_TYPE").getText();
			}
		 	return typeStr;
	 }
}
