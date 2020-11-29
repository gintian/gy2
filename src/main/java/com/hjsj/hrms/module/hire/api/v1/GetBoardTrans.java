package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.module.hire.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GetBoardTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			// 取公告列表时，hire_channel传入公告所属渠道，取公式时，hire_channel传入publicity
			String hireChannel = (String) this.getFormHM().get("hire_channel");
			String type = (String) this.getFormHM().get("type");
			PositionBo posBo = new PositionBo(this.frameconn);
			//获取公告列表
			if("board_list".equalsIgnoreCase(type)) {
				int pageNum = (Integer) this.getFormHM().get("pageNum");
				int pageSize = (Integer) this.getFormHM().get("pageSize");
				ArrayList boardlist = posBo.getBoardlist(hireChannel);
				int pageTotal = boardlist.size();
				pageNum = (pageNum-1)*pageSize>pageTotal? 1 : pageNum;
				int startIndex = (pageNum-1)*pageSize;
				int endIndex = pageNum*pageSize;
				HashMap<String, Object> return_data = new HashMap();
				return_data.put("list", boardlist.subList(startIndex, endIndex>pageTotal?pageTotal:endIndex));
				return_data.put("pageTotal", pageTotal);
				this.getFormHM().put("return_data", return_data);
			}else if("notice_list".equalsIgnoreCase(type)) {//获取公告信息
				String id = (String) this.getFormHM().get("id");
				id = PubFunc.decrypt(id);
				HashMap<String, String> boardInfo = posBo.getBoardInfo(id);
				this.getFormHM().put("return_data", boardInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
