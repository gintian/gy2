package com.hjsj.hrms.businessobject.kq.register.statfx;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

/**
 * 统计分析
 * @author Owner
 * wangyao
 */
public class RegisterStatBo {
    /**
     * 统计分析 头展现根据  kq_parameter 展现
     * @param kq_param 字段
     * @param conn
     * @return
     */
    public static ArrayList savekqq03list(String kq_param, Connection conn, ArrayList fielditemlist) {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        RowSet rowSet = null;
        String content = ""; //保存指标
        ContentDAO dao = new ContentDAO(conn);
        try {
            sql.append("select content from kq_parameter where B0110='UN' and UPPER(name) = '" + kq_param + "'");
            rowSet = dao.search(sql.toString());
            while (rowSet.next()) {
                content = rowSet.getString("content");
            }
            String[] con = content.split(",");
            for (int j = 0; j < fielditemlist.size(); j++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(j);
                for (int i = 0; i < con.length; i++) {
                    if (con[i].equalsIgnoreCase(fielditem.getItemid())) {
                        list.add(fielditem.clone());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return list;
    }

    public static ArrayList setnamelist(String kqname, Connection conn, ArrayList fielditemlist) {
        ArrayList list = new ArrayList();
        try {
            for (int j = 0; j < fielditemlist.size(); j++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(j);
                if ("b0110".equalsIgnoreCase(fielditem.getItemid()) || "e0122".equalsIgnoreCase(fielditem.getItemid())
                        || "a0101".equalsIgnoreCase(fielditem.getItemid()) || kqname.equalsIgnoreCase(fielditem.getItemid())) {
                    list.add(fielditem.clone());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList newFieldItemListQ09(ArrayList list, String codesetid) {
        FieldItem fielditem = new FieldItem();
        fielditem.setFieldsetid("Q09");
        fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.deptname"));
        fielditem.setItemid("b0110");
        fielditem.setItemtype("A");
        fielditem.setCodesetid(codesetid);
        fielditem.setVisible(true);
        list.add(0, fielditem);

        FieldItem fielditem1 = new FieldItem();
        fielditem1.setFieldsetid("Q09");
        fielditem1.setItemdesc(ResourceFactory.getProperty("kq.register.codesetid"));
        fielditem1.setItemid("setid");
        fielditem1.setItemtype("A");
        fielditem1.setCodesetid("0");
        fielditem1.setVisible(false);
        list.add(fielditem1);

        return list;
    }

    public static ArrayList getSqlstrHistory(ArrayList fieldsetlist, String b0110s, String cur_date, String tablename,
            String userOrgId, String codeid, String B0110z, Connection conn, String file) {
        StringBuffer wheresql = new StringBuffer();
        StringBuffer condition = new StringBuffer();//打印高级花名册的条件
        StringBuffer sql2 = new StringBuffer();
        ArrayList list = new ArrayList();
        ContentDAO dao = new ContentDAO(conn);
        RowSet rowSet = null;
        //生成没有高级条件的from后的sql语句
        StringBuffer column = new StringBuffer();
        try {
            for (int i = 0; i < fieldsetlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fieldsetlist.get(i);
                column.append(fielditem.getItemid() + ",");
            }
            
            StringBuffer historyWhr = new StringBuffer();
            historyWhr.append(Sql_switcher.dateValue(DateStyle.dateformat(new Date(),"yyyy-MM-dd")));
            historyWhr.append(" between organization.start_date and organization.end_date ");
            
            StringBuffer haveChildSQL = new StringBuffer();
            haveChildSQL.append(" select codesetid from organization where parentid=? and codesetid<>'@K'");
            haveChildSQL.append(" and ").append(historyWhr.toString());
            
            ArrayList<String> sqlParams = new ArrayList<String>();
            
            int l = column.toString().length() - 1;
            String columnstr = column.toString().substring(0, l);
            String sqlstr = "select " + columnstr + " ";
            wheresql.append(" from " + tablename + ",organization where ");
            condition.append("  Q03Z0 ='" + cur_date + "'");
            if (codeid != null && !"".equals(codeid)) {
                if (userOrgId != null && !"".equals(userOrgId)) {
                    sqlParams.clear();
                    sqlParams.add(codeid);                    
                    rowSet = dao.search(haveChildSQL.toString(), sqlParams);
                    if (rowSet.next()) {
                        if (file == null) {
                            file = "1";
                        }
                        if ("0".equalsIgnoreCase(file)) {
                            condition
                                    .append(" and exists(select * from organization where parentid='"
                                            + codeid
                                            + "' and codesetid!='@K' and codeitemid!= parentid and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                        } else {
                            condition
                                    .append(" and exists(select * from organization where codeitemid='"
                                            + codeid
                                            + "' and codesetid!='@K' and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                        }
                    } else {
                        condition
                                .append(" and exists(select * from organization where codeitemid='"
                                        + codeid
                                        + "' and codesetid!='@K' and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                    }
                } else {
                    sqlParams.clear();
                    sqlParams.add(codeid);                    
                    rowSet = dao.search(haveChildSQL.toString(), sqlParams);
                    if (rowSet.next()) {
                        condition
                                .append(" and exists(select * from organization where parentid='"
                                        + codeid
                                        + "' and codesetid!='@K' and codeitemid!= parentid and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                    } else {
                        condition
                                .append(" and exists(select * from organization where codeitemid='"
                                        + codeid
                                        + "' and codesetid!='@K' and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                    }
                }
            } else {
                if (userOrgId != null && !"".equals(userOrgId)) {
                    sqlParams.clear();
                    sqlParams.add(userOrgId);                    
                    rowSet = dao.search(haveChildSQL.toString(), sqlParams);
                    if (rowSet.next()) {
                        if (codeid == null || "".equals(codeid)) {
                            condition
                                    .append(" and exists(select * from organization where codeitemid='"
                                            + userOrgId
                                            + "' and codesetid!='@K' and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                        } else {
                            condition
                                    .append(" and exists(select * from organization where parentid='"
                                            + userOrgId
                                            + "' and codesetid!='@K' and codeitemid!= parentid and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                        }
                    } else {
                        condition
                                .append(" and exists(select * from organization where codeitemid='"
                                        + userOrgId
                                        + "' and codesetid!='@K' and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                    }
                } else {
                    sqlParams.clear();
                    sqlParams.add(B0110z);                    
                    rowSet = dao.search(haveChildSQL.toString(), sqlParams);
                    if (rowSet.next()) {
                        //                          condition.append(" and exists(select * from organization where parentid='"+B0110z+"' and codesetid!='@K' and codeitemid!= parentid and organization.codeitemid=Q09.b0110)");
                        condition
                                .append(" and exists(select * from organization where grade='1' and codesetid!='@K' and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                    } else {
                        condition
                                .append(" and exists(select * from organization where codeitemid='"
                                        + B0110z
                                        + "' and codesetid!='@K' and organization.codeitemid=Q09.b0110) and Q09.b0110=organization.codeitemid ");
                    }
                }
            }

            wheresql.append(" " + condition.toString());
            wheresql.append(" and ").append(historyWhr.toString());

            list.add(0, sqlstr);
            list.add(1, wheresql.toString());
            
            list.add(2, "order by organization.A0000");
            list.add(3, columnstr);
            list.add(4, condition.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return list;
    }

    /**
     * 
     * @param time
     * @param b0110
     * @param itemid
     * @param Q03
     * @return
     */
    public static ArrayList Sevsetname(String time, String b0110, String itemid, String Q03, ArrayList kqq03list, String cur_co,
            String codesetidvalue, String empPrivWhr) {

        StringBuffer wheresql = new StringBuffer();
        ArrayList list = new ArrayList();
        cur_co = cur_co + "-PT";
        StringBuffer column = new StringBuffer();
        for (int i = 0; i < kqq03list.size(); i++) {
            FieldItem fielditem = (FieldItem) kqq03list.get(i);

            column.append(fielditem.getItemid() + ",");
        }
        int l = column.toString().length() - 1;
        String columnstr = column.toString().substring(0, l);
        columnstr += ",a0100,scope,nbase";
        String sqlstr = "select b0110,e0122,A0101," + itemid + ",a0100,scope,nbase ";
        wheresql.append(" from " + Q03 + " where ");
        if ("UM".equals(codesetidvalue)) {
            wheresql.append(" e0122 like '" + b0110 + "%' and q03z0 like '" + cur_co + "%' and scope='" + time + "'");
        } else {
            wheresql.append(" b0110 like '" + b0110 + "%' and q03z0 like '" + cur_co + "%' and scope='" + time + "'");
        }
        wheresql.append(" and " + itemid + ">0 ");
        
        //zxj 20160504 增加人员权限
        if(empPrivWhr != null && !"".equals(empPrivWhr)) {
            wheresql.append(" AND ").append(empPrivWhr);
        }
        
        list.add(0, sqlstr);
        list.add(1, wheresql.toString());
        //          list.add(2,"group by a0100 order by b0110");
        list.add(2, "order by b0110,e0122,a0100,nbase");
        list.add(3, columnstr);
        return list;
    }

    /**
     * 
     * @param scope
     * @param a0100
     * @param usernbase
     * @param item_id  类型
     * @param b0110
     * @param getTable 表名
     * @return
     */
    public ArrayList getTableList(String scope, String a0100, String usernbase, String item_id, String b0110, String getTable) {
        ArrayList list = new ArrayList();
        String kstime = scope.substring(0, 10);
        String jstime = scope.substring(scope.length() - 10, scope.length());
        
        String dateTimeFormat = "yyyy-mm-dd hh:mi";
        if(Sql_switcher.searchDbServer() != com.hrms.hjsj.sys.Constant.MSSQL) {
            dateTimeFormat = "yyyy-mm-dd hh24:mi";
        }
        
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select b0110,e0122,A0101,").append(getTable).append("03,");
        sqlstr.append(Sql_switcher.dateToChar(getTable + "z1", dateTimeFormat)).append(" ").append(getTable).append("z1,");
        sqlstr.append(Sql_switcher.dateToChar(getTable + "z3", dateTimeFormat)).append(" ").append(getTable).append("z3");
        
        StringBuffer wheresql = new StringBuffer();
        wheresql.append(" from " + getTable);
        wheresql.append(" where nbase='" + usernbase + "'");
        wheresql.append(" and b0110='" + b0110 + "'");
        wheresql.append(" and a0100='" + a0100 + "'");
        wheresql.append(" and " + getTable + "03='" + item_id + "'");
        wheresql.append(" and ((" + getTable + "z1 between ");
        wheresql.append( Sql_switcher.dateValue(kstime + " 00:00:00"));
        wheresql.append(" and " + Sql_switcher.dateValue(jstime + " 23:59:59"));
        wheresql.append(") or (" + getTable + "z3 between ");
        wheresql.append( Sql_switcher.dateValue(kstime + " 00:00:00"));
        wheresql.append(" and " + Sql_switcher.dateValue(jstime + " 23:59:59"));
        wheresql.append(") or (" + getTable + "z1<=" + Sql_switcher.dateValue(kstime + " 00:00:00"));
        wheresql.append(" and " + getTable + "z3>=" + Sql_switcher.dateValue(jstime + " 23:59:59"));
        wheresql.append(")) and " + getTable + "z5='03'");
        if ("Q15".equalsIgnoreCase(getTable)) {
            wheresql.append(" and ").append(Sql_switcher.isnull("Q1517", "0")).append("<>1");
            wheresql.append(" and q1501 not in(select q1501 from q15 q");
            wheresql.append(" where exists(select * from q15 qq");
            wheresql.append(" where q.q15z1=qq.q15z1");
            wheresql.append(" and q.q15z3=qq.q15z3");
            wheresql.append(" and ").append(Sql_switcher.isnull("qq.Q1517", "0")).append("=1");
            wheresql.append(" and q.q1501=qq.q1519))");
        }
        
        String columnstr = "b0110,e0122,A0101," + getTable + "03," + getTable + "z1," + getTable + "z3";
        
        list.add(0, sqlstr.toString());
        list.add(1, wheresql.toString());
        list.add(2, "order by b0110,e0122,a0100," + getTable + "Z1");
        list.add(3, columnstr);
        return list;
    }

    /**
     * 
     * @param scope 时间
     * @param a0100
     * @param usernbase 人员库
     * @param itemid  指标
     * @param b0110
     * @return
     */
    public ArrayList getTableListnull(String scope, String a0100, String usernbase, String itemid, String b0110) {
        ArrayList list = new ArrayList();
        StringBuffer wheresql = new StringBuffer();
        String kstime = scope.substring(0, 10);
        String jstime = scope.substring(scope.length() - 10, scope.length());
        String sqlstr = "select q03z0,b0110,e0122,a0101," + itemid;
        wheresql.append(" from q03 where b0110='" + b0110 + "' and a0100='" + a0100 + "' and nbase='" + usernbase
                + "' and q03z0>='" + kstime + "' and q03z0<='" + jstime + "' and " + Sql_switcher.isnull(itemid, "'-1'")
                + " <>'-1' and " + itemid + ">'0'");
        String columnstr = "q03z0,b0110,e0122,a0101," + itemid;
        list.add(0, sqlstr);
        list.add(1, wheresql.toString());
        list.add(2, "order by q03z0,b0110,e0122");
        list.add(3, columnstr);
        return list;
    }

    /**
     * 在考勤规则表里去的 数据是从那张表中取得的
     * @param itemid
     * @return
     */
    public String getkq_item_sdata(String itemid, Connection conn) {
        String sdata_src = "";
        ContentDAO dao = new ContentDAO(conn);
        String sql = "select sdata_src from kq_item where upper(fielditemid)='" + itemid.toUpperCase() + "'";
        RowSet rowSet = null;
        try {
            rowSet = dao.search(sql);
            if (rowSet.next()) {
                sdata_src = rowSet.getString("sdata_src");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return sdata_src;
    }
}