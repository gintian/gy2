package com.hjsj.hrms.transaction.train.signCollect;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class CheckTrainEmpCollectSignTrans extends IBusiness {

	public void execute() throws GeneralException {		
		String sort=(String)this.getFormHM().get("sort");
		String type="xx";
		if("2".equals(sort))
		{
			String classplan=(String)this.getFormHM().get("classplan");
			if(classplan==null||classplan.length()<=0)
				throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("train.no.take.classplan.info")));
			StringBuffer sql=new StringBuffer();
			sql.append("select 1 from R47 WHERE Exists(");
			sql.append("select r41.r4101 from r41,r13 where r1301=r4105 and r4103='"+classplan+"'");
			sql.append(" and r41.r4101=R47.r4101)");
			//System.out.println(sql.toString());
			//sql.append("select 1 from R40 WHERE R4101='"+courseplan+"'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			try {
				this.frowset=dao.search(sql.toString());
				if(this.frowset.next())
				{
					type= "ok";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.getFormHM().put("type", type);
	}

}
