package com.hjsj.hrms.module.system.hrcloud;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hjsj.hrms.module.system.hrcloud.util.SyncAssessDataLoggerUtil;
import com.hjsj.hrms.module.system.hrcloud.util.SyncCloudDataLoggerUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncInfoTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		Map map = new HashMap();  
		try{
			String transType = (String)this.getFormHM().get("transType");
			if("getOpLogParentCount".equals(transType)){
				//获取一级 日志数量
				ContentDAO dao = new ContentDAO(this.frameconn);
				StringBuffer sql = new StringBuffer();
				sql.append("select count(1) pageCount from t_sys_hrcloud_synclog where logtype in (1,4) ");
				this.frowset = dao.search(sql.toString());
				ArrayList logList = new ArrayList();
				if(this.frowset.next()){
					this.getFormHM().put("pageCount", this.frowset.getInt("pageCount"));
				}
			}else if("showMainLogs".equals(transType)){//显示一级 日志明细
				int page = (Integer)this.getFormHM().get("page");
				int size = (Integer)this.getFormHM().get("size");
				ContentDAO dao = new ContentDAO(this.frameconn);
				StringBuffer sql = new StringBuffer();
				sql.append("select logid,logtype,loginfo,"+Sql_switcher.dateToChar("syncdate","YYYY-MM-DD HH24:MI:SS")+" syncdate,status,errormsg from t_sys_hrcloud_synclog where logtype in (1,4) order by syncdate desc");
				this.frowset = dao.search(sql.toString(),size,page);
				ArrayList logList = new ArrayList();
				while(this.frowset.next()){
					HashMap logHM = new HashMap();
					logHM.put("logid", this.frowset.getString("logid"));
					logHM.put("logtype", this.frowset.getInt("logtype"));
					logHM.put("loginfo", this.frowset.getString("loginfo"));
					String date = this.frowset.getString("syncdate");
					logHM.put("syncdate", date);
					logHM.put("status", this.frowset.getInt("status"));
					logHM.put("errormsg", this.frowset.getString("errormsg"));
					logList.add(logHM);
				}
				this.getFormHM().put("logsData", logList);
			}else if("showChildLogs".equals(transType)){//显示下级日志 明细
				ArrayList childLogsList = null;
				String logid = (String) this.formHM.get("logid");
				childLogsList = getChildLogsList(logid,true);
				this.formHM.put("childLogsData", childLogsList);
			}else if("downLoad".equals(transType)){
				String mainLogid = (String) this.getFormHM().get("mainLogid");
				String fileName = exportSyncChildInfo(mainLogid);
				if(fileName == null){
					this.getFormHM().put("flag", false);
					this.getFormHM().put("message", "导出失败");
				}else{
					this.getFormHM().put("flag", true);
					this.getFormHM().put("fileName", PubFunc.encrypt(fileName));
				}
			}else if("showDataLogs".equals(transType)){
				ArrayList fileList = SyncCloudDataLoggerUtil.getCloudFileList();
				ArrayList returnList = new ArrayList();
				for(Object o:fileList) {
					String filename = (String)o;
					HashMap hash = new HashMap();
					hash.put("filename", filename);
					hash.put("fileid", PubFunc.encrypt(filename));
					returnList.add(hash);
				}
				this.getFormHM().put("flag", true);
				this.getFormHM().put("fileList", returnList);
			}else if("downLoadDataLog".equals(transType)){
				String filename = (String) this.getFormHM().get("filename");
				String filepath = SyncCloudDataLoggerUtil.getNo_name_path();
				if(filepath == null){
					this.getFormHM().put("flag", false);
					this.getFormHM().put("message", "导出失败");
				}else{
					this.getFormHM().put("flag", true);
					this.getFormHM().put("fileName", PubFunc.encrypt(filename));
					this.getFormHM().put("filePath", PubFunc.encrypt(filepath+filename));
				}
			}else if("showAssessDataLogs".equals(transType)){
				ArrayList fileList = SyncAssessDataLoggerUtil.getCloudFileList();
				ArrayList returnList = new ArrayList();
				for(Object o:fileList) {
					String filename = (String)o;
					HashMap hash = new HashMap();
					hash.put("filename", filename);
					hash.put("fileid", PubFunc.encrypt(filename));
					returnList.add(hash);
				}
				this.getFormHM().put("flag", true);
				this.getFormHM().put("fileList", returnList);
			}else if("downAssessLoadDataLog".equals(transType)){
				String filename = (String) this.getFormHM().get("filename");
				String filepath = SyncAssessDataLoggerUtil.getNo_name_path();
				if(filepath == null){
					this.getFormHM().put("flag", false);
					this.getFormHM().put("message", "导出失败");
				}else{
					this.getFormHM().put("flag", true);
					this.getFormHM().put("fileName", PubFunc.encrypt(filename));
					this.getFormHM().put("filePath", PubFunc.encrypt(filepath+filename));
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("flag", false);
		}finally {
		}
	}
	
	private String exportSyncChildInfo(String mainLogid)  throws Exception{
		ArrayList childLogsList = null;
		childLogsList = getChildLogsList(mainLogid,false);
		if(childLogsList == null){
			return null;
		}
		String fileName = "";
		fileName = "sync_child_log_info_"+mainLogid+".xls";
		
		HSSFWorkbook  wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(1 + "");
		sheet.setDefaultColumnWidth(40);
		HSSFCellStyle style = getExpStyle("head", wb);
		HSSFCellStyle rightStyle = getExpStyle("right", wb);
		HSSFCellStyle centerStyle = getExpStyle("center", wb);
		HSSFCellStyle leftStyle = getExpStyle("left", wb);
		
		
		
		//---------------------------------------导出head行--------------------------------------
		int rowIndex = 0;
		HSSFRow row = sheet.getRow(rowIndex);
		if(row==null)
			row = sheet.createRow(rowIndex);
		
		row.setHeight((short)600);
		HSSFCell cell = row.getCell(0);
		if(cell == null)
			cell = row.createCell(0);
		
		
		//设置该单元格样式
		cell.setCellStyle(style);
		//给该单元格赋值
		cell.setCellValue(new HSSFRichTextString("人员/机构名称"));
		
		cell = row.getCell(1);
		if(cell==null)
			cell = row.createCell(1);
		//设置该单元格样式
		cell.setCellStyle(style);
		//给该单元格赋值
		cell.setCellValue(new HSSFRichTextString("人员/机构信息"));
		
		cell = row.getCell(2);
		if(cell==null)
			cell = row.createCell(2);
		//设置该单元格样式
		cell.setCellStyle(style);
		//给该单元格赋值
		cell.setCellValue(new HSSFRichTextString("错误信息"));
//		colIndexMap.put(cloumn, i);
		//---------------------------------------导出head行--------------------------------------end
		
		//导出数据
		rowIndex++;
		for(Object o : childLogsList){
			HashMap map = (HashMap) o;
			String name = map.get("name")==null?"":(String) map.get("name");
			String orgInfo = map.get("orgInfo")==null?"":(String) map.get("orgInfo");
			String errormsg = map.get("errormsg")==null?"":(String) map.get("errormsg");
			
			row = sheet.getRow(rowIndex);
			if(row==null)
				row = sheet.createRow(rowIndex);
			cell = row.getCell(0);
			if(cell==null)
				cell = row.createCell(0);
			//设置该单元格样式
			cell.setCellStyle(leftStyle);
			//给该单元格赋值
			cell.setCellValue(new HSSFRichTextString(name));
			
			cell = row.getCell(1);
			if(cell==null)
				cell = row.createCell(1);
			//设置该单元格样式
			cell.setCellStyle(leftStyle);
			//给该单元格赋值
			cell.setCellValue(new HSSFRichTextString(orgInfo));
			
			cell = row.getCell(2);
			if(cell==null)
				cell = row.createCell(2);
			//设置该单元格样式
			cell.setCellStyle(leftStyle);
			//给该单元格赋值
			cell.setCellValue(new HSSFRichTextString(errormsg));
			rowIndex++;
		}
		
		FileOutputStream fileOut = null;
		try {
			String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
			fileOut = new FileOutputStream(url);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeIoResource(fileOut);
			PubFunc.closeResource(wb);
		}
		return fileName;
	}
	
	/**
	 * 获取单元格样式
	 * @param type
	 * @param wb
	 * @return
	 */
	public HSSFCellStyle getExpStyle(String type,HSSFWorkbook  wb){
		HSSFCellStyle style = wb.createCellStyle();
		// 自动换行
		style.setWrapText(true);
		//设置border
		style.setBorderLeft(BorderStyle.valueOf((short) 1));
		style.setBorderRight(BorderStyle.valueOf((short) 1));
		style.setBorderTop(BorderStyle.valueOf((short) 1));
		style.setBorderBottom(BorderStyle.valueOf((short) 1));
		short borderColor = IndexedColors.BLACK.index;
		style.setLeftBorderColor(borderColor);
		style.setRightBorderColor(borderColor);
		style.setTopBorderColor(borderColor);
		style.setBottomBorderColor(borderColor);
		
		FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
		style.setFillPattern(fillPattern);
		
		short fillForegroundColor = IndexedColors.WHITE.index;
		style.setFillForegroundColor(fillForegroundColor);
		
		//字体
		int fontSize = 10;// 字体大小
		String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) fontSize);
		font.setFontName(fontName);
		//对齐方式
		HorizontalAlignment align = HorizontalAlignment.CENTER;
		if("head".equals(type)){
			font.setBold(true);// 加粗
		}else if("right".equals(type)){
			align = HorizontalAlignment.RIGHT;
			font.setBold(false);// 加粗
		}else if("center".equals(type)){
			align = HorizontalAlignment.CENTER;
			font.setBold(false);// 加粗
		}else if("left".equals(type)){
			align = HorizontalAlignment.LEFT;
			font.setBold(false);// 加粗
		}else if("title".equals(type) || "subhead".equals(type) || "dept".equals(type)) {
			if("title".equals(type)) {
				font.setBold(true);// 加粗
				font.setFontHeightInPoints((short) 16);
				align = HorizontalAlignment.CENTER;
			} else if("dept".equals(type)) {
				font.setBold(false);// 加粗
				align = HorizontalAlignment.LEFT;
			} else if("subhead".equals(type)) {
				font.setBold(false);// 加粗
				align = HorizontalAlignment.CENTER;
			}
			
			style.setBorderLeft(BorderStyle.NONE);
			style.setBorderRight(BorderStyle.NONE);
			style.setBorderTop(BorderStyle.NONE);
			style.setBorderBottom(BorderStyle.NONE);
		} 
		//对齐方式
		style.setAlignment(align);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);
		style.setWrapText(true);
		return style;
	}
	/**
	 * 根据mainLogid 获取子日志数据
	 * @param mainLogid
	 * @return
	 */
	public ArrayList getChildLogsList(String mainLogid,boolean isShow){
		ArrayList childLogsList = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "";
			ArrayList sqlList = new ArrayList();
			sqlList.add(mainLogid);
			
			
			sql += "select logtype,loginfo,errormsg from t_sys_hrcloud_synclog where logtype in (2,3,5) and main_logid=?";
			if(isShow){
				sql = Sql_switcher.sqlTop(sql.toString(), 30);
			}
			this.frowset = dao.search(sql,sqlList);
			while(this.frowset.next()){
				int logtype = this.frowset.getInt("logtype");
				String unique_id = this.frowset.getString("loginfo");
				String errormsg = this.frowset.getString("errormsg");
				Map logsHM = getOrganizationInfo(unique_id);
				logsHM.put("errormsg", errormsg);
				childLogsList.add(logsHM);
			}
			
			if(isShow){
				String countsql = "select count(1) count from t_sys_hrcloud_synclog where logtype in (2,3,5) and main_logid=?";
				this.frowset = dao.search(countsql,sqlList);
				if(this.frowset.next()){
					if(this.frowset.getInt("count") > 30){
						Map logsHM = new HashMap() ;
						logsHM.put("outLimit", "错误日志只显示30条，查看更多请点击下载查看！");
						childLogsList.add(logsHM);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return childLogsList;
	}
	

	/**
	 * 通过机构guidkey 获取当前机构信息    不控制权限
	 * @param guidkey
	 * @return
	 * @throws SQLException
	 */
	private Map getOrganizationInfo(String guidkey){
		StringBuffer sql = new StringBuffer();
		ArrayList sqlList = new ArrayList();
		sql.append("select codesetid,codeitemid,codeitemdesc from organization where guidkey=?");
		sqlList.add(guidkey);
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs =null;
		String name="";
		String orgInfo="";
		String codesetid = "";
		String codeitemid = "";
		try {
			rs= dao.search(sql.toString(),sqlList);
			if(rs.next()){
				//获取机构信息
				codesetid = rs.getString("codesetid");
				codeitemid = rs.getString("codeitemid");
				name = rs.getString("codeitemdesc");
				if("@K".equalsIgnoreCase(codesetid)){
					CodeItem codeItem = AdminCode.getCode(codesetid, codeitemid);
					CodeItem UMcodeItem = AdminCode.getCode("UM", codeItem.getPcodeitem());
					if(UMcodeItem!=null){
						CodeItem UNcodeItem = AdminCode.getCode("UN", AdminCode.getCode("UM", codeItem.getPcodeitem(),5).getPcodeitem());
						orgInfo = UNcodeItem.getCodename() + "/" +UMcodeItem.getCodename();
					}else{
						CodeItem UNcodeItem = AdminCode.getCode("UN", codeItem.getPcodeitem());
						orgInfo = UNcodeItem.getCodename();
					}
				}else if("UM".equalsIgnoreCase(codesetid)){
					CodeItem codeItem = AdminCode.getCode(codesetid, codeitemid,5);
					CodeItem UNcodeItem = AdminCode.getCode("UN", codeItem.getPcodeitem());
					orgInfo = UNcodeItem.getCodename();
				}
				
			}else{
				//获取人员信息
				sql.setLength(0);
				sqlList = new ArrayList();
				HrSyncBo hsb = new HrSyncBo(this.frameconn);
				String dbnamestr = hsb.getTextValue(HrSyncBo.BASE);
			    String[] dbnames = dbnamestr.split(",");
				if(dbnames.length == 0){
					return new HashMap();
				}
				for(String nbase:dbnames){
					if( sql.length() >0 ){
						sql.append(" union ");
					}
					sql.append("select a0101,B0110,E0122,E01A1 from "+nbase+"A01 where guidkey=?");
					sqlList.add(guidkey);
				}
				rs= dao.search(sql.toString(), sqlList);
				if(rs.next()){
					name = rs.getString("a0101");
					String B0110 = "";
					String E0122 = "";
					String E01A1 = "";
					orgInfo = "";
					CodeItem item = null;
					if(rs.getString("B0110") != null){
						item = AdminCode.getCode("UN", rs.getString("B0110"));
						if(item != null && item.getCodename() != null){
							orgInfo += item.getCodename()+"/";
						}
					}
					if(rs.getString("E0122") != null){
						item = AdminCode.getCode("UM", rs.getString("E0122"));
						if(item != null && item.getCodename() != null){
							orgInfo += item.getCodename()+"/";
						}
					}
					if(!StringUtils.isEmpty(rs.getString("E01A1"))){
						AdminCode.getCode("@K", rs.getString("E01A1"));
						if(item != null && item.getCodename() != null){
							orgInfo += item.getCodename()+"/";
						}
					}
					if(orgInfo.length()>0){
						orgInfo = orgInfo.substring(0, orgInfo.length()-1);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		HashMap map = new HashMap();
		map.put("name",name);
		map.put("orgInfo",orgInfo );
		return map;
	}
	/**
	 * 获取人员日志信息  不控制权限
	 * @param guidkey
	 * @return
	 * @throws SQLException
	 */
	private Map getA0100Info(String guidkey){
		StringBuffer sql = new StringBuffer();
		ArrayList sqlList = new ArrayList();
		sql.append("select nbase_0 from t_hr_view where unique_id=? ");
		sqlList.add(guidkey);
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		String nbase = "";
		Map map = new HashMap();
		try {
			rs = dao.search(sql.toString(), sqlList);
			if(rs.next()){
				nbase = rs.getString("nbase_0");
			}
			if(nbase == null || nbase.trim().length() == 0)
				return new HashMap();
			sql.setLength(0);
			sql.append("select a0101,B0110,E0122,E01A1 from "+nbase+"A01 where guidkey=?");
			rs= dao.search(sql.toString(), sqlList);
			if(rs.next()){
				map.put("name", rs.getString("a0101"));
				String B0110 = "";
				String E0122 = "";
				String E01A1 = "";
				String orgInfo = "";
				CodeItem item = null;
				if(rs.getString("B0110") != null){
					item = AdminCode.getCode("UN", rs.getString("B0110"));
					if(item != null && item.getCodename() != null){
						orgInfo += item.getCodename()+"/";
					}
				}
				if(rs.getString("E0122") != null){
					item = AdminCode.getCode("UM", rs.getString("E0122"));
					if(item != null && item.getCodename() != null){
						orgInfo += item.getCodename()+"/";
					}
				}
				if(!StringUtils.isEmpty(rs.getString("E01A1"))){
					AdminCode.getCode("@K", rs.getString("E01A1"));
					if(item != null && item.getCodename() != null){
						orgInfo += item.getCodename()+"/";
					}
				}
				if(orgInfo.length()>0){
					orgInfo = orgInfo.substring(0, orgInfo.length()-1);
				}
				map.put("orgInfo",orgInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		
		return map;
	}
}
