package com.hjsj.hrms.transaction.hire.jp_contest;

import com.hjsj.hrms.businessobject.hire.JingPingPosBo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:Search_jp_pos.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class Search_jp_pos extends IBusiness {
	public void execute() throws GeneralException {
		try {
			JingPingPosBo jpbo = new JingPingPosBo(this.getFrameconn());
			ArrayList fieldlist = jpbo.getFieldlist();
			String state = (String)this.getFormHM().get("state");
			String where = "";
			if(!(state==null || "".equals(state)))
			{
				if(!"00".equals(state))
				{
					where = " where Z0713='"+state+"' ";
					this.getFormHM().put("state",state);
				}else
				{
					this.getFormHM().put("state","00");
				}
			}else
			{
				this.getFormHM().put("state","00");
			}
			StringBuffer sql = new StringBuffer();
			String select = "";
			ArrayList list = DataDictionary.getFieldList("z07",Constant.USED_FIELD_SET);
			for (int i = 0; i < list.size(); i++) {
				FieldItem item = (FieldItem) list.get(i);
				if(!"1".equalsIgnoreCase(item.getState()))
					continue;
				Field field = (Field) item.cloneField();
				select += field.getName()+",";
			}
			sql.append(" select "+select+" z0701 as b from Z07 ");
			sql.append(where);
//			sql.append(" order by Z0700");
//			System.out.println(sql);
			String tablename = "Z07";
			this.getFormHM().put("tablename", tablename);
			this.getFormHM().put("sql", sql.toString().toUpperCase());
			this.getFormHM().put("fieldlist", fieldlist);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}