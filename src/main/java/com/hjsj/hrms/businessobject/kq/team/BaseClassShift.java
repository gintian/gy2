package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

/**
 * 基本班次排列
 * <p>
 * Title:BaseClassShift.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Nov 2, 2006 11:14:48 AM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 * zxj update 2013.05.13
 * 1、去除用继承方式错误使用接口KqClassArrayConstant的方式
 * 2、去除大量的没必要分数据库类型的重复sql
 * 3、去除其它无用代码
 */
public class BaseClassShift
{

    private UserView userView;
    private Connection conn;
    private String destTab = KqClassArrayConstant.kq_employ_shift_table; 

    public BaseClassShift()
    {

    }

    public BaseClassShift(UserView userView, Connection conn)
    {
        this.userView = userView;
        this.conn = conn;
    }
    
    /**
     * 选择了单位,部门,职位(排班/个人排班,此时要将人员单位编码传过来)
     * 
     * @param t_table
     */
    public void insrtTempData(String t_table, String date_Table, String nbase, String whereIN, String sWhere,String b0110) throws GeneralException
    {
    	if (b0110 != null && b0110.length() > 0) 
		{
    		sWhere += " and DT.ORGID = '" + b0110 + "'";
		}
    	insrtTempData(t_table, date_Table, nbase, whereIN, sWhere);
    }
    
