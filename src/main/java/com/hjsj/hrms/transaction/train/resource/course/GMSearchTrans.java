package com.hjsj.hrms.transaction.train.resource.course;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:GMSearchTrans</p>
 * <p>Description:查询培训课程记录</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author xujian
 * @version 1.0
 * 
 */
public class GMSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");

		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		reqhm.remove("type");
		
		String tablename = (String)reqhm.get("tablename");
		tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
		reqhm.remove("tablename");
		
		ArrayList list=DataDictionary.getFieldList(tablename,Constant.USED_FIELD_SET);
		
		ArrayList fieldsetlist = new ArrayList();
		for(int i=0;i<list.size();i++){
			FieldItem fieldItem = (FieldItem)list.get(i);
			if("M".equals(fieldItem.getItemtype()))
		    	continue;
			if("0".equalsIgnoreCase(fieldItem.getUseflag()))
				continue;
			if(fieldItem.getItemid().equalsIgnoreCase(tablename+"00"))
				continue;	
			CommonData obj = null;
			if("r5004".equals(fieldItem.getItemid())){
				obj = new CommonData(fieldItem.getItemid()+":"+fieldItem.getItemtype()
			    		  +":55:"+fieldItem.getFieldsetid(),
			    		  fieldItem.getItemdesc());
			}else{
				obj = new CommonData(fieldItem.getItemid()+":"+fieldItem.getItemtype()
			    		  +":"+fieldItem.getCodesetid()+":"+fieldItem.getFieldsetid(),
			    		  fieldItem.getItemdesc());
			}
			fieldsetlist.add(obj);
		}
		
		this.getFormHM().put("itemlist",fieldsetlist);
		this.getFormHM().put("tablename",tablename);

	}

}
