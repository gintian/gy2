package com.hjsj.hrms.module.serviceclient.serviceHome;

import com.hjsj.hrms.businessobject.ykcard.YkcardOutWord;
import com.hjsj.hrms.module.serviceclient.serviceSetting.businessobject.ServiceSettingBo;
import com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject.OutWordBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("serial")
public class LoadPrintDataTrans extends IBusiness {
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute() throws GeneralException {
        String serviceId = (String) this.formHM.get("serviceId");//服务号
        String ins_id = (String) this.formHM.get("ins_id");//流程实例编号
        String task_id = (String) this.formHM.get("task_id");//任务号
        String number = (String) this.formHM.get("number");//打印初始化
        String ip = (String) this.formHM.get("ip");//获取ip
        String templateType = (String)this.formHM.get("templateType");
        int template_id = Integer.parseInt((String) this.formHM.get("templateId"));//模板号
        ServiceSettingBo bo = new ServiceSettingBo(this.frameconn, this.userView);
        Map<String, String> service = bo.getPrintService(serviceId);//获取服务的部分信息 服务描述 可以打印页数 价格
        String description = (String) service.get("description");
        String canPrintCount = (String) service.get("canPrintCount");
        String printPrice = (String) service.get("printPrice");
        String pageCount =bo.getPageCount(ip);
        String loadPrintList = (String) this.formHM.get("printList");//流程实例编号
        String encryptFileName = (String) this.formHM.get("filename");
        if("Initialize".equals(number)) {
            String filename = "";
           if(!"loadPrintList".equals(loadPrintList)) {
               if("1".equals(templateType)) {
                    filename = getTemplatePdfFile(template_id, ins_id, task_id);
                }else {
                    filename = getYkCardPdfFile(template_id);
                  }
            }else {
                ArrayList printList = pdf2Image(PubFunc.decrypt(encryptFileName));
                this.formHM.put("printList", printList);
            }
            if(StringUtils.isBlank(filename)) {//获取到返回回来的pdf文件名,如果为空,则认为生成过程中出错,前台不再进行加载
                this.formHM.put("fileError", "fileError");
            }
            //模拟返回数据
            this.formHM.put("filename", PubFunc.encrypt(filename));
        }

        this.formHM.put("description", description);
        this.formHM.put("canPrintCount", canPrintCount);
        this.formHM.put("printPrice", printPrice);
        this.formHM.put("pageCount", pageCount);
    }

    /**
     * 获取业务表单图片集合
     * @return
     */
    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
    private String getTemplatePdfFile(int templateId, String ins_id, String task_id) {
        @SuppressWarnings("rawtypes")
        ArrayList filenameList = new ArrayList();
        String filename = null;
        ContentDAO dao = new ContentDAO(this.frameconn);
        try {
            TemplateParam parambo = new TemplateParam(this.frameconn, this.userView, templateId);
            int infor_type = parambo.getInfor_type();
            OutWordBo owbo = new OutWordBo(this.getFrameconn(), this.userView, templateId, task_id + "");
            owbo.setDowntype("1");//非压缩下载
            owbo.setOuttype("0");//pdf
            ArrayList inslist = new ArrayList();
            inslist.add(ins_id + "");
            ArrayList objlist = new ArrayList();
            objlist.add(this.userView.getDbname() + this.userView.getA0100());
            filename = owbo.outword(objlist, infor_type, inslist);//获取pdf文件名 请假申请单-本地公出_su.pdf 卡 
            //String prefix = "serviceclient_"+userView.getA0100();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    /**
     * 获取登记表图片集合
     * @return
     * @throws Exception 
     */
    @SuppressWarnings({ "unused", "rawtypes" })
    private String getYkCardPdfFile(int tabId) {
        ArrayList filenameList = new ArrayList();
        String filename = null;
        try {
            YkcardOutWord outWord = new YkcardOutWord(this.userView, this.frameconn);
            filename = outWord.outPdfYkcard(tabId, this.userView.getA0100(), "0", "1", this.userView.getDbname(), "selfinfo", "0", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ArrayList pdf2Image(String filename) { 
        getLicense();
        ArrayList filenameList = new ArrayList();
        String prefix = "serviceclient_" + userView.getA0100();
        int count = OutWordBo.pdf2Image(System.getProperty("java.io.tmpdir"), filename, "", prefix, 0, 0, 220);
        for (int i = 1; i <= count; i++) {
            String name = prefix + "_" + i + ".jpg";//.jpg
            filenameList.add(PubFunc.encrypt(name));
        }
        return filenameList;
    }

    private boolean getLicense() {
        boolean result = false;
        String strs = "";
        ServletContext ServletContext = null;
        try {
        	ServletContext = SystemConfig.getServletContext();
            strs = ServletContext.getRealPath("WEB-INF");
            if (strs == null) {
                strs = ServletContext.getResource("/").getFile();
                if (strs.indexOf("WEB-INF") > -1) {
                    strs = strs.substring(0, strs.indexOf("WEB-INF") + "WEB-INF".length());
                } else {
                    if ("\\".equals(File.separator)) {//证明是windows
                        strs = strs + "WEB-INF";
                    } else if ("/".equals(File.separator)) {//证明是linux
                        strs = strs + "WEB-INF";
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("获取aspose.Total文件失败！");
            e.printStackTrace();
        }

        File file = new File(strs);
        file = new File(file + File.separator + "Aspose.Total.Java.lic");
        InputStream license = null;
        try {
            license = new FileInputStream(file.getPath());
            com.aspose.pdf.License aposeLic = new com.aspose.pdf.License();//卡
            aposeLic.setLicense(license);//卡 不动
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(license);
        }
        return result;
    }

}
