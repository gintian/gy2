package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
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
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * <p>Title:保存调换班组</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 30, 2010:8:11:05 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SavePutUsrClassTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            ArrayList selectedinfolist = (ArrayList) this.getFormHM().get("selectedinfolist");
            if (selectedinfolist == null || selectedinfolist.size() <= 0)
                return;
            
            String classId = (String) this.getFormHM().get("classId"); //班组id
            String start_date_save = (String) this.getFormHM().get("start_date_save"); //开始时间
            String end_date_save = (String) this.getFormHM().get("end_date_save"); //结束时间
            String zhji = (String) this.getFormHM().get("zhji"); //0:不更改主集班次；1：更改；
            start_date_save = start_date_save.replaceAll("-", "\\.");
            end_date_save = end_date_save.replaceAll("-", "\\.");
            
            if ("1".equals(zhji))
                upclassvalue(selectedinfolist, classId);
            
            upgorup(selectedinfolist, classId); //更改班组
            
            getbmtable(selectedinfolist, classId, start_date_save, end_date_save); //更新人员班组
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    //更新人员班组
    private void upgorup(ArrayList selectedinfolist, String classId) {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("update kq_group_emp set ");
        sql.append(" group_id=? ");
        sql.append(" where a0100=? and nbase=?");
        for (int i = 0; i < selectedinfolist.size(); i++) {
            ArrayList one_value = new ArrayList();
            LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
            one_value.add(classId);
            one_value.add(rec.get("a0100"));
            one_value.add(rec.get("nbase"));
            list.add(one_value);
        }
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            dao.batchUpdate(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //更改人员班组
    private void getbmtable(ArrayList selectedinfolist, String classId, String start_date_save, String end_date_save) {
        //1.先把这一个时间段的人员排班信息删除；第二步在建立新的排班信息
        StringBuffer sql = new StringBuffer();
        ArrayList list = new ArrayList();
        sql.append("delete from kq_employ_shift where ");
        sql.append(" nbase=? and a0100=? and q03z0>=? and q03z0<=?");
        for (int i = 0; i < selectedinfolist.size(); i++) {
            ArrayList one_value = new ArrayList();
            LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
            one_value.add(rec.get("nbase"));
            one_value.add(rec.get("a0100"));
            one_value.add(start_date_save);
            one_value.add(end_date_save);
            list.add(one_value);
        }
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            dao.batchUpdate(sql.toString(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2,创建新的班次
        BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
        ArrayList date_list = baseClassShift.getDatelist(start_date_save, end_date_save); //时间list
        list = new ArrayList();
        for (int k = 0; k < selectedinfolist.size(); k++) {
            LazyDynaBean bean = new LazyDynaBean();
            LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(k);
            String a0100 = (String) rec.get("a0100");
            bean.set("a0100", a0100);
            String nbase2 = (String) rec.get("nbase");
            bean.set("nbase2", nbase2);
            String e01a1 = (String) rec.get("e01a1"); //职位
            if (e01a1 == null)
                e01a1 = "";
            bean.set("e01a1", e01a1);
            String e0122 = (String) rec.get("e0122"); //部门
            bean.set("e0122", e0122);
            String b0110 = (String) rec.get("b0110"); //单位
            bean.set("b0110", b0110);
            String a0101 = (String) rec.get("a0101");//姓名
            bean.set("a0101", a0101);
            list.add(bean);
        }
        for (int i = 0; i < list.size(); i++) {
            LazyDynaBean bean = (LazyDynaBean) list.get(i);
            saveInheritCalss(bean, date_list, classId); //得到每个人进行排版
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
    private void getbmtable(String a0100, String nbase, String e01a1, String e0122, String b0110, String a0101, String date_Table,
            String group_id) {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        //		String sql1="select group_id from kq_group_emp where a0100='"+a0100+"' and nbase='"+nbase+"'";
        //		RowSet rs = null;
        //		String group_id="";
        try {
            //			rs=dao.search(sql1);
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
     * 调换班组更改主集
     * @param selectedinfolist
     * @param classId
     */
    private void upclassvalue(ArrayList selectedinfolist, String classId) {
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rowSet = null;
        StringBuffer sql = new StringBuffer();
        try {
            String groupName = getGroupName(classId); //通过班组id得到班组名称
            
            //0为字符型直接写入班组名称，1为代码型在查找对应的代码写入;
            String contentid = KqParam.getInstance().getShiftGroupItem();
            String flag = getcodesetid(contentid);
            String codeV = "";
            if (!"0".equals(flag))
                codeV = getcodeitemName(flag, groupName);
            for (int i = 0; i < selectedinfolist.size(); i++) {
                LazyDynaBean rec = (LazyDynaBean) selectedinfolist.get(i);
                String a0100 = (String) rec.get("a0100");
                String nabase = (String) rec.get("nbase");
                if ("0".equals(flag)) {
                    sql.setLength(0);
                    sql.append("update " + nabase + "A01 set " + contentid + "='" + groupName + "'");
                    sql.append(" where A0100='" + a0100 + "'");
                    dao.update(sql.toString());
                } else {
                    if (codeV != null && codeV.length() > 0) {
                        sql.setLength(0);
                        sql.append("update " + nabase + "A01 set " + contentid + "='" + codeV + "'");
                        sql.append(" where A0100='" + a0100 + "'");
                        dao.update(sql.toString());
                    }
                }
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
            if (rowSet != null)
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return name;
    }

    //查看对应指标是否是代码型 1为代码型
    private String getcodesetid(String contentid) {
        String flag = "0";
        StringBuffer sql = new StringBuffer();
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        sql.append("select itemdesc,codesetid from fielditem where fieldsetid='A01' and itemid='" + contentid + "'");
        try {
            rowSet = dao.search(sql.toString());
            String codesetid = "";
            while (rowSet.next()) {
                codesetid = rowSet.getString("codesetid");
            }
            if (codesetid.length() > 0 && !"0".equalsIgnoreCase(codesetid))
                flag = codesetid;
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
            while (rowSet.next()) {
                codeV = rowSet.getString("codeitemid");
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
        return codeV;
    }
}
