package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 给没有排班的人员排班
 *<p> Title: </p>
 *<p> Description: </p>
 *<p> Company:HJHJ </p>
 *<p> Create time:Feb 1, 2008 </p>
 * 
 * @author sunxin
 * @version 4.0
 */
public class SaveNoArrayKqShiftTrans extends IBusiness{

    public void execute() throws GeneralException {
        try {
            ArrayList selectedinfolist = (ArrayList) this.getFormHM().get("selectedinfolist");
            
            AnnualApply annualApply = new AnnualApply(userView, frameconn);
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String selected_class = (String) hm.get("selected_class");
            if (selected_class == null || selected_class.length() <= 0)
                return;
            
            if ("-1".equals(selected_class)) {
                selected_class = null;
            }
            
            String start_date = (String) hm.get("start_date");
            
            String end_date = (String) hm.get("end_date");            
            String rest_postpone = (String) hm.get("rest_postpone");
            String feast_postpone = (String) hm.get("feast_postpone");
            String a_code = (String) hm.get("a_code");
            
            start_date = start_date.replaceAll("-", "\\.");
            end_date = end_date.replaceAll("-", "\\.");
            boolean iscorrect = annualApply.getGroupDailyDataState(selectedinfolist, start_date, end_date);
            if (!iscorrect) 
            	throw new GeneralException("请求的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！");
            
            String code = "";
            String kind = "";
            if (a_code != null && a_code.length() > 0) {
                String codesetid = a_code.substring(0, 2);
                String codeitemid = a_code.substring(2);
                code = codeitemid;
                if ("UN".equalsIgnoreCase(codesetid)) {
                    kind = "2";
                } else if ("UM".equalsIgnoreCase(codesetid)) {
                    kind = "1";
                } else if ("@K".equalsIgnoreCase(codesetid)) {
                    kind = "0";
                } else {
                    ManagePrivCode managePrivCode = new ManagePrivCode(this.userView, this.getFrameconn());
                    code = managePrivCode.getPrivOrgId();
                    kind = "2";
                }
            } else {
                ManagePrivCode managePrivCode = new ManagePrivCode(this.userView, this.getFrameconn());
                code = managePrivCode.getPrivOrgId();
                kind = "2";
            }
            String b0110 = RegisterInitInfoData.getDbB0100(code, kind, this.getFormHM(), this.userView, this.getFrameconn());
            shift_employee(selectedinfolist, b0110, start_date, end_date, selected_class, rest_postpone, feast_postpone, "02");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public void shift_employee(ArrayList selectedinfolist, String b0110, String start_date, String end_date, String class_id, String rest_postpone, String feast_postpone, String kq_type) throws GeneralException {

        BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());

        ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.getFrameconn());
        String rest_date = restList.get(0).toString();
        String rest_b0110 = restList.get(1).toString();
        String t_table = baseClassShift.tempClassTable();
        String date_Table = baseClassShift.creat_KqTmp_Table(this.userView.getUserId());
        ArrayList date_list = baseClassShift.getDatelist(start_date, end_date);
        baseClassShift.initializtion_date_Table(date_list, rest_date, date_Table, rest_b0110, b0110);
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
        String empTable = kqUtilsClass.createContrastTemp();
        insrtTempData(selectedinfolist, empTable);
        insrtTempData(t_table, date_Table, empTable);// 插入临时表
        baseClassShift.insertClassToTemp(class_id, t_table, rest_postpone, feast_postpone);// 修改临时表
        insertClassToShift(t_table);
        baseClassShift.dropTable(t_table);
        baseClassShift.dropTable(date_Table);
        baseClassShift.dropTable(empTable);
    }

