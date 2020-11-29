/**
 * FileName: DeclareInforTrans
 * Author:   hssoft
 * Date:     2018/12/6 11:43
 * Description: 查询申报详情交易类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.zxdeclare.transaction;

import com.hjsj.hrms.module.gz.zxdeclare.businessobject.IDeclareService;
import com.hjsj.hrms.module.gz.zxdeclare.businessobject.impl.DeclareServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈类功能描述〉<br>
 * 〈查询申报详情交易类〉
 *
 * @author hssoft
 * @create 2018/12/6
 * @since 1.0.0
 */
public class DeclareInforTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
    	HashMap returnMap =  new HashMap();
    	String id = (String) this.formHM.get("id");
		IDeclareService iDeclareService = new DeclareServiceImpl(this.frameconn, this.userView);
    	Map inforHM = null;
		try {
			inforHM = iDeclareService.getDeclareInfor(id);
		} catch (GeneralException e) {
//			e.printStackTrace();
			returnMap.put("return_code", "fail");
			returnMap.put("return_msg", e.getErrorDescription());
			return;
		}
    	returnMap.put("return_code", "success");
    	returnMap.put("return_data", inforHM);
    	this.formHM.put("returnStr", returnMap);
    }
}
