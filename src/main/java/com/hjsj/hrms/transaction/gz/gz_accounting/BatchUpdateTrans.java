/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 *<p>Title:</p> 
 *<p>Description:执行批量修改交易</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-13:下午03:54:47</p> 
 *@author cmq
 *@version 4.0
 */
public class BatchUpdateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");	
		String itemid=(String)this.getFormHM().get("itemid");
		String formula=(String)this.getFormHM().get("formula");	
		String cond=(String)this.getFormHM().get("cond");
		String whl=(String)this.getFormHM().get("whl");
		cond=SafeCode.decode(cond);
		formula=SafeCode.decode(formula);
        cond=PubFunc.keyWord_reback(cond);
        formula=PubFunc.keyWord_reback(formula);
        whl=PubFunc.decrypt(whl);
		try
		{
			//对代码型指标进行判断
		/*	FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
			if(fielditem!=null&&fielditem.getItemtype().equalsIgnoreCase("A")&&!fielditem.getCodesetid().equals("0"))
			{
				if(formula.indexOf("\"")==-1)
				{
					throw GeneralExceptionHandler.Handle(new Exception("代码值描述不正确！"));
				}
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				String sql="select codeitemid from codeitem where codesetid='"+fielditem.getCodesetid()+"' and codeitemid='"+formula.substring(1,formula.length()-1)+"'";
				if(fielditem.getCodesetid().equalsIgnoreCase("UM")||fielditem.getCodesetid().equalsIgnoreCase("UN")||fielditem.getCodesetid().equalsIgnoreCase("@K"))
					sql="select codeitemid from organization where codesetid='"+fielditem.getCodesetid()+"' and codeitemid='"+formula.substring(1,formula.length()-1)+"'";
				this.frowset=dao.search(sql);
				if(this.frowset.next())
				{
					
				}
				else
					throw GeneralExceptionHandler.Handle(new Exception("没有相匹配的代码值"));
			}*/
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			gzbo.batchUpdateItem(itemid, formula, cond,whl.trim());
			/**人员计算过滤条件*/
			String strwhere="";
		}
		catch(Exception ex)
		{
		//	ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
