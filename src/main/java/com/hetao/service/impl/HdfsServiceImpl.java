package com.hetao.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.FileStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.client.DataTransporter;
import com.fline.hadoop.data.client.HDFSDataManager;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.hetao.bean.FileInfo;
import com.hetao.service.HdfsService;
import com.hetao.util.DateUtil;
import com.hetao.util.ResourceUtils;

@Service("hdfsService")
public class HdfsServiceImpl implements HdfsService {
	
	public void writeUTFToFile(String path,String newPath,String[] errorLine){
		InputStream in = null;
		BufferedReader read = null;
		String s = null;
		BufferedWriter writer = null;
		int iLine = 1;
		OutputStreamWriter sout;
		InputStreamReader sin;
		try {
			System.out.println("filePath1:"+path);
			in = new FileInputStream(new File(path));
			sin = new InputStreamReader(in, codeString(path));
			read = new BufferedReader(sin);
			sout = new OutputStreamWriter(new FileOutputStream(new File(newPath)),	"UTF-8");
			writer = new BufferedWriter(sout);
			while ((s = read.readLine()) != null) {
				System.out.println("------------------------------"+s);
				boolean isWrite = true;
				for (String line : errorLine) {
					if(line.equals(iLine+"")){
						isWrite = false;
					}
				}
				if(isWrite){
					System.out.println(s);
					writer.write(s);
					writer.newLine();
					writer.flush();
				}
				iLine++;
			}
		} catch (FileNotFoundException ex) {
			System.out.println("找不到指定文件！！");
		} catch (IOException e) {
			System.out.println("文件读取有误！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				read.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	
	public void writeUTFToFile(String path,String newPath,String[] errorLine, InputStream in) throws Exception {
		BufferedReader buff_in;
		BufferedWriter buff_out;
		InputStreamReader sin;
		OutputStreamWriter sout;
		String s = null;
		int iLine = 1;
		try {
			path = path.replaceAll("\\\\","\\\\\\\\");  
			newPath = newPath.replaceAll("\\\\","\\\\\\\\");  
			sin = new InputStreamReader(in, codeString(path));
			buff_in = new BufferedReader(sin);
			sout = new OutputStreamWriter(new FileOutputStream(new File(newPath)),	"UTF-8");
			buff_out = new BufferedWriter(sout);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			throw new Exception("File " + path + " not found");
		}

		try {
			while ((s = buff_in.readLine()) != null) {
				System.out.println("------------------------------"+s);
				boolean isWrite = true;
				for (String line : errorLine) {
					if(line.equals(iLine+"")){
						isWrite = false;
					}
				}
				if(isWrite){
					System.out.println(s);
					buff_out.write(s);
					buff_out.newLine();
					buff_out.flush();
				}
				iLine++;
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			throw new Exception("Exception while copying");
		}

		try {
			buff_in.close();
			buff_out.flush();
			buff_out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new Exception("Exception while closing");
		}
	}
	
	public void writeUTFToFile(String path, InputStream in) throws Exception {
		BufferedReader buff_in;
		BufferedWriter buff_out;
		InputStreamReader sin;
		OutputStreamWriter sout;

		try {
			sin = new InputStreamReader(in, codeString(path));
			buff_in = new BufferedReader(sin);
			sout = new OutputStreamWriter(new FileOutputStream(new File(path)),	"UTF-8");
			buff_out = new BufferedWriter(sout);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			throw new Exception("File " + path + " not found");
		}

		try {
			int c;
			while ((c = buff_in.read()) != -1)
				buff_out.write(c);

		} catch (IOException ex) {
			ex.printStackTrace();
			throw new Exception("Exception while copying");
		}

		try {
			buff_in.close();
			buff_out.flush();
			buff_out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new Exception("Exception while closing");
		}
	}
	
	private String codeString(String fileName) throws Exception {
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(
				fileName));
		int p = (bin.read() << 8) + bin.read();
		String code = null;

		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}
		bin.close();
		return code;
	}
	
	public List<FileInfo> selectFileInfos(String dir) {
		try {
			HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
			String[] fileinfs = operator.selectFileInfos(dir);
			List<FileInfo> list = new ArrayList<FileInfo>();
			for (String s : fileinfs) {
				list.add(groupFileInfo(s));
			}	
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void writeToFile(String filename,String filecontent) {
		try {                                                                        
             RandomAccessFile randomFile = new RandomAccessFile(filename, "rw");     
             randomFile.writeBytes(filecontent);
             randomFile.close();                                                     
         } catch (IOException e) {                                                   
             e.printStackTrace();                                                    
         }           
	}
	public FileInfo selectFileInfo(String filepath){
		try {
			HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
			String[] fileinfs = operator.selectFileInfos(filepath);
			String s = fileinfs[0];
			return getInfo(s,filepath,operator);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private FileInfo groupFileInfo(String s){
		String[] arr = s.split(",");
		FileInfo fileinfo = new FileInfo();
		fileinfo.setAuthority(arr[5]);
		fileinfo.setUsername(arr[3]);
		fileinfo.setGroup(arr[4]);
		fileinfo.setFiletype(arr[0]);
		fileinfo.setFilesize( arr[2]);
		fileinfo.setCreateDate(DateUtil.fromDateCST(arr[6],DateUtil.LONG_MODEL));
		String relateR = arr[1].replace(ResourceUtils.getDefaultfs(), "");
		fileinfo.setFilepath(relateR);
		fileinfo.setFilename(arr[1].substring(arr[1].lastIndexOf("/")+1, arr[1].length()));
		fileinfo.setFiledir(relateR.substring(0,relateR.lastIndexOf("/")));
		fileinfo.setLink("");
		return fileinfo;
	}
	private FileInfo getInfo(String s,String filepath,HDFSDataManager operator){
		FileInfo fileinfo = groupFileInfo(s);
		if(fileinfo.getFiletype().equals("file")){
			fileinfo.setFilecontent(showFileContent(operator,filepath));
		}
		return fileinfo;
	}
	
	public List<String> list(String dir,String field) {
		try {
			HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
			String[] fileinfos = operator.selectFileInfos(dir);
			List<String> list = new ArrayList<String>();
			for (String s : fileinfos) {
				String[] arr = s.split(",");
				if("dir".equals(arr[0])){
					arr[1] = arr[1].substring(arr[1].lastIndexOf("/")+1, arr[1].length());
					list.add(arr[1]);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public Boolean rename(String oldname,String destname) {
		String dir = "";
		if(!StringUtils.isEmpty(oldname)){
			dir = oldname.substring(0,oldname.lastIndexOf("/")+1);
		}
		HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
		boolean status = operator.renameTo(oldname, dir+destname);
		if(status) {
			// update solr
			try {
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				FileInfo file = selectFileInfo(dir+destname);
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("filename", file.getFilename());
				map.put("username", file.getUsername());
				map.put("createdTime", sdf.parse(file.getCreateDate()).getTime()+"");
				map.put("filecontent", file.getFilecontent());
				map.put("sourceType", "1");
				map.put("label", "solrTest");
				map.put("hdfspath", dir+destname);
				map.put("filesize", file.getFilesize());
				ResourceUtils.getSolrInstance().updateSolrIndex(oldname, map);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}
	
	public void writeFileContent2HDFS(byte[] content, String destpath,boolean overwrite){
		HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
		operator.writeFileContent2HDFS(content, destpath, overwrite);
	}
	
	public Boolean deleteDir(String destname) {
		HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
		return operator.deleteFile(destname);
	}

	public Boolean delete(String destname) {
		String[] destnameArr = destname.substring(destname.lastIndexOf("/")+1).split(",");
		String dir = destname.substring(0,destname.lastIndexOf("/")+1);
		HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
		for (String dest : destnameArr) {
			if(!StringUtils.isEmpty(dest) && !operator.deleteFile(dir + dest)){
				return false;
			}
			try {
				ResourceUtils.getSolrInstance().deleteDoc(dir + dest);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public void addFile(String filename) {
		HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
		operator.createNewFile(filename);
	}
	
	public void addDir(String dirname) {
		HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
		operator.createDir(dirname);
	}

	public void upload(String filename,String dirname) {
		try {
			HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
			DataProgressListener listener = new DataProgressListener() {
				@Override
				public void handleEvent(ProgressEvent e) {
					if (e.getSource() instanceof ProgressSource) {
						System.out.println(((ProgressSource) e.getSource())
								.getStat());
					}
				}
			};
			DataTransporter.uploadFile2HDFSWithIndexOnSolr(filename, dirname, true, "zhongliang-solr",
					"solrTest", ResourceUtils.getSolrMasterUrl(),1,listener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void upload(String filename,String dirname,int checkCode) {
		try {
//			HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
			DataProgressListener listener = new DataProgressListener() {
				@Override
				public void handleEvent(ProgressEvent e) {
					if (e.getSource() instanceof ProgressSource) {
						System.out.println(((ProgressSource) e.getSource())
								.getStat());
					}
				}
			};
			new DataTransporter().uploadFile2HDFSWithIndexOnSolr(filename, dirname, true, "zhongliang-solr",
					"solrTest", ResourceUtils.getSolrMasterUrl(),checkCode,listener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void uploadZipFile(String filename,String dirname) {
		try {
			HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
			//TODO
			List<String> uploadedFiles = operator.uploadZipFile(filename, dirname,null);
			for(String fileName : uploadedFiles) {
				addCopyFileToSolr(fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void download(String downfile,String destdir) {
		HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
		String[] fileArr = downfile.substring(downfile.lastIndexOf("/")+1).split(",");
		String dir = downfile.substring(0,downfile.lastIndexOf("/")+1);
		for (String file : fileArr) {
			if(!StringUtils.isEmpty(file)){
				down4Recursion(operator,destdir + file,dir + file);
			}
		}
		
	}
	
	public void download2local(String source,String dest){
		HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
		operator.download2local(source, dest);
	}
	
	public String showFileContent(HDFSDataManager operator,String filepath){
		String content = operator.showFileContent(filepath);
		return content;
	}
	
	private void down4Recursion(HDFSDataManager operator,String dest,String source){
		try {
			operator.download2local(source,dest);
			FileStatus[] status = operator.selectFileStatuses(source);
			for (FileStatus fileStatus : status) {
				String cur = "/" + fileStatus.getPath().getName();
				if (fileStatus.isDirectory()) {
					down4Recursion(operator, dest + cur,source + cur);
				} else {
					operator.download2local(source + cur, dest + cur);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getDirs(String dir) {
		try {
			HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
			String[] fileinfos = operator.selectFileInfos(dir);
			List<String> list = new ArrayList<String>();
			for (String s : fileinfos) {
				String[] arr = s.split(",");
				if(!StringUtils.isEmpty(arr[0]) && "dir".equals(arr[0])){
					list.add(arr[1].substring(arr[1].lastIndexOf("/")+1, arr[1].length()));
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void query4Recursion(String source,String filename, List<FileInfo> list,HDFSDataManager operator){
		
		try {
			Pattern pattern = Pattern.compile("^.*" + filename + ".*$", Pattern.MULTILINE);
			String[] searchResult = operator.selectFileInfos(source);
			String hdfs = ResourceUtils.getDefaultfs();
			String path = hdfs.substring(0,hdfs.lastIndexOf("/"));
			for (String s : searchResult) {
				String[] arr = s.split(",");
				FileInfo fileinfo = new FileInfo();
				fileinfo.setAuthority(arr[5]);
				fileinfo.setUsername(arr[3]);
				fileinfo.setGroup(arr[4]);
				fileinfo.setFiletype(arr[0]);
				fileinfo.setFilesize( arr[2]);
				fileinfo.setCreateDate(DateUtil.fromDateCST(arr[6],DateUtil.LONG_MODEL));
				String relateR = arr[1].replace(path, "");
				fileinfo.setFilepath(relateR);
				fileinfo.setFilename(arr[1].substring(arr[1].lastIndexOf("/")+1, arr[1].length()));
				fileinfo.setFiledir(relateR.substring(0,relateR.lastIndexOf("/")));
				fileinfo.setLink("");
				if(fileinfo.getFiletype().equals("file")){
					if(pattern.matcher(fileinfo.getFilename()).find()){
						list.add(fileinfo);
					}
				}else{
					query4Recursion(fileinfo.getFilepath(),filename,list,operator);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<FileInfo> queryByFilename(String filename,String currentDir){
		List<FileInfo> list = new ArrayList<FileInfo>();
		HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
		String source = currentDir;
		query4Recursion(source,filename,list,operator);
		return list;
	}
	
	private void copy4Recursion(HDFSDataManager operator,String dest,String source,boolean flag){
		try {
			FileStatus[] status = operator.selectFileStatuses(source);
			if (status.length == 1) {
				String hdfs = ResourceUtils.getDefaultfs();
				String path = hdfs.substring(0,hdfs.lastIndexOf("/"));
				String  relatePath = status[0].getPath().toString().replace(path, "");
				if(relatePath.equals(source)){
					operator.copyTo(source, dest);
					//create solr index
					addCopyFileToSolr(dest);
				}else if(!status[0].isDirectory()){
					String cur = "/" + status[0].getPath().getName();
					operator.copyTo(source+cur, dest+cur);
					
					//create solr index
					addCopyFileToSolr(dest+cur);
				}else{
					String cur = "/" + status[0].getPath().getName();
					addDir(dest + cur);
					copy4Recursion(operator, dest + cur,source + cur,false);
				}
			} else {
				addDir(dest);
				for (FileStatus fileStatus : status) {
					String cur = "/" + fileStatus.getPath().getName();
					if (fileStatus.isDirectory()) {
						addDir(dest + cur);
						copy4Recursion(operator, dest + cur,source + cur,false);
					} else {
						operator.copyTo(source + cur, dest + cur);
						
						// create solr index
						addCopyFileToSolr(dest+cur);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addCopyFileToSolr(String hdfspath ) {
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			FileInfo file = selectFileInfo(hdfspath);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("filename", file.getFilename());
			map.put("username", file.getUsername());
			map.put("createdTime", sdf.parse(file.getCreateDate()).getTime()+"");
			map.put("filecontent", file.getFilecontent());
			map.put("sourceType", "1");
			map.put("label", "solrTest");
			map.put("hdfspath", hdfspath);
			map.put("filesize", file.getFilesize());
			ResourceUtils.getSolrInstance().addDoc(map);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void copy(String filename, String dirname,String sourcedirname) {
		try {
			HDFSDataManager operator = ResourceUtils.getHDFSDataManager();
			String[] fileArr = filename.split(",");
			for (String file : fileArr) {
				if(!StringUtils.isEmpty(file)){
					String source = sourcedirname.trim() + "/" + file.trim();
					String dest = dirname.trim() + "/" + file.trim();
					copy4Recursion(operator,dest,source,true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
