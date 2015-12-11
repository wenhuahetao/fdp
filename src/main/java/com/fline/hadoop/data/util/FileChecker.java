package com.fline.hadoop.data.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.fline.hadoop.data.common.HDFSOperator;
import com.fline.hadoop.data.common.analysis.impl.TokenAnalyzer;

/**
 * FileChecker is used to check whether file has same words in neighbor Or same
 * line in whole text Or segTypes in the same column. And so on.
 * 
 * @author zhongliang
 * 
 */
public class FileChecker {
	// check code.
	public static final int SMARTCN_CHECKER = 1;
	public static final int LOG_CHECKER = 2; // check line whether is empty
	public static final int CSV_CHECKER = 3;
	public static final int EMPTY_FILE = 4;
	private static final String suffix = ".CHECK";
	public static final String CHECK_REPEAT_GAP = "@repeat@";
	public static final String CHECK_NORMAL_REPEAT_GAP = "@repeatchar@"; // normal
																			// file
																			// check,
																			// currently
																			// check
																			// repeat
																			// word
	public static final String CHECK_TYPE_GAP = "@typeError@";
	public static final String CHECK_EMPTY_GAP = "@emptyCSV@";
	public static final String CHECK_EMPTY_LINE_GAP = "@emptyLine@";

	// csv check - segType
	public static final int CSV_STRING_TYPE = 1;
	public static final int CSV_NUM_TYPE = 2;
	public static final int CSV_DATE_TYPE = 3;
	private static Pattern numPattern = Pattern
			.compile("[\\+\\-]?[0-9]+(\\.[0-9]+)?");
	private static Pattern datePattern = Pattern
			.compile("[0-9]+[\\-\\/年][0-9]+[\\-\\/月][0-9]+");

