package com.hjsj.hrms.transaction.stat;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 归档显示信息集设置
 * @author xujian
 *Mar 22, 2010
 */
public class SubInfoSetupArchiveTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList tempCondList = (ArrayList) this.getFormHM().get("selectList");
		StringBuffer html = new StringBuffer();
		html.append("<select id='condition' name='condition' multiple='multiple' style='height:150px;width:200px;'>");
		
		if (tempCondList != null) {
			for (int i = 0; i < tempCondList.size(); i++) {
				RecordVo vo = (RecordVo) tempCondList.get(i);
				html.append("<option value='");
				html.append(vo.getString("id"));
				html.append("' title='");
				html.append(vo.getString("name"));
				html.append("'>");
				html.append(vo.getString("name"));
				html.append("</option>");
			}
			
		}
		html.append("</select>");
	
		this.getFormHM().put("html", html.toString());
		this.getFormHM().put("tempCondList", tempCondList);
		this.getFormHM().put("flag", "1");
		
	}

}
