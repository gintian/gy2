package com.hjsj.hrms.transaction.general.inform.emp.e_archive;

import com.hjsj.hrms.businessobject.general.e_archive.E_ArchiveXMLParamBo;
import com.hjsj.hrms.businessobject.general.inform.e_archive.E_ArchiveBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitE_ArchiveTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			/**登录用户的应用库*/
			String pre=(String)map.get("userbase");
			E_ArchiveXMLParamBo xmlbo=new E_ArchiveXMLParamBo(this.getFrameconn());
			E_ArchiveBo bo = new E_ArchiveBo(this.getFrameconn());
			/**人员基本情况子集是否构建档案号指标*/
			boolean anflag=bo.a01HasArchiveNoField();
			if(!anflag)
			{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("columns.archive.noanfield")));
			}
			//System.out.println(this.userView.getS_userName());
			/**得到档案目录子集*/
			String setid=xmlbo.getPropertyValue(E_ArchiveXMLParamBo.CATALOGSET, "setid");
			if(setid==null|| "".equals(setid))
			{
				throw GeneralExceptionHandler.Handle(new Exception("未设置目录子集"));
			}
		    /**文件名指标*/
			String filenameitemid=xmlbo.getPropertyValue(E_ArchiveXMLParamBo.FTP, "docfilename");
			if(filenameitemid==null|| "".equals(filenameitemid))
			{
				throw GeneralExceptionHandler.Handle(new Exception("未设置文件名指标"));
			}
			ArrayList list = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
			if(list==null||list.size()==0)
			{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("columns.archive.nofield")));
			}
			String tableName=pre+setid;
			boolean lflag=bo.hasArchiveField(list,"类号");
			boolean cflag=bo.hasArchiveField(list,"材料名称");
			boolean yflag=bo.hasArchiveField(list,"年");
			boolean mflag=bo.hasArchiveField(list,"月");
			boolean dflag=bo.hasArchiveField(list,"日");
			boolean sflag=bo.hasArchiveField(list,"份数");
			boolean pflag=bo.hasArchiveField(list,"页数");
			if(!(lflag&&cflag&&yflag&&mflag&&dflag&&sflag&&pflag))
			{
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("columns.archive.nofield")));
			}
			String litemid=bo.getArchiveItemid(list,"类号");
			String citemid=bo.getArchiveItemid(list,"材料名称");
			String yitemid=bo.getArchiveItemid(list,"年");
			String mitemid=bo.getArchiveItemid(list,"月");
			String ditemid=bo.getArchiveItemid(list,"日");
			String sitemid=bo.getArchiveItemid(list,"份数");
			String pitemid=bo.getArchiveItemid(list,"页数");
			String fitemid=filenameitemid;
			String a0100=(String)map.get("a0100");
			ArrayList archiveList = bo.getArchiveList(litemid,citemid,yitemid,mitemid,ditemid,sitemid,pitemid,filenameitemid,tableName,a0100,this.userView);
			HashMap hm=bo.getUNAndUMAndName(pre, a0100);
			this.getFormHM().put("b0110",(String)hm.get("b0110"));
			this.getFormHM().put("e0122",(String)hm.get("e0122"));
			this.getFormHM().put("a0101",(String)hm.get("a0101"));
			this.getFormHM().put("archiveList", archiveList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
