package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.businessobject.sys.param.Sys_Infom_Parameter;
import com.hjsj.hrms.module.certificate.config.businessobject.CertificateConfigBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.sql.RowSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
/**
 * 批量导入附件交易类
 * @Title:        ImportCertificateAttachmentTrans.java
 * @Description:  批量导入人员证书的附件，并保存导数据库，证书附件保存到服务器硬盘中
 * @Company:      hjsj
 * @Create time:  2018年8月13日 下午2:36:13
 * @author        chenxg
 * @version       1.0
 */
public class ImportCertificateAttachmentTrans extends IBusiness {
	// 读取包中的单个文件流
	InputStream inOneFile = null;

	@Override
	public void execute() throws GeneralException {
		// 成功导入数据计算器
		int importCount = 0;
		// 导入文件的总数
		int amount = 0;

		long maxSize =0;

		String nbaseTemp = "";
        String a0100Temp = "";

		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		StringBuffer info = new StringBuffer("");
		try {
			String fileid = (String) this.getFormHM().get("fileid");

			String firstField = (String) this.getFormHM().get("firstField");
			String secondField = (String) this.getFormHM().get("secondField");
			String importTpye = (String) this.getFormHM().get("importTpye");

			Sys_Infom_Parameter sys_Infom_Parameter=new Sys_Infom_Parameter(this.getFrameconn(),"INFOM");
			String multimedia_maxsize=sys_Infom_Parameter.getValue(Sys_Infom_Parameter.MULTIMEDIA,"MultimediaMaxSize");
			multimedia_maxsize=multimedia_maxsize!=null&&multimedia_maxsize.length()>=0?multimedia_maxsize:"-1";
			//是否控制上传图片大小
			boolean sizeControl = false;
			long multimediaMaxSize = Long.parseLong(multimedia_maxsize)*1024;
            if(multimediaMaxSize>0){
                sizeControl = true;
                maxSize=multimediaMaxSize;
            }

            CertificateConfigBo ccbo = new CertificateConfigBo(this.frameconn, this.userView);
            ArrayList<String> userbaseList = ccbo.getCertNbase();
			if (userbaseList == null || userbaseList.size() == 0) {
				info.append("人员库不能为空！");
				return;
			}

			String setid = ccbo.getCertSubset();
			if (StringUtils.isEmpty(setid)) {
				info.append("证书子集不能为空！");
				return;
			}

			String certNameItem = ccbo.getCertName();
			String certNOItem = ccbo.getCertNOItemId();

			StringBuffer sqlStr = new StringBuffer();
			for(String nbase : userbaseList) {
				if(StringUtils.isNotEmpty(sqlStr.toString()))
					sqlStr.append(" union all ");

				sqlStr.append("select '" + nbase + "' nbase,a0100,a0101");
				sqlStr.append(" from " + nbase + "a01");
				sqlStr.append(" where " + firstField + "=?");
			}

			String fileTypeSql = "select flag from mediasort where SORTNAME=?";
			MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn, this.userView);
			// vfs改造
			InputStream input = VfsService.getFile(fileid);
			// 59997 缺少分隔符 导致临时文件直接到服务器路径下
			String tempfile = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + new Date().getTime()+".zip";
			File file = multiMediaBo.inputstreamtofile(input, tempfile);

			// 59733 解决文件名称中文乱码
            String encoding = PubFunc.getZIPEncoding(tempfile);
            ZipFile zipFile = new ZipFile(file, encoding);

			Enumeration e = zipFile.getEntries();
			ZipEntry zipEntry = null;
			boolean isDelete = true;
			// 遍历压缩包中的每个文件
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				// 判断是否是文件夹，若是则不上传，但文件夹中的文件会上传 chenxg 2016-11-09
				if (zipEntry.isDirectory())
					continue;

				amount++;
				String fileName = zipEntry.getName();
				// 判断是不是GBK或GB2312，如果不是则转成GB2312
				String encode = PubFunc.getEncoding(fileName);
				if (StringUtils.isNotEmpty(encode) && !"GB2312".equals(encode) && !"GBK".equals(encode)) {
					String unicode = new String(fileName.getBytes(), encode);
					fileName = new String(unicode.getBytes("GB2312"));
				}

				inOneFile = zipFile.getInputStream(zipEntry);
				if(inOneFile == null) {
					info.append("&nbsp;&nbsp;\"" + fileName + "\"文件解压失败！<br>");
					continue;
				}

				long size = zipEntry.getSize();
				if (0 == size) {
					info.append("&nbsp;&nbsp;\"" + fileName + "\"大小为0Byte，不允许上传！<br>");
					continue;
				}

				boolean isFileTypeEqual = FileTypeUtil.isFileTypeEqual(file);
				if (!isFileTypeEqual) {
					info.append("&nbsp;&nbsp;\"" + fileName + "\"是非法文件，不允许上传！<br>");
					continue;
				}

				String filePackageName = "";
				if (fileName.indexOf("/") != -1) {
					filePackageName = fileName.substring(0, fileName.lastIndexOf("/") + 1);
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());

				}
				if (fileName.length() == 0) {
					amount--;
					continue;
				}
				// 检查文件:这里必须重新取压缩包中的文件流（该方法中要读取流会使流中的标记位置改变，为了避免入库时流的标记位置不是文件的开始位置这里一定不能使用inOneFile）
				String realtype = getFormatName(zipFile.getInputStream(zipEntry));
				if (!checkFile(zipEntry, realtype, sizeControl, maxSize, info))
					continue;

