/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:批量导入薪资数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-13:下午02:14:16</p> 
 *@author cmq
 *@version 4.0
 */
public class BatchImportItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		try
		{
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			/**导入类型
			 *=1同月上次
			 *=2上月同次 
			 *=3档案数据
			 *=4某年某月某次
			 */			
			String importtype=(String)this.getFormHM().get("importtype");
			/**前台数组转换成后台ArrayList对象,前台只选中一个项目时，转换成String*/		
			Object obj=this.getFormHM().get("items");
			
			// gby,获取人员筛选条件
			String screeningWhere = (String)this.userView.getHm().get("gz_filterWhl");;
			
			ArrayList items=null;
			if(obj instanceof String)
			{
				items=new ArrayList();
				items.add(obj);
			}
			else
				items=(ArrayList)this.getFormHM().get("items");	
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			gzbo.setScreeningWhereSql(screeningWhere);//gby,保存人员筛选条件
			
			String year="";
			String month="";
			String count="";
			if("4".equals(importtype))
			{
				year=(String)this.getFormHM().get("year");
				month=(String)this.getFormHM().get("month");
				count=(String)this.getFormHM().get("count");
			}
			
			if(this.getFormHM().get("ym")!=null&&this.getFormHM().get("_count")!=null)
			{
				HashMap map=new HashMap();
				if(((String)this.getFormHM().get("ym")).trim().length()>0)
				{
					map.put("ym", ((String)this.getFormHM().get("ym")).replaceAll("\\.", "-"));
					map.put("count",(String)this.getFormHM().get("_count"));
					gzbo.batchImport_history(map,importtype, items,year,month,count);
					gzbo.batchUpdateTempData(items,map);
				}
			}
			else
				gzbo.batchImport(importtype, items,year,month,count);
			/**人员计算过滤条件*/
			String strwhere="";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
