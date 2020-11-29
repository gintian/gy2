package com.hjsj.hrms.transaction.kq.register.history;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
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

public class DailyBrowseDataTrans extends IBusiness {
    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String coursedate = (String) this.getFormHM().get("coursedate");// 考勤期间
        String code = (String) this.getFormHM().get("code");
        CheckPrivSafeBo checkPriv = new CheckPrivSafeBo(this.frameconn, this.userView);
        code = checkPriv.checkOrg(code, "");
        String kind = (String) this.getFormHM().get("kind");
        String year = (String) this.getFormHM().get("year");// 考勤期间
        String duration = (String) this.getFormHM().get("duration");
        ArrayList yearlist = (ArrayList) this.getFormHM().get("yearlist");
        ArrayList durationlist = (ArrayList) this.getFormHM().get("durationlist");
        ArrayList courselist = (ArrayList) this.getFormHM().get("courselist");
        ArrayList kq_dbase_list = (ArrayList) this.getFormHM().get("kq_dbase_list");
        String registerdate = (String) this.getFormHM().get("registerdate");
        ArrayList datelist = (ArrayList) this.getFormHM().get("datelist");
        String select_flag = (String) this.getFormHM().get("select_flag");
        String select_name = (String) this.getFormHM().get("select_name");
        String select_type = (String) this.getFormHM().get("select_type");
        // zxj 注释掉，查询条件需保留
        //		this.getFormHM().put("select_type", "0");
        //		this.getFormHM().put("select_name", "");
        String select_pre = (String) this.getFormHM().get("select_pre");
        this.getFormHM().put("select_flag", select_flag);
        // this.getFormHM().put("select_name",select_name);
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
        HashMap map = new HashMap();
        HashMap maps = new HashMap();
        String code_kind = "";
        if (kind == null || kind.length() <= 0) {
            kind = RegisterInitInfoData.getKindValue(kind, this.userView);
            code = "";
        }
        if ("2".equals(kind)) {
            if (code == null || code.length() <= 0) {
                code = RegisterInitInfoData.getKqPrivCodeValue(userView); // 成空
                // 修改为如果 code 不为空就不得到用户的权限；改正 0017417 BUG
                if (code == null || code.length() <= 0) {
                    code = this.userView.getManagePrivCodeValue(); // 成空
                }
            }
        } else {
            code_kind = RegisterInitInfoData.getDbB0100(RegisterInitInfoData.getKqPrivCodeValue(userView), kind, map,
                    this.userView, this.getFrameconn());
        }
//  zxj 2014.01.16 业务平台怎么可能按操作人员所在机构显示数据呢？！
//        if (code == null || code.length() <= 0)
//            code = this.userView.getUserOrgId(); // 成了SM
        
        if (kq_dbase_list == null || kq_dbase_list.size() <= 0) {
            // kq_dbase_list=userView.getPrivDbList();
            kq_dbase_list = RegisterInitInfoData.getDase3(this.getFormHM(), this.userView, this.getFrameconn());
        } else {
            if (code != null && code.length() > 0) {
                if ("2".equals(kind)) {
                    kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(), code);
                } else if (code_kind != null && code_kind.length() > 0) {
                    kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(),
                            code_kind);
                } else {
                    kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(), code);
                }
            } else {
                kq_dbase_list = RegisterInitInfoData.getDase3(this.getFormHM(), this.userView, this.getFrameconn());
            }
        }
        String cur_course = "";
        if (coursedate == null || coursedate.length() <= 0) {
            courselist = RegisterDate.sessionDate(this.frameconn, "1");
            if (courselist != null && courselist.size() > 0) {
                if (coursedate != null && coursedate.length() > 0) {
                    cur_course = coursedate;
                } else {
                    CommonData vo = (CommonData) courselist.get(0);
                    cur_course = vo.getDataValue();
                }
            } else {

                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory
                        .getProperty("kq.register.session.nohistory"), "", ""));
            }
        } else {
            cur_course = coursedate;
        }
        String cur_year = "";
        String cur_duration = "";
        if (cur_year == null || cur_year.length() <= 0) {
            yearlist = RegisterDate.yearDate(this.frameconn, "1");
            if (yearlist != null && yearlist.size() > 0) {
                if (year != null && year.length() > 0) {
                    cur_year = year;
                } else {
                    CommonData vy = (CommonData) yearlist.get(0);
                    cur_year = vy.getDataValue();
                }
            } else {

                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory
                        .getProperty("kq.register.session.nohistory"), "", ""));
            }
        } else {
            cur_year = year;
        }
        if (cur_duration == null || cur_duration.length() <= 0) {
            durationlist = RegisterDate.durationDate(this.frameconn, "1", cur_year);
            if (durationlist != null && durationlist.size() > 0) {
                if (duration != null && duration.length() > 0) {
                    cur_duration = duration;
                } else {
                    CommonData vd = (CommonData) durationlist.get(0);
                    cur_duration = vd.getDataValue();
                }
            } else {

                throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory
                        .getProperty("kq.register.session.nohistory"), "", ""));
            }
        } else {
            cur_duration = duration;
        }

        if (code == null || code.length() <= 0) {
            code = "";
        }

        if (kind == null || kind.length() <= 0) {
            kind = "2";
        }
        String cur_date = "";
        if (registerdate != null && registerdate.length() > 0) {
            cur_date = registerdate;
        } else {
            if (datelist != null && datelist.size() > 0) {
                CommonData vo = (CommonData) datelist.get(0);
                cur_date = vo.getDataValue();
            }
        }
        //判断Q03_arc归档表是否存在
        DbWizard dbw = new DbWizard(this.frameconn);
        boolean isQ03_arc = dbw.isExistTable("Q03_arc",false);
        boolean dataInQ03 = dataInQ03(registerdate);
        ArrayList fieldlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        ArrayList fielditemlist = RegisterInitInfoData.newFieldItemList(fieldlist, this.userView, this.frameconn);
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
        HashMap hashmap = para.getKqParamterMap();
        String g_no = (String) hashmap.get("g_no");
        String cardno = (String) hashmap.get("cardno");
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
//        fielditemlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101", "Q03", g_no, cardno, fielditemlist);
        //根据日期判断是否在归档表
        if(dataInQ03 || !isQ03_arc){
        	fielditemlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101", "Q03", g_no, cardno, fielditemlist);
        }else{
        	fielditemlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101", "Q03_arc", g_no, cardno, fielditemlist);
        }
        String workcalendar = RegisterInitInfoData.getDateSelectHtml(datelist, cur_date);
        ArrayList sql_db_list = new ArrayList();
        if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
            sql_db_list.add(select_pre);
        } else {
            sql_db_list = kq_dbase_list;
        }
        
        if (sql_db_list == null || sql_db_list.size() <= 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("", 
                    ResourceFactory.getProperty("kq.register.dbase.nosave"), 
                    "", ""));
        
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
        String where_c = null;
        if ("0".equals(select_type)) {
            where_c = kqUtilsClass.getWhere_C(select_flag, "a0101", select_name);
        } else if ("1".equals(select_type)) {
            where_c = kqUtilsClass.getWhere_C(select_flag, g_no, select_name);
        } else {
            where_c = kqUtilsClass.getWhere_C(select_flag, cardno, select_name);
        }
