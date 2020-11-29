package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @Description: 保存数据规范交易类
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:22:55 
 * @version: 1.0
 */
public class SaveProgrammeTrans extends IBusiness {
	@Override
	public void execute() throws GeneralException {
		try {
			SetupSchemeBo bo = new SetupSchemeBo(userView,this.frameconn);
			String dbnameRelationField = (String) this.getFormHM().get("dbnameRelationField");//人员库和具体代码的对应
			String dbnameRelationCodeitemid = (String) this.getFormHM().get("dbnameRelationCodeitemid");//人员库和具体代码的对应
			String arr=(String) this.getFormHM().get("arr");//存选中的指标
			String photoCheck=(String)this.getFormHM().get("photoCheck");//是否上传照片
			String peopleCheckbox=(String)this.getFormHM().get("peopleCheckbox");//存是否启用保护人员条件
			String protectDbname=(String)this.getFormHM().get("protectDbname");//保护人员条件人员库
			String protectPeople=(String)this.getFormHM().get("protectPeople");//第一个受保护人员条件
			String fieldCheckbox=(String)this.getFormHM().get("fieldCheckbox");//存是否启用保护指标条件
			String protectFieldDbname=(String)this.getFormHM().get("protectFieldDbname");//保护指标条件人员库
			String protectPeopleFieldOne=(String)this.getFormHM().get("protectPeopleFieldOne");//第二个受保护人员条件
			String protectPeopleFieldTwo=(String)this.getFormHM().get("protectPeopleFieldtwo");//第三个受保护人员条件
			//3、将规范保存到constant表
			bo.addConstant(arr,photoCheck,peopleCheckbox,protectDbname,protectPeople,
					fieldCheckbox,protectFieldDbname,protectPeopleFieldOne,protectPeopleFieldTwo,dbnameRelationField,dbnameRelationCodeitemid);
			//4、根据选中的指标判断中间表是否存在。如果中间表不存在：创建中间表，把选中的指标加上；如果中间表存在：获得中间表的表结构，判断选中的指标是否有其对应的字段，如果没有则添加。
			bo.alertMiddleTable(arr);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	
	
	
}
