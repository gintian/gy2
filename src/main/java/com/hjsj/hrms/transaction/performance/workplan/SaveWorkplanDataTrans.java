package com.hjsj.hrms.transaction.performance.workplan;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * <p>Title:SaveWorkplanDataTrans.java</p>
 * <p>Description:工作计划参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-06-15 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class SaveWorkplanDataTrans extends IBusiness 
{

	Document a_doc = null;
	
	public void execute() throws GeneralException 
	{
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement ps = null;
		String str_value = "";
		ElementWork(); // 获得xml节点信息
		try
		{
			
			String sp_relation = (String)this.getFormHM().get("sp_relation");
			String sp_level = (String)this.getFormHM().get("sp_level");
			String record_grade = (String)this.getFormHM().get("record_grade");
			String defaultLines = (String)this.getFormHM().get("defaultLines");
			if(defaultLines==null || defaultLines.trim().length()<=0 || "0".equalsIgnoreCase(defaultLines))
				defaultLines = "12";
			String dailyPlan_attachment = (String)this.getFormHM().get("dailyPlan_attachment");
			String dailySumm_attachment = (String)this.getFormHM().get("dailySumm_attachment");
			String planTarget = (String)this.getFormHM().get("planTarget");			
			String summTarget = (String)this.getFormHM().get("summTarget");			
			String nbase = (String)this.getFormHM().get("nbase");	
			if(nbase!=null && nbase.trim().length()>0)
				nbase = nbase.substring(0,nbase.length()-1);
			
			// 年报 工作计划参数
			String valid14 = (String)this.getFormHM().get("valid14");
			String prior_end14 = (String)this.getFormHM().get("prior_end14");
			String current_start14 = (String)this.getFormHM().get("current_start14");
			String refer_id14 = (String)this.getFormHM().get("refer_id14");
			String print_id14 = (String)this.getFormHM().get("print_id14");
			// 年报 工作总结参数
			String valid24 = (String)this.getFormHM().get("valid24");
			String current_end24 = (String)this.getFormHM().get("current_end24");
			String last_start24 = (String)this.getFormHM().get("last_start24");
			String refer_id24 = (String)this.getFormHM().get("refer_id24");
			String print_id24 = (String)this.getFormHM().get("print_id24");
			
			
			// 季报 工作计划参数
			String valid13 = (String)this.getFormHM().get("valid13");
			String prior_end13 = (String)this.getFormHM().get("prior_end13");
			String current_start13 = (String)this.getFormHM().get("current_start13");
			String period13 = "";
			String period131 = (String)this.getFormHM().get("period131");
		    String period132 = (String)this.getFormHM().get("period132");
		    String period133 = (String)this.getFormHM().get("period133");
		    String period134 = (String)this.getFormHM().get("period134");
		    if("True".equalsIgnoreCase(zeroToFalse(period131)))
		    	period13 += ",1";
			if("True".equalsIgnoreCase(zeroToFalse(period132)))
				period13 += ",2";
			if("True".equalsIgnoreCase(zeroToFalse(period133)))
				period13 += ",3";
			if("True".equalsIgnoreCase(zeroToFalse(period134)))
				period13 += ",4";
			if(period13!=null && period13.trim().length()>0)
				period13 = period13.substring(1);
			String refer_id13 = (String)this.getFormHM().get("refer_id13");
			String print_id13 = (String)this.getFormHM().get("print_id13");
			// 季报 工作总结参数
			String valid23 = (String)this.getFormHM().get("valid23");
			String current_end23 = (String)this.getFormHM().get("current_end23");
			String last_start23 = (String)this.getFormHM().get("last_start23");
			String period23 = "";
			String period231 = (String)this.getFormHM().get("period231");
		    String period232 = (String)this.getFormHM().get("period232");
		    String period233 = (String)this.getFormHM().get("period233");
		    String period234 = (String)this.getFormHM().get("period234");
		    if("True".equalsIgnoreCase(zeroToFalse(period231)))
		    	period23 += ",1";
			if("True".equalsIgnoreCase(zeroToFalse(period232)))
				period23 += ",2";
			if("True".equalsIgnoreCase(zeroToFalse(period233)))
				period23 += ",3";
			if("True".equalsIgnoreCase(zeroToFalse(period234)))
				period23 += ",4";
			if(period23!=null && period23.trim().length()>0)
				period23 = period23.substring(1);
			String refer_id23 = (String)this.getFormHM().get("refer_id23");
			String print_id23 = (String)this.getFormHM().get("print_id23");
			
			
			// 月报 工作计划参数
			String valid12 = (String)this.getFormHM().get("valid12");
			String prior_end12 = (String)this.getFormHM().get("prior_end12");
			String current_start12 = (String)this.getFormHM().get("current_start12");
			String period12 = "";
			String period121 = (String)this.getFormHM().get("period121");
		    String period122 = (String)this.getFormHM().get("period122");
		    String period123 = (String)this.getFormHM().get("period123");
		    String period124 = (String)this.getFormHM().get("period124");
		    String period125 = (String)this.getFormHM().get("period125");
		    String period126 = (String)this.getFormHM().get("period126");
		    String period127 = (String)this.getFormHM().get("period127");
		    String period128 = (String)this.getFormHM().get("period128");
		    String period129 = (String)this.getFormHM().get("period129");
		    String period1210 = (String)this.getFormHM().get("period1210");
		    String period1211 = (String)this.getFormHM().get("period1211");
		    String period1212 = (String)this.getFormHM().get("period1212");
		    if("True".equalsIgnoreCase(zeroToFalse(period121)))
		    	period12 += ",1";
			if("True".equalsIgnoreCase(zeroToFalse(period122)))
				period12 += ",2";
			if("True".equalsIgnoreCase(zeroToFalse(period123)))
				period12 += ",3";
			if("True".equalsIgnoreCase(zeroToFalse(period124)))
				period12 += ",4";
			if("True".equalsIgnoreCase(zeroToFalse(period125)))
		    	period12 += ",5";
			if("True".equalsIgnoreCase(zeroToFalse(period126)))
				period12 += ",6";
			if("True".equalsIgnoreCase(zeroToFalse(period127)))
				period12 += ",7";
			if("True".equalsIgnoreCase(zeroToFalse(period128)))
				period12 += ",8";
			if("True".equalsIgnoreCase(zeroToFalse(period129)))
		    	period12 += ",9";
			if("True".equalsIgnoreCase(zeroToFalse(period1210)))
				period12 += ",10";
			if("True".equalsIgnoreCase(zeroToFalse(period1211)))
				period12 += ",11";
			if("True".equalsIgnoreCase(zeroToFalse(period1212)))
				period12 += ",12";
			if(period12!=null && period12.trim().length()>0)
				period12 = period12.substring(1);
			String refer_id12 = (String)this.getFormHM().get("refer_id12");
			String print_id12 = (String)this.getFormHM().get("print_id12");
			// 月报 工作总结参数
			String valid22 = (String)this.getFormHM().get("valid22");
			String current_end22 = (String)this.getFormHM().get("current_end22");
			String last_start22 = (String)this.getFormHM().get("last_start22");
			String period22 = "";
			String period221 = (String)this.getFormHM().get("period221");
		    String period222 = (String)this.getFormHM().get("period222");
		    String period223 = (String)this.getFormHM().get("period223");
		    String period224 = (String)this.getFormHM().get("period224");
		    String period225 = (String)this.getFormHM().get("period225");
		    String period226 = (String)this.getFormHM().get("period226");
		    String period227 = (String)this.getFormHM().get("period227");
		    String period228 = (String)this.getFormHM().get("period228");
		    String period229 = (String)this.getFormHM().get("period229");
		    String period2210 = (String)this.getFormHM().get("period2210");
		    String period2211 = (String)this.getFormHM().get("period2211");
		    String period2212 = (String)this.getFormHM().get("period2212");
		    if("True".equalsIgnoreCase(zeroToFalse(period221)))
		    	period22 += ",1";
			if("True".equalsIgnoreCase(zeroToFalse(period222)))
				period22 += ",2";
			if("True".equalsIgnoreCase(zeroToFalse(period223)))
				period22 += ",3";
			if("True".equalsIgnoreCase(zeroToFalse(period224)))
				period22 += ",4";
			if("True".equalsIgnoreCase(zeroToFalse(period225)))
		    	period22 += ",5";
			if("True".equalsIgnoreCase(zeroToFalse(period226)))
				period22 += ",6";
			if("True".equalsIgnoreCase(zeroToFalse(period227)))
				period22 += ",7";
			if("True".equalsIgnoreCase(zeroToFalse(period228)))
				period22 += ",8";
			if("True".equalsIgnoreCase(zeroToFalse(period229)))
		    	period22 += ",9";
			if("True".equalsIgnoreCase(zeroToFalse(period2210)))
				period22 += ",10";
			if("True".equalsIgnoreCase(zeroToFalse(period2211)))
				period22 += ",11";
			if("True".equalsIgnoreCase(zeroToFalse(period2212)))
				period22 += ",12";
			if(period22!=null && period22.trim().length()>0)
				period22 = period22.substring(1);
			String refer_id22 = (String)this.getFormHM().get("refer_id22");
			String print_id22 = (String)this.getFormHM().get("print_id22");
			
			
			// 周报 工作计划参数
			String valid11 = (String)this.getFormHM().get("valid11");
			String prior_end11 = (String)this.getFormHM().get("prior_end11");
			String current_start11 = (String)this.getFormHM().get("current_start11");
			String refer_id11 = (String)this.getFormHM().get("refer_id11");
			String print_id11 = (String)this.getFormHM().get("print_id11");
			// 周报 工作总结参数
			String valid21 = (String)this.getFormHM().get("valid21");
			String current_end21 = (String)this.getFormHM().get("current_end21");
			String last_start21 = (String)this.getFormHM().get("last_start21");
			String refer_id21 = (String)this.getFormHM().get("refer_id21");
			String print_id21 = (String)this.getFormHM().get("print_id21");
			
			// 日报 工作计划参数
			String valid0 = (String)this.getFormHM().get("valid0");
			String current_date = (String)this.getFormHM().get("current_date");
//			String time = (String)this.getFormHM().get("time");
			String limit_HH = (String)this.getFormHM().get("limit_HH");
			if(limit_HH==null || limit_HH.trim().length()<=0)
				limit_HH = "00";
			String limit_MM = (String)this.getFormHM().get("limit_MM");
			if(limit_MM==null || limit_MM.trim().length()<=0)
				limit_MM = "00";
			String refer_id0 = (String)this.getFormHM().get("refer_id0");
			String print_id0 = (String)this.getFormHM().get("print_id0");
			
			
						
			Element root = null;
			Element plannode = null;
			if(this.a_doc==null)
			{
				root = new Element("Per_Parameters");
				Element ele = new Element("work_records");
	        	ele.setAttribute("sp_relation", sp_relation);
	        	ele.setAttribute("sp_level", sp_level);
	        	ele.setAttribute("record_grade", zeroToFalse(record_grade));
	        	ele.setAttribute("defaultLines", defaultLines);
	        	ele.setAttribute("dailyPlan_attachment", zeroToFalse(dailyPlan_attachment));
	        	ele.setAttribute("dailySumm_attachment", zeroToFalse(dailySumm_attachment));
	        	ele.setAttribute("planTarget", planTarget);
	        	ele.setAttribute("summTarget", summTarget);
	        	ele.setAttribute("nbase", nbase);
	        	root.addContent(ele);
	        	plannode = ele;	        							
			}
			else
			{
				root = this.a_doc.getRootElement();
				plannode = root.getChild("work_records");
			}
	        if(plannode==null)
	        {       		
	        	Element ele = new Element("work_records");
	        	ele.setAttribute("sp_relation", sp_relation);
	        	ele.setAttribute("sp_level", sp_level);
	        	ele.setAttribute("record_grade", zeroToFalse(record_grade));
	        	ele.setAttribute("defaultLines", defaultLines);
	        	ele.setAttribute("dailyPlan_attachment", zeroToFalse(dailyPlan_attachment));
	        	ele.setAttribute("dailySumm_attachment", zeroToFalse(dailySumm_attachment));
	        	ele.setAttribute("planTarget", planTarget);
	        	ele.setAttribute("summTarget", summTarget);
	        	ele.setAttribute("nbase", nbase);
	        	root.addContent(ele);
	        	plannode = ele;
	        }else
	        {
	        	// 删除节点 work_records
	        	root.removeChildren("work_records");
	        	Element ele = new Element("work_records");
	        	ele.setAttribute("sp_relation", sp_relation);
	        	ele.setAttribute("sp_level", sp_level);
	        	ele.setAttribute("record_grade", zeroToFalse(record_grade));
	        	ele.setAttribute("defaultLines", defaultLines);
	        	ele.setAttribute("dailyPlan_attachment", zeroToFalse(dailyPlan_attachment));
	        	ele.setAttribute("dailySumm_attachment", zeroToFalse(dailySumm_attachment));
	        	ele.setAttribute("planTarget", planTarget);
	        	ele.setAttribute("summTarget", summTarget);
	        	ele.setAttribute("nbase", nbase);
	        	root.addContent(ele);
	        	plannode = ele;
	        }
	        	
	        for (int i = 0; i <= 4; i++)
	    	{
		        Element Rplan = new Element("work_record");
		        Rplan.setAttribute("cycle", String.valueOf(i));
	        	
		        if(i==0)
		        {
		        	Element temp = new Element("template");
			        temp.setAttribute("type", "0");
			        temp.setAttribute("valid", falseToZero(valid0));
			        temp.setAttribute("current_date", current_date);
			        temp.setAttribute("time", limit_HH+":"+limit_MM);
			        temp.setAttribute("refer_id", isNull(refer_id0));
			        temp.setAttribute("print_id", isNull(print_id0));	
			        Rplan.addContent(temp);
			        	
		        }else if(i==1)
		        {
				    for (int j = 1; j <= 2; j++)
				    {
				    	Element temp = new Element("template");
					    temp.setAttribute("type", String.valueOf(j));
					    if(j==1)
					    {
					    	temp.setAttribute("valid", falseToZero(valid11));
						    temp.setAttribute("prior_end", prior_end11);
						    temp.setAttribute("current_start", current_start11);
						    temp.setAttribute("refer_id", isNull(refer_id11));
						    temp.setAttribute("print_id", isNull(print_id11));
						    Rplan.addContent(temp);
					    }else
					    {
					        temp.setAttribute("valid", falseToZero(valid21));
						    temp.setAttribute("current_end", current_end21);
						    temp.setAttribute("last_start", last_start21);
						    temp.setAttribute("refer_id", isNull(refer_id21));
						    temp.setAttribute("print_id", isNull(print_id21));
						    Rplan.addContent(temp);
					    }
				    }		        		
		        }else if(i==2)
		        {
				    for (int j = 1; j <= 2; j++)
				    {
					    Element temp = new Element("template");
					    temp.setAttribute("type", String.valueOf(j));
					    if(j==1)
					    {
						    temp.setAttribute("valid", falseToZero(valid12));
						    temp.setAttribute("prior_end", prior_end12);
						    temp.setAttribute("current_start", current_start12);
						    temp.setAttribute("period", period12);
						    temp.setAttribute("refer_id", isNull(refer_id12));
						    temp.setAttribute("print_id", isNull(print_id12));
						    Rplan.addContent(temp);
					    }else
					    {
					        temp.setAttribute("valid", falseToZero(valid22));
						    temp.setAttribute("current_end", current_end22);
						    temp.setAttribute("last_start", last_start22);
						    temp.setAttribute("period", period22);
						    temp.setAttribute("refer_id", isNull(refer_id22));
						    temp.setAttribute("print_id", isNull(print_id22));
						    Rplan.addContent(temp);
					    }
				    }		        		
		        }else if(i==3)
		        {
				    for (int j = 1; j <= 2; j++)
				    {
					    Element temp = new Element("template");
					    temp.setAttribute("type", String.valueOf(j));
					    if(j==1)
					    {
						    temp.setAttribute("valid", falseToZero(valid13));
						    temp.setAttribute("prior_end", prior_end13);
						    temp.setAttribute("current_start", current_start13);
						    temp.setAttribute("period", period13);
						    temp.setAttribute("refer_id", isNull(refer_id13));
						    temp.setAttribute("print_id", isNull(print_id13));
						    Rplan.addContent(temp);
					    }else
					    {
					        temp.setAttribute("valid", falseToZero(valid23));
						    temp.setAttribute("current_end", current_end23);
						    temp.setAttribute("last_start", last_start23);
						    temp.setAttribute("period", period23);
						    temp.setAttribute("refer_id", isNull(refer_id23));
						    temp.setAttribute("print_id", isNull(print_id23));
						    Rplan.addContent(temp);
					    }
				    }		        		
		        }else if(i==4)
		        {
				    for (int j = 1; j <= 2; j++)
				    {
					    Element temp = new Element("template");
					    temp.setAttribute("type", String.valueOf(j));
					    if(j==1)
					    {
						    temp.setAttribute("valid", falseToZero(valid14));
						    temp.setAttribute("prior_end", prior_end14);
						    temp.setAttribute("current_start", current_start14);
						    temp.setAttribute("refer_id", isNull(refer_id14));
						    temp.setAttribute("print_id", isNull(print_id14));
						    Rplan.addContent(temp);
					    }else
					    {
					        temp.setAttribute("valid", falseToZero(valid24));
						    temp.setAttribute("current_end", current_end24);
						    temp.setAttribute("last_start", last_start24);
						    temp.setAttribute("refer_id", isNull(refer_id24));
						    temp.setAttribute("print_id", isNull(print_id24));
						    Rplan.addContent(temp);
					    }
				    }		        		
		        }		        	
		        plannode.addContent(Rplan);
	    	}	        		        	

	    	if(this.a_doc==null)
			{
				Document myDocument = new Document(root);
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				str_value = outputter.outputString(myDocument);
			}
			else
			{
				XMLOutputter outputter=new XMLOutputter();
		    	Format format=Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				str_value = outputter.outputString(this.a_doc);
			}	    				
			
	    	ifNoParameterInsert("PER_PARAMETERS"); // 如果数据库中没有这个名称记录则插入
	    	String sql = "update constant set str_value=? where constant='PER_PARAMETERS'";
			ps = this.getFrameconn().prepareStatement(sql);	
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
					ps.setString(1, str_value);
					break;
				case Constant.ORACEL:
					ps.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					break;
				case Constant.DB2:
					ps.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					break;
			}
			// 打开Wallet
			dbS.open(this.getFrameconn(),sql);
			ps.executeUpdate();						
			
	//		System.out.println("======"+prior_end14+"&&&"+last_start14);			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(ps);
			try {
				// 关闭Wallet
				dbS.close(this.getFrameconn());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 如果数据库中没有这个名称记录则插入
	 * @param param_name  
	 */
	public void ifNoParameterInsert(String param_name)
	{
		String sql="select * from constant where UPPER(Constant)='"+param_name.toUpperCase()+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs=null;
		try{
			rs=dao.search(sql);		  
			if(!rs.next())
			{
				insertNewParameter(param_name);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}		 
	}
	/**
	 * 插入
	 * @param param_name
	 */
	public void insertNewParameter(String param_name)
	{
		String insert="insert into constant(Constant) values (?)";
		ArrayList list=new ArrayList();
		list.add(param_name.toUpperCase());			
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try{
			dao.insert(insert,list);		    
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public void ElementWork()
    {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select str_value from constant where constant='PER_PARAMETERS'";
		ResultSet rs = null;
		try
		{
		    rs = dao.search(sql);
		    String xmlContext = "";
	
		    if (rs.next())
		    {
		    	xmlContext = Sql_switcher.readMemo(rs, "str_value"); // PubFunc.nullToStr(rs.getString("parameter_content"));
		    }
	
		    if (xmlContext!=null && xmlContext.trim().length()>0 && !"".equals(xmlContext.trim()))
		    {
				this.a_doc = PubFunc.generateDom(xmlContext);
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
    }
	
	public String zeroToFalse(String str)
	{
		if ("0".equals(str))
			return "False";
		else if ("1".equals(str))
			return "True";
		else
			return str;
	}
	public String falseToZero(String str)
	{
		if ("False".equalsIgnoreCase(str))
			return "0";
		else if ("True".equalsIgnoreCase(str))
			return "1";
		else
			return str;
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "null".equalsIgnoreCase(str))
		    str = "";
		return str;
    }
	
}