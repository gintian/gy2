package com.hjsj.hrms.transaction.sys;


import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
 *<p>Title:SearchAllRoleTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Feb 17, 2009:3:31:13 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SearchAllRoleTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
	    StringBuffer strsql=new StringBuffer();
	    String user_id=userView.getUserId();
	    String order_name = (String) this.getFormHM().get("order_name");
	    String order_type = (String) this.getFormHM().get("order_type");
	    if(userView.isBThreeUser()){//有三员角色的用户
	    	/**只能查询到自己拥有的角色列表*/
	    	strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role");
	    	strsql.append(" where role_id in (");
	    	strsql.append("select role_id from t_sys_staff_in_role where staff_id='");
	    	if(userView.getStatus()==4)
	    	{
	    		strsql.append(userView.getDbname());
	    	}
	        strsql.append(user_id);
	        strsql.append("' and status=");
            if(userView.getStatus()==0)
    	        strsql.append("0");            	
            else
  	            strsql.append("1");//4
	        strsql.append(") and role_property not in(0,15,16)");	
	        //strsql.append(" order by role_id");
    }else{
    	 if(userView.isSuper_admin()){
    		 if("su".equals(userView.getUserId()))
		    	strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role ");
    		 else
    			 strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role where role_property not in(0,15,16) ");
    	 } else
		    {
		    	/**只能查询到自己拥有的角色列表*/
		    	strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role");
		    	strsql.append(" where role_id in (");
		    	strsql.append("select role_id from t_sys_staff_in_role where staff_id='");
		    	if(userView.getStatus()==4)
		    	{
		    		strsql.append(userView.getDbname());
		    	}
		        strsql.append(user_id);
		        strsql.append("' and status=");
	            if(userView.getStatus()==0)
	    	        strsql.append("0");            	
	            else
	  	            strsql.append("1");//4
		        strsql.append(") and role_property not in(0,15,16)");	
		        //strsql.append(" order by role_id");
		    }
    }	//【7105】角色管理中，按照角色特称排序后，调整顺序界面显示不对。 jingq upd 2015.01.29
	    if(order_name!=null&&order_name.length()>0){
	    	strsql.append(" order by "+order_name+" "+order_type);
	    } else {
	    	strsql.append(" order by norder");
	    }
	    cat.debug("query_role_sql="+strsql.toString());
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          CommonData ordervo = new CommonData(PubFunc.nullToStr(this.getFrowset().getString("role_id")),this.getFrowset().getString("role_name"));
	          list.add(ordervo);
	      }
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("orderList",list);
	    }
	}

}

