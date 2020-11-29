package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.general.info.EmpMaintenanBo;
import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchMagManagerTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String code = (String) hm.get("code");
            String kind = (String) hm.get("kind");
            String sort = (String) hm.get("sort");
            hm.remove("code");
            hm.remove("kind");
            hm.remove("sort");
            String selectWhere = (String) this.getFormHM().get("selectWhere");
            ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
            if (code == null || code.length() <= 0) {
                code = managePrivCode.getPrivOrgId();
                String orgPri = RegisterInitInfoData.getKqPrivCode(userView);
                if ("UM".equalsIgnoreCase(orgPri))
                    kind = "1";
                else if ("UN".equalsIgnoreCase(orgPri))
                    kind = "2";
            } else if (kind == null || kind.length() <= 0) {
                code = managePrivCode.getPrivOrgId();
                String orgPri = RegisterInitInfoData.getKqPrivCode(userView);
                if ("UM".equalsIgnoreCase(orgPri))
                    kind = "1";
                else if ("UN".equalsIgnoreCase(orgPri))
                    kind = "2";
            } else {
                if (AdminCode.getCode("UM", code) != null)
                    kind = "1";
                else if (AdminCode.getCode("UN", code) != null)
                    kind = "2";
            }

            String a_code = "";
            if ("2".equals(kind)) {
                a_code = "UN" + code;
            } else if ("1".equals(kind)) {
                a_code = "UM" + code;
            }
            this.getFormHM().put("a_code", a_code);
            String select_name = (String) this.getFormHM().get("select_name");
            String select_pre = (String) this.getFormHM().get("select_pre");
            //		this.getFormHM().put("select_name","");
            String viewsearch = (String) hm.get("viewsearch");
            viewsearch = viewsearch != null && viewsearch.trim().length() > 0 ? viewsearch : "0";
            hm.remove("viewsearch");
            String kq_cardno = (String) this.getFormHM().get("kq_cardno");
            selectWhere = (String) this.getFormHM().get("selectWhere");
            if (kq_cardno == null || kq_cardno.length() <= 0) {
                KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, "", this.getFrameconn());
                kq_cardno = kq_paramter.getCardno();
                if (kq_cardno == null || kq_cardno.length() <= 0)
                    throw GeneralExceptionHandler.Handle(new GeneralException("", "没有定义考勤卡号！", "", ""));
            }
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
            String where_c = kqUtilsClass.getWhere_C("1", "a0101", select_name);
            ArrayList dblist = RegisterInitInfoData.getDbList(code, kind, this.getFormHM(), this.userView, this.getFrameconn());
            ArrayList sql_db_list = new ArrayList();
            if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
                sql_db_list.add(select_pre);
            } else {
                //			select_pre=(String)dblist.get(0);
                //			sql_db_list.add(select_pre);
                sql_db_list = dblist;
            }
            this.getFormHM().put("select_pre", select_pre);
            this.getFormHM().put("nbaselist", dblist);
            ArrayList kq_list = kqUtilsClass.getKqNbaseList(dblist);
            //		if(kq_list!=null&&kq_list.size()>0)
            //			kq_list.remove(0);		
            this.getFormHM().put("kq_list", kq_list);
            String magcard_setid = KqParam.getInstance().getMagcardSetId();
            if (magcard_setid == null || magcard_setid.trim().length() <= 0) {
                magcard_setid = "A01";
            }
            ArrayList fieldlist = new ArrayList();
            if (!"A01".equalsIgnoreCase(magcard_setid))
                fieldlist = DataDictionary.getFieldList(magcard_setid.toLowerCase(), Constant.USED_FIELD_SET);
            ArrayList newfieldlist = new ArrayList();
            StringBuffer column = new StringBuffer();
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fieldlist.get(i);
                if ("1".equals(fielditem.getState() == null ? "" : fielditem.getState())) {
                    fielditem.setVisible(true);
                } else {
                    fielditem.setVisible(false);
                }
                column.append(fielditem.getItemid() + ",");
                newfieldlist.add(fielditem.clone());
            }
            if (column != null && column.length() > 0) {
                column.append("i9999");
            }
            String[] columns = column.toString().split(",");
            String ucolumn = "";
            for (int i = 0; i < columns.length; i++) {
                ucolumn += "null as " + columns[i] + ",";
            }
            ucolumn = ucolumn.substring(0, ucolumn.length() - 1);
            StringBuffer sql = new StringBuffer();
            String base = "";
            EmpMaintenanBo embo = new EmpMaintenanBo(this.getFrameconn());
            for (int i = 0; i < sql_db_list.size(); i++) {
                base = (String) sql_db_list.get(i);
                sql.append("select  " + i + " as i,'" + base + "' as nbase," + base + "A01.b0110 as orgid," + base + "A01.e0122 as depid," + base
                        + "A01.a0100 as a0100,");
                sql.append("" + base + "A01.a0101 as name");
                if (kq_cardno != null && kq_cardno.length() > 0)
                    sql.append("," + base + "A01." + kq_cardno + " as kqcard,A0000");
                if (!"A01".equalsIgnoreCase(magcard_setid)) {
                    sql.append(" ," + column.toString());
                    sql.append(" from  " + base + "A01," + base + magcard_setid);
                    sql.append(" where " + base + "A01.a0100=" + base + magcard_setid + ".a0100 " + where_c);
                } else {
                    sql.append(" from  " + base + "A01");
                    sql.append(" where 1=1 " + where_c);
                }

                if ("1".equals(kind)) {
                    sql.append(" and " + base + "A01.e0122 like '" + code + "%'");
                } else {
                    sql.append(" and " + base + "A01.b0110 like '" + code + "%'");
                }
                
                //zxj 20170527 需要走考勤管理范围权限
                sql.append(" and ").append(base).append("A01.A0100 in (select A0100 ").append(RegisterInitInfoData.getWhereINSql(userView, base)).append(")");
                
                if (viewsearch != null && "1".equals(viewsearch)) {
                    if (embo.checkResult(this.userView.getUserName(), base)) {
                        sql.append(" and " + base + "A01.A0100 in (select A0100 from ");
                        sql.append(this.userView.getUserName() + base + "result)");
                    } else {
                        sql.append(" and 1=2");
                    }
                }
                if (!"".equalsIgnoreCase(selectWhere.trim()))
                    sql.append(" and " + selectWhere);
                if (!"A01".equalsIgnoreCase(magcard_setid)) {
                    sql.append(" union ");
                    sql.append("select  " + i + " as i,'" + base + "' as nbase," + base + "A01.b0110 as orgid," + base + "A01.e0122 as depid,"
                            + base + "A01.a0100 as a0100,");
                    sql.append("" + base + "A01.a0101 as name");
                    if (kq_cardno != null && kq_cardno.length() > 0)
                        sql.append("," + base + "A01." + kq_cardno + " as kqcard,A0000");//2
                    sql.append(" ," + ucolumn);
                    sql.append(" from  " + base + "A01 ");
                    sql.append(" where " + base + "A01.a0100 not in (select distinct a0100  from " + base + magcard_setid + ") "
                            + where_c);

                    if ("1".equals(kind)) {
                        sql.append(" and " + base + "A01.e0122 like '" + code + "%'");
                    } else {
                        sql.append(" and " + base + "A01.b0110 like '" + code + "%'");
                    }
                    if (viewsearch != null && "1".equals(viewsearch)) {
                        if (embo.checkResult(this.userView.getUserName(), base)) {
                            sql.append(" and " + base + "A01.A0100 in (select A0100 from ");
                            sql.append(this.userView.getUserName() + base + "result)");
                        } else {
                            sql.append(" and 1=2");
                        }
                    }
                }
                sql.append(" union ");
            }
            sql.setLength(sql.length() - 7);
            this.getFormHM().put("strsql", sql.toString());
            String orderby = kqUtilsClass.getSortOrderBY(sort);
            if (orderby == null || orderby.length() <= 0) {
                if (!"A01".equalsIgnoreCase(magcard_setid))
                    orderby = "order by orgid,depid,i9999 desc";
                else
                    orderby = "order by i,a0000";
            }
            //this.getFormHM().put("fieldlist",fieldlist);	
            this.getFormHM().put("orderby", orderby);
            if (kq_cardno != null && kq_cardno.length() > 0)
                this.getFormHM().put("columns", "orgid,depid,name,a0100,kqcard,nbase," + column.toString());
            else
                this.getFormHM().put("columns", "orgid,depid,name,a0100,nbase," + column.toString());
            FieldItem item = new FieldItem();
            item.setItemid("orgid");
            item.setItemdesc("单位名称");
            item.setItemtype("A");
            item.setCodesetid("UN");
            item.setVisible(true);
            fieldlist.add(0, item);
            item = new FieldItem();
            item.setItemid("depid");
            item.setItemdesc("部门名称");
            item.setItemtype("A");
            item.setCodesetid("UM");
            item.setVisible(true);
            fieldlist.add(1, item);
            item = new FieldItem();
            item = new FieldItem();
            item.setItemid("name");
            item.setItemdesc("姓名");
            item.setItemtype("A");
            item.setCodesetid("0");
            item.setVisible(true);
            fieldlist.add(2, item);
            if (!"A01".equalsIgnoreCase(magcard_setid)) {
                item = new FieldItem();
                item.setItemid("i9999");
                item.setItemdesc("序号");
                item.setItemtype("D");
                item.setCodesetid("0");
                item.setVisible(true);
                fieldlist.add(3, item);
            }
            this.getFormHM().put("fieldlist", fieldlist);
            this.getFormHM().put("magcard_setid", magcard_setid);
            this.getFormHM().put("kq_cardno", kq_cardno);
            KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn());
            ArrayList machinelist = kqCardData.getMachineList();
            this.getFormHM().put("machinelist", machinelist);
            this.getFormHM().put("viewsearch", viewsearch);
            this.getFormHM().put("searchlist", searchTable("1"));
            String magcard_cardid = (String) this.getFormHM().get("magcard_cardid");
            if (magcard_cardid == null || magcard_cardid.length() <= 0) {
                magcard_cardid = KqParam.getInstance().getMagcardCardId();
            }
            this.getFormHM().put("magcard_cardid", magcard_cardid);
            String dbType = "1";
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {
                dbType = "1";
                break;
            }
            case Constant.ORACEL: {
                dbType = "2";
                break;
            }
            case Constant.DB2: {
                dbType = "3";
                break;
            }
            }
            this.getFormHM().put("dbType", dbType);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList searchTable(String type) {
        ArrayList searchlist = new ArrayList();
        ContentDAO dao = new ContentDAO(this.getFrameconn());

        String sqlstr = "select id,name from LExpr where Type=" + type;
        try {
            this.frowset = dao.search(sqlstr);
            int n = 1;
            while (this.frowset.next()) {
                if (!(this.userView.isHaveResource(IResourceConstant.LEXPR, this.frowset.getString("id"))))
                    continue;
                CommonData job = new CommonData();
                job.setDataName(this.frowset.getString("id"));
                job.setDataValue(this.frowset.getString("id") + "." + this.frowset.getString("name"));
                searchlist.add(job);
                n++;
                if (n > 10)
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchlist;
    }
}
