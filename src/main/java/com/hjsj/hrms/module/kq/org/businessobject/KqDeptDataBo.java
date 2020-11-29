package com.hjsj.hrms.module.kq.org.businessobject;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.module.kq.kqself.kqempcal.businessobject.KqEmployeeCalendar;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class KqDeptDataBo {
    private Connection conn;
    private ContentDAO dao;
    private UserView userView;
    
    private KqDeptDataBo() {
        
    }
    
    public KqDeptDataBo(Connection conn) {
        this.conn = conn;        
        this.dao = new ContentDAO(conn);
    }
    
    public KqDeptDataBo(Connection conn, UserView userView) {
        this.conn = conn;        
        this.dao = new ContentDAO(conn);
        this.userView = userView;
    }
    
    /**
     * 获取某部门某段日期内的考勤汇总数据
     * @param dept 
     * @param fromDate 开始日期
     * @param toDate 结束日期
     * @return
     */
    public HashMap getDeptSumData(String dept, Date fromDate, Date toDate) {
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    	
    	ArrayList sumDatalist = getSumInfo("dept", dept, sdf.format(fromDate), sdf.format(toDate), 0, 0);
    	HashMap sumData = (HashMap) sumDatalist.get(0);
    	
        return sumData;
    }
    
    /**
     * 获取某部门下人员某段日期内的考勤汇总数据（分页）
     * @param dept 
     * @param fromDate 开始日期
     * @param toDate 结束日期
     * @param limit 每页条数
     * @param page 第几页
     * @return
     */
    public ArrayList getDeptPersonSumDate(String dept, Date fromDate, Date toDate,
            int limit, int page) {
    	
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        ArrayList personData = getSumInfo("person", dept, sdf.format(fromDate), sdf.format(toDate), limit, page);
        
        return personData;
    }
    
    /**
     * 得到某段日期内考勤汇总情况
     * @param type 区分部门 dept 人员person
     * @param fromDate 开始日期
     * @param toDate 结束日期
     * @param limit 每页条数
     * @param page 第几页
     * @return
     */
    public ArrayList getSumInfo(String type, String dept, String fromDate, String toDate, int limit, int page) {
    	KqEmployeeCalendar kqEmployeeCalendar = new KqEmployeeCalendar(this.conn, this.userView, "", "");
    	ArrayList datalist = new ArrayList();
        HashMap hm = new HashMap();
//        fromDate = fromDate.replaceAll("-", ".");
//        toDate = toDate.replaceAll("-", ".");
        RowSet rs = null;
        String sumField = "";
        //需要汇总的指标
        StringBuffer sumSQL = new StringBuffer();
        sumSQL.append("SELECT 0");
        if("person".equalsIgnoreCase(type))
        	sumSQL.append(",nbase,a0100,max(a0101) as a0101,max(A0000) as A0000,max(B0110) as B0110,max(E0122) as E0122,max(E01A1) as E01A1");
        //应出勤
        sumField = kqEmployeeCalendar.getKqItemSumSQL("27", "onduty");
        if (!"".equals(sumField)) {
            sumSQL.append(",");        
            sumSQL.append(sumField);
        }
        //实出勤||正常
        sumField = kqEmployeeCalendar.getKqItemSumSQL("28", "normal");
        if (!"".equals(sumField)) {
            sumSQL.append(",");        
            sumSQL.append(sumField);
        }
        
        //异常(迟到、早退、旷工)
        sumField = kqEmployeeCalendar.getKqItemSumSQL("21", "be_late");
        if (!"".equals(sumField)) {
            sumSQL.append(",");        
            sumSQL.append(sumField);
        }
        
        sumField = kqEmployeeCalendar.getKqItemSumSQL("23", "leave_early");
        if (!"".equals(sumField)) {
            sumSQL.append(",");        
            sumSQL.append(sumField);
        }
        
        sumField = kqEmployeeCalendar.getKqItemSumSQL("25", "absent");
        if (!"".equals(sumField)) {
            sumSQL.append(",");        
            sumSQL.append(sumField);
        }
        
        //请假公出
        sumField = kqEmployeeCalendar.getKqItemSumSQL("0%", "leave");
        if (!"".equals(sumField)) {
            sumSQL.append(",");        
            sumSQL.append(sumField);
        }        
        
        //加班
        sumField = kqEmployeeCalendar.getKqItemSumSQL("1%", "overtime");
        if (!"".equals(sumField)) {
            sumSQL.append(",");        
            sumSQL.append(sumField);
        }
        
        //公出
        sumField = kqEmployeeCalendar.getKqItemSumSQL("3%", "office_leave");
        if (!"".equals(sumField)) {
            sumSQL.append(",");        
            sumSQL.append(sumField);
        }
        String whereIn = RegisterInitInfoData.getKqEmpPrivWhr(conn, userView, "Q03");
        sumSQL.append(" FROM Q03");
        sumSQL.append(" WHERE ");
        sumSQL.append(whereIn);
        sumSQL.append(" and Q03Z0>=?");
        sumSQL.append(" and Q03Z0<=?");
        sumSQL.append(" and (b0110 like ? or e0122 like ? )");
        if("person".equalsIgnoreCase(type))
        	sumSQL.append(" group by nbase,A0100 order by  A0000,B0110,E0122,E01A1,A0100 ");
        
        ArrayList params = new ArrayList();
        params.add(fromDate.replaceAll("-", "."));
        params.add(toDate.replaceAll("-", "."));
        params.add(dept+"%");
        params.add(dept+"%");
        
        double except = 0;//异常
        try {
        	if("dept".equalsIgnoreCase(type)){
        		rs = dao.search(sumSQL.toString(), params);
        	}else if("person".equalsIgnoreCase(type)){
        		rs=dao.search(sumSQL.toString(), params, limit, page);
        	}else{
        		return datalist;
        	}
            while (rs.next()) {
            	 
            	hm = new HashMap();
            	//两位小数处理 (double)(Math.round(a*100))/100
            	double normal = rs.getDouble("normal");
            	hm.put("normal", (double)(Math.round(normal*100))/100); //正常
            	double leave = rs.getDouble("leave");
            	hm.put("leave", (double)(Math.round(leave*100))/100);//请假
            	double overtime = rs.getDouble("overtime");
            	hm.put("overtime", (double)(Math.round(overtime*100))/100);//加班
            	double office_leave = rs.getDouble("office_leave");
            	hm.put("office_leave", (double)(Math.round(office_leave*100))/100);  //公出 
            	
            	double be_late = rs.getDouble("be_late");//迟到
            	double absent = rs.getDouble("absent");//旷工
            	double leave_early = rs.getDouble("leave_early");//早退
            	except = be_late + absent + leave_early;
            	hm.put("except", (double)(Math.round(except*100))/100);//异常
            	
            	if("dept".equalsIgnoreCase(type)){
            		double onduty = rs.getDouble("onduty");
	            	hm.put("onduty", (double)(Math.round(onduty*100))/100); //应出勤
	            	hm.put("duty", (double)(Math.round(normal*100))/100); //实出勤
            	}
            	
            	if("person".equalsIgnoreCase(type)){
            		hm.put("a0100", rs.getString("a0100")); 
                	hm.put("nbase", rs.getString("nbase"));
                	hm.put("a0101", rs.getString("a0101"));
            	}
            	
            	datalist.add(hm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
        
        return datalist;
    }
    
    /**
     * 获得总条数
     * @param fromDate 开始日期
     * @param toDate 结束日期
     * @return
     */
    public int getSumCount(String dept, String fromDate, String toDate){
    	int sum = 0;
    	String whereIn = RegisterInitInfoData.getKqEmpPrivWhr(conn, userView, "Q03");
    	StringBuffer sumSQL = new StringBuffer();
    	sumSQL.append("SELECT count(1) sum ");
    	sumSQL.append(" from ( ");
        sumSQL.append(" SELECT A0100");
        sumSQL.append(" FROM Q03");
        sumSQL.append(" WHERE ");
        sumSQL.append(whereIn);
        sumSQL.append(" and Q03Z0>=?");
        sumSQL.append(" and Q03Z0<=?");
        sumSQL.append(" and (b0110 like ? or e0122 like ? )");
        sumSQL.append(" group by nbase,A0100 ");
        sumSQL.append(" ) a");
        
        ArrayList params = new ArrayList();
        params.add(fromDate.replaceAll("-", "."));
        params.add(toDate.replaceAll("-", "."));
        params.add(dept+"%");
        params.add(dept+"%");
        
        RowSet rs = null;
        try {
        	rs = dao.search(sumSQL.toString(), params);
        	while (rs.next()) {
        		sum = rs.getInt("sum");
        	}
        	
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        PubFunc.closeDbObj(rs);
	    }
	        
    	return sum;
    }
    
}
