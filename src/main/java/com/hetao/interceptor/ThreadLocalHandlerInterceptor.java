package com.hetao.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.hetao.util.ResourceUtils;

/**
 * 
 * @author hetao
 *
 */
public class ThreadLocalHandlerInterceptor implements HandlerInterceptor {

	@Autowired private MessageSource coreAppSetting;
	
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2) throws Exception {
		if (!ResourceUtils.isInit()) {
			ResourceUtils.initMessageSource(coreAppSetting);
		}
		return true;
	}
	
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}


	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
}
