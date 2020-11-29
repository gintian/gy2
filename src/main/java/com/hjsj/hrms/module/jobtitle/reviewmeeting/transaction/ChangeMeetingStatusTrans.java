package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingBo;
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
 * <p>Title:ChangeMeetingStatusTrans </p>
 * <p>Description: 启动、暂停、结束评审会议类</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ChangeMeetingStatusTrans extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String idlist  = (String)this.getFormHM().get("idlist");//会议编号
		idlist = idlist.substring(1,idlist.length()-1);
		idlist = idlist.replaceAll("\"", "");
		String [] ids = idlist.split(",");
		String type  = (String)this.getFormHM().get("type");//当前操作 =1启动，=2暂停，=3结束
		ContentDAO dao = new ContentDAO(this.frameconn);
		ReviewMeetingBo rmBo = new ReviewMeetingBo(this.frameconn,userView);
		RowSet rs = null;
		boolean flag = false;//判断操作是否选非法
		String msg = "";//提示信息
		StringBuffer message = new StringBuffer();
		ArrayList<RecordVo> volist = new ArrayList<RecordVo>();//将要修改的会议编号和状态放入数组
		try {
			for(int i=0;i<ids.length;i++){
				String w0301 = ids[i];//会议编号
				w0301 = PubFunc.decrypt(w0301);
				String sql = "select W0321 from W03 where W0301='"+w0301+"'";
				rs = dao.search(sql);
				if(rs.next()){
					String status = rs.getString("W0321");
					if("1".equals(type)){//启动						
						if("01".equals(status)||"09".equals(status)){//起草和暂停的可以启动
							RecordVo resultVo = new RecordVo("W03");
							resultVo.setString("w0301", w0301);
							resultVo.setString("w0321", "05");
							volist.add(resultVo);
							
							rmBo.executeBusiness(message, w0301);//处理业务
							msg = "会议已启动！";
						}else {
							flag = true;
							msg = "只有起草和暂停状态的会议才能启动！";
							break;
						}
					}else if("2".equals(type)){//暂停
						if("05".equals(status)){//执行中的可以暂停
							RecordVo resultVo = new RecordVo("W03");
							resultVo.setString("w0301", w0301);
							resultVo.setString("w0321", "09");
							volist.add(resultVo);
							msg = "会议已暂停！";
						}else {
							flag = true;
							msg = "只有执行中的状态的会议才能暂停！";
							break;
						}
					}else if("3".equals(type)){//结束
						if("05".equals(status)||"09".equals(status)){//暂停和执行的可以结束
							RecordVo resultVo = new RecordVo("W03");
							resultVo.setString("w0301", w0301);
							resultVo.setString("w0321", "06");
							volist.add(resultVo);
						}else {
							flag = true;
							msg = "只有暂停和执行状态的会议才能结束！";
							break;
						}
					}
				}
			}
			if(message.length()>0){
				flag = true;
				msg = message.toString();
			}
			if(flag){//非法操作
				this.getFormHM().put("msg", msg);
				this.getFormHM().put("flag", "1");
			}else{//修改会议状态
				for(int i = 0;i < volist.size();i++){
					RecordVo resultVo = volist.get(i);
					String w0301 = resultVo.getString("w0301");
					String w0321 = resultVo.getString("w0321");
					dao.update("update W03 set W0321 = '"+ w0321 +"' where W0301 = '"+ w0301 +"'");
				}
				if("1".equals(type)){//启动会议
					//删除没有模板的账号密码(清脏数据)
		    		StringBuffer del_sql = new StringBuffer();
		    		del_sql.append("delete from zc_expert_user where type in ('1','2') and w0101 not in(");
		    		del_sql.append(" select w0101 from zc_expert_user where w0501='xxxxxx' and type in ('1','2'))");
		    		del_sql.append(" or w0301 not in(select w0301 from W03)");
		    		dao.delete(del_sql.toString(), new ArrayList());
				}
				this.getFormHM().put("msg", msg);
				this.getFormHM().put("flag", "0");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
