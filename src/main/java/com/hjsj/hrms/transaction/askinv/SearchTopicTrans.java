/*
 * Created on 2005-5-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.businessobject.askinv.TopicBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchTopicTrans extends IBusiness {

		
	     private DateStyle first_date=new DateStyle();
	     private DateStyle second_date=new DateStyle();
	     
	     public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
               
        String id=PubFunc.decryption((String)hm.get("a_id"));
        String judge=(String)this.getFormHM().get("judge");
        String flag=(String)hm.get("flag");
        String status=(String)hm.get("status");
        
		hm.remove("id");
		
		String chflag = (String) hm.get("chflag");
		chflag=chflag!=null?chflag:"";
		hm.remove("chflag");
		
		String trainid = (String) hm.get("trainid");
		trainid=trainid!=null?trainid:"";
		hm.remove("trainid");
		
		
		this.getFormHM().put("spersonlist", selectPer());
		this.getFormHM().put("sperson","00");
		this.getFormHM().put("chflag",chflag);
		this.getFormHM().put("trainid",trainid);
		
		if ("1".equals(judge)){
			first_date=DateStyle.getSystemDate();
			this.getFormHM().put("first_date",first_date);
			this.getFormHM().put("noticeperson", "");
		    this.getFormHM().put("selectPerson", "");
			return;
		}
		
      // System.out.println("SearchTopicTrans--->"+flag);
        /**
         * 按新增按钮时，则不进行查询，直接退出；
         * 
         */
        if("1".equals(judge))
            return;
        cat.debug("------>investigate_id====="+id);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("investigate");
        try
        {
			TopicBo topicBo = new TopicBo(this.getFrameconn(),this.userView);
        	String pArr[] = topicBo.getPriUser(id);
			if(pArr!=null&&pArr.length==2){
				this.getFormHM().put("noticeperson", pArr[1]);
				this.getFormHM().put("selectPerson", pArr[0]);
			}
        
           vo.setString("id",id);
           vo=dao.findByPrimaryKey(vo);
           vo.setString("flag",flag);
           vo.setString("status",status);
           String releasedate=vo.getString("releasedate");
           String enddate=vo.getString("enddate");
           first_date.setDateString(releasedate);
           second_date.setDateString(enddate);
                 
           this.getFormHM().put("first_date",first_date);
	       this.getFormHM().put("second_date",second_date);
            
        }
        catch(Exception sqle)
        {
        	
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("topicTb",vo);
        }


    }

	 	private ArrayList selectPer(){
			ArrayList list = new ArrayList();
			CommonData temp=new CommonData("00","请选择");
			list.add(temp);
			temp=new CommonData("01","人员");
			list.add(temp);
			temp=new CommonData("02","角色");
			list.add(temp);
			return list;
		}

}
