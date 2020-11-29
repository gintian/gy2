package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * <p>Title:指标添加</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 5, 2008:10:41:41 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class AddIndexTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		//【6984】系统管理/指标体系，在主集中新建指标时，界面报错，不对。 jingq upd 2015.01.26
		String dev_flag = SystemConfig.getPropertyValue("dev_flag");
		this.getFormHM().put("dev_flag", dev_flag);
		String setid = (String)this.formHM.get("setid");
		IndexBo index = new IndexBo(this.getFrameconn());
		String indexcode = index.getindex(setid,dev_flag);
		
		this.getFormHM().put("indexcode", indexcode);
		ArrayList dateList = index.getdate();
		this.getFormHM().put("dateList", dateList);
		ArrayList joincodeList = index.getjoincode();
		this.getFormHM().put("joincodeList", joincodeList);
		List inputtypeMList = index.getInputtypeMList();
		this.getFormHM().put("inputtypeMList", inputtypeMList);
		
	}
	
}
