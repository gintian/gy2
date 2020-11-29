package com.hjsj.hrms.businessobject.sys.bos;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class XmlResourceUtil {

    public static Document readXmlDocument(String absolutePath,String relativePath) throws GeneralException {
        Document doc = null;
        InputStream in = null;
        JarFile jf = null;
        try
        {
            /**cmq added for jboss eap6 at 20121019*/
            String webserver= SystemConfig.getProperty("webserver");
            if("jboss".equalsIgnoreCase(webserver)|| "inforsuite".equalsIgnoreCase(webserver))
            {
                in=XmlResourceUtil.class.getResourceAsStream(relativePath);
            }
            else
            {
                if(absolutePath.indexOf("hrpweb3.jar")!=-1)
                {
                    jf = new JarFile(absolutePath);
                    Enumeration es = jf.entries();
                    while (es.hasMoreElements()) {
                        JarEntry je = (JarEntry) es.nextElement();
                        if (je.getName().equals(relativePath)) {
                            in = jf.getInputStream(je);
                            break;
                        }

                    }
                }
            }
            // 把文件写入seesion里
            if(in==null){
                in = new FileInputStream(absolutePath);
            }
            if(in==null) {
                throw new GeneralException("找不到"+relativePath+"文件!");
            }
            doc = PubFunc.generateDom(in);
        }  catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally{
            if(in != null) {
                PubFunc.closeIoResource(in);
            }
            PubFunc.closeIoResource(jf);
        }
        return doc;
    }

    public static void writeXmlDocument(String absolutePath,String relativePath,Document doc) throws JDOMException, GeneralException, IOException {
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        outputter.setFormat(format);
        String xml = outputter.outputString(doc);
        FileOutputStream fileOut=null;
        try {
            byte[] data = xml.getBytes("UTF-8");
            if(absolutePath.indexOf("hrpweb3.jar")!=-1){
                editJar(absolutePath, relativePath, data);
            }else{
                fileOut = new FileOutputStream(absolutePath);
                fileOut.write(data);
                fileOut.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeResource(fileOut);
        }
    }

    private static void editJar(String file, String EntryName, byte[] data) {
        InputStream is = null;
        JarOutputStream out =null;
        JarFile jf = null;
        try {

            jf = new JarFile(file);
            TreeMap tm = new TreeMap();
            Enumeration es = jf.entries();
            while (es.hasMoreElements()) {
                JarEntry je = (JarEntry) es.nextElement();
                is = jf.getInputStream(je);
                byte[] b = getByte(is);
                tm.put(je.getName(), b);
            }
            out = new JarOutputStream(
                    new FileOutputStream(file));
            Set set = tm.entrySet();
            Iterator it = set.iterator();
            // boolean has = false;
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                String name = (String) me.getKey();
                JarEntry jeNew = new JarEntry(name);
                out.putNextEntry(jeNew);
                byte[] b;

                if (name.equals(EntryName)) {
                    //System.out.println(name);
                    b = data;
                    // has = true;

                } else {
                    b = (byte[]) me.getValue();
                }
                out.write(b, 0, b.length);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeIoResource(is);
            PubFunc.closeIoResource(out);
            PubFunc.closeResource(jf);
        }
    }

    private static byte[] getByte(java.io.InputStream s) {
        byte[] buffer = new byte[0];
        byte[] chunk = new byte[4096];
        int count;
        try {
            while ((count = s.read(chunk)) >= 0) {
                byte[] t = new byte[buffer.length + count];
                System.arraycopy(buffer, 0, t, 0, buffer.length);
                System.arraycopy(chunk, 0, t, buffer.length, count);
                buffer = t;
            }
            s.close();
        } catch (Exception e) {
        }
        return buffer;
    }

    /**
     * 获得绝对路径
     */
    public static String getResourcePath(String resourcePath) {

        String classPath = "";
        try
        {
            classPath = XmlResourceUtil.class.getResource("").toString();
            classPath=java.net.URLDecoder.decode(classPath,"utf-8");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        if (classPath.indexOf("hrpweb3.jar") != -1) {
            int beginIndex=-1,endIndex=-1;
            /**weblogic,环境布署时*/
            if(classPath.startsWith("zip:"))
            {
                beginIndex = classPath.indexOf("zip:") + 4;
                endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
                classPath = classPath.substring(beginIndex, endIndex);
            }
            else
            {
                Properties props=System.getProperties(); //系统属性
                String sysname = props.getProperty("os.name");

                if(sysname.startsWith("Win")){
                    beginIndex = classPath.indexOf("/") + 1;
                    endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
                    classPath = classPath.substring(beginIndex, endIndex);
                }else{
                    beginIndex = classPath.indexOf("/") ;
                    endIndex = classPath.lastIndexOf("hrpweb3.jar") + 11;
                    classPath = classPath.substring(beginIndex, endIndex);
                }
            }
        } else {
            Properties props=System.getProperties(); //系统属性
            String sysname = props.getProperty("os.name");
            //zhaoxj 20150514 开发环境下，windows平台需要去掉路径开头的“/”，其它如OSX，linux等不能去掉
            int beginIndex = classPath.indexOf("/");
            if(sysname.startsWith("Win")){
                beginIndex++;
            }
            if(classPath.indexOf("businessobject")!=-1){
                int endIndex = classPath.lastIndexOf("businessobject")-1;
                classPath = classPath.substring(beginIndex, endIndex)+resourcePath;
            }
        }
        return classPath;
    }

    public static InputStream getInputStreamFromjar(String absolutePath,String relativePath){
        InputStream in =null;
        JarFile jf = null;
        try{
            jf = new JarFile(absolutePath);
            Enumeration es = jf.entries();
            while(es.hasMoreElements()){
                JarEntry je = (JarEntry)es.nextElement();
                if(je.getName().equals(relativePath)){
                    in =jf.getInputStream(je);
                    break;
                }
            }
        }catch(Exception e){}
        finally{
            PubFunc.closeIoResource(jf);
        }
        return in;
    }
}
