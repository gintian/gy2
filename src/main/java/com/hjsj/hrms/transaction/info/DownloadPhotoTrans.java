package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DownloadPhotoTrans extends IBusiness {

	private String tmptable;
	private String lawDir;
	static final int BUFFER = 2048;

    public void execute() throws GeneralException {
        System.gc();
        String userbase = (String) this.getFormHM().get("userbase");
        String where_n = (String) this.getFormHM().get("where_n");
        where_n = SafeCode.decode(where_n);
        String formula = (String) this.getFormHM().get("formula");
        formula = formula.replaceAll("``#", "+");
        int notphotoname = 0;// 没有下载的无名人数
        int notext = 0;// 没有下载的无附件人数
        int pNumber = 0;// 总人数
        FileOutputStream zipFileStream = null;
        try {
            String colums = this.getYksjParserSql(formula, userbase);
            ContentDAO dao = new ContentDAO(this.frameconn);
            StringBuffer sb = new StringBuffer();
            sb.append("select t2.ext,t2.Ole,(select case when  " + Sql_switcher.trim(colums)
                    + "= '' then (select A0101 from UsrA01 where a0100=" + tmptable.toLowerCase() + ".a0100) else "
                    + colums + " end");
            sb.append(" from " + tmptable + " " + tmptable.toLowerCase() + " where " + tmptable.toLowerCase()
                    + ".a0100=t2.a0100)  photoname ");
            sb.append(" from " + userbase + "A00 t2 where t2.a0100 in (select a0100 from " + userbase + "A01 " + where_n
                    + ") and t2.flag='P'");
            this.frowset = dao.search(sb.toString());
            String photodir = this.getPhtotoDir();
            String zipFileName = photodir + ".zip"; // 打包后文件名字
            File oldfile = new File(zipFileName);
            if (oldfile.exists())
                oldfile.delete();
            
            zipFileStream = new FileOutputStream(zipFileName);
            Charset gbk = Charset.forName("gbk");
            //解决压缩包中，照片的文件名汉字乱码的问题，给压缩包设置编码格式的方法，需要jdk1.7+
//            ZipOutputStream out = new ZipOutputStream(zipFileStream, gbk);
            try(ZipOutputStream out = new ZipOutputStream(zipFileStream)) {
                ArrayList list = new ArrayList();
                boolean hasData = false;// tiany add 添加hasData标记 记录是否有照片
                while (this.frowset.next()) {
                    if (!hasData)
                        hasData = true;

                    String photoname = this.frowset.getString("photoname");
                    InputStream in = this.frowset.getBinaryStream("Ole");
                    String ext = frowset.getString("ext");
                    pNumber++;
                    if (photoname == null || photoname.length() == 0) {
                        notphotoname++;
                        continue;
                    }

                    if (ext == null || ext.length() == 0) {
                        notext++;
                        continue;
                    }

                    if (list.contains(photoname)) {
                        for (int i = 1; i < 100; i++) {
                            String newphotoname = photoname + "(" + i + ")";
                            if (!list.contains(newphotoname)) {
                                photoname = newphotoname;
                                break;
                            }
                        }
                    }

                    list.add(photoname);
                    addZipFile(out, photoname + ext, in);
                }
                this.getFormHM().put("hasData", hasData + ""); // tiany add 添加hasData标记 记录是否有照片
            }
            // zip(photodir);
            // deletetempphotos(photodir);
            lawDir = PubFunc.encrypt(lawDir + ".zip");
            this.getFormHM().put("outName", lawDir);
            this.getFormHM().put("notphotoname", String.valueOf(notphotoname));
            this.getFormHM().put("notext", String.valueOf(notext));
            this.getFormHM().put("pNumber", String.valueOf(pNumber));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(zipFileStream);
        }
    }
	
    private void addZipFile(ZipOutputStream zipOut, String fileName, InputStream fileInputStream) {
        try {
            if (fileInputStream == null) { return; }
            byte data[] = new byte[BUFFER];
            BufferedInputStream origin = null;
            org.apache.tools.zip.ZipEntry entry = new org.apache.tools.zip.ZipEntry(fileName);
            zipOut.putNextEntry(entry);
            origin = new BufferedInputStream(fileInputStream, BUFFER);
            int count = 0;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                zipOut.write(data, 0, count);
            }
            // zipOut.closeEntry();
            origin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private  void deletetempphotos(String lawbase){
		File tempLawDir = new File(lawbase);
        if(tempLawDir.exists()){
        	File[] files = tempLawDir.listFiles();
        	for(int i=0;i<files.length;i++){
        			files[i].delete();
        	}
        	tempLawDir.delete();
        }
	}
	
    private String getPhtotoDir() {
        String tempDirName = System.getProperty("java.io.tmpdir");
        lawDir =this.userView.getUserName()+ "_photos"  ;
        String lawbase = tempDirName + File.separator + lawDir;
        try {
            if (tempDirName == null) 
                throw new RuntimeException("Temporary directory system property (java.io.tmpdir) is null."); 
            
            File tempDir = new File(tempDirName);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lawbase;
    }
	
    private String getYksjParserSql(String sformula, String userbase) throws GeneralException {
        String sql = "";
        try {
            YksjParser yp = new YksjParser(userView, DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET),
                    YksjParser.forSearch, YksjParser.STRVALUE, YksjParser.forPerson, "Ht", userbase);
            yp.setCon(this.frameconn);
            yp.run(sformula);
            sql = yp.getSQL();
            tmptable = yp.getTempTableName();
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sql;
    }
	
    public void zip(String inputFileName) throws Exception {
        String zipFileName = inputFileName + ".zip"; // 打包后文件名字
        File oldfile = new File(zipFileName);
        if (oldfile.exists())
            oldfile.delete();

        zip(zipFileName, new File(inputFileName));
    }

    private void zip(String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        try {
            zip(out, inputFile, "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    private void zip(ZipOutputStream out, File f, String base) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
            FileInputStream in = null;
            try {
                in = new FileInputStream(f);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                PubFunc.closeIoResource(in);
            }
        }
    }

}
