package com.hjsj.hrms.businessobject.sys.sysout;

import com.hjsj.hrms.utils.PubFunc;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * 查找xml（不适用大文件）
 * @author wangzj
 *使用方法，获取oa.xml文件内容：
 *XmlResourceBundleFactory.XMLResourceBundle bundle = (XmlResourceBundleFactory.XMLResourceBundle)XmlResourceBundleFactory.getBundle("oa");
 *System.out.println(bundle.getXMLContent());
 */
public class XmlResourceBundleFactory {

    private static XMLResourceBundleControl DEFAULT_CONTROL = new XMLResourceBundleControl(null,true);
    private final static String             POINT_REGEX     = "\\.";

    /**
     * 通过默认的XMLResourceBundleControl获取资源Bundle
     * 
     * <pre>
     * 默认的XMLResourceBundleControl实现，就在classpath的跟目录下查找资源文件。
     * </pre>
     * 
     * @param fileName
     * @return
     */
    public static ResourceBundle getBundle(String fileName) {
        return null;//ResourceBundle.getBundle(fileName, DEFAULT_CONTROL);
    }

    /**
     * 通过指定path的XMLResourceBundleControl获取资源Bundle
     * 
     * <pre>
     * 指定path的XMLResourceBundleControl实现，会在指定的classpath目录下查找资源文件。
     * </pre>
     * 
     * @param fileName
     * @param classpath
     * @return
     */
    public static ResourceBundle getBundle(String classpath, String fileName) {
        XMLResourceBundleControl xmlResourceBundleControl = new XMLResourceBundleControl(classpath,true);
        return null;//ResourceBundle.getBundle(fileName, xmlResourceBundleControl);
    }

    public static class XMLResourceBundleControl{// extends ResourceBundle.Control {

        private String  resourcePath;    // 資源的classpath路径

        private boolean seprateDotPrefix; // 用于标识是否把包含点的baseName前缀过滤掉。

        XMLResourceBundleControl(String resourcePath, boolean seprateDotPrefix){
            this.resourcePath = resourcePath;
            this.seprateDotPrefix = seprateDotPrefix;
        }

        public List getFormats(String baseName) {
            return Collections.singletonList("xml");
        }

        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
                                        boolean reload) throws IllegalAccessException, InstantiationException,
                                                       IOException {

            if ((baseName == null) || (locale == null) || (format == null) || (loader == null)) {
                throw new NullPointerException();
            }
            ResourceBundle bundle = null;
            if (!"xml".equals(format)) {
                return null;
            }

            if (seprateDotPrefix) {
                if (baseName.indexOf(".") > 0) {
                    String array[] = baseName.split(POINT_REGEX);
                    baseName = array[array.length - 1];
                }
            }
            String bundleName = null;//toBundleName(baseName, locale);
            String resourceName = null;//toResourceName(bundleName, format);
            String resourceFullName = resourceName;
            if (resourcePath != null && !"".equals(resourcePath.trim())) {// baseName可能包含包命前綴，把包命前綴去除
                resourceFullName = resourcePath + "/" + resourceName;
            }
            URL url = loader.getResource(resourceFullName);
            if (url == null) {
                return null;
            }
            URLConnection connection = url.openConnection();
            if (connection == null) {
                return null;
            }
            if (reload) {
                connection.setUseCaches(false);
            }
            InputStream stream = null;
            try{
                 stream = connection.getInputStream();
                if (stream == null) {
                    return null;
                }
                BufferedInputStream bis = new BufferedInputStream(stream);
                bundle = new XMLResourceBundle(bis);
                bis.close();
            }
            finally{
                PubFunc.closeResource(stream);   
            }
            return bundle;
        }
    }

    public static class XMLResourceBundle extends ResourceBundle {

        private String xmlContent;

        XMLResourceBundle(InputStream in) throws IOException{
        	StringBuffer buff = new StringBuffer();
        	InputStreamReader reader = new InputStreamReader(in);
        	BufferedReader bufR = new BufferedReader(reader);
        	
        	String str = null;
        	while ((str = bufR.readLine()) != null) {
        		buff.append(str);
        	}
        	
        	xmlContent = buff.toString();
        }


		/*
		 * 获取xml内容
		 */
        public String getXMLContent() {

            return xmlContent;
        }



		@Override
        protected Object handleGetObject(String key) {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
        public Enumeration getKeys() {
			// TODO Auto-generated method stub
			return null;
		}
    }

}
