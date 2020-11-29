package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>
 * Title:SaveRecruitFlowTrans.java
 * </p>
 * <p>
 * Description:修改招聘流程环节的用户自定义名称。
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-9 10:18:02
 * </p>
 * 
 * @author zhangx
 * @version 1.0
 *
 */
public class UpdateLinkUnameTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        try {
            String linkid = (String) this.getFormHM().get("linkid");
            String custom_name = (String) this.getFormHM().get("custom_name");
            
            String sql = "update zp_flow_links set custom_name=? where id=?";
            ArrayList values = new ArrayList();
            values.add(custom_name);
            values.add(linkid);
            ContentDAO dao = new ContentDAO(this.frameconn);
            dao.update(sql, values);
            this.getFormHM().put("value", custom_name);
            this.getFormHM().put("custom_name", custom_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
