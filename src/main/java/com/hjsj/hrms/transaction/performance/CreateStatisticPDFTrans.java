package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.ykcard.YkcardPdf;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 生成计划登记的PDF
 * <p>Title:CreateStatisticPDFTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 23, 2006 9:56:49 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class CreateStatisticPDFTrans  extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		String nid=(String)this.getFormHM().get("nid");	
		YkcardPdf   ykcardPdf=new YkcardPdf(this.getFrameconn());
		String infokind=(String)this.getFormHM().get("infokind");
        String tabid=(String)this.getFormHM().get("tabid");
        String cyear=(String)this.getFormHM().get("cyear");	
		String querytype=(String)this.getFormHM().get("querytype");	
		String cmonth=(String)this.getFormHM().get("cmonth");	
		String userpriv=(String)this.getFormHM().get("userpriv");	
		String istype=(String)this.getFormHM().get("istype");              /*0代表薪酬1登记表*/	
		String season=(String)this.getFormHM().get("season");	
		String ctimes=(String)this.getFormHM().get("ctimes");	
		String cdatestart=(String)this.getFormHM().get("cdatestart");	
		String cdateend=(String)this.getFormHM().get("cdateend");			
		String cardid=(String)this.getFormHM().get("cardid");		
		String userbase=(String)this.getFormHM().get("userbase");
		/***/
		String plan_id=(String)this.getFormHM().get("plan_id");
		try{
			
			ykcardPdf.setPlan_id(plan_id);
			
		    String url=ykcardPdf.executePdf(Integer.parseInt(tabid),nid,userbase,this.userView,cyear,querytype,cmonth,userpriv,istype,season,ctimes,cdatestart,cdateend,"5","","");
		    //liuy 2015-1-30 7142：自助服务/绩效考评/考评反馈/本人考核结果：按照“登记表”查看，输出pdf，页面空白 start 
		    url = PubFunc.encrypt(url);
		    //liuy 2015-1-30 end
		    this.getFormHM().put("url",url);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
