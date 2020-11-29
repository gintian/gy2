package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author chenmengqing
 */
public class DeleteRoleTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        ArrayList rolelist=(ArrayList)this.getFormHM().get("selectedlist");
        if(rolelist==null||rolelist.size()==0)
            return;
        StringBuffer strrole_id=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
    	
            dao.deleteValueObject(rolelist);
            /**删除其它相关表中的信息,t_sys_staff_in_role,t_sys_funtion_priv*/
            for(int i=0;i<rolelist.size();i++)
            {
            	RecordVo vo=(RecordVo)rolelist.get(i);
            	strrole_id.append("'");
            	strrole_id.append(vo.getString("role_id"));
            	strrole_id.append("',");
            }    
            strrole_id.setLength(strrole_id.length()-1);
            strsql.append("delete from t_sys_staff_in_role where role_id in(");
            strsql.append(strrole_id.toString());
            strsql.append(")");
            dao.delete(strsql.toString(),new ArrayList());
            /**delete from t_sys_funtion_priv where id ...*/
            strsql.setLength(0);
            strsql.append("delete from t_sys_function_priv where id in(");
            strsql.append(strrole_id.toString());
            strsql.append(") and status=1");
            dao.delete(strsql.toString(),new ArrayList());
            
            /**移走角色*/
            for(int i=userView.getRolelist().size()-1;i>=0;i--)
            {
            	String id=(String)userView.getRolelist().get(i);
            	if(strrole_id.indexOf(id)!=-1)
            		userView.getRolelist().remove(i);
            }              
        }
	    catch(Exception ee)
	    {
	      ee.printStackTrace();
	      throw GeneralExceptionHandler.Handle(ee);
	    }
    }

}
