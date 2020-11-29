package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SaveChangeAppTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String mess = (String) this.getFormHM().get("mess");
            String table = (String) this.getFormHM().get("table");
            ArrayList viewlist = DataDictionary.getFieldList(table, Constant.USED_FIELD_SET);// 字段名
            for (int i = 0; i < viewlist.size(); i++) {
                FieldItem field = (FieldItem) viewlist.get(i);
                String itemid = field.getItemid();
                String value = (String) this.getFormHM().get(itemid);
                if (value == null || value.length() <= 0)
                    continue;
                
                field.setValue(value);
            }
            
            java.util.Date kq_start = null;
            java.util.Date kq_end = null;

            String ta = table.toLowerCase();

            for (int i = 0; i < viewlist.size(); i++) {
                FieldItem field = (FieldItem) viewlist.get(i);
                /** 不分析是否在考勤期内,chenmengqing added at 20070214 */
                if (field.getItemid().equals(ta + "z1") && "D".equals(field.getItemtype())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if (field.getValue().length() == 8) {
                        String tem = field.getValue();
                        String mm = tem.replaceAll("-", "-0");
                        kq_start = Date.valueOf(sdf.format(DateUtils.getDate(mm, "yyyy-MM-dd")));
                    } else {
                        kq_start = Date.valueOf(sdf.format(DateUtils.getDate(field.getValue().toString(), "yyyy-MM-dd")));
                    }
                }
                
                if (field.getItemid().equals(ta + "z3") && "D".equals(field.getItemtype())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if (field.getValue().length() == 8) {
                        String tem = field.getValue();
                        String mm = tem.replaceAll("-", "-0");
                        kq_end = Date.valueOf(sdf.format(DateUtils.getDate(mm, "yyyy-MM-dd")));

                    } else {
                        kq_end = Date.valueOf(sdf.format(DateUtils.getDate(field.getValue().toString(), "yyyy-MM-dd")));
                    }
                }
            }
            
            /** 判断开始日期是否在结束日期之前 */
            if (kq_start.after(kq_end))
                throw new GeneralException(ResourceFactory.getProperty("error.kq.wrongrequence"));

            String sp = (String) this.getFormHM().get("sp");
            if (sp == null || "".equalsIgnoreCase(sp))
                sp = "0";

            boolean isCorrect = update(table, mess, viewlist, sp);
            if (isCorrect && "1".equals(sp))
                this.getFormHM().put("spFlag", "申请批准成功！");
            else if (!isCorrect && "1".equals(sp))
                this.getFormHM().put("spFlag", "申请批准失败！");
            else if (isCorrect && "2".equals(sp))
                this.getFormHM().put("spFlag", "申请驳回成功！");
            else if (!isCorrect && "2".equals(sp))
                this.getFormHM().put("spFlag", "申请驳回失败！");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 员工->考勤员（或部门经理）签批或者考勤员直接录入
     * 
     * @param table
     * @param man
     * @param viewlist
     * @param sp
     *            =1 批准 =2驳回
     * @throws GeneralException
     */
    private boolean update(String table, String man, ArrayList viewlist, String sp) throws GeneralException {
        String insertid = "";
        String insertname = table + "01";
        boolean isCorrect = true;
        for (int i = 0; i < viewlist.size(); i++) {
            FieldItem field = (FieldItem) viewlist.get(i);
            if (field.getItemid().equalsIgnoreCase(insertname)) {
                insertid = field.getValue();
                break;
            }
        }

        if (insertid == null || insertid.length() <= 0)
            return true;

        String ta = table.toLowerCase();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        AnnualApply annualApply = new AnnualApply(this.userView, this.getFrameconn());
        try {
            RecordVo vo = new RecordVo(table);
            vo.setString(ta + "01", insertid);
            vo = dao.findByPrimaryKey(vo);
            insertname = ta + "01";
            java.util.Date kq_start = null;
            java.util.Date kq_end = null;
            String start = "";
            String end = "";

            for (int i = 0; i < viewlist.size(); i++) {
                FieldItem field = (FieldItem) viewlist.get(i);
                if (field.getItemid().equals(insertname))
                    continue;

                if ("q1517".equalsIgnoreCase(field.getItemid()) || "q1519".equalsIgnoreCase(field.getItemid()))
                    continue;

                if ("N".equals(field.getItemtype()))
                    vo.setDouble(field.getItemid().toLowerCase(), Double.parseDouble(field.getValue()));

                if ("D".equals(field.getItemtype()) && (field.getItemid().equals(ta + "z1") || field.getItemid().equals(ta + "z3"))) {

                    java.util.Date dd = DateUtils.getDate(field.getValue(), "yyyy-MM-dd HH:mm");
                    if (field.getItemid().equals(ta + "z1")) {
                        start = DateUtils.format(dd, "yyyy-MM-dd HH:mm");
                        kq_start = dd;
                    }
                    if (field.getItemid().equals(ta + "z3")) {
                        end = DateUtils.format(dd, "yyyy-MM-dd HH:mm");
                        kq_end = dd;
                    }
                    vo.setDate(field.getItemid().toLowerCase(), dd);
                } else if ("D".equals(field.getItemtype()) && (field.getItemid().equals(ta + "05"))) {
                    java.util.Date dd = DateUtils.getDate(field.getValue(), "yyyy-MM-dd HH:mm");
                    vo.setDate(field.getItemid().toLowerCase(), dd);
                } else {
                    if (field.getItemid().equals(ta + "03")) {
                        vo.setString(field.getItemid(), man);

                    } else {
                        vo.setString(field.getItemid().toLowerCase(), field.getValue());
                    }
                }
            }
            if ("1".equalsIgnoreCase(sp)) {
                annualApply.checkAppInSealDuration(kq_start);

                isCorrect = !annualApply.isRepeatedAllAppType(ta, man, vo.getString("a0100"), vo.getString("a0101"), start, end, this.getFrameconn(), insertid, "");

                vo.setString(ta + "z0", "01"); // 同意
                vo.setString(ta + "z5", "03"); // 已批
            }
            
            if ("2".equalsIgnoreCase(sp)) {
                vo.setString(ta + "z0", "02"); // 02 不同意
                vo.setString(ta + "z5", "07"); // 07 驳回
            }
            
            if ("q11".equalsIgnoreCase(ta)) {
                annualApply.overTimeApp("up", vo, man, kq_start, kq_end, isCorrect, sp);
            } else if ("q13".equalsIgnoreCase(ta)) {
                annualApply.awayTimeApp("up", vo, man, kq_start, kq_end, isCorrect, sp);
            } else if ("q15".equalsIgnoreCase(ta)) {
                annualApply.leaveTimeApp("up", vo, man, kq_start, kq_end, isCorrect, sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isCorrect = false;
            throw GeneralExceptionHandler.Handle(e);
        }
        return isCorrect;
    }
}
