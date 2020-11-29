package com.etong.webclient.utils;

import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import org.apache.log4j.Category;
import org.jbpm.ExecutionService;
import org.jbpm.JbpmServiceFactory;
import org.jdom.Document;
import org.jdom.Element;

import java.io.File;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: etong</p>
 * @author chenmengqing
 * @version 1.0
 */

public class ParsingExFileOfDefintion
{
  private HashMap hm = new HashMap();
  private Connection conn;
  private Category cat = Category.getInstance(this.getClass());
  /**
   * 取得文书模板内容
   * @param id
   * @return
   */
  private String findContent(String id)
  {
    StringBuffer strsql = new StringBuffer();
    strsql.append("select bytes from jbpm_file where definitionid=");
    strsql.append(id);
    strsql.append(" and name='notice_template'");
    ResultSet rs = null;
    String result = null;
    try
    {
    	ContentDAO dao = new ContentDAO(conn);
    	rs = dao.search(strsql.toString());
      if (rs.next())
      {
        /**
         * 读写CLOB中的内容,select mw from xxxxx;
         * Clob clob = rset.getClob("mw");
         * String mw=clob.getSubString((long) 1, (int) clob.length());
         */
        Clob clob = rs.getClob(1);
        clob=OracleBlobUtils.convertDruidToOracle(clob);
        result = clob.getSubString(1, (int) clob.length());
      }
      rs.close();
      rs = null;
    }
    catch (SQLException se)
    {
      se.printStackTrace();
    }
    catch (Exception ee)
    {
      ee.printStackTrace();
    }
    finally
    {
      try
      {
        if (rs != null)
        {
          rs.close();
        }
//        if (stmt != null)
//        {
//          stmt.close();
//        }
      }
      catch (SQLException ee)
      {
        ee.printStackTrace();
      }
    }
    return result;
  }

  private byte[] getFileBytes(Long definitionId, String fileName)
  {
    ExecutionService executionService = JbpmServiceFactory.getInstance().openExecutionService(null,conn);
    byte[] bytes = executionService.getFile(definitionId, fileName);
    if(!(bytes==null||bytes.length==0))
    {
      String xxx=new String(bytes);
      cat.debug("----------------->"+xxx);
    }
    executionService.close();
    return bytes;
  }

  public ParsingExFileOfDefintion(String definition_id,Connection conn)
  {
    this.conn = conn;
    InputStream in = null;
    Long process_id=new Long(definition_id);
    byte[] content=getFileBytes(process_id,"notice_template");
    if (!(content == null || content.length==0))
    {
      File file=null;
      try
      {
        file = File.createTempFile("bpm", "fmt");
        in = file.toURL().openStream();
        Document doc = PubFunc.generateDom(in);
        cat.debug("Document successfully readed");
        Element root = doc.getRootElement();
        Element web_url = root.getChild("web_url");
        java.util.List list = web_url.getChildren();
        for (int i = 0; i < list.size(); i++)
        {
          Element child = (Element) list.get(i);
          String name = child.getAttributeValue("name");
          String path = child.getAttributeValue("path");
          hm.put(name, path);
        }
      }
      catch (Exception ee)
      {
        ee.printStackTrace();
      }
      finally
      {
        if(file!=null){
          file.deleteOnExit();
        }
        PubFunc.closeResource(in);
      }
    }

  }

  /**
   * 取得文书有关url
   * @param name for edit_eform_path,view_eform_path,input_result_path,view_result_path
   * @return
   */
  public String getWebUrl(String name)
  {
    return (String) hm.get(name);
  }
}