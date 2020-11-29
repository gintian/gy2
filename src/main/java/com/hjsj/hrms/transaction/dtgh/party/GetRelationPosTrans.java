package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class GetRelationPosTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String ps_c_job=(String)this.getFormHM().get("ps_c_job");
		String codeitemid=(String)this.getFormHM().get("codeitemid");
		if(ps_c_job == null || ps_c_job.length()<1)
			throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("pos.posbusiness.nosetposccode.job")));
		StringBuffer sb = new StringBuffer();
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select e01a1 from k01 where "+ps_c_job+"='"+codeitemid+"'";
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				sb.append(","+this.frowset.getString("e01a1"));
			}
			if(sb.length()==0)
				sb.append(",");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.getFormHM().put("msg", sb.substring(1));
		}
	}
}
