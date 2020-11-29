package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.StringTokenizer;
/**
 * 重设IP
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 3, 2007:9:05:34 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class ResetUserIPTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		RecordVo vo=(RecordVo)this.getFormHM().get("user_vo");
        String dbpre=(String)this.getFormHM().get("dbpre");      
        String ip_addr=(String)this.getFormHM().get("ip_addr");
        if(vo==null)
            return ;
        /**登录参数表,登录用户指定不是username or userpassword*/
        String username=null;
        String password=null;
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD",this.getFrameconn());
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
                password=login_name.substring(idx+1);
                if("#".equals(password)|| "".equals(password))
                	password="userpassword";
            }
        }

        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String tablename=dbpre+"a01";
        RecordVo user_vo=new RecordVo(tablename);
        try{
        	String ip_addr_value="";
            if(ip_addr!=null&&ip_addr.length()>0)
            {
            	ip_addr_value=vo.getString(ip_addr);
            	if(IsExistsIP(ip_addr,ip_addr_value,dbpre,vo.getString("a0100"),this.getFrameconn()))
            		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.ip.exist"),"",""));;
            }
            user_vo.setString("a0100",vo.getString("a0100"));
            if(ip_addr!=null&&ip_addr.length()>0)
            {
            	user_vo.setString(ip_addr,ip_addr_value);
            }
            dao.updateValueObject(user_vo);
        }catch(Exception e)
        {
          e.printStackTrace();	
          throw GeneralExceptionHandler.Handle(e);
        }
	}
	private boolean IsExistsIP(String ip_addr,String ip_addr_value,String nbase,String a0100,Connection conn)
    {
    	boolean isCorrect=false;
    	RecordVo vo =ConstantParamter.getConstantVo("SS_LOGIN",conn);
    	String dbpre=null;
        if(vo==null)
            dbpre="usr,";
        /**
         * 登录主集usrA01/retA01/....
         */
        StringBuffer strsql=new StringBuffer();               
        dbpre = vo.getString("str_value");
        /**default usra01*/
        if(dbpre==null|| "".equals(dbpre))
            dbpre="usr,";
        StringTokenizer st = new StringTokenizer(dbpre, ",");
        while (st.hasMoreTokens())
        {
          String pre=st.nextToken().trim();
         
          strsql.append("select a0100 from ");
          strsql.append(pre);
          strsql.append("A01 ");
          strsql.append(" where ");
          strsql.append(ip_addr);
          strsql.append("='");
          strsql.append(ip_addr_value+"'");
          if(nbase.equalsIgnoreCase(pre))
        	  strsql.append(" and a0100 <>'"+a0100+"'");
          strsql.append(" UNION ");
        }    
        strsql.setLength(strsql.length()-7);
        ContentDAO dao=new ContentDAO(conn);
        try
        {
             this.frowset=dao.search(strsql.toString());
             if(this.frowset.next())
             {
            	 isCorrect=true;
             }
             //return bflag;                
        }
        catch(Exception sqle)
        {
            sqle.printStackTrace();
        }
    	return isCorrect;
    }
}
