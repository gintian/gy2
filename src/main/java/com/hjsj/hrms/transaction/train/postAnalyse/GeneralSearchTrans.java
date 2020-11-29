package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GeneralSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"1";
		reqhm.remove("type");
		
		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		reqhm.remove("a_code");
		
		String tablename = (String)reqhm.get("tablename");
		tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
		reqhm.remove("tablename");

		ArrayList setlist = new ArrayList();
		ArrayList fieldsetlist = new ArrayList();
		String fieldsetid="0";
		String fieldsetdesc="";
    	if("1".equals(type)){
	    	//setlist=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
    		setlist=this.userView.getPrivFieldSetList(Constant.ALL_FIELD_SET);
	    }else if("2".equals(type)){
	    	setlist=this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
	    }else if("3".equals(type)){
	    	setlist=this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
	    }
	    for(int i=0;i<setlist.size();i++){
		    FieldSet fieldset = (FieldSet)setlist.get(i);
		    if("0".equalsIgnoreCase(fieldset.getUseflag()))
		     	continue;
		    if(fieldset.getFieldsetid().toUpperCase().startsWith("A"))
		    	continue;	
		    if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
		    	continue;
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
			    continue;
		    CommonData obj=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
		    fieldsetlist.add(obj);
	    }
	    if(reqhm.get("fieldsetid")!=null&&!"".equals((String)reqhm.get("fieldsetid")))
	    {
			fieldsetid=(String)reqhm.get("fieldsetid");
			FieldSet ff=DataDictionary.getFieldSetVo(fieldsetid);
			fieldsetdesc=ff.getCustomdesc();
			reqhm.remove("fieldsetid");
		}
		this.getFormHM().put("setlist",fieldsetlist);
		this.getFormHM().put("type",type);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("tablename",tablename);
		this.getFormHM().put("fieldSetId", fieldsetid);
		this.getFormHM().put("fieldSetDesc", fieldsetdesc);
	}

}
