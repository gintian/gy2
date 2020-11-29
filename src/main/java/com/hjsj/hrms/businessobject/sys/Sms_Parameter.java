package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.smslib.helper.CommPortIdentifier;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

//import gnu.io.CommPortIdentifier;

public class Sms_Parameter
{
  private Connection conn = null;
  private ContentDAO dao = null;

  public Sms_Parameter()
  {
  }

  public Sms_Parameter(Connection conn)
  {
    this.conn = conn;
    this.dao = new ContentDAO(this.conn);
  }

  private void firstAddCommPort(String saveStr, int flag)
    throws GeneralException
  {
    try
    {
      if ("".equals(saveStr)) {
          return;
      }

      String[] strRow = saveStr.split(";");
      int count = strRow.length;
      Element root = new Element("ports");
      List list = new ArrayList();
      for (int i = 0; i < count; i++) {
        LazyDynaBean bean = new LazyDynaBean();
        String[] cellStr = strRow[i].split(",");

        String valid = cellStr[5];
        String password = cellStr[4];
        password = (password != null) ? password : "0000";

        Element child = new Element("port");

        child.setAttribute("name", cellStr[0]);

        if ((cellStr[1] != null) && (!("".equals(cellStr[1]))))
        {
          child.setAttribute("company", cellStr[1]);
        }
        if ((cellStr[2] != null) && (!("".equals(cellStr[2]))))
        {
          child.setAttribute("modeltype", cellStr[2]);
        }
        if ((cellStr[3] != null) && (!("".equals(cellStr[3]))))
        {
          child.setAttribute("bit", cellStr[3]);
        }
        if ((password != null) && (!("".equals(password))))
        {
          child.setAttribute("pin", password);
        }

        if ("1".equals(valid)) {
          valid = "true";
          child.setAttribute("valid", "true");
        }
        else {
          valid = "false";
          child.setAttribute("valid", "false");
        }

        root.addContent(child);

        if ("true".equals(valid))
        {
          bean.set("name", cellStr[0]);
          bean.set("company", cellStr[1]);
          bean.set("modeltype", cellStr[2]);
          bean.set("bit", cellStr[3]);
          bean.set("pin", cellStr[4]);

          list.add(bean);
        }

      }

      Document myDocument = new Document(root);
      XMLOutputter outputter = new XMLOutputter();
      Format format = Format.getPrettyFormat();
      format.setEncoding("UTF-8");
      outputter.setFormat(format);
      RecordVo para_vo = new RecordVo("constant");
      para_vo.setString("constant", "SS_SMS_OPTIONS");
      para_vo.setString("str_value", 
        outputter.outputString(myDocument));
      if (flag == -1) {
          this.dao.addValueObject(para_vo);
      } else {
          this.dao.updateValueObject(para_vo);
      }
      ConstantParamter.putConstantVo(para_vo, "SS_SMS_OPTIONS");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw GeneralExceptionHandler.Handle(ex);
    }
  }

