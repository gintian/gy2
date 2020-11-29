/*
 * Created on 2005-9-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchOrganizationTrans</p>
 * <p>Description:查询岗位信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchOrganizationTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	       // String edition=(String)hm.get("edition");
	       // hm.remove("edition");  
		 	String edition="2";
		 	String a_code=(String)hm.get("a_code");
	        StringBuffer cond_str=new StringBuffer();
	        /**相关代码类及代码值*/
	        if(a_code==null|| "".equals(a_code))
	            a_code="UN";
	        String codevalue=a_code.substring(2);
	        if("UN".equals(a_code))
	        {
	            cond_str.append(" where  organization.parentid=organization2.codeitemid and  organization.codesetid = '@K' and organization.parentid like '");
	            cond_str.append(this.userView.getManagePrivCodeValue());
	            cond_str.append("%'");
	        }
	        else if((a_code.indexOf("UM") != -1) || (a_code.indexOf("UN") != -1)) 
	        {
	            cond_str.append(" where organization.parentid=organization2.codeitemid and  organization.parentid like '");
	            cond_str.append(codevalue);
	            cond_str.append("%' and organization.codesetid = '@K'");      
	        }
	        else if(a_code.indexOf("@K") != -1) 
	        {
	            cond_str.append(" where organization.parentid=organization2.codeitemid and organization.codeitemid like '");
	            cond_str.append(codevalue);
	            cond_str.append("%' and organization.codesetid = '@K'");      
	        }
	        
	        /**招聘2版扩展 （显示用工需求中的职位（不包括结束的招聘计划 相关联的职位)）*/
	       if(edition!=null&& "2".equals(edition))
	        {
	        	cond_str.append(" and organization.codeitemid in (select z0311 from z03 where z0101 is null or z0101='' ");
	        	cond_str.append("union select z0311 from z03,z01 where z03.z0101=z01.z0101 and z01.z0129!='06' )");
	        }
	        
	        
	        
	        /**查询条件*/
	       if(edition!=null&& "2".equals(edition))
	    	   this.getFormHM().put("cond_str","  from organization  ,(select * from organization where codesetid='UM') organization2 "+cond_str.toString());
	       else
	    	   this.getFormHM().put("cond_str","  from organization  ,(select * from organization where codesetid='UM') organization2 "+cond_str.toString());
	       
	      StringBuffer strsql=new StringBuffer();   
	      if(edition!=null&& "2".equals(edition))
	        strsql.append("select organization.codeitemid,organization.codeitemdesc ,organization2.codeitemdesc depart,CASE WHEN organization.pos_cond is null THEN '无'  ELSE '有' END flag");
	      else
	    	  strsql.append("select organization.codeitemid,organization.codeitemdesc ,organization2.codeitemdesc depart,CASE WHEN organization.pos_cond is null THEN '无'  ELSE '有' END flag");  
	        
	      this.getFormHM().put("sql_str",strsql.toString());
	       strsql.setLength(0);
	        
	     if(edition!=null&& "2".equals(edition))
	        strsql.append("codeitemid,codeitemdesc,depart,flag");
	        this.getFormHM().put("columns",strsql.toString());
		}

}
