package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ClearAssignPrivTrans extends IBusiness {

    private void saveCondPriv(String role_id,String user_flag,String cond)
    {
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("condpriv",cond);
        vo.setString("status",user_flag/*GeneralConstant.ROLE*/);
        cat.debug("role_vo="+vo.toString());	
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();         
    }  	
	public void execute() throws GeneralException {
        /**取得人员标识和角色键值*/
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        String user_flag=(String)hm.get("a_flag");
        String user_id=(String)this.getFormHM().get("role_id");

        cat.debug("user_id="+user_id);
        cat.debug("user_flag="+user_flag);
        if(user_id==null|| "".equals(user_id))
            return;
        //如果为空，则default为角色
        if(user_flag==null|| "".equals(user_flag))
            user_flag=GeneralConstant.ROLE;  
        saveCondPriv(user_id,user_flag,"");
        this.getFormHM().put("factorlist",new ArrayList());
        this.getFormHM().put("expression","");
	}


}
