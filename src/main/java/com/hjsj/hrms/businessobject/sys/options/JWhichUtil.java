package com.hjsj.hrms.businessobject.sys.options;

public class JWhichUtil {

	 /**   
	  *   根据当前的classpath设置，   
	  *   显示出包含指定类的类文件所在   
	  *   位置的绝对路径   
	  *   
	  *   @param   sResourceName   <加载文件的名字>   
	  */   
	public static String getResourceFilePath(String sResourceName)
    {
        if (!sResourceName.startsWith("/"))
        {
            sResourceName = "/" + sResourceName;
        }

        java.net.URL classUrl = JWhichUtil.class.getResource(sResourceName);

        if (classUrl == null)
        {
            /*System.out.println("\nResource '" + sResourceName + "' not found in \n'" 
                            + System.getProperty("java.class.path") + "'");*/
            
            return null;
        }
        else
        {
            //System.out.println("\nResource '" + sResourceName + "' found in \n'" + classUrl.getFile() + "'");
            return classUrl.getFile();
        }

    }


  

}
