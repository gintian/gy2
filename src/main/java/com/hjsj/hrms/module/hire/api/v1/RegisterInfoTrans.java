package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.Des;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterInfoTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String type = (String) this.getFormHM().get("type");
			//用于获取注册指标时同时获取注册协议
			String licenses_flag = (String) this.getFormHM().get("licenses_flag");
			String applyCodeValue = (String) this.getFormHM().get("applyCodeValue");
			ParameterSetBo bo=new ParameterSetBo(this.frameconn);
			ResumeBo resumeBo = new ResumeBo(this.frameconn);
			HashMap<String, Object> return_data = new HashMap();
			if(StringUtils.isNotEmpty(applyCodeValue)) {
				CodeItem code = AdminCode.getCode("35", applyCodeValue);
				if(code!=null)
					return_data.put("applyCodeDesc", code.getCodename());
			}
			if("licenses".equalsIgnoreCase(type)) {//获取注册协议
				String licenses=bo.getLicense_agreement();
				return_data.put("licenses", licenses);
			} else if("register".equalsIgnoreCase(type)) {//获取注册指标
				if("1".equals(licenses_flag))
					return_data.put("licenses", bo.getLicense_agreement());
				ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				HashMap map=parameterXMLBo.getAttributeValues();
				String complexPassword="";//是否使用复杂密码 0未使用 1使用
				String passwordMinLength="";//密码最小长度
				String passwordMaxLength="";//密码最大长度
				String candidateStatusValue="";//应聘身份
				
				if(map!=null) {
					if(map.get("complexPassword")!=null)
						complexPassword=(String)map.get("complexPassword");
					if(map.get("passwordMinLength")!=null)
						passwordMinLength=(String)map.get("passwordMinLength");
					if(map.get("passwordMaxLength")!=null)
						passwordMaxLength=(String)map.get("passwordMaxLength");
					if(map.get("candidate_status")!=null)
						candidateStatusValue=(String)map.get("candidate_status");
				}
				HashMap onlyName = resumeBo.getOnlyName();
				String onlyItemid = (String) onlyName.get("itemid");
                FieldItem only_fieldItem = DataDictionary.getFieldItem(onlyItemid,"A01");
                /*if(only_fieldItem == null||!"1".equals(only_fieldItem.getUseflag())) {
                    return_data.put("only_fieldFail", "唯一性指标未构库，请联系管理员！");//唯一性指标与黑名单指标 相同
                    this.getFormHM().put("return_data", return_data);
                    return;
                }*/

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

                boolean isVisibleExplaination = resumeBo.isVisibleExplaination();//是否显示指标解释
                return_data.put("isVisibleExplaination", isVisibleExplaination);
                
                String email_item = ConstantParamter.getEmailField();//系统配置的邮箱指标
                FieldItem email_itemField = DataDictionary.getFieldItem(email_item);
				HashMap candidateStatus = resumeBo.getCandidateStatus();
				String apply_explain = (String) candidateStatus.get("explain");
				/**得到系统的身份证指标**/
			    String cardid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");
			    /**黑名单指标*/
	            String blacklist_field = sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");
	            /**黑名单人员库*/
	            String blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");
	            if(StringUtils.isBlank(blacklist_per)) {
	                return_data.put("blackNbase", "");
	            }else {
	                return_data.put("blackNbase",blacklist_per);
	            }
	            
	            String blackFieldDesc = "";
	            String black_explain = "";
	            if(StringUtils.isBlank(blacklist_field)) {
	                return_data.put("blackField", "");
	                return_data.put("equalFlag", "2");//唯一性指标与黑名单指标 不同
	                return_data.put("blackFieldDesc", blackFieldDesc);
	            }else {
	                FieldItem fieldItem = DataDictionary.getFieldItem(blacklist_field);
	                black_explain = fieldItem.getExplain();
	                blackFieldDesc = fieldItem.getItemdesc();
	                if(StringUtils.isNotBlank(onlyItemid)) {
	                    if(onlyItemid.equalsIgnoreCase(blacklist_field)) {
	                        return_data.put("equalFlag", "3");//唯一性指标与黑名单指标 相同
	                    }else {
	                        return_data.put("equalFlag", "2");//唯一性指标与黑名单指标 不同
	                    }
	                }else {
	                    return_data.put("equalFlag", "2");//唯一性指标与黑名单指标 不同
	                }
	                return_data.put("blackFieldDesc", blackFieldDesc);
	                return_data.put("blackField", blacklist_field);
	            }
	            
	            FieldItem nameItem = DataDictionary.getFieldItem("a0101");
                return_data.put("email_explain", email_itemField==null?"":email_itemField.getExplain());
                return_data.put("black_explain", black_explain);
            	return_data.put("only_explain", only_fieldItem==null?"":only_fieldItem.getExplain());
                if(nameItem!=null) {
                    return_data.put("name_explain", nameItem.getExplain());
                } else {
                    return_data.put("name_explain", "");
                }
                if(StringUtils.isNotBlank(apply_explain)) {
                    return_data.put("apply_explain", apply_explain);
                } else {
                    return_data.put("apply_explain", "");
                }
			    
                return_data.put("complexPassword", complexPassword);
			    return_data.put("passwordMinLength", passwordMinLength);
			    return_data.put("passwordMaxLength", passwordMaxLength);
			    return_data.put("cardid", cardid.toLowerCase());
			    return_data.put("onlyName", onlyName);
			    return_data.put("candidateStatus", candidateStatus);
			    return_data.put("candidateStatusValue", candidateStatusValue);
			} else if("save".equalsIgnoreCase(type)) {
				MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("params");
	    		Map<String,String> params = PubFunc.DynaBean2Map(bean);
	    		ArrayList<String> list = new ArrayList<String>();
	    		/**为了兼容新版招聘将拿到的password根据系统决定是否进行加密 begin**/
                String password = params.get("password");
                RecordVo recordVo = ConstantParamter.getRealConstantVo("EncryPwd");
                if(recordVo != null) {
                    String isEncryPwd = recordVo.getString("str_value");
                    if("1".equals(isEncryPwd)) {
                        password = new Des().EncryPwdStr(password);
                    }
                }
                params.put("password",password);
                /**为了兼容新版招聘将拿到的password根据系统决定是否进行加密 end**/
	    		String msg = resumeBo.registerCheck(params);
	    		if("success".equals(msg)) {
	    			msg = resumeBo.register(params, list);
	    			if("success".equalsIgnoreCase(msg))
	    				this.getFormHM().put("guidkey", list.get(0));
	    			
	    		}
	    		this.getFormHM().put("msg", msg);
			}else if("checkEmail".equalsIgnoreCase(type)){
			    String inputEmail = (String)this.getFormHM().get("inputEmail");
			    String return_code = resumeBo.checkEmail(inputEmail);
			    this.getFormHM().put("return_code", return_code);
			}
			this.getFormHM().put("return_data", return_data);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

}
