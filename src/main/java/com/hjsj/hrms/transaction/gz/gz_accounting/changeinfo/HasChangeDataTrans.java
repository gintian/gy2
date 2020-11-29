package com.hjsj.hrms.transaction.gz.gz_accounting.changeinfo;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 判断工资套是否有新增，减少，信息变动人员
 * <p>Title:HasChangeDataTrans.java</p>
 * <p>Description>:HasChangeDataTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Apr 10, 2010  3:31:05 PM </p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class HasChangeDataTrans extends IBusiness{

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");	
		String filterid=(String)this.getFormHM().get("filterid");
		String fieldstr=SafeCode.decode((String)this.getFormHM().get("fieldstr"));
		String fromflag=(String)this.getFormHM().get("fromflag");
		String flowflag=(String)this.getFormHM().get("flow_flag");
		String error="0";
		if(filterid==null)
			filterid="null";
		if(fieldstr==null)
			fieldstr="null";
		String gz_module=(String)this.getFormHM().get("gz_module");
		gz_module=(gz_module==null?"0":gz_module); 
		 
			
		try
		{
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,gz_module);
			SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			templatebo.updateSalarySetDbpres(salaryid);
			String manager=templatebo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName()))
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select * from gz_extend_log where salaryid="+salaryid+"  and username='"+manager+"'");
				if(this.frowset.next())
				{
					
				}
				else
				{
					this.getFormHM().put("error",error);
					if("1".equals(gz_module))
						throw GeneralExceptionHandler.Handle(new Exception("该保险类别的管理员还没有建立保险表!"));
					else
						throw GeneralExceptionHandler.Handle(new Exception("该薪资类别的管理员还没有建立薪资表!"));
				}
			}
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			if(uniquenessvalid!=null&&!"0".equals(uniquenessvalid)&&!"".equals(uniquenessvalid))
		    	templatebo.setOnlyField(onlyname);
			templatebo.syncGzTableStruct();
			
			 
			String a01z0Flag=templatebo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
			if(a01z0Flag!=null&& "1".equals(a01z0Flag))
				templatebo.createA01Z0ChangeManTable();
			
			templatebo.createDelManTable();
			templatebo.createAddManTable();
			
			if(!"null".equalsIgnoreCase(filterid)&&!"all".equalsIgnoreCase(filterid)&&!"new".equalsIgnoreCase(filterid))
			{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				
				this.frowset=dao.search("select * from gzitem_filter where id='"+filterid+"'");
				if(this.frowset.next())
					fieldstr=Sql_switcher.readMemo(this.frowset, "cfldname");
				/*
				RecordVo vo = new RecordVo("gzitem_filter");
				vo.setString("id", filterid);
				vo = dao.findByPrimaryKey(vo);
				fieldstr=vo.getString("cfldname");*/
			}
			templatebo.createChangeInfoManTable(fieldstr,filterid,gz_module);
			ArrayList changeTabList=new ArrayList();
			
			boolean add_flag=templatebo.getTableIsData("t#"+this.getUserView().getUserName()+"_gz_Ins");
			boolean del_flag=templatebo.getTableIsData("t#"+this.getUserView().getUserName()+"_gz_Dec");
			boolean info_flag=templatebo.getTableIsData("t#"+this.getUserView().getUserName()+"_gz_Bd");
			boolean stop_flag=false;
			if(a01z0Flag!=null&& "1".equals(a01z0Flag))
				stop_flag=templatebo.getTableIsData("t#"+this.getUserView().getUserName()+"_gz_Tf");
			String add="0";
			String del="0";
			String info="0";
			String stop="0";
			if(add_flag)
				add="1";
			if(del_flag)
				del="1";
			if(info_flag)
				info="1";
			if(a01z0Flag!=null&& "1".equals(a01z0Flag)&&stop_flag)
				stop="1";
			RecordVo vo = new RecordVo("salarytemplate");
			vo.setInt("salaryid", Integer.parseInt(salaryid));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo);
			String name=vo.getString("cname");
			this.getFormHM().put("add", add);
			this.getFormHM().put("del", del);
			this.getFormHM().put("info", info);
			this.getFormHM().put("stop", stop);
			this.getFormHM().put("salaryid", salaryid);
			this.getFormHM().put("filterid", filterid);
			this.getFormHM().put("fieldstr", SafeCode.encode(fieldstr));
			this.getFormHM().put("gz_module", gz_module);
			this.getFormHM().put("fromflag", fromflag);
			this.getFormHM().put("cname", SafeCode.encode(name));
			this.getFormHM().put("flow_flag", flowflag);
			this.getFormHM().put("error",error);
			
			 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
