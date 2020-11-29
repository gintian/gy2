package com.hjsj.hrms.transaction.gz.voucher;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 类名称:DeleteVoucherJournalTrans
 * 类描述:删除凭证分录交易类
 * 创建人: xucs
 * 创建时间:2013-8-23 上午09:50:48 
 * 修改时间:xucs
 * 修改时间:2013-8-23 上午09:50:48
 * 修改备注:
 * @version
 *
 */
public class DeleteVoucherJournalTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String pn_id=(String) this.getFormHM().get("pn_id");//凭证id
			ArrayList boardlist = (ArrayList) this.getFormHM().get("selectedList");//已经选中的分录
			if (boardlist == null || boardlist.size() == 0)
				return;		
			ArrayList fl_idList = new ArrayList();//存放要删除的分录id
			for(int i=0;i<boardlist.size();i++){
				HashMap listMap=(HashMap) boardlist.get(i);
				String fl_id=(String)listMap.get("fl_id");
				fl_idList.add(fl_id);
			}
			if (fl_idList == null || fl_idList.size() == 0)
				return;
			Connection conn =this.getFrameconn();
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<fl_idList.size();i++){
				sb.append("'");
				sb.append(fl_idList.get(i));
				sb.append("',");
			}
			String ss =sb.toString().substring(0, sb.toString().length()-1);
			String sql="delete from gz_warrantlist where pn_id='"+pn_id+"'and fl_id in(+"+ss+")";
			dao.delete(sql, new ArrayList());
		}catch(Exception e ){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
