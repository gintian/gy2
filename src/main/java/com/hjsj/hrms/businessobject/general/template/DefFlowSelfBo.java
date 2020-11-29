package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
    /*xml格式形式
    <flow>
    <reportapprove>
    <level levelnum="1">
    <actor id ="1"  actorid ="usr00000001" actorname="" seq="1">
    <actor id ="2"  actorid ="usr00000001" actorname="" seq="2">
    </level>
    <level levelnum="2">
    <actor id ="3"  actorid ="usr00000001" actorname="" seq="1">
    <actor id ="4"  actorid ="usr00000001" actorname="" seq="2">
    </level>
    </reportapprove>
    <reportback>
    <level levelnum="1">
    <actor id ="1"  actorid ="usr00000001" actorname="" seq="1">
    <actor id ="2"  actorid ="usr00000001" actorname="" seq="2">
    </level>
    </reportback>
    </flow>
     */
/**
 * <p>Title:DefFlowSelfBo.java</p>
 * <p>Description>:自定义审批流程业务类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 26, 2013 5:37:15 PM</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
public class DefFlowSelfBo {
    private Connection conn=null;
    private UserView userview;
    /**模板号*/
    private int tab_id=-1;
    /**流程实例号*/
    private int ins_id=0;
    /**流程任务号*/
    private int task_id=0;
    /**节点编号*/
    private int node_id=0;
    
    private String strXml="";
    
    ContentDAO dao=null;
    
 public DefFlowSelfBo(Connection conn, UserView userview,
                int tab_id,int task_id,int ins_id,int node_id
                 )throws GeneralException {      
        this.conn = conn;
        this.tab_id = tab_id;
        this.task_id = task_id;
        this.ins_id = ins_id;
        this.node_id = node_id;
        this.userview=userview;
        dao = new ContentDAO(this.conn);

    }
    /**   
     * @Title: getBeanList   
     * @Description:返回前台使用的数据bean    
     * @param @return
     * @param @throws GeneralException 
     * @return ArrayList    
     * @throws   
    */
    public ArrayList getBeanList() throws GeneralException { 
        synSpLevel();
        return getBeanList(getXml());
    }
    
    /**   
     * @Title: SetBeanListFromElement   
     * @Description: 设置前台使用的数据bean   
     * @param @param list
     * @param @param eleApprove
     * @param @param bBack
     * @param @throws GeneralException 
     * @return void    
     * @throws   
    */
    private void SetBeanListFromElement(ArrayList list,
              Element eleApprove,boolean bBack) throws GeneralException {
   
        try {
            if (eleApprove != null) {
                if (eleApprove.getChildren("level") != null) {
                    List elelist = (List) eleApprove.getChildren("level");

                    for (int i = 0; i < elelist.size(); i++) {
                        Element eleLevel = (Element) elelist.get(i);
                        LazyDynaBean bean = new LazyDynaBean();
                        bean.set("tabid", String.valueOf(this.tab_id));
                        bean.set("task_id", String.valueOf(this.task_id));
                        bean.set("ins_id", String.valueOf(this.ins_id));
                        String levelnum = eleLevel.getAttributeValue("levelnum");
                        bean.set("levelnum", levelnum);
                        if (bBack){  //报备人员                          
                            bean.set("canCheck", "0");
                            bean.set("levelDesc", ResourceFactory.getProperty("t_template.approve.reportBack") );
                            bean.set("bsflag", "3");
                        }
                        else {
                            bean.set("canCheck", "1");                            
                            bean.set("levelDesc", translate(levelnum));
                            bean.set("bsflag", "1");
                        }
                        list.add(bean);
                        
                        ArrayList personlist = new ArrayList();
                             
                        if (eleLevel.getChildren("actor") != null) {
                            List actorList = (List) eleLevel.getChildren("actor");
                            for (int j = 0; j < actorList.size(); j++) {
                                Element actorLevel = (Element) actorList.get(j);

                                LazyDynaBean personbean = new LazyDynaBean();
                                personbean.set("actorid", actorLevel.getAttributeValue("actorid"));              
                                personbean.set("personname",actorLevel.getAttributeValue("actorname"));
                                personbean.set("id", actorLevel.getAttributeValue("id"));
                                personlist.add(personbean);
                            }
                        }
                        bean.set("personlist", personlist);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }     
    /**   
     * @Title: getBeanList   
     * @Description: 返回前台使用的数据bean   
     * @param @param strXml
     * @param @return
     * @param @throws GeneralException 
     * @return ArrayList    
     * @throws   
    */
    public ArrayList getBeanList(String strXml) throws GeneralException {
        ArrayList list = new ArrayList();
        try {
            Document doc = PubFunc.generateDom(strXml);;
            String xpath = "//flow";
            XPath xpath_ = XPath.newInstance(xpath);
            Element root = (Element) xpath_.selectSingleNode(doc);
            if (root != null) {
                Element eleApprove = root.getChild("reportapprove");
                SetBeanListFromElement(list,eleApprove,false);
                
                Element eleback = root.getChild("reportback");                
                SetBeanListFromElement(list,eleback,true);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return list;

    }  
    
    /**   
     * @Title: getCommonSqlWhere   
     * @Description: 公用sql条件   
     * @param @return
     * @param @throws GeneralException 
     * @return String    
     * @throws   
    */
    private String getCommonSqlWhere() throws GeneralException {
        
        StringBuffer strWhere = new StringBuffer();
        strWhere.append(" tabid=");
        strWhere.append(String.valueOf(this.tab_id));
        strWhere.append(" and task_id=");
        strWhere.append(String.valueOf(this.task_id));
        strWhere.append(" and ins_id=");
        strWhere.append(String.valueOf(this.ins_id));
        strWhere.append(" and node_id=");
        strWhere.append(String.valueOf(this.node_id));
        if (this.ins_id == -1) {
            strWhere.append(" and create_user='");
            strWhere.append(this.userview.getUserName());
            strWhere.append("'");

        } else {
            ;
        }
        return strWhere.toString();
    }
    
    /**   
     * @Title: getXml   
     * @Description:以xml格式返回其他使用的数据bean    
     * @param @return
     * @param @throws GeneralException 
     * @return String    
     * @throws   
    */
    public String getXml() throws GeneralException {
        String strValue="";
        Element root = null;
        Element approveNode =null;
        Element levelnode = null;
        Element backNode =null;
        Element backlevelnode = null;
        Element actornode = null;        
        StringBuffer strSql = new StringBuffer();
        strSql.append("select * from t_wf_node_manual where ");
        strSql.append(getCommonSqlWhere());
        strSql.append(" order by bs_flag,sp_level,seq");
        try {
            root = new Element("flow");    
            approveNode = new Element("reportapprove");
            root.addContent(approveNode);
            backNode = new Element("reportback");
            root.addContent(backNode);
            
            backlevelnode = new Element("level");
            backlevelnode.setAttribute("levelnum", "1");
            backNode.addContent(backlevelnode);  
            
            String oldLevel = "";
            RowSet rset = dao.search(strSql.toString());
            while (rset.next()) {
                String bs_flag = rset.getString("bs_flag");
                String level = rset.getString("sp_level");
                String Actorname = rset.getString("actorname");
                if ("3".equals(bs_flag)){//报备
                    ;              
                }  
                else {//报批
                    if (!level.equals(oldLevel)) {                     
                        oldLevel = level;                      
                        levelnode = new Element("level");
                        levelnode.setAttribute("levelnum", level);
                        approveNode.addContent(levelnode);            
                    }                  
                }
                actornode = new Element("actor");
                actornode.setAttribute("id", rset.getString("id"));   
                String actorid = rset.getString("actorid");
                String A0101 = getA0101(actorid);
                actornode.setAttribute("actorid", actorid);
                actornode.setAttribute("actorname", A0101);
                actornode.setAttribute("seq", rset.getString("seq"));
                if ("3".equals(bs_flag)){//报备
                    backlevelnode.addContent(actornode);
                }  
                else {//报批
                    levelnode.addContent(actornode);           
                } 
            }           
            rset.close();
            Document myDocument = new Document(root);
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            strValue = outputter.outputString(myDocument);
     
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        this.strXml =strValue;
        return strValue;

    }
    
    
    /**   
     * @Title: AddLevel   
     * @Description: 增加审批层级   
     * @param @param strXml
     * @param @return
     * @param @throws GeneralException 
     * @return HashMap    
     * @throws   
    */
    public HashMap AddLevel(String strXml) throws GeneralException {
        HashMap map = new HashMap();
        try {
            Document doc = PubFunc.generateDom(strXml);;
            String xpath = "//flow";
            XPath xpath_ = XPath.newInstance(xpath);
            Element root = (Element) xpath_.selectSingleNode(doc);
            int maxlevel=0;
            if (root != null) {
                Element eleApprove = root.getChild("reportapprove");
               //取得最大level                
                if (eleApprove.getChildren("level") != null) {
                    List elelist = (List) eleApprove.getChildren("level");
                    for (int i = 0; i < elelist.size(); i++) {
                        Element eleLevel = (Element) elelist.get(i);                       
                        String levelnum = eleLevel.getAttributeValue("levelnum");
                        if (levelnum!=null){                            
                            if (Integer.parseInt(levelnum)>maxlevel) {
                                maxlevel = Integer.parseInt(levelnum);
                            }
                        }
                    }
                }
                maxlevel++;
                Element levelnode = new Element("level");
                levelnode.setAttribute("levelnum", String.valueOf(maxlevel));
                eleApprove.addContent(levelnode); 
                
            }
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            this.strXml = outputter.outputString(doc);
            map.put("levelnum", maxlevel+"");
            map.put("leveldesc", translate(String.valueOf(maxlevel)));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return map;

    } 
    
    /**   
     * @Title: delLevel   
     * @Description: 删除审批层级   
     * @param @param strXml
     * @param @param levels
     * @param @return
     * @param @throws GeneralException 
     * @return HashMap    
     * @throws   
    */
    public HashMap delLevel(String strXml,String levels) throws GeneralException {
        HashMap map = new HashMap();
        try {     
            levels =","+levels+",";
            String levelDesc="";
            String sql="";
            Document doc = PubFunc.generateDom(strXml);;
            String xpath = "//flow";
            XPath xpath_ = XPath.newInstance(xpath);
            Element root = (Element) xpath_.selectSingleNode(doc);
            if (root != null) {
                Element eleApprove = root.getChild("reportapprove");             
                if (eleApprove.getChildren("level") != null) {
                    List elelist = (List) eleApprove.getChildren("level");
                    for (int i = elelist.size()-1; i >=0; i--) {
                        Element eleLevel = (Element) elelist.get(i);   
                        String levelnum = eleLevel.getAttributeValue("levelnum");
                        if (levels.indexOf(","+levelnum+",")>-1){                            
                           // eleApprove.removeContent(eleLevel);  
                            elelist.remove(i); 
                            sql="delete from t_wf_node_manual where "
                                + getCommonSqlWhere()
                                +" and bs_flag=1"
                                +" and sp_level=" +levelnum;
                            this.dao.update(sql);
                        }    
                    }
                }
                      
            }
            //更改审批层级 顺序前移
            if (root != null) {
                Element eleApprove = root.getChild("reportapprove");             
                if (eleApprove.getChildren("level") != null) {
                    List elelist = (List) eleApprove.getChildren("level");
                    for (int i = 0; i <elelist.size(); i++) {
                        Element eleLevel = (Element) elelist.get(i);   
                        String levelnum = eleLevel.getAttributeValue("levelnum");
                        if (!String.valueOf(i+1).equals(levelnum)){                         
                            sql="update t_wf_node_manual set sp_level =" +String.valueOf(i+1)
                                + " where "
                                + getCommonSqlWhere()
                                +" and bs_flag=1"
                                +" and sp_level=" +levelnum;
                            this.dao.update(sql);
                            eleLevel.setAttribute("levelnum",String.valueOf(i+1)); 
                        }    
                        levelDesc =levelDesc+ translate(String.valueOf(i+1))+",";
                    }
                }
                      
            }
            
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            this.strXml = outputter.outputString(doc);
            map.put("levelDescs", levelDesc);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return map;
    }   
    
    /**   
     * @Title: getMaxId   
     * @Description:返回当前审批层级的最大编号    
     * @param @param bs_flag
     * @param @param sp_level
     * @param @return
     * @param @throws GeneralException 
     * @return int    
     * @throws   
    */
    private int getMaxId(int bs_flag,int sp_level) throws GeneralException {
        int id = 0;
        try { 
            String sql="select max(id) as id from t_wf_node_manual where "
                       + this.getCommonSqlWhere()
                       + " and bs_flag="+ String.valueOf(bs_flag)
                       +" and sp_level ="+ String.valueOf(sp_level);
     
            RowSet rSet =dao.search(sql);
            if (rSet.next()){//
               id = rSet.getInt(1); 
            }
            rSet.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return id;
    }
    /**   
     * @Title: getA0101   
     * @Description:根据编号获取自助用户的姓名    
     * @param @param usrA0100
     * @param @return
     * @param @throws GeneralException 
     * @return String    
     * @throws   
    */
    private String getA0101(String usrA0100) throws GeneralException {
        String sinfo = "";
        try { 
            String base = usrA0100.substring(0,3);
            String A0100= usrA0100.substring(3, usrA0100.length()); 
            String sql ="select * from "+base+"A01 where A0100 ='"+A0100+"'";
                      
            RowSet rSet =dao.search(sql);
            if (rSet.next()){//
                String E0122 = rSet.getString("e0122");
                String E01A1 = rSet.getString("e01a1");
                String A0101 = rSet.getString("a0101");
               
                E0122 = AdminCode.getCodeName("UM", E0122);
                E01A1 = AdminCode.getCodeName("@K", E01A1);
        
               
                if(E0122!=null&&E0122.length()>0) {
                    sinfo+=E0122+"/";
                }
                if(E01A1!=null&&E01A1.length()>0) {
                    sinfo+=E01A1 +"/";
                }
                sinfo+=A0101;
                
  //             sinfo = E0122 +"/"+E01A1 +"/"+A0101; 
            }
            rSet.close(); 
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return sinfo;
    }

    /**   
     * @Title: addPerson   
     * @Description:新增审批人员    
     * @param @param strXml
     * @param @param bs_flag
     * @param @param levelnum
     * @param @param A0100s
     * @param @return
     * @param @throws GeneralException 
     * @return HashMap    
     * @throws   
    */
    public HashMap addPerson(String strXml,String bs_flag,String levelnum,String A0100s) throws GeneralException {
        HashMap map = new HashMap();
        try {    
            String ids="";
            String A0101Descs="";
            String levelDesc="";
            RowSet rSet=null;
            String sql="";
            Document doc = PubFunc.generateDom(strXml);;
            String xpath = "//flow";
            XPath xpath_ = XPath.newInstance(xpath);
            Element root = (Element) xpath_.selectSingleNode(doc);
            if (root != null) {
                Element eleApprove =null;
                if ("1".equals(bs_flag)) {
                    eleApprove = root.getChild("reportapprove");
                } else {
                    eleApprove = root.getChild("reportback");
                }
                if (eleApprove.getChildren("level") != null) {
                    List elelist = (List) eleApprove.getChildren("level");
                    for (int i = elelist.size()-1; i >=0; i--) {
                        Element eleLevel = (Element) elelist.get(i);   
                        String slevel = eleLevel.getAttributeValue("levelnum");
                        if (levelnum.equals(slevel)){ //增加审批人  
                            String[] arrA0100=A0100s.split(",");
                          //取得此层级最大顺序号
                            int seq=0;
                            sql="select max(seq) as seq from t_wf_node_manual where "
                                + getCommonSqlWhere()
                                +" and bs_flag="+bs_flag
                                +" and sp_level=" +levelnum;
                            rSet =dao.search(sql);
                            if (rSet.next()){//
                               seq = rSet.getInt(1); 
                            }
                            rSet.close();
                            //循环新增人员
                            for (int j=0;j<arrA0100.length;j++){
                                String usrA0100 = arrA0100[j];
                                if (("".equals(usrA0100))||(",".equals(usrA0100))){
                                    continue;
                                }
                                //是否存在此审批人
                                sql="select * from t_wf_node_manual where "
                                    + getCommonSqlWhere()
                                    +" and bs_flag="+bs_flag
                                    +" and sp_level=" +levelnum
                                    +" and upper(actorid)='"+usrA0100.toUpperCase()+"'";
                                rSet =dao.search(sql);
                                if (rSet.next()){//存在此人
                                    continue;  
                                }                                

                                //新增数据
                                seq++;
                                String A0101Desc =getA0101(usrA0100);
                                int k= A0101Desc.lastIndexOf("/");
                                
                                /*                                
                                RecordVo recvo=new RecordVo("t_wf_node_manual");
                                recvo.setInt("tabid", this.tab_id);
                                recvo.setInt("node_id", 0);
                                recvo.setInt("task_id",0);
                                recvo.setInt("ins_id", -1);
                                recvo.setString("create_user", this.userview.getUserName());
                                recvo.setInt("bs_flag", Integer.parseInt(bs_flag));
                                recvo.setInt("sp_level", Integer.parseInt(levelnum));
                                recvo.setInt("actor_type", 1);
                                recvo.setString("actorid", usrA0100);
                                recvo.setString("actorname", A0101Desc.substring(k+1,A0101Desc.length()));
                                recvo.setInt("seq", seq);
                                int id =getMaxId()+1;
                                recvo.setInt("id", id);
                                dao.addValueObject(recvo);   
                                */
                               
                                ArrayList filedList = new ArrayList();  
                                LazyDynaBean fieldBean=null;
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "tabid");
                                fieldBean.set("type", "N");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", String.valueOf(this.tab_id));
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "node_id");
                                fieldBean.set("type", "N");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", "0");
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "task_id");
                                fieldBean.set("type", "N");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", "0");
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "ins_id");
                                fieldBean.set("type", "N");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", "-1");
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "create_user");
                                fieldBean.set("type", "A");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", this.userview.getUserName());
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "bs_flag");
                                fieldBean.set("type", "N");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", bs_flag);
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "sp_level");
                                fieldBean.set("type", "N");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", levelnum);
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "actor_type");
                                fieldBean.set("type", "N");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", "1");
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "actorid");
                                fieldBean.set("type", "A");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", usrA0100);
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "actorname");
                                fieldBean.set("type", "A");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", A0101Desc.substring(k+1,A0101Desc.length()));
                                filedList.add(fieldBean);
                                
                                fieldBean= new LazyDynaBean();
                                fieldBean.set("itemid", "seq");
                                fieldBean.set("type", "N");
                                fieldBean.set("decimal", "0");
                                fieldBean.set("value", String.valueOf(seq));    
                                filedList.add(fieldBean);
                                
                                DbNameBo.insertNewRecord("t_wf_node_manual","id",this.conn,filedList); 
                                int id =this.getMaxId(Integer.parseInt(bs_flag), Integer.parseInt(levelnum));
                                //更新xml
                                Element actornode = new Element("actor");
                                actornode.setAttribute("id", id+"");
                                actornode.setAttribute("actorid", usrA0100);
                                actornode.setAttribute("actorname", A0101Desc);
                                actornode.setAttribute("seq", seq+"");
                                eleLevel.addContent(actornode);   
                                
                                ids =ids +"`"+String.valueOf(id);
                                A0101Descs =A0101Descs +"`"+A0101Desc;                                
                            }
                            rSet.close();
                        }    
                    }
                }
                      
            }
               
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            this.strXml = outputter.outputString(doc);
            
            if (ids.length()>0) {
                ids =ids.substring(1,ids.length());
            }
            if (A0101Descs.length()>0) {
                A0101Descs =A0101Descs.substring(1,A0101Descs.length());
            }
            map.put("ids", ids);
            map.put("A0101Descs", A0101Descs);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return map;
    }   
    
    /**   
     * @Title: delPerson   
     * @Description:删除审批人员    
     * @param @param strXml
     * @param @param id
     * @param @return
     * @param @throws GeneralException 
     * @return HashMap    
     * @throws   
    */
    public HashMap delPerson(String strXml,String id) throws GeneralException {
        HashMap map = new HashMap();
        try {    
            String ids="";
            String A0101Descs="";
            RowSet rSet=null;
            String sql="";
            Document doc = PubFunc.generateDom(strXml);;
            String xpath = "//flow";
            XPath xpath_ = XPath.newInstance(xpath);
            Element root = (Element) xpath_.selectSingleNode(doc);
            String bs_flag ="1";
            String levelnum ="1";
            if (root != null) {
                sql = "select * from t_wf_node_manual where " + " id=" + id;
                rSet = dao.search(sql);
                if (rSet.next()) {//
                    bs_flag = rSet.getString("bs_flag");
                    levelnum = rSet.getString("sp_level");
                }
                rSet.close();
                Element eleApprove = null;
                if ("1".equals(bs_flag)) {
                    eleApprove = root.getChild("reportapprove");
                } else {
                    eleApprove = root.getChild("reportback");
                }
                if (eleApprove.getChildren("level") != null) {
                    List elelist = (List) eleApprove.getChildren("level");
                    for (int i = elelist.size() - 1; i >= 0; i--) {
                        Element eleLevel = (Element) elelist.get(i);
                        String slevel = eleLevel.getAttributeValue("levelnum");
                        if (levelnum.equals(slevel)) {
                            List actorlist = (List) eleLevel.getChildren("actor");
                            for (int j = actorlist.size() - 1; j >= 0; j--) {
                                Element eleActor = (Element) actorlist.get(j);
                                String sid = eleActor.getAttributeValue("id");
                                if (id.equals(sid)) {
                                    // 删除此人
                                    sql = "delete from t_wf_node_manual where " + " id=" + id;
                                    dao.update(sql);
                                    // 更新xml
                                    actorlist.remove(j);
                                    break;
                                }
                            }
                            for (int j =0; j < actorlist.size(); j++) {
                                Element eleActor = (Element) actorlist.get(j);
                                String sid = eleActor.getAttributeValue("id");   
                                String actorname = eleActor.getAttributeValue("actorname");   
                                ids =ids +"`"+sid;
                                A0101Descs =A0101Descs +"`"+actorname;      
                            }
                        }
                    }

                }
            }
               
            XMLOutputter outputter = new XMLOutputter();
            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            this.strXml = outputter.outputString(doc);
                  
            map.put("bs_flag", bs_flag);
            map.put("levelnum", levelnum);
            if (ids.length()>0) {
                ids =ids.substring(1,ids.length());
            }
            if (A0101Descs.length()>0) {
                A0101Descs =A0101Descs.substring(1,A0101Descs.length());
            }
            map.put("ids", ids);
            map.put("A0101Descs", A0101Descs);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return map;
    }   
    
    /**   
     * @Title: synSpLevel   
     * @Description: 同步审批层级，将断层去掉   
     * @param @throws GeneralException 
     * @return void    
     * @throws   
    */
    public void synSpLevel() throws GeneralException {
   
        StringBuffer strSql = new StringBuffer();
        strSql.append("select sp_level from t_wf_node_manual where ");
        strSql.append(getCommonSqlWhere());
        strSql.append(" and bs_flag =1");
        strSql.append(" group by sp_level order by sp_level");
        try {
            int num=1;
            RowSet rset = dao.search(strSql.toString());
            boolean bHaveSp=false;
            while (rset.next()) {
                bHaveSp=true;
                int sp_level = rset.getInt("sp_level");
                if (num!=sp_level){
                    String sql="update t_wf_node_manual set sp_level =" +String.valueOf(num)
                    + " where "
                    + getCommonSqlWhere()
                    +" and bs_flag=1"
                    +" and sp_level=" +String.valueOf(sp_level);
                   this.dao.update(sql);                    
                }
                num++;
            } 
            if (!bHaveSp){
                String sql="delete from  t_wf_node_manual"
                + " where "
                + getCommonSqlWhere()
                +" and bs_flag=3";
               this.dao.update(sql);                   
            }
            rset.close();
     
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }   
    
    
    /**   
     * @Title: translate   
     * @Description:将数字翻译成汉字描述    
     * @param @param input
     * @param @return 
     * @return String    
     * @throws   
    */
    public String translate(String input) {
        String[] num=new String[] {"零","一","二","三","四","五","六","七","八","九"};
        String[] unit=new String[]{"","十","百","千","万","亿"};
        String[] mid=new String[input.length()];
        String output="";
        
        //转换数字
        for (int i=0; i<input.length(); i++) {
                mid[i]=num[Integer.parseInt(""+input.charAt(i))];
                output+=mid[i];
        }
              
        String str="";
        String result="";
        for (int i=0; i<output.length(); i++) {
                if (output.length()-i-1==0) {
                        str=""+output.charAt(i);
                }
                else if ((output.length()-i-1+4)%8==0) {
                        str=output.charAt(i)+unit[4];
                }
                else if ((output.length()-i-1)%8==0) {
                        str=output.charAt(i)+unit[5];
                }
                else {
                        str=output.charAt(i)+unit[(output.length()-i-1)%4];
                }
                result+=str;
        }

        //格式化成中文习惯表达
        result=result.replaceAll("零[千百十]", "零");
        result=result.replaceAll("亿零+万", "亿零");
        result=result.replaceAll("万零+亿", "万亿");
        result=result.replaceAll("零+", "零");
        result=result.replaceAll("零万", "万");
        result=result.replaceAll("零亿", "亿");
        result=result.replaceAll("^一十", "十");
        result=result.replaceAll("零$", "");
        return result+ResourceFactory.getProperty("t_template.approve.numSp") ;
}
    public String getStrXml() {
        return strXml;
    }
    public void setStrXml(String strXml) {
        this.strXml = strXml;
    }  
}
