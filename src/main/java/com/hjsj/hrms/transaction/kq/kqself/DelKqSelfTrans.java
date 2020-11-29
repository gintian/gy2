package com.hjsj.hrms.transaction.kq.kqself;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 
 * <p>Title:</p>
 * <p>Description:删除考勤自助未审批的考勤纪录</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-16:11:24:51</p>
 * @author kf-1
 * @version 1.0
 *
 */
public class DelKqSelfTrans  extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
	   ArrayList dellist=(ArrayList)this.getFormHM().get("dellist");
	   String table=(String)this.getFormHM().get("table");
	   String ta=table.toLowerCase();	   
	   ArrayList list=new ArrayList();
	   StringBuffer delSQL=new StringBuffer();
	   delSQL.append("delete from "+table);
	   delSQL.append(" where "+ta+"01=? ");
	   delSQL.append(" and "+ta+"z5 in (?,?,?)" );	   
	   for(int i=0;i<dellist.size();i++)
       {
		 ArrayList one_list = new ArrayList();
		 LazyDynaBean rec=(LazyDynaBean)dellist.get(i); 
		 String str_key=rec.get(ta+"01").toString();
		 String start=rec.get(ta+"z5").toString();
		 one_list.add(str_key);
		 one_list.add("01");
		 one_list.add("02");	
		 one_list.add("07");	
		 list.add(one_list);		 
       }
	   ContentDAO dao=new ContentDAO(this.getFrameconn());
	   try
	   {
		   dao.batchUpdate(delSQL.toString(),list);
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   getTit(table);
	}
	private void getTit(String table)
	{
		String ta =table.toString();
		ArrayList fieldlist = DataDictionary.getFieldList(table,Constant.USED_FIELD_SET);// 字段名
		ArrayList list=new ArrayList();
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem)fieldlist.get(i);
			field.setValue("");
			field.setViewvalue("");
			if("b0110".equals(field.getItemid())||field.getItemid().equals(ta+"01")|| "a0101".equals(field.getItemid())|| "e0122".equals(field.getItemid())|| "nbase".equals(field.getItemid())|| "a0100".equals(field.getItemid())|| "e01a1".equals(field.getItemid()))
		      field.setVisible(false);
			else if("q1517".equals(field.getItemid())|| "q1519".equals(field.getItemid()))
				field.setVisible(false);
			else
			{
				if("1".equalsIgnoreCase(field.getState()))
			          field.setVisible(true);
					else 
						field.setVisible(false);
				
				
			}
			FieldItem field_n=(FieldItem)field.cloneItem();
			list.add(field_n);
		} 
		this.getFormHM().put("flist", list);
	}

}
