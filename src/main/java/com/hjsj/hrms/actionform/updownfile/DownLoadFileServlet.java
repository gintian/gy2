/*
 * Created on 2005-5-31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.updownfile;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DownLoadFileServlet extends HttpServlet {

    public DownLoadFileServlet() {
    }

    @Override
    public void doGet(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
            throws ServletException, IOException {
        doPost(httpservletrequest, httpservletresponse);
    }

    @Override
    public void doPost(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
            throws ServletException, IOException {
        Connection connection = null;
        ResultSet resultset = null;
        InputStream inputstream = null;
        FileOutputStream fileoutputstream = null;
        ServletOutputStream servletoutputstream = null;
        ServletOutputStream servletoutputstream1 = null;
        FileInputStream fileinputstream = null;
        File file = null;
        DataInputStream datainputstream = null;


        try {


            String s;
            String s1;
            String s2;
            String s3;
            String s4;
            s = httpservletrequest.getParameter("id");
            connection = (Connection) AdminDb.getConnection();
        	/*
        	s1 = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
        	s2 = "jdbc:microsoft:sqlserver://127.0.0.1:1433;DatabaseName=ykchr";
        	s3 = "sa";
        	s4 = "";
        	
        	String s5 = "";
        	*/
            String lname;
        	
        	
        	/*
        	Class.forName(s1);
        	
       	 	connection = DriverManager.getConnection(s2, s3, s4);
       	 	
       	 	*/

            String sql = "select * from resource_list where contentid=" + s;
            ContentDAO dao = new ContentDAO(connection);
            resultset = dao.search(sql);

            Object obj = null;
            Object obj1 = null;
            inputstream = null;
            servletoutputstream = httpservletresponse.getOutputStream();
            if (resultset.next()) {
                lname = resultset.getString("description");
                int i = lname.length();
                int j = lname.lastIndexOf(".");
                String ex = lname.substring(j + 1, i);
                System.out.println("the file extend Filename  " + ex);
                inputstream = resultset.getBinaryStream("content");
            } else {
                return;
            }

            file = new File(httpservletrequest.getRealPath("") + "/" + lname);
            fileoutputstream = new FileOutputStream(file);
            System.out.println("realpath is " + httpservletrequest.getRealPath(""));
            long length = 0L;
            int k;

            //取得文件长度
            while ((k = inputstream.read()) != -1)
                fileoutputstream.write(k);

            inputstream.close();
            length = file.length();

            System.out.println("secont file length " + length);


            //下载操作
            fileinputstream = new FileInputStream(file);

            httpservletresponse.setHeader("Content-disposition", "attachment;filename=" + lname);
            httpservletresponse.setContentType("multipart/form-data");
            String fileSize = Long.toString(length);
            httpservletresponse.setHeader("Content-Length", fileSize);
            datainputstream = null;
            if (fileinputstream != null)
                datainputstream = new DataInputStream(fileinputstream);
            byte abyte0[] = new byte[1024];


            servletoutputstream1 = httpservletresponse.getOutputStream();
            for (int i1 = 0; (long) i1 < length; ) {
                i1 += 1024;
                if ((long) i1 > length) {
                    byte abyte1[] = new byte[1024 - (int) ((long) i1 - length)];
                    datainputstream.readFully(abyte1);
                    servletoutputstream1.write(abyte1);
                } else {
                    datainputstream.readFully(abyte0);
                    servletoutputstream1.write(abyte0);
                }
            }

        } catch (IOException ex) {
            System.out.println("down io error");
        } catch (SQLException ex) {
            System.out.println("down connection error");
        } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println("downfilereg(): " + exception.getMessage());
        } finally {
            try {
                if (fileoutputstream != null) {
                    fileoutputstream.close();
                }
                if (fileinputstream != null)
                    fileinputstream.close();
                if (inputstream != null)
                    inputstream.close();
                if (datainputstream != null)
                    datainputstream.close();
                if (servletoutputstream != null)
                    servletoutputstream.flush();
                if (servletoutputstream != null)
                    servletoutputstream.close();
                if (servletoutputstream1 != null)
                    servletoutputstream1.close();
                if (resultset != null)
                    resultset.close();
                if (connection != null)
                    connection.close();
                if (file.exists())
                    file.delete();
            } catch (Exception ex) {

            }
        }
        return;
    }
}
