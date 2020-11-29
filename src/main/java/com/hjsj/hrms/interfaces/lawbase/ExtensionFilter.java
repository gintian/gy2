package com.hjsj.hrms.interfaces.lawbase;

import java.io.File;
import java.io.FilenameFilter;

/**
 * <p>Title:ExtensionFilter</p>
 * <p>Description:取得一个目录下指定扩展名的文件</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 1, 2005:11:54:05 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class ExtensionFilter implements FilenameFilter {

	private String extension;    
    /**
     * 
     */
    public ExtensionFilter(String ext) {
		extension = "." + ext;
    }

    /* 
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File dir, String name) {
		return (
				name.endsWith(extension) || name.endsWith(extension.toLowerCase()));

    }

}
