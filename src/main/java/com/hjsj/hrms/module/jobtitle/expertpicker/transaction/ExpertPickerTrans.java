package com.hjsj.hrms.module.jobtitle.expertpicker.transaction;

import com.hjsj.hrms.module.jobtitle.expertpicker.businessobject.ExpertPickerBo;
import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 资格评审_专家选择控件
 * 
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 */
@SuppressWarnings("serial")
public class ExpertPickerTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		try {
			
			String sql = (String)this.getFormHM().get("sql");//检索语句
			sql = PubFunc.decrypt(sql);
			String orderBy = (String)this.getFormHM().get("orderBy");//顺序
			orderBy = PubFunc.decrypt(orderBy);
			String searchText = (String)this.getFormHM().get("searchText");//提示语
			
			ExpertPickerBo bo = new ExpertPickerBo(this.frameconn, this.userView);
			ExpertsBo expertsBo = new ExpertsBo(this.frameconn,this.userView);

			String unit = this.userView.getUnitIdByBusi("9");
			String orgid = "";
			if(unit!=null&&!"".equals(unit)){
				if("UN`".equals(unit)){//全部范围
					orgid = unit;
				}
				else{
					String [] unitarr = unit.split("`");
					for(String arr:unitarr){
						arr = arr.substring(2,arr.length());
						orgid+=arr+",";
					}
					orgid = orgid.substring(0,orgid.length()-1);
				}
			}

			/** 获取列头 */
			ArrayList<ColumnsInfo> columnList = bo.getColumnList();
			
			TableConfigBuilder builder = new TableConfigBuilder( "experts_picker_00001", columnList, "zc_experts_picker", userView, this.getFrameconn());
			builder.setDataSql(sql + " and 1=1 ");
			builder.setOrderBy(orderBy);
			builder.setTitle("");
			builder.setAutoRender(false);
			builder.setSetScheme(false);
			builder.setLockable(true);
			builder.setSearchConfig("ZC00002207", searchText, false);
			builder.setTableTools( new ArrayList());
			builder.setSelectable(true);
			builder.setEditable(false);
			builder.setPageSize(20);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());

			this.getFormHM().put("orgid",orgid);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
