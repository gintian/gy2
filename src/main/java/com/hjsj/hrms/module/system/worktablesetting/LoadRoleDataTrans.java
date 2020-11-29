package com.hjsj.hrms.module.system.worktablesetting;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;


/**
 *获取角色信息
 * 
 * @author xus
 *
 */
@SuppressWarnings("serial")
public class LoadRoleDataTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		StringBuffer strsql=new StringBuffer();
		String user_id=userView.getUserId();
		if(userView.isBThreeUser()){//有三员角色的用户
	    	/**只能查询到自己拥有的角色列表*/
			strsql.append("select role_id,role_name,q.menuinfo menuinfo from t_sys_role t left join t_sys_quicklink q on t.role_id=q.username ");
			strsql.append(" where t.role_id in (");
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
			strsql.append(") and t.role_property not in(0,15,16)");
	        //strsql.append(" order by role_id");
		}else{
			if(userView.isSuper_admin()){
				if("su".equals(userView.getUserId()))
					strsql.append("select role_id,role_name,q.menuinfo menuinfo from t_sys_role t left join t_sys_quicklink q on t.role_id=q.username ");
				else
					strsql.append("select role_id,role_name,q.menuinfo menuinfo from t_sys_role t left join t_sys_quicklink q on t.role_id=q.username where t.role_property not in(0,15,16) ");
			}else{
				/**只能查询到自己拥有的角色列表*/
				strsql.append("select role_id,role_name,q.menuinfo menuinfo from t_sys_role t left join t_sys_quicklink q on t.role_id=q.username ");
				strsql.append(" where t.role_id in (");
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
				strsql.append(") and t.role_property not in(0,15,16)");
			}
		}
		strsql.append(" order by t.norder");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
		try {
			this.frowset = dao.search(strsql.toString());
			LazyDynaBean bean=new LazyDynaBean();
		      while(this.frowset.next())
		      {
		    	  bean=new LazyDynaBean();
		    	  bean.set("role_id", this.getFrowset().getString("role_id"));
		    	  bean.set("role_name",this.getFrowset().getString("role_name"));
		    	  bean.set("menuinfo", this.getFrowset().getString("menuinfo"));
		          list.add(bean);	         
		      }
		      this.formHM.put("data", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
