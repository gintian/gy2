/*
 * Created on 2005-6-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchInfoDataTrans</p>
 * <p>Description:查询面试信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchInfoDataTrans extends IBusiness {	
	 /**取得自助用户登录名字段*/
    private String getLoginUserName()
    {
        /**登录参数表,登录用户指定不是username or userpassword*/
        String username=null;
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        if(login_vo==null)
        {
            username="username";
        }
        else
        {
            String login_name = login_vo.getString("str_value").toLowerCase();
            int idx=login_name.indexOf(",");

            if(idx==-1)
            {
                username="username";
            }
            else
            {
                username=login_name.substring(0,idx);
                if("#".equals(username)|| "".equals(username))
                	username="username";
            }
            
        }  
        return username;
    }
    
    /**求得登录用户的应用库列表*/
    private ArrayList getLoginBaseList()throws GeneralException
    {
        /**登录参数表*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
        String A01 = login_vo.getString("str_value");
        /**系统所有存在的数据库列表usr,oth,trs,ret*/
        StringBuffer strsql=new StringBuffer();
        strsql.append("select pre,dbname from dbname");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList dblist=new ArrayList();
        try
        {
            this.frowset=dao.search(strsql.toString());
            while(this.frowset.next())
            {
                String dbpre=this.frowset.getString("pre");
                /**权限分析*/
                if((A01.indexOf(dbpre)!=-1)&&userView.hasTheDbName(dbpre))
                {
                    CommonData vo=new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
                    dblist.add(vo);
                }
            }
            if(dblist.size()==0)
            {
                CommonData vo=new CommonData("usr",ResourceFactory.getProperty("label.sys.userbase"));
                dblist.add(vo);                
            }
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(sqle);                
        }
        return dblist;
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        String a_code=(String)hm.get("a_code");
        StringBuffer cond_str=new StringBuffer();
        String dbpre=(String)this.getFormHM().get("dbpre");
        /**相关代码类及代码值*/
        try
        {
	        if(a_code==null|| "".equals(a_code))
	            a_code="UN";
	        String codesetid=a_code.substring(0,2);
	        String codevalue=a_code.substring(2);
	        StringBuffer  strexpr=new StringBuffer();
	        StringBuffer  strfactor=new StringBuffer();
	        if("UN".equals(codesetid))
	        {
	            strfactor.append("B0110=");
	            strfactor.append(codevalue);
	            strfactor.append("*`");
	            strexpr.append("1");
	        }
	        else if("UM".equals(codesetid))
	        {
	            strfactor.append("E0122=");
	            strfactor.append(codevalue);
	            strfactor.append("*`");
	            strexpr.append("1");            
	        }
	        else if("@K".equals(codesetid))
	        {
	            strfactor.append("E01A1=");
	            strfactor.append(codevalue);
	            strfactor.append("*`");
	            strexpr.append("1");            
	        }
	        else
	        {
	           strfactor.append("B0110=*`");
	           strexpr.append("1");           
	        }
	        ArrayList fieldlist=new ArrayList();
	        String strwhere="";
	        if(!userView.isSuper_admin())
	        {
	            strwhere=userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre,true,fieldlist);
	        }
	        else
	        {
	        	FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,true,true,1,userView.getUserName());
	            strwhere= factorlist.getSqlExpression();        	
	        }
            cat.debug("strwhere="+strwhere);
	        /**查询条件*/
	        this.getFormHM().put("tablename",dbpre+"A01");
	        this.getFormHM().put("cond_str",strwhere/*cond_str.toString()*/);
	        /**条件列表*/
	        StringBuffer strsql=new StringBuffer();
	        strsql.append("select distinct a0000,");
	        strsql.append(dbpre);
	        strsql.append("a01.a0100 ");
	        strsql.append(",b0110,e0122,e01a1,a0101, ");   
	        strsql.append(getLoginUserName());

	        this.getFormHM().put("sql_str",strsql.toString());
	        /**字段列表*/
	        strsql.setLength(0);
	        strsql.append("a0100,b0110,e0122,e01a1,a0101,");
	        strsql.append(getLoginUserName());
	        strsql.append(",");        
	        this.getFormHM().put("columns",strsql.toString());
	        ArrayList dblist=getLoginBaseList();
	        this.getFormHM().put("dblist",dblist);
       }
	   catch(Exception ee)
	   {
		   ee.printStackTrace();
	   }
	}

}
