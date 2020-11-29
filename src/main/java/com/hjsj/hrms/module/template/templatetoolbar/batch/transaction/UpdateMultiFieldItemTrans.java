package com.hjsj.hrms.module.template.templatetoolbar.batch.transaction;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.module.template.templatelist.businessobject.TemplateListShowBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
* <p>Title:TemplateBatchTrans </p>
* <p>Description:批量修改多指标 </p>
* <p>Company:hjsoft </p> 
* @author gaohy
* @date 2015-11-24下午01:47:04
 */
public class UpdateMultiFieldItemTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		try
		{	
			HashMap hmMap= this.getFormHM();
			String logo=(String)this.getFormHM().get("transType");
			String tabid = TemplateFuncBo.getValueFromMap(hmMap,"tab_id");
			String ins_id = TemplateFuncBo.getValueFromMap(hmMap,"ins_id");
			String task_id = TemplateFuncBo.getDecValueFromMap(hmMap,"task_id");
			String selchecked=TemplateFuncBo.getValueFromMap(hmMap,"selchecked");//复选框选中
		
			if("init".equalsIgnoreCase(logo)){//初始化指标
			    String approve_flag = TemplateFuncBo.getValueFromMap(hmMap,"approve_flag");//复选框选中
			    String module_id = TemplateFuncBo.getValueFromMap(hmMap,"module_id");//复选框选中
				//对于变化后的指标，判断是否具有修改指标的权限
				ArrayList beanList=getBeanList(module_id,tabid,task_id,approve_flag);
				
				ArrayList batchlist = new ArrayList(); //封装后的结果
				HashMap itemmap = new HashMap();
				for (int i = 0; i < beanList.size(); i++) {//封装符合条件的指标
					itemmap = new HashMap();
					LazyDynaBean abean = (LazyDynaBean)beanList.get(i);
					itemmap.put("itemname", abean.get("field_hz").toString());
					itemmap.put("field_value", "");
					if("0".equals(abean.get("codeid").toString())){
						itemmap.put("field_type", abean.get("field_type").toString());
					}else
						itemmap.put("field_type", abean.get("field_type")+"|"+abean.get("codeid"));
					itemmap.put("field_item", abean.get("field_name").toString()+"_2");
					itemmap.put("itemlength", abean.get("itemlength").toString());
					itemmap.put("limitManagePriv", abean.get("limitManagePriv").toString());
					itemmap.put("disformat", abean.get("disformat").toString());
					itemmap.put("imppeople", abean.get("imppeople"));
					itemmap.put("fatherRelationField", abean.get("fatherRelationField"));
					itemmap.put("childRelationField", abean.get("childRelationField"));
					batchlist.add(itemmap);
				}
				net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(batchlist);//将封装结果转换json格式
				this.getFormHM().put("data", jsonArray);//最终指标json串
			}else if("ok".equalsIgnoreCase(logo)){//update数据库表
				ArrayList field_item_list = (ArrayList)this.getFormHM().get("fielditem_array");//指标
				ArrayList field_value_list = (ArrayList)this.getFormHM().get("fieldvalue_array");//修改值
				ArrayList field_type_list = (ArrayList)this.getFormHM().get("fieldtype_array");//指标类型
				ArrayList disformat_list = (ArrayList)this.getFormHM().get("disformat_array");
				DbWizard db = new DbWizard(this.frameconn);
				//获得临时表
				String table_name=this.userView.getUserName()+"templet_"+tabid; 
				if(!"0".equalsIgnoreCase(task_id)){
					table_name="templet_"+tabid;
				}
				//当只有部门单位 清空职位
				if(field_item_list.contains("B0110_2")&&field_item_list.contains("E0122_2")&&!field_item_list.contains("E01A1_2")){
					if(db.isExistField(table_name, "E01a1_2", false)){
						field_item_list.add("E01a1_2");
						field_value_list.add("");
					    field_type_list.add("A");
					}
				}
				//当只有单位清空职位部门
				if(field_item_list.contains("B0110_2")&&!field_item_list.contains("E0122_2")&&!field_item_list.contains("E01A1_2")){
					if(db.isExistField(table_name, "E01a1_2", false)){
						field_item_list.add("E01a1_2");
						field_value_list.add("");
						field_type_list.add("A");
					}
					if(db.isExistField(table_name, "E0122_2", false)){
						field_item_list.add("E0122_2");
						field_value_list.add("");
						field_type_list.add("A");
					}
	            }
				TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
				boolean b = bo.batchUpdateFields(field_item_list,field_value_list,field_type_list,table_name,task_id,selchecked,disformat_list);//修改指标值
				if(b){
					this.getFormHM().put("flag", "1");
				}else{
					this.getFormHM().put("flag", "0");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
     * 对于变化后的指标，判断是否具有修改指标的权限
     * @param tabid 模板号
     * @param taskid 任务编号
     * @param fieldSetSortStr 指标排列顺序
     * @param infor_type 1:人员 2：单位部门 3：岗位
     * @param sp_batch 批量方式
     * @return
     */
    public ArrayList getBeanList(String module_id,String tabid,String taskid,String approve_flag) throws GeneralException{
        try
        {
            if (tabid == null || "-1".equalsIgnoreCase(tabid))
                throw new GeneralException(ResourceFactory.getProperty("error.notdefine.tabid"));
            ArrayList lastlist = new ArrayList();// 专门存放变化后的字段（满足条件的）
            TemplateListShowBo listBo = new TemplateListShowBo(this.frameconn, this.userView, Integer.valueOf(tabid));
            listBo.setApproveFlag(approve_flag);
            ArrayList templateSetList = listBo.getTableHeadSetList(taskid);
            //批量修改显示指标的顺序与列表模式下的栏目设置一致 lis 20160723 start
            String dataTabName = listBo.getDataBo().getUtilBo().getTableName(module_id, Integer.valueOf(tabid), taskid);
            //暂时不走栏目设置
            int schemeId = -1;//listBo.getSchemeId(SafeCode.encode(PubFunc.encrypt(dataTabName)));
            // 从栏目设置中数据库中得到可以显示的
			if(schemeId > 0){
				HashMap<String, LazyDynaBean> templateSetMap = listBo.getTempleteSetMap(templateSetList);
				ArrayList<String> itemIdList = listBo.getSchemeItems(schemeId, "1");
				for (int i = 0; i < itemIdList.size(); i++) {
					String itemId = itemIdList.get(i);
					if(templateSetMap.containsKey(itemId)){
						lastlist.add(templateSetMap.get(itemId));
					}
				}
			}else{
				for (int i = 0; i < templateSetList.size(); i++) {
					LazyDynaBean abean = (LazyDynaBean) templateSetList.get(i);
					String subflag = (String) abean.get("subflag");
					String isvar = (String) abean.get("isvar");
					String chgstate = (String) abean.get("chgstate");
					String state = (String) abean.get("state");// 是否可编辑
					String flag = (String)abean.get("flag");
					if ("1".equals(subflag))// 去掉子集项
						continue;
					if("F".equalsIgnoreCase(flag))//附件
						continue;
					if ("0".equals(isvar)) {
						if ("2".equals(chgstate)) {// 变化后
							if ("2".equals(state)){
								lastlist.add(abean);
							}
						}
					}
				}
			}
            return lastlist;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
   }
}
