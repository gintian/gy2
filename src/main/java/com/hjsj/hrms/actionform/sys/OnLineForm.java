package com.hjsj.hrms.actionform.sys;

import com.hjsj.hrms.businessobject.lawbase.LawbaseExcel;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.admin.OnlineUserView;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.commons.collections.FastHashMap;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title:OnLineForm</p>
 * <p>Description:在线用户</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 7, 2005:4:43:41 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
// 修改：yangj，时间：2013-10-28
public class OnLineForm extends FrameForm {

	/** 在线用户 */
	private PaginationForm userlistform = new PaginationForm();
	// 导出文件名
	private String filename;
	private EncryptLockClient lockclient;

	/**
     * 
     */
	public OnLineForm() {
		super();
	}

	/** 取得在线用户列表 */
	private ArrayList getOnlineUsers() {
		ArrayList list = new ArrayList();
		// 从系统中取出在线的用户
		FastHashMap hm = (FastHashMap) getServlet().getServletContext()
				.getAttribute("userNames");
		if (hm == null)
			return null;
		Iterator iter = hm.values().iterator();
		HashMap map = new HashMap();
		// 遍历集合，将在线用户放入到list中
		while (iter.hasNext()) {
			OnlineUserView vo = (OnlineUserView) iter.next();
			if (vo.getUsername() != null && vo.getUsername().length() > 0) {
				// System.out.println(vo.getUsername());
				//if (map.get(vo.getUsername()) == null) {
					list.add(vo);
					map.put(vo.getUsername(), "1");
				//}
			}

		}
		map.clear();
		showExcel(list);
		return list;
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#outPutFormHM()
	 */
	@Override
    public void outPutFormHM() {
		// 输出到页面
		this.getUserlistform().setList(getOnlineUsers());
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		// 获得页面选中的在线用户
		this.getFormHM().put("selectedlist",
				this.getUserlistform().getSelectedList());
	}

	public PaginationForm getUserlistform() {
		return userlistform;
	}

	public void setUserlistform(PaginationForm userlistform) {
		this.userlistform = userlistform;
	}

	public void showExcel(ArrayList list) {
		ArrayList column = new ArrayList();
		ArrayList columnlist = new ArrayList();
		column.add(ResourceFactory.getProperty("column.sys.org"));
		columnlist.add("orgname");
		column.add(ResourceFactory.getProperty("column.sys.dept"));
		columnlist.add("dept");
		column.add(ResourceFactory.getProperty("column.sys.pos"));
		columnlist.add("pos");
		column.add(ResourceFactory.getProperty("column.sys.name"));
		columnlist.add("username");
		column.add(ResourceFactory.getProperty("column.sys.ipaddr"));
		columnlist.add("ip_addr");
		column.add(ResourceFactory.getProperty("column.sys.logindate"));
		columnlist.add("login_date");
		// 生成要导出的excel表
		LawbaseExcel exc = new LawbaseExcel();
		// 生成的excel表名称为 用户名_随机数.xls jingq   add   2014.5.7
		String uname = userView.getUserName();
		String excelfile = exc.creatExcel(column, list, lockclient,uname);
//		String excelfile = exc.creatExcel(column, list, lockclient);
		this.setFilename(excelfile);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		lockclient = (EncryptLockClient) request.getSession()
				.getServletContext().getAttribute("lock");
	}

}
