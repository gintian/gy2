package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryBrowse {

	
	 /**
	    * 返回未封存最小月的日期list
	    * @param finished 
	    *        封存标签;0:未封存
	    * @return
	    *        返回未封存最小月的所有日期
	    *        getDataName --工作日  yyyy.MM.dd 星期x
	    *        getDataValue yyyy.MM.dd
	    * */
	   public static ArrayList registerdate(String b0110,Connection conn,UserView userView,String cur_course,String finished) throws GeneralException  {
			String kq_start;				
			String kqStrdate;
			String kq_year="";
			String kq_duration="";		
			if(cur_course.indexOf("-")>1)
			{
				String[] course= cur_course.split("-");
				kq_year=course[0];
				kq_duration=course[1];
			}	 		
			int spacedate = 0;
			ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,conn);
			String rest_date=restList.get(0).toString();
			ArrayList datelist = new ArrayList();
			StringBuffer strsql = new StringBuffer();
			strsql
					.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
			strsql
					.append(" where kq_year ='"+kq_year+"'");
			strsql
					.append(" and kq_duration ='"+kq_duration+"'and finished="+finished+" ");
			ContentDAO dao = new ContentDAO(conn);
			
			RowSet rowSet=null;
			try {
				rowSet = dao.search(strsql.toString());
				if (rowSet.next())
{				
					Date d1 = rowSet.getDate("kq_start");
					Date d2 = rowSet.getDate("kq_end");				
					spacedate = DateUtils.dayDiff(d1,d2);				
					SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
					               
	                d1=DateUtils.addDays(d1,0);   
	               
					for (int i = 0; i <=spacedate; i++) 
					{
						CommonData vo = new CommonData();					
						kq_start = format1.format(d1);
						
						String rest=IfRestDate.is_RestDate(kq_start,userView,rest_date,b0110,conn);
						
						vo.setDataValue(kq_start);
						kqStrdate = KqUtilsClass.getWeekName(d1);
						
						kqStrdate = kq_start + " " + kqStrdate + " " + rest;
						vo.setDataName(kqStrdate);
										
						datelist.add(vo);
						
						d1=DateUtils.addDays(d1,1);   
					}	
					
				}else{
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
			}finally
		    {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		    } 

			return datelist;
		} 
	   /**
	    * 返回未封存最小月的日期list
	    * @param finished 
	    *        封存标签;0:未封存
	    * @return
	    *        返回未封存最小月的所有日期
	    *        getDataName --工作日  yyyy.MM.dd 星期x
	    *        getDataValue yyyy.MM.dd
	    * */
	   public static ArrayList registerdate(String b0110,Connection conn,UserView userView,String finished) throws GeneralException  {
			String kq_start;				
			String kqStrdate;				
			 		
			int spacedate = 0;
			ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,conn);
			String rest_date=restList.get(0).toString();
			ArrayList datelist = new ArrayList();
			StringBuffer strsql = new StringBuffer();
			strsql
					.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
			strsql
					.append(" where  finished='"+finished+"' order by kq_year desc,kq_duration desc");
			ContentDAO dao = new ContentDAO(conn);
			RowSet rowSet=null;
			try {
				rowSet = dao.search(strsql.toString());
				if (rowSet.next()) {				
					Date d1 = rowSet.getDate("kq_start");
					Date d2 = rowSet.getDate("kq_end");				
					spacedate = DateUtils.dayDiff(d1,d2);				
					SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
               
	                d1=DateUtils.addDays(d1,0);   
	               
					for (int i = 0; i <=spacedate; i++) {
						CommonData vo = new CommonData();					
						kq_start = format1.format(d1);
						String rest=IfRestDate.is_RestDate(kq_start,userView,rest_date,b0110,conn);
						
						vo.setDataValue(kq_start);
						kqStrdate = KqUtilsClass.getWeekName(d1);
						
						kqStrdate = kq_start + " " + kqStrdate + " " + rest;
						vo.setDataName(kqStrdate);
										
						datelist.add(vo);
						
						d1=DateUtils.addDays(d1,1);   
						
					}	
					
				}else{
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));	
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
			}finally
		    {
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
		    } 

			return datelist;
		} 
}
