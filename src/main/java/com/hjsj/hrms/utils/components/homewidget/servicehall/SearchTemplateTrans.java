package com.hjsj.hrms.utils.components.homewidget.servicehall;

import com.hjsj.hrms.module.system.servicehallsetting.ServiceHallSettingTrans;
import com.hjsj.hrms.module.template.utils.TemplateServiceBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTemplateTrans extends IBusiness {

	
	/*
	 * 
	 格式：
	[{
	   name:”考勤”，//分类名称
	   temps:[{
	       tempid:1,//模板id
	       name:”请假”,//模板别名
	       icon:””,//图标
	   }........]
	}.....]
	
	*/
	
	public void execute() throws GeneralException {
		if(StringUtils.isEmpty(this.userView.getA0100())){
			this.getFormHM().put("errormessage","未关联自助用户");
			return;
		}
		//if(this.userView.getVersion()<70) return;
		String type = (String)this.getFormHM().get("type");
		
		//加密url中的taskid
		if(!StringUtils.isEmpty(type)&&"encryptParam".equals(type)&&!StringUtils.isEmpty((String)this.getFormHM().get("needEncryptParam"))){
			String needEncryptParam = (String)this.getFormHM().get("needEncryptParam");
			String tabid = (String)this.getFormHM().get("tabid");
			String taskid = (String)this.getFormHM().get("taskid");
			String encryptTaskid = "";
			if(!StringUtils.isEmpty(taskid)){
				if(taskid.contains(",")){
					for(int i =0;i<taskid.split(",").length;i++){
						encryptTaskid+=PubFunc.encrypt(taskid.split(",")[i]);
						encryptTaskid+=",";
					}
					encryptTaskid = encryptTaskid.substring(0, encryptTaskid.length()-1);
				}else
					encryptTaskid = PubFunc.encrypt(taskid);
			}
			if(encryptTaskid.length()>0)
				needEncryptParam = "task_id="+encryptTaskid+needEncryptParam;
			this.getFormHM().put("html", PubFunc.encrypt(needEncryptParam));
			return;
		}
		
		TemplateServiceBo tsbo = new TemplateServiceBo(this.frameconn,this.userView);
		ArrayList als = tsbo.getServiceTemplate();
		ArrayList serviceList = new ServiceHallSettingTrans().getServiceData(this.frameconn,this.frowset);
		
		ArrayList newList = reBuildService(serviceList,als);
		
		
		//判断是否更改了
		if(!StringUtils.isEmpty(type)&&"beforedestroy".equals(type)){
			String tabid = (String)this.getFormHM().get("tabid");
			for(int k = 0;k<newList.size();k++){
				ArrayList tempsList = (ArrayList)((LazyDynaBean)als.get(k)).get("temps");
				for(int j =0;j<tempsList.size();j++){
					LazyDynaBean ldb = (LazyDynaBean)tempsList.get(j);
					if((ldb.get("tabid")).equals(tabid)){
						this.getFormHM().put("temp", ldb);
					}
				}
			}
		}
		
		this.getFormHM().put("mergeList", newList);//collectionList.get(0));
	}
	
	private ArrayList reBuildService(ArrayList plan,ArrayList services) {
		ArrayList newServicesData = new ArrayList();
		HashMap templateMap = new HashMap();
		for(int i=0;i<services.size();i++) {
			ArrayList tempsList = (ArrayList)((LazyDynaBean)services.get(i)).get("temps");
			for(int j =0;j<tempsList.size();j++){
				LazyDynaBean ldb = (LazyDynaBean)tempsList.get(j);
				templateMap.put(ldb.get("tabid"),ldb);
			}
		}
		
		for(int i=0;i<plan.size();i++) {
			HashMap serviceGroup = (HashMap)plan.get(i);
			ArrayList tempsList = (ArrayList)serviceGroup.get("services");
			ArrayList newServiceList = new ArrayList();
			for(int j =0;j<tempsList.size();j++){
				HashMap serviceObj = (HashMap)tempsList.get(j);
				
				int type = (Integer)serviceObj.get("type");
				if(type==1) {
					int tabid = (Integer)serviceObj.get("tabid");
					LazyDynaBean ldb = (LazyDynaBean)templateMap.get(tabid+"");
					if(ldb==null)
						continue;
					HashMap tempObj = new HashMap();
					tempObj.putAll(ldb.getMap());
					tempObj.put("type", type);
					tempObj.put("tabname",serviceObj.get("tabname"));
					tempObj.put("icon",serviceObj.get("icon"));
					newServiceList.add(tempObj);
				}else if(type==2) {
					int tabid = (Integer)serviceObj.get("tabid");
					if(!this.userView.isHaveResource(IResourceConstant.CARD, tabid+""))
						continue;
					newServiceList.add(serviceObj);
				}else {
					newServiceList.add(serviceObj);
				}
			}
			
			if(newServiceList.size()>0) {
				serviceGroup.put("temps",newServiceList);
				serviceGroup.put("name",serviceGroup.get("groupname"));
				serviceGroup.remove("groupname");
				serviceGroup.remove("services");
				newServicesData.add(serviceGroup);
			}
		}
		return newServicesData;
	}
}
