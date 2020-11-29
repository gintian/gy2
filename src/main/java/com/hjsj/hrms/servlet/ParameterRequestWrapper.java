package com.hjsj.hrms.servlet;

import com.hjsj.hrms.utils.PubFunc;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private Map params;
    private HashMap queryKeyMap=new HashMap(); //url链接参数
    private byte[] body; // 报文  
    

    public ParameterRequestWrapper(HttpServletRequest request, Map newParams) {
        super(request);
        try
        { 
        	/*inputstream无法重复读取，此处缓存inputstream数据，实现重复使用 guodd 2018-11-03*/
        	body = IOUtils.toByteArray(request.getInputStream());
        	
	        this.queryKeyMap=getQueryKeyMap(request);
	        this.params = newParams;
	        if(this.params.get("encryptParam")!=null)
	        {
	        	 String[] values = this.getParameterValues("encryptParam");  
			     String encryptParam_value=PubFunc.decrypt(values[0]); 
			     //xuj add xtree上的action链接需要将&amp;转换为&
			     encryptParam_value = encryptParam_value.replaceAll("&amp;", "&");
			     String[] args=encryptParam_value.split("&");
			     for(int i=0;i<args.length;i++)
			     {
			    	   if(args[i]!=null&&args[i].trim().length()>0)
			    	   {
			    		   //可能参数值中含有=
			    		   int index = args[i].indexOf("=");
			    		   if(index>-1){
			    			   String key =args[i].substring(0,index);
			    			   String value = args[i].substring(index+1);
			    			   this.params.put(key,new String[] {value});
			    			   this.queryKeyMap.put(key,"1");
			    		   }
			    	   }
			     }
			     this.params.remove("encryptParam");
	        }else if (this.params.get("amp;encryptParam")!=null)
	        {
	       	 String[] values = this.getParameterValues("amp;encryptParam");  
			     String encryptParam_value=PubFunc.decrypt(values[0]); 
			     //xuj add xtree上的action链接需要将&amp;转换为&
			     encryptParam_value = encryptParam_value.replaceAll("&amp;", "&");
			     String[] args=encryptParam_value.split("&");
			     for(int i=0;i<args.length;i++)
			     {
			    	   if(args[i]!=null&&args[i].trim().length()>0)
			    	   {
			    		   //可能参数值中含有=
			    		   int index = args[i].indexOf("=");
			    		   if(index>-1){
			    			   String key =args[i].substring(0,index);
			    			   String value = args[i].substring(index+1);
			    			   this.params.put(key,new String[] {value});
			    			   this.queryKeyMap.put(key,"1");
			    		   }
			    	   }
			     }
			     this.params.remove("amp;encryptParam");
	       }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }
    
    /**
     * 获得链接参数
     * @param request
     * @return
     */
    public HashMap getQueryKeyMap(HttpServletRequest request)
    {
    	HashMap map=new HashMap();
    	String urlValues=request.getQueryString();
    	if(urlValues!=null&&urlValues.length()>0)
    	{
    		String[] values=urlValues.split("&");
    		for(int i=0;i<values.length;i++)
    		{
    			if(values[i]!=null&&values[i].trim().length()>0)
    			{
    				String[] args=values[i].split("=");
    				map.put(args[0].trim(),"1");
    			
    			}
    		}
    	}
    	map.remove("encryptParam");
    	map.remove("amp;encryptParam"); 
    	return map;
    }
    
   
    public void addParameter( String name, String value ) {
        params.put( name, value );
    }

    /** *//**
     * 直接接受request
     * 注：request中提供的getQueryString方法只对Get方法才能生效
     * @param request
     * @return
     */
    public   String getQueryString() {
        boolean first = true;
        StringBuffer strbuf = new StringBuffer("");
        Enumeration emParams = this.getParameterNames();
       
        do {
            if (!emParams.hasMoreElements()) {
                break;
            } 
            String sParam = (String) emParams.nextElement();
            
            if("encryptParam".equalsIgnoreCase(sParam))
		    {
	 		       continue;
		    }
           if(this.queryKeyMap.get(sParam.trim())==null)
            	continue;
            String[] sValues = this.getParameterValues(sParam);  
            String sValue = "";            
            for (int i = 0; i < sValues.length; i++) {
                sValue = sValues[i];
                if (sValue != null && sValue.trim().length() != 0 && first == true) {
                    //第一个参数
                    first = false;                    
                    strbuf.append(sParam).append("=").append(sValue);
                    break;	// 如果是传过来的参数是数组，那么只取第一个值(薪资发放-引入单位、部门变动人员-复杂查询结果错误问题) xiaoyun 2014-9-28
                } else if (sValue != null && sValue.trim().length() != 0 && first == false) {
                    strbuf.append("&").append(sParam).append("=").append(sValue);
                    break; // 如果是传过来的参数是数组，那么只取第一个值(薪资发放-引入单位、部门变动人员-复杂查询结果错误问题) xiaoyun 2014-9-28
                }
                else if(sValue.trim().length() == 0)  //解决key没有value时报错问题  dengcan upd
                {
                 
                    if(sParam!=null&&sParam.length()!=0)//如果sParam为空，则乱安装等号   zhaoxg add 2014-9-9
                    {
                    	if(strbuf.length()!=0)
                               strbuf.append("&");
                    	strbuf.append(sParam).append("=");
                    	first = false; 
                    	break; // 如果是传过来的参数是数组，那么只取第一个值(薪资发放-引入单位、部门变动人员-复杂查询结果错误问题) xiaoyun 2014-9-28
                    }
                }
            }
        } while (true);

      
        
        return strbuf.toString();
    }
   


    public Map getParameterMap() {
        return params;
    }

    public Enumeration getParameterNames() {
        Vector l = new Vector(params.keySet());
        return l.elements();
    }

    public String[] getParameterValues(String name) {
        Object v = params.get(name);
        if (v == null) {
            return null;
        } else if (v instanceof String[]) {
            return (String[]) v;
        } else if (v instanceof String) {
            return new String[] { (String) v };
        } else {
            return new String[] { v.toString() };
        }
    }

    public String getParameter(String name) {
        Object v = params.get(name);
        if (v == null) {
            return null;
        } else if (v instanceof String[]) {
            String[] strArr = (String[]) v;
            if (strArr.length > 0) {
                return strArr[0];
            } else {
                return null;
            }
        } else if (v instanceof String) {
            return (String) v;
        } else {
            return v.toString();
        }
    }

    /**
     * 重写方法，读取缓存数据，实现request的inputstream重复使用
     */
    public BufferedReader getReader() throws IOException {  
        return new BufferedReader(new InputStreamReader(getInputStream()));  
    }  
      
    /**
     * 重写方法，读取缓存数据，实现request的inputstream重复使用
     */
    public ServletInputStream getInputStream() throws IOException {  
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);  
        return new ServletInputStream() {

            @Override
            public int read() throws IOException {  
                return bais.read();  
            }  
        };  
    }  
    
}


