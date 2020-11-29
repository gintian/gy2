package com.hjsj.hrms.transaction.sys.bos.portal;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddPortalPrivTrans extends IBusiness {
	 public void execute() throws GeneralException 
	 {
	        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	        String portalid=(String)hm.get("portalid");
	        StringBuffer strsql=new StringBuffer();
		    strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role where valid=1");
		    strsql.append(" order by norder");
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    ArrayList list=new ArrayList();
		    String roles = "";
		    try
		    {
		      this.frowset = dao.search(strsql.toString());

		      
		      while(this.frowset.next())
		      {
		    	  LazyDynaBean abean=new LazyDynaBean();
		    	  abean.set("role_id",this.getFrowset().getString("role_id"));
		          /**有此角色,才能赋角色给别人,解决分布式授权机机制*/
		          if(!isHaveRole(this.getFrowset().getString("role_id")))
		        	  continue;
		          abean.set("role_name",this.getFrowset().getString("role_name"));
		          abean.set("role_desc",PubFunc.toHtml(this.getFrowset().getString("role_desc")==null?"":this.getFrowset().getString("role_desc")));
		          abean.set("role_property",this.getFrowset().getString("role_property"));
		          abean.set("valid","0");	
		          abean.set("status",this.getFrowset().getString("status"));
		          SysPrivBo privbo=new SysPrivBo(this.getFrowset().getString("role_id"),GeneralConstant.ROLE,this.getFrameconn(),"warnpriv");
					 String res_str=privbo.getWarn_str();
					 ResourceParser parser=new ResourceParser(res_str,IResourceConstant.PORTAL);
					 //parser.addContent(str_value.toString());
					 res_str=parser.outResourceContent();
					 if(res_str.indexOf("<portal>")!=-1){
						String temp =  res_str.substring(res_str.indexOf("<portal>"),res_str.indexOf("</portal>"));
						if(temp.indexOf(portalid)!=-1){
							roles+=this.getFrowset().getString("role_id")+","; 
							  abean.set("flag","1");
						}else{
							  abean.set("flag","0");
						}
					 }else{
						  abean.set("flag","0");
					 }
		        
		//          vo.setString("sid","1");	
		          list.add(abean);
		      }
		    } catch(SQLException sqle)
		    {
			      sqle.printStackTrace();
			      throw GeneralExceptionHandler.Handle(sqle);
		    }
		    finally
			{
			       this.getFormHM().put("rolelist",list);
			       this.getFormHM().put("portalid", portalid);
			       this.getFormHM().put("roles", roles);
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
