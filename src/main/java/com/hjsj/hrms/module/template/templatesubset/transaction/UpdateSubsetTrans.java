package com.hjsj.hrms.module.template.templatesubset.transaction;

import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class UpdateSubsetTrans extends IBusiness{
	@Override
	public void execute() throws GeneralException {
		
		try{
			String tab_id = (String)this.getFormHM().get("tab_id");
			tab_id=tab_id!=null&&tab_id.trim().length()>0?tab_id:"";
			//String view_type = (String)this.getFormHM().get("view_type");
			String infor_type = (String)this.getFormHM().get("infor_type");
			infor_type = infor_type!=null&&infor_type.trim().length()>0?infor_type:"";
			String basepre = (String)this.getFormHM().get("basepre");
			String id = (String)this.getFormHM().get("id");
			String module_id = (String)this.getFormHM().get("module_id");
			String task_id = (String)this.getFormHM().get("task_id");
			Pattern pattern = Pattern.compile("[0-9]+");   
			Boolean isSysData =(Boolean)this.getFormHM().get("isSysData");
			if(isSysData!=null&&isSysData)
			{
				TemplateBo templateBo=new TemplateBo(this.getFrameconn(),this.userView,Integer.parseInt(tab_id));
				templateBo.setModuleId(module_id);
				if(pattern.matcher(task_id).matches()){
				}else{
					if (task_id.contains(",")){
					    String[] strArr= task_id.split(",");
					    task_id="";
					    for (int i=0;i<strArr.length;i++){
					        String tmp = strArr[i];
					        if ("".equals(tmp)){
					            continue;
					        }
					        String _value= PubFunc.decryption(tmp);
					        if ("".equals(task_id)){
					        	task_id=_value;
					        }
					        else {
					        	task_id=task_id+","+_value;
					        }
					    }
					}
					else {
						task_id= PubFunc.decryption(task_id);
					}
				}
						
			    templateBo.setTaskId(task_id);
				templateBo.syncDataFromArchive();	
			}
			else
			{
				if(pattern.matcher(task_id).matches()){
				}else
					task_id = PubFunc.decryption(task_id);
				
				String columnName = (String)this.getFormHM().get("columnname");
				if(StringUtils.isBlank(id))
					return;
				ArrayList resultlist = new ArrayList();
		        TemplateBo templateBo=new TemplateBo(this.getFrameconn(),this.userView,Integer.parseInt(tab_id));
		        templateBo.setModuleId(module_id);
		        templateBo.setTaskId(task_id);
				if("1".equals(infor_type)){//人事
					ArrayList a0100s = new ArrayList();//人员编号
					if(!"".equals(id)){
						id = PubFunc.decryption(id);
					}
					String a0100 = id.split("`")[1];
					basepre = id.split("`")[0];
					a0100s.add(a0100);
					if(a0100s.size()>0){
						resultlist=templateBo.refDataFromArchive(a0100s, basepre,columnName);//按人员库前缀刷新数据
					}
					
				}else {//单位、岗位
					ArrayList a0100s = new ArrayList();//单位、岗位编号
					if(!"".equals(id)){
						id = PubFunc.decryption(id);
					}
					a0100s.add(id);
					if("2".equals(infor_type)){
						resultlist=templateBo.refDataFromArchive(a0100s, "B",columnName);//单位
					}else if("3".equals(infor_type)){
						resultlist=templateBo.refDataFromArchive(a0100s, "K",columnName);//岗位
					}
				}
				this.getFormHM().put("subsetlist", resultlist);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
