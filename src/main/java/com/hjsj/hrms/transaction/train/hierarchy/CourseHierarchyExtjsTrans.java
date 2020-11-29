package com.hjsj.hrms.transaction.train.hierarchy;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 * @Title: CourseHierarchyExtjsTrans.java
 * @Description: 培训自助-浏览课程 传递参数
 * @Company: hjsj
 * @Create time: 2015-7-18 下午05:09:38
 * @author chenxg
 * @version 1.0
 */
public class CourseHierarchyExtjsTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String a_code = (String) hm.get("a_code");
        try {

            if (a_code != null && a_code.length() > 0)
                a_code = PubFunc.decrypt(SafeCode.decode(a_code));

            if (a_code == null)
                a_code = (String) this.getFormHM().get("a_code1");

            a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
            this.getFormHM().put("a_code1", a_code);
            hm.remove("a_code");

            if (this.userView.getA0100() == null || this.userView.getA0100().length() < 1)
                throw GeneralExceptionHandler.Handle(new GeneralException("", "非自助用户不能使用此功能！", "", ""));

            this.getFormHM().put("a_code", SafeCode.encode(PubFunc.encrypt(a_code)));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
