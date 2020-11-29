package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommonBusiness {
    private Connection con;
    private String old_wed_all="ALL";
    public CommonBusiness(Connection con) {
        super();
        this.con = con;

    }

    /**
     * 
     * @param str
     *            源字符串
     * @param delstr
     *            要删除的字符串列表
     * @return 删除delstr后的str字符串
     */
    public static String deleteString(String str, String[] delstr) {
        String str1 = str;
        for (int i = 0; i < delstr.length; i++) {
            str1 = str1.replaceAll(delstr[i], " ");
        }
        return str1;
    }

    /**
     * 
     * @param is
     *            excel格式输入流
     * @return 返回文本格式输入流
     */
    public static InputStream excelToText(InputStream is) {
        StringBuffer sb = new StringBuffer("");
        try( Workbook workbookr=WorkbookFactory.create(is)) {
            //HSSFWorkbook workbookr = new HSSFWorkbook(is);

            for (int i = 0; i < workbookr.getNumberOfSheets(); i++) {
                Sheet sheet0 = workbookr.getSheetAt(i);
                Iterator rows = sheet0.rowIterator();
                while (rows.hasNext()) {
                    Row hssfrow = (Row) rows.next();
                    int count = hssfrow.getPhysicalNumberOfCells();
                    for (short j = 0; j < count; j++) {
                        Cell cell = (Cell) hssfrow.getCell(j);
                        if(cell==null)
                            continue;
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                            sb.append(cell.getStringCellValue());
                        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
                            sb.append(String
                                    .valueOf(cell.getNumericCellValue()));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    public static void excelHighLight(InputStream is, List hightWords,
            OutputStream outputStream) {
        try(Workbook workbookr = WorkbookFactory.create(is)) {

            for (int i = 0; i < workbookr.getNumberOfSheets(); i++) {
                Sheet sheet0 = workbookr.getSheetAt(i);
                Iterator rows = sheet0.rowIterator();
                while (rows.hasNext()) {
                    Row hssfrow = (Row) rows.next();
                    int count = hssfrow.getPhysicalNumberOfCells();
                    for (short j = 0; j < count; j++) {
                        Cell cell = (Cell) hssfrow.getCell(j);
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            for (int k = 0; k < hightWords.size(); k++) {
                                if (cell.getStringCellValue().indexOf(
                                        (String) hightWords.get(k)) >= 0) {
                                    Font font = workbookr.createFont();
                                    font.setColor(Font.COLOR_RED);
                                    CellStyle cellStyle = workbookr
                                            .createCellStyle();
                                    cellStyle.setFont(font);
                                    cell.setCellStyle(cellStyle);
                                }
                            }
                        }
                    }
                }
            }
            workbookr.write(outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param is
     *            word格式输入流
     * @return 文本格式输入流
     */
    public static InputStream wordToText(InputStream is) {
        ByteArrayInputStream byte_input_stream = null;
        try {
            //saveToFile(is, "c:\\1.doc");
            //OPCPackage opcPackage = POIXMLDocument.openPackage(tmpfilepath);
            //POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
            HWPFDocument doc = new HWPFDocument(is);
            Range range = doc.getRange();
            //System.out.println("--->"+range.text().length());
            byte_input_stream = new ByteArrayInputStream(range.text().getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return byte_input_stream;
    }

    /**
     * 将内存流以文件的形式保存到硬盘上
     * 
     * @param str
     * @return
     */
    public static void saveToFile(InputStream is, String url) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(url);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        byte[] b = new byte[1024];
        try {
            while (is.read(b) > 0) {
                fos.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static boolean judgeNull(String str) {
        if (str == null || "".equals(str.trim()) || "null".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查找目录下包含子目录的所有文件
     * 
     * @author lzy
     * @param catalog
     *            目录
     * @param 要搜索组织机构条件
     * @return orgId 机构ID
     */
    public String findLawbaseFile(String catalog, String orgId, String dir,String basetype,UserView userView) {
        return findLawbaseFile(catalog, orgId, dir, basetype, userView, "");
    }
    /**
     * 查找目录下包含子目录的所有文件
     * 
     * @author lzy
     * @param catalog
     *            目录
     * @param 要搜索组织机构条件
     * @return orgId 机构ID
     */
    public String findLawbaseFile(String catalog, String orgId, String dir,String basetype,UserView userView,String itemStr) {
        StringBuffer sqlBuffer = null;
        TreeHandle treehandle = new TreeHandle(con);
        try {
            sqlBuffer = new StringBuffer(
                    //file_id,law_base_file.base_id,digest,content_type,law_base_file.name,title,type,valid,note_num,"
                    //      + "issue_org,notes,issue_date,implement_date,valid_date,ext, viewcount,originalext, b0110,keywords
                    "select *"
                            + " from law_base_file left join law_base_struct on law_base_file.base_id=law_base_struct.base_id ");
            if (!"".equals(itemStr) && itemStr != null) {
                itemStr.replace("`", ",");
                sqlBuffer.append("," + itemStr);
                sqlBuffer.deleteCharAt(sqlBuffer.length());
            }
            if (catalog == null || "".equals(catalog)
                    || "null".equals(catalog.trim())
                    || "root".equals(catalog.trim())) {
                String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
                if((userView.isSuper_admin()&&!userView.isBThreeUser())||!"false".equals(law_file_priv.trim())){
                    sqlBuffer
                        .append(" where (("+dir+") or UPPER(law_base_file.base_id)='"+this.old_wed_all.toUpperCase()+"') ");
                }else{
                    sqlBuffer.append(" where (1=2");
                    ContentDAO dao = new ContentDAO(con);
                    ArrayList base_ids=(ArrayList)this.getBaseids(basetype,dao,userView);
                    for(int i=0;i<base_ids.size();i++){
                        String _base_id=(String)base_ids.get(i);
                        sqlBuffer.append(" or law_base_file.base_id='" + _base_id + "'");
                        String temp = treehandle.selectAllParentStr("law_base_struct",
                                "up_base_id", "base_id", _base_id, null, false);
                        if (temp != null && !"".equals(temp)) {
                            temp=temp.replaceAll("base_id", "law_base_file.base_id");
                            sqlBuffer.append(" or " + temp);
                        }
                    }
                    sqlBuffer
                    .append(")");
                }
            } else {
                String temp = treehandle.selectAllParentStr("law_base_struct",
                        "up_base_id", "base_id", catalog, null, false);
                sqlBuffer.append(" where (law_base_file.base_id='" + catalog + "'");
                if (temp != null && !"".equals(temp)) {
                    temp=temp.replaceAll("base_id", "law_base_file.base_id");
                    sqlBuffer.append(" or " + temp);
                }
                sqlBuffer.append(" or UPPER(law_base_file.base_id)='"+this.old_wed_all.toUpperCase()+"' ");
                sqlBuffer.append(")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("------>"+sqlBuffer.toString());
        return sqlBuffer.toString();
    }
    /**
     * 查找目录下包含子目录的所有文件id
     * 
     * @author lzy
     * @param catalog
     *            目录
     * @param 要搜索组织机构条件
     * @return orgId 机构ID
     */
    public String findLawbaseFileId(String catalog, String orgId, String dir) {
        StringBuffer sqlBuffer = null;
        TreeHandle treehandle = new TreeHandle(con);
        try {
            sqlBuffer = new StringBuffer("select file_id"
                    + " from law_base_file");
            if (catalog == null || "".equals(catalog)
                    || "null".equals(catalog.trim())
                    || "root".equals(catalog.trim())) {
                sqlBuffer
                        .append(" where base_id in (select base_id from law_base_struct where "
                                + dir + ")");
            } else {
                String temp = treehandle.selectAllParentStr("law_base_struct",
                        "up_base_id", "base_id", catalog, null, false);
                sqlBuffer.append(" where base_id='" + catalog + "'");
                if (temp != null && !"".equals(temp)) {
                    sqlBuffer.append(" or " + temp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlBuffer.toString();
    }

    /**
     * 
     * @param catalog
     *            目录名
     * @return 是否有子结点
     */
    public String findChildNode(String catalog) {
        String flg = "true";
        Statement stmt = null;
        ResultSet rs = null;
        DbSecurityImpl dbS = new DbSecurityImpl();
        try {
            stmt = con.createStatement();
            if (catalog == null || "".equals(catalog.trim())) {
                String sql = "select base_id from law_base_struct where up_base_id = base_id";
                dbS.open(con, sql);
                rs = stmt.executeQuery(sql);
            } else {
                String sql = "select base_id from law_base_struct where up_base_id = '"
                        + catalog + "' and up_base_id <> base_id";
                dbS.open(con, sql);
                rs = stmt.executeQuery(sql);
            }
            if (rs.next()) {
                flg = "true";
            } else {
                flg = "false";
            }
            //rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭Wallet
                dbS.close(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PubFunc.closeResource(rs);
            PubFunc.closeResource(stmt);
        }
        return flg;
    }

    public synchronized static void addIndex(String file_id, FormFile file,String ext) {
        String path = "";
        IndexWriter writer = null;
        File f = null;
        Document doc = null;
        InputStream in = null;
        try {
            path =  LawDirectory.getLawbaseDir();
            // 索引默认路径
            writer = null;
            // 索引文件默认文件名segents
            if (path.charAt(path.length() - 1) != '\\') {
                path += "\\";
            }
            f = new File(path + "\\segments");
            // 如果文件存在追加索引如果文件不存在创建索引
            if (f.exists()) {
                writer = new IndexWriter(path, new ChineseAnalyzer(), false);
            } else {
                writer = new IndexWriter(path, new ChineseAnalyzer(), true);
            }
            doc = new Document();
            doc.add(Field.Keyword("id", file_id));
            doc.add(Field.Keyword("base_id", file_id));
            in = file.getInputStream();
            if ("doc".equals(ext.trim().toLowerCase())) {
                doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.wordToText(in))));
            }else if ("xls".equals(ext.trim().toLowerCase())|| "xlsx".equals(ext.trim().toLowerCase())) {
                doc.add(Field.Text("body", (Reader) new InputStreamReader(CommonBusiness.excelToText(in))));
            }else if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase())) {
                doc.add(Field.Text("body", (Reader) new InputStreamReader(in)));
            }else{
                doc.add(Field.Text("body", (Reader) new InputStreamReader(in)));
            }

            // 将文档写入索引
            writer.addDocument(doc);
            // 索引优化
            // writer.optimize();
            // 关闭写索引器
            
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {           
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(writer);
            PubFunc.closeResource(doc);         
            PubFunc.closeResource(in);
        }
    }

    public synchronized static void addIndex(String file_id, String content) {
        String path = "";
        IndexWriter writer = null;
        File f = null;
        Document doc = null;
        try {
            path =  LawDirectory.getLawbaseDir();
            // 索引默认路径
            writer = null;
            // 索引文件默认文件名segents
            if (path.charAt(path.length() - 1) != '\\') {
                path += "\\";
            }
            f = new File(path + "\\segments");
            // 如果文件存在追加索引如果文件不存在创建索引
            if (f.exists()) {
                writer = new IndexWriter(path, new ChineseAnalyzer(), false);
            } else {
                writer = new IndexWriter(path, new ChineseAnalyzer(), true);
            }
            doc = new Document();
            doc.add(Field.Keyword("id", file_id));
            doc.add(Field.Keyword("base_id", file_id));
            doc.add(Field.Text("body", content));

            // 将文档写入索引
            writer.addDocument(doc);
            // 索引优化
            // writer.optimize();
            // 关闭写索引器
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateViewCount(String fileid) {
        Statement stmt = null;
        DbSecurityImpl dbS = new DbSecurityImpl();
        try {
            String sql = "update law_base_file set viewcount = viewcount + 1 where file_id='"
                    + fileid + "'";
            stmt = con.createStatement();
            dbS.open(con, sql);
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                try {
                    // 关闭Wallet
                    dbS.close(con);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (stmt != null)
                    stmt.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    private ArrayList getBaseids(String basetype,ContentDAO dao,UserView userView) throws SQLException{
        ArrayList baseids = new ArrayList();
        ResultSet rs = null;
        try{
            String sql ="select base_id from law_base_struct where basetype=" + basetype;
            rs = dao.search(sql);
            while(rs.next()){
                String base_id = rs.getString("base_id");
                if("1".equalsIgnoreCase(basetype))
                {
                    if (!userView.isHaveResource(IResourceConstant.LAWRULE, base_id))
                        continue;
                }
                if("5".equalsIgnoreCase(basetype))
                {
                    if (!userView.isHaveResource(IResourceConstant.DOCTYPE, base_id))
                        continue;
                }
                if("4".equalsIgnoreCase(basetype))
                {
                    if (!userView.isHaveResource(IResourceConstant.KNOWTYPE, base_id))
                        continue;
                }
                baseids.add(base_id);
            }
        }finally{
            if(rs != null){
                rs.close();
            }
        }
        return baseids;
    }
	/**
     * 判断上传的文件是否是word、excle、txt、html
     * 
     * @param ext
     *            上传文件的后缀名
     * @return true 是 | false 不是
     */
    public static boolean checkExt(String ext) {
        boolean flag = false;
        if (ext == null || ext.trim().length() < 1)
            return false;

        if (ext.trim().startsWith("."))
            ext = ext.trim().substring(1);

        if ("doc".equals(ext.trim().toLowerCase()) || "docx".equals(ext.trim().toLowerCase()))
            flag = true;
        else if ("xls".equals(ext.trim().toLowerCase()) || "xlsx".equals(ext.trim().toLowerCase()))
            flag = true;
        else if ("txt".equals(ext.trim().toLowerCase()) || "html".equals(ext.trim().toLowerCase()) || "htm".equals(ext.trim().toLowerCase()))
            flag = true;

        return flag;
    }
}
