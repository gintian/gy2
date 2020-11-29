package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchKqRuleDataTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String rule_id=(String)this.getFormHM().get("rule_id");		
		StringBuffer sql=new StringBuffer();
		sql.append("select rule_id,rule_name,machine_s,machine_e,card_s,card_e,");
		sql.append("year_s,year_e,md_s,md_e,hm_s,hm_e,status from kq_data_rule");
		if(rule_id!=null&&rule_id.length()>0)
		{
			sql.append(" where rule_id='"+rule_id+"'");
		}
		sql.append(" order by rule_id");
		RecordVo kq_rule_vo=new RecordVo("kq_data_rule");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		
	    try
	    {
	    	this.frowset=dao.search(sql.toString());
	    	if(this.frowset.next())
	    	{
	    		kq_rule_vo.setString("rule_name",this.frowset.getString("rule_name"));
	    		kq_rule_vo.setString("machine_s",this.frowset.getString("machine_s"));
	    		kq_rule_vo.setString("machine_e",this.frowset.getString("machine_e"));
	    		kq_rule_vo.setString("card_s",this.frowset.getString("card_s"));
	    		kq_rule_vo.setString("card_e",this.frowset.getString("card_e"));
	    		kq_rule_vo.setString("year_s",this.frowset.getString("year_s"));
	    		kq_rule_vo.setString("year_e",this.frowset.getString("year_e"));
	    		kq_rule_vo.setString("md_s",this.frowset.getString("md_s"));
	    		kq_rule_vo.setString("md_e",this.frowset.getString("md_e"));
	    		kq_rule_vo.setString("hm_s",this.frowset.getString("hm_s"));
	    		kq_rule_vo.setString("hm_e",this.frowset.getString("hm_e"));
	    		kq_rule_vo.setString("status",this.frowset.getString("status"));
	    		rule_id=this.frowset.getString("rule_id");
	    		kq_rule_vo.setString("rule_id",rule_id);
	    	}
	    }catch(Exception e)
	    {
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.work.error"),"",""));
	    }
	    this.getFormHM().put("kq_rule_vo",kq_rule_vo);
	    this.getFormHM().put("rule_id",rule_id);
	}

}
