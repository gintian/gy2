package com.hjsj.hrms.module.recruitment.resumecenter.businessobject;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
/**
 * 简历中心导出附件类
 * @Title:        ResumeExportExcelBo.java
 * @Description:  简历中心导出Excle时，导出附件或登记表时添加附件列并设置对应的超链接
 * @Company:      hjsj     
 * @Create time:  2017年12月22日 下午5:33:38
 * @author        chenxg
 * @version       1.0
 */
public class ResumeExportExcelBo extends ExportExcelUtil {
	private Connection conn;
	private String nbase;
	private String field;
	private String attachmentFlag;
	private String registration;

	public ResumeExportExcelBo(Connection con) {
		super(con);
		this.conn = con;
	}
	//覆写导出模板中对应的方法
	@Override
    public void setHyperLink(HSSFWorkbook wb, HSSFRow row, ArrayList<LazyDynaBean> headList, Object data)
			throws GeneralException {
		RowSet rs = null;
		try {
			if (row == null || headList.isEmpty() 
					|| (!"1".equals(attachmentFlag) && !"1".equals(registration)))
				return;

			int idex = 0;
			for (int i = 0; i < headList.size(); i++) {
				LazyDynaBean bean = headList.get(i);
				String itemId = (String) bean.get("itemid");
				if (!"guidkey".equalsIgnoreCase(itemId))
					continue;

				idex = i;
				break;
			}

			String guidKey = "";
			if (data instanceof ArrayList)
				guidKey = (String) ((ArrayList) data).get(0);
			else {
				LazyDynaBean bean = (LazyDynaBean) ((LazyDynaBean) data).get("guidkey");
				guidKey = (String) bean.get("content");
			}
			//设置附件列单元格样式
			HSSFCellStyle hlink_style = wb.createCellStyle();
			HSSFFont hlink_font = wb.createFont();
			hlink_font.setUnderline(HSSFFont.U_SINGLE);
			hlink_font.setColor(HSSFColor.BLUE.index);
			hlink_style.setFont(hlink_font);
			hlink_style.setBorderRight(BorderStyle.THIN);
			hlink_style.setBorderBottom(BorderStyle.THIN);
			hlink_style.setAlignment(HorizontalAlignment.CENTER);
			hlink_style.setVerticalAlignment(VerticalAlignment.CENTER);
			HSSFCell cell = row.getCell(idex);
			HSSFCreationHelper helper = wb.getCreationHelper();
			//附件列单元格默认显示为“无”
			cell.setCellValue("无");

			String sql = "select 1 from zp_attachment where guidkey='" + guidKey + "'";
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			//设置有附件单元格中的超链接
			if (rs.next() || "1".equals(registration)) {
				String path = getOnlyFlag(guidKey);
				String[] paths = path.split("::");
				String filePath = "./attachment/" + paths[0];
				if(StringUtils.isNotEmpty(field) && paths.length > 1)
					filePath = filePath + "-" + paths[1];
					
				cell.setCellValue("附件");
				HSSFHyperlink link = helper.createHyperlink(HyperlinkType.URL);
				link.setAddress(filePath);
				cell.setHyperlink(link);
				cell.setCellStyle(hlink_style);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}

	}

	/**
	 * 获取唯一性指标值
	 * 
	 * @param a0100
	 * @param nbase
	 * @param field
	 * @return
	 */
	private String getOnlyFlag(String guidkey) {
		String value = "";
		String a0101 = "";
		boolean onlyflag = StringUtils.isNotEmpty(field);
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select guidkey,a0101");
			if (onlyflag)
				sql.append("," + field.split(",")[0].substring(field.indexOf(".") + 1));
			
			sql.append(" from " + nbase + "A01");
			sql.append(" where guidkey = '" + guidkey + "'");
			rs = dao.search(sql.toString());
			if (rs.next()) {
				guidkey = rs.getString(1);
				a0101 = rs.getString(2);
				if (onlyflag)
					value = rs.getString(3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return a0101 + "::" + value;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getAttachmentFlag() {
		return attachmentFlag;
	}

	public void setAttachmentFlag(String attachmentFlag) {
		this.attachmentFlag = attachmentFlag;
	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}
	
}
