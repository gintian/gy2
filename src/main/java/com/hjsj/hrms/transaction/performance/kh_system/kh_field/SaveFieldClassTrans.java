package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.servlet.performance.KhFieldTree;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveFieldClassTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String saveandcontinue=(String)this.getFormHM().get("saveandcontinue");
			String flag= (String)this.getFormHM().get("flag");
			String parent_id = (String)this.getFormHM().get("parent_id");
			String scope = (String)this.getFormHM().get("scope");
			int scopeip=Integer.parseInt(scope);
			String pointname = SafeCode.decode((String)this.getFormHM().get("pointname"));
			//pointname=pointname.replaceAll("\"", "\\\"");
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			String type=(String)this.getFormHM().get("type");//=1new;=2edit
			String pointsetid = (String)this.getFormHM().get("pointsetid");
			String b0110 ="HJSJ";
			KhFieldBo bo = new KhFieldBo(this.getFrameconn());
			if(this.userView.getStatus()==0)
			{
				if(this.userView.getManagePrivCodeValue()!=null&&!"".equals(this.userView.getManagePrivCodeValue()))
				{
//					b0110 = this.userView.getManagePrivCodeValue();
				}
			}
			else if(this.userView.getStatus()==4)
			{
				String a0100=this.userView.getA0100();
				String pre = this.userView.getDbname();
				String unit=bo.getB0110(pre, a0100);
				if(unit!=null&&!"".equals(unit))
				{
					b0110 = unit;
				}
			}
//			if(!this.userView.isAdmin() && !this.userView.getGroupId().equals("1"))
//			{
			 b0110 = KhFieldTree.getyxb0110(this.userView,this.getFrameconn());
			 b0110 = b0110==null|| "".equalsIgnoreCase(b0110)?"HJSJ":b0110;

//			}
			
//			TrainCourseBo tbo = new TrainCourseBo(this.getUserView());//用户所在单位
//			String s = tbo.getUnitIdByBusi();
			if("1".equals(type))//new
			{
				/**b0110全部存入hjsj和cs保持一致*/
			      pointsetid=String.valueOf(bo.saveFieldClass(parent_id, pointname, flag, b0110, subsys_id,scopeip));
			      if(!(this.userView.isAdmin()&& "1".equals(this.userView.getGroupId())))
					{
						UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
						user_bo.saveResource(pointsetid,this.userView,IResourceConstant.KH_FIELD);
					}
			
			}
			else//edit
			{
				bo.editFieldClass(pointname, flag, pointsetid,scopeip);
				ArrayList list = new ArrayList();
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				bo.configChildFlag(pointsetid, flag, dao, list);
				dao.batchUpdate(list);
			}
			this.getFormHM().put("type",type);
			this.getFormHM().put("pointname",SafeCode.encode(pointname));
			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("parent_id",parent_id+"");
			this.getFormHM().put("isClose",saveandcontinue);
			this.getFormHM().put("pointsetid",pointsetid);
			this.getFormHM().put("validflag","1");
			this.getFormHM().put("isrefresh","2");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	/**
	 * 通过部门得到所属单位
	 * */
	public String getUnit(String codeid){
		String unit = "";
		try{
			String style = "";//返回UM或者UN
			StringBuffer sb = new StringBuffer();
			sb.append("select codesetid,codeitemid from organization where codeitemid= (select parentid from organization where codeitemid='"+codeid+"')");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				style = this.frowset.getString("codesetid");
				unit = this.frowset.getString("codeitemid");
			}
			if("UM".equalsIgnoreCase(style))
				getUnit(unit);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return unit;
	}
	

}
