package com.hjsj.hrms.businessobject.performance.workplanteam;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * WorkPlanTeamBo.java
 * Description: 
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Aug 3, 2012 3:12:45 PM Jianghe created
 */
public class WorkPlanTeamBo {
	
	private Connection con;
	private UserView userView;
	public static HashMap workParametersMap=new HashMap();//参数采用静态变量，不用每次都查。
	private String workType;//=1个人年工作计划总结。=2团队年工作计划总结=3个人季度工作计划总结=4团队季度工作计划总结=5个人月工作计划总结=6团队月工作计划总结
	private String state;//=0 日报    =1 周报    =2 月报     =3 季报     =4 年报
	//判断用哪个审批关系表
	String Relation_id = null;
	//库名
	String nbase = "Usr";
	String a0100 = "";
	WeekUtils weekutil = new WeekUtils();
	public WorkPlanTeamBo()
	{
		
	}
	public WorkPlanTeamBo(UserView userView,Connection con)
	{
		this.userView=userView;
		this.con=con;
		a0100 = userView.getA0100();
	}
	public WorkPlanTeamBo(Connection con,UserView userView,String workType,String state)
	{
		this.con=con;
		this.userView=userView;
		this.workType=workType;
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
            String constant = "constant";
            if(Sql_switcher.searchDbServer()==Constant.KUNLUN)
            		constant = "\"constant\"";
			rs = dao.search("select str_value from "+constant+" where "+constant+"='PER_PARAMETERS'");
		    if ( rs.next())
		    {
		    	String str_value = Sql_switcher.readMemo(rs, "str_value");
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
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
	public String isNullToZero(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str))
		    str = "0";
		return str;
    }
	/**
	 * Description: 取得所查询的年列表
	 * @Version1.0 
	 * Aug 3, 2012 3:13:47 PM Jianghe created
	 * @param flag addAll 添加全部 null 不添加 
	 * @param state 0：日 1：周 2：月 3：季度 4：年
	 * @return
	 */
	public ArrayList getYearList(String flag,String state,String a_code)
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		Relation_id = (String)workParametersMap.get("sp_relation");
		try{
		    StringBuffer buf = new StringBuffer("");
		    buf.append(" select distinct "+Sql_switcher.year("p.p0104")+" as ayear from p01 p ");
		    buf.append(" where 1=1 ");
		    if(state!=null&&!"".equals(state)){
		    	buf.append(" and p.state="+state);
		    }
		    buf.append(" and( ");
		    
		    buf.append(" exists ( "); 
		    if(Relation_id!=null && !"null".equals(Relation_id)){
			    buf.append(" select null from t_wf_mainbody twm where twm.mainbody_id='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' and twm.Relation_id" +
		    	 		"='"+Relation_id+"' and p.a0100="+Sql_switcher.substr("twm.object_id", "4", "11")+" and lower(p.nbase)=lower("+Sql_switcher.substr("twm.object_id", "1", "3")+")  ");
		    }else{
				//如果是考核关系表
		    	buf.append(" select null from per_mainbody_std pms where pms.mainbody_id='"+this.userView.getA0100()+"' and p.a0100=pms.object_id ");
			}
		    buf.append(" ) ");
		    buf.append("or ( p.curr_user='"+this.userView.getUserName()+"' ) ");
			buf.append(" or exists ( ");
			    buf.append(" select null from per_diary_actor pda where p.p0100=pda.p0100  and lower(p.nbase)=lower(pda.nbase) ");
			    if(this.userView.getA0100()==null || this.userView.getA0100().trim().length()<=0){
			    	buf.append(" and pda.a0100='' ");
			    }else{
			    	buf.append(" and pda.a0100="+userView.getA0100());
			    }
			buf.append(" ) ");
	            //权限范围，flag 0 写or 1写and
			//权限范围
				if("".equals(this.getUserViewPersonWhere(userView))){
					buf.append(" or 1=1 ");
				}else{
					buf.append(" or ( "+this.getUserViewPersonWhere(userView).substring(4)+" ) ");
				}
			    //树链接范围
			    buf.append(this.getAcodePersonWhere(a_code));
			    //人员库范围
			    buf.append(this.getNbaseFilter(userView));
		    buf.append(" ) ");
		    buf.append(" order by "+Sql_switcher.year("p.p0104")+" desc");
		    ContentDAO dao = new ContentDAO(this.con);
		    //System.out.println(buf.toString());
		    rs=dao.search(buf.toString());
		    boolean isAddThisYear=true; 
		    Calendar d = Calendar.getInstance();
		    String thisYear=Calendar.getInstance().get(Calendar.YEAR)+"";
		    String nextYear="";
		    if(flag!=null){
		    	//全部
	    		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
			}
		    list.add(new CommonData(thisYear,thisYear));
		    String prior_end11 = isNullToZero((String)WorkPlanTeamBo.workParametersMap.get("prior_end11")); 
		    String prior_end12 = isNullToZero((String)WorkPlanTeamBo.workParametersMap.get("prior_end12")); 
		    String prior_end13 = isNullToZero((String)WorkPlanTeamBo.workParametersMap.get("prior_end13")); 
		    String prior_end14 = isNullToZero((String)WorkPlanTeamBo.workParametersMap.get("prior_end14")); 
		    //2015/12/29 wangjl 当一年剩余天数少于填写计划时年列表中增加一年
		    if((d.getActualMaximum(Calendar.DAY_OF_YEAR)-d.get(Calendar.DAY_OF_YEAR))<=Integer.parseInt(prior_end11)||
	    		(d.getActualMaximum(Calendar.DAY_OF_YEAR)-d.get(Calendar.DAY_OF_YEAR))<=Integer.parseInt(prior_end12)||
	    		(d.getActualMaximum(Calendar.DAY_OF_YEAR)-d.get(Calendar.DAY_OF_YEAR))<=Integer.parseInt(prior_end13)||
	    		(d.getActualMaximum(Calendar.DAY_OF_YEAR)-d.get(Calendar.DAY_OF_YEAR))<=Integer.parseInt(prior_end14))
		    {	
		    	nextYear=Calendar.getInstance().get(Calendar.YEAR)+1+"";
		    	list.add(new CommonData(nextYear,nextYear));
		    }
		    
//		    if(flag!=null){
//		    	//全部
//	    		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
//			}
//		    int n=0;
		    while(rs.next())
		    {
//		    	n++;
//		    	if(ayear!=null && ayear.trim().length()>0 && ayear.equalsIgnoreCase(thisYear))
//		    		isAddThisYear=false;
		    	String ayear=rs.getString("ayear");
		    	if(StringUtils.isNotEmpty(ayear)&&!ayear.equals(thisYear)&&!ayear.equals(nextYear)){
		    		list.add(new CommonData(ayear,ayear));
		    	}
		    }
//		    if(n==0)
//		    	list.add(new CommonData(Calendar.getInstance().get(Calendar.YEAR)+"",Calendar.getInstance().get(Calendar.YEAR)+""));
//		    
//		    if(isAddThisYear)
//		    	list.add(new CommonData(thisYear,thisYear));
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * Description: 季度列表
	 * @Version1.0 
	 * Aug 3, 2012 3:18:09 PM Jianghe created
	 * @param flag addAll 添加全部 null 不添加 
	 * @return
	 */
	public static ArrayList getSeasonList(String flag){
		ArrayList list = new ArrayList();
		if(flag!=null){
    		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
		}
		list.add(new CommonData("1",ResourceFactory.getProperty("log.teamwork.workplan.oneQuarter")));//第1季度
		list.add(new CommonData("2",ResourceFactory.getProperty("log.teamwork.workplan.twoQuarter")));
		list.add(new CommonData("3",ResourceFactory.getProperty("log.teamwork.workplan.threeQuarter")));
		list.add(new CommonData("4",ResourceFactory.getProperty("log.teamwork.workplan.fourQuarter")));
		return list;
	}
	/**
	 * Description: 获取月列表
	 * @Version1.0 
	 * Aug 3, 2012 3:16:31 PM Jianghe created
	 * @param flag  addAll 添加全部 null 不添加 
	 * @return
	 */
	public static ArrayList getMonthList(String flag){
		ArrayList list = new ArrayList();
		if(flag!=null){
    		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
		}
		list.add(new CommonData("1",ResourceFactory.getProperty("performance.workplan.workplanteam.january")));
		list.add(new CommonData("2",ResourceFactory.getProperty("performance.workplan.workplanteam.february")));
		list.add(new CommonData("3",ResourceFactory.getProperty("performance.workplan.workplanteam.march")));
		list.add(new CommonData("4",ResourceFactory.getProperty("performance.workplan.workplanteam.april")));
		list.add(new CommonData("5",ResourceFactory.getProperty("performance.workplan.workplanteam.may")));
		list.add(new CommonData("6",ResourceFactory.getProperty("performance.workplan.workplanteam.june")));
		list.add(new CommonData("7",ResourceFactory.getProperty("performance.workplan.workplanteam.july")));
		list.add(new CommonData("8",ResourceFactory.getProperty("performance.workplan.workplanteam.august")));
		list.add(new CommonData("9",ResourceFactory.getProperty("performance.workplan.workplanteam.september")));
		list.add(new CommonData("10",ResourceFactory.getProperty("performance.workplan.workplanteam.october")));
		list.add(new CommonData("11",ResourceFactory.getProperty("performance.workplan.workplanteam.november")));
		list.add(new CommonData("12",ResourceFactory.getProperty("performance.workplan.workplanteam.december")));
		return list;
	}
	/**
	 * Description: 周列表
	 * @Version1.0 
	 * Aug 3, 2012 3:18:33 PM Jianghe created
	 * @param year  
	 * @param month
	 * @param flag addAll 添加全部 null 不添加 
	 * @return
	 */
	public static ArrayList getWeekList(String year,String month,String flag){
		ArrayList list = new ArrayList();
		WeekUtils weekutils = new WeekUtils();
		int count;
		if("all".equals(month)){
			count = weekutils.totalWeek(Integer.parseInt(year),1);
		}else{
			count = weekutils.totalWeek(Integer.parseInt(year),Integer.parseInt(month));
		}
		
		if(flag!=null){
    		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
		}
		for (int i = 1; i <= count; i++) {
			list.add(new CommonData(String.valueOf(i),ResourceFactory.getProperty("hmuster.label.d")+i+ResourceFactory.getProperty("kq.wizard.week")));
		}
		return list;
	}
	/**
	 * Description: 日列表
	 * @Version1.0 
	 * Aug 3, 2012 3:19:49 PM Jianghe created
	 * @param year  
	 * @param month
	 * @param flag  addAll 添加全部 null 不添加 
	 * @return
	 */
	public static ArrayList getDayList(String year,String month,String flag){
		ArrayList list = new ArrayList();
	
		Date mDate = null; 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		try
		{ 
			if(month!=null&&!"".equals(month)&&!"all".equals(month)){
				mDate = sdf.parse(year+"-"+month+"-"+"01"); 
			}else{
				month="1";
				mDate = sdf.parse(year+"-"+month+"-"+"01"); 
			}
			
		}catch(ParseException pe){
			pe.printStackTrace();
		} 
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(mDate);
		cal.add(Calendar.MONTH,1);   
		cal.add(Calendar.DAY_OF_MONTH,-1);
		int count = cal.get(Calendar.DATE);	
		if(flag!=null){
    		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
		}
		for (int i = 1; i <= count; i++) {
			list.add(new CommonData(String.valueOf(i),""+i+ResourceFactory.getProperty("label.sys.warn.freq.everymonth.day")));
		}
		return list;
	}
	/**
	 * Description: 状态列表
	 * @Version1.0 
	 * Aug 3, 2012 3:16:55 PM Jianghe created
	 * @return
	 */
	public static ArrayList getStatusList(){
		ArrayList list = new ArrayList();
		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
		list.add(new CommonData("31",ResourceFactory.getProperty("log.teamwork.workplan.dp")));//待批
		list.add(new CommonData("30",ResourceFactory.getProperty("edit_report.status.wt")));//未填
		list.add(new CommonData("01",ResourceFactory.getProperty("hire.jp.pos.draftout")));//起草
		list.add(new CommonData("02",ResourceFactory.getProperty("workdiary.message.apped")));//已报批
		list.add(new CommonData("03",ResourceFactory.getProperty("label.hiremanage.status3")));//已批
		list.add(new CommonData("07",ResourceFactory.getProperty("edit_report.status.dh")));//驳回
		return list;
	}
	/**
	 * Description: 日志类型列表
	 * @Version1.0 
	 * Aug 16, 2012 1:35:11 PM Jianghe created
	 * @return
	 */
	public static ArrayList getLogtypeList(){
		ArrayList list = new ArrayList();
		list.add(new CommonData("all",ResourceFactory.getProperty("label.all")));
		list.add(new CommonData("1",ResourceFactory.getProperty("performance.workdiary.workplan")));//工作计划
		list.add(new CommonData("2",ResourceFactory.getProperty("performance.workdiary.worksummary")));//工作总结
		return list;
	}
	/**
	 * Description: 查询的where条件子句
	 * @Version1.0 
	 * Aug 3, 2012 3:26:29 PM Jianghe created
	 * @param workType //=1个人年工作计划总结。=2团队年工作计划总结=3个人季度工作计划总结=4团队季度工作计划总结=5个人月工作计划总结=6团队月工作计划总结
	 * @param year
	 * @param season
	 * @param month
	 * @param week
	 * @param day
	 * @param status 状态 "all","全部"
						("31","待批"));
						("30","未填"));
						("01","起草"));
						("02","报批"));
						("03","已批"));
						("07","驳回"));
	 * @param name
	 * @param a_code 单位编码(从树结构传过来的)
	 * @param state =0 日报    =1 周报    =2 月报     =3 季报     =4 年报
	 * @param 日志类型 =1 工作计划  2 工作总结  "all" 全部
	 * @return
	 */
	public String getStr_whl(String workType,String year,String season,String month,String week,String day,String status,String name,String a_code,String state,String log_type){
		Relation_id = (String)workParametersMap.get("sp_relation");
		StringBuffer buf = new StringBuffer("");
		buf.append("where 1=1");
		 buf.append(this.getAcodePersonWhere(a_code));
		//人员库范围
		    buf.append(this.getNbaseFilter(userView));
		//查询条件过滤	
		buf.append(" and p.state="+state);
		//过滤时间
		buf.append(this.getDateFilter(workType,year,season,month,week,day,log_type));
		//日志类型过滤
	    if(log_type!=null&&!"".equals(log_type)&&!"all".equals(log_type)){
	    	buf.append(" and p.log_type="+log_type);
	    }
	    if(status!=null&&!"".equals(status)&&!"all".equals(status)){
	    	buf.append(" and p.p0115="+status);
	    }
	    if (name!=null&&!"".equalsIgnoreCase(name.trim())){
	    	
//	    	buf.append(" and p.a0101 like '%" + name + "%' ");
	    	//2016/1/20 wangjl 全总将只能按姓名查询改成可以模糊查询姓名，领导批示，工作内容等
	    	buf.append(" and ( p.a0101 like '%" + name + "%' ");
		    buf.append(" or  exists (select 1 from per_diary_opinion pdd where p.p0100=pdd.p0100 and lower(p.nbase)=lower(pdd.nbase)  and Description like '%"+ name +"%')");
		    buf.append("or  exists (select 1 from per_diary_content pdc where  p.p0100=pdc.p0100 and lower(p.nbase)=lower(pdc.nbase)  and  Content like '%"+ name +"%' ))");
	    }
	    buf.append(" and( exists ( "); 
	    if(!"null".equals(Relation_id)){
		    buf.append(" select null from t_wf_mainbody twm where twm.mainbody_id='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' and twm.Relation_id" +
	    	 		"='"+Relation_id+"' and p.a0100="+Sql_switcher.substr("twm.object_id", "4", "11")+" and lower(p.nbase)=lower("+Sql_switcher.substr("twm.object_id", "1", "3")+")  ");   
	    }else{
			//如果是考核关系表
	    	buf.append(" select null from per_mainbody_std pms where pms.mainbody_id='"+this.userView.getA0100()+"' and p.a0100=pms.object_id ");
		}
	    buf.append(" ) ");
	    buf.append("or ( p.curr_user='"+this.userView.getUserName()+"' ) ");
	    //审批表
	    buf.append(" or exists ( ");
	    buf.append(" select null from per_diary_opinion pdo where p.p0100=pdo.p0100 and lower(p.nbase)=lower(pdo.nbase) and pdo.a0100=");
	    //考虑自助用户
		if(this.userView.getA0100()==null || this.userView.getA0100().trim().length()<=0){
	    	buf.append("null");
	    }else{
	    	buf.append("'"+userView.getA0100()+"'");
	    }
	    buf.append(" ) ");
		buf.append(" or exists ( ");
		//抄送表
		buf.append(" select null from per_diary_actor pda where p.p0100=pda.p0100  and lower(p.nbase)=lower(pda.nbase) and pda.a0100=");
		//考虑自助用户
		if(this.userView.getA0100()==null || this.userView.getA0100().trim().length()<=0){
	    	buf.append("null");
	    }else{
	    	buf.append("'"+userView.getA0100()+"'");
	    }
		buf.append(" ) ");
		
		//权限范围
		if("".equals(this.getUserViewPersonWhere(userView))){
			buf.append(" or 1=1 ");
		}else{
			buf.append(" or ( "+this.getUserViewPersonWhere(userView).substring(4)+" ) ");
		}
	    buf.append(" ) ");
	    return buf.toString();	
	}
	/**
	 * Description: 获取可审批记录MAP
	 * @Version1.0 
	 * Aug 3, 2012 3:31:26 PM Jianghe created
	 * @param workType
	 * @param year
	 * @param season
	 * @param month
	 * @param week
	 * @param day
	 * @param name
	 * @param a_code
	 * @param state
	 * @return
	 */
	public HashMap getSpMap(String workType,String year,String season,String month,String week,String day,String name,String a_code,String state,String log_type){
		Relation_id = (String)workParametersMap.get("sp_relation");
		HashMap spMap = new HashMap();
		RowSet rs = null;
		StringBuffer buf = new StringBuffer("");
		buf.append(" select * from p01 p where 1=1 ");
		//查询条件过滤	
		buf.append(this.getDateFilter(workType, year, season, month, week, day,log_type));
	    buf.append(" and (p.p0115='02' or (p.p0115='07' and curr_user is not null))");
	    
	    if (name!=null&&!"".equalsIgnoreCase(name.trim())){
	    	buf.append(" and p.a0101 like '%" + name + "%' ");
	    	buf.append("or  exists (select 1 from per_diary_content pdc where  p.p0100=pdc.p0100 and lower(p.nbase)=lower(pdc.nbase) and  Content like '%"+ name +"%' )");
	    }
	    if(state!=null&&!"".equals(state)){
	    	buf.append(" and p.state="+state);
	    }
	    //日志类型过滤
	    if(log_type!=null&&!"".equals(log_type)&&!"all".equals(log_type)){
	    	buf.append(" and p.log_type="+log_type);
	    }
	    buf.append(this.getAcodePersonWhere(a_code));
	    //人员库范围
	    buf.append(this.getNbaseFilter(userView));
	    buf.append(" and( ");
	    buf.append(" p.curr_user='"+this.userView.getUserName()+"' ");
	      buf.append(" or( ");
	      buf.append(" p.curr_user is null ");
	         if("".equals(this.getUserViewPersonWhere(userView)))
	         {
	        	 buf.append(" and 1=1 ");
	         }else{
	        	buf.append(this.getUserViewPersonWhere(userView)); 
	         }
	      buf.append(" ) ");
	      buf.append(" or( ");
	         buf.append(" p.curr_user is null and exists( ");
	         if(!"null".equals(Relation_id)){
				    buf.append(" select null from t_wf_mainbody twm where twm.mainbody_id='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"'and twm.Relation_id" +
			    	 		"='"+Relation_id+"' and p.a0100="+Sql_switcher.substr("twm.object_id", "4", "11")+" and lower(p.nbase)=lower("+Sql_switcher.substr("twm.object_id", "1", "3")+")  ");   
			    }else{
			    	//如果是考核关系
			    	buf.append(" select null from per_mainbody_std pms where pms.mainbody_id='"+this.userView.getA0100()+"' and p.a0100=pms.object_id ");
				} 
	         buf.append(" ) ");
	      buf.append(" ) ");
	    buf.append(" ) ");
	    buf.append(" order by "+Sql_switcher.year("p.p0104")+" desc");
	    try{	
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search(buf.toString());
			while(rs.next()){
				if(((String)spMap.get(rs.getString("p0100")))!=null){
					continue;
				}
				spMap.put(rs.getString("p0100"), "1");  
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return spMap;
	}
	/**
	 * Description: 待批的sql where子句
	 * @Version1.0 
	 * Aug 3, 2012 3:32:31 PM Jianghe created
	 * @param workType
	 * @param year
	 * @param season
	 * @param month
	 * @param week
	 * @param day
	 * @param name
	 * @param a_code
	 * @param state
	 * @return
	 */
	public String getStr_whl_SP(String workType,String year,String season,String month,String week,String day,String name,String a_code,String state,String log_type){
		StringBuffer buf = new StringBuffer("");
		buf.append(" where 1=1 ");
		buf.append(this.getDateFilter(workType, year, season, month, week, day,log_type));
	    buf.append(" and (p.p0115='02' or (p.p0115='07' and curr_user is not null))");
	    
	    if (name!=null&&!"".equalsIgnoreCase(name.trim())){
	    	buf.append(" and ( p.a0101 like '%" + name + "%' ");
	    	//2016/1/20 wangjl 全总将只能按姓名查询改成可以模糊查询姓名，领导批示，工作内容等
		    buf.append("or  exists (select 1 from per_diary_content pdc where  p.p0100=pdc.p0100 and lower(p.nbase)=lower(pdc.nbase) and  Content like '%"+ name +"%' ))");
	    }
	    if(state!=null&&!"".equals(state)){
	    	buf.append(" and p.state="+state);
	    }
	    //日志类型过滤
	    if(log_type!=null&&!"".equals(log_type)&&!"all".equals(log_type)){
	    	buf.append(" and p.log_type="+log_type);
	    }
	    //人员库范围
	    buf.append(this.getNbaseFilter(userView));
	    buf.append(this.getAcodePersonWhere(a_code));
	    buf.append(" and( ");
	    buf.append(" p.curr_user='"+this.userView.getUserName()+"' ");
	      buf.append(" or( ");
	         buf.append(" p.curr_user is null ");
	         if("".equals(this.getUserViewPersonWhere(userView)))
	         {
	        	 buf.append(" and 1=1 ");
	         }else{
	        	buf.append(this.getUserViewPersonWhere(userView)); 
	         }
	      buf.append(" ) ");
	      buf.append(" or( ");
	         buf.append(" p.curr_user is null and exists( ");
	         if(!"null".equals(Relation_id)){
				    buf.append(" select null from t_wf_mainbody twm where twm.mainbody_id='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"'and twm.Relation_id" +
			    	 		"='"+Relation_id+"' and p.a0100="+Sql_switcher.substr("twm.object_id", "4", "11")+" and lower(p.nbase)=lower("+Sql_switcher.substr("twm.object_id", "1", "3")+")  ");   
			    }else{
			    	//如果是考核关系
			    	buf.append(" select null from per_mainbody_std pms where pms.mainbody_id='"+this.userView.getA0100()+"' and p.a0100=pms.object_id ");
				} 
	         buf.append(" ) ");
	      buf.append(" ) ");
	    buf.append(" ) ");
	    return buf.toString();
	}
	/**
	 * Description: 没有填写记录的列表
	 * @Version1.0 
	 * Aug 3, 2012 3:33:46 PM Jianghe created
	 * @param year
	 * @param season
	 * @param month
	 * @param week
	 * @param day
	 * @param state
	 * @param a_code
	 * @return
	 */
	public ArrayList getNoFillList(String year,String season,String month,String week,String day,String name,String state,String a_code,String log_type){
		ArrayList list = new ArrayList();
		nbase = (String)workParametersMap.get("nbase");
		RowSet rs = null;
		int count_managePer = 0;
		ArrayList dblist = userView.getPrivDbList();
		ArrayList from_base = new ArrayList();
		//自己管理范围内的人数 count_managePer
		if(nbase!=null){
			String[] str_array = nbase.split(",");
			for (int i = 0; i < str_array.length; i++) {
				if(dblist.contains(str_array[i])){
					from_base.add(str_array[i]);
				}
			}
			for (int i = 0; i < from_base.size(); i++) {
				StringBuffer buf_managePer = new StringBuffer(" select count(*) from "+from_base.get(i)+"a01 a");
				buf_managePer.append(" where 1=1 ");
				buf_managePer.append(this.getUserViewPersonWhere(userView));
				buf_managePer.append(this.getAcodePersonWhere(a_code));
				try{	
					ContentDAO dao = new ContentDAO(this.con);
					rs = dao.search(buf_managePer.toString());
					if(rs.next()){
						count_managePer +=rs.getInt(1);
					}	
				}catch(Exception e)
				{
					e.printStackTrace();
				}finally{
					try{
						if(rs!=null)
							rs.close();
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
		    }
		}
		//起始时间 格式20110812
		String startTime = this.getStartTime(year,season,month,week,day,state,a_code,log_type);
		//统计自己管理范围已填的人数 count,log_type,p0104
		//ArrayList filledList = getFilledList(year, season, month, week, day, name, state, a_code,log_type);
		//System.out.println("已填人数："+filledList.size());
		//修改的代码
		//工作计划未填写的人列表 a0100,a0101,nbase
		ArrayList hnfList1 = gethaveNotFilledList(workType,year,season,month,week,day,name,a_code,state,"1");
		//工作总结未填写的人列表 a0100,a0101,nbase
		ArrayList hnfList2 = gethaveNotFilledList(workType,year,season,month,week,day,name,a_code,state,"2");
		//System.out.println("工作计划未填写的人数："+hnfList1.size());
		//System.out.println("工作总结未填写的人数："+hnfList2.size());
		//插数据
		//年团队
		if(state!=null&&"4".equals(state)){
			//插入工作计划数据
			if("1".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(year)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList yearlist = this.getYearList(null,state, a_code);
					for (int j = 0; j < yearlist.size(); j++) {
						CommonData cd = (CommonData)yearlist.get(j);
						  String begin_time = format.format(this.getStartDateAndEndDate(cd.getDataValue(), season, month, week, day, 1));
						  
							  for (int i = 0; i < hnfList1.size(); i++){
									LazyDynaBean abean = new LazyDynaBean();
									LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
									abean.set("year", cd.getDataValue());
									abean.set("log_type", "1");
									abean.set("name", hbean.get("a0101"));
									abean.set("begin_time", begin_time);
									abean.set("end_time", format.format(this.getStartDateAndEndDate(cd.getDataValue(), season, month, week, day, 2)));
									abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
									abean.set("nbase", hbean.get("nbase"));
									abean.set("a0100", hbean.get("a0100"));
									abean.set("p0100", "");
									abean.set("state", state);
									abean.set("p0115", "01");
									abean.set("ayear", cd.getDataValue());
									abean.set("aquarter", season);
									abean.set("amonth", month);
									abean.set("aweek", week);
									abean.set("aday", day);
									abean.set("sp_flag", this.isLessThanCurrent(begin_time));
									
									String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
									String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
									String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
									String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
									abean.set("mdnofillopt", mdnofillopt);
									abean.set("mdnofillnbase", mdnofillnbase);
									abean.set("mdnofilla0100", mdnofilla0100);
									abean.set("mdnofillp0100", mdnofillp0100);
									
									list.add(abean);
							  }
						  
					}
				}else{
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1));
					
						for (int i = 0; i < hnfList1.size(); i++){
							LazyDynaBean abean = new LazyDynaBean();
							LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
							abean.set("year", year);
							abean.set("log_type", "1");
							abean.set("name", hbean.get("a0101"));
							abean.set("begin_time", begin_time);
							abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
							abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
							abean.set("nbase", hbean.get("nbase"));
							abean.set("a0100", hbean.get("a0100"));
							abean.set("p0100", "");
							abean.set("state", state);
							abean.set("p0115", "01");
							abean.set("ayear", year);
							abean.set("aquarter", season);
							abean.set("amonth", month);
							abean.set("aweek", week);
							abean.set("aday", day);
							abean.set("sp_flag", this.isLessThanCurrent(begin_time));
							String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
							String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
							String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
							String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
							abean.set("mdnofillopt", mdnofillopt);
							abean.set("mdnofillnbase", mdnofillnbase);
							abean.set("mdnofilla0100", mdnofilla0100);
							abean.set("mdnofillp0100", mdnofillp0100);
							list.add(abean);
						}
						
				}
			}
			//插入工作总结数据
			if("2".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(year)){
					ArrayList yearlist = this.getYearList(null,state, a_code);
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					for (int j = 0; j < yearlist.size(); j++) {
						CommonData cd = (CommonData)yearlist.get(j);
						String begin_time = format.format(this.getStartDateAndEndDate(cd.getDataValue(), season, month, week, day, 1));
						
							for (int i = 0; i < hnfList2.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
								abean.set("year", cd.getDataValue());
								abean.set("log_type", "2");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(cd.getDataValue(), season, month, week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", cd.getDataValue());
								abean.set("aquarter", season);
								abean.set("amonth", month);
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
						  }
						
					}
				}else{
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1));
					
						for (int i = 0; i < hnfList2.size(); i++){
							LazyDynaBean abean = new LazyDynaBean();
							LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
							abean.set("year", year);
							abean.set("log_type", "2");
							abean.set("name", hbean.get("a0101"));
							abean.set("begin_time", begin_time);
							abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
							abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
							abean.set("nbase", hbean.get("nbase"));
							abean.set("a0100", hbean.get("a0100"));
							abean.set("p0100", "");
							abean.set("state", state);
							abean.set("p0115", "01");
							abean.set("ayear", year);
							abean.set("aquarter", season);
							abean.set("amonth", month);
							abean.set("aweek", week);
							abean.set("aday", day);
							abean.set("sp_flag", this.isLessThanCurrent(begin_time));
							String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
							String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
							String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
							String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
							abean.set("mdnofillopt", mdnofillopt);
							abean.set("mdnofillnbase", mdnofillnbase);
							abean.set("mdnofilla0100", mdnofilla0100);
							abean.set("mdnofillp0100", mdnofillp0100);
							list.add(abean);
						}
					
				}
			}
		}
		//季报
		if(state!=null&&"3".equals(state)){
			//插入工作计划数据
			// 季计划
		      String valid13="0";
		      if(workParametersMap.get("valid13")!=null)
	  		     valid13 = (String)workParametersMap.get("valid13");
	 		  String period13="";
	 		  if(workParametersMap.get("period13")!=null)   		      	
	  	         period13 = (String)workParametersMap.get("period13");
	  		  // 季总结
	  		  String valid23="0";
	  		  if(workParametersMap.get("valid23")!=null)
	  		     valid23 = (String)workParametersMap.get("valid23");	
	  		  String period23="";	
	  		  if(workParametersMap.get("period23")!=null)
	  		     period23 = (String)workParametersMap.get("period23");
	  		  String t_period13 = ","+period13+",";
		      String t_period23 = ","+period23+",";
		      
			if("1".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(season)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList seasonlist = this.getSeasonList(null);
					for (int j = 0; j < seasonlist.size(); j++) {
						CommonData cd = (CommonData)seasonlist.get(j);
						String str = ","+cd.getDataValue()+",";
						if("1".equals(valid13)&&t_period13.indexOf(str)==-1){
							continue;
						}
						String begin_time = format.format(this.getStartDateAndEndDate(year, cd.getDataValue(), month, week, day, 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList1.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
								abean.set("year", year);
								abean.set("season", cd.getDataValue());
								abean.set("log_type", "1");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, cd.getDataValue(), month, week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", cd.getDataValue());
								abean.set("amonth", month);
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
					}
				}else{
					String str = ","+season+",";
					if("1".equals(valid13)&&t_period13.indexOf(str)!=-1){
						SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList1.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
								abean.set("year", year);
								abean.set("season", season);
								abean.set("log_type", "1");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", month);
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
					}	
				}
			}
			//插入工作总结数据
			if("2".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(season)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList seasonlist = this.getSeasonList(null);
					for (int j = 0; j < seasonlist.size(); j++) {
						CommonData cd = (CommonData)seasonlist.get(j);
						String str = ","+cd.getDataValue()+",";
						if("1".equals(valid23)&&t_period23.indexOf(str)==-1){
							continue;
						}
						String begin_time = format.format(this.getStartDateAndEndDate(year, cd.getDataValue(), month, week, day, 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList2.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
								abean.set("year", year);
								abean.set("season", cd.getDataValue());
								abean.set("log_type", "2");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, cd.getDataValue(), month, week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", cd.getDataValue());
								abean.set("amonth", month);
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}else{
					String str = ","+season+",";
					if("1".equals(valid23)&&t_period23.indexOf(str)!=-1){
						SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1)) ;
	                    if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
	                    	for (int i = 0; i < hnfList2.size(); i++){
	    						LazyDynaBean abean = new LazyDynaBean();
	    						LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
	    						abean.set("year", year);
	    						abean.set("season", season);
	    						abean.set("log_type", "2");
	    						abean.set("name", hbean.get("a0101"));
	    						abean.set("begin_time",begin_time);
	    						abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
	    						abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
	    						abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", month);
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
	    						list.add(abean);
	    					}
						}
					}
					
				}
			}
		}
		//月报
		if(state!=null&&"2".equals(state)){
			//计划参数
			String valid12="0";
			if(workParametersMap.get("valid12")!=null)
				valid12=(String)workParametersMap.get("valid12");
			String period12="";
			if(workParametersMap.get("period12")!=null)
				period12=(String)workParametersMap.get("period12");
			//总结参数
			String valid22="0";
			if(workParametersMap.get("valid22")!=null)
				valid22=(String)workParametersMap.get("valid22");
            String period22="";
            if(workParametersMap.get("period22")!=null)
            	period22=(String)workParametersMap.get("period22");
			String t_period12=","+period12+",";
	    	String t_period22=","+period22+",";
			//插入工作计划数据
			if("1".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(month)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						CommonData cd = (CommonData)monthlist.get(j);
						String str = ","+cd.getDataValue()+",";
						if("1".equals(valid12)&&t_period12.indexOf(str)==-1){
							continue;
						}
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 1)) ;
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList1.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
								abean.set("year", year);
								abean.set("month", cd.getDataValue());
								abean.set("log_type", "1");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time",begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", cd.getDataValue());
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}else{
					String str = ","+month+",";
					if("1".equals(valid12)&&t_period12.indexOf(str)!=-1){
						SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1));
	                    if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
	                    	for (int i = 0; i < hnfList1.size(); i++){
	    						LazyDynaBean abean = new LazyDynaBean();
	    						LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
	    						abean.set("year", year);
	    						abean.set("month", month);
	    						abean.set("log_type", "1");
	    						abean.set("name", hbean.get("a0101"));
	    						abean.set("begin_time", begin_time);
	    						abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
	    						abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
	    						abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", month);
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
	    						list.add(abean);
	    					}
						}
					}
				}
			}
			//插入工作总结数据
			if("2".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(month)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						CommonData cd = (CommonData)monthlist.get(j);
						String str = ","+cd.getDataValue()+",";
						if("1".equals(valid22)&&t_period22.indexOf(str)==-1){
							continue;
						}
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 1)) ;
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList2.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
								abean.set("year", year);
								abean.set("month", cd.getDataValue());
								abean.set("log_type", "2");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time",begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", cd.getDataValue());
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}else{
					String str = ","+month+",";
					if("1".equals(valid22)&&t_period22.indexOf(str)!=-1){
						SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1));
	                    if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
	                    	for (int i = 0; i < hnfList2.size(); i++){
	    						LazyDynaBean abean = new LazyDynaBean();
	    						LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
	    						abean.set("year", year);
	    						abean.set("month", month);
	    						abean.set("log_type", "2");
	    						abean.set("name", hbean.get("a0101"));
	    						abean.set("begin_time", begin_time);
	    						abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
	    						abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
	    						abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", month);
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
	    						list.add(abean);
	    					}
						}
					}
				}
			}
		}
		//周报
		if(state!=null&&"1".equals(state)){
			//插入工作计划数据
			if("1".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(month)&& "all".equals(week)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						//每月有多少周
						CommonData cdMonth = (CommonData)monthlist.get(j);
						ArrayList weeklist = this.getWeekList(year, month, null);
						for (int k = 0; k < weeklist.size(); k++) {
							CommonData cdWeek = (CommonData)monthlist.get(k);
							String begin_time = format.format(this.getStartDateAndEndDate(year, season, cdMonth.getDataValue(), cdWeek.getDataValue(), day, 1));
							if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
								for (int i = 0; i < hnfList1.size(); i++){
									LazyDynaBean abean = new LazyDynaBean();
									LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
									abean.set("year", year);
									abean.set("month", cdMonth.getDataValue());
									abean.set("week", cdWeek.getDataValue());
									abean.set("log_type", "1");
									abean.set("name", hbean.get("a0101"));
									abean.set("begin_time", begin_time);
									abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cdMonth.getDataValue(), cdWeek.getDataValue(), day, 2)));
									abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
									abean.set("nbase", hbean.get("nbase"));
									abean.set("a0100", hbean.get("a0100"));
									abean.set("p0100", "");
									abean.set("state", state);
									abean.set("p0115", "01");
									abean.set("ayear", year);
									abean.set("aquarter", season);
									abean.set("amonth", cdMonth.getDataValue());
									abean.set("aweek", cdWeek.getDataValue());
									abean.set("aday", day);
									abean.set("sp_flag", this.isLessThanCurrent(begin_time));
									String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
									String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
									String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
									String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
									abean.set("mdnofillopt", mdnofillopt);
									abean.set("mdnofillnbase", mdnofillnbase);
									abean.set("mdnofilla0100", mdnofilla0100);
									abean.set("mdnofillp0100", mdnofillp0100);
									list.add(abean);
								}
							}
							
						}	
					}
				}
				else if("all".equals(month)&&!"all".equals(week)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						CommonData cd = (CommonData)monthlist.get(j);
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList1.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
								abean.set("year", year);
								abean.set("month", cd.getDataValue());
								abean.set("week", week);
								abean.set("log_type", "1");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", cd.getDataValue());
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}
				else if(!"all".equals(month)&& "all".equals(week)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList weeklist = this.getWeekList(year, month, null);
					for (int k = 0; k < weeklist.size(); k++) {
						CommonData cd = (CommonData)weeklist.get(k);
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, cd.getDataValue(), day, 1)) ;
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList1.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
								abean.set("year", year);
								abean.set("month",month );
								abean.set("week", cd.getDataValue());
								abean.set("log_type", "1");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time",begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, cd.getDataValue(), day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", month);
								abean.set("aweek", cd.getDataValue());
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}
				else if(!"all".equals(month)&&!"all".equals(week)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1));
                    if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
                    	for (int i = 0; i < hnfList1.size(); i++){
    						LazyDynaBean abean = new LazyDynaBean();
    						LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
    						abean.set("year", year);
    						abean.set("month",month );
    						abean.set("week", week);
    						abean.set("log_type", "1");
    						abean.set("name", hbean.get("a0101"));
    						abean.set("begin_time",begin_time );
    						abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
    						abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
    						abean.set("nbase", hbean.get("nbase"));
							abean.set("a0100", hbean.get("a0100"));
							abean.set("p0100", "");
							abean.set("state", state);
							abean.set("p0115", "01");
							abean.set("ayear", year);
							abean.set("aquarter", season);
							abean.set("amonth", month);
							abean.set("aweek", week);
							abean.set("aday", day);
							abean.set("sp_flag", this.isLessThanCurrent(begin_time));
							String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
							String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
							String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
							String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
							abean.set("mdnofillopt", mdnofillopt);
							abean.set("mdnofillnbase", mdnofillnbase);
							abean.set("mdnofilla0100", mdnofilla0100);
							abean.set("mdnofillp0100", mdnofillp0100);
    						list.add(abean);
    					}
					}
					
				}
			}
			//插入工作总结数据
			if("2".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(month)&& "all".equals(week)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						//每月有多少周
						CommonData cdMonth = (CommonData)monthlist.get(j);
						ArrayList weeklist = this.getWeekList(year, month, null);
						for (int k = 0; k < weeklist.size(); k++) {
							CommonData cdWeek = (CommonData)monthlist.get(k);
							String begin_time = format.format(this.getStartDateAndEndDate(year, season, cdMonth.getDataValue(), cdWeek.getDataValue(), day, 1));
							if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
								for (int i = 0; i < hnfList2.size(); i++){
									LazyDynaBean abean = new LazyDynaBean();
									LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
									abean.set("year", year);
									abean.set("month", cdMonth.getDataValue());
									abean.set("week", cdWeek.getDataValue());
									abean.set("log_type", "2");
									abean.set("name", hbean.get("a0101"));
									abean.set("begin_time", begin_time);
									abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cdMonth.getDataValue(), cdWeek.getDataValue(), day, 2)));
									abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
									abean.set("nbase", hbean.get("nbase"));
									abean.set("a0100", hbean.get("a0100"));
									abean.set("p0100", "");
									abean.set("state", state);
									abean.set("p0115", "01");
									abean.set("ayear", year);
									abean.set("aquarter", season);
									abean.set("amonth", cdMonth.getDataValue());
									abean.set("aweek", cdWeek.getDataValue());
									abean.set("aday", day);
									abean.set("sp_flag", this.isLessThanCurrent(begin_time));
									String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
									String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
									String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
									String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
									abean.set("mdnofillopt", mdnofillopt);
									abean.set("mdnofillnbase", mdnofillnbase);
									abean.set("mdnofilla0100", mdnofilla0100);
									abean.set("mdnofillp0100", mdnofillp0100);
									list.add(abean);
								}
							}
							
						}	
					}
				}
				else if("all".equals(month)&&!"all".equals(week)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						CommonData cd = (CommonData)monthlist.get(j);
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList2.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
								abean.set("year", year);
								abean.set("month", cd.getDataValue());
								abean.set("week", week);
								abean.set("log_type", "2");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", cd.getDataValue());
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}
				else if(!"all".equals(month)&& "all".equals(week)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList weeklist = this.getWeekList(year, month, null);
					for (int k = 0; k < weeklist.size(); k++) {
						CommonData cd = (CommonData)weeklist.get(k);
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, cd.getDataValue(), day, 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList2.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
								abean.set("year", year);
								abean.set("month",month );
								abean.set("week", cd.getDataValue());
								abean.set("log_type", "2");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, cd.getDataValue(), day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", month);
								abean.set("aweek", cd.getDataValue());
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}
				else if(!"all".equals(month)&&!"all".equals(week)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1));
                    if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
                    	for (int i = 0; i < hnfList2.size(); i++){
    						LazyDynaBean abean = new LazyDynaBean();
    						LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
    						abean.set("year", year);
    						abean.set("month",month );
    						abean.set("week", week);
    						abean.set("log_type", "2");
    						abean.set("name", hbean.get("a0101"));
    						abean.set("begin_time", begin_time);
    						abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
    						abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
    						abean.set("nbase", hbean.get("nbase"));
							abean.set("a0100", hbean.get("a0100"));
							abean.set("p0100", "");
							abean.set("state", state);
							abean.set("p0115", "01");
							abean.set("ayear", year);
							abean.set("aquarter", season);
							abean.set("amonth", month);
							abean.set("aweek", week);
							abean.set("aday", day);
							abean.set("sp_flag", this.isLessThanCurrent(begin_time));
							String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
							String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
							String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
							String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
							abean.set("mdnofillopt", mdnofillopt);
							abean.set("mdnofillnbase", mdnofillnbase);
							abean.set("mdnofilla0100", mdnofilla0100);
							abean.set("mdnofillp0100", mdnofillp0100);
    						list.add(abean);
    					}
					}
					
				}
			}
		}
		//日报
		if(state!=null&&"0".equals(state)){
			//插入工作计划数据
			if("1".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(month)&& "all".equals(day)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						//每月有多少天
						CommonData cdMonth = (CommonData)monthlist.get(j);
						ArrayList daylist = this.getDayList(year, cdMonth.getDataValue(), null);
						for (int k = 0; k < daylist.size(); k++) {
							CommonData cdDay = (CommonData)daylist.get(k);
							String begin_time = format.format(this.getStartDateAndEndDate(year, season, cdMonth.getDataValue(), week, cdDay.getDataValue(), 1));
							if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
								for (int i = 0; i < hnfList1.size(); i++){
									LazyDynaBean abean = new LazyDynaBean();
									LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
									abean.set("year", year);
									abean.set("month", cdMonth.getDataValue());
									abean.set("day", cdDay.getDataValue());
									abean.set("log_type", "1");
									abean.set("name", hbean.get("a0101"));
									abean.set("begin_time", begin_time);
									abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cdMonth.getDataValue(), week, cdDay.getDataValue(), 2)));
									abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
									abean.set("nbase", hbean.get("nbase"));
									abean.set("a0100", hbean.get("a0100"));
									abean.set("p0100", "");
									abean.set("state", state);
									abean.set("p0115", "01");
									abean.set("ayear", year);
									abean.set("aquarter", season);
									abean.set("amonth", cdMonth.getDataValue());
									abean.set("aweek", week);
									abean.set("aday", cdDay.getDataValue());
									abean.set("sp_flag", this.isLessThanCurrent(begin_time));
									String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
									String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
									String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
									String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
									abean.set("mdnofillopt", mdnofillopt);
									abean.set("mdnofillnbase", mdnofillnbase);
									abean.set("mdnofilla0100", mdnofilla0100);
									abean.set("mdnofillp0100", mdnofillp0100);
									list.add(abean);
								}
							}
							
						}	
					}
				}
				else if("all".equals(month)&&!"all".equals(day)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						CommonData cd = (CommonData)monthlist.get(j);
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList1.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
								abean.set("year", year);
								abean.set("month", cd.getDataValue());
								abean.set("day", day);
								abean.set("log_type", "1");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", cd.getDataValue());
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}
				else if(!"all".equals(month)&& "all".equals(day)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList daylist = this.getDayList(year, month, null);
					for (int k = 0; k < daylist.size(); k++) {
						CommonData cd = (CommonData)daylist.get(k);
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, cd.getDataValue(), 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList1.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
								abean.set("year", year);
								abean.set("month",month );
								abean.set("day", cd.getDataValue());
								abean.set("log_type", "1");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, cd.getDataValue(), 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", month);
								abean.set("aweek", week);
								abean.set("aday", cd.getDataValue());
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}
				else if(!"all".equals(month)&&!"all".equals(day)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1));
                    if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
                    	for (int i = 0; i < hnfList1.size(); i++){
    						LazyDynaBean abean = new LazyDynaBean();
    						LazyDynaBean hbean = (LazyDynaBean)hnfList1.get(i);
    						abean.set("year", year);
    						abean.set("month",month );
    						abean.set("day", day);
    						abean.set("log_type", "1");
    						abean.set("name", hbean.get("a0101"));
    						abean.set("begin_time",begin_time );
    						abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
    						abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
    						abean.set("nbase", hbean.get("nbase"));
							abean.set("a0100", hbean.get("a0100"));
							abean.set("p0100", "");
							abean.set("state", state);
							abean.set("p0115", "01");
							abean.set("ayear", year);
							abean.set("aquarter", season);
							abean.set("amonth", month);
							abean.set("aweek", week);
							abean.set("aday", day);
							abean.set("sp_flag", this.isLessThanCurrent(begin_time));
							String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
							String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
							String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
							String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
							abean.set("mdnofillopt", mdnofillopt);
							abean.set("mdnofillnbase", mdnofillnbase);
							abean.set("mdnofilla0100", mdnofilla0100);
							abean.set("mdnofillp0100", mdnofillp0100);
    						list.add(abean);
    					}
					}
					
				}
			}
			//插入工作总结数据
			if("2".equals(log_type)|| "all".equals(log_type)){
				if("all".equals(month)&& "all".equals(day)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						//每月有多少天
						CommonData cdMonth = (CommonData)monthlist.get(j);
						ArrayList daylist = this.getDayList(year, cdMonth.getDataValue(), null);
						for (int k = 0; k < daylist.size(); k++) {
							CommonData cdDay = (CommonData)daylist.get(k);
							String begin_time = format.format(this.getStartDateAndEndDate(year, season, cdMonth.getDataValue(), week, cdDay.getDataValue(), 1));
							if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
								for (int i = 0; i < hnfList2.size(); i++){
									LazyDynaBean abean = new LazyDynaBean();
									LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
									abean.set("year", year);
									abean.set("month", cdMonth.getDataValue());
									abean.set("day", cdDay.getDataValue());
									abean.set("log_type", "2");
									abean.set("name", hbean.get("a0101"));
									abean.set("begin_time", begin_time);
									abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cdMonth.getDataValue(), week, cdDay.getDataValue(), 2)));
									abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
									abean.set("nbase", hbean.get("nbase"));
									abean.set("a0100", hbean.get("a0100"));
									abean.set("p0100", "");
									abean.set("state", state);
									abean.set("p0115", "01");
									abean.set("ayear", year);
									abean.set("aquarter", season);
									abean.set("amonth", cdMonth.getDataValue());
									abean.set("aweek", week);
									abean.set("aday", cdDay.getDataValue());
									abean.set("sp_flag", this.isLessThanCurrent(begin_time));
									String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
									String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
									String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
									String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
									abean.set("mdnofillopt", mdnofillopt);
									abean.set("mdnofillnbase", mdnofillnbase);
									abean.set("mdnofilla0100", mdnofilla0100);
									abean.set("mdnofillp0100", mdnofillp0100);
									list.add(abean);
								}
							}
							
						}	
					}
				}
				else if("all".equals(month)&&!"all".equals(day)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList monthlist = this.getMonthList(null);
					for (int j = 0; j < monthlist.size(); j++) {
						CommonData cd = (CommonData)monthlist.get(j);
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList2.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
								abean.set("year", year);
								abean.set("month", cd.getDataValue());
								abean.set("day", day);
								abean.set("log_type", "2");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, cd.getDataValue(), week, day, 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", cd.getDataValue());
								abean.set("aweek", week);
								abean.set("aday", day);
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}
				else if(!"all".equals(month)&& "all".equals(day)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					ArrayList daylist = this.getDayList(year, month, null);
					for (int k = 0; k < daylist.size(); k++) {
						CommonData cd = (CommonData)daylist.get(k);
						String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, cd.getDataValue(), 1));
						if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
							for (int i = 0; i < hnfList2.size(); i++){
								LazyDynaBean abean = new LazyDynaBean();
								LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
								abean.set("year", year);
								abean.set("month",month );
								abean.set("day", cd.getDataValue());
								abean.set("log_type", "2");
								abean.set("name", hbean.get("a0101"));
								abean.set("begin_time", begin_time);
								abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, cd.getDataValue(), 2)));
								abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
								abean.set("nbase", hbean.get("nbase"));
								abean.set("a0100", hbean.get("a0100"));
								abean.set("p0100", "");
								abean.set("state", state);
								abean.set("p0115", "01");
								abean.set("ayear", year);
								abean.set("aquarter", season);
								abean.set("amonth", month);
								abean.set("aweek", week);
								abean.set("aday", cd.getDataValue());
								abean.set("sp_flag", this.isLessThanCurrent(begin_time));
								String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
								String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
								String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
								String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
								abean.set("mdnofillopt", mdnofillopt);
								abean.set("mdnofillnbase", mdnofillnbase);
								abean.set("mdnofilla0100", mdnofilla0100);
								abean.set("mdnofillp0100", mdnofillp0100);
								list.add(abean);
							}
						}
						
					}
				}
				else if(!"all".equals(month)&&!"all".equals(day)){
					SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
					String begin_time = format.format(this.getStartDateAndEndDate(year, season, month, week, day, 1));
                    if(Integer.parseInt(begin_time.replace(".", ""))>=Integer.parseInt(startTime)){
                    	for (int i = 0; i < hnfList2.size(); i++){
    						LazyDynaBean abean = new LazyDynaBean();
    						LazyDynaBean hbean = (LazyDynaBean)hnfList2.get(i);
    						abean.set("year", year);
    						abean.set("month",month );
    						abean.set("day", day);
    						abean.set("log_type", "2");
    						abean.set("name", hbean.get("a0101"));
    						abean.set("begin_time", begin_time);
    						abean.set("end_time", format.format(this.getStartDateAndEndDate(year, season, month, week, day, 2)));
    						abean.set("status", ResourceFactory.getProperty("edit_report.status.wt"));
    						abean.set("nbase", hbean.get("nbase"));
							abean.set("a0100", hbean.get("a0100"));
							abean.set("p0100", "");
							abean.set("state", state);
							abean.set("p0115", "01");
							abean.set("ayear", year);
							abean.set("aquarter", season);
							abean.set("amonth", month);
							abean.set("aweek", week);
							abean.set("aday", day);
							abean.set("sp_flag", this.isLessThanCurrent(begin_time));
							String mdnofillnbase = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("nbase"))));
							String mdnofilla0100 = SafeCode.encode(PubFunc.convertTo64Base(isNull((String)hbean.get("a0100"))));
							String mdnofillp0100 = SafeCode.encode(PubFunc.convertTo64Base(""));
							String mdnofillopt = SafeCode.encode(PubFunc.convertTo64Base("3"));
							abean.set("mdnofillopt", mdnofillopt);
							abean.set("mdnofillnbase", mdnofillnbase);
							abean.set("mdnofilla0100", mdnofilla0100);
							abean.set("mdnofillp0100", mdnofillp0100);
    						list.add(abean);
    					}
					}
					
				}
			}
		}
		return list;
	}
	/**
	 * Description: 判断时间是否小于当前时间1，小于等于  0，大于
	 * @Version1.0 
	 * Aug 22, 2012 11:11:12 AM Jianghe created
	 * @param begin_time
	 * @return
	 */
	public String isLessThanCurrent(String begin_time){
		int flag=0;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String dateStr = format.format(new Date());
		if(Integer.parseInt(begin_time.replace(".", ""))<=Integer.parseInt(dateStr)){
			flag=1;
		}
		return String.valueOf(flag);
	}
	/**
	 * Description: 获取已填的记录列表
	 * @Version1.0 
	 * Aug 3, 2012 3:46:18 PM Jianghe created
	 * @param year
	 * @param season
	 * @param month
	 * @param week
	 * @param day
	 * @param state
	 * @param a_code
	 * @return
	 */
	public ArrayList getFilledList(String year,String season,String month,String week,String day,String name,String state,String a_code,String log_type){
		ArrayList list = new ArrayList();
		RowSet rs = null;
		StringBuffer buf = new StringBuffer("");
		buf.append(" select");
		buf.append(" count(*) as count");
		buf.append(" ,p.log_type");
		buf.append(" ,convert(varchar(10),p.p0104,120) as p0104,convert(varchar(10),p.p0106,120) as p0106");
		buf.append(" from p01 p");
		buf.append(" where 1=1");
		buf.append(" and p.log_type is not null and p.state="+state);
		//日志类型过滤
	    if(log_type!=null&&!"".equals(log_type)&&!"all".equals(log_type)){
	    	buf.append(" and p.log_type="+log_type);
	    }
		buf.append(this.getDateFilter(workType, year, season, month, week, day,log_type));
		buf.append(this.getUserViewPersonWhere(userView));
		buf.append(this.getAcodePersonWhere(a_code));
		//人员库范围
		buf.append(this.getNbaseFilter(userView));
	    buf.append(" group by p.log_type,convert(varchar(10),p.p0104,120),convert(varchar(10),p.p0106,120)");
		buf.append(" order by p.log_type asc,convert(varchar(10),p.p0104,120) desc,convert(varchar(10),p.p0106,120) desc ");
		try{	
			ContentDAO dao = new ContentDAO(this.con);
			//System.out.println("统计已填："+buf.toString());
			rs = dao.search(buf.toString());
			while(rs.next()){
				LazyDynaBean abean  = new LazyDynaBean();
				abean.set("count", rs.getString("count"));
				abean.set("log_type", rs.getString("log_type"));
				//和oracle兼容
				//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				//String p0104_format=format.format(rs.getDate("p0104"));
				//String p0106_format=format.format(rs.getDate("p0106"));
				
				String p0104_format=rs.getString("p0104");
				String p0106_format=rs.getString("p0106");
				abean.set("p0104", p0104_format);
				abean.set("p0106", p0106_format);
				list.add(abean);
			}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * Description: 获取系统启动时间
	 * @Version1.0 
	 * Aug 17, 2012 3:23:10 PM Jianghe created
	 * @param year
	 * @param season
	 * @param month
	 * @param week
	 * @param day
	 * @param state
	 * @param a_code
	 * @param log_type
	 * @return
	 */
	public String getStartTime(String year,String season,String month,String week,String day,String state,String a_code,String log_type){
	    StringBuffer buf = new StringBuffer();
	    RowSet rs = null;
	    String startTime = "19990101";
	    buf.append(" select min(p.p0104) as p0104 from p01 p ");
	    buf.append(" where 1=1");
	    /*
	    //日志类型过滤
	    if(log_type!=null&&!"".equals(log_type)&&!"all".equals(log_type)){
	    	buf.append(" and p.log_type="+log_type);
	    }
	    buf.append(" and p.state="+state);
	   // buf.append(this.getDateFilter(workType, year, season, month, week, day));
		buf.append(this.getUserViewPersonWhere(userView,1));
		buf.append(this.getAcodePersonWhere(a_code));
		//人员库范围
		buf.append(this.getNbaseFilter(userView));*/
		try{	
			ContentDAO dao = new ContentDAO(this.con);
			rs = dao.search(buf.toString());
			if(rs.next()){
				//和oracle兼容
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				startTime=format.format(rs.getDate("p0104"));
			}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return startTime;
	}
	/**
	 * Description: 管理范围没有填计划或总结的人列表
	 * @Version1.0 
	 * Aug 3, 2012 3:49:43 PM Jianghe created
	 * @param workType
	 * @param year
	 * @param season
	 * @param month
	 * @param week
	 * @param day
	 * @param a_code
	 * @param state
	 * @param log_type
	 * @return
	 */
	public ArrayList gethaveNotFilledList(String workType,String year,String season,String month,String week,String day,String name,String a_code,String state,String log_type){
		ArrayList list = new ArrayList();
		nbase = (String)workParametersMap.get("nbase");
		RowSet rs = null;
		
		
		ArrayList dblist = userView.getPrivDbList();
		ArrayList from_base = new ArrayList();
		if(nbase!=null){
			String[] str_array = nbase.split(",");
			int count=0;
			for (int i = 0; i < str_array.length; i++) {
				if(dblist.contains(str_array[i])){
					from_base.add(str_array[i]);
					count++;
				}
			}
			if(count!=0){
				for (int i = 0; i < from_base.size(); i++) {
					StringBuffer buf = new StringBuffer("");
					buf.append(" select ua.a0100,ua.a0101 from "+from_base.get(i)+"a01 ua where 1=1");
					if (name!=null&&!"".equalsIgnoreCase(name.trim()))
				    	buf.append(" and ua.a0101 like '%" + name + "%' ");
					//获取管理范围人员where子句
					buf.append(this.getUserViewPersonWhere(userView));
					//获取树链接所属单位或部门人员where子句
					buf.append(this.getAcodePersonWhere(a_code));
					buf.append(" and ua.a0100 not in(select p.a0100 from p01 p where p.a0100=ua.a0100");
					//判断工作计划还是总结
					buf.append(" and p.log_type="+log_type);
					buf.append(" and p.state="+state);
					buf.append(this.getDateFilter(workType, year, season, month, week, day,log_type));
					buf.append(")");
					buf.append(" order by ua.a0000 asc,ua.a0100 desc");
					//System.out.println(buf.toString());
					try{	
						ContentDAO dao = new ContentDAO(this.con);
						//System.out.println(buf.toString());
						rs = dao.search(buf.toString());
						while(rs.next()){
							LazyDynaBean abean  = new LazyDynaBean();
							abean.set("a0100", rs.getString("a0100"));
							abean.set("a0101", isNull(rs.getString("a0101")));
							abean.set("nbase", from_base.get(i));
							list.add(abean);
						}	
					}catch(Exception e)
					{
						e.printStackTrace();
					}finally{
						try{
							if(rs!=null)
								rs.close();
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			
		}	
		return list;
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
	 * Description: 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * @Version1.0 
	 * Aug 9, 2012 11:54:56 AM Jianghe created
	 * @param userView
	 * @param flag
	 * @return
	 */
	public String getUserViewPersonWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnit_id();
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or e0122 like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else if("UN".equalsIgnoreCase(codeid))
						buf.append(" and b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and e01a1 like '" + codevalue + "%'");
					else
						buf.append(" and b0110 like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}
        //System.out.println("管理范围sql："+str);
		return str;		
	}
	
	/**
	 * Description: 获取树链接所属单位或部门员工的where子句
	 * @Version1.0 
	 * Aug 9, 2012 11:47:46 AM Jianghe created
	 * @param a_code
	 * @return
	 */
	public String getAcodePersonWhere(String a_code){
		StringBuffer buf = new StringBuffer();
		if(a_code!=null && a_code.trim().length()>0)
		{								
			if(a_code.indexOf("UN")!=-1)
			{				
				if(!"".equals(a_code.substring(2, a_code.length()))){
					
					buf.append(" and b0110 like '" + a_code.substring(2, a_code.length()) + "%'");
				}
				
			}else if(a_code.indexOf("UM")!=-1)
			{
				buf.append(" and e0122 like '" + a_code.substring(2, a_code.length()) + "%'");
			}
		}
		return buf.toString();
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
	 * Description: 公共where子句 过滤条件
	 * @Version1.0 
	 * Aug 9, 2012 1:15:12 PM Jianghe created
	 * @param workType
	 * @param year
	 * @param season
	 * @param month
	 * @param week
	 * @param day
	 * @param status
	 * @param name
	 * @param a_code
	 * @param state
	 * @return
	 */
	public String getDateFilter(String workType,String year,String season,String month,String week,String day,String log_type){
		StringBuffer buf = new StringBuffer();
		//年团队
		if(workType!=null&&"2".equals(workType)){
			if(year!=null&&!"".equals(year)&&!"all".equals(year)){
				buf.append(" and "+Sql_switcher.year("p.p0104")+"="+year);
	    }
		}
		//季度团队
		if(workType!=null&&"4".equals(workType)){
			if(year!=null&&!"".equals(year)&&!"all".equals(year)){
				buf.append(" and "+Sql_switcher.year("p.p0104")+"="+year);
	        }
			// 季计划
	      String valid13="0";
	      if(workParametersMap.get("valid13")!=null)
  		     valid13 = (String)workParametersMap.get("valid13");
 		  String period13="";
 		  if(workParametersMap.get("period13")!=null)   		      	
  	         period13 = (String)workParametersMap.get("period13");
  		  // 季总结
  		  String valid23="0";
  		  if(workParametersMap.get("valid23")!=null)
  		     valid23 = (String)workParametersMap.get("valid23");	
  		  String period23="";	
  		  if(workParametersMap.get("period23")!=null)
  		     period23 = (String)workParametersMap.get("period23");
  		  String t_period13 = ","+period13+",";
	      String t_period23 = ","+period23+",";
			
	        String jh_tmp=period13;
			String jh_flag=" in ";
			if(!"all".equals(season)){
				String str = ","+season+",";
				if(t_period13.indexOf(str)!=-1){
					jh_tmp=season;
				}else{
					jh_tmp=null;
				}
			}
				
			if("0".equals(valid13)){
				jh_flag=" not in ";
			}
			String zj_tmp=period23;
			String zj_flag=" in ";
			if("0".equals(valid23)){
				zj_flag=" not in ";
			}
			if(!"all".equals(season)){
				String str = ","+season+",";
				if(t_period23.indexOf(str)!=-1){
					zj_tmp=season;
				}else{
					zj_tmp=null;
				}
			}
			buf.append(" and (");
			buf.append("(log_type=1 and "+Sql_switcher.quarter("p.p0104")+jh_flag+" ( "+jh_tmp+")) or ( log_type=2 and "+Sql_switcher.quarter("p.p0104")+zj_flag+" ("+zj_tmp+"))");
			buf.append(")");
		}
		//月团队
		
		if(workType!=null&&"6".equals(workType)){
			if(year!=null&&!"".equals(year)&&!"all".equals(year)){
				buf.append(" and "+Sql_switcher.year("p.p0104")+"="+year);
	        }
			//计划参数
			String valid12="0";
			if(workParametersMap.get("valid12")!=null)
				valid12=(String)workParametersMap.get("valid12");
			String period12="";
			if(workParametersMap.get("period12")!=null)
				period12=(String)workParametersMap.get("period12");
			//总结参数
			String valid22="0";
			if(workParametersMap.get("valid22")!=null)
				valid22=(String)workParametersMap.get("valid22");
            String period22="";
            if(workParametersMap.get("period22")!=null)
            	period22=(String)workParametersMap.get("period22");
			String t_period12=","+period12+",";
	    	String t_period22=","+period22+",";
	    	      
			
				String jh_tmp=period12;
				String jh_flag=" in ";
				if(!"all".equals(month)){
					String str = ","+month+",";
					if(t_period12.indexOf(str)!=-1){
						jh_tmp=month;
					}else{
						jh_tmp=null;
					}
				}
					
				if("0".equals(valid12)){
					jh_flag=" not in ";
				}
				String zj_tmp=period22;
				String zj_flag=" in ";
				if("0".equals(valid22)){
					zj_flag=" not in ";
				}
				if(!"all".equals(month)){
					String str = ","+month+",";
					if(t_period22.indexOf(str)!=-1){
						zj_tmp=month;
					}else{
						zj_tmp=null;
					}
				}
				buf.append(" and (");
				buf.append("(log_type=1 and "+Sql_switcher.month("p.p0104")+jh_flag+" ( "+jh_tmp+")) or ( log_type=2 and "+Sql_switcher.month("p.p0104")+zj_flag+" ("+zj_tmp+"))");
				buf.append(")");
		}
			
		
		//周团队
		if(workType!=null&&"10".equals(workType)){
			if(!"all".equals(month)){
				//月不全部
				//周不全部
				if(!"all".equals(week)){
					//该月该周的起始日期
					int aaweek = weekutil.totalWeek(Integer.parseInt(year),Integer.parseInt(month));
					Date date = null;
					//2015/12/28 wangjl 防止用户选择没有的周期
					week=aaweek>=Integer.parseInt(week)?week:aaweek+"";
					for (int i = 1; i <= aaweek; i++) {
						if(i==Integer.parseInt(week)){
							date = weekutil.numWeek(Integer.parseInt(year), Integer.parseInt(month), i, 1);
							break;
						}
					}
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
					String strDate = sdf.format(date);
					buf.append(" and "+Sql_switcher.year("p.p0104")+"*10000+"+Sql_switcher.month("p.p0104")+"*100+"+Sql_switcher.day("p.p0104")+"="+strDate);
				}
				//周全部
				else{
					int aaweek = weekutil.totalWeek(Integer.parseInt(year),Integer.parseInt(month));
					Date beginDate=null;
					Date endDate=null;
					for (int i = 1; i <= aaweek; i++) {
						if(i==1){
							beginDate =  weekutil.numWeek(Integer.parseInt(year), Integer.parseInt(month), i, 1);
							continue;
						}
						if(i==aaweek){
							endDate = weekutil.numWeek(Integer.parseInt(year), Integer.parseInt(month), i, 7);
							break;
						}
					}
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
					String strBeginDate = sdf.format(beginDate);
					String strEndDate = sdf.format(endDate);
					buf.append(" and "+Sql_switcher.year("p.p0104")+"*10000+"+Sql_switcher.month("p.p0104")+"*100+"+Sql_switcher.day("p.p0104")+">="+strBeginDate);
					buf.append(" and "+Sql_switcher.year("p.p0104")+"*10000+"+Sql_switcher.month("p.p0104")+"*100+"+Sql_switcher.day("p.p0104")+"<="+strEndDate);
				}
			}else{
				//月全部
				//周不全部
				if(!"all".equals(week)){
					//找出每个月固定周的起始日期
					buf.append(" and ( ");
					for (int i = 1; i <= 12; i++) {
						int aaweek = weekutil.totalWeek(Integer.parseInt(year),i);
						Date date = null;
						date = weekutil.numWeek(Integer.parseInt(year), i, Integer.parseInt(week), 1);
						SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
						String strDate = sdf.format(date);
						if(i!=1){
							buf.append(" or (");
						}
						buf.append(Sql_switcher.year("p.p0104")+"*10000+"+Sql_switcher.month("p.p0104")+"*100+"+Sql_switcher.day("p.p0104")+"="+strDate);
						if(i!=1){
							buf.append(" ) ");
						}
					}
					buf.append(" ) ");
				}
				//周全部
				else{
					//找出1月的第一周的起始日期和12月最后一周的结束日期
					int weekOfDecember = weekutil.totalWeek(Integer.parseInt(year),12);
					Date beginDate=null;
					Date endDate=null;
					beginDate =  weekutil.numWeek(Integer.parseInt(year), 1, 1, 1);
					endDate = weekutil.numWeek(Integer.parseInt(year), 12, weekOfDecember, 7);
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
					String strBeginDate = sdf.format(beginDate);
					String strEndDate = sdf.format(endDate);
					buf.append(" and "+Sql_switcher.year("p.p0104")+"*10000+"+Sql_switcher.month("p.p0104")+"*100+"+Sql_switcher.day("p.p0104")+">="+strBeginDate);
					buf.append(" and "+Sql_switcher.year("p.p0104")+"*10000+"+Sql_switcher.month("p.p0104")+"*100+"+Sql_switcher.day("p.p0104")+"<="+strEndDate);
				
				}
			}
		}
		//日团队
		if(workType!=null&&"8".equals(workType)){
			if(year!=null&&!"".equals(year)&&!"all".equals(year)){
				buf.append(" and "+Sql_switcher.year("p.p0104")+"="+year);
	        }
			if(month!=null&&!"".equals(month)&&!"all".equals(month)){
		    	buf.append(" and "+Sql_switcher.month("p.p0104")+"="+month);
		    }
			if(day!=null&&!"".equals(day)&&!"all".equals(day)){
		    	buf.append(" and "+Sql_switcher.day("p.p0104")+"="+day);
		    }
		}
		return buf.toString();
	}
	/**
	 * Description: 卡条件 自己的人员库管理范围
	 * @Version1.0 
	 * Aug 9, 2012 2:19:25 PM Jianghe created
	 * @return
	 */
	public String getNbaseFilter(UserView userView){
		StringBuffer strbuf = new StringBuffer();
		StringBuffer buf = new StringBuffer();
		nbase = (String)workParametersMap.get("nbase");
		ArrayList dblist = userView.getPrivDbList();
				
		if(dblist.size()==0){
			buf.append("null");
		}else
		{
			int count=0;
			if(nbase!=null && nbase.trim().length()>0)
			{
				String[] str_array = nbase.split(",");
			    for (int i = 0; i < str_array.length; i++) 
			    {
				   if(dblist.contains(str_array[i]))
				   {
					   buf.append(",");				   
					   buf.append("'"+str_array[i]+"'");
					   count++;
				   }
			    }
			}
		    if(count==0){
		    	buf.append("null");
		    }
		}
		String str = buf.toString();
		if(!"null".equals(str)&&str.length()>0){
			str = str.substring(str.indexOf(",")+1,str.length());
		}
		strbuf.append(" and p.NBASE in ( "+str+" ) " );
		//System.out.println(strbuf.toString());
		return strbuf.toString();
	}
	/**
	 * 根据当前填写的计划记录，求得该计划的起始时间和结束时间
	 * @param year_num
	 * @param quarter_num
	 * @param month_num
	 * @param week_num
	 * @param day_num
	 * @param type
	 * @return
	 */
	public Date getStartDateAndEndDate(String year_num,String quarter_num,String month_num,String week_num,String day_num,int type)
	{
		Date date = null;
		if(type==1)//求起始时间
		{			
			if("0".equals(this.state))//日计划
			{
	        	Calendar start_calendar=Calendar.getInstance();
	        	start_calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        	start_calendar.set(Calendar.MONTH,Integer.parseInt(month_num)-1);
	        	start_calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(day_num));
	        	start_calendar.set(Calendar.HOUR_OF_DAY,0);
	        	start_calendar.set(Calendar.MINUTE,0);
	        	start_calendar.set(Calendar.SECOND,0);
	            date = start_calendar.getTime();
			}
			else if("1".equals(this.state))// 周计划
			{
				WeekUtils weekutils = new WeekUtils();
	        	return weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),1);
			}
			else if("2".equals(this.state))//月计划
			{
				Calendar calendar=Calendar.getInstance();
				calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
				calendar.set(Calendar.MONTH,Integer.parseInt(month_num)-1);
				calendar.set(Calendar.DAY_OF_MONTH,1);
				date = calendar.getTime();
			}
			else if("3".equals(this.state)) // 季计划
			{
				//curr_day为当前记录是第几季度
	        	if(Integer.parseInt(quarter_num)==1)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,0);
	        		calendar.set(Calendar.DAY_OF_MONTH,1);	        	    
	        	    date = calendar.getTime();
	        	}
	        	else if(Integer.parseInt(quarter_num)==2)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,3);
	        		calendar.set(Calendar.DAY_OF_MONTH,1);	        	    
	        	    date = calendar.getTime();        		
	        	}
	        	else if(Integer.parseInt(quarter_num)==3)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,6);
	        		calendar.set(Calendar.DAY_OF_MONTH,1);	        	    
	        	    date = calendar.getTime();
	        	}
	        	else if(Integer.parseInt(quarter_num)==4)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,9);
	        		calendar.set(Calendar.DAY_OF_MONTH,1);	        	    
	        	    date = calendar.getTime();
	        	}
				
			}
			else if("4".equals(this.state)) // 年计划
			{
				Calendar calendar = Calendar.getInstance();	    		
				calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
				calendar.set(Calendar.MONTH,0);
				calendar.set(Calendar.DAY_OF_MONTH,1);
	    		date = calendar.getTime();				
			}
			
		}else // 结束时间
		{			
			if("0".equals(this.state)) // 日计划
			{
				Calendar start_calendar=Calendar.getInstance();
	        	start_calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        	start_calendar.set(Calendar.MONTH,Integer.parseInt(month_num)-1);
	        	start_calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(day_num));
	        	start_calendar.set(Calendar.HOUR_OF_DAY,0);
	        	start_calendar.set(Calendar.MINUTE,0);
	        	start_calendar.set(Calendar.SECOND,0);
	            date=start_calendar.getTime();
			}
			else if("1".equals(this.state)) // 周计划
			{
				WeekUtils weekutils = new WeekUtils();
	        	return weekutils.numWeek(Integer.parseInt(year_num),Integer.parseInt(month_num),Integer.parseInt(week_num),7);
			}
			else if("2".equals(this.state)) // 月计划
			{
				Calendar calendar=Calendar.getInstance();
				calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
				calendar.set(Calendar.MONTH,Integer.parseInt(month_num)-1);
				calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				date=calendar.getTime();
			}
			else if("3".equals(this.state)) // 季计划
			{
				// curr_day为当前记录是第几季度
	        	if(Integer.parseInt(quarter_num)==1)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,2);
	        		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	        	    
	        	    date = calendar.getTime();
	        	}
	        	else if(Integer.parseInt(quarter_num)==2)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,5);
	        		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	        	    
	        	    date = calendar.getTime();        		
	        	}
	        	else if(Integer.parseInt(quarter_num)==3)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,8);
	        		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	        	    
	        	    date = calendar.getTime();
	        	}
	        	else if(Integer.parseInt(quarter_num)==4)
	        	{
	        		Calendar calendar = Calendar.getInstance();	        		
	        		calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
	        		calendar.set(Calendar.MONTH,11);
	        		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	        	    
	        	    date = calendar.getTime();
	        	}				
			}
			else if("4".equals(this.state)) // 年计划
			{
				Calendar calendar = Calendar.getInstance();	    		
				calendar.set(Calendar.YEAR,Integer.parseInt(year_num));
				calendar.set(Calendar.MONTH,11);
				calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));	
	    		date = calendar.getTime();				
			}
		}
		return date;
	}
	/**
	 * 取得用户涉及年列表
	 * @return
	 */
	public ArrayList getUserYearList()
	{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try{
		    StringBuffer buf = new StringBuffer("");
		    buf.append(" select distinct "+Sql_switcher.year("p0104")+" as ayear from p01 ");
		    buf.append(" where ");
		    buf.append(" state="+this.state);
		    buf.append(" and UPPER(nbase)='"+this.nbase.toUpperCase()+"'");
		    buf.append(" and a0100='"+this.userView.getA0100()+"' order by "+Sql_switcher.year("p0104")+" desc");
		    ContentDAO dao = new ContentDAO(this.con);
		    rs=dao.search(buf.toString());
		    boolean isAddThisYear=true;
		    String thisYear=Calendar.getInstance().get(Calendar.YEAR)+"";
		    while(rs.next())
		    {
		    	String ayear=rs.getString("ayear");
		    	if(ayear!=null && ayear.trim().length()>0 && ayear.equalsIgnoreCase(thisYear))
		    		isAddThisYear=false;
		    	list.add(new CommonData(ayear,ayear));
		    }
		    if(isAddThisYear)
		    	list.add(new CommonData(thisYear,thisYear));
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	//判断日期在哪年哪月第几周
	public int[] getWeekWhere(Date date){
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
		String a= DateUtils.format(date, "yyyy.MM.dd");
		String b= DateUtils.format(endDate, "yyyy.MM.dd");
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
			//2015/12/28 wangjl 当month=13的时候到下一年
			if(month==13){
				month = 1;
				year = year+1;
			}
//			if(month==1){
//				year = year+1;
//			}
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
		if(key)
		    obj.setNullable(false);
		obj.setKeyable(key);	
		return obj;
    }
}		

