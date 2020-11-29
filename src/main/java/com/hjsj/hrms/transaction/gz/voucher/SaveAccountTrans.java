package com.hjsj.hrms.transaction.gz.voucher;


import com.hjsj.hrms.businessobject.gz.voucher.VoucherBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
* 
* 类名称：SaveAccountTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 16, 2013 5:46:21 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 16, 2013 5:46:21 PM   
* 修改备注：   科目保存
* @version    
*
 */
public class SaveAccountTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		VoucherBo bo = new VoucherBo(this.frameconn,this.userView);
		String opt = (String) this.getFormHM().get("opt");
		if(opt==null|| "".equals(opt)){
			opt = (String) map.get("opt");
		}		
		String ccode = (String) this.getFormHM().get("accid");
		String ccode_name = (String) this.getFormHM().get("accname");
		String igrade1 = (String) this.getFormHM().get("accgrade");
		if(igrade1==null|| "".equals(igrade1)){
			igrade1="0";
		}
		if("new".equals(opt)){
			int igrade = Integer.parseInt(igrade1);
			bo.NewAccount(ccode, ccode_name, igrade);
			this.getFormHM().put("accid", "");
			this.getFormHM().put("flag", "1");
		}else if("update".equals(opt)){
			int igrade = Integer.parseInt(igrade1);
			String i_id = (String) this.getFormHM().get("i_id");
			bo.UpdateAccount(ccode, ccode_name, igrade, i_id);
		}else if("1".equals(opt)){
			this.getFormHM().put("flag", "1");
		}
		this.getFormHM().put("accid", "");
		this.getFormHM().put("accname", "");
		this.getFormHM().put("accgrade", "");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}


	}

}
