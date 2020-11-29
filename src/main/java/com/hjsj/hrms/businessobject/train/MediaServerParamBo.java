package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;

public class MediaServerParamBo {

	// 流媒体服务器类型
	private static String mediaServerType = "";

	// 流媒体服务器地址
	private static String mediaServerAddress = "";

	// 流媒体服务器端口
	private static String mediaServerPort = "1935";
	
	// 发布点
	private static String mediaServerPubRoot = "";

	// ftp服务器地址
	private static String ftpServerAddress = "";

	// ftp服务器端口
	private static String ftpServerPort = "21";

	// ftp服务器用户名
	private static String ftpServerUserName = "";

	// ftp服务器密码
	private static String ftpServerPwd = "";

	// 文件路径
	private static String filePath = "/media";

	// 文件大小限制
	private static String fileSize = "500";

	// 是否允许下载
	private static String isDownload = "";
	
	// openoffice地址
	//private static String openOfficeAdd = "127.0.0.1";
	
	// openoffice端口
	//private static String openOfficePort = "8100";

	static {
		init();

	}

	public static String getMediaServerType() {
		return mediaServerType;
	}

	public static String getMediaServerAddress() {
		return mediaServerAddress;
	}

	public static String getMediaServerPort() {
		return mediaServerPort;
	}

	public static String getFtpServerAddress() {
		return ftpServerAddress;
	}

	public static String getMediaServerPubRoot() {
		return mediaServerPubRoot;
	}
	
	public static String getFtpServerPort() {
		return ftpServerPort;
	}

	public static String getFtpServerUserName() {
		return ftpServerUserName;
	}

	public static String getFtpServerPwd() {
		return ftpServerPwd;
	}

	public static String getFilePath() {
		return filePath;
	}

	public static String getFileSize() {
		return fileSize;
	}

	public static String getIsDownload() {
		return isDownload;
	}
	
