package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 30200710249
 * <p>Title:ActiveResumeTrans.java</p>
 * <p>Description>:ActiveResumeTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 7, 2009 4:00:12 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ActiveResumeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String dbName=(String)this.getFormHM().get("dbName");
			String a0100=(String)this.getFormHM().get("a0100");
			String activeValue=(String)this.getFormHM().get("activeValue");
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			String active_field=(String)map.get("active_field");
			StringBuffer buf = new StringBuffer();
			String value="1";
			if("1".equals(activeValue))
				value="2";
			a0100=PubFunc.getReplaceStr(a0100);
			buf.append("update "+dbName+"a01 set "+active_field+"='"+value+"' where a0100='"+a0100+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(buf.toString());
			String isUpPhoto="0";   //是否必须上传照片
			if(map.get("photo")!=null&&((String)map.get("photo")).length()>0)
				isUpPhoto=(String)map.get("photo");
			this.getFormHM().put("isUpPhoto",isUpPhoto);
			this.getFormHM().put("activeValue",value);
			this.getFormHM().put("isDefinitionActive", "1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
