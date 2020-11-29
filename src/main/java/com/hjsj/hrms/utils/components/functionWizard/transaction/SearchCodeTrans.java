package com.hjsj.hrms.utils.components.functionWizard.transaction;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.components.functionWizard.businessobject.FunctionWizardbo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SearchCodeTrans 
 * 类描述： 代码联动，取指标
 * 创建人：zhaoxg
 * 创建时间：Nov 19, 2015 10:26:11 AM
 * 修改人：zhaoxg
 * 修改时间：Nov 19, 2015 10:26:11 AM
 * 修改备注： 
 * @version
 */
public class SearchCodeTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		TempvarBo tempvarbo = new TempvarBo();
		
		String itemid = (String)this.getFormHM().get("itemid");
		itemid = itemid!=null&&itemid.length()>0?itemid:"";
		
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		fieldsetid = fieldsetid!=null&&fieldsetid.length()>0?fieldsetid:"";
		String tempid = (String)this.getFormHM().get("tempid");
		FunctionWizardbo bo = new FunctionWizardbo(this.frameconn,this.userView);
		
		if(itemid.trim().length()>0){
			if("tempvar".equalsIgnoreCase(fieldsetid)|| "vartemp".equalsIgnoreCase(fieldsetid)){

			}else{
				if(tempid!=null && "T_item6_5".equalsIgnoreCase(tempid)){
					hm.put("data",bo.codeListForFormula(itemid));//获得“代码转名称2这个公式的列表”
				}else{
					hm.put("data",bo.codeList(itemid));
				}
			}
		}
			
	}

}
