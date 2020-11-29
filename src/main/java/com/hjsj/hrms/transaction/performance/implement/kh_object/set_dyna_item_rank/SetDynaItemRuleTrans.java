package com.hjsj.hrms.transaction.performance.implement.kh_object.set_dyna_item_rank;

import com.hjsj.hrms.transaction.performance.LoadXml;
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
 * <p>Title:SetDynaItemTrans.java</p>
 * <p>Description:设置动态项目权重(分值)的任务规则</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SetDynaItemRuleTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		String planid = (String) this.getFormHM().get("planid");	
		String objTypeId = (String) this.getFormHM().get("objTypeId");	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String item_id = (String) hm.get("item_id");
		hm.remove("item_id");
		
    	String maxTaskNumber = "";
    	String minTaskNumber = "";
    	String maxScoreValue = "";	
    	String minScoreValue = "";
    	String canshow="";
    	String flag="0";
    	String scope="";
    	String to_scope="";
	 	ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			String sql = "select * from per_dyna_item where plan_id="+planid+" and body_id="+objTypeId+" and Item_id="+item_id;
			this.frowset = dao.search(sql);
			if(this.frowset.next())			
			{
				String rule = Sql_switcher.readMemo(this.frowset, "Task_rule");
				
				if (rule == null || (rule != null && "".equals(rule)))
				{

				} else
				{
				    Document doc = PubFunc.generateDom(rule);
				    String xpath = "//Task";
				    XPath xpath_ = XPath.newInstance(xpath);
				    Element ele = (Element) xpath_.selectSingleNode(doc);
				    Element child;
				    if (ele != null)
				    {			    	
				    	child = ele.getChild("TaskNumber");
				    	if (child != null)
				    	{
				    		maxTaskNumber = child.getAttributeValue("MaxCount");
				    		minTaskNumber = child.getAttributeValue("MinCount");
				    	}
				    	child = ele.getChild("TaskScore");
				    	if (child != null)	
				    	{
				    		maxScoreValue = child.getAttributeValue("MaxValue");
				    		minScoreValue = child.getAttributeValue("MinValue");
				    	}
				    	child = ele.getChild("AddMinusScore");
				    	if (child != null)	
				    	{
				    		flag = child.getAttributeValue("flag");
				    		scope = child.getAttributeValue("scope");
				    		if((child.getAttributeValue("f_scope")!=null) || (child.getAttributeValue("t_scope")!=null))
				    		{
				    			scope=child.getAttributeValue("f_scope");
				    			to_scope=child.getAttributeValue("t_scope");
				    		}else
				    		{
				    			to_scope=scope;
				    			scope="-"+scope;
				    		}
				    	}
				    }
				    
				}				
			}//dml 2011年9月14日10:45:13
			LoadXml loadxml = new LoadXml(this.getFrameconn(), planid);
			Hashtable params = loadxml.getDegreeWhole();
			String scoreflag=(String)params.get("scoreflag");
			String scoreFromItem=(String)params.get("ScoreFromItem");
			if(("1".equals(scoreflag)|| "2".equalsIgnoreCase(scoreflag))&&(scoreFromItem==null||scoreFromItem.trim().length()==0||!"True".equalsIgnoreCase(scoreFromItem))){
				canshow="true";
			}else{
				canshow="false";
			}
			this.getFormHM().put("minTaskCount", minTaskNumber);
			this.getFormHM().put("maxTaskCount", maxTaskNumber);
			this.getFormHM().put("maxScore", maxScoreValue);
			this.getFormHM().put("minScore", minScoreValue);
			this.getFormHM().put("flag", flag);
			this.getFormHM().put("scope", scope);
			this.getFormHM().put("canshow", canshow);
			this.getFormHM().put("to_scope", to_scope);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
}