				// 20200528 优化1： 支持-分隔符
				String splitChar = fileName.contains("_") ?  "_" : "-";
				int underlineCount = fileName.split("_").length - 1;
				int strikeCount = fileName.split("-").length - 1;

				// 20200528 优化： 除人员唯一id和证书编号，附件类型和“文件名”不要求必须有
				String[] fieldValues = null;
				// 1: 四段都是_分隔； 2： 四段都是-分隔； 3： 四段混用-和_; 4: 两段用_分隔； 5： 两段用-分隔； 6：异常
				if (underlineCount == 3 || (underlineCount == 1 && strikeCount == 0)) {
					fieldValues = fileName.split("_");
				} else if (strikeCount == 3 || (strikeCount == 1 && underlineCount == 0)) {
					fieldValues = fileName.split("-");
				} else if (underlineCount + strikeCount == 3) {
					fieldValues = fileName.replaceAll("-", "_").split("_");
				} else if (underlineCount == 1) {
					fieldValues = fileName.split("_");
				} else if (strikeCount == 1) {
					fieldValues = fileName.split("-");
				} else {
					info.append("失败原因：<BR>\"" + fileName + "\"的文件名不符合规则！<br>");
					continue;
				}

				// 人员唯一性编号
				String empUniqueNumber = fieldValues[0];
				// 证书编号
				String certNumber = fieldValues[1];
				if (fieldValues.length == 2) {
					certNumber = certNumber.substring(0, certNumber.lastIndexOf("."));
				}
				// 文件后缀
				String ext = fileName.substring(fileName.lastIndexOf("."));


				ArrayList<String> paramList = new ArrayList<String>();
				for(int i = userbaseList.size(); i > 0; i--) {
					paramList.add(empUniqueNumber);
				}
				// 查找附件归属人员信息
				rs = dao.search(sqlStr.toString(), paramList);
				int countRow = rs.getMaxRows();
				if(countRow > 1) {
					info.append("&nbsp;&nbsp;文件上传失败,\"" + empUniqueNumber + "\"不能和多个人员对应！<br>");
					continue;
				}

				String nbase = "";
				String a0100 = "";
				String a0101 = "";
				if(rs.next()) {
					nbase = rs.getString("nbase");
					a0100 = rs.getString("a0100");
					a0101 = rs.getString("a0101");
				}

				if(!nbase.equalsIgnoreCase(nbaseTemp) || !a0100.equals(a0100Temp)) {
				    nbaseTemp = nbase;
				    a0100Temp = a0100;
				    isDelete = true;
				}

