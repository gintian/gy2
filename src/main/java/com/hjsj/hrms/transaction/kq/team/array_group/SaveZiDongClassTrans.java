package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 自动分配班组保存人员并且排班
 * @author Owner
 * wangyao
 */
public class SaveZiDongClassTrans extends IBusiness implements KqClassArrayConstant {

    public void execute() throws GeneralException {
        try {
            String group_id = (String) this.getFormHM().get("group_id"); //班组id
            String start_date = (String) this.getFormHM().get("start_date"); //开始时间
            String end_date = (String) this.getFormHM().get("end_date"); //结束时间
            String nbase = (String) this.getFormHM().get("nbase"); //库
            String a0100s = (String) this.getFormHM().get("a0100s");
            
            BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
            
            ArrayList date_list = baseClassShift.getDatelist(start_date, end_date); //时间list
            if (!"".equals(a0100s)) {
                deleteUserGroup(a0100s, nbase, group_id, start_date, end_date, date_list);
            }
            
            String a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
            String kind = "";
            String code = "";
            if (a_code == null || a_code.length() <= 0) {
                String privcode = RegisterInitInfoData.getKqPrivCode(userView);
                if ("UN".equalsIgnoreCase(privcode))
                    kind = "2";
                else if ("UM".equalsIgnoreCase(privcode))
                    kind = "1";
                else if ("@K".equalsIgnoreCase(privcode))
                    kind = "0";
                code = RegisterInitInfoData.getKqPrivCodeValue(userView);
            } else {
                if (a_code.indexOf("UN") != -1) {
                    kind = "2";
                } else if (a_code.indexOf("UM") != -1) {
                    kind = "1";
                } else if (a_code.indexOf("@K") != -1) {
                    kind = "0";
                }
                code = a_code.substring(2);
            }
            
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            ArrayList dblist = kqUtilsClass.getKqPreList();
            ArrayList kq_db_list = new ArrayList();
            if (nbase == null || "".equals(nbase)) {
                kq_db_list.add(dblist.get(0));
            } else if ("all".equals(nbase)) {
                kq_db_list = dblist;
            } else {
                kq_db_list.add(nbase);
            }
            //1.通过班组id得到班组名称
            String groupName = getGroupName(group_id);
            //2.通过人员主集中“班组指标”
            String a01Name = "";
            for (Iterator iterator = kq_db_list.iterator(); iterator.hasNext();) {
                String dbpre = (String) iterator.next();
                a01Name = geta01Nme(dbpre, groupName, group_id, date_list, start_date, end_date);
            }
            this.getFormHM().put("save_flag", a01Name);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    //1.通过班组id得到班组名称
    private String getGroupName(String group_id) {
        String name = "";
        String sql = "select name from kq_shift_group where group_id='" + group_id + "'";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            rowSet = dao.search(sql);
            while (rowSet.next()) {
                name = rowSet.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return name;
    }

    //2.通过人员主集中“班组指标”
    /**
     * nbase 人员库
     * groupName 班组的中文名称
     * group_id 班组ID
     * date_list 时间list
     */
    private String geta01Nme(String nbase, String groupName, String group_id, ArrayList date_list, String start_date,
            String end_date) {
        String name = "";
        String save_flag = "false";
        String contentid = ""; //对应ID
        String codesetid = "";
        
        start_date = start_date.replaceAll("-", "\\.");
        end_date = end_date.replaceAll("-", "\\.");
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rowSet = null;
        StringBuffer bug = new StringBuffer();
        try {
            /*
             * 第二步：根据主集中的排班指标进行排班
             * 主集有班次，但是人员班组对应表中没有排班这样人员进行排班
             */
            contentid = KqParam.getInstance().getShiftGroupItem();
            
            //找到名称
            bug.append("select itemdesc,codesetid from fielditem where fieldsetid='A01' and itemid='" + contentid + "'");
            rowSet = dao.search(bug.toString());
            if (rowSet.next()) {
                name = rowSet.getString("itemdesc");
                codesetid = rowSet.getString("codesetid");
            }
            //不等于 0 就是 代码型,通过codeitemid到usrA01里找一个一样的
            if (!"0".equalsIgnoreCase(codesetid)) {
                ArrayList list = new ArrayList();
                ArrayList selectedinfolist = new ArrayList();
                String codeitemid = "";
                
                bug.setLength(0);
                bug.append("select codeitemdesc,codeitemid");
                bug.append(" from codeitem");
                bug.append(" where codesetid='" + codesetid);
                bug.append("' and codeitemdesc='" + groupName + "'");
                rowSet = dao.search(bug.toString());
                if (rowSet.next()) {
                    codeitemid = rowSet.getString("codeitemid");
                }
                
                bug.setLength(0);
                
                //建立第二个临时表，人员；这个表中存放这主集为A组，并且这个人没有分配班组
                String date_name_table = creat_Name_Table(this.userView.getUserId());
                initializtion_date_name_Table(nbase, group_id, contentid, codeitemid, date_name_table);
                
                //将date_name_table表中查到的班组成员放入班组人员表中
                bug.append("insert into " + kq_group_emp_table);
                bug.append(" (a0100,nbase,group_id,a0101,b0110,e0122,e01a1)");
                bug.append(" select a0100,'" + nbase + "','" + group_id + "',a0101,b0110,e0122,e01a1");
                bug.append(" FROM " + date_name_table);
                dao.insert(bug.toString(), list);
                
                bug.setLength(0);
                bug.append("SELECT a0100,nbase,a0101,b0110,e0122,e01a1 FROM " + date_name_table);
                deleteA01SQL(nbase, group_id, start_date, end_date, date_name_table); //删除同主集表排班相同的
                rowSet = dao.search(bug.toString());
                while (rowSet.next()) {
                    LazyDynaBean bean = new LazyDynaBean();
                    String a0100 = rowSet.getString("a0100");
                    bean.set("a0100", a0100);
                    String nbase2 = rowSet.getString("nbase");
                    bean.set("nbase2", nbase2);
                    String e01a1 = rowSet.getString("e01a1"); //职位
                    if (e01a1 == null)
                        e01a1 = "";
                    bean.set("e01a1", e01a1);
                    String e0122 = rowSet.getString("e0122"); //部门
                    if (e0122 == null)
                        e0122 = "";
                    bean.set("e0122", e0122);
                    String b0110 = rowSet.getString("b0110"); //单位
                    if (b0110 == null)
                        b0110 = "";
                    bean.set("b0110", b0110);
                    String a0101 = rowSet.getString("a0101");//姓名
                    if (a0101 == null)
                        a0101 = "";
                    bean.set("a0101", a0101);
                    selectedinfolist.add(bean);
                }
                dropTable(date_name_table);
                for (int i = 0; i < selectedinfolist.size(); i++) {
                    LazyDynaBean bean = (LazyDynaBean) selectedinfolist.get(i);
                    saveInheritCalss(bean, date_list, group_id); //得到每个人进行排版
                }

                save_flag = "true";
            } else {
                bug.setLength(0);
                bug.append("select name from kq_shift_group where group_id='" + group_id + "'");
                rowSet = dao.search(bug.toString());
                if (rowSet.next()) {
                    name = rowSet.getString("name");
                }
                
                ArrayList list = new ArrayList();
                ArrayList selectedinfolist = new ArrayList();
                
                bug.setLength(0);
                //生成当前班组成员,根据 人员主集 中的contentid=codeitemid
                bug.append("insert into " + kq_group_emp_table);
                bug.append(" (a0100,nbase,group_id,a0101,b0110,e0122,e01a1)");
                bug.append(" select a0100,'" + nbase + "','" + group_id + "',a0101,b0110,e0122,e01a1 from " + nbase + "A01 ");
                bug.append("where " + contentid + "='" + name + "' and ");
                bug.append("NOT EXISTS(SELECT * FROM " + kq_group_emp_table + " where " + nbase + "A01.a0100="
                        + kq_group_emp_table + ".a0100 and " + kq_group_emp_table + ".nbase='" + nbase + "')");
                bug.append(" AND A0100 IN (SELECT A0100 " + RegisterInitInfoData.getWhereINSql(userView, nbase) + ")");
                dao.insert(bug.toString(), list);
                
                bug.setLength(0);
                //得到用户 放到 list
                bug.append(" select a0100,'" + nbase + "' as nbase,'" + group_id + "',a0101,b0110,e0122,e01a1 from " + nbase
                        + "A01 ");
                bug.append("where " + contentid + "='" + name + "' and ");
                bug.append("NOT EXISTS(SELECT * FROM " + kq_group_emp_table + " where " + nbase + "A01.a0100="
                        + kq_group_emp_table + ".a0100 and " + kq_group_emp_table + ".nbase='" + nbase + "')");
                bug.append(" AND A0100 IN (SELECT A0100 " + RegisterInitInfoData.getWhereINSql(userView, nbase) + ")");
                rowSet = dao.search(bug.toString());
                while (rowSet.next()) {
                    LazyDynaBean bean = new LazyDynaBean();
                    String a0100 = rowSet.getString("a0100");
                    bean.set("a0100", a0100);
                    String nbase2 = rowSet.getString("nbase");
                    bean.set("nbase2", nbase2);
                    String e01a1 = rowSet.getString("e01a1"); //职位
                    if (e01a1 == null)
                        e01a1 = "";
                    bean.set("e01a1", e01a1);
                    String e0122 = rowSet.getString("e0122"); //部门
                    if (e0122 == null)
                        e0122 = "";
                    bean.set("e0122", e0122);
                    String b0110 = rowSet.getString("b0110"); //单位
                    if (b0110 == null)
                        b0110 = "";
                    bean.set("b0110", b0110);
                    String a0101 = rowSet.getString("a0101");//姓名
                    if (a0101 == null)
                        a0101 = "";
                    bean.set("a0101", a0101);
                    selectedinfolist.add(bean);
                }
                
                for (int i = 0; i < selectedinfolist.size(); i++) {
                    LazyDynaBean bean = (LazyDynaBean) selectedinfolist.get(i);
                    saveInheritCalss(bean, date_list, group_id); //得到每个人进行排版
                }

                save_flag = "true";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return save_flag;
    }

    /**
     * 建立时间临时表
     * @param userid
     * @return
     * @throws GeneralException
     */
    private String creat_KqTmp_Table(String userid) throws GeneralException {
        String tablename = getTmpTableName(this.userView.getUserName(), RegisterInitInfoData.getKqPrivCode(userView));
        DbWizard dbWizard = new DbWizard(this.getFrameconn());
        Table table = new Table(tablename);
        if (dbWizard.isExistTable(tablename, false)) {
            dropTable(tablename);
        }
        Field temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        Field temp1 = new Field("a0100", "人员编号");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(10);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        table.addField(temp1);
        Field temp2 = new Field("sDate", "考勤日期");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(20);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        table.addField(temp2);
        Field temp3 = new Field("b0110", "单位");
        temp3.setDatatype(DataType.STRING);
        temp3.setLength(30);
        temp3.setKeyable(false);
        temp3.setVisible(false);
        table.addField(temp3);
        Field temp4 = new Field("e0122", "部门");
        temp4.setDatatype(DataType.STRING);
        temp4.setLength(30);
        temp4.setKeyable(false);
        temp4.setVisible(false);
        table.addField(temp4);
        Field temp5 = new Field("e01a1", "职位");
        temp5.setDatatype(DataType.STRING);
        temp5.setLength(30);
        temp5.setKeyable(false);
        temp5.setVisible(false);
        table.addField(temp5);
        Field temp6 = new Field("a0101", "姓名");
        temp6.setDatatype(DataType.STRING);
        temp6.setLength(50);
        temp6.setKeyable(false);
        temp6.setVisible(false);
        table.addField(temp6);
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return tablename;
    }

    /**
    * 新建临时表的名字
    * **/
    private String getTmpTableName(String UserId, String PrivCode) {
        StringBuffer tablename = new StringBuffer();
        tablename.append("kqsequelclass");
        tablename.append("_");
        tablename.append(PrivCode);
        tablename.append("_");
        tablename.append(UserId);
        return tablename.toString();
    }

    /**
     * 删除临时表
     * @param tablename
     */
    private void dropTable(String tablename) {
        try {
            DbWizard dbWizard = new DbWizard(this.getFrameconn());
            dbWizard.dropTable(tablename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param nbase
     * @param group_id
     * @param start_date
     * @param end_date
     * @param table
     */
    private void deleteA01SQL(String nbase, String group_id, String start_date, String end_date, String table) {
        StringBuffer deleteSQL = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        deleteSQL.append("delete from kq_employ_shift where kq_employ_shift.nbase='" + nbase + "' and ");
        deleteSQL.append("kq_employ_shift.Q03Z0>='" + start_date + "' and kq_employ_shift.Q03Z0<='" + end_date + "' ");
        deleteSQL.append(" and kq_employ_shift.A0100 in (select A0100 from " + table + " where nbase='" + nbase + "')");
        try {
            dao.delete(deleteSQL.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给每个人建立
     * @param bean
     * @param date_list
     * group_id 班组ID
     */
    private void saveInheritCalss(LazyDynaBean bean, ArrayList date_list, String group_id) {
        try {
            String a0100 = (String) bean.get("a0100");
            String nbase = (String) bean.get("nbase2");
            String e01a1 = (String) bean.get("e01a1"); //职位
            String e0122 = (String) bean.get("e0122"); //部门
            String b0110 = (String) bean.get("b0110"); //单位
            String a0101 = (String) bean.get("a0101");//姓名
            String date_Table = creat_KqTmp_Table(this.userView.getUserId()); //建立第一个临时表
            initializtion_date_Table(date_list, date_Table, nbase, e01a1, e0122, b0110, a0100, a0101); //给每个人建立一个时间范围
            getbmtable(a0100, nbase, e01a1, e0122, b0110, a0101, date_Table, group_id); //排班 把人员放入 到员工排班信息
            dropTable(date_Table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断 为人员生成排班
     * @param a0100
     * @param nbase
     * @param e01a1
     * @param e0122
     * @param b0110
     * @param a0101
     * @param date_Table
     * group_id 班组ID
     */
    private void getbmtable(String a0100, String nbase, String e01a1, String e0122, String b0110, String a0101, String date_Table,
            String group_id) {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            StringBuffer insertSQL = new StringBuffer();
            insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
            insertSQL.append("SELECT a.nbase,a.A0100,a.a0101,a.b0110,a.e0122,a.e01a1,a.sDate,b.class_id,0 ");
            insertSQL.append(" FROM " + date_Table + " a,kq_org_dept_shift b");
            insertSQL.append(" WHERE b.q03z0=a.sDate and b.org_dept_id='" + group_id + "' and b.codesetid='@G'");
            ArrayList list = new ArrayList();
            dao.insert(insertSQL.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成初始时间表 初始数据
     * @param date_list
     * @param date_Table
     * @param nbase
     * @param e01a1
     * @param e0122
     * @param b0110
     * @param a0100
     * @return
     * @throws GeneralException
     */
    private ArrayList initializtion_date_Table(ArrayList date_list, String date_Table, String nbase, String e01a1, String e0122,
            String b0110, String a0100, String a0101) throws GeneralException {
        String deleteSQL = "delete from " + date_Table;
        ArrayList deletelist = new ArrayList();
        String insertSQL = "insert into " + date_Table + " (nbase,a0100,sDate,b0110,e0122,e01a1,a0101) values (?,?,?,?,?,?,?)";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList insertList = new ArrayList();
        try {
            dao.delete(deleteSQL, deletelist);
            for (int i = 0; i < date_list.size(); i++) {
                String cur_date = date_list.get(i).toString();
                ArrayList list = new ArrayList();
                list.add(nbase);
                list.add(a0100);
                list.add(cur_date);
                list.add(b0110);
                list.add(e0122);
                list.add(e01a1);
                list.add(a0101);
                insertList.add(list);
            }
            dao.batchInsert(insertSQL, insertList);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return insertList;
    }

    /**
     * 建立人员临时表
     * @param userid
     * @return
     * @throws GeneralException
     */
    private String creat_Name_Table(String userid) throws GeneralException {
        String tablename = getTmpTableName2(this.userView.getUserName(), RegisterInitInfoData.getKqPrivCode(userView));
        DbWizard dbWizard = new DbWizard(this.getFrameconn());
        Table table = new Table(tablename);
        if (dbWizard.isExistTable(tablename, false)) {
            dropTable(tablename);
        }
        Field temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        Field temp1 = new Field("a0100", "人员编号");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(10);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        table.addField(temp1);
        Field temp3 = new Field("b0110", "单位");
        temp3.setDatatype(DataType.STRING);
        temp3.setLength(30);
        temp3.setKeyable(false);
        temp3.setVisible(false);
        table.addField(temp3);
        Field temp4 = new Field("e0122", "部门");
        temp4.setDatatype(DataType.STRING);
        temp4.setLength(30);
        temp4.setKeyable(false);
        temp4.setVisible(false);
        table.addField(temp4);
        Field temp5 = new Field("e01a1", "岗位");
        temp5.setDatatype(DataType.STRING);
        temp5.setLength(30);
        temp5.setKeyable(false);
        temp5.setVisible(false);
        table.addField(temp5);
        Field temp6 = new Field("a0101", "姓名");
        temp6.setDatatype(DataType.STRING);
        temp6.setLength(50);
        temp6.setKeyable(false);
        temp6.setVisible(false);
        table.addField(temp6);
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return tablename;
    }

    /**
     * 新建临时表的名字
     * **/
    private static String getTmpTableName2(String UserId, String PrivCode) {
        StringBuffer tablename = new StringBuffer();
        tablename.append("kqclassname");
        tablename.append("_");
        tablename.append(PrivCode);
        tablename.append("_");
        tablename.append(UserId);
        return tablename.toString();
    }

    /**
     * 生成初始人员表 初始数据
     * @param date_list
     * @param date_Table
     * @param nbase
     * @param e01a1
     * @param e0122
     * @param b0110
     * @param a0100
     * @return 得出未在人员对应表中的人员
     * @throws GeneralException
     */
    private ArrayList initializtion_date_name_Table(String nbase, String group_id, String contentid, String codeitemid,
            String date_name_table) throws GeneralException {
        String deleteSQL = "delete from " + date_name_table;
        ArrayList deletelist = new ArrayList();
        StringBuffer bug = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList insertList = new ArrayList();
        try {
            KqUtilsClass kqUtils = new KqUtilsClass(this.frameconn, this.userView);
            String kqTypeWhr = kqUtils.getKqTypeWhere(KqConstant.KqType.STOP, true);
            
            //只更新A01主集，排班指标相同的；并且排除 已分配班组的人员
            dao.delete(deleteSQL, deletelist);
            bug.append("insert into " + date_name_table);
            bug.append(" (a0100,nbase,a0101,b0110,e0122,e01a1)");
            bug.append(" select a0100,'" + nbase + "',a0101,b0110,e0122,e01a1 from " + nbase + "A01 ");
            bug.append("where " + contentid + "='" + codeitemid + "' and ");
            bug.append("NOT EXISTS(SELECT * FROM " + kq_group_emp_table + " where " + nbase + "A01.a0100=" + kq_group_emp_table
                    + ".a0100 and " + kq_group_emp_table + ".nbase='" + nbase + "')");
            bug.append(" AND A0100 IN (SELECT a0100 " + RegisterInitInfoData.getWhereINSql(userView, nbase) + ")");
            
            //过滤掉暂停考勤人员
            bug.append(kqTypeWhr);
            
            dao.insert(bug.toString(), insertList);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return insertList;
    }

    private void deleteUserGroup(String a0100s, String nbase, String group_id, String start_date, String end_date,
            ArrayList date_list) {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        try {
            String[] emps = a0100s.split(",");
            
            for (int i = 0; i < emps.length; i++) {
                if (11 != emps[i].trim().length())
                    continue;
                    
                String aNbase = emps[i].trim().substring(0, 3);
                String a0100 = emps[i].trim().substring(3);
                ArrayList aEmpParam = new ArrayList();
                aEmpParam.add(aNbase);
                aEmpParam.add(a0100);
                list.add(aEmpParam);
            }
            
            if (0 == list.size()) 
                return;
            
            sql.append("DELETE FROM kq_group_emp WHERE nbase=? AND a0100=?");
            dao.batchUpdate(sql.toString(), list);
      /*      
            if ("all".equalsIgnoreCase(nbase))
			{	//szk全部人员库时遍历所有
            	StringBuffer nbasewhere=new StringBuffer();
            	KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
                String nbases = para.getNbase();
                String[] dbases = nbases.split(",");
                for (int i = 0; i < dbases.length; i++)
    			{
                	nbasewhere.append("nbase='"+dbases[i]+"' or ");
    			}
                if (nbasewhere.length() > 0)
                	nbasewhere.setLength(nbasewhere.length() - 4);
            	sql.append("delete from kq_group_emp where a0100 in (" + a0100s1 + ") and "+nbasewhere);
			}
            else {
            	sql.append("delete from kq_group_emp where a0100 in (" + a0100s1 + ") and nbase='" + nbase + "'");
			}
            dao.delete(sql.toString(), list);
            */
            
            for (int i = 0; i < list.size(); i++) {
                ArrayList emp = (ArrayList)list.get(i);
                    
                String aNbase = (String)emp.get(0);
                String a0100 = (String)emp.get(1);
                
                sql.setLength(0);
                sql.append("insert into " + kq_group_emp_table);
                sql.append(" (a0100,nbase,group_id,a0101,b0110,e0122)");
                sql.append(" select  a0100,'" + aNbase + "','" + group_id + "',a0101,b0110,e0122 ");
                sql.append(" from " + aNbase + "A01 where a0100='" + a0100 + "'");
                dao.update(sql.toString());
                
                //第一步：删除每个人传入的时间段的排班信息,删除员工排班信息
                deleteSQL(aNbase, a0100, start_date, end_date);
                //第二步:对加入进来的人进行排班
                saveInheritCalss2(aNbase, a0100, group_id, date_list);
            }
            /*
            for (int i = 0; i < n_a0100s.length; i++) {
                String n_a0100 = n_a0100s[i];
                String id = n_a0100;
                //szk根据a0100获取人员库
                KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
                String lnbase = kqUtilsClass.getNbaseByA0100(id);
                StringBuffer insetSQL = new StringBuffer();
                insetSQL.append("insert into " + kq_group_emp_table);
                insetSQL.append(" (a0100,nbase,group_id,a0101,b0110,e0122)");
                insetSQL.append(" select  a0100,'" + lnbase + "','" + group_id + "',a0101,b0110,e0122 ");
                insetSQL.append(" from " + lnbase + "A01 where a0100='" + id + "'");
                insetSQL.append(" and NOT EXISTS(SELECT * FROM " + kq_group_emp_table + "");
                insetSQL.append(" where a0100='" + id + "' and nbase='" + lnbase + "')");
                dao.insert(insetSQL.toString(), list);
                //第一步：删除每个人传入的时间段的排班信息,删除员工排班信息
                deleteSQL(lnbase, id, start_date, end_date);
                //第二步:对加入进来的人进行排班
                saveInheritCalss2(lnbase, id, group_id, date_list);
            }
            */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除每个人传入的时间段的排班信息
     * @param nbase
     * @param a0100
     * @param start_date
     * @param end_date
     */
    private void deleteSQL(String nbase, String a0100, String start_date, String end_date) {
        StringBuffer deleteSQL = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        start_date = start_date.replaceAll("-", "\\.");
        end_date = end_date.replaceAll("-", "\\.");
        deleteSQL.append("delete  from kq_employ_shift where nbase='" + nbase + "'");
        deleteSQL.append(" and a0100='" + a0100 + "' and Q03Z0>='" + start_date + "' and Q03Z0<='" + end_date + "'");
        try {
            dao.delete(deleteSQL.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第二步:对加入进来的人进行排班
     * @param nbase
     * @param a0100
     * @param group_id 班组
     * @param date_list 时间list
     */
    private void saveInheritCalss2(String nbase, String a0100id, String group_id, ArrayList date_list) {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rowSet = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select a0100,nbase,a0101,b0110,e0122,e01a1 from kq_group_emp where nbase='" + nbase + "'");
        sql.append(" and a0100='" + a0100id + "' and group_id='" + group_id + "'");
        try {
            rowSet = dao.search(sql.toString());
            ArrayList listGroup = new ArrayList();
            while (rowSet.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                String a0100 = (String) rowSet.getString("a0100");
                bean.set("a0100", a0100);
                String nbase2 = (String) rowSet.getString("nbase");
                bean.set("nbase2", nbase2);
                String e01a1 = (String) rowSet.getString("e01a1"); //职位
                if (e01a1 == null)
                    e01a1 = "";
                bean.set("e01a1", e01a1);
                String e0122 = (String) rowSet.getString("e0122"); //部门
                if (e0122 == null)
                    e0122 = "";
                bean.set("e0122", e0122);
                String b0110 = (String) rowSet.getString("b0110"); //单位
                if (b0110 == null)
                    b0110 = "";
                bean.set("b0110", b0110);
                String a0101 = (String) rowSet.getString("a0101");//姓名
                if (a0101 == null)
                    a0101 = "";
                bean.set("a0101", a0101);
                listGroup.add(bean);
            }
            for (int i = 0; i < listGroup.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) listGroup.get(i);
                saveUserClass(bean, date_list, group_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
    }

    /**
     * 给每个人建立
     * @param bean
     * @param date_list
     * @param group_id
     */
    private void saveUserClass(LazyDynaBean bean, ArrayList date_list, String group_id) {
        try {
            String a0100 = (String) bean.get("a0100");
            String nbase = (String) bean.get("nbase2");
            String e01a1 = (String) bean.get("e01a1"); //职位
            String e0122 = (String) bean.get("e0122"); //部门
            String b0110 = (String) bean.get("b0110"); //单位
            String a0101 = (String) bean.get("a0101");//姓名
            String date_Table = creat_KqTmp_Table(this.userView.getUserId()); //建立第一个临时表
            initializtion_date_Table(date_list, date_Table, nbase, e01a1, e0122, b0110, a0100, a0101); //给每个人建立一个时间范围
            getbmtable(a0100, nbase, e01a1, e0122, b0110, a0101, date_Table, group_id); //排班 把人员放入 到员工排班信息
            dropTable(date_Table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}