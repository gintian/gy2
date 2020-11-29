package com.hjsj.hrms.transaction.gz.gz_accounting.in_out;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;


/**
 * 
 *<p>Title:GetRelationSchemeListTrans.java</p> 
 *<p>Description:读取方案列表</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 14, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class GetRelationSchemeListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList schemeList=new ArrayList();
			
			DbWizard dbWizard=new DbWizard(this.getFrameconn());
			if(dbWizard.isExistTable("gz_relation",false))
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());		
				Table table=new Table("gz_relation");
				if (!dbWizard.isExistField("gz_relation", "seq"))
				{
					Field temp4=new Field("seq","序号");
					temp4.setDatatype(DataType.INT);
					temp4.setNullable(true);
					temp4.setKeyable(false);	
					table.addField(temp4);	
					dbWizard.addColumns(table);// 更新列
				}			
				
				this.frowset=dao.search("select * from gz_relation order by seq");
				String updateStr = "update gz_relation set seq=? where id=?";
				int index = 1;
				ArrayList list = new ArrayList();
				while(this.frowset.next())
				{
						String temp = this.frowset.getString("id")+"."+this.frowset.getString("name").replaceAll("\\\\", "\\\\\\\\");//由于java里面反斜杠是特殊的，把\转换成\\前台即可正常显示  zhaoxg 2013-6-25
						schemeList.add(new CommonData(this.frowset.getString("id"),temp));
					//	schemeList.add(this.frowset.getString("id")+"/"+this.frowset.getString("name"));
					
						ArrayList list1 = new ArrayList();
						list1.add(new Integer(index));
						list1.add(new Integer(this.frowset.getInt("id")));
						list.add(list1);
						index++;						
				}
				dao.batchUpdate(updateStr, list);
			}
			this.getFormHM().put("schemeList",schemeList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
