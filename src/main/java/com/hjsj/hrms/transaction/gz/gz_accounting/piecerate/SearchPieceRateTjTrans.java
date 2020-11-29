package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.gz.piecerate.PieceReportBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * @author tianye
 * @date 2013-3-30
 */
public class SearchPieceRateTjTrans extends IBusiness {
	public void execute()  {
		try{
			HashMap requestMap =(HashMap)this.getFormHM().get("requestPamaHM");
			String tjWhere="";
			String defId= (String)requestMap.get("defId");
			if (defId!=null){			
				requestMap.remove("defId");
				this.getFormHM().put("defId", defId);
				tjWhere="-1";
			}
			else {
				defId = (String)this.getFormHM().get("defId");				
				tjWhere= (String)requestMap.get("tjWhere");
				if (tjWhere!=null){			
					tjWhere=SafeCode.decode(tjWhere);
					tjWhere=PubFunc.keyWord_reback(tjWhere);
					requestMap.remove("tjWhere");
					this.getFormHM().put("tjWhere", tjWhere);
				}
				else {
					tjWhere = (String)this.getFormHM().get("tjWhere");
					/* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 start */
					tjWhere = PubFunc.decrypt(SafeCode.decode(tjWhere)); 
					/* 安全问题 sql-in-url 计件薪资-报表-统计条件 xiaoyun 2014-9-18 end */
				}
			}
			String startDate= (String)requestMap.get("startDate");
			if (startDate!=null){			
				requestMap.remove("startDate");
				this.getFormHM().put("startDate", startDate);
			}
			else {
				startDate = (String)this.getFormHM().get("startDate");
			}
			String endDate = (String)requestMap.get("endDate");
			if (endDate!=null){			
				requestMap.remove("endDate");
				this.getFormHM().put("endDate", endDate);
			}
			else {
				endDate = (String)this.getFormHM().get("endDate");
			}
		

			StringBuffer getAllPieceRateReportSql = new StringBuffer();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sort =  new StringBuffer();
		    getAllPieceRateReportSql.append("select defId,defName,SortId from hr_summarydef order by SortId");//根据序号排序时为了保存排序做准备
			this.frowset=dao.search(getAllPieceRateReportSql.toString());
			ArrayList reportList = new ArrayList();
			String fristDefId = "-1";//用于存储第一条defid，如果数据库中没有记录则为-1
			while(this.frowset.next()){
				CommonData report = new CommonData(this.frowset.getString("defId"),this.frowset.getString("defName"));
				sort.append(","+this.frowset.getString("SortId"));
				reportList.add(report);
			}
			if(reportList.size()!=0){
				CommonData cd = (CommonData)(reportList.get(0));
				fristDefId = cd.getDataValue();
			}
			if ("new".equals(defId)){
			    defId ="";
			}
			if(defId==null||"".equals(defId)||"-1".equals(defId)){//
				defId = fristDefId;
				this.getFormHM().put("pageRows", "19");
			}
			//PieceReportBo bo =new PieceReportBo(this.frameconn,this.userView,Integer.parseInt("-1"));
			try
			{
				PieceReportBo bo =new PieceReportBo(this.frameconn,this.userView,Integer.parseInt(defId));
				if(startDate!=null&&!"".equals(startDate)&&endDate!=null&&!"".equals(endDate)){
					bo.setStartDate(startDate);
					bo.setEndDate(endDate);
	
					if (!"-1".equals(tjWhere)){
						bo.setTempCond(tjWhere);
					}
					else {
						/* 安全问题 sql-in-url 计件薪资-报表 xiaoyun 2014-9-18 start */
						// this.getFormHM().put("tjWhere", bo.getTempCond());
						this.getFormHM().put("tjWhere", SafeCode.encode(PubFunc.encrypt(bo.getTempCond())));
						/* 安全问题 sql-in-url 计件薪资-报表 xiaoyun 2014-9-18 end */
					}
				}
				
				ArrayList list=new ArrayList();
				StringBuffer sql = new StringBuffer();
				sql.append(bo.getSql());
				list = bo.getFieldList();
	
				this.getFormHM().put("fieldlist",list);
				/* 安全问题 sql-in-url 计件薪资-报表 xiaoyun 2014-9-18 start */
				this.userView.getHm().put("gz_sql_1", sql.toString());
				/* 安全问题 sql-in-url 计件薪资-报表 xiaoyun 2014-9-18 end */
				this.getFormHM().put("sql", sql.toString());
			}
			catch (Exception e){
				e.printStackTrace();	
			}
			this.getFormHM().put("tableName", "S01");
			this.getFormHM().put("defId", defId);
			this.getFormHM().put("reportList", reportList);
			this.getFormHM().put("sortId", "".equals(sort.toString())?"":sort.toString().substring(1));
		}catch (SQLException e){
			e.printStackTrace();
			
		}
	}

}
