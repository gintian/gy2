package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * 处理招聘外网公告信息分页
 * @author wangjl
 *
 */
public class SearchBoardListTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn());
		String hireChannel  = (String) this.getFormHM().get("hireChannel");
		ArrayList<LazyDynaBean> boardlist = new ArrayList<LazyDynaBean>();
		//homepage招聘首页
		if("homepage".equals(hireChannel)) {
			/*获取招聘公告标题 String opt 接口 1：ehr系统公告 2 ：招聘公告 String hireChannel 公告类型 1.ehr首页公告 2 招聘首页公告
			(String opt, String hireChannel, String other_flag)*/
			boardlist = bo.SQLExecute("2", "2", "");
		}else {
			//招聘渠道
            hireChannel = PubFunc.hireKeyWord_filter(hireChannel);
            hireChannel=PubFunc.getReplaceStr(hireChannel);
            if(StringUtils.isEmpty(hireChannel)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
				hireChannel = bo.getHireChannelFromTable();
            
            boardlist = bo.SQLExecute("2", "", hireChannel);
		}
		bo.getPageBoardList(boardlist, this.getFormHM());
	}

}
