package com.hetao.constroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.fline.hadoop.data.util.bigdata.Solr_Query;
import com.hetao.bean.FileInfo;
import com.hetao.bean.SolrDocBean;
import com.hetao.page.Pager;
import com.hetao.service.HdfsService;
import com.hetao.service.SolrService;
import com.hetao.util.ResourceUtils;

@Controller
public class FileManagerConstroller {

	@Autowired private HdfsService hdfsService;
	
	@Autowired private SolrService solrService;
	
	
	/**
	 * query by condition (filename)
	 */
	@RequestMapping(value = "/queryByfilename", method = RequestMethod.POST)
	public ModelAndView queryByfilename(@RequestParam(value = "qfilename") final String qfilename,
			@RequestParam(value = "currentDir") String currentDir, ModelMap modelMap) throws Exception {
		String downFileDirStr = currentDir;
		if(!currentDir.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
		List<FileInfo> list = hdfsService.queryByFilename(qfilename, downFileDirStr);
		modelMap.put("fileinfos", list);
		String[] dirs  = downFileDirStr.substring(1, downFileDirStr.length()).replace("/", ",").split(",");
		modelMap.put("dir", dirs);
		modelMap.put("qfilename", qfilename);
		modelMap.put("qflag", 1);
		return new ModelAndView("/index", modelMap);
	}
	

	/**
	 * list
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response, ModelMap modelMap,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "rows", required = false, defaultValue = "10") Integer rows) throws Exception {
		String dirname = request.getParameter("cur_dir");
		String[] dirs= ResourceUtils.getDirs("dir");
		if(StringUtils.isEmpty(dirname)){
			dirname = ResourceUtils.getAppSetting("dir");
		}else{
			dirs = dirname.substring(1, dirname.length()).replace("/", ",").split(",");
		}
		List<FileInfo> list = hdfsService.selectFileInfos(dirname);
		if(list!=null){
			Pager pager = new Pager(page,rows,list);
			int countPage = list.size() % rows == 0  ? (list.size() / rows) : (list.size() / rows + 1);
			modelMap.put("total", list.size());
			modelMap.put("fileinfos", pager.getPagerList());
			modelMap.put("countPage", countPage);
			modelMap.put("pageIndex", page);
			modelMap.put("dir", dirs);
		}
		return new ModelAndView("/index", modelMap);
	}

	/**
	 * rename
	 */
	@RequestMapping(value = "/rename", method = RequestMethod.POST)
	protected ModelAndView rename(@RequestParam(value = "filename") final String destname,
			@RequestParam(value = "oldname") String oldname) {
		String downFileDirStr = oldname;
		if(!oldname.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
		hdfsService.rename(downFileDirStr, destname);
		String dir = downFileDirStr.substring(0,downFileDirStr.lastIndexOf("/"));
		return new ModelAndView("redirect:/jump?current_name_dir="+dir);
	}
	
	/**
	 * jump to
	 */
	@RequestMapping(value = "/jump", method = RequestMethod.GET)
	public ModelAndView jump(HttpServletRequest request,ModelMap modelMap,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "rows", required = false, defaultValue = "10") Integer rows) throws Exception {
		String dirname = request.getParameter("current_name_dir");
		String[] dirs= ResourceUtils.getDirs("dir");
		if(!StringUtils.isEmpty(dirname)){
			List<FileInfo> list = hdfsService.selectFileInfos(dirname);
			Pager pager = new Pager(page,rows,list);
			int countPage = list.size() % rows == 0  ? (list.size() / rows) : (list.size() / rows + 1);
			modelMap.put("fileinfos", pager.getPagerList());
			modelMap.put("countPage", countPage);
			modelMap.put("pageIndex", page);
			modelMap.put("total", list.size());
		}
		dirs = dirname.substring(1, dirname.length()).replace("/", ",").split(",");
		modelMap.put("dir", dirs);
		String successedCount = request.getParameter("successedCount");
		if(!StringUtils.isEmpty(successedCount)){
			modelMap.put("successedCount", successedCount);
			modelMap.put("dirname", dirname);
			String type = request.getParameter("type");
			modelMap.put("type", type);
			return new ModelAndView("/index4collect", modelMap);
		}
		return new ModelAndView("/index", modelMap);
	}

	
	/**
	 * delete
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	protected ModelAndView delete(@RequestParam(value = "deletename") final String deletename) {
		String downFileDirStr = deletename;
		if(!deletename.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
		hdfsService.delete(downFileDirStr);
		String dir = downFileDirStr.substring(0,downFileDirStr.lastIndexOf("/"));
		return new ModelAndView("redirect:/jump?current_name_dir="+dir);
	}
	
	/**
	 * touch file
	 */
	@RequestMapping(value = "/newFile", method = RequestMethod.POST)
	protected ModelAndView newFile(@RequestParam(value = "newfilename") final String newfilename,@RequestParam(value = "newFileDir") final String newFileDir) {
		hdfsService.addFile(newFileDir + "/" + newfilename);
        return new ModelAndView("redirect:/jump?current_name_dir="+newFileDir);
	}
	
	/**
	 * mkdir
	 */
	@RequestMapping(value = "/newDir", method = RequestMethod.POST)
	protected ModelAndView newDir(@RequestParam(value = "newDirname") final String newDirname,@RequestParam(value = "mkdirname") final String mkdirname) {
		hdfsService.addDir(mkdirname + "/" + newDirname);
        return new ModelAndView("redirect:/jump?current_name_dir="+mkdirname);
	}
	
	/**
	 * upload zip file
	 */
	 @RequestMapping(value="/uploadZip")
	 public ModelAndView uploadZip(@RequestParam(value = "importzipdirname") final String importzipdirname,
			 @RequestParam(value = "importzipfilename", required = false) MultipartFile importzipfilename, HttpServletRequest request, ModelMap model) {
	        try {
		        String path = request.getSession().getServletContext().getRealPath("upload");
		        String fileName = importzipfilename.getOriginalFilename();
		        mkdir(path);
		        String[] dirs = importzipdirname.substring(1, importzipdirname.length()).replace("/", ",").split(",");
		        for (String dir : dirs) {
		        	path = path + "/" + dir; 
		        	mkdir(path);
				}
		        File targetFile = new File(path,fileName);
		        importzipfilename.transferTo(targetFile);
	        	hdfsService.uploadZipFile(path+"/"+fileName,importzipdirname);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return new ModelAndView("redirect:/jump?current_name_dir="+importzipdirname);
	    }
	
	 /**
	  * upload common file
	  */
	 @RequestMapping(value="/upload")
	 public ModelAndView upload(@RequestParam(value = "importdirname") final String importdirname,
			 @RequestParam(value = "importfilename", required = false) MultipartFile importfilename, HttpServletRequest request, ModelMap model) {
	        try {
		        String path = request.getSession().getServletContext().getRealPath("upload");
		        String fileName = importfilename.getOriginalFilename();
		        mkdir(path);
		        String[] dirs = importdirname.substring(1, importdirname.length()).replace("/", ",").split(",");
		        for (String dir : dirs) {
		        	path = path + "/" + dir; 
		        	mkdir(path);
				}
		        File targetFile = new File(path,fileName);
		        targetFile.createNewFile();
		        
		        InputStream inputStream = importfilename.getInputStream();
		       
		        boolean isText = fileName.contains("txt");
		        // utf-8 write into server
		        if(isText){
		        	hdfsService.writeUTFToFile(path+"/"+fileName,inputStream);
		        }else{
		        	importfilename.transferTo(targetFile);
		        }
		        ResourceUtils.getSolrInstance().deleteDoc(importdirname+"/"+fileName);
	        	hdfsService.upload(path+"/"+fileName,importdirname+"/"+fileName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return new ModelAndView("redirect:/jump?current_name_dir="+importdirname);
	}

	 private void mkdir(String path){
		 File targetFile = new File(path);
	        if(!targetFile.exists()){
	            targetFile.mkdirs();
	        }
	 }
	
	/**
	 * txt -> download
	 */
	@RequestMapping(value = "/download4txt", method = RequestMethod.POST)
	protected ModelAndView download4txt(@RequestParam(value = "downfiledir") final String downfiledir,
    		@RequestParam(value = "downfilename") final String downfilename, ModelMap modelMap) throws Exception {
		String downFileDirStr = downfiledir;
		if(!downfiledir.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
		System.out.println(downFileDirStr);
		FileInfo file = hdfsService.selectFileInfo(downFileDirStr + "/" + downfilename);
		modelMap.put("fileinfo", file);
		modelMap.put("dir", downFileDirStr.replace("/", ",").substring(1).split(","));
		modelMap.put("downfilename", downfilename);
		List<SolrDocBean> solrDocBeanList = solrService.listAutoLableDoc(downFileDirStr + "/" + downfilename);
		modelMap.put("solrDocBeanList", solrDocBeanList);
		
		return new ModelAndView("/downInfo",modelMap);
	}

	
	/**
	 * doc -> download
	 */
	@RequestMapping(value = "/download4doc", method = RequestMethod.POST)
	protected ModelAndView download4doc(@RequestParam(value = "downfiledir") final String downfiledir,
    		@RequestParam(value = "downfilename") final String downfilename, ModelMap modelMap) throws Exception {
		String downFileDirStr = downfiledir;
		if(!downfiledir.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
		FileInfo file = hdfsService.selectFileInfo(downFileDirStr + "/" + downfilename);
		file.setFilecontent(solrService.showContent(downFileDirStr + "/" + downfilename));
		modelMap.put("fileinfo", file);
		modelMap.put("dir", downFileDirStr.replace("/", ",").substring(1).split(","));
		modelMap.put("downfilename", downfilename);
		List<SolrDocBean> solrDocBeanList = solrService.listAutoLableDoc(downFileDirStr + "/" + downfilename);
		modelMap.put("solrDocBeanList", solrDocBeanList);
		return new ModelAndView("/downInfo4doc",modelMap);
	}
	
	 // 直接抽取全部内容
    public static String readDoc1(InputStream is) throws IOException {
        WordExtractor extractor = new WordExtractor(is);
        return extractor.getText();
    }
    
    //分章节Section、段落Paragraph、字符串CharacterRun抽取
    public String readDoc2(InputStream is) throws IOException {
    	String text = "";
        HWPFDocument doc=new HWPFDocument(is);
        Range r=doc.getRange();
        for(int x=0;x<r.numSections();x++){
            Section s=r.getSection(x);
            for(int y=0;y<s.numParagraphs();y++){
                Paragraph p=s.getParagraph(y);
                for(int z=0;z<p.numCharacterRuns();z++){
                    CharacterRun run=p.getCharacterRun(z);
                    text += run.text();
                }
            }
        }
        return text;
    }

    public static void main(String[] args) {
       String a = "/user";
       System.out.println(a.startsWith("/"));
    }
	
	
	
	/**
	 * txt ->download-> edit
	 */
	@RequestMapping(value = "/editTxt", method = RequestMethod.POST)
	protected ModelAndView editTxt(@RequestParam(value = "downfiledir") final String downfiledir,
    		@RequestParam(value = "downfilename") final String downfilename, ModelMap modelMap) throws Exception {
		String downFileDirStr = downfiledir;
		if(!downfiledir.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
		FileInfo file = hdfsService.selectFileInfo(downFileDirStr + "/" + downfilename);
		modelMap.put("fileinfo", file);
		modelMap.put("dir", downFileDirStr.replace("/", ",").substring(1).split(","));
		modelMap.put("downfilename", downfilename);
		return new ModelAndView("/editdownInfo",modelMap);
	}
	/**
	 * txt ->download-> edit->into hdfs
	 */
	@RequestMapping(value = "/editTxt2Hdfs", method = RequestMethod.POST)
	protected ModelAndView editTxt2Hdfs(@RequestParam(value = "downfilepath") final String downfilepath,
    		@RequestParam(value = "downfilename") final String downfilename,HttpServletRequest request,
    		@RequestParam(value = "filecontent") final String filecontent, ModelMap modelMap) throws Exception {
		String remotepath = downfilepath.substring(0,downfilepath.lastIndexOf("/"));
		String newFilename = downfilepath.substring(downfilepath.lastIndexOf("/")+1);
		String downFileDirStr = remotepath;
		if(!remotepath.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
		//to HDFS
		hdfsService.writeFileContent2HDFS(filecontent.getBytes(), downFileDirStr + "/" + newFilename, true);
		System.out.println(downFileDirStr + "/" + newFilename);
		// update solr
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("downfilepath:"+downfilepath);
		String downfilepathStr = downfilepath;
		if(!downfilepath.startsWith("/")){
			downfilepathStr = "/" + downfilepathStr;
		}
		FileInfo file = hdfsService.selectFileInfo(downfilepathStr);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("filename", file.getFilename());
		map.put("username", file.getUsername());
		map.put("createdTime", sdf.parse(file.getCreateDate()).getTime()+"");
		map.put("filecontent", filecontent);
		map.put("sourceType", "1");
		map.put("label", "solrTest");
		map.put("hdfspath", downFileDirStr + "/" + newFilename);
		map.put("filesize", file.getFilesize());
		String solrmasterurl = ResourceUtils.getSolrMasterUrl();
		new Solr_Query(solrmasterurl).updateSolrIndex(downFileDirStr + "/" + newFilename, map);
		System.out.println("solr:"+downFileDirStr + "/" + newFilename);
		return new ModelAndView("redirect:/jump?current_name_dir="+downFileDirStr);
	}
	
	
	
	/**
	 * download -> common file
	 */
	@RequestMapping(value = "/download", method = RequestMethod.POST) 
    public ResponseEntity<byte[]> download(@RequestParam(value = "downfiledir") final String downfiledir,
    		@RequestParam(value = "downfilename") final String downfilename,HttpServletRequest request) {
    	String downFileDirStr = downfiledir;
		if(!downfiledir.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
    	return downloadCommon(request,downfilename,downFileDirStr,"0");    
    }
    
   /**
    * download -> txt
    */
	@RequestMapping(value = "/downloadTxt", method = RequestMethod.POST) 
    public ResponseEntity<byte[]> downloadTxt(@RequestParam(value = "downfiledir") final String downfiledir,
    		@RequestParam(value = "downfilename") final String downfilename,HttpServletRequest request) {
    	String downFileDirStr = downfiledir;
		if(!downfiledir.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
    	return downloadCommon(request,downfilename,downFileDirStr,".txt");  
    }
	
    private ResponseEntity<byte[]> downloadCommon(HttpServletRequest request,String downfilename,String downfiledir,String type){
    	try {
			String path = request.getSession().getServletContext().getRealPath("download");
			mkdir(path);
			String[] dirs = downfiledir.substring(1, downfiledir.length()).replace("/", ",").split(",");
			for (String dir : dirs) {
	        	path = path + "/" + dir; 
	        	mkdir(path);
			}
			String localfilePath = path + "/" +downfilename;
			String remotefilePath = downfiledir +  "/" + downfilename;
			if(!StringUtils.isEmpty(downfilename)){
				if(!downfilename.contains(".txt")){
					hdfsService.download2local(remotefilePath,localfilePath);
				}else if(type.contains(".txt")){
					hdfsService.download2local(remotefilePath,localfilePath);
				}
			}
			File file=new File(localfilePath);
			HttpHeaders headers = new HttpHeaders();  
			String fileName=new String(downfilename.getBytes("UTF-8"),"iso-8859-1");
			headers.setContentDispositionFormData("attachment", fileName); 
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);  
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
    	return null;
    }
    
	/**
	 * copy
	 */
	@RequestMapping(value = "/copy", method = RequestMethod.POST)
	protected ModelAndView copy(@RequestParam(value = "copydirname") final String copydirname,
			@RequestParam(value = "copyfilename") String copyfilename,@RequestParam(value = "sourcecopydirname") String sourcecopydirname) {
		String downFileDirStr = sourcecopydirname;
		if(!sourcecopydirname.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
		hdfsService.copy(copyfilename,copydirname,downFileDirStr);
		// update solr
		return new ModelAndView("redirect:/list");
	}
	/**
	 * move
	 */
	@RequestMapping(value = "/move", method = RequestMethod.POST)
	protected ModelAndView move(@RequestParam(value = "movedirname") final String movedirname,
			@RequestParam(value = "movefilename") String movefilename,
			@RequestParam(value = "sourcemovedirname") String sourcemovedirname) {
		String downFileDirStr = sourcemovedirname;
		if(!sourcemovedirname.startsWith("/")){
			downFileDirStr = "/" + downFileDirStr;
		}
		hdfsService.copy(movefilename,movedirname,downFileDirStr);
		hdfsService.delete(downFileDirStr + "/" + movefilename);
		return new ModelAndView("redirect:/list");
	}
	
	/**
	 * downloadAjax
	 */
//	@RequestMapping(value = "/downloadAjax", method = RequestMethod.GET)
//	@ResponseBody
//	protected JSONArray download(HttpServletRequest request) {
//		String downfilename = request.getParameter("downfilename");
//		String downtodir = request.getParameter("downtodir");
//		hdfsService.download(downfilename, downtodir);
//		List<String> list = new ArrayList<String>();
//		list.add("success");
//		JSONArray jo = JSONArray.fromObject(list);
//		return jo;
//	}
	
	/**
	 * ajaxJump
	 */
	@RequestMapping(value = "/ajaxJump", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject ajaxJump(HttpServletRequest request) throws Exception {
		String page = request.getParameter("page");
		String rows = request.getParameter("rows");
		Map<String,Object> modelMap= new HashMap<String,Object>();
		String dirname = request.getParameter("current_name_dir");
		String[] dirs= ResourceUtils.getDirs("dir");
		List<String> list = null;
		if(!StringUtils.isEmpty(dirname)){
			list = hdfsService.list(dirname,null);
			dirs = dirname.substring(1, dirname.length()).replace("/", ",").split(",");
		}else{
			list=hdfsService.getDirs(ResourceUtils.getAppSetting("dir"));
		}
		if(!StringUtils.isEmpty(page) && !StringUtils.isEmpty(rows)){
			Pager pager = new Pager(Integer.parseInt(page),Integer.parseInt(rows),list);
			modelMap.put("fileinfos", pager.getPagerList());
		}else{
			modelMap.put("fileinfos", list);
		}
		modelMap.put("total", list.size());
		modelMap.put("dirs", dirs);
		JSONObject jo = JSONObject.fromObject(modelMap);
		return jo;
	}
	
	/**
	 * ajaxCreateDir
	 */
	@RequestMapping(value="/ajaxCreateDir",method=RequestMethod.GET)	
	@ResponseBody
	public JSONArray  ajaxCreateDir(HttpServletRequest request){
		String dirname = request.getParameter("dirname");
		String dirs = ResourceUtils.getAppSetting("dir");
		hdfsService.addDir(dirs+"/"+dirname);
		List<String> list=hdfsService.getDirs(dirs);
		return JSONArray.fromObject(list);
	}
}
