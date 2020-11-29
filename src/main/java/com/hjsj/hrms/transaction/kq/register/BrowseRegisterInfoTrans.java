package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.SelectAllOperate;
import com.hjsj.hrms.businessobject.kq.query.CodingAnalytical;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class BrowseRegisterInfoTrans extends IBusiness {
    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        if (hm.get("select_flag") != null) {
            this.getFormHM().put("select_flag", hm.get("select_flag"));
            hm.remove("select_flag");
        }
        
        String kqAction = (String)hm.get("action");
        if (kqAction != null && !"".equals(kqAction))
            this.userView.getHm().put("kq_action", kqAction);
        
        SelectAllOperate selectAllOperate = new SelectAllOperate(this.getFrameconn(), this.userView);
        selectAllOperate.allOperate("q05");
        
        String kq_duration = (String) this.getFormHM().get("kq_duration");
        if (kq_duration == null || kq_duration.length() <= 0) {
            kq_duration = RegisterDate.getKqDuration(this.frameconn);
        }
        
        ArrayList kq_dbase_list = (ArrayList) this.getFormHM().get("kq_dbase_list");
        String select_flag = (String) this.getFormHM().get("select_flag");
        String select_name = (String) this.getFormHM().get("select_name");
        String select_pre = (String) this.getFormHM().get("select_pre");
        
        String select_type = (String) this.getFormHM().get("select_type");
        // 转换小时 1=默认；2=HH:MM
        String selectys = (String) hm.get("selectys");
        if (selectys == null || "".equals(selectys)) {
            String selectyis = (String) this.getFormHM().get("selectys");
            if (selectyis == null || "".equals(selectyis)) {
                selectys = "1";
            } else {
                selectys = selectyis;
            }

        }
        this.getFormHM().put("selectys", selectys);

        String code = (String) hm.get("code");
        
        String showtype = (String) this.getFormHM().get("showtype");
        if (showtype == null || showtype.length() <= 0) {
            if ("audit_registerdata.do".equals(this.userView.getHm().get("kq_action"))) {
                if (userView.hasTheFunction("0C321") && userView.hasTheFunction("0C323")) {
                    showtype = "08,02";
                } else if (userView.hasTheFunction("0C321")) {
                    showtype = "08";
                } else if (userView.hasTheFunction("0C323")) {
                    showtype = "02";
                } else {
                    showtype = "08,02";
                }
            }
            else
              showtype = "all";
        }
        
        String coursedate = (String) this.getFormHM().get("coursedate");
        
        HashMap map = new HashMap();
        HashMap maps = new HashMap();
        
        String code_kind = "";
        String kind = (String) hm.get("kind");
        if (kind == null || kind.length() <= 0) {
            kind = RegisterInitInfoData.getKindValue(kind, this.userView);
        }
        
        if (!"2".equals(kind)) {
            code_kind = RegisterInitInfoData.getDbB0100(RegisterInitInfoData.getKqPrivCodeValue(userView), kind, map, this.userView, this.getFrameconn());
        }
        
        if (code == null || code.length() <= 0)
            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
        
        if (kq_dbase_list == null || kq_dbase_list.size() <= 0) {
            kq_dbase_list = RegisterInitInfoData.getDase3(this.getFormHM(), this.userView, this.getFrameconn());
        } else {
            if (code != null && code.length() > 0) {
                if ("2".equals(kind)) {
                    kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(), code);
                } else if (code_kind != null && code_kind.length() > 0) {
                    kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(), code_kind);
                } else {
                    kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(), code);
                }
            } else {
                kq_dbase_list = RegisterInitInfoData.getDase3(this.getFormHM(), this.userView, this.getFrameconn());
            }
        }
        
        if (kq_dbase_list == null || kq_dbase_list.size() == 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.register.dbase.nosave"), "", ""));
        
        String user = "";
        for (int i = 0; i < kq_dbase_list.size(); i++) {
            user = user + "'" + kq_dbase_list.get(i).toString() + "',";
        }
        user = user.trim();
        user = user.substring(0, user.length() - 1);

        if (code == null || code.length() <= 0) {
            code = "";
        }

        ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        ArrayList fieldlist = RegisterInitInfoData.newFieldItemList(fielditemlist, this.userView, this.frameconn);
        FieldItem fielditem = new FieldItem();
        fielditem.setFieldsetid("Q05");
        fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.period"));
        fielditem.setItemid("scope");
        fielditem.setItemtype("A");
        fielditem.setCodesetid("0");
        fielditem.setVisible(true);
        fieldlist.add(fielditem);
        
        FieldItem fielditem1 = new FieldItem();
        fielditem1.setFieldsetid("Q05");
        fielditem1.setItemdesc(ResourceFactory.getProperty("kq.register.overrule"));
        fielditem1.setItemid("overrule");
        fielditem1.setItemtype("A");
        fielditem1.setCodesetid("0");
        fielditem1.setVisible(true);
        //zxj 20161222 列太多，审批意见是关键信息，审核（批）过程中比较关心此项，放最前面
        fieldlist.add(0, fielditem1);
        
        ArrayList courselist = RegisterDate.sessionDate(this.frameconn);
        if (courselist == null || courselist.size() == 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.register.session.nosave"), "", ""));    
        
        String cur_course;
        if (coursedate != null && coursedate.length() > 0) {
            cur_course = coursedate;
        } else {
            CommonData vo = (CommonData) courselist.get(0);
            cur_course = vo.getDataValue();
        }
            
        ArrayList sql_db_list = new ArrayList();
        if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
            sql_db_list.add(select_pre);
        } else {
            sql_db_list = kq_dbase_list;
        }
        
        String selectResult = (String) this.getFormHM().get("selectResult");
        if (selectResult != null && selectResult.length() > 0)
            selectResult = SafeCode.decode(selectResult);
        selectResult = PubFunc.keyWord_reback(selectResult);
        this.getFormHM().remove("selectResult");
        
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
        HashMap hashmap = para.getKqParamterMap();
        String g_no = (String) hashmap.get("g_no");
        String cardno = (String) hashmap.get("cardno");
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        fieldlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101", "Q03", g_no, cardno, fieldlist);
        
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
        String where_c = "";
        if ("0".equals(select_type)) {
            where_c = kqUtilsClass.getWhere_C(select_flag, "a0101", select_name);
        } else if ("1".equals(select_type)) {
            where_c = kqUtilsClass.getWhere_C(select_flag, g_no, select_name);
        } else {
            where_c = kqUtilsClass.getWhere_C(select_flag, cardno, select_name);
        }
        
        if (selectResult != null && selectResult.length() > 0 && !"0".equals(select_flag)) {
            where_c = where_c + " AND " + new CodingAnalytical().analytical(selectResult);
        }
        
        if ("1".equals(select_flag)) {
            this.getFormHM().put("selectResult", selectResult);
        }
        
        this.getFormHM().put("select_flag", select_flag);
        selectAllOperate.operateQ05State(kq_dbase_list, kq_duration);
        
        this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(kq_dbase_list));
        this.getFormHM().put("showtype", showtype);
        
