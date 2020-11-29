package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCrads;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 换卡
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 17, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class ChangeMagManagerTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            //    	String select_pre=(String)this.getFormHM().get("select_pre");
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
            this.getFormHM().put("changefieldlist", getChangeFieldlist(select_pre, a0100, i9999, magcard_setid, fieldlist));
            String magcard_com = KqParam.getInstance().getMagcardCom();
            this.getFormHM().put("magcard_com", magcard_com);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList getChangeFieldlist(String select_pre, String a0100, String i9999, String magcard_setid, ArrayList fieldlist) {
        ArrayList list = new ArrayList();
        String sql = "select * from " + select_pre + magcard_setid 
                   + " where a0100='" + a0100 + "'";
        //zxj changed 2014.04.29 子集才有i9999条件
        if (!"A01".equalsIgnoreCase(magcard_setid))
            sql = sql + " AND i9999=" + i9999;
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sql);
            if (this.frowset.next()) {
                for (int i = 0; i < fieldlist.size(); i++) {
                    FieldItem field = (FieldItem) fieldlist.get(i);
                    FieldItem cfield = (FieldItem) field.clone();
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
