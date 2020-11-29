package com.hjsj.hrms.module.system.personalsoduku.boxreport.transaction;

import com.hjsj.hrms.module.system.personalsoduku.boxreport.businessobject.BoxReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 获取横纵指标以及常用统计条件
* <p>Title:GetCodeIndexTrans </p>
* <p>Description: </p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 10, 2015 6:09:54 PM
 */
public class GetCodeIndexTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		
		String datasource = (String)this.getFormHM().get("datasource");
		String flag = (String)this.getFormHM().get("flag");
		BoxReportBo bo = new BoxReportBo(this.frameconn,this.userView);
		RowSet rst = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			if("0".equals(flag)){//获取横纵指标
				ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
				list = bo.gethzindex(datasource);
				this.getFormHM().put("codeindex", list);
			}
			if("1".equals(flag)){//获取常用统计条件
				String sql = "select a.id,a.name from sname a,SLegend b where a.id=b.id and a.infokind='1' group by a.Id,a.name";
				rst = dao.search(sql);
				ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
				while(rst.next()){
					HashMap<String,String> map = new HashMap<String,String>();
					String id = rst.getString("id");
					String name = rst.getString("name");
					map.put("id", id);
					map.put("name", name);
					list.add(map);
				}
				this.getFormHM().put("condition", list);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			 PubFunc.closeDbObj(rst);
		}
	}
}
