package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * @Description: 获得指标描述
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:27:22 
 * @version: 1.0
 */
public class ShowFielditemdescTrans  extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String itemdesc="";
			String fields=(String)this.getFormHM().get("fields");
			if (StringUtils.isNotEmpty(fields)) {
			    String[] fieldArray =fields.substring(1, fields.length()).split("/");
	            for(int i=0;i<fieldArray.length;i++){
	                if (i!=0) {
	                    itemdesc+=",";
	                }
	                FieldItem item =DataDictionary.getFieldItem(fieldArray[i]);
	                itemdesc+=item.getItemdesc();
	            }
            }
	        this.getFormHM().put("itemdesc", itemdesc);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
}
