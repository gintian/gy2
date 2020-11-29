package com.hjsj.hrms.transaction.kq.kqself.kq_class;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.team.KqShiftClass;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class KqShiftTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
		String session_date=(String)this.getFormHM().get("session_data");
		ArrayList sessionlist=(ArrayList)this.getFormHM().get("sessionlist");
		if(!(this.userView.getA0100()!=null&&this.userView.getA0100().length()>0))
		  throw new GeneralException(ResourceFactory.getProperty("employ.no.use.model"));
		if(sessionlist==null||sessionlist.size()<=0)
		{
			   sessionlist=RegisterDate.sessionDate(this.getFrameconn());
		}
		String cur_date="";
		if(session_date!=null&&session_date.length()>0)
		{
			   cur_date=session_date;
		}else
		{
			if(sessionlist==null||sessionlist.size()<=0)
			{
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
			}else
			{
				CommonData vo=(CommonData)sessionlist.get(0);
				cur_date=vo.getDataValue();
			}
		}   
		ArrayList  datelist=RegisterDate.getOneDurationDateList(this.getFrameconn(),cur_date,"","");
		KqShiftClass kqShiftClass=new KqShiftClass(this.getFrameconn(),this.userView);
		String nbase=this.userView.getDbname();
		String a_code="EP"+this.userView.getA0100();
		kqShiftClass.setSelf(true);
		String table_html=kqShiftClass.returnShiftHtml(datelist,a_code,nbase);
		this.getFormHM().put("session_data",cur_date);
	    this.getFormHM().put("table_html",table_html);
	    this.getFormHM().put("sessionlist",sessionlist);
	    this.getFormHM().put("a_code",a_code);
	    this.getFormHM().put("nbase",nbase);
	    this.getFormHM().put("datelist",datelist);
	}

}
