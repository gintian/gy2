package com.hjsj.hrms.module.workplan.config.transaction;

import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanFunctionBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.HashMap;
import java.util.List;
/**
 * 区间设置信息获取与更新
 * 
 * @author haosl
 * @date	20161203
 */
public class WorkPlanFunctionTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		HashMap hm = this.getFormHM();
		try {
			WorkPlanFunctionBo funcBo = new WorkPlanFunctionBo(this.getFrameconn());
			String opt = (String) hm.get("opt");
			if("1".equals(opt)){//查询数据 
				List<HashMap<String, HashMap<String, String>>> list = funcBo.getXmlData();
				hm.clear();
				hm.put("data", list);
			}else if("2".equals(opt)){//保存更新
				List<MorphDynaBean> list =(List<MorphDynaBean>)hm.get("list");
				funcBo.saveXml(list);
				hm.clear();
				hm.put("sucflag", true);
			}else if("3".equals(opt)){//清空设置
				funcBo.cancelSetting();
			}
		} catch (Exception e) {
			hm.clear();
			hm.put("sucflag", false);
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	public HashMap<String, String> getConfigMap(){
		HashMap<String, String> configMap = new HashMap<String, String>();
    	configMap.put("p00", "个人年计划");
    	configMap.put("p01", "部门年计划");
    	configMap.put("p10", "个人半年计划");
    	configMap.put("p11", "部门半年计划");
    	configMap.put("p20", "个人季度计划");
    	configMap.put("p21", "部门季度计划");
    	configMap.put("p30", "个人月度计划");
    	configMap.put("p31", "部门月度计划");
    	configMap.put("p40", "个人周计划");
    	configMap.put("p41", "部门周计划");
    	
    	configMap.put("s00", "个人年总结");
    	configMap.put("s01", "部门年总结");
    	configMap.put("s10", "个人半年总结");
    	configMap.put("s11", "部门半年总结");
    	configMap.put("s20", "个人季度总结");
    	configMap.put("s21", "部门季度总结");
    	configMap.put("s30", "个人月度总结");
    	configMap.put("s31", "部门月度总结");
    	configMap.put("s40", "个人周总结");
    	configMap.put("s41", "部门周总结");
    	
    	configMap.put("p5", "日志");
    	
    	return configMap;
	}
	
	
}