//        ArrayList sqllist = RegisterInitInfoData.getSqlstr5(fielditemlist, sql_db_list, cur_date, code, kind, "Q03",
//                this.userView, "all", where_c, this.frameconn);
        ArrayList sqllist = new ArrayList();
        if(dataInQ03 || !isQ03_arc){
        	sqllist = RegisterInitInfoData.getSqlstr5(fielditemlist, sql_db_list, cur_date, code, kind, "Q03",this.userView, "all", where_c, this.frameconn);
        }else{
        	sqllist = com.hjsj.hrms.businessobject.kq.register.history.RegisterInitInfoData.getSqlstr6(fielditemlist, sql_db_list, cur_date,code,kind,"Q03_arc",this.userView,"all",where_c,this.frameconn);
        }
        maps = count_Leave();
        this.getFormHM().put("kqItem_hash", maps);
        this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(kq_dbase_list));
        this.getFormHM().put("sqlstr", sqllist.get(0).toString());
        this.getFormHM().put("workcalendar", workcalendar);
        this.getFormHM().put("strwhere", sqllist.get(1).toString());
        this.getFormHM().put("orderby", sqllist.get(2).toString());
        this.getFormHM().put("columns", sqllist.get(3).toString());
        this.getFormHM().put("fielditemlist", fielditemlist);
        this.getFormHM().put("kq_dbase_list", kq_dbase_list);
        this.getFormHM().put("code", code);
        this.getFormHM().put("kind", kind);
        this.getFormHM().put("courselist", courselist);
        this.getFormHM().put("coursedate", cur_course);
        this.getFormHM().put("yearlist", yearlist);
        this.getFormHM().put("durationlist", durationlist);
        this.getFormHM().put("year", cur_year);
        this.getFormHM().put("duration", cur_duration);
        this.getFormHM().put("registerdate", registerdate);
        this.getFormHM().put("condition", SafeCode.encode("3`" + sqllist.get(4).toString()));
        this.getFormHM().put("relatTableid", "3");
        this.getFormHM().put("returnURL", "/kq/register/history/dailybrowsedata.do?b_search=link");
    }

    /**
     * 考勤规则的一个hashmap集
     * 
     * @return
     * @throws GeneralException
     */
    private HashMap count_Leave() throws GeneralException {
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
                        // System.out.println(fielditemid+"---------"+fielditem.getItemid());
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
    /**
     * 判断某日期数据是否在Q03（员工日明细表）中
     * 
     * @return
     * @throws GeneralException
     */
    private boolean dataInQ03(String registerdate) throws GeneralException {
    	boolean bool = true;
    	RowSet rs = null;
    	if (registerdate == null) {
    		return false;
    	}
    	StringBuffer sql = new StringBuffer();
    	sql.append("select count(q03z0) num from Q03 ");
    	sql.append(" where q03z0=?");
    	ArrayList<String> list = new ArrayList<String>();
    	list.add(registerdate);
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	int num = 0;
        try {
            rs = dao.search(sql.toString(), list);
            while (rs.next()) {
            	num = rs.getInt("num");
            }
            if(num == 0){
            	bool = false; 
            }            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeDbObj(rs);
        }
        return bool;
    }
}