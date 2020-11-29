package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.sys.ValidateDateFilled;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CheckDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		UserView uv=this.getUserView();
		ArrayList setlist=uv.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET,0);
		StringBuffer citem=new StringBuffer();
		StringBuffer cset=new StringBuffer();
		ContentDAO dao =new ContentDAO(this.getFrameconn());
		
		String  pdbflag=(String) hm.get("pdbflag");
        String  a0100=(String)hm.get("a0100");
        String selfinfo = (String)hm.get("selfinfo");
        selfinfo = selfinfo == null ? "0" : selfinfo;
        
		if("1".equals(selfinfo))
		{
		    pdbflag = userView.getDbname();
		    a0100 = userView.getA0100();
		}
		else
		{
		    CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
		    String privA0100 = cps.checkA0100("", pdbflag, a0100, "");
		    if(!privA0100.equals(a0100))
		    {
		       throw GeneralExceptionHandler.Handle(new GeneralException("您无权操作该人员信息！"));
		    }
		}
		
		for(Iterator it=setlist.iterator();it.hasNext();){
			FieldSet fs=(FieldSet)it.next();
			if(!"A00".equals(fs.getFieldsetid())){
				ValidateDateFilled vdf=new ValidateDateFilled(uv,fs.getFieldsetid());
			
			
			String sql="select * from "+pdbflag+fs.getFieldsetid()+" where a0100='"+a0100+"'";
			ArrayList dynalist=dao.searchDynaList(sql);
			if(dynalist==null||dynalist.size()<=0){
				String message=vdf.getFieldsetdesc()+"指标集中没有记录！是否要报批？\\n";
				cset.append(message);
			}else{
				String message=vdf.getValidate(dynalist);
				citem.append(message);
			}
			}
		}
		hm.put("cset",cset.toString());
		hm.put("citem",citem.toString());
	}

}
