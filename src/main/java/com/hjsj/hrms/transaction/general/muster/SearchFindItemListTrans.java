/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Aug 15, 20063:18:49 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchFindItemListTrans extends IBusiness {

	private ArrayList getMusterFieldList(int tabid,String inforkind)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			MusterBo musterbo=new MusterBo(this.getFrameconn());
			if(musterbo.openMusterTable(inforkind,"",String.valueOf(tabid),this.userView.getUserName()))
			{
				ArrayList fieldlist=musterbo.getFieldlist();
				if(fieldlist==null)
					return null;
				for(int i=0;i<fieldlist.size();i++)
				{
					CommonData vo=new CommonData();	
					Field field=(Field)fieldlist.get(i);					
					vo.setDataName(field.getLabel());
					vo.setDataValue(field.getName());
					list.add(vo);							
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
	
	public void execute() throws GeneralException {
		try
		{
			/**未定义信息类别,默认为人员信息*/
			String infor_kind=(String)this.getFormHM().get("inform_kind");
			if(infor_kind==null|| "".equals(infor_kind))
				infor_kind="1";
			String type=(String)this.getFormHM().get("type");
			if(type==null|| "".equals(type))
				type="0";	
			String tabid=(String)this.getFormHM().get("tabid");
			ArrayList list=new ArrayList();			
			if("0".equals(type))
				list=getMusterFieldList(Integer.parseInt(tabid),infor_kind);
			this.getFormHM().put("list",list);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}

}
