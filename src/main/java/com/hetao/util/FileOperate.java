package com.hetao.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * <b>文件读取类</b><br />
 * 1、按字节读取文件内容<br />
 * 2、按字符读取文件内容<br />
 * 3、按行读取文件内容<br />
 * 
 * @author qin_xijuan
 * 
 */
public class FileOperate {

	private static final String FILE_PATH = "d:/work/jipinwodi.txt";

	/**
	 * 以字节为单位读写文件内容
	 * 
	 * @param filePath
	 *            ：需要读取的文件路径
	 */
	public static void readFileByByte(String filePath) {
		File file = new File(filePath);
		// InputStream:此抽象类是表示字节输入流的所有类的超类。
		InputStream ins = null;
		OutputStream outs = null;
		try {
			// FileInputStream:从文件系统中的某个文件中获得输入字节。
			ins = new FileInputStream(file);
			outs = new FileOutputStream("d:/work/readFileByByte.txt");
			int temp;
			// read():从输入流中读取数据的下一个字节。
			while ((temp = ins.read()) != -1) {
				outs.write(temp);
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (ins != null && outs != null) {
				try {
					outs.close();
					ins.close();
				} catch (IOException e) {
					e.getStackTrace();
				}
			}
		}
	}

	/**
	 * 创建文件夹
	 */
	public static boolean mkdir(String path) {
		boolean flag = false;
		File fd = null;
		try {
			fd = new File(path);
			if (!fd.exists()) {
				flag = fd.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fd = null;
		}
		return flag;
	}

	/**
	 * 写文件内容
	 */
	public static void writeFile(String buildDir,String filePath,String remoteFilePath) {
		mkdir(buildDir);
		BufferedReader bufReader = null;
		BufferedWriter bufWriter = null;
		try {
			bufReader = new BufferedReader(new FileReader(remoteFilePath));
			bufWriter = new BufferedWriter(new FileWriter(filePath));
			String temp = null;
			while ((temp = bufReader.readLine()) != null) {
				bufWriter.write(temp + "\n");
				System.out.println("bufWriter : " + temp);
				bufWriter.flush();
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (bufReader != null && bufWriter != null) {
				try {
					bufReader.close();
					bufWriter.close();
				} catch (IOException e) {
					e.getStackTrace();
				}
			}
		}
	}

	/**
	 * 删除文件，可以是单个文件或文件夹
	 * 
	 * @param fileName
	 *            待删除的文件名
	 * @return 文件删除成功返回true,否则返回false
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("删除文件失败：" + fileName + "文件不存在");
			return false;
		} else {
			if (file.isFile()) {

				return deleteFile(fileName);
			} else {
				return deleteDirectory(fileName);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileName
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true,否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			System.out.println("删除单个文件" + fileName + "成功！");
			return true;
		} else {
			System.out.println("删除单个文件" + fileName + "失败！");
			return false;
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param dir
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true,否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			System.out.println("删除目录失败" + dir + "目录不存在！");
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}
		if (!flag) {
			System.out.println("删除目录失败");
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			System.out.println("删除目录" + dir + "成功！");
			return true;
		} else {
			System.out.println("删除目录" + dir + "失败！");
			return false;
		}
	}

	/**
	 * 以字符为单位读写文件内容
	 * 
	 * @param filePath
	 */
	public static void readFileByCharacter(String filePath) {
		File file = new File(filePath);
		// FileReader:用来读取字符文件的便捷类。
		FileReader reader = null;
		FileWriter writer = null;
		try {
			reader = new FileReader(file);
			writer = new FileWriter("d:/work/readFileByCharacter.txt");
			int temp;
			while ((temp = reader.read()) != -1) {
				writer.write((char) temp);
			}
		} catch (IOException e) {
			e.getStackTrace();
		} finally {
			if (reader != null && writer != null) {
				try {
					reader.close();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 以行为单位读写文件内容
	 * 
	 * @param filePath
	 */
	public static void readFileByLine(String filePath) {
		File file = new File(filePath);
		// BufferedReader:从字符输入流中读取文本，缓冲各个字符，从而实现字符、数组和行的高效读取。
		BufferedReader bufReader = null;
		BufferedWriter bufWriter = null;
		try {
			// FileReader:用来读取字符文件的便捷类。
			bufReader = new BufferedReader(new FileReader(file));
			bufWriter = new BufferedWriter(new FileWriter(
					"d:/work/readFileByLine.txt"));
			// buf = new BufferedReader(new InputStreamReader(new
			// FileInputStream(file)));
			String temp = null;
			while ((temp = bufReader.readLine()) != null) {
				bufWriter.write(temp + "\n");
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			if (bufReader != null && bufWriter != null) {
				try {
					bufReader.close();
					bufWriter.close();
				} catch (IOException e) {
					e.getStackTrace();
				}
			}
		}
	}

	/**
	 * 使用Java.nio ByteBuffer字节将一个文件输出至另一文件
	 * 
	 * @param filePath
	 */
	public static void readFileByBybeBuffer(String filePath) {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			// 获取源文件和目标文件的输入输出流
			in = new FileInputStream(filePath);
			out = new FileOutputStream("d:/work/readFileByBybeBuffer.txt");
			// 获取输入输出通道
			FileChannel fcIn = in.getChannel();
			FileChannel fcOut = out.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				// clear方法重设缓冲区，使它可以接受读入的数据
				buffer.clear();
				// 从输入通道中将数据读到缓冲区
				int r = fcIn.read(buffer);
				if (r == -1) {
					break;
				}
				// flip方法让缓冲区可以将新读入的数据写入另一个通道
				buffer.flip();
				fcOut.write(buffer);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null && out != null) {
				try {
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static long getTime() {
		return System.currentTimeMillis();
	}

	public static void main(String args[]) {
		long time1 = getTime();
		// readFileByByte(FILE_PATH);// 8734,8281,8,7781,8047
		// readFileByCharacter(FILE_PATH);// 734, 437, 437, 438, 422
		// readFileByLine(FILE_PATH);// 110, 94, 94, 110, 93
		readFileByBybeBuffer(FILE_PATH);// 125, 78, 62, 78, 62
		long time2 = getTime();
		System.out.println(time2 - time1);
	}
}