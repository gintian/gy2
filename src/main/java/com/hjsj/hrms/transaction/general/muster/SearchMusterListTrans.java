/**
 * 
 */
package com.hjsj.hrms.transaction.general.muster;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchMusterListTrans</p>
 * <p>Description:常用花名册列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-26:12:42:25</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchMusterListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		  try
		  {
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			/**未定义信息类别,默认为人员信息*/
			String infor_kind=(String)hm.get("a_inforkind");
			if(infor_kind==null|| "".equals(infor_kind))
				infor_kind="1";
			this.getFormHM().put("inforkind",infor_kind);
			
			String checkflag=(String)hm.get("checkflag");
			checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"0";
			hm.remove("checkflag");
			
			String result=(String)hm.get("result");
			result=result!=null?result:"0";
			hm.remove("result");
			this.getFormHM().put("result",result);
			/**权限范围内的人员库列表*/
			ArrayList dblist=this.userView.getPrivDbList();
			DbNameBo dbvo=new DbNameBo(this.getFrameconn());
			dblist=dbvo.getDbNameVoList(dblist);
			ArrayList list=new ArrayList();
			for(int i=0;i<dblist.size();i++)
			{
				CommonData vo=new CommonData();
				RecordVo dbname=(RecordVo)dblist.get(i);
				vo.setDataName(dbname.getString("dbname"));
				vo.setDataValue(dbname.getString("pre"));
				list.add(vo);
			}
			this.getFormHM().put("dblist",list);
			/**花名册列表*/
			MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
			ArrayList musterlist=musterbo.getMusterList(infor_kind);
			ArrayList list_temp=new ArrayList();
			for(int i=0;i<musterlist.size();i++)
			{
				RecordVo vo=(RecordVo)musterlist.get(i);
				if(!(this.userView.isHaveResource(IResourceConstant.MUSTER,vo.getString("tabid"))))
					continue;
				list_temp.add(vo);
			}
			this.getFormHM().put("musterlist",list_temp);
			this.getFormHM().put("history","0");
			this.getFormHM().put("checkflag",checkflag);
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  throw GeneralExceptionHandler.Handle(ex);
		  }


	}

}
