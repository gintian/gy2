package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BatchUpdateManagerTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
        
    	//ArrayList opinlist=(ArrayList)this.getFormHM().get("opinlist");
    	ArrayList opinlist=(ArrayList)this.getFormHM().get("selectedinfolist");		
    	if(opinlist==null||opinlist.size()==0)
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.noselect.manager"),"",""));
		
		String kq_type=(String)this.getFormHM().get("kq_type");
		String kq_code=(String)this.getFormHM().get("kq_code");
		ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
		String userOrgId=managePrivCode.getPrivOrgId();  
		KqParameter para=new KqParameter(this.getFormHM(),this.userView,userOrgId,this.getFrameconn());
		String kq_cardno=para.getCardno();
		if(kq_type==null||kq_type.length()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.type.nosave"),"",""));
		}
		StringBuffer update=null;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	String cardno="";
    	KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
    	Calendar now = Calendar.getInstance();
		Date cur_d=now.getTime();
		StringBuffer updateQ03=new StringBuffer();
    	for(int i=0;i<opinlist.size();i++)
		{
    		LazyDynaBean rec=(LazyDynaBean)opinlist.get(i); 
			String nbase=rec.get("nbase").toString();
			String a0100=rec.get("a0100").toString();
			update=new StringBuffer();
			update.append("update "+nbase+"A01 set");
			update.append(" "+kq_type+"='"+kq_code+"'");
			
			if(kq_code!=null&& "02".equals(kq_code)&&kq_cardno!=null&&kq_cardno.length()>0)
			{
				cardno=kqCardLength.getCardOneNo(a0100,nbase,kq_cardno);
				if(cardno!=null&&cardno.length()>0)
				update.append(","+kq_cardno+"='"+cardno+"'");	
			}
			update.append(" where 1=1 ");
			update.append(" and a0100='"+a0100+"'");
			String q03z0Str=DateUtils.format(cur_d,"yyyy.MM.dd");
			updateQ03.delete(0, updateQ03.length());
    		updateQ03.append("update q03 set");
    		updateQ03.append(" q03z3='"+kq_code+"'");
    		updateQ03.append(" where a0100='"+a0100+"'");
    		updateQ03.append(" and nbase='"+nbase+"'");
    		updateQ03.append(" and q03z0>='"+q03z0Str+"'");
			try
			{
				dao.update(update.toString());
				dao.update(updateQ03.toString());
			}catch(Exception e)
			{
			  e.printStackTrace();
			  throw GeneralExceptionHandler.Handle(e);
			}
		}
    }

}
