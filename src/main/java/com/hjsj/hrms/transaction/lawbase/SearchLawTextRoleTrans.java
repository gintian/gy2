package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SearchLawTextRoleTrans extends IBusiness {
	 public void execute() throws GeneralException 
	 {
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

}
