package com.hjsj.hrms.transaction.gz.gz_accounting.importMen;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:保存  引入单位\部门变动人员 参数</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 21, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveGzParamTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			GzAmountXMLBo bo=new GzAmountXMLBo(this.getFrameconn(),0);
			String fieldSetId=(String)this.getFormHM().get("fieldSetId");
			String fieldItems=(String)this.getFormHM().get("fieldItems");
		/*	ArrayList fieldItemList=(ArrayList)this.getFormHM().get("fieldItemList");
			
			StringBuffer context=new StringBuffer("");
			for(int i=0;i<fieldItemList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)fieldItemList.get(i);
				String isExist=(String)abean.get("isExist");
				if(isExist.equals("1"))
					context.append(","+(String)abean.get("itemid"));
			}
			
			if(context.length()>0)
				bo.setOriValue("CHG_SET","",context.substring(1),0);
			else
				bo.setOriValue("CHG_SET","","",0);
			*/
			bo.setOriValue("CHG_SET","",fieldItems,0);
			bo.setOriValue("CHG_SET","chg_set",fieldSetId,1);
			bo.saveParameters();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
