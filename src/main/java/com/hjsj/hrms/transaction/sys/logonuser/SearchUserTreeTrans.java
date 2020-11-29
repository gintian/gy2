/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SearchUserTreeTrans</p>
 * <p> Description:</p>
 * <p>Company:hjsj</p>
 * <p> create time:2006-6-6:14:41:30</p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchUserTreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		String groupid = this.userView.getGroupId();
		if (groupid == null||groupid.length()<=0) {
			throw new GeneralException(ResourceFactory.getProperty("sys.user.admin.err"));
		} else {
			TreeItemView treeItem = new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/group.gif");
			treeItem.setTarget("il_body");
			String rootdesc = ResourceFactory.getProperty("label.user.group");
			treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc);
			treeItem.setLoadChieldAction("/system/logonuser/search_user_servlet?level0=0&groupid="
							+ groupid);
			treeItem.setAction("javascript:void(0)");
			try {
				this.getFormHM().put("usertree", treeItem.toJS());
			} catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}
		}
	}

}
