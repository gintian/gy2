package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
* 
* 类名称：GetFormulaTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 20, 2013 3:51:02 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 20, 2013 3:51:02 PM   
* 修改备注：   获取简单复杂条件的内容
* @version    
*
 */
public class GetFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
			DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
			String flag  = bo.getXmlValue1("flag",fieldsetid);
			String value = bo.getValue(fieldsetid);
			this.getFormHM().put("condStr",SafeCode.encode(value));
			this.getFormHM().put("cexpr",SafeCode.encode(flag));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
