package com.hjsj.hrms.transaction.general.template.myapply;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListBusinessApplyTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	try
	{ 
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String operationcode="";
		if(hm.get("operationcode")!=null)
		{
			operationcode=(String)hm.get("operationcode");
			
		}
		this.getFormHM().put("operationcode",operationcode);
		
	    ArrayList setlist = this.searchBusinessApplyList(operationcode);
	    this.getFormHM().put("setlist", setlist);
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	}
    }

    public ArrayList searchBusinessApplyList(String operationcode) throws GeneralException
    {

	UserView userView = this.getUserView();
	
	//业务用户关联自助用户 按自助用户走
	if(userView.getS_userName()!=null&&userView.getS_userName().length()>0&&userView.getStatus()==0&&userView.getBosflag()!=null){
		userView=new UserView(userView.getS_userName(), userView.getS_pwd(), this.getFrameconn());
		try {
			userView.canLogin();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
	String type=(String)map.get("type");  //1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整  23考勤业务  24：非考勤业务(业务申请不包含考勤信息)
	
	String tabids="";
	if(type!=null&& "24".equals(type))
	{
		TemplateTableParamBo tp=new TemplateTableParamBo(this.frameconn); 
    	tabids=tp.getAllDefineKqTabs(0); 
		if(tabids.length()==0)
			tabids+=",-1000"; 
	}
	
	ArrayList list = new ArrayList();
	StringBuffer strsql = new StringBuffer();
	String _static="static";
	if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
		_static="static_o";
	}
	strsql.append("select a.TabId,a.Name,a.operationcode,b.operationname,b."+_static+" "+_static+" from ");
	strsql.append("template_table a ,operation b where a.operationcode=b.operationcode ");
	strsql.append("and b.operationtype <> 0 ");
	if(operationcode!=null&&operationcode.trim().length()>0)
		strsql.append(" and a.operationcode like '"+operationcode+"%'");
	//and a.operationcode ='0130'
	//strsql.append("  and a.sp_flag=1");
	strsql.append(" ORDER BY tabid");
	ContentDAO dao = new ContentDAO(this.getFrameconn());
	try
	{
	    this.frowset = dao.search(strsql.toString());
	    while (this.frowset.next())
	    { // 权限控制
		   if(this.frowset.getString(_static)!=null&& "1".equals(this.frowset.getString(_static)))
	          if (!userView.isHaveResource(IResourceConstant.RSBD, this.frowset.getString("tabid")))
		        continue;
		   if(this.frowset.getString(_static)!=null&& "2".equals(this.frowset.getString(_static)))
		          if (!userView.isHaveResource(IResourceConstant.GZBD, this.frowset.getString("tabid")))
			        continue;
		   if(this.frowset.getString(_static)!=null&& "8".equals(this.frowset.getString(_static)))
		          if (!userView.isHaveResource(IResourceConstant.INS_BD, this.frowset.getString("tabid")))
			        continue;
		   if(this.frowset.getString(_static)!=null&& "3".equals(this.frowset.getString(_static)))
		          if (!userView.isHaveResource(IResourceConstant.PSORGANS, this.frowset.getString("tabid")))
			        continue;
		   if(this.frowset.getString(_static)!=null&& "4".equals(this.frowset.getString(_static)))
		          if (!userView.isHaveResource(IResourceConstant.PSORGANS_FG, this.frowset.getString("tabid")))
			        continue;
		   if(this.frowset.getString(_static)!=null&& "5".equals(this.frowset.getString(_static)))
		          if (!userView.isHaveResource(IResourceConstant.PSORGANS_GX, this.frowset.getString("tabid")))
			        continue;
		   if(this.frowset.getString(_static)!=null&& "6".equals(this.frowset.getString(_static)))
		          if (!userView.isHaveResource(IResourceConstant.PSORGANS_JCG, this.frowset.getString("tabid")))
			        continue;
		   if(this.frowset.getString(_static)!=null&&("10".equals(this.frowset.getString(_static))|| "11".equals(this.frowset.getString(_static))))
			   continue;
		   RecordVo vo = new RecordVo("template_table");
		   String tabid=this.frowset.getString("tabid");
		   if((tabids+",").indexOf(","+tabid+",")!=-1) //不包含考勤业务申请信息
		   {
			   continue;
		   }
		   
		   vo.setString("tabid", this.frowset.getString("tabid"));
		   vo.setString("name", this.frowset.getString("name"));
		   list.add(vo);
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
	return list;
    }

}
