package com.hjsj.hrms.transaction.gz.gz_analyse.gz_fare_analyse;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.GzFareAnalyseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ExportFileTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
		   String year =(String)this.getFormHM().get("year");
		   String code=(String)this.getFormHM().get("code");
		   String m_code=(String)this.getFormHM().get("m_code");
		   m_code=PubFunc.decrypt(m_code);
		   if(!"null".equals(m_code)&&!m_code.equalsIgnoreCase(code))
		   {
			   throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.fileouterror")));
		   }
		   String chartkind=(String)this.getFormHM().get("chartkind");
		   String planitem=(String)this.getFormHM().get("planitem");
		   String planItemDesc=(String)this.getFormHM().get("planItemDesc");
		   GzAmountXMLBo bo =  new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map = bo.getValuesMap();
			ArrayList itemlist = (ArrayList)map.get("ctrl_item");
			ArrayList alist = new ArrayList();
			String name="";
			for(int j=0;j<itemlist.size();j++)
			{
				LazyDynaBean abean = (LazyDynaBean)itemlist.get(j);
				String plan=(String)abean.get("planitem");
				if("0".equals(this.userView.analyseFieldPriv(plan)))
					continue;
				String classname=(String)abean.get("classname");
				if("5".equals(chartkind))
				{
					name=planitem.split("`")[1];
					if(classname==null|| "".equals(classname.trim())||!classname.equalsIgnoreCase(name))
						continue;
				}
				alist.add(abean);
			}
			if(alist.size()<=0)
				throw GeneralExceptionHandler.Handle(new Exception("您没有权限查看薪资总额参数中定义的参数指标"));
			String amountAdjustSet="-1";
			if(map!=null&&map.get("amountAdjustSet")!=null&&((String)map.get("amountAdjustSet")).length()>0)
				amountAdjustSet=(String)map.get("amountAdjustSet");
			String amountPlanitemDescField="-1";
			if(map!=null&&map.get("amountPlanitemDescField")!=null&&((String)map.get("amountPlanitemDescField")).length()>0)
				amountPlanitemDescField=(String)map.get("amountPlanitemDescField");
			String setid=(String)map.get("setid");
			GzFareAnalyseBo gbo= new GzFareAnalyseBo(this.getFrameconn());
			String outName=gbo.getFileName(this.userView, alist, year, code, setid, chartkind,name,planItemDesc,amountAdjustSet,amountPlanitemDescField);
			/* 安全问题 文件下载 发放进展表-总额使用情况-导出 xiaoyun 2014-9-19 start  */
			//outName=outName.replace(".xls","#");
			outName = SafeCode.encode(PubFunc.encrypt(outName));
			/* 安全问题 文件下载 发放进展表-总额使用情况-导出 xiaoyun 2014-9-19 end  */
			this.getFormHM().put("outName",outName);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
