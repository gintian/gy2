package com.hjsj.hrms.transaction.sys;

import com.hrms.struts.admin.OnlineUserView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * <p>Title: DeleteOnLineTrans</p>
 * <p>Description:注销在线用户</p>
 * <p>Company: hjsj</p>
 * <p>create time 2013-10-28 上午10:34:28</p>
 * 
 * @author yangj
 * @version 1.0
 */
public class DeleteOnLineUsersTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try {
			ArrayList queryOnlinelist = (ArrayList) this.getFormHM().get(
					"selectedlist");
			// ArrayList中没有在线用户时，直接返回
			if (queryOnlinelist == null || queryOnlinelist.size() == 0)
				return;
			// 遍历ArrayList集合
			for (int i = 0; i < queryOnlinelist.size(); i++) {
				// 将ArrayList集合中的数据放入OnlineUserView对象中
				OnlineUserView vo = (OnlineUserView) queryOnlinelist.get(i);
				// 通过得到session注销用户
				vo.getSession().invalidate();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
