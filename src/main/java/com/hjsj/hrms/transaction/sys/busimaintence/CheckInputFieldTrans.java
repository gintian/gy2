package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckInputFieldTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		String fielditemids = ((String)this.getFormHM().get("fielditemids")).toUpperCase();
		FieldSet set = (FieldSet)com.hrms.hjsj.sys.DataDictionary.getFieldSetVo(fieldsetid);
		String setdesc="";
		StringBuffer msg=new StringBuffer();
		if(set!=null)
			setdesc=set.getCustomdesc();
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search("select itemid,itemdesc from t_hr_busifield where upper(fieldsetid)='"+fieldsetid.toUpperCase()+"'");
			while(this.frowset.next()){
				String itemid = this.frowset.getString("itemid");
				if(fielditemids.indexOf(itemid)!=-1)
					msg.append(itemid+":"+this.getFrowset().getString("itemdesc")+"\\n");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(msg.length()>2){
				if(setdesc.length()<1)
					setdesc="信息表";
				msg.append("在"+setdesc+"中已存在!");
			}else{
				msg.append("ok");
			}
			this.getFormHM().put("msg", msg.toString());
		}
	}

}
