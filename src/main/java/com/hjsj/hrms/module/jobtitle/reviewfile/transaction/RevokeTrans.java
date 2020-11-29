package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.List;
/**
 * 资格评审_职称评审_上会材料_撤销
 * @createtime August 31, 2015 9:07:55 PM
 * @author chent
 */
public class RevokeTrans extends IBusiness {


    @Override
    public void execute() throws GeneralException {
    	
    	ArrayList<MorphDynaBean> idlist = (ArrayList<MorphDynaBean>)this.getFormHM().get("idlist");
    	
        ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			for(int i = 0; i<idlist.size(); i++){
				String w0501 = PubFunc.decrypt((String)idlist.get(i).get("w0501"));
				String w0301 = PubFunc.decrypt((String)idlist.get(i).get("w0301"));
				if(!isCanRevoke(w0301, w0501)) {
					this.getFormHM().put("flag", "0");
					return;
				}
			}
			
			for(int i = 0; i<idlist.size(); i++){
				String w0501 = PubFunc.decrypt((String)idlist.get(i).get("w0501"));
				String w0301 = PubFunc.decrypt((String)idlist.get(i).get("w0301"));

				String sql = "";
				ArrayList<String> list = new ArrayList<String>();
				
	            // 删除申请记录
	            sql = "delete from w05 where w05.w0501=?";
	            list.add(w0501);
	            dao.delete(sql, list);
	            
	            
	            // 删除评审账号、密码
	            sql = "delete from zc_expert_user where w0501=? and w0301=?";
	            list.clear();
	            list.add(w0501);
	            list.add(w0301);
	            dao.delete(sql, list);
	            
	            //删除投票记录
	            sql = "delete from zc_data_evaluation where w0501=? and w0301=?";
	            list.clear();
	            list.add(w0501);
	            list.add(w0301);
	            dao.delete(sql, list);
			}
			
//			// 重新获取会议检索数据源
//			ArrayList<HashMap> meetingList = new ArrayList<HashMap>();
//			TableDataConfigCache catche = (TableDataConfigCache)this.userView.getHm().get("reviewFile");
//			String sql = catche.getTableSql();// 直接获取页面数据
//			ReviewFileBo reviewFileBo = new ReviewFileBo(this.frameconn, this.userView);
//			meetingList = reviewFileBo.getMeetingList(sql);
//			this.getFormHM().put("meetingList", meetingList);
			this.getFormHM().put("flag", "1");
		} catch(Exception e) {
			throw GeneralExceptionHandler.Handle(e);
			
		}
	}
    
    /**
     * 判断申报人是否可以被撤销，评审中的不允许撤销
     * @param w0301
     * @param w0501
     * @return
     */
    private boolean isCanRevoke(String w0301,String w0501) throws GeneralException{
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	RowSet rs = null;
    	try {
    		StringBuffer sql = new StringBuffer();
        	sql.append("select * from zc_personnel_categories where categories_id = (select categories_id from zc_categories_relations where w0501=?) and w0301=? and approval_state='1'");
        	List<String> values = new ArrayList<String>();
        	values.add(w0501);
        	values.add(w0301);
        	
        	rs = dao.search(sql.toString(),values);
        	if(rs.next())
        		return false;
        	return true;
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
    }
}