  private void addElement(String saveStr, RecordVo vo)
    throws GeneralException
  {
    String content = vo.getString("str_value");
    if(content==null || "".equals(content))
    {
        content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ports/>";
    }
    
    try
    {
      Document doc = PubFunc.generateDom(content);
      String xpath = "//port";

      XPath xpath_ = XPath.newInstance(xpath);
      Element ele = (Element)xpath_.selectSingleNode(doc);
      while (ele != null) {
        doc.getRootElement().removeContent(ele);

        XPath xpath1_ = XPath.newInstance(xpath);
        Element ele1 = (Element)xpath1_.selectSingleNode(doc);
        ele = ele1;
      }

      if ("".equals(saveStr)) {
          return;
      }

      String[] strRow = saveStr.split(";");
      int count = strRow.length;

      List list = new ArrayList();
      for (int i = 0; i < count; i++)
      {
        LazyDynaBean bean = new LazyDynaBean();

        String[] cellStr = strRow[i].split(",");

        String valid = cellStr[5];
        String password = cellStr[4];
        password = (password != null) ? password : "0000";

        Element child = new Element("port");

        child.setAttribute("name", cellStr[0]);

        if ((cellStr[1] != null) && (!("".equals(cellStr[1]))))
        {
          child.setAttribute("company", cellStr[1]);
        }
        if ((cellStr[2] != null) && (!("".equals(cellStr[2]))))
        {
          child.setAttribute("modeltype", cellStr[2]);
        }
        if ((cellStr[3] != null) && (!("".equals(cellStr[3]))))
        {
          child.setAttribute("bit", cellStr[3]);
        }
        if ((password != null) && (!("".equals(password))))
        {
          child.setAttribute("pin", password);
        }

        if ("1".equals(valid)) {
          valid = "true";
          child.setAttribute("valid", valid);
        }
        else {
          valid = "false";
          child.setAttribute("valid", "false");
        }
        doc.getRootElement().addContent(child);

        if ("true".equals(valid)) {
          bean.set("name", cellStr[0]);
          bean.set("company", cellStr[1]);
          bean.set("modeltype", cellStr[2]);
          bean.set("bit", cellStr[3]);
          bean.set("pin", cellStr[4]);

          list.add(bean);
        }

      }

      XMLOutputter outputter = new XMLOutputter();
      Format format = Format.getPrettyFormat();
      format.setEncoding("UTF-8");
      outputter.setFormat(format);
      RecordVo para_vo = new RecordVo("constant");
      para_vo.setString("constant", "SS_SMS_OPTIONS");
      para_vo.setString("str_value", outputter.outputString(doc));
      this.dao.updateValueObject(para_vo);
      ConstantParamter.putConstantVo(para_vo, "SS_SMS_OPTIONS");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw GeneralExceptionHandler.Handle(ex);
    }
  }

  public LazyDynaBean searchCommPort(String com)
    throws GeneralException
  {
    RecordVo sms_vo = ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
    if (sms_vo == null) {
        return null;
    }
    
    String content = sms_vo.getString("str_value");
    if ((content == null) || ("".equals(content))) {
        return null;
    }
    
    LazyDynaBean para_vo = new LazyDynaBean();
    try {
      Document doc = PubFunc.generateDom(content);

      String xpath = "//port[@name=\"" + com + "\"]";
      XPath xpath_ = XPath.newInstance(xpath);
      Element ele = (Element)xpath_.selectSingleNode(doc);

      if (ele != null) {
        para_vo.set("com", com);
        para_vo.set("company", ele.getAttributeValue("company"));
        para_vo.set("modeltype", ele.getAttributeValue("modeltype"));
        para_vo.set("bit", ele.getAttributeValue("bit"));
        para_vo.set("password", ele.getAttributeValue("password"));
        para_vo.set("valid", ele.getAttributeValue("valid"));
      }
      else {
        para_vo.set("com", com);
        para_vo.set("company", "");
        para_vo.set("modeltype", "");
        para_vo.set("bit", "");
        para_vo.set("password", "0000");
        para_vo.set("valid", "false");
      }

      xpath = "//ports";
      xpath_ = XPath.newInstance(xpath);
      ele = (Element)xpath_.selectSingleNode(doc);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw GeneralExceptionHandler.Handle(ex);
    }
    return para_vo;
  }

  public void saveCommPort(String saveStr)
    throws GeneralException
  {
    RecordVo sms_vo;
    try
    {
      sms_vo = ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
      if (sms_vo == null)
      {
        firstAddCommPort(saveStr, -1); return;
      }

      addElement(saveStr, sms_vo);
    }
    catch (Exception ex) {
      ex.printStackTrace();

      throw GeneralExceptionHandler.Handle(ex);
    }
  }

  public void saveGateway(String service, String userName, String password, String upUrl, String downUrl, String channelId, String qy, String spname)
    throws GeneralException
  {
    if ((service == null) || ("".equals(service))) {
        throw new GeneralException(
          ResourceFactory.getProperty("errors.sms.comm.null"));
    }
    try {
      RecordVo sms_vo = ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
      if (sms_vo == null || "".equals(sms_vo)) {
        firstAddGateway(service, userName, password, upUrl, downUrl, 
          channelId, qy,spname, -1);

        return;
      }

      addGateway(service, userName, password, upUrl, downUrl, 
        channelId, qy,spname, sms_vo);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw GeneralExceptionHandler.Handle(ex);
    }
  }

