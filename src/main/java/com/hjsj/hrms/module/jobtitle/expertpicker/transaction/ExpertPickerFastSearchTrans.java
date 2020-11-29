package com.hjsj.hrms.module.jobtitle.expertpicker.transaction;

import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 资格评审_专家选择控件检索
 * 
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 */
@SuppressWarnings("serial")
public class ExpertPickerFastSearchTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		try {
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("experts_picker_00001");
			String sql = catche.getTableSql();
			
			StringBuilder newsql = new StringBuilder(sql);
			ArrayList<String> valuesList = new ArrayList<String>();
			String subModuleId = (String) this.getFormHM().get("subModuleId");// 模块id
			if("experts_picker_00001".equals(subModuleId)){
				// 查询类型，1为输入查询，2为方案查询
				String type = (String) this.getFormHM().get("type");
				if("1".equals(type)) {
					// 输入的内容
					valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
					int index = sql.lastIndexOf("1=1");
					newsql = new StringBuilder(sql.substring(0, index+4));
					// 快速查询
					for(int i = 0; i < valuesList.size(); i++){
						String queryVal = valuesList.get(i);
						queryVal = SafeCode.decode(queryVal);
						if(i == 0){
							newsql.append(" and ");
						}else{
							newsql.append(" or ");
						}
						newsql.append("(W0103 like '%"+queryVal+"%' or W0105 like '%"+queryVal+"%' or W0107 like '%"+queryVal+"%' )");
					}
				}
			}
			
			catche.setTableSql(newsql.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
