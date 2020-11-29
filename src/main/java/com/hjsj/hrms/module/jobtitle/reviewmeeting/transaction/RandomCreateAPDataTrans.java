package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.GenerateAcPwBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ChooseStaffBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:RandomCreateAPDataTrans </p>
 * <p>Description: 为选中会议的所有参会人员随机生成账号密码</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class RandomCreateAPDataTrans extends IBusiness {

	@Override
    @SuppressWarnings({ "unchecked", "static-access" })
	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.frameconn);
		ChooseStaffBo bo = new ChooseStaffBo(this.frameconn,this.userView);
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
		try {
			String msg = "";//结果信息
			String idlist  = (String)this.getFormHM().get("idlist");//会议编号
			idlist = idlist.substring(1,idlist.length()-1);
			idlist = idlist.replaceAll("\"", "");
			String [] ids = idlist.split(",");
			sql.append(" select user_id,username,password from zc_expert_user ");
			sql.append(" where w0501='xxxxxx' and type in ('1','2') and w0301 in (");
			for(int i=0;i<ids.length;i++){
				String w0301 = ids[i];//会议编号
				w0301 = PubFunc.decrypt(w0301);
				if(i==0)
					sql.append("'"+ w0301 +"'");
				else
					sql.append(",'"+ w0301 +"'");
			}
			sql.append(")");
			ArrayList userList = new ArrayList();
			rs = dao.search(sql.toString());
			while(rs.next()){
				if(StringUtils.isEmpty(rs.getString("username"))||StringUtils.isEmpty(rs.getString("password")))
					userList.add(rs.getString("user_id"));
			}
			GenerateAcPwBo gbo = new GenerateAcPwBo(this.frameconn);
			ArrayList list = GenerateAcPwBo.generate(userList.size(), dao);
			bo.randomCreate(list, userList);//为选中人员随机生成账号密码
			
			this.getFormHM().put("msg", msg);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try {
				if(rs!=null)
					PubFunc.closeResource(rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
