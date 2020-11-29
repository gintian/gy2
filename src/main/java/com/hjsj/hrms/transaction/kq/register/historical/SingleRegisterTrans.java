package com.hjsj.hrms.transaction.kq.register.historical;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * 检索个人考勤信息
 * @param userbase 库前缀
 * @param code 部门
 * @A0100 员工编号 
 * */
public class SingleRegisterTrans extends IBusiness {
    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String userbase = (String) hm.get("userbase");
        userbase = PubFunc.decrypt(userbase);
        String start_date = (String) hm.get("start_date");
        String end_date = (String) hm.get("end_date");
        String code = (String) hm.get("code");
        String rflag = (String) hm.get("rflag");
        String marker = (String) hm.get("marker");
        if (rflag == null || rflag.length() <= 0)
            rflag = "";
        this.getFormHM().put("rflag", rflag);
        if (code == null || code.length() <= 0) {
            code = "";
        }
        String A0100 = (String) hm.get("A0100");
        A0100 = PubFunc.decrypt(A0100); 
        ArrayList fieldlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        getSingleMessage(userbase, A0100);
        //ArrayList fielditemlist=RegisterInitInfoData.newFieldItemList(fieldlist);
        ArrayList fielditemlist = new ArrayList();
        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fieldlist.get(i);
            if ("1".equals(fielditem.getState())) {

                fielditem.setVisible(true);
            } else if ("q03z0".equals(fielditem.getItemid())) {
                fielditem.setVisible(true);

            } else {
                fielditem.setVisible(false);
            }
            if (!"state".equals(fielditem.getItemid())) {
                fielditemlist.add(fielditem.clone());
            }
        }
        StringBuffer column = new StringBuffer();
        for (int i = 0; i < fielditemlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            column.append(fielditem.getItemid() + ",");
        }
        String columnstr = column + "q03z5";
        String sqlstr = "select " + columnstr;
        StringBuffer wheresql = new StringBuffer();
        StringBuffer condition = new StringBuffer();
        wheresql.append(" from Q03_arc where");
        condition.append("  a0100='" + A0100 + "'");
        condition.append(" and nbase='" + userbase + "'");
        condition.append(" and Q03Z0 >= '" + start_date + "'");
        condition.append(" and Q03Z0 <= '" + end_date + "'");
        wheresql.append(" " + condition.toString());
        String orderby = " order by a0100";
        /** *****输出数据******* */
        int num = fielditemlist.size();
        String numstr = "" + num;
        
        //修改日明细登记数据
        String up_dailyregister = KqParam.getInstance().getUpdateDailyRegister();
        this.getFormHM().put("up_dailyregister", up_dailyregister);
        this.getFormHM().put("num", numstr);
        this.getFormHM().put("sqlstr", sqlstr);
        this.getFormHM().put("columns", columnstr);
        this.getFormHM().put("strwhere", wheresql.toString());
        this.getFormHM().put("orderby", orderby);
        this.getFormHM().put("singfielditemlist", fielditemlist);
        this.getFormHM().put("code", code);
        this.getFormHM().put("condition", "3`" + condition.toString());
        this.getFormHM().put("relatTableid", "3");
        this.getFormHM().put("returnURL", "/kq/register/browse_single.do?b_browse=link");
        this.getFormHM().put("marker", marker);
    }

    /***************************
     * 得到用户的基本信息
     * @param userbase 库前缀
     * @param code 部门
     * @A0100 员工编号 
     * 直接this.getFormHM().put();
     * 
     * */
    public void getSingleMessage(String userbase, String A0100) {
        StringBuffer sql = new StringBuffer();
        sql.append("select b0110,e0122,e01a1,a0101 ");
        sql.append(" from " + userbase + "A01 ");
        sql.append(" where a0100='" + A0100 + "'");
        String b0110 = "";
        String e0122 = "";
        String e01a1 = "";
        String a0101 = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next()) {
                b0110 = (String) this.frowset.getString("b0110");
                e0122 = (String) this.frowset.getString("e0122");
                e01a1 = (String) this.frowset.getString("e01a1");
                a0101 = (String) this.frowset.getString("a0101");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.getFrameconn());
        String rest_date = restList.get(0).toString();
        this.getFormHM().put("rest_date", rest_date);
        String b0110_value = b0110;
        this.getFormHM().put("b0110_value", b0110_value);
        b0110 = AdminCode.getCodeName("UN", b0110);
        e0122 = AdminCode.getCodeName("UM", e0122);
        e01a1 = AdminCode.getCodeName("@K", e01a1);
        this.getFormHM().put("b0110", b0110);
        this.getFormHM().put("e0122", e0122);
        this.getFormHM().put("e01a1", e01a1);
        this.getFormHM().put("a0101", a0101);
    }
}
