package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PinyinUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:PinYinJianMaTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 17, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class PinYinJianMaTrans extends IBusiness {

	public void execute() throws GeneralException{	
		
		PubFunc pf = new PubFunc();		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		HashMap hm=this.getFormHM();
		String type=(String)hm.get("type");  
		if("dialog".equalsIgnoreCase(type))
		{
			if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) ))
			{				
				FieldItem fi = DataDictionary.getFieldItem(pinyin_field);	
				if(fi!=null&&!"0".equals(fi.getUseflag())&&fi.getUseflag().length()>0){
					this.getFormHM().put("result","yes");
					this.getFormHM().put("pinyin_field",fi.getItemdesc());
				}else{
					this.getFormHM().put("result","no");
				}
			}else
			{
				this.getFormHM().put("result","no");
			}
			return;
		}else if("transform".equalsIgnoreCase(type))
		{
			ArrayList list = this.userView.getPrivDbList();
			if(!(pinyin_field==null || "".equals(pinyin_field)))
			{
				PinYinJianMa( dao, pinyin_field, list);
			}
		}

	}
	/**
	 * 拼音转码操作
	 * @param dao
	 * @param pinyin_field
	 * @param list
	 */
	public void PinYinJianMa(ContentDAO dao,String pinyin_field,ArrayList list)
	{		
		//PubFunc pf = new PubFunc();		
		RowSet rowset=null;
		try
		{
			/**
			 * xus
			 * 获取拼音简码长度pyLength
			 * 17/01/13
			 */
			StringBuffer string = new StringBuffer();
			string.append("select itemlength from fielditem where itemid = '"+ pinyin_field+"'");
			rowset= dao.search(string.toString());
			int pyLength=50;
			if(rowset.next()){
				pyLength=rowset.getInt("itemlength");
			}
			for(int i=0;i<list.size();i++)
			{
				String dbname = (String)list.get(i);				
				StringBuffer sb = new StringBuffer();
				sb.append(" select a0100,a0101 from "+dbname+"a01 ");
				this.frowset = dao.search(sb.toString());
				String sql="update "+dbname+"a01 set "+pinyin_field.toLowerCase()+"=? where a0100=?";
				ArrayList updatalist = new ArrayList();
				while(this.frowset.next())
				{
					String a0100 = this.frowset.getString("a0100");
					String a0101 = this.frowset.getString("a0101");
					/*RecordVo rv = new RecordVo(dbname+"a01");
					rv.setString("a0100",a0100);*/
									
					ArrayList olist=new ArrayList();				
					
					if(!(a0101==null || "".equals(a0101)))
					{
						String pinyin = PinyinUtil.stringToHeadPinYin(a0101);
						//String pinyin = pf.getPinym(a0101);					
						//rv.setString(pinyin_field.toLowerCase(),pinyin);
						/**
						 * xus
						 * 超过拼音简码字段的长度时 直接返回
						 * 17/01/13
						 */
						if(pinyin.length()>pyLength)
							return;
						olist.add(pinyin);
					}else
					{
						//rv.setString(pinyin_field.toLowerCase(),a0101);
						olist.add(a0101);
					}	
					olist.add(a0100);
					updatalist.add(olist);
					//updatalist.add(rv);
				}	
				dao.batchUpdate(sql, updatalist);
			}
			//dao.updateValueObject(updatalist);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
