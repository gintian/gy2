/*
 * Created on 2005-12-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchPosBusinessListRefreshTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String codesetid=(String)this.getFormHM().get("codesetid");
		String a_code=(String)hm.get("a_code"); 
		codesetid=a_code.substring(0,2);
		ArrayList codeitemlist=new ArrayList();
		StringBuffer strsql=new StringBuffer();
		if(a_code!=null && a_code.trim().length()>2){
			strsql.append("select * from codeitem where parentid='");
			strsql.append(a_code.substring(2));
			strsql.append("' and codeitemid<>parentid and  codesetid='" + a_code.substring(0,2) + "'");
		}
		else
		{
			strsql.append("select * from codeitem where codeitemid=parentid and codesetid='" + codesetid + "'");
		}
		//【5801】系统管理-库结构-代码体系-13月份-点击修改-不改直接保存（月份顺序发生改变，不对）   jingq upd 2014.12.08
		strsql.append(" order by a0000,codeitemid");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		//String sql = "select validateflag from codeset where codesetid='"+codesetid+"'";
		try{
			String validateflag = (String)this.getFormHM().get("validateflag");
			//this.frecset = dao.search(sql);
			//while(this.frecset.next()){
			//	validateflag = this.frecset.getString("validateflag");
			//}
			this.frowset=dao.search(strsql.toString());
			while(this.frowset.next())
			{
				RecordVo codeitemvo=new RecordVo("codeitem");
				codeitemvo.setString("codesetid",this.frowset.getString("codesetid"));
				codeitemvo.setString("codeitemid",this.frowset.getString("codeitemid"));
				codeitemvo.setString("codeitemdesc",this.frowset.getString("codeitemdesc"));
				codeitemvo.setString("parentid",this.frowset.getString("parentid"));
				codeitemvo.setString("childid",this.frowset.getString("childid"));
				if(validateflag!=null&& "1".equals(validateflag)){
					codeitemvo.setDate("start_date", this.frowset.getDate("start_date"));
					codeitemvo.setDate("end_date", this.frowset.getDate("end_date"));
				}else{
					codeitemvo.setInt("invalid", this.frowset.getInt("invalid"));
				}
				codeitemvo.setInt("flag",0);
				codeitemvo.setString("corcode",this.frowset.getString("corcode"));
				if("68".equals(a_code.substring(0,2))){
					String b0110=frowset.getString("b0110");
					TrainCourseBo tbo = new TrainCourseBo(userView);
	            	int isP = tbo.isUserParent(b0110);
	            	if(isP==-1){
	            		continue;
	            	}else if(isP==2&&userView!=null&&!userView.isSuper_admin())
	            		codeitemvo.setString("b0110", "0");
	            	else
	            		codeitemvo.setString("b0110", "1");
				}
				codeitemvo.setInt("a0000", this.frowset.getInt("a0000"));
				codeitemlist.add(codeitemvo);
			}
			this.getFormHM().put("codeitemlist",codeitemlist);
			this.getFormHM().put("validateflag",validateflag);
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}		
	}

}