//        ArrayList sqllist = CollectRegister.getSqlstr5(fieldlist, sql_db_list, kq_duration, code, kind, "Q05", this.userView, showtype, where_c, this.frameconn);
        ArrayList sqllist = RegisterInitInfoData.getSqlstr5(fieldlist, sql_db_list, kq_duration, code, kind, "Q05", this.userView, showtype, where_c, this.frameconn);
        
        this.getFormHM().put("sqlstr", sqllist.get(0).toString());
        this.getFormHM().put("columns", sqllist.get(3).toString());
        this.getFormHM().put("strwhere", sqllist.get(1).toString());
        this.getFormHM().put("orderby", sqllist.get(2).toString());
        
        String strwhere = sqllist.get(1).toString();
        strwhere = strwhere + " and q03z5 <>'01'";
        this.getFormHM().put("strwhere", sqllist.get(1).toString());
        this.getFormHM().put("orderby", sqllist.get(2).toString());
        this.getFormHM().put("fielditemlist", fieldlist);
        this.getFormHM().put("courselist", courselist);

        ArrayList datelist = RegisterDate.getKqDate(this.frameconn, cur_course);
        String start_date = datelist.get(0).toString();
        String end_date = datelist.get(1).toString();
        this.getFormHM().put("coursedate", cur_course);
        this.getFormHM().put("code", code);
        this.getFormHM().put("kind", kind);
        this.getFormHM().put("datelist", datelist);
        this.getFormHM().put("start_date", start_date);
        this.getFormHM().put("end_date", end_date);
        this.getFormHM().put("kq_duration", kq_duration);
        this.getFormHM().put("kq_dbase_list", kq_dbase_list);
        
        String pigeonhole_flag = (String) this.getFormHM().get("pigeonhole_flag");
        String pigeonhole_flag2 = (String) this.getFormHM().get("pigeonhole_flag2");
        if (pigeonhole_flag == null || pigeonhole_flag.length() <= 0 || pigeonhole_flag2 == null || pigeonhole_flag2.length() <= 0) {
            pigeonhole_flag = "xxx";
            pigeonhole_flag2 = "yyy";
        }
        
        if (!"true".equals(pigeonhole_flag) && !"false".equals(pigeonhole_flag)) {
            this.getFormHM().put("pigeonhole_flag", "xxx");
            this.getFormHM().put("pigeonhole_flag2", "yyy");
        }
        
        if ("true".equals(pigeonhole_flag2) && !"xxx".equals(pigeonhole_flag)) {
            this.getFormHM().put("pigeonhole_flag2", "yyy");
        } else if ("yyy".equals(pigeonhole_flag2) && !"xxx".equals(pigeonhole_flag)) {
            this.getFormHM().put("pigeonhole_flag", "xxx");
        }
        
        String error_flag = "0";
        this.getFormHM().put("error_flag", error_flag);
        maps = count_Leave();
        this.getFormHM().put("kqItem_hash", maps);
        RegisterInitInfoData registerInitInfoData = new RegisterInitInfoData();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        registerInitInfoData.cleanState(dao, userView, kq_dbase_list, start_date, end_date, kq_duration);

        // 显示部门层数
        Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
        if (uplevel == null || uplevel.length() == 0)
            uplevel = "0";
        this.getFormHM().put("uplevel", uplevel);
    }

    /**
     * 考勤规则的一个hashmap集
     * 
     * @return
     * @throws GeneralException
     */
    public HashMap count_Leave() throws GeneralException {
        RowSet rs = null;
        String kq_item_sql = "select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";

        ContentDAO dao = new ContentDAO(this.getFrameconn());

        HashMap hashM = new HashMap();
        String fielditemid = "";
        try {
            rs = dao.search(kq_item_sql);
            while (rs.next()) {
                HashMap hashm_one = new HashMap();
                if (rs.getString("fielditemid") == null || rs.getString("fielditemid").length() <= 0)
                    continue;
                
                ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
                for (int i = 0; i < fielditemlist.size(); i++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    fielditemid = rs.getString("fielditemid");
                    if (fielditemid.equalsIgnoreCase(fielditem.getItemid())) {
                        hashm_one.put("fielditemid", rs.getString("fielditemid"));
                        hashm_one.put("has_rest", PubFunc.DotstrNull(rs.getString("has_rest")));
                        hashm_one.put("has_feast", PubFunc.DotstrNull(rs.getString("has_feast")));
                        hashm_one.put("item_unit", PubFunc.DotstrNull(rs.getString("item_unit")));
                        hashm_one.put("sdata_src", PubFunc.DotstrNull(rs.getString("sdata_src")));
                        hashM.put(fielditemid, hashm_one);
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return hashM;
    }
}
