package com.hjsj.hrms.businessobject.kq.register.pigeonhole;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Pigeonhole
{
    private Connection conn;
    private UserView userView;

    public Pigeonhole()
    {

    }

    public Pigeonhole(Connection conn, UserView userView)
    {
        this.conn = conn;
        this.userView = userView;
    }

    public void saveTempData()
    {

    }

    public void insertInitDestData(String srcData_table, String setlist, String kq_duration, String nbase, String kq_date, String username, String num) throws GeneralException
    {

        String dest_Table = nbase + setlist;
        String z0 = setlist + "z0";
        String z1 = setlist + "z1";
        
        StringBuffer insert = new StringBuffer();
        
        Calendar now = Calendar.getInstance();
        Date cur_d = now.getTime();
        
        String createTime = DateUtils.format(cur_d, "yyyy-MM-dd");
        String createTime_str = Sql_switcher.dateValue(createTime);
        String zo_date_str = Sql_switcher.dateValue(kq_date);
        
        insert.append("INSERT INTO " + dest_Table + " (A0100,i9999," + z0 + "," + z1 + ",CreateTime,CreateUserName)");
        insert.append(" select a0100,0," + zo_date_str + ",'" + num + "'," + createTime_str + ",'" + username + "'");
        insert.append(" from " + srcData_table + "");
        insert.append(" WHERE NOT EXISTS(SELECT * FROM " + dest_Table + "");
        insert.append(" where " + srcData_table + ".a0100=" + dest_Table + ".a0100");
        insert.append(" and " + srcData_table + ".q03z0='" + kq_duration + "'");
        insert.append(" and " + dest_Table + "." + z1 + "='" + num + "' and " + dest_Table + "." + z0 + "=" + zo_date_str + ")");
        
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        // System.out.println(insert.toString());
        String strJoin = dest_Table + ".A0100=" + srcData_table + ".A0100";// 关联串
                                                                            // xxx.field_name=yyyy.field_namex,....
        String strSet = dest_Table + "." + z0 + "=" + zo_date_str + "`" + dest_Table + "." + z1 + "='" + num + "'`" + dest_Table + ".CreateTime=" + createTime_str + "`" + dest_Table + ".CreateUserName='" + username + "'";// 更新串
                                                                                                                                                                                                                                // xxx.field_name=yyyy.field_namex,....
        String strDWhere = dest_Table + "." + z0 + "=" + zo_date_str + " and " + dest_Table + ".a0100 in (select a0100 from " + srcData_table + ")";// 更新目标的表过滤条件
        String strSWhere = "";// 源表的过滤条件
        String update = Sql_switcher.getUpdateSqlTwoTable(dest_Table, srcData_table, strJoin, strSet, strDWhere, strSWhere);
        update = KqUtilsClass.repairSqlTwoTable(srcData_table, strJoin, update, strDWhere, "");
        try
        {
            // System.out.println(update);
            dao.update(update);
            dao.insert(insert.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.pigeonhole.save.lost"), "", ""));
        }
    }

    /**
     * 按人员库初始化归档临时数据表
     * 
     * @param data_table
     * @param a0100list
     */
    public void insertInitTempData(String data_table, String kq_duration, String nbase, String column, String a0100) throws GeneralException
    {
        StringBuffer sql = new StringBuffer();
        KqUtilsClass.dropTable(conn, data_table);
        
        String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            switch (Sql_switcher.searchDbServer())
            {
                case Constant.MSSQL:
                {
                    sql.append("select " + column + " Into " + data_table + " from q05");
                    sql.append(" where q03z0='" + kq_duration + "'");
                    sql.append(" and nbase='" + nbase + "'");
                    if (a0100 != null && a0100.length() > 0)
                    {
                        sql.append(" and a0100='" + a0100 + "'");
                    }
                    else
                    {
                        sql.append(" and a0100 in(select " + nbase + "A01.a0100 " + whereIN + ")");
                    }
                    dao.update(sql.toString());
                    break;
                }
                case Constant.ORACEL:
                {
                    sql.append("Create Table " + data_table + " as select " + column + " from q05");
                    sql.append(" where q03z0='" + kq_duration + "'");
                    sql.append(" and nbase='" + nbase + "'");
                    if (a0100 != null && a0100.length() > 0)
                    {
                        sql.append(" and a0100='" + a0100 + "'");
                    }
                    else
                    {
                        sql.append(" and a0100 in(select " + nbase + "A01.a0100 " + whereIN + ")");
                    }
                    dao.update(sql.toString());
                    break;
                }
                case Constant.DB2:
                {
                    sql.append(" SELECT " + column + " from q05");
                    sql.append(" where q03z0='" + kq_duration + "'");
                    sql.append(" and nbase='" + nbase + "'");
                    if (a0100 != null && a0100.length() > 0)
                    {
                        sql.append(" and a0100='" + a0100 + "'");
                    }
                    else
                    {
                        sql.append(" and a0100 in(select " + nbase + "A01.a0100 " + whereIN + ")");
                    }
                    String execSql = "Create Table " + data_table + " AS (" + sql.toString() + " ) DEFINITION ONLY";
                    String insertSql = "INSERT INTO " + data_table + "" + sql.toString();
                    dao.update(execSql);
                    dao.update(insertSql);
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.pigeonhole.save.lost"), "", ""));
        }
    }

    /**
     * 清空数据表
     * 
     * @param table_name
     */
    public void deleteDataTable(String table_name)
    {
        String del = "delete from " + table_name;
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList list = new ArrayList();
        try
        {
            dao.delete(del, list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /***************************************************************************
     * 新建临时表的名字
     **************************************************************************/
    public String getTmpTableName(String UserId, String PrivCode)
    {
        StringBuffer tablename = new StringBuffer();
        tablename.append("kq");
        tablename.append("_");
        tablename.append(PrivCode);
        tablename.append("_");
        tablename.append(UserId);
        return tablename.toString();
    }

    /***************************************************************************
     * 处理I9999字段
     **************************************************************************/
    public void updateI9999(String nbase, String setlist, String date, String num, boolean bI9999) throws GeneralException
    {
        StringBuffer update = new StringBuffer();
        String dest_Table = nbase + setlist;
        String z0 = setlist + "z0";
        String z1 = setlist + "z1";
        String zo_date_str = Sql_switcher.dateValue(date);
        /*
         * delete.append("delete from "+dest_Table+" where i9999=");
         * //delete.append("(select "+isNull+"+1 AS I9999 from "+dest_Table+"
         * A"); //delete.append(" WHERE A.A0100="+dest_Table+".A0100 and
         * I9999=0)"); delete.append("delect from "+dest_Table+" where ");
         * delete.append(" EXISTS(SELECT * FROM "+dest_Table+" B Where
         * "+dest_Table+".A0100=B.A0100"); delete.append(" And B."+z0+" =
         * "+dest_Table+"."+z0+" And B."+z1+" = "+dest_Table+"."+z1);
         */
        switch (Sql_switcher.searchDbServer())
        {
            case Constant.MSSQL:
            {
                update.append("UPDATE " + dest_Table + " SET I9999 = A.I9 ");
                update.append(" FROM (SELECT A0100, ISNULL( MAX(I9999) ,0)+1 AS I9 ");
                update.append(" FROM " + dest_Table + "  GROUP BY  A0100) A");
                update.append(" WHERE " + dest_Table + ".A0100=A.A0100 AND " + dest_Table + "." + z0 + "=" + zo_date_str + " and " + dest_Table + "." + z1 + "='" + num + "'");
                if (bI9999) {
                    update.append(" AND " + dest_Table + ".I9999=0");
                }
                break;
            }
            case Constant.ORACEL:
            {
                update.append("UPDATE " + dest_Table + " SET I9999=");
                update.append("(select I9 from ");
                update.append("(SELECT a0100,NVL(MAX(I9999),0)+1 AS I9 FROM " + dest_Table + " A group by A0100) A");
                update.append(" WHERE A.A0100=" + dest_Table + ".A0100)");
                if (bI9999) {
                    update.append(" WHERE " + dest_Table + ".I9999=0");
                }
                break;
            }
            case Constant.DB2:
            {
                update.append("UPDATE " + dest_Table + " SET I9999=");
                update.append("(SELECT COALESCE(MAX(I9999),0)+1 AS I9 FROM " + dest_Table + " A ");
                update.append(" WHERE " + dest_Table + ".A0100=" + dest_Table + ".A0100 and A." + z0 + "=" + dest_Table + "." + z0 + ")");
                if (bI9999) {
                    update.append(" WHERE " + dest_Table + ".I9999=0");
                }
                break;
            }
        }
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            // ArrayList del_list=new ArrayList();
            // dao.delete(delete.toString(),del_list);
            dao.update(update.toString());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("", ResourceFactory.getProperty("kq.pigeonhole.save.lost"), "", ""));
        }
    }

    public void updateDestData(String temp_table_oper, String temp_table_data, String nbase, String setlist, String kq_date, String username, String num) throws GeneralException
    {
    	String scr_field = "";
    	String dest_field = "";
        StringBuffer sql = new StringBuffer();
        String dest_Table = nbase + setlist;
        String z0 = setlist + "z0";
        String z1 = setlist + "z1";
        // java.sql.Date z0_date=DateUtils.getSqlDate(kq_date,"yyyy-MM-dd");
        String zo_date_str = Sql_switcher.dateValue(kq_date);
        Calendar now = Calendar.getInstance();
        Date cur_d = now.getTime();
        String modTime = DateUtils.format(cur_d, "yyyy-MM-dd");
        String zo_date_str_str = Sql_switcher.dateValue(modTime);
        sql.append("select SrcFldType,SrcFldId,SrcFldName," + Sql_switcher.isnull("DestFldId", "'0'") + " as DestFldId,DestFldName");
        sql.append(" from " + temp_table_oper);
        switch (Sql_switcher.searchDbServer())
        {
            case Constant.MSSQL:
            {
                sql.append(" where DestFldId<>'0' and DestFldId<>''");
                break;
            }
            case Constant.ORACEL:
            {
                sql.append(" where DestFldId<>'0'");
                break;
            }
            case Constant.DB2:
            {
                sql.append(" where DestFldId<>'0'");
                break;
            }
        }
        String existWhr = " and DestFldId not in (select itemid from fielditem where fieldsetid = '"+setlist+"' and useflag = '1')";
        sql.append(existWhr);
        ArrayList scr_list = new ArrayList();
        ArrayList dest_list = new ArrayList();
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try
        {
            rs = dao.search(sql.toString());
            if (rs.next()) {
				String itemdesc = rs.getString("destfldname");
				throw new GeneralException("目的指标“" + itemdesc + "”未构库，无法进行归档！");
			}
            
            sql.setLength(sql.length() - existWhr.length());
            sql.append(" and DestFldId in (select itemid from fielditem where fieldsetid = '"+setlist+"' and useflag = '1')");
            rs = dao.search(sql.toString());
            while (rs.next())
            {
                String destfldid = rs.getString("destfldid");
                if (destfldid == null || destfldid.length() <= 0) {
                    continue;
                }
                if (destfldid.toLowerCase().indexOf(z1.toLowerCase()) != -1)
                {
                    continue;
                }
                scr_list.add(rs.getString("srcfldid"));
                dest_list.add(rs.getString("destfldid"));
            }
            switch (Sql_switcher.searchDbServer())
            {
                case Constant.MSSQL:
                {
                    for (int i = 0; i < dest_list.size(); i++)
                    {
                        scr_field = scr_list.get(i).toString();
                        dest_field = dest_list.get(i).toString();
                        StringBuffer updatesql = new StringBuffer();
                        if ("q03z0".equalsIgnoreCase(scr_field) && dest_field.toLowerCase().indexOf(z0.toLowerCase()) != -1)
                        {
                            updatesql.append("Update " + dest_Table + "");
                            updatesql.append(" SET " + dest_field + "=" + Sql_switcher.charToDate(zo_date_str) + ",ModTime=" + zo_date_str_str + ",ModUserName='" + username + "'");
                            updatesql.append(" FROM (SELECT " + temp_table_data + "." + scr_field + "," + temp_table_data + ".a0100");
                            updatesql.append(" FROM " + temp_table_data + ") A ");
                            updatesql.append(" WHERE " + dest_Table + ".a0100=A.a0100 AND " + dest_Table + "." + z0 + "=" + zo_date_str + "");
                            updatesql.append("  AND " + dest_Table + "." + z1 + "='" + num + "'");
                        }
                        else
                        {
                            updatesql.append("Update " + dest_Table + "");
                            updatesql.append(" SET " + dest_field + "=A." + scr_field + ",ModTime=" + zo_date_str_str + ",ModUserName='" + username + "'");
                            updatesql.append(" FROM (SELECT " + temp_table_data + "." + scr_field + "," + temp_table_data + ".a0100");
                            updatesql.append(" FROM " + temp_table_data + ") A ");
                            updatesql.append(" WHERE " + dest_Table + ".a0100=A.a0100 AND " + dest_Table + "." + z0 + "=" + zo_date_str + "");
                            updatesql.append("  AND " + dest_Table + "." + z1 + "='" + num + "'");
                        }
                        dao.update(updatesql.toString());
                    }
                    break;
                }
                case Constant.ORACEL:
                {
                    for (int i = 0; i < dest_list.size(); i++)
                    {
                        scr_field = scr_list.get(i).toString();
                        dest_field = dest_list.get(i).toString();
                        StringBuffer updatesql = new StringBuffer();
                        if ("q03z0".equalsIgnoreCase(scr_field) && dest_field.toLowerCase().indexOf(z0.toLowerCase()) != -1)
                        {
                            /*
                             * updatesql.append("Update "+dest_Table+"");
                             * updatesql.append(" SET
                             * "+dest_field+"="+Sql_switcher.charToDate(zo_date_str)+",ModTime="+zo_date_str_str+",ModUserName='"+username+"'");
                             * updatesql.append(" FROM (SELECT
                             * "+temp_table_data+"."+scr_field+","+temp_table_data+".a0100");
                             * updatesql.append(" FROM "+temp_table_data+") A
                             * "); updatesql.append(" WHERE
                             * "+dest_Table+".a0100=A.a0100 AND
                             * "+dest_Table+"."+z0+"="+zo_date_str+"");
                             * updatesql.append(" AND
                             * "+dest_Table+"."+z1+"='"+num+"'");
                             */
                            updatesql.append("update " + dest_Table + "");
                            updatesql.append(" set (" + dest_field + ",ModTime,ModUserName)=");
                            updatesql.append(" (select " + zo_date_str + "," + zo_date_str_str + ",'" + username + "' from " + temp_table_data + "");
                            updatesql.append(" where " + dest_Table + ".a0100=" + temp_table_data + ".a0100)");

                        }
                        else
                        {
                            updatesql.append("update " + dest_Table + "");
                            updatesql.append(" set (" + dest_field + ")=");
                            updatesql.append(" (select " + scr_field + " from " + temp_table_data + "");
                            updatesql.append(" where " + dest_Table + ".a0100=" + temp_table_data + ".a0100)");
                        }
                        updatesql.append(" where " + dest_Table + "." + z0 + "=" + zo_date_str + "");
                        updatesql.append(" AND " + dest_Table + "." + z1 + "='" + num + "'");
                        updatesql.append(" and  EXISTS(select a0100 from  " + temp_table_data + " where " + dest_Table + ".a0100=" + temp_table_data + ".a0100)");
                        dao.update(updatesql.toString());
                    }
                    break;
                }
                case Constant.DB2:
                {
                    for (int i = 0; i < dest_list.size(); i++)
                    {
                        scr_field = scr_list.get(i).toString();
                        dest_field = dest_list.get(i).toString();
                        StringBuffer updatesql = new StringBuffer();
                        updatesql.append("update " + dest_Table + "");
                        updatesql.append(" set (" + dest_field + ")=");
                        updatesql.append(" (select " + scr_field + " from " + temp_table_data + "");
                        updatesql.append(" where " + dest_Table + ".a0100=" + temp_table_data + ".a0100)");
                        updatesql.append(" where " + dest_Table + "." + z0 + "=" + zo_date_str + "");
                        updatesql.append(" AND " + dest_Table + "." + z1 + "='" + num + "'");
                        dao.update(updatesql.toString());
                    }
                    break;
                }
            }
            // dao.batchUpdate(update_list);
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            if (sw.toString().indexOf("算术溢出错误") != -1 || sw.toString().indexOf("允许精度") != -1 || sw.toString().indexOf("数据类型") != -1) {//sql/oracle
            	String  destDesc = DataDictionary.getFieldItem(dest_field).getItemdesc();
            	String srcDesc = DataDictionary.getFieldItem(scr_field).getItemdesc();
				throw new GeneralException("归档失败！请检查：<br>考勤子集指标【"+destDesc+"(" + dest_field + ")】长度是否小于汇总指标【"+srcDesc+"(" + scr_field + ")】的长度，<br>以及二者数据类型是否一致！");
			} else {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
        }
        finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
    }

    /** ****************以下是信息临时表**************** */
    /**
     * 建立临时表
     * 
     * @return
     */
    public String createTempTable(String userName)
    {
        String tablename = RegisterInitInfoData.getTmpTableName(userName, "arch_f");
        DbWizard dbWizard = new DbWizard(this.conn);
        
        KqUtilsClass.dropTable(conn, tablename);
       
        Table table = new Table(tablename);
        
        Field temp6 = new Field("SrcFldType", "原表指标类型");
        temp6.setDatatype(DataType.STRING);
        temp6.setLength(2);
        temp6.setKeyable(false);
        temp6.setVisible(false);
        table.addField(temp6);
        Field temp5 = new Field("SrcFldId", "原表的字段代码");
        temp5.setDatatype(DataType.STRING);
        temp5.setLength(50);
        temp5.setKeyable(false);
        temp5.setVisible(false);
        table.addField(temp5);
        Field temp4 = new Field("SrcFldName", ResourceFactory.getProperty("kq.pigeonhole.srcfldname"));
        temp4.setDatatype(DataType.STRING);
        temp4.setLength(50);
        temp4.setKeyable(false);
        temp4.setVisible(false);
        table.addField(temp4);
        Field temp3 = new Field("SrcCodeSet", "原表对应代码类");
        temp3.setDatatype(DataType.STRING);
        temp3.setLength(50);
        temp3.setKeyable(false);
        temp3.setVisible(false);
        table.addField(temp3);
        Field temp1 = new Field("DestFldId", "归档字段代码");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(20);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        table.addField(temp1);
        Field temp = new Field("DestFldName", ResourceFactory.getProperty("kq.pigeonhole.destfldname"));
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        Field temp2 = new Field("DestCodeSet", "归档对应代码类");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(2);
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
        }
        return tablename;
    }

    /**
     * 给归档临时表中插入数据
     * 
     * @param temp_table
     */
    public void insertActivPigeonhole(String temp_table)
    {
        ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        ArrayList list = new ArrayList();
        for (int i = 0; i < fielditemlist.size(); i++)
        {
            ArrayList one_list = new ArrayList();
            FieldItem fielditem = (FieldItem) fielditemlist.get(i);
            String itemId = fielditem.getItemid();
            //30223 linbz 除序号、人员库、人员编号、姓名外其他指标都可归档
            if ("i9999".equalsIgnoreCase(itemId) || "nbase".equalsIgnoreCase(itemId) 
            		|| "a0100".equalsIgnoreCase(itemId) || "a0101".equalsIgnoreCase(itemId)) {
                continue;
            }
            
            one_list.add(fielditem.getItemtype());
            one_list.add(fielditem.getItemid());
            one_list.add(fielditem.getItemdesc());
            one_list.add(fielditem.getCodesetid());
            if ("A".equals(fielditem.getItemtype()) && "q03z0".equals(fielditem.getItemid()))
            {
                //工作日期放在第一行
                list.add(0, one_list);
            }else{
            	list.add(one_list);
            }

        }
        StringBuffer insert = new StringBuffer();
        insert.append("insert into " + temp_table + " (SrcFldType,SrcFldId,SrcFldName,SrcCodeSet)");
        insert.append(" values (?,?,?,?)");
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            dao.batchInsert(insert.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 修改归档方案的纪录
     * 
     * @param table_name 归档方案临时表名
     * @param list
     */
    public void updateActivPigeonhole(String table_name, ArrayList list)
    {

        if (list != null && list.size() > 0)
        {
            StringBuffer update = new StringBuffer();
            update.append("update " + table_name + " set");
            update.append(" DestFldName=?,DestFldId=?,DestCodeSet=?");
            update.append(" where UPPER(SrcFldId)=?");
            ContentDAO dao = new ContentDAO(this.conn);
            try
            {
                dao.batchUpdate(update.toString(), list);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    /**
     * 得到归档方案表里的数据,添加到BS归档方案临时表里面
     * 
     */
    public void delectInIt(String nbase, String setlist, String date, String num, String data_table_name)
    {
        StringBuffer delete = new StringBuffer();
        String dest_Table = nbase + setlist;
        String z0 = setlist + "z0";
        String z1 = setlist + "z1";
        String zo_date_str = Sql_switcher.dateValue(date);
        delete.append("delete from " + dest_Table + " where 1=1");
        // delete.append(" EXISTS(SELECT * FROM "+dest_Table+" B Where
        // "+dest_Table+".A0100=B.A0100");
        delete.append(" And " + z0 + " = " + zo_date_str + " And " + z1 + " = " + num + " and a0100 in (select a0100 from data_table_name)");
        try
        {
            ContentDAO dao = new ContentDAO(this.conn);
            ArrayList list = new ArrayList();
            dao.update(delete.toString(), list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 检查归档对应指标是否合法（指标是否存在、类型是否一致，暂不检查长度一致性）
     * @Title: archiveItemsValidate   
     * @Description:    
     * @param fieldList
     * @param destSet
     * @return 返回非法信息描述，如为“”，则表示检查通过
     */
    public String archiveItemsValidate(ArrayList fieldList, String destSet) {
        StringBuilder validateMsg = new StringBuilder();
        
        for (int i = 0; i < fieldList.size(); i++) {
            LazyDynaBean dbean = (LazyDynaBean) fieldList.get(i);
            String srcFldId = (String) dbean.get("srcfldid");
            String destFldId = (String) dbean.get("destfldid");
            String destFldName = (String)dbean.get("destfldname");
            if (destFldId != null && destFldId.length() > 0 && destFldName != null && destFldName.length() > 0) {
                FieldItem srcItem = DataDictionary.getFieldItem(srcFldId, "Q03");
                if (null == srcItem) {
                    validateMsg.append("<br>考勤月汇总指标【").append(srcFldId).append("】不存在！");
                }
                
                FieldItem destItem = DataDictionary.getFieldItem(destFldId, destSet);
                if (null == destItem) { 
                    validateMsg.append("<br>");
                    validateMsg.append("月汇总指标【");
                    
                    if (null == srcItem) {
                        validateMsg.append(srcFldId);
                    } else {
                        validateMsg.append(srcItem.getItemdesc());
                    }
                    
                    validateMsg.append("】对应的目标指标【").append(destFldId).append("】不存在！");
                }
                
                if (null == srcItem || null == destItem) {
                    continue;
                }
                    
                //类型相同检查指标长度 目标指标长度不能小于源指标长度
                if (srcItem.getItemtype().equalsIgnoreCase(destItem.getItemtype())) {
                    /* 暂不检查长度问题，因为只要实际的数据不超过目标指标的长度也可以归档成功
                    if (srcItem.getItemlength() > destItem.getItemlength()) {
                        validateMsg.append("<br>目标指标").append(destItem.getItemdesc()).append("的长度不能小于源指标")
                            .append(srcItem.getItemdesc()).append("的长度！");
                        continue;
                    }
                        
                    //数值型指标还需检查小数位的长度
                    if ("N".equals(srcItem.getItemtype())) {
                        if (srcItem.getDecimalwidth() > destItem.getDecimalwidth()) 
                            validateMsg.append("<br>目标指标").append(destItem.getItemdesc()).append("的小数位长度不能小于源指标")
                                .append(srcItem.getItemdesc()).append("的小数位长度！");
                        continue;
                    }
                    */
                } else {
                    //月汇总归档时，考勤月度（工作日期）指标特殊处理，既可以对应字符型指标，也可以对应日期型指标
                    if ("Q03Z0".equalsIgnoreCase(srcFldId) 
                            && ("A".equalsIgnoreCase(destItem.getItemtype()) || "D".equalsIgnoreCase(destItem.getItemtype()))) {
                        continue;
                    }
                    
                    //指标类型不同
                    validateMsg.append("<br>目标指标").append(destItem.getItemdesc()).append("的类型(")
                        .append(getItemTypeName(destItem.getItemtype())).append(")与源指标")
                        .append(getItemTypeName(srcItem.getItemtype())).append("(")
                        .append(srcItem.getItemdesc()).append(")的类型不同！");
                }
            }
        }
        
        return validateMsg.toString();
    }
    
    private String getItemTypeName(String itemType) {
        String typeName = "未知";
        if ("A".equalsIgnoreCase(itemType)) {
            typeName = "字符型";
        } else if ("N".equalsIgnoreCase(itemType)) {
            typeName = "数值型";
        } else if ("D".equalsIgnoreCase(itemType)) {
            typeName = "日期型";
        } else if ("M".equalsIgnoreCase(itemType)) {
            typeName = "备注型";
        }
        
        return typeName;
    }
}
