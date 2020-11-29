package com.hjsj.hrms.transaction.attestation.unicom;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;

public class SaveItemsTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ArrayList itemsList=(ArrayList)this.getFormHM().get("itemsList");
			RecordVo vo = new RecordVo("usra01");
			vo.setString("a0100",this.userView.getA0100());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo);
			for(int i=0;i<itemsList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)itemsList.get(i);
				String itemtype=(String)abean.get("itemtype");
				String itemid=((String)abean.get("itemid")).toLowerCase();
				String value=(String)abean.get("value");
				String deciwidth=(String)abean.get("deciwidth");
				if("N".equalsIgnoreCase(itemtype))
				{
					if("0".equals(deciwidth))
						vo.setInt(itemid, Integer.parseInt(value));
					else
						vo.setDouble(itemid, Double.parseDouble(value));
				}
				else if("D".equalsIgnoreCase(itemtype))
				{
					Calendar d=Calendar.getInstance();
					String[] temp=value.split("-");
					d.set(Calendar.YEAR, Integer.parseInt(temp[0]));
					d.set(Calendar.MONTH, Integer.parseInt(temp[1])-1);
					d.set(Calendar.DATE, Integer.parseInt(temp[2]));
					vo.setDate(itemid, d.getTime());
				}
				else if("A".equalsIgnoreCase(itemtype))
				{
					vo.setString(itemid, value);
				}
				else{
					vo.setString(itemid, value);
				}
			}
			dao.updateValueObject(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
