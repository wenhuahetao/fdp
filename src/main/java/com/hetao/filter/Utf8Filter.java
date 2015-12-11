package com.hetao.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class Utf8Filter implements Filter {

	private String encoding=null;
	public void init(FilterConfig filterConfig) throws ServletException {		
		encoding=filterConfig.getInitParameter("encoding");
	}
	class heyley extends HttpServletRequestWrapper{
		String utf=null;
		public String getParameter(String name){
			try {
				if(super.getParameter(name)!=null){
					utf=new String(super.getParameter(name).getBytes("iso8859-1"),encoding);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return utf;			
		}
		public heyley(HttpServletRequest request) {
			super(request);
		}		
	}
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		request=new heyley((HttpServletRequest)request);
		chain.doFilter(request, response);
	}

	public void destroy() {		
	}

}
