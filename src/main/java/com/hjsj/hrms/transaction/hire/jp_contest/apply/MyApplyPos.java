package com.hjsj.hrms.transaction.hire.jp_contest.apply;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 
 *<p>Title:MyApplyPos.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 25, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class MyApplyPos extends IBusiness {
	
	public void execute() throws GeneralException {
		
		try 
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList allApplyPos = this.getAllApplyPosId(dao);			
			this.getFormHM().put("allApplyPos",allApplyPos);
//			String applyhtml = this.getApplyPosId(dao);
//			this.getFormHM().put("applyhtml",applyhtml);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayList getAllApplyPosId(ContentDAO dao)
	{
		ArrayList allApplyPos = new ArrayList();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select z0700,z0701 from z07 ");
		sql.append("  where z0700 in (select z0700 from zp_apply_jobs ");
		sql.append(" where a0100='"+this.userView.getA0100()+"')");	
//		System.out.println(sql.toString());
		StringBuffer html = new StringBuffer();
		int i =1;
		try 
		{
			this.frowset = dao.search(sql.toString().toUpperCase());
			while(this.frowset.next())
			{
				String z0700 = "";
				String z0701 = "";
				z0700 = this.frowset.getString("Z0700");
				z0701 = this.frowset.getString("Z0701");
				String postion = "";
				if(z0701 !=null && z0701.trim().length()>0)
					postion=AdminCode.getCode("@K",z0701)!=null?AdminCode.getCode("@K",z0701).getCodename():z0701;
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("z0700",z0700);
				abean.set("postion",postion);
				allApplyPos.add(abean);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return allApplyPos;
	}

	
	public String getApplyPosId(ContentDAO dao)
	{
		ArrayList allApplyPos = new ArrayList();
		String z0700 = "";
		StringBuffer sql = new StringBuffer();
		sql.append(" select z0700 from zp_apply_jobs ");
		sql.append("  where ");
		sql.append(" a0100='"+this.userView.getA0100()+"'");
		StringBuffer html = new StringBuffer();
		int i =1;
		try 
		{
			html.append("<hrms:tabset name=\"sys_param\" width=\"550\" height=\"450\" type=\"true\">");
			this.frowset = dao.search(sql.toString().toUpperCase());
			if(this.frowset.next())
			{
				z0700 = this.frowset.getInt("Z0700")+"";
				String postion = "";
				if(z0700 !=null && z0700.trim().length()>0)
					postion=AdminCode.getCode("@K",z0700)!=null?AdminCode.getCode("@K",z0700).getCodename():z0700;
	           //<hrms:tabset name="sys_param" width="550" height="450" type="true"> 
				html.append("<hrms:tab name=\"param"+i+"\" label=\""+postion+"\" visible=\"true\"");	
				html.append(" url=\"/hire/jp_contest/apply/apply_jp_pos.do?b_search=link\">");	
				html.append("</hrms:tab>");	
//				CommonData cd = new CommonData(z0700,postion);	
//				allApplyPos.add(cd);
				i++;
			}
			html.append("</hrms:tabset>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return html.toString();
	}

}