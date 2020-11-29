package com.hjsj.hrms.module.gz.salaryaccounting.changecompare.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.changecompare.businessobject.ChangeCompareBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 薪资发放_更新选中状态
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 *
 */
public class SetValidTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		// 薪资类别编号
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		
		try {
			// 薪资表名称
			String gz_tablename = "";
			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.getFrameconn(), Integer.parseInt(salaryid));
			String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user"); // 工资管理员
			if (manager.length() == 0) {
				gz_tablename = this.userView.getUserName() + "_salary_" + salaryid;
			} else {
				gz_tablename = manager + "_salary_" + salaryid;
			}
			
			// 发放日期、次数
			ChangeCompareBo changeCompareBo = new ChangeCompareBo(this.getFrameconn(), this.userView);// 工具类
			HashMap ycmap = changeCompareBo.getYearMonthCount(salaryid);
			String date = (String) ycmap.get("ym");
			String count = (String) ycmap.get("count");

			// 根据工资类别id得到类别下的所有项目列表
			SalaryTemplateBo salaryTemplateBo = new SalaryTemplateBo(this.getFrameconn(), this.userView);
			ArrayList itemList = salaryTemplateBo.getSalaryItemList("", salaryid, 1);
			
			/** 导入新增人员的数据 */
			SalaryAccountBo salaryAccountBo = new SalaryAccountBo(this.getFrameconn(), this.userView, Integer.parseInt(salaryid));
			salaryAccountBo.importAddManData(true, date, count, itemList, false);
			/** 从薪资表中删除薪资停发的人员 */
			changeCompareBo.removeA01Z0ManData(salaryid, gz_tablename, manager);
			/** 从薪资表中删除档案库中不存在的人员 */
			changeCompareBo.removeDelManData(salaryid, gz_tablename, manager);
			/** 更新信息发生变化的人员信息至薪资表中 */
			//属性中设置的比对指标
			String rightvalue=ctrlparam.getValue(SalaryCtrlParamBo.COMPARE_FIELD);
			changeCompareBo.updateChgInfoManData(salaryid, gz_tablename, manager, rightvalue);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
