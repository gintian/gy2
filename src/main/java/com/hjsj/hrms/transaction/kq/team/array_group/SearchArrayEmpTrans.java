package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.GroupsArray;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SearchArrayEmpTrans extends IBusiness implements KqClassArrayConstant {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String group_id = (String) hm.get("group_id");
            //考勤自己的取权限的方法
            String privCode = RegisterInitInfoData.getKqPrivCode(userView);
            String privCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);

            ArrayList dlist = getDbase();
            this.getFormHM().put("dlist", dlist);
            String a0101_s = (String) this.getFormHM().get("a0101_s");
            String dbper = (String) this.getFormHM().get("dbper");
            /*ArrayList fieldlist = DataDictionary.getFieldList(this.kq_group_emp_table,
            		Constant.USED_FIELD_SET);*/
            GroupsArray groupsArray = new GroupsArray(this.frameconn, this.userView);
            ArrayList fieldlist = groupsArray.groupEmpFieldlist();
            //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
            KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
            HashMap hashmap = para.getKqParamterMap();
            String g_no = ((String) hashmap.get("g_no")).toLowerCase();
            String cardno = ((String) hashmap.get("cardno")).toLowerCase();
            //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
            //		String columns=groupsArray.groupEmpColumns();
            String columns = "a0100,nbase,group_id,a0101," + g_no + "," + cardno + ",b0110,e0122";
            String dbcolumns = "Q.a0100,Q.nbase,group_id,A.A0101," + g_no + "," + cardno + ",b0110,e0122";
            String sqlstr = "select " + columns;
            StringBuffer whereSTR = new StringBuffer();
            whereSTR.append("from ");
            ArrayList dblist = new ArrayList();

            if (dbper != null && dbper.length() > 0 && !"all".equals(dbper)) {
                CommonData vo = new CommonData();
                vo.setDataName("");
                vo.setDataValue(dbper);
                dblist.add(vo);
            } else if ("all".equals(dbper)) {
                dblist = getDbase();
                dblist.remove(0);
            } else {
                if (dlist != null && dlist.size() > 0) {
                    dbper = ((CommonData) dlist.get(0)).getDataValue();
                    if ("all".equals(dbper)) {
                        dblist = getDbase();
                        dblist.remove(0);
                    } else
                    dblist.add(dlist.get(0));
                }
            }
            this.getFormHM().put("dbper", dbper);
            StringBuffer joinTable = new StringBuffer();

            if (dblist.isEmpty())
                return;

            for (Iterator it = dblist.iterator(); it.hasNext();) {
                CommonData vo = (CommonData) it.next();
                dbper = vo.getDataValue();
                String whereA0100In = RegisterInitInfoData.getWhereINSql(this.userView, dbper);
                if (joinTable.length() > 0) {
                    joinTable.append(" UNION SELECT A0100,A0101,'" + dbper + "' nbase," + g_no + "," + cardno + whereA0100In);
                } else {
                    joinTable.append("SELECT A0100,A0101,'" + dbper + "' nbase," + g_no + "," + cardno + whereA0100In);
                }
            }
            whereSTR.append("(SELECT " + dbcolumns + " FROM " + kq_group_emp_table + " Q INNER JOIN (" + joinTable
                    + ") A ON Q.A0100=A.A0100 AND Q.nbase=A.nbase) B");
            whereSTR.append(" where " + kq_shift_group_Id + "='" + group_id + "'");

            //非su用户，得到所属权限的分组人员
            if (!this.userView.isSuper_admin()) {
                if (!"".equals(privCodeValue)) {
                    if (privCode != null && "UN".equals(privCode))
                        whereSTR.append(" and b0110 like '" + privCodeValue + "%'");
                    else if (privCode != null && "UM".equals(privCode))
                        whereSTR.append(" and e0122 like '" + privCodeValue + "%'");
                    else if (privCode != null && "@K".equals(privCode))
                        whereSTR.append(" and e01a1 like '" + privCodeValue + "%'");
                }
            }

            if (a0101_s != null && a0101_s.length() > 0)
                a0101_s = PubFunc.getStr(a0101_s);

            String select_type = (String) this.getFormHM().get("select_type");
            if (a0101_s != null && a0101_s.length() > 0) {
                if ("0".equals(select_type)) {
                    whereSTR.append(" and a0101 like '%" + a0101_s + "%'");
                } else if ("1".equals(select_type)) {
                    whereSTR.append(" and " + g_no + " like '%" + a0101_s + "%'");
                } else {
                    whereSTR.append(" and " + cardno + " like '%" + a0101_s + "%'");
                }
            }

            this.getFormHM().put("sqlstr", sqlstr);
            this.getFormHM().put("where", whereSTR.toString());
            this.getFormHM().put("column", columns);
            this.getFormHM().put("fieldlist", fieldlist);

            String org_code = groupsArray.getA_codeFromCodeItemId(privCodeValue, this.getFrameconn());
            if (org_code == null || org_code.length() <= 0)
                org_code = "UN";

            this.getFormHM().put("org_code", org_code);
            KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, "", this.getFrameconn());
            String kq_type = kq_paramter.getKq_type();
            if (a0101_s != null && a0101_s.length() > 0)
                this.getFormHM().put("kq_type", kq_type);

            this.getFormHM().put("a0101_s", a0101_s);
            //自动分配班组 1=展现 0=不展现
            String fil = getbzindex();
            this.getFormHM().put("fil", fil);
            //增加班组名称
            String groupName = getGroupName(group_id);
            this.getFormHM().put("groupName", groupName);
            /* -----------显示部门层数-------------------------------------------------- */
            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn()); //
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122); //
            if (uplevel == null || uplevel.length() == 0) //
                uplevel = "0"; //
            this.getFormHM().put("uplevel", uplevel); //
            /* ------------显示部门层数------------------------------------------------- */
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList getDbase() throws GeneralException {

        StringBuffer stb = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList dbaselist = RegisterInitInfoData.getDase3(this.formHM, this.userView, this.getFrameconn());
        ArrayList slist = new ArrayList();
        CommonData vo1 = new CommonData("all", "全部人员库");
        slist.add(vo1);
        try {
            stb.append("select * from dbname");
            stb.append(" order by dbid");
            this.frowset = dao.search(stb.toString());
            while (this.frowset.next()) {
                String dbpre = this.frowset.getString("pre");
                for (int i = 0; i < dbaselist.size(); i++) {
                    String userbase = dbaselist.get(i).toString();
                    if (dbpre != null && dbpre.equalsIgnoreCase(userbase)) {
                        CommonData vo = new CommonData(this.frowset.getString("pre"), this.frowset.getString("dbname"));
                        slist.add(vo);
                    }
                }
            }
            if(slist.size() == 2)
            	slist.remove(0);
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
        return slist;

    }

    private String getbzindex() {
        String fil = "0";
        String tt = KqParam.getInstance().getShiftGroupItem();

        if (tt != null && !"".equals(tt) && tt.length() > 0) {
            fil = "1";
        }

        return fil;
    }

    /**
     *  取得班组名称
     * @param groupid 班组ID
     * @return
     */
    private String getGroupName(String groupid) {
        String groupName = "";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String sql = "select name from kq_shift_group where group_id='" + groupid + "'";
        try {
            rowSet = dao.search(sql);
            if (rowSet.next()) {
                groupName = rowSet.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rowSet != null)
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return groupName;

    }
}
