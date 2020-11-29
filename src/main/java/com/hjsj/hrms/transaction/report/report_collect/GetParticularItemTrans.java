package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * <p>Title:</p>
 * <p>Description:得到待选条件的详细子项列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 26, 2006:10:08:26 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class GetParticularItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String provisionTerm=(String)this.getFormHM().get("provisionTerm");   //得到待选条件	
			provisionTerm = PubFunc.keyWord_reback(provisionTerm);  //add by wangchaoqun on 2014-9-30
			String  unitcode=(String)this.getFormHM().get("unitcode");
			String  tabid=(String)this.getFormHM().get("tabid");
			
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			
			String  reportTypes=(String)this.getFormHM().get("reportTypes");
			String  backdate=(String)this.getFormHM().get("backdate");
			IntegrateTableBo bo=new IntegrateTableBo(this.getFrameconn(),this.getUserView().getUserId(),this.getUserView().getUserName(),tabid,unitcode,reportTypes);
			bo.setBackdate(backdate);
			ArrayList defaultItemList=bo.getLeftFields(provisionTerm,"1");
			this.getFormHM().put("defaultItemList",defaultItemList);
			this.getFormHM().put("provisionTerm",provisionTerm);
		}
		 catch(Exception sqle)
		 {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		 }
	}

}
