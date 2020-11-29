package com.hrms.hjsj.syssqllog;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 根据文件名和后缀找出符合条件的文件
 * @author guodd
 * createtime 2016-09-08
 *
 */
public class LogFileFilter implements FilenameFilter{
    String preName;
    String ext;
    public LogFileFilter(String preName,String ext){
        this.preName = preName;
        this.ext = ext;
    }

    @Override
    public boolean accept(File dir, String name) {
        if(name.indexOf(this.preName)!=-1 && name.endsWith(this.ext) && !name.equals(this.preName+this.ext))
            return true;
        return false;
    }
}
