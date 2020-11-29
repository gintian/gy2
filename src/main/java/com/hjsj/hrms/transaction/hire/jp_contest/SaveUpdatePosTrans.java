package com.hjsj.hrms.transaction.hire.jp_contest;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:SaveUpdatePosTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 27, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SaveUpdatePosTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("z07_set_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("z07_set_record");			
		
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			for(int i=0;i<list.size();i++){
				//ArrayList uplist = new ArrayList();
				//ArrayList volist = new ArrayList();
				RecordVo vo =(RecordVo)list.get(i);
				String sql = "select z0700 from "+name+" where z0700 ='"+vo.getString("z0700")+"'";
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					/*String upsql = "update "+name+" set z0701=?,z0711=?,z0705=?,z0713=? where z0700=?";
					volist.add(vo.getString("z0701"));
					volist.add(vo.getString("z0711"));
					volist.add(vo.getDate("z0705"));
					volist.add(vo.getString("z0713"));
					volist.add(Integer.valueOf(vo.getString("z0700")));
					uplist.add(volist);
					dao.batchUpdate(upsql,uplist);*/
					dao.updateValueObject(vo);
				}else{
					/*String insql = "insert into "+name+"(z0700,z0701,z0705,z0711,z0713) values (?,?,?,?,?)";
					volist.add(Integer.valueOf(vo.getString("z0700")));
					volist.add(vo.getString("z0701"));
					volist.add(vo.getDate("z0705"));
					volist.add(vo.getString("z0711"));
					volist.add(vo.getString("z0713"));
					uplist.add(volist);
					dao.batchInsert(insql,uplist);*/
					if("05".equalsIgnoreCase(vo.getString("z0713")))
						continue;
					dao.addValueObject(vo);
				}
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}