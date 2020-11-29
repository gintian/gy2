/*
 * Created on 2005-12-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sys.LibraryStructureListener;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;

public class DeletePosBusinessTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList delposbusinesslist=(ArrayList)this.getFormHM().get("selectedlist");
		if(delposbusinesslist==null||delposbusinesslist.size()==0)
            return;
		 
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		 
		StringBuilder msg = new StringBuilder();
		if(!canDeleted(dao, delposbusinesslist, msg))
		    throw new GeneralException("以下代码已在招聘职位中使用，不允许删除，请重新选择要删除的代码！<br>" + msg.toString());		 
		 
      	StringBuffer delsql=new StringBuffer();
      	ArrayList codelist = new ArrayList();
        try
        {
        	String codesetid = "";
        	String codeItemId = "";
        	for(int i=0; i<delposbusinesslist.size(); i++){
        		RecordVo codeitemvo = (RecordVo)delposbusinesslist.get(i);
        		codesetid = codeitemvo.getString("codesetid");
        		codeItemId = codeitemvo.getString("codeitemid");
        		
        		delsql.delete(0,delsql.length());
        		delsql.append("delete from codeitem");
        		delsql.append(" where codesetid=? ");
        		delsql.append(" and codeitemid like ? ");
        		
        		codelist.add(codesetid + codeItemId);
        		dao.delete(delsql.toString(), Arrays.asList(codesetid,codeItemId+"%"));
        		
        		//zxj 20151118 同时从AdminCode中删掉，避免未刷新数据字典前读取到旧数据
        		CodeItem item = new CodeItem();
        		item.setCodeid(codesetid);
        		item.setCodeitem(codeItemId);
        		AdminCode.removeCodeItem(item);
				/*通知系统代码进行了删除*/
				LibraryStructureListener.deleteCode(item);

        	}
        	this.checkTree(this.frameconn,codesetid);
        	this.getFormHM().put("codelist",codelist);
        	this.getFormHM().put("isrefresh","ok");
        }
	    catch(Exception sqle)
	    {
	       sqle.printStackTrace();
	       throw GeneralExceptionHandler.Handle(sqle);
	    }
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
       		updateParentcode.append("codeitem.codeitemid and codeitem.codesetid=c.codesetid and c.parentid<>c.codeitemid ) AND codeitem.childid <> codeitem.codeitemid and codesetid='"+codesetid+"'");
		     dao.update(updateParentcode.toString());
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	     }
	}
	
	private boolean canDeleted(ContentDAO dao, ArrayList delItems, StringBuilder msg) {
	    boolean canDel = true;
	    
	    msg.setLength(0);
	    
	    String codeSetId = "";
	    String codeItemId = "";
	    for (int i=0; i<delItems.size(); i++) {
	        RecordVo codeitemvo = (RecordVo)delItems.get(i);
	        codeSetId = codeitemvo.getString("codesetid");
	        codeItemId = codeitemvo.getString("codeitemid");
            
            //79=考试科目代码
	        if (!"79".equals(codeSetId)) 
	            continue;
	        
	        //检查考试科目是否已经被使用
	        if (!usedByHirePosition(dao, codeSetId, codeItemId))
	            continue;
	            
	        msg.append(codeSetId).append(codeItemId).append("<br>");
	    }
	    
	    //没有用到要删除的科目，那么允许删除
	    canDel = msg.length() == 0;
	    return canDel;
	}
	
	private boolean usedByHirePosition(ContentDAO dao, String codeSetId, String codeItemId) {
	    boolean used = false;
	    if (!"79".equals(codeSetId))
	        return used;
	    
	    //取当前科目对应的一级科目代码
	    StringBuilder sql = new StringBuilder();
	    sql.append("select MIN(codeitemid) as minid");
	    sql.append(" from codeitem");
	    sql.append(" where codeitemid=").append(Sql_switcher.left("'"+codeItemId+"'", Sql_switcher.length("codeitemid")));
	    sql.append(" and codesetid='79'");

	    String subjectId = "";
	    RowSet rs = null;
	    try {
            rs = dao.search(sql.toString());
            if (rs.next())
                subjectId = rs.getString("minid");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
	    
        if ("".equals(subjectId))
            return used;
        
        subjectId = "subject_" + subjectId;
	    DbWizard db = new DbWizard(this.getFrameconn()); 
	    //如果招聘职位表中没有本科目字段，那么说明本科目没用到
	    if (!db.isExistField("Z03", subjectId, false))
	        return used;
	    
	    //招聘职位表中有科目字段，检查字段值中是否用到了本科目
	    sql.setLength(0);
	    sql.append("select distinct 1 from Z03");
	    sql.append(" where ").append(subjectId).append(" like '").append(codeItemId).append("%'");
	    rs = null;
        try {
            rs = dao.search(sql.toString());
            used = rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }
	    
	    return used;	    
	}
}
