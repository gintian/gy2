package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GetCommonQueryCondTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			/**opt=1 简历状态，=0简历统计条件*/
			String opt=(String)hm.get("opt");
			ParameterSetBo bo = new ParameterSetBo(this.getFrameconn());
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			if("0".equals(opt))
			{
	     		ArrayList commonQueryCondlist = bo.getCommonQueryCondList("1");
	    		String ids = (String)map.get("common_query");
	     		ArrayList selectedCommonQuery= null;
		    	if(ids==null|| "".equals(ids))
		    		selectedCommonQuery = new ArrayList();
		     	else
				selectedCommonQuery = bo.getSelectedCommonQueryCondList(ids,"1");
		    	this.getFormHM().put("selectedCommonQuery",selectedCommonQuery);
		    	this.getFormHM().put("commonQueryCondlist", commonQueryCondlist);
			}
			else
			{
				ArrayList list = bo.getResumeCodeList();
				String ids=(String)map.get("resume_code");
				ArrayList selectedList=null;
				if(ids==null|| "".equals(ids))
					selectedList=new ArrayList();
				else
					selectedList = bo.getSelectedResumeCodeList(ids);
				this.getFormHM().put("selectedCommonQuery",selectedList);
		    	this.getFormHM().put("commonQueryCondlist", list);
			}
			this.getFormHM().put("optType",opt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
