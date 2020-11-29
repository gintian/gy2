package com.hjsj.hrms.transaction.hire.zp_options.positionstat;

import com.hjsj.hrms.businessobject.hire.zp_options.stat.positionstat.PositionStatBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class PersonWishTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String zp_pos_id = (String)map.get("zp_pos_id");
			String atk=(String)map.get("atk");
			String count = (String)map.get("count");
			String condid=(String)map.get("condid");
			int type = 1;
			
			if(map.get("type")!=null)
				type=Integer.parseInt((String)map.get("type"));
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设之中配置招聘人才库！"));
			PositionStatBo bo = new PositionStatBo(this.getFrameconn());
			HashMap hm = bo.getSqlAndColumns(zp_pos_id, dbname, atk, this.userView, condid, type);
			this.getFormHM().put("select_sql",(String)hm.get("1"));
			this.getFormHM().put("where_sql",(String)hm.get("2"));
			this.getFormHM().put("order_sql",(String)hm.get("3"));
			this.getFormHM().put("columns",(String)hm.get("4"));
			this.getFormHM().put("zp_pos_name",(String)hm.get("5"));
			this.getFormHM().put("count",count);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
