package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.hire.ResumeImportSchemeXmlBo;
import com.hjsj.hrms.businessobject.hire.TransResumeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.axis.encoding.Base64;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.struts.upload.FormFile;
import org.w3c.dom.NodeList;

import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @ClassName: ResumeZipTrans
 * @Description: TODO简历导入
 * @author xmsh
 * @date 2013-12-27 上午11:33:17
 * 
 */
public class ResumeZipTrans extends IBusiness {

	public void execute() throws GeneralException {

		// StringBuffer Log = new StringBuffer(""); // 导入信息log

		InputStream inputStream = null;
		FormFile form_file = (FormFile) getFormHM().get("zipFile"); // 获取zip文件
		String name = form_file.getFileName();
		String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + name;
//		String ResumeName = "";

		ResumeImportSchemeXmlBo resumeImportSchemeXmlBo = new ResumeImportSchemeXmlBo(this.getFrameconn());

		ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.frameconn, "1");
		HashMap map = parameterXMLBo.getAttributeValues();

		//从system中获取简历解析服务地址
		String url = SystemConfig.getPropertyValue("resume_service_url");
		if(url==null|| "".equals(url))
		{
			url = "http://www.51ats.com/ResumeService.asmx";
		}
		String nameSpace = "http://tempuri.org/";
		String methodName = "TransResume";

		ZipInputStream is = null;
		BufferedReader br = null;
		BufferedReader bu = null;
		ZipInputStream zs = null;
		int Num = 0; // 简历总数

