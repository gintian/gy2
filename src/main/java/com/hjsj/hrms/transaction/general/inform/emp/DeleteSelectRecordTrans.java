/**
 * 
 */
package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *<p>Title:DeleteSelectRecordTrans</p> 
 *<p>Description:删除选中的记录</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-16:下午03:48:28</p> 
 *@author cmq
 *@version 4.0
 */
public class DeleteSelectRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_table_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("data_table_record");
		String dbpre = this.userView.getDbpriv().toString();
		String setname=name;
		ArrayList dblist=this.userView.getPrivDbList();
		for(int i=0;i<dblist.size();i++){
			String pre=(String)dblist.get(i);
			if(pre!=null&&pre.trim().length()>0)
				setname = setname.replace(pre,"");
		}
		
		try{
			FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
			if(fieldset!=null&&!"2".equals(this.userView.analyseTablePriv(setname)))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.delete.record.competence")));
			
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			dao.deleteValueObject(list);
			HashSet hsset = null;
			if(fieldset!=null){
				ArrayList itemlist = new ArrayList();
				hsset = new HashSet();
				for(int i=0;i<list.size();i++){
					RecordVo vo = (RecordVo)list.get(i);
					String A0100=vo.getString("a0100");
					if(!fieldset.isMainset()){
						hsset.add(A0100);
					}else{
						ArrayList valuelist=new ArrayList();
						valuelist.add(A0100);
						itemlist.add(valuelist);
					}
				}
				if(fieldset!=null&&fieldset.isMainset()){
					ArrayList fieldlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
					for(int i=0;i<fieldlist.size();i++){
						FieldSet setitem = (FieldSet)fieldlist.get(i);
						if("A01".equalsIgnoreCase(setitem.getFieldsetid()))
							continue;
						String tablename = name.replace(setname,"")+setitem.getFieldsetid();
						String delall = "delete from "+tablename+" where A0100=?";
						dao.batchUpdate(delall,itemlist);
					}
				}
//				sortMiant(dao,name);
				sortItem(dao,name,hsset);
			}
			else{
				sortItem(dao,name,hsset);
			}
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private void sortMiant(ContentDAO dao,String name){
		ArrayList sortlist = new ArrayList();
		try {
			this.frowset = dao.search("select A0100 from "+name);
			int i=1;
			while(this.frowset.next()){
				ArrayList list = new ArrayList();
				list.add(i+"");
				list.add(this.frowset.getString("A0100"));
				sortlist.add(list);
				i++;
			}
			String updatesql = "update "+name+" set A0000=? where A0100=?";
			dao.batchUpdate(updatesql,sortlist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void sortItem(ContentDAO dao,String name,HashSet hsset){
		ArrayList sortlist = new ArrayList();
		try {
			Iterator  it   =  hsset.iterator(); 
			while(it.hasNext()){
				String A0100 = (String)it.next();
				this.frowset = dao.search("select A0100,I9999 from "+name+" where A0100='"+A0100+"' order by I9999");
				int i=1;
				while(this.frowset.next()){
					ArrayList list = new ArrayList();
					list.add(i+"");
					list.add(this.frowset.getString("A0100"));
					list.add(this.frowset.getString("I9999"));
					sortlist.add(list);
					i++;
				}
				String updatesql = "update "+name+" set I9999=? where A0100=? and I9999=?";
				dao.batchUpdate(updatesql,sortlist); 
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
