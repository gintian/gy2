package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveSignPointTrans extends IBusiness {

    public void execute() throws GeneralException {

        String sign_point_name = (String) this.getFormHM().get("sign_point_name");
        String city = (String) this.getFormHM().get("city");
        String location = (String) this.getFormHM().get("location");

        try {
            int pid = 1;
            String sql = "select max(pid) pid from kq_sign_point ";
            ContentDAO dao = new ContentDAO(this.frameconn);
            this.frowset = dao.search(sql);
            if (this.frowset.next())
                pid = this.frowset.getInt("pid") + 1;

            //tiany add 针对考勤员角色权限 不是考勤员角色，仍走用户的人员范围权限 进行筛选考勤点
            String privCode = RegisterInitInfoData.getKqPrivCode(userView);
            String codeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
            if (privCode.length() == 0 && !userView.isSuper_admin()) {
                this.getFormHM().put("saveRs", "noPriv");
                throw GeneralExceptionHandler.Handle(new Exception("您无权限操作"));
            }
            //end
            
            sql = "insert into kq_sign_point(pid,name,city,location,b0110) values(" + pid + ",'" + sign_point_name + "','" + city
                    + "','" + location + "','" + codeValue + "')";
            dao.insert(sql, new ArrayList());

            this.getFormHM().put("location", location);
            this.getFormHM().put("saveRs", "succeed");
            this.getFormHM().put("pid", "P" + pid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
