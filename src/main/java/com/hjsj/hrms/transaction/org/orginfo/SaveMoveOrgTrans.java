package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SaveMoveOrgTrans extends IBusiness {

    public void execute() throws GeneralException {
        String[] str_valueList = (String[]) this.getFormHM().get("right_fields");
        if (str_valueList == null || str_valueList.length==1)
            return;
        
        
        /*重新排序  guodd 2017-03-29  注：排序规则没想好，先走原来的方式 17-04-05
        
        //父节点id
        String code = (String) this.getFormHM().get("code");
        StringBuffer sql = new StringBuffer();
        ArrayList values = new ArrayList();
        int order=0;
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
        	    //更新organization 选中机构的顺序
        	    sql.append("update organization set a0000=? where codeitemid=?");
        	    for(;order<str_valueList.length;order++){
             	   ArrayList itemValue = new ArrayList();
             	   itemValue.add(order+1);
             	   itemValue.add(str_valueList[order]);
             	   values.add(itemValue);
             }
			dao.batchUpdate(sql.toString(), values);
			//更新vorganization 选中机构的顺序
			String vSql = sql.toString().replace("organization", "vorganization");
			dao.batchUpdate(vSql, values);
			
			/更新organization 未选中机构(撤销的机构)的顺序
			sql.setLength(0);
			values.clear();
			sql.append("update organization set a0000=? where parentid=? and codeitemid<>parentid and codeitemid not in(");
			values.add(order+1);
			values.add(code);
			for(int i=0;i<str_valueList.length;i++){
				sql.append("?,");
				values.add(str_valueList[i]);
	        }
			sql.append("?) ");
			values.add(" ");
			dao.update(sql.toString(), values);
			//更新vorganization 未选中机构(撤销的机构)的顺序
			vSql = sql.toString().replace("organization", "vorganization");
			dao.update(vSql.toString(), values);
			
		} catch (SQLException e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
        this.getFormHM().put("isrefresh", "move");
        /*排序完毕  */
        
        
        
        String code = (String) this.getFormHM().get("code");
        updateA0000_move(str_valueList, code);
        this.getFormHM().put("isrefresh", "move");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        //调整顺序后重新排序levelA0000值  wangb 20170807
        for(int i = 0 ; i < str_valueList.length ; i++){
        	try {
				dao.update("UPDATE ORGANIZATION SET levelA0000="+ (i+1) +" where codeitemid='"+ str_valueList[i] +"'");
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
        }
    }

    /**
     * 保存组织机构调整的顺序
     * 
     * @param str_valueList
     * @param descOrdId
     * @throws GeneralException
     */
    private void updateA0000_move(String[] str_valueList, String descOrdId) throws GeneralException {
        //创建临时表并插入数据
        String tempTable = InsertOrgA0000(descOrdId);
        //更新临时表中组织机构的顺序
        updateOrgId(tempTable, descOrdId, str_valueList);
        //更新组织机构表与虚拟机构表的组织机构的顺序
        updateOrgA0000(tempTable, descOrdId);

    }

    /**
     * 创建临时表并在临时表中插入需要排序的数据
     * 
     * @param orgId
     *            页面树中选中的节点
     * @return 临时表名
     * @throws GeneralException
     */
    private String InsertOrgA0000(String orgId) throws GeneralException {
        String strSelect;
        String tempTable; // 临时表
        StringBuffer sql = new StringBuffer();
        tempTable = "org_order_temp";
        sql.delete(0, sql.length());
        sql.append("drop table ");
        sql.append(tempTable);
        DbWizard dbwizard = new DbWizard(this.frameconn);
        try {
            if(dbwizard.isExistTable(tempTable, false))
                ExecuteSQL.createTable(sql.toString(), this.getFrameconn());

            sql.delete(0, sql.length());
            // 创建排序临时表
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {
                sql.append("CREATE TABLE ");
                sql.append(tempTable);
                sql.append(" (orgId varchar(50), seqId Int IDENTITY(1,1), OrgA0000 Int,OrgType varchar(50))");
                break;
            }
            case Constant.DB2: {
                sql.append("CREATE TABLE ");
                sql.append(tempTable);
                sql.append(" (OrgId varchar(50),seqId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),OrgA0000 INTEGER,OrgType varchar(50))");
                break;
            }
            case Constant.ORACEL: {
                sql.append("CREATE TABLE ");
                sql.append(tempTable);
                sql.append(" (orgId varchar2(50), seqId int, OrgA0000 int,OrgType varchar2(50))");
                break;
            }
            }
            ExecuteSQL.createTable(sql.toString(), this.getFrameconn());

            strSelect = "select CodeItemId,'org' OrgType,a0000 from Organization " + " where CodeItemId Like '" + orgId + "%'";
            String where = "";
            if (orgId != null && orgId.length()>0) // 不包括根节点
                where = " and CodeItemId <> '" + orgId + "'";
            strSelect += where + " union select codeitemid,'vorg' OrgType,a0000 from vorganization where codeitemid like '" + orgId + "%' " + where;
            strSelect = strSelect + " Order by A0000 ";

            sql.delete(0, sql.length());
            // 往临时表中出入需要排序的节点数据
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {
                sql.append("Insert into ");
                sql.append(tempTable);
                sql.append("(orgId,OrgType,orgA0000) ");
                sql.append(strSelect);
                break;
            }
            case Constant.DB2: {
                sql.append("Insert into ");
                sql.append(tempTable);
                sql.append("(orgId,OrgType,orgA0000) ");
                sql.append(strSelect);
                break;
            }
            case Constant.ORACEL: {
                sql.append("Insert into ");
                sql.append(tempTable);
                sql.append(" (orgId,orgA0000,OrgType) ");
                sql.append(" select a.CodeItemId,a.a0000,a.OrgType from (");
                sql.append(strSelect);
                sql.append(") a"); // 别名
                break;
            }
            }
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return tempTable;
    }

    /**
     * 将临时表中新顺序组织机构更新到组织机构表和虚拟机构表中
     * 
     * @param tempTable
     *            临时表名
     * @param orgId
     *            页面树中选中的节点
     * @throws GeneralException
     */
    private void updateOrgA0000(String tempTable, String orgId) throws GeneralException {
        try {
            // 更新 A0000
            String strOn = "organization.CodeItemId = " + tempTable + ".orgId and " + tempTable + ".orgtype='org'";
            String strOnV = "vorganization.CodeItemId = " + tempTable + ".orgId and " + tempTable + ".orgtype='vorg'";

            String strWhere = "organization.CodeItemId like '" + orgId + "%'";
            if (orgId != null && orgId.length()>0) { // 不包括根节点
                strWhere = strWhere + " and organization.CodeItemId <> '" + orgId + "'";
            }

            String strWhereV = "vorganization.CodeItemId like '" + orgId + "%'";
            if (orgId != null && orgId.length()>0) { // 不包括根节点
                strWhereV = strWhereV + " and vorganization.CodeItemId <> '" + orgId + "'";
            }
            StringBuffer sql = new StringBuffer();

            ArrayList sqlList = new ArrayList();
            // 设置 SeqId
            /*
             * 例： SQLSERVER: Update destTable Set destTable.F1 = srcTable.FA
             * From DestTable Left Join srcTable On DestTable.FB = srcTable.FB
             * WHERE srcWhere ACCESS: Update destTable Left Join srcTable On
             * DestTable.FB = srcTable.FB Set destTable.F1 = srcTable.FA WHERE
             * srcWhere WHERE destWhere
             */
            /*
             * 例: ORACLE, DB2: Update destTable Set (destTable.F1, destTable.F2)
             * = (SELECT srcTable.F1, srcTable.F2 FROM srcTable WHERE strOn and
             * srcWhere ) WHERE destWhere
             */
            // 将临时表中排好序的组织机构同步到组织机构表和虚拟机构表
            switch (Sql_switcher.searchDbServer()) {
            case Constant.MSSQL: {
                sql.append("Update organization Set ");
                sql.append("organization.A0000 = " + tempTable + ".orgA0000");
                sql.append(" from organization left join ");
                sql.append(tempTable);
                sql.append(" on organization.CodeItemId = " + tempTable + ".orgId");
                sql.append(" where ");
                sql.append(strWhere);

                sqlList.add(sql.toString());

                // update虚拟机构表
                sql.delete(0, sql.length());
                sql.append("update vorganization set vorganization.a0000 =" + tempTable + ".orgA0000");
                sql.append(" from vorganization left join ");
                sql.append(tempTable);
                sql.append(" on vorganization.CodeItemId = " + tempTable + ".orgId where ");
                sql.append(strWhereV);
                sqlList.add(sql.toString());
                break;
            }
            case Constant.DB2: {
                sql.append("Update organization set ");
                sql.append("(organization.A0000)=(SELECT ");
                sql.append(tempTable);
                sql.append(".orgA0000 from ");
                sql.append(tempTable);
                sql.append(" where ");
                sql.append(strOn);
                sql.append(" and ");
                sql.append(strWhere);
                sql.append(")");
                sql.append(" where ");
                sql.append(strWhere);
                sqlList.add(sql.toString());

                sql.delete(0, sql.length());
                sql.append("Update vorganization set ");
                sql.append("(vorganization.A0000)=(SELECT ");
                sql.append(tempTable);
                sql.append(".orgA0000 from ");
                sql.append(tempTable);
                sql.append(" where ");
                sql.append(strOnV);
                sql.append(" and ");
                sql.append(strWhereV);
                sql.append(")");
                sql.append(" where ");
                sql.append(strWhereV);
                sqlList.add(sql.toString());
                break;
            }
            case Constant.ORACEL: {
                sql.append("Update organization set ");
                sql.append("(organization.A0000)=(SELECT ");
                sql.append(tempTable);
                sql.append(".orgA0000 from ");
                sql.append(tempTable);
                sql.append(" where ");
                sql.append(strOn);
                sql.append(" and ");
                sql.append(strWhere);
                sql.append(")");
                sql.append(" where ");
                sql.append(strWhere);
                sqlList.add(sql.toString());

                sql.delete(0, sql.length());
                sql.append("Update vorganization set ");
                sql.append("(vorganization.A0000)=(SELECT ");
                sql.append(tempTable);
                sql.append(".orgA0000 from ");
                sql.append(tempTable);
                sql.append(" where ");
                sql.append(strOnV);
                sql.append(" and ");
                sql.append(strWhereV);
                sql.append(")");
                sql.append(" where ");
                sql.append(strWhereV);
                sqlList.add(sql.toString());
                break;
            }
            }

            ContentDAO dao = new ContentDAO(this.getFrameconn());
            dao.batchUpdate(sqlList);

            // 删除临时表
            sql.delete(0, sql.length());
            sql.append("drop table ");
            sql.append(tempTable);
            ExecuteSQL.createTable(sql.toString(), this.getFrameconn());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 更新临时表中组织机构与虚拟机构的顺序
     * 
     * @param tablename
     *            临时表名
     * @param code
     *            页面选中节点
     * @param orgvalue
     *            新顺序排列的组织结构（包含虚拟机构）
     */
    private void updateOrgId(String tablename, String code, String[] orgvalue) {
        try {
            if (orgvalue == null || orgvalue.length < 1)
                return;

            if (tablename == null || tablename.length() < 1)
                return;

            ArrayList OrgA0000List = getOrgList(code);
            ArrayList sqllist = new ArrayList();
            HashMap orgmap = new HashMap();
            // 将a0000的值从小到大一次赋予新排序的组织机构
            for (int i = 0; i < OrgA0000List.size(); i++) {
                String org = (String) OrgA0000List.get(i);
                String[] orgs = org.split("=");
                String a0000 = orgs[1];
                String codeid = orgs[0];
                orgmap.put(codeid, a0000);
                String codeitemid = orgvalue[i];
                String sql = "update " + tablename + " set orgA0000='" + a0000 + "' where orgId='" + codeitemid + "'";
                sqllist.add(sql);
            }

            ContentDAO dao = new ContentDAO(this.frameconn);
            dao.batchUpdate(sqllist);
            // 需要排序的组织机构调整后的顺序号
            int newa0000 = 0;
            // 调整组织机构表中的节点及其子节点的顺序
            for (int i = 0; i < orgvalue.length; i++) {
                String codeitemid = orgvalue[i];
                String org = (String) OrgA0000List.get(i);
                String[] orgs = org.split("=");
                String codeid = orgs[0];
                // 某个顺序号下新组织机构的节点数量（包含节点本身与其子节点）
                int newcount = getOrgChildCount(codeitemid);
                // 某个顺序号下原组织机构的节点数量（包含节点本身与其子节点）
                int oldcount = getOrgChildCount(codeid);

                this.frowset = dao.search("select orgA0000 from " + tablename + " where orgId='" + codeitemid + "'");
                if(this.frowset.next())
                    newa0000 = this.frowset.getInt("orgA0000");

                String value = "";
                if (newcount > oldcount)
                    value = " orgA0000 = orgA0000+" + (newcount - oldcount);
                else if (oldcount > newcount)
                    value = " orgA0000 = orgA0000-" + (oldcount - newcount);

                //从此节点的下一个兄弟节点与其之后的节点统一后移或前移（下一个兄弟节点的顺序号为：newa0000 + oldcount）
                if (value != null && value.length() > 0)
                    dao.update("update " + tablename + " set" + value + " where orgA0000>=" + (newa0000 + oldcount));

                int olda0000 = Integer.parseInt((String) orgmap.get(codeitemid));
                String dvalue = "a0000";
                if (newa0000 > olda0000)
                    dvalue = "A0000+" + (newa0000 - olda0000);
                else if (newa0000 < olda0000)
                    dvalue = "A0000-" + (olda0000 - newa0000);
                // 更新节点与节点下子节点的顺序
                updateTableA0000(tablename, dvalue, codeitemid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取组织机构中页面选中节点下的所有子节点
     * 
     * @param code
     *            页面选中的节点编号
     * @return OrgValuelist 页面选中的节点所有子节点
     */
    private ArrayList getOrgList(String code) {
        ContentDAO dao = new ContentDAO(this.frameconn);
        ArrayList OrgValuelist = new ArrayList();

        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select codeitemid,a0000 from organization where parentid=");
            if (code != null && code.length() > 0) {
                sql.append("'");
                sql.append(code);
                sql.append("' and parentid<>codeitemid");
            } else {
                if (userView.isSuper_admin()) {
                    sql.append("codeitemid");
                } else {
                    String busi = getBusi_org_dept(this.userView);
                    if (busi.length() > 2) {
                        if (busi.indexOf("`") != -1) {
                            String[] tmps = busi.split("`");
                            String a_code = tmps[0];
                            if (a_code.length() > 2) {
                                code = a_code.substring(2);
                            }
                        }
                    }
                    sql.append("'");
                    sql.append(code);
                    sql.append("' and parentid<>codeitemid");
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sql.append(" and " + Sql_switcher.dateValue(sdf.format(new Date())) + " between start_date and end_date");
            sql.append(" union select codeitemid,a0000 from vorganization where parentid=");
            if (code != null && code.length() > 0) {
                sql.append("'");
                sql.append(code);
                sql.append("' and parentid<>codeitemid");
            } else {
                if (userView.isSuper_admin()) {
                    sql.append("codeitemid");
                } else {
                    String busi = getBusi_org_dept(this.userView);
                    if (busi.length() > 2) {
                        if (busi.indexOf("`") != -1) {
                            String[] tmps = busi.split("`");
                            String a_code = tmps[0];
                            if (a_code.length() > 2) {
                                code = a_code.substring(2);
                            }
                        }
                    }
                    sql.append("'");
                    sql.append(code);
                    sql.append("' and parentid<>codeitemid");
                }
            }
            sql.append(" and " + Sql_switcher.dateValue(sdf.format(new Date())) + " between start_date and end_date");
            sql.append(" order by a0000");
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                String codeitemid = this.frowset.getString("codeitemid");
                String a0000 = this.frowset.getString("a0000");
                OrgValuelist.add(codeitemid + "=" + a0000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OrgValuelist;
    }
    /**
     * 更新临时表中某节点下子节点的顺序
     * @param tablename 临时表名
     * @param dvalue 子节点调整顺序的方式（前移：顺序号减小，后移：顺序号增大）
     * @param codeitemid 某节点的id
     */
    private void updateTableA0000(String tablename, String dvalue, String codeitemid) {
        ArrayList sqlList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        try {
            switch (Sql_switcher.searchDbServer()) {
                case Constant.MSSQL: {
                    sql.append("Update " + tablename + " Set ");
                    sql.append(tablename + ".orgA0000=organization." + dvalue);
                    sql.append(" from " + tablename);
                    sql.append(" left join ");
                    sql.append(" organization on organization.CodeItemId = " + tablename + ".orgId");
                    sql.append(" where organization.CodeItemId like '" + codeitemid + "%'");
                    sqlList.add(sql.toString());
    
                    sql.delete(0, sql.length());
                    sql.append("Update " + tablename + " Set ");
                    sql.append(tablename + ".orgA0000=vorganization." + dvalue);
                    sql.append(" from " + tablename);
                    sql.append(" left join ");
                    sql.append(" vorganization on vorganization.CodeItemId = " + tablename + ".orgId");
                    sql.append(" where vorganization.CodeItemId like '" + codeitemid + "%'");
    
                    sqlList.add(sql.toString());
                    break;
                }
                case Constant.DB2: {
                    sql.append("Update " + tablename + " set ");
                    sql.append("(" + tablename + ".orgA0000)=(SELECT");
                    sql.append(" a." + dvalue);
                    sql.append(" from (select codeitemid,a0000,'org' orgtype from  organization where codeitemid like '" + codeitemid + "%'");
                    sql.append(" union all select codeitemid,a0000,'vorg' orgtype from  vorganization where codeitemid like '" + codeitemid + "%') a");
                    sql.append(" where a.CodeItemId = org_order_temp.orgId");
                    sql.append(" and a.orgtype=org_order_temp.orgtype)");
                    sql.append(" where " + tablename + ".orgId like '" + codeitemid + "%'");
                    sqlList.add(sql.toString());
                    break;
                }
                case Constant.ORACEL: {
                    sql.append("Update " + tablename + " set ");
                    sql.append("(" + tablename + ".orgA0000)=(SELECT");
                    sql.append(" a." + dvalue);
                    sql.append(" from (select codeitemid,a0000,'org' orgtype from  organization where codeitemid like '" + codeitemid + "%'");
                    sql.append(" union all select codeitemid,a0000,'vorg' orgtype from  vorganization where codeitemid like '" + codeitemid + "%') a");
                    sql.append(" where a.CodeItemId = org_order_temp.orgId");
                    sql.append(" and a.orgtype=org_order_temp.orgtype)");
                    sql.append(" where " + tablename + ".orgId like '" + codeitemid + "%'");
                    sqlList.add(sql.toString());
                    break;
                }
            }
            
            ContentDAO dao = new ContentDAO(this.frameconn);
            dao.batchUpdate(sqlList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取当前用户的权限
     * 
     * @param userView
     *            当前用户
     * @return 用户权限 如 1,UNxxx`UM9191`|2,UNxxx`UM9191`
     */
    private String getBusi_org_dept(UserView userView) {
        String busi = "";
        String busi_org_dept = "";
        try {
            busi_org_dept = userView.getUnitIdByBusi("4");
            if (busi_org_dept.length() > 0) {
                busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
            } else {
                busi = userView.getManagePrivCode() + userView.getManagePrivCodeValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return busi;
    }

    /**
     * 获取某节点下的子节点数（包含节点本身）
     * 
     * @param parentId
     *            节点id
     * @return int 子节点数目
     * @throws GeneralException
     */
    private int getOrgChildCount(String parentId) throws GeneralException {
        int n = 0;
        try {
            // 加上虚拟机构 by guodd
            String s = "SELECT count(*) as count FROM (select A0000 from organization where codeitemid like '" + parentId + "%' union select A0000 from vorganization where codeitemid like '"
                    + parentId + "%') u ";
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(s);
            if (this.frowset.next()) {
                n = this.frowset.getInt("count");
            }
        } catch (Exception sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        }
        return n;
    }

}
