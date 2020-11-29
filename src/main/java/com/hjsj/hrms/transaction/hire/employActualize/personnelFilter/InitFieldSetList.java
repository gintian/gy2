package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class InitFieldSetList extends IBusiness {

	public void execute() throws GeneralException {
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String operate2=(String)hm.get("operate2");
		if(operate2!=null)
		{
			this.getFormHM().put("selectedFieldList",new ArrayList());
			hm.remove("operate2");
		}
		
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			StringBuffer sql=new StringBuffer("select fieldSetId,fieldSetDesc from fieldSet where useFlag=1 ");
			ArrayList fieldSetList=new ArrayList();
			String fieldSetIDs="";
			this.frowset=dao.search("select * from constant where constant='ZP_SUBSET_LIST'");
			if(this.frowset.next())
			{
				fieldSetIDs=Sql_switcher.readMemo(this.frowset,"str_value");
			}
			if(!"".equals(fieldSetIDs))
			{
				if(fieldSetIDs.indexOf(",")==-1)
				{
					sql.append(" and fieldSetId='"+fieldSetIDs+"'");
				}
				else
				{
					String[] fielsSetID=fieldSetIDs.split(",");
					sql.append(" and fieldSetId in ( ");
					StringBuffer whl=new StringBuffer("");
					for(int i=0;i<fielsSetID.length;i++)
					{
						whl.append(",'"+fielsSetID[i]+"'");
					}
					sql.append(whl.substring(1)+" ) ");
				}
				
				this.frowset=dao.search(sql.toString());
				while(this.frowset.next())
				{
					 CommonData dataobj = new CommonData(this.frowset.getString("fieldSetId"), this.frowset.getString("fieldSetDesc"));
					 fieldSetList.add(dataobj);
				}
				this.getFormHM().put("fieldSetList",fieldSetList);
				
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
