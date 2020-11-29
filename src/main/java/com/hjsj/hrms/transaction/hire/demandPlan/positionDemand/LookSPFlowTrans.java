package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.businessobject.hire.PositionDemand;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class LookSPFlowTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String z0301 = (String)hm.get("z0301");
			PositionDemand bo = new PositionDemand(this.getFrameconn());
			/**招聘安全改造,判断当前操作用户是否有查看的权限**/
			String sql = (String) this.userView.getHm().get("hire_sql");
			//当直接从邮件登录进行批准时不进行验证当前用户范围
			if(sql!=null && !"".equals(sql)){				
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				int index = sql.indexOf("order by");
				if(index!=-1){
					sql = sql.substring(0, index);
				}
				sql = sql+" and z0301='"+z0301+"'";
				this.frowset = dao.search(sql);
				if(!this.frowset.next()){
					throw new GeneralException(ResourceFactory.getProperty("label.hireemploye.no.contorl"));
				}
			}

			String reasons=bo.getReasons(z0301);
			this.getFormHM().put("reasons", reasons);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
