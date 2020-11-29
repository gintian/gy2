/**
 * 
 */
package com.hjsj.hrms.transaction.browse;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * <p>
 * Title:SearchBrowseNuclearTrans
 * </p>
 * <p>
 * Description:按照核二三决策系统条件查询人员
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-12-22
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */

public class SearchBrowseNuclearTrans extends IBusiness {

	public void execute() throws GeneralException {	
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String type = (String) map.get("type");
		String a_code = (String) map.get("a_code");
		this.getFormHM().put("type", type);
		this.getFormHM().put("a_code", a_code);
		
    }
}
