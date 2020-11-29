package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCrads;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 编辑 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 21, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class EditCardnoUseTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String nbase = (String) this.getFormHM().get("nbase");
            String a0100 = (String) this.getFormHM().get("a0100");
            String i9999 = (String) this.getFormHM().get("i9999");
            String magcard_setid = (String) this.getFormHM().get("magcard_setid");
            ArrayList fieldlist = DataDictionary.getFieldList(magcard_setid.toLowerCase(), Constant.USED_FIELD_SET);
            this.getFormHM().put("a0100", a0100);
            this.getFormHM().put("i9999", i9999);
            this.getFormHM().put("nbase", nbase);
            this.getFormHM().put("newfieldlist", fieldlist);
            KqCrads kqCrads = new KqCrads(this.getFrameconn());
            this.getFormHM().put("singmess", kqCrads.getSingleMessage(nbase, a0100));
            this.getFormHM().put("changefieldlist", getEditFieldlist(nbase, a0100, i9999, magcard_setid, fieldlist));
            String magcard_com = KqParam.getInstance().getMagcardCom();
            this.getFormHM().put("magcard_com", magcard_com);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList getEditFieldlist(String select_pre, String a0100, String i9999, String magcard_setid, ArrayList fieldlist) {
        ArrayList list = new ArrayList();
        String sql = "select * from " + select_pre + magcard_setid + " where a0100='" + a0100 + "' and i9999='" + i9999 + "'";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sql);
            if (this.frowset.next()) {
                for (int i = 0; i < fieldlist.size(); i++) {
                    FieldItem field = (FieldItem) fieldlist.get(i);
                    FieldItem cfield = (FieldItem) field.clone();
                    if ("0".equals(cfield.getCodesetid())) {
                        if ("N".equals(cfield.getItemtype())) {
                            cfield.setValue(this.frowset.getString(cfield.getItemid()));
                        } else if ("D".equals(cfield.getItemtype())) {
                            java.util.Date dd = this.frowset.getDate(cfield.getItemid());
                            if (dd != null)
                                cfield.setValue(DateUtils.format(dd, "yyyy-MM-dd"));
                            else
                                cfield.setValue("");
                        } else {
                            cfield.setValue(this.frowset.getString(cfield.getItemid()));
                        }
                    } else {
                        String codevalue = this.frowset.getString(cfield.getItemid()) != null ? this.frowset.getString(
                                cfield.getItemid()).toString() : ""; //是,转换代码->数据描述	       //是,转换代码->数据描述	
                        String codesetid = cfield.getCodesetid();
                        if (codevalue != null && codevalue.trim().length() > 0 && codesetid != null
                                && codesetid.trim().length() > 0) {
                            String value = AdminCode.getCode(codesetid, codevalue) != null
                                    && AdminCode.getCode(codesetid, codevalue).getCodename() != null ? AdminCode.getCode(
                                    codesetid, codevalue).getCodename() : "";
                            cfield.setViewvalue(value);
                        } else {
                            cfield.setViewvalue("");
                        }
                        cfield.setValue(this.frowset.getString(cfield.getItemid()));
                    }

                    list.add(cfield);
                }
            } else {
                list = (ArrayList) fieldlist.clone();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
