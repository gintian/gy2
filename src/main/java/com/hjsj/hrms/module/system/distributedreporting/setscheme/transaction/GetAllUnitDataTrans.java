package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.LinkedHashMap;
/**
 * @version: 1.0
 * @Description: 用于定义上单位获得机构树的全部数据，已经定义的上报单位为选中状态
 * @author: zhiyh  
 * @date: 2019年3月12日 下午1:32:37
 */
public class GetAllUnitDataTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			SetupSchemeBo bo = new SetupSchemeBo(userView,this.frameconn);
			ArrayList<LinkedHashMap<String, Object>> list = bo.getAllUnJsonList();
			//查询出t_sys_asyn_scheme表里面的UnitCode存入arraylist中去。
			ArrayList<String> unitCodeList =bo.getUnitCodeList();//查询出保存进数据库的上报单位
		    list = checkedList(list,unitCodeList);
			this.getFormHM().put("data", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * @Description: 递归遍历单位，已选的设置为true
	 * @author: zhiyh  
	 * @date: 2019年4月2日 上午11:15:11
	 * @param list
	 * @param unitCodeList
	 * @return
	 */
    private ArrayList<LinkedHashMap<String, Object>> checkedList(ArrayList<LinkedHashMap<String, Object>> list,ArrayList<String> unitCodeList) {
    	for(int i=0;i<list.size();i++) {
    		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			map = (LinkedHashMap<String, Object>) list.get(i);
			String idString = (String) map.get("id");
			for(int j=0;j<unitCodeList.size();j++) {
				String unitCode=unitCodeList.get(j);
				if (unitCode.equalsIgnoreCase(idString)) {
					map.put("checked", true);
				}
			}
			boolean leaf = (Boolean) map .get("leaf");//true 代表叶子节点
			if (!leaf) {
				ArrayList<LinkedHashMap<String, Object>> childrenList= (ArrayList<LinkedHashMap<String, Object>>) map.get("children");
				childrenList = checkedList(childrenList, unitCodeList);
			}
		}
		return list;
	}
}
