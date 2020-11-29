package com.hjsj.hrms.businessobject.kq.register.history;


import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;
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


/**
 * 考勤日期
 * <p>Title:RegisterDate.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 13, 2006 1:34:41 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class RegisterDate {
   private static final String finished="0";
   public static String getKqDuration(Connection conn)throws GeneralException
   {
	   ContentDAO dao = new ContentDAO(conn);
		RowSet rowSet=null;
	   StringBuffer strsql = new StringBuffer();
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_start =(SELECT MIN(kq_start) from kq_duration where finished="+finished+")");
		strsql
				.append(" and kq_end =(select min(kq_end) from kq_duration where finished="+finished+")");
		
		String kq_duration="";		
		try {
			rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				String kq_year = rowSet.getString("kq_year");
				String duration = rowSet.getString("kq_duration");				
				kq_duration=kq_year+"-"+duration;					
				
			}else{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}
		} catch (Exception e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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
		return kq_duration;
   }
   public static int diffDate(Date d_start,Date d_end)
   {
 	  String start_date=DateUtils.format(d_start,"yyyy-MM-dd"); 
 	  String end_date=DateUtils.format(d_end,"yyyy-MM-dd"); 
 	  Date d1=DateUtils.getDate(start_date,"yyyy-MM-dd");  
   	  Date d2=DateUtils.getDate(end_date,"yyyy-MM-dd");  
   	  int diff=DateUtils.dayDiff(d1,d2);
 	  return diff;
   }
   
   /**
    * 返回未封存最小月的日期list(只包含第一天和最后一天)
    * @param finished 
    *        封存标签;1:未封存
    * @return
    *        返回未封存最小月的所有日期
    * */
   public static  ArrayList  getKqDayList(Connection conn) throws GeneralException  {
		String kq_start="";	 		
		RowSet rowSet=null;	 		
		
		StringBuffer strsql = new StringBuffer();
		ArrayList list= new ArrayList();
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_start =(SELECT MIN(kq_start) from kq_duration where finished="+finished+")");
		strsql
				.append(" and kq_end =(select min(kq_end) from kq_duration where finished="+finished+")");
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");	 						
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				kq_start = format1.format(d1);	 									
				list.add(kq_start);
				list.add(format1.format(d2));
			}else{
				//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}
		} catch (Exception e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return list;
	}
   /**
    * 返回未封存最小月的日期list(只包含第一天和最后一天)
    * @param conn
    *        数据库连接
    * @param kq_year
    *         考勤年
    * @param kq_duration
    *         考勤期
    * @return        list(只包含第一天和最后一天) 
    * */
   public static  ArrayList  getKqDayList(Connection conn,String kq_year,String kq_duration) throws GeneralException  {
		String kq_start="";	 		
		RowSet rowSet=null;	 		
		
		StringBuffer strsql = new StringBuffer();
		ArrayList list= new ArrayList();
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_year ='"+kq_year+"'");
		strsql
				.append(" and kq_duration ='"+kq_duration+"'and finished="+finished+"");
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");	 						
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				kq_start = format1.format(d1);	 									
				list.add(kq_start);
				list.add(format1.format(d2));
			}else{
				list=getKqDayList(conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return list;
	}
   /**
    * 得到一个考勤期间内所有的日期
    * */
   public static ArrayList getKqDurationList(Connection conn) throws GeneralException  {
		String kq_start;		 		
		RowSet rowSet=null;	 		
		int spacedate = 0;
		ArrayList datelist = new ArrayList();
		StringBuffer strsql = new StringBuffer();		
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_start =(SELECT MIN(kq_start) from kq_duration where finished="+finished+")");
		strsql
				.append(" and kq_end =(select min(kq_end) from kq_duration where finished="+finished+")");
		ContentDAO dao = new ContentDAO(conn);
		try {
			 rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");
				
				spacedate = DateUtils.dayDiff(d1,d2);				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");				             
                d1=DateUtils.addDays(d1,0);                
				for (int i = 0; i <=spacedate; i++) {					
					kq_start = format1.format(d1);					
					d1=DateUtils.addDays(d1,1);					
					datelist.add(kq_start);
				}				
			}else{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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
    * 得到未封存考勤时间
    * @param finished 
    *        封存标签：1.表示为封存
    * @return 返回未封存考勤时间，年月list
    * */
   public static  ArrayList sessionDate(Connection conn) throws GeneralException{
		String kq_year;		
		String kq_duration;		
		ArrayList courselist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT kq_year,kq_duration,kq_start FROM kq_duration");
		strsql.append("  where finished="+finished+"  order by kq_year,kq_duration");
		RowSet rowSet=null;	 
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(strsql.toString());			
			 while (rowSet.next()) {
				CommonData vo = new CommonData();
				kq_year = rowSet.getString("kq_year");
				kq_duration = rowSet.getString("kq_duration");				
				String kq_date=kq_year+"-"+kq_duration;
				kq_date=kq_date.trim();
				vo.setDataValue(kq_date);
				vo.setDataName(kq_date);
				courselist.add(vo);				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return courselist;
	}
   /**
    * 得到未封存考勤时间
    * @param finished 
    *        封存标签：1.表示为封存
    * @return 返回未封存考勤时间，年月list
    * */
   public static  ArrayList sessionDate(Connection conn,String finish) throws GeneralException{
		String kq_year;		
		String kq_duration;		
		ArrayList courselist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT kq_year,kq_duration,kq_start FROM kq_duration");
		strsql.append("  where finished="+finish+" order by kq_year desc,kq_duration desc");
		RowSet rowSet=null;	 
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(strsql.toString());			
			 while (rowSet.next()) {
				CommonData vo = new CommonData();
				kq_year = rowSet.getString("kq_year");
				kq_duration = rowSet.getString("kq_duration");				
				String kq_date=kq_year+"-"+kq_duration;
				kq_date=kq_date.trim();
				vo.setDataValue(kq_date);
				vo.setDataName(kq_date);
				courselist.add(vo);				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return courselist;
	}
   public static  ArrayList yearDate(Connection conn,String finish) throws GeneralException{
		String kq_year;		
		String kq_duration;		
		ArrayList courselist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT distinct kq_year FROM kq_duration");
		strsql.append("  where finished="+finish+" order by kq_year desc");
		RowSet rowSet=null;	 
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(strsql.toString());			
			 while (rowSet.next()) {
				CommonData vo = new CommonData();
				kq_year = rowSet.getString("kq_year");
				kq_year=kq_year.trim();
				vo.setDataValue(kq_year);
				vo.setDataName(kq_year);
				courselist.add(vo);				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return courselist;
	}
   public static  ArrayList durationDate(Connection conn,String finish,String cur_year) throws GeneralException{
		String kq_year;		
		String kq_duration;		
		ArrayList courselist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT distinct kq_duration FROM kq_duration");
		strsql.append("  where finished="+finish+" and kq_year="+cur_year+" order by kq_duration desc");
		RowSet rowSet=null;	 
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(strsql.toString());			
			 while (rowSet.next()) {
				CommonData vo = new CommonData();
				kq_duration = rowSet.getString("kq_duration");				
				kq_duration=kq_duration.trim();
				vo.setDataValue(kq_duration);
				vo.setDataName(kq_duration);
				courselist.add(vo);				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return courselist;
	}   
   /**
    * 返回未封存最小月的日期list
    * @param conn 
    *        封存标签;1:未封存
    * @param duration
    *         考勤期间
    * @return
    *        返回未封存最小月的所有日期
    * */
   public static ArrayList getKqDate(Connection conn,String cur_course) throws GeneralException  {		
		String kq_start;
	    String kq_end;	
	    String kq_year="";
		String kq_duration="";
		ArrayList list = new ArrayList();
		if(cur_course.indexOf("-")>1)
		{
			String[] course= cur_course.split("-");
			kq_year=course[0];
			kq_duration=course[1];
		}
		if(kq_year==null||kq_year.length()<=0||kq_duration==null||kq_duration.length()<=0)
		{
			 ArrayList sessionlist=sessionDate(conn);
			 CommonData vo=(CommonData)sessionlist.get(0);
			 String cur_date=vo.getDataValue();
			 if(cur_date.indexOf("-")>1)
			 {
					String[] course= cur_date.split("-");
					kq_year=course[0];
					kq_duration=course[1];
			 }
		}
		StringBuffer strsql = new StringBuffer();		
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_year ='"+kq_year+"'");
		strsql
				.append(" and kq_duration ='"+kq_duration+"'and finished="+finished+"");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowSet=null;	
		try {
			rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				kq_start = format1.format(d1);
			    kq_end = format1.format(d2);		
				list.add(kq_start);
				list.add(kq_end);			
			}else{
				list=getKqDayList(conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return list;
	}
   /**
    * 通过考勤期间
    * 得到一个考勤期间内所有的日期
    * @param cur_course 当前考勤期间
    * @return datelist
    * */
   public static ArrayList getOneDurationDateList(Connection conn,String cur_course) throws GeneralException  
   {
	    String kq_start;	    	
	    String kq_year="";
		String kq_duration="";		
		if(cur_course.indexOf("-")>1)
		{
			String[] course= cur_course.split("-");
			kq_year=course[0];
			kq_duration=course[1];
		}	 		
		RowSet rowSet=null;	 		
		int spacedate = 0;
		ArrayList datelist = new ArrayList();
		StringBuffer strsql = new StringBuffer();		;		
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_year ='"+kq_year+"'");
		strsql
				.append(" and kq_duration ='"+kq_duration+"'and finished="+finished+"");
		ContentDAO dao = new ContentDAO(conn);
		try {
			 rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");
				
				spacedate = DateUtils.dayDiff(d1,d2);				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");				             
                d1=DateUtils.addDays(d1,0);                
				for (int i = 0; i <=spacedate; i++) {					
					kq_start = format1.format(d1);					
					d1=DateUtils.addDays(d1,1);					
					datelist.add(kq_start);
				}				
			}else{
				datelist=getKqDurationList(conn);
				if(datelist==null||datelist.size()<=0) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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
    * 通过考勤期间
    * 得到一个考勤期间内所有的日期
    * @param cur_course 当前考勤期间
    * @return datelist
    * */
   public static ArrayList getOneSealDurationDateList(Connection conn,String cur_course,String finish) throws GeneralException  
   {
	    String kq_start;	    	
	    String kq_year="";
		String kq_duration="";		
		if(cur_course.indexOf("-")>1)
		{
			String[] course= cur_course.split("-");
			kq_year=course[0];
			kq_duration=course[1];
		}	 		
		RowSet rowSet=null;	 		
		int spacedate = 0;
		ArrayList datelist = new ArrayList();
		StringBuffer strsql = new StringBuffer();				
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_year ='"+kq_year+"'");
		strsql
				.append(" and kq_duration ='"+kq_duration+"'and finished="+finish+"");
		ContentDAO dao = new ContentDAO(conn);
		try {
			 rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");
				
				spacedate = DateUtils.dayDiff(d1,d2);				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");				             
                d1=DateUtils.addDays(d1,0);                
				for (int i = 0; i <=spacedate; i++) {					
					kq_start = format1.format(d1);					
					d1=DateUtils.addDays(d1,1);					
					datelist.add(kq_start);
				}				
			}else{
				datelist=getKqDurationList(conn);
				if(datelist==null||datelist.size()<=0) {
                    throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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
     * 得到两个给定日期中间的所有日起
     * @param  start_date 开始日期
     * @param end_date  结束日期
     * @return  datelist
     * */
   public static ArrayList getDis_DateList(String start_date,String end_date)
   {
	   ArrayList datelist = new ArrayList();
	   String date;
	   Date d1=DateUtils.getDate(start_date,"yyyy-MM-dd");
	   Date d2=DateUtils.getDate(end_date,"yyyy-MM-dd");
	   int spacedate = DateUtils.dayDiff(d1,d2);
	   SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");				             
       d1=DateUtils.addDays(d1,0);  
       for (int i = 0; i <=spacedate; i++) 
       {					
    	    date = format1.format(d1);					
			d1=DateUtils.addDays(d1,1);					
			datelist.add(date);
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
   public static ArrayList registerdate(String b0110,Connection conn,UserView userView) throws GeneralException  {
		String kq_start;				
		String kqStrdate;
		int spacedate = 0;
		ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,conn);
		String rest_date=restList.get(0).toString();
		String rest_b0110=restList.get(1).toString();		
		ArrayList datelist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_start =(SELECT MIN(kq_start) from kq_duration where finished="+finished+")");
		strsql
				.append(" and kq_end =(select min(kq_end) from kq_duration where finished="+finished+")");
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
					String rest=IfRestDate.is_RestDate(kq_start,userView,rest_date,rest_b0110,conn);
					
					vo.setDataValue(kq_start);
					kqStrdate = KqUtilsClass.getWeekName(d1);

					kqStrdate = kq_start + " " + kqStrdate + ""+rest;
					vo.setDataName(kqStrdate);
					
					datelist.add(vo);
					
					d1 = DateUtils.addDays(d1,1);					
				}	
				
			}else{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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
    * 返回以封存最大月的日期list(只包含第一天和最后一天)
    * @param finished 
    *        封存标签;1:以封存
    * @return
    *        返回未封存最小月的所有日期
    * */
   public static  ArrayList  getKqSealMaxDayList(Connection conn) throws GeneralException  {
		String kq_start="";	 		
		RowSet rowSet=null;	 		
		
		StringBuffer strsql = new StringBuffer();
		ArrayList list= new ArrayList();
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_start =(SELECT MAX(kq_start) from kq_duration where finished=1)");
		strsql
				.append(" and kq_end =(select MAX(kq_end) from kq_duration where finished=1)");
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");	 						
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				kq_start = format1.format(d1);	 									
				list.add(kq_start);
				list.add(format1.format(d2));
			}else
			{
				list=getKqDayList(conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return list;
	}
   /**
    * 得到考勤的年
    * @param conn
    * @return
    * @throws GeneralException
    */
   public static ArrayList getKqYear(Connection conn) throws GeneralException  {		
		
	    ArrayList list = new ArrayList();
		String kq_year="";
		StringBuffer strsql = new StringBuffer();		
		strsql.append("SELECT DISTINCT kq_year FROM kq_duration order by kq_year desc");
		
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowSet=null;	
		try {
			rowSet = dao.search(strsql.toString());
			while (rowSet.next())
			{				
				kq_year=rowSet.getString("kq_year");
				CommonData vo = new CommonData();
				vo.setDataName(kq_year);
				vo.setDataValue(kq_year);
				list.add(vo);		
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return list;
	}
   public static String getKqMonth(Connection conn,String cur_course) throws GeneralException  {		
		String gz_year;
	    String gz_duration;	
	    String kq_year="";
		String kq_duration="";
		String month=null;
		int num=0;		
		if(cur_course.indexOf("-")>1)
		{
			String[] course= cur_course.split("-");
			kq_year=course[0];
			kq_duration=course[1];
		}
		StringBuffer strsql = new StringBuffer();		
		strsql
				.append("SELECT gz_year,gz_duration FROM kq_duration");
		strsql
				.append(" where kq_year ='"+kq_year+"'");
		strsql
				.append(" and kq_duration ='"+kq_duration+"'");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowSet=null;				
		try {
			rowSet = dao.search(strsql.toString());
			if (rowSet.next())
			{				
				gz_year=rowSet.getString("gz_year");
				gz_duration=rowSet.getString("gz_duration");	
				month=gz_year+"-"+gz_duration+"-01";
			}else
			{
				month=cur_course+"-01";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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
		
		return month;
	}
 /**
  * 通过考勤期间得到本期间是同一个月中第几个期间
  * @param conn
  * @param cur_course
  * @return
  * @throws GeneralException
  */
   public static String getKqNum(Connection conn,String cur_course) throws GeneralException  {		
		String gz_year;
	    String gz_duration;	
	    String kq_year="";
		String kq_duration="";
		int num=0;		
		if(cur_course.indexOf("-")>1)
		{
			String[] course= cur_course.split("-");
			kq_year=course[0];
			kq_duration=course[1];
		}
		StringBuffer strsql = new StringBuffer();		
		strsql
				.append("SELECT gz_year,gz_duration FROM kq_duration");
		strsql
				.append(" where kq_year ='"+kq_year+"'");
		strsql
				.append(" and kq_duration ='"+kq_duration+"'");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowSet=null;	
		RowSet row=null;	
		try {
			rowSet = dao.search(strsql.toString());
			if (rowSet.next())
			{				
				gz_year=rowSet.getString("gz_year");
				gz_duration=rowSet.getString("gz_duration");
				if(gz_year!=null&&gz_year.length()>0&&gz_duration!=null&&gz_duration.length()>0)
				{
					String duration=null;
					String year=null;
					StringBuffer sql=new StringBuffer();
					sql.append("SELECT kq_year,kq_duration from kq_duration");
					sql.append(" where gz_year='"+gz_year+"' and gz_duration='"+gz_duration+"'");
					sql.append("order by kq_duration");
					row=dao.search(sql.toString());
					while(row.next())
					{
						num=num+1;
						year=row.getString("kq_year");
						duration=row.getString("kq_duration");
						if(year.equals(kq_year)&&duration.equals(kq_duration))
						{
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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
		    if(row!=null) {
                try {
                        row.close();
                } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
            }
	     }
		if(num==0) {
            num=1;
        }
		return num+"";
	}
   /**
    * 通过考勤期间
    * 得到一个考勤期间内所有的日期
    * @param cur_course 当前考勤期间
    * @return datelist
    * */
   public static ArrayList getOneDurationDate(Connection conn,String cur_course) throws GeneralException  
   {
	    String kq_year="";
		String kq_duration="";		
		if(cur_course.indexOf("-")>1)
		{
			String[] course= cur_course.split("-");
			kq_year=course[0];
			kq_duration=course[1];
		}	 		
		RowSet rowSet=null;		
		ArrayList datelist = new ArrayList();
		StringBuffer strsql = new StringBuffer();			
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_year ='"+kq_year+"'");
		strsql
				.append(" and kq_duration ='"+kq_duration+"'");
		ContentDAO dao = new ContentDAO(conn);
		try {
			 rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");							
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");				             
				datelist.add(format1.format(d1));
				datelist.add(format1.format(d2));              
							
			}else{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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
   
   public static String getDurationFromDate(String date_str,Connection conn)
   {
	   String char_date=Sql_switcher.dateValue(date_str);
	   StringBuffer sql=new StringBuffer();
	   sql.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
	   sql.append(" where kq_start< ="+char_date+"");
	   sql.append(" and kq_end >="+char_date+"");	   
	   String year="";
	   String duration="";
	   RowSet rowSet=null;
	   ContentDAO dao = new ContentDAO(conn);
		try {
			 rowSet = dao.search(sql.toString());
			 if(rowSet.next())
			 {
				 year=rowSet.getString("kq_year");
				 duration=rowSet.getString("kq_duration");
			 }
		}catch(Exception e)
		{
			e.printStackTrace();
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
		return year+"-"+duration;
   }
   /**
    * 得到封存的最小年
    * @param conn
    */
   public static String getNoSealMinYear(Connection conn)
   {
	    String kq_year="";
		StringBuffer strsql = new StringBuffer();		
		strsql.append("SELECT DISTINCT kq_year FROM kq_duration");
		strsql.append(" where finished="+finished+"");
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(conn);
		try 
		{
			rowSet = dao.search(strsql.toString());
			if(rowSet.next())
			{
					 kq_year=rowSet.getString("kq_year");					
			}
		}catch(Exception e)
		{
				e.printStackTrace();
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
		return kq_year;	
   }
   /**
    * 得到未封存的最小年最小期间
    * @param conn
    */
   public static String getNoSealDuration(Connection conn,String kq_year)
   {
	    String kq_duration="";
		StringBuffer strsql = new StringBuffer();		
		strsql.append("SELECT DISTINCT kq_duration FROM kq_duration");
		strsql.append(" where finished="+finished+"");
		strsql.append(" and kq_year='"+kq_year+"'");
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(conn);
		try 
		{
			rowSet = dao.search(strsql.toString());
			if(rowSet.next())
			{
				kq_duration=rowSet.getString("kq_duration");					
			}
		}catch(Exception e)
		{
				e.printStackTrace();
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
		return kq_duration;	
   }
   public static  ArrayList sessionYaer(Connection conn,String finish) throws GeneralException{
		String kq_year;		
		String kq_duration;		
		ArrayList courselist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT DISTINCT kq_year FROM kq_duration");
		strsql.append("  where finished="+finish+" order by kq_year  desc");
		RowSet rowSet=null;	 
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(strsql.toString());			
			 while (rowSet.next()) {
				CommonData vo = new CommonData();
				kq_year = rowSet.getString("kq_year");
				vo.setDataValue(kq_year);
				vo.setDataName(kq_year);
				courselist.add(vo);				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return courselist;
	}
   
   public static  ArrayList arcYaer(Connection conn,String finish) throws GeneralException{
		String kq_year;		
		String kq_duration;		
		ArrayList courselist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		String q03z0=Sql_switcher.year(Sql_switcher.charToDate("q03z0"));
        //strsql.append("select k.kq_year from kq_org_dept_shift_arc s ");
		//strsql.append("inner join kq_duration k on k.kq_year="+q03z0+" and finished='"+finish+"' ");
		//strsql.append("union select k.kq_year from kq_employ_shift_arc a ");
		//strsql.append("inner join kq_duration k on k.kq_year="+q03z0+" and finished='"+finish+"' ");
		
		strsql.append("select kq_year from (select kq_year from kq_duration k where exists (select 1 from kq_org_dept_shift_arc a");
		strsql.append(" where k.kq_year="+q03z0+") and finished='"+finish+"' group by kq_year");
		strsql.append(" union ");
		strsql.append("select kq_year from kq_duration k where exists (select 1 from kq_employ_shift_arc a");
		strsql.append(" where k.kq_year="+q03z0+") and finished='"+finish+"' group by k.kq_year");
		strsql.append(") y order by kq_year");
		
		RowSet rowSet=null;	 
		ContentDAO dao = new ContentDAO(conn);
		try {
			rowSet = dao.search(strsql.toString());			
			 while (rowSet.next()) {
				CommonData vo = new CommonData();
				kq_year = rowSet.getString("kq_year");
				vo.setDataValue(kq_year);
				vo.setDataName(kq_year);
				courselist.add(vo);				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.pigeonhole.no.tabledata"),"",""));
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

		return courselist;
	}
   /**
    * 得到指定年的指定期间
    * @param conn
    * @param kq_year
    * @param finish
    * @return
    */
   public static ArrayList getSealDurationList(Connection conn,String kq_year,String finish)
   {
	    ArrayList list=new ArrayList();
		StringBuffer strsql = new StringBuffer();		
		strsql.append("SELECT DISTINCT kq_duration FROM kq_duration");
		strsql.append(" where finished="+finish+"");
		strsql.append(" and kq_year='"+kq_year+"'");
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(conn);
		try 
		{
			String kq_duration="";
			rowSet = dao.search(strsql.toString());
			while(rowSet.next())
			{
				CommonData vo = new CommonData();
				kq_duration = rowSet.getString("kq_duration");
				vo.setDataValue(kq_duration);
				vo.setDataName(kq_duration);
				list.add(vo);	
			}
		}catch(Exception e)
		{
				e.printStackTrace();
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
		return list;	
   }
   
   public static ArrayList arcDuration(Connection conn,String kq_year,String finish)
   {
	    ArrayList list=new ArrayList();
		StringBuffer strsql = new StringBuffer();
		String q03z0=Sql_switcher.month(Sql_switcher.charToDate("q03z0"));
       // strsql.append("select k.kq_duration from kq_org_dept_shift_arc s ");
		//strsql.append("inner join kq_duration k on k.kq_duration="+q03z0+" and k.kq_year='"+kq_year+"' and finished='"+finish+"' ");
		//strsql.append("union select k.kq_duration from kq_employ_shift_arc a ");
		//strsql.append("inner join kq_duration k on k.kq_duration="+q03z0+" and k.kq_year='"+kq_year+"' and finished='"+finish+"'");
		
		strsql.append("select kq_duration from (select kq_duration from kq_duration k where exists (select 1 from kq_org_dept_shift_arc a");
		strsql.append(" where k.kq_duration="+q03z0+") and k.kq_year='"+kq_year+"' and finished='"+finish+"' group by kq_duration");
		strsql.append(" union ");
		strsql.append("select kq_duration from kq_duration k where exists (select 1 from kq_employ_shift_arc a");
		strsql.append(" where k.kq_duration="+q03z0+") and k.kq_year='"+kq_year+"' and finished='"+finish+"' group by k.kq_duration");
		strsql.append(") y order by kq_duration");
		
		RowSet rowSet=null;
		ContentDAO dao = new ContentDAO(conn);
		try 
		{
			String kq_duration="";
			rowSet = dao.search(strsql.toString());
			while(rowSet.next())
			{
				CommonData vo = new CommonData();
				kq_duration = rowSet.getString("kq_duration");
				vo.setDataValue(kq_duration);
				vo.setDataName(kq_duration);
				list.add(vo);	 
			}
		}catch(Exception e)
		{
				e.printStackTrace();
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
		return list;	
   }
   public static ArrayList getKqDate(Connection conn,String cur_course,int finished) throws GeneralException  {		
		String kq_start;
	    String kq_end;	
	    String kq_year="";
		String kq_duration="";
		ArrayList list = new ArrayList();
		if(cur_course.indexOf("-")>1)
		{
			String[] course= cur_course.split("-");
			kq_year=course[0];
			kq_duration=course[1];
		}
		StringBuffer strsql = new StringBuffer();		
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_year ='"+kq_year+"'");
		strsql
				.append(" and kq_duration ='"+kq_duration+"'and finished="+finished+"");
		ContentDAO dao = new ContentDAO(conn);
		RowSet rowSet=null;	
		try {
			rowSet = dao.search(strsql.toString());
			if (rowSet.next()) {				
				Date d1 = rowSet.getDate("kq_start");
				Date d2 = rowSet.getDate("kq_end");
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
				kq_start = format1.format(d1);
			    kq_end = format1.format(d2);		
				list.add(kq_start);
				list.add(kq_end);			
			}else{
				list=getKqDayList(conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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

		return list;
	}
   /**
    * 通过考勤期间
    * 得到一个考勤期间内所有的日期
    * @param cur_course 当前考勤期间
    * @return datelist
    * */
   public static ArrayList getOneDurationDateList(String b0110,UserView userView,Connection conn,String cur_course) throws GeneralException  
   {
	    String kq_start;	    	
	    String kq_year="";
		String kq_duration="";
		String kqStrdate;
		ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,conn);
		String rest_date=restList.get(0).toString();
		if(cur_course.indexOf("-")>1)
		{
			String[] course= cur_course.split("-");
			kq_year=course[0];
			kq_duration=course[1];
		}	 		
		RowSet rowSet=null;	 		
		int spacedate = 0;
		ArrayList datelist = new ArrayList();
		StringBuffer strsql = new StringBuffer();				
		strsql
				.append("SELECT kq_year,kq_duration,kq_start,kq_end FROM kq_duration");
		strsql
				.append(" where kq_year ='"+kq_year+"'");
		strsql
				.append(" and kq_duration ='"+kq_duration+"'and finished="+finished+"");
		ContentDAO dao = new ContentDAO(conn);
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
					
					d1 = DateUtils.addDays(d1,1);						
				}				
			}else{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));
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
    * 得到所有年
    * @param conn
    * @return
    * @throws GeneralException
    */
   public ArrayList getAllYearListVo(Connection conn)throws GeneralException  
   {
	   ArrayList yearlist = new ArrayList();
	   ContentDAO year_dao = new ContentDAO(conn);
	    StringBuffer year_str=new StringBuffer();
		year_str.append("select distinct kq_year from kq_duration");
		RowSet rs=null;
		try {
			
			rs = year_dao.search(year_str.toString());
			while (rs.next()) {
				CommonData yearvo = new CommonData(rs.getString("kq_year"),rs.getString("kq_year"));
				yearlist.add(yearvo);
				}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}finally
	     {
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     }
		return  yearlist;
   }
   /**
    * 得到一年的考勤期间的list
    * @param kq_year
    * @param conn
    * @return 如：01(2007.01.01-2007.01.31)
    * @throws GeneralException
    */
   public ArrayList getOneYearDuration(String kq_year,Connection conn)throws GeneralException
   {
	    ContentDAO duration_dao = new ContentDAO(conn);
	    StringBuffer duration_str=new StringBuffer();
		duration_str.append("select  kq_duration,kq_start,kq_end from kq_duration where kq_year='");
		duration_str.append(kq_year);
		duration_str.append("'");
		ArrayList durationlist=new ArrayList();
		RowSet rs=null;
		try {
			
			rs = duration_dao.search(duration_str.toString());  
			CommonData durationvo=null;
			while (rs.next()) {
				durationvo= new CommonData();
				durationvo.setDataName(rs.getString("kq_duration")+'('+ PubFunc.FormatDate(rs.getDate("kq_start")).replaceAll("-","\\.")+'-'+PubFunc.FormatDate(rs.getDate("kq_end")).replaceAll("-","\\.")+')');
				durationvo.setDataValue(kq_year+"-"+rs.getString("kq_duration"));
				durationlist.add(durationvo);
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}finally
	     {
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 
		return durationlist;
   }
   /**
    * 得到一年中最小的未封存的考勤期间
    * @param kq_year
    * @param conn
    * @return
    * @throws GeneralException
    */
   public ArrayList getYearMinYearList (String kq_year,Connection conn) throws GeneralException
   {
        StringBuffer sbu=new StringBuffer();
		ArrayList dlist=new ArrayList();
		ContentDAO duration_dao = new ContentDAO(conn);
		sbu.append("select  kq_duration,kq_start,kq_end from kq_duration where ");
		sbu.append(" kq_year='"+kq_year+"'");
		sbu.append(" and kq_duration=(select min(kq_duration)  from kq_duration  where finished='0' and kq_year='"+kq_year+"')");
		RowSet rs=null;
		try {
			
			rs = duration_dao.search(sbu.toString()); 
			CommonData durationvo=null;
			while (rs.next()) {
				durationvo= new CommonData();
				durationvo.setDataName(rs.getString("kq_duration")+'('+ PubFunc.FormatDate(rs.getDate("kq_start")).replaceAll("-","\\.")+'-'+PubFunc.FormatDate(rs.getDate("kq_end")).replaceAll("-","\\.")+')');
				durationvo.setDataValue(kq_year+"-"+rs.getString("kq_duration"));
				dlist.add(durationvo);
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}finally
	    {
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     } 	
		return dlist;		
	}
   /**
    * 通过开始结束日期，得到考勤期间
    * @param start_date
    * @param end_date
    * @param conn
    * @return
    */
   public ArrayList getKq_duration(String start_date,String end_date,Connection conn)
   {
	   ArrayList list=new ArrayList();
	   ContentDAO dao = new ContentDAO(conn);
	   StringBuffer sbu=new StringBuffer();
	   sbu.append("select  kq_year,kq_duration from kq_duration where ");
	   sbu.append(" kq_start>="+Sql_switcher.dateValue(start_date)+"");
	   sbu.append(" and kq_end<="+Sql_switcher.dateValue(end_date)+"");
	   RowSet rs=null;
	   try
	   {
		   rs=dao.search(sbu.toString());
		   while(rs.next())
		   {
			   list.add(rs.getString("kq_year")+"-"+rs.getString("kq_duration"));
		   }
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally
	     {
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     }
	   return list;
   }
   /**
    * 通过一天得到这个考勤期间的第一天和最后一天
    * @param date
    * @param conn
    * @return
    */
   public static ArrayList getKq_duration(String date,Connection conn)
   {
	   ArrayList list=new ArrayList();
	   ContentDAO dao = new ContentDAO(conn);
	   StringBuffer sbu=new StringBuffer();
	   sbu.append("select  kq_start,kq_end from kq_duration where ");
	   sbu.append(" kq_start<="+Sql_switcher.dateValue(date)+"");
	   sbu.append(" and kq_end>="+Sql_switcher.dateValue(date)+"");
	   RowSet rs=null;
	   try
	   {
		   rs=dao.search(sbu.toString());
		   if(rs.next())
		   {
			   Date d1 = rs.getDate("kq_start");
			   Date d2 = rs.getDate("kq_end");
			   SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");	
			   list.add(format1.format(d1));
			   list.add(format1.format(d2));
		   }
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }finally
	     {
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
	     }
	   return list;
   }
   
}
