package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.businessobject.report.user_defined_reoprt.UserdefinedReport;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.net.URLDecoder;
import java.util.HashMap;
/**
 * 显示自定义报表
 * <p>Title:DisplayCustomReportTrans.java</p>
 * <p>Description>:DisplayCustomReportTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 11, 2010 11:27:30 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class DisplayExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		//是否加有权限
		String isprivstr = (String) this.getFormHM().get("ispriv");
		//自定义表格id
		String report_id = (String) this.getFormHM().get("id");
		// url
		String url = (String) this.getFormHM().get("realurl");
		
		// 输入的参数
		String inputParam = (String) this.getFormHM().get("inputParam");
		
		if (url != null) {
			url = URLDecoder.decode(url);
		}
		boolean ispriv=false;
		if(isprivstr!=null&& "1".equals(isprivstr))
			ispriv=true;
		
		//填充页面内容
		String context="";
		String str[] = new String[3];
		//文件扩展名
		String ext = "";
		UserdefinedReport userdefinedReport = null;
		try {	
			userdefinedReport=new UserdefinedReport(userView,this.frameconn,report_id,ispriv);
			//获得所有下拉列表的名称
			String hiddenValueName = (String) this.getFormHM().get("hiddenValueName");
			if (hiddenValueName != null && hiddenValueName.length() > 0) {
				String []names = hiddenValueName.split(",");
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
							String value = (String) this.getFormHM().get(names[i]);
							value = URLDecoder.decode(value);
							map.put(names[i], value);
						}
					}
				}
				userdefinedReport.setPublicParamMap(map);
			}
			
			
			// 保存输入参数
			if (inputParam != null && inputParam.length() > 0) {
				HashMap map = new HashMap();
				HashMap map2 = userdefinedReport.getPublicParamMap();
				
				if (map2 == null) {
					map2 = new HashMap();
				}
				
				
				String names[] = userdefinedReport.getNames();
				
				String[] params = inputParam.split(",");
				
				String[] strNames = null;
				int index = 0;
				if (names == null) {
					strNames = new String[params.length];
				} else {
					strNames = new String[params.length + names.length];
					for (int i = 0; i < names.length; i++) {
						strNames[i] = names[i];
					}
					index = names.length;
				}
				
				for (int i = 0; i < params.length; i++) {
					if (params[i] != null) {
						String[] param = params[i].split(":");
						map.put(param[0], param[1]);
						map2.put(param[0], param[1]);
						strNames[index + i] = param[0];
					}
				}
				userdefinedReport.setInputParamMap(map);
				userdefinedReport.setPublicParamMap(map2);
				userdefinedReport.setNames(strNames);
			}
			
			
			ext=userdefinedReport.getExt();	
			String filename = "";
			if(ext.indexOf("htm")!=-1)
			{
				str=userdefinedReport.analyseUserdefinedHtmlReport(url);
			} else if (ext.indexOf("xls") != -1 || ext.indexOf("xlt") != -1){
				filename=userdefinedReport.analyseUserdefinedExcelReport();
				this.getFormHM().put("filename", filename);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (ext.indexOf("htm")!=-1) {
			context = SafeCode.encode(context);
			String htmlpar = str[1];
//			htmlpar = SafeCode.encode(htmlpar);
//			this.getFormHM().put("htmlparam", htmlpar);
			this.getFormHM().put("filename", str[0]);
			this.getFormHM().put("url","/system/options/customreport/html/"+str[0]);
		} else if (ext.indexOf("xls") != -1 || ext.indexOf("xlt") != -1) {
			String ht = userdefinedReport.getHtmlcontent();
			ht = SafeCode.encode(ht);
			this.getFormHM().put("htmlparam", ht);
			this.getFormHM().put("url","/system/options/customreport/displayExcelFromServlet.jsp");
		}
		
	}

}
