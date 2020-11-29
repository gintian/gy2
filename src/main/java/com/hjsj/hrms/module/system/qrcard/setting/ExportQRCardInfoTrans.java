package com.hjsj.hrms.module.system.qrcard.setting;

import com.aspose.words.DocumentBuilder;
import com.aspose.words.SaveFormat;
import com.hjsj.hrms.module.utils.asposeword.AsposeLicenseUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.io.File;

/**
*
* @Titile: ExportQRCardInfoTrans
* @Description:导出二维码时，生成pdf并保存到临时目录
* @Company:hjsj
* @Create time:2018年8月7日10:17:54
* @author: wangwh
* @version 1.0
*
*/
public class ExportQRCardInfoTrans extends IBusiness{
    @Override
    public void execute() throws GeneralException {
        //本类用于pdf的导出操作
        //获取pdf的内容
        String html = (String)this.getFormHM().get("html");
        //zhangh 2020-1-10 【54460】Oracle19C+was8.5：入职登记二维码制作，打印二维码/导出报会话超时
        html =  PubFunc.keyWord_reback(SafeCode.decode(html));
        //获取二维码表单id号
        String qrid = (String)this.getFormHM().get("qrid");
        //表单名称
        String name = (String)this.getFormHM().get("name");
        //创建文件名
        UserView userview = this.userView;
        String createuser = userview.getUserName();
        String pdfName = createuser+"_"+name+".pdf";//"qr_"+createuser+qrid+"_card.pdf";
        StringBuffer strHtml = new StringBuffer();
        strHtml.append("<!DOCTYPE html>");
        strHtml.append("<html>");
        strHtml.append("<head>");
        strHtml.append("<title></title>");
        strHtml.append("<meta http-equiv=\"Content-type\" content=\"text/html; charset=GB2312\">");
        strHtml.append("</head>");
        strHtml.append("<div style='text-align:center;'><font size=5 face='Microsoft YaHei'>");
        strHtml.append(name);
        strHtml.append("</font></div>");
        strHtml.append("<body>");
        strHtml.append(html);
        strHtml.append("</body>");
        strHtml.append("</html>");
        //获取临时目录用于存储生成的文件
        String tempdir = System.getProperty("java.io.tmpdir");//获得临时目录路径
        if(!tempdir.endsWith("\\")) {
            tempdir = tempdir+"\\";
        }
        tempdir = tempdir.replace("\\", File.separator).replace("/", File.separator);//解决linux和windows文件路径分隔符不同的问题
        String pdfPath = tempdir+pdfName;
        try {
            com.aspose.words.Document doc = new com.aspose.words.Document();
            DocumentBuilder builder = new AsposeLicenseUtil(doc);
            builder.insertHtml(strHtml.toString()); 
            doc.save(pdfPath,SaveFormat.PDF);
            this.getFormHM().put("pdfName", PubFunc.encrypt(pdfName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
