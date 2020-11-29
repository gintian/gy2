package com.hjsj.hrms.utils.components.personpicker.transaction;

import com.hjsj.hrms.utils.components.personpicker.support.PersonPickerSupport;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * <p>Title: QueryCandidateTrans.java</p>
 * <p>Description: 通过选人控件查询候选人</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-1-15 15:46:11</p>
 * @author 刘蒙
 * @version 1.0
 */
public class QueryCandidateTrans extends IBusiness {

	private static final long serialVersionUID = 6502325967118820485L;

	public void execute() throws GeneralException {
		PersonPickerSupport support = new PersonPickerSupport(frameconn,this.userView);

		String keyword = (String) formHM.get("keyword");
		keyword = PersonPickerSupport.nvl(keyword, "");
		keyword = keyword.replace("%", "");
		
		List recommend = (List) formHM.get("recommend");
		List r = support._decrypt(recommend); // 解密后的recommend
		
		List deprecate = (List) formHM.get("deprecate");
		List d = support._decrypt(deprecate); // 解密后的deprecate
		
		String addunit = (String)formHM.get("addunit");//是否可选单位
		String adddepartment = (String) formHM.get("adddepartment");//是否可选部门
		String orgid = (String) formHM.get("orgid");//指定的机构范围
		
		boolean isSelfUser = (Boolean) this.getFormHM().get("isSelfUser");//是否自助用户  zhaoxg add 2015-9-7
		
		Boolean isPrivExpression = (Boolean) this.getFormHM().get("isPrivExpression");//是否启用高级条件 chent 20160520
		
		String nbases = (String) formHM.get("nbases");
		
		boolean addpost = (Boolean) this.getFormHM().get("addpost");// 是否可以添加岗位 chent 20170216
		boolean validateSsLOGIN = (Boolean) this.getFormHM().get("validateSsLOGIN");// 是否启用认证库校验 chent 20170313
		boolean selfUserIsExceptMe = (Boolean) this.getFormHM().get("selfUserIsExceptMe");//业务用户时是否排除自己。chent 20170329
		boolean selectByNbase = (Boolean) this.getFormHM().get("selectByNbase");//是否按不同人员库显示 chent 20170419
		
		//haosl delete  传了orgid的时候，该走高级还是要走高级   2017-10-27
//		if(!StringUtils.isEmpty(orgid)){// 如果指定了orgid，则不走高级
//			isPrivExpression = false;
//		}
		
		List candidates = null; // 符合条件的候选名单
        try {
            // 按检索条件和人员范围 
        	String extend_str = (String) formHM.get("extend_str");
        	if(isSelfUser){//自助用户
            	if(!StringUtils.isEmpty(addunit) || !StringUtils.isEmpty(adddepartment)){
            		candidates = support.getCandidateByKeywordForUnit(keyword,d, addunit, adddepartment, orgid, addpost);
        		}else{
        		    // 按检索条件和人员范围 
        			candidates = support.getCandidateByKeyword(keyword, r, d, orgid, isPrivExpression, nbases, validateSsLOGIN, selectByNbase,extend_str);
        		}
        	}else{//业务用户
        		candidates = support.queryPerson(keyword, extend_str, selfUserIsExceptMe);
        	}
        	formHM.put("candidates", candidates == null ? Collections.EMPTY_LIST : candidates);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}
