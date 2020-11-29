package com.hjsj.hrms.transaction.org.gzdatamaint;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class DeleteRecodTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("position_set_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("position_set_record");		
		try{
			HashSet hsset = new HashSet();
			for(int i=0;i<list.size();i++){
				RecordVo vo = (RecordVo)list.get(i);
				if("B".equalsIgnoreCase(name.substring(0,1))){
					String b0110=vo.getString("b0110");
					hsset.add(b0110);
				}else{
					String e01a1=vo.getString("e01a0");
					hsset.add(e01a1);
				}
			}
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			dao.deleteValueObject(list);
			if(hsset.size()>0&&name!=null&&name.trim().length()>0)
				sortItem(dao,name,hsset);
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	private void sortItem(ContentDAO dao,String name,HashSet hsset){
		ArrayList sortlist = new ArrayList();
		try {
			Iterator  it   =  hsset.iterator(); 
			while(it.hasNext()){
				String A0100 = (String)it.next();
				if("B".equalsIgnoreCase(name.substring(0,1)))
					this.frowset = dao.search("select B0110,I9999 from "+name+" where B0110='"+A0100+"' order by I9999");
				else
					this.frowset = dao.search("select E01A1,I9999 from "+name+" where E01A1='"+A0100+"' order by I9999");
				int i=1;
				while(this.frowset.next()){
					ArrayList list = new ArrayList();
					list.add(i+"");
					if("B".equalsIgnoreCase(name.substring(0,1)))
						list.add(this.frowset.getString("B0110"));
					else
						list.add(this.frowset.getString("E01A1"));
					list.add(this.frowset.getString("I9999"));
					sortlist.add(list);
					i++;
				}
				String updatesql = "update "+name+" set I9999=? where ";
				if("B".equalsIgnoreCase(name.substring(0,1)))
					updatesql+="B0110=? and I9999=?";
				else
					updatesql+="E01A1=? and I9999=?";
				dao.batchUpdate(updatesql,sortlist); 
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
