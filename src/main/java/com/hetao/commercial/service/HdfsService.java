package com.hetao.commercial.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface HdfsService {

	/**
	 * create hdfs file
	 * @param hdfsFileName
	 */
	void createFile(String hdfsFileName);
	
	/**
	 * create hdfs dir
	 * @param hdfsDir
	 */
	void createDir(String hdfsDir);
	
	/**
	 * upload loacl file to hdfs
	 * @param checkCode 
	 * 1=SmartCn Check, 2=LineCheck, 3=CSV Check , <=0 means close check
	 * @param multipartFile
	 */
	void upload4Local (int checkCode, MultipartFile multipartFile);
	
	/**
	 * upload remote file to hdfs
	 * @param remoteIp
	 * @param remoteFilePath
	 * @param remoteUserName
	 * @param remotePassword
	 */
	void upload4Remote(String remoteIp, String remoteFilePath,String remoteUserName, String remotePassword);
	
	/**
	 * upload zipFile to hdfs
	 * @param importDirName
	 * @param multipartFile
	 */
	void upload4Zip (String importDirName, MultipartFile multipartFile);
	
	/**
	 * query hdfs file
	 * @param qfilename
	 * @param currentDir
	 * @return
	 */
	Map<String,Object> queryByFileName(String qfilename, String currentDir);

	/**
	 * rename hdfs file
	 * @param destName
	 * @param oldName
	 */
	void rename(String destName, String oldName);
	
	/**
	 * move hdfs file to other dir
	 * @param moveDirName
	 * @param moveFileName
	 * @param sourceMoveDirname
	 */
	void move(String moveDirName, String moveFileName, String sourceMoveDirName);
	
	/**
	 * copy hdfs file to other dir
	 * @param copyDirName
	 * @param copyFileName
	 * @param sourceCopyDirName
	 */
	void copy(String copyDirName, String copyFileName, String sourceCopyDirName);
	
	/**
	 * delete hdfs file
	 * @param deleteName
	 */
	void delete(String deleteName);
}
