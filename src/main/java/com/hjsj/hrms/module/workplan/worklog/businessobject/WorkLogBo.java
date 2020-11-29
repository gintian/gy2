package com.hjsj.hrms.module.workplan.worklog.businessobject;

import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hjsj.hrms.module.workplan.yearplan.businessobject.YearPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * <p>Title: WorkLogBo </p>
 * <p>Company: hjsj</p>
 * <p>create time: 下午02:46:44 </p>
 * @author linbz
 * @version 1.0
 */
public class WorkLogBo {

	private String nbase;
    private String A0100;
    private Connection conn;
    private UserView userView;
    private ContentDAO dao;
    private Boolean isself = true;//是否是本人标示

	public WorkLogBo(Connection conn, UserView userView, String A0100, String nbase) {
        this.conn = conn;
        this.nbase = nbase;
        this.A0100 = A0100;
        this.userView = userView;
        this.dao = new ContentDAO(conn);
        
        if(this.userView.getDbname().equalsIgnoreCase(nbase.toLowerCase()) && this.userView.getA0100().equalsIgnoreCase(A0100.toLowerCase()))
        	this.isself = true;
        else
        	this.isself = false;
        	
    }
	
	/**
     * 获取某个月的填写日志情况（例行，重点，其他）
     * @param mdate 某年某月  2016-01
     * @return 每个bean中，包含当天的填写类别
	 * @throws GeneralException 
     */
	public HashMap getDateInfoList(String monthdate) throws GeneralException{
		HashMap hm = new HashMap();
		ArrayList dateList = new ArrayList();
		ArrayList dateListinfo = new ArrayList();
		ArrayList fillStateList = new ArrayList();
		ArrayList p0100InfoList = new ArrayList();
		
		try {
			dateList = getDateList(monthdate);	
			String fromdt = dateList.get(0).toString();
			String todt = dateList.get(dateList.size()-1).toString();
			fillStateList = getDateClassId(fromdt, todt);
			p0100InfoList = getBeanInfoList("1", fromdt, todt);
			
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy.MM.dd");  
			for(int i=0;i<dateList.size();i++){
				String datestr = (String) dateList.get(i);
				LazyDynaBean infoBean = new LazyDynaBean();
		        infoBean.set("date", datestr);
		        infoBean.set("duty", "0");//=0 ；=1 例行工作
		        infoBean.set("key", "0");//=0 ；=1  重点工作
		        infoBean.set("oth", "0");//=0 ；=1  其他工作
		        infoBean.set("no", "0");//=0 ；=1 为空
		        infoBean.set("flag", "-1");//=0 正常填写；=1 补填 ;=2不需要填写但是已经填写了
				
		        for(int k=0;k<p0100InfoList.size();k++){
		        	LazyDynaBean p0100Bean = (LazyDynaBean) p0100InfoList.get(k);
		        	String p0100date = (String) p0100Bean.get("date");
		        	if(p0100date.equals(datestr)){
		        		String work_type = (String) p0100Bean.get("work_type");
		        		if("01".equals(work_type)){
		            		infoBean.set("duty", "1");
		            	}else if("02".equals(work_type)){
		            		infoBean.set("key", "1");
		            	}else if("03".equals(work_type)){
		            		infoBean.set("oth", "1");
		            	}else{
		            		infoBean.set("no", "1");
		            	}
		        		infoBean.set("flag", (String) p0100Bean.get("flag"));
		        		p0100InfoList.remove(k);
						k--;
		        	}
		        }
				
				infoBean.set("class_id", "0");
				infoBean.set("state", "");
				
				if(fillStateList.size() > 0){
					//有排班的情况
					for(int j=0;j<fillStateList.size();j++){
						LazyDynaBean fillBean = (LazyDynaBean) fillStateList.get(j);
						String filldate = (String) fillBean.get("date");
						String classId = (String) fillBean.get("class_id");
						if(datestr.equalsIgnoreCase(filldate)){
							if(!"0".equals(classId)){
							    String flag = (String) infoBean.get("flag");
							    if("0".equals(flag)){
							    	infoBean.set("state", "normal");//正常填写
							    }else if("1".equals(flag)){
							    	infoBean.set("state", "overdue");//逾期补填
							    }else if("2".equals(flag)){
							    	infoBean.set("state", "");//排班不需要填写但是填写了
							    }else{
							    	infoBean.set("state", "isnull");//未填
							    }
							}
							infoBean.set("class_id", classId);
							fillStateList.remove(j);
							j--;
							break;
						}
					}
				}else{
					//如果未排班，按周六、周日休息处理即可
					String flag = (String) infoBean.get("flag");
					
					int dayWeek = dayForWeek(datestr);
					if("0".equals(flag) && dayWeek!=1 && dayWeek!=7){
				    	infoBean.set("state", "normal");//正常填写
				    }else if("1".equals(flag) && dayWeek!=1 && dayWeek!=7){
				    	infoBean.set("state", "overdue");//逾期补填
				    }else {//=-1或者其他  没有填写日志
				    	if(dayWeek!=1 && dayWeek!=7){//周一到周五没有填写属于未填写
							infoBean.set("state", "isnull");//未填写
						}else{//周日周六不需要填写
							infoBean.set("state", "");
						}
				    }
					
					infoBean.set("class_id", "-99");
				}
				
				//超过当前日期的不可编辑
				Date mdate = sdfdate.parse(datestr);
				if(mdate.after(new Date())){
					infoBean.set("state", "after");
				}
				dateListinfo.add(infoBean);
			}
				
		} catch (ParseException e) {
			e.printStackTrace();
		}
		hm.put("dateList", dateList);
    	hm.put("dateListinfo", dateListinfo);
    	
		return hm;
	}
	
