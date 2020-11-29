package com.hjsj.hrms.businessobject.kq.team.historical;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class KqShiftClass implements KqClassArrayConstant,KqClassConstant{
    private Connection conn;
    private UserView userView;
    private float tiems=0;
    private String where_c;
    private ArrayList db_list=new ArrayList();
    // 是否是自助用户的排班
    private boolean self = false;
	public String getWhere_c() {
		return where_c;
	}
	public void setWhere_c(String where_c) {
		this.where_c = where_c;
	}
	public KqShiftClass()
	{		
	}
	public KqShiftClass(Connection conn,UserView userView)
	{
		this.conn=conn;
		this.userView=userView;
	}
	/**
	 * 通过周期班次得到对应基本班次的序号
	 * @param shift_id
	 * @return
	 */
	public ArrayList getClassIdFromShiftId(String shift_id)throws GeneralException
	{
		StringBuffer sql=new StringBuffer();
		sql.append("select "+this.kq_shift_class_classID);
		sql.append(" from "+this.kq_shift_class_table+"");
		sql.append(" where "+this.kq_shift_class_shiftID+"='"+shift_id+"'");
		sql.append(" order by "+this.kq_shift_class_seq);
		ArrayList list =new ArrayList();
		RowSet rs =null;
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				list.add(rs.getString(this.kq_shift_class_classID));
			}
		}catch(Exception e)
		{
		  e.printStackTrace();	
		  throw GeneralExceptionHandler.Handle(e);
		}finally{
	    	  if(rs!=null){
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
	 * class_id得到class_name
	 * @param class_id_list
	 * @return
	 */
	public ArrayList getClassNameFormClassID(ArrayList class_id_list)
	{
		ArrayList list=new ArrayList();
		CommonData dataobj = new CommonData();
		String sql="";
		for(int i=0;i<class_id_list.size();i++)
		{
			String class_id=list.get(i).toString();
			sql="select "+this.kq_class_name+" from "+this.kq_class_table+" where "+this.kq_class_id+"='"+class_id+"'";		
		}
		return list;
	}
	/**
	 * 增加周期班次
	 * @param shift_name
	 * @throws GeneralException
	 */
	public void addKqShift(String shift_name)throws GeneralException
	{
		if(shift_name==null||shift_name.length()<=0) {
            return;
        }
    	ArrayList list =new  ArrayList();
    	IDGenerator idg=new IDGenerator(2,this.conn);
    	ContentDAO dao=new ContentDAO(this.conn);
    	try
    	{
    		String shift_id=idg.getId(this.kq_shift_table+"."+this.kq_shift_ID).toUpperCase();
        	String sql="insert into "+this.kq_shift_table+" ("+this.kq_shift_ID+", "+this.kq_shift_name+") values (?,?)";
    	    list.add(shift_id);
    	    list.add(shift_name);
    	    dao.insert(sql,list);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e); 
    	}  
	}
	/**
	 * 修改周期班名称
	 * @param shift_name
	 * @param shift_id
	 * @throws GeneralException
	 */
	public void updateKqShiftName(String shift_name,String shift_id)throws GeneralException 
	{
		if(shift_name==null||shift_name.length()<=0) {
            return;
        }
		if(shift_id==null||shift_id.length()<=0) {
            return;
        }
		ContentDAO dao=new ContentDAO(this.conn);
		StringBuffer sql=new StringBuffer();
		sql.append("update "+this.kq_shift_table+" set");
		sql.append(" "+this.kq_shift_name+"='"+shift_name+"'");
		sql.append(" where "+this.kq_shift_ID+"='"+shift_id+"'");
		try
		{
		   dao.update(sql.toString());	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 修改周期班次的信息
	 * @param shift_name
	 * @param week_flag
	 * @param feast_flag
	 * @param shift_id
	 * @throws GeneralException
	 */
	public void updateKqOneShift(String shift_name,String week_flag,String feast_flag,String shift_id)throws GeneralException 
	{
		if(shift_id==null||shift_id.length()<=0) {
            return;
        }
		ContentDAO dao=new ContentDAO(this.conn);
		StringBuffer sql=new StringBuffer();
		sql.append("update "+this.kq_shift_table+" set");
		sql.append(" "+this.kq_shift_feast_flag+"='"+feast_flag+"',");
		sql.append(""+this.kq_shift_week_flag+"='"+week_flag+"',");
		sql.append(" where "+this.kq_shift_ID+"='"+shift_id+"'");
		try
		{
		   dao.update(sql.toString());	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void deleteKqShiftName(String shift_id)throws GeneralException 
	{
		
		if(shift_id==null||shift_id.length()<=0) {
            return;
        }
		ContentDAO dao=new ContentDAO(this.conn);
		StringBuffer sql=new StringBuffer();
		sql.append("delete from "+this.kq_shift_table+"");		
		sql.append(" where "+this.kq_shift_ID+"='"+shift_id+"'");
		ArrayList list =new ArrayList();
		try
		{
		   dao.delete(sql.toString(),list);	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public HashMap getCodeItem(String a_code)
	{
		HashMap hashmap=new HashMap();
		if(a_code!=null&&a_code.length()>0)
		{
			hashmap.put("codesetid",a_code.substring(0,2));
			hashmap.put("codeitemid",a_code.substring(3));
		}
		return hashmap;
	}
	
	public String returnShiftHtml(ArrayList datelist,String a_code,String nbase)throws GeneralException
	{
		StringBuffer html=new StringBuffer();
	    html.append("<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' class='ListTable'>");
	    html.append("<thead> <tr> ");
	    html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.sunday.short") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.monday.short") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.tuesday.short") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.wednesday.short") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.thursday.short") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.firday.short") + "</td>");
        html.append("<td align='center' class='TableRow' nowrap>" + ResourceFactory.getProperty("kq.kq_rest.Saturday.short") + "</td>");
	    html.append(" </tr></thead> ");	    
	    html.append(getDateHteml(datelist,a_code,nbase));
	    html.append("<tr>");
	    html.append("<td align='center' nowrap bgcolor='#CCCCFF' class='RecordRow common_background_color'> 合计（小时）</td>");
	    html.append("<td colspan='6' align='left' nowrap bgcolor='#FFFF99' class='RecordRow'>");
	    html.append("&nbsp;"+this.tiems+"</td></tr>");
	    html.append("</table>");
	    return html.toString();
	}
	/**
	 * 日历
	 * @param datelist
	 * @param a_code
	 * @param nbase
	 * @return
	 */
    private String getDateHteml(ArrayList datelist,String a_code,String nbase)throws GeneralException
    {
    	try
    	{
    	 StringBuffer html=new StringBuffer();
    	 int theRows=datelist.size()/7;
    	 int mod=datelist.size()%7;
    	 if(mod>0)
    	 {
    		 theRows=theRows+1;
    	 }
    	 String fristday=datelist.get(0).toString();
    	 String end_day=datelist.get(datelist.size()-1).toString();
    	 String flag="1";
 	     if("UN".equals(a_code)&&(where_c==null||where_c.length()<=0))
 	     {
 	    	flag="0";
 	     }else
 	     {
 	    	flag="1";
 	     }
    	 ArrayList	recordlist= getRecord(datelist.size(),fristday,end_day,a_code,nbase);
    	 
    	 Date date=DateUtils.getDate(fristday,"yyyy.MM.dd");
    	 String FirstDay = KqUtilsClass.getWeekName(date);
    	 if(datelist.size()==28)
    	 {
    		 if(!FirstDay.equalsIgnoreCase(ResourceFactory.getProperty("kq.kq_rest.sunday")))
        	 {
    			 theRows=theRows+1;
        	 }
    	 }    	
    	 String rest=KQRestOper.getRestStrTurn(FirstDay);
    	 if(rest.indexOf("7")!=-1)
    	 {
    		 rest="0,";
    	 }
    	 if(rest!=null&&rest.length()>1) {
             rest=rest.substring(0,1);
         }
    	 int theFirstDay=Integer.parseInt(rest);
    	 int theMonthLen=theFirstDay+datelist.size();
    	 if(7-theFirstDay<mod) {
             theRows=theRows+1;
         }
    	 int n=0;
    	 int day=0;
    	 String day_str="";
    	 String nbase1 = PubFunc.encrypt(nbase);
         String a_code1 = PubFunc.encrypt(a_code);
    	 for(int i=0;i<theRows;i++)
    	 {
    		 html.append("<tr>");
    		 for(int j=0;j<7;j++)
    		 {
    			 n++;
    			 if(n>theFirstDay&&n<=theMonthLen)
        		 {
        			 day=n-theFirstDay-1;
        			 day_str=datelist.get(day).toString();
        			 String tsd_str=getTdStr(recordlist,day_str,flag);
        			 String onDblClick="onDblClick=\"javascript:editClass('"+nbase1+"','"+a_code1+"','"+day_str+"')\"";
        			 html.append(getOneTd(tsd_str,onDblClick)); 
        		 }else
        		 {
        			 html.append(getOneTd("&nbsp;","")); 
        		 }
    		 }
    		 html.append("</tr>");
    	 }
     	 return html.toString();    	 
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		throw GeneralExceptionHandler.Handle(ex);
    	}

    }    
    /**
     * 得到纪录班次时间内的表格内容
     * @param day_str
     * @param recordlist
     * @param flag
     * @return
     * @throws GeneralException
     */
    
    private String getTdStr(ArrayList recordlist,String day_str,String flag)throws GeneralException
    {
    	StringBuffer str_html=new StringBuffer();    
    	if(!"3".equals(flag)) {
            str_html.append(day_str);
        }
    	String a0100="";
    	String nbase="";
    	if(!"0".equals(flag))
    	{    		
    		for(int i=0;i<recordlist.size();i++)
        	{
        		HashMap hashmap=(HashMap)recordlist.get(i);
        		String q03z0=hashmap.get("q03z0").toString();
        		if(day_str.equals(q03z0.trim()))
        		{
        			String n_a0100=(String)hashmap.get("a0100");
        			String n_nbase=(String)hashmap.get("nbase").toString();
        			if(a0100!=null&&a0100.length()>0)
        			{
        				if(!a0100.equals(n_a0100)) {
                            ;
                        }
        				break;
        			}else
        			{
        				a0100=n_a0100;
        			}
        			if(nbase!=null&&nbase.length()>0&&!"all".equalsIgnoreCase(nbase))
        			{
        				if(!nbase.equals(n_nbase)) {
                            ;
                        }
        				break;
        			}else
        			{
        				nbase=n_nbase;
        			}
        			String name=(String)hashmap.get("name");
            		String onduty_1=(String)hashmap.get("onduty_1");
            		String offduty_1=(String)hashmap.get("offduty_1");
            		String onduty_2=(String)hashmap.get("onduty_2");
            		String offduty_2=(String)hashmap.get("offduty_2");
            		String onduty_3=(String)hashmap.get("onduty_3");
            		String offduty_3=(String)hashmap.get("offduty_3");
            		String onduty_4=(String)hashmap.get("onduty_4");
            		String offduty_4=(String)hashmap.get("offduty_4");
            		str_html.append("<br>"+name);
            		if(onduty_1!=null&&onduty_1.length()>0&&offduty_1!=null&&offduty_1.length()>0)
            		{
            			//str_html.append("<br>"+onduty_1+"~"+offduty_1);
            			getWork_Time(onduty_1,offduty_1);
            		}
            		if(onduty_2!=null&&onduty_2.length()>0&&offduty_2!=null&&offduty_2.length()>0)
            		{
            			//str_html.append("<br>"+onduty_2+"~"+offduty_2);
            			getWork_Time(onduty_2,offduty_2);
            		}
            		if(onduty_3!=null&&onduty_3.length()>0&&offduty_3!=null&&offduty_3.length()>0)
            		{
            			//str_html.append("<br>"+onduty_3+"~"+offduty_3);
            			getWork_Time(onduty_3,offduty_3);
            		}  
            		/*if(onduty_4!=null&&onduty_4.length()>4&&offduty_4!=null&&offduty_4.length()>4)
            		{
            			str_html.append("<br>"+onduty_4+"~"+offduty_4);
            			getWork_Time(onduty_4,offduty_4);
            		}*/
        			break;
        		}
        	}
    	}else
    	{
    		str_html.append("<br><br>");
    	}   	
    	return str_html.toString();
    }   
    /**
     * 得到一条纪录班次时间内的表格内容
     * @param day_str
     * @param recordlist
     * @param flag
     * @return
     * @throws GeneralException
     */
    private String getTdStrOneRecord(String nbase,String a_code,String day_str,String flag)throws GeneralException
    {
    	StringBuffer str_html=new StringBuffer();    	
    	str_html.append(day_str);    
    	if("1".equals(flag))
    	{
    		String day_where=" and "+this.kq_employ_shift_q03z0+"='"+day_str+"'";
        	String sql=getJoinKqClass(nbase,a_code,day_where);
        	ContentDAO dao=new ContentDAO(this.conn);
        	RowSet rs=null;
        	try
        	{
        		rs=dao.search(sql);
        		if(rs.next())
        		{
        			String q03z0=rs.getString("q03z0");
        			String name=rs.getString("name");
        			String onduty_1=rs.getString("onduty_1");
        			String offduty_1=rs.getString("offduty_1");
        	   		String onduty_2=rs.getString("onduty_2");
        	   		String offduty_2=rs.getString("offduty_2");
        	   		String onduty_3=rs.getString("onduty_3");
        	   		String offduty_3=rs.getString("offduty_3");
        	   		String onduty_4=rs.getString("onduty_4");
        	   		String offduty_4=rs.getString("offduty_4");
        	   		str_html.append("<br>"+name);
            		/*if(onduty_1!=null&&onduty_1.length()>4&&offduty_1!=null&&offduty_1.length()>4)
            		{
            			str_html.append("<br>"+onduty_1+"~"+offduty_1);
            		}
            		if(onduty_2!=null&&onduty_2.length()>4&&offduty_2!=null&&offduty_2.length()>4)
            		{
            			str_html.append("<br>"+onduty_2+"~"+offduty_2);
            		}
            		if(onduty_3!=null&&onduty_3.length()>4&&offduty_3!=null&&offduty_3.length()>4)
            		{
            			str_html.append("<br>"+onduty_3+"~"+offduty_3);
            		} 
            		if(onduty_4!=null&&onduty_4.length()>4&&offduty_4!=null&&offduty_4.length()>4)
            		{
            			str_html.append("<br>"+onduty_4+"~"+offduty_4);
            		}*/
        		}else
        		{
        			str_html.append("<br><br>");	
        		}
        	}catch(Exception e)
        	{
        		e.printStackTrace();
        	}finally{
	    	  if(rs!=null){
	    		  try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  }
	      }    		
    	}else
    	{
    		str_html.append("<br><br>");
    	}   	
    	return str_html.toString();
    }
    /**
     * 一个表格
     * @param str
     * @return
     */
    private String getOneTd(String str,String onDblClick_str)
    {
    	StringBuffer str_html=new StringBuffer();
    	String name = "";
    	if (str != null && str.indexOf("<br>") != -1) 
		{
			int index = str.indexOf("<br>") + 4;
			name = str.substring(index);
		}
    	if("休息".equals(name))
    	{
    		if("sign".equals(onDblClick_str))
    		{    			
    			str_html.append("<td align='center' bgcolor='#99FF99' class='RecordRow3 common_border_color' style='border-left:none;' onClick=\"javascript:tr_onclick(this,'#99FF99')\" "+onDblClick_str+" nowrap>");
    		}else
    		{
    			str_html.append("<td align='center' bgcolor='#99FF99' class='RecordRow3 common_border_color'  onClick=\"javascript:tr_onclick(this,'#99FF99')\" "+onDblClick_str+" nowrap>");    			
    		}
        	str_html.append(str);    	
        	str_html.append("</td>");
    	}else
    	{
    		if("sign".equals(onDblClick_str))
    		{    			
    			str_html.append("<td align='center' class='RecordRow3 common_border_color' style='border-left:none;' onClick=\"javascript:tr_onclick(this,'')\" "+onDblClick_str+" nowrap>");
    		}else
    		{
    			str_html.append("<td align='center' class='RecordRow3 common_border_color' onClick=\"javascript:tr_onclick(this,'')\" "+onDblClick_str+" nowrap>");
    		}
        	str_html.append(str);    	
        	str_html.append("</td>");
    	}
    	return str_html.toString();
    }    
    /**
     * 得到员工排班的一个期间的的对应纪录
     * @param day_str
     * @param a_code
     * @param nbase
     * @return
     * @throws GeneralException
     */
    public ArrayList getRecord(int days,String start_day,String end_day,String a_code,String nbase)throws GeneralException
    {
    	ArrayList list=new ArrayList();  
    	StringBuffer day_where=new StringBuffer();    	
    	day_where.append(" and "+kq_employ_shift_q03z0+">='"+start_day+"'");
    	day_where.append(" and "+kq_employ_shift_q03z0+"<='"+end_day+"'");
    	String sql="";    	
    	ContentDAO dao=new ContentDAO(this.conn);
    	RowSet rs=null;
    	HashMap hashmap=null;
    	try
    	{
    		if(a_code.indexOf("EP")!=-1)
        	{
        	
    			sql=getJoinKqClass(nbase,a_code,day_where.toString());        		
        		rs=dao.search(sql,0,31);
        		int i=0;
        	   	while(rs.next())
        	   	{   
        	   		if(i>=days)
        	   		{
        	   			break;
        	   		}else
        	   		{
        	   			i++;
        	   		}
        	   		hashmap=new HashMap();    	   		
        	   		hashmap.put("q03z0",rs.getString("q03z0")!=null&&rs.getString("q03z0").length()>0?rs.getString("q03z0"):"");
        	   		hashmap.put("a0100",rs.getString("a0100")!=null&&rs.getString("a0100").length()>0?rs.getString("a0100"):"");
        	   		hashmap.put("nbase",rs.getString("nbase")!=null&&rs.getString("nbase").length()>0?rs.getString("nbase"):"");
        	   		hashmap.put("name",rs.getString("name")!=null&&rs.getString("name").length()>0?rs.getString("name"):"");
        	   		hashmap.put("onduty_1",rs.getString("onduty_1")!=null&&rs.getString("onduty_1").length()>0?rs.getString("onduty_1"):"");
        	   		hashmap.put("offduty_1",rs.getString("offduty_1")!=null&&rs.getString("offduty_1").length()>0?rs.getString("offduty_1"):"");
        	   		hashmap.put("onduty_2",rs.getString("onduty_2")!=null&&rs.getString("onduty_2").length()>0?rs.getString("onduty_2"):"");
        	   		hashmap.put("offduty_2",rs.getString("offduty_2")!=null&&rs.getString("offduty_2").length()>0?rs.getString("offduty_2"):"");
        	   		hashmap.put("onduty_3",rs.getString("onduty_3")!=null&&rs.getString("onduty_3").length()>0?rs.getString("onduty_3"):"");
        	   		hashmap.put("offduty_3",rs.getString("offduty_3")!=null&&rs.getString("offduty_3").length()>0?rs.getString("offduty_3"):"");
        	   		hashmap.put("onduty_4",rs.getString("onduty_4")!=null&&rs.getString("onduty_4").length()>0?rs.getString("onduty_4"):"");
        	   		hashmap.put("offduty_4",rs.getString("offduty_4")!=null&&rs.getString("offduty_4").length()>0?rs.getString("offduty_4"):"");
        	   		list.add(hashmap);
        	   	}
        	}else if(this.where_c!=null&&this.where_c.toLowerCase().indexOf("a0101")>0)
        	{
        		sql=getJoinKqClass(nbase,a_code,day_where.toString());        		
        		rs=dao.search(sql,0,31);
        		int i=0;
        	   	while(rs.next())
        	   	{   
        	   		if(i>=days)
        	   		{
        	   			break;
        	   		}else
        	   		{
        	   			i++;
        	   		}
        	   		hashmap=new HashMap();    	   		
        	   		hashmap.put("q03z0",rs.getString("q03z0")!=null&&rs.getString("q03z0").length()>0?rs.getString("q03z0"):"");
        	   		hashmap.put("a0100",rs.getString("a0100")!=null&&rs.getString("a0100").length()>0?rs.getString("a0100"):"");
        	   		hashmap.put("nbase",rs.getString("nbase")!=null&&rs.getString("nbase").length()>0?rs.getString("nbase"):"");
        	   		hashmap.put("name",rs.getString("name")!=null&&rs.getString("name").length()>0?rs.getString("name"):"");
        	   		hashmap.put("onduty_1",rs.getString("onduty_1")!=null&&rs.getString("onduty_1").length()>0?rs.getString("onduty_1"):"");
        	   		hashmap.put("offduty_1",rs.getString("offduty_1")!=null&&rs.getString("offduty_1").length()>0?rs.getString("offduty_1"):"");
        	   		hashmap.put("onduty_2",rs.getString("onduty_2")!=null&&rs.getString("onduty_2").length()>0?rs.getString("onduty_2"):"");
        	   		hashmap.put("offduty_2",rs.getString("offduty_2")!=null&&rs.getString("offduty_2").length()>0?rs.getString("offduty_2"):"");
        	   		hashmap.put("onduty_3",rs.getString("onduty_3")!=null&&rs.getString("onduty_3").length()>0?rs.getString("onduty_3"):"");
        	   		hashmap.put("offduty_3",rs.getString("offduty_3")!=null&&rs.getString("offduty_3").length()>0?rs.getString("offduty_3"):"");
        	   		hashmap.put("onduty_4",rs.getString("onduty_4")!=null&&rs.getString("onduty_4").length()>0?rs.getString("onduty_4"):"");
        	   		hashmap.put("offduty_4",rs.getString("offduty_4")!=null&&rs.getString("offduty_4").length()>0?rs.getString("offduty_4"):"");
        	   		list.add(hashmap);
        	   	}
        	}else
        	{
        		//kq_org_dept_shift
        		sql=getJoinKqOrgDeptClass(a_code,day_where.toString());
        		//System.out.println(sql);
        		rs=dao.search(sql,0,31);
        		int i=0;
        	   	while(rs.next())
        	   	{   
        	   		if(i>=days)
        	   		{
        	   			break;
        	   		}else
        	   		{
        	   			i++;
        	   		}
        	   		hashmap=new HashMap();    	   		
        	   		hashmap.put("q03z0",rs.getString("q03z0")!=null&&rs.getString("q03z0").length()>0?rs.getString("q03z0"):"");
        	   		hashmap.put("a0100",rs.getString("org_dept_id")!=null&&rs.getString("org_dept_id").length()>0?rs.getString("org_dept_id"):"");
        	   		hashmap.put("nbase","");
        	   		hashmap.put("name",rs.getString("name")!=null&&rs.getString("name").length()>0?rs.getString("name"):"");
        	   		hashmap.put("onduty_1",rs.getString("onduty_1")!=null&&rs.getString("onduty_1").length()>0?rs.getString("onduty_1"):"");
        	   		hashmap.put("offduty_1",rs.getString("offduty_1")!=null&&rs.getString("offduty_1").length()>0?rs.getString("offduty_1"):"");
        	   		hashmap.put("onduty_2",rs.getString("onduty_2")!=null&&rs.getString("onduty_2").length()>0?rs.getString("onduty_2"):"");
        	   		hashmap.put("offduty_2",rs.getString("offduty_2")!=null&&rs.getString("offduty_2").length()>0?rs.getString("offduty_2"):"");
        	   		hashmap.put("onduty_3",rs.getString("onduty_3")!=null&&rs.getString("onduty_3").length()>0?rs.getString("onduty_3"):"");
        	   		hashmap.put("offduty_3",rs.getString("offduty_3")!=null&&rs.getString("offduty_3").length()>0?rs.getString("offduty_3"):"");
        	   		hashmap.put("onduty_4",rs.getString("onduty_4")!=null&&rs.getString("onduty_4").length()>0?rs.getString("onduty_4"):"");
        	   		hashmap.put("offduty_4",rs.getString("offduty_4")!=null&&rs.getString("offduty_4").length()>0?rs.getString("offduty_4"):"");
        	   		list.add(hashmap);
        	   	}
        	}
        	
        	
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		//throw GeneralExceptionHandler.Handle(e);
    	}finally{
	    	  if(rs!=null){
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
    public String getCodeItemWhere(String a_code,String nbase)
	{
		String where="";
		String org_str="";
		if(a_code!=null&&a_code.length()>0)
		{
			String codesetid=a_code.substring(0,2);
			if("UN".equalsIgnoreCase(codesetid))
			{
				org_str="b0110";
			}else if("UM".equalsIgnoreCase(codesetid))
			{
				org_str="e0122";
			}else if("@K".equalsIgnoreCase(codesetid))
			{
				org_str="e01a1";
			}else if("GP".equalsIgnoreCase(codesetid))
			{
				org_str="a0100";
				if("GP".equals(a_code))
				{
					where=org_str+" in (select a0100 from kq_group_emp ";
					if(nbase!=null&&!"all".equalsIgnoreCase(nbase))
					{
						where=where+ " where nbase='"+nbase+"'";
					}
					where=where+")";
					return where;
				}
			}else if("EP".equalsIgnoreCase(codesetid))
			{
				org_str="a0100";
			}
			if(a_code.length()>=3)
			{
				String codeitemid=a_code.substring(2);				
				if(codeitemid!=null&&codeitemid.length()>0)
				{
					if(!"GP".equalsIgnoreCase(codesetid))
					{
						where=org_str+" like '"+codeitemid+"%'";
					}else
					{
						
						if(codeitemid==null||codeitemid.length()<=0)
						{
							String a0100s=getEmploys_Group(codeitemid);
							if(a0100s==null||a0100s.length()<=0) {
                                a0100s="''";
                            }
							where=org_str+" in ("+a0100s+")";
						}else
						{
							if(nbase!=null&&!"all".equalsIgnoreCase(nbase)) {
                                where=org_str+" in (select a0100 from kq_group_emp where nbase='"+nbase+"'  and group_id='"+codeitemid+"')";
                            } else {
                                where=org_str+" in (select a0100 from kq_group_emp where group_id='"+codeitemid+"')";
                            }
						}
						
					}
					
				}
			}			
		}		
		return where;
	}
    /**
     * 带机构的
     * @param a_code
     * @param nbase
     * @return
     */
    public String getCodeOrgItemWhere(String a_code)
	{
		String where="";
		String codesetid_v="";
		if(a_code!=null&&a_code.length()>0)
		{
			String codesetid=a_code.substring(0,2);
			if("UN".equalsIgnoreCase(codesetid))
			{
				codesetid_v="UN";
			}else if("UM".equalsIgnoreCase(codesetid))
			{
				codesetid_v="UM";
			}else if("@K".equalsIgnoreCase(codesetid))
			{
				codesetid_v="@K";
			}else if("GP".equalsIgnoreCase(codesetid))
			{
				codesetid_v="@G";
				
			}else if("EP".equalsIgnoreCase(codesetid))
			{
				//org_str="a0100";				
			}
			if(a_code.length()>=3)
			{
				String codeitemid=a_code.substring(2);				
				if(codeitemid!=null&&codeitemid.length()>0)
				{			
					if("EP".equalsIgnoreCase(codesetid))
					{
						where="a0100 = '"+codeitemid+"'";
					}else
					{
						where="upper(codesetid)='"+codesetid_v.toUpperCase()+"' and org_dept_id='"+codeitemid+"'";
					}
				  				
				}
			}			
		}		
		return where;
	}
    /**
     * 组合排班表和班次表
     * @param nbase
     * @param a_code
     * @return
     * @throws GeneralException
     */
    private String getJoinKqClass(String nbase,String a_code,String day_where)throws GeneralException
	{
    	 String code_where=getCodeItemWhere(a_code,nbase);
		 String ltable=kq_employ_shift_table_arc;//目标表
		 String rtable=kq_class_table;//源表		 
		 String lfield=ltable+"."+kq_employ_shift_classid;//源表的过滤条件  
		 String rfield=rtable+"."+kq_class_id;
		 /*SELECT Q03Z0,nbase,A0100,A0101,a.class_id,name,onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3
	        FROM kq_employ_shift a LEFT JOIN kq_class b 
	        ON a.class_id=b.class_id
	        WHERE a.Q03Z0>=:From AND a.Q03Z0<=:To*/
		 String join_str=Sql_switcher.left_join(ltable,rtable,lfield,rfield);		 
		 StringBuffer sql=new StringBuffer();
		 sql.append("select nbase,a0100,"+ltable+"."+kq_employ_shift_q03z0+" as q03z0,");
		 sql.append(rtable+"."+kq_class_name+" as name,");
		 sql.append("onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,onduty_4,offduty_4");
		 sql.append(" from "+ltable+" "+join_str);
		 sql.append(" where 1=1 ");
		 if(nbase!=null&&nbase.length()>0&&!"all".equalsIgnoreCase(nbase)) {
             sql.append(" and nbase='"+nbase+"'");
         }
		 if(day_where!=null&&day_where.length()>0)
		 {
			 sql.append(" "+day_where);
		 }		 
		 if(code_where!=null&&code_where.length()>0)
		 {
			 sql.append(" and "+code_where);
		 }	
		//29614 
//		 if(this.where_c!=null&&this.where_c.length()>0)
//			 sql.append(where_c);
		 if(!this.userView.isSuper_admin() && !self)
		 {
			 if(nbase!=null&&nbase.length()>0&&!"all".equalsIgnoreCase(nbase))
			 {
				 String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase); 
				 if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1) {
                     sql.append(" and  EXISTS(select a0100 "+whereIN+" and "+nbase+"A01.a0100="+ltable+".a0100)");
                 } else {
                     sql.append(" and  EXISTS(select a0100 "+whereIN+" where "+nbase+"A01.a0100="+ltable+".a0100)");
                 }
			 }
		 }		
		 sql.append(" order by a0100,"+kq_employ_shift_q03z0);			 
		 return sql.toString();
	}
    
    /**
     * 组合部门班次排班表和班次表
     * @param nbase
     * @param a_code
     * @return
     * @throws GeneralException
     */
    private String getJoinKqOrgDeptClass(String a_code,String day_where)throws GeneralException
	{
    	 String code_where=getCodeOrgItemWhere(a_code);
		 String ltable=kq_org_dept_shift_table_arc;//目标表
		 String rtable=kq_class_table;//源表		 
		 String lfield=ltable+"."+kq_employ_shift_classid;//源表的过滤条件  
		 String rfield=rtable+"."+kq_class_id;
		 /*SELECT Q03Z0,nbase,A0100,A0101,a.class_id,name,onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3
	        FROM kq_employ_shift a LEFT JOIN kq_class b 
	        ON a.class_id=b.class_id
	        WHERE a.Q03Z0>=:From AND a.Q03Z0<=:To*/
		 String join_str=Sql_switcher.left_join(ltable,rtable,lfield,rfield);		 
		 StringBuffer sql=new StringBuffer();
		 sql.append("select org_dept_id,"+ltable+"."+kq_employ_shift_q03z0+" as q03z0,");
		 sql.append(rtable+"."+kq_class_name+" as name,");
		 sql.append("onduty_1,offduty_1,onduty_2,offduty_2,onduty_3,offduty_3,onduty_4,offduty_4");
		 sql.append(" from "+ltable+" "+join_str);
		 sql.append(" where 1=1 ");		 
		 if(day_where!=null&&day_where.length()>0)
		 {
			 sql.append(" "+day_where);
		 }		 
		 if(code_where!=null&&code_where.length()>0)
		 {
			 sql.append(" and "+code_where);
		 }	
		 sql.append(" order by "+kq_employ_shift_q03z0);			 
		 return sql.toString();
	}
    /**
     * 计算时间
     * @param sb_time
     * @param xb_time
     * @return
     * @throws GeneralException 
     */
    public  float getWork_Time(String sb_time,String xb_time) throws GeneralException
	{
		float work_tiem=0;
		Date sb_T=DateUtils.getDate(sb_time,"HH:mm");
        Date xb_T=DateUtils.getDate(xb_time,"HH:mm");
        work_tiem=KQRestOper.toHourFormMinute(sb_T,xb_T);   
        if(work_tiem<0)
        {
        	sb_T=DateUtils.getDate("2007.03.08 "+sb_time,"yyyy.MM.dd HH:mm");
            xb_T=DateUtils.getDate("2007.03.09 "+xb_time,"yyyy.MM.dd HH:mm");
            work_tiem=KQRestOper.toHourFormMinute(sb_T,xb_T); 
        }
        this.tiems=this.tiems+work_tiem;
    	return work_tiem;
	}
    /**
     * 得到一个组的一个员工编号
     * @param parentid
     * @return
     */
    private String getEmploys_Group(String group_id)
    {
      StringBuffer strsql=new StringBuffer();   
      
	  strsql.append("select a0100,nbase ,a0101 from kq_group_emp");
	  strsql.append(" where group_id='"+group_id+"'");
	  ContentDAO dao=new ContentDAO(conn);
      RowSet rset=null;
      StringBuffer a0100s=new StringBuffer();
      try
      {
    	  rset=dao.search(strsql.toString());
    	  while(rset.next())
    	  {
    		  a0100s.append("'"+rset.getString("a0100")+"',");
    	  }
      }catch(Exception e)
      {
    	e.printStackTrace();  
      }finally{
    	  if(rset!=null){
    		  try {
				rset.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	  }
      }
      if(a0100s!=null&&a0100s.length()>0) {
          a0100s.setLength(a0100s.length()-1);
      }
      
      return a0100s.toString();
    }
    /**
     * 按纪录现实
     * @param datelist
     * @param a_code
     * @param db_list
     * @param curpage
     * @param pagesize
     * @return
     * @throws GeneralException
     */
    public String returnRecordHtml(ArrayList datelist,String a_code,ArrayList db_list,int curpage,int pagesize)throws GeneralException
	{
		StringBuffer html=new StringBuffer();
		html.append("<table border='0' cellspacing='0' cellpadding='0'><tr><td>");
		html.append("<div class='fixedDiv common_border_color' style='border:1px solid;'>");
		
	    html.append("<table width='100%' border='0' cellspacing='0'  align='center' cellpadding='0' style='border-collapse: collapse;'");
	    html.append("<thead> <tr> ");
	    html.append("<td align='center' class='TableRow' nowrap style='border-top:none;border-left:none;' width='70'> &nbsp;姓名&nbsp; </td>");	
	    for(int i=0;i<datelist.size();i++)
	    {
	    	html.append("<td align='center' class='TableRow' style='border-top:none;' nowrap width='70' onClick=\"javascript:tr_onclick(this,'')\"> &nbsp;"+datelist.get(i).toString()+"&nbsp; </td>");	
	    }
	    html.append(" </tr></thead> ");	    
	    html.append(getRecordDateHteml(datelist,a_code,db_list,curpage,pagesize));
	  
	    html.append("</table>");
	    return html.toString();
	}
    /**
     * 主体
     * @param datelist
     * @param a_code
     * @param db_list
     * @param curpage
     * @param pagesize
     * @return
     * @throws GeneralException
     */
    private String getRecordDateHteml(ArrayList datelist,String a_code,ArrayList db_list,int curpage,int pagesize)throws GeneralException
    {
    	 StringBuffer html=new StringBuffer();String fristday=datelist.get(0).toString();
    	 String end_day=datelist.get(datelist.size()-1).toString();    	
    	 int allrows=getRecordCount(db_list,a_code,fristday,end_day);
    	 //int allrows=count/datelist.size();
    	 int sum_page=(allrows-1)/pagesize+1;
		    curpage=getCurpage(curpage,pagesize,sum_page);
		 ArrayList a0100List= getA0100Record(fristday,end_day,a_code,db_list,pagesize,curpage);    	 
    	 for(int i=0;i<a0100List.size();i++)
    	 {
    		 html.append("<tr>");   
    		 HashMap hash=(HashMap)a0100List.get(i);
    		 String a0100=(String)hash.get("a0100");
    		 String nbase=(String)hash.get("nbase");
    		 String a0101=(String)hash.get("a0101");
    		 html.append(getOneTd(a0101,"sign")); 
    		 ArrayList recordlist=getRecord(datelist.size(),fristday,end_day,"EP"+a0100,nbase);
    		 String nbase1 = PubFunc.encrypt(nbase);
    		 String a01001 = PubFunc.encrypt("EP"+a0100);
    		 for(int j=0;j<datelist.size();j++)
    		 {
    			     String day_str=datelist.get(j).toString();
        			 String tsd_str=getTdStr(recordlist,day_str,"3");
        			 String onDblClick="onDblClick=\"javascript:editClass('"+nbase1+"','"+a01001+"','"+day_str+"')\"";
        			 html.append(getOneTd(tsd_str,onDblClick)); 
    		 }
    		 html.append("</tr>");
    	 } 
 	     html.append("</table></div>");
 	     html.append("</td></tr><tr><td><table width='100%' border='0' cellspacing='0' cellpadding='0'>");
 	     if(a0100List!=null&&a0100List.size()>0)
 	     {
 	    	 html.append("<tr>");
 	    	// html.append("<td align='center' nowrap class='TableRow'>&nbsp; </td>");
 	    	 html.append("<td colspan='31' align='left' nowrap class='TableRow' style='border-top:none;'>");
 	    	 html.append("<select name='curpage' size='1' onchange='javascript:change()'>");
 	    	 for(int i=1;i<=sum_page;i++)
 	    	 {
 	    		 if(i==curpage)
 	    		 {
 	    			 html.append("<option value='"+i+"' selected='selected'>第"+i+"页</option>");
 	    		 }else
 	    		 {
 	    			 html.append("<option value='"+i+"'>第"+i+"页</option>"); 
 	    		 }			 
 	    	 }	    
 	    	 html.append("</select>");
 	    	 html.append("</td></tr>");
 	     }    	 
 	     html.append("</table></td></tr>");
    	 return html.toString();
    }     
    /**
     * 当前页的纪录
     * @param start_day
     * @param end_day
     * @param a_code
     * @param db_list
     * @param pagesize
     * @param curpage
     * @return
     * @throws GeneralException
     */
    private ArrayList getA0100Record(String start_day,String end_day,String a_code,ArrayList db_list,int pagesize,int curpage)throws GeneralException
    {
    	 ArrayList list=new ArrayList();  
    	 StringBuffer day_where=new StringBuffer();    	
    	 day_where.append(" and "+kq_employ_shift_q03z0+">='"+start_day+"'");
    	 day_where.append(" and "+kq_employ_shift_q03z0+"<='"+end_day+"'");
    	 
    	 //29614 这里this.where_c是传的需要查询的值
 		 KqParameter para = new KqParameter(this.userView, "", this.conn);
         HashMap map = para.getKqParamterMap();
         String gnoField = (String) map.get("g_no");//工号指标
         String cardnoField= (String) map.get("cardno");//卡号指标
         StringBuffer selWhr = new StringBuffer();
         selWhr.append(" where ");
     	 selWhr.append(" A0101 like '%" + this.where_c + "%'");//按姓名查询
     	 selWhr.append(" or " + gnoField + " like '%" + this.where_c + "%'");//按工号查询
     	 selWhr.append(" or " + cardnoField + " like '%" + this.where_c + "%'");//按考勤卡号查询
     	
    	 String ltable=kq_employ_shift_table_arc;//目标表		  
		 StringBuffer sql=new StringBuffer();
    	 for(int i=0;i<db_list.size();i++)
    	 {
    		 String nbase=(String)db_list.get(i);
    		 String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,nbase);
    		 String code_where=getCodeItemWhere(a_code,(String)db_list.get(i));
    		 /*sql.append("select distinct nbase,a0100,a0101");		
    		 sql.append(" from "+ltable+" ");*/
    		 sql.append("select a0100,a0101,'"+nbase+"' nbase from "+ltable+" ");    		 
    		 sql.append(" where nbase='"+nbase+"'");
    		 if(day_where!=null&&day_where.length()>0)
    		 {
    			 sql.append(" "+day_where);
    		 }	
    		 if(code_where!=null&&code_where.length()>0)
    		 {
    			 sql.append(" and "+code_where);
    		 }
    		 sql.append(" and   a0100 in(select a0100 "+whereA0100In+") "); 
//    		 if(this.where_c!=null&&this.where_c.length()>=0)
//    			 sql.append(this.where_c);
    		//29614  记录这里this.where_c是传的需要查询的值
            if (StringUtils.isNotEmpty(this.where_c)){
            	 sql.append(" and (");
            	 sql.append(" A0100 IN (select A0100 from " + nbase + "A01");
            	 sql.append(selWhr.toString());
            	 sql.append(")");
            	 sql.append(")");
             }
             
    		 sql.append(" group  by a0100,a0101");
    		 sql.append(" union ");
    	 }
		 if(sql!=null&&sql.toString().length()>0) {
             sql.setLength(sql.length()-7);
         }
		 sql.append(" order by nbase,a0100");	    
    	 ContentDAO dao=new ContentDAO(this.conn);
    	 RowSet rs=null;
    	 HashMap hashmap=null;    	
    	 ArrayList keylist=new ArrayList();
	     //keylist.add("q03z0");
	     keylist.add("a0100");
	     keylist.add("nbase");	
	     //System.out.println(sql);
    	try
    	{
    		rs=dao.search(sql.toString(),pagesize,curpage);
    	   	while(rs.next())
    	   	{
    	   		hashmap=new HashMap();
    	   		
    	   		hashmap.put("a0100",rs.getString("a0100")!=null&&rs.getString("a0100").length()>0?rs.getString("a0100"):"");
    	   		hashmap.put("a0101",rs.getString("a0101")!=null&&rs.getString("a0101").length()>0?rs.getString("a0101"):"");
    	   		hashmap.put("nbase",rs.getString("nbase")!=null&&rs.getString("nbase").length()>0?rs.getString("nbase"):"");
    	   		list.add(hashmap);
    	   	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
    	} finally{
	    	  if(rs!=null){
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
     * 总数
     * @param db_list
     * @param a_code
     * @param start_day
     * @param end_day
     * @return
     * @throws GeneralException
     */
    private int getRecordCount(ArrayList db_list,String a_code,String start_day,String end_day)throws GeneralException
    {
    	int countI=0;    	
        ContentDAO dao = new ContentDAO(this.conn);
        String count=""; 
        RowSet rowSet=null;
        try{
        	//29614 记录这里this.where_c是传的需要查询的值
        	KqParameter para = new KqParameter(this.userView, "", this.conn);
            HashMap map = para.getKqParamterMap();
            String gnoField = (String) map.get("g_no");//工号指标
            String cardnoField= (String) map.get("cardno");//卡号指标
            StringBuffer selWhr = new StringBuffer();
            selWhr.append(" where ");
        	selWhr.append(" A0101 like '%" + this.where_c + "%'");//按姓名查询
        	selWhr.append(" or " + gnoField + " like '%" + this.where_c + "%'");//按工号查询
        	selWhr.append(" or " + cardnoField + " like '%" + this.where_c + "%'");//按考勤卡号查询
        	
      	    for(int i=0;i<db_list.size();i++)
    		{
    			String dbase=db_list.get(i).toString();
    			String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
    			//a0100whereIN.add(whereA0100In);	
    			String code_where=getCodeItemWhere(a_code,dbase);
    			StringBuffer sqlstr= new StringBuffer();
    			sqlstr.append("select count(a0100) a from ( ");
    	  	   /* sqlstr.append("select distinct A0100,nbase  from "+kq_employ_shift_table_arc);	  
    	  	    sqlstr.append(" where 1=1 ");  */
    			sqlstr.append("select a0100,a0101,'"+dbase+"' nbase from "+kq_employ_shift_table_arc);
    			sqlstr.append(" where  nbase='"+dbase+"' ");
    	  	    sqlstr.append(" and "+kq_employ_shift_q03z0+">='"+start_day+"'");
    	  	    sqlstr.append(" and "+kq_employ_shift_q03z0+"<='"+end_day+"'");	 
    	  	    
    	  	 	if(code_where!=null&&code_where.length()>0)
    	  	 	{
    	  	 		sqlstr.append(" and "+code_where);
    	  	 	}
//       		    if(this.where_c!=null&&this.where_c.length()>0)
//       		    	sqlstr.append(this.where_c);
    	  	 	//29614 
                if (StringUtils.isNotEmpty(this.where_c)){
                	sqlstr.append(" and (");
                	sqlstr.append(" A0100 IN (select A0100 from " + dbase + "A01");
                	sqlstr.append(selWhr.toString());
                	sqlstr.append(")");
                	sqlstr.append(")");
                }
                
    	  	    sqlstr.append(" and  a0100 in(select  a0100 "+whereA0100In+") "); 
    	  	    sqlstr.append(" group by a0100,a0101");
    	  		sqlstr.append(") aaaa"); 
    	  		rowSet = dao.search(sqlstr.toString());     
    	        if (rowSet.next())
    	        {
    	      	 count=rowSet.getString("a");
    	      	 countI=Integer.parseInt(count)+countI;
    	        }
    		}
        }catch(Exception e){
   	      throw GeneralExceptionHandler.Handle(e); 
        }finally{
	    	  if(rowSet!=null){
	    		  try {
	    			  rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  }
	      }
        return countI;
    }
    /**
     * 判断页码
     * 
     * */
    public int getCurpage(int curpage,int pagesize,int sum_page)
    {
    	if(curpage<=0)
    	{
    		curpage=1;
    	}else if(curpage>sum_page)
    	{
    		curpage=sum_page;
    	}
    	return curpage;
    }
	public ArrayList getDb_list() {
		return db_list;
	}
	public void setDb_list(ArrayList db_list) {
		this.db_list = db_list;
	}
	public boolean isSelf() {
		return self;
	}
	public void setSelf(boolean self) {
		this.self = self;
	}
	/**
     * 取得排班界面输入查询sql条件
     * @param select_flag
     * @param name 姓名或工号或卡号
     * @param nbase
     * @return 
     * @throws GeneralException
     */
	public String getSelWhere(String select_flag, String name, String nbase) throws GeneralException {
		com.hjsj.hrms.businessobject.kq.team.KqShiftClass kqShiftClass = new com.hjsj.hrms.businessobject.kq.team.KqShiftClass(this.conn, this.userView);
		return kqShiftClass.getSelWhere(select_flag, name, nbase);
	}
}
