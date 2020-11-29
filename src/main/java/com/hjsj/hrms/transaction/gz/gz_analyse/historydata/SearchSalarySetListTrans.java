package com.hjsj.hrms.transaction.gz.gz_analyse.historydata;

import com.hjsj.hrms.businessobject.gz.gz_analyse.GzAnalyseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 3020130024
 * <p>Title:SearchSalarySetListTrans.java</p>
 * <p>Description>:SearchSalarySetListTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jun 17, 2009 2:12:40 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchSalarySetListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String gz_module=(String)this.getFormHM().get("gz_module");
			if(gz_module==null|| "".equalsIgnoreCase(gz_module))
				gz_module="0";
			int imodule=Integer.parseInt(gz_module);
			StringBuffer buf=new StringBuffer();
			buf.append("select salaryid,cname,cbase,cond,seq from salarytemplate ");
			if(imodule==0)
				buf.append(" where (cstate is null or cstate='')");//薪资类别
			else
				buf.append(" where cstate='1'");//险种类别
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(buf.toString()+" order by seq");
			ArrayList list = new ArrayList();
			while(rset.next())
			{
				/**加上权限过滤*/
				if(imodule==0)
				{
					if(!this.userView.isHaveResource(IResourceConstant.GZ_SET, rset.getString("salaryid")))
						continue;
				}
				else
				{
					if(!this.userView.isHaveResource(IResourceConstant.INS_SET, rset.getString("salaryid")))
						continue;
				}
				LazyDynaBean lazyvo=new LazyDynaBean();
				lazyvo.set("salaryid", rset.getString("salaryid"));
				lazyvo.set("seq",rset.getString("seq")!=null?rset.getString("seq"):"0");
				lazyvo.set("cname", rset.getString("cname"));
				list.add(lazyvo);
			}
			GzAnalyseBo bo = new GzAnalyseBo(this.getFrameconn());
			bo.setTableName("salaryarchive");
			bo.createIndexArchive();
			this.getFormHM().put("salarySetList", list);
			this.getFormHM().put("gz_module", gz_module);
			this.getFormHM().put("condid", "");//取消人员过滤，和项目过滤选中
			this.getFormHM().put("cond_id_str", "");
			this.getFormHM().put("itemid", "");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
