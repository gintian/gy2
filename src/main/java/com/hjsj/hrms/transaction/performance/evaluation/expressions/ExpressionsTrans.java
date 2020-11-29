package com.hjsj.hrms.transaction.performance.evaluation.expressions;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:ExpressionsTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jun 21, 2008:5:16:34 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ExpressionsTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String planid = (String)this.getFormHM().get("planid");
		LoadXml loadxml = new LoadXml(this.frameconn,planid,"");
		ArrayList planlist = loadxml.getRelatePlanValue("Plan","ID");
		ArrayList exprrelatelist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from per_plan ");
		if(planlist.size()>0){
			sql.append(" where plan_id in (");
			for(int i=0;i<planlist.size();i++){
				sql.append("'"+planlist.get(i).toString().trim()+"',");
			}
			sql.setLength(sql.length()-1);
			sql.append(")");
			try {
				this.frowset = dao.search(sql.toString());
				while(frowset.next()){
					CommonData data = new CommonData("["+this.frowset.getString("name")+"]","(计划"+this.frowset.getString("plan_id")+")"+this.frowset.getString("name"));
					exprrelatelist.add(data);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		this.getFormHM().put("exprrelatelist",exprrelatelist);
		ArrayList formulalist = loadxml.getRelatePlanValue("Formula","Caption");
		String formula = "";
		if(formulalist.size()>0)
			formula = formulalist.get(0).toString();
		else
			formula = "[本次得分]";
		this.getFormHM().put("formula",formula);
		//System.out.println("1111");
	}

}
