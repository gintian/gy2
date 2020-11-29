package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveFieldTemplateByAjaxTrans.java</p>
 * <p>Description>:SaveFieldTemplateByAjaxTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 2, 2009 3:26:14 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */

public class SaveFieldTemplateByAjaxTrans extends IBusiness
{
	private String userglag=GeneralConstant.ROLE;
	//10400201023
	private void saveResourceString(String role_id,String flag,String res_str)
	{
		if(res_str==null)
			res_str="";
	    /*
	        RecordVo vo=new RecordVo("t_sys_function_priv",1);
	        vo.setString("id",role_id);
	        vo.setString("status",flag);
	        vo.setString("warnpriv",res_str);
	        cat.debug("role_vo="+vo.toString());	
	        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
	        sysbo.save(); 
	    */
		StringBuffer strsql=new StringBuffer();
		strsql.append("select id from t_sys_function_priv where id='");
		strsql.append(role_id);
		strsql.append("' and status=");
		strsql.append(flag);
		try
		{
			ArrayList paralist=new ArrayList();
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    this.frowset=dao.search(strsql.toString());
		    cat.debug("select sql="+strsql.toString());	

		    if(this.frowset.next())
		    {
			    paralist.add(res_str);	    		
		    	strsql.setLength(0);
		    	strsql.append("update t_sys_function_priv set warnpriv=?");
		    	//strsql.append(field_str);
		    	strsql.append(" where id='");
		    	strsql.append(role_id);
		    	strsql.append("' and status=");
		    	strsql.append(flag);
		    }
		    else
		    {
			    paralist.add(role_id);	    		
			    paralist.add(res_str);	    		
		    	strsql.setLength(0);
		    	strsql.append("insert into t_sys_function_priv (id,warnpriv,status) values(?,?,");
		    	strsql.append(flag);
		    	strsql.append(")");
		    }
		    cat.debug("updat warnpriv sql="+strsql.toString());
		    dao.update(strsql.toString(),paralist);
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
	}
	
	private String commfunstr(String func_str,String srcstr,String checked)
	{
	    String[] funcarr=StringUtils.split(func_str, ',');
	    StringBuffer bufa=new StringBuffer();
	    StringBuffer srcbuff=new StringBuffer();
	    srcbuff.append(","+srcstr+",");
	    int idx=0;
	    for(int i=0;i<funcarr.length;i++)
	    {
	    	bufa.setLength(0);
	    	bufa.append(",");
	    	bufa.append(funcarr[i]);
	    	bufa.append(",");
	    	String tmp=bufa.toString();
	    	idx=srcbuff.indexOf(tmp);
	    	if("1".equalsIgnoreCase(checked))//新增功能号
	    	{
	    		if(idx==-1)//原授权的功能串找不到，则追加进去
	    		{
	    			srcbuff.append(bufa.toString());
	    		}	    			
	    	}
	    	else
	    	{
	    		if(idx!=-1)//原授权的功能串能找到，则删除掉
	    		{
	    			srcbuff.replace(idx, idx+tmp.length(), ",");
	    		}
	    	}
	    }
	    return srcbuff.toString();
	}
	 
	public void execute() throws GeneralException 
	{
		try
		{
			String  role_id=(String)this.getFormHM().get("role_id");
			userglag=(String)this.getFormHM().get("user_flag");
			if(userglag==null|| "".equals(userglag))
				userglag=GeneralConstant.ROLE;
			String res_flag=(String)this.getFormHM().get("res_flag");
			String selstr=(String)this.getFormHM().get("selstr");
			selstr=SafeCode.decode(selstr);
			String checked=(String)this.getFormHM().get("checked");
			SysPrivBo privbo=new SysPrivBo(role_id,userglag,this.getFrameconn(),"warnpriv");
			String res_str=privbo.getWarn_str();
			int res_type=Integer.parseInt(res_flag);
			ResourceParser parser=new ResourceParser(res_str,res_type);
			/**1,2,3*/
			//parser.reSetContent(res_str);
			//String str_content=parser.outResourceContent();
			String str_content=parser.getContent();
			String privstr=this.commfunstr(selstr, "", checked);
			parser.reSetContent(privstr);
			String str=parser.outResourceContent();
			this.saveResourceString(role_id, userglag, str);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