  private void addGateway(String service, String userName, String password, String upUrl, String downUrl, String channelId, String qy,String spname, RecordVo vo)
    throws GeneralException
  {
    String content = vo.getString("str_value");
    if(content==null || "".equals(content))
    {
        content = "<?xml version=\"1.0\" encoding=\"GB2312\"?><ports/>";
    }
    
    try {
      if ((content == null) || ("".equals(content))) {
        firstAddGateway(service, userName, password, upUrl, downUrl, 
          channelId, qy,spname, 1);
        return;
      }

      Document doc = PubFunc.generateDom(content);
      String xpath = "//gateway";
      XPath xpath_ = XPath.newInstance(xpath);
      Element ele = (Element)xpath_.selectSingleNode(doc);

      if (ele != null) {
          doc.getRootElement().removeChildren("gateway");
      }
      ele = new Element("gateway");
      doc.getRootElement().addContent(ele);

      Element ports = doc.getRootElement();
      if ((qy != null) && ("1".equals(qy))) {
          ports.setAttribute("flag", "1");
      } else {
          ports.setAttribute("flag", "0");
      }

      ele.setAttribute("username", userName);
      ele.setAttribute("password", password);
      ele.setAttribute("channelid", channelId);
      ele.setAttribute("service", service);
      if(spname==null || spname.trim().length()<=0) {
          spname = "";
      }
      ele.setAttribute("spname", spname);

      ele.removeContent();

      Element grandchild1 = new Element("up_url");
      grandchild1.addContent(upUrl);
      ele.addContent(grandchild1);

      Element grandchild2 = new Element("down_url");
      grandchild2.addContent(downUrl);
      ele.addContent(grandchild2);

      XMLOutputter outputter = new XMLOutputter();
      Format format = Format.getPrettyFormat();
      format.setEncoding("UTF-8");
      outputter.setFormat(format);
      RecordVo para_vo = new RecordVo("constant");
      para_vo.setString("constant", "SS_SMS_OPTIONS");
      para_vo.setString("str_value", outputter.outputString(doc));
      this.dao.updateValueObject(para_vo);
      ConstantParamter.putConstantVo(para_vo, "SS_SMS_OPTIONS");
    } catch (Exception ex) {
      ex.printStackTrace();
      throw GeneralExceptionHandler.Handle(ex);
    }
  }

  private void firstAddGateway(String service, String userName, String password, String upUrl, String downUrl, String channelId, String qy,String spname, int flag)
    throws GeneralException
  {
    Element root;
    try
    {
      root = new Element("ports");

      if ((qy != null) && ("1".equals(qy))) {
          root.setAttribute("flag", "1");
      } else {
          root.setAttribute("flag", "0");
      }

      Element child = new Element("gateway");
      child.setAttribute("username", userName);
      child.setAttribute("password", password);
      child.setAttribute("channelid", channelId);
      child.setAttribute("service", service);
      if(spname==null || spname.trim().length()<=0) {
          spname = "";
      }
      child.setAttribute("spname", spname);

      Element grandchild1 = new Element("up_url");
      grandchild1.addContent(upUrl);
      child.addContent(grandchild1);

      Element grandchild2 = new Element("down_url");
      grandchild2.addContent(downUrl);
      child.addContent(grandchild2);

      root.addContent(child);

      Document myDocument = new Document(root);
      XMLOutputter outputter = new XMLOutputter();
      Format format = Format.getPrettyFormat();
      format.setEncoding("UTF-8");
      outputter.setFormat(format);
      RecordVo para_vo = new RecordVo("constant");
      para_vo.setString("constant", "SS_SMS_OPTIONS");
      para_vo.setString("str_value", outputter.outputString(myDocument));
      if (flag == -1) {
          this.dao.addValueObject(para_vo);
      } else {
          this.dao.updateValueObject(para_vo);
      }
      ConstantParamter.putConstantVo(para_vo, "SS_SMS_OPTIONS");
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw GeneralExceptionHandler.Handle(ex);
    }
  }

