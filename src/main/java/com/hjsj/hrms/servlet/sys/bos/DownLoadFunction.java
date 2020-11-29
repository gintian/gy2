package com.hjsj.hrms.servlet.sys.bos;

import com.hjsj.hrms.actionform.sys.bos.func.FunctionMainForm;
import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hjsj.hrms.utils.PubFunc;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownLoadFunction extends HttpServlet {

    public DownLoadFunction() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String ext = "xml";
        ServletOutputStream sos = response.getOutputStream();
        String name = "function";
        InputStream inputStream = null;
        // 备份function.xml,session中存在就从session中取出,不存在找绝对路径
        FunctionMainForm functionMainForm = (FunctionMainForm) request.getSession().getAttribute("functionMainForm");
        Document doc = functionMainForm.getFunction_dom();

        String parentid = functionMainForm.getParentid();
        if (doc != null) {
            XMLOutputter outputter = new XMLOutputter();
            if ("-1".equals(parentid)) {// dml 2011-04-19

                Format format = Format.getPrettyFormat();
                format.setEncoding("UTF-8");
                outputter.setFormat(format);
                String xml = outputter.outputString(doc);
                inputStream = new ByteArrayInputStream(xml.getBytes());
            } else {
                try {
                    String xpath = "//function[@id=\"" + parentid + "\"]";
                    XPath xPath = XPath.newInstance(xpath);
                    Element function = (Element) xPath.selectSingleNode(doc);
                    Element hr = new Element("hrp_function");
                    if (function == null) {

                    } else {
                        Element pa = function.getParentElement();
                        String parent = "";
                        if (pa.getAttribute("id") != null) {
                            parent = pa.getAttributeValue("id");
                        } else {
                            parent = "";
                        }
                        if (parent.length() != 0) {
                            hr.setAttribute("parentid", parent);
                        }
                        Element doc1 = (Element) function.clone();
                        hr.addContent(doc1);
                        Format format = Format.getPrettyFormat();
                        format.setEncoding("UTF-8");
                        outputter.setFormat(format);
                        Document d = new Document(hr);
                        String xml = outputter.outputString(d);
                        inputStream = new ByteArrayInputStream(xml.getBytes());
                    }
                } catch (JDOMException e) {
                    e.printStackTrace();
                }
            }

        } else {
            FuncMainBo bo = new FuncMainBo();
            inputStream = bo.getInputStreamFromjar();
        }
        try {

            response.setContentType("text/xml");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + name + "." + ext + "\"");
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                sos.write(buf, 0, len);
            }

        } finally {
            if (inputStream != null)
                PubFunc.closeResource(inputStream);
            sos.close();
        }

    }

}
