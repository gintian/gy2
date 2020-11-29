package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hjsj.hrms.utils.ResourceFactory;
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

public class SaveArrayEmpTrans extends IBusiness implements KqClassArrayConstant {
    public void execute() throws GeneralException {
        try {
            String a0100s = (String) this.getFormHM().get("a0100s");
            String group_id = (String) this.getFormHM().get("group_id");
            String start_date = (String) this.getFormHM().get("start_date"); //开始时间
            String end_date = (String) this.getFormHM().get("end_date"); //结束时间
            String flag = (String) this.getFormHM().get("flag"); //true:继承班组排班 false：不继承排班
            String zhji = (String) this.getFormHM().get("zhji");
            BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
            ArrayList date_list = baseClassShift.getDatelist(start_date, end_date); //时间list

            if (a0100s == null || "".equals(a0100s))
                throw new GeneralException(ResourceFactory.getProperty("error.link.employ"));

            String[] n_a0100s = a0100s.split(",");
            ArrayList list = new ArrayList();
            String save_flag = "false";
            try {
                for (int i = 0; i < n_a0100s.length; i++) {
                    String n_a0100 = n_a0100s[i];
                    //2014.10.30 xxd 防止为空的情况
                    if(n_a0100==null||"".equals(n_a0100)){
                    	continue;
                    }
                    String nbase = n_a0100.substring(0, 3);
                    String id = n_a0100.substring(3);
                    StringBuffer insetSQL = new StringBuffer();
                    insetSQL.append("insert into " + kq_group_emp_table);
                    insetSQL.append(" (a0100,nbase,group_id,a0101,b0110,e0122,e01a1)");
                    insetSQL.append(" select  a0100,'" + nbase + "','" + group_id + "',a0101,b0110,e0122,e01a1 ");
                    insetSQL.append(" from " + nbase + "A01 where a0100='" + id + "'");
                    insetSQL.append(" and NOT EXISTS(SELECT * FROM " + kq_group_emp_table + "");
                    insetSQL.append(" where a0100='" + id + "' and nbase='" + nbase + "')");
                    ContentDAO dao = new ContentDAO(this.getFrameconn());
                    dao.insert(insetSQL.toString(), list);
                    if ("true".equalsIgnoreCase(flag)) {
                        //第一步：删除每个人传入的时间段的排班信息,删除员工排班信息
                        deleteSQL(nbase, id, start_date, end_date);
                        //第二步:对加入进来的人进行排班
                        saveInheritCalss(nbase, id, group_id, date_list);
                    }

                    if ("1".equals(zhji))
                        upclassvalue(id, nbase, group_id);//调换班组更改主集
                }
                save_flag = "true";
            } catch (Exception e) {
                throw new GeneralException(ResourceFactory.getProperty("kq.group.add.emp.error"));
            }
            this.getFormHM().put("save_flag", save_flag);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
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
    private void saveInheritCalss(String nbase, String a0100id, String group_id, ArrayList date_list) {
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
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        Field temp1 = new Field("a0100", "人员编号");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(50);
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
        temp3.setLength(20);
        temp3.setKeyable(false);
        temp3.setVisible(false);
        table.addField(temp3);
        Field temp4 = new Field("e0122", "部门");
        temp4.setDatatype(DataType.STRING);
        temp4.setLength(20);
        temp4.setKeyable(false);
        temp4.setVisible(false);
        table.addField(temp4);
        Field temp5 = new Field("e01a1", "职位");
        temp5.setDatatype(DataType.STRING);
        temp5.setLength(20);
        temp5.setKeyable(false);
        temp5.setVisible(false);
        table.addField(temp5);
        Field temp6 = new Field("a0101", "姓名");
        temp6.setDatatype(DataType.STRING);
        temp6.setLength(20);
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
     * 删除临时表
     * @param tablename
     */
    private void dropTable(String tablename) {
        String deleteSQL = "delete from " + tablename + "";
        ArrayList deletelist = new ArrayList();

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            dao.delete(deleteSQL, deletelist);
            DbWizard dbWizard = new DbWizard(this.getFrameconn());
            Table table = new Table(tablename);
            dbWizard.dropTable(table);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * 新建临时表的名字
    * **/
    private String getTmpTableName(String UserId, String PrivCode) {
        StringBuffer tablename = new StringBuffer();
        tablename.append("kquserclass");
        tablename.append("_");
        tablename.append(PrivCode);
        tablename.append("_");
        tablename.append(UserId);
        return tablename.toString();
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
    private void getbmtable(String a0100, String nbase, String e01a1, String e0122, String b0110, String a0101,
            String date_Table, String group_id) {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            StringBuffer insertSQL = new StringBuffer();
            insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
            insertSQL.append("SELECT a.nbase,a.A0100,a.a0101,a.b0110,a.e0122,a.e01a1,a.sDate,b.class_id,0 ");
            insertSQL.append(" FROM " + date_Table + " a,kq_org_dept_shift b");
            insertSQL.append(" WHERE b.q03z0=a.sDate and b.org_dept_id='" + group_id + "' and b.codesetid='@G'");
            dao.update(insertSQL.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调换班组更改主集
     * @param a0100
     * @param nabase
     * @param classId
     */
    private void upclassvalue(String a0100, String nabase, String classId) {
        String contentid = KqParam.getInstance().getShiftGroupItem();
        //没有设置班组指标
        if (contentid == null || "".equals(contentid.trim()) || "#".equals(contentid.trim()))
            return;
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rowSet = null;
        StringBuffer sql = new StringBuffer();

        String groupName = getGroupName(classId); //通过班组id得到班组名称
        try {
            //0为字符型直接写入班组名称，1为代码型在查找对应的代码写入;
            String flag = getcodesetid(contentid);
            if (flag == null || "".equals(flag))
                return;
            
            if (!"0".equals(flag)) {
                String codeV = getcodeitemName(flag, groupName);

                if (codeV == null || codeV.length() <= 0)
                    return;

                groupName = codeV;
            }

            sql.append("update " + nabase + "A01 set " + contentid + "='" + groupName + "'");
            sql.append(" where A0100='" + a0100 + "'");
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
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
            if (rowSet.next()) {
                name = rowSet.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return name;
    }

    //查看对应指标是否是代码型 1为代码型
    private String getcodesetid(String contentid) {
        String flag = "0";
        StringBuffer sql = new StringBuffer();
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        sql.append("select itemdesc,codesetid from fielditem");
        sql.append(" where fieldsetid='A01' and itemid='" + contentid + "'");
        sql.append(" AND useflag='1'");
        try {
            rowSet = dao.search(sql.toString());
            String codesetid = "";
            if (rowSet.next()) {
                codesetid = rowSet.getString("codesetid");
            } else {
                flag = ""; //指定的班组指标已不存在
            }
            
            if (codesetid.length() > 0 && !"0".equalsIgnoreCase(codesetid))
                flag = codesetid;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return flag;
    }

    private String getcodeitemName(String contentid, String groupName) {
        String codeV = "";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer sql = new StringBuffer();
        sql.append("select codeitemdesc,codeitemid from codeitem where codesetid='" + contentid + "' and codeitemdesc='"
                + groupName + "'");
        try {
            rowSet = dao.search(sql.toString());
            if (rowSet.next()) {
                codeV = rowSet.getString("codeitemid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return codeV;
    }
}
