package com.hjsj.hrms.transaction.train.request;
/**
 * 培训班审核公式保存时测试审核公式是否符合规则
 */

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CheckTrainFormulaTrans extends IBusiness{

	public void execute() throws GeneralException {
		String formula=this.getFormHM().get("formula").toString();
		formula=SafeCode.decode(formula);
		formula=PubFunc.keyWord_reback(formula);
		String flag="";
		if(formula!=null && formula.length()>0){
			ArrayList fieldList=DataDictionary.getFieldList("R31", Constant.USED_FIELD_SET);
			YksjParser yjp=new YksjParser(userView,fieldList, YksjParser.forSearch, YksjParser.LOGIC, YksjParser.forPerson, "Ht", "");
			yjp.setCon(this.getFrameconn());
			
			boolean b = false;
			try{
				b = yjp.Verify_where(formula.trim());
			}catch (Exception e) {
				e.printStackTrace();
				
				b = false;
			}
			if(b){
				flag="1";
			}else{
				flag=yjp.getStrError();
			}
		}else{
			flag="1";
		}
		this.getFormHM().put("flag", SafeCode.encode(flag));
	}
    
}
