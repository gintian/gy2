package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.servlet.performance.KhFieldTree;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;

public class AddFieldClassTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String type=(String)map.get("type");
			String pointsetid=(String)map.get("pointsetid");
			String subsys_id = (String)map.get("subsys_id");
			String pointname="";
			String validflag = "1";
			String scope = "";
			Connection con=null;
			RowSet rs = null;
			StringBuffer sql = new StringBuffer();
			
			if(!"root".equalsIgnoreCase(pointsetid)){//根节点
				
			sql.append("select scope,pointsetid,pointsetname,parent_id,b0110,child_id,seq,validflag,subsys_id from per_pointset where pointsetid='"+pointsetid+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql.toString());
			String b0110 = "";
			if(this.frowset.next()){
				b0110 = this.frowset.getString("b0110");
			}
			String yxb0110 = KhFieldTree.getyxb0110(this.userView,this.getFrameconn());//根据业务范围得到一个单位编码
			String b01s0 =  this.userView.getUserOrgId();//所在单位
			int yxb0110le = yxb0110.length();
			int b0110le = b0110.length();
			if(yxb0110le<b0110le)
				yxb0110le = yxb0110.length();
			else 
				yxb0110le = b0110.length();
			if(!b0110.substring(0,yxb0110le).equals(yxb0110)&&!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId())&&!"hjsj".equalsIgnoreCase(b0110))
			throw GeneralExceptionHandler.Handle(new Exception("您没有该指标分类的编辑权限！"));
			}
			if("1".equals(type))//new
			{
				scope = "0";//默认共享
			}
			if("2".equals(type))//edit
			{
				KhFieldBo bo = new KhFieldBo(this.getFrameconn());
				HashMap hm =bo.getFieldClassById(pointsetid);
				pointname = (String)hm.get("name");
				scope = (String)hm.get("scope");
				validflag = (String)hm.get("flag");
			}
			this.getFormHM().put("type",type);
			this.getFormHM().put("pointsetid",pointsetid);
			this.getFormHM().put("pointname",pointname);
			this.getFormHM().put("validflag",validflag);
			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("parent_id", pointsetid);
			this.getFormHM().put("isClose","2");
			this.getFormHM().put("isrefresh","1");
			this.getFormHM().put("scope",scope);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