				if(StringUtils.isEmpty(nbase) || StringUtils.isEmpty(a0100)) {
					info.append("&nbsp;&nbsp;没有找到（" + filePackageName + empUniqueNumber + "）人员或没有操作的权限！<br>");
					continue;
				}

				// 证书名称
				String certName = "";
				String i9999 = "0";
				StringBuffer sql = new StringBuffer();
				sql.append("select i9999,").append(certNameItem);
				sql.append(" from ").append(nbase).append(setid);
				sql.append(" where ").append(secondField).append("=?");
				sql.append(" and a0100=?");
				paramList.clear();
				paramList.add(certNumber);
				paramList.add(a0100);
				// 查找附件对应的证书子集记录
				rs = dao.search(sql.toString(), paramList);
				if(rs.next()) {
					i9999 = rs.getString("i9999");
					certName = rs.getString(certNameItem);
				}

				if(StringUtils.isEmpty(i9999) || "0".equalsIgnoreCase(i9999)) {
					String setName = DataDictionary.getFieldSetVo(setid).getFieldsetdesc();
					a0101 = StringUtils.isEmpty(a0101) ? empUniqueNumber : a0101;
					info.append("&nbsp;&nbsp;&nbsp;&nbsp;文件：" + fileName + "<br>&nbsp;&nbsp;&nbsp;&nbsp;原因：" + a0101 + "的"
							+ setName + "下没有找到对应证书的记录！<br>");
					continue;
				}

				// 附件分类
				String attachmentClass = "F";
				// 20200528 优化3： 文件名中不包含附件部分时，默认放到F分类
				if (fieldValues.length > 2) {
					paramList.clear();
					paramList.add(fieldValues[2]);
					rs = dao.search(fileTypeSql, paramList);
					if(rs.next())
						attachmentClass = rs.getString("flag");

					attachmentClass = StringUtils.isEmpty(attachmentClass) ? "F" : attachmentClass;
				}


				certName = fieldValues.length <= 2 ? certName : fieldValues[3];

				File attachmentFile = multiMediaBo.inputstreamtofile(inOneFile, fileName);
				if (attachmentFile != null) {
					// 图片安全过滤
					if ("/.png/.jpg/.jpeg/.bmp/.gif/".indexOf("/" + ext + "/") > -1) {
						File tempFile = multiMediaBo.inputstreamtofile(ImageBO.imgStream(attachmentFile, ext), certName + ext);
						attachmentFile.delete();
						attachmentFile = tempFile;
					}

					sql.setLength(0);
					sql.append("select a.id,a.mainguid,a.childguid,a.path,a.filename,a.ext,a.class");
					sql.append(" from hr_multimedia_file a left join " + nbase + "A01 b on a.mainguid=b.GUIDKEY");
					sql.append(" left join " + nbase + setid +" c on a.childguid=c.GUIDKEY and b.A0100=c.A0100");
					sql.append(" where " + Sql_switcher.isnull("a.childguid", "'#'") + "<>'#'");
					sql.append(" and a.A0100=? and a.nbase=?");

					paramList.clear();
					paramList.add(a0100);
					paramList.add(nbase);
					if("1".equals(importTpye)) {
						sql.append(" and c.i9999=? and a.topic=? and a.class=?");
						paramList.add(i9999);
						paramList.add(certName);
						paramList.add(attachmentClass);
					}

					if(isDelete) {
						ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
						rs = dao.search(sql.toString(), paramList);
						while (rs.next()) {
							LazyDynaBean bean = new LazyDynaBean();
							bean.set("mediaid", rs.getString("id"));
							bean.set("mainguid", rs.getString("mainguid"));
							bean.set("childguid", rs.getString("childguid"));
							bean.set("nbase", nbase);
							bean.set("a0100", a0100);
							bean.set("path", rs.getString("path"));
							bean.set("filename", rs.getString("filename"));
							bean.set("filetype", rs.getString("class"));
							bean.set("dbflag", 'A');
							list.add(bean);
						}

						multiMediaBo.deleteMultimediaRecord(list);
					}

					HashMap<String, String> vlaueMap = new HashMap<String, String>();
					vlaueMap.put("filetype" , attachmentClass);
					vlaueMap.put("filetitle" , certName);
					multiMediaBo.setParam("A", nbase, setid, a0100, Integer.parseInt(i9999));
					multiMediaBo.saveMultimediaFile(vlaueMap, attachmentFile, false);
					if(attachmentFile.isFile() && attachmentFile.exists())
						attachmentFile.delete();

					if(!"1".equals(importTpye))
						isDelete = false;

				} else {
					info.append(filePackageName + empUniqueNumber + "从zip压缩包复制文件失败！<br>");
					continue;
				}

