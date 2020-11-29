package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:DeleteMeetingDataTrans </p>
 * <p>Description: 删除起草状态评审会议类</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class DeleteMeetingDataTrans extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String idlist  = (String)this.getFormHM().get("idlist");//会议编号
		idlist = idlist.substring(1,idlist.length()-1);
		idlist = idlist.replaceAll("\"", "");
		String [] ids = idlist.split(",");
		ArrayList deleteIdlist = new ArrayList();//将要删除的会议编号数组
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try {
			for(int i=0;i<ids.length;i++){
				String w0301 = ids[i];//会议编号
				w0301 = PubFunc.decrypt(w0301);
				String sql = "select W0321 from W03 where W0301='"+w0301+"'";
				rs = dao.search(sql);
				if(rs.next()){
					deleteIdlist.add(w0301);
				}
			}
			ArrayList<RecordVo> volist = new ArrayList<RecordVo>();
			for(int i = 0;i < deleteIdlist.size();i++){
				RecordVo resultVo = new RecordVo("W03");
				resultVo.setString("w0301", String.valueOf(deleteIdlist.get(i)));
				volist.add(resultVo);
			}
			dao.deleteValueObject(volist);
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
