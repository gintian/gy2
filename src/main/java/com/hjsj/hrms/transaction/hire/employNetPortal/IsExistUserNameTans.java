package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsExistUserNameTans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			String info="0";
			String txtEmail=(String)this.getFormHM().get("txtEmail");//注册邮箱	
			String paramFlag=(String)this.getFormHM().get("paramFlag");
			String dbName="";
			String password=(String)this.getFormHM().get("password");//密码
			password=PubFunc.hireKeyWord_filter_reback(password);
			String txtName=(String)this.getFormHM().get("txtName");//注册姓名
            String onlyValue=(String)this.getFormHM().get("onlyValue");//唯一性指标值
			String blackFieldValue=(String)this.getFormHM().get("blackFieldValue");//黑名单指标值
            onlyValue=PubFunc.getReplaceStr(onlyValue);
			txtEmail=PubFunc.getReplaceStr(txtEmail);
			paramFlag=PubFunc.getReplaceStr(paramFlag);
			Class returnType =blackFieldValue.getClass();

//			password=PubFunc.getReplaceStr(password);
		    this.frowset=dao.search("select str_value  from constant where constant='ZP_DBNAME'");
		    if(this.frowset.next())
		    {
		    	dbName=this.frowset.getString("str_value");
		    }
		    else
		    	throw GeneralExceptionHandler.Handle(new Exception("后台参数没有设置应聘人才库"));
		    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");
			String blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");
			EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn());
			/**黑名单检查*/
			if(!"3".equals(paramFlag)&&blacklist_field!=null&&!"".equals(blacklist_field)&&blacklist_per!=null&&!"".equals(blacklist_per))
			{

				
				if(bo.isBlackPerson(blacklist_field, blacklist_per, blackFieldValue))
				{
					/*String msg="您已经被系统列入黑名单，不能提交简历！";
					throw GeneralExceptionHandler.Handle(new Exception(msg));*/
					info="2";
				}
			}
			if(!"1".equals(paramFlag))
			{
				String onlyname = bo.getOnly_field();
			//	String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name")==null?"":sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			//	String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid")==null?"":sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//=1

                this.frowset=dao.search("select * from "+dbName+"A01 where UPPER("+onlyname+")='"+onlyValue.toUpperCase()+"'");
                while(this.frowset.next())
                {
                	info="3";
                }
                if("3".equals(paramFlag)&& "0".equals(info))
                {
                //	EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn());
    				if(bo.isBlackPerson(onlyname, blacklist_per, onlyValue))
    				{
    					/*String msg="您已经被系统列入黑名单，不能提交简历！";
    					throw GeneralExceptionHandler.Handle(new Exception(msg));*/
    					info="2";
    				}
                }
			}
			this.frowset=dao.search("select * from "+dbName+"A01 where userName='"+txtEmail+"'");
			if(this.frowset.next())
				info="1";
			String onlyNameDesc=(String)this.getFormHM().get("onlyNameDesc");
			this.getFormHM().put("onlyNameDesc", onlyNameDesc);
			String person_type=(String)this.getFormHM().get("person_type");
			this.getFormHM().put("person_type", person_type);
			String acountBeActived = "0";
			String complexPassword="";
			String passwordMinLength="";
			String passwordMaxLength="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=bo2.getAttributeValues();
			if(map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0)
				acountBeActived=(String)map.get("acountBeActived");
			if(map.get("complexPassword")!=null&&((String)map.get("complexPassword")).length()>0)
				complexPassword=(String)map.get("complexPassword");
			if(map.get("passwordMinLength")!=null&&((String)map.get("passwordMinLength")).length()>0)
				passwordMinLength=(String)map.get("passwordMinLength");
			if(map.get("passwordMaxLength")!=null&&((String)map.get("passwordMaxLength")).length()>0)
				passwordMaxLength=(String)map.get("passwordMaxLength");
			
			/**验证密码是否符合规则**/
			if("1".equals(complexPassword)&& "0".equals(info)){
				if(password==null){
				   info="密码不能为空";
				}else if(password.length()<Integer.parseInt(passwordMinLength)||password.length()>Integer.parseInt(passwordMaxLength)){
					info="密码长度应为"+passwordMinLength+"-"+passwordMaxLength+"位!"; 
				}else{
					int numasc = 0;
					int charasc = 0;
					int otherasc = 0;
					for(int i=0;i<password.length();i++){
						byte[] by=password.substring(i,i+1).getBytes();
						if(by[0]>= 48 && by[0]<= 57){
							numasc+=1;
						}
		                if ((by[0] >= 65 && by[0] <= 90)||(by[0] >= 97 && by[0] <= 122)) {
		                    charasc += 1;
		                } 
		                if ((by[0] >= 33 && by[0] <= 42)||by[0] == 64 ||by[0] == 94 ||by[0] == 126 ) {
		                    otherasc += 1;
		                }
		            }
		            if(numasc==0)  {
		            	info="密码必须含有数字";
		            }else if(charasc==0){
		            	info="密码必须含有字母";
		            }else if(otherasc==0){
		            	info="密码必须含有特殊字符(%$#@!~^&*()')";
		            }
				}
			}
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn(),"1");
            HashMap Validatemap=parameterXMLBo.getAttributeValues();
            String MAIL_VALDATE=(String) Validatemap.get("MAIL_VALDATE");
			if("1".equals(acountBeActived)&&MAIL_VALDATE==null){
			    EMailBo emb=null;
                try
                {
                   emb= new EMailBo(this.getFrameconn(),true,"","scxybtide");
                   Validatemap.put("MAIL_VALDATE", "1");
                }
                catch(Exception e)
                {
                   e.printStackTrace();
                   info="账号注册失败，请检查网络连接是否正常或者电子邮箱录入是否正确！";
                }
			}
			/**验证邮箱长度是否大于定义长度**/
			if(txtEmail.length()>50){
				info="邮箱长度大于系统定义的50字节长度";
			}
			/**验证姓名长度是否大于定义长度**/
			if("0".equals(info))
			info=this.validateLength("A0101", txtName, info);
			/**验证唯一性指标长度是否大于定义长度**/
			if(bo.getOnly_field()!=null&& "0".equals(info))
			info=this.validateLength(bo.getOnly_field(), onlyValue, info);
			/**验证黑名单长度是否大于定义长度**/
			if(blacklist_field!=null&& "0".equals(info))
			info=this.validateLength(blacklist_field, blackFieldValue, info);
			/**验证姓名数据类型**/
			if("0".equals(info))
			info=this.validateType("A0101", txtName, info);
			/**验证黑名单数据类型**/
			/**验证唯一性指标数据类型**/
			this.getFormHM().put("acountBeActived", acountBeActived);
			this.getFormHM().put("info",info);
			this.getFormHM().remove("password"); //20140812 基于安全考虑，避免返回信息中带有password信息
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 验证输入字段长度
	 * @param key 输入字段对应代码名称
	 * @param value 输入值
	 * @param info  提示信息
	 * @return
	 */
	public String validateLength(String key,String value,String info){
		FieldItem item=null;
		item=DataDictionary.getFieldItem(key);
		value=value==null?"":value;
		byte[] b=value.getBytes();
		if(item!=null&&value!=null){
			if(b.length>item.getItemlength()){
				info="注册"+item.getItemdesc()+"长度大于系统定义的"+item.getItemlength()+"字节长度";
			}
		}
		return info;
	}
	/**
	 * 验证输入姓名的数据类型
	 * @param key 输入字段对应代码名称
	 * @param value 输入值
	 * @param info 提示信息
	 * @return
	 */
	public String validateType(String key,String value,String info){
		FieldItem item=null;
		item=DataDictionary.getFieldItem(key);
		if(item!=null&&value!=null){
			String itemType=item.getItemtype();
			if("true".equalsIgnoreCase(value)|| "false".equalsIgnoreCase(value)){
				info=item.getItemdesc()+"不能为布尔型数据";
			}
			 String eL= "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";   
		     Pattern p = Pattern.compile(eL);
		     Matcher m = p.matcher(value.trim());    
			 if(m.matches()){
					info=item.getItemdesc()+"不能为日期";
			 }
			 eL="^[-+]?\\d+(\\.\\d+)?$";//^(-?\d+)(\.\d+)? 
			 p = Pattern.compile(eL);
			 m = p.matcher(value.trim());  
			 if(m.matches()){
				 info=item.getItemdesc()+"不能为数字";
			 }

//			if(itemType.equalsIgnoreCase("A")){
//				
//			}else if(itemType.equalsIgnoreCase("N")){
//				
//			}
		}
		return info;
	}
}
