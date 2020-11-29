package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * <p>Title:</p>
 * <p>Description:初始化简单条件汇总，得到单位下的全局参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 14, 2006:11:05:56 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class InitSimpleConditionCollectTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String unitcode=(String)hm.get("unitcode");
		String sortid_str=(String)hm.get("sortid_str");
		
		HashSet sortidSet=new HashSet();
		String[] a_sort=sortid_str.split(",");
		//得到指定的表类id串
		for(int i=0;i<a_sort.length;i++)
			sortidSet.add(a_sort[i]);

		ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
		ArrayList commonsParam=reportCollectBo.getCommonsParam(unitcode,sortidSet);
		ArrayList list=new ArrayList();
		if(commonsParam.size()>0)
		{
			DynaBean bean=(DynaBean)commonsParam.get(0);
			String paramCode=(String)bean.get("paramCode");
			list=reportCollectBo.getCodeItemList(paramCode,1);
			
		}
		
		this.getFormHM().put("codeItemList",list);
		this.getFormHM().put("commonsParam",commonsParam);
	}

}
