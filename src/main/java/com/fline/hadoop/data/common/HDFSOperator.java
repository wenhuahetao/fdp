package com.fline.hadoop.data.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.fline.hadoop.data.common.event.ProgressSource;
import com.fline.hadoop.data.core.StaticConfiguration;

/**
 * Version1.0.1 added function `writeFileContent2HDFS(byte[] content, String
 * destpath, boolean overwrite)` to support write byte[] content to hdfs.
 * 
 * @author zhongliang
 * 
 */
public class HDFSOperator {
	private FileSystem fs = null;
	private static Logger logger = Logger.getLogger(HDFSOperator.class);
	private static HDFSOperator defaultInstance = null;

	public HDFSOperator(Configuration conf) throws Exception {
		fs = FileSystem.get(conf);
	}

	/**
	 * create hdfs dir
	 * 
	 * @param dirpath
	 *            the dir will be created on hdfs
	 * @return whether create successfully.
	 */
	public boolean createDir(String dirpath) {
		try {
			return fs.mkdirs(new Path(dirpath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("create dir failed...", e);
			return false;
		}
	}

	public boolean createNewFile(String filepath) {
		try {
			return fs.createNewFile(new Path(filepath));
		} catch (Exception e) {
			logger.error("create-new-file error.", e);
			return false;
		}
	}

	/**
	 * upload local file to hdfs.
	 * 
	 * @param filepath
	 *            local source file
	 * @param destpath
	 *            hdfs dest path
	 * @param overwrite
	 *            if hdfs dest path exists, then delete it.
	 * @param psource
	 *            the source to send source to ProgressListener when upload
	 *            progress has been changed.
	 * @throws Exception
	 */
	public void uploadFile(String filepath, String destpath, boolean overwrite,
			ProgressSource psource) throws Exception {
		File localfile = new File(filepath);
		if (localfile.exists() == false) {
			throw new FileNotFoundException(filepath);
		} else if (localfile.isDirectory()) {
			String[] filenames = localfile.list();
			for (String filename : filenames) {
				this.uploadFile(filepath + "/" + filename, destpath + "/"
						+ filename, overwrite, null);
			}
			// throw new Exception(
			// "mehtod - uploadFile does not support upload directory");
		}
		// Path dest = new Path(destpath);
		// if (overwrite) {
		// if (fs.exists(dest)) {
		// fs.delete(dest, false);
		// }
		// }
		fs.copyFromLocalFile(false, overwrite, new Path(filepath), new Path(
				destpath));
	}

	/**
	 * upload zip with prgress stat. when progressource has been added listener,
	 * then listener will work when progress state changed
	 * 
	 * @param destpath
	 *            hdfs's dest dir
	 * @param filepath
	 *            local zip sourcepath
	 * @param psource
	 *            upload status
	 */
	@SuppressWarnings("unchecked")
	public List<String> uploadZipFile(String filepath, String destpath,
			ProgressSource psource) {
		List<String> createdFileList = new ArrayList<String>();
		try {
			File f = new File(filepath);
			ZipFile zipFile = new ZipFile(filepath);

			if ((!f.exists()) && (f.length() <= 0)) {
				logger.error("ZIP file error...");
			}
			String strPath, gbkPath, strtemp;
			File tempFile = new File(f.getParent());
			strPath = tempFile.getAbsolutePath();
			java.util.Enumeration<ZipEntry> e = zipFile.getEntries();
			// count filenums to calculate percent
			int entryCount = 0;
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				if (entry.isDirectory() == false)
					entryCount++;
			}
			double percent = 1.0d / entryCount;
			e = zipFile.getEntries();

			while (e.hasMoreElements()) {
				org.apache.tools.zip.ZipEntry zipEnt = (ZipEntry) e
						.nextElement();
				gbkPath = zipEnt.getName();
				if (zipEnt.isDirectory()) {
					strtemp = strPath + "/" + gbkPath;
					File dir = new File(strtemp);
					dir.mkdirs();
					continue;
				} else {
					InputStream is = zipFile.getInputStream(zipEnt);
					BufferedInputStream bis = new BufferedInputStream(is);
					gbkPath = zipEnt.getName();
					strtemp = strPath + "/" + gbkPath;

					FSDataOutputStream fsdos = fs.create(new Path(destpath
							+ "/" + gbkPath));
					int c;
					while ((c = bis.read()) != -1) {
						fsdos.write((byte) c);
					}
					fsdos.close();
					// trans percent
					if (psource != null) {
						psource.setStat(strtemp + " finished...");
						psource.addProgressPercent(percent);
					}

					// add to createdFileList
					createdFileList.add(destpath + "/" + gbkPath);

				}
				if (psource != null) {
					psource.setStat("upload zip finished.");
					psource.changeProgressPercent(1.0d);
				}
			}
		} catch (Exception e) {
			logger.error("uploadZipFile error", e);
		}
		return createdFileList;
	}

	/**
	 * delete hdfs file which named filepath
	 * 
	 * @param filepath
	 * @return whether the filepath has benn deleted.
	 */
	public boolean deleteFile(String filepath) {
		try {
			return fs.delete(new Path(filepath), true);
		} catch (Exception e) {
			logger.error("deleteFile error...", e);
			return false;
		}
	}

	/**
	 * get file infos, if the filepath is a directory , then return multi
	 * record.
	 * 
	 * @param filepath
	 * @return filepath info
	 * @throws Exception
	 */
	public FileStatus[] selectFileStatuses(String filepath) throws Exception {
		FileStatus status = fs.getFileStatus(new Path(filepath));
		FileStatus[] statuses = null;
		if (status.isDirectory()) {
			statuses = fs.listStatus(new Path(filepath));
		} else {
			statuses = new FileStatus[] { status };
		}
		return statuses;
	}

	public String[] selectFileStatusWithPattern(String filepath, String pattern)
			throws Exception {
		FileStatus[] statuses = fs.globStatus(new Path(filepath),
				new HDFSpathFilter(pattern));
		String[] retstrs = new String[statuses.length];
		// record = permission,owner,group,replication,len,lastmodified,path
		for (int i = 0; i < statuses.length; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(statuses[i].isDirectory() ? "dir" : "file");
			sb.append(',');
			sb.append(statuses[i].getPath().toString());
			sb.append(',');
			sb.append(statuses[i].getLen());
			sb.append(',');
			sb.append(statuses[i].getOwner());
			sb.append(',');
			sb.append(statuses[i].getGroup());
			sb.append(',');
			sb.append(statuses[i].getPermission().toString());
			sb.append(',');
			sb.append(new Date(statuses[i].getModificationTime()).toString());
			sb.append(',');

			// sb.append(statuses[i].getReplication());
			retstrs[i] = sb.toString();
		}
		return retstrs;
	}

	/**
	 * get file infos by return constructed string
	 * 
	 * @param filepath
	 * @return file infos constructed with string
	 *         [dir|file],filepath,filelen,owner
	 *         ,group,permission,modifiedtime,createmethod
	 * @throws Exception
	 */
	public String[] selectFileInfos(String filepath) throws Exception {
		FileStatus[] statuses = selectFileStatuses(filepath);
		String[] retstrs = new String[statuses.length];
		// record = permission,owner,group,replication,len,lastmodified,path
		for (int i = 0; i < statuses.length; i++) {
			StringBuilder sb = new StringBuilder();
			sb.append(statuses[i].isDirectory() ? "dir" : "file");
			sb.append(',');
			sb.append(statuses[i].getPath().toString());
			sb.append(',');
			sb.append(statuses[i].getLen());
			sb.append(',');
			sb.append(statuses[i].getOwner());
			sb.append(',');
			sb.append(statuses[i].getGroup());
			sb.append(',');
			sb.append(statuses[i].getPermission().toString());
			sb.append(',');
			sb.append(new Date(statuses[i].getModificationTime()).toString());
			sb.append(',');

			// sb.append(statuses[i].getReplication());
			retstrs[i] = sb.toString();
		}
		return retstrs;
	}

	/**
	 * get the head content of file named filepath
	 * 
	 * @param filepath
	 * @return the head content of the file
	 */
	public String showFileContent(String filepath) {
		try {
			Path f = new Path(filepath);
			FSDataInputStream fsin = fs.open(f);
			int fsinavaliable = fsin.available();
			// show 20KB data...
			int readnum = 1;
			int bufunitlen = 4096;
			byte[] buf = new byte[bufunitlen * readnum];
			for (int i = 0; i < readnum; i++) {
				fsin.read(buf, i * bufunitlen, bufunitlen);
			}
			fsin.close();
			if (buf.length > fsinavaliable) {
				return new String(Arrays.copyOf(buf, fsinavaliable), "UTF-8");
			} else {
				return new String(buf);
			}
		} catch (Exception e) {
			logger.error("showFileContent error...", e);
			return null;
		}
	}

	public boolean renameTo(String filepath, String destpath) {
		try {
			return fs.rename(new Path(filepath), new Path(destpath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(filepath + " rename to " + destpath + "failed.", e);
			return false;
		}
	}

	/**
	 * copy file from hdfs to hdfs.
	 * 
	 * @param filepath
	 * @param destpath
	 * @return whether copy successfully
	 */
	public boolean copyTo(String filepath, String destpath) {
		try {
			FSDataInputStream fsdin = fs.open(new Path(filepath));
			FSDataOutputStream fsdou = fs.create(new Path(destpath));
			int readnum;
			byte[] buf = new byte[4096];
			while ((readnum = fsdin.read(buf)) > 0) {
				fsdou.write(buf, 0, readnum);
			}
			fsdin.close();
			fsdou.close();
			return true;
		} catch (Exception e) {
			logger.error(filepath + " copy to " + destpath, e);
			return false;
		}
	}

	/**
	 * download file from hdfs to local
	 * 
	 * @param filepath
	 * @param destpath
	 */
	public void download2local(String filepath, String destpath) {
		try {
			fs.copyToLocalFile(new Path(filepath), new Path(destpath));
		} catch (Exception e) {
			logger.error("download2local error...", e);
		}
	}

	public void writeFileContent2HDFS(byte[] content, String destpath,
			boolean overwrite) {
		try {
			FSDataOutputStream fsdou = fs.create(new Path(destpath), overwrite);
			fsdou.write(content);
			fsdou.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getDiskUsed() throws Exception {
		return "{used:" + fs.getStatus().getUsed() + ",all:"
				+ fs.getStatus().getCapacity() + "}";
	}
	
	public Long[] getDiskUsed4Array() throws Exception {
		Long[] disk = new Long[2];
//		disk[0] = 3000L;
//		disk[1] = 10000L;
		disk[0] = fs.getStatus().getUsed();
		disk[1] = fs.getStatus().getCapacity();
		return disk;
	}

	public static HDFSOperator getDefaultInstance() throws Exception {
		if (defaultInstance == null) {
			Configuration conf = new Configuration();
			conf.addResource(new FileInputStream(HDFSOperator.class
					.getClassLoader().getResource("").getPath()
					+ "hdfs-site.xml"));
			defaultInstance = new HDFSOperator(conf);
		}
		return defaultInstance;
	}

	/**
	 * close connection to hdfs
	 */
	public void close() {
		try {
			fs.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("hdfsdatamanager close error.", e);
		}
	}

	public class HDFSpathFilter implements PathFilter {
		private final String regex;

		public HDFSpathFilter(String regex) {
			this.regex = regex;
		}

		public boolean accept(Path path) {
			return !path.toString().matches(regex);
		}
	}

	public static void main(String[] args) throws Exception {
		// HDFSOperator operator = new HDFSOperator(
		// "http://121.40.99.124:10501/xmlrpc");
		// String[] fileinfos = operator.selectFileInfos("/user/root");
		// for (String s : fileinfos) {
		// System.out.println(s);
		// }
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// Configuration conf = new Configuration();
		// conf.set("fs.defaultFS", "hdfs://hdp-master:8020/");
		// String content = new HDFSOperator(conf)
		// .showFileContent("/user/root/pom.xml");
		// System.out.println(content);
		// new HDFSOperator(conf).download2local(
		// "/user/public/hetao1/20150815.txt", "./");
		// new HDFSOperator(conf).writeFileContent2HDFS(content.getBytes(),
		// "/user/public/hetao/a.txt", true);
		System.out.println(HDFSOperator.getDefaultInstance().getDiskUsed());
		HDFSOperator.getDefaultInstance().close();
		// operator.uploadFile("./build.sh", "/user/root/build.sh", null,
		// false);
	}
}
