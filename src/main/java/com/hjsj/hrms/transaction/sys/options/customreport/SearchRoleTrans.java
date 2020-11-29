package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchRoleTrans extends IBusiness {
	 public void execute() throws GeneralException 
	 {
	        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	        String tabid=(String)hm.get("tabid");
	        String num = (String) hm.get("num");
	        StringBuffer strsql=new StringBuffer();
		    strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role where valid=1");
		    strsql.append(" order by norder");
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    ArrayList list=new ArrayList();
		    try
		    {
		      this.frowset = dao.search(strsql.toString());

		      
		      while(this.frowset.next())
		      {
		          RecordVo vo=new RecordVo("T_SYS_ROLE");
		          vo.setString("role_id",this.getFrowset().getString("role_id"));
		          /**有此角色,才能赋角色给别人,解决分布式授权机机制*/
		          if(!isHaveRole(this.getFrowset().getString("role_id")))
		        	  continue;
		          vo.setString("role_name",this.getFrowset().getString("role_name"));
		          vo.setString("role_desc",PubFunc.toHtml(this.getFrowset().getString("role_desc")));
		          vo.setString("role_property",this.getFrowset().getString("role_property"));
		          vo.setString("valid","0");	
		          vo.setString("status",this.getFrowset().getString("status"));
		          list.add(vo);
		      }
		    } catch(SQLException sqle)
		    {
			      sqle.printStackTrace();
			      throw GeneralExceptionHandler.Handle(sqle);
		    }
		    finally
			{
			       this.getFormHM().put("rolelist",list);
			       if (num != null && "1".equals(num)) {
			    	   this.getFormHM().put("rolesHas",this.getPrivRole(tabid));
			       } else {
			    	   this.getFormHM().put("rolesHas","");
			       }
			}
	 }
	 	/***
	     * 分析登录用户是否拥有此角色
	     * @param role_id
	     * @return
	     */
	    private boolean isHaveRole(String role_id)
	    {
	      if(userView.isSuper_admin())
	    	return true;
	      /**登录用户拥有的角色*/
	      boolean flag=false;
	      ArrayList rolelist=userView.getRolelist();
	      for(int i=0;i<rolelist.size();i++)
	      {
	    	  if(role_id.equals(rolelist.get(i)))
	    	  {
	    		  flag=true;
	    		  break;
	    	  }
	      }
	      return flag;
	    }
	    
	    /**
	     * 获得有权限的角色
	     * @param tabid
	     * @return
	     */
	    private String getPrivRole(String tabid) {
	    	String roles = "";
	    	StringBuffer strsql=new StringBuffer();
		    strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role where valid=1");
		    strsql.append(" order by norder");
		    ContentDAO dao = new ContentDAO(this.frameconn);
		    try {
			    this.frowset = dao.search(strsql.toString()); 
			    while (frowset.next()) {
			    	String role_id = frowset.getString("role_id");
		         SysPrivBo privbo=new SysPrivBo(role_id,GeneralConstant.ROLE,this.getFrameconn(),"warnpriv");
				 String res_str=privbo.getWarn_str();
				 ResourceParser parser=new ResourceParser(res_str,IResourceConstant.CUSTOM_REPORT);
				 String str_content=parser.getContent();
				 if(str_content==null||str_content.length()<=0)
					 str_content="";
				 str_content = "," + str_content + ",";
				 if(str_content.indexOf(","+tabid + ",")!=-1)
					 roles = roles + role_id + ",";
			    }
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
	    	return "," + roles;
	    }

}
