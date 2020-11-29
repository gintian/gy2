package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author chenmengqing
 */
public class ViewRoleTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String role_id=(String)hm.get("a_roleid");
        String flag=(String)this.getFormHM().get("flag");
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
        ArrayList propertylist=new ArrayList();
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
        if("1".equals(flag))
            return;
        cat.debug("------>a_roleid====="+role_id);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("t_sys_role");
        try
        {
            vo.setString("role_id",role_id);
            vo=dao.findByPrimaryKey(vo);
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("rolevo",vo);
        }
    }

}
