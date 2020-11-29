package com.hjsj.hrms.transaction.general.inform.emp.output;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title:OpenMusterTrans</p>
 * <p>Description:打开花名册</p>
 * <p>Company:hjsj</p>
 * <p>create time:2007-12-06:14:01:33</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class OpenMusterTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
	  try
	  {				
		String[] tabid=(String[])this.getFormHM().get("tabid");
		if(tabid==null)
			throw new GeneralException(ResourceFactory.getProperty("error.muster.notselect"));
		/**未定义信息类别,默认为人员信息*/
		String infor_kind=(String)this.getFormHM().get("inforkind");
		if(infor_kind==null|| "".equals(infor_kind))
			infor_kind="1";

		String dbpre=(String)this.getFormHM().get("dbpre");
		if(dbpre==null)
			dbpre="";
		String result=(String)this.getFormHM().get("result");
		result=result!=null&&result.trim().length()>0?result:"0";
		
		String returncheck=(String)this.getFormHM().get("returncheck");
		returncheck=returncheck!=null&&returncheck.trim().length()>0?returncheck:"1";
		this.getFormHM().put("returncheck",returncheck);

		
		List tablist=Arrays.asList(tabid);
		/**选中多个花名册时,仅以第一个为准*/
		String thetabid=(String)tablist.get(0);
		MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
		String tabname=null;
		if(musterbo.openMusterTable(infor_kind,dbpre,thetabid,this.userView.getUserName()))
			tabname=musterbo.getTableName(infor_kind,dbpre,thetabid,this.userView.getUserName());
		if(!musterbo.haveDataInMuster(tabname))
			  throw new GeneralException(ResourceFactory.getProperty("error.muster.notdata"));	
		
		//动态创建主键（针对以前的花名册）
		musterbo.autoCreatePrimaryKey(tabname,thetabid,infor_kind);
		
		this.getFormHM().put("mustername",tabname);
		/**把花名册指标-->显示的数据格式*/
		ArrayList fieldlist = musterbo.getFieldlist();
		fieldlist.remove(0);
		this.getFormHM().put("fieldlist",fieldlist);
		this.getFormHM().put("condlist",musterbo.getUsuallyCondList(infor_kind,this.userView));
		String sql="select * from "+tabname+" order by recidx";
		this.getFormHM().put("sql",sql);
		this.getFormHM().put("currid",thetabid);	
		this.getFormHM().put("infor_Flag",infor_kind);
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);
	  }		
	}
}
