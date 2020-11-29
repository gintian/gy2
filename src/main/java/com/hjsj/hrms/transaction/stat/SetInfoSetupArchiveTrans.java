package com.hjsj.hrms.transaction.stat;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 归档显示信息集设置
 * @author xujian
 *Mar 22, 2010
 */
public class SetInfoSetupArchiveTrans extends IBusiness {

	public void execute() throws GeneralException {

		// 常用条件列表
		ArrayList condList = new ArrayList();
		String condSql = "select id,name,type from lexpr where type='1' order by norder";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frecset = dao.search(condSql);
			while (frecset.next()) {
				if(this.userView.isHaveResource(IResourceConstant.LEXPR,frecset.getString("id"))) {
					RecordVo vo = new RecordVo("lexpr");
					vo.setString("name", frecset.getString("name"));
					vo.setString("id", frecset.getString("id"));					
					condList.add(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.getFormHM().put("condList", condList);
		
	}

}
