package com.hjsj.hrms.transaction.smartphone;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SetStatChartTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        String charttype=(String)this.getFormHM().get("charttype");
        cond.append("select pre,dbname from dbname where pre in (");
        for(int i=0;i<dblist.size();i++)
        {
            if(i!=0)
                cond.append(",");
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        cond.append(" order by dbid");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list =new ArrayList(); 
        try {
			this.frowset=dao.search(cond.toString());
			while(this.frowset.next())
			{
				CommonData da=new CommonData();
				da.setDataName(this.getFrowset().getString("dbname"));
				da.setDataValue(this.getFrowset().getString("pre"));
				list.add(da);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String userbase=(String)this.getFormHM().get("nbase");
		this.getFormHM().put("nbase", userbase);
		this.getFormHM().put("dblist", list);
	}
}
