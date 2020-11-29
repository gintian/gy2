package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
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
public class SaveLawTextRoleTrans extends IBusiness {
	 public void execute() throws GeneralException 
	 {
		 HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		 String base_ids=(String)hm.get("a_base_ids");
		 ArrayList list = (ArrayList) this.getFormHM().get("selectedlist");
		 if (list == null || list.size() == 0)
			return;
		 if(base_ids==null||base_ids.length()<=0)
			 return;
		 StringBuffer str_value=new StringBuffer();
		 for(int i=0;i<list.size();i++)
		 {
			 RecordVo vo=(RecordVo)list.get(i);
	    	 String file_id= vo.getString("file_id");
	    	 if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, file_id))
	    		 continue;
	    	 str_value.append(file_id+",");
		 }
		 if(str_value.length()!=0)
				str_value.setLength(str_value.length()-1);
		 String [] base_id_array=base_ids.split("`");
		 for(int i=0;i<base_id_array.length;i++)
		 {
			 String roleid=base_id_array[i];
			 if(roleid==null||roleid.length()<=0)
				 continue;
			 SysPrivBo privbo=new SysPrivBo(roleid,GeneralConstant.ROLE,this.getFrameconn(),"warnpriv");
			 String res_str=privbo.getWarn_str();
			 ResourceParser parser=new ResourceParser(res_str,IResourceConstant.LAWRULE_FILE);
			 parser.addContent(str_value.toString());
			 res_str=parser.outResourceContent();
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
