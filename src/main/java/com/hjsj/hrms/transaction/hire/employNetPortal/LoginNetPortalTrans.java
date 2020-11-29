package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.Md5ForHire;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LoginNetPortalTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap mm=(HashMap)this.getFormHM().get("requestPamaHM");
		if(mm==null)
		mm=new HashMap();
		String loginName=(String)this.getFormHM().get("loginName");
		String password=(String)this.getFormHM().get("password");
		loginName=PubFunc.getReplaceStr(loginName);
		if(loginName==null||loginName.trim().length()==0)//无登录名 不准登录
			return;
		password=PubFunc.getReplaceStr(password);
		String operate=(String)this.getFormHM().get("operate");
		ParameterXMLBo bo2=new ParameterXMLBo(this.getFrameconn(),"1");
		HashMap map=bo2.getAttributeValues();
		String isAttach = "0";
		if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
			isAttach=(String)map.get("attach");
		String acountBeActived = "0";
		if(map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0)
			acountBeActived=(String)map.get("acountBeActived");
		HttpSession session=(HttpSession)this.getFormHM().get("session");
		
		//2016/2/15  wangjl 旧版招聘页面 问题 16556 昆明医科大学：应聘界面，第一次登陆成功后，退出，在不清理浏览器缓存的情况下不能登陆进去
		boolean isforward=false;
	    int version_flag=0;
	    EncryptLockClient lockclient = null;
	    this.getFormHM().get("");
	    if(session!=null){
	    	lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
		    if(lockclient!=null){
			    version_flag=lockclient.getVersion_flag();
			       if(lockclient.getVersion()>=50) //zxj 20160613 招聘不区分标准版专业版 &&version_flag==1
			           isforward=true;
		   }
	    }
	    if(isforward&&version_flag==1){
			//20140812 注释掉防止刷新 loginName、password为空登录
			if(session.getAttribute(WebConstant.userView)!=null&&((UserView)session.getAttribute(WebConstant.userView)).getUserEmail().equalsIgnoreCase(loginName))
			 	return;
	    }
	    
		String login2=(String)this.getFormHM().get("login2");
		try
		{
			EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
			String dbName=bo.getZpkdbName();			
			String a0100="";
			String userName="";
			String person_type="";
			String info="0";
			/**
			String resume_state="";
			if(map.get("resume_state")!=null&&((String)map.get("resume_state")).length()>0)
			{
				resume_state=(String)map.get("resume_state");
			}
			if(resume_state==null||resume_state.equals(""))
				throw GeneralExceptionHandler.Handle(new Exception("系统运行错误，请联系系统管理员！"));
			**/
			String  person_type_field="";
		
			
			if(map!=null&&map.get("person_type")!=null)
				person_type_field=((String)map.get("person_type")).toLowerCase();
			
			if(person_type_field==null|| "".equals(person_type_field.trim()))
			{
		    	throw GeneralExceptionHandler.Handle(new Exception("用户名 或者 密码 不正确！"));
				
			}
			String isDefinitionActive=(String)this.getFormHM().get("isDefinitionActive");
			if(isDefinitionActive==null)
				isDefinitionActive="2";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sql=new StringBuffer();
			String field="";
			sql.append("select a0101,a0100"); 
			String workExperience=bo.getWorkExperience();
			String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
			String value="";
			if("1".equals(isDefinitionActive))
			{
				field=(String)map.get("active_field");
				sql.append(","+field);
			}
			if("1".equals(isDefineWorkExperience))
			{
				sql.append(","+workExperience);
			}
			if("1".equals(acountBeActived))
			{
				sql.append(",state ");
			}
			String acountActivedValue="1";
			sql.append(" from "+dbName+"a01 where lower(userName)=lower(?) and UserPassword=?");
			ArrayList<String> values = new ArrayList<String>();
			values.add(PubFunc.getStr(loginName));
			values.add(PubFunc.getStr(password));
			this.frowset=dao.search(sql.toString(),values);
		    if(this.frowset.next())
		    {
		    	userName=this.frowset.getString("a0101");
		    	a0100=this.frowset.getString("a0100");
//		    	person_type=this.frowset.getString(person_type_field);
		    	if("1".equals(isDefinitionActive))
		    	{
		    		this.getFormHM().put("activeValue", this.frowset.getString(field)==null?"1":this.frowset.getString(field));
		    	}
		    	if("1".equals(isDefineWorkExperience))
		    	{
		    		value=this.frowset.getString(workExperience)==null?"":this.frowset.getString(workExperience);
		    	}
		    	if("1".equals(acountBeActived))
				{
		    		if(this.frowset.getString("state")==null)//如果为空的，算以激活的状态
		    		{
		    			acountActivedValue="1";
		    		}
		    		else{
		    			acountActivedValue=this.frowset.getString("state");
		    		}
				}
		    	info="1";
		    }
		    if("1".equals(acountBeActived)&& "0".equals(acountActivedValue))
		    {
		    	info="5";
		    }
		    String corcode="";
		    String jobname="";
		    String code=bo.getcorcode(a0100);
		    String hasapply="1";
		    if(code==null|| "false".equalsIgnoreCase(code)){
		    	hasapply="1";
		    }else{
		    	if(code.indexOf("&")!=-1){
		    		String[] a=code.split("&");
		    		if(a.length<2){
		    			hasapply="1";
		    		}else{
			    		corcode=a[1];
			    		jobname=a[0];
			    		hasapply="2";
		    		}
		    	}
		    }
		    String previewTableId = "";
		    if(map!=null&&map.get("preview_table")!=null)
			{
				previewTableId = (String)map.get("preview_table");
			}
		    this.getFormHM().put("previewTableId", previewTableId);
		    
		    this.getFormHM().put("admissionCard",(String)map.get("admissionCard"));
		    
			String hdt =SystemConfig.getPropertyValue("hdtconnect");
			if(hdt!=null&& "true".equalsIgnoreCase(hdt)){
				hasapply="2";
			}else{
				hasapply="1";
			}
		    Calendar cd=Calendar.getInstance();
			String yy=""+cd.get(Calendar.YEAR);
			String mm1=cd.get(Calendar.MONTH)+1<=9?"0"+(cd.get(cd.MONTH)+1):(cd.get(cd.MONTH)+1)+"";
			String dd=cd.get(Calendar.DATE)<=9?"0"+cd.get(Calendar.DATE):cd.get(Calendar.DATE)+"";
			String partime=yy+mm1+dd;
			String cer="";
			Md5ForHire md5 =new Md5ForHire();
			String keycode="klskuge9723kgs8772k3";
			this.getFormHM().put("loginName",loginName);
			this.getFormHM().put("cer",cer);
			this.getFormHM().put("corcode",corcode);
			this.getFormHM().put("jobid",corcode);
			this.getFormHM().put("jobname",jobname);
			this.getFormHM().put("isapply",hasapply);
			cer=md5.getMD5((loginName+keycode+partime).getBytes());
			this.getFormHM().put("hdtusername",loginName);
			this.getFormHM().put("cer",cer);
			String writeable=bo.getWriteable(dao, a0100, dbName);
			this.getFormHM().put("person_type",person_type);
		    this.getFormHM().put("userName",userName);
			this.getFormHM().put("dbName",dbName);
			this.getFormHM().put("a0100",a0100);
			this.getFormHM().put("info",info);
			this.getFormHM().put("isDefinitionActive", isDefinitionActive);
			/**简历是否可修改*/
			this.getFormHM().put("writeable", writeable);
			if(login2!=null&&login2.trim().length()!=0){
				String z0301=(String)this.getFormHM().get("z0301");
			}
			String isOnlyChecked="0";
			String onlyField="";
			if(bo.isOnlyChecked())
			{
				isOnlyChecked="1";
				onlyField=EmployNetPortalBo.isOnlyChecked;
			}
			this.getFormHM().put("onlyField",onlyField);
			this.getFormHM().put("isOnlyCheck", isOnlyChecked);
			if(session!=null)
			{
			 UserView userview=new UserView(loginName,password,this.getFrameconn());
	         userview.setUserId(a0100);
	         userview.setA0100(a0100);
	         userview.setUserEmail(loginName);
	         userview.getHm().put("isEmployee","1");
	         userview.getHm().put("isHeadhunter","0");//是否是猎头登录用户  0：不是     1：是
	       //  session.setAttribute(WebConstant.isLogon, new Boolean(true));
	         session.setAttribute(WebConstant.userView, userview);
			}
			
			if(operate==null||!"ajax".equals(operate))
			{
				ArrayList list=bo.getZpFieldList();
				this.getFormHM().put("fieldSetList",(ArrayList)list.get(0));
				this.getFormHM().put("fieldMap",(HashMap)list.get(1));
				this.getFormHM().put("currentSetID","0");
				
			}
			else
			{
				if(this.getFormHM().get("sAction")!=null)
				{
		    		String sAction=(String)this.getFormHM().get("sAction");
		    		String fAction=(String)this.getFormHM().get("fAction");
		    		this.getFormHM().put("sAction",sAction);
		    		this.getFormHM().put("fAction",fAction);
				}
			}
			this.getFormHM().put("workExperience", value);
			
			this.getFormHM().remove("password"); //20140812 基于安全考虑，避免返回信息中带有password信息
//			this.getFormHM().put("loginName", ""); 2013/11/13 注释掉防止刷新 loginName、password为空登录
//			this.getFormHM().put("password", "");
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}


}
