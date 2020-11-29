package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;

public class FinishStateTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			String ids="";
			if(((String)this.getFormHM().get("selectIds")).trim().length()>0 && this.getFormHM().get("selectIds")!= null)
				ids = (String)this.getFormHM().get("selectIds");
			if(ids.indexOf(",") != -1)
				ids=ids.substring(1);
			StringBuffer sb= new StringBuffer();
			Calendar calendar =Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH)+1;
			int day = calendar.get(Calendar.DATE);
			String endtime=String.valueOf(year)+"."+String.valueOf(month)+"."+String.valueOf(day);
			sb.append("update p02 set p0209='06',p0207='"+endtime+"' where p0201 in (");
			sb.append(ids+")");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sb.toString());
			//进行票数统计
			CommendSetBo bo = new CommendSetBo(this.getFrameconn());
			if(ids != null && ids.trim().length()>0)
				bo.AnalyseVote(ids);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
}
