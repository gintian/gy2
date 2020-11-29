package com.hjsj.hrms.transaction.performance.evaluation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchImportDataOptionTrans</p>
 * <p>Description:查询引入考核计划数据选项</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:Jan 25, 2010</p> 
 * @author JinChunhai
 * @version 4.2
 */

public class SearchImportDataOptionTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String relaPlan = (String) hm.get("relaPlan");
		hm.remove("relaPlan");
//		
//		String planid = (String)hm.get("planid");
//		hm.remove("planid");
//		
//		LoadXml loadxml = new LoadXml(this.frameconn, planid, "");
//		String planMenus = loadxml.getRelatePlanMenuValue(relaPlan);
		
		
		String planMenus = (String) hm.get("planMenu");
		hm.remove("planMenu");
		
		String isScore = "0";
		String isGrpAvg = "0";
		String isorder = "0";
		String isGrade = "0";
		String isXiShu = "0";
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		if (!"".equals(planMenus))
		{
			String[] menus = planMenus.split(",");
			for (int i = 0; i < menus.length; i++)
			{
				String menu = menus[i];
				if ("Score".equalsIgnoreCase(menu))
					isScore = "1";
				else if ("Order".equalsIgnoreCase(menu))
					isorder = "1";
				else if ("Grade".equalsIgnoreCase(menu))
					isGrade = "1";
				else if ("Avg".equalsIgnoreCase(menu))
					isGrpAvg = "1";
				else if ("XiShu".equalsIgnoreCase(menu))
					isXiShu = "1";
				else if ("Body".equalsIgnoreCase(menu.substring(0, 4)))
				{
					map.put(menu.substring(4), "");
				}
			}
		}

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			String strSql = "select a.body_id,b.name from per_plan_body a,per_mainbodyset b where a.body_id=b.body_id and a.plan_id=" + relaPlan;
			this.frowset = dao.search(strSql);
			while (this.frowset.next())
			{
				String bodyid = this.frowset.getString("body_id");
				String name = this.frowset.getString("name");
				String selected = "0";
				if (map.get(bodyid) != null)
					selected = "1";
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("body_id", bodyid);
				abean.set("selected", selected);
				abean.set("name", name);
				list.add(abean);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		if("0".equals(isGrpAvg) && "0".equals(isorder) && "0".equals(isGrade) && "0".equals(isXiShu))
			isScore="1";		
		
		this.getFormHM().put("isScore", isScore);
		this.getFormHM().put("isGrpAvg", isGrpAvg);
		this.getFormHM().put("isorder", isorder);
		this.getFormHM().put("isGrade", isGrade);
		this.getFormHM().put("isXiShu", isXiShu);
		this.getFormHM().put("bodyTypeList", list);
	}
	
}
