package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

public class InitReviewExamPlanTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String planId=(String)hm.get("planId");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
        if(!_bo.isHavePriv(this.userView, planId)){	
        	return;
        } 
        hm.remove("planId");
        
        RecordVo vo = new RecordVo("per_plan");		  
        ContentDAO dao = new ContentDAO(this.getFrameconn());

        StringBuffer strsql = new StringBuffer();
        strsql.append("select * from per_plan where plan_id=");
        strsql.append(planId);

	try
	{
	    this.frowset = dao.search(strsql.toString());
	    if (this.frowset.next()) {
		vo.setString("plan_id", planId);
		vo.setString("agree_idea", this.frowset.getString("agree_idea"));
		
		vo.setString("status", this.frowset.getString("status"));
		vo.setString("name", this.frowset.getString("name"));
		vo.setString("plan_type", this.frowset.getString("plan_type"));
		
		vo.setString("b0110", this.frowset.getString("b0110"));
		
		vo.setString("object_type", this.frowset.getString("object_type"));
		vo.setString("cycle", this.frowset.getString("cycle"));
		vo.setString("gather_type", this.frowset.getString("gather_type"));
		
		//vo.setString("start_date", this.frowset.getString("start_date")); 
		//vo.setString("end_date", this.frowset.getString("end_date"));
		vo.setDate("start_date", this.frowset.getDate("start_date")); 
		vo.setDate("end_date", this.frowset.getDate("end_date"));
		vo.setString("template_id", this.frowset.getString("template_id"));
		
		vo.setString("agree_user", this.frowset.getString("agree_user"));  
		//vo.setString("agree_date", PubFunc.DoFormatDate(isNull(this.frowset.getString("agree_date")).length()>10?this.frowset.getString("agree_date").substring(0, 10):""));
		vo.setDate("agree_date", this.frowset.getDate("agree_date"));
		vo.setString("approve_result", this.frowset.getString("approve_result"));		  
		
		vo.setString("descript", PubFunc.toHtml(isNull(this.frowset.getString("descript"))));		
		vo.setString("target", PubFunc.toHtml(isNull(this.frowset.getString("target"))));
		
		vo.setString("content", PubFunc.toHtml(isNull(this.frowset.getString("content"))));
		vo.setString("flow", PubFunc.toHtml(isNull(this.frowset.getString("flow"))));
		vo.setString("result", PubFunc.toHtml(isNull(this.frowset.getString("result"))));
		
		vo.setString("create_user", this.frowset.getString("create_user"));
		vo.setString("parameter_content", isNull(this.frowset.getString("parameter_content")));
		//vo.setString("create_date", PubFunc.DoFormatDate(this.frowset.getString("create_date").substring(0,10)));
		vo.setDate("create_date", this.frowset.getDate("create_date"));
		
		vo.setString("theyear", isNull(this.frowset.getString("theyear")));
		vo.setString("themonth", isNull(this.frowset.getString("themonth")));
		vo.setString("thequarter", isNull(this.frowset.getString("thequarter")));
		//vo.setString("start_date", PubFunc.DoFormatDate(isNull(this.frowset.getString("start_date")).length()>10?this.frowset.getString("start_date").substring(0, 10):""));
		//vo.setString("end_date", PubFunc.DoFormatDate(isNull(this.frowset.getString("end_date")).length()>10?this.frowset.getString("end_date").substring(0, 10):""));
		
	}
	} catch (SQLException e)
	{
	    e.printStackTrace();
	}finally
	{
	    this.getFormHM().put("khplanvo",vo);
	}
	
    }
    public String isNull(String str)
    {
	if(str==null)
	    str="";
	return str;	  
    } 
}
