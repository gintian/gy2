package com.hjsj.hrms.module.jobtitle.committee.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
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
public class RandomSelectionTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		try {
			CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类
			/** 获取列头 */
			ArrayList<ColumnsInfo> columnList = committeeBo.getColumnListRandom();
			
			/** 获取查询语句 */
			String id =(String)this.getFormHM().get("id");//当前的编号，为了排除当前评委会中已有的人。
			id = PubFunc.decrypt(id);
			String moduleType =(String)this.getFormHM().get("moduleType");//模块区分 1：评委会 2：学科组
			
			
			TableConfigBuilder builder = new TableConfigBuilder( "random_selection_00001", columnList, "random_selection", userView, this.getFrameconn());
			builder.setDataSql("select * from w01 where 1=2");
			builder.setOrderBy("order by seq");
			builder.setAutoRender(false);
			builder.setSetScheme(false);
			builder.setLockable(true);
			builder.setTableTools( new ArrayList());
			builder.setSelectable(true);
			builder.setEditable(false);
			builder.setPageSize(15);
			String config = builder.createExtTableConfig();
			this.getFormHM().put("tableConfig", config.toString());
			
			String sql = committeeBo.getSelectSqlRandom(id, moduleType);
			userView.getHm().put("random_selection_tablesql", sql.replace("not in", "{state}"));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
