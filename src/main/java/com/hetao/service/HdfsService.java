package com.hetao.service;

import java.io.InputStream;
import java.util.List;
import com.hetao.bean.FileInfo;


public interface HdfsService {
	
	public List<FileInfo> selectFileInfos(String dir);
	public List<String> list(String dir,String field);
	public Boolean rename(String oldname,String destname);
	public Boolean delete(String destname);
	public void copy(String filename,String dirname,String destdirname);
	public List<FileInfo> queryByFilename(String filename,String currentDir);
	public List<String> getDirs(String dir);
	public void addDir(String dirname);
	public void addFile(String filename);
	public void upload(String filename,String dirname);
	public void uploadZipFile(String filename,String dirname);
	public void download(String downfile,String destdir);
	public void download2local(String source,String dest);
	public FileInfo selectFileInfo(String filepath);
	public void writeToFile(String filename,String filecontent);
	public Boolean deleteDir(String destname);
	public void writeFileContent2HDFS(byte[] content, String destpath,boolean overwrite);
	public void upload(String filename,String dirname,int checkCode);
	public void writeUTFToFile(String path, InputStream in) throws Exception;
	public void writeUTFToFile(String path,String newPath,String[] errorLine) throws Exception;
	public void writeUTFToFile(String path,String newPath,String[] errorLine, InputStream in) throws Exception;
}
