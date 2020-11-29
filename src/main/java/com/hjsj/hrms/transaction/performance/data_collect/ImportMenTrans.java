package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
* 
* 类名称：ImportMenTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 1:13:51 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 1:13:51 PM   
* 修改备注：   手工选人入库
* @version    
*
 */
public class ImportMenTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");//数据库
			ContentDAO dao=new ContentDAO(this.frameconn);
			HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
			DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
			DataCollectBo databo = new DataCollectBo(this.frameconn,this.userView);
			String set_id  = bo.getXmlValue1("set_id",fieldsetid);
			String state_id  = bo.getXmlValue1("state_id",fieldsetid);
			String[] right_fields=new String[0];
			right_fields=(String[])this.getFormHM().get("right_fields");
			String year = (String) reqhm.get("year");
			String month = (String) reqhm.get("month");
			String ym = Sql_switcher.dateValue(year+"."+month+"."+01);
			for(int i=0;i<right_fields.length;i++){
				String sql = "insert into "+right_fields[i].replaceAll("／", "/").split("/")[1]+set_id+" (a0100,"+state_id+","+set_id+"z0,"+set_id+"z1,i9999) values ('"+right_fields[i].replaceAll("／", "/").split("/")[0]+"','01',"+ym+",'0','0')";
				dao.update(sql);
				databo.UpdateIZ(right_fields[i].replaceAll("／", "/").split("/")[1], set_id, ym);
			}
			
		}		
		catch(Exception e)
		{
			e.printStackTrace();
		
		}
	}

}
