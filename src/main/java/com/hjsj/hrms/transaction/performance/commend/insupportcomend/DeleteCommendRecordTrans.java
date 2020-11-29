package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.businessobject.performance.commend.CommendSetBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteCommendRecordTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			String ids = "";
			if(this.getFormHM().get("selectID")!= null && ((String)this.getFormHM().get("selectID")).trim().length()>0)
				ids=(String)this.getFormHM().get("selectID");
			if(ids.indexOf(",") != -1)
			   ids=ids.substring(1);
			CommendSetBo bo=new CommendSetBo(this.getFrameconn());
			String p0300s=bo.getP0300sByP0201(ids);
			ContentDAO dao=new ContentDAO(this.getFrameconn()); 
			String p02sql="delete from p02 where p0201 in ("+ids+")";
			dao.delete(p02sql,new ArrayList());
			String p03sql="delete from p03 where p0201 in ("+ids+")";
			dao.delete(p03sql,new ArrayList());
			String persql="delete from per_talent_vote where p0300 in ("+p0300s+")";
			dao.delete(persql,new ArrayList());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	

}
