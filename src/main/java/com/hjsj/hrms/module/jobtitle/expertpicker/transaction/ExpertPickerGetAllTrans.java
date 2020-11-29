package com.hjsj.hrms.module.jobtitle.expertpicker.transaction;

import com.hjsj.hrms.module.jobtitle.expertpicker.businessobject.ExpertPickerBo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
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
public class ExpertPickerGetAllTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {

		try {
			ArrayList<String> EspectList = new ArrayList<String>();// 需要排除人员列表
			EspectList = (ArrayList<String>)this.getFormHM().get("espectlist");
			
			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("experts_picker_00001");
			String sql = catche.getTableSql();
			
			ArrayList<String> AllPersonList = new ArrayList<String>();//所有选中人员
			ExpertPickerBo bo = new ExpertPickerBo(this.frameconn, this.userView);
			AllPersonList = bo.getAllPerson(sql, EspectList);
			
			this.getFormHM().put("allpersonlist", AllPersonList);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
