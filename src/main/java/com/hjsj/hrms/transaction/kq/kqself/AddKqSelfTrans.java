/*
 * Created on 2006-3-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.kqself;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.kqself.KqSelfBusiness;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author wxh
 * 
 */
public class AddKqSelfTrans extends IBusiness {

    /*
     * (non-Javadoc)
     * 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {

        // HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        // String table = (String) hm.get("table");
        String table = (String) this.getFormHM().get("table");
        String ta = table.toLowerCase();
        ArrayList fieldlist = DataDictionary.getFieldList(table, Constant.USED_FIELD_SET);// 字段名
        ArrayList list = new ArrayList();
        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem field = (FieldItem) fieldlist.get(i);
            field.setValue("");
            field.setViewvalue("");
            if (field.getItemid().equals(ta + "05")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String strDate = sdf.format(new java.util.Date());
                field.setItemlength(20);
                field.setValue(strDate);
            }
            if (field.getItemid().equals(ta + "01") || field.getItemid().equals(ta + "09")
                    || field.getItemid().equals(ta + "11") || field.getItemid().equals(ta + "13")
                    || field.getItemid().equals(ta + "15") || field.getItemid().equals(ta + "z5")
                    || field.getItemid().equals(ta + "z0") || "nbase".equals(field.getItemid())
                    || "a0100".equals(field.getItemid()) || "b0110".equals(field.getItemid())
                    || "e0122".equals(field.getItemid()) || "e01a1".equals(field.getItemid())
                    || "a0101".equals(field.getItemid()))
                field.setVisible(false);
            else
            // field.setVisible(true);
            if ("q15".equalsIgnoreCase(table)) {
                if ("q1517".equals(field.getItemid()) || "q1519".equals(field.getItemid()))
                    field.setVisible(false);
            } else {
                if (field.getState() != null && "1".equals(field.getState())) {
                    field.setVisible(true);
                } else {
                    field.setVisible(false);
                }
            }

            FieldItem field_n = (FieldItem) field.cloneItem();
            list.add(field_n);
        }
        // szk取默认时间
        String strDate = RegisterDate.getDefaultDay(this.frameconn);

        this.formHM.put("start_d", strDate);
        this.formHM.put("end_d", "");
        this.formHM.put("mess", "");
        this.formHM.put("scope_start_time", strDate + " 00:00");
        this.formHM.put("scope_end_time", strDate + " 23:59");
        
        // linbz 20161114 我的考勤日历传日期参数
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String empcalday = (String)hm.get("empcalday");
        empcalday =  empcalday == null ? "" : empcalday;
        if(!"null".equals(empcalday) && empcalday!=null && empcalday.length()>0){
        	empcalday = empcalday.replaceAll( "\\.", "-");
        	this.formHM.put("start_d", empcalday);
        	this.formHM.put("scope_start_time", empcalday + " 00:00");
            this.formHM.put("scope_end_time", empcalday + " 23:59");
            this.formHM.put("kqempcal", "1");
            hm.remove("empcalday");
        }else{
        	this.formHM.put("kqempcal", "0");
        }

        this.getFormHM().put("table", table);
        this.getFormHM().put("fieldlist", list);
        this.getFormHM().put("id", "");
        if ("q15".equalsIgnoreCase(table)) {
            String leave_rule = KqParam.getInstance().getLeavetimeRule(this.frameconn, this.userView.getUserOrgId());
            if (leave_rule == null || leave_rule.length() <= 0)
                leave_rule = "";
            this.getFormHM().put("rule_day", leave_rule);
        }
        // 页面下拉菜单数据获得
        KqUtilsClass kqcl = new KqUtilsClass(this.frameconn, this.userView);
        ArrayList kqcllist = new ArrayList();
        kqcllist = kqcl.getKqClassListInPriv();
        LazyDynaBean ldb = new LazyDynaBean();
        CommonData da = new CommonData();
        ArrayList class_list = new ArrayList();
        da.setDataName("<无>");
        da.setDataValue("#");
        class_list.add(da);
        for (int i = 0; i < kqcllist.size(); i++) {
            String onduty = "";
            String offduty = "";
            ldb = (LazyDynaBean) kqcllist.get(i);
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
        SearchAllApp searchAllApp = new SearchAllApp();
        this.getFormHM().put("selist", searchAllApp.getTableList(table, this.getFrameconn()));
        this.getFormHM().put("sels", "#");
        this.getFormHM().put("class_list", class_list);
        KqSelfBusiness kqSelfBusiness = new KqSelfBusiness(this.userView, this.getFrameconn());
        String isTemplate = "0";
        if (kqSelfBusiness.getIsTemplate(table))
            isTemplate = "1";
        this.getFormHM().put("isTemplate", isTemplate);

        if ("q15".equalsIgnoreCase(table)) {
            this.getFormHM().put("dert_itemid", "");// 是否有扣除休息时间
        }
        if ("q11".equalsIgnoreCase(table)) {
            this.getFormHM().put("dert_itemid", searchAllApp.isDeductResttime(table));// 是否有扣除休息时间
        }
        if ("q13".equalsIgnoreCase(table)) {
            this.getFormHM().put("dert_itemid", "");// 是否有扣除休息时间
        }

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
