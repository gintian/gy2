package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class ReLoadValue extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		  {	
			String tabid=(String)this.getFormHM().get("tabid");
			if(tabid==null)
			{
				throw new GeneralException(ResourceFactory.getProperty("error.muster.notselect"));
			}
			MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
			boolean isExistField = Boolean.parseBoolean((String)this.getFormHM().get("isExistField"));
			musterbo.setExistField(isExistField);
			String wherestr=(String)this.getFormHM().get("wherestr");
			if(StringUtils.isNotEmpty(wherestr)){
				wherestr = SafeCode.decode(wherestr);
				musterbo.setWherestr(wherestr);
			}
			String wheresql=(String)this.getFormHM().get("wheresql");
			if(StringUtils.isNotEmpty(wheresql)){
				wheresql = SafeCode.decode(wheresql);
				musterbo.setWheresql(wheresql);
			}
			/**未定义信息类别,默认为人员信息*/
			String infor_kind=(String)this.getFormHM().get("inforkind");
			if(infor_kind==null|| "".equals(infor_kind))
				infor_kind="1";
			/**用查询结果,重新生成花名册数据*/
			String dbpre=(String)this.getFormHM().get("dbpre");
			if(dbpre==null)
				dbpre="";	
			String a_code=(String)this.getFormHM().get("a_code");
			a_code=a_code!=null?a_code:"";
			if(a_code.trim().length()>1){
				a_code=a_code.substring(2);
			}
			
			
			String returncheck=(String)this.getFormHM().get("returncheck");
			returncheck=returncheck!=null&&returncheck.trim().length()>0?returncheck:"0";
			this.getFormHM().put("returncheck",returncheck);
			
			/** 是否包含历史记录*/
			String history="0";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select expr from lbase where tabid="+tabid);
			if(this.frowset.next())
			{
				String expr=Sql_switcher.readMemo(this.frowset,"expr");
				if(expr!=null&& "1".equals(expr.trim()))
					history="1";
			}
			String username = this.userView.getUserName().trim().replaceAll(" ", "");
			if(!"1".equals(infor_kind))
				history="0";  //目前部门、职位花名册没有提供历史记录功能,所以默认置为 0  dengcan 2008/02/03
			ArrayList dblist=musterbo.getUserAllDBList();
			if("1".equals(returncheck)){
			    if("1".equals(infor_kind)&&"ALL".equals(dbpre)&&MusterBo.isHkyh())
			        musterbo.createAllDBTempTable(infor_kind,dblist,tabid,username,history,a_code);
			    else
			        musterbo.createMusterTempTable(infor_kind,dbpre,tabid,username,history,a_code);
//				if(musterbo.createMusterTempTable(infor_kind,dbpre,tabid,username,history,a_code))
//					musterbo.getTableName(infor_kind,dbpre,tabid,username);
			}else{
			    if("1".equals(infor_kind)&&"ALL".equals(dbpre)&&MusterBo.isHkyh())
			        musterbo.createAllDBTempTable(infor_kind,dblist,tabid,username,history);
			    else
			        musterbo.createMusterTempTable(infor_kind,dbpre,tabid,username,history);
//				if(musterbo.createMusterTempTable(infor_kind,dbpre,tabid,username,history))
//					musterbo.getTableName(infor_kind,dbpre,tabid,username);
			}
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }		
	}

}
