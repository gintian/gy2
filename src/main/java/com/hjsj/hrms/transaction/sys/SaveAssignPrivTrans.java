package com.hjsj.hrms.transaction.sys;


import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:SaveAssignPrivTrans</p>
 * <p>Description:保存用户权限信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 9, 2005:10:28:51 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveAssignPrivTrans extends IBusiness {
    /**
     * 用户标识
     */
    private String userflag=GeneralConstant.ROLE;
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        String role_id=(String)this.getFormHM().get("role_id");
       		
        String tab_name=(String)this.getFormHM().get("tab_name");
        userflag=(String)this.getFormHM().get("user_flag");
        
        cat.debug("role_id="+role_id);
        cat.debug("tab_name="+tab_name); 
        cat.debug("user_flag="+userflag);
        try
        {
	        if(role_id==null|| "".equals(role_id))
	            return;
	        //role_id=role_id.toUpperCase(); chenmengqing added 20080605 for oracle大小写问题
	        if(tab_name==null|| "".equals(tab_name))
	            return;
	        //如果为空，则default为角色
	        if(userflag==null|| "".equals(userflag))
	            userflag=GeneralConstant.ROLE;
	        //role_id=PubFunc.ToGbCode(role_id);
	        /**
	         * 功能授权
	         */
	        if("funcpriv".equals(tab_name))
	        {
	            saveFunctionPriv(role_id);
	        }
	        /**
	         * 人员库授权
	         */
	        if("dbpriv".equals(tab_name))
	        {
	            saveDbPriv(role_id);
	        }
	        /**
	         * 子集授权
	         */
	        if("tablepriv".equals(tab_name))
	        {
	            saveTablePriv(role_id);
	        }
	        /**
	         * 指标授权
	         */
	        if("fieldpriv".equals(tab_name))
	        {
	            saveFieldPriv(role_id);
	        }
	        /**管理范围*/
	        if("managepriv".equals(tab_name))
	        {
	            saveManagePriv(role_id);
	        }
	        /**多媒体子集*/        
	        if("mediapriv".equals(tab_name))
	        {
	        	saveMediaPriv(role_id);
	        }
	        /**报表*/            
	        if("reportpriv".equals(tab_name))
	        {
	        	saveReportPriv(role_id);
	        }    
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
    }
    
    private void saveReportPriv(String role_id)
    {
        StringBuffer func_str=new StringBuffer();              
        String[] func=(String[])this.getFormHM().get("func");
        func_str.append(",");
        for(int i=0;i<func.length;i++)
        {
            if("".equals(func[i].trim()))
                continue;
            func_str.append(func[i]);
            func_str.append(",");
        }
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
        vo.setString("reportsortpriv",func_str.toString());
        cat.debug("report_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();    	
    }    
    /**
     * 保存多媒体授权信息
     * @param role_id
     */
    private void saveMediaPriv(String role_id)
    {
        StringBuffer func_str=new StringBuffer();              
        String[] func=(String[])this.getFormHM().get("func");
        func_str.append(",");
        for(int i=0;i<func.length;i++)
        {
            if("".equals(func[i].trim()))
                continue;
            func_str.append(func[i]);
            func_str.append(",");
        }

        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag);
        vo.setString("mediapriv",func_str.toString());
        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();  
             /*
		SysPrivBo privbo=new SysPrivBo(role_id,this.userflag,this.getFrameconn(),"warnpriv");
		String res_str=privbo.getWarn_str();
		ResourceParser parser=new ResourceParser(res_str,IResourceConstant.MEDIA_EMP);  
		parser.reSetContent(func_str.toString());
		res_str=parser.outResourceContent();
		saveResourceString(role_id,this.userflag,res_str);		
		  */ 
    }
    
    private void saveResourceString(String role_id,String flag,String res_str)
    {
        if(res_str==null)
        	res_str="";
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",flag/*GeneralConstant.ROLE*/);
        vo.setString("warnpriv",res_str);
        cat.debug("role_vo="+vo.toString());	
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();        
    }	
    
    private void saveManagePriv(String role_id)
    {
        String manage_str=(String)this.getFormHM().get("org");
        if(manage_str==null)
            return;
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
        vo.setString("managepriv",manage_str);
        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();         
    }
    /**
     * 保存指标权限
     * @param role_id
     */
//    private void saveFieldPriv(String role_id)
//    {
//        String field_str=(String)this.getFormHM().get("field_set_str");
//        if(field_str==null)
//            return;
//        RecordVo vo=new RecordVo("t_sys_function_priv");
//        vo.setString("id",role_id);
//        vo.setString("fieldpriv",field_str);
//        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
//        cat.debug("role_vo="+vo.toString());	
//        
//        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
//        sysbo.save();         
//    }
	  private void saveFieldPriv(String role_id)
	  {
	      String field_str=(String)this.getFormHM().get("field_set_str");
	      if(field_str==null)
	          field_str="";
	      StringBuffer strsql=new StringBuffer();
	      strsql.append("select id from t_sys_function_priv where id='");
	      strsql.append(role_id);
	      strsql.append("' and status=");
	      strsql.append(this.userflag);
	      try
	      {
	    	ArrayList paralist=new ArrayList();
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	this.frowset=dao.search(strsql.toString());
	    	cat.debug("select sql="+strsql.toString());	

	    	if(this.frowset.next())
	    	{
		    	paralist.add(field_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("update t_sys_function_priv set fieldpriv=?");
	    		//strsql.append(field_str);
	    		strsql.append(" where id='");
	    		strsql.append(role_id);
	    		strsql.append("' and status=");
	    		strsql.append(this.userflag);
	    	}
	    	else
	    	{
		    	paralist.add(role_id);	    		
		    	paralist.add(field_str);	    		
	    		strsql.setLength(0);
	    		strsql.append("insert into t_sys_function_priv (id,fieldpriv,status) values(?,?,");
	    		/*
	    		strsql.append(role_id);
	    		strsql.append("',");
	    		strsql.append(field_str);
	    		strsql.append("',");
	    		*/
	    		strsql.append(this.userflag);
	    		strsql.append(")");
	    	}
	    	cat.debug("updat field_priv sql="+strsql.toString());
	    	dao.update(strsql.toString(),paralist);
	      }
	      catch(SQLException sqle)
	      {
	    	  sqle.printStackTrace();
	      }
//	      RecordVo vo=new RecordVo("t_sys_function_priv");
//	      vo.setString("id",role_id);
//	      vo.setString("fieldpriv",field_str);
//	      vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
//	      cat.debug("role_vo="+vo.toString());	
//	      
//	      SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
//	      sysbo.save();         
	  }    
    /**
     * 保存表权限
     * @param role_id
     */
    private void saveTablePriv(String role_id)
    {
        String table_str=(String)this.getFormHM().get("field_set_str");
        if(table_str==null)
        	table_str="";
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
        vo.setString("tablepriv",table_str);
        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();        
    }
    /**
     * @param role_id
     */
    private void saveDbPriv(String role_id) {
        StringBuffer func_str=new StringBuffer();              
        String[] func=(String[])this.getFormHM().get("func");
        func_str.append(",");
        for(int i=0;i<func.length;i++)
        {
            if("".equals(func[i].trim()))
                continue;
            func_str.append(func[i]);
            func_str.append(",");
        }
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);        
        vo.setString("dbpriv",func_str.toString());

        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();
    }

    /**
     * @param role_id
     */
    private void saveFunctionPriv(String role_id) {
        StringBuffer func_str=new StringBuffer();            
        String[] func=(String[])this.getFormHM().get("func");
        func_str.append(",");
        for(int i=0;i<func.length;i++)
        {
            if("".equals(func[i].trim()))
                continue;
            func_str.append(func[i]);
            func_str.append(",");
        }

        RecordVo vo=new RecordVo("t_sys_function_priv",1);
        vo.setString("id",role_id);
        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
        vo.setString("functionpriv",func_str.toString());
        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();

    }

}
