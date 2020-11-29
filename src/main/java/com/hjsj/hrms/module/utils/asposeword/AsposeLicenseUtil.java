package com.hjsj.hrms.module.utils.asposeword;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.License;
import com.hrms.struts.constant.SystemConfig;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AsposeLicenseUtil extends DocumentBuilder {

	private static InputStream license;
	public AsposeLicenseUtil() throws Exception {
		super();
		getLicense("");// 导出添加授权文件不需要创建对象 添加授权文件
	}
	public AsposeLicenseUtil(Document doc) throws Exception {
		super(doc);
		getLicense("");
	}
	public AsposeLicenseUtil(Document doc,String url) throws Exception {
		super(doc);
		getLicense(url);
	}
	public static boolean getLicense(String url) {
		boolean result = false;
		String strs = "";
		if("".equals(url)){
			//url = Thread.currentThread().getContextClassLoader().getResource("")+ "";
			try {
				ServletContext ServletContext=SystemConfig.getServletContext();
				strs=ServletContext.getRealPath("WEB-INF");
				if(strs==null)
				{
					strs=ServletContext.getResource("/").getFile();
					if(strs.indexOf("WEB-INF")>-1)
					{
						strs=strs.substring(0,strs.indexOf("WEB-INF")+"WEB-INF".length());
					}
					else
					{
						if("\\".equals(File.separator)){//证明是windows
							strs=strs+"WEB-INF";
						}else if("/".equals(File.separator)){//证明是linux
							strs = strs+"WEB-INF";
						}						
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("获取aspose.Total文件失败！");
				e.printStackTrace();
			}
		}else{
			strs = url;
		}

		File file = new File(strs);
		file = new File(file, "Aspose.Total.Java.lic");
		try {
			license = new FileInputStream(file.getPath());
			License aposeLic = new License();
			aposeLic.setLicense(license);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
