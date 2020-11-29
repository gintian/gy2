package com.hjsj.hrms.transaction.performance.workplan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:WorkplanDataTrans.java</p>
 * <p>Description:工作计划参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-06-15 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class WorkplanDataTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());	
			
			String sp_relation = ""; // 审批关系
			String sp_level = "1"; // 审批层级 1: 一级审批 2:逐级审批 默认为1
			String record_grade = "False"; // 是否对纪实进行评分 False：不评 True：评 默认为 False
			String defaultLines = "12"; // 工作计划默认显示行数
			String dailyPlan_attachment = "True"; // 是否显示工作计划附件上传功能 False：不显示 True：显示 默认为 True
			String dailySumm_attachment = "True"; // 是否显示工作总结附件上传功能 False：不显示 True：显示 默认为 True
			String planTarget = ""; // 工作计划显示的日志指标
			String summTarget = ""; // 工作总结显示的日志指标
			String nbase = ""; // 人员库			
			ArrayList dbnameList = new ArrayList(); // 人员库列表
			dbnameList = getDbnameList("");
			
			// 年报 工作计划参数
			String valid14 = "True";
			String prior_end14 = "";
			String current_start14 = "";
			String refer_id14 = "";
			String print_id14 = "";
			// 年报 工作总结参数
			String valid24 = "True";
			String current_end24 = "";
			String last_start24 = "";
			String refer_id24 = "";
			String print_id24 = "";

			// 季报 工作计划参数
			String valid13 = "True";
			String prior_end13 = "";
			String current_start13 = "";
			String period13 = "";
			String period131 = "False";
		    String period132 = "False";
		    String period133 = "False";
		    String period134 = "False";
			String refer_id13 = "";
			String print_id13 = "";
			// 季报 工作总结参数
			String valid23 = "True";
			String current_end23 = "";
			String last_start23 = "";
			String period23 = "";
			String period231 = "False";
		    String period232 = "False";
		    String period233 = "False";
		    String period234 = "False";
			String refer_id23 = "";
			String print_id23 = "";
			
			// 月报 工作计划参数
			String valid12 = "True";
			String prior_end12 = "";
			String current_start12 = "";
			String period12 = "";
			String period121 = "False";
		    String period122 = "False";
		    String period123 = "False";
		    String period124 = "False";
		    String period125 = "False";
		    String period126 = "False";
		    String period127 = "False";
		    String period128 = "False";
		    String period129 = "False";
		    String period1210 = "False";
		    String period1211 = "False";
		    String period1212 = "False";
			String refer_id12 = "";
			String print_id12 = "";
			// 月报 工作总结参数
			String valid22 = "True";
			String current_end22 = "";
			String last_start22 = "";
			String period22 = "";
			String period221 = "False";
		    String period222 = "False";
		    String period223 = "False";
		    String period224 = "False";
		    String period225 = "False";
		    String period226 = "False";
		    String period227 = "False";
		    String period228 = "False";
		    String period229 = "False";
		    String period2210 = "False";
		    String period2211 = "False";
		    String period2212 = "False";
			String refer_id22 = "";
			String print_id22 = "";
			
			// 周报 工作计划参数
			String valid11 = "True";
			String prior_end11 = "";
			String current_start11 = "";
			String refer_id11 = "";
			String print_id11 = "";
			// 周报 工作总结参数
			String valid21 = "True";
			String current_end21 = "";
			String last_start21 = "";
			String refer_id21 = "";
			String print_id21 = "";
			
			// 日报 工作计划参数
			String valid0 = "True";
			String current_date = "0";
			String time = "";
			String limit_HH = "00";
		    String limit_MM = "00";
			String refer_id0 = "";
			String print_id0 = "";			
			
			this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if ( this.frowset.next())
		    {
		    	String str_value = this.frowset.getString("str_value");
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
		    			sp_relation = ele.getAttributeValue("sp_relation");
		    			sp_level = ele.getAttributeValue("sp_level");
		    			record_grade = isNull(ele.getAttributeValue("record_grade"));
		    			defaultLines = isNull(ele.getAttributeValue("defaultLines"));
		    			if(defaultLines==null || defaultLines.trim().length()<=0 || "0".equalsIgnoreCase(defaultLines))
		    				defaultLines = "12";
		    			dailyPlan_attachment = isNull(ele.getAttributeValue("dailyPlan_attachment"));
		    			if(dailyPlan_attachment==null || dailyPlan_attachment.trim().length()<=0)
		    				dailyPlan_attachment = "True";
		    			dailySumm_attachment = isNull(ele.getAttributeValue("dailySumm_attachment"));
		    			if(dailySumm_attachment==null || dailySumm_attachment.trim().length()<=0)
		    				dailySumm_attachment = "True";
		    			planTarget = isNull(ele.getAttributeValue("planTarget"));
		    			summTarget = isNull(ele.getAttributeValue("summTarget"));		    			
		    			nbase = ele.getAttributeValue("nbase");	
		    			dbnameList = getDbnameList(nbase);
		    			
		    			List list = (List) ele.getChildren("work_record");
	    				for (int i = 0; i < list.size(); i++)
	    				{   					
	    					Element wele = (Element) list.get(i);;     						   	    					
	    					if (wele != null)
	    		    	    {	    							    						
	    						String cycle = wele.getAttributeValue("cycle"); // cycle: 填报周期:(0|1|2|3|4)=( 日|周|月|季|年)
	    		    	    	
	    		    	    	/*
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
		    		    	    		
		    		    	    		if("4".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))
		    		    	    		{
		    		    	    			valid14 = isNull(temp.getAttributeValue("valid"));
		    		    	    			prior_end14 = isNull(temp.getAttributeValue("prior_end"));
		    		    	    			current_start14 = isNull(temp.getAttributeValue("current_start"));
		    		    	    			refer_id14 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			print_id14 = isNull(temp.getAttributeValue("print_id"));
		    		    	    		}
		    		    	    		else if("4".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))
		    		    	    		{
		    		    	    			valid24 = isNull(temp.getAttributeValue("valid"));
		    		    	    			current_end24 = isNull(temp.getAttributeValue("current_end"));
		    		    	    			last_start24 = isNull(temp.getAttributeValue("last_start"));
		    		    	    			refer_id24 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			print_id24 = isNull(temp.getAttributeValue("print_id"));
		    		    	    		}
		    		    	    		else if("3".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))
		    		    	    		{
		    		    	    			valid13 = isNull(temp.getAttributeValue("valid"));
		    		    	    			prior_end13 = isNull(temp.getAttributeValue("prior_end"));
		    		    	    			current_start13 = isNull(temp.getAttributeValue("current_start"));
		    		    	    			period13 = isNull(temp.getAttributeValue("period"));
		    		    	    			
		    		    	    			if(period13!=null && period13.trim().length()>0)
		    		    	    			{
		    		    	    				String[] meters = period13.split(",");
		    		    	    				if(meters!=null && meters.length>0)
		    		    	    				{
		    		    	    					for (int x = 0; x < meters.length; x++)
		    		    	    					{
		    		    	    						if("1".equals(meters[x]))
		    		    	    							period131 = "True";
		    		    	    						else if("2".equals(meters[x]))
		    		    	    							period132 = "True";
		    		    	    						else if("3".equals(meters[x]))
		    		    	    							period133 = "True";
		    		    	    						else if("4".equals(meters[x]))
		    		    	    							period134 = "True";
		    		    	    					}
		    		    	    				}
		    		    	    			}
		    		    	    			refer_id13 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			print_id13 = isNull(temp.getAttributeValue("print_id"));
		    		    	    		}
		    		    	    		else if("3".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))
		    		    	    		{
		    		    	    			valid23 = isNull(temp.getAttributeValue("valid"));
		    		    	    			current_end23 = isNull(temp.getAttributeValue("current_end"));
		    		    	    			last_start23 = isNull(temp.getAttributeValue("last_start"));
		    		    	    			period23 = isNull(temp.getAttributeValue("period"));
		    		    	    			
		    		    	    			if(period23!=null && period23.trim().length()>0)
		    		    	    			{
		    		    	    				String[] meters = period23.split(",");
		    		    	    				if(meters!=null && meters.length>0)
		    		    	    				{
		    		    	    					for (int x = 0; x < meters.length; x++)
		    		    	    					{
		    		    	    						if("1".equals(meters[x]))
		    		    	    							period231 = "True";
		    		    	    						else if("2".equals(meters[x]))
		    		    	    							period232 = "True";
		    		    	    						else if("3".equals(meters[x]))
		    		    	    							period233 = "True";
		    		    	    						else if("4".equals(meters[x]))
		    		    	    							period234 = "True";
		    		    	    					}
		    		    	    				}
		    		    	    			}		    		    	    			
		    		    	    			refer_id23 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			print_id23 = isNull(temp.getAttributeValue("print_id"));
		    		    	    		}
		    		    	    		else if("2".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))
		    		    	    		{
		    		    	    			valid12 = isNull(temp.getAttributeValue("valid"));
		    		    	    			prior_end12 = isNull(temp.getAttributeValue("prior_end"));
		    		    	    			current_start12 = isNull(temp.getAttributeValue("current_start"));
		    		    	    			period12 = isNull(temp.getAttributeValue("period"));
		    		    	    			
		    		    	    			if(period12!=null && period12.trim().length()>0)
		    		    	    			{
		    		    	    				String[] meters = period12.split(",");
		    		    	    				if(meters!=null && meters.length>0)
		    		    	    				{
		    		    	    					for (int x = 0; x < meters.length; x++)
		    		    	    					{
		    		    	    						if("1".equals(meters[x]))
		    		    	    							period121 = "True";
		    		    	    						else if("2".equals(meters[x]))
		    		    	    							period122 = "True";
		    		    	    						else if("3".equals(meters[x]))
		    		    	    							period123 = "True";
		    		    	    						else if("4".equals(meters[x]))
		    		    	    							period124 = "True";
		    		    	    						else if("5".equals(meters[x]))
		    		    	    							period125 = "True";
		    		    	    						else if("6".equals(meters[x]))
		    		    	    							period126 = "True";
		    		    	    						else if("7".equals(meters[x]))
		    		    	    							period127 = "True";
		    		    	    						else if("8".equals(meters[x]))
		    		    	    							period128 = "True";
		    		    	    						else if("9".equals(meters[x]))
		    		    	    							period129 = "True";
		    		    	    						else if("10".equals(meters[x]))
		    		    	    							period1210 = "True";
		    		    	    						else if("11".equals(meters[x]))
		    		    	    							period1211 = "True";
		    		    	    						else if("12".equals(meters[x]))
		    		    	    							period1212 = "True";
		    		    	    					}
		    		    	    				}
		    		    	    			}		    		    	    			
		    		    	    			refer_id12 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			print_id12 = isNull(temp.getAttributeValue("print_id"));
		    		    	    		}
		    		    	    		else if("2".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))
		    		    	    		{
		    		    	    			valid22 = isNull(temp.getAttributeValue("valid"));
		    		    	    			current_end22 = isNull(temp.getAttributeValue("current_end"));
		    		    	    			last_start22 = isNull(temp.getAttributeValue("last_start"));
		    		    	    			period22 = isNull(temp.getAttributeValue("period"));
		    		    	    			
		    		    	    			if(period22!=null && period22.trim().length()>0)
		    		    	    			{
		    		    	    				String[] meters = period22.split(",");
		    		    	    				if(meters!=null && meters.length>0)
		    		    	    				{
		    		    	    					for (int x = 0; x < meters.length; x++)
		    		    	    					{
		    		    	    						if("1".equals(meters[x]))
		    		    	    							period221 = "True";
		    		    	    						else if("2".equals(meters[x]))
		    		    	    							period222 = "True";
		    		    	    						else if("3".equals(meters[x]))
		    		    	    							period223 = "True";
		    		    	    						else if("4".equals(meters[x]))
		    		    	    							period224 = "True";
		    		    	    						else if("5".equals(meters[x]))
		    		    	    							period225 = "True";
		    		    	    						else if("6".equals(meters[x]))
		    		    	    							period226 = "True";
		    		    	    						else if("7".equals(meters[x]))
		    		    	    							period227 = "True";
		    		    	    						else if("8".equals(meters[x]))
		    		    	    							period228 = "True";
		    		    	    						else if("9".equals(meters[x]))
		    		    	    							period229 = "True";
		    		    	    						else if("10".equals(meters[x]))
		    		    	    							period2210 = "True";
		    		    	    						else if("11".equals(meters[x]))
		    		    	    							period2211 = "True";
		    		    	    						else if("12".equals(meters[x]))
		    		    	    							period2212 = "True";
		    		    	    					}
		    		    	    				}
		    		    	    			}		    		    	    			
		    		    	    			refer_id22 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			print_id22 = isNull(temp.getAttributeValue("print_id"));
		    		    	    		}
		    		    	    		else if("1".equalsIgnoreCase(cycle) && "1".equalsIgnoreCase(type))
		    		    	    		{
		    		    	    			valid11 = isNull(temp.getAttributeValue("valid"));
		    		    	    			prior_end11 = isNull(temp.getAttributeValue("prior_end"));
		    		    	    			current_start11 = isNull(temp.getAttributeValue("current_start"));
		    		    	    			refer_id11 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			print_id11 = isNull(temp.getAttributeValue("print_id"));
		    		    	    		}
		    		    	    		else if("1".equalsIgnoreCase(cycle) && "2".equalsIgnoreCase(type))
		    		    	    		{
		    		    	    			valid21 = isNull(temp.getAttributeValue("valid"));
		    		    	    			current_end21 = isNull(temp.getAttributeValue("current_end"));
		    		    	    			last_start21 = isNull(temp.getAttributeValue("last_start"));
		    		    	    			refer_id21 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			print_id21 = isNull(temp.getAttributeValue("print_id"));
		    		    	    		}
		    		    	    		else if("0".equalsIgnoreCase(cycle))
		    		    	    		{
		    		    	    			valid0 = isNull(temp.getAttributeValue("valid"));
		    		    	    			current_date = isNull(temp.getAttributeValue("current_date"));
		    		    	    			time = isNull(temp.getAttributeValue("time"));
		    		    	    			if(time!=null && time.trim().length()>0)
		    		    	    			{
			    		    	    			limit_HH = time.substring(0,time.indexOf(":"));
			    		    	    		    limit_MM = time.substring(time.indexOf(":")+1,time.length());
		    		    	    			}
		    		    	    			refer_id0 = isNull(temp.getAttributeValue("refer_id"));
		    		    	    			print_id0 = isNull(temp.getAttributeValue("print_id"));
		    		    	    		}
			    		    	    }
	    		    	    	}	    		    	    		    		    	    		    		    	    	
	    		    	    }
	    		    	}																									    			
		    		}
		    	}
		    }
			
			
		//	System.out.println("======================"+this.userView.getPrivDbList());
			
			
			this.getFormHM().put("sp_relation",sp_relation);
			this.getFormHM().put("sp_level",sp_level);
			this.getFormHM().put("record_grade",record_grade);
			this.getFormHM().put("defaultLines",defaultLines);
			this.getFormHM().put("dailyPlan_attachment",dailyPlan_attachment);
			this.getFormHM().put("dailySumm_attachment",dailySumm_attachment);
			this.getFormHM().put("planTarget",planTarget);
			this.getFormHM().put("summTarget",summTarget);			
			this.getFormHM().put("nbase",nbase);
			this.getFormHM().put("dbnameList",dbnameList);
			
			this.getFormHM().put("valid14",valid14);
			this.getFormHM().put("prior_end14",prior_end14);
			this.getFormHM().put("current_start14",current_start14);
			this.getFormHM().put("refer_id14",refer_id14);
			this.getFormHM().put("print_id14",print_id14);
			this.getFormHM().put("valid24",valid24);
			this.getFormHM().put("current_end24",current_end24);
			this.getFormHM().put("last_start24",last_start24);
			this.getFormHM().put("refer_id24",refer_id24);
			this.getFormHM().put("print_id24",print_id24);
			
			this.getFormHM().put("valid13",valid13);
			this.getFormHM().put("prior_end13",prior_end13);
			this.getFormHM().put("current_start13",current_start13);
			this.getFormHM().put("period13",period13);
			this.getFormHM().put("period131",period131);
			this.getFormHM().put("period132",period132);
			this.getFormHM().put("period133",period133);
			this.getFormHM().put("period134",period134);			
			this.getFormHM().put("refer_id13",refer_id13);
			this.getFormHM().put("print_id13",print_id13);
			this.getFormHM().put("valid23",valid23);
			this.getFormHM().put("current_end23",current_end23);
			this.getFormHM().put("last_start23",last_start23);
			this.getFormHM().put("period23",period23);
			this.getFormHM().put("period231",period231);
			this.getFormHM().put("period232",period232);
			this.getFormHM().put("period233",period233);
			this.getFormHM().put("period234",period234);
			this.getFormHM().put("refer_id23",refer_id23);
			this.getFormHM().put("print_id23",print_id23);
			
			this.getFormHM().put("valid12",valid12);
			this.getFormHM().put("prior_end12",prior_end12);
			this.getFormHM().put("current_start12",current_start12);
			this.getFormHM().put("period12",period12);
			this.getFormHM().put("period121",period121);
			this.getFormHM().put("period122",period122);
			this.getFormHM().put("period123",period123);
			this.getFormHM().put("period124",period124);
			this.getFormHM().put("period125",period125);
			this.getFormHM().put("period126",period126);
			this.getFormHM().put("period127",period127);
			this.getFormHM().put("period128",period128);
			this.getFormHM().put("period129",period129);
			this.getFormHM().put("period1210",period1210);
			this.getFormHM().put("period1211",period1211);
			this.getFormHM().put("period1212",period1212);			
			this.getFormHM().put("refer_id12",refer_id12);
			this.getFormHM().put("print_id12",print_id12);
			this.getFormHM().put("valid22",valid22);
			this.getFormHM().put("current_end22",current_end22);
			this.getFormHM().put("last_start22",last_start22);
			this.getFormHM().put("period22",period22);
			this.getFormHM().put("period221",period221);
			this.getFormHM().put("period222",period222);
			this.getFormHM().put("period223",period223);
			this.getFormHM().put("period224",period224);
			this.getFormHM().put("period225",period225);
			this.getFormHM().put("period226",period226);
			this.getFormHM().put("period227",period227);
			this.getFormHM().put("period228",period228);
			this.getFormHM().put("period229",period229);
			this.getFormHM().put("period2210",period2210);
			this.getFormHM().put("period2211",period2211);
			this.getFormHM().put("period2212",period2212);
			this.getFormHM().put("refer_id22",refer_id22);
			this.getFormHM().put("print_id22",print_id22);
			
			this.getFormHM().put("valid11",valid11);
			this.getFormHM().put("prior_end11",prior_end11);
			this.getFormHM().put("current_start11",current_start11);
			this.getFormHM().put("refer_id11",refer_id11);
			this.getFormHM().put("print_id11",print_id11);
			this.getFormHM().put("valid21",valid21);
			this.getFormHM().put("current_end21",current_end21);
			this.getFormHM().put("last_start21",last_start21);
			this.getFormHM().put("refer_id21",refer_id21);
			this.getFormHM().put("print_id21",print_id21);
			
			this.getFormHM().put("valid0",valid0);
			this.getFormHM().put("current_date",current_date);
			this.getFormHM().put("time",time);
			this.getFormHM().put("limit_HH",limit_HH);
			this.getFormHM().put("limit_MM",limit_MM);
			this.getFormHM().put("refer_id0",refer_id0);
			this.getFormHM().put("print_id0",print_id0);	
			
			this.getFormHM().put("personSheetList",getPersonSheetList());
			this.getFormHM().put("sp_relationList",getSp_relationList());
			this.getFormHM().put("sp_levelList",getSp_levelList());
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	/** 
     * 取得人员库列表
     * @return
     */
	public ArrayList getDbnameList(String nbase)
	{
		ArrayList list = new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			HashMap map = new HashMap();
			if(nbase!=null && nbase.trim().length()>0)
			{
				String[] items = nbase.split(",");			
				for (int i = 0; i < items.length; i++)
				{
					map.put(items[i], "");				
				}
			}
			
			/**登录参数表 认证用户库*/
            RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
            String A01="";
            if(login_vo!=null) 
              A01 = login_vo.getString("str_value").toLowerCase();
			
			String sqlStr = "select * from dbname order by dbid";
			RowSet rowSet = dao.search(sqlStr);			
			while (rowSet.next())
			{
				String pre = rowSet.getString("pre") == null ? "" : rowSet.getString("pre");
			//	String pre = rowSet.getString("pre").toLowerCase();
				if(A01.indexOf(pre.toLowerCase())==-1)
                    continue;
								
				String dbname = rowSet.getString("dbname") == null ? "" : rowSet.getString("dbname");
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("pre", pre);
				abean.set("dbname", dbname);
				abean.set("selected", map.get(pre) == null ? "0" : "1");
				list.add(abean);
			}
			if(rowSet!=null)
				rowSet.close();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
     * 取得人员登记表列表
     * @return
     */
	public ArrayList getPersonSheetList()
	{
	
		ArrayList list = new ArrayList();
		try
		{
		    ContentDAO dao = new ContentDAO(this.frameconn);
		    String sql = "select tabid,name from rname where flagA='a' or flagA='A'";		    		    
		    RowSet rowSet = dao.search(sql);
		    CommonData vo = new CommonData("null", "请选择...");
		    list.add(vo);
		    while (rowSet.next())
		    {
				vo = new CommonData(rowSet.getString("tabid"), rowSet.getString("name"));
				list.add(vo);
		    }
		
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
	}
	
	/**
     * 审批关系列表
     * @return
     */
	public ArrayList getSp_relationList()
	{
	
		ArrayList list = new ArrayList();
		try
		{
			// 有效的，自助用户
			ContentDAO dao = new ContentDAO(this.frameconn);
		    String sql = "select relation_id,cname from t_wf_relation where actor_type='1' and validflag='1' order by seq ";		    		    
		    RowSet rowSet = dao.search(sql);
		    CommonData vo = new CommonData("null", "请选择...");
		    list.add(vo);
		    while (rowSet.next())
		    {
				vo = new CommonData(rowSet.getString("relation_id"), rowSet.getString("cname"));
				list.add(vo);
		    }
		
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return list;
	}
	
	/**
     * 审批层级列表
     * @return
     */
	public ArrayList getSp_levelList()
	{
	
		ArrayList levelList = new ArrayList();		
		try
		{															
			levelList.add(new CommonData("1", ResourceFactory.getProperty("log.workplan.oneLevelApprove")));
			levelList.add(new CommonData("2", ResourceFactory.getProperty("log.workplan.multilevelApprove")));								
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return levelList;
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
	
}