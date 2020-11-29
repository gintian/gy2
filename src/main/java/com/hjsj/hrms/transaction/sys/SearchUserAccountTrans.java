package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchUserAccountTrans</p>
 * <p>Description:查询用户的账号信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 30, 2005:10:07:23 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchUserAccountTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        String dbpre=(String)this.getFormHM().get("dbpre");
        
        String a_id=(String)hm.get("a_id");
        if(a_id==null|| "".equals(a_id))
            return;
        ArrayList dblist=getLoginBaseList();
        if(dbpre==null|| "".equals(dbpre))
        {
        	dbpre=getFirstDbase(dblist);
        }    
        /**登录参数表,登录用户指定不是username or userpassword*/
        String username=null;
        String password=null;
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        /**default值*/
        this.getFormHM().put("userlen","50");
        this.getFormHM().put("pwdlen","20");        
        if(login_vo==null)
        {
            username="username";
            password="userpassword";
        }
        else
        {
            String login_name = login_vo.getString("str_value").toLowerCase();
            int idx=login_name.indexOf(",");
            if(idx==-1)
            {
                username="username";
                password="userpassword";
            }
            else
            {
                username=login_name.substring(0,idx);
                if("#".equals(username)|| "".equals(username))
                	username="username";
                else
                {
                	FieldItem item=DataDictionary.getFieldItem(username);
                	if(item==null)
                		username="username";
                	else
                		this.getFormHM().put("userlen",Integer.toString(item.getItemlength()));
                }
                password=login_name.substring(idx+1);  
                if("#".equals(password)|| "".equals(password))
                	password="userpassword";
                else
                {
                	FieldItem item=DataDictionary.getFieldItem(password);
                	this.getFormHM().put("pwdlen",Integer.toString(item.getItemlength()));                	
                }
            }
        }
        RecordVo ip_vo=ConstantParamter.getConstantVo("SS_BIND_IPADDR");
        String ip_addr="";
        if(ip_vo!=null)
        	ip_addr=ip_vo.getString("str_value");
        if(ip_addr==null|ip_addr.length()<=0|| "#".equals(ip_addr))
        	ip_addr="";
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        /**
         * 对在职人员而言。
         */
        String tablename=dbpre+"a01";
        RecordVo vo=new RecordVo(tablename,1);
        vo.setString("a0100",a_id);
        RecordVo user_vo=new RecordVo(tablename,1);        
        try
        {
            vo=dao.findByPrimaryKey(vo);

            user_vo.setString("a0100",a_id);
            user_vo.setString("username",vo.getString(username));
            user_vo.setString("state","");
            user_vo.setString("b0110",vo.getString("b0110"));
            //【5712】当a01信息集中的E0122未勾库时，到系统管理-账号管理，一点就报附件中的错误。   jingq add 2014.12.06
            if(user_vo.hasAttribute("e0122")){
            	user_vo.setString("e0122",vo.getString("e0122"));
            }
            user_vo.setString("e01a1",vo.getString("e01a1")); 
            user_vo.setString("a0101",vo.getString("a0101"));       
            if(ip_addr!=null&&ip_addr.length()>0)
            	user_vo.setString(ip_addr.toLowerCase(),vo.getString(ip_addr.toLowerCase()));      
            cat.debug("user_vo="+user_vo.toString());
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
   	     	throw GeneralExceptionHandler.Handle(sqle);                
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
   	     	throw GeneralExceptionHandler.Handle(ee);            	
        }
        this.getFormHM().put("ip_addr",ip_addr.toLowerCase());
        this.getFormHM().put("user_vo",user_vo);
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
	                if((A01.indexOf(dbpre)!=-1)&&(userView.isSuper_admin()||userView.hasTheDbName(dbpre)))
	                {
	                	CommonData vo=new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
	                	dblist.add(vo);
	                }
            }
            /**认为是在职人员库*/
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
    
    private String getFirstDbase(ArrayList dblist)
    {
    	CommonData vo=(CommonData)dblist.get(0);
    	return vo.getDataValue();
    }    
}
