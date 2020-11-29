package com.hjsj.hrms.module.serviceclient.serviceSetting;

import com.hjsj.hrms.module.serviceclient.serviceSetting.businessobject.ServiceSettingBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.List;
import java.util.Map;

public class SaveServiceSettingTrans extends IBusiness {
	private enum SaveType{
	    /**添加分类*/
        checkName,
		/**添加分类*/
		addGroup,
		/**更新分类*/
		updateGroup,
		/**删除分类*/
		deleteGroup,
		/**分类排序*/
		sortGroup,
		/**添加服务*/
		addService,
		/**删除服务*/
		deleteService,
		/**编辑服务*/
		editService,
		/**回显服务信息*/
		enformService,
		/**参数设置*/
		saveParamSetting,
		/**获取参数设置*/
		searchParamSetting,
		/**校验服务关联的登记表或业务模板是否还存在*/
		checkTabExist
	}
	@Override
    public void execute() throws GeneralException {

		String saveType = (String)this.formHM.get("saveType");
		String groupName =(String)this.formHM.get("groupName");
		String groupId = (String)this.formHM.get("groupId");
		
		if(saveType==null || saveType.length()<1)
			return;
		ServiceSettingBo bo = new ServiceSettingBo(this.getFrameconn(), this.userView);
		//检查分类名
        if(saveType.equals(SaveType.checkName.toString())) {
            boolean flag = bo.checkgroupName(groupName);
            this.formHM.put("flag", flag);
        }
        //保存参数设置
       if(saveType.equals(SaveType.saveParamSetting.toString())) {
            String needPwdInput = (String) this.formHM.get("needPwdInput");
            String itemId = (String) this.formHM.get("itemId");
            boolean result = bo.saveSettings(needPwdInput,itemId);
            this.getFormHM().put("result", result);
        }//获取配置信息
       if(saveType.equals(SaveType.searchParamSetting.toString())) {
           Map map = bo.searchSettings();
           this.getFormHM().put("settingMsg", map);
       }
		// 添加分类
		if(saveType.equals(SaveType.addGroup.toString())){
			String groupid = bo.addGroupData(groupName);
			this.formHM.put("groupId", groupid);
		}
		// 更新分类
		else if(saveType.equals(SaveType.updateGroup.toString())){
			bo.updateGroupData(groupName,groupId);
		}
		// 删除分类
		else if(saveType.equals(SaveType.deleteGroup.toString())){
			bo.deleteGroupData(groupId);
		}
		// 分类排序
		else if(saveType.equals(SaveType.sortGroup.toString())){
			List<MorphDynaBean> sort = (List<MorphDynaBean>) this.formHM.get("groupOrder");//排序
			bo.saveGroupSortData(sort);
		}
		// 添加服务
		else if(saveType.equals(SaveType.addService.toString())){
			MorphDynaBean morphDynaBean=(MorphDynaBean) this.formHM.get("serviceConfig");//前台传的是map后台的是MorphDynaBean
			int serviceId = bo.addService(morphDynaBean);
			this.formHM.put("serviceId",serviceId);
		}
		// 删除服务
		else if(saveType.equals(SaveType.deleteService.toString())){
			String serviceId = (String) this.formHM.get("serviceId");
			bo.deleteServiceData(serviceId);
		}
		//编辑服务
		else if(saveType.equals(SaveType.editService.toString())) {
		    MorphDynaBean morphDynaBean=(MorphDynaBean) this.formHM.get("editData");
		    bo.editService(morphDynaBean);
		}
		//回显服务信息
		else if(saveType.equals(SaveType.enformService.toString())) {
            String serviceId = (String) this.formHM.get("serviceId");
            String templateType = (String) this.formHM.get("templateType");
            Map map = bo.showService(serviceId,templateType);
            this.getFormHM().put("infos", map);
        }
		//校验服务关联的登记表或业务模板是否还存在
		else if(saveType.equals(SaveType.checkTabExist.toString())) {
			String templateId = (String) this.formHM.get("templateId");
			String templateType = (String) this.formHM.get("templateType");
			boolean tabExistFlag =bo.checkTabExist(templateId,templateType);
			this.getFormHM().put("tabExistFlag", tabExistFlag);
		}
	}

}
