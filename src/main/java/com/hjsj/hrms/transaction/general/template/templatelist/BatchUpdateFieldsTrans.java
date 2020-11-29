package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class BatchUpdateFieldsTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String table_name=(String)this.getFormHM().get("table_name");
			String task_id=(String)this.getFormHM().get("task_id");
			HashMap templateMap= (HashMap) this.userView.getHm().get("templateMap");
			/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
			if(!templateMap.containsKey(task_id)){
				throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
			}
			*/
			ArrayList field_item_list = (ArrayList)this.getFormHM().get("fielditem_array");
			ArrayList field_value_list = (ArrayList)this.getFormHM().get("fieldvalue_array");
			ArrayList field_type_list = (ArrayList)this.getFormHM().get("fieldtype_array");
			String selchecked=(String)this.getFormHM().get("selchecked");//复选框选中
			String tabid=(String)this.getFormHM().get("tabid");
			//当只有部门单位 清空职位
			if(field_item_list.contains("B0110_2")&&field_item_list.contains("E0122_2")&&!field_item_list.contains("E01A1_2")){
			    field_item_list.add("E01a1_2");
			    field_value_list.add("");
			    field_type_list.add("A");
			}
			//当只有单位清空职位部门
			if(field_item_list.contains("B0110_2")&&!field_item_list.contains("E0122_2")&&!field_item_list.contains("E01A1_2")){
                field_item_list.add("E01a1_2");
                field_item_list.add("E0122_2");
                field_value_list.add("");
                field_value_list.add("");
                field_type_list.add("A");
                field_type_list.add("A");
            }
			//批量处理方式的两个变量
			String sp_batch=(String)this.getFormHM().get("sp_batch");
			String batch_task=(String)this.getFormHM().get("batch_task");
			
			//String whl=(String)this.getFormHM().get("whl");//查询过滤条件串
			//String needcondition=(String)this.getFormHM().get("needcondition");
			
			if("1".equals(sp_batch)&&batch_task!=null){//如果是批量方式
				batch_task =batch_task.replace(",,", ",");
				if(batch_task.startsWith(","))
					batch_task = batch_task.substring(1);
				if(batch_task.endsWith(","))
					batch_task = batch_task.substring(0,batch_task.length()-1);
				/**安全平台改造，批量审批方式必须判断所有的task_id是否是有后台处理过去的**/
				String []taskIdArray=batch_task.split(",");
				/*注释掉 存在误报的情况，暂时没查找原因 wangrd 2015-04-10
				for(int i=0;i<taskIdArray.length;i++){
					String temptask_id = taskIdArray[i];
					if(!templateMap.containsKey(temptask_id)){
						throw new GeneralException(ResourceFactory.getProperty("lable.rsbd.modify.taskid"));
					}
				}
				*/
				task_id =batch_task;
			}
			TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
			boolean b = bo.batchUpdateFields(field_item_list,field_value_list,field_type_list,table_name,task_id,selchecked);
			if(b){
				this.getFormHM().put("flag", "1");
			}else{
				this.getFormHM().put("flag", "0");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
