/*
 * Created on Apr 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author chenmengqing
 */
public class RoleListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
	    StringBuffer strsql=new StringBuffer();
	    String user_id=userView.getUserId();
	    HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
	    String order_name = (String)this.getFormHM().get("order_name");
	    order_name= order_name==null?"":order_name;
	    this.getFormHM().put("order_name", order_name);
	    String order_type = (String)this.getFormHM().get("order_type");
	    order_type= order_type==null?"":order_type;
	    this.getFormHM().put("order_type", order_type);
	    String isquery = (String)hm.get("isquery");
	    hm.remove("isquery");
	    ArrayList propertylist=new ArrayList();
	    propertylist.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
        propertylist.add(new CommonData("-1",ResourceFactory.getProperty("label.role.general")));
        if(this.userView.isSuper_admin()&& "su".equals(this.userView.getUserId()))
        {
        	propertylist.add(new CommonData("0",ResourceFactory.getProperty("label.role.sys")));
        }
        if(this.userView.isSuper_admin()&& "su".equals(this.userView.getUserId()))
        {
            propertylist.add(new CommonData("15",ResourceFactory.getProperty("label.role.sycrecy")));
        }
        if(this.userView.isSuper_admin()&& "su".equals(this.userView.getUserId()))
        {
            propertylist.add(new CommonData("16",ResourceFactory.getProperty("label.role.auditor")));
        }
        if((this.userView.isSuper_admin()&&!userView.isBThreeUser())||this.userView.haveTheRoleProperty("5"))
        {
            propertylist.add(new CommonData("5",ResourceFactory.getProperty("label.role.employ")));
        }
        if((this.userView.isSuper_admin()&&!userView.isBThreeUser())||this.userView.haveTheRoleProperty("1"))
        {
            propertylist.add(new CommonData("1",ResourceFactory.getProperty("label.role.leader")));
        }
        if((this.userView.isSuper_admin()&&!userView.isBThreeUser())||this.userView.haveTheRoleProperty("6"))
        {
            propertylist.add(new CommonData("6",ResourceFactory.getProperty("label.role.uleader")));
        }
        if((this.userView.isSuper_admin()&&!userView.isBThreeUser())||this.userView.haveTheRoleProperty("7"))
        {
            propertylist.add(new CommonData("7",ResourceFactory.getProperty("label.role.gleader")));
        }
        propertylist.add(new CommonData("2",ResourceFactory.getProperty("label.role.train")));
        propertylist.add(new CommonData("3",ResourceFactory.getProperty("label.role.kq")));
        propertylist.add(new CommonData("4",ResourceFactory.getProperty("label.role.per")));
        
        propertylist.add(new CommonData("8",ResourceFactory.getProperty("label.role.zp")));

        propertylist.add(new CommonData("9",ResourceFactory.getProperty("label.role.fleader")));
        propertylist.add(new CommonData("10",ResourceFactory.getProperty("label.role.sleader")));
        propertylist.add(new CommonData("11",ResourceFactory.getProperty("label.role.tleader")));
        propertylist.add(new CommonData("12",ResourceFactory.getProperty("label.role.ffleader")));        
        propertylist.add(new CommonData("13",ResourceFactory.getProperty("label.role.allleader")));  
        propertylist.add(new CommonData("14",ResourceFactory.getProperty("label.role.self")));          
        this.getFormHM().put("propertylist",propertylist);
        
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
	    }
	    
	    if("1".equals(isquery)){
	    	String qname = (String)this.getFormHM().get("qname");
	    	qname = qname==null?"":qname;
	    	String qroleproperty = (String)this.getFormHM().get("qroleproperty");
	    	if(qname.length()>0)
	    	if(strsql.indexOf("where")>-1){
	    		strsql.append(" and role_name like '%"+qname+"%'");
	    	}else{
	    		strsql.append(" where role_name like '%"+qname+"%'");
	    	}
	    	if(!"all".equals(qroleproperty)){
	    		if(strsql.indexOf("where")>-1){
	    			strsql.append(" and role_property="+qroleproperty);
	    		}else{
	    			strsql.append(" where role_property="+qroleproperty);
	    		}
	    	}
	    	this.getFormHM().put("qname", "");
	    	this.getFormHM().put("qroleproperty", "all");
	    	this.getFormHM().put("oqname", qname);
	    	this.getFormHM().put("oqroleproperty", qroleproperty);
	    }else{
	    	String oqname = (String)this.getFormHM().get("oqname");
	    	oqname = oqname==null?"":oqname;
	    	String oqroleproperty = (String)this.getFormHM().get("oqroleproperty");
	    	oqroleproperty=oqroleproperty==null?"all":oqroleproperty;
	    	if(oqname.length()>0){
	    		if(strsql.indexOf("where")>-1){
		    		strsql.append(" and role_name like '%"+oqname+"%'");
		    	}else{
		    		strsql.append(" where role_name like '%"+oqname+"%'");
		    	}
	    	}
	    	if(oqroleproperty.length()>0&&!"all".equals(oqroleproperty)){
	    		if(strsql.indexOf("where")>-1){
	    		strsql.append(" and role_property="+oqroleproperty);
	    		}else{
	    			strsql.append(" where role_property="+oqroleproperty);
	    		}
	    	}
	    }
	    
	    if(order_name!=null&&order_name.length()>0)
	    	strsql.append(" order by "+ order_name +" "+order_type);
	    else
	    	strsql.append(" order by norder");
	    cat.debug("query_role_sql="+strsql.toString());
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("T_SYS_ROLE");
	          vo.setString("role_id",this.getFrowset().getString("role_id"));
	          vo.setString("role_name",this.getFrowset().getString("role_name"));
	          vo.setString("role_desc",PubFunc.toHtml(this.getFrowset().getString("role_desc")));
	          vo.setString("role_property",this.getFrowset().getString("role_property"));
	          vo.setString("valid",this.getFrowset().getString("valid"));
	          vo.setString("status",this.getFrowset().getString("status"));
	          list.add(vo);	         
	      }
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("rolelist",list);
	    }
	}

}
