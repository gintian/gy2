package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * 上会材料-导入数据-浏览导入数据
 * <p>Title: ImportTemplateTrans </p>
 * <p>create time  2016-5-25 下午05:03:15</p>
 * @author linbz
 */

public class ImportTemplateTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {

        ArrayList<String> importMsgList = new ArrayList<String>();//存放返回信息
        InputStream in = null;
        Workbook wb = null;
        try{
            //每次导入前先把提示消息清空
            this.getFormHM().put("importMsg", "");
            
            
            String fileName = (String) this.getFormHM().get("filename");//加密后的文件名
            fileName = PubFunc.decrypt(fileName);
            String path = (String) this.getFormHM().get("path");//路径
            path = PubFunc.decrypt(path);
            String filePath = path + fileName;
            
            File file = new File(filePath);
            
            
            in = new FileInputStream(file);
            wb = WorkbookFactory.create(in);// 创建excel
            
            ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
            importMsgList = reviewFileBo.importTemplate(wb);
            String meetting_name = (String)this.getFormHM().get("w0301");
            String[] temp = meetting_name.split("_");
            if(temp.length>0 && "w0301".equals(temp[0])){
        	    String w0301 = PubFunc.decrypt(temp[1]);
        	    reviewFileBo.asyncPersonNum(w0301);//同步人数
        	    reviewFileBo.asyncStatus(w0301);// 同步是否通过
            }
         } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("importMsg", importMsgList);  
            PubFunc.closeIoResource(in);
            PubFunc.closeIoResource(wb);
        } 
    }
    
}
