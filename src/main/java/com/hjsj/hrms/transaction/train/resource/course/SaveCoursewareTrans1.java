package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>
 * Title:SaveCoursewareTrans
 * </p>
 * <p>
 * Description:保存添加的培训课程课件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author LiWeichao
 * @version 1.0
 * 
 */
public class SaveCoursewareTrans1 extends IBusiness {

	public void execute() throws GeneralException {
		String r5115 = (String)this.getFormHM().get("r5115");
		String r5100 = (String)this.getFormHM().get("r5100");
		if(r5100!=null&&r5100.length()>0)
		    r5100=PubFunc.decrypt(SafeCode.decode(r5100));
		
		r5115=r5115==null||r5115.length()<1?"":PubFunc.keyWord_reback(r5115);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			if(r5100!=null&&r5100.length()>0){
			    
			    ArrayList list = new ArrayList();
			    list.add(r5115);
			    list.add(r5100);
			    ArrayList values = new ArrayList();
			    values.add(list);
				String sql="update r51 set r5115=? where r5100=?";
				dao.batchUpdate(sql, values);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
