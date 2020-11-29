package com.hjsj.hrms.transaction.train.signCollect;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 修改培训汇总记录
 * <p>Title:UpdateTrainEmpCollectSignTrans.java</p>
 * <p>Description>:UpdateTrainEmpCollectSignTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 18, 2011 10:55:54 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class UpdateTrainEmpCollectSignTrans extends IBusiness {

	public void execute() throws GeneralException {
		String courseplan=(String)this.getFormHM().get("courseplan");//课程id
		if(courseplan!=null&&courseplan.length() > 0)
		    courseplan = PubFunc.decrypt(SafeCode.decode(courseplan));
		    
		String value=(String)this.getFormHM().get("value");
		String itemid=(String)this.getFormHM().get("item");
		String nbase=(String)this.getFormHM().get("nbase");
		String a0100=(String)this.getFormHM().get("a0100");
		if(courseplan==null||courseplan.length()<=0)
		{
			this.getFormHM().put("flag", "false");
			this.getFormHM().put("mess", "保存失败：没有得到课程ID");		
			return;
		}
		if(nbase==null||nbase.length()<=0||a0100==null||a0100.length()<=0)
		{
			this.getFormHM().put("flag", "false");
			this.getFormHM().put("mess", "保存失败：没有得到人员信息");		
			return;
		}
		if(itemid==null||itemid.length()<=0)
		{
			this.getFormHM().put("flag", "false");
			this.getFormHM().put("mess", "保存失败!");	
			return;
		}
		value=value!=null&&value.length()>0?value:"0";
		StringBuffer sql=new StringBuffer();
		sql.append("update R47 set "+itemid+"="+value+" where a0100=? and nbase=? and R4101=?");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=new ArrayList();
		list.add(a0100);
		list.add(nbase);
		list.add(courseplan);
		try {
			dao.update(sql.toString(),list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.getFormHM().put("flag", "false");
			this.getFormHM().put("mess", "保存失败!");	
			return;
		}
		this.getFormHM().put("flag", "ok");
		this.getFormHM().put("mess", "");	
	}

}
