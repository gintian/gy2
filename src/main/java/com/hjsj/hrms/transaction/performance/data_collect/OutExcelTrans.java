package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
* 
* 类名称：OutExcelTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 12:00:20 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 12:00:20 PM   
* 修改备注：   导出excel
* @version    
*
 */
public class OutExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String fieldsetid=(String)this.getFormHM().get("fieldsetid");
		String sql = PubFunc.keyWord_reback((String) this.getFormHM().get("sql"));
		DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
		DataCollectBo databo = new DataCollectBo(this.frameconn,this.userView);
		String state_id  = bo.getXmlValue1("state_id",fieldsetid);
		String set_id  = bo.getXmlValue1("set_id",fieldsetid);
		try {
			ArrayList accountlist = databo.getExcelList(set_id, state_id);
			ArrayList accountDatalist = databo.getDataList(accountlist, sql, dao);
			if(accountDatalist.size()==0)
			{
				throw GeneralExceptionHandler.Handle(new Exception("无数据导出!"));
			}
			String fileName=this.getUserView().getUserName()+PubFunc.getStrg()+".xls";
			String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName;
			databo.exportData(fileName, accountDatalist, accountlist, url,state_id);
			this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
		} catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
