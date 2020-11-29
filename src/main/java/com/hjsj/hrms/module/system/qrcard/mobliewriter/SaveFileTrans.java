/*
 * @(#)SaveFile.java 2018年6月21日上午11:16:01 ehr Copyright 2018 HJSOFT, Inc. All
 * rights reserved. HJSOFT PROPRIETARY/CONFIDENTIAL. Use is subject to license
 * terms.
 */
package com.hjsj.hrms.module.system.qrcard.mobliewriter;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 *
 * @Titile: SaveFileTrans
 * @Description:
 * @Company:hjsj
 * @Create time: 2018年6月21日上午11:16:01
 * @author: wangz
 * @version 1.0
 *
 */
public class SaveFileTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        cat.debug("进入到保存图片交易类");
        ArrayList fieldList = (ArrayList) this.formHM.get("photodata");
        boolean saveFlag = true;
        cat.debug("wz图片集合"+fieldList);
        for (int i = 0; i < fieldList.size(); i++) {//单元格的集合
            ArrayList fieldData = (ArrayList) fieldList.get(i);//单元格数据的集合
            if(fieldData == null) {
                continue;
            }
            for (int j = 0; j < fieldData.size(); j++) {
                MorphDynaBean mo = (MorphDynaBean) fieldData.get(j);
                String fileName = (String) mo.get("name");
                cat.debug("图片名称"+fileName);
                String fileData = ((String) mo.get("fileData"));
                String base64 = fileData.substring(fileData.indexOf("base64,")+7);
                String filetype = (String) mo.get("filetype");
                saveFlag = writeDataToDir(fileName, base64, filetype);
            }
        }
        this.getFormHM().put("saveFlag",saveFlag);
    }

    /**
    *将图片写入到临时目录中 
    * @param fileName 图片名称
    * @param fileData 图片base64编码
    * @param filetype 图片格式
    * @return
    */
    private boolean writeDataToDir(String fileName, String fileData, String filetype) {
        cat.debug("进入到存储图片方法");
        String tempdir = System.getProperty("java.io.tmpdir");//获得临时目录路径
        if(!tempdir.endsWith("\\")) {
            tempdir = tempdir+"\\";
        }
        cat.debug("临时目录路径"+tempdir);
        tempdir.replace("\\", File.separator).replace("/", File.separator);//解决linux和windows文件路径分隔符不同的问题
        String imgPath = tempdir + fileName+"."+filetype;//图片地址
        cat.debug("图片地址"+imgPath);
        if (StringUtils.isEmpty(fileData)) {// 图像数据为空  
            return false;
        }
        OutputStream out = null;
        try {
            // Base64解码  
            byte[] b = Base64.decodeBase64(fileData);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据  
                    b[i] += 256;
                }
            }

            out = new FileOutputStream(imgPath);
            cat.debug("wzimgpath"+imgPath);
            out.write(b);
            out.flush();
            
            return true;
        } catch (Exception e) {
            return false;
        }finally {
        	PubFunc.closeResource(out);
        }

    }
}
