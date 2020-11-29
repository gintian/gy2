package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @Description:获得设置人员库类别-获得主集的代码型指标
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:21:02 
 * @version: 1.0
 */
public class GetCodeFieldsDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String fields = (String) this.getFormHM().get("fileds");
			String [] fieldArray = fields.split(",");
			ArrayList<HashMap<String,String>> setlist = new ArrayList<HashMap<String,String>>();
			HashMap<String, String> mapnull = new HashMap<String, String>();
			mapnull.put("fieldName","无");
			mapnull.put("fieldValue"," ");
			setlist.add(mapnull);
			for(int i = 0;i<fieldArray.length;i++) {
				String fielditemid = fieldArray[i];
				FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid);
				String itemtype = fieldItem.getItemtype();
				String codesetid = fieldItem.getCodesetid();
				String fieldsetid = fieldItem.getFieldsetid();
				String itemdesc = fieldItem.getItemdesc();
				if ("A01".equalsIgnoreCase(fieldsetid)&&!"0".equals(codesetid)&& "A".equals(itemtype)&&!"UN".equals(codesetid)&&!"UM".equals(codesetid)&&!"@K".equals(codesetid)) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("fieldName", itemdesc);
					map.put("fieldValue", fielditemid.toUpperCase());
					setlist.add(map);
				}
			}
			this.getFormHM().put("list", setlist);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
