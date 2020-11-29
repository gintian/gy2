package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;

public class GetKqSignPointTreeXml {

    private String city; //id，有前缀，分类为C，考勤点为P
    private String action;
    private String treeSource;
    private UserView userView;
    
    
    public GetKqSignPointTreeXml(String city,UserView userView)
    {   
        this.city = city;
        this.userView = userView;
        this.action="/kq/options/sign_point/setsign_point.do?";
        this.treeSource="/kq/options/signpoint/getSignTreeXml.jsp?city=";
    }
    
    
    public String getTreeXml(){
        String xmls = "";
        StringBuffer sqlstr = new StringBuffer();
        Connection conn = null;
        RowSet rs = null;
        try{
          conn = AdminDb.getConnection();
          ContentDAO dao = new ContentDAO(conn);
          
          Element root = new Element("TreeNode");
          root.setAttribute("id","$$00");
          root.setAttribute("text","root");
          root.setAttribute("title","考勤点");
          Document myDocument = new Document(root);
          
          //tiany add 针对考勤员角色权限 不是考勤员角色，仍走用户的人员范围权限 进行筛选考勤点
         
          String privCode = RegisterInitInfoData.getKqPrivCode(userView);
          String codeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
          
          //end
          
          if(city!=null && city.trim().length()>0){
              String cityname = city.substring(1);
            
              sqlstr.append(" select pid,name from kq_sign_point");
              
              if (!"".equals(cityname))
                  sqlstr.append(" where city='"+cityname+"'");
              else {
                  //zxj 20150424 城市为空的可能是国外，百度地图暂不支持
                  sqlstr.append(" where (").append(Sql_switcher.isnull("city", "'hjsj--'")).append("='hjsj--'");
                  if (Constant.MSSQL == Sql_switcher.searchDbServer())
                      sqlstr.append(" or city=''");
                  sqlstr.append(")");
              }
              
              if(!userView.isSuper_admin()){
                  if(privCode!=null && privCode.length()!=0){
                    sqlstr.append(" and ( b0110 like '"+codeValue+"%'");
                    sqlstr.append(" or b0110 is null or b0110 ='' )");//公共考勤点
                  }else{
                      sqlstr.append(" and 1=2"); 
                  }
              }
              rs = dao.search(sqlstr.toString());
              while(rs.next()){
                  Element child = new Element("TreeNode");
                  child.setAttribute("id", "P"+rs.getString("pid"));
                  child.setAttribute("text", rs.getString("name"));
                  child.setAttribute("href", action+"b_showpoint=link&pid=P"+rs.getString("pid"));
                  child.setAttribute("title",rs.getString("name"));
                  child.setAttribute("target","mil_body");
                  child.setAttribute("icon","/images/table.gif");
                  root.addContent(child);
              }
          }else{
              sqlstr.append(" select city from kq_sign_point ");

              if(!userView.isSuper_admin()){
                  if(privCode!=null && privCode.length()!=0){
                      sqlstr.append(" where ( b0110 like '"+codeValue+"%'");
                        sqlstr.append(" or b0110 is null or b0110 ='' )");//公共考勤点
                  }else{
                      sqlstr.append(" where 1=2"); 
                  }
              }
              
              sqlstr.append(" group by city ");
              rs = dao.search(sqlstr.toString());
              while(rs.next()){
                  String cityname = rs.getString("city");
                  if (null == cityname) 
                      cityname = "";
                          
                  Element child = new Element("TreeNode");
                  child.setAttribute("id", "C"+cityname);
                  if (!"".equalsIgnoreCase(cityname))
                      child.setAttribute("text", cityname);
                  else
                      child.setAttribute("text", "未知城市");
                  child.setAttribute("href",  action + "encryptParam=" + PubFunc.encrypt("b_showpoint=link&pid=C"+cityname));
                  child.setAttribute("target","mil_body");
                  child.setAttribute("xml", treeSource + PubFunc.encryption("C" + cityname));
                  child.setAttribute("title",cityname);
                  root.addContent(child);
              }
          }
        
          XMLOutputter outputter = new XMLOutputter(); 
          Format format=Format.getPrettyFormat();
          format.setEncoding("UTF-8");
          outputter.setFormat(format);
          xmls = outputter.outputString(myDocument);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            KqUtilsClass.closeDBResource(conn);
            KqUtilsClass.closeDBResource(rs);
        }
        return xmls;
    }
}
