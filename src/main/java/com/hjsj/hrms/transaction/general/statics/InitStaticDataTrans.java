/**
 * 
 */
package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:InitStaticDataTrans</p>
 * <p>Description:初始化数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-2-15:11:29:05</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class InitStaticDataTrans extends IBusiness {
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		/**未定义信息类别,默认为人员信息*/
		String infor_kind=(String)hm.get("a_inforkind");
		if(infor_kind==null|| "".equals(infor_kind))
			infor_kind="1";
		this.getFormHM().put("infor_Flag",infor_kind);
		
        ArrayList dblist=userView.getPrivDbList();
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dblist=dbvo.getDbNameVoList(dblist);
		ArrayList lists=new ArrayList();
		for(int i=0;i<dblist.size();i++)
		{
			
			CommonData vo=new CommonData();
			RecordVo dbname=(RecordVo)dblist.get(i);
			vo.setDataName(dbname.getString("dbname"));
			vo.setDataValue(dbname.getString("pre"));
			if(i==0){
				this.getFormHM().put("userbases", dbname.getString("pre"));
				this.getFormHM().put("init_userbases", dbname.getString("pre"));
				this.getFormHM().put("viewuserbases", dbname.getString("dbname"));
				this.getFormHM().put("init_viewuserbases", dbname.getString("dbname"));
			}
			lists.add(vo);
		}
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);
		this.getFormHM().put("dblist",lists);	
		this.getFormHM().put("vtotal", "0");
		this.getFormHM().put("htotal", "0");
		ArrayList selectdlist = (ArrayList) this.getFormHM().get("selectedlist");
		if(selectdlist == null)
			this.getFormHM().put("selectedlist", new ArrayList());
			
	}

}
