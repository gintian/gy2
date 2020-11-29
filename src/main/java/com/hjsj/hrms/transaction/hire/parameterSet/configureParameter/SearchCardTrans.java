package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchCardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap xmlMap=parameterXMLBo.getAttributeValues();
			ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
			ArrayList hireObjList=parameterSetBo.getCodeValueList();//取得招聘对象集合
			ArrayList previewTableList = parameterSetBo.getAllPreviewTable();
			ArrayList cardList = new ArrayList();
			for(int i=0;i<hireObjList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)hireObjList.get(i);
				String codeitemid=(String)abean.get("codeitemid");
				String key="CARDTABLE_"+codeitemid;
				String value="";
				if(xmlMap.get(key)!=null)
					value=(String)xmlMap.get(key);
				abean.set("value", value);
				cardList.add(abean);
			}
			this.getFormHM().put("cardList", cardList);
			this.getFormHM().put("previewTableList", previewTableList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
