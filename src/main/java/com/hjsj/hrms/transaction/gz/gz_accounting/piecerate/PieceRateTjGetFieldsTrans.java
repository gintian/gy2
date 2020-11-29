package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceReportBo;
import com.hjsj.hrms.businessobject.gz.piecerate.PieceReportDefBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class PieceRateTjGetFieldsTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
			HashMap hm = this.getFormHM();
			ContentDAO dao = new ContentDAO(this.getFrameconn());		
				
			String flag = (String)hm.get("flag");
			flag=(flag==null)?"":flag;
			PieceReportDefBo defBo =new PieceReportDefBo(this.frameconn);
			String setId = "";
			setId = (String) hm.get("setId");
			if ("getLeftFieldlist".equals(flag)) {// 返回指标列表
				hm.put("leftFieldList", defBo.getLeftFieldList(setId));
			} 
			else if ("getFieldItemList".equals(flag)) {// 返回指标列表
				hm.put("cond_itemList", defBo.getCondFieldList(setId,true));
			} 
			else if ("checkTjWhere".equals(flag)) {// 校验统计条件
				String formulacontent = (String)hm.get("cond");
				formulacontent=SafeCode.decode(formulacontent);
				formulacontent=PubFunc.keyWord_reback(formulacontent);	
				String info="true";
				String strError="";
				if (!"".equals(formulacontent.trim())){
					if (defBo.CheckTjWhere(this.userView, formulacontent)){
						info ="true";					
					}
					else{
						info ="false";
						strError =defBo.getLastError();
					}
					
				}

				/* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 start */
				this.getFormHM().put("tjWhere", SafeCode.encode(PubFunc.encrypt(formulacontent)));
			//	this.getFormHM().put("tjWhere", SafeCode.encode(formulacontent));
				/* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 end */
				this.getFormHM().put("info",info);
				this.getFormHM().put("error",SafeCode.encode(strError));
			} 
			else if ("expReport".equals(flag)){//批量导出
				String defId = (String)hm.get("defId");
				/* 安全问题 sql-in-url 计件薪资-报表  xiaoyun 2014-9-18 start */
				//String sql = (String)hm.get("sql");
				//sql=SafeCode.decode(sql);
				//sql=PubFunc.keyWord_reback(sql);
				String sql = (String)this.userView.getHm().get("gz_sql_1");
				/* 安全问题 sql-in-url 计件薪资-报表  xiaoyun 2014-9-18 end */
				if ((defId==null)||("".equals(defId))) return;
				PieceReportBo bo =new PieceReportBo(this.frameconn,this.userView,Integer.parseInt(defId));
				String fileName = bo.expReport(sql);
				/* 安全问题 文件下载 计件薪资-报表导出 xiaoyun 2014-9-13 start */
				//this.getFormHM().put("fileName", fileName);
				this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
				/* 安全问题 文件下载 计件薪资-报表导出 xiaoyun 2014-9-13 end */
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			}

	}

}
