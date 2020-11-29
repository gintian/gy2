/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster.struct;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchMusterFieldsTrans</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-24:16:27:08</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchMusterFieldsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try
		{
			/**未定义信息类别,默认为人员信息*/
			String infor_kind=(String)this.getFormHM().get("inforkind");
			if(infor_kind==null|| "".equals(infor_kind))
				infor_kind="1";
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			
			String tabid=(String)hm.get("currid");
			tabid=tabid!=null?tabid:"";
			hm.remove("currid");
			
			MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
			//ArrayList list=(ArrayList)this.getFormHM().get("fieldlist");
			ArrayList list = musterbo.getMusterFieldsTemp(tabid,infor_kind);
			ArrayList fieldlist=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				CommonData vo=new CommonData();
				Field field=(Field)list.get(i);
				if("recidx".equalsIgnoreCase(field.getName()))
					continue;
				if("1".equals(infor_kind)&& "A0100".equalsIgnoreCase(field.getName()))
					continue;
				if("2".equals(infor_kind)&& "B0110_CODE".equalsIgnoreCase(field.getName()))
					continue;				
				if("3".equals(infor_kind)&& "E01A1_CODE".equalsIgnoreCase(field.getName()))
					continue;	
				if("3".equalsIgnoreCase(infor_kind)&& "e0122".equalsIgnoreCase(field.getName()))
				{
					field.setLabel("所属部门");
				}
				vo.setDataName(field.getLabel());
				vo.setDataValue(field.getName());
				fieldlist.add(vo);				
			}
			this.getFormHM().put("mfields",fieldlist);
			this.getFormHM().put("currid",tabid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
