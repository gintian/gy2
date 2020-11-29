/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.myapply;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 25, 2008:1:40:45 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ImpEmployDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		
		try
		{
			if(this.userView.getStatus()!=4&&!(this.userView.getA0100()!=null&&this.userView.getA0100().length()>0))
			{
				throw new GeneralException("非自助平台用户!");
			} 
			/**开始处理自助用户平台时,将userView中的templateMap,清空**/
			this.userView.getHm().remove("templateMap");
			CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this.frameconn,this.userView);
			//tabid=checkPrivSafeBo.checkResource(7, tabid);
			ArrayList a0100list=new ArrayList();
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			
			if(!tablebo.isCorrect(tabid))
				throw new GeneralException(ResourceFactory.getProperty("template.operation.noResource"));
			
			tablebo.setBEmploy(true);
		//	a0100list.add(this.userView.getUserId());
			a0100list.add(this.userView.getA0100());
			
			
			String	strDesT="g_templet_"+tabid;
		/*	ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select count(*) from "+strDesT+" where a0100="+this.userView.getA0100()+" and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
			int n=0;
			if(rowSet.next())
				n=rowSet.getInt(1);
			if(n==0)*/
				tablebo.impDataFromArchive(a0100list,this.userView.getDbname());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}


	}

}
