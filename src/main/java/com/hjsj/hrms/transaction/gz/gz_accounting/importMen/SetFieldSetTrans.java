package com.hjsj.hrms.transaction.gz.gz_accounting.importMen;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:设置变动子集</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 21, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SetFieldSetTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String salaryid=(String)hm.get("salaryid");
			String fieldItemId=(String)this.getFormHM().get("fieldItemId");
			GzAmountXMLBo bo=new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap map=bo.getValuesMap();
			ArrayList fieldSetList=new ArrayList();
			String    fieldSetId="";
			ArrayList fieldItemList=new ArrayList();
			
			ArrayList list=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
			for(int i=0;i<list.size();i++)
			{
				 FieldSet fieldset=(FieldSet)list.get(i);
				 if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
					 continue;
				 
				 CommonData data=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
				 fieldSetList.add(data);
			}
			if(map!=null&&map.get("chg_set")!=null&&((String)map.get("chg_set")).trim().length()>0)
			{
				fieldSetId=(String)map.get("chg_set");
			}
			else
				fieldSetId=((CommonData)fieldSetList.get(0)).getDataValue();
			
			//查询子集中的子标列表
			list=DataDictionary.getFieldList(fieldSetId, Constant.USED_FIELD_SET);
			String chg_set_context="";
			if(map!=null&&map.get("chg_set_context")!=null)
			{
				chg_set_context=(","+(String)map.get("chg_set_context")+",").toLowerCase();
			}
			for(int i=0;i<list.size();i++)
			{
				 FieldItem fielditem=(FieldItem)list.get(i);
				 if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
					 continue;
				 LazyDynaBean abean=new LazyDynaBean();
				 abean.set("itemid",fielditem.getItemid());
				 abean.set("itemdesc",fielditem.getItemdesc());
				 abean.set("codesetid", fielditem.getCodesetid());
				 abean.set("itemtype",fielditem.getItemtype());
				 if(chg_set_context.indexOf(","+fielditem.getItemid().toLowerCase()+",")!=-1)
					 abean.set("isExist","1");
				 else 
					 abean.set("isExist","0");
				 fieldItemList.add(abean);
			}
			
			this.getFormHM().put("fieldItemList",fieldItemList);
			this.getFormHM().put("fieldSetId",fieldSetId);
			this.getFormHM().put("fieldSetList",fieldSetList);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("fieldItemId", fieldItemId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
