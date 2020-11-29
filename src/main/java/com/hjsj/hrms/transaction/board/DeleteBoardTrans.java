/*
 * Created on 2005-5-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.board;

import com.hjsj.hrms.businessobject.board.SendBoard;
import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteBoardTrans extends IBusiness {

	
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	 public void execute() throws GeneralException {
        ArrayList boardlist=(ArrayList)this.getFormHM().get("selectedlist");
        if(boardlist==null||boardlist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
        	//删除公告时同时删除公告浏览记录   jingq add 2014.07.14
        	RecordVo bvo = new RecordVo("announce");
        	for (int i = 0; i < boardlist.size(); i++) {
            	bvo = (RecordVo) boardlist.get(i);
            	String id = bvo.getString("id");
				frowset = dao.search("select topic,fileid from announce where id='"+id+"'");
				String topic = "";
				while(frowset.next()){
					topic = frowset.getString("topic");
					/** xus 20/5/22 【60608】VFS+UTF- 8+达梦：系统管理，流程上传、公告栏维护，删除流程和公告后上传的附件不联动删除 */
					if(StringUtils.isNotBlank(frowset.getString("fileid"))) {
						String fileid = frowset.getString("fileid");
						try {
							VfsService.deleteFile(this.getUserView().getUserName(), fileid);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				ArrayList values = new ArrayList();
				values.add(topic);
				dao.delete("delete from t_keyinfor_log where content=?", values);
			}
        	
            dao.deleteValueObject(boardlist);
            SendBoard sb = new SendBoard(this.frameconn,this.userView);
            for(Iterator it = boardlist.iterator();it.hasNext();){
            	RecordVo vo = (RecordVo)it.next();
            	sb.delBoard(vo.getString("id"));
            }
          //发布、暂停、结束 后刷新外网职位列表
            EmployNetPortalBo bo = new EmployNetPortalBo(this.frameconn);
			bo.refreshStaticAttribute();
        }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    
    }

}
