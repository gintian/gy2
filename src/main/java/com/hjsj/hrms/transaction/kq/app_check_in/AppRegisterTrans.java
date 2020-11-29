package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 申请登记
 * <p>
 * Title:AppRegisterTrans.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Oct 16, 2007
 * </p>
 * 
 * @author sunxin
 * @version 4.0
 */
public class AppRegisterTrans extends IBusiness {

    public void execute() throws GeneralException {
        String table = (String) this.getFormHM().get("table");
        if (table == null || "".equals(table))
            return;
        SearchAllApp searchAllApp = new SearchAllApp(this.getFrameconn(), this.userView);
        searchAllApp.reconstructionApp(table);
        this.getFormHM().put("app_type_list", searchAllApp.getTableList(table, this.getFrameconn()));
        if ("q15".equalsIgnoreCase(table)) {
            this.getFormHM().put("dert_itemid", "");// 是否有扣除休息时间
        }
        if ("q11".equalsIgnoreCase(table)) {
            this.getFormHM().put("dert_itemid", searchAllApp.isDeductResttime(table));// 是否有扣除休息时间
        }
        if ("q13".equalsIgnoreCase(table)) {
            this.getFormHM().put("dert_itemid", "");// 是否有扣除休息时间
        }
        // szk取默认时间
        String strDate = RegisterDate.getDefaultDay(this.frameconn);

        this.getFormHM().put("app_fashion", "0");
        this.getFormHM().put("intricacy_app_fashion", "0");
        this.getFormHM().put("intricacy_app_start_date", strDate);
        this.getFormHM().put("intricacy_app_end_date", strDate);
        this.getFormHM().put("intricacy_app_start_time_h", "00");
        this.getFormHM().put("intricacy_app_end_time_h", "00");
        this.getFormHM().put("intricacy_app_start_time_m", "00");
        this.getFormHM().put("intricacy_app_end_time_m", "00");
        this.getFormHM().put("easy_app_start_date", strDate);
        this.getFormHM().put("scope_start_time", strDate + " 00:00");
        this.getFormHM().put("scope_end_time", strDate + " 23:59");

        KqUtilsClass kqcl = new KqUtilsClass(this.frameconn, this.userView);
        ArrayList list = new ArrayList();
        list = kqcl.getKqClassListInPriv();
        LazyDynaBean ldb = new LazyDynaBean();
        CommonData da = new CommonData();
        ArrayList class_list = new ArrayList();
        da.setDataName("<无>");
        da.setDataValue("#");
        class_list.add(da);
        for (int i = 0; i < list.size(); i++) {
            String onduty = "";
            String offduty = "";
            ldb = (LazyDynaBean) list.get(i);
            if("0".equals((String)ldb.get("classId"))){
                continue;
            }
            da = new CommonData();
            onduty = (String) ldb.get("onduty_1");
            for (int j = 3; j > 0; j--) {
                offduty = (String) ldb.get("offduty_" + j);
                if (offduty != null && offduty.length() == 5)
                    break;
            }
            if (onduty != null && onduty.trim().length() > 0 && offduty != null && offduty.trim().length() > 0) {
                da.setDataName((String) ldb.get("name") + "(" + onduty + "~" + offduty + ")");
                da.setDataValue((String) ldb.get("classId"));
                class_list.add(da);
            }
          //29404增加过滤 班次时间不完整直接过滤掉，不显示
//            else{
//                da.setDataName((String) ldb.get("name") + "()");
//                da.setDataValue((String) ldb.get("classId"));
//                class_list.add(da);
//            }
        }

        this.getFormHM().put("class_list", class_list);

        this.getAppReaCode();

        String isExistIftoRest = KqUtilsClass.getFieldByDesc(table,
                ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
        if (isExistIftoRest != null && isExistIftoRest.length() > 0)
            this.getFormHM().put("isExistIftoRest", "1");
    }

    private void getAppReaCode() {
        StringBuffer sb = new StringBuffer();
        sb.append("select * from t_hr_busifield");
        sb.append(" where fieldsetid = 'Q11'");
        sb.append(" and itemdesc like '%加班原因%'");
        ContentDAO dao = new ContentDAO(frameconn);
        try {
            this.frecset = dao.search(sb.toString());
            if (this.frecset.next()) {
                String itemid = this.frecset.getString("itemid");
                String codesetid = this.frecset.getString("codesetid");

                if (codesetid != null && codesetid.length() > 0) {
                    DbWizard dbWizard = new DbWizard(frameconn);
                    boolean isExist = dbWizard.isExistField("Q11", itemid, false);
                    if (isExist) {
                        this.getFormHM().put("appReaCodesetid", codesetid);
                        this.getFormHM().put("appReaField", itemid);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
