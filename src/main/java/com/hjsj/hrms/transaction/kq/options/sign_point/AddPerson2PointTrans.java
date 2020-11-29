package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class AddPerson2PointTrans extends IBusiness{
	
    public void execute() throws GeneralException {
    	try{
    		
    		String pid = (String)this.getFormHM().get("pid");
    		String a0100 = (String)this.getFormHM().get("a0100"); //格式为 人员库`人员编号:  Usr`00000009 
    		
    		if(a0100.indexOf("`")==-1)
    			return;
    		ContentDAO dao = new ContentDAO(frameconn);
    		String[] person = a0100.split("`");
    		StringBuffer sql = new StringBuffer();
    		sql.append(" select '"+person[0]+"' nbase, a0100,a0101,b0110,e0122,e01a1,a0000 from ");
    		sql.append(person[0]+"a01 where a0100='"+person[1]+"'");
    		this.frowset = dao.search(sql.toString());
    		if(this.frowset.next()){
    			sql.setLength(0);
    			sql.append(" insert into kq_sign_point_emp(pid,a0100,a0101,b0110,e0122,e01a1,nbase,a0000) values(?,?,?,?,?,?,?,?)");
    			ArrayList valueList = new ArrayList();
    			valueList.add(pid);
    			valueList.add(this.frowset.getString("a0100"));
    			valueList.add(this.frowset.getString("a0101"));
    			valueList.add(this.frowset.getString("b0110"));
    			valueList.add(this.frowset.getString("e0122"));
    			valueList.add(this.frowset.getString("e01a1"));
    			valueList.add(this.frowset.getString("nbase"));
    			valueList.add(this.frowset.getString("a0000"));
    			
    			dao.insert(sql.toString(), valueList);
    		}
    			
    		this.getFormHM().put("saveRs", "succeed");
    	}catch(Exception e){
    		e.printStackTrace();
    		this.getFormHM().put("saveRs", "failed");
    		
    	}
    }
}
