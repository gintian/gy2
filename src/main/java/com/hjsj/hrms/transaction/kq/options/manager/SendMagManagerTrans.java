package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCrads;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 发卡
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 17, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class SendMagManagerTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            //全部人员库，就要从前台页面得到库前缀
            String select_pre = (String) this.getFormHM().get("nbase");
            String a0100 = (String) this.getFormHM().get("a0100");
            String i9999 = (String) this.getFormHM().get("i9999");
            String magcard_setid = (String) this.getFormHM().get("magcard_setid");
            ArrayList fieldlist = DataDictionary.getFieldList(magcard_setid.toLowerCase(), Constant.USED_FIELD_SET);
            this.getFormHM().put("a0100", a0100);
            this.getFormHM().put("i9999", i9999);
            this.getFormHM().put("select_pre", select_pre);
            this.getFormHM().put("newfieldlist", fieldlist);
            this.getFormHM().put("cardno_value", "");
            KqCrads kqCrads = new KqCrads(this.getFrameconn());
            this.getFormHM().put("singmess", kqCrads.getSingleMessage(select_pre, a0100));
            String magcard_com = KqParam.getInstance().getMagcardCom();
            this.getFormHM().put("magcard_com", magcard_com);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
