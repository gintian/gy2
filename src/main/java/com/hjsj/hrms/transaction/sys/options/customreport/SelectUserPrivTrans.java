package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.businessobject.board.BoardBo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;


/**
 * <p>
 * Title:AuthorizationTrans
 * </p>
 * <p>
 * Description:自定制报表---查询对某条记录有权限的业务用户
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-06-17
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class SelectUserPrivTrans extends IBusiness {

	public void execute() throws GeneralException {
		//选择的记录数
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String num = (String) map.get("num");
		String tabid = (String) map.get("tabid");
		BoardBo boardBo = new BoardBo(this.getFrameconn(),this.userView);
		String privusers = "";
		if (num != null && "1".equalsIgnoreCase(num)) {
			privusers = boardBo.getPriUser(tabid, IResourceConstant.CUSTOM_REPORT, "0");
		}
		this.getFormHM().put("privusers", privusers);
		this.getFormHM().put("num", num);
		
	}

}
