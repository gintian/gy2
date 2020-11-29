/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster.struct;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:ShowAllColumnTrans</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-21:10:49:32</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ShowAllColumnTrans extends IBusiness {


	public void execute() throws GeneralException {
		String infor_Flag=(String)this.getFormHM().get("infor_Flag");
		String currid=(String)this.getFormHM().get("currid");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update("update lbase set Width=1800 where Width=0 and tabid="+currid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
		ArrayList list=musterbo.getMusterFields(currid,infor_Flag);
		
		//ArrayList list=(ArrayList)this.getFormHM().get("fieldlist");
		if(list==null||list.size()==0)
			return;
		for(int i=0;i<list.size();i++)
		{
			Field item=(Field)list.get(i);
			if("A0100".equals(item.getName())|| "B0110".equals(item.getName())
					|| "E01A1".equals(item.getName()))
				continue;
			item.setVisible(true);
			if("0".equals(this.userView.analyseFieldPriv(item.getName())))
				item.setVisible(false);
			if("1".equals(this.userView.analyseFieldPriv(item.getName())))
				item.setReadonly(true);
			if("E01A1_CODE".equals(item.getName())|| "B0110_CODE".equals(item.getName()))
				item.setVisible(false);
			if("recidx".equals(item.getName()))
				item.setVisible(true);
			item.setSortable(false);
			if("3".equals(infor_Flag)&& "e0122".equalsIgnoreCase(item.getName()))
			{
				item.setLabel("所属部门");
			}
		}
		this.getFormHM().put("fieldlist",list);
		this.getFormHM().put("coumsize",list.size()+"");
	}
}
