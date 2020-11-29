package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
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

public class QueryLawBaseRoleTrans extends IBusiness {
	 public void execute() throws GeneralException 
	 {
	        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	        String base_id=(String)hm.get("a_base_id");
	        if(base_id != null && base_id.length() > 0){
	            base_id = PubFunc.decrypt(SafeCode.decode(base_id));
	        }
	        String basetype = (String)this.getFormHM().get("basetype");
	        StringBuffer strsql=new StringBuffer();
		    strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role where valid=1");
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
		          if(!isLawBaseUsrRole(this.getFrowset().getString("role_id"),base_id,basetype))
		        	  continue;	
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
		    this.getFormHM().put("base_id",SafeCode.encode(PubFunc.encrypt(base_id)));
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
	     * 该角色是否拥有这个制度节点的权限
	     * @param base_id
	     * @param role_id
	     * @return
	     */
	    private boolean isLawBaseUsrRole(String role_id,String base_id,String basetype)
	    {
	         boolean isCorrect =false;	         
	         SysPrivBo privbo=new SysPrivBo(role_id,GeneralConstant.ROLE,this.getFrameconn(),"warnpriv");
			 String res_str=privbo.getWarn_str();
			 ResourceParser parser=null;
			 if("1".equalsIgnoreCase(basetype))
				 parser=new ResourceParser(res_str,IResourceConstant.LAWRULE);
			 if("5".equalsIgnoreCase(basetype))
				 parser=new ResourceParser(res_str,IResourceConstant.DOCTYPE);
			 if("4".equalsIgnoreCase(basetype))
				 parser=new ResourceParser(res_str,IResourceConstant.KNOWTYPE);
			 String str_content=parser.getContent();
			 if(str_content==null||str_content.length()<=0)
				 str_content="";
			 if(str_content.indexOf(base_id)!=-1)
				 isCorrect=true;
	         return isCorrect;
	    }
}

