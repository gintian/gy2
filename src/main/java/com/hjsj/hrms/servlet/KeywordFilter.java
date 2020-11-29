package com.hjsj.hrms.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class KeywordFilter  implements Filter {
	
	public void destroy() { 
		
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		 
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest _request=(HttpServletRequest) request;
		if("/ajax/ajaxService".equalsIgnoreCase(_request.getRequestURI()))
			_request.setCharacterEncoding("UTF-8"); 
		else
			_request.setCharacterEncoding("GBK"); 
		if(SystemConfig.getPropertyValue("webserver")!=null&&("weblogic".equalsIgnoreCase(SystemConfig.getPropertyValue("webserver").trim())|| "websphere".equalsIgnoreCase(SystemConfig.getPropertyValue("webserver").trim())))
		{
			HashMap map = new HashMap(_request.getParameterMap());
			String param = "";
	        String paramValue = "";
			 //通过继承HttpServletRequestWrapper类转化
	        ParameterRequestWrapper wrapRequest = new ParameterRequestWrapper(_request, map);
	        java.util.Enumeration params = wrapRequest.getParameterNames();
	        Map requestParams = wrapRequest.getParameterMap();
	        while (params.hasMoreElements()) {
	            param = (String) params.nextElement();
	            
	            if("__xml".equalsIgnoreCase(param)|| "__type".equalsIgnoreCase(param))
		        { 
		        	 continue;
		        } 
	            if(requestParams.get(param)!=null)
	            {
		            String[] values =(String[]) requestParams.get(param);// 获得每个参数的value
		            for (int i = 0; i < values.length; i++) {
		            	if(values[i]==null)
		 		    		 continue;
		                paramValue = values[i]; 
		                boolean flag=false;
				    	if(paramValue.indexOf("<@>")!=-1)
				    	{
				    		paramValue=paramValue.replaceAll("<@>","~~");
				    		 flag=true;
				        }
				    	paramValue= PubFunc.keyWord_filter(paramValue);
				    	if(flag)
				    		paramValue=paramValue.replaceAll("~~","<@>"); 
		                values[i] = paramValue;
		            }
		            // 把转义后的参数重新放回request中
		            requestParams.put(param, values);
	            }
	        } 
	        chain.doFilter(wrapRequest, response); 
			
		}
		else
		{ 
	 	
			Map map = _request.getParameterMap(); 
			Set set = map.entrySet();
			if(map!= null)
		    {
			    for(Iterator it = set.iterator();it.hasNext();)
			    {
				     Map.Entry entry = (Entry) it.next();
				     
				   
				     if(entry.getKey()!=null&&entry.getKey() instanceof String)
				     {	
				    	 String key=(String)entry.getKey(); 
				         if("__xml".equalsIgnoreCase(key)|| "__type".equalsIgnoreCase(key))
				         {
				        //	 System.out.println(key+":   "+_request.getParameter(key));
				        	 continue;
				         }
				     }
				     
				     if(entry.getValue()!=null&&entry.getValue() instanceof String[])
				     { 
				       String[] values = (String[]) entry.getValue();  
		 		       for(int i = 0 ; i < values.length ; i++)
		 		       { 
			 		    	 boolean flag=false;
			 		    	 if(values[i]==null)
			 		    		 continue;
			 		    	 if(values[i].indexOf("<@>")!=-1)
			 		    	 {
			 		    		 values[i]=values[i].replaceAll("<@>","~~");
			 		    		 flag=true;
			 		    	 }
			 		    	 values[i] = PubFunc.keyWord_filter(values[i]);
			 		    	 if(flag)
			 		    		 values[i]=values[i].replaceAll("~~","<@>"); 
		 		       }
				       entry.setValue(values);
				     }
	
			    }
			 } 
			chain.doFilter(request, response); 
		}
		 
		
		
		
		 
	}
 
     
}
