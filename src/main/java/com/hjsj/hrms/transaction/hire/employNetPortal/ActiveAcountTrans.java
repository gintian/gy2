package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ActiveAcountTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap amap = (HashMap)this.getFormHM().get("requestPamaHM");
			String ativeid=(String)amap.get("activeid");
			ativeid = PubFunc.keyWord_reback(ativeid);
			String activeDate=(String)amap.get("activeDate");//发送激活邮件的时间
			activeDate=PubFunc.decryption(activeDate);
       	 	Calendar calendar = Calendar.getInstance(); 
       	 	Date date =  calendar.getTime(); //激活的时间
       	 	SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       	 	String activedDate =  format1.format(date);
       	 	Date date1=format1.parse(activeDate);
       	 	Date date2=format1.parse(activedDate);
       	 	long dmm = date2.getTime()-date1.getTime();
       	 	int hour =(int) dmm/1000/60/60;
       	 	if(hour>=1){
       	 		throw GeneralExceptionHandler.Handle(new Exception("已超过有效激活时间，请重新索取激活邮件以激活账号!"));
       	 	}
			ativeid=PubFunc.convert64BaseToString(ativeid);
			ativeid=PubFunc.getReplaceStr(ativeid);
			
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			
			//zxj 20180108  取logo类型等参数，否则，激活页面页头banner会默认为SWF
			String lfType="1";
            String hbType="1";
            if(map!=null&&map.get("lftype")!=null)
                lfType=(String)map.get("lftype");
            if(map!=null&&map.get("hbtype")!=null)
                hbType=(String)map.get("hbtype");
            this.getFormHM().put("lfType", "".equals(lfType.trim())?"1":lfType);
            this.getFormHM().put("hbType", "".equals(hbType.trim())?"1":hbType);
            
			String isAttach="0";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			
			EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			String dbName=bo.getZpkdbName();
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList<String> value = new ArrayList<String>();
			value.add(ativeid);
			this.frowset=dao.search("select username,userpassword,state from "+dbName+"A01 where a0100=?", value);
			String username="";
			String password="";
			String state="";
			while(this.frowset.next())
			{
				username = this.frowset.getString("username");
				password = this.frowset.getString("userpassword");
				state = this.frowset.getString("state");
			}
			if("1".equals(state)){
				throw GeneralExceptionHandler.Handle(new Exception("账号已激活，无需重复激活!"));
			}
			
			dao.update("update "+dbName+"A01 set state='1' where a0100=?", value);
			HttpSession session=(HttpSession)this.getFormHM().get("session");
			session.setAttribute(WebConstant.isLogon, new Boolean(true));             
            UserView userview=new UserView(username,password,this.getFrameconn());            
            userview.setUserId(ativeid);
            session.setAttribute(WebConstant.userView, userview);
            
            String applyMessage=bo.getApplyMessage(ativeid);
            this.getFormHM().put("applyMessage",applyMessage);
			this.getFormHM().put("loginName",username);
			this.getFormHM().put("password",password);
			this.getFormHM().put("a0100", ativeid);
			this.getFormHM().put("dbName", dbName);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
