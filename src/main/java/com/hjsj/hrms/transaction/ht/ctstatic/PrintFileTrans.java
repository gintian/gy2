package com.hjsj.hrms.transaction.ht.ctstatic;

import com.hjsj.hrms.businessobject.hire.ExecuteExcel;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:PrintFileTrans.java
 * </p>
 * <p>
 * Description:合同统计输出Excel
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-03-21 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class PrintFileTrans extends IBusiness {
    public void execute() throws GeneralException {
        String cols = (String) this.getFormHM().get("cols");
        cols = PubFunc.hireKeyWord_filter_reback(cols);
        cols = PubFunc.decrypt(cols);
        String sqlStr = (String)this.userView.getHm().get("ht_sql");
        ArrayList fieldList = new ArrayList();

        String[] colArray = cols.split(",");
        for (int i = 0; i < colArray.length; i++) {
            String col = colArray[i];
            if (col.length() == 0)
                continue;
            //过滤大字段（sqlserver没有问题，orcle有问题（参考页面显示时拼接的sql语句））
            FieldItem fielditem = DataDictionary.getFieldItem(col);
            if("M".equalsIgnoreCase(fielditem.getItemtype().toUpperCase()))
                continue;
            
            fieldList.add(fielditem);
        }

        String outName = this.userView.getUserName() + "_contract.xls";
        ExecuteExcel executeExcel = new ExecuteExcel(this.getFrameconn(), this.getUserView(), "");
        executeExcel.setFileName(outName);
        outName = executeExcel.createTabExcelHt(fieldList, sqlStr, "2");
        outName = com.hrms.frame.codec.SafeCode.encode(PubFunc.encrypt(outName));
        this.getFormHM().put("outName", outName);
    }

}
