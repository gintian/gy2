package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewArrange;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class InitEmailTemplateTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap map=(HashMap)this.getFormHM().get("requestPamaHM");
		String id="";
		String dbname="";
		String isMailField="";
		String a0100="";
		String zploop="";
		String zp_pos_id="";
		String zpbatch="";
		String codeid="";
		String extendWhereSql1="";
		if(map != null){
			id=(String)map.get("id");
			dbname=(String)map.get("dbname");
			isMailField=(String)map.get("isMailField");
			a0100=(String)map.get("a0100");
			zploop=(String)map.get("zploop");
			zp_pos_id=(String)map.get("zp_pos_id");
			zpbatch=(String)map.get("zpbatch");
			zpbatch=zpbatch==null?"":zpbatch;
			map.remove("zpbatch");
			codeid=(String)map.get("codeid");
			extendWhereSql1=(String)map.get("extendWhereSql1");
		}
		ArrayList templateList = this.getTemplateList(zploop);
		ArrayList zbj_list = new ArrayList();
		ArrayList zb_list = new ArrayList();
		this.getFormHM().put("content","");
		this.getFormHM().put("title","");
		this.getFormHM().put("mailTempID","");
		this.getFormHM().put("mailTempList",templateList);
		this.getFormHM().put("zbj_list",zbj_list);
		this.getFormHM().put("zb_list",zb_list);
		this.getFormHM().put("id",id);
		this.getFormHM().put("isMailField",isMailField);
		this.getFormHM().put("dbname",dbname);
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("zploop",zploop);
		this.getFormHM().put("zp_pos_id", zp_pos_id);
		this.getFormHM().put("zpbatch", zpbatch);
		this.getFormHM().put("codeid", codeid);
		this.getFormHM().put("extendWhereSql1", extendWhereSql1);
		this.getFormHM().put("rovkeName", "");
		/**初始化发送邮件时,将flag置空**/
		this.getFormHM().put("falg", "");
	}
	
	
	
	public ArrayList getTemplateList(String zploop){
		ArrayList list = new ArrayList();
		list.add(new CommonData("","  "));
		try{
			String sql = "select template_id,name from t_sys_msgtemplate where zploop='"+zploop+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				list.add(new CommonData(this.frowset.getString("template_id"),this.frowset.getString("name")));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	

}
