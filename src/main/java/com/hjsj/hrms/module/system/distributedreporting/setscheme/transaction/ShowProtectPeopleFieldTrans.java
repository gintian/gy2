package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * @version: 1.0
 * @Description: 用于回写受保护指标条件
 * @author: zhiyh  
 * @date: 2019年3月12日 下午1:47:20
 */
public class ShowProtectPeopleFieldTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String protectPeopleFieldtwo = (String) this.getFormHM().get("protectPeopleFieldtwo");
			List<CommonData> list =new ArrayList<CommonData>();
			if (StringUtils.isNotEmpty(protectPeopleFieldtwo)) {
			    protectPeopleFieldtwo=protectPeopleFieldtwo.substring(1, protectPeopleFieldtwo.length());
	            String[] fieldArray =protectPeopleFieldtwo.split("/");
	            for(int i=0;i<fieldArray.length;i++) {
	                FieldItem item= DataDictionary.getFieldItem(fieldArray[i]);
	                list.add(new CommonData(item.getItemid(),item.getItemdesc()));
	            }
            }
			this.getFormHM().put("rightDataList", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
