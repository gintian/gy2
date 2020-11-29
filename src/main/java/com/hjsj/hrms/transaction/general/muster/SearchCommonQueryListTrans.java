package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.muster.MusterXMLStyleBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 0521010024
 * <p>Title:SearchCommonQueryListTrans.java</p>
 * <p>Description>:SearchCommonQueryListTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 20, 2010 11:18:20 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchCommonQueryListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String info_flag=(String)map.get("info_flag");
			String tableid=(String)map.get("tableid");
			MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
			ArrayList list=musterbo.getUsuallyCondList(info_flag, this.userView);
			MusterXMLStyleBo mxbo=new MusterXMLStyleBo(this.getFrameconn(),tableid);
			String commonQueryId=mxbo.getParamValue2(MusterXMLStyleBo.Param, "usual_query");
			ArrayList commonQueryList=new ArrayList();
			commonQueryList.add(new CommonData("","请选择..."));
			for(int i=0;i<list.size();i++)
			{
				DynaBean bean =(DynaBean)list.get(i);
				CommonData cd = new CommonData((String)bean.get("id"),(String)bean.get("name"));
				commonQueryList.add(cd);
			}
			this.getFormHM().put("commonQueryList", commonQueryList);
			this.getFormHM().put("commonQueryId", commonQueryId);
			this.getFormHM().put("currid",tableid);	
			this.getFormHM().put("infor_Flag",info_flag);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
