package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SaveFeastInfoTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            ArrayList userlist = (ArrayList) this.getFormHM().get("list");
            String hols_status = (String) this.getFormHM().get("hols_status");
            if (userlist == null)
                return;

            String kq_year = (String) this.getFormHM().get("kq_year");
            int year = 0;
            if (kq_year == null || kq_year.length() <= 0) {
                Calendar now = Calendar.getInstance();
                Date cur_d = now.getTime();
                year = DateUtils.getYear(cur_d);
            } else {
                year = Integer.parseInt(kq_year);
            }

            Date d1 = DateUtils.getDate(year, 1, 1);
            Date d2 = DateUtils.getDate(year, 12, 31);
            String feast_start = DateUtils.format(d1, "yyyy.MM.dd");
            String feast_end = DateUtils.format(d2, "yyyy.MM.dd");

            DynaBean dbean = null;
            String a0100 = null;
            String nbase = null;
            String q1701 = null;//年

            ArrayList fielditemlist = DataDictionary.getFieldList("Q17", Constant.USED_FIELD_SET);
            ArrayList lis_update = new ArrayList();
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            int decimal = 0;
            String item_value = "";

            for (int i = 0; i < userlist.size(); i++) {

                ArrayList one_list_update = new ArrayList();
                dbean = (LazyDynaBean) userlist.get(i);
                a0100 = (String) dbean.get("a0100");
                nbase = (String) dbean.get("nbase");
                q1701 = (String) dbean.get("q1701");
                one_list_update.add(a0100);
                one_list_update.add(nbase);
                one_list_update.add(q1701);
                lis_update.add(one_list_update);

                StringBuffer set_sql = new StringBuffer();
                ArrayList lists = new ArrayList();
                for (int r = 0; r < fielditemlist.size(); r++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(r);
                    String itemid = fielditem.getItemid();
                    if ("N".equalsIgnoreCase(fielditem.getItemtype()) && !"q1707".equalsIgnoreCase(fielditem.getItemid())) {
                        float item_value_f = 0;
                        decimal = fielditem.getDecimalwidth();
                        item_value = (String) dbean.get(itemid);
                        item_value = (item_value == null || ".".equals(item_value.trim()) || item_value.length() <= 0) ? "0" : item_value;
                        item_value_f = KQRestOper.round(item_value, decimal);
                        lists.add(itemid + "=" + item_value_f);
                    }
                    
                    if ("D".equalsIgnoreCase(fielditem.getItemtype())) {
                        item_value = (String) dbean.get(itemid);
                        String item_value_d = "";
                        if ("q17z1".equalsIgnoreCase(itemid)) {
                            if (item_value == null || item_value.length() < 8) {

                                item_value_d = Sql_switcher.dateValue(feast_start);
                            } else {
                                item_value_d = Sql_switcher.dateValue(item_value);
                            }
                        } else if ("q17z3".equalsIgnoreCase(itemid)) {
                            if (item_value == null || item_value.length() < 8) {
                                item_value_d = Sql_switcher.dateValue(feast_end);
                            } else {

                                item_value_d = Sql_switcher.dateValue(item_value);
                            }
                        } else if ("结余截止时间".equalsIgnoreCase(fielditem.getItemdesc())) {
                            if (item_value == null || item_value.length() < 8) {
                                item_value_d = Sql_switcher.dateValue(feast_end);
                            } else {

                                item_value_d = Sql_switcher.dateValue(item_value);
                            }
                        } else {
                            if (item_value == null || item_value.length() < 8) {
                                item_value_d = "";
                            } else {
                                item_value_d = Sql_switcher.dateValue(item_value);
                            }
                        }
                        
                        if (item_value_d != null && item_value_d.length() > 0)
                            lists.add(itemid + "=" + item_value_d);
                    }
                }
                
                for (int r = 0; r < lists.size(); r++) {
                    set_sql.append(lists.get(r).toString() + ",");
                }
                set_sql.setLength(set_sql.length() - 1);
                
                StringBuffer update = new StringBuffer();
                update.append("update q17 set");
                update.append(" " + set_sql.toString() + " ");
                update.append(" where a0100='" + a0100 + "' and nbase='" + nbase + "' and q1701='" + q1701 + "'");
                update.append(" and q1709='" + hols_status + "'");
                dao.update(update.toString());
            }
            updateData(lis_update);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void updateData(ArrayList list) {
        StringBuffer update = new StringBuffer();
        update.append("update q17 set");
        update.append(" q1707=q1703-q1705");
        update.append(" where a0100=?");
        update.append(" and nbase=?");
        update.append(" and q1701=?");

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            dao.batchUpdate(update.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
