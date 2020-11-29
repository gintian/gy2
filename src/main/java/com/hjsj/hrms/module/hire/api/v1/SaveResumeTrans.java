package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveResumeTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			// update修改数据，add 增加数据，delete 删除数据
			String type = (String) this.getFormHM().get("type");
			String candidate_status = (String) this.getFormHM().get("candidate_status");
			String return_code = "success";
			ResumeBo bo = new ResumeBo(this.frameconn,this.userView);
			//保存应聘身份指标
			if("true".equalsIgnoreCase(candidate_status)) {
				String candidateItemid = (String) this.getFormHM().get("candidateItemid");
				String candidateValue = (String) this.getFormHM().get("candidateValue");
				return_code = bo.changeCandidate(candidateItemid, candidateValue);
				if("success".equals(return_code))
					this.userView.getHm().put("applyCode",candidateValue);
			} else if(!"delete".equalsIgnoreCase(type)) {
				MorphDynaBean beans = (MorphDynaBean) this.getFormHM().get("params");
				HashMap<String,ArrayList> params = PubFunc.DynaBean2Map(beans);
				//校验唯一性指标是否已被填写
				return_code = bo.checkOnlyValue(params);
				if(!StringUtils.equals(return_code, "success")) {//如果校验成功会返回success
				    if(StringUtils.equals(return_code, "fail")) {//只有出现Exception时才会是fail
				        return_code ="数据处理异常,请联系系统管理员";
				    }
				    this.getFormHM().put("return_code", "fail");
					this.getFormHM().put("return_msg", return_code);
					return;
				}
				HashMap<String, Object> i9999_map = bo.addResumeInfo(params, type);
				return_code = (String) i9999_map.get("return_code");
				if("success".equals(return_code)) {
					//判断简历资料必填项是否没填
			    	return_code = bo.checkRequired();
			    	if(!"success".equals(return_code)) {
			    		String[] split = return_code.split("-");
			    		this.getFormHM().put("subset", split[0]);
			    		this.getFormHM().put("return_msg", return_code.substring(return_code.indexOf("-")+1));
			    		return_code = "fail";
			    	}
			    	this.getFormHM().put("i9999_map", i9999_map);
				}
			} else if("delete".equalsIgnoreCase(type)){
				MorphDynaBean beans = (MorphDynaBean) this.getFormHM().get("params");
				HashMap<String,ArrayList> params = PubFunc.DynaBean2Map(beans);
				bo.deleteResume(params);
			}
			this.getFormHM().put("return_code", return_code);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
