package com.hjsj.hrms.transaction.gz.gz_accounting.voucher;

import com.hjsj.hrms.businessobject.gz.GzVoucherBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;


/**
 * DeleteVoucherDataTrans.java
 * Description: 删除凭证数据
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Aug 14, 2012 6:11:55 PM Jianghe created
 */
public class DeleteVoucherDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
        {
			String msg="ok";
			String delStr=(String)this.getFormHM().get("deletestr");		
			String type=(String)this.getFormHM().get("type");
			String voucher_id=(String)this.getFormHM().get("voucher_id");
			GzVoucherBo bo=new GzVoucherBo(this.getFrameconn(),this.getUserView());
			
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			if(!safeBo.isVoucherPriv(voucher_id))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.noVoucherAuthority")+"!"));
			
			
			if("1".equals(type))
			{
				delStr = delStr.substring(0, delStr.length() - 1);
				String[] matters = delStr.split("/");
				msg = bo.delDataValue(matters);
			}else{
				msg=bo.deleteData(delStr, voucher_id);
			}
			this.getFormHM().put("msg",SafeCode.encode(msg));
			this.getFormHM().put("type", type);
        }
			
        catch(Exception sqle)
        {
            sqle.printStackTrace();
  	        throw GeneralExceptionHandler.Handle(sqle);             
        }
	}

}
