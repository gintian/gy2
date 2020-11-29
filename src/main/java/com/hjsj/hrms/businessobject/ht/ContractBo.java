package com.hjsj.hrms.businessobject.ht;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:ContractBo.java</p>
 * <p>Description:合同</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-03-11 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ContractBo
{
	/**数据库连接*/
	private Connection conn;
	
	public ContractBo(Connection con)
	{
		this.conn=con;		
	}
	UserView userView;
	public ContractBo(Connection con,UserView u)
	{
		this.conn=con;
		this.userView=u;
	}
	  public ArrayList searchNbase(HashMap nbases) throws GeneralException
	  {
	      ArrayList list = new ArrayList();
	      ContentDAO dao = new ContentDAO(this.conn);
	      try
		{
		    RowSet rs = dao.search("select * from dbname order by dbid");
		    while (rs.next())
		    {
			LazyDynaBean abean = new LazyDynaBean();
			String pre = rs.getString("pre");
			String dbname= rs.getString("dbname");
			
			abean.set("pre", pre);
			abean.set("dbname", dbname);
			if(nbases.get(pre.toLowerCase())==null)	
			    abean.set("dbsel", "0");
			else
			    abean.set("dbsel", "1");
			list.add(abean);
		    }

		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	      return list;
	  }
	  
	  public ArrayList searchNbase(HashMap nbases,UserView userview) throws GeneralException {
		  ArrayList list = new ArrayList();
	      ContentDAO dao = new ContentDAO(this.conn);
	      
	      try {
	    	  RowSet rs = dao.search("select * from dbname order by dbid");
	    	  while (rs.next()) {
	    		  LazyDynaBean abean = new LazyDynaBean();
	    		  String pre = rs.getString("pre");
	    		  String dbname= rs.getString("dbname");
	    		  if (!userview.isSuper_admin()){
	    		  if (userview.getDbpriv().indexOf(pre) != -1) {
	    			  abean.set("pre", pre);
	    			  abean.set("dbname", dbname);
	    			  if(nbases.get(pre.toLowerCase())==null) {	
	    				  abean.set("dbsel", "0");
	    			  } else {
	    				  abean.set("dbsel", "1");
	    			  }
	    			  
					list.add(abean);
				}
	    	  } else {
	    		  abean.set("pre", pre);
    			  abean.set("dbname", dbname);
    			  if(nbases.get(pre.toLowerCase())==null) {	
    				  abean.set("dbsel", "0");
    			  } else {
    				  abean.set("dbsel", "1");
    			  }
    			  
				list.add(abean); 
	    	  }
		    }

		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	      return list;
	  }
	  public HashMap searchNbase() throws GeneralException
	  {
	      HashMap map = new HashMap();
	      ContentDAO dao = new ContentDAO(this.conn);
	      try
		{
		    RowSet rs = dao.search("select * from dbname order by dbid");
		    while (rs.next())
		    {	
			String pre = rs.getString("pre");
			String dbname= rs.getString("dbname");
			map.put(pre, dbname);
		    }

		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	      return map;
	  }
	  public ArrayList searchCodeSet() throws GeneralException
	  {
	      ArrayList list = new ArrayList();
	      ContentDAO dao = new ContentDAO(this.conn);
	      try
		{
		    RowSet rs = dao.search("select * from codeset");
		    while (rs.next())
		    {
			CommonData temp=new CommonData(rs.getString("codesetid"),rs.getString("codesetdesc"));
			list.add(temp);
		    }

		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	      return list;
	  }
	  
	  public HashMap searchCodeSetMap() throws GeneralException
	  {
	      HashMap map = new HashMap();
	      ContentDAO dao = new ContentDAO(this.conn);
	      try
		{
		    RowSet rs = dao.search("select * from codeset");
		    while (rs.next())
		    {
			map.put(rs.getString("codesetid"),rs.getString("codesetdesc"));
		    }

		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	      return map;
	  }
	  
	  public ArrayList searchCodeSet(String codeSetId, UserView userView) throws GeneralException {
		  ArrayList list = new ArrayList();
	      ArrayList checklist = this.userView.getPrivFieldList(codeSetId, Constant.USED_FIELD_SET);
	      if (checklist.size() == 0) {
	    	  return list;
	      } else {
	    	 Map map = searchCodeSetMap();
	    	  for (int i = 0; i < checklist.size(); i++) {
	    		  FieldItem item = (FieldItem) checklist.get(i);
	    		  if (item.isCode()) {
	    			  String codeset = item.getCodesetid();
	    			  String desc = (String) map.get(codeset);
	    			  desc = desc==null?"":desc;
	    			  CommonData temp=new CommonData(codeset,item.getItemdesc());
	    			  list.add(temp);
	    		  }
	    	  }
	      }
					    
	      return list;
	  }
	  public ArrayList searchEmpSubSet() throws GeneralException
	  {
	      ArrayList list = new ArrayList();
	      ContentDAO dao = new ContentDAO(this.conn);
	      try
		{
		    RowSet rs = dao.search("select * from fieldset where fieldsetid like 'A%' and fieldsetid<>'A01' order by fieldsetid");
		    while (rs.next())
		    {
			String fieldsetid = rs.getString("fieldsetid");
			String fieldsetdesc = rs.getString("fieldsetdesc");
			
			String priv = this.userView.analyseTablePriv(fieldsetid);//无权限
			if("0".equals(priv))
			    continue;
			CommonData temp=new CommonData(fieldsetid,fieldsetid+":"+fieldsetdesc);
			list.add(temp);
		    }

		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	      return list;
	  }  
	  /**取得主集信息*/
	  public HashMap searchEmpInfo(String a0100,String db) throws GeneralException
	  {
	      HashMap abean = new HashMap();
	      ContentDAO dao = new ContentDAO(this.conn);
	      String strSql = "select * from "+db+"A01 where a0100='"+a0100+"'";
	      try
		{
		    RowSet rs = dao.search(strSql);
		    if (rs.next())
		    {			
			String b0110 = rs.getString("b0110");
			String e0122= rs.getString("e0122");
			String e01a1 = rs.getString("e01a1");
			String a0101= rs.getString("a0101");
			
			abean.put("b0110", b0110 != null ? AdminCode.getCodeName("UN", b0110) : "");
			abean.put("e0122", e0122 != null ? AdminCode.getCodeName("UM", e0122) : "");
			abean.put("e01a1", e01a1 != null ? AdminCode.getCodeName("@K", e01a1) : "");
			abean.put("a0101", a0101!= null ?a0101:"");
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	      return abean;
	  }
	  /**取得子集的匹配字段*/
	  public String getRelFld(String fieldsetid,String codesetid) throws GeneralException
	  {
	      String relFld = "";
	      String strSql = "select itemid from fielditem  where fieldsetid='"+fieldsetid.toUpperCase()+"' and codesetid='"+codesetid+"'";
	      ContentDAO dao = new ContentDAO(this.conn);
	      try
		{
		    RowSet rs = dao.search(strSql);
		    if (rs.next())		    
			relFld = rs.getString("itemid");	    

		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}	      
	      return relFld;
	  }
}
