package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.report.user_defined_reoprt.UserdefinedReport;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.net.URLDecoder;
import java.util.HashMap;

public class PieceRatePrintViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		// 是否加有权限
		String isprivstr = (String) this.getFormHM().get("ispriv");
		// 自定义表格id
		String report_id = (String) this.getFormHM().get("id");
		// url
		String url = (String) this.getFormHM().get("realurl");
		if (url != null) {
			url = URLDecoder.decode(url);
		}
		boolean ispriv = false;
		if (isprivstr != null && "1".equals(isprivstr))
			ispriv = true;

		// 填充页面内容
		String context = "";
		String str[] = new String[3];
		// 文件扩展名
		String ext = "";
		UserdefinedReport userdefinedReport = null;
		try {
			userdefinedReport = new UserdefinedReport(userView, this.frameconn,
					report_id, ispriv);
			// 获得所有下拉列表的名称
			String hiddenValueName = (String) this.getFormHM().get(
					"hiddenValueName");
			if (hiddenValueName != null && hiddenValueName.length() > 0) {
				String[] names = hiddenValueName.split(",");
				userdefinedReport.setNames(names);
				HashMap map = new HashMap();
				if (names != null && names.length > 0) {
					for (int i = 0; i < names.length; i++) {
						if (names[i].contains(":M")) {
							String[] nas = names[i].split(":");
							String strs = (String) this.getFormHM().get(nas[0]);
							strs = URLDecoder.decode(strs);
							map.put(nas[0], strs);
						} else {
							String value = (String) this.getFormHM().get(
									names[i]);
							value = URLDecoder.decode(value);
							map.put(names[i], value);
						}
					}
				}
				userdefinedReport.setPublicParamMap(map);
			}

			ext = userdefinedReport.getExt();
			String filename = "";
			if (ext.indexOf("htm") != -1) {
				str = userdefinedReport.analyseUserdefinedHtmlReport(url);
			} else if (ext.indexOf("xls") != -1 || ext.indexOf("xlt") != -1) {
				filename = userdefinedReport.analyseUserdefinedExcelReport();
				this.getFormHM().put("filename", filename);
			} else if (ext.indexOf("mht") != -1) {
				str = userdefinedReport.analyseUserdefinedMhtReport(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (ext.indexOf("htm") != -1 || ext.indexOf("mht") != -1) {
			context = SafeCode.encode(context);
			String htmlpar = str[1];
			// htmlpar = SafeCode.encode(htmlpar);
			// this.getFormHM().put("htmlparam", htmlpar);
			this.getFormHM().put("filename", str[0]);
			this.getFormHM().put("url","/system/options/customreport/html/" + str[0]);
		} else if (ext.indexOf("xls") != -1 || ext.indexOf("xlt") != -1) {
			String ht = userdefinedReport.getHtmlcontent();
			ht = SafeCode.encode(ht);
			this.getFormHM().put("htmlparam", ht);
			this.getFormHM().put("url","/system/options/customreport/displayExcelcustomreport.jsp");
		}else{
			this.getFormHM().put("htmlparam", "");
			this.getFormHM().put("url","");
		}
	}
}