	/**
	 * 获取当天是周几
	 * @param dateValue
	 * @return 1=周日
	 * @throws GeneralException
	 */
	public int dayForWeek(String dateValue) throws GeneralException{
		int weekday = 0;
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");  
			Date date = sdf.parse(dateValue);
			cal.setTime(date);
			weekday = cal.get(Calendar.DAY_OF_WEEK);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return weekday;
	}
	
	/**
	 * 获取该月份的所有日期的集合
	 * @param monthdate 2017-01
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getDateList(String monthdate) throws GeneralException{
		
		ArrayList dateList = new ArrayList();
		try {
			Calendar cal = Calendar.getInstance();
			String year = "";
			String month = "";
			if("null".equals(monthdate) || monthdate.length() == 0){
				year = String.valueOf(cal.get(Calendar.YEAR));
				month = String.valueOf(cal.get(Calendar.MONTH)+1);
				monthdate = year+"-"+month;
			}else{
				year = monthdate.split("-")[0];
				month = monthdate.split("-")[1];
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  
			Date date = sdf.parse(monthdate);
			
			cal.setTime(date);
			String day = "";
			int daynum = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
			for(int i=0;i<daynum;i++){
				String datestr = "";
				day = String.valueOf(i+1);
				if(day.length()==1)
					day = "0"+day;
				datestr = year+"."+month+"."+day;
				dateList.add(datestr);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return dateList;
	}
	
	
	/**
     * 获取某段时间的班次id
     * @param fromDate 
     * @param toDate
     * @return 
	 * @throws GeneralException 
     */
	public ArrayList getDateClassId(String fromDate, String toDate) throws GeneralException{
		
		ArrayList dateListinfo = new ArrayList();
		
        RowSet rs = null;
        try {
        	//kq_employ_shift_arc 
            StringBuffer sql = new StringBuffer();
            sql.append("select class_id,q03z0 from kq_employ_shift ");
            sql.append(" where nbase=?");
            sql.append(" and a0100=?");
            sql.append(" and q03z0>=?");
            sql.append(" and q03z0<=?");
            
            ArrayList params = new ArrayList();
            params.add(nbase);
            params.add(A0100);
            params.add(fromDate);
            params.add(toDate);
            
            rs = dao.search(sql.toString(), params);
            LazyDynaBean infoBean = new LazyDynaBean();
            while (rs.next()) {
            	infoBean = new LazyDynaBean();
            	String class_id = "";
                String q03z0 = "";
                
                class_id = rs.getString("class_id");
                q03z0 = rs.getString("q03z0");
                if (class_id == null || class_id.length() <= 0)
                	class_id = "0";
                
                infoBean.set("date", q03z0);
                infoBean.set("class_id", class_id);
                
                dateListinfo.add(infoBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
	    } finally {
	        PubFunc.closeDbObj(rs);
	    }
        
	    return dateListinfo;
	}

	/**
	 * 获取某天的工作类型
	 * @param type 1:日历信息；2：汇总时间信息
	 * @param aDate
	 * @return
	 * @throws GeneralException
	 */
	
	public ArrayList getBeanInfoList (String type, String fromdt, String todt) throws GeneralException{
		ArrayList list = new ArrayList();
		LazyDynaBean infoBean = new LazyDynaBean();
        /*
        SELECT b.work_type,a.P0100,a.flag,a.P0104
        	,sum(b.work_time) work_time
     	FROM P01 a  inner  join  per_diary_content b 
     	on  a.nbase='Usr' 
		AND a.a0100='00000009'
		AND a.P0104>='2017-02-01' 
		AND a.P0104<='2017-02-28'
		and a.P0100=b.P0100
		and a.state=0 
		AND (a.p0115='02' or a.p0115 ='03') 
		AND (a.belong_type is null or a.belong_type=0)
			group by b.work_type,a.P0100,a.flag,a.P0104
        */
        
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT b.work_type,a.P0100,a.flag,a.P0104 ");
        if("2".equals(type))
        	sql.append(",sum(b.work_time) work_time");
        sql.append(" FROM P01 a inner join per_diary_content b ");
        sql.append(" ON  a.nbase=?");
        sql.append(" AND a.a0100=?");
        sql.append(" AND a.P0104>="+Sql_switcher.dateValue(fromdt));
        sql.append(" AND a.P0104<="+Sql_switcher.dateValue(todt));
        sql.append(" AND a.P0100=b.P0100 ");
        sql.append(" AND a.state=0 ");
        sql.append(" AND (a.p0115='02' or a.p0115 ='03') ");
        sql.append(" AND (a.belong_type is null or a.belong_type=0) ");
        if("2".equals(type))
        	sql.append("group by b.work_type,a.P0100,a.flag,a.P0104");
        
        ArrayList params = new ArrayList();
        params.add(nbase);
        params.add(A0100);
        
        RowSet rs = null;
        try {
        	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        	int duty_time = 0;
        	int key_time = 0;
        	int oth_time = 0;
        	rs = dao.search(sql.toString(), params);
            while (rs.next()) {
            	infoBean = new LazyDynaBean();
            	String work_type = rs.getString("work_type");
            	if("2".equals(type)){
            		int work_time = rs.getInt("work_time");
            		infoBean.set("work_time", String.valueOf(rs.getInt("work_time")));
                	if("01".equals(work_type)){
                		duty_time += work_time;
                	}else if("02".equals(work_type)){
                		key_time += work_time;
                	}else if("03".equals(work_type)){
                		oth_time += work_time;
                	}
            	}
            	infoBean.set("date", df.format(rs.getTimestamp("p0104")));
            	infoBean.set("work_type", work_type);
            	infoBean.set("flag", String.valueOf(rs.getInt("flag")));
            	infoBean.set("P0100", rs.getString("P0100"));
            	if(!"2".equals(type)){
            		list.add(infoBean);
            	}
            }
            if("2".equals(type)){
				infoBean.set("dutytime", String.valueOf(duty_time));
				infoBean.set("keytime", String.valueOf(key_time));
				infoBean.set("othtime", String.valueOf(oth_time));
    			list.add(infoBean);
            }
            
        } catch (Exception e) {
            e.printStackTrace();   
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
        
        return list;
	}
	
	/**
	 * 获取某个月/周的填写日志情况（例行，重点，其他）耗时汇总
	 * @param dateList 一个日期集合
	 * @param dropMonth  一个月份 2016-12
	 * @param dropDay  一个日期
	 * @param droptype  月汇总=0、周汇总=1
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getSumTypetime(ArrayList dateList, String dropMonth, String dropDay, String droptype) throws GeneralException{
		HashMap hm = new HashMap();
		ArrayList<LazyDynaBean> sumlist = getTypetime(dateList, dropMonth, dropDay, droptype);
		LazyDynaBean infoBean = (LazyDynaBean) sumlist.get(0);
		String dutystr = (String) infoBean.get("dutytime");
		String keystr = (String) infoBean.get("keytime");
		String othstr = (String) infoBean.get("othtime");
		hm.put("duty", dutystr);
		hm.put("key", keystr);
		hm.put("oth", othstr);
		return hm;
	}
	
	
	/**
	 * 获取每天的填写日志情况（例行，重点，其他）耗时汇总
	 * @param dateList 一个日期集合
	 * @param dropMonth  一个月份 2016-12
	 * @param dropDay  一个日期
	 * @param droptype  月汇总=0、周汇总=1
	 * @return ArrayList<LazyDynaBean> 每天的对象集合
	 * @throws GeneralException
	 */
    public ArrayList<LazyDynaBean> getTypetime(ArrayList dateList, String dropMonth, String dropDay, String droptype) throws GeneralException {
    	
    	ArrayList<LazyDynaBean> dailyInfo = new ArrayList<LazyDynaBean>();
    	ArrayList dates = new ArrayList();
	    	if("0".equals(droptype)){//本月汇总
	    		dates = getMonthDates(dropMonth);
	    	}else if("1".equals(droptype)){//本周汇总
	    		dates = dateToWeek(dropDay);
	    	} else{
	    		dates = dateList;
	    	}
	    	
	    	if(dates.size() > 1){
	    		ArrayList p0100InfoList = getBeanInfoList("2", dates.get(0).toString(), dates.get(dates.size()-1).toString());
	    		LazyDynaBean infoBean = (LazyDynaBean) p0100InfoList.get(0);
	    		dailyInfo.add(infoBean);
	    	}
        
        return dailyInfo;
    }
    
    /**
     * 获取某周日期集合
     * @param day  2016.01.01
     * @return 
     */
    public  ArrayList<String> dateToWeek(String dropDay) {
    	ArrayList<String> list = new ArrayList<String>();
    	try {
	    	if("".equals(dropDay))
	    		return list;
				
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");  
	    	Date mdate = sdf.parse(dropDay);
			int b = mdate.getDay();
			if(b == 0)
				b = 7;
			Date fdate;
			Long fTime = mdate.getTime() - b * 24 * 3600000;
			for (int a = 1; a <= 7; a++) {
				fdate = new Date();
				fdate.setTime(fTime + (a * 24 * 3600000));
	
				list.add(sdf.format(fdate));
			}
    	} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return list;
	}
    
    /**
     * 获取某个月日期集合
     * @param mdate 某年某月  2016-01
     * @return 
     * @throws GeneralException 
     */
	public ArrayList getMonthDates(String monthdate) throws GeneralException{
		ArrayList dateList = new ArrayList();
		try {
			Calendar cal = Calendar.getInstance();
			String year = "";
			String month = "";
			if("null".equals(monthdate) || monthdate.length() == 0){
				year = String.valueOf(cal.get(Calendar.YEAR));
				month = String.valueOf(cal.get(Calendar.MONTH)+1);
				monthdate = year+"-"+month;
			}else{
				year = monthdate.split("-")[0];
				month = monthdate.split("-")[1];
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  
			Date date = sdf.parse(monthdate);
			
			cal.setTime(date);
			String day = "";
			int daynum = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
			for(int i=0;i<daynum;i++){
				String datestr = "";
				day = String.valueOf(i+1);
				if(day.length()==1)
					day = "0"+day;
				datestr = year+"."+month+"."+day;
				dateList.add(datestr);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
    	
		return dateList;
	}
    
	/**
	 * 获取日志表格数据
	 * @param p0100
	 * @param datevalue
	 * @return
	 * @throws GeneralException
	 */
	public HashMap getTableList(String p0100, String datevalue) throws GeneralException {
			HashMap map = new HashMap();
			ArrayList tablelist = new ArrayList();
			
			if("".equals(p0100) && p0100.length()<=0){
				p0100 =  getLogP0100(datevalue);//获取此人对应的P0100
				if(p0100.equals(0)){
					map.put("tabledata", tablelist);
		            map.put("p0100", "");
		            map.put("p0115", "");
		            map.put("flag", "");
					return map;
				}
			}
			//前台表格数据中隐藏年月日  haosl 2018-2-6
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
	    	StringBuffer selectsql = new StringBuffer();
			selectsql.append(" select Record_num,work_type,content,finish_desc,start_time,end_time,work_time,other_desc ");
			selectsql.append(" from per_diary_content ");
			selectsql.append(" where a0100=?");
			selectsql.append(" and nbase=?");
			selectsql.append(" and p0100=?");
			
			ArrayList list = new ArrayList();
			list.add(A0100);
			list.add(nbase);
			list.add(p0100);
			
			StringBuffer p01sql = new StringBuffer();
			p01sql.append(" select p0115,flag ");
			p01sql.append(" from p01 ");
			p01sql.append(" where p0100=?");
			
			ArrayList p01list = new ArrayList();
			p01list.add(p0100);
			
			HashMap tablemap = new HashMap();
			String p0115 = "";
        	int flag = -1;
			RowSet rs = null;
            try {
            	rs = dao.search(selectsql.toString(), list);
                while (rs.next()) {
                	tablemap = new HashMap();
                	tablemap.put("work_type", rs.getString("work_type"));
                	tablemap.put("content", rs.getString("content"));
                	tablemap.put("finish_desc", rs.getString("finish_desc"));
                	tablemap.put("work_time", rs.getInt("work_time"));
                	tablemap.put("start_time", df.format(rs.getTimestamp("start_time")));
                	tablemap.put("end_time", df.format(rs.getTimestamp("end_time")));
                	tablemap.put("other_desc", rs.getString("other_desc"));
                	tablemap.put("Record_num", rs.getInt("Record_num"));
                    tablelist.add(tablemap);
                }
                
                rs = dao.search(p01sql.toString(), p01list);
                if (rs.next()) {
                	p0115 = rs.getString("p0115");
                	flag = rs.getInt("flag");
                }
            } catch (Exception e) {
                e.printStackTrace();  
                throw GeneralExceptionHandler.Handle(e);
            } finally {
                PubFunc.closeDbObj(rs);
            }
			
            map.put("tabledata", tablelist);
            map.put("p0100", p0100);
            map.put("p0115", p0115);
            map.put("flag", flag);
			return map;
		}
    
    /**
     * 添加日志记录
     *  @param bean
     *  @return 
     */
    public HashMap addWorkLog(LazyDynaBean bean) throws GeneralException {
    	HashMap map = new HashMap();
    	String p0100 = (String) bean.get("p0100");
    	if("".equals(p0100) && p0100.length()<=0){
    		String startime = (String) bean.get("start_time");
			p0100 =  getLogP0100(startime.substring(0, 10));//获取此人对应的P0100
    	}
    	int result = 0;
    	int record_num = getMaxRecord_num(Integer.parseInt(p0100));//获取当天条数数
    	try {
	    	RecordVo perVo = new RecordVo("per_diary_content");
	    	
	    	perVo.setInt("p0100", Integer.parseInt(p0100));
	    	perVo.setInt("record_num", record_num);
	    	perVo.setString("a0100", userView.getA0100());
	    	perVo.setString("nbase", userView.getDbname());
	    	perVo.setString("b0110", userView.getUserOrgId());
	    	perVo.setString("e0122", userView.getUserDeptId());
	    	perVo.setString("a0101", userView.getUserFullName());
			perVo.setString("e01a1", userView.getUserPosId());
			
			perVo.setString("content", (String) bean.get("content"));
			perVo.setString("work_type", (String) bean.get("work_type"));
			perVo.setString("finish_desc", (String) bean.get("finish_desc"));
			perVo.setInt("work_time", Integer.parseInt((String) bean.get("work_time")));
			perVo.setString("other_desc", (String) bean.get("other_desc"));
			
			DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			Date start = df.parse((String) bean.get("start_time"));
			Date end = df.parse((String) bean.get("end_time"));
			perVo.setDate("start_time", DateUtils.getSqlDate(start));
			perVo.setDate("end_time", DateUtils.getSqlDate(end));// 结束时间
		
			result = dao.addValueObject(perVo);
			
    	} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

    	map.put("result", result == 1 ? "0" : "1");
    	map.put("record_num", record_num);
    	return map;
    	
    }
    
    /**
	 * 取id值
	 * @return
     * @throws GeneralException 
	 * @throws SQLException 
	 */
	public int getMaxRecord_num(int p0100) throws GeneralException 
	{
		int maxid=0;
		RowSet rowSet = null;
		try{
			
			rowSet = dao.search("select max(record_num) from per_diary_content where p0100="+p0100);
		        while(rowSet.next())
		        {
		        	String id = rowSet.getString(1);
		        	if((id!=null) && (id.trim().length()>0))
		        		maxid=Integer.parseInt(id);	
		        }
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rowSet);
		}
	    return  ++maxid;	
	}
    
    /**
     * @throws GeneralException    
     * @Title: getPeopleP0100   
     * @Description: 返回某人或部门总结p0100
     * @param @param nbase
     * @param @param a0100
     * @param @param summaryCycle =0日志标示
     * @param @param p0104 起始日期
     * @param @param type =“sum”汇总数据时应为已发布状态
     * @param @return P0100
     * @return int 
     * @throws   
    */
    public LazyDynaBean getPeopleP0100(String nbase,String a0100,String summaryCycle,String p0104, String type) throws GeneralException { 
    	LazyDynaBean infoBean = new LazyDynaBean();
    	int p0100 = 0;
    	int flag = -1; 
    	String p0115 = "";
        RowSet rset = null;
        try{           
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT p0100,flag,p0115 ");
            sql.append(" FROM P01 ");
            sql.append(" WHERE state=?");
            sql.append(" AND P0104="+Sql_switcher.dateValue(p0104));
//            sql.append(" AND P0106=" + p0106);//日志的结束日期不用考虑
            
            sql.append(" AND nbase=?");
            sql.append(" AND a0100=?");
            if(!this.isself || "sum".equalsIgnoreCase(type))
            	sql.append(" AND (p0115='02' or p0115 ='03')");
            sql.append(" AND (belong_type is null or belong_type=0)");

            ArrayList params = new ArrayList();
            params.add(summaryCycle);
            params.add(nbase);
            params.add(a0100);
            
            rset = dao.search(sql.toString(), params);
            if (rset.next()) {
            	p0100= rset.getInt("p0100");
                flag = rset.getInt("flag");
                p0115 = rset.getString("p0115");
            }
            infoBean.set("flag", String.valueOf(flag));   
    		infoBean.set("p0100", String.valueOf(p0100));   
    		infoBean.set("p0115", p0115);   
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
			PubFunc.closeDbObj(rset);
		}     
        return infoBean;
    } 
    
    /**
	 * 获取此人日志总结表P0100
	 * 
	 * @param bean
	 * @return
     * @throws GeneralException 
	 */
	public String getLogP0100(String datevalue) throws GeneralException {
		
		
	    //检查是否存在此人的日志
		LazyDynaBean infoBean = getPeopleP0100( nbase, A0100, "0", datevalue, "");
	    String p0100 = (String) infoBean.get("p0100");
	    if (!"0".equals(p0100)){
	        return p0100;
	    }
	    if(!this.isself)
	    	return String.valueOf(p0100);
	    
		RecordVo p01Vo = new RecordVo("p01");
		IDGenerator idg = new IDGenerator(2, this.conn);

		String pid = "";
		try {
			pid = idg.getId("P01.P0100");
			p01Vo.setInt("p0100", Integer.parseInt(pid));
			p01Vo.setString("a0100", userView.getA0100());
			p01Vo.setString("nbase", userView.getDbname());
			p01Vo.setString("b0110", userView.getUserOrgId());
			p01Vo.setString("e0122", userView.getUserDeptId());
			p01Vo.setString("a0101", userView.getUserFullName());
			p01Vo.setString("e01a1", userView.getUserPosId());
			p01Vo.setInt("state", 0);// 0:日报、1：周报、
			p01Vo.setString("p0115", "01");//审批标志01起草
			
			DateFormat df = new SimpleDateFormat("yyyy.MM.dd");// HH:mm
			p01Vo.setDate("p0104", df.parse(datevalue));// 开始时间
			p01Vo.setDate("p0106", df.parse(datevalue));// 结束时间

			dao.addValueObject(p01Vo);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return pid;
	}
	/**
	 * 获取参数设置  填写日志期间
	 * @return
	 * @throws GeneralException
	 */
	public String getWorkLogSection() throws GeneralException {
		String section = "";
		WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(conn, userView);
		List<HashMap<String,HashMap<String,String>>> list = funcBo.getXmlData();
		
	    for(int i=0;i<list.size();i++){
	    	HashMap<String,HashMap<String,String>> maplist = list.get(i);
	    	HashMap<String,String> maplog = maplist.get("p5");
	    	if(maplog==null)
	    		continue;
	    	section = maplog.get("now");
	    }
	    
	    return section;
	}
	
	/**
	 * 获取参数设置 日志 耗时相关列参数
	 * @return 1显示，2不显示
	 * @throws GeneralException
	 */
	public String getTaskTimeSign () throws GeneralException {
		String taskTimeSign  = "";
		WorkPlanConfigBo funcBo = new WorkPlanConfigBo(conn, userView);
		Map map = funcBo.getXmlData();
		
		taskTimeSign  = (String) map.get("taskTimeSign");
		if(taskTimeSign==null || "".equals(taskTimeSign))
			return "2" ;
		
		StringBuffer selectsql = new StringBuffer();
		selectsql.append(" select "+taskTimeSign);
		selectsql.append(" from k01 ");
		selectsql.append(" where E01A1=(");
		selectsql.append(" select E01A1 from "+nbase+"A01 ");
		selectsql.append(" where A0100=?");
		selectsql.append(" )");
		
		ArrayList list = new ArrayList();
		list.add(A0100);
		
		RowSet rs = null;
		try {
        	rs = dao.search(selectsql.toString(), list);
            if (rs.next()) {
            	String value = rs.getString(taskTimeSign);
            	value = value==null?"":value;
            	if("".equals(value))
            		return "2" ;
            	else
            		return value ;
            }else{
            	return "2" ;
            }
            
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
            PubFunc.closeDbObj(rs);
        }
//	    return taskTimeSign ;
	}
	
	/**
	 * 更新记录
	 * @param p0100
	 * @param record_num
	 * @param field
	 * @param value
	 * @return
	 * @throws GeneralException
	 */
	public HashMap updateRecord(int p0100, int record_num, String field, String value) throws GeneralException {
		HashMap map = new HashMap();

		try {
			RecordVo vo = new RecordVo("per_diary_content");
			vo.setInt("p0100", p0100);
			vo.setInt("record_num", record_num);
			vo = dao.findByPrimaryKey(vo);
			if("start_time".equalsIgnoreCase(field) || "end_time".equalsIgnoreCase(field)){
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Date date = df.parse(value);
				vo.setDate(field, DateUtils.getSqlDate(date));
			}else{
				vo.setString(field, value);
			}
			int result = dao.updateValueObject(vo);
			map.put("errorcode", result == 1 ? "0" : "1");

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return map;
	}
    /**
     * 删除记录
     * @param p0100
     * @param record_num
     * @return
     * @throws GeneralException
     */
	public HashMap deleteRecord(int p0100, int record_num) throws GeneralException {
		HashMap map = new HashMap();

        try {
        	RecordVo vo = new RecordVo("per_diary_content");
			vo.setInt("p0100", p0100);
			vo.setInt("record_num", record_num);
			vo = dao.findByPrimaryKey(vo);
			int result = dao.deleteValueObject(vo);
			map.put("errorcode", result == 1 ? "0" : "1");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	
	/**
	 * 发布日志
	 * @param p0100
	 * @param flag =0 正常填写；=1 补填  =2 排班不需要填写但是填写了
	 * @return
	 * @throws GeneralException
	 */
	public HashMap publishLog(int p0100, int flag) throws GeneralException {
		HashMap map = new HashMap();

        try {
        	RecordVo vo = new RecordVo("p01");
			vo.setInt("p0100", p0100);
			vo = dao.findByPrimaryKey(vo);
			vo.setString("p0115", "02");
			vo.setInt("flag", flag);
			int result = dao.updateValueObject(vo);
			map.put("errorcode", result == 1 ? "0" : "1");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	
	/**
	 * 撤回日志
	 * @param p0100
	 * @return
	 * @throws GeneralException
	 */
	public HashMap recallLog(int p0100) throws GeneralException {
		HashMap map = new HashMap();

        try {
        	RecordVo vo = new RecordVo("p01");
			vo.setInt("p0100", p0100);
			vo = dao.findByPrimaryKey(vo);
			vo.setString("p0115", "01");
			int result = dao.updateValueObject(vo);
			map.put("errorcode", result == 1 ? "0" : "1");
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;
	}
	
	/**
	 * 根据一天获取该周的工作计划
	 * @param datevalue
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getWeekTableList(String datevalue) throws GeneralException {
		
		datevalue = getLastWeek(datevalue);
//		DateUtils.format(DateUtils.addDays(DateUtils.getDate(datevalue, "yyyy.MM.dd"), -7), "yyyy.MM.dd");
		
		ArrayList tablelist = new ArrayList();
    	StringBuffer selectsql = new StringBuffer();
		selectsql.append(" select p1901,p1903 ");
		selectsql.append(" from p19 ");
		selectsql.append(" where P1917=1");
		selectsql.append(" and p0100 in (");
		selectsql.append(" select p0100 from p01 ");
		selectsql.append(" where state=1");
		selectsql.append(" and nbase=?");
		selectsql.append(" and a0100=?");
		selectsql.append(" and p0106>="+Sql_switcher.dateValue(datevalue));
		selectsql.append(" and p0104<="+Sql_switcher.dateValue(datevalue));
		selectsql.append(" )");
		
		ArrayList params = new ArrayList();
        params.add(nbase);
        params.add(A0100);
         
		HashMap tablemap = new HashMap();
		RowSet rs = null;
		try {
        	rs = dao.search(selectsql.toString(), params);
            while (rs.next()) {
            	tablemap = new HashMap();
            	tablemap.put("p1901", rs.getString("p1901"));
            	tablemap.put("p1903", rs.getString("p1903"));
                tablelist.add(tablemap);
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
            PubFunc.closeDbObj(rs);
        }
		return tablelist;
	}
	
	/**
	 * 获取本周的计划，需要关联上周的p0100的下期计划
	 * @param datevalue
	 * @return 比选中的日期减去7天
	 * @throws GeneralException
	 */
	public String getLastWeek(String datevalue) throws GeneralException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");  
			Date date = sdf.parse(datevalue);
			Long fTime = date.getTime() - 7 * 24 * 3600000;
			Date fdate = new Date();
			fdate.setTime(fTime);
			datevalue = sdf.format(fdate);
		} catch (ParseException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return datevalue;
	}
	/**
	 * 获取人员是否有填写日志权限
	 * @return true|false
	 * @throws GeneralException
	 */
	public boolean getPower()throws GeneralException{
		boolean model = false;
		YearPlanBo bo = new YearPlanBo(userView, conn);
		String guidkey = "";
		if(StringUtils.isNotBlank(this.userView.getDbname())){
			String id  = nbase+A0100;
			guidkey = bo.getGuidKey(id,null);
		}
		WorkPlanConfigBo funcBo = new WorkPlanConfigBo(conn, userView);
		try {
			model = funcBo.personOpenModel(guidkey, "P5");
		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return model;
	}
	
}
