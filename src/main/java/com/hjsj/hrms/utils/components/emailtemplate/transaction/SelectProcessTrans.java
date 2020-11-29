package com.hjsj.hrms.utils.components.emailtemplate.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SelectProcessTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		ContentDAO dao = null;
		RowSet rs = null;
		ArrayList<CommonData> list = new ArrayList<CommonData>();
		
		try {
			//HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String opt = (String)this.getFormHM().get("opt");//判断是什么模块进入的，暂时9：绩效
			
			dao = new ContentDAO(this.getFrameconn());
			StringBuffer sql = new StringBuffer("select codeitemid,codeitemdesc from codeitem where codesetid = '36' and codeitemid=parentid and invalid='1' order by a0000,codeitemid");
			rs = dao.search(sql.toString());
			while(rs.next()){
				if("04".equals(rs.getString("codeitemid"))||"09".equals(rs.getString("codeitemid")))
					continue;
				CommonData com = new CommonData(rs.getString("codeitemid"), rs.getString("codeitemdesc"));
				list.add(com);
			}
			ArrayList parmList = new ArrayList();
			if("7".equals(opt)) {//招聘模块
				CommonData com = new CommonData("11","拒绝职位申请通知");
				parmList.add(com);
				com = new CommonData("80","简历评价通知（评价人）");
				parmList.add(com);
				com = new CommonData("82","简历中心通知");
				parmList.add(com);
				com = new CommonData("10","接受职位申请通知");
				parmList.add(com);
				com = new CommonData("20","面试安排通知（申请人）");
				parmList.add(com);
				com = new CommonData("40","面试通知（通过）");
				parmList.add(com);
				com = new CommonData("50","面试通知（淘汰）");
				parmList.add(com);
				com = new CommonData("60","Offer");
				parmList.add(com);
				com = new CommonData("70","入职通知（管理人员）");
				parmList.add(com);
				com = new CommonData("81","转发简历通知");
				parmList.add(com);
				com = new CommonData("92","招聘批次通知");
				parmList.add(com);
				com = new CommonData("91","职位推荐");
				parmList.add(com);
				com = new CommonData("90","其它通知");
				parmList.add(com);
			}
			this.formHM.put("opt", opt);
			this.formHM.put("typeList", parmList);//模版类别通过这里控制
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.formHM.put("processlist", list);
			PubFunc.closeResource(rs);
		}
	}

}
