package com.hjsj.hrms.interfaces.lawbase;

import java.io.File;
import java.io.FileFilter;

/**
 * <p>Title:DirectionFilter</p>
 * <p>Description:取得一个目录下的子目录</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 1, 2005:11:52:04 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DirectionFilter implements FileFilter {

    /**
     * 
     */
    public DirectionFilter() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {
		return file.isDirectory();
    }

}
