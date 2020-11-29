package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.businessobject.board.BoardBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Map;


/**
 * <p>
 * Title:AuthorizationTrans
 * </p>
 * <p>
 * Description:自定制报表信息授权
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-8
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class AuthorizationTrans extends IBusiness {

	public void execute() throws GeneralException {
		//获得选择的记录
		ArrayList selectList = (ArrayList) this.getFormHM().get("selectList");
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String selectPerson = (String) map.get("privuser");
		map.remove("privuser");
		selectPerson = SafeCode.decode(selectPerson);
		//选择了几条记录，如果为1,则先把有权限人删除
		String num = (String) map.get("num");
		map.remove("num");
		// 记录id
		String tabid = "";
		StringBuffer priv = new StringBuffer();
		if (selectList != null && selectList.size() >0) {
			// 获取所有表id
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			for (int i = 0; i < selectList.size(); i++) {
				RecordVo vo = (RecordVo) selectList.get(i);
				int id = vo.getInt("id");
				tabid = id + "";
				if (i == 0) {
					priv.append(id + "");
				} else {
					priv.append("," + id);
				}
			}
		}
		
		// 需要授权的用户
		StringBuffer selectBuff = new StringBuffer();
		if (selectPerson != null && selectPerson.length() > 0) {
			String []selectPer = selectPerson.split(",");
			for (int i = 0; i < selectPer.length; i++) {
				if (selectPer[i] != null && selectPer[i].trim().length() > 0) {
					selectBuff.append(",0:");
					selectBuff.append(selectPer[i]);

				}
			}
		}
		
		
		BoardBo boardBo = new BoardBo(this.getFrameconn(),this.userView);		
		if (selectBuff.length() > 0) {			
			// 删除授权
			if (num != null && "1".equalsIgnoreCase(num)) {
				boardBo.deleteUserPriv(IResourceConstant.CUSTOM_REPORT, tabid, "0");
			}
			//授权
			boardBo.savePriv2(selectBuff.toString(), priv.toString());
		} else {
			// 删除授权
			if (num != null && "1".equalsIgnoreCase(num)) {
				boardBo.deleteUserPriv(IResourceConstant.CUSTOM_REPORT, tabid, "0");
			}
		}
		
	}

}
