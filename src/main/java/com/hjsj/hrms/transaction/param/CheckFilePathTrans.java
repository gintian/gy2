package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckFilePathTrans extends IBusiness{

	public void execute() throws GeneralException {
		try {
            ConstantXml constantXml = new ConstantXml(this.getFrameconn(), "FILEPATH_PARAM");
            String oldfpath = constantXml.getNodeAttributeValue("/filepath", "rootpath");
            oldfpath = oldfpath == null ? "" : oldfpath.trim();
            String newfpath = (String) this.getFormHM().get("newfpath");
            newfpath = newfpath == null ? "" : newfpath.replace(" ", "");
            String changepath = "false";
            String existspath = "true";
            if (!"".equals(oldfpath)) {
                if (!oldfpath.equalsIgnoreCase(newfpath)) {
                    changepath = "true";
                }
            }
            //【7923】系统管理/参数设置/系统参数：文件存放目录，linux环境时应该是/的斜杠，不应该是\斜杠，建议规则跟操作系统统一。  jingq upd 2015.03.10
            /*// WJH 固定目录, 支持相对目录..演示库可以设置这个
            if (newfpath.indexOf(":")!=1 && newfpath.indexOf("\\")!=0 && newfpath.indexOf("..")==-1) {
            	existspath = "false";
            }else{
            	//liuy 2014-7-25 bengin
            	//判断盘符后面是否跟有\
            	if(newfpath.indexOf(":")==1){
            		if(newfpath.length()>=((newfpath.indexOf(":"))+2)){
            			String subPath = newfpath.substring((newfpath.indexOf(":")+1), (newfpath.indexOf(":")+2));
            			if(!"\\".equals(subPath)){
            				existspath = "false";
            			}
            		}else {
            			existspath = "false";
            		}
            	}
            	// 三个.及以上不允许
            	if(newfpath.indexOf("...")!=-1){
            		existspath = "false";
            	}
            	//liuy 固定目录，一个.不允许,创建文件夹不能有.
            	String tempPath = newfpath.replace("..\\", "a");
            	if(tempPath.indexOf(".")!=-1){
            		existspath = "false";
            	}//liuy 2014-7-25 end
            	File tempDir = new File(newfpath);
            if (!tempDir.exists()&& "true".equals(existspath)) {
                tempDir.mkdirs();
            }
            if (!tempDir.exists()) {
            	existspath = "false";
            }
            }
            */
            //不同操作系统文件路径格式不同 验证不同操作系统对应文件格式  27505 wangb1 20170504
            //获取当前用户的操作系统名称
            String sys = System.getProperties().getProperty("os.name");
            if(sys.toLowerCase().startsWith("win")) {
                // windows路径第二位必须为冒号，例如c:/abc
                if (newfpath.indexOf(":") != 1)
                    throw new GeneralException(ResourceFactory.getProperty("sys.options.param.filepath.error.win"));
            } else {
                // linux或unix路径中不能包含冒号，一般有冒号的是设成了windows目录
                if (newfpath.contains(":"))
                    throw new GeneralException(ResourceFactory.getProperty("sys.options.param.filepath.error.linux"));
            }
            
            //判断路径是否含有特殊字符
            String arr = newfpath.replace(File.separator, "");
            if (arr.indexOf(":") == 1) {
                arr = arr.substring(0, 1) + arr.substring(2, arr.length());
            }
            Pattern p = Pattern.compile("[\\/*:?<>\"]+");
            Matcher m = p.matcher(arr);
            if (m.find()) {
                throw new GeneralException(ResourceFactory.getProperty("sys.options.param.filepath.error.char"));
            }

            //新路径格式正确且文件夹不存在，则创建文件夹
            if ("true".equals(existspath)) {
                File tempDir = new File(newfpath.replace("\\", File.separator));
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                if (!tempDir.exists()) {//检查文件夹是否存在，不存在，则路径无效
                    existspath = "false";
                }
            }
            oldfpath = oldfpath.replace("\\", "*");
            newfpath = newfpath.replace("\\", "*");
            this.getFormHM().put("existspath", existspath);
            this.getFormHM().put("changepath", changepath);
            this.getFormHM().put("oldfpath", oldfpath);
            this.getFormHM().put("newfpath", newfpath);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}

}
