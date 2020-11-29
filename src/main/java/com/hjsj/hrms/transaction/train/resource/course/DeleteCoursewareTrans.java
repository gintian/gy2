/**
 * 
 */
package com.hjsj.hrms.transaction.train.resource.course;

import com.hjsj.hrms.businessobject.general.ftp.FtpMediaBo;
import com.hjsj.hrms.businessobject.train.MediaServerParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.File;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * <p>
 * Title:DeleteCoursewareTrans
 * </p>
 * <p>
 * Description:删除培训课程课件
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 23, 2009:1:07:05 PM
 * </p>
 * 
 * @author xujian
 * @version 1.0
 * 
 */
public class DeleteCoursewareTrans extends IBusiness {

    /**
	 * 
	 */
    public DeleteCoursewareTrans() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */

    public void execute() throws GeneralException {
        HashMap hm = this.getFormHM();
        String sel = (String) hm.get("sel");
        ArrayList idList = new ArrayList();
        if (sel != null && sel.length() > 0) {
            String[] sels = sel.split(",");
            int n = 0;
            String id = "";
            for (int i = 0; i < sels.length; i++) {
                if (n > 0)
                    id += ",";
                id += PubFunc.decrypt(SafeCode.decode(sels[i]));
                n++;
                if (n == 1000) {
                    idList.add(id);
                    id = "";
                    n = 0;
                }
            }

            if (id.length() > 0)
                idList.add(id);
        }
        String urlsb = (String) hm.get("urlsb");
        urlsb = SafeCode.decode(urlsb);
        String a_code = (String) hm.get("a_code");
        a_code = PubFunc.decrypt(SafeCode.decode(a_code));
        String sep = System.getProperty("file.separator");
        String filepath = (String) hm.get("filepath");
        filepath = SafeCode.decode(filepath);
        filepath = filepath.replace("``", sep);
        filepath = URLDecoder.decode(filepath);

        ContentDAO cd = new ContentDAO(this.getFrameconn());
        for (int m = 0; m < idList.size(); m++) {
            String ids = (String) idList.get(m);
            //删除已选的scorm课件信息
            String sqlsc = "delete from tr_selected_course_scorm where r5100 in (" + ids + ")";
            //删除已选的课件信息
            String sqls = "delete from tr_selected_course where r5100 in (" + ids + ")";
            String sql = "delete from r51 where r5100 in(" + ids + ")";
            String sqlc = "select r5000 from r51 where r5100 in (" + ids + ")";
            RowSet rs = null;
            try {
                // 删除文件
                if (urlsb != null && urlsb.length() > 0) {
                    String[] urls = urlsb.split(",");
                    for (int i = 0; i < urls.length; i++) {
                        String url = urls[i];
                        url = url.replace("\\", "\\\\");
                        File file = new File(filepath + url);
                        if (file.exists()) {
                            file.delete();
                        }

                        if (url.toLowerCase().endsWith(".zip")) {
                            deleteFiles(filepath + url.substring(0, url.toLowerCase().lastIndexOf(sep) + 1) + ids.split(",")[i]);
                        }
                    }
                }

                this.frowset = cd.search("select * from r51 where r5100 in (" + ids + ")");
                while (this.frowset.next()) {
                    String r5105 = this.frowset.getString("r5105");
                    String r5113 = this.frowset.getString("r5113");
                    r5113 = r5113 == null || r5113.length() < 1 ? "" : r5113;
                    String r5100 = this.frowset.getString("r5100");
                    if (MediaServerParamBo.getMediaServerAddress() != null && MediaServerParamBo.getMediaServerAddress().length() > 0) {
                        if ("3".equals(r5105)) {
                            String filePath = "";
                            if (a_code != null && a_code.length() > 0) {
                                for (int i = 0; i < a_code.length() / 2; i++) {
                                    filePath += a_code.substring(0, 2 * (i + 1)) + sep;
                                }
                            }

                            String path = MediaServerParamBo.getFilePath();
                            if (path != null && !path.endsWith("/")) {
                                path += "/";
                            }

                            path = path.replaceAll("/", Matcher.quoteReplacement(sep));

                            String fileName = "";

                            if (r5113 != null) {
                                int index = r5113.lastIndexOf("/");
                                if (index == -1) {
                                    index = r5113.lastIndexOf("\\");
                                }
                                fileName = r5113.substring(index + 1, r5113.length());
                            }

                            // ftp删除
                            if (MediaServerParamBo.getFtpServerAddress() != null && MediaServerParamBo.getFtpServerAddress().length() > 0) {
                                FtpMediaBo bo = new FtpMediaBo(MediaServerParamBo.getFtpServerAddress(), Integer.parseInt(MediaServerParamBo.getFtpServerPort()), MediaServerParamBo
                                        .getFtpServerUserName(), MediaServerParamBo.getFtpServerPwd());
                                bo.deleteFile(path + filePath, fileName);
                            } else {// 本地删除

                                File file = new File(path + filePath + fileName);
                                if (file.exists()) {
                                    file.delete();
                                }

                            }

                        }
                    }

                    if ("1".equals(r5105)) {
                        String coursePath = r5113.toLowerCase();
                        if (coursePath.endsWith(".doc") || coursePath.endsWith(".docx") || coursePath.endsWith(".xls") || coursePath.endsWith(".xlsx") || coursePath.endsWith(".pdf")
                                || coursePath.endsWith(".ppt") || coursePath.endsWith(".pptx")) {
                            String delpath = filepath + coursePath;
                            delpath = delpath.substring(0, delpath.lastIndexOf(sep)) + sep + r5100 + ".swf";
                            File file = new File(delpath);
                            if (file.exists()) {
                                file.delete();
                            }

                        }
                    }
                }

                this.frowset = cd.search(sqlc);
                int r5000 = 0;
                if (this.frowset.next()) {
                    r5000 = this.frowset.getInt("r5000");
                }
                cd.update(sqlsc);
                cd.update(sqls);
                String searchSql = "select fileid from r51 where r5100 in (" + ids + ")";
                this.frecset = cd.search(searchSql);
                while (this.frecset.next()) {
                	String fileid = this.frecset.getString("fileid");
                	if(StringUtils.isNotEmpty(fileid)) {
                		VfsService.deleteFile(this.userView.getUserName(), fileid);
                	}
                }
                
                cd.delete(sql, new ArrayList());
                // 更改课程的学习进度
                int count = 1;
                rs = cd.search("select id from tr_selected_lesson where r5000=" + r5000);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    this.frowset = cd.search("select count(*) a from tr_selected_course where id =" + id);
                    if (this.frowset.next()) {
                        count = this.frowset.getInt("a");
                        count = count == 0 ? 1 : count;
                    }
                    StringBuffer buff = new StringBuffer();
                    buff.append("update tr_selected_lesson set lprogress=(select sum(lprogress)/");
                    buff.append(count);
                    buff.append(" from tr_selected_course where id=" + id + ") where id=" + id);
                    cd.update(buff.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                GeneralExceptionHandler.Handle(e);
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 如果是ZIP文件 删除ZIP自解压文件
    public void deleteFiles(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            if (file.listFiles().length > 0) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteFiles(files[i].getAbsolutePath());
                    }

                    files[i].delete();
                }

            } else {
                file.delete();
            }
        }
        file.delete();
    }
}
