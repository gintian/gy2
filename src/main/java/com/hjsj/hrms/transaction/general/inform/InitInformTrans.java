/**
 * 
 */
package com.hjsj.hrms.transaction.general.inform;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:InitInformTrans</p>
 * <p>Description:初始化维护维护接口</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-23:8:40:56</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class InitInformTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String codesetid=(String)hm.get("codesetid");
		String codeitemid=(String)hm.get("codeitemid");	
		String dbpre=(String)this.getFormHM().get("dbpre");
		if(codesetid==null|| "".equals(codesetid))
			return;
		/*测试主集*/
		String tablename=dbpre+"a01";
		StringBuffer strsql=new StringBuffer();
		strsql.append("select * from ");
		strsql.append(tablename);
		strsql.append(" where ");
		if("UN".equals(codesetid))
			strsql.append(" B0110 like '");
		else if("UM".equals(codesetid))
			strsql.append(" e0122 like '");	
		else if("@K".equals(codesetid))
			strsql.append(" e01a1 like '");	
		strsql.append(codeitemid.trim());
		strsql.append("%'");
		strsql.append(" order by a0000");
		this.getFormHM().put("sql",strsql.toString());
		this.getFormHM().put("tablename",tablename);
		ArrayList fieldlist=new ArrayList();
		
		ArrayList list=DataDictionary.getFieldList("A01",Constant.USED_FIELD_SET);
//		for(int i=0;i<list.size();i++)
//		{
//			FieldItem item=(FieldItem)list.get(i);
//			Field field=(Field)item.cloneField();
//			fieldlist.add(field);
//		}
		this.getFormHM().put("fieldlist",list);
	}
}
