package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
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

		ConstantXml constantbo = new ConstantXml(this.frameconn,"TR_PARAM");
		 String tmpnbase = constantbo.getTextValue("/param/post_traincourse/nbase");
		 if(tmpnbase == null || tmpnbase.length()<1)
			 throw GeneralExceptionHandler.Handle(new Exception("未设置人员库！<br><br>请到   培训管理>参数设置>其它参数>岗位培训指标设置   中设置人员库。"));
		 
		String checkflag = (String)reqhm.get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"1";
		reqhm.remove("checkflag");
		
		String itemkey = (String)reqhm.get("itemkey");
		itemkey=itemkey!=null&&itemkey.trim().length()>0?itemkey:"";
		reqhm.remove("itemkey");
		
		String nbase = (String)reqhm.get("nbase");
		nbase=nbase!=null&&nbase.trim().length()>0?nbase:"all";
		reqhm.remove("nbase");
		
		String a_code = (String)reqhm.get("a_code");
		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"UN";
		reqhm.remove("a_code");

		ArrayList setlist= new ArrayList();;
		if("1".equals(checkflag))//培训学员
			setlist=this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		ArrayList fieldsetlist = new ArrayList();
		for(int i=0;i<setlist.size();i++){
			FieldSet fieldset = (FieldSet)setlist.get(i);
			if("0".equalsIgnoreCase(fieldset.getUseflag()))
				continue;
			if("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;	
			if("B00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			if("K00".equalsIgnoreCase(fieldset.getFieldsetid()))
				continue;
			CommonData obj=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
			fieldsetlist.add(obj);
		}
		this.getFormHM().put("setlist",fieldsetlist);
		this.getFormHM().put("checkflag",checkflag);
		this.getFormHM().put("itemkey",itemkey);
		this.getFormHM().put("nbase",nbase);
		this.getFormHM().put("a_code",a_code);
	}

}
