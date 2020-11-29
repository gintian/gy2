package com.hjsj.hrms.utils.sys;

import com.hrms.virtualfilesystem.service.VfsService;

public class VfsJob implements CrontabJob{

    /**
     * 默认执行的方法
     */
    @Override
    public void executeJobs() {
        //调用VFS清理掉需要删除的临时文件
        try {
            VfsService.clearTempFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