    /**
     * 选择了单位,部门,职位
     * 
     * @param t_table
     */
    public void insrtTempData(String t_table, String date_Table, String emp_Table) throws GeneralException {
        StringBuffer insertSql = new StringBuffer();
        switch (Sql_switcher.searchDbServer()) {
        case Constant.MSSQL: {
            insertSql.append("INSERT INTO " + t_table + "(nbase,A0100,B0110,E0122,E01A1,A0101,Q03Z0,class_id,flag) ");
            insertSql.append("SELECT  nbase,A0100, B0110, " + Sql_switcher.isnull("E0122", "''") + ", " + Sql_switcher.isnull("E01A1", "''") + ", A0101, DT.sDate AS q03z5,0,DT.dkind ");
            insertSql.append(" FROM " + emp_Table + " , " + date_Table + " as DT");
            break;
        }
        case Constant.ORACEL: {
            insertSql.append("INSERT INTO " + t_table + "(nbase, A0100, B0110, E0122, E01A1, A0101,Q03Z0,class_id,flag) ");
            insertSql.append("SELECT  nbase, A0100, B0110, " + Sql_switcher.isnull("E0122", "''") + ", " + Sql_switcher.isnull("E01A1", "''") + ", A0101,DT.sDate As Q03Z0,0,DT.dkind  ");
            insertSql.append(" FROM " + emp_Table + " , " + date_Table + " DT");
            break;
        }
        case Constant.DB2: {
            insertSql.append("INSERT INTO " + t_table + "(nbase, Q03Z0, A0100, B0110, E0122, E01A1, A0101,Q03Z0,class_id,flag) ");
            insertSql.append("SELECT nbase, A0100, B0110, " + Sql_switcher.isnull("E0122", "''") + ", " + Sql_switcher.isnull("E01A1", "''") + ", A0101, DT.sDate As Q03Z0,0,DT.dkind ");
            insertSql.append(" FROM " + emp_Table + " , " + date_Table + " DT");
            insertSql.append(" WHERE 1=1 ");
            break;
        }
        }
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            ArrayList list = new ArrayList();
            dao.insert(insertSql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 修改排班表
     * 
     * @param t_table
     * @throws GeneralException
     */
    public void insertClassToShift(String t_table) throws GeneralException {
        String destTab = KqClassArrayConstant.kq_employ_shift_table;// 目标表
        String srcTab = t_table;// 源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase and " + destTab + ".q03z0=" + srcTab + ".q03z0";// 关联串
        // xxx.field_name=yyyy.field_namex,....
        String strSet = destTab + ".class_id=" + srcTab + ".class_id";// 更新串
        // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "";// destTab+".status='0'";//更新目标的表过滤条件
        String strSWhere = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase and " + destTab + ".q03z0=" + srcTab + ".q03z0";// 源表的过滤条件
        // ;//源表的过滤条件
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            dao.update(update);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        StringBuffer insertSQL = new StringBuffer();
        insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
        insertSQL.append(" SELECT a.nbase,a.A0100,a.A0101,a.B0110,a.E0122,a.E01A1,a.Q03Z0,a.class_id,0");
        insertSQL.append(" FROM " + t_table + " a ");
        insertSQL.append("WHERE NOT EXISTS(SELECT * FROM kq_employ_shift b");
        insertSQL.append(" WHERE a.A0100=b.A0100 and a.nbase=b.nbase and a.Q03Z0=b.Q03Z0 )");
        try {
            ArrayList list = new ArrayList();
            // System.out.println(insertSQL.toString());
            dao.insert(insertSQL.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 人员临时表
     * 
     * @param selectedinfolist
     * @param empTable
     * @return
     */
    private boolean insrtTempData(ArrayList selectedinfolist, String empTable) {

        StringBuffer insertSql = new StringBuffer();
        insertSql.append("INSERT INTO " + empTable + "(nbase,A0100,B0110,E0122,E01A1,A0101) ");
        insertSql.append("values (?,?,?,?,?,?)");
        ArrayList list = new ArrayList();
        for (int i = 0; i < selectedinfolist.size(); i++) {
            LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
            ArrayList onelist = new ArrayList();
            onelist.add(rec.get("nbase"));
            onelist.add(rec.get("a0100"));
            onelist.add(rec.get("b0110"));
            onelist.add(rec.get("e0122"));
            onelist.add(rec.get("e01a1"));
            onelist.add(rec.get("a0101"));
            list.add(onelist);
        }
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        boolean isCorrect = true;
        try {
            dao.batchInsert(insertSql.toString(), list);
        } catch (Exception e) {
            isCorrect = false;
            e.printStackTrace();
        }
        return isCorrect;
    }

}
