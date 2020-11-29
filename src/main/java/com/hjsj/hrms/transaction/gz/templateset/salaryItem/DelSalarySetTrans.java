package com.hjsj.hrms.transaction.gz.templateset.salaryItem;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

public class DelSalarySetTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String[] salarySetIDs=(String[])this.getFormHM().get("salarySetIDs");
			if(salarySetIDs!=null&&salarySetIDs.length>0)
			{
				HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
				String salaryid=(String)hm.get("salaryid");
				
				CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
				safeBo.isSalarySetResource(salaryid,null);
				
				SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.getUserView());
				
				ContentDAO dao=new ContentDAO(this.frameconn);
				StringBuffer context = new StringBuffer();
				context.append("删除:"+bo.getSalaryName(salaryid)+"("+salaryid+")<br>");
				StringBuffer whl=new StringBuffer("");
				for(int i=0;i<salarySetIDs.length;i++)
					whl.append(","+salarySetIDs[i]);
				RowSet rs = dao.search("select itemdesc from  salaryset where salaryid="+salaryid+" and fieldid in ("+whl.substring(1)+")");
				while(rs.next())
				{
					context.append(rs.getString("itemdesc"));
					context.append(",");
				}
				
				bo.delSalarySet(salarySetIDs);
				
				this.getFormHM().put("@eventlog", context.toString());
				/**删除后刷新静态变量，不然数据不同步*/
				/* 觉得下面代码没有用 薪资项目多的时候，很慢 wangrd 2015-02-02
				String itemid="";
				SalaryPkgBo pkgbo=new SalaryPkgBo(this.getFrameconn(),null,0);
				pkgbo.setAllitem_hm(null);
				pkgbo.searchItemById(itemid);
				*/
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
