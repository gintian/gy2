package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
/**
 * 保存考勤方式并对没有分配卡号的人，分配卡号
 * <p>Title:SaveKqTypeTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 13, 2007 1:39:31 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SaveKqTypeTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
		String a0100=(String)this.getFormHM().get("a0100");
		String kq_type=(String)this.getFormHM().get("kq_type");
		String kq_cardno=(String)this.getFormHM().get("kq_cardno");
		String nbase=(String)this.getFormHM().get("nbase");
		String type_value=(String)this.getFormHM().get("type_value");
		String rowN=(String)this.getFormHM().get("rowN");		
		String isCreate=(String)this.getFormHM().get("isCreate");
		if(isCreate==null||isCreate.length()<=0)
			isCreate="yes";
		ArrayList list=new ArrayList();
    	StringBuffer sql=new StringBuffer();
    	sql.append("update "+nbase+"A01 set");
    	if(kq_type!=null&&kq_type.length()>0)
    	{
    		sql.append(" "+kq_type+"=?");
    		list.add(type_value);
    	}
    	String cardno="";
    	if("yes".equals(isCreate))
    	{
    		KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
        	if(type_value!=null&& "02".equals(type_value))
        	{
        		cardno=kqCardLength.getCardOneNo(a0100,nbase,kq_cardno);
            	if(cardno!=null&&cardno.length()>0)
            	{
            		sql.append(","+kq_cardno+"=?");
            		list.add(cardno);
            	} 
        	}
    	}
    	
    	   
    	sql.append(" where a0100='"+a0100+"'");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	if(list!=null&&list.size()>0)
    	{
    		
    		try
    		{
    			dao.update(sql.toString(),list);
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	try
    	{
    		StringBuffer updateQ03=new StringBuffer();
        	Calendar now = Calendar.getInstance();
    		Date cur_d=now.getTime();
    		String q03z0Str=DateUtils.format(cur_d,"yyyy.MM.dd");
    		updateQ03.append("update q03 set");
    		updateQ03.append(" q03z3='"+type_value+"'");
    		updateQ03.append(" where a0100='"+a0100+"'");
    		updateQ03.append(" and nbase='"+nbase+"'");
    		updateQ03.append(" and q03z0>='"+q03z0Str+"'");
    		dao.update(updateQ03.toString());
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		
    	this.getFormHM().put("rowN",rowN);
    	this.getFormHM().put("cardno",cardno);
	}
	
}
