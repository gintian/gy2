package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 
* 
* 类名称：AddDataCollectTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 1:19:58 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 1:19:58 PM   
* 修改备注：   新增人员取得年月标识
* @version    
*
 */
public class AddDataCollectTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
		    String temp_str="";   
		    Date dt = new Date();   
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   
		    temp_str=sdf.format(dt);   
			DataCollectBo databo = new DataCollectBo(this.frameconn,this.userView);
			boolean isHaveItem = databo.isHaveItem(fieldsetid);
			if(!isHaveItem){
				throw GeneralExceptionHandler.Handle(new Exception("当前用户没有该子集下的全部指标权限，不允许增加人员!"));
			}

			String[] ym=StringUtils.split(temp_str,"-");
			
			this.getFormHM().put("theyear", ym[0]);
			this.getFormHM().put("themonth",ym[1]);
			this.getFormHM().put("fieldsetid",fieldsetid);
		}
		catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
