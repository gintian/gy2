package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:InitMessageTrans.java</p>
 * <p>Description>:初始化发送短信和邮件</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 18, 2010 11:11:11 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class InitMessageTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt="1"; // 1:目标卡状态  2：打分状态 主体  3: 目标执行（回顾情况） 4：目标执行情况
			if(hm.get("opt")!=null && ((String)hm.get("opt")).length()>0)
				opt=(String)hm.get("opt");
			String content="";
			String subject="";
			AnalysePlanParameterBo bo=new AnalysePlanParameterBo(this.getFrameconn());
			Hashtable ht_table=bo.analyseParameterXml();
		    String creatCard_mail="";
		    String creatCard_mail_template="";
		    String evaluateCard_mail="";
		    String evaluateCard_mail_template="";
			if(ht_table!=null)
			{
				if(ht_table.get("creatCard_mail")!=null)
					creatCard_mail=(String)ht_table.get("creatCard_mail");
				if(ht_table.get("creatCard_mail_template")!=null)
					creatCard_mail_template=(String)ht_table.get("creatCard_mail_template");
				if(ht_table.get("evaluateCard_mail")!=null)
					evaluateCard_mail=(String)ht_table.get("evaluateCard_mail");
				if(ht_table.get("evaluateCard_mail_template")!=null)
					evaluateCard_mail_template=(String)ht_table.get("evaluateCard_mail_template");
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			if("1".equals(opt) && creatCard_mail_template!="-1" && creatCard_mail_template.length()>0)
			{ 
				this.frowset=dao.search("select * from email_name where id="+creatCard_mail_template);
				if(this.frowset.next())
				{
					subject=this.frowset.getString("subject");
					content=Sql_switcher.readMemo(this.frowset,"content"); 
				}
				
			}
			else if("2".equals(opt) && evaluateCard_mail_template!="-1" && evaluateCard_mail_template.length()>0)
			{
				this.frowset=dao.search("select * from email_name where id="+evaluateCard_mail_template);
				if(this.frowset.next())
				{
					subject=this.frowset.getString("subject");
					content=Sql_switcher.readMemo(this.frowset,"content"); 
				}
				
			}else if("3".equals(opt) || "4".equals(opt))
			{
				this.frowset = dao.search("select str_value from constant where constant='PER_PERFORMCASE'");
			    if ( this.frowset.next())
			    {
			    	String str_value = this.frowset.getString("str_value");
			    	if (str_value == null || (str_value != null && "".equals(str_value)))
			    	{
		
			    	} else
			    	{
			    		Document doc = PubFunc.generateDom(str_value);
			    		String xpath = "//Per_Performcase";
			    		XPath xpath_ = XPath.newInstance(xpath);
			    		Element ele = (Element) xpath_.selectSingleNode(doc);
			    		Element child;
			    		if (ele != null)
			    		{		
			    			child = ele.getChild("Title");
							if (child != null)			
								subject = child.getTextTrim();
							child = ele.getChild("EmailDistri");
							if (child != null)			
								content = child.getTextTrim();
//			    			subject = ele.getAttributeValue("Title");						    
//			    			content = ele.getAttributeValue("EmailDistri");			    			
			    		}
			    	}
			    }				
			}else if("5".equals(opt)){
				
			}
			this.getFormHM().put("subject",subject);
			this.getFormHM().put("content",content);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
