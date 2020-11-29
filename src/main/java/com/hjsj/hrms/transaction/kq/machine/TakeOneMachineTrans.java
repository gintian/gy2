package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 取得考勤机信息
 * <p>Title:TakeOneMachineTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 6, 2007 5:15:07 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class TakeOneMachineTrans extends IBusiness {

	
	public void execute() throws GeneralException 
	{
		String machine_num=(String)this.getFormHM().get("machine_num");
		StringBuffer sql=new StringBuffer();
		sql.append("select machine_no,baud_rate,port,type_id,ip_address,card_len from kq_machine_location");
		sql.append(" where location_id='"+machine_num+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String machine_no="";//机号
		String baud_rate="";
		String port="";
		String type_id="";
		String ip_address="";
		String card_len="";
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				machine_no=this.frowset.getString("machine_no")==null?"":this.frowset.getString("machine_no");
				baud_rate=this.frowset.getString("baud_rate")==null?"":this.frowset.getString("baud_rate");
				port= this.frowset.getString("port")==null?"":this.frowset.getString("port");
				type_id=this.frowset.getString("type_id")==null?"":this.frowset.getString("type_id");
				ip_address=this.frowset.getString("ip_address")==null?"":this.frowset.getString("ip_address");
				card_len=this.frowset.getString("card_len");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("machine_no",machine_no);
		this.getFormHM().put("baud_rate",baud_rate);
		this.getFormHM().put("port",port);
		this.getFormHM().put("type_id",type_id);
		this.getFormHM().put("ip_address",ip_address);
		if(card_len==null||card_len.length()<=0)
		{
			KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
	    	int int_id_len=kqCardLength.tack_CardLen();
	    	this.getFormHM().put("cardno_len",int_id_len+"");
		}else
		{
			this.getFormHM().put("cardno_len",card_len);
		}
		
    	
	}

}