				importCount++;
				if (inOneFile != null) {
					inOneFile.close();
					inOneFile = null;
				}
			}
			// 关闭压缩文件
			zipFile.close();

			info.insert(0, ("共计"+amount+"个，成功导入"+importCount+"个文件，失败"+(amount-importCount)+"个！<br>"));
            this.getFormHM().put("info", info.toString());
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("errorMessage", "导入过程中发生错误！<br>异常：" + e.getClass().getName()
					+ "<br>信息：" + e.getMessage() + "<br>建议：检查要导入的文件名称中各部分是否符合导入要求，是否能正常分割。");
		} finally {
			PubFunc.closeResource(rs);
		}
	}

	public String getFormatName(Object object) throws IOException {
		ImageInputStream iis = ImageIO.createImageInputStream(object);
		Iterator iterator = ImageIO.getImageReaders(iis);

		if (!iterator.hasNext())
			return null;

		ImageReader reader = (ImageReader) iterator.next();
		String name = reader.getFormatName();
		iis.close();
		return name;
	}

	private static String getDateFormat(int length) {
		String format = "";
		if (4 == length)
			format = "yyyy";
		else if (7 == length)
			format = "yyyy-MM";
		else if (16 == length)
			format = "yyyy-MM-dd hh:mm";
		else if (18 <= length)
			format = "yyyy-MM-dd hh:mm:ss";
		else
			format = "yyyy-MM-dd";

		return format;
	}

	/**
	 * tianye create method 文件检查
	 *
	 * @param zipEntry
	 *            压缩包中的文件实体对象
	 * @param sizeControl
	 *            是否对文件大小控制
	 * @param maxSize
	 *            控制的最大值
	 * @param info
	 *            检测后记录的相关信息
	 * @return
	 */
	public boolean checkFile(ZipEntry zipEntry, String realtype, boolean sizeControl, long maxSize, StringBuffer info) {
		try {
			String fileName = zipEntry.getName();
			// 判断是不是GBK或GB2312，如果不是则转成GB2312
			String encode = PubFunc.getEncoding(fileName);
			if (StringUtils.isNotEmpty(encode) && !"GB2312".equals(encode) && !"GBK".equals(encode)) {
				String unicode = new String(fileName.getBytes(), encode);
				fileName = new String(unicode.getBytes("GB2312"));
			}
			int indexInt = fileName.lastIndexOf(".");
			String type = "";
			if (indexInt != -1) {
				type = fileName.substring(indexInt + 1, fileName.length());
			} else {
				info.append("&nbsp;&nbsp;请检查文件（" + fileName + "）没有扩展名！<br>");
				return false;
			}

			String str = "bmp.jpg.jpeg.jpe.jfif";// 因为JPEG类型的文件后缀名包括这四个
			if (!type.equalsIgnoreCase(realtype)) {
				if (("JPEG".equalsIgnoreCase(realtype) && !str.toUpperCase().contains(type.toUpperCase()))) {
					info.append("&nbsp;&nbsp;" + fileName
							+ ResourceFactory.getProperty("workbench.info.noalterextension") + "<br>");
					return false;
				}
			}
			// 判断文件大小
			if (sizeControl && zipEntry.getSize() > maxSize) {
				info.append("&nbsp;&nbsp;" + fileName + "文件超过了" + maxSize / 1024 + "KB !<br>");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
}
