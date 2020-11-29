package com.hjsj.hrms.utils.components.personpicker.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.personpicker.support.PersonPickerSupport;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title: QuerySubordinationTrans.java</p>
 * <p>Description: 选取下级单位</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-2-9 15:46:11</p>
 * @author 刘蒙
 * @version 1.0
 */
public class QuerySubordinationTrans extends IBusiness {

	private static final long serialVersionUID = 6502325967118820485L;

	public void execute() throws GeneralException {
		PersonPickerSupport support = new PersonPickerSupport(frameconn,this.userView);
		try {
			String attachTo = (String) formHM.get("attachTo");
			attachTo = PubFunc.decrypt(attachTo);

			String ancester = (String) formHM.get("ancester");
			String level = (String) formHM.get("level");
			String nbases = (String) formHM.get("nbases");
			String orgid = (String) formHM.get("orgid");
			String extend_str = (String) formHM.get("extend_str");
			boolean isSelfUser = (Boolean) this.getFormHM().get("isSelfUser");//是否自助用户  zhaoxg add 2015-9-7
			boolean isPrivExpression = (Boolean) this.getFormHM().get("isPrivExpression");//是否启用高级条件 chent 20160520
			List deprecate = (List) formHM.get("deprecate");//不推荐显示人员
			List d = support._decrypt(deprecate); // 解密后的deprecate
			boolean recruitmentSpecial = (Boolean) this.getFormHM().get("recruitmentSpecial");//显示已聘用/未聘用人数。注意：只有招聘模块调用时可用，招聘专有。 chent 20161028
			boolean addunit = (Boolean) this.getFormHM().get("addunit");// 是否可以添加单位 chent
			boolean adddepartment = (Boolean) this.getFormHM().get("adddepartment");// 是否可以添加部门 chent
			boolean addpost = (Boolean) this.getFormHM().get("addpost");// 是否可以添加岗位 chent 20161102
			boolean validateSsLOGIN = (Boolean) this.getFormHM().get("validateSsLOGIN");// 是否启用认证库校验 chent 20170313
			boolean selfUserIsExceptMe = (Boolean) this.getFormHM().get("selfUserIsExceptMe");//业务用户时是否排除自己。chent 20170329
			boolean selectByNbase = (Boolean) this.getFormHM().get("selectByNbase");//是否按不同人员库显示 chent 20170419
			List defaultSelected = (List) formHM.get("defaultSelected");//默认已选 chent 20170509
			
			String contentNbs = "";//区分人员库时，要获取当前的人员库
			if(selectByNbase){
				contentNbs = (String) formHM.get("contentNbs");
				if(StringUtils.isNotEmpty(contentNbs)){
					contentNbs = PubFunc.decrypt(contentNbs).substring(4);//substring是截掉前面的"nbs_"
				}
			}
			
			
			/* 传了orgid的时候，该走高级还是要走高级 chent delete 20170726
			 * if(!StringUtils.isEmpty(orgid)){// 如果指定了orgid，则不走高级
				isPrivExpression = false;
			}*/
			
			if(isSelfUser){//自助用户
				if(selectByNbase && PersonPickerSupport.isEmpty(attachTo)){//区分人员库并且第一次进来的时候先加上人员库
					//薪资发放个性化内容 添加薪资类别人员范围的限制 extend_str串内容为 salaryid=xxx;appdate=xxx zhanghua 2019-05-15
					if(StringUtils.isNotBlank(extend_str)&&extend_str.toLowerCase().indexOf("salaryid")!=-1){
						
						String appdate = extend_str.split(";")[1].replaceAll("appdate=", ""),
								salaryid = extend_str.split(";")[0].replaceAll("salaryid=", "");
						salaryid= PubFunc.decrypt(SafeCode.decode(salaryid));
						appdate=PubFunc.decrypt(SafeCode.decode(appdate));
						if(appdate.length()==7){
							appdate+="-01";
						}
						
						SalaryAccountBo salaryAccountBo=new SalaryAccountBo(this.getFrameconn(), this.getUserView(), Integer.parseInt(salaryid));
						salaryAccountBo.searchLike_For_HandImport(appdate, new HashMap());
					}
					formHM.put("unit", support.loadNbases(nbases, validateSsLOGIN));
				}else {
					formHM.put("unit", support.subordinateUnit(attachTo, ancester, level, orgid, isPrivExpression, recruitmentSpecial, addpost, selectByNbase));
				}
				if (PersonPickerSupport.isNotEmpty(attachTo)) {
					formHM.put("person", support.subordinatePerson(attachTo, ancester, level, nbases, extend_str, isPrivExpression, d, validateSsLOGIN,selectByNbase,contentNbs));
				} else {
					// ajax的漏洞(command.js)，_outParameters属性会保留上次查询的结果，因此用空的集合覆盖掉person的值
					formHM.put("person", Collections.EMPTY_LIST);
				}
				
				
				if (defaultSelected.size() > 0 && PersonPickerSupport.isEmpty(attachTo)) {// 默认已选人员初始化 chent 20170509
					formHM.put("defaultSelectedList", support.getDefaultSelected(defaultSelected, addunit, adddepartment, addpost,isSelfUser));
				} else{
					formHM.put("defaultSelectedList", Collections.EMPTY_LIST);
				}
			}else{//业务用户
				if(!"1".equals(attachTo))
					formHM.put("unit", support.loadUsergrops(attachTo, level,ancester,extend_str));
				if (PersonPickerSupport.isNotEmpty(attachTo)) {
					formHM.put("person", support.loadUserNodes(attachTo,level,ancester,extend_str,selfUserIsExceptMe));
				} else {
					// ajax的漏洞(command.js)，_outParameters属性会保留上次查询的结果，因此用空的集合覆盖掉person的值
					formHM.put("person", Collections.EMPTY_LIST);
				}
				if (defaultSelected.size() > 0 && PersonPickerSupport.isEmpty(attachTo)) {// 默认已选人员初始化 chent 20170509
					formHM.put("defaultSelectedList", support.getDefaultSelected(defaultSelected, addunit, adddepartment, addpost,isSelfUser));
				} else{
					formHM.put("defaultSelectedList", Collections.EMPTY_LIST);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
