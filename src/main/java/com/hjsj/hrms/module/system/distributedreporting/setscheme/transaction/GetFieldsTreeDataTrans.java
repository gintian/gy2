package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.LinkedHashMap;
/**
 * @Description:一次性加载已选和备选的指标数据
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:26:36 
 * @version: 1.0
 */
public class GetFieldsTreeDataTrans extends IBusiness{
	@Override
	@SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		try {
			SetupSchemeBo bo = new SetupSchemeBo(userView, frameconn);
			//1、获取备选指标数据
			ArrayList<LinkedHashMap<String, Object>> alternativeList = bo.getALLAlternativeList();
			//2、获取已选指标数据
			ArrayList<LinkedHashMap<String, Object>> allSelectedList = bo.getAllSelectedList();
			//3、去除备选的已选指标
			for(int i=alternativeList.size()-1;i>-1;i--) {
				String fieldsetid = (String) alternativeList.get(i).get("id");
				ArrayList<LinkedHashMap<String, Object>> fieldList = (ArrayList<LinkedHashMap<String, Object>>) alternativeList.get(i).get("children");
				for(int j=allSelectedList.size()-1;j>-1;j--) {
					String selectedFieldsetid = (String) allSelectedList.get(j).get("id");
					ArrayList<LinkedHashMap<String, Object>> selectedFieldList = (ArrayList<LinkedHashMap<String, Object>>) allSelectedList.get(j).get("children");
					if (selectedFieldsetid.equalsIgnoreCase(fieldsetid)) {
						if (fieldList.size()==selectedFieldList.size()) {//代表子集全部选择了
							alternativeList.remove(i);
							break;
						}else {
							for(int k=fieldList.size()-1;k>-1;k--) {
								String itemid = (String) fieldList.get(k).get("id");
								for(int g=selectedFieldList.size()-1;g>-1;g--) {
									String selectedItemid = (String) selectedFieldList.get(g).get("id");
									if (selectedItemid.equalsIgnoreCase(itemid)) {
										fieldList.remove(k);
										break;
									}
								}
							}
						}
					}
				}
			}
		/*	Collections.sort(alternativeList, new Comparator<LinkedHashMap<String, Object>>() {
	            public int compare(LinkedHashMap<String, Object> o1,LinkedHashMap<String, Object> o2) {
	                return o1.get("id").toString().compareTo(o2.get("id").toString());
	            }
            });
			Collections.sort(allSelectedList, new Comparator<LinkedHashMap<String, Object>>() {
		        public int compare(LinkedHashMap<String, Object> o1,LinkedHashMap<String, Object> o2) {
		            return o1.get("id").toString().compareTo(o2.get("id").toString());
		        }
		    });*/
			this.getFormHM().put("data", alternativeList);
			this.getFormHM().put("selectedJsonData", allSelectedList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
