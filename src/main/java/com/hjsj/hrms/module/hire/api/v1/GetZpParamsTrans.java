package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.Counter;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取身份证，出生日期等系统指标
 * @author Administrator
 *
 */
public class GetZpParamsTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
            ResumeBo resumeBo = new ResumeBo(this.frameconn);
			OtherParam param=new OtherParam(this.getFrameconn());
			Map setmap=param.serachAtrr("/param/formual[@name='bycardno']");
			HashMap<String, Object> return_data = new HashMap<String, Object>();
			if(setmap!=null) {
				//身份证
				String cardIdName = setmap.get("src").toString().toLowerCase();
				//出生日期
				String BirthdayName = setmap.get("birthday").toString().toLowerCase();
				//年龄
				String ageName = setmap.get("age").toString().toLowerCase();
				//性别
				String axName = setmap.get("ax").toString().toLowerCase();
				FieldItem caidItem = DataDictionary.getFieldItem(cardIdName, "a01");
				FieldItem birthItem = DataDictionary.getFieldItem(BirthdayName, "a01");
				FieldItem ageItem = DataDictionary.getFieldItem(ageName, "a01");
				FieldItem axItem = DataDictionary.getFieldItem(axName, "a01");
				if(caidItem!=null&&"1".equals(caidItem.getUseflag()))
					return_data.put("caidItem", caidItem);
				if(birthItem!=null&&"1".equals(birthItem.getUseflag()))
					return_data.put("birthItem", birthItem);
				if(ageItem!=null&&"1".equals(ageItem.getUseflag()))
					return_data.put("ageItem", ageItem);
				if(axItem!=null&&"1".equals(axItem.getUseflag()))
					return_data.put("axItem", axItem);
			}
			
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			String complexPassword="";//是否使用复杂密码 0未使用 1使用
			String passwordMinLength="";//密码最小长度
			String passwordMaxLength="";//密码最大长度
			String net_href = "";//logo 链接地址
			String max_count = "0";//最大应聘职位数
			if(map!=null) {
				if(map.get("complexPassword")!=null)
					complexPassword=(String)map.get("complexPassword");
				if(map.get("passwordMinLength")!=null)
					passwordMinLength=(String)map.get("passwordMinLength");
				if(map.get("passwordMaxLength")!=null)
					passwordMaxLength=(String)map.get("passwordMaxLength");
				if(map.get("net_href")!=null)
					net_href = (String)map.get("net_href");
				if(map.get("max_count")!=null)
					max_count = (String)map.get("max_count");
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				// 证件类型
				String id_type = (String) map.get("id_type");
				boolean idFlag = true;
				if (StringUtils.isBlank(id_type) || "#".equals(id_type)) {
					idFlag = false;
					id_type = sysbo.getValue(Sys_Oth_Parameter.CHK_IdTYPE); // 证件类型指标
					return_data.put("id_type_flag", false);
				}else {
					return_data.put("id_type_flag", true);
				}
				if (StringUtils.isNotEmpty(id_type) && !"#".equals(id_type)) {
					FieldItem fieldItem = DataDictionary.getFieldItem(id_type, "A01");
					if (fieldItem == null || !"1".equals(fieldItem.getUseflag()))
						throw GeneralExceptionHandler.Handle(new Exception("证件类型指标未构库"));
					else {
						return_data.put("id_type_item", fieldItem);
						return_data.put("idTypeValue", idFlag?RecruitUtilsBo.getIdTypeValue():sysbo.getIdTypeValue());
					}
				}
			}
			//"true"显示注册按钮，"false"隐藏
			String register_flag = "true";
			String regEndTime = RecruitUtilsBo.getRegisterEndTime();
			//判断注册是否已截止
			if(StringUtils.isNotEmpty(regEndTime)) {
				String format = "yyyy-MM-dd HH:mm";
				Date endtime = DateUtils.getDate(regEndTime, format);
				Date now  =  new Date();
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				now = DateUtils.getDate(sdf.format(now), format);
				if(now.after(endtime))
					register_flag = "false";
			}
			return_data.put("register_flag",register_flag);
			/**增加获取唯一性指标的功能,微招聘需要使用**/
            HashMap onlyName = resumeBo.getOnlyName();
            String onlyItemid = (String) onlyName.get("itemid");
            FieldItem only_fieldItem = DataDictionary.getFieldItem(onlyItemid,"A01");
            return_data.put("only_fieldItem",only_fieldItem);

		    return_data.put("complexPassword", complexPassword);
		    return_data.put("passwordMinLength", passwordMinLength);
		    return_data.put("passwordMaxLength", passwordMaxLength);
		    return_data.put("net_href", net_href);
		    return_data.put("max_count", max_count);
			
			RecordVo vo = ConstantParamter.getConstantVo("SS_MOBILE_PHONE", this.getFrameconn());
			if(vo!=null){
				 String phone = vo.getString("str_value");
				 if(StringUtils.isNotEmpty(phone)){
					 FieldItem phoneItem = DataDictionary.getFieldItem(phone, "a01");
					 if(phoneItem!=null&&"1".equals(phoneItem.getUseflag()))
						 return_data.put("phoneItem", phoneItem);
				 }
			}
			vo = ConstantParamter.getConstantVo("SS_EMAIL", this.getFrameconn());
			if(vo!=null){
				String email=vo.getString("str_value");
				if(StringUtils.isNotEmpty(email)){
					 FieldItem emailItem = DataDictionary.getFieldItem(email, "a01");
					 if(emailItem!=null&&"1".equals(emailItem.getUseflag()))
						 return_data.put("emailItem", emailItem);
				 }
			}
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());//获取系统其它参数
            String cardid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");//获取到系统中配置的身份证指标
            return_data.put("cardid",only_fieldItem==null?cardid:onlyItemid);
			// 访问计数
			Counter counter = new Counter(this.getFrameconn(), this.userView);
			counter.saveCount();
			return_data.put("counter", counter.getCount());
			
	        // 门户样式
            String portType = SystemConfig.getPropertyValue("zp_port_type");
            return_data.put("portType", StringUtils.isNotBlank(portType) ? portType : "0");
			
            if(this.userView != null) {  
            	EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(this.getFrameconn(),"0");
                String dbName=employNetPortalBo.getZpkdbName();
            	String a0100 = employNetPortalBo.getA0100(this.userView.getUserName());
                ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
                HashMap AttMap=xmlBo.getAttributeValues();
                String admissionCard="#";
                if(map.get("admissionCard")!=null&&!"".equals((String)map.get("admissionCard")))
                {
                    admissionCard=(String)map.get("admissionCard");
                }
                //判断是否可以打印准考证
			    boolean canPrintExamno = employNetPortalBo.canPrintExamNo(a0100, admissionCard);
			    return_data.put("canPrintExamno", String.valueOf(canPrintExamno));
			    //判断是否查询考试成绩 
			    boolean canQueryScore = employNetPortalBo.canQueryScore(dbName,a0100); 
			    if (canQueryScore) {
			    	return_data.put("canQueryScore", "1"); 
			    }else {
			    	return_data.put("canQueryScore", "0"); 
			    }
			   
            }

			this.formHM.put("return_data", return_data);
		} catch (Exception e) {
			e.printStackTrace();
		}		  
	}

}
