package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 查询培训班下载模板可用信息集（目前只用r31、r41、r40）
 */
public class ExportClassOrLessonTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList dataList = new ArrayList();
		StringBuffer sql = new StringBuffer();
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String flag = (String)hm.get("flag");
		flag=PubFunc.nullToStr(flag);
		hm.remove("flag");

		sql.append("select fieldsetid,fieldsetdesc from t_hr_busiTable where id='20'");
		sql.append(" and useflag='1' and ownflag='1' and ");
		if("student".equalsIgnoreCase(flag))
			sql.append("fieldsetid='R40'");
		else
			sql.append("fieldsetid in ('R31','R41')");
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				CommonData cd = new CommonData(this.frowset.getString("fieldsetid"), this.frowset.getString("fieldsetdesc"));
				dataList.add(cd);
			}
			if (dataList.size() == 0) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.batchinout.nopriv")));
			}
			
			ArrayList fielditemlist = getUsedFieldBySetNameTransOutNum("A01",this.userView);
			
			this.getFormHM().put("fieldSetDataList", dataList);
			this.getFormHM().put("fielditemlist", fielditemlist);
			this.getFormHM().put("student", flag);

		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	public ArrayList getUsedFieldBySetNameTransOutNum(String tablename,UserView userView) {
    	ArrayList list=new ArrayList();
    	CommonData dataobj = new CommonData();
		dataobj.setDataName(ResourceFactory.getProperty("label.select"));
		dataobj.setDataValue("");		
		list.add(dataobj);
		
		String setname=tablename;		
		if(setname==null||setname.length()<=0)
           return list;
		ArrayList fielditemlist=DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		
		if(fielditemlist!=null)
		{
			for(int i=0;i<fielditemlist.size();i++)
		    {
		      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		      if("M".equals(fielditem.getItemtype()))
		    	  continue;
		      if("0".equals(userView.analyseFieldPriv(fielditem.getItemid())))
		        continue;
		      if("A".equals(fielditem.getItemtype())&&!"0".equalsIgnoreCase(fielditem.getCodesetid()))
		    	  continue;
		      if("D".equals(fielditem.getItemtype()))
		    	  continue;
		      if("N".equals(fielditem.getItemtype()))
		    	  continue;
		      
		      if(fielditem.getCodesetid()!=null)
		      {
		    	  dataobj = new CommonData();
			      dataobj = new CommonData(fielditem.getItemid(), fielditem.getItemid().toUpperCase()+ ":"+ fielditem.getItemdesc());
			      list.add(dataobj);		     
			    }
		      }
		     
		}
	    return list;
    }
}
