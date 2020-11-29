package com.hjsj.hrms.transaction.train.attendance.card;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title:EmpRetListTrans.java
 * </p>
 * <p>
 * Description>:EmpRetListTrans.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Mar 14, 2011 3:05:36 PM
 * </p>
 * <p>
 * @version: 5.0
 * </p>
 * <p>
 * @author: 郑文龙
 */
public class EmpRetListTrans extends IBusiness {

    private static final long serialVersionUID = 1L;

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String courseplan = (String) hm.get("courseplan");// 培训课程
        String classplan = (String) hm.get("classplan");// 培训班
        if(courseplan != null && courseplan.length() > 0)
            courseplan = PubFunc.decrypt(SafeCode.decode(courseplan));
        
        if(classplan != null && classplan.length() > 0)
            classplan = PubFunc.decrypt(SafeCode.decode(classplan));
        hm.remove("courseplan");
        ConstantXml constantbo = new ConstantXml(this.getFrameconn(), "TR_PARAM");
        String card_no = constantbo.getTextValue("/param/attendance/card_no");// 获得考勤卡字段名称
        if (card_no == null || card_no.length() < 1) {
            throw new GeneralException(ResourceFactory.getProperty("train.attendance.set.kqcard") + "!");
        }
        ArrayList nbases = DataDictionary.getDbpreList();
        StringBuffer innTable = new StringBuffer();
        for (int i = 0; i < nbases.size(); i++) {
            String nbase = (String) nbases.get(i);
            innTable.append("SELECT  " + nbase + "A01." + card_no + ",a0100,R.nbase nbase,a0101,R.e0122,R.b0110,e01a1 FROM " + nbase + "A01 INNER JOIN R40 R ON R.R4001=" + nbase + "A01.A0100 AND R.nbase='" + nbase
                    + "' WHERE r4013='03' AND r4005='" + classplan + "' UNION ");
        }
        innTable.delete(innTable.length() - 6, innTable.length());
        String columns = "a0100,nbase,b0110,e0122,a0101,e01a1," + card_no;
        String sql_str = "SELECT " + columns;
        sql_str = sql_str.replace(card_no, "A." + card_no);
        String cond_str = " FROM (" + innTable + ") A WHERE " + Sql_switcher.isnull(card_no, "'#'") + "<>'#'";
        this.getFormHM().put("courseplan", SafeCode.encode(PubFunc.encrypt(courseplan)));
        /* 初始化 */
        this.getFormHM().put("regFlag", "3");
        this.getFormHM().put("nowDate", OperateDate.dateToStr(new Date(), "yyyy-MM-dd"));
        this.getFormHM().put("nowHours", "00");
        this.getFormHM().put("nowMinutes", "00");
        /* 初始化 */
        this.getFormHM().put("columns", columns);
        this.getFormHM().put("sql_str", sql_str);
        this.getFormHM().put("cond_str", cond_str);
        this.getFormHM().put("card_no", card_no.toLowerCase());
    }

}