	private static final String CHECK_HDFS_HOME = "/user/check";
	private static CharArraySet stopSet = null;
	static {
		// load stopwords, when load the class.
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(FileChecker.class
							.getClassLoader().getResource("").getPath()
							+ "stopwords.txt"))));
			List<String> words = new ArrayList<String>();
			while (reader.ready()) {
				words.add(reader.readLine());
			}
			reader.close();
			stopSet = new CharArraySet(words, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Check File with checkcode =
	 * CodeSet{SMARTCN_CHECKER,LINE_CHECKER,CSV_CHECKER,EMPTY_FILE} if checkcode
	 * is not in CodeSet, then do nothing.
	 * 
	 * @param localfilepath
	 *            sourcepath
	 * @param hdfspath
	 *            hdfspath is used to generate the check file path on hdfs.
	 * @param checkCode
	 *            how to check the file.
	 * @throws Exception
	 */
	public static void checkFile(String localfilepath, String hdfspath,
			int checkCode) throws Exception {
		String content = checkFile(localfilepath, checkCode);
		if (content.length() > 0) {
			HDFSOperator.getDefaultInstance().writeFileContent2HDFS(
					content.getBytes(), getCheckFilePath(hdfspath), true);
		}
	}

	@SuppressWarnings("deprecation")
	private static String checkFileWithSMARTCH(String localfilepath)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		String lastString = null;
		BufferedReader smartbr = null;
		if (localfilepath.toLowerCase().endsWith("docx")) {
			FileInputStream fis = new FileInputStream(new File(localfilepath));
			XWPFWordExtractor extractor = new XWPFWordExtractor(
					new XWPFDocument(fis));
			smartbr = new BufferedReader(new StringReader(extractor.getText()));
			fis.close();
			extractor.close();
		} else if (localfilepath.toLowerCase().endsWith("doc")) {
			FileInputStream fis = new FileInputStream(new File(localfilepath));
			WordExtractor extractor = new WordExtractor(fis);
			smartbr = new BufferedReader(new StringReader(extractor.getText()));
			fis.close();
			extractor.close();
		} else {
			smartbr = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(localfilepath))));
		}

		// SmartChineseAnalyzer sca = new SmartChineseAnalyzer(
		// Version.LUCENE_4_9_0, false);
		SmartChineseAnalyzer sca = new SmartChineseAnalyzer(
				Version.LUCENE_4_9_0, stopSet);
		TokenStream ts = sca.tokenStream("field", smartbr);
		CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
		TokenAnalyzer tokenAnalyzer = new TokenAnalyzer();
		ts.reset();
		while (ts.incrementToken()) {
			if (lastString == null) {
				lastString = ch.toString();
			} else {
				int start = ((OffsetAttribute) ch).startOffset();
				if (tokenAnalyzer.compareSimilarity(ch.toString(), lastString) > 0.999) {
					sb.append(start);
					sb.append(CHECK_NORMAL_REPEAT_GAP);
					sb.append(ch.toString());
					sb.append("\n");
				}
				lastString = ch.toString();
			}
		}
		smartbr.close();
		sca.close();
		return sb.toString();
	}

	private static String checkFileWithLine(String localfilepath)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader linebr = null;
		if (localfilepath.toLowerCase().endsWith("docx")) {
			FileInputStream fis = new FileInputStream(new File(localfilepath));
			XWPFWordExtractor extractor = new XWPFWordExtractor(
					new XWPFDocument(fis));
			linebr = new BufferedReader(new StringReader(extractor.getText()));
			fis.close();
			extractor.close();
		} else if (localfilepath.toLowerCase().endsWith("doc")) {
			FileInputStream fis = new FileInputStream(new File(localfilepath));
			WordExtractor extractor = new WordExtractor(fis);
			linebr = new BufferedReader(new StringReader(extractor.getText()));
			fis.close();
			extractor.close();
		} else {
			linebr = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(localfilepath))));
		}
		int linenum = 1;
		while (linebr.ready()) {
			String line = linebr.readLine();
			if (line.isEmpty()) {
				sb.append(linenum);
				sb.append(CHECK_EMPTY_LINE_GAP);
				sb.append(line);
				sb.append('\n');
			}
			linenum++;
		}
		linebr.close();
		return sb.toString();
	}

	private static String checkFileWithCSV(String localfilepath)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(localfilepath))));
		Map<String, Integer> lineMap = new HashMap<String, Integer>();
		Map<Integer, Map<Integer, Integer>> SegTypes = new HashMap<Integer, Map<Integer, Integer>>();
		List<int[]> lineSegBuffer = new ArrayList<int[]>();
		int csvlinenum = 1;
		int totalline = 0;
		while (br.ready()) {
			String line = br.readLine();
			Integer lineNum = lineMap.get(line);
			// line repeat
			if (lineNum != null) {
				sb.append(csvlinenum);
				sb.append(CHECK_REPEAT_GAP);
				sb.append(lineNum);
				sb.append('@');
				sb.append(line);
				sb.append('\n');
			} else {
				lineMap.put(line, csvlinenum);
				String[] splits = line.split("\t", 100);
				int spaceSegNum = 0;
				int[] lineSeg = new int[splits.length];
				for (int i = 0; i < splits.length; i++) {
					String split = splits[i];
					if (split.isEmpty()) {
						spaceSegNum++;
					} else {
						Matcher m = numPattern.matcher(split);
						if (m.matches()) {
							Map<Integer, Integer> map = SegTypes.get(i);
							if (map == null) {
								map = new HashMap<Integer, Integer>();
								SegTypes.put(i, map);
							}
							Integer num = map.get(CSV_NUM_TYPE);
							if (num == null) {
								map.put(CSV_NUM_TYPE, 1);
							} else {
								map.put(CSV_NUM_TYPE, 1 + num);
							}
							lineSeg[i] = CSV_NUM_TYPE;
						} else {
							Matcher datem = datePattern.matcher(split);
							if (datem.find()) {
								Map<Integer, Integer> map = SegTypes.get(i);
								if (map == null) {
									map = new HashMap<Integer, Integer>();
									SegTypes.put(i, map);
								}
								Integer num = map.get(CSV_DATE_TYPE);
								if (num == null) {
									map.put(CSV_DATE_TYPE, 1);
								} else {
									map.put(CSV_DATE_TYPE, 1 + num);
								}
								lineSeg[i] = CSV_DATE_TYPE;
							} else {
								Map<Integer, Integer> map = SegTypes.get(i);
								if (map == null) {
									map = new HashMap<Integer, Integer>();
									SegTypes.put(i, map);
								}
								Integer num = map.get(CSV_STRING_TYPE);
								if (num == null) {
									map.put(CSV_STRING_TYPE, 1);
								} else {
									map.put(CSV_STRING_TYPE, 1 + num);
								}
								lineSeg[i] = CSV_STRING_TYPE;
							}
						}
					}
				}
				// spaceSegNum > 50% * splits.length
				if (spaceSegNum * 2 > splits.length) {
					sb.append(csvlinenum);
					sb.append(CHECK_EMPTY_GAP);
					sb.append(line);
					sb.append('\n');
					lineSegBuffer.add(null);
				} else {
					lineSegBuffer.add(lineSeg);
					totalline++;
				}
			}
			csvlinenum++;
		}
		Set<Integer> keys = SegTypes.keySet();
		for (Integer key : keys) {
			Map<Integer, Integer> tmpresult = SegTypes.get(key);
			Set<Integer> tmpkeys = tmpresult.keySet();
			int maxNum = 0;
			int maxType = 0;
			for (Integer tmpkey : tmpkeys) {
				if (tmpresult.get(tmpkey) > maxNum) {
					maxNum = tmpresult.get(tmpkey);
					maxType = tmpkey;
				}
			}
			if (maxNum > totalline * 0.9) {
				for (int index = 0; index < lineSegBuffer.size(); index++) {
					if (lineSegBuffer.get(index) == null) {
						continue;
					}
					if (lineSegBuffer.get(index)[key] != maxType) {
						sb.append(index + 1);
						sb.append(CHECK_TYPE_GAP);
						sb.append(key + 1);
						sb.append('@');
						sb.append(maxType);
						sb.append('@');
						sb.append(lineSegBuffer.get(index)[key]);
						sb.append('\n');
					}
				}
			}
		}
		br.close();
		lineMap.clear();
		SegTypes.clear();
		return sb.toString();
	}

	private static String checkFileWithEmpty(String localfilepath)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		if (localfilepath.toLowerCase().endsWith("docx")) {
			FileInputStream fis = new FileInputStream(new File(localfilepath));
			XWPFWordExtractor extractor = new XWPFWordExtractor(
					new XWPFDocument(fis));
			if (extractor.getText().length() <= 0) {
				sb.append(CHECK_REPEAT_GAP);
			}
			fis.close();
			extractor.close();
		} else if (localfilepath.toLowerCase().endsWith("doc")) {
			FileInputStream fis = new FileInputStream(new File(localfilepath));
			WordExtractor extractor = new WordExtractor(fis);
			if (extractor.getText().length() <= 0) {
				sb.append(CHECK_REPEAT_GAP);
			}
			fis.close();
			extractor.close();
		} else {
			FileInputStream tmpfis = new FileInputStream(
					new File(localfilepath));
			if (tmpfis.available() <= 0) {
				sb.append(CHECK_REPEAT_GAP);
			}
			tmpfis.close();
		}
		return sb.toString();
	}

	public static String checkFile(String localfilepath, int checkCode)
			throws Exception {
		switch (checkCode) {
		case SMARTCN_CHECKER:
			return checkFileWithSMARTCH(localfilepath);
		case LOG_CHECKER:
			return checkFileWithLine(localfilepath);
		case CSV_CHECKER:
			return checkFileWithCSV(localfilepath);
		case EMPTY_FILE:
			return checkFileWithEmpty(localfilepath);
		default:
			return null;
		}
	}

	/**
	 * auto generate file label. This method will generate the label
	 * corresponding to filepath. currently , we support file = doc, docx and
	 * txt. all other file will be handled as txt.
	 * 
	 * @param filepath
	 *            source file path
	 * @return
	 * @throws Exception
	 */
	public static String getAutoLabel(String filepath) throws Exception {
		File file = new File(filepath);
		BufferedReader smartbr = null;
		if (filepath.toLowerCase().endsWith("docx")) {
			FileInputStream fis = new FileInputStream(new File(filepath));
			XWPFWordExtractor extractor = new XWPFWordExtractor(
					new XWPFDocument(fis));
			smartbr = new BufferedReader(new StringReader(extractor.getText()));
			fis.close();
			extractor.close();
		} else if (filepath.toLowerCase().endsWith("doc")) {
			FileInputStream fis = new FileInputStream(new File(filepath));
			WordExtractor extractor = new WordExtractor(fis);
			smartbr = new BufferedReader(new StringReader(extractor.getText()));
			fis.close();
			extractor.close();
		} else {
			smartbr = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
		}

		@SuppressWarnings("deprecation")
		SmartChineseAnalyzer sca = new SmartChineseAnalyzer(
				Version.LUCENE_4_9_0, stopSet);
		TokenStream ts = sca.tokenStream("field", smartbr);
		CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
		ts.reset();
		Map<String, Integer> token2size = new HashMap<String, Integer>();
		while (ts.incrementToken()) {
			String token = ch.toString();
			if (token.length() <= 1) {
				continue;
			}
			Integer size = token2size.get(token);
			if (size == null) {
				token2size.put(token, 1);
			} else {
				token2size.put(token, size + 1);
			}
		}
		smartbr.close();
		ts.close();

		String filename = file.getName();
		int fileNameEndtPoint = filename.lastIndexOf('.'); // used to set the
															// end of filename
															// without suffix
		String filenameWithOutSuffix = null;
		if (fileNameEndtPoint >= 0) {
			filenameWithOutSuffix = filename.substring(0, fileNameEndtPoint);
		} else {
			filenameWithOutSuffix = filename;
		}
		TokenStream filenamets = sca
				.tokenStream("field", filenameWithOutSuffix);
		CharTermAttribute filenamech = filenamets
				.addAttribute(CharTermAttribute.class);
		filenamets.reset();
		while (filenamets.incrementToken()) {
			String token = filenamech.toString();
			if (token.length() <= 1) {
				continue;
			}
			token2size.put(token, Integer.MAX_VALUE);
		}
		Set<String> keys = token2size.keySet();
		int switched = 10;
		String[] tokens = new String[switched];
		int[] sizes = new int[switched];
		int index = 0;
		for (String key : keys) {
			if (index < switched) {
				tokens[index] = key;
				sizes[index] = token2size.get(key);
				index++;
			} else {
				int minSize = Integer.MAX_VALUE;
				int minIndex = -1;
				for (int i = 0; i < sizes.length; i++) {
					if (minSize > sizes[i]) {
						minSize = sizes[i];
						minIndex = i;
					}
				}
				int tokensize = token2size.get(key);
				if (minSize < tokensize) {
					tokens[minIndex] = key;
					sizes[minIndex] = tokensize;
				}
			}
		}
		sca.close();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			sb.append(tokens[i]);
			sb.append('\t');
		}
		return sb.toString();
	}

	public static String getCheckFilePath(String hdfspath) {
		return CHECK_HDFS_HOME + hdfspath + suffix;
	}

	public static String codeString(String fileName) throws Exception {
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

	/**
	 * trans File Code to targetCode.
	 * 
	 * @param filepath
	 * @param targetCode
	 * @throws Exception
	 */
	public static void transFileCode(String filepath, String targetCode)
			throws Exception {
		String lowercase = filepath.toLowerCase();
		// do not handle special document.
		if (lowercase.endsWith("doc") || lowercase.endsWith("docx")) {
			return;
		}
		String codestring = codeString(filepath);
		// if code is not the same, then trans, else do nothing
		if (targetCode.equalsIgnoreCase(codestring) == false) {
			File sourcefile = new File(filepath);
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					sourcefile), codestring);
			String tmppath = filepath + ".tmp";
			File destfile = new File(tmppath);
			OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(destfile), targetCode);
			char[] buf = new char[4096];
			int readnum = 0;
			while (isr.ready()) {
				readnum = isr.read(buf);
				osw.write(buf, 0, readnum);
			}
			isr.close();
			osw.close();
			boolean delete = sourcefile.delete();
			boolean renameTo = destfile.renameTo(sourcefile);
			if (delete && renameTo == false) {
				throw new Exception("Trans File Code Failed.  sourcecode ="
						+ codestring + "\ttargetCode=" + targetCode);
			}
		}

	}

	public static void main(String[] args) throws Exception {
		System.out.println(FileChecker.checkFile("D:\\project\\workspace\\work/test2.csv",
				LOG_CHECKER));
		// System.out.println(FileChecker.getAutoLabel("./testinput/v1.0.0.doc"));
		// String codestring =
		// FileChecker.codeString("./testinput/决策分析系统-会议纪要2015-08-27.doc");
		// BufferedReader br = new BufferedReader(new InputStreamReader(
		// new FileInputStream(new File("./testinput/345.txt")),
		// codestring));
		// while (br.ready()) {
		// System.out.println(br.readLine());
		// }
		// br.close();
		// System.out.println(new String("english".getBytes(),
		// "ANSI_X3.4-1968"));
	}

	public static class FileCheckResult {

	}
}
