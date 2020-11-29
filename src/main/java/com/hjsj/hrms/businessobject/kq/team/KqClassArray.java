package com.hjsj.hrms.businessobject.kq.team;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * 排班处理的业务类
 * <p>Title:KqClassArray.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 21, 2006 2:17:41 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class KqClassArray implements KqClassArrayConstant, KqClassConstant {
    private Connection conn;
    private UserView   userView;

    public KqClassArray() {
    }

    public KqClassArray(Connection conn) {
        this.conn = conn;
    }

    public KqClassArray(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }

    /**
     * 班次列表
     * @return
     * @throws GeneralException
     */
    public ArrayList getClassList() throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select " + kq_class_name + "," + kq_class_id);
        sql.append(" from " + kq_class_table);
        sql.append(" where " + kq_class_id + "<>'0'");
        sql.append(" order by " + kq_class_id);
        ContentDAO dao = new ContentDAO(this.conn);
        CommonData vo = null;
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                vo = new CommonData();
                vo.setDataName(rs.getString(kq_class_name));
                vo.setDataValue(rs.getString(kq_class_id));
                list.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);

        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    /**
     * 得到周期班次
     * @return
     * @throws GeneralException
     */
    public ArrayList getCycleList() throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select " + kq_shift_ID + "," + kq_shift_name);
        sql.append(" from " + kq_shift_table);
        ContentDAO dao = new ContentDAO(this.conn);
        CommonData vo = null;
        vo = new CommonData();
        vo.setDataName("");
        vo.setDataValue("");
        list.add(vo);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                boolean boo = this.cycleShiftInPriv(rs.getString(kq_shift_ID));
                if (boo) {
                    vo = new CommonData();
                    vo.setDataName(rs.getString(kq_shift_name));
                    vo.setDataValue(rs.getString(kq_shift_ID));
                    list.add(vo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        vo = new CommonData();
        vo.setDataName("<新增..>");
        vo.setDataValue("add");
        list.add(vo);
        return list;
    }

    /**
     * 判断是否有周期班权限（周期班中包含的基本班次是否都有权限）
     * @param cycleId 周期班id
     * @return
     */
    private boolean cycleShiftInPriv(String cycleId) {
        boolean boo = true;

        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
        StringBuffer sql = new StringBuffer();
        //班次需要过滤一下，根据权限里的基本班次对比一样才展现
        sql.append("select a.id,a.days,b." + kq_class_name + ",b." + kq_class_id);
        sql.append(" from " + kq_shift_class_table + " a," + kq_class_table + " b");
        sql.append(" where a." + kq_shift_class_shiftID + "='" + cycleId + "'");
        sql.append(" and a." + kq_shift_class_classID + "=b." + kq_class_id);
        sql.append(" order by a." + kq_shift_class_seq);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                if (!"0".equals(rs.getString(KqClassConstant.kq_class_id))) {
                    //zxj 班次授权已调整为按所属机构控制，不再是资源授权，此处属于漏改
                    if(!kqUtilsClass.classInPriv(cycleId)) {
                    //if (!userView.isHaveResource(IResourceConstant.KQ_BASE_CLASS, rs.getString(KqClassConstant.kq_class_id))) {
                        boo = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return boo;
    }

    /**
     * 通过周期排班id，得到包含的基本班次
     * @param cycleId
     * @return
     * @throws GeneralException
     */
    public ArrayList getClassFromCycleId(String cycleId) throws GeneralException {
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("select a." + kq_shift_class_classID + ",b." + kq_class_name);
        sql.append(" from " + kq_shift_class_table + " a," + kq_class_table + " b");
        sql.append(" where a." + kq_shift_class_shiftID + "='" + cycleId + "'");
        sql.append(" and a." + kq_shift_class_classID + "=b." + kq_class_id);
        sql.append(" order by a." + kq_shift_class_seq);
        ContentDAO dao = new ContentDAO(this.conn);
        CommonData vo = null;
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
                vo = new CommonData();
                vo.setDataName(rs.getString(kq_class_name));
                vo.setDataValue(rs.getString(kq_shift_class_classID));
                list.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return list;
    }

    public HashMap getClassFromId(String cycleId) throws GeneralException {
        HashMap map = new HashMap();
        ArrayList list = new ArrayList();
        ArrayList day_list = new ArrayList();
        StringBuffer sql = new StringBuffer();
        //班次需要过滤一下，根据权限里的基本班次对比一样才展现
        sql.append("select a.id,a.days,b." + kq_class_name + ",b." + kq_class_id);
        sql.append(" from " + kq_shift_class_table + " a," + kq_class_table + " b");
        sql.append(" where a." + kq_shift_class_shiftID + "='" + cycleId + "'");
        sql.append(" and a." + kq_shift_class_classID + "=b." + kq_class_id);
        sql.append(" order by a." + kq_shift_class_seq);
        ContentDAO dao = new ContentDAO(this.conn);
        CommonData vo = null;
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            String days = "";
            while (rs.next()) {
                vo = new CommonData();
                //	    	     if(!rs.getString(this.kq_class_id).equals("0"))
                //    			 {
                //    				 if (!userView.isHaveResource(IResourceConstant.KQ_BASE_CLASS, rs.getString(this.kq_class_id)))
                //    	 					continue;
                //    			 }
                vo.setDataName(rs.getString(kq_class_name));
                vo.setDataValue(rs.getString("id"));
                list.add(vo);
                days = rs.getString("days");
                if (days == null || days.length() <= 0) {
                    days = "1";
                }
                day_list.add(days);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        map.put("id_list", list);
        map.put("days_list", day_list);
        return map;
    }

    /**
     * 
     * @Title:selectAllKaClassVo
     * @Description：获取周期排班添加班次列表数据
     * @author liuyang
     * @param flag 如果flag=1则不显示信息班次
     * @return
     * @throws GeneralException
     */
    public ArrayList selectAllKaClassVo(String flag) throws GeneralException {
        KqUtilsClass kqcl = new KqUtilsClass(conn,this.userView);
        LazyDynaBean ldb = new LazyDynaBean();
        ArrayList list = new ArrayList();
        ArrayList kqlist = new ArrayList();
        kqlist = kqcl.getKqClassListInPriv();
        try {
            for(int i=0;i<kqlist.size();i++){
                ldb = (LazyDynaBean) kqlist.get(i);
                RecordVo vo = new RecordVo(kq_class_table);
                String name = (String)ldb.get("name");
                String class_id = (String)ldb.get("classId");
                if ("1".equals(flag)) {
                   if(!"0".equals(class_id)) {
                       continue;
                   }
                }
                vo.setString(kq_class_id, class_id);
                vo.setString(kq_class_name, name);
                vo.setString("onduty_1", (String)ldb.get("onduty_1"));
                vo.setString("offduty_1", (String)ldb.get("offduty_1"));
                vo.setString("onduty_2", (String)ldb.get("onduty_2"));
                vo.setString("offduty_2", (String)ldb.get("offduty_2"));
                vo.setString("onduty_3", (String)ldb.get("onduty_3"));
                vo.setString("offduty_3", (String)ldb.get("offduty_3"));
                vo.setString("onduty_4", (String)ldb.get("onduty_4"));
                vo.setString("offduty_4", (String)ldb.get("offduty_4"));
                list.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
        return list;
    }

    /**
     * 取
    * @param dateString，
    *         某年某月某天
    * @param  afterNum
    *         天数  
    * @return string
    *          返回相加后得到新的某年某月某天
    * */
    public String getDateByAfter(java.util.Date date, int afterNum) throws GeneralException {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        calendar.add(GregorianCalendar.DAY_OF_YEAR, afterNum);
        return new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime());
    }

    /**
    * 得到班次id
    * @param conn
    * @param start_date
    * @param end_date
    * @param a0100
    * @param nbase
    * @return
    */
    public String getClassId(String op_date, String a0100, String nbase) {
        String class_id = "";
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select class_id from kq_employ_shift ");
            sql.append(" where a0100='" + a0100 + "' and UPPER(nbase)='" + nbase.toUpperCase() + "'");
            sql.append(" and q03z0 ='" + op_date.toString() + "'");
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                class_id = rs.getString("class_id");
            }
            if (class_id == null || class_id.length() <= 0) {
                class_id = "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return class_id;
    }

    /**
     * 得到班次信息的RecordVo
     * @param classId_list
     * @return
     */
    public RecordVo getClassMessage(String class_id) {
        if (class_id == null || class_id.length() <= 0 || "#".equals(class_id)) {
            return null;
        }
        ContentDAO dao = new ContentDAO(this.conn);
        RecordVo vo = null;
        try {
            vo = new RecordVo("kq_class");
            vo.setString("class_id", class_id);
            vo = dao.findByPrimaryKey(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

}
