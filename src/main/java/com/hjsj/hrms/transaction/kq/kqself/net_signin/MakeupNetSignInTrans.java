package com.hjsj.hrms.transaction.kq.kqself.net_signin;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

public class MakeupNetSignInTrans extends IBusiness{
	   
	   public void execute()throws GeneralException
	   {
		   String singin_flag="0";
		   String card_causation = KqParam.getInstance().getCardCausation();
		   if(card_causation==null||card_causation.length()<=0)
		   {
			   card_causation="";
			   //throw GeneralExceptionHandler.Handle(new GeneralException("","没有定义补刷卡原因代码项，请到考勤参数-结构参数-其他参数中设置对应的代码项！","",""));
		   }
		   KqCardData kqCardData=new KqCardData(this.userView,this.getFrameconn());
		   boolean isInout_flag=kqCardData.isViewInout_flag();
		   this.getFormHM().put("isInout_flag", isInout_flag+"");
		   this.getFormHM().put("card_causation",card_causation);////补刷卡原因代码项
		   NetSignIn netSignIn=new NetSignIn();
		   String makeup_date=netSignIn.getWork_date();
		   this.getFormHM().put("singin_flag",singin_flag);
		   this.getFormHM().put("oper_cause","");
		   this.getFormHM().put("makeup_date",makeup_date);
		   this.getFormHM().put("inout_flag","0");
		   //签到是否限制IP 0：不绑定 1：绑定（默认）2：有IP绑定，无IP不绑定 wangyao
		   String net_sign_check_ip = KqParam.getInstance().getNetSignCheckIP();
		   this.getFormHM().put("net_sign_check_ip",net_sign_check_ip);
		   
		   // linbz 20161114 我的考勤日历传日期参数
		   HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		   
	       String kqempcal = (String)hm.get("kqempcal");
	       kqempcal =  kqempcal == null ? "" : kqempcal;
	       String empcalday = (String)hm.get("empcalday");
	       empcalday =  empcalday == null ? "" : empcalday;
	       String classtime = (String)hm.get("classtime");
	       classtime =  classtime == null ? "" : classtime;
	       if("1".equals(kqempcal) && !"null".equals(empcalday) && StringUtils.isNotEmpty(empcalday)
	    		   && StringUtils.isNotEmpty(classtime) && !("undefined").equalsIgnoreCase(classtime)){
	        	this.getFormHM().put("makeup_date",empcalday);
	        	this.getFormHM().put("makeup_time",classtime.split(" ")[1].toString());
	        	this.getFormHM().put("kqempcal","1");
	       }else{
	    	   this.getFormHM().put("kqempcal","0");
	    	   this.getFormHM().put("makeup_time","");
	       }
	       hm.remove("kqempcal");
	       hm.remove("empcalday");
	       hm.remove("classtime");
		   
	   }
}
