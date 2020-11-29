/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course.scorm;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>
 * Title:ScormHandlerTrans
 * </p>
 * <p>
 * Description:处理scorm课件学习状态
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class ScormHandlerTrans extends IBusiness {

	/**
	 * 
	 */
	public ScormHandlerTrans() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		String type = (String) this.getFormHM().get("type");
		if ("get".equalsIgnoreCase(type)) {
			this.getFormHM().put("cmi.core.lesson_status", "not attempted");
		} else if ("set".equalsIgnoreCase(type)) {
			String lesson_status = (String) this.getFormHM().get("cmi.core.lesson_status");
			// 是否需要保存学习进度，0为不需要，1为需要
			String isLearn = (String) this.getFormHM().get("isLearn");
			if ("completed".equalsIgnoreCase(lesson_status) && "1".equals(isLearn)) {
				// 用户人员库
				String nbase  = this.userView.getDbname();
				// 用户编号
				String a0100 = this.userView.getA0100();
				// 课程id
				String scoId = (String) this.getFormHM().get("scoId");
				// 课件id
				String r5100 = (String) this.getFormHM().get("r5100");
				r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
				String r5000 = (String) this.getFormHM().get("r5000");
				r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
				
				StringBuffer sql = new StringBuffer();
				
				
				try {
					ContentDAO dao = new ContentDAO(this.frameconn);
					ArrayList list = new ArrayList();
					list.add(nbase);
					list.add(a0100);
					list.add(r5100);
					list.add(scoId);
					sql.delete(0, sql.length());
					sql.append("update tr_selected_course_scorm set lesson_status=2");
					sql.append(" where nbase=? and a0100=? and r5100=? and scoid=?"); 
					dao.update(sql.toString(), list);
					
					// 查询所有节点
					sql.delete(0, sql.length());
					sql.append("select scoid,parent,lesson_status from tr_selected_course_scorm where nbase=? and a0100=? and r5100=?");
					list = new ArrayList();
					list.add(nbase);
					list.add(a0100);
					list.add(r5100);
					this.frowset = dao.search(sql.toString(),list);
					StringBuffer buff = new StringBuffer();
					while (this.frowset.next()) {
						buff.append(this.frowset.getString("scoid"));
						buff.append("&;&");
						buff.append(this.frowset.getString("parent"));
						buff.append("&;&");
						buff.append(this.frowset.getString("lesson_status"));
						buff.append("&;;&");
					}
					
					String[] rows = buff.toString().split("&;;&");
					String[][] data = new String[rows.length][3];
					for (int i = 0; i < rows.length; i++) {
						String str = rows[i];
						String st[] = str.split("&;&");
						data[i][0] = st[0];
						data[i][1] = st[1];
						data[i][2] = st[2];
					}
					
					// 使用递归查询有哪个父节点需要更新
					String dataStr = getUpdateParent(scoId, data);
					String[] dStr = null;
					if (dataStr.length() > 0) {
						dStr = dataStr.split("&;&");
					} 
					
					// 数据集合
					ArrayList dataList = new ArrayList();
					// sql集合
					ArrayList sqlList = new ArrayList();
					// 更新学习状态sql语句
					// 更新学习状态
					sql.delete(0, sql.length());
					sql.append("update tr_selected_course_scorm set lesson_status=2");
					sql.append(" where nbase=? and a0100=? and r5100=? and scoid=?"); 
					
					
					if (dStr != null) {
						for (int i = 0; i < dStr.length; i++) {
							list = new ArrayList();
							list.add(nbase);
							list.add(a0100);
							list.add(r5100);
							list.add(dStr[i]);
							dataList.add(list);
							sqlList.add(sql.toString());
						}
					}
					
					
					
//					dao.batchUpdate(sql.toString(), dataList);
//					dao.batchUpdate(sqls, values)
					
					// 更新课件学习进度
					sql.delete(0, sql.length());
					sql.append("update tr_selected_course set lprogress=(select (SUM(ed) * 100)/SUM(cout) from (");
					sql.append("select 1 cout, case when lesson_status=2 then 1 else 0 end ed from tr_selected_course_scorm ");
					sql.append("where nbase=? and a0100=?  and r5100=?) mm ),state=(case (select (SUM(ed) * 100)/SUM(cout) from (");
					sql.append("select 1 cout, case when lesson_status=2 then 1 else 0 end ed from tr_selected_course_scorm ");
					sql.append("where nbase=? and a0100=?  and r5100=?) cc ) when 100 then 2 else 1 end) where nbase=? and a0100=? and R5100=?");
					list = new ArrayList();
					list.add(nbase);
					list.add(a0100);
					list.add(r5100);
					list.add(nbase);
					list.add(a0100);
					list.add(r5100);
					list.add(nbase);
					list.add(a0100);
					list.add(r5100);
//					dao.update(sql.toString(), list);
					sqlList.add(sql.toString());
					dataList.add(list);
					// 更新课程学习进度
					
					// 查询课件个数
					sql.delete(0, sql.length());
					sql.append("select count(*) a from r51 where r5000=");
					sql.append(r5000);
				
					int count = 1;
					this.frowset = dao.search(sql.toString());
					if (this.frowset.next()) {
						count = this.frowset.getInt("a");
						count = count == 0 ? 1 : count;
					} 
				
					sql.delete(0, sql.length());
					sql.append("update tr_selected_lesson set lprogress=(select sum(lprogress)/");
					sql.append(count);
					sql.append(" from tr_selected_course where r5100 in (select r5100 from r51 where r5000=");
			
					sql.append(r5000);
					sql.append(")) where r5000=");
					sql.append(r5000);
					sql.append(" and a0100='");
					sql.append(a0100);
					sql.append("' and nbase='");
					sql.append(nbase);
					sql.append("'");
				
				
//					dao.update(sql.toString());
					sqlList.add(sql.toString());
					dataList.add(new ArrayList());
					
					
					// 批量更新
					dao.batchUpdate(sqlList, dataList);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			// 保存学习状态
			this.getFormHM().put("setResult", "OK");
			
		}
		
	}
	
	private String getUpdateParent(String scoId, String[][] str) {
		// 父节点id
		String pScoId = "";
		// 父节点位置
		int pPost = -1;
		
		for (int i = 0; i < str.length; i++) {
			if (scoId.equals(str[i][0])) {
				pScoId = str[i][1];
				pPost = i;
				break;
			}
		}
		
		// 达到根节点时返回空字符窜
		if (pScoId.equals(scoId)) {
			return "";
		} 
		
		// 父节点不需要更新时，返回空字符窜
		boolean flag = true;
		for (int i = 0; i < str.length; i++) {
			if (pScoId.equals(str[i][1]) && !pScoId.equals(str[i][0]) && "2".equals(str[i][2])) {
				flag = flag && true;
			} else if (pScoId.equals(str[i][1]) && !pScoId.equals(str[i][0]) && !"2".equals(str[i][2])) {
				flag = flag && false;
				break;
			}
		}
		
		if (flag) {
			return pScoId + "&;&" + getUpdateParent(pScoId, str);
		} else {
			return "";
		}
		
		
		
		
		
	}

}
