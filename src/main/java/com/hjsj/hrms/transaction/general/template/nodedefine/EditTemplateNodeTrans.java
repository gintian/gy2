/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.nodedefine;

import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class EditTemplateNodeTrans extends IBusiness{

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		/**如果代码太多的话，AdminCode.getCodeItemList效率不高
		 * 有待优化
		 * */
		ArrayList rolelist=AdminCode.getCodeItemList("41");
		ArrayList templist=new ArrayList();
		for(int i=0;i<rolelist.size();i++)
		{
			CodeItem  item=(CodeItem)rolelist.get(i);
			if("0".equals(item.getCodeitem()))
				continue;
			templist.add(item);
		}
		this.getFormHM().put("rolelist",templist);		
	}

}
