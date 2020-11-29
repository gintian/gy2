package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

public class GetUnitIntroTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String info="";
			String b0110=(String)hm.get("b0110");
			b0110=PubFunc.decrypt(b0110);
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=parameterXMLBo.getAttributeValues();
			HashMap amap=new HashMap();
			if(map.get("org_brief")!=null&&((String)map.get("org_brief")).trim().length()>0)
			{
				String temp=(String)map.get("org_brief");
				String[] temps=temp.split(",");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				RowSet rowSet = null;
				rowSet=dao.search("select b0110,"+temps[0]+" from b01 where b0110='"+b0110+"'");
				if(rowSet.next())
					info=Sql_switcher.readMemo(rowSet,temps[0]);
				
			}
			this.getFormHM().put("info",info);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
