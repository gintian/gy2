package com.hjsj.hrms.module.gz.salaryaccounting.updisk.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.updisk.businessobject.BankDiskSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * 项目名称：hcm7.x 
 * 类名称：SaveBankTemplateTrans 
 * 类描述：新建代发银行报盘保存交易类 
 * 创建人：sunming 
 * 创建时间：2015-9-9
 * @version
 * 
 */
public class SaveBankTemplateTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		try{
			/**代发银行要求的数据内容列表**/
			ArrayList list =(ArrayList)this.getFormHM().get("selectedFieldList");
			/**薪资id**/
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			/**银行id**/
			String bank_id=(String)this.getFormHM().get("bankid");
			/**代发银行标志位置的下拉框 =0无 =1首行 =2末行**/
			String bankCheck=(String)this.getFormHM().get("bankCheck");
			/**代发银标志位置信息**/
			String bankFormat = (String)this.getFormHM().get("bankFormat");
			/**银行的作用范围 =0私有 =1共享**/
			String scope=(String)this.getFormHM().get("scope");    		
			/**银行名称**/
			String bankname=(String)this.getFormHM().get("bankname");
			bankname=SafeCode.decode(bankname);
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn(), Integer
					.parseInt(salaryid), this.userView);
			bo.deleteItem(list, bank_id);
			/**type=0 新增的保存 type=1修改的保存**/
			String type=(String) this.getFormHM().get("type");
			String bankid = "";
			if("0".equals(type)){
				bankid = bo.getMaxBank_id();
				if(bankid==null|| "".equals(bankid))
					bankid="1";
				else
					bankid = Integer.valueOf(bankid) + 1 +"";
			}else{
				bankid=bank_id;
			}
			bo.updateBankTemplate(bankCheck,bankFormat,bankname, scope,bankid,type);
			if("0".equals(type)){
				bankid = bo.getMaxBank_id();
				if(bankid==null|| "".equals(bankid))
					bankid="1";
			}else{
				bankid=bank_id;
			}
			bo.saveTemplateItem(list,bankid);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
