package com.hjsj.hrms.module.hire.transaction;

import com.hjsj.hrms.module.hire.businessobject.GetZpAccountsBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 找回注册帐号页面显示交易类
 * @Title:        ShowZpAccountsTrans.java
 * @Description:  查询招聘外网—找回注册帐号页面显示的指标名（固定显示姓名、电话/手机号、唯一性指标）
 * @Company:      hjsj     
 * @Create time:  2016-12-1 下午05:31:20
 * @author        chenxg
 * @version       1.0
 */
public class ShowZpAccountsTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            GetZpAccountsBo bo = new GetZpAccountsBo(this.frameconn);
            
            this.getFormHM().put("nameDesc", bo.getA0101Desc());
            this.getFormHM().put("onlynFieldDesc", bo.getOnlynFieldDesc());
            this.getFormHM().put("phoneFieldDesc", bo.getPhoneFieldDesc());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
