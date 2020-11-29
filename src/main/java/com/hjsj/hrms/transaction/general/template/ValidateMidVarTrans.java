package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ValidateMidVarTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			/**如果ins_id=0,表示对与用户有关的临时表进行计算，否则对*/
			String ins_id=(String)this.getFormHM().get("ins_id");
			String ins_ids=(String)this.getFormHM().get("ins_ids");
			
			StringBuffer message=new StringBuffer("");
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			ArrayList fieldlist=tablebo.getMidVariableList();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String formula=item.getFormula();
				if(formula==null||formula.trim().length()==0)
				{
					String itemdesc=item.getItemdesc();
					String itemtype=item.getItemtype();
					String codesetid=item.getCodesetid();
					int length=item.getItemlength();
					if("A".equalsIgnoreCase(itemtype)&&("0".equals(codesetid)||codesetid.trim().length()==0))
						message.append(","+itemdesc+":"+itemtype+":"+length);
					else
						message.append(","+itemdesc+":"+itemtype+":0");
				}
			}
			
			if(message.length()>0)
				this.getFormHM().put("message",SafeCode.encode(message.substring(1)));
			else
				this.getFormHM().put("message","");
			
			this.getFormHM().put("tabid", tabid);
			this.getFormHM().put("ins_id", ins_id);
			this.getFormHM().put("ins_ids", ins_ids);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