  public LazyDynaBean searchGateway(String service)
    throws GeneralException
  {
    RecordVo sms_vo = ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
    if (sms_vo == null) {
        return null;
    }
    String content = sms_vo.getString("str_value");
    if ((content == null) || ("".equals(content))) {
        return null;
    }
    LazyDynaBean para_vo = new LazyDynaBean();
    try {

      Document doc = PubFunc.generateDom(content);

      String xpath = "//gateway[@service=\"" + service + "\"]";
      XPath xpath_ = XPath.newInstance(xpath);
      Element ele = (Element)xpath_.selectSingleNode(doc);

      para_vo.set("service", service);
      if (ele != null) {
        
        para_vo.set("username", 
          isNull(ele.getAttributeValue("username")));
        para_vo.set("password", 
          isNull(ele.getAttributeValue("password")));
        para_vo.set("channelid", 
          isNull(ele.getAttributeValue("channelid")));
        para_vo.set("spname", 
                isNull(ele.getAttributeValue("spname")));

        Element upUrl = ele.getChild("up_url");
        para_vo.set("up_url", isNull(upUrl.getTextTrim()));

        Element downUrl = ele.getChild("down_url");
        para_vo.set("down_url", isNull(downUrl.getTextTrim()));
      }
      else
      {
        para_vo.set("username", "");
        para_vo.set("password", "");
        para_vo.set("channelid", "");
        para_vo.set("up_url", "");
        para_vo.set("down_url", "");
        para_vo.set("spname", "");
      }

      xpath = "//ports";
      xpath_ = XPath.newInstance(xpath);
      ele = (Element)xpath_.selectSingleNode(doc);
      String flag = ele.getAttributeValue("flag");
      if ((flag != null) && ("1".equals(flag))) {
          para_vo.set("qy", "1");
      } else {
          para_vo.set("qy", "0");
      }

      xpath = "//ports";
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw GeneralExceptionHandler.Handle(ex);
    }
    return para_vo;
  }

  public String isNull(String str) {
    if (str != null) {
        return str;
    }
    return "";
  }

