package com.hjsj.hrms.module.vuesupport.component.fileupload;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.VFSUtil;
import com.hrms.virtualfilesystem.VfsParam;

import java.util.HashMap;

/***
 * 获取文件配置参数
 * @author ZhangHua
 * @date 16:46 2020/6/23
 */

public class GetFileSettingsTrans extends IBusiness {
    /**
     * 所有的交易的子类须实现的方法
     *
     * @throws Exception
     */
    @Override
    public void execute() throws GeneralException {
        try {

            VfsParam vfsParam = VFSUtil.getParam();
            HashMap map = new HashMap();
            map.put("multimedia", vfsParam.getFilesize_multimedia());
            map.put("doc", vfsParam.getFilesize_doc());
            map.put("videostreams", vfsParam.getFilesize_videostreams());
            map.put("other", "0".equals(vfsParam.getFilesize_other()) ? vfsParam.getFilesize_doc() : vfsParam.getFilesize_other());
            this.getFormHM().put("param", map);
        } catch (Exception e) {
            e.printStackTrace();
            this.getFormHM().put("msg", e.getMessage());
        }

    }
}
