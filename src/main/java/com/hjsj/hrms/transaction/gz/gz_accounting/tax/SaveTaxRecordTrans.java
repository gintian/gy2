package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:SaveTaxRecordTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 13, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SaveTaxRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("tax_table_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("tax_table_record");			
		try
		{
			TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());
			ArrayList fieldlist=taxbo.getFieldlist();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			for(int i=0;i<list.size();i++)
			{
	    		//dao.updateValueObject(list);
				RecordVo vo =(RecordVo)list.get(i);
				//dao.updateValueObject(vo);
				int tax_max_id = vo.getInt("tax_max_id");
				String tablename = vo.getModelName();
		        StringBuffer buf = new StringBuffer();
		        buf.append(" update "+ tablename+" set ");
				for(int j=0;j<fieldlist.size();j++)
				{
				 
					Field field=(Field)fieldlist.get(j);
					int itemtype=field.getDatatype();
					if(itemtype==DataType.DATE)
					{
						String namet=field.getName();
						if(vo.getDate(field.getName().toLowerCase())!=null)
						{
							
							if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							{
								buf.append(namet+"=to_date('"+vo.getString(field.getName().toLowerCase())+"','YYYY-MM-DD HH:MI:SS')");
							}
							else
							{
								buf.append(namet+"='"+vo.getDate(field.getName().toLowerCase())+"'");
							}
							buf.append(",");
						}
					}else if(itemtype==DataType.INT||itemtype==DataType.FLOAT||itemtype==DataType.DOUBLE)
					{
						
						String namet=field.getName();
						if(vo.getString(field.getName().toLowerCase())!=null)
						{
						
							
							buf.append(namet+"="+vo.getString(field.getName().toLowerCase()));
							buf.append(",");
						}
					}else
					{
						String namet=field.getName();
						if(vo.getString(field.getName().toLowerCase())!=null&&!"".equals(vo.getString(field.getName().toLowerCase())))
						{
					
						
							buf.append(namet+"='"+vo.getString(field.getName().toLowerCase())+"'");
							buf.append(",");
						}
					}
					
					
					
				}
				buf.setLength(buf.length()-1);
				buf.append(" where tax_max_id="+tax_max_id);
				dao.update(buf.toString());
				buf.setLength(0);
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
