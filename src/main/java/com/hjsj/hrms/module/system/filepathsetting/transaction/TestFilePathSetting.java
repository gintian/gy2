package com.hjsj.hrms.module.system.filepathsetting.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.manager.VfsManagerFactory;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author zhangh
 */
public class TestFilePathSetting extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        JSONObject returnStr = new JSONObject();
        boolean flag = false;
        InputStream inputStream = null;
        try {
            String username = "test ";
            VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
            VfsModulesEnum vfsModulesEnum = VfsModulesEnum.MB;
            VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
            String CategoryGuidKey = "guidkey";
            inputStream = new ByteArrayInputStream("测试内容".getBytes());
            String fileName = "test.txt";
            //文件扩展标识统一限制为要么为空，要么长度不得小于6位
            String filetag = "";
            boolean isTempFile = true;
            //zhangh 2020-2-18 在测试连接之前，需要销毁掉原来的实例
            VfsManagerFactory.destroyManager();
            String fileId = VfsService.addFile(username,vfsFiletypeEnum,vfsModulesEnum,vfsCategoryEnum,CategoryGuidKey,inputStream,fileName,filetag,isTempFile);
            if(VfsService.deleteFile("test",fileId)){
                flag = true;
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }finally{
            PubFunc.closeIoResource(inputStream);
        }
        returnStr.put("return_code", flag ==true ?"success" :"fail");
        this.getFormHM().put("returnStr", returnStr);

    }

}
