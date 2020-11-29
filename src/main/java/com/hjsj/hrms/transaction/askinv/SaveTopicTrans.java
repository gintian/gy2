/*
 * Created on 2005-5-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.businessobject.askinv.TopicBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveTopicTrans extends IBusiness {

	 public void execute() throws GeneralException {
        RecordVo vo=(RecordVo)this.getFormHM().get("topicov");

        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        DateStyle first_date=(DateStyle)this.getFormHM().get("first_date");
        String a_id="";
        
        if(hm.get("a_id")!=null)
        	a_id=PubFunc.decryption(hm.get("a_id").toString());  
        if(vo==null)
            return;
        String judge=(String)this.getFormHM().get("judge");
       
        /*fckeditor 提交内容过滤注入js代码  guodd 2019-05-06 */
        String description = PubFunc.keyWord_reback(vo.getString("description"));
        description = PubFunc.stripScriptXss(description);
        vo.setString("description", description);
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
       
        if("1".equals(judge))
        {
            insert(vo, first_date, dao);
        }
        else if("0".equals(judge))
        {
	        /**点编辑链接后，进行保存处理*/
            cat.debug("update_topicvo="+vo.toString());
            save_edit(vo, first_date, dao);
        }
        else
        {
	        /**点结束调查链接后，进行保存处理*/
            save_end(a_id, dao);            
        }
    }

	/**
	 * @param a_id
	 * @param dao
	 * @throws GeneralException
	 */
	private void save_end(String a_id, ContentDAO dao) throws GeneralException {
		RecordVo end_vo=new RecordVo("investigate");
		try
		{
			end_vo.setString("id",a_id);
			end_vo=dao.findByPrimaryKey(end_vo);
			end_vo.setDate("enddate",DateStyle.getSystemTime());  
			end_vo.setInt("flag",0);
			cat.debug("end_topic_update_topicvo="+end_vo.toString());			
		    dao.updateValueObject(end_vo);
		}
		catch(Exception sqle)
		{
		     sqle.printStackTrace();
		     throw GeneralExceptionHandler.Handle(sqle);            
		}
	}

	/**
	 * @param vo
	 * @param first_date
	 * @param dao
	 * @throws GeneralException
	 */
	private void save_edit(RecordVo vo, DateStyle first_date, ContentDAO dao) throws GeneralException {
		String content=vo.getString("content")==null?"":vo.getString("content");
		String selectPerson = (String)this.getFormHM().get("selectPerson");
		TopicBo topicBo = new TopicBo(this.getFrameconn(),this.userView);
		String noticeperson = (String)this.getFormHM().get("noticeperson");
		this.getFormHM().put("noticeperson", noticeperson);
		this.getFormHM().put("selectPerson", selectPerson);
		
		vo.setString("content",PubFunc.doStringLength(content,100));
		vo.setDate("releasedate",first_date.getDataStringToSecond());  
		if("1".equals(vo.getString("flag")))
			vo.setString("enddate",null);
		try
		{
			dao.updateValueObject(vo);
			topicBo.deletePriv(selectPerson, vo.getString("id"));//xuj 2009-10-28 修改热点调查时，调查对象修改不起作用，当减少调查对象时并未更新数据,在保存之前做了清空
			if(selectPerson!=null&&selectPerson.trim().length()>0){
				topicBo.savePriv(selectPerson, vo.getString("id"));
			}
		}
		catch(Exception sqle)
		{
		     sqle.printStackTrace();
		     throw GeneralExceptionHandler.Handle(sqle);            
		}
	}

	/**
	 * @param vo
	 * @param first_date
	 * @param dao
	 * @throws GeneralException
	 */
	private void insert(RecordVo vo, DateStyle first_date, ContentDAO dao) throws GeneralException {
		/**新加建议，进行保存处理*/
		String selectPerson = (String)this.getFormHM().get("selectPerson");
		TopicBo topicBo = new TopicBo(this.getFrameconn(),this.userView);
		
		String noticeperson = (String)this.getFormHM().get("noticeperson");
		this.getFormHM().put("noticeperson", noticeperson);
		this.getFormHM().put("selectPerson", selectPerson);
		String chflag = (String)this.getFormHM().get("chflag");
		chflag=chflag!=null&&chflag.trim().length()>0?chflag:"";
		String trainid = (String)this.getFormHM().get("trainid");
		try
		{
			trainid=trainid!=null&&trainid.trim().length()>0?trainid:"";
			if("1".equals(chflag)){
				
				if(trainid!=null&&trainid.trim().length()>0&&selectPerson!=null
						&&selectPerson.trim().length()>0){
					topicBo.saveTrainPriv(selectPerson,trainid);
				}
			}
			
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			String id=idg.getId("investigate.id");
			vo.setString("id",id);
			String content=vo.getString("content")==null?"":vo.getString("content");

			vo.setString("content",PubFunc.doStringLength(content,250));
			vo.setDate("releasedate",first_date.getDataStringToSecond());  
			cat.debug("add_topicvo="+vo.toString());           
			if(selectPerson!=null&&selectPerson.trim().length()>0){
				topicBo.savePriv(selectPerson,id);
			}
		    dao.addValueObject(vo);
		    
		    /**非su创建时保存资源权限*/
		    if(!(this.getUserView().isAdmin()&& "1".equals(this.userView.getGroupId())))
		    {
		    	UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
		    	user_bo.saveResource(vo.getString("id"),this.userView,IResourceConstant.INVEST);
		    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);  
		}
	}

}
