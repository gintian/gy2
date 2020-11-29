package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.param.DocumentParamXML;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;

/**
 * 规章制度导出
 * <p>
 * Title:LawbaseFileOutput.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Nov 14, 2006 5:22:19 PM
 * </p>
 * 
 * @author sunxin
 * @version 1.0
 * 
 */
public class LawbaseFileOutput extends HttpServlet {

    private HashMap baseId_map = new HashMap();
    private HashMap fileId_map = new HashMap();
    static final int BUFFER = 2048;

    private Element fileElement = null;

    private DocumentBuilderFactory factory = null;

    private DocumentBuilder builder = null;

    private Document doc = null;

    private Connection con = null;
    private UserView userview = null;
    String law_file_priv = SystemConfig.getPropertyValue("law_file_priv");

    public LawbaseFileOutput() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    /**
     * 将文件添加到输出流中
     * 
     * @param zipOut
     * @param fileName
     * @param fileInputStream
     */
    private void addZipFile(ZipOutputStream zipOut, String fileName, ResultSet rs, String itemdesc) {
        BufferedInputStream origin = null;
        InputStream fileInputStream = null;
        try {
            fileInputStream=VfsService.getFile(rs.getString(itemdesc));
            if (fileInputStream == null) {
                return;
            }
            byte data[] = new byte[BUFFER];
            ZipEntry entry = new ZipEntry(fileName);
            zipOut.putNextEntry(entry);
            origin = new BufferedInputStream(fileInputStream, BUFFER);
            int count = 0;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                zipOut.write(data, 0, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(origin);
            PubFunc.closeResource(fileInputStream);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        doc = builder.newDocument();
        Element lawBaseElement = doc.createElement("lawbase");
        Element dirElement = doc.createElement("dir");
        dirElement.setAttribute("columns", "base_id,up_base_id,name,description,dir,status," + "basetype,DisplayOrder,field_str,root");
        fileElement = doc.createElement("file");
        fileElement.setAttribute("columns", "file_id,base_id,name,title,type," + "content_type,valid,note_num,issue_org,notes,issue_date,"
                + "implement_date,valid_date,ext,viewcount,digest,fileorder,originalext,b0110,keywords");
        Element extElement = doc.createElement("ext");
        extElement.setAttribute("columns", "ext_file_id,file_id,version,name,ext," + "create_time,create_user");
        String base_id = request.getParameter("base_id");
        if (!"root".equalsIgnoreCase(base_id))
            base_id = PubFunc.decrypt(SafeCode.decode(base_id));
        String basetype = request.getParameter("basetype");
        if (basetype == null || basetype.length() <= 0)
            basetype = "1";
        String fileName = "规章制度";
        fileName = new String(fileName.getBytes(), "GB2312");
        ZipOutputStream zipOut = null;
        ResultSet rs_struct = null;

        Statement stmt = null, stmt1 = null;
        ResultSet rs_file = null;
        ResultSet dbrs = null;
        ResultSet unamers = null;
        DbSecurityImpl dbS = new DbSecurityImpl();
        try {
            try {
                con = AdminDb.getConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stmt = con.createStatement();
            stmt1 = con.createStatement();
            StringBuffer sb = new StringBuffer();
            ArrayList list = new ArrayList();
            String sqlStr="";
            userview = (UserView) request.getSession().getAttribute(WebConstant.userView);
            if ("root".equalsIgnoreCase(base_id)) {
            	
                if (!(this.userview.isSuper_admin() && !this.userview.isBThreeUser())) {
                	
                    if (userview.getUserOrgId() != null && userview.getUserOrgId().length() > 0 && !"false".equals(law_file_priv.trim())) {
                    	
                    	sqlStr="select * from law_base_struct where base_id=up_base_id and dir='" + userview.getUserOrgId() + "' and basetype='" + basetype + "'";
                    	dbS.open(con,sqlStr);
                        rs_struct = stmt.executeQuery(sqlStr);

                    } else {
                        if (!"false".equals(law_file_priv.trim())){
                        	
                        	sqlStr="select * from law_base_struct where base_id=up_base_id and basetype='" + basetype + "'";
                        	dbS.open(con,sqlStr);
                            rs_struct = stmt.executeQuery(sqlStr);
                        }
                      else{
                    	  
                    	  sqlStr="select * from law_base_struct where basetype='" + basetype + "'";
                    	  	dbS.open(con,sqlStr);
                            rs_struct = stmt.executeQuery(sqlStr);
                    }
                        }
                } else {
                	
                	sqlStr="select * from law_base_struct where base_id=up_base_id and basetype='" + basetype + "'";
            	  	dbS.open(con,sqlStr);
                    rs_struct = stmt.executeQuery(sqlStr);
                }

                sb.append("select * from law_base_file where base_id in (select base_id from law_base_struct where basetype='" + basetype + "') ");
                boolean flg = false;
                while (rs_struct.next()) {
                    if (!"false".equals(law_file_priv.trim())) {
                        if ("1".equalsIgnoreCase(basetype)) {
                            if (!userview.isHaveResource(IResourceConstant.LAWRULE, rs_struct.getString("base_id")))
                                continue;
                        }
                        if ("5".equalsIgnoreCase(basetype)) {
                            if (!userview.isHaveResource(IResourceConstant.DOCTYPE, rs_struct.getString("base_id")))
                                continue;
                        }
                        if ("4".equalsIgnoreCase(basetype)) {
                            if (!userview.isHaveResource(IResourceConstant.KNOWTYPE, rs_struct.getString("base_id")))
                                continue;
                        }
                    } else {
                        if ("1".equalsIgnoreCase(basetype)) {
                            if (!userview.isHaveResource(IResourceConstant.LAWRULE, rs_struct.getString("base_id")))
                                continue;
                        }
                        if ("5".equalsIgnoreCase(basetype)) {
                            if (!userview.isHaveResource(IResourceConstant.DOCTYPE, rs_struct.getString("base_id")))
                                continue;
                        }
                        if ("4".equalsIgnoreCase(basetype)) {
                            if (!userview.isHaveResource(IResourceConstant.KNOWTYPE, rs_struct.getString("base_id")))
                                continue;
                        }
                        Vector v = selectAllParentList("law_base_struct", "up_base_id", "base_id", rs_struct.getString("base_id"), null, con);
                        boolean flag = false;
                        for (int i = 0; i < v.size(); i++) {
                            String up_base_id = (String) v.get(i);
                            if (up_base_id.equals(rs_struct.getString("base_id")))
                                break;
                            if ("1".equalsIgnoreCase(basetype)) {
                                if (userview.isHaveResource(IResourceConstant.LAWRULE, up_base_id)) {
                                    flag = true;
                                    break;
                                }
                            }
                            if ("5".equalsIgnoreCase(basetype)) {
                                if (userview.isHaveResource(IResourceConstant.DOCTYPE, up_base_id)) {
                                    flag = true;
                                    break;
                                }
                            }
                            if ("4".equalsIgnoreCase(basetype)) {
                                if (userview.isHaveResource(IResourceConstant.KNOWTYPE, up_base_id)) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if (flag)
                            continue;
                    }
                    if (!flg) {
                        sb.append(" and ");
                        sb.append(" base_id = '" + rs_struct.getString("base_id") + "'");
                        flg = true;
                    } else {
                        sb.append(" or base_id = '" + rs_struct.getString("base_id") + "'");
                    }
                    list.add(rs_struct.getString("base_id"));
                    baseId_map.put(rs_struct.getString("base_id"), new Boolean(true));
                }
            } else {
                sb.append("select * from law_base_file where base_id=" + "'" + base_id + "'");
            }
            sb.append(" or UPPER(base_id)='ALL'");
            // System.out.println(sb.toString());
            
            response.setCharacterEncoding("GB2312");
            ServletOutputStream out = response.getOutputStream();

            response.setContentType("APPLICATION/OCTET-STREAM");
            UserView userView = (UserView) request.getSession().getAttribute("userView");
            String name = userView.getUserName() + "_train.zip";
            name = new String(name.getBytes("gb2312"), "ISO8859_1");
            response.setHeader("Content-Disposition", "attachment;   filename=\"" + name + "\"");
            CheckedOutputStream checksum = new CheckedOutputStream(out, new Adler32());
            zipOut = new ZipOutputStream(new BufferedOutputStream(checksum));
            ArrayList dblist = new ArrayList();
            dbS.open(con, sb.toString());
            rs_file = stmt.executeQuery(sb.toString());
            DocumentParamXML documentParamXML = new DocumentParamXML(con);
            String codesetid = "";
            String codeitemid = "";
            if ("5".equalsIgnoreCase(basetype)) {
                String dbsql = "select Pre from DBName";
                dbS.open(con, dbsql);
                dbrs = stmt1.executeQuery(dbsql);
                while (dbrs.next()) {
                    dblist.add(dbrs.getString("Pre"));
                }
                codesetid = documentParamXML.getValue(DocumentParamXML.FILESET, "setid");
                codeitemid = documentParamXML.getValue(DocumentParamXML.FILESET, "fielditem");
            }

            while (rs_file.next()) {
                /*
                 * if(basetype.equalsIgnoreCase("1")){ if
                 * (!userview.isHaveResource(IResourceConstant.LAWRULE,
                 * rs_file.getString("file_id"))) continue; }
                 * if(basetype.equalsIgnoreCase("5")) {
                 */
                if (!"false".equals(law_file_priv.trim())) {
                    if (!userview.isHaveResource(IResourceConstant.LAWRULE_FILE, rs_file.getString("file_id")))
                        continue;
                }
                // }
                String digest = rs_file.getString("digest") != null && rs_file.getString("digest").length() > 0 ? rs_file.getString("digest") : "null";

                String str = "";
                String note = rs_file.getString("note_num") == null ? "" : rs_file.getString("note_num");
                str += rs_file.getString("name") + "`" + rs_file.getString("title") + "`" + rs_file.getString("type") + "`" + rs_file.getString("content_type") + "`" + rs_file.getString("valid")
                        + "`" + note + "`" + rs_file.getString("issue_org") + "`" + rs_file.getString("notes") + "`" + rs_file.getString("issue_date") + "`" + rs_file.getString("implement_date")
                        + "`" + rs_file.getString("valid_date") + "`" + rs_file.getString("ext") + "`" + rs_file.getInt("viewcount") + "`" + digest + "`" + rs_file.getInt("fileorder") + "`"
                        + rs_file.getString("originalext") + "`" + rs_file.getString("b0110") + "`" + rs_file.getString("keywords");

                Element recordElement = doc.createElement("record");
                String file_id = rs_file.getString("file_id");
                recordElement.setAttribute("file_id", file_id);
                recordElement.setAttribute("base_id", rs_file.getString("base_id"));
                recordElement.setAttribute("file_name", "file_" + rs_file.getString("file_id") + "_" + rs_file.getString("name"));
                recordElement.setAttribute("ext", rs_file.getString("ext"));
                recordElement.setAttribute("digest", rs_file.getString("digest"));
                recordElement.setAttribute("originalfile", "original_" + rs_file.getString("file_id") + "_" + rs_file.getString("title"));
                recordElement.setAttribute("originalext", rs_file.getString("originalext"));

                if ("5".equalsIgnoreCase(basetype) && codeitemid != null && !"".equalsIgnoreCase(codeitemid)) {
                    String unamestr = "";
                    FieldSet fs = DataDictionary.getFieldSetVo(codesetid);
                    if(fs != null && !"0".equals(fs.getUseflag())) {
                    	for (int i = 0; i < dblist.size(); i++) {
                    		String sql = "select a0100,max(i9999) i9999 from " + dblist.get(i) + codesetid + " where " + codeitemid.toUpperCase() + " = '" + file_id.toUpperCase() + "' group by a0100";
                    		
                    		dbS.open(con, sql);
                    		unamers = stmt1.executeQuery(sql);
                    		while (unamers.next()) {
                    			unamestr += dblist.get(i) + unamers.getString("a0100") + "`";
                    		}
                    	}
                    }
                    
                    if (unamestr.length() > 1)
                        unamestr = unamestr.substring(0, unamestr.length() - 1);
                    recordElement.setAttribute("relat", unamestr);
                }
                recordElement.appendChild(doc.createTextNode(str));
                fileElement.appendChild(recordElement);

                baseId_map.put(rs_file.getString("base_id"), new Boolean(true));
                fileId_map.put(rs_file.getString("file_id"), new Boolean(true));
                addZipFile(zipOut, "file_" + rs_file.getString("file_id") + "_" + rs_file.getString("name") + "." + rs_file.getString("ext"), rs_file, "fileid");
                addZipFile(zipOut, "original_" + rs_file.getString("file_id") + "_" + rs_file.getString("title") + "." + rs_file.getString("originalext"), rs_file, "originalfileid");
            }

            if ("root".equalsIgnoreCase(base_id)) {
                for (int i = 0; i < list.size(); i++) {
                    String id = (String) list.get(i);
                    findChildStructAndFile(con, id, zipOut, basetype);
                }
            } else {
                findChildStructAndFile(con, base_id, zipOut, basetype);
            }

            createDirElement(dirElement, basetype);
            createExtFile(extElement, zipOut);
            lawBaseElement.appendChild(dirElement);
            lawBaseElement.appendChild(fileElement);
            lawBaseElement.appendChild(extElement);
            doc.appendChild(lawBaseElement);

            ZipEntry entry = new ZipEntry("1.xml");
            zipOut.putNextEntry(entry);
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(zipOut));
            zipOut.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        } finally {
            baseId_map.clear();
            fileId_map.clear();
            
            try {
        		// 关闭Wallet
            	try {
            		// 关闭Wallet
            		dbS.close(con);
            	} catch (Exception e) {
            		e.printStackTrace();
            	}

        	} catch (Exception e) {
        		e.printStackTrace();
        	}

            PubFunc.closeResource(zipOut);
            PubFunc.closeResource(rs_file);
            PubFunc.closeResource(rs_struct);
            PubFunc.closeResource(dbrs);
            PubFunc.closeResource(unamers);
            PubFunc.closeResource(stmt);
            PubFunc.closeResource(stmt1);
            PubFunc.closeResource(con);
        }
    }

    /**
     * 找到当前规章目录结点的所有子结点，并把这些目录结点所包含的文件输出到输出流中。
     * 
     * @param con
     * @param base_id
     *            当前结点id
     * @param zipOut
     */
    public void findChildStructAndFile(Connection con, String base_id, ZipOutputStream zipOut, String basetype) {

        String nodeId = base_id;
        StringBuffer sb = new StringBuffer("select * from law_base_struct where up_base_id=? and base_id <> up_base_id");

        PreparedStatement ps = null;
        ResultSet rs_struct = null;
        ResultSet rs_file = null;
        ResultSet dbrs = null;
        ResultSet unamers = null;
        DbSecurityImpl dbS = new DbSecurityImpl();
        ContentDAO dao = new ContentDAO(con);
        try {
        	List values=new ArrayList();
        	values.add(nodeId);
        	rs_struct =dao.search(sb.toString(), values);
        	ArrayList base_list = new ArrayList();
            String codesetid = "";
            String codeitemid = "";
            ArrayList dblist = new ArrayList();
            DocumentParamXML documentParamXML = new DocumentParamXML(con);
            if ("5".equalsIgnoreCase(basetype)) {
                String dbsql = "select Pre from DBName";
                dbS.open(con, dbsql);
                dbrs = dao.search(dbsql);
                while (dbrs.next()) {
                    dblist.add(dbrs.getString("Pre"));
                }
                codesetid = documentParamXML.getValue(DocumentParamXML.FILESET, "setid");
                codeitemid = documentParamXML.getValue(DocumentParamXML.FILESET, "fielditem");
            }
            // base_list.add(base_id);
            while (rs_struct.next()) {
                nodeId = rs_struct.getString("base_id");
                base_list.add(nodeId);
            }
            TreeHandle treehandle = new TreeHandle(con);
            for (int i = 0; i < base_list.size(); i++) {
                nodeId = base_list.get(i).toString();
                ArrayList list = treehandle.getChildIdlist(nodeId, "law_base_struct", "up_base_id", "base_id");
                if ("1".equalsIgnoreCase(basetype)) {
                    if (!userview.isHaveResource(IResourceConstant.LAWRULE, nodeId))
                        continue;
                }
                if ("5".equalsIgnoreCase(basetype)) {
                    if (!userview.isHaveResource(IResourceConstant.DOCTYPE, nodeId))
                        continue;
                }
                if ("4".equalsIgnoreCase(basetype)) {
                    if (!userview.isHaveResource(IResourceConstant.KNOWTYPE, nodeId))
                        continue;
                }
                if (list != null && list.size() > 0)
                    baseId_map.put(nodeId, new Boolean(true));
                else
                    baseId_map.put(nodeId, new Boolean(false));
                for (int r = 0; r < list.size(); r++) {
                    baseId_map.put(list.get(r), new Boolean(false));
                    base_list.add(list.get(r));
                }
            }
            for (int r = 0; r < base_list.size(); r++) {
                nodeId = base_list.get(r).toString();
                String sql1="select * from law_base_file where base_id = '" + nodeId + "'";
                dbS.open(con, sql1);
                rs_file = dao.search(sql1);
                while (rs_file.next()) {
                    /*
                     * if(basetype.equalsIgnoreCase("1")) { if
                     * (!userview.isHaveResource(IResourceConstant.LAWRULE,
                     * rs_file.getString("file_id"))) continue; }
                     * if(basetype.equalsIgnoreCase("5")) {
                     */
                    if (!"false".equals(law_file_priv.trim())) {
                        if (!userview.isHaveResource(IResourceConstant.LAWRULE_FILE, rs_file.getString("file_id")))
                            continue;
                    }
                    // }

                    /*
                     * file_id,base_id,name,title,type" +
                     * "content_type,valid,note_num,issue_org,notes,issue_date,"
                     * + "implement_date,valid_date,ext,viewcount,digest
                     */
                    String digest = rs_file.getString("digest") != null && rs_file.getString("digest").length() > 0 ? rs_file.getString("digest") : "null";
                    String str = "";
                    String note = rs_file.getString("note_num") == null ? "" : rs_file.getString("note_num");
                    str += rs_file.getString("name") + "`" + rs_file.getString("title") + "`" + rs_file.getString("type") + "`" + rs_file.getString("content_type") + "`" + rs_file.getString("valid")
                            + "`" + note + "`" + rs_file.getString("issue_org") + "`" + rs_file.getString("notes") + "`" + rs_file.getString("issue_date") + "`" + rs_file.getString("implement_date")
                            + "`" + rs_file.getString("valid_date") + "`" + rs_file.getString("ext") + "`" + rs_file.getInt("viewcount") + "`" + digest + "`" + rs_file.getInt("fileorder") + "`"
                            + rs_file.getString("originalext") + "`" + rs_file.getString("b0110") + "`" + rs_file.getString("keywords");
                    Element recordElement = doc.createElement("record");
                    String file_id = rs_file.getString("file_id");
                    recordElement.setAttribute("file_id", file_id);
                    recordElement.setAttribute("base_id", rs_file.getString("base_id"));
                    recordElement.setAttribute("file_name", "file_" + rs_file.getString("file_id") + "_" + rs_file.getString("name"));
                    recordElement.setAttribute("ext", rs_file.getString("ext"));
                    recordElement.setAttribute("digest", rs_file.getString("digest"));
                    recordElement.setAttribute("originalfile", "original_" + rs_file.getString("file_id") + "_" + rs_file.getString("title"));
                    recordElement.setAttribute("originalext", rs_file.getString("originalext"));
                    if ("5".equalsIgnoreCase(basetype) && codeitemid != null && !"".equalsIgnoreCase(codeitemid)) {
                        String unamestr = "";
                        FieldSet fs = DataDictionary.getFieldSetVo(codesetid);
                        if(fs != null && !"0".equals(fs.getUseflag())) {
                        	for (int i = 0; i < dblist.size(); i++) {
                        		String sql = "select a0100,max(i9999) i9999 from " + dblist.get(i) + codesetid + " where " + codeitemid.toUpperCase() + " = '" + file_id.toUpperCase() + "' group by a0100";
                        		dbS.open(con, sql);
                        		unamers = dao.search(sql);
                        		while (unamers.next()) {
                        			unamestr += dblist.get(i) + unamers.getString("a0100") + "`";
                        		}
                        	}
                        }
                        
                        if (unamestr.length() > 1)
                            unamestr = unamestr.substring(0, unamestr.length() - 1);
                        recordElement.setAttribute("relat", unamestr);
                    }
                    recordElement.appendChild(doc.createTextNode(str));
                    fileElement.appendChild(recordElement);
                    fileId_map.put(rs_file.getString("file_id"), new Boolean(false));
                    addZipFile(zipOut, "file_" + rs_file.getString("file_id") + "_" + rs_file.getString("name") + "." + rs_file.getString("ext"), rs_file, "content");
                    addZipFile(zipOut, "file_" + rs_file.getString("file_id") + "_" + rs_file.getString("title") + "." + rs_file.getString("originalext"), rs_file, "originalfile");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
        		// 关闭Wallet
        		dbS.close(con);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
            PubFunc.closeResource(rs_file);
            PubFunc.closeResource(unamers);
            PubFunc.closeResource(rs_struct);
            PubFunc.closeResource(dbrs);
            PubFunc.closeResource(ps);
        }
    }

    /**
     * 建立目录
     * 
     * @param e
     */
    public void createDirElement(Element e, String basetype) {
        ResultSet rs = null;
        DbSecurityImpl dbS = new DbSecurityImpl();
        try {
        	ContentDAO dao = new ContentDAO(this.con);
            Set set = baseId_map.keySet();
            Iterator it = set.iterator();
            String base_id = "";
            while (it.hasNext()) {
                base_id = (String) it.next();
                String sql="select * from law_base_struct where base_id='" + base_id + "' order by base_id";
                dbS.open(con, sql);
                rs = dao.search(sql);

                if (rs.next()) {
                    if ("1".equalsIgnoreCase(basetype)) {
                        if (!userview.isHaveResource(IResourceConstant.LAWRULE, rs.getString("base_id")))
                            continue;
                    }
                    if ("5".equalsIgnoreCase(basetype)) {
                        if (!userview.isHaveResource(IResourceConstant.DOCTYPE, rs.getString("base_id")))
                            continue;
                    }
                    if ("4".equalsIgnoreCase(basetype)) {
                        if (!userview.isHaveResource(IResourceConstant.KNOWTYPE, rs.getString("base_id")))
                            continue;
                    }

                    String key = base_id;
                    String str_key = "";
                    String str = "";
                    str += rs.getString("name") + "`" + rs.getString("description") + "`" + rs.getString("dir") + "`" + rs.getString("status") + "`" + rs.getString("basetype") + "`"
                            + rs.getString("DisplayOrder");
                    Boolean b = (Boolean) baseId_map.get(key);
                    if (b.booleanValue()) {
                        str += "`true";
                        str_key = "true";
                    } else {
                        str += "`false";
                        str_key = "false";
                    }
                    Element recordElement = doc.createElement("record");
                    recordElement.setAttribute("base_id", rs.getString("base_id"));
                    recordElement.setAttribute("up_base_id", rs.getString("up_base_id"));
                    recordElement.setAttribute("field_str", rs.getString("field_str"));
                    recordElement.setAttribute("key", str_key);
                    recordElement.appendChild(doc.createTextNode(str));
                    e.appendChild(recordElement);
                }

            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
            	
            	try {
            		// 关闭Wallet
            		dbS.close(con);
            	} catch (Exception e2) {
            		e2.printStackTrace();
            	}
                if (rs != null)
                    rs.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        }
    }

    /**
     * 建立附件
     * 
     * @param e
     * @param zipOut
     */
    public void createExtFile(Element e, ZipOutputStream zipOut) {
        ResultSet rs = null;
        
        DbSecurityImpl dbS = new DbSecurityImpl();
        try {
        	ContentDAO dao = new ContentDAO(this.con);
            Set set = fileId_map.keySet();
            Iterator it = set.iterator();
            String file_id = "";

            while (it.hasNext()) {
                file_id = (String) it.next();
                String sql="select * from law_ext_file where file_id=" + "'" + file_id + "'";
                dbS.open(con, sql);
                rs = dao.search(sql);

                while (rs.next()) {
                    String create_user = rs.getString("create_user") != null && rs.getString("create_user").length() > 0 ? rs.getString("create_user") : "null";
                    String str = "";
                    str += rs.getInt("version") + "`" + rs.getString("name") + "`" + rs.getString("ext") + "`" + rs.getString("create_time") + "`" + create_user;
                    Element recordElement = doc.createElement("record");
                    recordElement.setAttribute("ext_file_id", rs.getString("ext_file_id"));
                    recordElement.setAttribute("file_id", rs.getString("file_id"));
                    recordElement.setAttribute("file_name", "ext_" + rs.getString("ext_file_id") + "_" + rs.getString("name"));
                    recordElement.setAttribute("ext", rs.getString("ext"));
                    recordElement.setAttribute("name", rs.getString("name"));
                    recordElement.appendChild(doc.createTextNode(str));
                    e.appendChild(recordElement);
                    addZipFile(zipOut, "ext_" + rs.getString("ext_file_id") + "_" + rs.getString("name") + "." + rs.getString("ext"), rs, "content");
                }

            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
        	try {
        		// 关闭Wallet
        		dbS.close(con);
        	} catch (Exception e4) {
        		e4.printStackTrace();
        	}

            PubFunc.closeResource(rs);
        }
    }

    public Vector selectAllParentList(String tableName, String parentFieldName, String childFieldName, String nodeId, String baseTermValue, Connection conn) {
        Vector vct = new Vector();
        StringBuffer sb = new StringBuffer("select " + childFieldName + "," + parentFieldName);
        sb.append(" from " + tableName + " where " + childFieldName + "=?");
       // PreparedStatement ps = null;
        boolean flg = false;// 标志baseTerm是否是默认操作
        if (baseTermValue == null || "".equals(baseTermValue.trim())) {
            flg = true;
        }
        ResultSet rs = null;
        ContentDAO dao = new ContentDAO(conn);
        try {
        	List values=new ArrayList();
        	values.add(nodeId);
        	rs=dao.search(sb.toString(), values);
            if (rs.next())
                nodeId = rs.getString(parentFieldName);
            while (true) {
            	List value=new ArrayList();
            	value.add(nodeId);
            	rs=dao.search(sb.toString(), value);
                if (!rs.next())
                    break;
                vct.add(rs.getString(childFieldName));
                nodeId = rs.getString(parentFieldName);
                if (flg) {
                    if (nodeId.trim().equals(rs.getString(childFieldName).trim())) {
                        break;
                    }
                } else {
                    if (nodeId.trim().equals(baseTermValue.trim())) {
                        break;
                    }
                }
                rs.close(); // chenmengqing added at 20061010
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                //ps.close();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        return vct;
    }

    public void init() throws ServletException {
    }

}