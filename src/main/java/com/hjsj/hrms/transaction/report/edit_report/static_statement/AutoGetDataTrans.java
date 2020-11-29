package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
public class AutoGetDataTrans extends IBusiness {

	public void execute() throws GeneralException {
	String tabid = (String)this.getFormHM().get("tabid");
	String scopeid =(String)this.getFormHM().get("scopeid");
	String ownerunitid = "";
	boolean flag = false;
	ContentDAO dao=new ContentDAO(this.getFrameconn());
	try
	{
		String sql = "select * from tscope where scopeid="+scopeid;
		this.frowset = dao.search(sql);
		if(this.frowset.next()){
			ownerunitid = this.frowset.getString("owner_unit");
			if(ownerunitid.indexOf("UN")!=-1||ownerunitid.indexOf("UM")!=-1){
				ownerunitid = ownerunitid.substring(2, ownerunitid.length()).replace("`", "");
			}
		}
		RecordVo vo=new RecordVo("operuser");
		vo.setString("username",this.userView.getUserName());
		vo=dao.findByPrimaryKey(vo);
		vo.setString("tablepriv","");
		String hz=null;
		if(vo!=null)
		{
			String dept_id=vo.getString("org_dept");
			StringBuffer buf=new StringBuffer();
			if(dept_id==null|| "".equals(dept_id))
				vo.setString("fieldpriv", "");  //用作显示归属单位的汉字描述
			else
			{
				String[] itemarr=StringUtils.split(dept_id,"`");
				
				for(int i=0;i<itemarr.length;i++)
				{
					String codesetid=itemarr[i].substring(0,2);
					String value=itemarr[i].substring(2);
					if(value.length()==0)
						flag =true;
					buf.append(hz);
					buf.append(",");
				}
			
				
			}
			if(buf.length()==0){
				if(this.userView.isSuper_admin()){
					flag=true;
				}else{
					String codes  = this.userView.getManagePrivCodeValue();
					if(ownerunitid.indexOf(codes)!=-1)
					flag =true;
				}
			}else{
				String codes2[] =  buf.toString().split(",");
				for(int i =0;i<codes2.length;i++){
					if(ownerunitid.indexOf(codes2[i])!=-1){
						flag =true;
						break;
					}
				}
			}
		}
		if(flag)
		this.getFormHM().put("info","ok");
		else
		this.getFormHM().put("info","fail");
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
		throw GeneralExceptionHandler.Handle(ex);
	}
	
	}

}
