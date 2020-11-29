package com.hjsj.hrms.transaction.hire.interviewEvaluating.interviewArrange;

import com.hjsj.hrms.businessobject.hire.InterviewEvaluatingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveBatchArrangeInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
	
		String ids=(String)this.getFormHM().get("selectIDs");
		ids = PubFunc.hireKeyWord_filter_reback(ids);
		String zpdd=(String)this.getFormHM().get("zpdd");
		if ("null".equalsIgnoreCase(zpdd))
			zpdd = null;
		
		String zykg=(String)this.getFormHM().get("zykg");
		if ("null".equalsIgnoreCase(zykg))
			zykg = null;
		
		String wykg=(String)this.getFormHM().get("wykg");
		if ("null".equalsIgnoreCase(wykg))
			wykg = null;
		
		String mssj=(String)this.getFormHM().get("mmsj");
		if ("null".equalsIgnoreCase(mssj))
			mssj = null;
		
		String state=(String)this.getFormHM().get("state");//dml 2011年8月22日17:14:11
		if ("null".equalsIgnoreCase(state))
			state = null;		
		
		//System.out.println(ids+"  "+zpdd+"  "+zykg+"   "+wykg+"  "+mssj);
		InterviewEvaluatingBo interviewEvaluatingBo=new InterviewEvaluatingBo(this.getFrameconn());
		String[] id=ids.split("#");
		for(int i=0;i<id.length;i++)
		{
		    String anId = PubFunc.decrypt(id[i]);
		    
			if(zpdd!=null)
				interviewEvaluatingBo.saveInterviewArrangeInfo(anId,"z0503",zpdd.trim(),"A");//面试地点
			if(zykg!=null)
				interviewEvaluatingBo.saveInterviewArrangeInfo(anId,"z0505",zykg,"A");//专业考官
			if(wykg!=null)
				interviewEvaluatingBo.saveInterviewArrangeInfo(anId,"z0507",wykg,"A");//外语考官
			if(mssj!=null)
				interviewEvaluatingBo.saveInterviewArrangeInfo(anId,"z0509",mssj,"D");//面试时间
			if(state!=null) {
				interviewEvaluatingBo.saveInterviewArrangeInfo(anId,"state",state,"A");//dml 2011年8月22日17:14:26  状态	关联代码类36
				// 联系人 谁更改了通知的状态,那么联系人就会变成谁的,如果是业务用户修改的,当业务用户没有关联人员的话,取业务用户的
				String userid ="";
				if(this.userView.getA0100()!=null&&!"".equals(this.userView.getA0100().trim()))
	            {
	                userid=this.userView.getDbname()+"`"+this.userView.getA0100();
	            }
	            else
	            {
	                userid=this.userView.getUserId();
	            }
			interviewEvaluatingBo.saveInterviewArrangeInfo(anId,"z0511",userid,"A"); // 刘蒙 2014年4月15日14:14:04 联系人
	
			interviewEvaluatingBo.saveInterviewArrangeInfo(anId,"state_date",
			        PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"),"D"); // 简历状态修改时间 2014-05-28
		}
		}
		

	}

}
