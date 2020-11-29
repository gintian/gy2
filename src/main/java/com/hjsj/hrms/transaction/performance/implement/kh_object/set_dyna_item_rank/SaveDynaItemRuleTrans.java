package com.hjsj.hrms.transaction.performance.implement.kh_object.set_dyna_item_rank;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveDynaItemRuleTrans.java</p>
 * <p>Description:保存动态项目权重(分值)的任务规则</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveDynaItemRuleTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		String planid = (String) this.getFormHM().get("planid");
		String objTypeId = (String) this.getFormHM().get("objTypeId");
		String minTaskCount = (String) this.getFormHM().get("minTaskCount");
		String maxTaskCount = (String) this.getFormHM().get("maxTaskCount");
		String maxScore = (String) this.getFormHM().get("maxScore");
		String minScore = (String) this.getFormHM().get("minScore");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String item_id = (String) hm.get("item_id");
		hm.remove("item_id");
		String flag = (String) this.getFormHM().get("flag");
		String scope = (String) this.getFormHM().get("scope");
		String to_scope=(String)this.getFormHM().get("to_scope");
		Element root = new Element("Task");
		Element child = new Element("TaskNumber");
		child.setAttribute("MaxCount", maxTaskCount);
		child.setAttribute("MinCount", minTaskCount);
		root.addContent(child);

		child = new Element("TaskScore");
		child.setAttribute("MaxValue", maxScore);
		child.setAttribute("MinValue", minScore);
		root.addContent(child);
		if(flag!=null&&flag.trim().length()!=0){
			child = new Element("AddMinusScore");
			child.setAttribute("flag", flag);
			child.setAttribute("f_scope", scope);
			child.setAttribute("t_scope",to_scope);
			root.addContent(child);
		}
		Document myDocument = new Document(root);
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		try
		{

			ContentDAO dao = new ContentDAO(this.frameconn);
//			if (!(minTaskCount + maxTaskCount + maxScore + minScore+scope+to_scope).equals(""))
//			{
				String task_rule = outputter.outputString(myDocument);
				String sql = "update per_dyna_item set task_rule=? where plan_id=" + planid + " and body_id=" + objTypeId + " and Item_id=" + item_id;
				ArrayList list = new ArrayList();
				list.add(task_rule);
				dao.update(sql, list);
//			} else
			{
//				dao.update("update per_dyna_item set task_rule=null where plan_id=" + planid + " and body_id=" + objTypeId + " and Item_id=" + item_id);
			}

		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
