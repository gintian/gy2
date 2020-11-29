/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:ClearManagePrivTrans</p>
 * <p>Description:清空管理范围</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-5-29:10:28:00</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ClearManagePrivTrans extends IBusiness {
    /**用户标识*/
    private String userflag=GeneralConstant.ROLE;
    
    private void saveManagePriv(String role_id)
    {
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("status",this.userflag/*GeneralConstant.ROLE*/);
        vo.setString("managepriv","");
        cat.debug("role_vo="+vo.toString());	
        
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();         
    }
    
	public void execute() throws GeneralException {
		try
		{
		    String role_id=(String)this.getFormHM().get("role_id");
		    String tab_name=(String)this.getFormHM().get("tab_name");
		    userflag=(String)this.getFormHM().get("user_flag");
		    cat.debug("role_id="+role_id);
		    cat.debug("tab_name="+tab_name); 
		    cat.debug("user_flag="+userflag);
		    saveManagePriv(role_id);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
