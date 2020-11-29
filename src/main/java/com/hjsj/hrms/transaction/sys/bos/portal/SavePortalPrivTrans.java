package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 文章的角色分配
 * @author Owner
 *
 */
public class SavePortalPrivTrans extends IBusiness {
	 public void execute() throws GeneralException 
	 {
		 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		 String base_ids=(String)hm.get("a_base_ids");
		 String str_value=(String)hm.get("portalid");
		 String roles = (String)this.getFormHM().get("roles");
//		 if(base_ids==null||base_ids.length()<=0)
//			 return;
		 String [] base_id_array=base_ids.split("`");
		 for(int i=0;i<base_id_array.length;i++)
		 {
			 String roleid=base_id_array[i];
			 if(roleid==null||roleid.length()<=0)
				 continue;
			 SysPrivBo privbo=new SysPrivBo(roleid,GeneralConstant.ROLE,this.getFrameconn(),"warnpriv");
			 String res_str=privbo.getWarn_str();
			 ResourceParser parser=new ResourceParser(res_str,IResourceConstant.PORTAL);
			 parser.addContent(str_value.toString());
			 res_str=parser.outResourceContent();
			 saveResourceString(roleid,GeneralConstant.ROLE,res_str);
		 }
		 //更新以前的   把以前此角色中有此授权的现在没了清除掉
		 String []roles_temp = roles.split(",");
		 for(int i=0;i<roles_temp.length;i++){
			 String roleid = roles_temp[i];
			 if(base_ids.indexOf(roleid)!=-1)
				 continue;
			 SysPrivBo privbo=new SysPrivBo(roleid,GeneralConstant.ROLE,this.getFrameconn(),"warnpriv");
			 String res_str=privbo.getWarn_str();
			 ResourceParser parser=new ResourceParser(res_str,IResourceConstant.PORTAL);
			 parser.addContent(str_value.toString());
			 res_str=parser.outResourceContent();
			String res_str_pre = res_str.substring(0,res_str.indexOf("<portal>"));
			String res_str_end= res_str.substring(res_str.indexOf("</portal>"),res_str.length());
			String res_str_portal = res_str.substring(res_str.indexOf("<portal>"),res_str.indexOf("</portal>"));
			res_str_portal = res_str_portal.replace(","+str_value, "");
			res_str_portal = res_str_portal.replace(str_value, "");
			res_str= res_str_pre+res_str_portal+res_str_end;
			saveResourceString(roleid,GeneralConstant.ROLE,res_str);
		 }
		 
	 }
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

}
