/*
 * Created on 2005-5-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteTopicTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	 public void execute() throws GeneralException {
        ArrayList topiclist=(ArrayList)this.getFormHM().get("selectedlist");
        ArrayList itemlist=new ArrayList();
            
        if(topiclist==null||topiclist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
             
        
        try
        {
        	//删除主题表
               	
        	dao.deleteValueObject(topiclist);
          
        	this.frowset=dao.search("select * from investigate_item where id not in (select id from investigate)");
        	String temp="";
        	  //删除要点表
        	ArrayList lsitem=new ArrayList();
        	 while(this.frowset.next())
        	 {
        	 	 ArrayList pointlist=new ArrayList();
        	 	 pointlist.add(this.frowset.getString("itemid"));
        	 	 lsitem.add(this.frowset.getString("itemid"));
        	 	 dao.delete("delete from investigate_point where itemid=?",pointlist); 
        	 }
        	
        	  //删除项目表及结果表、项目内容表
        	 for(int i=0;i<lsitem.size();i++)
        	 {
        	 	ArrayList pointlist=new ArrayList();
        	 	pointlist.add(lsitem.get(i).toString());
        	 	dao.delete("delete from investigate_item where itemid=?",pointlist);
        	 	dao.delete("delete from investigate_result where itemid=?",pointlist);
        	 	//dao.delete("delete from investigate_content where itemid=?",pointlist);
        	 }
//        	 PreparedStatement ps=this.getFrameconn().prepareStatement("delete from investigate_content where itemid=?");
//        	 for(int i=0;i<lsitem.size();i++)
//        	 {
//        	 	ps.setString(1,lsitem.get(i).toString().trim());
//        	 	ps.executeUpdate();
//        	 }
        	 StringBuffer strsql=new StringBuffer();
        	 strsql.append("delete from investigate_content where itemid=?");
        	 for(int i=0;i<lsitem.size();i++)
        	 {
        		ArrayList paramlist=new ArrayList();
        		paramlist.add(lsitem.get(i).toString().trim());
        		dao.update(strsql.toString(),paramlist);
        	 }
        }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }


}
