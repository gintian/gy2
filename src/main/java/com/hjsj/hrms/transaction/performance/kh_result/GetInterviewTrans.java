package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetInterviewTrans extends IBusiness
{
	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id=(String)hm.get("planid");
			String object_id=(String)hm.get("object_id");
			
			// 页面传递的url：/performance/kh_result/kh_result_interview.do?b_interview=link`object_id=00000009`planid=219
			// KhResultForm中直接取值：this.getFormHM().put("planid", this.getPlanid());
			// 所以排除参数加密的可能
			Pattern p = Pattern.compile("^[0-9]*$"); //判断传过来的是否是数字   是（不需转码）  否（乱码，需转码）  zhaoxg add 2014-6-28
			Matcher m = p.matcher(plan_id); 
			boolean yesorno = m.matches();  
			if(!yesorno){
				plan_id = PubFunc.decryption(plan_id);
				//团队考核结果 面谈记录 wangrd 20141226
				object_id=PubFunc.decryption(object_id);
			}		
			ResultBo bo = new ResultBo(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList list = bo.getInterviewList(plan_id, object_id, dao);
			this.getFormHM().put("interviewList", list);
			this.getFormHM().put("drawId", "6");
			AnalysePlanParameterBo abo=new AnalysePlanParameterBo(this.getFrameconn());
			Hashtable ht_table=abo.analyseParameterXml();
			String templet_id="-1";
			if(ht_table!=null)
			{
				if(ht_table.get("interview_template")!=null)
					templet_id=(String)ht_table.get("interview_template");
			}
			if(templet_id==null|| "".equals(templet_id))
			{
				templet_id="-1";
			}
			this.getFormHM().put("templet_id", templet_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