	public static String getIsDownload1(String r5000) {
	    r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
		String isDown="1";
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			DbWizard wizard = new DbWizard(conn);
			if(wizard.isExistField("r50", "R5033")){
				rs = dao.search("select r5033 from r50 where r5000='" + r5000 + "'");
				if(rs.next()){
					isDown = rs.getString("r5033");
					if(isDown==null||isDown.length()<1) {
                        isDown = "2";
                    }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isDown;
	}

	/**
	 * 查询设置的参数xml
	 * 
	 * @return
	 */
	private static String querryStr() {
		Connection conn = null;
		String xml = "";
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);

			// 常量表中查找MEDIASERVERSET常量
			rs = dao
					.search("select str_value from constant where constant='MEDIASERVERSET'");
			if (rs.next()) {
				// 获取XML文件
				xml = rs.getString("str_value");
				xml = xml == null ? "" : xml;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return xml;
	}

	/**
	 * 初始化
	 */
	private static void init() {
		try {
			XPath xPath = XPath.newInstance("/servers/server");
			String xml = querryStr();

			if (xml != null && xml.length() > 0) {
				Document doc = PubFunc.generateDom(xml);

				Element el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/mediaserverip");
				if (el != null) {
					mediaServerAddress = handlerNull(el.getText());
					mediaServerType = handlerNull(el.getAttributeValue("type"));
				}

				el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/mediaserverport");
				if (el != null) {
					mediaServerPort = handlerNull(el.getText());
				}
				
				el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/mediaServerPubRoot");
				if (el != null) {
					mediaServerPubRoot = handlerNull(el.getText());
				}
				
				el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/ftpserverip");
				if (el != null) {
					ftpServerAddress = handlerNull(el.getText());
				}

				el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/ftpserverport");
				if (el != null) {
					ftpServerPort = handlerNull(el.getText());
				}

				el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/ftpserverusername");
				if (el != null) {
					ftpServerUserName = handlerNull(el.getText());
				}

				el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/ftpserverpassword");
				if (el != null) {
					ftpServerPwd = handlerNull(el.getText());
				}

				el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/savepath");
				if (el != null) {
					filePath = handlerNull(el.getText());
				}

				el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/filesize");
				if (el != null) {
					fileSize = handlerNull(el.getText());
				}

				el = (Element) xPath.selectSingleNode(doc,
						"/servers/server[@type='main']/isupload");
				if (el != null) {
					isDownload = handlerNull(el.getText());
				}
			}

		} catch (JDOMException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 更新
	 * @param mediaServerType
	 * @param mediaServerAddress
	 * @param mediaServerPort
	 * @param ftpServerAddress
	 * @param ftpServerPort
	 * @param ftpServerUserName
	 * @param ftpServerPwd
	 * @param filePath
	 * @param fileSize
	 * @param isDownload
	 * @return
	 */
	public static boolean update(String mediaServerType, String mediaServerAddress,
			String mediaServerPort, String ftpServerAddress,
			String ftpServerPort, String ftpServerUserName,
			String ftpServerPwd, String filePath, String fileSize,
			String isDownload, String mediaServerPubRoot) {
		
		mediaServerType = handlerNull(mediaServerType);
		mediaServerAddress = handlerNull(mediaServerAddress);
		mediaServerPort = handlerNull(mediaServerPort);
		ftpServerAddress = handlerNull(ftpServerAddress);
		ftpServerPort = handlerNull(ftpServerPort);
		ftpServerUserName = handlerNull(ftpServerUserName);
		ftpServerPwd = handlerNull(ftpServerPwd);
		filePath = handlerNull(filePath);
		fileSize = handlerNull(fileSize);
		isDownload = handlerNull(isDownload);
		mediaServerPubRoot = handlerNull(mediaServerPubRoot);
		//openOfficeAdd = handlerNull(openOfficeAdd);
		//openOfficePort = handlerNull(openOfficePort);
		
		
		boolean flag = false;
		StringBuffer buff = new StringBuffer();
		
		buff.append("<?xml version='1.0' encoding='GB2312'?>");
		buff.append("<servers>");
		buff.append("<server type='main'>"); 
		buff.append("<mediaserverip type='" + mediaServerType + "'>" + mediaServerAddress + "</mediaserverip>");
		buff.append("<mediaserverport>"+mediaServerPort+"</mediaserverport>");
		buff.append("<mediaServerPubRoot>" + mediaServerPubRoot + "</mediaServerPubRoot>");
		buff.append("<ftpserverip>"+ftpServerAddress+"</ftpserverip>");
		buff.append("<ftpserverport>"+ftpServerPort+"</ftpserverport>");
		buff.append("<ftpserverusername>"+ftpServerUserName+"</ftpserverusername>");
		buff.append("<ftpserverpassword>"+ftpServerPwd+"</ftpserverpassword>");
		buff.append("<savepath>"+filePath+"</savepath>");
		buff.append("<filesize>"+fileSize+"</filesize>");
		buff.append("<isupload>"+isDownload+"</isupload>");
//		buff.append("<openofficeadd>"+openOfficeAdd+"</openofficeadd>");
//		buff.append("<openofficeport>"+openOfficePort+"</openofficeport>");
		buff.append("</server>");
		buff.append("</servers>");
		
		Connection conn = null;
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			RecordVo vo = new RecordVo("constant");
			vo.setString("constant", "MEDIASERVERSET");
			vo.setString("describe", "流媒体服务器设置");
			//vo.setString("str_value", buff.toString());
			vo.setObject("str_value", buff.toString());
			
			rs = dao.search("select * from constant where constant='MEDIASERVERSET'");
			if (rs.next()) {
				dao.updateValueObject(vo);
			} else {			
				dao.addValueObject(vo);
			}
			
			MediaServerParamBo.filePath = filePath;
			MediaServerParamBo.fileSize = fileSize;
			MediaServerParamBo.ftpServerAddress = ftpServerAddress;
			MediaServerParamBo.ftpServerPort = ftpServerPort;
			MediaServerParamBo.ftpServerPwd = ftpServerPwd;
			MediaServerParamBo.ftpServerUserName = ftpServerUserName;
			MediaServerParamBo.isDownload = isDownload;
			MediaServerParamBo.mediaServerAddress = mediaServerAddress;
			MediaServerParamBo.mediaServerPort = mediaServerPort;
			MediaServerParamBo.mediaServerType = mediaServerType;
			MediaServerParamBo.mediaServerPubRoot = mediaServerPubRoot;
//			MediaServerParamBo.openOfficeAdd = openOfficeAdd;
//			MediaServerParamBo.openOfficePort = openOfficePort;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return flag;
	}
	
	/**
	 * 处理空的情况
	 * @param str
	 * @return
	 */
	private static String handlerNull (String str) {
		if (str == null || str.trim().length() <= 0 || "null".equalsIgnoreCase(str)) {
			str = "";
		}
		
		return str.trim();
	}

	

//	public static String getOpenOfficeAdd() {
//		return openOfficeAdd;
//	}
//
//	public static String getOpenOfficePort() {
//		return openOfficePort;
//	}

}
