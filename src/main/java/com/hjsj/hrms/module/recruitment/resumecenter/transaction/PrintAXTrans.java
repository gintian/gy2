package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.PrintResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class PrintAXTrans  extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	@Override
    public void execute() throws GeneralException {
		ArrayList persList=(ArrayList)this.getFormHM().get("pers");
		if(persList==null||persList.size()<=0)
			return;
		String z0301 = (String)this.getFormHM().get("z0301");
		z0301 = PubFunc.decrypt(z0301);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=new ArrayList();
		try
		{
			String pers="";
			String[] mess;
			String a0100="";
			String nbase="";
			String sql="";
			String tag="";
		    for(int i=0;i<persList.size();i++)
		    {
		    	pers=(String)persList.get(i);
		    	if(pers==null||pers.length()<=0)
		    		continue;
		    	mess=pers.split("`");
		    	if(mess==null||mess.length!=2)
		    		continue;
		    	nbase=mess[0];
		    	a0100=mess[1];
		    	nbase = PubFunc.decrypt(nbase);
		    	a0100 = PubFunc.decrypt(a0100);
		    	
		    	sql="select a0101 from "+nbase+"A01 where a0100='"+a0100+"'";
		    	this.frowset=dao.search(sql);
		    	if(this.frowset.next())
		    	{
		    		tag="<NBASE>"+nbase+"</NBASE><ID>"+a0100+"</ID><NAME>"+this.frowset.getString("a0101")+"</NAME>";
		    		CommonData dataobj = new CommonData(tag,a0100);
				    list.add(dataobj);
		    	}
		    }
		    this.getFormHM().put("nbase",nbase);
		    PrintResumeBo printResumeBo = new PrintResumeBo(this.frameconn,this.userView);
		    //默认获取第一个人的模板id
		    String cardid = "#";
		    if(StringUtils.isNotEmpty(z0301))
				cardid = printResumeBo.getResumeTemplateId("",z0301);
		    if(persList.size()>0 && persList!=null&&StringUtils.isEmpty(z0301)){
		    	pers = (String)persList.get(0);
		    	if(pers!=null||pers.length()>0){
			    	 mess=pers.split("`");
			    	 if(mess!=null||mess.length==2){
			    		 a0100=mess[1];
			    		 cardid = printResumeBo.getResumeTemplateId(PubFunc.decrypt(a0100));
			    	 }
			    }
		    }
		    this.getFormHM().put("cardid",cardid);	 
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);  
		}
		this.getFormHM().put("personlist",list);
	}

}
