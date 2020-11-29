package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class BatchSendCardTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
    	String kq_gno=(String)this.getFormHM().get("kq_gno");
    	ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
		String org_id=managePrivCode.getPrivOrgId();     	
		KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,org_id,this.getFrameconn());
    	if(kq_cardno==null||kq_cardno.length()<=0)
    	{
    		
    		kq_cardno=kq_paramter.getCardno();
    	}
    	if(kq_gno==null||kq_gno.length()<=0)
    	{
    		kq_gno=kq_paramter.getG_no();
    	}
    	this.getFormHM().put("kq_cardno",kq_cardno);
    	ArrayList order_list=new ArrayList();
    	CommonData vo=null;
		vo = new CommonData();
		vo.setDataName("");
		vo.setDataValue("");
		order_list.add(vo);
		vo = new CommonData();
		vo.setDataName(ResourceFactory.getProperty("kq.emp.a0100"));
		vo.setDataValue("1");
		order_list.add(vo);
		vo = new CommonData();
		vo.setDataName(ResourceFactory.getProperty("kq.emp.a0101"));
		vo.setDataValue("2");
		order_list.add(vo);
		if(kq_gno!=null&&kq_gno.length()>0)
		{
			vo = new CommonData();
			vo.setDataName(ResourceFactory.getProperty("kq.emp.gono"));
			vo.setDataValue("3");
			order_list.add(vo);
		}		
		this.getFormHM().put("order_status","");
		this.getFormHM().put("order_list",order_list);
		this.getFormHM().put("kq_gno",kq_gno);
		this.getFormHM().put("flag", "xxx");
    }

}
