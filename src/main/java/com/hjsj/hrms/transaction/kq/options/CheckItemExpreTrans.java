package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class CheckItemExpreTrans extends IBusiness {

	public void execute() throws GeneralException {

		String c_expr=(String)this.getFormHM().get("c_expr");
		c_expr = SafeCode.decode(c_expr);
		c_expr = PubFunc.keyWord_reback(c_expr);
		ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET); 
		YksjParser yp = new YksjParser(getUserView(), fielditemlist, 
		        YksjParser.forSearch, YksjParser.FLOAT, YksjParser.forPerson,
		        "Ht", "");
		yp.setCon(this.getFrameconn());
		if(!yp.Verify_where(c_expr.trim()) ){//校验不通过
			String strErrorMsg = yp.getStrError();
			this.getFormHM().put("sige","1");
			if(strErrorMsg==null||strErrorMsg.length()<=0)
			    this.getFormHM().put("sigh",ResourceFactory.getProperty("errors.query.expression"));	
			else
				this.getFormHM().put("sigh",SafeCode.encode(strErrorMsg));	
		}else{
		
			this.getFormHM().put("sige","2");
			this.getFormHM().put("sigh",ResourceFactory.getProperty("kq.formula.tcheck"));
		}
	}

}
