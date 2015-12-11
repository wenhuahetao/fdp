package com.hetao.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import com.fline.hadoop.data.client.HDFSDataManager;
import com.hetao.util.ResourceUtils;


public class HdfsServiceTest {
	
	@Test
	public void testBufferedReader(){
		FileReader in = null;
		BufferedReader read = null;
		String s = null;
		BufferedWriter writer = null;
		int iLine = 1;
		try {
			in = new FileReader("D:\\project\\workspace\\work\\fline_hdfs\\src\\main\\webapp\\upload\\test.csv");
			read = new BufferedReader(in);
			writer = new BufferedWriter(new FileWriter("D:\\project\\workspace\\work\\fline_hdfs\\src\\main\\webapp\\upload\\test1.csv"));
			while ((s = read.readLine()) != null) {
				if(iLine!=2){
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
		} finally {
			try {
				writer.close();
				read.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
}
