package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 上会材料 _展示
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */
@SuppressWarnings("serial")
public class GetRandomSchemeTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		
		try {
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类
			
			String subModuleId = (String) this.getFormHM().get("subModuleId");// 1:评委会 2：学科组
			if(StringUtils.isNotEmpty(subModuleId)){
				subModuleId = subModuleId.substring(0, 7) + PubFunc.decrypt(subModuleId.substring(7));
			}
			
			ArrayList<HashMap<String, String>> randomScheme = committeeBo.getRandomScheme(subModuleId);// 获取方案信息 
			this.getFormHM().put("randomScheme", randomScheme);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
