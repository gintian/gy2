package com.hjsj.hrms.transaction.org.autostatic.mainp;

import com.hjsj.hrms.businessobject.sys.org.DecExpresion;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SearchFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		
		StringBuffer buf = new StringBuffer();
		String itemid = (String)hm.get("itemid");
		itemid=itemid!=null?itemid.toUpperCase():"";
		DecExpresion decexp = new DecExpresion(this.frameconn,itemid);
		
		String expreField = decexp.expreField();
		expreField=expreField!=null?expreField:"";
		buf.append(expreField);
		
		String expr = decexp.expreExpr();
		expr=expr!=null?expr:"";
		buf.append("::");
		buf.append(expr);
		
		String factor = decexp.expreFactor();
		factor=factor!=null&&factor.trim().length()>0?"|"+factor:"";
		buf.append(factor);
		
		String mode = decexp.expreMode();
		mode=mode!=null?mode:"";
		buf.append("::");
		buf.append(mode);
		
		String includechild = decexp.expreInCludeChild();
		includechild=includechild!=null&&includechild.trim().length()>0?includechild:"0";
		
		hm.put("info",SafeCode.encode(buf.toString()));
		hm.put("includechild",includechild);
	}
	
}
