package com.hjsj.hrms.transaction.sys.dbinit.fielditem;

import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/*
 * 代码类长度
 */
public class CodeLengthTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//<input type="text" value="<%= XXVo.getUsername()%>"> 
		String obj = (String)this.getFormHM().get("obj");
		IndexBo subset = new IndexBo(this.getFrameconn());
		String codelength = subset.getlength(obj);
		this.getFormHM().put("itemlength", codelength==null?"0":codelength);
		
		String codesetid=(String)this.getFormHM().get("codesetid");//修改了以前为代码型的指标
		if(codesetid!=null&&!"0".equals(codesetid)){
			String fieldsetid = (String)this.getFormHM().get("fieldsetid");
			String itemid=(String)this.getFormHM().get("itemid");
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				dao.update("update fielditem set itemlength="+codelength+" where fieldsetid='"+fieldsetid+"' and itemid='"+itemid+"'");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
//	public String getlength(){
//		String obj = (String)this.getFormHM().get("obj");
//		System.out.println("代码类长度 = "+obj);
//		return null;
//		
//	}

}
