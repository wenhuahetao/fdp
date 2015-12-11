package com.fline.hadoop.data.common.analysis.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;

import com.fline.hadoop.data.common.analysis.ContentReader;
import com.fline.hadoop.data.util.FileChecker;
import com.hetao.util.ResourceUtils;

public class ScpReader implements ContentReader {
	public static final String REMOTE_LINUX_IP = "remote_linux_ip";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String REMOTE_ENCODING = "remote_encoding";
	public static final String REMOTE_FILEPATH = "filepath";
	public static final String LOCAL_TEMP_PATH = "local.tmppath";

	private Connection con;
	private SCPClient scpclient;
	private String localfilepath;
	private String remotefilepath;
	private String remoteEncoding;

	private List<String> localfilepaths = null;
	private BufferedReader reader = null;
	private static Logger LOG = Logger.getLogger(ScpReader.class);

	@Override
	public byte[] getNextBytes() throws Exception {
		// TODO Auto-generated method stub
		if (reader == null || reader.ready() == false) {
			if (localfilepaths.size() > 0) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					reader = null;
				}
				String localPath = localfilepaths.remove(0);
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(localPath))));
			} else {
				return null;
			}
		}
		if (reader.ready()) {
			return reader.readLine().getBytes();
		} else {
			return getNextBytes();
		}
	}

	@Override
	public boolean setupReader(Map<String, String> params) {
		// TODO Auto-generated method stub
		try {
			String remoteip = params.get(REMOTE_LINUX_IP);
			String username = params.get(USER_NAME);
			String password = params.get(PASSWORD);
			localfilepath = params.get(LOCAL_TEMP_PATH);
			remotefilepath = params.get(REMOTE_FILEPATH);
			remoteEncoding = params.get(REMOTE_ENCODING);
			if (remoteip == null || username == null || password == null) {
				LOG.error("scpReader params lose... REMOTE_LINUX_IP, USER_NAME, PASSWORD could not be empty.");
				return false;
			}

			con = new Connection(remoteip);
			con.connect();
			boolean result = con.authenticateWithPassword(username, password);
			if (result == false) {
				LOG.error("Auth Failed with username = " + username
						+ "\tpassword = " + password);
				return false;
			}
			File localf = new File(localfilepath);
			if (localf.exists() == false) {
				localf.mkdirs();
			}
			scpclient = con.createSCPClient();
			downloadRemoteFiles(remotefilepath);
			localfilepaths = listFiles(localfilepath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private List<String> listRemoteFiles(String filepath) throws Exception {
		List<String> ret = new ArrayList<String>();
		if (con != null) {
			try {
				Session s = con.openSession();
				s.execCommand("ls -l " + filepath);
				InputStream is = s.getStdout();
				BufferedReader br = null;
				if (remoteEncoding != null) {
					br = new BufferedReader(new InputStreamReader(is,
							remoteEncoding));
				} else {
					br = new BufferedReader(new InputStreamReader(is, "gbk"));
				}
				s.waitForCondition(ChannelCondition.STDOUT_DATA, 3000);
				while (br.ready()) {
					String line = br.readLine();
					String[] splits = line.split("\\s+");
					if (splits.length < 8) {
						continue;
					} else {
						if (splits[0].startsWith("d")) {
							ret.add("d " + filepath + "/"
									+ splits[splits.length - 1]);
						} else {
							if (splits[splits.length - 1].equals(filepath)) {
								ret.add(filepath);
							} else {
								ret.add(filepath + "/"
										+ splits[splits.length - 1]);
							}
						}
					}
				}
				br.close();
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public void downloadRemoteFiles(String remote_path_) throws Exception {
		List<String> paths = listRemoteFiles(remote_path_);
		for (String path : paths) {
			String localpath = path.replace(remotefilepath, localfilepath);
			if (path.startsWith("d ")) {
				String dirpath = localpath.split("\\s+")[1];
				new File(dirpath).mkdir();
				downloadRemoteFiles(path.split("\\s+")[1]);
			} else {
				if (path.equals(remotefilepath)) {
					File localf = new File(localfilepath);
					File remotef = new File(path);
					if (localf.isDirectory()) {
						localpath = localfilepath + "/" + remotef.getName();
					}
				}
				FileOutputStream fos = new FileOutputStream(localpath);
				scpclient.get(path, fos, remoteEncoding);
				fos.close();
				// trans code to utf-8
				FileChecker.transFileCode(localpath, "utf-8");
			}
		}
	}

	public List<String> listFiles(String localfilepath) throws Exception {
		List<String> filepaths = new ArrayList<String>();
		File f = new File(localfilepath);
		if (f.exists()) {
			if (f.isDirectory()) {
				String[] filenames = f.list();
				for (String filename : filenames) {
					filepaths.addAll(listFiles(localfilepath + "/" + filename));
				}
			} else if (f.isFile()) {
				filepaths.add(localfilepath);
			}
		}
		return filepaths;
	}

	@Override
	public double getTotalSize() {
		// TODO Auto-generated method stub
		if (con != null) {
			try {
				Session s = con.openSession();
				s.execCommand("/usr/bin/du -sh " + remotefilepath);
				InputStream is = s.getStdout();
				double size = -1;
				byte[] buf = new byte[1024];
				is.read(buf);
				String resultstr = new String(buf);
				String[] splits = resultstr.split("\\s+");
				if (splits.length >= 2) {
					String num = splits[0].substring(0, splits[0].length() - 1);
					switch (splits[0].charAt(splits[0].length() - 1)) {
					case 'K':
						size = Double.valueOf(num) * 1024;
						break;
					case 'M':
						size = Double.valueOf(num) * 1024 * 1024;
						break;
					case 'G':
						size = Double.valueOf(num) * 1024 * 1024;
						break;
					default:
						LOG.warn("UNDEFINED FILE SIZE UNIT AT LAST CHAR = "
								+ splits[0].charAt(splits[0].length() - 1));
						break;
					}
				}
				is.close();
				s.close();
				return size;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error(e);
				return -1;
			}
		} else {
			return 0;
		}
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (reader != null) {
			reader.close();
		}
		if (con != null) {
			con.close();
		}
	}

	public List<String> getLocalPaths() {
		return localfilepaths;
	}

	public static void main(String[] args) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(REMOTE_LINUX_IP, ResourceUtils.getRemoteLinuxIP());
		map.put(USER_NAME, "root");
		map.put(PASSWORD, "Feixian2015");
		map.put(REMOTE_FILEPATH, "/root/t/");
		map.put(LOCAL_TEMP_PATH, "./testinput/t/");
		map.put(REMOTE_ENCODING, "gbk");
		ScpReader reader = new ScpReader();
		reader.setupReader(map);
		// System.out.println(reader.getTotalSize());
		// byte[] data = null;
		// while ((data = reader.getNextBytes()) != null) {
		// System.out.println(new String(data));
		// }
		reader.close();
		// Connection con = new Connection(map.get(REMOTE_LINUX_IP));
		// con.connect();
		// boolean result = con.authenticateWithPassword(map.get(USER_NAME),
		// map.get(PASSWORD));
		// if (result) {
		// System.out.println("authorized.");
		// Session s = con.openSession();
		// s.execCommand("cat /root/nohup.out");
		// System.out.println(s.getExitStatus());
		// BufferedReader reader = new BufferedReader(new InputStreamReader(
		// s.getStdout()));
		// while (reader.ready()) {
		// System.out.println(reader.readLine());
		// }
		// reader.close();
		// s.close();
		// System.out.println(s.getExitStatus());
		// }
		// con.close();
	}
}
