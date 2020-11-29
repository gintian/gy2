package com.hjsj.hrms.businessobject.info;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class AgentsetUtils {

	private Connection conn;
	public AgentsetUtils(Connection conn)
	{
		this.conn=conn;
	}
	public String getFunctionprivStr(String id)
	{
	    String sql="select functionpriv from agent_set where id="+id+"";
	    ContentDAO dao=new ContentDAO(this.conn);
	    RowSet rs=null;
	    String funct_str="";
	    try
	    {
	    	rs=dao.search(sql);
	    	if(rs.next()) {
                funct_str=Sql_switcher.readMemo(rs,"functionpriv");
            }
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally{
	    	if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	    }
	    return funct_str;
	}
	public String getWarnprivStr(String id)
	{
	    String sql="select warnpriv from agent_set where id="+id+"";
	    ContentDAO dao=new ContentDAO(this.conn);
	    RowSet rs=null;
	    String warn_str="";
	    try
	    {
	    	rs=dao.search(sql);
	    	if(rs.next()) {
                warn_str=Sql_switcher.readMemo(rs,"warnpriv");
            }
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }finally{
	    	if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	    }
	    return warn_str;
	}
	/**
	 * 分析设置的用于工作计划和总结的参数
	 */
	public HashMap analyseParameter(String warn_priv_str)
	{
		
		RowSet rs =null;
		HashMap warnPrivMap = new HashMap();
		try
		{
			warnPrivMap.clear();
		    
		    	if (warn_priv_str == null || warn_priv_str.trim().length()<=0)
		    	{
	
		    	} else
		    	{
		    		Document doc = PubFunc.generateDom(warn_priv_str);
		    		String xpath = "//warn_priv";
		    		XPath xpath_ = XPath.newInstance(xpath);
		    		Element ele = (Element) xpath_.selectSingleNode(doc);
		    		if (ele != null)
		    		{
		    			String rsbd = ele.getAttributeValue("rsbd");
		    			warnPrivMap.put("rsbd", rsbd);
		    			String gzbd = ele.getAttributeValue("gzbd");
		    			warnPrivMap.put("gzbd", gzbd);
		    			String ins_bd = ele.getAttributeValue("ins_bd");
		    			warnPrivMap.put("ins_bd", ins_bd);
		    		}
		    	}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return warnPrivMap;
	}
}
