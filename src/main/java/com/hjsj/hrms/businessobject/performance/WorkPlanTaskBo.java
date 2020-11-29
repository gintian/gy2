package com.hjsj.hrms.businessobject.performance;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class WorkPlanTaskBo {
	private Connection con;
	private UserView userView;
	public static HashMap workParametersMap=new HashMap();//参数采用静态变量，不用每次都查。
	private String state;//=0 日报    =1 周报    =2 月报     =3 季报     =4 年报
	String nbase = "Usr";
	String a0100 = "";
	WeekUtils weekutil = new WeekUtils();
	public WorkPlanTaskBo()
	{
		
	}
	public WorkPlanTaskBo(UserView userView,Connection con)
	{
		this.userView=userView;
		this.con=con;
		a0100 = userView.getA0100();
	}
	public WorkPlanTaskBo(Connection con,UserView userView,String workType,String state)
	{
		this.con=con;
		this.userView=userView;
		this.state=state;
		a0100 = userView.getA0100();
	}
	/**
	 * 分析设置的用于工作计划和总结的参数
	 */
	public void analyseParameter()
	{
		
		RowSet rs =null;
		try
		{
			workParametersMap.clear();
            ContentDAO dao = new ContentDAO(this.con);	
          /*  < work_records  sp_relation=”xxx” nbase="Usr" sp_level="2" record_grade="True" planTarget="P0413,P0415" summTarget="P04Aa,P04Ac" 
           * 	dailyPlan_attachment=”true” dailySumm_attachment =”true” >   
           * 	//sp_relation:审批关系 nbase:人员库设置  sp_level: 审批层级 1|2 一级审批|逐级审批  record_grade: 纪实评分，True|False  
           * 	planTarget: 工作计划指标 ，分隔  summTarget: 工作总结指标 ，分隔  dailyPlan_attachment:工作计划附件上传  dailySumm_attachment：工作总结附件上传
                cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
                type:(1|2)=(工作计划|工作总结)  
        		valid:是否有效（1|0）=（有效|无效）
                prior_end:上期末 current_start:本期初  current_end:本期末 
                last_start:下期初   Period:填报周期  refer_id:参考信息登记表
                print_id:打印信息登记表
        		<work_record     cycle=”3” > 
        	        <template type="2"  valid=”1”  prior_end=”3”  current_start=”5” Period=” 1,2,3,4,5,6,7,8,9,10,11,12”  refer_id=”1”  print_id=”2” >
        	    </work_record>

        	    <work_record     cycle=”4” > //日报  current_date:（1|0）=（当日|次日）
                   <template type="0"  valid=”1”  current_date=”1” time=”8:30” refer_id=”1”  print_id=”2” > 
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
	    		    	    	
	    		    	    	/*
	    		    	    	 * cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
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
		    		    	    			String prior_end14 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start14 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String refer_id14 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id14 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid14", valid14);
		    		    	    			workParametersMap.put("prior_end14", prior_end14);
		    		    	    			workParametersMap.put("current_start14", current_start14);
		    		    	    			workParametersMap.put("refer_id14", refer_id14);
		    		    	    			workParametersMap.put("print_id14", print_id14);
		    		    	    		}
		    		    	    		else if("4".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//年总结
		    		    	    		{
		    		    	    			String valid24 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String current_end24 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start24 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String refer_id24 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id24 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid24", valid24);
		    		    	    			workParametersMap.put("current_end24", current_end24);
		    		    	    			workParametersMap.put("last_start24", last_start24);
		    		    	    			workParametersMap.put("refer_id24", refer_id24);
		    		    	    			workParametersMap.put("print_id24", print_id24);
		    		    	    		}
		    		    	    		else if("3".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//季计划
		    		    	    		{
		    		    	    			String valid13 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String prior_end13 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start13 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String period13 = isNull(temp.getAttributeValue("period"));
		    		    	    			String refer_id13 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id13 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid13", valid13);
		    		    	    			workParametersMap.put("prior_end13", prior_end13);
		    		    	    			workParametersMap.put("current_start13", current_start13);
		    		    	    			workParametersMap.put("period13", period13);
		    		    	    			workParametersMap.put("refer_id13", refer_id13);
		    		    	    			workParametersMap.put("print_id13", print_id13);
		    		    	    		}
		    		    	    		else if("3".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//季总结
		    		    	    		{
		    		    	    			String valid23 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String current_end23 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start23 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String period23 = isNull(temp.getAttributeValue("period"));	    	    			
		    		    	    			String refer_id23 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id23 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid23",valid23);
		    		    	    			workParametersMap.put("current_end23",current_end23);
		    		    	    			workParametersMap.put("last_start23",last_start23);
		    		    	    			workParametersMap.put("period23",period23);
		    		    	    			workParametersMap.put("refer_id23",refer_id23);
		    		    	    			workParametersMap.put("print_id23",print_id23);
		    		    	    		}
		    		    	    		else if("2".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//月计划
		    		    	    		{
		    		    	    			String valid12 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String prior_end12 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start12 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String period12 = isNull(temp.getAttributeValue("period"));		    		    	    			
		    		    	    			String refer_id12 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id12 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid12",valid12);
		    		    	    			workParametersMap.put("prior_end12",prior_end12);
		    		    	    			workParametersMap.put("current_start12",current_start12);
		    		    	    			workParametersMap.put("period12",period12);
		    		    	    			workParametersMap.put("refer_id12",refer_id12);
		    		    	    			workParametersMap.put("print_id12",print_id12);
		    		    	    		}
		    		    	    		else if("2".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//月总结
		    		    	    		{
		    		    	    			String valid22 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String current_end22 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start22 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String period22 = isNull(temp.getAttributeValue("period"));
		    		    	    			String refer_id22 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id22 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid22", valid22);
		    		    	    			workParametersMap.put("current_end22", current_end22);
		    		    	    			workParametersMap.put("last_start22", last_start22);
		    		    	    			workParametersMap.put("period22", period22);
		    		    	    			workParametersMap.put("refer_id22", refer_id22);
		    		    	    			workParametersMap.put("print_id22", print_id22);		    		    	    			
		    		    	    		}
		    		    	    		else if("1".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))//周计划
		    		    	    		{
		    		    	    			String valid11 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String prior_end11 = isNullToZero(temp.getAttributeValue("prior_end"));
		    		    	    			String current_start11 = isNullToZero(temp.getAttributeValue("current_start"));
		    		    	    			String refer_id11 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id11 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid11", valid11);
		    		    	    			workParametersMap.put("prior_end11", prior_end11);
		    		    	    			workParametersMap.put("current_start11", current_start11);
		    		    	    			workParametersMap.put("refer_id11", refer_id11);
		    		    	    			workParametersMap.put("print_id11", print_id11);
		    		    	    		}
		    		    	    		else if("1".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))//周总结
		    		    	    		{
		    		    	    			String valid21 = isNull(temp.getAttributeValue("valid"));		    		    	    		
		    		    	    			String current_end21 = isNullToZero(temp.getAttributeValue("current_end"));
		    		    	    			String last_start21 = isNullToZero(temp.getAttributeValue("last_start"));
		    		    	    			String refer_id21 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id21 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid21", valid21);		    		    	    		
		    		    	    			workParametersMap.put("current_end21", current_end21);
		    		    	    			workParametersMap.put("last_start21", last_start21);
		    		    	    			workParametersMap.put("refer_id21", refer_id21);
		    		    	    			workParametersMap.put("print_id21", print_id21);
		    		    	    		}
		    		    	    		else if("0".equalsIgnoreCase(cycle))
		    		    	    		{
		    		    	    			String valid0 = isNull(temp.getAttributeValue("valid"));
		    		    	    			String current_date = isNull(temp.getAttributeValue("current_date"));
		    		    	    			String time = isNull(temp.getAttributeValue("time"));
		    		    	    			String refer_id0 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			String print_id0 = isNull(temp.getAttributeValue("print_id"));
		    		    	    			workParametersMap.put("valid0", valid0);
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
	 * Description: 获取数据向首页展示version2版本
	 * @Version1.0 
	 * Aug 20, 2012 2:21:44 PM Jianghe created
	 * @param state
	 * @param date
	 * @param list
	 * @return
	 * @throws GeneralException 
	 */
	
	public ArrayList getDataversion2(String year,String season,String month,String week,String day,String state,ArrayList listAll,String returnURL,String target) throws GeneralException{
		this.createNewTable("per_diary_opinion");
		this.state = state;
		RowSet rs = null;
		String thenbase = SafeCode.encode(PubFunc.convertTo64Base(isNull(nbase)));
		String thea0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(a0100)));
		try{
		    ContentDAO dao = new ContentDAO(this.con);
			if("0".equals(state)){//日报
				String valid0=(String)workParametersMap.get("valid0");
				if(valid0==null || valid0.trim().length()<=0 || "0".equals(valid0)) {
                    return listAll;
                }
				HashMap thisjhMap = new HashMap();
				HashMap thiszjMap = new HashMap();
				HashMap prezjMap = new HashMap();
				HashMap nextjhMap = new HashMap();
				Date thisdate = new Date();
	    		int thisyear = DateUtils.getYear(thisdate);
	    		int thismonth = DateUtils.getMonth(thisdate);
	    		int thisday = DateUtils.getDay(thisdate);
	    		
	    		Date predate = DateUtils.addDays(thisdate, -1);
	    		int preyear = DateUtils.getYear(predate);
	    		int premonth = DateUtils.getMonth(predate);
	    		int preday = DateUtils.getDay(predate);
	    		
	    		Date nextdate = DateUtils.addDays(thisdate, 1);
	    		int nextyear = DateUtils.getYear(nextdate);
	    		int nextmonth = DateUtils.getMonth(nextdate);
	    		int nextday = DateUtils.getDay(nextdate);
	    		
	    		StringBuffer buf = new StringBuffer("");
				buf.append(" select p0104,"+Sql_switcher.year("p0104")+" as ayear,"+Sql_switcher.month("p0104")+" as amonth,"+Sql_switcher.day("p0104")+" as aday, p0115,log_type,p0100,curr_user from p01 ");
				buf.append(" where ");
				buf.append(" state="+state);
				buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
				buf.append(" and a0100='"+this.userView.getA0100()+"'");
				buf.append(" and( ");
				buf.append(" ( log_type=2 and "+Sql_switcher.year("p0104")+" = "+preyear+" and "+Sql_switcher.month("p0104")+"="+premonth+" and "+Sql_switcher.day("p0104")+" = "+preday+" )" );
				buf.append(" or ( "+Sql_switcher.year("p0104")+" = "+thisyear+" and "+Sql_switcher.month("p0104")+"="+thismonth+" and "+Sql_switcher.day("p0104")+" = "+thisday+" )" );
				buf.append(" or ( log_type=1 and "+Sql_switcher.year("p0104")+" = "+nextyear+" and "+Sql_switcher.month("p0104")+"="+nextmonth+" and "+Sql_switcher.day("p0104")+" = "+nextday+" )" );
				buf.append(" ) ");	
				buf.append(" order by p0104");
				rs = dao.search(buf.toString());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				while(rs.next())
				{
					int log_type=rs.getInt("log_type");//=1计划=2总结
					String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
					bean.set("p0115",p0115);
					bean.set("p0100", rs.getString("p0100"));
					bean.set("curr_user", isNull(rs.getString("curr_user")));
					String p0104_format=format.format(rs.getDate("p0104"));
					if(log_type==2 && p0104_format.equals(format.format(predate)))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						prezjMap.put(p0104_format+"", bean);
					}
					if(log_type==1 && p0104_format.equals(format.format(thisdate)))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						thisjhMap.put(p0104_format+"", bean);
					}
					if(log_type==2 && p0104_format.equals(format.format(thisdate)))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						thiszjMap.put(p0104_format+"", bean);
					}
					if(log_type==1 && p0104_format.equals(format.format(nextdate)))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						nextjhMap.put(p0104_format+"", bean);
					}
				}
				//上日总结
				if("1".equals(valid0)){
					String log_type="2";
					
	    			String year_num = String.valueOf(preyear);
	    			String quarter_num ="";
	    			String month_num = String.valueOf(premonth);
	    			String week_num = "";
	    			String day_num =String.valueOf(preday);
		    		if(prezjMap.get(format.format(predate)+"")!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)prezjMap.get(format.format(predate)+"");
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(this.isEditTimeZJ(0, 0, Integer.parseInt(month_num), "0", Integer.parseInt(year_num),Integer.parseInt(day_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.day")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}else{
                        String opt="0";
		    				if(this.isEditTimeZJ(0, 0, Integer.parseInt(month_num), "0", Integer.parseInt(year_num),Integer.parseInt(day_num))){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.day")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//今日计划
				if("1".equals(valid0)){
                    String log_type="1";
	    			String year_num = String.valueOf(thisyear);
	    			String quarter_num ="";
	    			String month_num = String.valueOf(thismonth);
	    			String week_num = "";
	    			String day_num =String.valueOf(thisday);
		    		if(thisjhMap.get(format.format(thisdate)+"")!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thisjhMap.get(format.format(thisdate)+"");
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(this.isEditTimeJH(0, 0, Integer.parseInt(month_num), "0", Integer.parseInt(year_num),Integer.parseInt(day_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.day")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}else{
                        String opt="0";
		    				if(this.isEditTimeJH(0, 0, Integer.parseInt(month_num), "0", Integer.parseInt(year_num),Integer.parseInt(day_num))){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.day")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		} 
				}
				//今日总结
				if("1".equals(valid0)){
					String log_type="2";
					
	    			String year_num = String.valueOf(thisyear);
	    			String quarter_num ="";
	    			String month_num = String.valueOf(thismonth);
	    			String week_num = "";
	    			String day_num =String.valueOf(thisday);
		    		if(thiszjMap.get(format.format(thisdate)+"")!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thiszjMap.get(format.format(thisdate)+"");
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(this.isEditTimeZJ(0, 0, Integer.parseInt(month_num), "0", Integer.parseInt(year_num),Integer.parseInt(day_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.day")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}else{
                        String opt="0";
		    				if(this.isEditTimeZJ(0, 0, Integer.parseInt(month_num), "0", Integer.parseInt(year_num),Integer.parseInt(day_num))){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.day")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//下日计划
				if("1".equals(valid0)){
                    String log_type="1";
	    			String year_num = String.valueOf(nextyear);
	    			String quarter_num ="";
	    			String month_num = String.valueOf(nextmonth);
	    			String week_num = "";
	    			String day_num =String.valueOf(nextday);
		    		if(nextjhMap.get(format.format(nextdate)+"")!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)nextjhMap.get(format.format(nextdate)+"");
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(this.isEditTimeJH(0, 0, Integer.parseInt(month_num), "0", Integer.parseInt(year_num),Integer.parseInt(day_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.day")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}else{
                        String opt="0";
		    				if(this.isEditTimeJH(0, 0, Integer.parseInt(month_num), "0", Integer.parseInt(year_num),Integer.parseInt(day_num))){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.day")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		} 
				}
				
			}
			if("1".equals(state))//周报
			{
				//周计划
	    		String valid11 = isNull((String)workParametersMap.get("valid11"));
	    		String prior_end11 = isNull((String)workParametersMap.get("prior_end11"));
	    		String current_start11 = isNull((String)workParametersMap.get("current_start11"));
	    		//周总结
	    		String valid21 = isNull((String)workParametersMap.get("valid21"));		    		    	    		
	    		String current_end21 = isNull((String)workParametersMap.get("current_end21"));
	    		String last_start21 = isNull((String)workParametersMap.get("last_start21"));
	    		
	    		if("0".equals(valid11) && "0".equals(valid21)) {
                    return listAll;
                }
	    		
	    		
	    		WeekUtils weekutils = new WeekUtils();
	    		Date thisdate = new Date();
	    		int thisyear = DateUtils.getYear(thisdate);
	    		int thismonth = DateUtils.getMonth(thisdate);
	    		int thisday = DateUtils.getDay(thisdate);
	    		int totalweek = weekutils.totalWeek(thisyear,thismonth);
	    		Date startDate = weekutils.numWeek(thisyear,thismonth,1,1);
	    		Date endDate = weekutils.numWeek(thisyear,thismonth,totalweek,7);
	    		//本周星期一日期  XX年XX月XX天
	    		Date thisMondaydate=null;
    		    if(!"".equals(this.getWeek(thisdate))){
    		    	int thisweek = Integer.parseInt(this.getWeek(thisdate));
	    			thisMondaydate = weekutils.numWeek(thisyear,thismonth,thisweek,1);
    		    }
	    		if(thisdate.compareTo(startDate)<0){
	    			//上个月最后一周
	    			thismonth = thismonth-1;
	    			if(thismonth==12){
	    				thisyear = thisyear-1;
	    			}
	    			totalweek = weekutils.totalWeek(thisyear,thismonth);
	    			//本周星期一日期  XX年XX月XX天
	    			thisMondaydate = weekutils.numWeek(thisyear,thismonth,totalweek,1);
	    		}
	    		if(thisdate.compareTo(endDate)>0){
	    			//下个月个月第一周
	    			thismonth = thismonth+1;
	    			if(thismonth==1){
	    				thisyear = thisyear+1;
	    			}
	    			thisMondaydate = weekutils.numWeek(thisyear,thismonth,1,1);
	    		}
	    		
	    		thisyear = DateUtils.getYear(thisMondaydate);
	    		thismonth = DateUtils.getMonth(thisMondaydate);
	    		thisday = DateUtils.getDay(thisMondaydate);
	    		
	    		Date preMondaydate = DateUtils.addDays(thisMondaydate, -7);
	    		int preyear = DateUtils.getYear(preMondaydate);
	    		int premonth = DateUtils.getMonth(preMondaydate);
	    		int preday = DateUtils.getDay(preMondaydate);
	    		
	    		Date nextMondaydate = DateUtils.addDays(thisMondaydate, 7);
	    		int nextyear = DateUtils.getYear(nextMondaydate);
	    		int nextmonth = DateUtils.getMonth(nextMondaydate);
	    		int nextday = DateUtils.getDay(nextMondaydate);
//	    		System.out.println("本周星期一："+thisMondaydate.toString());
//	    		System.out.println("上周星期一："+preMondaydate.toString());
//	    		System.out.println("下周星期一："+nextMondaydate.toString());
	    		HashMap prezjMap = new HashMap();
				HashMap thisjhMap = new HashMap();
				HashMap thiszjMap = new HashMap();
				HashMap nextjhMap = new HashMap();
				StringBuffer buf = new StringBuffer("");
				buf.append(" select "+Sql_switcher.year("p0104")+" as ayear,"+Sql_switcher.month("p0104")+" as amonth,"+Sql_switcher.day("p0104")+" as aday, p0115,log_type,p0100,p0104,curr_user from p01 ");
				buf.append(" where ");
				buf.append(" state="+state);
				buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
				buf.append(" and a0100='"+this.userView.getA0100()+"'");
				buf.append(" and( ");
				buf.append(" ( log_type=2 and "+Sql_switcher.year("p0104")+" = "+preyear+" and "+Sql_switcher.month("p0104")+"="+premonth+" and "+Sql_switcher.day("p0104")+" = "+preday+" )" );
				buf.append(" or ( "+Sql_switcher.year("p0104")+" = "+thisyear+" and "+Sql_switcher.month("p0104")+"="+thismonth+" and "+Sql_switcher.day("p0104")+" = "+thisday+" )" );
				buf.append(" or ( log_type=1 and "+Sql_switcher.year("p0104")+" = "+nextyear+" and "+Sql_switcher.month("p0104")+"="+nextmonth+" and "+Sql_switcher.day("p0104")+" = "+nextday+" )" );
				buf.append(" ) ");			
															
				buf.append(" order by p0104");
				//System.out.println(buf.toString());
				rs = dao.search(buf.toString());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				while(rs.next())
				{
					int log_type=rs.getInt("log_type");//=1计划=2总结
					String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
					bean.set("p0115",p0115);
					bean.set("p0100", rs.getString("p0100"));
					bean.set("curr_user", isNull(rs.getString("curr_user")));
					String p0104_format=format.format(rs.getDate("p0104"));
					if(log_type==2 && p0104_format.equals(format.format(preMondaydate)))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						prezjMap.put(p0104_format+"", bean);
					}
					if(log_type==1 && p0104_format.equals(format.format(thisMondaydate)))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						thisjhMap.put(p0104_format+"", bean);
					}
					if(log_type==2 && p0104_format.equals(format.format(thisMondaydate)))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						thiszjMap.put(p0104_format+"", bean);
					}
					if(log_type==1 && p0104_format.equals(format.format(nextMondaydate)))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						nextjhMap.put(p0104_format+"", bean);
					}
				}
				//上周总结
				if("1".equals(valid21)){
					String log_type="2";
					int []yAndmAndw = this.getDateWhere(preMondaydate);
	    			String year_num = String.valueOf(yAndmAndw[0]);
	    			String quarter_num ="";
	    			String month_num = String.valueOf(yAndmAndw[1]);
	    			String week_num =String.valueOf(yAndmAndw[2]);
	    			String day_num ="";
		    		if(prezjMap.get(format.format(preMondaydate)+"")!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)prezjMap.get(format.format(preMondaydate)+"");
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(current_end21)&&!"".equals(last_start21)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeZJ(Integer.parseInt(current_end21), Integer.parseInt(last_start21),Integer.parseInt(month_num) , "1", Integer.parseInt(year_num),Integer.parseInt(week_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.week")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(current_end21)&&!"".equals(last_start21)&&!"".equals(year_num)){
		                if(this.isEditTimeZJ(Integer.parseInt(current_end21), Integer.parseInt(last_start21), Integer.parseInt(month_num), "1", Integer.parseInt(year_num),Integer.parseInt(week_num))) {
                            opt="1";
                        }
		    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.week")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//本周计划
				if("1".equals(valid11)){
					String log_type="1";
					int []yAndmAndw = this.getDateWhere(thisMondaydate);
	    			String year_num = String.valueOf(yAndmAndw[0]);
	    			String quarter_num ="";
	    			String month_num = String.valueOf(yAndmAndw[1]);
	    			String week_num =String.valueOf(yAndmAndw[2]);
	    			String day_num ="";
	    			if(thisjhMap.get(format.format(thisMondaydate)+"")!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thisjhMap.get(format.format(thisMondaydate)+"");
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(prior_end11)&&!"".equals(current_start11)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end11), Integer.parseInt(current_start11),Integer.parseInt(month_num) , "1", Integer.parseInt(year_num),Integer.parseInt(week_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.week")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}		
				     }else{
			                String opt="0";
			                
			    			if(!"".equals(prior_end11)&&!"".equals(current_start11)&&!"".equals(year_num)){
			                if(this.isEditTimeZJ(Integer.parseInt(prior_end11), Integer.parseInt(current_start11), Integer.parseInt(month_num), "1", Integer.parseInt(year_num),Integer.parseInt(week_num))) {
                                opt="1";
                            }
			    			}
			                if("1".equals(opt)){
			    				CommonData data = new CommonData();
			    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
			    				
			    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
			    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.week")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
			    				data.setDataValue(href);
			    				listAll.add(data);
			    			}
			    	 }	
				}
				//本周总结
				if("1".equals(valid21)){
					String log_type="2";
					int []yAndmAndw = this.getDateWhere(thisMondaydate);
	    			String year_num = String.valueOf(yAndmAndw[0]);
	    			String quarter_num ="";
	    			String month_num = String.valueOf(yAndmAndw[1]);
	    			String week_num =String.valueOf(yAndmAndw[2]);
	    			String day_num ="";
		    		if(thiszjMap.get(format.format(thisMondaydate)+"")!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thiszjMap.get(format.format(thisMondaydate)+"");
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(current_end21)&&!"".equals(last_start21)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeZJ(Integer.parseInt(current_end21), Integer.parseInt(last_start21),Integer.parseInt(month_num) , "1", Integer.parseInt(year_num),Integer.parseInt(week_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.week")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(current_end21)&&!"".equals(last_start21)&&!"".equals(year_num)){
		                if(this.isEditTimeZJ(Integer.parseInt(current_end21), Integer.parseInt(last_start21), Integer.parseInt(month_num), "1", Integer.parseInt(year_num),Integer.parseInt(week_num))) {
                            opt="1";
                        }
		    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.week")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//下周计划
				if("1".equals(valid11)){
					String log_type="1";
					int []yAndmAndw = this.getDateWhere(nextMondaydate);
	    			String year_num = String.valueOf(yAndmAndw[0]);
	    			String quarter_num ="";
	    			String month_num = String.valueOf(yAndmAndw[1]);
	    			String week_num =String.valueOf(yAndmAndw[2]);
	    			String day_num ="";
	    			if(nextjhMap.get(format.format(nextMondaydate)+"")!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)nextjhMap.get(format.format(nextMondaydate)+"");
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(prior_end11)&&!"".equals(current_start11)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end11), Integer.parseInt(current_start11),Integer.parseInt(month_num) , "1", Integer.parseInt(year_num),Integer.parseInt(week_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.week")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}		
				     }else{
			                String opt="0";
			                
			    			if(!"".equals(prior_end11)&&!"".equals(current_start11)&&!"".equals(year_num)){
			                if(this.isEditTimeJH(Integer.parseInt(prior_end11), Integer.parseInt(current_start11), Integer.parseInt(month_num), "1", Integer.parseInt(year_num),Integer.parseInt(week_num))) {
                                opt="1";
                            }
			    			}
			                if("1".equals(opt)){
			    				CommonData data = new CommonData();
			    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
			    				
			    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
			    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+day_num+ResourceFactory.getProperty("performance.workplan.workplanview.week")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
			    				data.setDataValue(href);
			    				listAll.add(data);
			    			}
			    	 }	
				}
			}
			if("2".equals(state)) // 月报
			{
				//计划参数
				String valid12="0";
				if(workParametersMap.get("valid12")!=null) {
                    valid12=(String)workParametersMap.get("valid12");
                }
				String prior_end12="";
				if(workParametersMap.get("prior_end12")!=null) {
                    prior_end12=(String)workParametersMap.get("prior_end12");
                }
				String current_start12="";
				if(workParametersMap.get("current_start12")!=null) {
                    current_start12=(String)workParametersMap.get("current_start12");
                }
				String period12="";
				if(workParametersMap.get("period12")!=null) {
                    period12=(String)workParametersMap.get("period12");
                }
				//总结参数
				String valid22="0";
				if(workParametersMap.get("valid22")!=null) {
                    valid22=(String)workParametersMap.get("valid22");
                }
				String current_end22="";
				if(workParametersMap.get("current_end22")!=null) {
                    current_end22=(String)workParametersMap.get("current_end22");
                }
				String last_start22="";
				if(workParametersMap.get("last_start22")!=null) {
                    last_start22=(String)workParametersMap.get("last_start22");
                }
                String period22="";
                if(workParametersMap.get("period22")!=null) {
                    period22=(String)workParametersMap.get("period22");
                }
    			if("0".equals(valid12)&&"0".equals(valid22)) {
                    return listAll;
                }
    			HashMap prezjMap = new HashMap();
				HashMap thisjhMap = new HashMap();
				HashMap thiszjMap = new HashMap();
				HashMap nextjhMap = new HashMap();
    			String t_period12=","+period12+",";
		    	String t_period22=","+period22+",";
		    	String thisyear = year;
	    		String nextyear = thisyear;
	    		String preyear = thisyear;
	    		String thismonth = month;
	    		String nextmonth = String.valueOf(Integer.parseInt(thismonth)+1);
	    		String premonth = String.valueOf(Integer.parseInt(thismonth)-1);
	    		if("1".equals(thismonth)){
	    			premonth = "12";
	    			preyear = String.valueOf(Integer.parseInt(thisyear)-1);
	    		}
	    		if("12".equals(thismonth)){
	    			nextmonth = "1";
	    			nextyear = String.valueOf(Integer.parseInt(thisyear)+1);
	    		}
			
				StringBuffer buf = new StringBuffer("");
				buf.append(" select "+Sql_switcher.year("p0104")+" as ayear,"+Sql_switcher.month("p0104")+" as amonth,p0115,log_type,p0100,curr_user from p01 ");
				buf.append(" where ");
				buf.append(" state="+state);
				buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
				buf.append(" and a0100='"+this.userView.getA0100()+"'");
				buf.append(" and( ");
				buf.append(" ( log_type=2 and "+Sql_switcher.year("p0104")+" = "+preyear+" and "+Sql_switcher.month("p0104")+"="+premonth+" )" );
				buf.append(" or ( "+Sql_switcher.year("p0104")+" = "+thisyear+" and "+Sql_switcher.month("p0104")+"="+thismonth+" )" );
				buf.append(" or ( log_type=1 and "+Sql_switcher.year("p0104")+" = "+nextyear+" and "+Sql_switcher.month("p0104")+"="+nextmonth+" )" );
				buf.append(" ) ");
				buf.append(" order by p0104");
				
				rs = dao.search(buf.toString());
				while(rs.next())
				{
					int log_type=rs.getInt("log_type");//=1计划=2总结
					String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
					String amonth=rs.getString("amonth");
					String ayear=rs.getString("ayear");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
					bean.set("p0115",p0115);
					bean.set("p0100", rs.getString("p0100"));
					bean.set("curr_user", isNull(rs.getString("curr_user")));
					if(log_type==2 && ayear.equals(preyear) && amonth.equals(premonth))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						prezjMap.put(amonth, bean);
					}
					if(log_type==1 && ayear.equals(thisyear) && amonth.equals(thismonth))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						thisjhMap.put(amonth, bean);
					}
					if(log_type==2 && ayear.equals(thisyear) && amonth.equals(thismonth))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						thiszjMap.put(amonth, bean);
					}
					if(log_type==1 && ayear.equals(nextyear) && amonth.equals(nextmonth))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						nextjhMap.put(amonth, bean);
					}
				}
				//上月工作总结
				if("1".equals(valid22)&&t_period22.indexOf(","+premonth+",")!=-1)//总结
				{
					String log_type="2";
	    			String year_num = preyear;
	    			String quarter_num ="";
	    			String month_num =premonth;
	    			String week_num ="";
	    			String day_num ="";
		    		if(prezjMap.get(month_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)prezjMap.get(month_num);
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(current_end22)&&!"".equals(last_start22)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeZJ(Integer.parseInt(current_end22), Integer.parseInt(last_start22),Integer.parseInt(month_num) , "2", Integer.parseInt(year_num),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(current_end22)&&!"".equals(last_start22)&&!"".equals(year_num)){
		                if(this.isEditTimeZJ(Integer.parseInt(current_end22), Integer.parseInt(last_start22), Integer.parseInt(month_num), "2", Integer.parseInt(year_num),0)) {
                            opt="1";
                        }
		    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//本月工作计划
				if("1".equals(valid12)&&t_period12.indexOf(","+thismonth+",")!=-1)//计划
				{
					String log_type="1";
	    			String year_num = thisyear;
	    			String quarter_num ="";
	    			String month_num =thismonth;
	    			String week_num ="";
	    			String day_num ="";
		    		if(thisjhMap.get(month_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thisjhMap.get(month_num);
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(prior_end12)&&!"".equals(current_start12)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end12), Integer.parseInt(current_start12), Integer.parseInt(month_num), "2", Integer.parseInt(year_num),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(prior_end12)&&!"".equals(current_start12)&&!"".equals(year_num)){
			                if(this.isEditTimeJH(Integer.parseInt(prior_end12), Integer.parseInt(current_start12), Integer.parseInt(month_num), "2", Integer.parseInt(year_num),0)) {
                                opt="1";
                            }
			    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//本月工作总结
				if("1".equals(valid22)&&t_period22.indexOf(","+thismonth+",")!=-1)//总结
				{
					String log_type="2";
	    			String year_num = thisyear;
	    			String quarter_num ="";
	    			String month_num =thismonth;
	    			String week_num ="";
	    			String day_num ="";
		    		if(thiszjMap.get(month_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thiszjMap.get(month_num);
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(current_end22)&&!"".equals(last_start22)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeZJ(Integer.parseInt(current_end22), Integer.parseInt(last_start22),Integer.parseInt(month_num) , "2", Integer.parseInt(year_num),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(current_end22)&&!"".equals(last_start22)&&!"".equals(year_num)){
		                if(this.isEditTimeZJ(Integer.parseInt(current_end22), Integer.parseInt(last_start22), Integer.parseInt(month_num), "2", Integer.parseInt(year_num),0)) {
                            opt="1";
                        }
		    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//下月工作计划
				if("1".equals(valid12)&&t_period12.indexOf(","+nextmonth+",")!=-1)//计划
				{
					String log_type="1";
	    			String year_num = nextyear;
	    			String quarter_num ="";
	    			String month_num =nextmonth;
	    			String week_num ="";
	    			String day_num ="";
		    		if(nextjhMap.get(month_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)nextjhMap.get(month_num);
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(prior_end12)&&!"".equals(current_start12)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end12), Integer.parseInt(current_start12), Integer.parseInt(month_num), "2", Integer.parseInt(year_num),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(prior_end12)&&!"".equals(current_start12)&&!"".equals(year_num)){
			                if(this.isEditTimeJH(Integer.parseInt(prior_end12), Integer.parseInt(current_start12), Integer.parseInt(month_num), "2", Integer.parseInt(year_num),0)) {
                                opt="1";
                            }
			    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+month_num+ResourceFactory.getProperty("performance.workplan.workplanview.month")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}	
			}
			if("3".equals(state)) // 季报
			{				
				// 季计划
	    		String valid13 = isNull((String)workParametersMap.get("valid13"));
	    		String prior_end13 = isNull((String)workParametersMap.get("prior_end13"));
	    		String current_start13 = isNull((String)workParametersMap.get("current_start13"));
	    		String period13 = isNull((String)workParametersMap.get("period13"));
	    		// 季总结
	    		String valid23 = isNull((String)workParametersMap.get("valid23"));		    		    	    		
	    		String current_end23 = isNull((String)workParametersMap.get("current_end23"));
	    		String last_start23 = isNull((String)workParametersMap.get("last_start23"));
	    		String period23 = isNull((String)workParametersMap.get("period23"));
	    		String t_period13 = ","+period13+",";
		    	String t_period23 = ","+period23+",";
		    	String thisyear = year;
	    		String nextyear = thisyear;
	    		String preyear = thisyear;
	    		String thisseason = season;
	    		String nextseason = String.valueOf(Integer.parseInt(thisseason)+1);
	    		String preseason = String.valueOf(Integer.parseInt(thisseason)-1);
	    		if("1".equals(thisseason)){
	    			preseason = "4";
	    			preyear = String.valueOf(Integer.parseInt(thisyear)-1);
	    		}
	    		if("4".equals(thisseason)){
	    			nextseason = "1";
	    			nextyear = String.valueOf(Integer.parseInt(thisyear)+1);
	    		}
		    	if("0".equals(valid13) && "0".equals(valid23)){
		    		return listAll;
		    	}
		    	HashMap prezjMap = new HashMap();
				HashMap thisjhMap = new HashMap();
				HashMap thiszjMap = new HashMap();
				HashMap nextjhMap = new HashMap();
				StringBuffer buf = new StringBuffer("");
				buf.append(" select "+Sql_switcher.year("p0104")+" as ayear,"+Sql_switcher.quarter("p0104")+" as aquarter,p0115,log_type,p0100,curr_user from p01 ");
				buf.append(" where ");
				buf.append(" state="+this.state);
				buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
				buf.append(" and a0100='"+this.userView.getA0100()+"'");
				buf.append(" and( ");
				buf.append(" ( log_type=2 and "+Sql_switcher.year("p0104")+" = "+preyear+" and "+Sql_switcher.quarter("p0104")+"="+preseason+" )" );
				buf.append(" or ( "+Sql_switcher.year("p0104")+" = "+thisyear+" and "+Sql_switcher.quarter("p0104")+"="+thisseason+" )" );
				buf.append(" or ( log_type=1 and "+Sql_switcher.year("p0104")+" = "+nextyear+" and "+Sql_switcher.quarter("p0104")+"="+nextseason+" )" );
				buf.append(" ) ");
				buf.append(" order by p0104");
				rs = dao.search(buf.toString());
				while(rs.next())
				{
					int log_type=rs.getInt("log_type");//=1计划=2总结
					String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
					String ayear = rs.getString("ayear");
					String aquarter=rs.getString("aquarter");
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
					bean.set("p0115",p0115);
					bean.set("p0100", rs.getString("p0100"));
					bean.set("curr_user", isNull(rs.getString("curr_user")));
					if(log_type==2 && ayear.equals(preyear) && aquarter.equals(preseason))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						prezjMap.put(aquarter, bean);
					}
					if(log_type==1 && ayear.equals(thisyear) && aquarter.equals(thisseason))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						thisjhMap.put(aquarter, bean);
					}
					if(log_type==2 && ayear.equals(thisyear) && aquarter.equals(thisseason))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						thiszjMap.put(aquarter, bean);
					}
					if(log_type==1 && ayear.equals(nextyear) && aquarter.equals(nextseason))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						nextjhMap.put(aquarter, bean);
					}
				}
				//上季度工作总结
				if("1".equals(valid23)&&t_period23.indexOf(","+preseason+",")!=-1)//总结
				{
					String log_type="2";
	    			String year_num = preyear;
	    			String quarter_num =preseason;
	    			String month_num ="";
	    			String week_num ="";
	    			String day_num ="";
		    		if(prezjMap.get(quarter_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)prezjMap.get(quarter_num);
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(current_end23)&&!"".equals(last_start23)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeZJ(Integer.parseInt(current_end23), Integer.parseInt(last_start23), 0, "3", Integer.parseInt(year_num),Integer.parseInt(quarter_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+quarter_num+ResourceFactory.getProperty("performance.workplan.workplanview.quarter")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(current_end23)&&!"".equals(last_start23)&&!"".equals(year_num)){
		                if(this.isEditTimeZJ(Integer.parseInt(current_end23), Integer.parseInt(last_start23), 0, "3", Integer.parseInt(year_num),Integer.parseInt(quarter_num))) {
                            opt="1";
                        }
		    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+quarter_num+ResourceFactory.getProperty("performance.workplan.workplanview.quarter")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//本季度工作计划		
				if("1".equals(valid13)&&t_period13.indexOf(","+thisseason+",")!=-1)//计划
				{
					String log_type="1";
	    			String year_num = thisyear;
	    			String quarter_num =thisseason;
	    			String month_num ="";
	    			String week_num ="";
	    			String day_num ="";
		    		if(thisjhMap.get(quarter_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thisjhMap.get(quarter_num);
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(prior_end13)&&!"".equals(current_start13)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end13), Integer.parseInt(current_start13), 0, "3", Integer.parseInt(year_num),Integer.parseInt(quarter_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+quarter_num+ResourceFactory.getProperty("performance.workplan.workplanview.quarter")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(prior_end13)&&!"".equals(current_start13)&&!"".equals(year_num)){
			                if(this.isEditTimeJH(Integer.parseInt(prior_end13), Integer.parseInt(current_start13), 0, "3", Integer.parseInt(year_num),Integer.parseInt(quarter_num))) {
                                opt="1";
                            }
			    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+quarter_num+ResourceFactory.getProperty("performance.workplan.workplanview.quarter")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//本季度工作总结
				if("1".equals(valid23)&&t_period23.indexOf(","+thisseason+",")!=-1)//总结
				{
					String log_type="2";
	    			String year_num = thisyear;
	    			String quarter_num =thisseason;
	    			String month_num ="";
	    			String week_num ="";
	    			String day_num ="";
		    		if(thiszjMap.get(quarter_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thiszjMap.get(quarter_num);
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(current_end23)&&!"".equals(last_start23)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeZJ(Integer.parseInt(current_end23), Integer.parseInt(last_start23), 0, "3", Integer.parseInt(year_num),Integer.parseInt(quarter_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+quarter_num+ResourceFactory.getProperty("performance.workplan.workplanview.quarter")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(current_end23)&&!"".equals(last_start23)&&!"".equals(year_num)){
		                if(this.isEditTimeZJ(Integer.parseInt(current_end23), Integer.parseInt(last_start23), 0, "3", Integer.parseInt(year_num),Integer.parseInt(quarter_num))) {
                            opt="1";
                        }
		    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+quarter_num+ResourceFactory.getProperty("performance.workplan.workplanview.quarter")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
				//下季度工作计划		
				if("1".equals(valid13)&&t_period13.indexOf(","+nextseason+",")!=-1)//计划
				{
					String log_type="1";
	    			String year_num = nextyear;
	    			String quarter_num =nextseason;
	    			String month_num ="";
	    			String week_num ="";
	    			String day_num ="";
		    		if(nextjhMap.get(quarter_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)nextjhMap.get(quarter_num);
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String opt="0";
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(prior_end13)&&!"".equals(current_start13)&&!"".equals(year_num)){
			    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end13), Integer.parseInt(current_start13), 0, "3", Integer.parseInt(year_num),Integer.parseInt(quarter_num)) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+quarter_num+ResourceFactory.getProperty("performance.workplan.workplanview.quarter")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    			
		    		}else{
		                String opt="0";
		                
		    			if(!"".equals(prior_end13)&&!"".equals(current_start13)&&!"".equals(year_num)){
			                if(this.isEditTimeJH(Integer.parseInt(prior_end13), Integer.parseInt(current_start13), 0, "3", Integer.parseInt(year_num),Integer.parseInt(quarter_num))) {
                                opt="1";
                            }
			    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.di")+quarter_num+ResourceFactory.getProperty("performance.workplan.workplanview.quarter")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
				}
			}
			if("4".equals(state)) // 年报
			{
				// 年计划
	    		String valid14 = isNull((String)workParametersMap.get("valid14"));
	    		String prior_end14 = isNull((String)workParametersMap.get("prior_end14"));
	    		String current_start14 = isNull((String)workParametersMap.get("current_start14"));
	    		// 年总结
	    		String valid24 = isNull((String)workParametersMap.get("valid24"));		    		    	    		
	    		String current_end24 = isNull((String)workParametersMap.get("current_end24"));
	    		String last_start24 = isNull((String)workParametersMap.get("last_start24"));
	    		String thisyear = year;
	    		String nextyear = String.valueOf(Integer.parseInt(thisyear)+1);
	    		String preyear = String.valueOf(Integer.parseInt(thisyear)-1);
				
	    		if("0".equals(valid14) && "0".equals(valid24)){
	    			return listAll;
	    		}
                
	    		
	    		HashMap prezjMap = new HashMap();
				HashMap thisjhMap = new HashMap();
				HashMap thiszjMap = new HashMap();
				HashMap nextjhMap = new HashMap();
				StringBuffer buf = new StringBuffer("");
				buf.append(" select "+Sql_switcher.year("p0104")+" as ayear,p0115,log_type,p0100,curr_user from p01 ");
				buf.append(" where ");
				buf.append(" state="+state);
				buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
				buf.append(" and a0100='"+this.userView.getA0100()+"'");
				buf.append(" and( ");
				buf.append(" ( log_type=2 and "+Sql_switcher.year("p0104")+" = "+preyear+" )" );
				buf.append(" or ( "+Sql_switcher.year("p0104")+" = "+thisyear+" )" );
				buf.append(" or ( log_type=1 and "+Sql_switcher.year("p0104")+" = "+nextyear+" )" );
				buf.append(" ) ");
				buf.append(" order by p0104");
				rs = dao.search(buf.toString());
				while(rs.next())
				{ 	
					int log_type=rs.getInt("log_type");//=1计划=2总结
					String p0115=rs.getString("p0115")==null?"01":rs.getString("p0115");
					String ayear=rs.getString("ayear");
					
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("p0115",p0115);
					bean.set("p0100", rs.getString("p0100"));
					bean.set("curr_user", isNull(rs.getString("curr_user")));
					bean.set("p0115desc",AdminCode.getCodeName("23", p0115));
					if(log_type==2 && ayear.equals(preyear))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						prezjMap.put(ayear, bean);
					}
					if(log_type==1 && ayear.equals(thisyear))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						thisjhMap.put(ayear, bean);
					}
					if(log_type==2 && ayear.equals(thisyear))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workSummary"));
						thiszjMap.put(ayear, bean);
					}
					if(log_type==1 && ayear.equals(nextyear))
					{
						bean.set("name",ResourceFactory.getProperty("performance.workplan.workplanview.workPlan"));
						nextjhMap.put(ayear, bean);
					}	
				}
				//上年工作总结
				if("1".equals(valid24))//总结
				{
					String log_type="2";
					String year_num = preyear;
	    			String quarter_num ="";
	    			String month_num ="";
	    			String week_num ="";
	    			String day_num ="";
					if(prezjMap.get(year_num)!=null)//已填报
					{
						LazyDynaBean abean =(LazyDynaBean)prezjMap.get(year_num);
						String opt="0";//操作按钮：=0查看，=1可填报
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}
		    			else{
		    				if(!"".equals(current_end24)&&!"".equals(last_start24)){
		    					//判断当前时间是否满足上年的的填报周期
		    					if(this.isEditTimeZJ(Integer.parseInt(current_end24), Integer.parseInt(last_start24), 0, "4", Integer.parseInt(preyear),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
			    		    		opt="1";
			    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
					}
					//未填
					else{
						String opt="0";
		    			if(!"".equals(current_end24)&&!"".equals(last_start24)){
		                if(this.isEditTimeZJ(Integer.parseInt(current_end24), Integer.parseInt(last_start24), 0, "4", Integer.parseInt(preyear),0)) {
                            opt="1";
                        }
		    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
					}
				}
		    	if("1".equals(valid14))//今年工作计划
		    	{
		    		String log_type="1";
		    		String year_num = thisyear;
	    			String quarter_num ="";
	    			String month_num ="";
	    			String week_num ="";
	    			String day_num ="";
		    		if(thisjhMap.get(year_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thisjhMap.get(year_num);
		    			String opt="0";//操作按钮：=0查看，=1可填报
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");	
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}
		    			else{
		    				if(!"".equals(prior_end14)&&!"".equals(current_start14)){
		    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end14), Integer.parseInt(current_start14), 0, "4", Integer.parseInt(thisyear),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}else{//未填过的
		                String opt="0";
		                if(!"".equals(prior_end14)&&!"".equals(current_start14)){
		                if(this.isEditTimeJH(Integer.parseInt(prior_end14), Integer.parseInt(current_start14), 0, "4", Integer.parseInt(thisyear),0)) {
                            opt="1";
                        }
		                }
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
		    	}
		    	if("1".equals(valid24)){//总结
		    		String log_type="2";
		    		String year_num = thisyear;
	    			String quarter_num ="";
	    			String month_num ="";
	    			String week_num ="";
	    			String day_num ="";
		    		if(thiszjMap.get(year_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)thiszjMap.get(year_num);
		    			String opt="0";
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}else{
		    				if(!"".equals(current_end24)&&!"".equals(last_start24)){
		    		    	if(this.isEditTimeZJ(Integer.parseInt(current_end24), Integer.parseInt(last_start24), 0, "4", Integer.parseInt(thisyear),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}else{
		                String opt="0";
		    			if(!"".equals(current_end24)&&!"".equals(last_start24)){
		                if(this.isEditTimeZJ(Integer.parseInt(current_end24), Integer.parseInt(last_start24), 0, "4", Integer.parseInt(thisyear),0)) {
                            opt="1";
                        }
		    			}
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.workSummary")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
		    	}
		    	//下年工作计划
		    	if("1".equals(valid14)){
		    		String log_type="1";
		    		String year_num = nextyear;
	    			String quarter_num ="";
	    			String month_num ="";
	    			String week_num ="";
	    			String day_num ="";
		    		if(nextjhMap.get(year_num)!=null)//以填报
		    		{
		    			LazyDynaBean abean =(LazyDynaBean)nextjhMap.get(year_num);
		    			String opt="0";//操作按钮：=0查看，=1可填报
		    			String p0115=(String)abean.get("p0115");
		    			String p0115desc=(String)abean.get("p0115desc");
		    			String p0100=(String)abean.get("p0100");
		    			String curr_user = null;
		    			if(abean.get("curr_user")!=null) {
                            curr_user=(String)abean.get("curr_user");
                        }
		    			if("07".equals(p0115))//驳回状态何时都能填报
		    			{
		    				if(curr_user==null || "".equals(curr_user.trim())) {
                                opt="1";
                            }
		    			}
		    			else{
		    				if(!"".equals(prior_end14)&&!"".equals(current_start14)){
		    		    	if(this.isEditTimeJH(Integer.parseInt(prior_end14), Integer.parseInt(current_start14), 0, "4", Integer.parseInt(nextyear),0) && "01".equals(p0115)){//时间符合并且还没报批的的也可以填报
		    		    		opt="1";
		    	    		}
		    				}
		    			}
		    			if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String thep0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull(p0100)));
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100="+thep0100+"&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+p0115desc);
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}else{//未填过的
		                String opt="0";
		                if(!"".equals(prior_end14)&&!"".equals(current_start14)){
		                if(this.isEditTimeJH(Integer.parseInt(prior_end14), Integer.parseInt(current_start14), 0, "4", Integer.parseInt(nextyear),0)) {
                            opt="1";
                        }
		                }
		                if("1".equals(opt)){
		    				CommonData data = new CommonData();
		    				opt = SafeCode.encode(PubFunc.convertTo64Base(opt));
		    				
		    				String href = "/performance/workplan/workplanview/workplan_view_list.do?b_write=write&mdopt="+opt+"&mdnbase="+thenbase+"&mda0100="+thea0100+"&log_type="+log_type+"&mdp0100=&state="+state+"&year_num="+year_num+"&quarter_num="+quarter_num+"&month_num="+month_num+"&week_num="+week_num+"&day_num="+day_num+"&returnURL="+returnURL+"&target="+target;
		    				data.setDataName(year_num+ResourceFactory.getProperty("performance.workplan.workplanview.year")+ResourceFactory.getProperty("performance.workplan.workplanview.workPlan")+"   "+ResourceFactory.getProperty("performance.workplan.workplanview.haveNotFill"));
		    				data.setDataValue(href);
		    				listAll.add(data);
		    			}
		    		}
		    	
		    	}
				      					
			} 
	
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			try
			{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return listAll;
	}
	/**
	 * 判断当前计划是否可编辑
	 * @param oneDay 上月末XX天
	 * @param twoDay 本月初XX天
	 * @param curr_month 当前记录月份（可能别的填报周期的判断用不到这个）
	 * @param cycle 填报周期
	 * @param curr_year 当前记录年
	 * @param curr_day 当前记录日期（号）
	 * @return
	 */
	public boolean isEditTimeJH(int oneDay,int twoDay,int curr_month,String cycle,int curr_year,int curr_day)
	{
		//上月末，本月初    cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
		boolean flag=false;
        if("0".equals(cycle))
        {
        	//current_date=”1” time=”8:30”  current_date:（1|0）=（当日|次日）
        	//当为日报时，start_date,end_date值相同
        	Calendar calendar = Calendar.getInstance();
        	String current_date=(String)workParametersMap.get("current_date");
        	if(current_date!=null && current_date.trim().length()>0 && "0".equals(current_date))//当日
        	{
        		Calendar start_calendar=Calendar.getInstance();
        		start_calendar.set(Calendar.YEAR,curr_year);
        		start_calendar.set(Calendar.MONTH,curr_month-1);
        		start_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		return calendar.get(Calendar.YEAR)==start_calendar.get(Calendar.YEAR)&&calendar.get(Calendar.MONTH)==start_calendar.get(Calendar.MONTH)&&calendar.get(Calendar.DAY_OF_MONTH)==start_calendar.get(Calendar.DAY_OF_MONTH);
        	}else{//次日
        		String time = (String)workParametersMap.get("time");
        		Calendar start_calendar=Calendar.getInstance();
        		start_calendar.set(Calendar.YEAR,curr_year);
        		start_calendar.set(Calendar.MONTH,curr_month-1);
        		start_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		start_calendar.set(Calendar.HOUR_OF_DAY,0);
        		start_calendar.set(Calendar.MINUTE,0);
        		start_calendar.set(Calendar.SECOND,0);
        		
        		String[] hhmm = new String[2];
        		if(time!=null && time.trim().length()>0) {
                    hhmm = time.split(":");
                }
        		Calendar end_calendar=Calendar.getInstance();
        		end_calendar.set(Calendar.YEAR,curr_year);
        		end_calendar.set(Calendar.MONTH,curr_month-1);
        		end_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		end_calendar.add(Calendar.DAY_OF_MONTH,1);
        		end_calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hhmm[0]));
        		end_calendar.set(Calendar.MINUTE,Integer.parseInt(hhmm[1]));
        		end_calendar.set(Calendar.SECOND,0);
        		//System.out.println("day_num="+curr_day+"------"+start_calendar.get(Calendar.YEAR)+"-"+(start_calendar.get(Calendar.MONTH)+1)+"-"+start_calendar.get(Calendar.DAY_OF_MONTH));
        		int result=end_calendar.compareTo(calendar);
        		int result2=start_calendar.compareTo(calendar);
        		return ((result==0||result==1)&&(result2==0||result2==-1)&&calendar.get(Calendar.YEAR)==start_calendar.get(Calendar.YEAR))?true:false;
        	}
        	
        }
        else if("1".equals(cycle))//周报
        {
        	//curr_day为当前是第几周
        	Calendar acalendar = Calendar.getInstance();
        	WeekUtils weekutils = new WeekUtils();
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        	Date startdate = weekutils.numWeek(curr_year,curr_month,curr_day,1);
        	String[] s_t=format.format(startdate).split("-");
			Date enddate = weekutils.numWeek(curr_year,curr_month,curr_day,7);
			String[] e_t=format.format(enddate).split("-");
        	Calendar calendar=Calendar.getInstance();
        	Calendar bcalendar=Calendar.getInstance();
        	acalendar.set(Calendar.YEAR,Integer.parseInt(s_t[0]));
        	acalendar.set(Calendar.MONTH,Integer.parseInt(s_t[1])-1);
        	acalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(s_t[2]));
        	
        	bcalendar.set(Calendar.YEAR,Integer.parseInt(e_t[0]));
        	bcalendar.set(Calendar.MONTH,Integer.parseInt(e_t[1])-1);
        	bcalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(e_t[2]));
        	
        	acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	
        	Calendar ccalendar=Calendar.getInstance();
        	ccalendar.set(Calendar.YEAR,Integer.parseInt(s_t[0]));
        	ccalendar.set(Calendar.MONTH,Integer.parseInt(s_t[1])-1);
        	ccalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(s_t[2]));
        	ccalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	return calendar.compareTo(acalendar)>=0&&calendar.compareTo(ccalendar)<=0&&calendar.compareTo(bcalendar)<=0;
        	      	
        	
        }
        else if("2".equals(cycle))
        {
        	Calendar calendar = Calendar.getInstance();
        	int day=calendar.get(Calendar.DAY_OF_MONTH);
        	
        	Calendar upCalendar = Calendar.getInstance();//2012-06-27
        	//跨年？？
        	if(curr_month==1)
        	{
        		upCalendar.set(Calendar.MONTH,11);
        		upCalendar.set(Calendar.YEAR,curr_year-1);
        	}else{
            	upCalendar.set(Calendar.MONTH,curr_month-2);//2012-01-27
        	}
        	int upMaxDay=upCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        	upCalendar.set(Calendar.DAY_OF_MONTH, upMaxDay);
        	upCalendar.set(Calendar.DAY_OF_MONTH, (upMaxDay-oneDay));
           // System.out.println(upCalendar.get(Calendar.YEAR)+"-"+(upCalendar.get(Calendar.MONTH)+1)+"-"+upCalendar.get(Calendar.DAY_OF_MONTH));
            
        	Calendar dwCalendar = Calendar.getInstance();
        	dwCalendar.set(Calendar.MONTH, curr_month-1);
        	dwCalendar.set(Calendar.DAY_OF_MONTH, twoDay);
        	int dwCount=dwCalendar.get(Calendar.YEAR)*10000+(dwCalendar.get(Calendar.MONTH)+1)*100+dwCalendar.get(Calendar.DAY_OF_MONTH);
        	int upCount=upCalendar.get(Calendar.YEAR)*10000+(upCalendar.get(Calendar.MONTH)+1)*100+upCalendar.get(Calendar.DAY_OF_MONTH);
        	int cuCount=calendar.get(Calendar.YEAR)*10000+(calendar.get(Calendar.MONTH)+1)*100+calendar.get(Calendar.DAY_OF_MONTH);
        	
        	return (dwCount>=cuCount&&cuCount>=upCount);
             
        }
        else if("3".equals(cycle))//季报
        {
        	//curr_day为当前记录是第几季度
        	if(curr_day==1)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,-1);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,0);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        	else if(curr_day==2)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,2);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,3);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);        		
        	}
        	else if(curr_day==3)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,5);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,6);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        	//  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        	else if(curr_day==4)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,8);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,9);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        }
        else if("4".equals(cycle))//年报
        {
        	Calendar calendar = Calendar.getInstance();
    		Calendar acalendar=Calendar.getInstance();
    		Calendar bcalendar=Calendar.getInstance();
    		acalendar.set(Calendar.YEAR,curr_year);
    		acalendar.set(Calendar.MONTH,-1);
    		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
    		acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
    		
    		bcalendar.set(Calendar.YEAR,curr_year);
    		bcalendar.set(Calendar.MONTH,0);
    		bcalendar.set(Calendar.DAY_OF_MONTH,1);
    		bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
    		
    	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
    		
    		return calendar.compareTo(acalendar)>=0&&calendar.compareTo(bcalendar)<=0;
    		
        }
		return flag;
	}
	/**
	 * 判断当前总结是否可编辑
	 * @param oneDay
	 * @param twoDay
	 * @param curr
	 * @param cycle
	 * @param curr_year
	 * @return
	 */
	public boolean isEditTimeZJ(int oneDay,int twoDay,int curr_month,String cycle,int curr_year,int curr_day)
	{
		//本月末，下月初  cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
		boolean flag = false;
		
		if("0".equals(cycle))
	    {
			//current_date=”1” time=”8:30”  current_date:（1|0）=（当日|次日）
        	//当为日报时，start_date,end_date值相同
        	Calendar calendar = Calendar.getInstance();
        	String current_date=(String)workParametersMap.get("current_date");
        	if("0".equals(current_date))//当日
        	{
        		Calendar start_calendar=Calendar.getInstance();
        		start_calendar.set(Calendar.YEAR,curr_year);
        		start_calendar.set(Calendar.MONTH,curr_month-1);
        		start_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		return calendar.get(Calendar.YEAR)==start_calendar.get(Calendar.YEAR)&&calendar.get(Calendar.MONTH)==start_calendar.get(Calendar.MONTH)&&calendar.get(Calendar.DAY_OF_MONTH)==start_calendar.get(Calendar.DAY_OF_MONTH);
        	}
        	else // 次日
        	{
        		String time=(String)workParametersMap.get("time");
        		Calendar start_calendar=Calendar.getInstance();
        		start_calendar.set(Calendar.YEAR,curr_year);
        		start_calendar.set(Calendar.MONTH,curr_month-1);
        		start_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		start_calendar.set(Calendar.HOUR_OF_DAY,0);
        		start_calendar.set(Calendar.MINUTE,0);
        		start_calendar.set(Calendar.SECOND,0);
        		
        		String[] hhmm=time.split(":");
        		Calendar end_calendar=Calendar.getInstance();
        		end_calendar.set(Calendar.YEAR,curr_year);
        		end_calendar.set(Calendar.MONTH,curr_month-1);
        		end_calendar.set(Calendar.DAY_OF_MONTH,curr_day);
        		end_calendar.add(Calendar.DAY_OF_MONTH,1);
        		end_calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hhmm[0]));
        		end_calendar.set(Calendar.MINUTE,Integer.parseInt(hhmm[1]));
        		end_calendar.set(Calendar.SECOND,0);
        		//System.out.println("day_num="+curr_day+"------"+start_calendar.get(Calendar.YEAR)+"-"+(start_calendar.get(Calendar.MONTH)+1)+"-"+start_calendar.get(Calendar.DAY_OF_MONTH));
        		int result=end_calendar.compareTo(calendar);
        		int result2=start_calendar.compareTo(calendar);
        		return ((result==0||result==1)&&(result2==0||result2==-1)&&calendar.get(Calendar.YEAR)==start_calendar.get(Calendar.YEAR))?true:false;
        	}
			
	    }else if("1".equals(cycle))
	    {
	    	//curr_day为当前是第几周 //本月末，下月初  
	    	//curr_day为当前是第几周
        	WeekUtils weekutils = new WeekUtils();
        	Date startdate = weekutils.numWeek(curr_year,curr_month,curr_day,1);
			Date enddate = weekutils.numWeek(curr_year,curr_month,curr_day,7);
        	Calendar calendar=Calendar.getInstance();
        	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        	String[] e_t=format.format(enddate).split("-");
        	Calendar acalendar = Calendar.getInstance();
        	acalendar.set(Calendar.YEAR,Integer.parseInt(e_t[0]));
        	acalendar.set(Calendar.MONTH,Integer.parseInt(e_t[1])-1);
        	acalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(e_t[2]));
        	
        	acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	
        	Calendar bcalendar = Calendar.getInstance();
        	bcalendar.set(Calendar.YEAR,Integer.parseInt(e_t[0]));
        	bcalendar.set(Calendar.MONTH,Integer.parseInt(e_t[1])-1);
        	bcalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(e_t[2]));
        	bcalendar.add(Calendar.DAY_OF_MONTH,twoDay+1);
        	
        	return calendar.compareTo(acalendar)>=0&&calendar.compareTo(bcalendar)<=0;
	    }
	    else if("2".equals(cycle))
	    {
	        Calendar calendar = Calendar.getInstance();
	        Calendar cuCalendar = Calendar.getInstance();
	        Calendar ntCalendar = Calendar.getInstance();
	        cuCalendar.set(Calendar.MONTH,curr_month-1);
	        int maxDay=cuCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	        cuCalendar.set(Calendar.DAY_OF_MONTH,maxDay-oneDay);
	        	
	        if(curr_month==12)
	        {
	        	ntCalendar.set(Calendar.YEAR,curr_year+1);
	        	ntCalendar.set(Calendar.MONTH,0);
	        }else{
	            ntCalendar.set(Calendar.MONTH,curr_month);
	        }
	        ntCalendar.set(Calendar.DAY_OF_MONTH, twoDay);
	        int ntCount=ntCalendar.get(Calendar.YEAR)*10000+(ntCalendar.get(Calendar.MONTH)+1)*100+ntCalendar.get(Calendar.DAY_OF_MONTH);
	        int cuCount=cuCalendar.get(Calendar.YEAR)*10000+(cuCalendar.get(Calendar.MONTH)+1)*100+cuCalendar.get(Calendar.DAY_OF_MONTH);
	        int count=calendar.get(Calendar.YEAR)*10000+(calendar.get(Calendar.MONTH)+1)*100+calendar.get(Calendar.DAY_OF_MONTH);
	        return (ntCount>=count&&count>=cuCount);
	    }
	    else if("3".equals(cycle)) // 季报
	    {
	    	//curr_day为当前记录是第几季度
        	if(curr_day==1)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,2);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,3);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        	//  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        	else if(curr_day==2)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,5);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,6);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);        		
        	}
        	else if(curr_day==3)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,8);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,9);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        	//  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}
        	else if(curr_day==4)
        	{
        		Calendar calendar = Calendar.getInstance();
        		Calendar acalendar=Calendar.getInstance();
        		Calendar bcalendar=Calendar.getInstance();
        		acalendar.set(Calendar.YEAR,curr_year);
        		acalendar.set(Calendar.MONTH,11);
        		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
        	    acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
        	    
        	    bcalendar.set(Calendar.YEAR,curr_year);
        	    bcalendar.set(Calendar.MONTH,12);
        	    bcalendar.set(Calendar.DAY_OF_MONTH,1);
        	    bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
        	    
        	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
            //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
        	    
        	    return (acalendar.compareTo(calendar)<=0&&bcalendar.compareTo(calendar)>=0);
        	}	        	
	    }
	    else if("4".equals(cycle)) // 年报
	    {
	    	Calendar calendar = Calendar.getInstance();
    		Calendar acalendar=Calendar.getInstance();
    		Calendar bcalendar=Calendar.getInstance();
    		acalendar.set(Calendar.YEAR,curr_year);
    		acalendar.set(Calendar.MONTH,11);
    		acalendar.set(Calendar.DAY_OF_MONTH,acalendar.getMaximum(Calendar.DAY_OF_MONTH));
    		acalendar.add(Calendar.DAY_OF_MONTH,-oneDay);
    		
    		bcalendar.set(Calendar.YEAR,curr_year);
    		bcalendar.set(Calendar.MONTH,12);
    		bcalendar.set(Calendar.DAY_OF_MONTH,1);
    		bcalendar.add(Calendar.DAY_OF_MONTH,twoDay);
    		
    	//  System.out.println(acalendar.get(Calendar.YEAR)+"-"+(acalendar.get(Calendar.MONTH)+1)+"-"+acalendar.get(Calendar.DAY_OF_MONTH));
        //  System.out.println(bcalendar.get(Calendar.YEAR)+"-"+(bcalendar.get(Calendar.MONTH)+1)+"-"+bcalendar.get(Calendar.DAY_OF_MONTH));
    		
    		return calendar.compareTo(acalendar)>=0&&calendar.compareTo(bcalendar)<=0;	    	
	    }
		return flag;		
     }
	/**
	 * Description:  本月是第几季度
	 * @Version1.0 
	 * Aug 3, 2012 3:20:14 PM Jianghe created
	 * @param month
	 * @return
	 */
	public String getSeason(String month){
		
        int ynum=Integer.parseInt(month);
		String returnValue="";
		if(ynum==1||ynum==2||ynum==3){
			returnValue = "1";
		}
		if(ynum==4||ynum==5||ynum==6){
			returnValue = "2";
		}
		if(ynum==7||ynum==8||ynum==9){
			returnValue = "3";
		}
		if(ynum==10||ynum==11||ynum==12){
			returnValue = "4";
		}
	    return returnValue;
	}
	/**
	 * Description: 根据日期判断是本月的第几周，在前台显示
	 * @Version1.0 
	 * Aug 3, 2012 3:56:06 PM Jianghe created
	 * @param date
	 * @return
	 */
	public String getWeek(Date date){
		String index = "";
		LinkedHashMap weekMap = getWeekIndex(String.valueOf(date.getYear()+1900),String.valueOf(date.getMonth()+1),"1");
		Set keySet=weekMap.keySet();
		  java.util.Iterator t=keySet.iterator();
		  SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		  int d = Integer.parseInt(sdf.format(date));
			while(t.hasNext())
			{
				String strKey = (String)t.next();  //键值	    
				String strValue = (String)weekMap.get(strKey);   //value值  
				String[] strArray = strKey.split("between");
				try
				{
    				
    				int d1 = Integer.parseInt(strArray[0].replace("-", ""));
    				int d2 = Integer.parseInt(strArray[1].replace("-", ""));
    				if(d<=d2&&d>=d1){
    					index = strValue;
    					break;
    				}
				}
				catch (Exception e) {
                    e.printStackTrace();
                }
			}
		 return index;	
	}
	/**
	 * Description: 某周的起始日期map（Date,月|周）
	 * @Version1.0 
	 * Aug 3, 2012 3:51:25 PM Jianghe created
	 * @param year
	 * @param month
	 * @param flag 0 用作前台jsp判断      1：当前日期在哪周  在前台显示
	 * @return
	 */
	public LinkedHashMap getWeekIndex(String year,String month,String flag){
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String d1 = null;
	    String d2 = null;
	    
		LinkedHashMap lhm = new LinkedHashMap();
		if(!"all".equals(month)){
			int aaweek = weekutil.totalWeek(Integer.parseInt(year),Integer.parseInt(month));
			for (int i = 1; i <= aaweek; i++) {
				if("1".equals(flag)){
				    d1 = sdf.format(weekutil.numWeek(Integer.parseInt(year), Integer.parseInt(month), i, 1));
				    d2 = sdf.format(weekutil.numWeek(Integer.parseInt(year), Integer.parseInt(month), i, 7));
					lhm.put(d1 + "between" + d2, String.valueOf(i));
				}else{
				    d1 = sdf.format(weekutil.numWeek(Integer.parseInt(year), Integer.parseInt(month), i, 1));
					lhm.put(d1,month+"|"+String.valueOf(i));
				}
			}
		}else{
			for (int i = 1; i <= 12; i++) {
				int aaweek = weekutil.totalWeek(Integer.parseInt(year),i);
				for (int j = 1; j <= aaweek; j++) {
					if("1".equals(flag)){
					    d1 = sdf.format(weekutil.numWeek(Integer.parseInt(year), i, j, 1));
					    d2 = sdf.format(weekutil.numWeek(Integer.parseInt(year), i, j, 7));
						lhm.put(d1 + "between" + d2, String.valueOf(j));
					}else{
					    d1 = sdf.format(weekutil.numWeek(Integer.parseInt(year), i, j, 1));
						lhm.put(d1, String.valueOf(i)+"|"+String.valueOf(j));
					}
				}
			}
		}
		return lhm;
	}
	//判断日期在哪年哪月第几周
	public int[] getDateWhere(Date date){
		int[]strArray = new int[3];
		int year = DateUtils.getYear(date);
		int month = DateUtils.getMonth(date);
		int day = DateUtils.getDay(date);
		int weekNum=0;
		WeekUtils weekutils = new WeekUtils();
		//判断是否在当月
		int totalweek = weekutils.totalWeek(year,month);
		Date startDate = weekutils.numWeek(year,month,1,1);
		Date endDate = weekutils.numWeek(year,month,totalweek,7);
		if(date.compareTo(endDate)<=0 && date.compareTo(startDate)>=0){
			weekNum = Integer.parseInt(this.getWeek(date));
		}
		if(date.compareTo(startDate)<0){
			month = month-1;
			if(month==12){
				year = year-1;
			}
			totalweek = weekutils.totalWeek(year,month);
			//找最后一周
			weekNum = totalweek;
		}
		if(date.compareTo(endDate)>0){
			//第一周
			month = month+1;
			if(month==1){
				year = year+1;
			}
			weekNum=1;
		}
		strArray[0] = year;
		strArray[1] = month;
		strArray[2] = weekNum;	
		return strArray;
	}
	/**
	 * Description: 如果不存在，创建新表
	 * @Version1.0 
	 * Sep 10, 2012 10:10:34 AM Jianghe created
	 * @param tableName
	 */
	public void createNewTable(String tableName){
		try{
			DbWizard dbWizard = new DbWizard(this.con);	
		    //ContentDAO dao = new ContentDAO(this.con);
		    if(!dbWizard.isExistTable(tableName,false))
			{
		    	Table table = new Table(tableName);				
			    table.addField(getField("id", "I", 10, true));
			    table.addField(getField("p0100", "I", 10, false));				
			    table.addField(getField("B0110", "A", 30, false));				
			    table.addField(getField("E0122", "A", 30, false));				
			    table.addField(getField("E01A1", "A", 30, false));				
			    table.addField(getField("NBASE", "A", 3, false));				
			    table.addField(getField("A0100", "A", 30, false));				
			    table.addField(getField("A0101", "A", 30, false));				
			    table.addField(getField("sp_grade", "I", 4, false));				
			    table.addField(getField("Description", "M", 10, false));				
			    table.addField(getField("pg_code", "A", 30, false));				
			    table.addField(getField("sp_date", "D", 10, false));
			    dbWizard.createTable(table);
			}
		  
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 新建指标计算公式临时表字段
	 */
	public Field getField(String fieldname, String a_type, int length, boolean key)
    {
		Field obj = new Field(fieldname, fieldname);
		if ("A".equals(a_type))
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		} else if ("M".equals(a_type))
		{
		    obj.setDatatype(DataType.CLOB);
		} else if ("I".equals(a_type))
		{
		    obj.setDatatype(DataType.INT);
		    obj.setLength(length);
		} else if ("F".equals(a_type))
		{
		    obj.setDatatype(DataType.FLOAT);
		    obj.setLength(length);
		    obj.setDecimalDigits(5);
		} else if ("D".equals(a_type))
		{
		    obj.setDatatype(DataType.DATE);
		} else
		{
		    obj.setDatatype(DataType.STRING);
		    obj.setLength(length);
		}
		if(key) {
            obj.setNullable(false);
        }
		obj.setKeyable(key);	
		return obj;
    }
	
}