		try (OutputStream outputStream = new FileOutputStream(filePath)){

			String userName = ""; // 简历解析服务帐号
			String password = ""; // 简历解析服务密码
			String ForeignJob = ""; // 简历解析关联的对外岗位指标
			String PostID = ""; // 简历的应聘岗位

			if (map != null && map.get("resumeAnalysisMap") != null) {
				HashMap resumeAnalysisMap = (HashMap) map.get("resumeAnalysisMap");
				userName = (String) resumeAnalysisMap.get("resumeAnalysisName"); // 获取简历解析服务帐号
				password = (String) resumeAnalysisMap.get("resumeAnalysisPassword");
				ForeignJob = (String) resumeAnalysisMap.get("resumeAnalysisForeignJob");
			}

			TransResumeBo bo = new TransResumeBo(url, methodName, userName, password, this.frameconn);

			// 获取黑名单库
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String blacklist_per = sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST, "base");// 黑名单人员库
			String blacklist_field = sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST, "field");// 黑名单人员指标
			String blacklist_value = ""; // 黑名单值

			// 上传zip包到服务器
			inputStream = form_file.getInputStream();
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			// 从服务器获得zipfile对象并解析
			ZipFile zipFile = new ZipFile(filePath); // 根据路径取得需要解压的Zip文件
			zipFile.setFileNameCharset("GBK");

			List fileHeaderList = zipFile.getFileHeaders();
			String ferror = "";//格式错误
			String contentError = "";//内容不对应
			ArrayList menuList = resumeImportSchemeXmlBo.getResumeSchemeXML(); // 获取所有指标的属性(包括xml和数据库)
			for (int i = 0; i < fileHeaderList.size(); i++) {
				FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
				if(fileHeader.isDirectory())//若是文件夹 continue
					continue;
				String fileName = "";
				String extention = "";
				if (fileHeader.getFileName().length() > 0 && fileHeader.getFileName() != null) { // --截取文件名
					int j = fileHeader.getFileName().lastIndexOf(".");
					int k = fileHeader.getFileName().lastIndexOf("/");
					
					if (j > -1 && j < fileHeader.getFileName().length()&&!fileHeader.isDirectory()) {
						fileName = fileHeader.getFileName().substring(k + 1, j); // --文件名
						extention = fileHeader.getFileName().substring(j + 1); // --扩展名
					}
				}
				// System.out.println(fileName);
				String jl = "";
				ArrayList ResumeInfoList = new ArrayList(); // 个人简历信息集
				String wordString="";
				
				if (!"".equals(fileName) && "txt".equalsIgnoreCase(extention)) { // 解析txt格式的简历
					is = zipFile.getInputStream(fileHeader);
					br = new BufferedReader(new InputStreamReader(is));
					String str;

					while ((str = br.readLine()) != null) {
						jl += str;
					}
					Num++;

				} else if (!"".equals(fileName) // 解析htm或html格式的简历
						&& ("htm".equalsIgnoreCase(extention) || "html".equalsIgnoreCase(extention))) {
					is = zipFile.getInputStream(fileHeader);
					zs = zipFile.getInputStream(fileHeader);
					// Encoding code = Encoding.GetEncoding("UTF-8");

					br = new BufferedReader(new InputStreamReader(is));
					
					String charset = "utf-8";//默认使用utf-8编码读取文件
					String charsetByText="";//通过文本前3个字节进行判断，只需要判断UTF-16的这种有可能在soap协议中出错
					byte b[]=new byte[3];
					is.read(b);
					if(b[0] == -17 && b[1] == -69 && b[2] == -65){//特殊的三个字节
					    charsetByText="UTF-8";
					}else if(b[0] == -1&& b[1] == -2 && b[2] == 60){//特殊的三个字节
					    charsetByText="UTF-16";
					}
	                String line = "";
                    while ((line = br.readLine()) != null) {
                        // System.out.println(line);
                        if (line.contains("<meta") && line.contains("charset")) {
                            if (line.contains("UTF-8") || line.contains("utf-8")) {
                                charset = "utf-8";
                                break;
                            } else if (line.contains("gbk") || line.contains("GBK")) {
                                charset = "gbk";
                                break;
                            } else if (line.contains("gb2312") || line.contains("GB2312")) {
                                charset = "gb2312";
                                break;
                            }
                        }
                    }
                    if("UTF-16".equalsIgnoreCase(charsetByText)){//需要判断下UTF-16，不能简单的根据html页面上设置走
                        charset="UTF-16";
                    }
					bu = new BufferedReader(new InputStreamReader(zs, charset));
					String str = "";
					while ((str = bu.readLine()) != null) {
						jl += str;
					}
					jl = TransResumeBo.getPlainText(jl);
					Num++;

				} else if (!"".equals(fileName) && "mht".equalsIgnoreCase(extention)) {
					is = zipFile.getInputStream(fileHeader);
					Session mailSession = Session.getDefaultInstance(System.getProperties(), null);
					MimeMessage msg = new MimeMessage(mailSession, is);
					Object content = msg.getContent();
					if (content instanceof Multipart) {
						MimeMultipart mp = (MimeMultipart) content;
						MimeBodyPart bp1 = (MimeBodyPart) mp.getBodyPart(0);

						// 获取mht文件内容代码的编码
						String strEncodng = "gb2312";
						strEncodng = TransResumeBo.getEncoding(bp1);
						jl = TransResumeBo.getHtmlText(bp1, strEncodng);
						jl = TransResumeBo.getPlainText(jl);
					}

					Num++;

				}else if (!"".equals(fileName) && ("doc".equalsIgnoreCase(extention)|| "docx".equalsIgnoreCase(extention))){ //解析word文档
				 is = zipFile.getInputStream(fileHeader);
				 
				 ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
				 byte [] fileByte = new byte[1024];
				 int len=0;
				 while(( len = is.read(fileByte)) != -1){
				     out.write(fileByte, 0, len);
				 }
				 Base64 bin = new Base64();
				 wordString=bin.encode(out.toByteArray());
				 Num++;
				 }else if (!"".equals(fileName) && "pdf".equalsIgnoreCase(extention)) { // 解析pdf文档

					is = zipFile.getInputStream(fileHeader);

					PDFParser parser = new PDFParser(is);

					parser.parse();

					PDDocument document = parser.getPDDocument();

					PDFTextStripper stripper = new PDFTextStripper();
					jl = stripper.getText(document);

					Num++;
				}else if (!fileHeader.isDirectory()) {
					// 格式错误
					ferror += fileName + "." + extention + ";";
					Num++;
					continue;
				}

				if (jl!=null&&jl.length() > 0) {
					NodeList list = bo.getResumeList(jl, fileName + "." + extention); // 得到解析服务放回的节点集
					ArrayList ReturnInfoList = bo.ProceedReturnInfo(list); // 处理节点集并返回解析指标集下指标的值的集合
					getResumeList(ReturnInfoList, menuList, PostID, blacklist_value, ResumeInfoList, blacklist_field);
					// 增加简历信息
					if (ResumeInfoList.size() > 0) {
						bo.addA01(ResumeInfoList, blacklist_per, blacklist_field, blacklist_value, ForeignJob, PostID, this.userView, fileName + "." + extention);
					}else{
						if(bo.getFlistLog().indexOf(fileName)==-1){
							contentError += fileName + "." + extention + ";";
						};	

					}
				}else if(wordString.trim().length()>0){
				    NodeList list = bo.getResumeListForByteFile(wordString, fileName + "." + extention,extention); // 得到解析服务放回的节点集
				    ArrayList ReturnInfoList = bo.ProceedReturnInfoForFile(list); // 处理节点集并返回解析指标集下指标的值的集合
				    getResumeList(ReturnInfoList, menuList, PostID, blacklist_value, ResumeInfoList, blacklist_field);
                    // 增加简历信息
                    if (ResumeInfoList.size() > 0) {
                        bo.addA01(ResumeInfoList, blacklist_per, blacklist_field, blacklist_value, ForeignJob, PostID, this.userView, fileName + "." + extention);
                    }else{
                        if(bo.getFlistLog().indexOf(fileName)==-1){
                            contentError += fileName + "." + extention + ";";
                        };  

                    }
				}else{
					if(bo.getFlistLog().indexOf(fileName)==-1){
						contentError += fileName + "." + extention + ";";
					};	
				}

			}
			String outName = "";

			outName = bo.WriteImportDetail(this.getUserView(), bo.getBlacklistLog(), bo.getPlistLog(), ferror,contentError, bo.getClistLog(), bo.getFlistLog(), "手动导入简历", Num);

			this.getFormHM().put("Log", outName);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {

			try {
			    
			    if (bu != null) {
                    bu.close();
                }
			    if (br != null) {
                    br.close();
                }
				if (inputStream != null) {
					inputStream.close();
				}

				if (is != null) {
					is.close(true);
				}
				if (zs != null) {
					zs.close(true);
				}
				
				
			} catch (IOException e) {
				//e.printStackTrace(); 关闭流时出的错误
			}

		}
	}
    /**
     * 
     * @Title: getResumeList 获得简历解析人员的信息List 
     * @Description: TODO
     * @param ReturnInfoList 才智创新返还回来的人员信息list
     * @param menuList 简历指标对应List
     * @param PostID
     * @param blacklist_value  黑名单指标值
     * @param ResumeInfoList  当前解析的人员信息List
     * @param blacklist_field 黑名单人员指标
     * @throws
     */
    public void getResumeList(ArrayList ReturnInfoList,ArrayList menuList,String PostID,String blacklist_value,ArrayList ResumeInfoList,String blacklist_field){
        for (int j = 0; j < ReturnInfoList.size(); j++) {
		    HashMap hm = (HashMap) ReturnInfoList.get(j);
		    for (int k = 0; k < menuList.size(); k++) {
		        LazyDynaBean baen = (LazyDynaBean) menuList.get(k);
		        // 判断setid是否相等,若为空则为基本信息
		        if (((String) hm.get("setid")).equalsIgnoreCase((String) baen.get("setid"))) {//相对应的指标集
		            if (hm.containsKey(baen.get("itemid"))) {//得到相对应的指标
		                if ("应聘岗位".equals(baen.get("resumefld"))) {
		                    PostID = (String) hm.get(baen.get("itemid"));
		                }
		                // 判断指标是否导入
		                if ("1".equals(baen.get("valid"))) {
		                    LazyDynaBean ResumeBean = new LazyDynaBean();
		                    ResumeBean.set("itemtype", baen.get("itemtype"));
		                    ResumeBean.set("itemlength", baen.get("itemlength"));
		                    ResumeBean.set("valid", baen.get("valid"));
		                    ResumeBean.set("ehrfld", baen.get("ehrfld"));
		                    ResumeBean.set("resumefld", baen.get("resumefld"));
		                    ResumeBean.set("resumeset", baen.get("resumeset"));
		                    ResumeBean.set("itemformat", baen.get("itemformat"));
		                    ResumeBean.set("setid", baen.get("setid"));
		                    ResumeBean.set("itemid", baen.get("itemid"));
		                    ResumeBean.set("commonvalue", baen.get("commonvalue"));
		                    ResumeBean.set("itemid", baen.get("itemid"));
		                    ResumeBean.set("value", hm.get(baen.get("itemid")));//bean中获得的itemid就是resumeFLDXML中定义的itemid,而hm中存放的是才智创新返回来的值
		                    ResumeBean.set("i9999", hm.get("i9999"));
		                    // 若指标等于黑名单指标
		                    if (baen.get("ehrfld") != null && ((String) baen.get("ehrfld")).equalsIgnoreCase(blacklist_field)) {
		                        blacklist_value = (String) hm.get(baen.get("itemid"));
		                    }
		                    ResumeInfoList.add(ResumeBean);
		                }
		            }
		        }
   
		    }
		}
    }
}