    /**
     * 选择了单位,部门,职位
     * 
     * @param t_table
     */
    public void insrtTempData(String t_table, String date_Table, String nbase, String whereIN, String sWhere) throws GeneralException
    {
        StringBuffer insertSql = new StringBuffer();
        String insetWhere = "";
        String condition = whereIN;
        if (whereIN != null && whereIN.length() > 0)
        {
            // insetWhere=" and a0100 in(select a0100 "+whereIN+") ";
            // 首钢优化，in的速度会慢，改用EXISTS

            // 判断条件是否为空，防止sql语句出错
            if (condition != null && condition.toLowerCase().trim().contains("where"))
            {
                insetWhere = " and EXISTS (select a0100 " + whereIN + " AND a0100=" + nbase + "A01.a0100) ";
            }
            else
            {
                insetWhere = " and EXISTS (select a0100 " + whereIN + " where a0100=" + nbase + "A01.a0100) ";
            }
        }

        insertSql.append("INSERT INTO " + t_table + "(nbase,A0100,B0110,E0122,E01A1,A0101,Q03Z0,class_id,flag) ");
        insertSql.append("SELECT '" + nbase + "' AS nbase,A0100, B0110, " + Sql_switcher.isnull("E0122", "''") + ", " + Sql_switcher.isnull("E01A1", "''") + ", A0101, DT.sDate AS q03z5,0,DT.dkind ");
        insertSql.append(" FROM " + nbase + "A01 , " + date_Table + " DT");
        insertSql.append(" WHERE 1=1 ");
        insertSql.append(insetWhere);
        insertSql.append(sWhere);
        
        //zxj 不考勤人员（包括未设置考勤方式人员）不用排班
        KqParameter kqParameter = new KqParameter(this.userView, this.conn);
        String kqTypeFld = kqParameter.getKq_type();
        if (kqTypeFld==null || kqTypeFld.trim().length()==0)
		{
            throw  new GeneralException("参数设置中考勤方式未设置,请设置后再排班！");
		}
        insertSql.append(" AND " + Sql_switcher.isnull(kqTypeFld, "'04'") + "<>'04'");

        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            ArrayList list = new ArrayList();
            dao.insert(insertSql.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public void insrtGroupTempData(String t_table, String date_Table, String group_id) throws GeneralException
    {
        StringBuffer insertSql = new StringBuffer();
        String srcTab = "kq_group_emp";// 源表
        String insetWhere = "and " + srcTab + ".group_id='" + group_id + "'";

        insertSql.append("INSERT INTO " + t_table + "(nbase,A0100,B0110,E0122,E01A1,A0101,Q03Z0,class_id,flag) ");
        insertSql.append("SELECT  nbase,A0100, B0110, " + Sql_switcher.isnull("E0122", "''") + ", " + Sql_switcher.isnull("E01A1", "''") + ", A0101, DT.sDate AS q03z5,0,DT.dkind ");
        insertSql.append(" FROM " + srcTab + " , " + date_Table + " DT");
        insertSql.append(" WHERE 1=1 ");
        insertSql.append(insetWhere);

        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            ArrayList list = new ArrayList();
            dao.insert(insertSql.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 给班次临时表里插入班次
     * 
     * @param class_id
     * @param t_table
     * @param rest_postpone
     * @param feast_postpone
     */
    public void insertClassToTemp(String class_id, String t_table, String rest_postpone, String feast_postpone) throws GeneralException
    {
        String update = "";
        if (feast_postpone == null || feast_postpone.length() <= 0) {
            feast_postpone = "0";
        }
        if (rest_postpone == null || rest_postpone.length() <= 0) {
            rest_postpone = "0";
        }
        if (class_id != null && class_id.length() > 0)
        {
            update = "update " + t_table + " set class_id='" + class_id + "'";
        }
        else
        {
            update = "update " + t_table + " set class_id=null";
        }
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.update(update);
            if ("1".equals(feast_postpone))
            {
                update = "update " + t_table + " set class_id='0' where flag='3'";
                dao.update(update);
            }
            if ("1".equals(rest_postpone))
            {
                update = "update " + t_table + " set class_id='0' where flag='2'";
                dao.update(update);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public void insertClassToShift(String t_table, String whereIN) throws GeneralException
    {
        String srcTab = t_table;// 源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase and " + destTab + ".q03z0=" + srcTab + ".q03z0";// 关联串
                                                                                                                                                                // xxx.field_name=yyyy.field_namex,....
        String strSet = destTab + ".class_id=" + srcTab + ".class_id`" + destTab + ".B0110=" + srcTab + ".B0110`" + destTab + ".E0122=" + srcTab + ".E0122`" + destTab + ".E01A1=" + srcTab + ".E01A1";// 更新串
                                                                                                                                                                                                        // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "";// destTab+".status='0'";//更新目标的表过滤条件
        // String strSWhere=srcTab+".a0100 in(select a0100
        // "+whereIN+")";//源表的过滤条件 首钢优化，in的速度会慢，改用EXISTS
        // String strSWhere="EXISTS (select a0100 "+whereIN+" AND
        // a0100="+srcTab+".a0100)";//源表的过滤条件

        // 判断判断条件是否为空，防止sql语句出错 --wangzhongjun 2010.4.9
        String strSWhere = "";
        String condition = whereIN;
        if (condition != null && condition.toLowerCase().trim().contains("where"))
        {
            strSWhere = "EXISTS (select a0100 " + whereIN + " AND a0100=" + srcTab + ".a0100)";
        }
        else
        {
            strSWhere = "EXISTS (select a0100 " + whereIN + " where a0100=" + srcTab + ".a0100)";
        }
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        // String othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
        // 首钢优化，in的速度会慢，改用EXISTS
        // String othWhereSql="EXISTS (select a0100 "+whereIN+" AND
        // a0100="+destTab+".a0100)";

        // 判断判断条件是否为空，防止sql语句出错 --wangzhongjun 2010.4.9
        String othWhereSql = "";
        if (condition != null && condition.toLowerCase().trim().contains("where"))
        {
            othWhereSql = "EXISTS (select a0100 " + whereIN + " AND a0100=" + destTab + ".a0100)";
        }
        else
        {
            othWhereSql = "EXISTS (select a0100 " + whereIN + " where a0100=" + destTab + ".a0100)";
        }
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
        // System.out.println(update);
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.update(update);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        StringBuffer insertSQL = new StringBuffer();
        insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
        insertSQL.append(" SELECT a.nbase,a.A0100,a.A0101,a.B0110,a.E0122,a.E01A1,a.Q03Z0,a.class_id,0");
        insertSQL.append(" FROM " + t_table + " a ");
        insertSQL.append("WHERE NOT EXISTS(SELECT * FROM kq_employ_shift b");
        insertSQL.append(" WHERE a.A0100=b.A0100 and a.nbase=b.nbase and a.Q03Z0=b.Q03Z0 and a0100 in(select a0100 " + whereIN + "))");
        insertSQL.append(" and a0100 in(select a0100 " + whereIN + ")");
        try
        {
            ArrayList list = new ArrayList();
            // System.out.println(insertSQL.toString());
            dao.insert(insertSQL.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public void insertClassToShift(String t_table, String whereIN, String nbase, String whereD2, String whereS2) throws GeneralException
    {
        String srcTab = t_table;// 源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase and " + destTab + ".q03z0=" + srcTab + ".q03z0";// 关联串
                                                                                                                                                                // xxx.field_name=yyyy.field_namex,....
        String strSet = destTab + ".class_id=" + srcTab + ".class_id`" + destTab + ".B0110=" + srcTab + ".B0110`" + destTab + ".E0122=" + srcTab + ".E0122`" + destTab + ".E01A1=" + srcTab + ".E01A1`" + destTab + ".A0101=" + srcTab + ".A0101";// 更新串
                                                                                                                                                                                                                                                    // xxx.field_name=yyyy.field_namex,....
        String strDWhere = destTab + ".nbase='" + nbase + "'";// destTab+".status='0'";//更新目标的表过滤条件
        // String strSWhere=srcTab+".a0100 in(select a0100
        // "+whereIN+")";//源表的过滤条件 首钢优化，in的速度会慢，改用EXISTS
        // String strSWhere="EXISTS (select a0100 "+whereIN+" AND
        // a0100="+srcTab+".a0100)";//源表的过滤条件

        // 判断判断条件是否为空，防止sql语句出错 --wangzhongjun 2010.4.9
        String strSWhere = "";
        String condition = whereIN;
        if (condition != null && condition.toLowerCase().trim().contains("where"))
        {
            strSWhere = "EXISTS (select a0100 " + whereIN + " AND a0100=" + srcTab + ".a0100)";
        }
        else
        {
            strSWhere = "EXISTS (select a0100 " + whereIN + " where a0100=" + srcTab + ".a0100)";
        }
        if (whereS2 != null && whereS2.length() > 0) {
            strSWhere = strSWhere + " and " + whereS2;
        }
        if (whereD2 != null && whereD2.length() > 0) {
            strDWhere = strDWhere + " and " + whereD2;
        }
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        // String othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
        // 首钢优化，in的速度会慢，改用EXISTS
        // String othWhereSql="EXISTS (select a0100 "+whereIN+" AND
        // a0100="+destTab+".a0100)";

        // 判断判断条件是否为空，防止sql语句出错 --wangzhongjun 2010.4.9
        String othWhereSql = "";
        if (condition != null && condition.toLowerCase().trim().contains("where"))
        {
            othWhereSql = "EXISTS (select a0100 " + whereIN + " AND a0100=" + destTab + ".a0100)";
        }
        else
        {
            othWhereSql = "EXISTS (select a0100 " + whereIN + " where a0100=" + destTab + ".a0100)";
        }
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
        // System.out.println(update);
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.update(update);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        StringBuffer insertSQL = new StringBuffer();
        insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
        insertSQL.append(" SELECT a.nbase,a.A0100,a.A0101,a.B0110,a.E0122,a.E01A1,a.Q03Z0,a.class_id,0");
        insertSQL.append(" FROM " + t_table + " a ");
        insertSQL.append("WHERE NOT EXISTS(SELECT * FROM kq_employ_shift b");
        insertSQL.append(" WHERE a.A0100=b.A0100 and a.nbase=b.nbase and a.Q03Z0=b.Q03Z0 and a0100 in(select a0100 " + whereIN + "))");
        insertSQL.append(" and a0100 in(select a0100 " + whereIN + ")");
        try
        {
            ArrayList list = new ArrayList();
            // System.out.println(insertSQL.toString());
            dao.insert(insertSQL.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public void insertClassToShift(String t_table) throws GeneralException
    {
        String srcTab = t_table;// 源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase and " + destTab + ".q03z0=" + srcTab + ".q03z0";// 关联串
                                                                                                                                                                // xxx.field_name=yyyy.field_namex,....
        String strSet = destTab + ".class_id=" + srcTab + ".class_id`" + destTab + ".B0110=" + srcTab + ".B0110`" + destTab + ".E0122=" + srcTab + ".E0122`" + destTab + ".E01A1=" + srcTab + ".E01A1";// 更新串
                                                                                                                                                                                                        // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "";// destTab+".status='0'";//更新目标的表过滤条件
        String strSWhere = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase and " + destTab + ".q03z0=" + srcTab + ".q03z0";// 源表的过滤条件
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.update(update);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        StringBuffer insertSQL = new StringBuffer();
        insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
        insertSQL.append(" SELECT a.nbase,a.A0100,a.A0101,a.B0110,a.E0122,a.E01A1,a.Q03Z0,a.class_id,0");
        insertSQL.append(" FROM " + t_table + " a ");
        insertSQL.append("WHERE NOT EXISTS(SELECT * FROM kq_employ_shift b");
        insertSQL.append(" WHERE a.A0100=b.A0100 and a.nbase=b.nbase and a.Q03Z0=b.Q03Z0)");
        try
        {
            ArrayList list = new ArrayList();
            dao.insert(insertSQL.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 
     * @param codeitemid
     * @return
     */
    public ArrayList getOrgid_listFrom(String codeitemid, String codesetid) throws GeneralException
    {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select codesetid,codeitemid,codeitemdesc,childid from organization where ");
        sql.append("codesetid='" + codesetid + "' and codeitemid like'" + codeitemid + "%'");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try
        {
            rs = dao.search(sql.toString());
            while (rs.next())
            {
                list.add(rs.getString("codeitemid"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    /**
     * 建立临时表-正常排班
     * 
     * @param userView
     * @param conn
     * @return
     */
    public String tempClassTable()
    {
        String table_name = "t#" + userView.getUserName().trim() + "_kq_cl";
        table_name = table_name.toLowerCase();
        DbWizard dbWizard = new DbWizard(conn);
        Table table = new Table(table_name);
        if (dbWizard.isExistTable(table_name, false))
        {
            dropTable(table_name);
        }
        Field temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(true);
        temp.setNullable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("A0100", "人员编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(true);
        temp.setNullable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("q03z0", "工作日期");
        temp.setDatatype(DataType.STRING);
        temp.setLength(20);
        temp.setKeyable(true);
        temp.setNullable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("B0110", "单位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("E0122", "部门");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);

        temp = new Field("OB0110", "原单位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("E01A1", "职位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("A0101", "姓名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("class_id", "班次编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("flag", "标志");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("group_id", "组编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("cardno", "卡号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        try
        {
            dbWizard.createTable(table);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        /** 重新加载数据模型 */

        DBMetaModel dbmodel = new DBMetaModel(this.conn);
        dbmodel.reloadTableModel(table_name);
        return table_name;
    }

    public void deleteTable(String tablename) throws GeneralException
    {
        String deleteSQL = "delete from " + tablename + "";
        ArrayList deletelist = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.delete(deleteSQL, deletelist);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 删除临时表
     * 
     * @param tablename
     */
    public void dropTable(String tablename)
    {
        String deleteSQL = "delete from " + tablename + "";
        ArrayList deletelist = new ArrayList();

        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.delete(deleteSQL, deletelist);
            DbWizard dbWizard = new DbWizard(this.conn);
            Table table = new Table(tablename);
            dbWizard.dropTable(table);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /***************************************************************************
     * 建立时间临时表
     **************************************************************************/
    public String creat_KqTmp_Table(String userid) throws GeneralException
    {
        String tablename = RegisterInitInfoData.getTmpTableName(this.userView.getUserName(), RegisterInitInfoData.getKqPrivCode(userView));
        DbWizard dbWizard = new DbWizard(this.conn);
        Table table = new Table(tablename);
        if (dbWizard.isExistTable(tablename, false))
        {
            dropTable(tablename);
        }
        Field temp = new Field("orgid", "组织编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        Field temp1 = new Field("sDate", "考勤日期");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(20);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        table.addField(temp1);
        Field temp2 = new Field("dkind", "标志");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(5);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        table.addField(temp2);
        try
        {
            dbWizard.createTable(table);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return tablename;
    }

    // 生成初始时间表
    // 初始数据
    public ArrayList initializtion_date_Table(ArrayList periodlist, String rest_date, String date_Table, String rest_b0110, String orgid) throws GeneralException
    {
        String deleteSQL = "delete from " + date_Table + " where orgid='" + orgid + "'";
        ArrayList deletelist = new ArrayList();
        String insertSQL = "insert into " + date_Table + " (orgid,sDate,dkind) values (?,?,?)";
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList insertList = new ArrayList();
        try
        {
            dao.delete(deleteSQL, deletelist);
            for (int i = 0; i < periodlist.size(); i++)
            {

                String cur_date = periodlist.get(i).toString();

                ArrayList list = new ArrayList();
                String feast_name = IfRestDate.if_Feast(cur_date, this.conn);
                if (feast_name == null || feast_name.length() <= 0)
                {
                    if (!IfRestDate.if_Rest(cur_date, userView, rest_date))
                    {
                        String week_date = IfRestDate.getWeek_Date(rest_b0110, cur_date, this.conn);
                        if (week_date != null && week_date.length() > 0)
                        {
                            list.add(orgid);
                            list.add(cur_date);
                            list.add("2"); // 休息日
                        }
                        else
                        {

                            list.add(orgid);
                            list.add(cur_date);
                            list.add("1"); // 工作日
                        }
                    }
                    else
                    {
                        String turn_date = IfRestDate.getTurn_Date(rest_b0110, cur_date, this.conn);
                        if (turn_date == null || turn_date.length() <= 0)
                        {
                            list.add(orgid);
                            list.add(cur_date);
                            list.add("2"); // 休息日
                        }
                        else
                        {
                            list.add(orgid);
                            list.add(cur_date);
                            list.add("1"); // 工作日
                        }

                    }

                }
                else
                {
                    String turn_date = IfRestDate.getTurn_Date(rest_b0110, cur_date, this.conn);
                    if (turn_date == null || turn_date.length() <= 0)
                    {
                        list.add(orgid);
                        list.add(cur_date);
                        list.add("3"); // 节假日
                    }
                    else
                    {
                        if (IfRestDate.if_Rest(cur_date, userView, rest_date))
                        {
                            list.add(orgid);
                            list.add(cur_date);
                            list.add("3"); // 节假日
                        }
                        else
                        {
                            list.add(orgid);
                            list.add(cur_date);
                            list.add("1"); // 工作日
                        }
                    }
                }
                insertList.add(list);
            }
            dao.batchInsert(insertSQL, insertList);
        }
        catch (Exception e)
        {
            throw new GeneralException("初始化排班人员数据出错！");
        }
        return insertList;
    }

    public void synchronizationInitEmployee_Table(String nbase, String whereIN) throws GeneralException
    {

        String srcTab = nbase + "A01";// 源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100";// 关联串
                                                                    // xxx.field_name=yyyy.field_namex,....
        String strSet = destTab + ".B0110=" + srcTab + ".B0110`" + destTab + ".E0122=" + srcTab + ".E0122`" + destTab + ".E01A1=" + srcTab + ".E01A1";// 更新串
                                                                                                                                                        // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "" + destTab + ".nbase='" + nbase + "'";// 更新目标的表过滤条件
        // String strSWhere=srcTab+".a0100 in(select a0100
        // "+whereIN+")";//源表的过滤条件 首钢优化，in的速度会慢，改用EXISTS
        // String strSWhere="EXISTS (select a0100 "+whereIN+" AND
        // a0100="+srcTab+".a0100)";//源表的过滤条件

        // 判断判断条件是否为空，防止sql语句出错
        String strSWhere = "";
        String condition = whereIN;
        if (condition != null && condition.toLowerCase().trim().contains("where"))
        {
            // 源表的过滤条件
            strSWhere = "EXISTS (select a0100 " + whereIN + " AND a0100=" + srcTab + ".a0100)";
        }
        else
        {
            strSWhere = "EXISTS (select a0100 " + whereIN + " where a0100=" + srcTab + ".a0100)";
        }
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        // String othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
        // 首钢优化，in的速度会慢，改用EXISTS
        // String othWhereSql="EXISTS (select a0100 "+whereIN+" AND
        // a0100="+destTab+".a0100)";

        // 判断条件是否为空，防止sql语句出错
        String othWhereSql = "";
        if (condition != null && condition.toLowerCase().trim().contains("where"))
        {
            othWhereSql = "EXISTS (select a0100 " + whereIN + " AND a0100=" + destTab + ".a0100)";
        }
        else
        {
            othWhereSql = "EXISTS (select a0100 " + whereIN + " where a0100=" + destTab + ".a0100)";
        }
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.update(update);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("初始化排班人员信息数据出错！"));
        }
    }

    /**
     * 同步排班表人员基本信息（单位、部门、职位）
     * @param nbase
     * @param whereIN
     * @param whereD2
     *            目标条件
     * @param whereS2
     *            源条件
     * @throws GeneralException
     */
    public void synchronizationInitEmployee_Table(String nbase, String whereIN, String whereD2, String whereS2) throws GeneralException
    {

        String srcTab = nbase + "A01";// 源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100";// 关联串
                                                                    // xxx.field_name=yyyy.field_namex,....
        String strSet = destTab + ".B0110=" + srcTab + ".B0110`" + destTab + ".E0122=" + srcTab + ".E0122`" + destTab + ".E01A1=" + srcTab + ".E01A1";// 更新串
                                                                                                                                                        // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "" + destTab + ".nbase='" + nbase + "'";// 更新目标的表过滤条件
        // String strSWhere=srcTab+".a0100 in(select a0100
        // "+whereIN+")";//源表的过滤条件 首钢优化，in的速度会慢，改用EXISTS
        // String strSWhere="EXISTS (select a0100 "+whereIN+" AND
        // a0100="+srcTab+".a0100)";//源表的过滤条件

        // 判断判断条件是否为空，防止sql语句出错
        String strSWhere = "";
        String condition = whereIN;
        if (condition != null && condition.toLowerCase().trim().contains("where"))
        {
            // 源表的过滤条件
            strSWhere = "EXISTS (select a0100 " + whereIN + " AND " + destTab + ".a0100=" + srcTab + ".a0100";
        }
        else
        {
            strSWhere = "EXISTS (select a0100 " + whereIN + " where  " + destTab + ".a0100=" + srcTab + ".a0100";
        }
        if (whereS2 != null && whereS2.length() > 0) {
            strSWhere = strSWhere + " and " + whereS2 + ")";
        } else {
            strSWhere = strSWhere + ")";
        }
        
        if (whereD2 != null && whereD2.length() > 0) {
            strDWhere = strDWhere + " and " + whereD2;
        }
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        // String othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
        // 首钢优化，in的速度会慢，改用EXISTS
        // String othWhereSql="EXISTS (select a0100 "+whereIN+" AND
        // a0100="+destTab+".a0100)";

        // 判断条件是否为空，防止sql语句出错
        String othWhereSql = "";
        if (condition != null && condition.toLowerCase().trim().contains("where"))
        {
            othWhereSql = "EXISTS (select a0100 " + whereIN + " AND " + srcTab + ".a0100=" + destTab + ".a0100";
        }
        else
        {
            othWhereSql = "EXISTS (select a0100 " + whereIN + " where " + srcTab + ".a0100=" + destTab + ".a0100";
        }
        
        if (whereS2 != null && whereS2.length() > 0) {
            othWhereSql = othWhereSql + " and " + whereS2 + ")";
        } else {
            othWhereSql = othWhereSql + ")";
        }
        
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
        // System.out.println(update);
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.update(update);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("初始化考勤处理人员信息数据出错！"));
        }
    }

    public void synchronizationInitGtoupEmployee_Table(String group_id) throws GeneralException
    {

        String srcTab = "kq_group_emp";// 源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase";// 关联串
                                                                                                                // xxx.field_name=yyyy.field_namex,....
        String strSet = destTab + ".B0110=" + srcTab + ".B0110`" + destTab + ".E0122=" + srcTab + ".E0122`" + destTab + ".E01A1=" + srcTab + ".E01A1";// 更新串
                                                                                                                                                        // xxx.field_name=yyyy.field_namex,....
        String strDWhere = "";// 更新目标的表过滤条件
        String strSWhere = "";
        if (group_id != null && group_id.length() > 0)
        {
            strSWhere = srcTab + ".group_id='" + group_id + "'";// 源表的过滤条件
            strJoin = strJoin + " and " + strSWhere;
        }
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.update(update);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("初始化考勤处理人员信息数据出错！"));
        }
    }

    /**
     * 解决首钢同步人员排班信息过慢，增加时间限制
     * 
     * @param group_id
     * @param start_date
     * @param end_date
     * @throws GeneralException
     */
    public void synchronizationInitGtoupEmployee_Tablewy(String group_id, String start_date, String end_date) throws GeneralException
    {
        // 目标表
        // 源表
        String srcTab = "kq_group_emp";
        // 表连接
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase";
        // 更新值串                                                                                                        
        String strSet = destTab + ".B0110=" + srcTab + ".B0110`" + destTab + ".E0122=" + srcTab + ".E0122`" + destTab + ".E01A1=" + srcTab + ".E01A1";
        // 目标表条件                                                                                                                                                
        String strDWhere = "";
        // 源表条件
        String strSWhere = "";
        
        if (group_id != null && group_id.length() > 0)
        {
            if (!this.userView.isSuper_admin())
            {
                String privCode = RegisterInitInfoData.getKqPrivCode(userView);
                String privCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
                strSWhere = srcTab + ".group_id='" + group_id + "'";
                if (!"".equals(privCodeValue))
                {
                    if (privCode != null && "UN".equals(privCode)) {
                        strSWhere += " and " + srcTab + ".b0110 like '" + privCodeValue + "%'";
                    } else if (privCode != null && "UM".equals(privCode)) {
                        strSWhere += " and " + srcTab + ".e0122 like '" + privCodeValue + "%'";
                    } else if (privCode != null && "@K".equals(privCode)) {
                        strSWhere += " and " + srcTab + ".e01a1 like '" + privCodeValue + "%'";
                    }
                }
            }
            else
            {
                strSWhere = srcTab + ".group_id='" + group_id + "'";
                strJoin += " and " + srcTab + ".group_id='" + group_id + "'";
            }
        }

        //目标表条件 注意：oracle库必须有该条件，否则目标表将被全表扫描，影响速度
        StringBuffer destWhr = new StringBuffer();
        destWhr.append("EXISTS(SELECT 1 FROM ");
        destWhr.append(srcTab);
        destWhr.append(" WHERE ");
        if (!"".equals(strSWhere))
        {
            destWhr.append(strSWhere);
            destWhr.append(" AND ");
        }
        destWhr.append(srcTab + ".nbase=" + destTab + ".nbase");
        destWhr.append(" AND ");
        destWhr.append(srcTab + ".A0100=" + destTab + ".A0100");
        destWhr.append(")");
        strDWhere = destWhr.toString();

        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        if (!this.userView.isSuper_admin())
        {
            String privCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);

            if (!"".equals(privCodeValue))
            {
                update += " and " + destTab + ".e0122 like '" + privCodeValue + "%'";
            }
        }

        update += " and " + destTab + ".q03z0>='" + start_date + "' and " + destTab + ".q03z0<='" + end_date + "'";
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.update(update);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("初始化考勤处理人员信息数据出错！"));
        }
    }

    /**
     * 得到时间列表
     * 
     * @param start_date
     * @param end_date
     * @return
     */
    public ArrayList getDatelist(String start_date, String end_date)
    {
        start_date = start_date.replaceAll("-", "\\.");
        end_date = end_date.replaceAll("-", "\\.");
        ArrayList list = new ArrayList();
        Date s_date = DateUtils.getDate(start_date, "yyyy.MM.dd");
        Date e_date = DateUtils.getDate(end_date, "yyyy.MM.dd");
        int diff = RegisterDate.diffDate(s_date, e_date);
        Date cur_date = null;
        String cur_day = "";
        for (int i = 0; i <= diff; i++)
        {
            cur_date = DateUtils.addDays(s_date, i);
            cur_day = DateUtils.format(cur_date, "yyyy.MM.dd");
            list.add(cur_day);
        }
        return list;
    }

    /**
     * 得到人员库中某个人员的指定字段属性的值
     * 
     * @param a0100
     * @param nbase
     * @param field
     * @return
     */
    public String getEMpData(String a0100, String nbase, String field) throws GeneralException
    {
        String sql = "select " + field + " from " + nbase + "A01 where a0100='" + a0100 + "'";
        ContentDAO dao = new ContentDAO(this.conn);
        String field_value = "";
        RowSet rs = null;
        try
        {
            rs = dao.search(sql);
            if (rs.next())
            {
                field_value = rs.getString(field);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
        return field_value;
    }
    /**
     * szk 20140214
	 * 判断班组成员是否重复
	 */
	public String canEmployeeInsertTemp(String[] right_fields)throws GeneralException
	{
        RowSet rs = null;
        try {
            StringBuffer where = new StringBuffer();

            for (int i = 0; i < right_fields.length; i++) {
                String group_id = right_fields[i];
                where.append("'" + group_id + "',");
            }
            where.setLength(where.length() - 1);
            
            //zxj 20170220 a0100=>nbase+a0100 如果有不同人员库相同a0100的人，判断人员是否充分出错
            String sql = "SELECT nbase" + Sql_switcher.concat() + "A0100 as a0100,A0101 FROM kq_group_emp WHERE group_id in (" + where + ")";
            sql = sql + " AND " + RegisterInitInfoData.getKqEmpPrivWhr(this.conn, this.userView, "kq_group_emp");
            
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList list = new ArrayList();
            rs = dao.search(sql);
            while (rs.next()) {
                list.add(rs.getString("A0100"));
                list.add(rs.getString("A0101"));
            }
            String temp = "";
            StringBuffer name = new StringBuffer("");
            ;
            for (int i = 0; i < (list.size()) - 2; i = i + 2) {
                temp = (String) list.get(i);
                for (int j = i + 2; j < list.size(); j = j + 2) {
                    if (temp.equals(list.get(j))) {
                        name.append(list.get(j + 1) + "，");
                    }
                }
            }
            if (name.length() >= 1) {
                name.setLength(name.length() - 1);
            }
            return name.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
	}
    /**
     * 选择了单位,部门,职位
     * 
     * @param t_table
     */
    public void insrtTempGroupData(String t_table, String date_Table, String group_id) throws GeneralException
    {
        StringBuffer insertSql = new StringBuffer();
        insertSql.append("INSERT INTO " + t_table + "(nbase,A0100,B0110,E0122,E01A1,A0101,Q03Z0,class_id,flag,group_id) ");
        insertSql.append("SELECT  nbase,A0100,B0110,");
        insertSql.append(Sql_switcher.isnull("E0122", "''"));
        insertSql.append(",");
        insertSql.append(Sql_switcher.isnull("E01A1", "''"));
        insertSql.append(",A0101,DT.sDate AS q03z5,0,DT.dkind,group_id ");
        insertSql.append(" FROM kq_group_emp, " + date_Table + " DT");
        insertSql.append(" WHERE group_id='" + group_id + "'");
        insertSql.append(" AND " + RegisterInitInfoData.getKqEmpPrivWhr(this.conn, this.userView, "kq_group_emp"));
        
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            ArrayList list = new ArrayList();
            dao.insert(insertSql.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 选择了单位,部门,职位
     * 
     * @param t_table
     */
    public void insrtTempEmpData(String t_table, String nbase, String whereIN, String sWhere, String cardno, String g_no) throws GeneralException
    {
        StringBuffer insertSql = new StringBuffer();
        String insetWhere = "";
        if (whereIN != null && whereIN.length() > 0)
        {
            insetWhere = " and a0100 in(select a0100 " + whereIN + ") ";
        }

        insertSql.append("INSERT INTO " + t_table + "(a0000,nbase,A0100,B0110,E0122,E01A1,A0101,cardno,g_no) ");
        insertSql.append("SELECT a0000,'" + nbase + "' AS nbase,A0100, B0110, " + Sql_switcher.isnull("E0122", "''") + ", " + Sql_switcher.isnull("E01A1", "''") + ", A0101, " + cardno + "," + g_no + " ");
        insertSql.append(" FROM " + nbase + "A01 ");
        insertSql.append(" WHERE ");
        insertSql.append(cardno + " IS NOT NULL ");
        if(Sql_switcher.searchDbServer() == Constant.MSSQL) {
            insertSql.append(" AND " + cardno + "<>'' ");
        }
        insertSql.append(insetWhere);
        insertSql.append(sWhere);

        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            ArrayList list = new ArrayList();
            dao.insert(insertSql.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 建立人员临时表
     * 
     * @param userView
     * @param conn
     * @return
     */
    public String tempEmpClassTable()
    {
    	// 33888 linbz 防止UserName存在空格建临时表失败，提前处理
        String table_name = "t#" + userView.getUserName().trim() + "_kq_cl";
        table_name = table_name.toLowerCase();
        DbWizard dbWizard = new DbWizard(conn);
        Table table = new Table(table_name);
        if (dbWizard.isExistTable(table_name, false))
        {
            dropTable(table_name);
        }
        Field temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(true);
        temp.setNullable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("A0000", "顺序号");
        temp.setDatatype(DataType.INT);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setNullable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("A0100", "人员编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(true);
        temp.setNullable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("B0110", "单位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("E0122", "部门");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("E01A1", "职位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("A0101", "姓名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("class_id", "班次编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("flag", "标志");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("group_id", "组编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("cardno", "考勤卡号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("g_no", "工号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        try
        {
            dbWizard.createTable(table);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        /** 重新加载数据模型 */

        DBMetaModel dbmodel = new DBMetaModel(this.conn);
        dbmodel.reloadTableModel(table_name);
        return table_name;
    }
}
