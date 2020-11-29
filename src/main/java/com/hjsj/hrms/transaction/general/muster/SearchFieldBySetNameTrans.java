package com.hjsj.hrms.transaction.general.muster;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class SearchFieldBySetNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList list=new ArrayList();
			String setname=(String)this.getFormHM().get("tablename");
			String priv=(String)this.getFormHM().get("priv");
			priv=priv!=null&&priv.trim().length()>0?priv:"1";
			
			ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
			// 会报错：列名 'B0110' 无效。也不需要加这个固定指标(参考CS)。
			/*if(setname.equalsIgnoreCase("k01"))
			{
				list.add(new CommonData("b0110","单位"));
			}
			*/
		    if(fielditemlist!=null)
		    {
				for(int i=0;i<fielditemlist.size();i++)
			    {
			      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			      if("M".equals(fielditem.getItemtype()))
			    	continue;
			      if("1".equals(priv)&& "0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
			        continue;
			      CommonData dataobj = new CommonData();
			      dataobj = new CommonData(fielditem.getItemid(), /*"(" + fielditem.getItemid()+ ")"+*/ fielditem.getItemdesc());
			      
			      list.add(dataobj);
			    }
		    }
		    this.getFormHM().clear();
		    this.getFormHM().put("fieldlist",list);
		    this.getFormHM().put("sortitem","");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
	}

}
