package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.businessobject.dtgh.CodeUtilBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
/**
 * 
 * @author xujian
 *Jan 18, 2010
 */
public class DelPartyInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList codeitemlist = (ArrayList)this.getFormHM().get("codeitemlist");//批量删除所选项
		String a_code = (String)this.getFormHM().get("a_code");
		String codesetid = a_code.substring(0,2);
		String parentid = "";
		if(a_code.length()>2){
			parentid = a_code.substring(2);
		}
		ArrayList codeitemidlist = new ArrayList();
		if(codeitemlist!=null)
		for(int i=0;i<codeitemlist.size();i++){
			LazyDynaBean abean=(LazyDynaBean)codeitemlist.get(i);
      	   	String codeitemid=(String)abean.get("codeitemid"); 
      	   	CodeUtilBo.delCodeitem(this.frameconn, codesetid, parentid, codeitemid);
      	   	codeitemidlist.add(codesetid+codeitemid);
		}
		String codeitemid = (String)this.getFormHM().get("codeitemid");//单项删除
		if(CodeUtilBo.delCodeitem(this.frameconn, codesetid, parentid, codeitemid)){
			this.getFormHM().put("flag", "ok");
			this.getFormHM().put("uid", codesetid+codeitemid);
		}else{
			this.getFormHM().put("flag", "no");
		}
		this.checkTree(this.frameconn,codesetid);
		this.getFormHM().put("isrefresh", "delete");
		this.getFormHM().put("codeitemidlist", codeitemidlist);
	}

	private void checkTree(Connection conn,String codesetid){
		
		 StringBuffer sql =new StringBuffer();
		 ContentDAO dao=new ContentDAO(conn);
		 try{
			 //消除掉有子节点childid不正确的
			 sql.delete(0,sql.length());
		     sql.append("UPDATE ");
		     sql.append("codeitem SET childid =(SELECT MIN(codeitemid) FROM ");
		     sql.append("codeitem d");
		     sql.append(" WHERE d.parentid = ");
			 sql.append("codeitem.codeitemid AND d.parentid <> d.codeitemid and d.codesetid=codeitem.codesetid)");
		     sql.append(" WHERE  EXISTS (SELECT * FROM ");
		     sql.append("codeitem c");
		     sql.append(" WHERE c.parentid = ");
		     sql.append("codeitem.codeitemid AND c.parentid <> c.codeitemid  and codeitem.codesetid=c.codesetid) and codesetid='"+codesetid+"'");
		     dao.update(sql.toString());
//		   清除掉没有子节点childid不正确的
		    
		     StringBuffer updateParentcode=new StringBuffer();
  		updateParentcode.delete(0,updateParentcode.length());
  		updateParentcode.append("UPDATE ");
  		updateParentcode.append("codeitem SET childid =codeitemid  ");
  		updateParentcode.append(" WHERE not EXISTS (SELECT * FROM ");
  		updateParentcode.append("codeitem c");
  		updateParentcode.append(" WHERE c.parentid = ");
  		updateParentcode.append("codeitem.codeitemid and codeitem.codesetid=c.codesetid  and c.parentid<>c.codeitemid) AND codeitem.childid <> codeitem.codeitemid and codesetid='"+codesetid+"'");
		     dao.update(updateParentcode.toString());
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	}
}