  public List queryCommPort() {
    List list1 = null;
    try
    {
      RecordVo sms_vo = ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
      if (sms_vo == null)
      {
          try
          {
            list1 = new ArrayList();
            LazyDynaBean bean;
            for (int i = 1; i < 4; i++) {
              bean = new LazyDynaBean();
              bean.set("com", "COM" + i);

              list1.add(bean);
            }
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
          
          return list1;
      }
       

      list1 = QueryElement(sms_vo);

      if (list1.size() > 1)
      {
          for (int i = 0; i < list1.size(); i++) {
              for (int j = 1; j < list1.size(); j++) {
                LazyDynaBean bean1 = (LazyDynaBean)list1.get(i);
                LazyDynaBean bean2 = (LazyDynaBean)list1.get(j);

                int b1 = 0;
                int b2 = 0;
                if (bean1.get("com").toString().indexOf("COM") != -1)
                {
                  b1 = Integer.parseInt(bean1.get("com").toString().substring(3, bean1.get("com").toString().length()));
                  b2 = Integer.parseInt(bean2.get("com").toString().substring(3, bean2.get("com").toString().length()));
                }
                else if (bean1.get("com").toString().indexOf("ttyS") != -1)
                {
                  b1 = Integer.parseInt(bean1.get("com").toString().substring(4, bean1.get("com").toString().length()));
                  b2 = Integer.parseInt(bean2.get("com").toString().substring(4, bean2.get("com").toString().length()));
                }
                if (b1 > b2) {
                  LazyDynaBean bean3 = new LazyDynaBean();
                  bean3 = (LazyDynaBean)list1.get(i);
                  list1.remove(i);
                  list1.add(bean3);
                }
              }
          }
      }

    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    return list1;
  }

  public List ImportCommPort() throws Exception
  {
    List list1 = null;
    try
    {
      RecordVo sms_vo = ConstantParamter.getConstantVo("SS_SMS_OPTIONS");
      if (sms_vo == null)
      {
        list1 = firstQueryCommPort();
      }
      else 
      {
          list1 = ImportElement(sms_vo);
          if (list1.size() > 1)
          {
              for (int i = 0; i < list1.size(); i++) {
                  for (int j = 1; j < list1.size(); j++) {
                    LazyDynaBean bean1 = (LazyDynaBean)list1.get(i);
                    LazyDynaBean bean2 = (LazyDynaBean)list1.get(j);
                    int b1 = 0;
                    int b2 = 0;
                    if (bean1.get("com").toString().indexOf("COM") != -1)
                    {
                      b1 = Integer.parseInt(bean1.get("com").toString().substring(3, bean1.get("com").toString().length()));
                      b2 = Integer.parseInt(bean2.get("com").toString().substring(3, bean2.get("com").toString().length()));
                    } else if (bean1.get("com").toString().indexOf("ttyS") != -1) {
                      b1 = Integer.parseInt(bean1.get("com").toString().substring(4, bean1.get("com").toString().length()));
                      b2 = Integer.parseInt(bean2.get("com").toString().substring(4, bean2.get("com").toString().length()));
                    }
                    if (b1 > b2) {
                      LazyDynaBean bean3 = new LazyDynaBean();
                      bean3 = (LazyDynaBean)list1.get(i);
                      list1.remove(i);
                      list1.add(bean3);
                    }
                 }
              }
          }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw new Exception(ex.getMessage());
    }

    return list1;
  }
  
  //从constant中取端口列表
  private List getPortsFromConstant(RecordVo vo)
  {
      List elList = null;
      
      String content = vo.getString("str_value");
      if(content==null || "".equals(content))
      {
          content = "<?xml version=\"1.0\" encoding=\"GB2312\"?><ports/>";
      }
      
      try 
      {

          Document doc = PubFunc.generateDom(content);
          String path = "/ports/port";
          XPath xpath = XPath.newInstance(path);
          elList = xpath.selectNodes(doc);
      }
      catch(Exception ex)
      {
          ex.printStackTrace();
      }
      
      return elList;
  }

  private List QueryElement(RecordVo vo)
  {
    List list = new ArrayList();
    try 
    {
      List elList = getPortsFromConstant(vo);
      if(elList == null) {
          return list;
      }
      
      for (int i = 0; i < elList.size(); i++) {
        Element el = (Element)elList.get(i);
        String valid = el.getAttributeValue("valid");
        if ("true".equals(valid)) {
            valid = "1";
        } else {
            valid = "0";
        }

        LazyDynaBean bean = new LazyDynaBean();
        bean.set("com", (el.getAttributeValue("name") == null) ? "" : 
          el.getAttributeValue("name"));
        bean.set("company", 
          (el.getAttributeValue("company") == null) ? "" : 
          el.getAttributeValue("company"));
        bean.set("modeltype", 
          (el.getAttributeValue("modeltype") == null) ? "" : 
          el.getAttributeValue("modeltype"));
        bean.set("bit", (el.getAttributeValue("bit") == null) ? "" : 
          el.getAttributeValue("bit"));
        bean.set("password", (el.getAttributeValue("pin") == null) ? "" : 
          el.getAttributeValue("pin"));

        bean.set("valid", valid);

        list.add(bean);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  private List ImportElement(RecordVo vo) throws Exception
  {
    List listT = new ArrayList();

    List list2 = new ArrayList();
    LazyDynaBean bean2 = new LazyDynaBean();
        
    
      List elList = getPortsFromConstant(vo);

      try
      {
          listT = CommTest.test();
      }
      catch (Exception ex)
      {
          ex.printStackTrace();
          throw new Exception(ex.getMessage());
      }
      try 
      {
      for (int i = 0; i < elList.size(); i++) {
        String compString = "";
        String modelString = "";
        String bString = "";
        Element el = (Element)elList.get(i);

        for (int k = 0; k < listT.size(); ++k) {
          LazyDynaBean beanT = (LazyDynaBean)listT.get(k);
          if (beanT.get("com").equals(el.getAttributeValue("name"))) {
            compString = (String)beanT.get("company");
            modelString = (String)beanT.get("modeltype");
            bString = (String)beanT.get("bit");
            break;
          }
        }

        String valid = el.getAttributeValue("valid");
        if ("true".equals(valid)) {
            valid = "1";
        } else {
            valid = "0";
        }

        bean2 = new LazyDynaBean();
        bean2.set("com", (el.getAttributeValue("name") == null) ? "" : 
          el.getAttributeValue("name"));

        bean2.set("company", 
          (el.getAttributeValue("company") == null) ? compString : 
          el.getAttributeValue("company"));

        bean2.set("modeltype", 
          (el.getAttributeValue("modeltype") == null) ? modelString : 
          el.getAttributeValue("modeltype"));
        bean2.set("bit", (el.getAttributeValue("bit") == null) ? bString : 
          el.getAttributeValue("bit"));
        bean2.set("password", (el.getAttributeValue("pin") == null) ? "" : 
          el.getAttributeValue("pin"));
        bean2.set("valid", valid);
        list2.add(bean2);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception(e.getMessage());
    }

    ArrayList list1 = getPortList();
    /*暂不使用rxtx包
    Enumeration en = CommPortIdentifier.getPortIdentifiers();
    while (en.hasMoreElements()) {
      CommPortIdentifier portId = (CommPortIdentifier)en.nextElement();
      if (portId.getPortType() == 1) {
        bean1 = new LazyDynaBean();
        bean1.set("com", portId.getName());

        list1.add(bean1);
      }
    }
    if (list1.size() == 0) {
      return list2;
    }
    */
    for (int i = 0; i < list2.size(); i++) {
        for (int j = 0; j < list1.size(); j++) {
          LazyDynaBean bean3 = (LazyDynaBean)list2.get(i);
          LazyDynaBean bean4 = (LazyDynaBean)list1.get(j);
          if (bean3.get("com").equals(bean4.get("com"))) {
              list1.remove(j);
          }
        }
    }


    if (list1.size() == 0) {
        return list2;
    }

    if (list1.size() > 0) {
      for (int i = 0; i < list1.size(); i++)
      {
        List list3 = listT;

        for (int j = 0; j < list3.size(); j++) {
          LazyDynaBean bean3 = (LazyDynaBean)list1.get(i);
          LazyDynaBean bean4 = (LazyDynaBean)list3.get(j);
          if (bean3.get("com").equals(bean4.get("com"))) {
            list2.add(list3.get(j));
            list1.remove(i);
          }
        }
      }
      if (list1.size() > 0) {
          for (int i = 0; i < list1.size(); i++) {
              list2.add(list1.get(i));
          }
      }


      return list2;
    }

    return list2;
  }

  private List firstQueryCommPort() throws Exception
  {
    ArrayList list1 = getPortList();
    
    if (list1.size() > 0)
    {
      List list = new ArrayList();
      list = CommTest.test();
      for (int i = 0; i < list.size(); i++)
      {
          LazyDynaBean bean3 = (LazyDynaBean)list.get(i);
          
          for (int j = 0; j < list1.size(); j++) 
          {         
              LazyDynaBean bean4 = (LazyDynaBean)list1.get(j);
              if (bean3.get("com").equals(bean4.get("com"))) {
                  list1.remove(j);
              }
          }
      }

      if (list1.size() == 0) {
          return list;
      }

      for (int i = 0; i < list1.size(); i++)
      {
        list.add(list1.get(i));
      }

      return list;
    }

    try
    {
        LazyDynaBean bean = null;
        list1 = new ArrayList();
        
        for (int i = 1; i < 4; i++) 
        {
            bean = new LazyDynaBean();
            bean.set("com", "COM" + i);

            list1.add(bean);
        }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return list1;
  }
  
  private ArrayList getPortList()
  {
      ArrayList portList = new ArrayList(); 
      CommPortIdentifier port = null;
      LazyDynaBean bean = null;
      
      try
      {
          Enumeration ports = CommTest.getCleanPortIdentifiers();
          while (ports.hasMoreElements())
          {
              port = (CommPortIdentifier) ports.nextElement();
              if (port.getPortType() == CommPortIdentifier.PORT_SERIAL)
              {
                  bean = new LazyDynaBean();
                  bean.set("com", port.getName());
    
                  portList.add(bean);
              }
          }
      }
      catch(Exception ex)
      {
          ex.printStackTrace();
      }
      
      return portList;
  }

}