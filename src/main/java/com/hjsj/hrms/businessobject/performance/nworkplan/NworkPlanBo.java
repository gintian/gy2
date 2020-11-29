package com.hjsj.hrms.businessobject.performance.nworkplan;

import com.hjsj.hrms.businessobject.performance.nworkplan.week.WeekWorkPlanBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * NworkPlanBo.java
 * Description: 国网工作计划和总结
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Feb 28, 2013 8:50:09 PM Jianghe created
 */
public class NworkPlanBo {
	private Connection con = null;
	private UserView userView = null;
	private String nbase = "Usr";
	private String a0100 = "";
	public static HashMap workParametersMap = new HashMap();//参数采用静态变量，不用每次都查。
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
	WeekUtils weekutils = new WeekUtils();
	public NworkPlanBo()
	{
		
	}
	public NworkPlanBo(Connection con)
	{
		this.con=con;
	}
	public NworkPlanBo(UserView userView,Connection con)
	{
		this.userView=userView;
		this.con=con;
	}
	public NworkPlanBo(Connection con,UserView userView,String nbase,String a0100) throws GeneralException, SQLException
	{
		this.con = con;
		this.userView = userView;
		this.nbase = nbase;
		this.a0100 = a0100;
	}
	/**
	 * 分析设置的用于工作计划和总结的参数
	 */
	public void initParam()
	{
		
		RowSet rs =null;
		try
		{
			workParametersMap.clear();
            ContentDAO dao = new ContentDAO(this.con);	
          /*  < work_records  sp_relation=”xxx” nbase="Usr" sp_level="2" record_grade="True" planTarget="P0413,P0415" summTarget="P04Aa,P04Ac" defaultLines="12"
           * 	dailyPlan_attachment=”true” dailySumm_attachment =”true” >   
           * 	//sp_relation:审批关系 nbase:人员库设置  sp_level: 审批层级 1|2 一级审批|逐级审批  record_grade: 纪实评分，True|False  defaultLines:  默认行数
           * 	planTarget: 工作计划指标 ，分隔  summTarget: 工作总结指标 ，分隔  dailyPlan_attachment:工作计划附件上传  dailySumm_attachment：工作总结附件上传
                cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
                type:(1|2)=(工作计划|工作总结)  
        		valid:是否有效（1|0）=（有效|无效）
                prior_end:上期末 current_start:本期初  current_end:本期末 
                last_start:下期初   Period:填报周期  refer_id:参考信息登记表
                print_id:打印信息登记表
                summarize_fields:xxxx1,xxxx2 =总结有效指标 
                plan_fields=”xxxx1,xxx2” =计划有效指标
	    	    sp_flag: (0|1)=(不审批|审批)
        		<work_record     cycle=”3” plan_fields=”xxxx1,xxx2” summarize_fields=“xxxx1,xxxx2”> 
        	        <template type="2"  valid=”1” sp_flag=”1” prior_end=”3”  current_start=”5” Period=” 1,2,3,4,5,6,7,8,9,10,11,12”  refer_id=”1”  print_id=”2” >
        	    </work_record>

        	    <work_record     cycle=”4” plan_fields=”xxxx1,xxx2” summarize_fields=“xxxx1,xxxx2”> //日报  current_date:（1|0）=（当日|次日）
                   <template type="0"  valid=”1” sp_flag=”0” current_date=”1” time=”8:30” refer_id=”1”  print_id=”2” > 
                </work_record>
            < work_records />*/				
			rs = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if ( rs.next())
		    {
		    	String str_value = rs.getString("str_value");
		    	if (str_value == null || str_value.trim().length()<=0)
		    	{
	
		    	} else
		    	{
		    		Document doc = PubFunc.generateDom(str_value);
		    		String xpath = "//work_records";
		    		XPath xpath_ = XPath.newInstance(xpath);
		    		Element ele = (Element) xpath_.selectSingleNode(doc);
		    		if (ele != null)
		    		{
		    			String sp_relation = ele.getAttributeValue("sp_relation");
		    			workParametersMap.put("sp_relation", sp_relation);
		    			String nbase = ele.getAttributeValue("nbase");	
		    			workParametersMap.put("nbase", nbase);		    			
		    			workParametersMap.put("sp_level", ele.getAttributeValue("sp_level"));
		    			workParametersMap.put("record_grade", ele.getAttributeValue("record_grade"));
		    			workParametersMap.put("defaultLines", ele.getAttributeValue("defaultLines"));		    			
		    			workParametersMap.put("dailyPlan_attachment", ele.getAttributeValue("dailyPlan_attachment"));
		    			workParametersMap.put("dailySumm_attachment", ele.getAttributeValue("dailySumm_attachment"));
		    			workParametersMap.put("planTarget", ele.getAttributeValue("planTarget"));
		    			workParametersMap.put("summTarget", ele.getAttributeValue("summTarget"));		    			
		    			List list = (List) ele.getChildren("work_record");
	    				for (int i = 0; i < list.size(); i++)
	    				{   					
	    					Element wele = (Element) list.get(i);;     						   	    					
	    					if (wele != null)
	    		    	    {	    							    						
	    						String cycle = wele.getAttributeValue("cycle"); 
	    						String plan_fields = isNull(wele.getAttributeValue("plan_fields")); 
	    						String summarize_fields = isNull(wele.getAttributeValue("summarize_fields"));
	    						workParametersMap.put("plan_fields"+cycle, plan_fields);
	    						workParametersMap.put("summarize_fields"+cycle, summarize_fields);
	    		    	    	/*
	    		    	    	   cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
	    		    	    	   summarize_fields:xxxx1,xxxx2 =总结有效指标 
                                   plan_fields=”xxxx1,xxx2” =计划有效指标
	    		    	    	   sp_flag: (0|1)=(不审批|审批)
	    		    	    	   type:(1|2)=(工作计划|工作总结)  valid:是否有效（1|0）=（有效|无效）
 								   prior_end:上期末 current_start:本期初  current_end:本期末 
								   last_start:下期初   Period:填报周期  refer_id:参考信息登记表
								   print_id:打印信息登记表
	    		    	    	*/
	    		    	    	
	    		    	    	List dlist = (List) wele.getChildren("template");
	    		    	    	for (int k = 0; k < dlist.size(); k++)
	    		    	    	{    	    		    
	    		    	    		Element temp = (Element) dlist.get(k);
	    		    	    		if(temp!=null)
	    		    	    		{	 	    		    	    			
	    		    	    			String type = isNull(temp.getAttributeValue("type"));		    		    	    		
		    		    	    		
		    		    	    		if("4".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//年计划
		    		    	    		{
		    		    	    			String valid14 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String sp_flag14 = isNull(temp.getAttributeValue("sp_flag"));
		    		    	    			String prior_end14 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start14 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String refer_id14 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id14 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid14", valid14);
		    		    	    			workParametersMap.put("sp_flag14", sp_flag14);
		    		    	    			workParametersMap.put("prior_end14", prior_end14);
		    		    	    			workParametersMap.put("current_start14", current_start14);
		    		    	    			workParametersMap.put("refer_id14", refer_id14);
		    		    	    			workParametersMap.put("print_id14", print_id14);
		    		    	    		}
		    		    	    		else if("4".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//年总结
		    		    	    		{
		    		    	    			String valid24 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String sp_flag24 = isNull(temp.getAttributeValue("sp_flag"));
		    		    	    			String current_end24 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start24 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String refer_id24 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id24 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid24", valid24);
		    		    	    			workParametersMap.put("sp_flag24", sp_flag24);
		    		    	    			workParametersMap.put("current_end24", current_end24);
		    		    	    			workParametersMap.put("last_start24", last_start24);
		    		    	    			workParametersMap.put("refer_id24", refer_id24);
		    		    	    			workParametersMap.put("print_id24", print_id24);
		    		    	    		}
		    		    	    		else if("3".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//季计划
		    		    	    		{
		    		    	    			String valid13 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String sp_flag13 = isNull(temp.getAttributeValue("sp_flag"));
		    		    	    			String prior_end13 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start13 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String period13 = isNull(temp.getAttributeValue("period"));
		    		    	    			String refer_id13 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id13 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid13", valid13);
		    		    	    			workParametersMap.put("sp_flag13", sp_flag13);
		    		    	    			workParametersMap.put("prior_end13", prior_end13);
		    		    	    			workParametersMap.put("current_start13", current_start13);
		    		    	    			workParametersMap.put("period13", period13);
		    		    	    			workParametersMap.put("refer_id13", refer_id13);
		    		    	    			workParametersMap.put("print_id13", print_id13);
		    		    	    		}
		    		    	    		else if("3".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//季总结
		    		    	    		{
		    		    	    			String valid23 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String sp_flag23 = isNull(temp.getAttributeValue("sp_flag"));
		    		    	    			String current_end23 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start23 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String period23 = isNull(temp.getAttributeValue("period"));	    	    			
		    		    	    			String refer_id23 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id23 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid23",valid23);
		    		    	    			workParametersMap.put("sp_flag23",sp_flag23);
		    		    	    			workParametersMap.put("current_end23",current_end23);
		    		    	    			workParametersMap.put("last_start23",last_start23);
		    		    	    			workParametersMap.put("period23",period23);
		    		    	    			workParametersMap.put("refer_id23",refer_id23);
		    		    	    			workParametersMap.put("print_id23",print_id23);
		    		    	    		}
		    		    	    		else if("2".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//月计划
		    		    	    		{
		    		    	    			String valid12 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String sp_flag12 = isNull(temp.getAttributeValue("sp_flag"));
		    		    	    			String prior_end12 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start12 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String period12 = isNull(temp.getAttributeValue("period"));		    		    	    			
		    		    	    			String refer_id12 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id12 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid12",valid12);
		    		    	    			workParametersMap.put("sp_flag12",sp_flag12);
		    		    	    			workParametersMap.put("prior_end12",prior_end12);
		    		    	    			workParametersMap.put("current_start12",current_start12);
		    		    	    			workParametersMap.put("period12",period12);
		    		    	    			workParametersMap.put("refer_id12",refer_id12);
		    		    	    			workParametersMap.put("print_id12",print_id12);
		    		    	    		}
		    		    	    		else if("2".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//月总结
		    		    	    		{
		    		    	    			String valid22 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String sp_flag22 = isNull(temp.getAttributeValue("sp_flag"));
		    		    	    			String current_end22 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start22 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String period22 = isNull(temp.getAttributeValue("period"));
		    		    	    			String refer_id22 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id22 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid22", valid22);
		    		    	    			workParametersMap.put("sp_flag22", sp_flag22);
		    		    	    			workParametersMap.put("current_end22", current_end22);
		    		    	    			workParametersMap.put("last_start22", last_start22);
		    		    	    			workParametersMap.put("period22", period22);
		    		    	    			workParametersMap.put("refer_id22", refer_id22);
		    		    	    			workParametersMap.put("print_id22", print_id22);		    		    	    			
		    		    	    		}
		    		    	    		else if("1".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//周计划
		    		    	    		{
		    		    	    			String valid11 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String sp_flag11 = isNull(temp.getAttributeValue("sp_flag"));
		    		    	    			String prior_end11 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start11 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String refer_id11 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id11 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid11", valid11);
		    		    	    			workParametersMap.put("sp_flag11", sp_flag11);
		    		    	    			workParametersMap.put("prior_end11", prior_end11);
		    		    	    			workParametersMap.put("current_start11", current_start11);
		    		    	    			workParametersMap.put("refer_id11", refer_id11);
		    		    	    			workParametersMap.put("print_id11", print_id11);
		    		    	    		}
		    		    	    		else if("1".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//周总结
		    		    	    		{
		    		    	    			String valid21 = isNull(temp.getAttributeValue("valid"));		    		    	    		
		    		    	    			String sp_flag21 = isNull(temp.getAttributeValue("sp_flag"));		    		    	    		
		    		    	    			String current_end21 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start21 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String refer_id21 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id21 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid21", valid21);		    		    	    		
		    		    	    			workParametersMap.put("sp_flag21", sp_flag21);		    		    	    		
		    		    	    			workParametersMap.put("current_end21", current_end21);
		    		    	    			workParametersMap.put("last_start21", last_start21);
		    		    	    			workParametersMap.put("refer_id21", refer_id21);
		    		    	    			workParametersMap.put("print_id21", print_id21);
		    		    	    		}
		    		    	    		else if("0".equalsIgnoreCase(cycle))
		    		    	    		{
		    		    	    			String valid0 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String sp_flag0 = isNull(temp.getAttributeValue("sp_flag"));
		    		    	    			String current_date = isNull(temp.getAttributeValue("current_date"));
		    		    	    			String time = isNull(temp.getAttributeValue("time"));
		    		    	    			String refer_id0 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id0 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid0", valid0);
		    		    	    			workParametersMap.put("sp_flag0", sp_flag0);
		    		    	    			workParametersMap.put("current_date", current_date);
		    		    	    			workParametersMap.put("time", time);
		    		    	    			workParametersMap.put("refer_id0", refer_id0);
		    		    	    			workParametersMap.put("print_id0", print_id0);
		    		    	    			
		    		    	    		}
		    		    	    		
			    		    	    }
	    		    	    	}	    		    	    		    		    	    		    		    	    	
	    		    	    }
	    		    	}																									    			
		    		}
		    	}
		    } 
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str)) {
            str = "";
        }
		return str;
    }
	public String isNullToZero(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str)) {
            str = "0";
        }
		return str;
    }
	/**
	 * Description: 获取传入时间的工作总结的p0100
	 * @Version1.0 
	 * Mar 7, 2013 6:22:48 PM Jianghe created
	 * @param currentYear
	 * @param currentMonth
	 * @param state
	 * @return
	 */
	public String getSummarizeP0100(String currentYear,String currentMonth,String state,String personPage,String isChuZhang,String opt,String belong_type){
		GregorianCalendar cal_start = new GregorianCalendar();
		cal_start.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
		cal_start.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
		cal_start.set(GregorianCalendar.DAY_OF_MONTH, 1);
		
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rs = null;
		String startDate = sdf.format(cal_start.getTime());
		String thep0100=null;
		try
		{
			if("1".equals(opt)){
				if("0".equals(personPage)){
					if("0".equals(isChuZhang)|| "2".equals(isChuZhang)) {
                        rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and a0100='"+this.a0100+"' and nbase='"+this.nbase+"' and log_type=2 and state="+state+" and (belong_type=0 or belong_type is null)");
                    } else if("1".equals(isChuZhang)) {
                        rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and nbase='"+this.nbase+"' and log_type=2 and state="+state+" and ((belong_type=1 and e0122='"+this.getUserDetail(this.nbase+this.a0100, "e0122")+"') or ( (belong_type=0 or belong_type is null) and a0100='"+this.a0100+"'))");
                    }
				}else if("1".equals(personPage)){
					rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122='"+getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122"))+"' and nbase='"+this.nbase+"' and log_type=2 and state="+state+" and belong_type=2");
				}
			}else if("2".equals(opt)){
				if("0".equals(isChuZhang)){
					if("0".equals(belong_type)){//从人员进
						rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and a0100='"+userView.getA0100()+"' and nbase='"+userView.getDbname()+"' and log_type=2 and state="+state+" and belong_type="+belong_type);
					}else if("1".equals(belong_type)){//从处室
						rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122='"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"' and log_type=2 and state="+state+" and belong_type="+belong_type);
					}else if("2".equals(belong_type)){//从部门
						rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122='"+this.getParentE0122(this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122"))+"' and log_type=2 and state="+state+" and belong_type="+belong_type);
					}
				}else if("1".equals(isChuZhang)){
	                if("0".equals(belong_type)){//从人员进
	                	rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122 like'"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"%' and log_type=2 and state="+state+" and belong_type="+belong_type);
					}else if("1".equals(belong_type)){//从处室
						rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122='"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"' and log_type=2 and state="+state+" and belong_type="+belong_type);
					}else if("2".equals(belong_type)){//从部门
						rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122='"+this.getParentE0122(this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122"))+"' and log_type=2 and state="+state+" and belong_type="+belong_type);
					}
				}else if("2".equals(isChuZhang)){
	                if("0".equals(belong_type)){//从人员进
	                	rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122 like '"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"%' and log_type=2 and state="+state+" and belong_type="+belong_type);
					}else if("1".equals(belong_type)){//从处室
						rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122 like '"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"%' and log_type=2 and state="+state+" and belong_type="+belong_type);
					}else if("2".equals(belong_type)){//从部门
						rs = dao.search("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122='"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"' and log_type=2 and state="+state+" and belong_type="+belong_type);
						//System.out.println("select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+" and e0122='"+this.getUserDetail(this.nbase+this.a0100, "e0122")+"' and log_type=2 and state="+state+" and belong_type="+belong_type);
					}
				}
			}
	    	if(rs.next()){
	    		thep0100 = String.valueOf(rs.getInt("p0100"));
	    	}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
	    return thep0100;
	}
	/**
	 * Description: 获取报批给自己的上期的p0100（汇总用）
	 * @Version1.0 
	 * Mar 7, 2013 6:22:48 PM Jianghe created
	 * @param currentYear
	 * @param currentMonth
	 * @param state
	 * @return
	 */
	public String getGatherP0100(String currentYear,String currentMonth,String state,String personPage,String curr_user){
		GregorianCalendar cal_start = new GregorianCalendar();
		cal_start.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
		cal_start.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
		cal_start.set(GregorianCalendar.DAY_OF_MONTH, 1);
		
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rs = null;
		String startDate = sdf.format(cal_start.getTime());
		String thep0100="";
		try
		{
			String sql = "";
			if("0".equals(personPage)){
				//curruser p0115 e0122 belong_type=0 or belong_type is null
				sql = "select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+"  and nbase='"+this.nbase+"' and log_type=2 and e0122='"+this.getUserDetail(nbase+a0100, "e0122")+"' and state="+state+" and p0115='02' and (belong_type=0 or belong_type is null)"+" and curr_user='"+curr_user+"'";
			}else if("1".equals(personPage)){
				//
			    String c01sc = getUserDetail(nbase+a0100, "c01sc");
			    if("03".equals(c01sc)|| "04".equals(c01sc)){
			    	sql = "select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+"  and log_type=2 and state="+state+" and p0115='01' and belong_type=0  and a0100='"+a0100+"' and nbase='"+nbase+"'";
			    	//System.out.println(sql);
			    	rs = dao.search(sql);
			    	while(rs.next()){
			    		RecordVo vo = new RecordVo("p01");
			    		vo.setInt("p0100", rs.getInt("p0100"));
			    		vo = dao.findByPrimaryKey(vo);
			    		vo.setString("p0115", "02");
			    		vo.setInt("belong_type", 1);
			    		dao.updateValueObject(vo);
			    	}
			    	sql = "select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+"  and log_type=2 and state="+state+" and p0115='02' and belong_type=1  and ((curr_user='"+curr_user+"' or e0122='"+this.getUserDetail(nbase+a0100, "e0122")+"') or (a0100='"+a0100+"' and nbase='"+nbase+"'))";
			    	//System.out.println(sql);
			    }else{
			    	sql = "select p0100 from p01 where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate+"  and log_type=2 and state="+state+" and p0115='02' and belong_type=1  and (curr_user='"+curr_user+"' or e0122='"+this.getUserDetail(nbase+a0100, "e0122")+"')";
			    }
			}
			if(!"".equals(sql)) {
                rs = dao.search(sql);
            }
			while(rs.next()){
				thep0100 += String.valueOf(rs.getInt("p0100"))+",";
			}
			if(!"".equals(thep0100)){
				thep0100 = thep0100.substring(0,thep0100.length()-1);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return thep0100;
	}
	public String getP0115(String p0100){
		ContentDAO dao = new ContentDAO(this.con);
		String status = "";
		try
		{
			RecordVo vo = new RecordVo("p01");
			vo.setInt("p0100", Integer.parseInt(p0100));
			vo = dao.findByPrimaryKey(vo);
			status = vo.getString("p0115");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return status;
	}
	/**
	 * Description: 移动记录顺序
	 * @Version1.0 
	 * Mar 5, 2013 1:51:37 PM Jianghe created
	 * @param theP0100
	 * @param theRecord_num
	 * @param theSeq
	 * @param moveflag
	 */
	public void moveRecord(String theP0100,String theRecord_num,String theSeq,String moveflag,String theLogtype){
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rs = null;
		if("up".equals(moveflag)){
			//向上移动
			try
			{
				String previousP0100 = theP0100;
				String previousRecord_num = "";
				String previousSeq = "";
				rs = dao.search("select record_num,seq from per_diary_content where p0100="+theP0100+" and log_type="+theLogtype+" and seq=(select max(seq) from per_diary_content where seq<"+theSeq+" and p0100="+theP0100+" and log_type="+theLogtype+")");
		    	if(rs.next()){
		    		previousRecord_num = String.valueOf(rs.getInt("record_num"));
		    		previousSeq = String.valueOf(rs.getInt("seq"));
		    	}
		    	dao.update(" update per_diary_content set seq="+previousSeq+" where p0100="+theP0100+" and log_type="+theLogtype+" and record_num="+theRecord_num);
		    	dao.update(" update per_diary_content set seq="+theSeq+" where p0100="+theP0100+" and log_type="+theLogtype+" and record_num="+previousRecord_num);
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
		    		e.printStackTrace();
				}
			}
		}else{
			//向下移动
			try
			{
				String nextP0100 = theP0100;
				String nextRecord_num = "";
				String nextSeq = "";
				rs = dao.search("select record_num,seq from per_diary_content where p0100="+theP0100+" and log_type="+theLogtype+" and seq=(select min(seq) from per_diary_content where seq>"+theSeq+" and p0100="+theP0100+" and log_type="+theLogtype+")");
		    	if(rs.next()){
		    		nextRecord_num = String.valueOf(rs.getInt("record_num"));
		    		nextSeq = String.valueOf(rs.getInt("seq"));
		    	}
		    	dao.update(" update per_diary_content set seq="+nextSeq+" where p0100="+theP0100+" and log_type="+theLogtype+" and record_num="+theRecord_num);
		    	dao.update(" update per_diary_content set seq="+theSeq+" where p0100="+theP0100+" and log_type="+theLogtype+" and record_num="+nextRecord_num);
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
				}catch(Exception e){
		    		e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Description: 月报页面显示数据
	 * @Version1.0 
	 * Mar 5, 2013 3:33:02 PM Jianghe created
	 * @param p0100
	 * @param log_type
	 * @param validFields
	 * @return
	 */
	public ArrayList getDataList(String p0100,String log_type,String validFields){
		ContentDAO dao = new ContentDAO(this.con);
		RowSet rowSet = null;
		ArrayList list = new ArrayList();
		
		
		ArrayList fieldItemList = DataDictionary.getFieldList("PER_DIARY_CONTENT", Constant.USED_FIELD_SET);
		if(validFields!=null&&!"".equals(validFields.trim())) {
            validFields = ","+validFields+",";
        }
		try
		{
			//System.out.println("select * from per_diary_content where p0100="+Integer.parseInt(p0100)+" and log_type="+log_type+" order by seq asc");
			rowSet = dao.search("select * from per_diary_content where p0100="+Integer.parseInt(p0100)+" and log_type="+log_type+" order by seq asc");
			int count = 1;
			while(rowSet.next()){
				LazyDynaBean bean = new LazyDynaBean();
				for(int i=0;i<fieldItemList.size();i++)
				{
					FieldItem fielditem = (FieldItem)fieldItemList.get(i);
					String fielditemValue = "";
					String fieldId = fielditem.getItemid();
					if(!"0".equals(fielditem.getState())&&validFields.toUpperCase().indexOf((","+fieldId.toUpperCase()+","))!=-1){
						if("N".equalsIgnoreCase(fielditem.getItemtype())){
							fielditemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
							fielditemValue=PubFunc.round(fielditemValue,fielditem.getDecimalwidth());
						}else if("A".equalsIgnoreCase(fielditem.getItemtype())){
							if("0".equals(fielditem.getCodesetid())){
								fielditemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
							}else{
								fielditemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
								fielditemValue = AdminCode.getCodeName(fielditem.getCodesetid(),fielditemValue);
							}
						}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
							    Date date = rowSet.getDate(fieldId);
							    fielditemValue = sdf2.format(date);
						}else if("M".equalsIgnoreCase(fielditem.getItemtype())){
							   fielditemValue = Sql_switcher.readMemo(rowSet, fieldId)==null?"":Sql_switcher.readMemo(rowSet, fieldId);
						}else{
							fielditemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
						}
						bean.set(fieldId, fielditemValue);
						bean.set(fieldId+"_1", SafeCode.encode(fielditemValue));
						bean.set("itemtype", fielditem.getItemtype().toUpperCase());
					}
				}
				bean.set("p0100", rowSet.getInt("p0100")+"");
        		bean.set("record_num", rowSet.getInt("record_num")+"");
        		bean.set("b0110", rowSet.getString("b0110"));
        		bean.set("e0122", rowSet.getString("e0122"));
        		bean.set("e01a1", rowSet.getString("e01a1"));
        		bean.set("nbase", rowSet.getString("nbase"));
        		bean.set("a0100", rowSet.getString("a0100"));
        		bean.set("a0101", rowSet.getString("a0101"));
        		bean.set("type", rowSet.getInt("type")+"");
        		bean.set("log_type", rowSet.getInt("log_type")+"");
        		bean.set("seq", rowSet.getInt("seq")+"");
        		bean.set("count", String.valueOf(count));
			    String start_time = sdf2.format(rowSet.getDate("start_time"));
			    String end_time = sdf2.format(rowSet.getDate("end_time"));
			    bean.set("start_time", start_time);
			    bean.set("end_time", end_time);
			    
        		count++;
        		list.add(bean);
	    	}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * Description: 计划或修改的指标list
	 * @Version1.0 
	 * Mar 5, 2013 3:33:46 PM Jianghe created
	 * @param type
	 * @param log_type
	 * @param planFields
	 * @return
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	public ArrayList getJihuaOrZongjieFieldsList(String p0100,String record_num,String type,String log_type,String validFields) throws GeneralException, SQLException{
		ArrayList list = new ArrayList();
		ArrayList fieldItemList = DataDictionary.getFieldList("PER_DIARY_CONTENT", Constant.USED_FIELD_SET);
		if(validFields!=null&&!"".equals(validFields.trim())) {
            validFields = ","+validFields+",";
        }
		for(int i=0;i<fieldItemList.size();i++)
		{
			FieldItem fielditem = (FieldItem)fieldItemList.get(i);
			String itemid=fielditem.getItemid();
			String itemtype=fielditem.getItemtype().toUpperCase();
			String codesetid=fielditem.getCodesetid();
			String itemdesc=fielditem.getItemdesc();
			String itemlength=String.valueOf(fielditem.getItemlength());
			String decimalwidth=String.valueOf(fielditem.getDecimalwidth());
			String state=fielditem.getState();
			String isfillable=fielditem.isFillable()==true?"1":"0";
			if(!"0".equals(fielditem.getState())&&validFields.toUpperCase().indexOf((","+itemid.toUpperCase()+","))!=-1){
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemid",itemid);
				bean.set("itemtype",itemtype);
				bean.set("codesetid",codesetid!=null?codesetid:"0");
				bean.set("itemdesc",itemdesc);
				bean.set("itemlength",itemlength);
				bean.set("decimalwidth",decimalwidth);
				bean.set("state",state);
				bean.set("fillable",isfillable);
				if(type!=null&& "1".equals(type)){
					if("principal".equals(itemid)) {
                        bean.set("value",getUserDetail(nbase+a0100, "a0101"));
                    } else {
                        bean.set("value","");
                    }
					bean.set("viewvalue","");
					bean.set("canWrite", "1");
				}else if(type!=null&& "2".equals(type)){
					//编辑
					String fielditemValue = "";
					ContentDAO dao = new ContentDAO(this.con);
					RecordVo vo = new RecordVo("per_diary_content");
					RecordVo p01vo = new RecordVo("p01");
					vo.setInt("p0100", Integer.parseInt(p0100));
					p01vo.setInt("p0100", Integer.parseInt(p0100));
					vo.setInt("record_num", Integer.parseInt(record_num));
					vo = dao.findByPrimaryKey(vo);
					p01vo = dao.findByPrimaryKey(p01vo);
					int typestate = p01vo.getInt("state");
					int fromflag = vo.getInt("fromflag");
					int fromp0100 = vo.getInt("fromp0100");
					int fromrecord_num = vo.getInt("fromrecord_num");
					String plFields = ((String)workParametersMap.get("plan_fields"+typestate)).toLowerCase();
					if(plFields!=null&&!"".equals(plFields.trim())) {
                        plFields = ","+plFields+",";
                    }
					if("N".equalsIgnoreCase(itemtype)){
						fielditemValue = vo.getString(itemid)==null?"":vo.getString(itemid);
						fielditemValue=PubFunc.round(fielditemValue,fielditem.getDecimalwidth());
						bean.set("value",fielditemValue);
					}else if("A".equalsIgnoreCase(itemtype)){
						if("0".equals(codesetid)){
							fielditemValue = vo.getString(itemid)==null?"":vo.getString(itemid);
							bean.set("value",fielditemValue);
						}else{
							fielditemValue = vo.getString(itemid)==null?"":vo.getString(itemid);
							bean.set("value",fielditemValue);
							fielditemValue = AdminCode.getCodeName(fielditem.getCodesetid(),fielditemValue);
							bean.set("viewvalue",fielditemValue);
						}
					}else if("D".equalsIgnoreCase(itemtype)){
						    Date date = vo.getDate(itemid);
						    fielditemValue = sdf2.format(date);
						    bean.set("value",fielditemValue);
					}else if("M".equalsIgnoreCase(fielditem.getItemtype())){
						   fielditemValue = vo.getString(itemid)==null?"":vo.getString(itemid);
						   bean.set("value",fielditemValue);
					}else{
						fielditemValue = vo.getString(itemid)==null?"":vo.getString(itemid);
						bean.set("value",fielditemValue);
					}
					if("2".equals(log_type)){
						if(fromflag==1||fromflag==2){
							bean.set("canWrite", "1");
						}else{
							if(plFields.toUpperCase().indexOf(","+itemid.toUpperCase()+",")!=-1){
								bean.set("canWrite", "0");
							}else{
								bean.set("canWrite", "1");
							}
						}
					}else{
						bean.set("canWrite", "1");
					}
				}
				list.add(bean);
			}
		}
		return list;
	}
	
	/**
	 * Description: 保存记录
	 * @Version1.0 
	 * Mar 19, 2013 3:09:43 PM Jianghe created
	 * @param personPage
	 * @param isChuZhang
	 * @param type
	 * @param log_type
	 * @param p0100
	 * @param record_num
	 * @param fieldsValueList
	 * @param currentYear
	 * @param currentMonth
	 * @param nextYear
	 * @param nextMonth
	 * @return
	 */
	public String saveRecord(String personPage,String isChuZhang,String type,String log_type,String p0100,String record_num,ArrayList fieldsValueList,String currentYear,String currentMonth,String nextYear,String nextMonth){
		ContentDAO dao = new ContentDAO(this.con);
		RecordVo p01vo = new RecordVo("p01");
		RecordVo pdcvo = new RecordVo("per_diary_content");
		GregorianCalendar gcal_s = new GregorianCalendar();
		gcal_s.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
		gcal_s.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
		gcal_s.set(GregorianCalendar.DAY_OF_MONTH, 1);
		GregorianCalendar gcal_e = new GregorianCalendar();
		gcal_e.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
		gcal_e.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(weekutils.strTodate(weekutils.lastMonthStr(Integer.parseInt(currentYear),Integer.parseInt(currentMonth))));
		String gcal_e_totalday=cal.get(GregorianCalendar.DAY_OF_MONTH)+"";
		gcal_e.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(gcal_e_totalday));
		
		
		GregorianCalendar gcal_next_s = new GregorianCalendar();
		gcal_next_s.set(GregorianCalendar.YEAR, Integer.parseInt(nextYear));
		gcal_next_s.set(GregorianCalendar.MONTH, Integer.parseInt(nextMonth)-1);
		gcal_next_s.set(GregorianCalendar.DAY_OF_MONTH, 1);
		GregorianCalendar gcal_next_e = new GregorianCalendar();
		gcal_next_e.set(GregorianCalendar.YEAR, Integer.parseInt(nextYear));
		gcal_next_e.set(GregorianCalendar.MONTH, Integer.parseInt(nextMonth)-1);
		cal.setTime(weekutils.strTodate(weekutils.lastMonthStr(Integer.parseInt(nextYear),Integer.parseInt(nextMonth))));
		String gcal_next_e_totalday=cal.get(GregorianCalendar.DAY_OF_MONTH)+"";
		gcal_next_e.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(gcal_next_e_totalday));
		
		try{
			if("1".equals(type)){
				if(p0100==null|| "".equals(p0100)){
					p0100 = String.valueOf(getP0100());
					p01vo.setInt("p0100", Integer.parseInt(p0100));
					p01vo.setInt("state", 2);
					p01vo.setInt("log_type", 2);
					p01vo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
					if("0".equals(personPage)){
						p01vo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
					}else if("1".equals(personPage)){
						//父部门
						p01vo.setString("e0122", getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122")));
					}
					p01vo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
					p01vo.setString("nbase", this.nbase);
					p01vo.setString("a0100", this.a0100);
					p01vo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
					p01vo.setString("p0115", "01");
					p01vo.setDate("p0104", gcal_s.getTime());
					p01vo.setDate("p0106", gcal_e.getTime());
					if("0".equals(personPage)){
						//p01vo.setInt("belong_type", Integer.parseInt(isChuZhang));
						p01vo.setInt("belong_type", 0);
					}else if("1".equals(personPage)){
						p01vo.setInt("belong_type", 2);
					}
					dao.addValueObject(p01vo);
				}
	    		pdcvo.setInt("p0100", Integer.parseInt(p0100));
	    		record_num = String.valueOf(getMaxRecord_num(Integer.parseInt(p0100)));
	    		pdcvo.setInt("record_num", Integer.parseInt(record_num));
	    		pdcvo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
	    		pdcvo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
	    		pdcvo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
	    		pdcvo.setString("nbase", this.nbase);
	    		pdcvo.setString("a0100", this.a0100);
	    		pdcvo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
	    		pdcvo.setInt("log_type", Integer.parseInt(log_type));
	    		pdcvo.setInt("seq",getMaxSeq(Integer.parseInt(p0100),Integer.parseInt(log_type)));
	    		pdcvo.setInt("fromflag", 1);
	    		if("1".equals(log_type)){
	    			pdcvo.setDate("start_time", gcal_next_s.getTime());
	    			pdcvo.setDate("end_time", gcal_next_e.getTime());
	    		}else{
	    			pdcvo.setDate("start_time", gcal_s.getTime());
	    			pdcvo.setDate("end_time", gcal_e.getTime());
	    		}
	    		for (int i = 0; i < fieldsValueList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean)fieldsValueList.get(i);
					String itemtype = (String)bean.get("itemtype");
					String itemid = (String)bean.get("itemid");
					
					if("N".equalsIgnoreCase(itemtype)){
						pdcvo.setString(itemid, (String)bean.get("value"));
					}else if("A".equalsIgnoreCase(itemtype)){
						pdcvo.setString(itemid, PubFunc.keyWord_reback((String)bean.get("value")));
					}else if("D".equalsIgnoreCase(itemtype)){
						pdcvo.setDate(itemid, (Date)bean.get("value"));
					}else if("M".equalsIgnoreCase(itemtype)){
						pdcvo.setString(itemid, PubFunc.keyWord_reback((String)bean.get("value")));
					}else{
						pdcvo.setString(itemid, (String)bean.get("value"));
					}
				}
	    		dao.addValueObject(pdcvo);
	    		
			}else{
				pdcvo.setInt("p0100", Integer.parseInt(p0100));
	    		record_num = String.valueOf(record_num);
	    		pdcvo.setInt("record_num", Integer.parseInt(record_num));
	    		pdcvo=dao.findByPrimaryKey(pdcvo);
	    		if("1".equals(log_type)){
	    			pdcvo.setDate("start_time", gcal_next_s.getTime());
	    			pdcvo.setDate("end_time", gcal_next_e.getTime());
	    		}else{
	    			pdcvo.setDate("start_time", gcal_s.getTime());
	    			pdcvo.setDate("end_time", gcal_e.getTime());
	    		}
	    		for (int i = 0; i < fieldsValueList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean)fieldsValueList.get(i);
					String itemtype = (String)bean.get("itemtype");
					String itemid = (String)bean.get("itemid");
					
					if("N".equalsIgnoreCase(itemtype)){
						pdcvo.setString(itemid, (String)bean.get("value"));
					}else if("A".equalsIgnoreCase(itemtype)){
						pdcvo.setString(itemid, PubFunc.keyWord_reback((String)bean.get("value")));
					}else if("D".equalsIgnoreCase(itemtype)){
						pdcvo.setDate(itemid, (Date)bean.get("value"));
					}else if("M".equalsIgnoreCase(itemtype)){
						pdcvo.setString(itemid, PubFunc.keyWord_reback((String)bean.get("value")));
					}else{
						pdcvo.setString(itemid, (String)bean.get("value"));
					}
				}
	    		dao.updateValueObject(pdcvo);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return p0100;
	}
	/**
	 * Description: 删除记录
	 * @Version1.0 
	 * Mar 6, 2013 10:19:22 AM Jianghe created
	 * @param p0100
	 * @param record_num
	 */
	public void deleteRecord(String p0100,String record_num){
		try {
			ContentDAO dao = new ContentDAO(this.con);
			RecordVo pdcvo = new RecordVo("per_diary_content");
			pdcvo.setInt("p0100", Integer.parseInt(p0100));
    		pdcvo.setInt("record_num", Integer.parseInt(record_num));
    		pdcvo = dao.findByPrimaryKey(pdcvo);
    		dao.deleteValueObject(pdcvo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 取p0100序列
	 * @return
	 */
	public int getP0100()
	{
		int id=1;
		try{
			IDGenerator  idg=new IDGenerator(2,this.con);
			id=Integer.parseInt(idg.getId("P01.P0100"));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}
	/**
	 * 取record_num序列
	 * @return
	 * @throws SQLException 
	 */
	public int getMaxRecord_num(int p0100) 
	{
		int maxid=0;
		RowSet rowSet = null;
		try{
			
			ContentDAO dao = new ContentDAO(this.con);
			rowSet = dao.search("select max(record_num) from per_diary_content where p0100="+p0100);
		        while(rowSet.next())
		        {
		        	String id = rowSet.getString(1);
		        	if((id!=null) && (id.trim().length()>0)) {
                        maxid=Integer.parseInt(id);
                    }
		        }
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e){
	    		e.printStackTrace();
			}
		}
	    return  ++maxid;	
	}
	/**
	 * 取seq序列
	 * @return
	 * @throws SQLException 
	 */
	public int getMaxSeq(int p0100,int log_type) 
	{
		int maxid=0;
		RowSet rowSet = null;
		try{
			
			ContentDAO dao = new ContentDAO(this.con);
			rowSet = dao.search("select max(seq) from per_diary_content where p0100="+p0100+" and log_type="+log_type);
			while(rowSet.next())
			{
				String id = rowSet.getString(1);
				if((id!=null) && (id.trim().length()>0)) {
                    maxid=Integer.parseInt(id);
                }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return  ++maxid;	
	}
	 /**
	 * Description: 查看sql是否会返回值
	 * @Version1.0 
	 * Mar 7, 2013 6:45:34 PM Jianghe created
	 * @param sql
	 * @param dao
	 * @return
	 */
	public boolean isHaveResults(String sql,ContentDAO dao){
			boolean flag = false;
			if(!"".equals(sql)){
				RowSet rs = null;
				try {
					rs = dao.search(sql);
					if(rs.next()){
						flag = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(rs!=null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
				}
			}
			return flag;
	}
	 /**
		 * 获得审批人sql
		 * @param role_property 审批层级
		 * @param relation_id   审批关系ID
		 * @param userView  
		 * @return
		 * @throws GeneralException
		 */
		public String getSuperSql(int role_property,String relation_id,String e01a1,String e0122,String b0110) 
		{
			
			StringBuffer whl=new StringBuffer("");
			whl.append("'@k"+e01a1+"'");
			if(e0122!=null&&e0122.length()>0)
			{
				for(int i=e0122.length();i>0;i--)
				{
					String temp=e0122.substring(0,i);
					if(b0110!=null&&temp.equals(b0110)) {
                        break;
                    }
					whl.append(",'um"+temp+"'");
				}
			}
			if(b0110!=null&&b0110.length()>0)
			{
				for(int i=b0110.length();i>0;i--)
				{
					String temp=b0110.substring(0,i);
					whl.append(",'un"+temp+"'");
				}
			}
			
			String sql="select object_id,mainbody_id,a0101  from t_wf_mainbody where Relation_id="+relation_id+"  and lower(Object_id) in ("+whl.toString()+")";
			if(role_property!=13) {
                sql+=" and SP_GRADE="+role_property+"  " ;
            } else {
                sql+=" and SP_GRADE in (9,10,11,12) " ;
            }
			sql+=" order by object_id desc";
			return sql;
		}
		/**
		 * Description: 报批上级领导
		 * @Version1.0 
		 * Mar 6, 2013 1:36:08 PM Jianghe created
		 * @param p0100
		 * @param curr_user
		 */
		public void appiaryRecord(String p0100,String curr_user,String personPage){
			try {
				ContentDAO dao = new ContentDAO(this.con);
				RecordVo vo = new RecordVo("p01");
				vo.setInt("p0100", Integer.parseInt(p0100));
				vo = dao.findByPrimaryKey(vo);
				vo.setString("p0115", "02");
				if(personPage!=null&& "1".equals(personPage)){
					vo.setString("curr_user", null);
				}else{
					String name = "";
					if(curr_user!=null && curr_user.trim().length()>0) {
                        name = getUsername(curr_user);
                    }
					
					if(name!=null && name.trim().length()>0)
					{
						vo.setString("curr_user", name);
					}
					String thee0122 = getUserDetail(nbase+a0100, "e0122");
					String thec01sc = getUserDetail(nbase+a0100, "c01sc");
					String thate0122 = getUserDetail(curr_user, "e0122");
					String thatc01sc = getUserDetail(curr_user, "c01sc");
					
					if( (thee0122!=null&&thate0122!=null&&!thee0122.equalsIgnoreCase(thate0122))
					    ||
						(thec01sc!=null&&thatc01sc!=null&&Integer.parseInt(thec01sc)<Integer.parseInt(thatc01sc))	
					){
						vo.setInt("belong_type", 1);
					}
					String appuser = vo.getString("appuser")==null?"":vo.getString("appuser");
					appuser+=curr_user+";";
					vo.setString("appuser", appuser);
				}
				dao.updateValueObject(vo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * 取得登录用户名
		 * @param dao
		 * @param a0100
		 * @param usernamefield
		 * @return
		 */
		public String getUsername(String a0100)
		{
			RecordVo user_vo = new RecordVo(""+(a0100.substring(0,3)).toUpperCase()+"A01");
			user_vo.setString("a0100",a0100.substring(3));
			String name = "";
			try 
			{
				ContentDAO dao = new ContentDAO(this.con);
				
				String usernamefield = ConstantParamter.getLoginUserNameField().toLowerCase();
				
				if(dao.isExistRecordVo(user_vo))
				{
					user_vo=dao.findByPrimaryKey(user_vo);
					if(user_vo!=null)
					{
						name = user_vo.getString(usernamefield);					
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return name;
		}
		/**
		 * Description: 获取输入编号的详细信息
		 * @Version1.0 
		 * Mar 7, 2013 7:13:00 PM Jianghe created
		 * @param a0100
		 * @param field
		 * @return
		 */
		public String getUserDetail(String a0100,String field){
			RecordVo user_vo = new RecordVo(""+(a0100.substring(0,3)).toUpperCase()+"A01");
			user_vo.setString("a0100",a0100.substring(3));
			String detail = "";
			try 
			{
				ContentDAO dao = new ContentDAO(this.con);
				
				
				if(dao.isExistRecordVo(user_vo))
				{
					if(user_vo!=null)
					{
						detail = user_vo.getString(field);					
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return detail;
		}
		/**
		 * Description: 提取上期计划
		 * @Version1.0 
		 * Mar 7, 2013 9:25:23 AM Jianghe created
		 * @param p0100
		 * @param currentYear
		 * @param currentMonth
		 * @return
		 */
		public String collectRecord(String state,String p0100,String currentYear,String currentMonth,ArrayList jihuaFieldsList,ArrayList zongjieFieldsList,String personPage,String isChuZhang){
			RowSet rs = null;
			String message = "error";
			try {

				String previousYear = "";
				String previousMonth = "";
				
				ContentDAO dao = new ContentDAO(this.con);
				RecordVo p01vo = new RecordVo("p01");
				RecordVo pdcvo = new RecordVo("per_diary_content");
				GregorianCalendar gcal_s = new GregorianCalendar();
				gcal_s.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
				gcal_s.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
				gcal_s.set(GregorianCalendar.DAY_OF_MONTH, 1);
				GregorianCalendar gcal_e = new GregorianCalendar();
				gcal_e.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
				gcal_e.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(weekutils.strTodate(weekutils.lastMonthStr(Integer.parseInt(currentYear),Integer.parseInt(currentMonth))));
				String gcal_e_totalday=cal.get(GregorianCalendar.DAY_OF_MONTH)+"";
				gcal_e.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(gcal_e_totalday));
				
				GregorianCalendar gcal_previous_s = new GregorianCalendar();
				gcal_previous_s.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
				gcal_previous_s.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
				gcal_previous_s.set(GregorianCalendar.DAY_OF_MONTH, 1);
				gcal_previous_s.add(GregorianCalendar.MONTH, -1);
				previousYear = String.valueOf(gcal_previous_s.get(GregorianCalendar.YEAR));
				previousMonth = String.valueOf(gcal_previous_s.get(GregorianCalendar.MONTH)+1);
				String previousP0100 = getSummarizeP0100(previousYear,previousMonth,state,personPage,isChuZhang,"1","");
				if(previousP0100==null|| "".equals(previousP0100)){
					message = "error";
				}
				else{
					String sql = "select * from per_diary_content where log_type=1 and p0100="+previousP0100;
					rs  = dao.search(sql);
					HashMap p0100AndRecordMap = new HashMap();
					p0100AndRecordMap = getCollectMap(p0100,"2");
					message = "error";
					while(rs.next()){
						
						if(p0100==null|| "".equals(p0100)){
							p0100 = String.valueOf(getP0100());
							p01vo.setInt("p0100", Integer.parseInt(p0100));
							p01vo.setInt("state", 2);
							p01vo.setInt("log_type", 2);
							p01vo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
							if("0".equals(personPage)){
								p01vo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
							}else if("1".equals(personPage)){
								//父部门
								p01vo.setString("e0122", getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122")));
							}
							p01vo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
							p01vo.setString("nbase", this.nbase);
							p01vo.setString("a0100", this.a0100);
							p01vo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
							p01vo.setString("p0115", "01");
							p01vo.setDate("p0104", gcal_s.getTime());
							p01vo.setDate("p0106", gcal_e.getTime());
							if("0".equals(personPage)){
								//p01vo.setInt("belong_type", Integer.parseInt(isChuZhang));
								p01vo.setInt("belong_type", 0);
							}else if("1".equals(personPage)){
								p01vo.setInt("belong_type", 2);
							}
							dao.addValueObject(p01vo);
						}
						if(p0100AndRecordMap.containsKey(rs.getInt("p0100")+","+rs.getInt("record_num"))) {
                            continue;
                        }
			    		pdcvo.setInt("p0100", Integer.parseInt(p0100));
			    		String record_num = String.valueOf(getMaxRecord_num(Integer.parseInt(p0100)));
			    		pdcvo.setInt("record_num", Integer.parseInt(record_num));
			    		pdcvo.setString("b0110", rs.getString("b0110"));
			    		pdcvo.setString("e0122", rs.getString("e0122"));
			    		pdcvo.setString("e01a1", rs.getString("e01a1"));
			    		pdcvo.setString("nbase", rs.getString("nbase"));
			    		pdcvo.setString("a0100", rs.getString("a0100"));
			    		pdcvo.setString("a0101", rs.getString("a0101"));
			    		pdcvo.setInt("fromflag", 2);
			    		pdcvo.setInt("fromp0100", rs.getInt("p0100"));
			    		pdcvo.setInt("fromrecord_num", rs.getInt("record_num"));
			    		pdcvo.setInt("log_type", 2);
			    		pdcvo.setInt("seq",getMaxSeq(Integer.parseInt(p0100),Integer.parseInt("2")));
		    			pdcvo.setDate("start_time", gcal_s.getTime());
		    			pdcvo.setDate("end_time", gcal_e.getTime());
		    			
		    			for(int i=0;i<zongjieFieldsList.size();i++)
						{
							LazyDynaBean fieldbean = (LazyDynaBean)zongjieFieldsList.get(i);
							String fieldItemValue = "";
							String fieldId = (String)fieldbean.get("itemid");
							String fieldItemType = (String)fieldbean.get("itemid");
							String codesetid = (String)fieldbean.get("codesetid");
							String decimalwidth = (String)fieldbean.get("decimalwidth");
							if("N".equalsIgnoreCase(fieldItemType)){
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
							}else if("A".equalsIgnoreCase(fieldItemType)){
								if("0".equals(codesetid)){
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								}else{
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
									fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
								}
							}else if("D".equalsIgnoreCase(fieldItemType)){
								    Date date = rs.getDate(fieldId);
								    fieldItemValue = sdf2.format(date);
							}else if("M".equalsIgnoreCase(fieldItemType)){
								fieldItemValue = Sql_switcher.readMemo(rs, fieldId)==null?"":Sql_switcher.readMemo(rs, fieldId);
							}else{
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
							}
							if("D".equalsIgnoreCase(fieldItemType)){
								pdcvo.setDate(fieldId, fieldItemValue);
							}else{
								pdcvo.setString(fieldId, fieldItemValue);
							}
							
						}
			    		dao.addValueObject(pdcvo);
			    		message = "ok";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
			return message;
		}
		/**
		 * Description: 汇总报给自己的计划或总结
		 * @Version1.0 
		 * Mar 7, 2013 9:25:23 AM Jianghe created
		 * @param p0100
		 * @param currentYear
		 * @param currentMonth
		 * @return
		 */
		public String gatherRecord(String log_type,String personPage,String isChuZhang,String state,String p0115,String p0100,String currentYear,String currentMonth,ArrayList jihuaFieldsList,ArrayList zongjieFieldsList){
			RowSet rs = null;
			RowSet rs1 = null;
			String message = "error";
			try {
				
				String previousYear = "";
				String previousMonth = "";
				
				ContentDAO dao = new ContentDAO(this.con);
				RecordVo p01vo = new RecordVo("p01");
				RecordVo pdcvo = new RecordVo("per_diary_content");
				GregorianCalendar gcal_s = new GregorianCalendar();
				gcal_s.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
				gcal_s.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
				gcal_s.set(GregorianCalendar.DAY_OF_MONTH, 1);
				GregorianCalendar gcal_s_next = new GregorianCalendar();
				gcal_s_next.setTime(gcal_s.getTime());
				gcal_s_next.add(GregorianCalendar.MONTH, 1);
				GregorianCalendar gcal_e = new GregorianCalendar();
				gcal_e.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
				gcal_e.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(weekutils.strTodate(weekutils.lastMonthStr(Integer.parseInt(currentYear),Integer.parseInt(currentMonth))));
				String gcal_e_totalday=cal.get(GregorianCalendar.DAY_OF_MONTH)+"";
				gcal_e.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(gcal_e_totalday));
				
				GregorianCalendar gcal_e_next = new GregorianCalendar();
				gcal_e_next.setTime(gcal_e.getTime());
				gcal_e_next.add(GregorianCalendar.MONTH, 1);
				
				GregorianCalendar gcal_previous_s = new GregorianCalendar();
				gcal_previous_s.set(GregorianCalendar.YEAR, Integer.parseInt(currentYear));
				gcal_previous_s.set(GregorianCalendar.MONTH, Integer.parseInt(currentMonth)-1);
				gcal_previous_s.set(GregorianCalendar.DAY_OF_MONTH, 1);
				gcal_previous_s.add(GregorianCalendar.MONTH, -1);
				previousYear = String.valueOf(gcal_previous_s.get(GregorianCalendar.YEAR));
				previousMonth = String.valueOf(gcal_previous_s.get(GregorianCalendar.MONTH)+1);
				String previousP0100 = getGatherP0100(currentYear,currentMonth,state,personPage,getUsername(this.nbase+this.a0100));
				if(previousP0100==null|| "".equals(previousP0100)){
					message = "error";
				}
				else{
					String sql = "select * from per_diary_content where log_type="+log_type+" and p0100 in("+previousP0100+")";
					//System.out.println(sql);
					rs  = dao.search(sql);
					message = "error";
					HashMap p0100AndRecordMap = new HashMap();
					p0100AndRecordMap = getCollectMap(p0100,log_type);
					while(rs.next()){
						
						if(p0100==null|| "".equals(p0100)){
							p0100 = String.valueOf(getP0100());
							p01vo.setInt("p0100", Integer.parseInt(p0100));
							p01vo.setInt("state", 2);
							p01vo.setInt("log_type", 2);
							p01vo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
							if("0".equals(personPage)){
								p01vo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
							}else if("1".equals(personPage)){
								//父部门
								p01vo.setString("e0122", getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122")));
							}
							p01vo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
							p01vo.setString("nbase", this.nbase);
							p01vo.setString("a0100", this.a0100);
							p01vo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
							p01vo.setString("p0115", "01");
							p01vo.setDate("p0104", gcal_s.getTime());
							p01vo.setDate("p0106", gcal_e.getTime());
							if("0".equals(personPage)){
								//p01vo.setInt("belong_type", 1);
								p01vo.setInt("belong_type", 0);
							}else if("1".equals(personPage)){
								p01vo.setInt("belong_type", 2);
							}
							dao.addValueObject(p01vo);
						}
						if(p0100AndRecordMap.containsKey(rs.getInt("p0100")+","+rs.getInt("record_num"))) {
                            continue;
                        }
						//System.out.println(rs.getInt("fromp0100")+","+rs.getInt("fromrecord_num"));
						pdcvo.setInt("p0100", Integer.parseInt(p0100));
						String record_num = String.valueOf(getMaxRecord_num(Integer.parseInt(p0100)));
						pdcvo.setInt("record_num", Integer.parseInt(record_num));
						pdcvo.setString("b0110", rs.getString("b0110"));
						pdcvo.setString("e0122", rs.getString("e0122"));
						pdcvo.setString("e01a1", rs.getString("e01a1"));
						pdcvo.setString("nbase", rs.getString("nbase"));
						pdcvo.setString("a0100", rs.getString("a0100"));
						pdcvo.setString("a0101", rs.getString("a0101"));
						
						pdcvo.setInt("fromflag", 3);
			    		pdcvo.setInt("fromp0100", rs.getInt("p0100"));
			    		pdcvo.setInt("fromrecord_num", rs.getInt("record_num"));
						
						pdcvo.setInt("log_type", Integer.parseInt(log_type));
						pdcvo.setInt("seq",getMaxSeq(Integer.parseInt(p0100),Integer.parseInt(log_type)));
						if("1".equals(log_type)){
							pdcvo.setDate("start_time", gcal_s_next.getTime());
							pdcvo.setDate("end_time", gcal_e_next.getTime());
						}else{
							pdcvo.setDate("start_time", gcal_s.getTime());
							pdcvo.setDate("end_time", gcal_e.getTime());
						}
						if("1".equals(log_type)){
							for(int i=0;i<jihuaFieldsList.size();i++)
							{
								LazyDynaBean fieldbean = (LazyDynaBean)jihuaFieldsList.get(i);
								String fieldItemValue = "";
								String fieldId = (String)fieldbean.get("itemid");
								String fieldItemType = (String)fieldbean.get("itemid");
								String codesetid = (String)fieldbean.get("codesetid");
								String decimalwidth = (String)fieldbean.get("decimalwidth");
								if("N".equalsIgnoreCase(fieldItemType)){
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
									fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
								}else if("A".equalsIgnoreCase(fieldItemType)){
									if("0".equals(codesetid)){
										fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
									}else{
										fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
										fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
									}
								}else if("D".equalsIgnoreCase(fieldItemType)){
									Date date = rs.getDate(fieldId);
									fieldItemValue = sdf2.format(date);
								}else if("M".equalsIgnoreCase(fieldItemType)){
									fieldItemValue = Sql_switcher.readMemo(rs, fieldId)==null?"":Sql_switcher.readMemo(rs, fieldId);
								}else{
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								}
								if("D".equalsIgnoreCase(fieldItemType)){
									pdcvo.setDate(fieldId, fieldItemValue);
								}else{
									pdcvo.setString(fieldId, fieldItemValue);
								}
								
							}
						}else{
							for(int i=0;i<zongjieFieldsList.size();i++)
							{
								LazyDynaBean fieldbean = (LazyDynaBean)zongjieFieldsList.get(i);
								String fieldItemValue = "";
								String fieldId = (String)fieldbean.get("itemid");
								String fieldItemType = (String)fieldbean.get("itemid");
								String codesetid = (String)fieldbean.get("codesetid");
								String decimalwidth = (String)fieldbean.get("decimalwidth");
								if("N".equalsIgnoreCase(fieldItemType)){
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
									fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
								}else if("A".equalsIgnoreCase(fieldItemType)){
									if("0".equals(codesetid)){
										fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
									}else{
										fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
										fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
									}
								}else if("D".equalsIgnoreCase(fieldItemType)){
									Date date = rs.getDate(fieldId);
									fieldItemValue = sdf2.format(date);
								}else if("M".equalsIgnoreCase(fieldItemType)){
									fieldItemValue = Sql_switcher.readMemo(rs, fieldId)==null?"":Sql_switcher.readMemo(rs, fieldId);
								}else{
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								}
								if("D".equalsIgnoreCase(fieldItemType)){
									pdcvo.setDate(fieldId, fieldItemValue);
								}else{
									pdcvo.setString(fieldId, fieldItemValue);
								}
								
							}
						}
						dao.addValueObject(pdcvo);
						message = "ok";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
					if(rs1!=null) {
                        try {
                            rs1.close();
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
			}
			return message;
		}
		/**
		 * Description: 获取部门的父部门
		 * @Version1.0 
		 * Mar 11, 2013 9:56:55 AM Jianghe created
		 * @param e0122
		 * @return
		 */
		public String getParentE0122(String e0122){
			RowSet rs = null;
			ContentDAO dao = new ContentDAO(this.con);
			String parent = null;
			try {
				rs= dao.search("select codeitemid from organization where codesetid='UM' and codeitemid = (select parentid from organization where codeitemid='"+e0122+"')");
				if(rs.next()){
					parent = rs.getString("codeitemid");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(rs!=null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
			return parent==null?e0122:parent;
		}
		/**
		 * Description: 取得o01相对应的记录
		 * @Version1.0 
		 * Mar 11, 2013 6:37:35 PM Jianghe created
		 * @param p0100
		 * @return
		 */
		public LazyDynaBean getp01Detail(String p0100){
			RowSet rowSet = null;
			LazyDynaBean bean = new LazyDynaBean();
			ContentDAO dao = new ContentDAO(this.con);
			String parent = null;
			Calendar calendar = Calendar.getInstance();
			try {
				rowSet= dao.search("select * from p01 where p0100="+p0100);
				if(rowSet.next()){
					bean.set("p0100", rowSet.getInt("p0100")+"");
	        		bean.set("b0110", rowSet.getString("b0110"));
	        		bean.set("e0122", rowSet.getString("e0122"));
	        		bean.set("e01a1", rowSet.getString("e01a1"));
	        		bean.set("nbase", rowSet.getString("nbase"));
	        		bean.set("a0100", rowSet.getString("a0100"));
	        		bean.set("a0101", rowSet.getString("a0101"));
	        		bean.set("p0115", rowSet.getString("p0115")+"");
	        		bean.set("state", rowSet.getInt("state")+"");
	        		bean.set("log_type", rowSet.getInt("log_type")+"");
	        		bean.set("belong_type", rowSet.getInt("belong_type")+"");
				    String p0104 = sdf2.format(rowSet.getDate("p0104"));
				    String p0106 = sdf2.format(rowSet.getDate("p0106"));
				    bean.set("p0104", p0104);
				    bean.set("p0106", p0106);
				    calendar.setTime(rowSet.getDate("p0104"));
				    bean.set("year",calendar.get(Calendar.YEAR)+"");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(rowSet!=null) {
                    try {
                        rowSet.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
			}
			return bean;
		}
		public HSSFRichTextString cellStr(String context) {
			HSSFRichTextString textstr = new HSSFRichTextString(context);
			return textstr;
		}

		public String decimalwidth(int len) {

			StringBuffer decimal = new StringBuffer("0");
			if (len > 0) {
                decimal.append(".");
            }
			for (int i = 0; i < len; i++) {
				decimal.append("0");
			}
			decimal.append("_ ");
			return decimal.toString();
		}

		public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
			HSSFCellStyle style = workbook.createCellStyle();
			// style.setVerticalAlignment(VerticalAlignment.CENTER);
			return style;
		}
		/**
		 * Description: 导出周报
		 * @Version1.0 
		 * Mar 12, 2013 11:09:42 AM Jianghe created
		 * @param p0100
		 * @throws GeneralException 
		 */
		public String  creatWeekExcel(String p0100,ArrayList zongjieFieldsList,ArrayList jihuaFieldsList,String summarizeTime) throws GeneralException{
			ArrayList summarizeDataList = new ArrayList();
			ArrayList planDataList = new ArrayList();
			RowSet rowSet = null;
			String sql = "";
			String firstTitle="";//标题：人事董事部工作周报
			String summarizeTitle="";//总结标题
			String planTitle="";//计划标题
			int summarizeColWidth = zongjieFieldsList.size()+1;//总结占多少列
			int planColWidth = jihuaFieldsList.size()+1;//计划占多少列
			LazyDynaBean p01bean = this.getp01Detail(p0100);
			String belong_type = (String)p01bean.get("belong_type");
			String e0122 = (String)p01bean.get("e0122");
			String a0101 = (String)p01bean.get("a0101");
			String p0104 = (String)p01bean.get("p0104");
			String p0106 = (String)p01bean.get("p0106");
			String thee0122 = AdminCode.getCodeName("UM",e0122);
			String thea0101 = a0101;
			
			thee0122 = thee0122==null?"":thee0122;
			thea0101 = thea0101==null?"":thea0101;
			if(belong_type==null|| "0".equals(belong_type) ){
				firstTitle = thea0101+"工作周报";
			}else if("2".equals(belong_type) || "1".equals(belong_type)){
				firstTitle = thee0122+"工作周报";
			}
			summarizeTitle = "("+summarizeTime+")"+"工作总结";
			planTitle = "("+p0104+"--"+p0106+")"+"工作计划";
			sql = "select * from per_diary_content where log_type=2 and p0100="+p0100;
			try{
				
				ContentDAO dao = new ContentDAO(this.con);
				rowSet = dao.search(sql);
				int count = 1;
		        while(rowSet.next())
		        {
		        	LazyDynaBean bean = new LazyDynaBean();
		        	bean.set("count",String.valueOf(count));
	        		for(int i=0;i<zongjieFieldsList.size();i++)
					{
						LazyDynaBean fieldbean = (LazyDynaBean)zongjieFieldsList.get(i);
						String fieldItemValue = "";
						String fieldId = (String)fieldbean.get("itemid");
						String fieldItemType = (String)fieldbean.get("itemtype");
						String codesetid = (String)fieldbean.get("codesetid");
						String decimalwidth = (String)fieldbean.get("decimalwidth");
						if("N".equalsIgnoreCase(fieldItemType)){
							fieldItemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
							fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
						}else if("A".equalsIgnoreCase(fieldItemType)){
							if("0".equals(codesetid)){
								fieldItemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
							}else{
								fieldItemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
								fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
							}
						}else if("D".equalsIgnoreCase(fieldItemType)){
							Date date = rowSet.getDate(fieldId);
							fieldItemValue = sdf2.format(date);
						}else if("M".equalsIgnoreCase(fieldItemType)){
							fieldItemValue = Sql_switcher.readMemo(rowSet, fieldId)==null?"":Sql_switcher.readMemo(rowSet, fieldId);
						}else{
							fieldItemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
						}
						bean.set(fieldId, fieldItemValue);
					}
	        		count++;
	        		summarizeDataList.add(bean);
		        }
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rowSet!=null) {
                        rowSet.close();
                    }
				}catch(Exception e){
		    		e.printStackTrace();
				}
			}
			sql = "select * from per_diary_content where log_type=1 and p0100="+p0100;
			try{
				
				ContentDAO dao = new ContentDAO(this.con);
				rowSet = dao.search(sql);
				int count = 1;
				while(rowSet.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("count",String.valueOf(count));
					for(int i=0;i<jihuaFieldsList.size();i++)
					{
						LazyDynaBean fieldbean = (LazyDynaBean)jihuaFieldsList.get(i);
						String fieldItemValue = "";
						String fieldId = (String)fieldbean.get("itemid");
						String fieldItemType = (String)fieldbean.get("itemtype");
						String codesetid = (String)fieldbean.get("codesetid");
						String decimalwidth = (String)fieldbean.get("decimalwidth");
						if("N".equalsIgnoreCase(fieldItemType)){
							fieldItemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
							fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
						}else if("A".equalsIgnoreCase(fieldItemType)){
							if("0".equals(codesetid)){
								fieldItemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
							}else{
								fieldItemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
								fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
							}
						}else if("D".equalsIgnoreCase(fieldItemType)){
							Date date = rowSet.getDate(fieldId);
							fieldItemValue = sdf2.format(date);
						}else if("M".equalsIgnoreCase(fieldItemType)){
							fieldItemValue = Sql_switcher.readMemo(rowSet, fieldId)==null?"":Sql_switcher.readMemo(rowSet, fieldId);
						}else{
							fieldItemValue = rowSet.getString(fieldId)==null?"":rowSet.getString(fieldId);
						}
						bean.set(fieldId, fieldItemValue);
					}
					count++;
					planDataList.add(bean);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rowSet!=null) {
                        rowSet.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
			HSSFSheet sheet = wb.createSheet();
			//第一行标题字体样式
			HSSFFont font1 = wb.createFont(); 
			font1.setFontHeightInPoints((short) 20);
//			font1.setBoldweight((short) 500);
			font1.setBold(true);
			font1.setColor(HSSFFont.COLOR_NORMAL);
			//标题样式
			HSSFFont font4 = wb.createFont(); //设置样式
			font4.setFontHeightInPoints((short) 15);
//			font4.setBoldweight((short) 300);
			font4.setBold(true);
			font4.setColor(HSSFFont.COLOR_NORMAL);
			//
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setFont(font1);
			style2.setAlignment(HorizontalAlignment.CENTER);
			style2.setVerticalAlignment(VerticalAlignment.CENTER);
			style2.setWrapText(true);
			style2.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
	        style2.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
	        style2.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
	        style2.setBorderBottom(BorderStyle.valueOf((short)1));
	        
	        
	        HSSFCellStyle style3 = wb.createCellStyle();
	        style3.setFont(font4);
	        style3.setAlignment(HorizontalAlignment.LEFT);
	        style3.setVerticalAlignment(VerticalAlignment.CENTER);
	        style3.setWrapText(true);
	        style3.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
	        style3.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
	        style3.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
	        style3.setBorderBottom(BorderStyle.valueOf((short)1));
	        HSSFCellStyle style10 = wb.createCellStyle();
	        style10.setFont(font4);
	        style10.setAlignment(HorizontalAlignment.CENTER);
	        style10.setVerticalAlignment(VerticalAlignment.CENTER);
	        style10.setWrapText(true);
	        style10.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
	        style10.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
	        style10.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
	        style10.setBorderBottom(BorderStyle.valueOf((short)1));
			// style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short) 10);
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font2);
			style1.setAlignment(HorizontalAlignment.CENTER);
			style1.setVerticalAlignment(VerticalAlignment.CENTER);
			style1.setWrapText(true);
			style1.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
	        style1.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
	        style1.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
	        style1.setBorderBottom(BorderStyle.valueOf((short)1));
	        HSSFCellStyle styleContent = wb.createCellStyle();
	        styleContent.setFont(font2);
	        styleContent.setAlignment(HorizontalAlignment.LEFT);
	        styleContent.setVerticalAlignment(VerticalAlignment.CENTER);
	        styleContent.setWrapText(true);
	        styleContent.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
	        styleContent.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
	        styleContent.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
	        styleContent.setBorderBottom(BorderStyle.valueOf((short)1));
			// style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

			HSSFCellStyle styleN = dataStyle(wb);
			styleN.setAlignment(HorizontalAlignment.RIGHT);
			styleN.setWrapText(true);
			HSSFDataFormat df = wb.createDataFormat();
			styleN.setDataFormat(df.getFormat(decimalwidth(0)));

			HSSFCellStyle styleCol0 = dataStyle(wb);
			HSSFFont font0 = wb.createFont();
			font0.setFontHeightInPoints((short) 5);
			styleCol0.setFont(font0);
			// styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
			// 文本格式
			// styleCol0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFCellStyle styleCol0_title = dataStyle(wb);
			styleCol0_title.setFont(font2);
			// styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
			// 文本格式
			// styleCol0_title.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFCellStyle styleF1 = dataStyle(wb);
			styleF1.setAlignment(HorizontalAlignment.RIGHT);
			HSSFFont font3 = wb.createFont(); //设置样式
			font3.setFontHeightInPoints((short) 3);
			styleF1.setFont(font3);
			styleF1.setWrapText(true);
			HSSFDataFormat df1 = wb.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

			HSSFCellStyle styleF2 = dataStyle(wb);
			styleF2.setAlignment(HorizontalAlignment.RIGHT);
			styleF2.setFont(font3);
			styleF2.setWrapText(true);
			HSSFDataFormat df2 = wb.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

			HSSFCellStyle styleF3 = dataStyle(wb);
			styleF3.setAlignment(HorizontalAlignment.RIGHT);
			styleF3.setFont(font3);
			styleF3.setWrapText(true);
			HSSFDataFormat df3 = wb.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

			HSSFCellStyle styleF4 = dataStyle(wb);
			styleF4.setAlignment(HorizontalAlignment.RIGHT);
			styleF4.setFont(font3);
			styleF4.setWrapText(true);
			HSSFDataFormat df4 = wb.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

			HSSFCellStyle styleF5 = dataStyle(wb);
			styleF5.setAlignment(HorizontalAlignment.RIGHT);
			styleF5.setFont(font3);
			styleF5.setWrapText(true);
			HSSFDataFormat df5 = wb.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
			HSSFRow row = sheet.getRow(0);
			if (row == null) {
				row = sheet.createRow(0);
			}
			row.setHeight((short) 1000);
			HSSFCell cell = null;
            //设置列宽
			for (int i = 1; i < zongjieFieldsList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean)zongjieFieldsList.get(i);
				String itemtype = (String)bean.get("itemtype");
				if("M".equalsIgnoreCase(itemtype)) {
                    sheet.setColumnWidth((i), 15 * 900);
                } else {
                    sheet.setColumnWidth((i), 15 * 300);
                }
			}
				// 设置第一行的数据
			
			if (row.getRowNum() == 0) {
				cell = row.getCell(0);
				if (cell == null) {
					cell = row.createCell(0);
				}
				cell.setCellValue(cellStr(firstTitle));
				cell.setCellStyle(style2);
	            
				for (int i = 1; i < summarizeColWidth; i++) {
					cell = row.getCell(i);
					if(cell == null){
						cell = row.createCell(i);
					}
					cell.setCellStyle(style2);
				}
				
				ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short)(summarizeColWidth-1));// 合并第一行的单元格
				
				row = sheet.createRow(row.getRowNum() + 1); // 第二行开始
				row.setHeight((short) 500);
				cell = row.getCell(0); //第二行第一列
				if(cell == null){
					cell = row.createCell(0);
				}
				
				
				cell.setCellValue(summarizeTitle);
				cell.setCellStyle(style10);
				
				for (int i = 1; i < summarizeColWidth; i++) {
					cell = row.getCell(i);
					if(cell == null){
						cell = row.createCell(i);
					}
					cell.setCellStyle(style3);
				}
				ExportExcelUtil.mergeCell(sheet, 1, (short) 0, 1, (short)(summarizeColWidth-1));// 合并第二行的单元格
				
				row = sheet.createRow(row.getRowNum() + 1); // 第三行开始
			}
			cell = row.getCell(0); //第三行第一列
			if(cell == null){
				cell = row.createCell(0);
			}
			
			cell.setCellValue("序号");
			cell.setCellStyle(style1);
			
			for (int i = 1; i <= zongjieFieldsList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean)zongjieFieldsList.get(i-1);
				String itemdesc = (String)bean.get("itemdesc");
				
				cell = row.getCell(i);
				if(cell == null){
					cell = row.createCell(i);
				}
				cell.setCellValue(itemdesc);
				cell.setCellStyle(style1);
			}
			
			for (int i = 0; i < summarizeDataList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean)summarizeDataList.get(i);
				row = sheet.getRow(i+3);
				if (row == null) {
					row = sheet.createRow(i+3);
				}
				cell = row.getCell(0);
				if (cell == null) {
					cell = row.createCell(0);
				}
				cell.setCellValue((String)bean.get("count"));
				cell.setCellStyle(style1);
				
				
				for (int j = 1; j <= zongjieFieldsList.size(); j++) {
					LazyDynaBean abean = (LazyDynaBean)zongjieFieldsList.get(j-1);
					String itemid = (String)abean.get("itemid");
					
					cell = row.getCell(j);
					if(cell == null){
						cell = row.createCell(j);
					}
					cell.setCellValue((String)bean.get(itemid));
					cell.setCellStyle(styleContent);
				}
			}	
				
//				row = sheet.createRow(row.getRowNum() + 1);
//				row.setHeight((short) 1000);
				//设置列宽
				for (int i = 1; i <= jihuaFieldsList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean)jihuaFieldsList.get(i-1);
					String itemtype = (String)bean.get("itemtype");
					if("M".equalsIgnoreCase(itemtype)) {
                        sheet.setColumnWidth((i), 15 * 900);
                    } else {
                        sheet.setColumnWidth((i), 15 * 300);
                    }
				}
				// 设置第一行的数据
				
					
					
				row = sheet.createRow(row.getRowNum() + 1); // 第二行开始
				row.setHeight((short) 500);
				cell = row.getCell(0); //第二行第一列
				if(cell == null){
					cell = row.createCell(0);
				}
				
				
				cell.setCellValue("");
				//cell.setCellStyle(style10);
				
				for (int i = 1; i < planColWidth; i++) {
					cell = row.getCell(i);
					if(cell == null){
						cell = row.createCell(i);
					}
					//cell.setCellStyle(style3);
				}
				ExportExcelUtil.mergeCell(sheet, row.getRowNum(), (short) 0, row.getRowNum(), (short)(planColWidth-1));// 合并第二行的单元格
				row = sheet.createRow(row.getRowNum() + 1); // 第二行开始
				row.setHeight((short) 500);
				cell = row.getCell(0); //第二行第一列
				if(cell == null){
					cell = row.createCell(0);
				}
				
				
				cell.setCellValue(planTitle);
				cell.setCellStyle(style10);
				
				for (int i = 1; i < planColWidth; i++) {
					cell = row.getCell(i);
					if(cell == null){
						cell = row.createCell(i);
					}
					cell.setCellStyle(style3);
				}
				ExportExcelUtil.mergeCell(sheet, row.getRowNum(), (short) 0, row.getRowNum(), (short)(planColWidth-1));// 合并第二行的单元格
				row = sheet.createRow(row.getRowNum() + 1); // 第三行开始
				cell = row.getCell(0); //第三行第一列
				if(cell == null){
					cell = row.createCell(0);
				}
				
				cell.setCellValue("序号");
				cell.setCellStyle(style1);
				
				for (int i = 1; i <= jihuaFieldsList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean)jihuaFieldsList.get(i-1);
					String itemdesc = (String)bean.get("itemdesc");
					
					cell = row.getCell(i);
					if(cell == null){
						cell = row.createCell(i);
					}
					cell.setCellValue(itemdesc);
					cell.setCellStyle(style1);
				}
				
				for (int i = 0; i < planDataList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean)planDataList.get(i);
					row = sheet.createRow(row.getRowNum() + 1);
					cell = row.getCell(0);
					if (cell == null) {
						cell = row.createCell(0);
					}
					cell.setCellValue((String)bean.get("count"));
					cell.setCellStyle(style1);
					
					
					for (int j = 1; j <= jihuaFieldsList.size(); j++) {
						LazyDynaBean abean = (LazyDynaBean)jihuaFieldsList.get(j-1);
						String itemid = (String)abean.get("itemid");
						
						cell = row.getCell(j);
						if(cell == null){
							cell = row.createCell(j);
						}
						cell.setCellValue((String)bean.get(itemid));
						cell.setCellStyle(styleContent);
					}

			}
			
			String outName = new Date().getTime() + ".xls";
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(System
						.getProperty("java.io.tmpdir")
						+ System.getProperty("file.separator") + outName);
				wb.write(fileOut);
				fileOut.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}finally{
				PubFunc.closeResource(fileOut);
				PubFunc.closeResource(wb);
			}
			outName = outName.replace(".xls", "#");
			return outName;
		
			
		}
		public String  creatMonthExcel(String p0100,String currentYear,String currentMonth,String nextYear,String nextMonth) throws GeneralException{
			ArrayList summarizeDataList = new ArrayList();
			ArrayList planDataList = new ArrayList();
			RowSet rowSet = null;
			String sql = "";
			String firstTitle="";//什么处室
			String summarizeTitle="";//总结标题
			String planTitle="";//计划标题
			LazyDynaBean p01bean = this.getp01Detail(p0100);
			String belong_type = (String)p01bean.get("belong_type");
			String e0122 = (String)p01bean.get("e0122");
			String a0101 = (String)p01bean.get("a0101");
			String p0104 = (String)p01bean.get("p0104");
			String p0106 = (String)p01bean.get("p0106");
			String thee0122 = AdminCode.getCodeName("UM",e0122);
			String thea0101 = a0101;
			
			thee0122 = thee0122==null?"":thee0122;
			thea0101 = thea0101==null?"":thea0101;
			if(belong_type==null|| "0".equals(belong_type) ){
				firstTitle = thea0101;
			}else if("2".equals(belong_type) || "1".equals(belong_type)){
				firstTitle = thee0122;
			}
			summarizeTitle = firstTitle+currentYear+"年"+currentMonth+"月工作总结";
			planTitle = firstTitle+nextYear+"年"+nextMonth+"月工作计划";
			sql = "select content from per_diary_content where log_type=2 and p0100="+p0100;
			try{
				
				ContentDAO dao = new ContentDAO(this.con);
				rowSet = dao.search(sql);
				int count = 1;
				while(rowSet.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("count",String.valueOf(count));
					bean.set("content", Sql_switcher.readMemo(rowSet, "content"));
					count++;
					summarizeDataList.add(bean);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rowSet!=null) {
                        rowSet.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			sql = "select content from per_diary_content where log_type=1 and p0100="+p0100;
			try{
				
				ContentDAO dao = new ContentDAO(this.con);
				rowSet = dao.search(sql);
				int count = 1;
				while(rowSet.next())
				{
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("count",String.valueOf(count));
					bean.set("content", Sql_switcher.readMemo(rowSet, "content"));
					count++;
					planDataList.add(bean);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try{
					if(rowSet!=null) {
                        rowSet.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
			HSSFSheet sheet = wb.createSheet();
			//第一行标题字体样式
			HSSFFont font1 = wb.createFont(); 
			font1.setFontHeightInPoints((short) 20);
//			font1.setBoldweight((short) 500);
			font1.setBold(true);
			font1.setColor(HSSFFont.COLOR_NORMAL);
			//标题样式
			HSSFFont font4 = wb.createFont(); //设置样式
			font4.setFontHeightInPoints((short) 15);
			font4.setBold(true);
			font4.setColor(HSSFFont.COLOR_NORMAL);
			//
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setFont(font1);
			style2.setAlignment(HorizontalAlignment.CENTER);
			style2.setVerticalAlignment(VerticalAlignment.CENTER);
			style2.setWrapText(true);
			style2.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
			style2.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
			style2.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
			style2.setBorderBottom(BorderStyle.valueOf((short)1));
			
			
			HSSFCellStyle style3 = wb.createCellStyle();
			style3.setFont(font4);
			style3.setAlignment(HorizontalAlignment.LEFT);
			style3.setVerticalAlignment(VerticalAlignment.CENTER);
			style3.setWrapText(true);
			style3.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
			style3.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
			style3.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
			style3.setBorderBottom(BorderStyle.valueOf((short)1));
			HSSFCellStyle style10 = wb.createCellStyle();
			style10.setFont(font4);
			style10.setAlignment(HorizontalAlignment.CENTER);
			style10.setVerticalAlignment(VerticalAlignment.CENTER);
			style10.setWrapText(true);
			style10.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
			style10.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
			style10.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
			style10.setBorderBottom(BorderStyle.valueOf((short)1));
			// style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			
			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short) 10);
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font2);
			style1.setAlignment(HorizontalAlignment.CENTER);
			style1.setVerticalAlignment(VerticalAlignment.CENTER);
			style1.setWrapText(true);
			style1.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
			style1.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
			style1.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
			style1.setBorderBottom(BorderStyle.valueOf((short)1));
			HSSFCellStyle styleContent = wb.createCellStyle();
			styleContent.setFont(font2);
			styleContent.setAlignment(HorizontalAlignment.LEFT);
			styleContent.setVerticalAlignment(VerticalAlignment.CENTER);
			styleContent.setWrapText(true);
			styleContent.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
			styleContent.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
			styleContent.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
			styleContent.setBorderBottom(BorderStyle.valueOf((short)1));
			// style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
			
			HSSFCellStyle styleN = dataStyle(wb);
			styleN.setAlignment(HorizontalAlignment.RIGHT);
			styleN.setWrapText(true);
			HSSFDataFormat df = wb.createDataFormat();
			styleN.setDataFormat(df.getFormat(decimalwidth(0)));
			
			HSSFCellStyle styleCol0 = dataStyle(wb);
			HSSFFont font0 = wb.createFont();
			font0.setFontHeightInPoints((short) 5);
			styleCol0.setFont(font0);
			// styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
			// 文本格式
			// styleCol0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			
			HSSFCellStyle styleCol0_title = dataStyle(wb);
			styleCol0_title.setFont(font2);
			// styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
			// 文本格式
			// styleCol0_title.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			// styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			
			HSSFCellStyle styleF1 = dataStyle(wb);
			styleF1.setAlignment(HorizontalAlignment.RIGHT);
			HSSFFont font3 = wb.createFont(); //设置样式
			font3.setFontHeightInPoints((short) 3);
			styleF1.setFont(font3);
			styleF1.setWrapText(true);
			HSSFDataFormat df1 = wb.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));
			
			HSSFCellStyle styleF2 = dataStyle(wb);
			styleF2.setAlignment(HorizontalAlignment.RIGHT);
			styleF2.setFont(font3);
			styleF2.setWrapText(true);
			HSSFDataFormat df2 = wb.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));
			
			HSSFCellStyle styleF3 = dataStyle(wb);
			styleF3.setAlignment(HorizontalAlignment.RIGHT);
			styleF3.setFont(font3);
			styleF3.setWrapText(true);
			HSSFDataFormat df3 = wb.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));
			
			HSSFCellStyle styleF4 = dataStyle(wb);
			styleF4.setAlignment(HorizontalAlignment.RIGHT);
			styleF4.setFont(font3);
			styleF4.setWrapText(true);
			HSSFDataFormat df4 = wb.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));
			
			HSSFCellStyle styleF5 = dataStyle(wb);
			styleF5.setAlignment(HorizontalAlignment.RIGHT);
			styleF5.setFont(font3);
			styleF5.setWrapText(true);
			HSSFDataFormat df5 = wb.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
			HSSFRow row = sheet.getRow(0);
			if (row == null) {
				row = sheet.createRow(0);
			}
			row.setHeight((short) 1000);
			HSSFCell cell = null;
			//设置列宽
			sheet.setColumnWidth((0), 40000);
			// 设置第一行的数据
			
			if (row.getRowNum() == 0) {
				row.setHeight((short) 500);
				cell = row.getCell(0);
				if (cell == null) {
					cell = row.createCell(0);
				}
				
				cell.setCellValue(summarizeTitle);
				cell.setCellStyle(style10);
				
				row = sheet.createRow(row.getRowNum() + 1); // 第三行开始
			}
			cell = row.getCell(0); //第三行第一列
			if(cell == null){
				cell = row.createCell(0);
			}
			String summarizevalue = "";
			
			for (int i = 0; i < summarizeDataList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean)summarizeDataList.get(i);
				summarizevalue+=(String)bean.get("count")+"."+(String)bean.get("content")+"\n";
			}	
			if(!"".equals(summarizevalue)){
				summarizevalue=summarizevalue.substring(0,summarizevalue.length()-1);
			}
			cell.setCellValue(summarizevalue);
			cell.setCellStyle(styleContent);
			
			
			// 设置第一行的数据
			
			
			
			row = sheet.createRow(row.getRowNum() + 1); // 第二行开始
			row.setHeight((short) 500);
			cell = row.getCell(0); //第二行第一列
			if(cell == null){
				cell = row.createCell(0);
			}
			
			
			cell.setCellValue(planTitle);
			cell.setCellStyle(style10);
			
			row = sheet.createRow(row.getRowNum() + 1); // 第三行开始
			
			cell = row.getCell(0); //第三行第一列
			if(cell == null){
				cell = row.createCell(0);
			}
			
			String planvalue = "";
			
			for (int i = 0; i < planDataList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean)planDataList.get(i);
				planvalue+=(String)bean.get("count")+"."+(String)bean.get("content")+"\n";
			}
			if(!"".equals(planvalue)){
				planvalue=planvalue.substring(0,planvalue.length()-1);
			}
			cell.setCellValue(planvalue);
			cell.setCellStyle(styleContent);
			
			String outName = new Date().getTime() + ".xls";
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(System
						.getProperty("java.io.tmpdir")
						+ System.getProperty("file.separator") + outName);
				wb.write(fileOut);
				fileOut.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}finally{
				PubFunc.closeResource(fileOut);
				PubFunc.closeResource(wb);
			}
			outName = outName.replace(".xls", "#");
			return outName;
			
			
		}
public ArrayList getQueryDataList(String belong_type,String personPage,String state,String queryContent,String isChuZhang,String opt){
	RowSet rowSet = null;
	String sql = "";
	ArrayList list = new ArrayList();
	try{
		if("1".equals(opt)){
			if("0".equals(personPage)){
				if("0".equals(isChuZhang)) {
                    sql = "select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where a0100='"+this.a0100+"' and nbase='"+this.nbase+"'  and state="+state+" and (belong_type=0 or belong_type is null) ) order by start_time desc,p0100,seq";
                } else if("1".equals(isChuZhang)) {
                    sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where  nbase='"+this.nbase+"' and state="+state+" and ((belong_type=1 and e0122='"+this.getUserDetail(this.nbase+this.a0100, "e0122")+"') or ( (belong_type=0 or belong_type is null) and a0100='"+this.a0100+"'))) order by start_time desc,p0100,seq";
                }
			}else if("1".equals(personPage)){
				sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where  e0122='"+getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122"))+"' and nbase='"+this.nbase+"' and state="+state+" and belong_type=2) order by start_time desc,p0100,seq";
			}
		}else{
//			if(personPage.equals("0")){
//				if(isChuZhang.equals("0"))
//					sql = "select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where a0100='"+this.a0100+"' and nbase='"+this.nbase+"'  and state="+state+" and (belong_type=1) ) order by start_time desc,p0100,seq";
//				else if(isChuZhang.equals("1"))
//					sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where  nbase='"+this.nbase+"' and state="+state+" and ((belong_type=1 and e0122='"+this.getUserDetail(this.nbase+this.a0100, "e0122")+"') or ( (belong_type=0 or belong_type is null) and a0100='"+this.a0100+"'))) order by start_time desc,p0100,seq";
//				
//			}else if(personPage.equals("1")){
//				sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where  e0122='"+getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122"))+"' and nbase='"+this.nbase+"' and state="+state+" and belong_type=2) order by start_time desc,p0100,seq";
//			}
			
			
			if("0".equals(isChuZhang)){
				if("0".equals(belong_type)){//从人员进
					sql = "select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where a0100='"+userView.getA0100()+"' and nbase='"+userView.getDbname()+"'  and state="+state+" and belong_type="+belong_type+" ) order by start_time desc,p0100,seq";
				}else if("1".equals(belong_type)){//从处室
					sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where state="+state+" and belong_type="+belong_type+" and e0122='"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"') order by start_time desc,p0100,seq";
				}else if("2".equals(belong_type)){//从部门
					sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where state="+state+" and belong_type="+belong_type+" and e0122='"+this.getParentE0122(this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122"))+"') order by start_time desc,p0100,seq";
				}
			}else if("1".equals(isChuZhang)){
                if("0".equals(belong_type)){//从人员进
                	sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where state="+state+" and belong_type="+belong_type+" and e0122 like'"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"%') order by start_time desc,p0100,seq";
				}else if("1".equals(belong_type)){//从处室
					sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where state="+state+" and belong_type="+belong_type+" and e0122='"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"') order by start_time desc,p0100,seq";
				}else if("2".equals(belong_type)){//从部门
					sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where state="+state+" and belong_type="+belong_type+" and e0122='"+this.getParentE0122(this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122"))+"') order by start_time desc,p0100,seq";
				}
			}else if("2".equals(isChuZhang)){
                if("0".equals(belong_type)){//从人员进
                	sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where state="+state+" and belong_type="+belong_type+" and e0122 like '"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"%') order by start_time desc,p0100,seq";
				}else if("1".equals(belong_type)){//从处室
					sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where state="+state+" and belong_type="+belong_type+" and e0122 like '"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"%') order by start_time desc,p0100,seq";
				}else if("2".equals(belong_type)){//从部门
					sql="select p0100,content,start_time,end_time from per_diary_content where content like '%"+queryContent+"%' and p0100 in(select p0100 from p01 where state="+state+" and belong_type="+belong_type+" and e0122='"+this.getUserDetail(userView.getDbname()+userView.getA0100(), "e0122")+"') order by start_time desc,p0100,seq";
				}
			}
			
		}
		//System.out.println(sql);
		ContentDAO dao = new ContentDAO(this.con);
		rowSet = dao.search(sql);
		int count = 1;
		while(rowSet.next())
		{
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("count",String.valueOf(count));
			bean.set("content", Sql_switcher.readMemo(rowSet, "content"));
			Date date1 = rowSet.getDate("start_time");
			String d1 = sdf2.format(date1);
			Date date2 = rowSet.getDate("end_time");
			String d2 = sdf2.format(date2);
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date1);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date2);
			if("2".equals(state)){
				bean.set("cycle", cal1.get(Calendar.YEAR)+"-"+(cal1.get(Calendar.MONTH)+1));
			}else if("1".equals(state)){
				bean.set("cycle", d1+"--"+d2);
			}
			bean.set("p0100", rowSet.getString("p0100"));
			bean.set("state", state);
			count++;
			list.add(bean);
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}finally{
		try{
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	return list;
}
public HashMap getCollectMap(String p0100,String log_type){
	HashMap map = new HashMap();
	RowSet rowSet = null;
	try{
		ContentDAO dao = new ContentDAO(this.con);
		if(p0100!=null&&!"".equals(p0100)){
			rowSet = dao.search("select * from per_diary_content where log_type="+log_type+" and p0100="+p0100);
			while(rowSet.next()){
				map.put(rowSet.getInt("fromp0100")+","+rowSet.getInt("fromrecord_num"), "1");
			}
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}finally{
		try{
			if(rowSet!=null) {
                rowSet.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	return map;
	
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
/******************************周报******************************************************/	
/**
 * 
 * @return
 */
public String getSummarizeTime() {
	return summarizeTime;
}
public void setSummarizeTime(String summarizeTime) {
	this.summarizeTime = summarizeTime;
}
private String summarizeTime;
public String saveRecordWeek(String personPage,String isChuZhang,String type,String log_type,String p0100,String record_num,ArrayList fieldsValueList,String summarizeTime,Date start_date,Date end_date){
	ContentDAO dao = new ContentDAO(this.con);
	RecordVo p01vo = new RecordVo("p01");
	RecordVo pdcvo = new RecordVo("per_diary_content");
	SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
	String temp=p0100;
	try{
		if(summarizeTime==null|| "".equals(summarizeTime)){
			WeekWorkPlanBo bo = new WeekWorkPlanBo(this.userView,this.con,"1","",personPage);
			HashMap aMap =bo.getSameDeptUP(start_date, nbase+a0100);
			if(aMap.get("start_date")!=null){
				Date asDate = (Date)aMap.get("start_date");
				Date aeDate = (Date)aMap.get("end_date");
				summarizeTime=format.format(asDate)+"--"+format.format(aeDate);
			}
			if(summarizeTime==null|| "".equals(summarizeTime)){
				summarizeTime=bo.getInitZJ(format.format(start_date));//=
			}
			Date asZJdate =format.parse(summarizeTime.split("--")[0]);
			Date aeZJDate = format.parse(summarizeTime.split("--")[1]);
			String ap0100 = String.valueOf(getP0100());
			p01vo.setInt("p0100", Integer.parseInt(ap0100));
			p01vo.setInt("state", 1);
			p01vo.setInt("log_type", 1);
			p01vo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
			if("0".equals(personPage)){
				p01vo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
			}else if("1".equals(personPage)){
				//父部门
				p01vo.setString("e0122", getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122")));
			}
			p01vo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
			p01vo.setString("nbase", this.nbase);
			p01vo.setString("a0100", this.a0100);
			p01vo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
			p01vo.setString("p0115", "02");
			p01vo.setDate("p0104", asZJdate);
			p01vo.setDate("p0106", aeZJDate);
			if("0".equals(personPage)){
				p01vo.setInt("belong_type", 0);
			}else if("1".equals(personPage)){
				p01vo.setInt("belong_type", 2);
			}
			dao.addValueObject(p01vo);
		}
		this.setSummarizeTime(summarizeTime);
		Date sZJdate =format.parse(summarizeTime.split("--")[0]);
		Date eZJDate = format.parse(summarizeTime.split("--")[1]);
		if("1".equals(type)){
			
			if(p0100==null|| "".equals(p0100)){
				p0100 = String.valueOf(getP0100());
				temp=p0100;
				p01vo.setInt("p0100", Integer.parseInt(p0100));
				p01vo.setInt("state", 1);
				p01vo.setInt("log_type", 1);
				p01vo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
				if("0".equals(personPage)){
					p01vo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
				}else if("1".equals(personPage)){
					//父部门
					p01vo.setString("e0122", getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122")));
				}
				p01vo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
				p01vo.setString("nbase", this.nbase);
				p01vo.setString("a0100", this.a0100);
				p01vo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
				p01vo.setString("p0115", "01");
				p01vo.setDate("p0104", start_date);
				p01vo.setDate("p0106", end_date);
				if("0".equals(personPage)){
					p01vo.setInt("belong_type", 0);
				}else if("1".equals(personPage)){
					p01vo.setInt("belong_type", 2);
				}
				dao.addValueObject(p01vo);
			}
    		pdcvo.setInt("p0100", Integer.parseInt(p0100));
    		record_num = String.valueOf(getMaxRecord_num(Integer.parseInt(p0100)));
    		pdcvo.setInt("record_num", Integer.parseInt(record_num));
    		pdcvo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
    		pdcvo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
    		pdcvo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
    		pdcvo.setString("nbase", this.nbase);
    		pdcvo.setString("a0100", this.a0100);
    		pdcvo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
    		pdcvo.setInt("log_type", Integer.parseInt(log_type));
    		pdcvo.setInt("fromflag", 1);
    		pdcvo.setInt("seq",getMaxSeq(Integer.parseInt(p0100),Integer.parseInt(log_type)));
    		if("1".equals(log_type)){
    			pdcvo.setDate("start_time", start_date);
    			pdcvo.setDate("end_time", end_date);
    		}else{
    			pdcvo.setDate("start_time", sZJdate);
    			pdcvo.setDate("end_time", eZJDate);
    		}
    		for (int i = 0; i < fieldsValueList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean)fieldsValueList.get(i);
				String itemtype = (String)bean.get("itemtype");
				String itemid = (String)bean.get("itemid");
				
				if("N".equalsIgnoreCase(itemtype)){
					pdcvo.setString(itemid, (String)bean.get("value"));
				}else if("A".equalsIgnoreCase(itemtype)){
					pdcvo.setString(itemid, (String)bean.get("value"));
				}else if("D".equalsIgnoreCase(itemtype)){
					pdcvo.setDate(itemid, (Date)bean.get("value"));
				}else if("M".equalsIgnoreCase(itemtype)){
					pdcvo.setString(itemid, (String)bean.get("value"));
				}else{
					pdcvo.setString(itemid, (String)bean.get("value"));
				}
			}
    		dao.addValueObject(pdcvo);
    		
		}else{
			pdcvo.setInt("p0100", Integer.parseInt(p0100));
    		record_num = String.valueOf(record_num);
    		pdcvo.setInt("record_num", Integer.parseInt(record_num));
       		for (int i = 0; i < fieldsValueList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean)fieldsValueList.get(i);
				String itemtype = (String)bean.get("itemtype");
				String itemid = (String)bean.get("itemid");
				
				if("N".equalsIgnoreCase(itemtype)){
					pdcvo.setString(itemid, (String)bean.get("value"));
				}else if("A".equalsIgnoreCase(itemtype)){
					pdcvo.setString(itemid, PubFunc.keyWord_reback((String)bean.get("value")));
				}else if("D".equalsIgnoreCase(itemtype)){
					pdcvo.setDate(itemid, (Date)bean.get("value"));
				}else if("M".equalsIgnoreCase(itemtype)){
					pdcvo.setString(itemid, (String)bean.get("value"));
				}else{
					pdcvo.setString(itemid, PubFunc.keyWord_reback((String)bean.get("value")));
				}
			}
    		dao.updateValueObject(pdcvo);
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return temp;
}
/**
 * Description: 提取上期计划
 * @Version1.0 
 * Mar 7, 2013 9:25:23 AM Jianghe created
 * @param p0100
 * @param currentYear
 * @param currentMonth
 * @return
 */
public String collectRecordWeek(String state,String p0100,String summarizeTime,Date jhS_date,Date jhE_date,ArrayList jihuaFieldsList,ArrayList zongjieFieldsList,String personPage,String isChuZhang){
	RowSet rs = null;
	String message = "error";
	try {
		ContentDAO dao = new ContentDAO(this.con);
		RecordVo p01vo = new RecordVo("p01");
		RecordVo pdcvo = new RecordVo("per_diary_content");
		String[] temp =summarizeTime.split("--");
		Date sDate = sdf2.parse(temp[0]);
		Date eDate = sdf2.parse(temp[1]);
		String previousP0100 = getSummarizeP0100Week(sDate, eDate, state, personPage, isChuZhang);
		if(previousP0100==null|| "".equals(previousP0100)){
			message = "error";
		}
		else{
			String sql = "select * from per_diary_content where log_type=1 and p0100="+previousP0100;
			rs  = dao.search(sql);
			message = "error";
			if(p0100==null|| "".equals(p0100)){
				p0100 = String.valueOf(getP0100());
				p01vo.setInt("p0100", Integer.parseInt(p0100));
				p01vo.setInt("state", 1);
				p01vo.setInt("log_type", 1);
				p01vo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
				if("0".equals(personPage)){
					p01vo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
				}else if("1".equals(personPage)){
					//父部门
					p01vo.setString("e0122", getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122")));
				}
				p01vo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
				p01vo.setString("nbase", this.nbase);
				p01vo.setString("a0100", this.a0100);
				p01vo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
				p01vo.setString("p0115", "01");
				p01vo.setDate("p0104", jhS_date);
				p01vo.setDate("p0106", jhE_date);
				if("0".equals(personPage)){
					p01vo.setInt("belong_type", 0);
				}else if("1".equals(personPage)){
					p01vo.setInt("belong_type", 2);
				}
				dao.addValueObject(p01vo);
			}
			int  record_num =getMaxRecord_num(Integer.parseInt(p0100));
			int maxSeq=getMaxSeq(Integer.parseInt(p0100),Integer.parseInt("2"));
			HashMap map = this.getCollectMap(p0100, "2");
			while(rs.next()){
				int fromp0100=rs.getInt("p0100");
				int fromrecord_num = rs.getInt("record_num");
				if(map.get(fromp0100+","+fromrecord_num)!=null)//已经提取过的不能再次提取
                {
                    continue;
                }
	    		pdcvo.setInt("p0100", Integer.parseInt(p0100));
	    		pdcvo.setInt("record_num", record_num);
	    		pdcvo.setString("b0110", rs.getString("b0110"));
	    		pdcvo.setString("e0122", rs.getString("e0122"));
	    		pdcvo.setString("e01a1", rs.getString("e01a1"));
	    		pdcvo.setString("nbase", rs.getString("nbase"));
	    		pdcvo.setString("a0100", rs.getString("a0100"));
	    		pdcvo.setString("a0101", rs.getString("a0101"));
	    		pdcvo.setInt("log_type", 2);
	    		pdcvo.setInt("seq",maxSeq);
    			pdcvo.setDate("start_time", sDate);
    			pdcvo.setDate("end_time", sDate);
    			pdcvo.setInt("fromflag", 2);//=1自己新建，=2是提取计划，=3汇总
    			pdcvo.setInt("fromp0100", rs.getInt("p0100"));
    			pdcvo.setInt("fromrecord_num",rs.getInt("record_num"));
    			for(int i=0;i<zongjieFieldsList.size();i++)
				{
					LazyDynaBean fieldbean = (LazyDynaBean)zongjieFieldsList.get(i);
					String fieldItemValue = "";
					String fieldId = (String)fieldbean.get("itemid");
					String fieldItemType = (String)fieldbean.get("itemid");
					String codesetid = (String)fieldbean.get("codesetid");
					String decimalwidth = (String)fieldbean.get("decimalwidth");
					if("N".equalsIgnoreCase(fieldItemType)){
						fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
						fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
					}else if("A".equalsIgnoreCase(fieldItemType)){
						if("0".equals(codesetid)){
							fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
						}else{
							fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
							fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
						}
					}else if("D".equalsIgnoreCase(fieldItemType)){
						    Date date = rs.getDate(fieldId);
						    fieldItemValue = sdf2.format(date);
					}else if("M".equalsIgnoreCase(fieldItemType)){
						fieldItemValue = Sql_switcher.readMemo(rs, fieldId)==null?"":Sql_switcher.readMemo(rs, fieldId);
					}else{
						fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
					}
					if("D".equalsIgnoreCase(fieldItemType)){
						pdcvo.setDate(fieldId, fieldItemValue);
					}else{
						pdcvo.setString(fieldId, fieldItemValue);
					}
					
				}
	    		dao.addValueObject(pdcvo);
	    		message = "ok";
	    		record_num++;
	    		maxSeq++;
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		if(rs!=null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	return message;
}
/**
 * Description: 获取传入时间的工作总结的p0100 提取
 * @Version1.0 
 * Mar 7, 2013 6:22:48 PM Jianghe created
 * @param currentYear
 * @param currentMonth
 * @param state
 * @return
 */
public String getSummarizeP0100Week(Date sDate,Date eDate,String state,String personPage,String isChuZhang){
	ContentDAO dao = new ContentDAO(this.con);
	RowSet rs = null;
	String startDate = sdf.format(sDate);
	String endDate =sdf.format(eDate);
	String thep0100=null;
	try
	{
		StringBuffer buffer = new StringBuffer(); 
		if("0".equals(personPage)){		//人员进入
			if("0".equals(isChuZhang)|| "2".equals(isChuZhang)){
				buffer.append("select p0100 from p01");
		    	buffer.append(" where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate);
		    	buffer.append(" and "+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106")+"="+endDate);
		    	buffer.append(" and a0100='"+this.a0100+"' and nbase='"+this.nbase+"' and log_type=1 ");
		    	buffer.append(" and state="+state+" and (belong_type="+isChuZhang+" or belong_type is null)");
			}else if("1".equals(isChuZhang)){
				buffer.append("select p0100 from p01 ");
				buffer.append(" where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate);
				buffer.append(" and "+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106")+"="+endDate);
				buffer.append(" and a0100='"+this.a0100+"' and nbase='"+this.nbase+"' and log_type=1 and state="+state+" and (belong_type="+isChuZhang+" or belong_type=0)");
			}
		}else if("1".equals(personPage)){//部门进入
			buffer.append("select p0100 from p01 ");
			buffer.append(" where "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+startDate);
			buffer.append(" and "+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106")+"="+endDate);
			buffer.append(" and e0122='"+getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122"))+"' and nbase='"+this.nbase+"' ");
			buffer.append(" and log_type=1 and state="+state+" and belong_type=2");
		}
		rs=dao.search(buffer.toString());
    	if(rs.next()){
    		thep0100 = String.valueOf(rs.getInt("p0100"));
    	}
	}catch(Exception e)
	{
		e.printStackTrace();
	}finally{
		try{
			if(rs!=null) {
                rs.close();
            }
		}catch(Exception e){
    		e.printStackTrace();
		}
	}
    return thep0100;
}
/**
 * Description: 汇总报给自己的计划或总结
 * @Version1.0 
 * Mar 7, 2013 9:25:23 AM Jianghe created
 * @param p0100
 * @param currentYear
 * @param currentMonth
 * @return
 */
public String gatherRecordWeek(String log_type,String personPage,String isChuZhang,String state,String p0115,String p0100,String summarizeTime,Date jhs_dDate,Date jhe_dDate,ArrayList jihuaFieldsList,ArrayList zongjieFieldsList){
	RowSet rs = null;
	String message = "error";
	try {
		ContentDAO dao = new ContentDAO(this.con);
		RecordVo p01vo = new RecordVo("p01");
		RecordVo pdcvo = new RecordVo("per_diary_content");
		SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
	    /***
	     * 如果自己上期没有计划，自动创建，如果自己本期没有计划，自动创建，
	     */
		if(summarizeTime==null|| "".equals(summarizeTime)){
			WeekWorkPlanBo bo = new WeekWorkPlanBo(this.userView,this.con,"1","",personPage);
			HashMap aMap =bo.getSameDeptUP(jhs_dDate, nbase+a0100);
			if(aMap.get("start_date")!=null){
				Date asDate = (Date)aMap.get("start_date");
				Date aeDate = (Date)aMap.get("end_date");
				summarizeTime=format.format(asDate)+"--"+format.format(aeDate);
			}
			if(summarizeTime==null|| "".equals(summarizeTime)){
				summarizeTime=bo.getInitZJ(format.format(jhs_dDate));//=
			}
			Date asZJdate =format.parse(summarizeTime.split("--")[0]);
			Date aeZJDate = format.parse(summarizeTime.split("--")[1]);
			String ap0100 = String.valueOf(getP0100());
			p01vo.setInt("p0100", Integer.parseInt(ap0100));
			p01vo.setInt("state", 1);
			p01vo.setInt("log_type", 1);
			p01vo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
			if("0".equals(personPage)){
				p01vo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
			}else if("1".equals(personPage)){
				//父部门
				p01vo.setString("e0122", getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122")));
			}
			p01vo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
			p01vo.setString("nbase", this.nbase);
			p01vo.setString("a0100", this.a0100);
			p01vo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
			p01vo.setString("p0115", "02");
			p01vo.setDate("p0104", asZJdate);
			p01vo.setDate("p0106", aeZJDate);
			if("0".equals(personPage)){//0人员，1部门
				p01vo.setInt("belong_type", 0);
			}else if("1".equals(personPage)){
				p01vo.setInt("belong_type", 2);
			}
			dao.addValueObject(p01vo);
		}
		this.setSummarizeTime(summarizeTime);
		Date sZJdate =format.parse(summarizeTime.split("--")[0]);
		Date eZJDate = format.parse(summarizeTime.split("--")[1]);
		/*String previousP0100 = getGatherP0100(currentYear,currentMonth,state,personPage,getUsername(this.nbase+this.a0100));
		if(previousP0100==null||previousP0100.equals("")){
			message = "error";
		}
		else{*/
		    Calendar sCalendar = Calendar.getInstance();
		    Calendar eCalendar = Calendar.getInstance();
		    sCalendar.setTime(jhs_dDate);
		    eCalendar.setTime(jhe_dDate);
		    int sint=sCalendar.get(Calendar.YEAR)*10000+(sCalendar.get(Calendar.MONTH)+1)*100+sCalendar.get(Calendar.DAY_OF_MONTH);
		    int eint=eCalendar.get(Calendar.YEAR)*10000+(eCalendar.get(Calendar.MONTH)+1)*100+eCalendar.get(Calendar.DAY_OF_MONTH);
			StringBuffer buf = new StringBuffer("");
			buf.append(" select * from per_diary_content where ");
			buf.append(" log_type="+log_type);
			buf.append(" and p0100 in (");
			buf.append(" select p0100 from  p01 where ");
			buf.append(" state=1 and ");
			/**有问题？？*/
			if("0".equals(personPage)){
				buf.append(" belong_type=0");//个人的
			}else {
				buf.append(" belong_type=1");//部门部门进入汇总各处室
			}
			buf.append(" and "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+sint);
			buf.append(" and "+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106")+"="+eint);
			buf.append(" and p0115='02' ");
			buf.append(" and (curr_user='"+this.getUsername(this.nbase+this.a0100)+"'");
			if("1".equals(personPage)){
				buf.append(" or e0122='"+this.userView.getUserDeptId()+"'");
			}
			buf.append("))");
			rs  = dao.search(buf.toString());
			message = "error";
			if(p0100==null|| "".equals(p0100)){
				p0100 = String.valueOf(getP0100());
				p01vo.setInt("p0100", Integer.parseInt(p0100));
				p01vo.setInt("state", 1);
				p01vo.setInt("log_type", 1);
				p01vo.setString("b0110", getUserDetail(this.nbase+this.a0100,"b0110"));
				if("0".equals(personPage)){
					p01vo.setString("e0122", getUserDetail(this.nbase+this.a0100,"e0122"));
				}else if("1".equals(personPage)){
					//父部门
					p01vo.setString("e0122", getParentE0122(this.getUserDetail(this.nbase+this.a0100, "e0122")));
				}
				p01vo.setString("e01a1", getUserDetail(this.nbase+this.a0100,"e01a1"));
				p01vo.setString("nbase", this.nbase);
				p01vo.setString("a0100", this.a0100);
				p01vo.setString("a0101", getUserDetail(this.nbase+this.a0100,"a0101"));
				p01vo.setString("p0115", "01");
				p01vo.setDate("p0104", jhs_dDate);
				p01vo.setDate("p0106", jhe_dDate);
				if("0".equals(personPage)){
					p01vo.setInt("belong_type", 0);
				}else if("1".equals(personPage)){
					p01vo.setInt("belong_type", 2);
				}
				dao.addValueObject(p01vo);
			}
			int record_num=getMaxRecord_num(Integer.parseInt(p0100));
			int maxSeq=getMaxSeq(Integer.parseInt(p0100),Integer.parseInt(log_type));
			HashMap map = this.getCollectMap(p0100, log_type);
			while(rs.next()){
				int fromp0100=rs.getInt("p0100");
				int fromrecord_num = rs.getInt("record_num");
				if(map.get(fromp0100+","+fromrecord_num)!=null)//已经汇总过的不能再次汇总
                {
                    continue;
                }
				pdcvo.setInt("p0100", Integer.parseInt(p0100));
				pdcvo.setInt("record_num", record_num);
				pdcvo.setString("b0110", rs.getString("b0110"));
				pdcvo.setString("e0122", rs.getString("e0122"));
				pdcvo.setString("e01a1", rs.getString("e01a1"));
				pdcvo.setString("nbase", rs.getString("nbase"));
				pdcvo.setString("a0100", rs.getString("a0100"));
				pdcvo.setString("a0101", rs.getString("a0101"));
				pdcvo.setInt("log_type", Integer.parseInt(log_type));
				pdcvo.setInt("seq",maxSeq);
				if("1".equals(log_type)){//计划
					pdcvo.setDate("start_time", jhs_dDate);
					pdcvo.setDate("end_time", jhe_dDate);
				}else {
					pdcvo.setDate("start_time", sZJdate);
					pdcvo.setDate("end_time", eZJDate);
				}
				pdcvo.setInt("fromflag", 3);
				pdcvo.setInt("fromp0100", fromp0100);
				pdcvo.setInt("fromrecord_num",fromrecord_num);
				if("1".equals(log_type)){
					for(int i=0;i<jihuaFieldsList.size();i++)
					{
						LazyDynaBean fieldbean = (LazyDynaBean)jihuaFieldsList.get(i);
						String fieldItemValue = "";
						String fieldId = (String)fieldbean.get("itemid");
						String fieldItemType = (String)fieldbean.get("itemid");
						String codesetid = (String)fieldbean.get("codesetid");
						String decimalwidth = (String)fieldbean.get("decimalwidth");
						if("N".equalsIgnoreCase(fieldItemType)){
							fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
							fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
						}else if("A".equalsIgnoreCase(fieldItemType)){
							if("0".equals(codesetid)){
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
							}else{
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
							}
						}else if("D".equalsIgnoreCase(fieldItemType)){
							Date date = rs.getDate(fieldId);
							fieldItemValue = sdf2.format(date);
						}else if("M".equalsIgnoreCase(fieldItemType)){
							fieldItemValue = Sql_switcher.readMemo(rs, fieldId)==null?"":Sql_switcher.readMemo(rs, fieldId);
						}else{
							fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
						}
						if("D".equalsIgnoreCase(fieldItemType)){
							pdcvo.setDate(fieldId, fieldItemValue);
						}else{
							pdcvo.setString(fieldId, fieldItemValue);
						}
						
					}
				}else{
					for(int i=0;i<zongjieFieldsList.size();i++)
					{
						LazyDynaBean fieldbean = (LazyDynaBean)zongjieFieldsList.get(i);
						String fieldItemValue = "";
						String fieldId = (String)fieldbean.get("itemid");
						String fieldItemType = (String)fieldbean.get("itemid");
						String codesetid = (String)fieldbean.get("codesetid");
						String decimalwidth = (String)fieldbean.get("decimalwidth");
						if("N".equalsIgnoreCase(fieldItemType)){
							fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
							fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
						}else if("A".equalsIgnoreCase(fieldItemType)){
							if("0".equals(codesetid)){
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
							}else{
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
							}
						}else if("D".equalsIgnoreCase(fieldItemType)){
							Date date = rs.getDate(fieldId);
							fieldItemValue = sdf2.format(date);
						}else if("M".equalsIgnoreCase(fieldItemType)){
							fieldItemValue = Sql_switcher.readMemo(rs, fieldId)==null?"":Sql_switcher.readMemo(rs, fieldId);
						}else{
							fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
						}
						if("D".equalsIgnoreCase(fieldItemType)){
							pdcvo.setDate(fieldId, fieldItemValue);
						}else{
							pdcvo.setString(fieldId, fieldItemValue);
						}
					}
				}
				dao.addValueObject(pdcvo);
			    message = "ok";
			    record_num++;
			    maxSeq++;
			}
			if("1".equals(isChuZhang)&& "1".equals(personPage)){//如果处长从部门进入，汇总时，要把自己处室的汇总（因为不能自己报给自己，所以特殊处理）
				buf.setLength(0);
				buf.append("select p0100 from p01 where state=1 ");
				buf.append(" and "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+sint);
				buf.append(" and "+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106")+"="+eint);
                buf.append(" and a0100='"+this.a0100+"' and upper(nbase)='"+this.nbase.toUpperCase()+"' and belong_type=0 ");
                StringBuffer s_buf = new StringBuffer();
                s_buf.append(" select * from per_diary_content where ");
                s_buf.append(" log_type="+log_type);
                s_buf.append(" and p0100 in (");
				s_buf.append(buf.toString());
                s_buf.append(")");
				rs  = dao.search(s_buf.toString());
				int temp =0;
				while(rs.next()){
					int fromp0100=rs.getInt("p0100");
					int fromrecord_num = rs.getInt("record_num");
					if(map.get(fromp0100+","+fromrecord_num)!=null)//已经提取过的不能再次提取
                    {
                        continue;
                    }
					temp = rs.getInt("p0100");
					pdcvo.setInt("p0100", Integer.parseInt(p0100));
					pdcvo.setInt("record_num", record_num);
					pdcvo.setString("b0110", rs.getString("b0110"));
					pdcvo.setString("e0122", rs.getString("e0122"));
					pdcvo.setString("e01a1", rs.getString("e01a1"));
					pdcvo.setString("nbase", rs.getString("nbase"));
					pdcvo.setString("a0100", rs.getString("a0100"));
					pdcvo.setString("a0101", rs.getString("a0101"));
					pdcvo.setInt("log_type", Integer.parseInt(log_type));
					pdcvo.setInt("seq",maxSeq);
					pdcvo.setInt("fromflag", 3);
					pdcvo.setInt("fromp0100", fromp0100);
					pdcvo.setInt("fromrecord_num",fromrecord_num);
					if("1".equals(log_type)){//计划
						pdcvo.setDate("start_time", jhs_dDate);
						pdcvo.setDate("end_time", jhe_dDate);
					}else {
						pdcvo.setDate("start_time", sZJdate);
						pdcvo.setDate("end_time", eZJDate);
					}
					if("1".equals(log_type)){
						for(int i=0;i<jihuaFieldsList.size();i++)
						{
							LazyDynaBean fieldbean = (LazyDynaBean)jihuaFieldsList.get(i);
							String fieldItemValue = "";
							String fieldId = (String)fieldbean.get("itemid");
							String fieldItemType = (String)fieldbean.get("itemid");
							String codesetid = (String)fieldbean.get("codesetid");
							String decimalwidth = (String)fieldbean.get("decimalwidth");
							if("N".equalsIgnoreCase(fieldItemType)){
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
							}else if("A".equalsIgnoreCase(fieldItemType)){
								if("0".equals(codesetid)){
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								}else{
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
									fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
								}
							}else if("D".equalsIgnoreCase(fieldItemType)){
								Date date = rs.getDate(fieldId);
								fieldItemValue = sdf2.format(date);
							}else if("M".equalsIgnoreCase(fieldItemType)){
								fieldItemValue = Sql_switcher.readMemo(rs, fieldId)==null?"":Sql_switcher.readMemo(rs, fieldId);
							}else{
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
							}
							if("D".equalsIgnoreCase(fieldItemType)){
								pdcvo.setDate(fieldId, fieldItemValue);
							}else{
								pdcvo.setString(fieldId, fieldItemValue);
							}
							
						}
					}else{
						for(int i=0;i<zongjieFieldsList.size();i++)
						{
							LazyDynaBean fieldbean = (LazyDynaBean)zongjieFieldsList.get(i);
							String fieldItemValue = "";
							String fieldId = (String)fieldbean.get("itemid");
							String fieldItemType = (String)fieldbean.get("itemid");
							String codesetid = (String)fieldbean.get("codesetid");
							String decimalwidth = (String)fieldbean.get("decimalwidth");
							if("N".equalsIgnoreCase(fieldItemType)){
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								fieldItemValue=PubFunc.round(fieldItemValue,Integer.parseInt(decimalwidth));
							}else if("A".equalsIgnoreCase(fieldItemType)){
								if("0".equals(codesetid)){
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
								}else{
									fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
									fieldItemValue = AdminCode.getCodeName(codesetid,fieldItemValue);
								}
							}else if("D".equalsIgnoreCase(fieldItemType)){
								Date date = rs.getDate(fieldId);
								fieldItemValue = sdf2.format(date);
							}else if("M".equalsIgnoreCase(fieldItemType)){
								fieldItemValue = Sql_switcher.readMemo(rs, fieldId)==null?"":Sql_switcher.readMemo(rs, fieldId);
							}else{
								fieldItemValue = rs.getString(fieldId)==null?"":rs.getString(fieldId);
							}
							if("D".equalsIgnoreCase(fieldItemType)){
								pdcvo.setDate(fieldId, fieldItemValue);
							}else{
								pdcvo.setString(fieldId, fieldItemValue);
							}
						}
					}
					dao.addValueObject(pdcvo);
				    message = "ok";
				    record_num++;
				    maxSeq++;
				}
				if(temp!=0){
					buf.setLength(0);
					buf.append("update p01 set p0115='02',belong_type=1 where ");
					buf.append(" state=1 and "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+"="+sint);
					buf.append(" and "+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106")+"="+eint);
                    buf.append(" and a0100='"+this.a0100+"' and upper(nbase)='"+this.nbase.toUpperCase()+"' and belong_type=0 ");
                    dao.update(buf.toString());
				}
			}	
		/*}*/
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		if(rs!=null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	return message;
}
}
