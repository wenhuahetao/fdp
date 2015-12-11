package com.hetao.util;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpClientUtil {

	/**
	 * get http 
	 * @param param1
	 * @param param2
	 * @return
	 */
	public static String getHttp(String url,Map<String,String> paramMap) {
		String responseMsg = "";
		HttpClient httpClient = new HttpClient();
		
		StringBuffer params = new StringBuffer();
		if(paramMap.size()>0){
			params.append("?1=1");
		}
		for (String key : paramMap.keySet()) {
			params.append("&" + key+"="+paramMap.get(key));
		}
		url = url + params.toString();
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
		httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");//�����������
		try {
			httpClient.executeMethod(getMethod);
			byte[] responseBody = getMethod.getResponseBody();
			responseMsg = new String(responseBody,"UTF-8");

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return responseMsg;
	}
	
	/**
	 * post http
	 * @param param1
	 * @param param2
	 * @return
	 */
	public static String postHttp(String url,Map<String,String> paramMap) {
		String responseMsg = "";

		HttpClient httpClient = new HttpClient();
		httpClient.getParams().setContentCharset("GBK");
		PostMethod postMethod = new PostMethod(url);
		for (String key : paramMap.keySet()) {
			postMethod.addParameter(key, paramMap.get(key));
		}
		try {
			httpClient.executeMethod(postMethod);// 200
			responseMsg = postMethod.getResponseBodyAsString().trim();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		return responseMsg;
	}
}