package com.hjsj.hrms.transaction.sys.sms;


import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CommaMailListTrans extends IBusiness {

	public void execute() throws GeneralException {
		String startime=(String) this.getFormHM().get("startime");
		startime=startime==null||startime.length()<1?"":startime;
		String endtime=(String) this.getFormHM().get("endtime");
		endtime=endtime==null||endtime.length()<1?"":endtime;
		
		StringBuffer where=new StringBuffer();
		StringBuffer str=new StringBuffer();
		String coumu="";
	    str.append("select sms_id,sender,mobile_no,status,msg,send_time,a0100,nbase");
        coumu="sms_id,sender,mobile_no,status,msg,send_time,a0100,nbase,";

	    where.append(" from t_sys_smsbox where status='2'");
	    if(startime.length()>0)
	    	where.append(" and send_time >= "+Sql_switcher.dateValue(startime));
	    if(endtime.length()>0)
	    	where.append(" and send_time <= "+Sql_switcher.dateValue(endtime+" 23:59:59"));
	    this.getFormHM().put("str",str.toString());
	    
	    this.getFormHM().put("conum",coumu);
	    this.getFormHM().put("where",where.toString());
	    this.getFormHM().put("order", "order by send_time desc");
	    // 查询登记表的id
	    this.getFormHM().put("cardid", getCardid());

	}
	
	/**
	 * 获得登记表id
	 * @return
	 */
	private String getCardid() {
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String cardid = "-1";
		try {
			cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(cardid==null|| "".equalsIgnoreCase(cardid)
				|| "#".equalsIgnoreCase(cardid))  {
			 cardid="-1";
		}
		
		return cardid;
	}

}
