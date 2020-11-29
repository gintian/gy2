/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:查询薪资数据提交方式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-26:下午03:57:43</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchGzDataSubmitTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String type=(String)hm.get("type");    // 1:工资发放  2：工资审核 3：薪资汇总审批 zhaoxg add 2015-2-2
		String gz_module=(String)hm.get("gz_module");
		
		String bosdate=(String)hm.get("bosdate");
		String count=(String)hm.get("count");
		
		try
		{
			ArrayList list=new ArrayList();
			if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			/**取得全部的计算公式*/
			list=gzbo.getSubmitTypeList();
			this.getFormHM().put("formulalist", list);
			
			String isUpdateSet=getIsUpdateSet(list);
			this.getFormHM().put("isUpdateSet", isUpdateSet);
			this.getFormHM().put("gz_module",gz_module);
			this.getFormHM().put("bosdate",bosdate);
			this.getFormHM().put("count", count);
			
			 /**数据更新方式列表*/
		    ArrayList typelist=new ArrayList();  
		    CommonData vo=new CommonData("2",ResourceFactory.getProperty("label.gz.notchange"));
		    if(type==null|| "1".equals(type)|| "2".equals(type)|| "3".equals(type))
	        {
			   typelist.add(vo);
	        }
	        vo=new CommonData("0",ResourceFactory.getProperty("label.gz.update"));
	        typelist.add(vo);
	        if(type==null|| "1".equals(type)|| "2".equals(type)|| "3".equals(type))
	        {
	        	vo=new CommonData("1",ResourceFactory.getProperty("label.gz.append"));
	        	typelist.add(vo);
	        }
	        if(type!=null&& "2".equals(type)|| "3".equals(type)) //如果是审批模块提交薪资不判断是否有处于结束状态的记录
	        	this.getFormHM().put("isHistory","0");
	        else
	        	this.getFormHM().put("isHistory",gzbo.isHistory2()==true?"1":"0");
	        this.getFormHM().put("typelist",typelist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		

	}
	
	
	private String getIsUpdateSet(ArrayList list)
	{
		String isUpdateSet="none";
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean dynabean=(LazyDynaBean)list.get(i);
			String type=(String)dynabean.get("type");
			if("0".equals(type))
				isUpdateSet="block";
		}
		return isUpdateSet;
	}
	

}
