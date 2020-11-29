package com.hjsj.hrms.businessobject.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.transaction.kq.feast_manage.GetCheckExpreTrans;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class FeastComputer {

    private Connection conn;
    private UserView   userView;
    private String balanceDate;

    public FeastComputer(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }
    /**
     * 兼容旧程序保留此方法
     */
    @Deprecated
    public String getFeastComputer(String codeitemid, String field, String hols_status, Connection conn) {
    	return getFeastComputer(codeitemid, field, hols_status, conn, "") ;
    }
    /**
     * 查询计算公式内容及单位编码
     * @param codeitemid 单位编码
     * @param field 需要计算的指标
     * @param hols_status 假期类别
     * @param conn 数据库链接
     * @param year 年份
     * @return
     */
    public String getFeastComputer(String codeitemid, String field, String hols_status, Connection conn,
    		String year) {
        HashMap hashmap = new HashMap();
        try {
            ArrayList list = getParameter(codeitemid, field, hols_status, conn, year);
            hashmap = (HashMap) list.get(0);
            codeitemid = list.get(1).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return (String) hashmap.get("content");
    }

    public ArrayList getParameter(String codeitemid, String field, String hols_status, Connection conn,
    		String year) throws GeneralException {
        
        ArrayList list = new ArrayList();
        
        HashMap hashmap = new HashMap();
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs = null;
        String parentid = "";
        
        try {
            String orgSql = "SELECT parentid,codeitemid from organization where codeitemid='" + codeitemid + "'";
            rs = dao.search(orgSql);
            if (rs.next()) {
                parentid = rs.getString("parentid") != null ? rs.getString("parentid").toString() : "UN";
                codeitemid = rs.getString("codeitemid") != null ? rs.getString("codeitemid").toString() : "UM";
            }
            
            String b0110 = "";
            if (this.userView.isSuper_admin()) {
                b0110 = "UN";
            } else {
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, conn);
                String userOrgId = managePrivCode.getPrivOrgId();
                b0110 = "UN" + userOrgId;
            }
            //2014.8.19 xiexd更改假期管理业务用户不能看到假期定义公式
            
        	String name = "REST_" + field.toUpperCase() + "_" + hols_status;
        	String content = KqParam.getInstance().getContent(dao, name, b0110, "", year);
        	if (content != null && content.length() > 0) {
                hashmap.put("content", content);
                hashmap.put("b0110", b0110);
            }
           /* hashmap = getExp_c(b0110, field, hols_status, conn);
            if (hashmap.isEmpty() && parentid.equals(codeitemid)) {
                hashmap = getExp_c("UN", field, hols_status, conn);
                if (hashmap.isEmpty()) {
                    hashmap.put("b0110", "UN");
                    hashmap.put("content", "");
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        list.add(hashmap);
        list.add(parentid);
        
        return list;
    }

    public HashMap getExp_c(String b0110, String field, String hols_status, Connection conn) throws GeneralException {
        ContentDAO dao = new ContentDAO(conn);
        HashMap hashmap = new HashMap();
        String content = "";
        RowSet rs = null;
        String name = "REST_" + field.toUpperCase() + "_" + hols_status;
        StringBuffer str = new StringBuffer();
        try {
            str.append("select content  from kq_parameter where b0110='");
            str.append(b0110);
            str.append("'");
            str.append(" and UPPER(name)='" + name.toUpperCase() + "'");
            rs = dao.search(str.toString());
            if (rs.next()) {
                content = rs.getString("content");
                if (content != null && content.length() > 0) {
                    hashmap.put("content", content);
                    hashmap.put("b0110", b0110);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            throw GeneralExceptionHandler.Handle(sqle);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return hashmap;
    }

    public ArrayList getMusterSetTrans(String infor, UserView userView) {
        if (infor == null || "".equals(infor)) {
            infor = "1";
        }
        
        ArrayList list = new ArrayList();
        ArrayList fieldsetlist = null;
        if ("1".equals(infor)) {
            fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
        } else if (("2".equals(infor))) {
            fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
        } else {
            fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.POS_FIELD_SET);
        }
        
        for (int i = 0; i < fieldsetlist.size(); i++) {
            FieldSet fieldset = (FieldSet) fieldsetlist.get(i);
            if ("0".equals(userView.analyseTablePriv(fieldset.getFieldsetid()))) {
                continue;
            }
            
            if ("A00".equals(fieldset.getFieldsetid())) {
                continue;
            }
            
            if ("K00".equals(fieldset.getFieldsetid())) {
                continue;
            }
            
            if ("B00".equals(fieldset.getFieldsetid())) {
                continue;
            }
            
            CommonData dataobj = new CommonData(fieldset.getFieldsetid(), fieldset.getFieldsetid() + ":" + fieldset.getCustomdesc());
            list.add(dataobj);
        }
        
        return list;
    }

    public HashMap getFieldBySetNameTrans(String tablename, UserView userView) {
        ArrayList list = new ArrayList();
        String setname = tablename;
        ArrayList onefiledlist = new ArrayList();
        Field field = null;
        ArrayList fielditemlist = DataDictionary.getFieldList(setname, Constant.USED_FIELD_SET);
        if (fielditemlist != null) {
            for (int i = 0; i < fielditemlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                if ("M".equals(fielditem.getItemtype())) {
                    continue;
                }
                
                if ("0".equals(userView.analyseFieldPriv(fielditem.getItemid()))) {
                    continue;
                }
                
                CommonData dataobj = new CommonData();
                dataobj = new CommonData(fielditem.getItemid(), fielditem.getItemid().toUpperCase() + ":" + fielditem.getItemdesc());
                list.add(dataobj);
                
                field = new Field(fielditem.getItemdesc(), fielditem.getItemtype());
                field.setDatatype("string");
                onefiledlist.add(field);
            }
        }

        HashMap hash = new HashMap();
        hash.put("fieldlist", list);
        hash.put("onefiledlist", onefiledlist);
        
        return hash;
    }

    /**
     * 得到假期管理项目的描述
     * 
     * @param hols_type
     * @return
     */
    public ArrayList getHolsList(String hols_type) {
        String[] types = hols_type.split(",");
        StringBuffer typeIN = new StringBuffer();
        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            if (type == null || hols_type.length() <= 0) {
                continue;
            }
            typeIN.append("'" + type + "',");
        }
        typeIN.setLength(typeIN.length() - 1);
        ArrayList list = new ArrayList();
        CommonData vo = null;
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sql = new StringBuffer();
            sql.append("select codeitemid,codeitemdesc from codeitem");
            sql.append(" where codesetid='27' and parentid like '0%' and codeitemid<>parentid");
            sql.append(" and codeitemid in(" + typeIN.toString() + ")");
            // 33177 linbz 假期管理的假别排序问题
            sql.append(" order by a0000,codeitemid ");
            rs = dao.search(sql.toString());
            String codeitemid = "";
            while (rs.next()) {
                vo = new CommonData();
                codeitemid = rs.getString("codeitemid");
                if (codeitemid == null || codeitemid.length() <= 0) {
                    codeitemid = "";
                    continue;
                }
                vo.setDataName(rs.getString("codeitemdesc"));
                vo.setDataValue(codeitemid);
                list.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    /**
     * 取得假期管理项
     * 
     * @param codeitemid
     * @return
     */
    public ArrayList getHolsType(String codeitemid) {
        HashMap hashmap = new HashMap();
        String orgSql = "SELECT parentid,codeitemid from organization where codeitemid='" + codeitemid + "'";
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        String parentid = "";
        ArrayList list = new ArrayList();
        try {
            rs = dao.search(orgSql);
            if (rs.next()) {
                parentid = rs.getString("parentid") != null ? rs.getString("parentid").toString() : "UN";
                codeitemid = rs.getString("codeitemid") != null ? rs.getString("codeitemid").toString() : "UM";

            }
            String b0110 = "";
            if (codeitemid == null || codeitemid.length() <= 0) {
                b0110 = "UN";
            } else {
                b0110 = "UN" + codeitemid;
            }
            hashmap = ReadHoliday_type(b0110);
            if (hashmap.isEmpty()) {
                if (parentid.equals(codeitemid)) {
                    hashmap = ReadHoliday_type("UN");
                    if (hashmap.isEmpty()) {
                        hashmap.put("b0110", "UN");
                        hashmap.put("type", "");// 考勤人员库
                    }
                }
            }
        } catch (Exception e) {
            //20190227 jazz 45079 异常发生时，需返回默认数据，否则外部调用会产生死循环
            hashmap.put("b0110", "UN");
            hashmap.put("type", "");
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        list.add(hashmap);
        list.add(parentid);
        return list;
    }

    private HashMap ReadHoliday_type(String b0110) {
        StringBuffer sql = new StringBuffer();
        String name = "Holiday_type".toUpperCase();
        sql.append("select content from kq_parameter where ");
        sql.append(" B0110='" + b0110 + "' and UPPER(name)='" + name + "' and status=1");
        String content = "";
        HashMap hashmap = new HashMap();
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);

            rs = dao.search(sql.toString());
            if (rs.next()) {
                content = rs.getString("content");
                hashmap.put("type", content);
                hashmap.put("b0110", b0110);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return hashmap;
    }

    public static ArrayList newFieldItemList(ArrayList fielditemlist) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < fielditemlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            if (!"i9999".equals(fielditem.getItemid())) {
                if ("a0100".equals(fielditem.getItemid())) {

                    fielditem.setVisible(false);
                } else {
                    if ("1".equals(fielditem.getState())) {
                        fielditem.setVisible(true);
                    } else {
                        fielditem.setVisible(false);
                    }
                }
            }
            
            FieldItem fielditem_new = (FieldItem) fielditem.cloneItem();
            list.add(fielditem_new);
        }

        return list;
    }

    /**
     * 
     * @param fieldlist
     * @return
     */
    public String getColumn(ArrayList fieldlist) {
        StringBuffer column = new StringBuffer();
        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fieldlist.get(i);
            column.append(fielditem.getItemid() + ",");
        }
        column.setLength(column.length() - 1);
        return column.toString();
    }

    public void initComputer(String exp_field, String hols_status, String b0110) {
        StringBuffer sql = new StringBuffer();
        String oldname = "REST_FORMULA_" + hols_status;
        String newname = "REST_" + exp_field.toUpperCase() + "_" + hols_status;
        sql.append("update kq_parameter set");
        sql.append(" name='" + newname + "'");
        sql.append(" where UPPER(name)='" + oldname + "' and  B0110='" + b0110 + "'");
        String sqlstr = "select * from kq_parameter where UPPER(name)='" + newname + "' and  B0110='" + b0110 + "'";
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sqlstr);
            if (!rs.next()) {
                dao.update(sql.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
    }

    public ArrayList fieldList(UserView userView) {
        ArrayList list = new ArrayList();
        ArrayList fieldlist = DataDictionary.getFieldList("Q17", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem fielditem = (FieldItem) fieldlist.get(i);
            if ("N".equals(fielditem.getItemtype()) || ("q17z1").equals(fielditem.getItemid()) || ("q17z3").equals(fielditem.getItemid())) {
                CommonData dataobj = new CommonData();
                dataobj = new CommonData(fielditem.getItemid(), fielditem.getItemid().toUpperCase() + ":" + fielditem.getItemdesc());
                list.add(dataobj);
            }
            /*
             * if(userView.analyseFieldPriv(fielditem.getItemid()).equals("0"))
             * continue;
             */
        }
        return list;
    }

    /**
     * 添加不存在的用户
     * 
     * @param nbase
     * @param b0110
     * @param theYear
     * @param feast_start
     * @param feast_end
     * @param whereIN
     * @throws GeneralException
     */
    public void insertFeastUser(String nbase, String theYear, String feast_start, String feast_end, String whereIN, String hols_status, String balance, Connection conn) throws GeneralException {
        StringBuffer insert = new StringBuffer();
        synchronizationInit(nbase, theYear, whereIN, hols_status, conn);

        String last_balance_Time = KqUtilsClass.getFieldByDesc("q17", "结余截止日期");
        
        String char_to_date_start = Sql_switcher.dateValue(feast_start);
        String char_to_date_end = Sql_switcher.dateValue(feast_end);
        String char_to_balanceDate = Sql_switcher.dateValue(balanceDate);
        
        insert.append("INSERT INTO q17 (nbase,A0100,Q1701,B0110,E0122,E01A1,A0101,");
        insert.append("Q1703,");// 年假天数
        insert.append("Q17Z1,");// 年假开始
        if(!"".equals(this.balanceDate) && !"".equals(last_balance_Time)) {            
            insert.append(last_balance_Time + ",");
        }
        insert.append("Q17Z3,");// 年假结束
        insert.append("Q1705,");// 已休天数
        insert.append("Q1707,");// 可休天数
        // 上年结余
        if ("1".equals(balance)) {
            insert.append(this.getBalance());
            insert.append(",");
        }

        insert.append("Q1709) ");
        insert.append(" select '" + nbase + "',a0100,'" + theYear + "',");
        insert.append(" B0110,E0122,E01A1,A0101,0,");
        insert.append("" + char_to_date_start + ",");
        insert.append("" + char_to_date_end + ",");
        if(!"".equals(this.balanceDate) && !"".equals(last_balance_Time)) {
            insert.append("" + char_to_balanceDate + ",");
        }
        // 上年结余
        if ("1".equals(balance)) {
            insert.append("0,0,0,'" + hols_status + "' from " + nbase + "A01");
        } else {
            insert.append("0,0,'" + hols_status + "' from " + nbase + "A01");
        }
        insert.append(" WHERE NOT EXISTS(SELECT * FROM q17");
        insert.append(" where q17.a0100=" + nbase + "A01.a0100");
        // insert.append(" and q17.b0110="+nbase+"A01.b0110");
        String q17_b0110 = Sql_switcher.isnull("q17.b0110", "'a'");
        String a01_b0110 = Sql_switcher.isnull(nbase + "A01.b0110", "'a'");
        insert.append(" and " + q17_b0110 + "=" + a01_b0110 + "");
        insert.append(" and q17.q1701='" + theYear + "'");
        insert.append(" and q17.q1709='" + hols_status + "'");
        insert.append(" and q17.nbase='" + nbase + "')");
        insert.append(" AND a0100 in(select a0100 " + whereIN + ")");
        ContentDAO dao = new ContentDAO(conn);
        ArrayList insertList = new ArrayList();
        try {
            dao.insert(insert.toString(), insertList);
        } catch (Exception e) {
            // throw GeneralExceptionHandler.Handle(e);

            e.printStackTrace();
        }

    }

    public void synchronizationInit(String nbase, String theYear, String whereIN, String hols_status, Connection conn) throws GeneralException {

        String destTab = "q17";// 目标表
        String srcTab = nbase + "A01";// 源表
        String strJoin = "Q17.A0100=" + srcTab + ".A0100";// 关联串
                                                          // xxx.field_name=yyyy.field_namex,....
        String strSet = "Q17.B0110=" + srcTab + ".B0110`Q17.E0122=" + srcTab + ".E0122`Q17.E01A1=" + srcTab + ".E01A1`Q17.A0101=" + srcTab + ".A0101";// 更新串
                                                                                                                                                      // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "q17.q1701='" + theYear + "' and q17.nbase='" + nbase + "' and q17.q1709='" + hols_status + "'";// 更新目标的表过滤条件
        String strSWhere = srcTab + ".a0100 in (select a0100 " + whereIN + ")";// 源表的过滤条件
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        String othWhereSql = destTab + ".a0100 in(select a0100 " + whereIN + ")";
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
        ContentDAO dao = new ContentDAO(conn);
        try {
            dao.update(update);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获得上年结余的字段名称
     * 
     * @return
     */
    public String getBalance() {
        // 获得年假结余的列名
        String balance = "";

        ArrayList fieldList = DataDictionary.getFieldList("q17", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem item = (FieldItem) fieldList.get(i);
            if ("上年结余".equalsIgnoreCase(item.getItemdesc())) {
                balance = item.getItemid();
            }
        }

        return balance;
    }

    /**
     * 获得没有插入Q17的人员信息
     * 
     * @param nbase
     * @param theYear
     * @param whereIN
     * @param hols_status
     * @return
     */
    public ArrayList selectFeastUser(String nbase, String theYear, String whereIN, String hols_status, Connection conn) {
        StringBuffer sel = new StringBuffer();
        ArrayList userList = new ArrayList();
        sel.append(" select '");
        sel.append(nbase);
        sel.append("' nbase,a0100 from ");
        sel.append(nbase);
        sel.append("A01");
        sel.append(" WHERE NOT EXISTS(SELECT * FROM q17");
        sel.append(" where q17.a0100=");
        sel.append(nbase);
        sel.append("A01.a0100 and ");
        String q17_b0110 = Sql_switcher.isnull("q17.b0110", "'a'");
        String a01_b0110 = Sql_switcher.isnull(nbase + "A01.b0110", "'a'");
        sel.append(q17_b0110);
        sel.append("=");
        sel.append(a01_b0110);
        sel.append(" and q17.q1701='");
        sel.append(theYear);
        sel.append("' and q17.q1709='");
        sel.append(hols_status);
        sel.append("' and q17.nbase='");
        sel.append(nbase);
        sel.append("') AND a0100 in(select a0100 ");
        sel.append(whereIN);
        sel.append(")");
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sel.toString());
            while (rs.next()) {
                ArrayList list = new ArrayList();
                list.add(rs.getString("nbase"));
                list.add(rs.getString("a0100"));
                list.add(rs.getString("nbase"));
                list.add(rs.getString("a0100"));
                userList.add(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return userList;
    }

    /**
     * 删除不存在的用户
     * 
     * @param nbase
     * @param b0110
     * @param theYear
     * @param feast_start
     * @param feast_end
     * @param whereIN
     * @throws GeneralException
     */
    public void deleteNoFeastUser(String nbase, String b0110, String theYear, String hols_status) throws GeneralException {

        StringBuffer delete = new StringBuffer();
        delete.append("DELETE FROM q17 WHERE NOT A0100 IN (SELECT A0100 FROM " + nbase + "A01 where b0110='" + b0110 + "')");
        delete.append(" AND q17.q1701='" + theYear + "'");
        delete.append(" AND q17.nbase='" + nbase + "'");
        delete.append(" and q17.q1709='" + hols_status + "'");
        delete.append(" AND b0110='" + b0110 + "'");
        ContentDAO dao = new ContentDAO(this.conn);

        try {
            dao.update(delete.toString());
        } catch (Exception e) {
            e.printStackTrace();
            // throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 计算
     * 
     * @param alUsedFields
     *            所用的fieldlist
     * @param nbase
     *            人员库前缀
     * @param exc_p
     *            公式
     * @param whl
     *            select 过滤语句
     */
    public void countExc_p(ArrayList alUsedFields, String nbase, String exc_field, String exc_p, String whl, String theYear, String hols_status, UserView userView) {
        if (exc_p == null || exc_p.length() <= 0) {
            return;
        }

        int infoGroup = 0; // forPerson 人员
        
        //zxj 20150408 需要判断指标类型，不能写死成数值型
        GetCheckExpreTrans check = new GetCheckExpreTrans();
        int varType = check.getFieldType(exc_field);
        String varTypes = check.getFieldTypes(exc_field);
        
        YearMonthCount ycm = null;
        FieldItem exc_item = DataDictionary.getFieldItem(exc_field);
        ContentDAO dao = new ContentDAO(this.conn);
        YksjParser yp = new YksjParser(userView, alUsedFields, YksjParser.forSearch, varType, infoGroup, "Ht", nbase);
        yp.setRenew_term(" q1701='" + theYear + "' and q1709='" + hols_status + "'");
        // yp.setRenew_term("q1709='"+hols_status+"'");
        yp.run(exc_p, ycm, exc_field, "q17", dao, whl, this.conn, varTypes, 12, exc_item.getDecimalwidth(), 2, null);
    }

    public void updateData(String nbase, String b0110, String theYear, String whereIN, String hols_status) {
        StringBuffer update = new StringBuffer();
        update.append("update q17 set");
        update.append(" q1707=q1703-q1705");
        update.append(" where b0110='" + b0110 + "'");
        update.append(" and q1701='" + theYear + "'");
        update.append(" and nbase='" + nbase + "'");
        update.append(" and q1709='" + hols_status + "'");
        update.append(" and a0100 in(select a0100 " + whereIN + ")");
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.update(update.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteData(String userbase, String b0110, String whereIN, String year, String hols_status) {
        boolean isCollect = false;
        StringBuffer strsql = new StringBuffer();
        strsql.append("delete from Q17");
        strsql.append(" where nbase=?");
        strsql.append(" and b0110=?");
        strsql.append(" and q1701=?");
        strsql.append(" and q1709=?");
        strsql.append(" and a0100 in(select a0100 " + whereIN + ")");
        ArrayList deletelist = new ArrayList();
        deletelist.add(userbase);
        deletelist.add(b0110);
        deletelist.add(year);
        deletelist.add(hols_status);
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.delete(strsql.toString(), deletelist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCollect;
    }
    
    /**
     * 将上年结余更新到本年的数据中
     * @param nbase
     * @param b0110
     * @param theYear
     */
    public void updateBalance(String theYear, String hols_status, ArrayList list) {
        StringBuffer sql = new StringBuffer();
        int currYear = Integer.parseInt(theYear);
        String topYear = String.valueOf(currYear - 1);
        sql.append("update q17 set ");
        sql.append(this.getBalance());
        sql.append("=(select case when ");
        sql.append(Sql_switcher.isnull("q1707", "0"));
        sql.append(" < ");
        sql.append(Sql_switcher.isnull("q1703", "0"));
        sql.append(" then ");
        sql.append(Sql_switcher.isnull("q1707", "0"));
        sql.append(" else ");
        sql.append(Sql_switcher.isnull("q1703", "0"));
        sql.append(" end q1707 from q17 where ");
        sql.append("nbase=? and a0100=? and q1701='");
        sql.append(topYear);
        sql.append("' and q1709='");
        sql.append(hols_status);
        sql.append("') where nbase=? and a0100=? and q1701='");
        sql.append(theYear);
        sql.append("' and q1709='");
        sql.append(hols_status);
        sql.append("'");
        
        try {
            ContentDAO dao=new ContentDAO(this.conn);
            dao.batchUpdate(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(String balanceDate) {
        this.balanceDate = balanceDate;
    }

}
