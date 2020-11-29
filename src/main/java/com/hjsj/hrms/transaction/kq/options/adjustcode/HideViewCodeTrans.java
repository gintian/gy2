package com.hjsj.hrms.transaction.kq.options.adjustcode;

import com.hjsj.hrms.businessobject.kq.set.AdjustCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class HideViewCodeTrans extends IBusiness {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void execute() throws GeneralException {
        try {
            String table = (String) this.getFormHM().get("table");
            if (table == null)
                table = "q03";
            
            String flag = (String) this.getFormHM().get("flag");
            if (flag == null || flag.length() <= 0)
                flag = "";
            this.getFormHM().put("flag", flag);
            
            ArrayList fielditemlist = new AdjustCode().getFieldByOrder(table);
            this.getFormHM().put("fieldlist", fielditemlist);
            
            ArrayList v_h_list = new ArrayList();
            CommonData dataobj = new CommonData();
            dataobj.setDataName("显示");
            dataobj.setDataValue("1");
            v_h_list.add(dataobj);
            dataobj = new CommonData();
            dataobj.setDataName("隐藏");
            dataobj.setDataValue("0");
            v_h_list.add(dataobj);
            this.getFormHM().put("v_h_list", v_h_list);
            
            this.getFormHM().put("tablemess", getTablemess(table));
            this.getFormHM().put("table", table);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private String getTablemess(String table) {
        String tablemess = "";
        if ("q03".equalsIgnoreCase(table))
            tablemess = "考勤数据表";
        else if ("q11".equalsIgnoreCase(table))
            tablemess = "加班申请表";
        else if ("q13".equalsIgnoreCase(table))
            tablemess = "公出申请表";
        else if ("q15".equalsIgnoreCase(table))
            tablemess = "请假申请表";
        return tablemess;
    }
}
