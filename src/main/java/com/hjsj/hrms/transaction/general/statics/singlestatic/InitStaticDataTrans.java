/**
 * 
 */
package com.hjsj.hrms.transaction.general.statics.singlestatic;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:InitMusterDataTrans</p>
 * <p>Description:花名册制作过程中初始化数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-15:10:08:50</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class InitStaticDataTrans extends IBusiness {

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
		/**权限范围内的人员库列表*/
		ArrayList dblist=this.userView.getPrivDbList();		
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dblist=dbvo.getDbNameVoList(dblist);
		ArrayList list=new ArrayList();		 	        
		for(int i=0;i<dblist.size();i++){
			CommonData vo=new CommonData();
			RecordVo dbname=(RecordVo)dblist.get(i);
			vo.setDataName(dbname.getString("dbname"));
			vo.setDataValue(dbname.getString("pre"));
			if(i==0){
				this.getFormHM().put("userbases", dbname.getString("pre"));
				this.getFormHM().put("viewuserbases", dbname.getString("dbname"));
			}
			list.add(vo);
		}
		this.getFormHM().put("dblist",list);
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  throw GeneralExceptionHandler.Handle(ex);
	  }
	}

}
