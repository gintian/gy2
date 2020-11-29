package com.hjsj.hrms.businessobject.general.approve.personinfo;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.structuresql.A0100Bean;
import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PersonInfoBo {
    private ContentDAO dao;
    private Connection conn;
    private String nbase;
    private String b0110;
    private String e0122;
    private String a0100;
    private String keyvalue;
    public static TreeMap changelist;
    private boolean isBatchReject = false;
    public String getB0110() {
		return b0110;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getE0122() {
		return e0122;
	}

	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getA0101() {
		return a0101;
	}

	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}

	private String a0101;
    private Document doc;
   // private String sp_idea;
    private UserView userview;
    private boolean isChecked_may_reject = false;
    
    public PersonInfoBo(Connection conn,String chg_id,UserView userview){
    	this.dao=new ContentDAO(conn);
    	this.conn=conn;
    	this.userview = userview;
    	getChangeInfo(chg_id);
    	String may_reject = SystemConfig.getPropertyValue("checked_may_reject");// 审核后可驳回
																				// 2010.01.08
																				// s.xin加
		may_reject = may_reject != null && may_reject.trim().length() > 0 ? may_reject: "false";
		
		if (may_reject != null || "true".equalsIgnoreCase(may_reject)) {
            this.isChecked_may_reject = true;
        } else {
            this.isChecked_may_reject = false;
        }
    }
    
    /**
     * 查询操作信息
     * @param chg_id
     */
	public void getChangeInfo(String chg_id)
    {
    	StringBuffer sql = new StringBuffer("select nbase,b0110,e0122,a0100,a0101,content,sp_flag,description from t_hr_mydata_chg ");
    	sql.append(" where chg_id='"+chg_id+"' ");
    	RowSet rs =null;
    	String xmlcontent = "";
    	
		try {
			 rs = dao.search(sql.toString());
			if (rs.next()) {
				this.nbase = rs.getString("nbase");
				this.b0110=rs.getString("b0110");
				this.e0122=rs.getString("e0122");
				this.a0100=rs.getString("a0100");
				this.a0101=rs.getString("a0101");
				xmlcontent = rs.getString("content");
				//this.sp_idea = rs.getString("sp_idea");
			}
			if (xmlcontent == null || "".equals(xmlcontent)) {
				xmlcontent = "";
			}
            createDoc(xmlcontent);			

		} catch (Exception ex) {
			ex.printStackTrace();
		}finally
		{
			PubFunc.closeResource(rs);
		}
    	
    }
    
	//创建xml文档
    public void createDoc(String xmlcontent){
    	 Document document = null;
    	if (xmlcontent == null || xmlcontent.length() <= 0) {
			StringBuffer strxml = new StringBuffer();
			strxml.append("<?xml version='1.0' encoding='UTF-8' ?>");
			strxml.append("<root>");
			strxml.append("</root>");
			xmlcontent = strxml.toString();
		}
		try {
			document = PubFunc.generateDom(xmlcontent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	
		this.doc=document;
    }
    

    //查询指标 itemdesc和code id
    public FieldItem getcolumndesc(ArrayList itemlist,String itemid){
     //	HashMap iteminfo=new HashMap();
    	FieldItem fi = null;
    	for(int i=0;i<itemlist.size();i++){
    		FieldItem fis=(FieldItem)itemlist.get(i);
		    		if(itemid.equals(fis.getItemid().toLowerCase())){
		    			fi = fis;
		    	          break;
		    		}
    	}
    	return fi; 
    }
    
    
    //读取xml文档 
    public TreeMap changelist(){
    	 TreeMap changelist = new TreeMap();
    	
    	try{
	    	String xpath = "/root/setid";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			MultiMediaBo bo = new MultiMediaBo(conn, userview);
            String rootPath = bo.getRootDir();
            if(StringUtils.isNotEmpty(rootPath)) {
                rootPath = rootPath.replace("\\", File.separator) + File.separator + "multimedia" + File.separator;
            }
            
			Iterator setite=childlist.iterator();
			while(setite.hasNext()){
				 Element fieldsetE=(Element)setite.next();
				 String setid=fieldsetE.getAttributeValue("name");
				 ArrayList itemlist=userview.getPrivFieldList(setid.toUpperCase());
				 if(itemlist == null || itemlist.size()<1) {
                     continue;
                 }
				 
				//子集排序标示
				 FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				 int displayorder = fieldset.getDisplayorder();
				 
				 LazyDynaBean sadb = new LazyDynaBean();
				 sadb.set("setid", setid.toUpperCase());
				 sadb.set("setdesc", fieldset.getCustomdesc());
				 
					 
				 ArrayList recordlist=new ArrayList();
				 
				 childlist = fieldsetE.getChildren();
				 if(childlist == null || childlist.size()<1) {
                     continue;
                 }
				 Iterator recite = childlist.iterator();
				 LinkedHashMap columns=new LinkedHashMap();
				 
				 
				 if("A01".equals(setid.toUpperCase())){
					 
					 while(recite.hasNext()){
						 Element recordE=(Element)recite.next();
						 LazyDynaBean rldb=new LazyDynaBean();
						 rldb.set("type", "update");
						 rldb.set("recordid", recordE.getAttributeValue("keyvalue"));
						 rldb.set("sp_flag", recordE.getAttributeValue("sp_flag"));
						 sadb.set("sp_flag", recordE.getAttributeValue("sp_flag"));
						 rldb.set("sequence", recordE.getAttributeValue("sequence"));
						 
						 childlist = recordE.getChildren("multimedia");
						 if(childlist.size()>0){
							 rldb.set("multimedia", "true");
							 sadb.set("multimedia", "true");
							 String type;
							 ArrayList muList = new ArrayList();
						     ContentDAO dao = new ContentDAO(this.conn);
						     
							 for(int i=0;i<childlist.size();i++){
								 Element mulEl = (Element)childlist.get(i);
								 LazyDynaBean mldb=new LazyDynaBean();
								 type = mulEl.getAttributeValue("type");
								 mldb.set("type", type);
								 if("delete".equals(type)){
									 RecordVo vo = new RecordVo("hr_multimedia_file");
									 vo.setString("id",mulEl.getAttributeValue("fileid"));
									 if(!dao.isExistRecordVo(vo)) {
                                         continue;
                                     }
									 
									 mldb.set("topic", vo.getString("topic"));
									 mldb.set("desc", vo.getString("description"));
									 mldb.set("filename", vo.getString("filename"));
									 mldb.set("srcfilename", vo.getString("srcfilename"));
									 mldb.set("path", rootPath + vo.getString("path"));
								 }else{
									 mldb.set("topic", mulEl.getAttributeValue("topic"));
									 mldb.set("desc", mulEl.getAttributeValue("description"));
									 mldb.set("filename", mulEl.getAttributeValue("filename"));
									 mldb.set("srcfilename",mulEl.getAttributeValue("srcfilename"));
									 mldb.set("path", mulEl.getAttributeValue("path"));
								 }
								 muList.add(mldb);
							 }
							 sadb.set("multimedialist", muList);
						 }
						 
						 
						 childlist = recordE.getChildren("column");
						 if(childlist == null || childlist.size()<1) {
                             continue;
                         }
						 Iterator cite=childlist.iterator();
						 boolean hasChg = false;
						 while(cite.hasNext()){
							 Element columnE = (Element)cite.next();
							 String itemid = columnE.getAttributeValue("name");
							 String newvalue =  columnE.getAttributeValue("newvalue");
							 String oldvalue = columnE.getAttributeValue("oldvalue");
							 String newvalue1 = newvalue;
							   
							  FieldItem fi = getcolumndesc(itemlist,itemid);
									 
									 if(fi==null) {
                                         continue;
                                     }
									 
										 if(!newvalue.equals(oldvalue)){
										       columns.put(itemid,fi);
										       LazyDynaBean cldb=new LazyDynaBean();
										       String codesetid = fi.getCodesetid().toUpperCase();
											 if("0".equals(codesetid)){
										        cldb.set("newvalue", newvalue1);
											    cldb.set("oldvalue", oldvalue);
											 }else{
												     if("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)){
														 cldb.set("newvalue", getcodedesc(null,newvalue1));
														 cldb.set("oldvalue", getcodedesc(null,oldvalue));
				           							 }else{
				           								cldb.set("newvalue", getcodedesc(codesetid,newvalue1));
														 cldb.set("oldvalue", getcodedesc(codesetid,oldvalue));
				           							 }
											 }
											 cldb.set("changeflag", "N");
											 rldb.set(itemid, cldb);
											 hasChg = true;
										 }
						 }
						if(hasChg) {
                            recordlist.add(rldb);
                        }
					 }					 
				 }else{
					 
				 
						 while(recite.hasNext()){
							 Element recordE=(Element)recite.next();
							 LazyDynaBean rldb=new LazyDynaBean();
							 String type = recordE.getAttributeValue("type");
							 String spflag = recordE.getAttributeValue("sp_flag");
							 rldb.set("recordid", recordE.getAttributeValue("keyvalue"));
							 rldb.set("type", type);
							 if("03".equals(spflag)) {
                                 rldb.set("type", "select");
                             }
							 rldb.set("sp_flag", spflag);
							 rldb.set("sequence", recordE.getAttributeValue("sequence"));
							 
							 if(recordE.getChildren("multimedia").size()>0){
								 rldb.set("multimedia", "true");
								 sadb.set("multimedia", "true");
							 }
							 
							 childlist = recordE.getChildren("column");
							 if(childlist == null || childlist.size()<1) {
							     recordlist.add(rldb);
								 continue;
							 }
							 
							 Iterator cite=childlist.iterator();
							 while(cite.hasNext()){
								 Element columnE = (Element)cite.next();
								 String itemid = columnE.getAttributeValue("name");
								 String newvalue =  columnE.getAttributeValue("newvalue");
								 String oldvalue = columnE.getAttributeValue("oldvalue");
								 String newvalue1 = "";
								   if("delete".equals(type)) {
                                       newvalue1 = oldvalue;
                                   } else{
									    newvalue1 = newvalue;
								   }
								   FieldItem fi = getcolumndesc(itemlist,itemid);
										 
										 if(fi==null) {
                                             continue;
                                         }
										 
											 if(!newvalue.equals(oldvalue)){
											       columns.put(itemid,fi);
											       LazyDynaBean cldb=new LazyDynaBean();
											       String codesetid = fi.getCodesetid().toUpperCase();
												 if("0".equals(codesetid)){
											        cldb.set("newvalue", newvalue1);
												    cldb.set("oldvalue", oldvalue);
												 }else{
													     if("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)){
															 cldb.set("newvalue", getcodedesc(null,newvalue1));
															 cldb.set("oldvalue", getcodedesc(null,oldvalue));
					           							 }else{
					           								cldb.set("newvalue", getcodedesc(codesetid,newvalue1));
															 cldb.set("oldvalue", getcodedesc(codesetid,oldvalue));
					           							 }
												 }
												 cldb.set("changeflag", "N");
												 rldb.set(itemid, cldb);
											 }
							 }
							 
							 recordlist.add(rldb);
							
						 }
				 }
				 sadb.set("itemlist", recordlist);
				 sadb.set("columns", columns);
				 sadb.set("showsp", "true"); 
				 changelist.put(new Integer(displayorder), sadb);
			}
			
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	
    	this.changelist = changelist;
    	return changelist;
    }

     public TreeMap alllist(){
    	 TreeMap changelist = new TreeMap();
    	 
    	 try{
    	     MultiMediaBo bo = new MultiMediaBo(conn, userview);
    	     String rootPath = bo.getRootDir();
    	     if(StringUtils.isNotEmpty(rootPath)) {
                 rootPath = rootPath.replace("\\", File.separator) + File.separator + "multimedia" + File.separator;
             }
    	     
    	     List setlist =  userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
    	     ArrayList infolist = new ArrayList();
    	     Iterator setite=setlist.iterator();
    	     List childlist = new ArrayList();
    	     int index = 0;
    	     while(setite.hasNext()){
    	         FieldSet fs = (FieldSet)setite.next();
    	         String setid=fs.getFieldsetid();
    	         String setdesc =fs.getCustomdesc();
    	         ArrayList itemlist=userview.getPrivFieldList(setid.toUpperCase());
    	         
    	         if(itemlist == null || itemlist.size()<1) {
                     continue;
                 }
    	         LazyDynaBean sadb = new LazyDynaBean();
    	         sadb.set("setid", setid.toUpperCase());
    	         sadb.set("setdesc", setdesc);
    	         
    	         //子集排序标示
    	         int displayorder =fs.getDisplayorder();
    	         //判断是否是查询的第一个信息集，如果不是就只添加信息集的fieldsetid和名称
    	         if(index != 0){
    	             index++;
    	             changelist.put(new Integer(displayorder), sadb);
    	             continue;
    	         }
    	         
    	         index++;
    	         String xpath = "/root/setid[@name='"+setid+"']";
    	         XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
    	         Element  fieldsetE= (Element) reportPath.selectSingleNode(doc);
    	         
    	         boolean showsp = false;
    	         
    	         ArrayList recordlist=new ArrayList();
    	         LinkedHashMap columns=new LinkedHashMap();
    	         for(int i=0;i<itemlist.size();i++){
    	             FieldItem fi = (FieldItem)itemlist.get(i);
    	             columns.put(fi.getItemid(),fi);
    	         }
    	         sadb.set("columns", columns);
    	         infolist = getinfolist(setid,this.a0100,this.nbase,itemlist);
    	         
    	         if((infolist == null || infolist.size()<1) && fieldsetE == null) {
                     continue;
                 }
    	         
    	         Iterator recite = infolist.iterator();
    	         /////////////////////////////////////////////////////////////////////////////////////////    库里的数据和 type为update的数据    
    	         while(recite.hasNext()){
    	             LazyDynaBean infoitem = (LazyDynaBean)recite.next();
    	             LazyDynaBean rldb=new LazyDynaBean();
    	             
    	             boolean flag = false;
    	             if(!"A01".equals(setid) && fieldsetE!=null){
    	                 xpath="record[@keyvalue='"+infoitem.get("i9999")+"' and (@type='update' or @sp_flag='03')]";
    	                 reportPath = XPath.newInstance(xpath);
    	                 childlist = reportPath.selectNodes(fieldsetE);
    	             }else if(fieldsetE!=null){
    	                 
    	                 childlist = fieldsetE.getChildren();
    	             }else{
    	                 childlist = null;
    	             }
    	             if(childlist!=null && childlist.size()>0){
    	                 
    	                 flag = true;
    	                 showsp = true;
    	                 HashMap changevalue = new HashMap();
    	                 
    	                 ArrayList splist = new ArrayList();
    	                 
    	                 for(int i=0;i<childlist.size();i++){
    	                     
    	                     Element recordEs = (Element)childlist.get(i);
    	                     String type = recordEs.getAttributeValue("type");
    	                     rldb.set("type", type);
    	                     rldb.set("recordid", recordEs.getAttributeValue("keyvalue"));
    	                     rldb.set("sequence", recordEs.getAttributeValue("sequence"));
    	                     splist.add(recordEs.getAttributeValue("sp_flag"));
    	                     
    	                     List mulElList = (List)recordEs.getChildren("multimedia");
    	                     if(mulElList.size()>0){
    	                         rldb.set("multimedia", "true");
    	                         sadb.set("multimedia", "true");
    	                         
    	                         if("A01".equals(setid)){
    	                             String multype;
    	                             ArrayList muList = new ArrayList();
    	                             ContentDAO dao = new ContentDAO(this.conn);
    	                             for(int k=0;k<mulElList.size();k++){
    	                                 Element mulEl = (Element)mulElList.get(k);
    	                                 LazyDynaBean mldb=new LazyDynaBean();
    	                                 multype = mulEl.getAttributeValue("type");
    	                                 mldb.set("type", multype);
    	                                 if("delete".equals(multype)){
    	                                     RecordVo vo = new RecordVo("hr_multimedia_file");
    	                                     vo.setString("id",mulEl.getAttributeValue("fileid"));
    	                                     if(!dao.isExistRecordVo(vo)) {
                                                 continue;
                                             }
    	                                     mldb.set("topic", vo.getString("topic"));
    	                                     mldb.set("desc", vo.getString("description"));
    	                                     mldb.set("filename", vo.getString("filename"));
    	                                     mldb.set("path", rootPath + vo.getString("path"));
    	                                     mldb.set("srcfilename", vo.getString("srcfilename"));
    	                                 }else{
    	                                     mldb.set("topic", mulEl.getAttributeValue("topic"));
    	                                     mldb.set("desc", mulEl.getAttributeValue("description"));
    	                                     mldb.set("filename", mulEl.getAttributeValue("filename"));
    	                                     mldb.set("path", mulEl.getAttributeValue("path"));
    	                                     mldb.set("srcfilename", mulEl.getAttributeValue("srcfilename"));
    	                                 }
    	                                 muList.add(mldb);
    	                             }
    	                             sadb.set("multimedialist", muList);
    	                         }
    	                     }
    	                     
    	                     
    	                     
    	                     
    	                     List collist = recordEs.getChildren("column");
    	                     Iterator cite = collist.iterator();
    	                     while(cite.hasNext()){
    	                         Element columnE = (Element)cite.next();
    	                         String itemid = columnE.getAttributeValue("name");
    	                         String newvalue =  columnE.getAttributeValue("newvalue");
    	                         String oldvalue = columnE.getAttributeValue("oldvalue");
    	                         if(!newvalue.equals(oldvalue.toString())){
    	                             String[] diff = {newvalue,oldvalue};
    	                             changevalue.put(itemid,diff);
    	                         }
    	                     }
    	                     
    	                 }
    	                 
    	                 
    	                 String spflag = ""; 
    	                 for(int i=0;i<splist.size();i++){
    	                     if("02".equals(splist.get(i))){
    	                         spflag="02";
    	                         break;
    	                     }else if("03".equals(splist.get(i))){
    	                         spflag="03";
    	                         break;
    	                     }else{
    	                         spflag= "07";
    	                     }
    	                     
    	                 }
    	                 rldb.set("sp_flag", spflag);
    	                 
    	                 
    	                 
    	                 for(int i=0;i<itemlist.size();i++){
    	                     FieldItem fi = (FieldItem)itemlist.get(i);
    	                     String itemid = fi.getItemid();
    	                     String[] values ={"",""};
    	                     String oldvalue = "";
    	                     String newvalue = "";
    	                     if(changevalue.containsKey(itemid)){
    	                         values = (String[])changevalue.get(itemid);
    	                         newvalue = values[0];
    	                         if(values.length>1) {
                                     oldvalue = values[1];
                                 }
    	                         
    	                     }else{
    	                         oldvalue = infoitem.get(itemid).toString();
    	                     }
    	                     LazyDynaBean cldb=new LazyDynaBean();
    	                     if(!"0".equals(fi.getCodesetid()) && fi.getCodesetid()!= null){
    	                         if("UN".equals(fi.getCodesetid()) || "UM".equals(fi.getCodesetid()) || "@K".equals(fi.getCodesetid())){
    	                             
    	                             newvalue = getcodedesc(null, newvalue);
    	                             oldvalue = getcodedesc(null, oldvalue);
    	                         }else{
    	                             newvalue = getcodedesc(fi.getCodesetid(),newvalue);
    	                             oldvalue = getcodedesc(fi.getCodesetid(), oldvalue);
    	                         }
    	                     }
    	                     if(changevalue.containsKey(itemid)){
    	                         cldb.set("newvalue", newvalue);
    	                         cldb.set("oldvalue", oldvalue);
    	                         cldb.set("changeflag", "Y");
    	                     }else{
    	                         cldb.set("newvalue", oldvalue);
    	                         cldb.set("oldvalue", oldvalue);
    	                         cldb.set("changeflag", "N");
    	                     }
    	                     rldb.set(itemid, cldb);
    	                 }
    	                 
    	                 
    	                 
    	             }
    	             
    	             if(!flag){
    	                 if("A01".equals(setid.toUpperCase())) {
                             rldb.set("type", "update");
                         } else {
                             rldb.set("type", "select");
                         }
    	                 rldb.set("sp_flag", "03");
    	                 for(int i=0;i<itemlist.size();i++){
    	                     LazyDynaBean cldb=new LazyDynaBean();
    	                     FieldItem fi = (FieldItem)itemlist.get(i);
    	                     String value = infoitem.get(fi.getItemid()).toString();
    	                     if(fi.getCodesetid()==null || "0".equals((String)fi.getCodesetid())) {
                                 cldb.set("newvalue", value);
                             } else{
    	                         
    	                         if("UN".equals(fi.getCodesetid()) || "UM".equals(fi.getCodesetid()) || "@K".equals(fi.getCodesetid())){
    	                             
    	                             value = getcodedesc(null, value);
    	                         }else{
    	                             value = getcodedesc(fi.getCodesetid(),value);
    	                         }
    	                         cldb.set("newvalue",value);
    	                         
    	                     }
    	                     cldb.set("changeflag", "N");
    	                     rldb.set(fi.getItemid(),cldb);
    	                 }
    	                 
    	             }
    	             recordlist.add(rldb);
    	         }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////还没有批准入库的数据 	
    	         if(childlist ==null && infolist.size()>0){
    	             sadb.set("itemlist", recordlist);
    	             sadb.set("showsp", new Boolean(showsp));
    	             changelist.put(new Integer(displayorder), sadb);
    	             continue;
    	         }
    	         childlist = fieldsetE.getChildren();
    	         Iterator lastite = childlist.iterator();
    	         while(lastite.hasNext()){
    	             showsp = true;
    	             Element recordEs=(Element)lastite.next();
    	             LazyDynaBean rldb2=new LazyDynaBean();
    	             String type = recordEs.getAttributeValue("type");
    	             String sp_flag0 = recordEs.getAttributeValue("sp_flag");
    	             if(( "update".equals(type) || "03".equals(sp_flag0) ) && !"delete".equals(type)) //update 上边已经加载，所以这里过滤掉。删除不进行过滤
                     {
                         continue;
                     }
    	             rldb2.set("recordid", recordEs.getAttributeValue("keyvalue"));
    	             rldb2.set("type", type);
    	             rldb2.set("sp_flag", sp_flag0);
    	             rldb2.set("sequence", recordEs.getAttributeValue("sequence"));
    	             
    	             if(recordEs.getChildren("multimedia").size()>0){
    	                 rldb2.set("multimedia", "true");
    	                 sadb.set("multimedia", "true");
    	             }
    	             
    	             childlist = recordEs.getChildren("column");
    	             Iterator cite=childlist.iterator();
    	             while(cite.hasNext()){
    	                 Element columnE = (Element)cite.next();
    	                 String itemid = columnE.getAttributeValue("name");
    	                 String newvalue =  columnE.getAttributeValue("newvalue");
    	                 if("delete".equals(type)) {
                             newvalue = columnE.getAttributeValue("oldvalue");
                         }
    	                 // String oldvalue = columnE.getAttributeValue("oldvalue");
    	                 FieldItem fi = getcolumndesc(itemlist,itemid);
    	                 
    	                 if(fi==null) {
                             continue;
                         }
    	                 
    	                 
    	                 LazyDynaBean cldb=new LazyDynaBean();
    	                 String codesetid = fi.getCodesetid().toUpperCase();
    	                 if("0".equals(codesetid)){
    	                     cldb.set("newvalue", newvalue);
    	                     // cldb.set("oldvalue", oldvalue);
    	                 }else{
    	                     if("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)){
    	                         cldb.set("newvalue", getcodedesc(null,newvalue));
    	                         // cldb.set("oldvalue", getcodedesc(null,oldvalue));
    	                     }else{
    	                         cldb.set("newvalue", getcodedesc(codesetid,newvalue));
    	                         // cldb.set("oldvalue", getcodedesc(codesetid,oldvalue));
    	                     }
    	                 }
    	                 
    	                 rldb2.set(itemid, cldb);
    	                 
    	                 
    	             }
    	             
    	             recordlist.add(rldb2);
    	             
    	         }
    	         sadb.set("itemlist", recordlist);
    	         sadb.set("showsp", new Boolean(showsp));
    	         changelist.put(new Integer(displayorder), sadb);
    	     }
    	 }catch (Exception e) {
    	     e.printStackTrace(); 				
    	 }
    	 
 		 this.changelist = changelist;
    	 return changelist;
     }


   public String getcodedesc(String codesetid,String codeid){
	   if(codeid == null || "".equals(codeid)) {
           return "";
       }
	   String sql="";
	   if(codesetid == null) {
           sql="select codeitemdesc from organization where codeitemid='"+codeid+"'";
       } else {
           sql="select codeitemdesc from codeitem where codesetid='"+codesetid+"' and codeitemid='"+codeid+"'";
       }
	   
	   RowSet rs =null;
	   String codedesc="";
	   try {
		    rs=dao.search(sql);
		    while(rs.next()){
		    	codedesc  = rs.getString("codeitemdesc");
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
	   
	   return codedesc;
   }
   
   private ArrayList getinfolist(String setname,String a0100,String nbase,ArrayList itemlist){
	   ArrayList infolist= new ArrayList(); 
	   RowSet rs =null;
	   try {
		   StringBuffer sql = new StringBuffer("select ");
		   for(int i=0;i<itemlist.size();i++){
			   FieldItem fi = (FieldItem)itemlist.get(i);
				 sql.append(fi.getItemid()+",");
		   }
		     if(!"A01".equals(setname)) {
                 sql.append("i9999, ");
             }
		     sql.append("'pig' ");
		   sql.append(" from "+nbase+setname);
		   sql.append(" where a0100="+a0100);
		    
		   rs=dao.search(sql.toString());
		   while(rs.next()){
			   LazyDynaBean ldb = new LazyDynaBean();
			   for(int i=0;i<itemlist.size();i++){
				   FieldItem fi = (FieldItem)itemlist.get(i);
				    Object value=rs.getObject(fi.getItemid());
				    if(value == null) {
                        value=" ";
                    }
				    if("D".equals(fi.getItemtype()) && !" ".equals(value)){
				    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				    	Date date = (Date) sdf.parse(value.toString());
				    	value = sdf.format(date);
				    }
				    
					 ldb.set(fi.getItemid(), value.toString());
			   }
			   if(!"A01".equals(setname)) {
                   ldb.set("i9999", rs.getString("i9999"));
               }
			   infolist.add(ldb);
		   }
		   
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		PubFunc.closeResource(rs);
	}
	   
	   return infolist;
   }
   
   private String getXmlconent() {
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		String xmlContent = outputter.outputString(this.doc);
		return xmlContent;
	}
//   public void approveInfo(String setid,String recordid,String sequence,String state,String chg_id){
//	   try {
//		   String xpath="/root/setid[@name='"+setid+"']/record[@keyvalue='"+recordid+"' and @sequence='"+sequence+"']";   //record[@keyvalue='"+recordid+"' and @sequence='"+sequence+"']
//		   XPath reportPath = XPath.newInstance(xpath);
//		   List records = reportPath.selectNodes(doc);
//		   Element record = (Element)records.get(0);
//		   if(state.equals("pz"))
//			   record.setAttribute("sp_flag", "03");
//		   if(state.equals("bh"))
//			   record.setAttribute("sp_flag", "07");
//		  
//		   XMLOutputter outputter = new XMLOutputter();
//			Format format = Format.getPrettyFormat();
//			format.setEncoding("UTF-8");
//			outputter.setFormat(format);
//			String xmlContent = outputter.outputString(this.doc);
//			
//			RecordVo vo = new RecordVo("t_hr_mydata_chg");
//			vo.setString("chg_id",chg_id);
//			vo.setString("content", xmlContent);
//			dao.updateValueObject(vo);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//   }
   
   /**
	 * 修改一个子集一项的审批标志
	 * 
	 * @param chg_id
	 * @param fieldSet
	 * @param type
	 * @param sp_flag
	 * @param keyvalue
	 * @return
	 * @throws GeneralException
	 */
	public synchronized boolean updateApployMyselfDataApp(String chg_id,
			FieldSet fieldSet, String sp_flag, String keyvalue, String type,
			String sequence) throws GeneralException {
		boolean isCorrect = false;
		A0100Bean bean = getA0100bean(chg_id);
		if (keyvalue == null || keyvalue.length() <= 0) {
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"i9999不能为空！", "", ""));
		}

		try {
			RecordVo vo = new RecordVo("t_hr_mydata_chg");
			vo.setString("chg_id", chg_id);
			vo = this.dao.findByPrimaryKey(vo);
			String content = "";
			String old_spflag = "";
			if (vo != null) {
				content = vo.getString("content");
				old_spflag = vo.getString("sp_flag");
			} else {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						"记录存在", "", ""));
			}
			createDoc(content);
			isCorrect = editMyselfData(bean, fieldSet, sp_flag, type, sequence,
					keyvalue, chg_id);
			if (!isCorrect) {
                return false;
            }
			
			String a0100 = vo.getString("a0100");
            String nbase = vo.getString("nbase");
			String xmlContent = getXmlconent();
			Date date = DateUtils.getSqlDate(Calendar.getInstance());
			vo.setDate("create_time", date);
			vo.setString("content", xmlContent);
			//获取此次人员信息变动中的所有的变动信息
			TreeMap changelist = this.changelist();
			/*报批的变动信息是否全部都已审批，
			 * flag =2：全部变动信息记录都是批准；
			 *      =1：变动信息记录中至少有一条记录是驳回状态；
			 *      =0：变动信息记录中至少有一条记录不是批准或驳回状态
			 * flag的值默认为2
			 */
			int flag = 2;
			if(changelist != null && changelist.size() > 0) {
			    Iterator it = changelist.entrySet().iterator();  
		        while (it.hasNext()) {
		            Map.Entry entry =(Map.Entry) it.next();  
			        LazyDynaBean infoBean = (LazyDynaBean) entry.getValue();;
			        if(infoBean == null) {
                        continue;
                    }
			        
			        String infor_flag = (String) infoBean.get("sp_flag");
			        ArrayList itemList = (ArrayList) infoBean.get("itemlist");
			        
			        if(StringUtils.isEmpty(infor_flag) && itemList != null && itemList.size() > 0) {
			            for(int n = 0; n < itemList.size(); n++) {
			                LazyDynaBean itemBean = (LazyDynaBean) itemList.get(n);
			                if(itemBean == null) {
                                continue;
                            }
			                
			                infor_flag = (String) itemBean.get("sp_flag");
			                
			                if(!("03".equalsIgnoreCase(infor_flag) || "07".equalsIgnoreCase(infor_flag))) {
			                    //变动信息记录中只要有一条记录不是批准或驳回的状态 flag的值就重置为0
			                    flag = 0;
			                    break;
			                } else if(flag != 1 && "07".equalsIgnoreCase(infor_flag))
			                    //变动信息记录中只要有一条记录是驳回的状态 flag的值就重置为1
                            {
                                flag = 1;
                            }
			            }
			            
			        } else {
			            if(!("03".equalsIgnoreCase(infor_flag) || "07".equalsIgnoreCase(infor_flag))) {
			                //变动信息记录中只要有一条记录不是批准或驳回的状态 flag的值就重置为0
			                flag = 0;
			                break;
			            } else if(flag != 1 && "07".equalsIgnoreCase(infor_flag))
			                //变动信息记录中只要有一条记录是驳回的状态 flag的值就重置为1
                        {
                            flag = 1;
                        }
			        }
			        
			    }
			}

			MyselfDataApprove mysel = new MyselfDataApprove();
			String orgAndName = mysel.setOrgInfo(this.userview.getDbname(), this.userview.getA0100(), this.conn);
			if("///".equals(orgAndName)) {
                orgAndName = this.userview.getUserFullName();
            }
			
			String spIdea = vo.getString("sp_idea");
			if(flag == 1) {
			    //变动信息记录中只要有一条记录是驳回的状态此次人员变动信息报批的记录外部页面的状态就将已报批或批准的状态改为退回状态
				if(old_spflag!=null&&("03".equals(old_spflag)|| "02".equals(old_spflag))&&this.isChecked_may_reject) {
					vo.setString("sp_flag", "07");
					if(!this.isBatchReject) {
						spIdea = insertXml(spIdea, ResourceFactory.getProperty("info.appleal.state2"), orgAndName);
						vo.setString("sp_idea", spIdea);
					}
				}
			} else if(flag == 2) {
			    //变动信息记录中所有的记录是批准的状态此次人员变动信息报批的记录外部页面的状态就将已报批状态改为已批状态，不能更改退回状态的记录
                if(old_spflag!=null&& "02".equals(old_spflag)&&this.isChecked_may_reject) {
                	vo.setString("sp_flag", "03");
                	if(!this.isBatchReject) {
                		spIdea = insertXml(spIdea, ResourceFactory.getProperty("info.appleal.state3"), orgAndName);
                		vo.setString("sp_idea", spIdea);
                	}
                }
                
              }
			
			String operator = "";
			if(StringUtils.isNotEmpty(spIdea)) {
			    Document doc = PubFunc.generateDom(spIdea);
			    String xpath = "/root/rec[@sp_state=\"报批\"]";
			    XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			    List<Element> childList = reportPath.selectNodes(doc);
			    if(childList != null && childList.size() > 0) {
			    	Element ele = childList.get(0);
			    	operator = ele.getAttributeValue("name");
			    	if(operator.indexOf("/") > -1) {
                        operator = operator.substring(operator.lastIndexOf("/") + 1);
                    }
			    }
			}
            
            String a0101 = vo.getString("a0101");
            this.dao.updateValueObject(vo);
            if("07".equals(sp_flag) && !this.isBatchReject && operator.equals(a0101)) {
                sendEmail(fieldSet, nbase, a0100, "");
            }
            
			isCorrect = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return isCorrect;
	}
	
	/**
	 * 向sp_idea里面插入一条xml记录 <rec sp_state="报批" name="集团总部/集团领导/null/刘兵"
	 * date="2009.12.27 15:21:56" />
	 * 
	 * @param sp_state
	 * @param name
	 */
	private String insertXml(String spIdea, String sp_state, String name) {
		try {
            Document doc = PubFunc.generateDom(spIdea);
			XPath reportPath = XPath.newInstance("/root");// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			Element ele = (Element) childlist.get(0);
			Element childEle = new Element("rec");
			childEle.setAttribute("sp_state", sp_state);
			childEle.setAttribute("name", name);
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			String date = dateFm.format(new Date());
			childEle.setAttribute("date", date);
			ele.addContent(childEle);
			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			spIdea = outputter.outputString(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return spIdea;
	}
	
	private A0100Bean getA0100bean(String chg_id) {
		StringBuffer sql = new StringBuffer();
		sql = new StringBuffer();
		sql.append("select a0100,nbase,b0110,e0122,e01a1,a0000,a0101 from t_hr_mydata_chg");
		sql.append(" where chg_id='" + chg_id + "'");
		A0100Bean bean = new A0100Bean();
		RowSet rs=null;
		try {
			rs = this.dao.search(sql.toString());
			if (rs.next()) {
				bean.setA0000(rs.getString("a0000"));
				bean.setA0101(rs.getString("a0101"));
				bean.setB0110(rs.getString("b0110"));
				bean.setE0122(rs.getString("e0122"));
				bean.setE01a1(rs.getString("e01a1"));
				bean.setA0100(rs.getString("a0100"));
				bean.setNbase(rs.getString("nbase"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			PubFunc.closeResource(rs);
		}
		return bean;
	}
	
	private boolean editMyselfData(A0100Bean bean, FieldSet fieldSet,
			String sp_flag, String type, String sequence, String keyvalue,
			String chg_id) throws GeneralException {
		boolean isCorrect = true;
		String fieldSetId = fieldSet.getFieldsetid();
		String xpath = "";
		try {
			xpath = "/root/setid[@name='" + fieldSetId + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(this.doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue='" + keyvalue + "' and @type='"
						+ type + "' and @sequence='" + sequence + "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				if (r.hasNext())// 有记录
				{
					Element SetidR = (Element) r.next();
					String ol_sp_flag = SetidR.getAttributeValue("sp_flag") != null
							&& SetidR.getAttributeValue("sp_flag").length() > 0 ? SetidR
							.getAttributeValue("sp_flag")
							: "";
					if (!"03".equals(ol_sp_flag)) {
						if ("02".equals(ol_sp_flag) && "03".equals(sp_flag)) {
							type = SetidR.getAttributeValue("type");// 得到操作类型
							sequence = SetidR.getAttributeValue("sequence");// 同一子集的排序
							isCorrect = apployEdit(bean, fieldSet, keyvalue,
									type, sequence, SetidR);

						}
						SetidR.setAttribute("sp_flag", sp_flag);
						if("new".equalsIgnoreCase(type) && this.keyvalue!=null &&this.keyvalue.length()>0){
							SetidR.setAttribute("keyvalue",this.keyvalue);
							this.keyvalue="";
						}
					} else {
						if ("07".equals(sp_flag) && this.isChecked_may_reject)// 判断比准后驳回
						{
							SetidR.setAttribute("sp_flag", sp_flag);
						} else {
							throw GeneralExceptionHandler
									.Handle(new GeneralException("",
											"子集信息以批准不能修改", "", ""));
						}
					}
				} else// 没有记录
				{
					throw GeneralExceptionHandler.Handle(new GeneralException(
							"", "子集下没有找到该纪录", "", ""));
				}
			} else {
				throw GeneralExceptionHandler.Handle(new GeneralException("",
						"没有找到该子集！", "", ""));
			}
		} catch (JDOMException e) {
			isCorrect = false;
			e.printStackTrace();
			return isCorrect;
		}
		return isCorrect;
	}
	
	/**
	 * 编辑当前记录的该相同子集的keyvalue，，因为插入使得keyvalue对应的i9999混乱，所以用此方法当插入是，把增加、修改和删除的申请，keyvalue大于等于插入的时候keyvalue加一
	 * 
	 * @param fieldSet
	 * @param sp_flag
	 * @param type
	 * @param keyvalue
	 * @return
	 * @throws GeneralException
	 */

	public synchronized boolean editCurKeyValue(FieldSet fieldSet,
			String sp_flag, String type, String keyvalue, String operate,
			String chg_id, A0100Bean bean,String sequence) throws GeneralException {
		// see("开始");
		if (type == null || !"insert".equalsIgnoreCase(type)) {
            return true;
        }
		if (sp_flag == null || !"03".equals(sp_flag)) {
            return true;
        }
		boolean isCorrect = true;
		String fieldSetId = fieldSet.getFieldsetid();
		int iKeyvalue = Integer.parseInt(keyvalue);
		String xpath = "";
		try {
			xpath = "/root/setid[@name='" + fieldSetId + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(this.doc);
			Iterator t = childlist.iterator();
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				// 选择上下文节点的
				xpath = "record[@keyvalue>='" + keyvalue + "']";
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				while (r.hasNext())// 有记录
				{
					Element SetidR = (Element) r.next();
					String value = SetidR.getAttributeValue("keyvalue");
					String r_type = SetidR.getAttributeValue("type");
					String sequenceid = SetidR.getAttributeValue("sequence");
					if (r_type != null && !("insert".equalsIgnoreCase(r_type) && sequenceid.equalsIgnoreCase(sequence))) {
						if (value != null && value.length() > 0) {
							int ivalue = Integer.parseInt(value);
							if (ivalue >= iKeyvalue
									&& "up".equalsIgnoreCase(operate)) {
								ivalue++;
								SetidR.setAttribute("keyvalue", String
										.valueOf(ivalue));
							}
						}
					}
				}
			}
		} catch (JDOMException e) {
			isCorrect = false;
			e.printStackTrace();
			return isCorrect;
		}
		// see("结束");
		return isCorrect;
	}
	
	/***************************************************************************
	 * 编辑其他记录的keyvalue，因为插入使得keyvalue对应的i9999混乱，所以用此方法当插入是，把修改和删除的申请，keyvalue大于等于插入的时候keyvalue加一
	 * 
	 * @param chg_id
	 * @param bean
	 * @param fieldSetId
	 * @param keyvalue
	 * @param operate
	 */
	private void editOtherRocordkeyvalue(String chg_id, A0100Bean bean,
			String fieldSetId, String keyvalue, String operate) {
		StringBuffer sql = new StringBuffer();
		sql.append("select content from t_hr_mydata_chg where chg_id<>'"
				+ chg_id + "'");
		sql.append(" and a0100='" + bean.getA0100() + "' and nbase='"
				+ bean.getNbase() + "'");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String content = "";
			rs = dao.search(sql.toString());
			while (rs.next()) {
				content = Sql_switcher.readMemo(rs, "content");
				if (content != null && content.length() > 0) {
					editOtherRocordkeyvalue(dao, content, chg_id, fieldSetId,
							keyvalue, operate);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * 编辑其他记录的keyvalue
	 * 
	 * @param dao
	 * @param content
	 * @param chg_id
	 * @param fieldSetId
	 * @param keyvalue
	 * @param operate
	 */
	private void editOtherRocordkeyvalue(ContentDAO dao, String content,
			String chg_id, String fieldSetId, String keyvalue, String operate) {
		try {
			Document doc = PubFunc.generateDom(content);
			String xpath = "/root/setid[@name='" + fieldSetId + "']";
			XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
			List childlist = reportPath.selectNodes(doc);
			int iKeyvalue = Integer.parseInt(keyvalue);
			Iterator t = childlist.iterator();
			boolean isCorrect = false;
			if (t.hasNext()) {
				Element fieldSetE = (Element) t.next();
				xpath = "record[@keyvalue>='" + keyvalue
						+ "']";// 选择上下文节点的
				// setid
				// 元素孩子的record元素keyvalue=i9999
				reportPath = XPath.newInstance(xpath);// 取得子集记录结点
				childlist = reportPath.selectNodes(fieldSetE);
				Iterator r = childlist.iterator();
				while (r.hasNext())// 有记录
				{
					Element SetidR = (Element) r.next();
					String value = SetidR.getAttributeValue("keyvalue");
					String r_type = SetidR.getAttributeValue("type");
					if (r_type != null
							&& ("update".equalsIgnoreCase(r_type) || "delete"
									.equalsIgnoreCase(r_type) || "new".equalsIgnoreCase(r_type))) {
						if (value != null && value.length() > 0) {
							int ivalue = Integer.parseInt(value);
							if (ivalue >= iKeyvalue
									&& "up".equalsIgnoreCase(operate)) {
								ivalue++;
								SetidR.setAttribute("keyvalue", String
										.valueOf(ivalue));
								isCorrect = true;
							}
						}
					}
				}
			}
			if (isCorrect) {
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				content = outputter.outputString(doc);
				RecordVo vo = new RecordVo("t_hr_mydata_chg");
				vo.setString("chg_id", chg_id);
				vo = dao.findByPrimaryKey(vo);
				dao.updateValueObject(vo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private synchronized boolean apployEdit(A0100Bean bean, FieldSet fieldSet,
			String keyvalue, String type, String sequence, Element element) {
		if (type == null || type.length() <= 0) {
            return false;
        }
		boolean isCorrect = false;
		try {
			String fieldSetId = fieldSet.getFieldsetid();
			String tablename = bean.getNbase() + fieldSetId;
			StringBuffer fields = new StringBuffer();
			StringBuffer fieldvalues = new StringBuffer();
			List childList = element.getChildren("column");
			Iterator r = childList.iterator();
			String[] fieldsname = new String[childList.size()];
			String[] fieldcode = new String[childList.size()];
			int i = 0;
			while (r.hasNext()) {
				Element elementC = (Element) r.next();
				String name = elementC.getAttributeValue("name");
				String value = elementC.getAttributeValue("newvalue");
				String oldvalue=elementC.getAttributeValue("oldvalue");
                FieldItem fieldItem = (FieldItem)DataDictionary.getFieldItem(name).clone();
				fieldItem.setValue(value);
				fields.append(fieldItem.getItemid());
				fieldsname[i] = fieldItem.getItemid();
				boolean flag = true;
				//2015-04-02  guodd  因为此处保存的是 报批时的数据，如果报批后做了人事异动，然后再批准修改，发现单位部门和岗位会又变成以前的值了。所以讲此处注掉
				if ("update".equals(type)/* &&!fieldsname[i].equalsIgnoreCase("B0110")&&!fieldsname[i].equalsIgnoreCase("E0122")&&!fieldsname[i].equalsIgnoreCase("E01A1")*/){
					if(value.equals(oldvalue)){
						flag=false;
					}
				}
				if ("D".equals(fieldItem.getItemtype())) {
					fieldvalues.append(PubFunc.DateStringChange(fieldItem
							.getValue()));
					if(flag){
						fieldcode[i] = PubFunc.DateStringChange(fieldItem
							.getValue());
					}else{
						fieldcode[i] = fieldsname[i];
					}
				} else if ("M".equals(fieldItem.getItemtype())) {
					if (fieldItem.getValue() == null
							|| "null".equals(fieldItem.getValue())
							|| "".equals(fieldItem.getValue())) {
						if(flag) {
                            fieldcode[i] = "null";
                        } else {
                            fieldcode[i] = fieldsname[i];
                        }
						fieldvalues.append("null");
					} else {
						if(flag) {
                            fieldcode[i] = "'" + fieldItem.getValue() + "'";
                        } else {
                            fieldcode[i] = fieldsname[i];
                        }
						fieldvalues.append("'" + fieldItem.getValue() + "'");
					}
				} else if ("N".equals(fieldItem.getItemtype())) {
					if (fieldItem.getValue() == null
							|| "null".equals(fieldItem.getValue())
							|| "".equals(fieldItem.getValue())) {
						if(!flag) {
                            fieldcode[i] = fieldsname[i];
                        } else {
                            fieldcode[i] = "null";
                        }
						fieldvalues.append("null");
					} else {
						if(flag) {
                            fieldcode[i] = fieldItem.getValue();
                        } else {
                            fieldcode[i] = fieldsname[i];
                        }
						fieldvalues.append(fieldItem.getValue());
					}
				} else {
					if (fieldItem.getValue() == null
							|| "null".equals(fieldItem.getValue())
							|| "".equals(fieldItem.getValue())) {
						if(flag) {
                            fieldcode[i] = "null";
                        } else {
                            fieldcode[i] = fieldsname[i];
                        }
						fieldvalues.append("null");
					} else {
						if(flag){
							fieldcode[i] = "'"
								+ PubFunc.splitString(fieldItem.getValue(),
										fieldItem.getItemlength()) + "'";
						}else {
                            fieldcode[i] = fieldsname[i];
                        }
						fieldvalues.append("'"
								+ PubFunc.splitString(fieldItem.getValue(),
										fieldItem.getItemlength()) + "'");
					}
				}
				fields.append(",");
				fieldvalues.append(",");
				i++;
			}
			StructureExecSqlString structureExecSqlString = new StructureExecSqlString();
			structureExecSqlString.setFieldcode(fieldcode);
			UUID uuid = UUID.randomUUID();
			String insertGuidkey = uuid.toString().toUpperCase();
			if ("new".equals(type) || "insert".equals(type)) {
				String ids = structureExecSqlString.InfoInsert("1", tablename, fields
						.toString(), fieldvalues.toString(), bean.getA0100(),
						this.userview.getUserName(), this.conn);
				if (ids != null && ids.length() > 0) {
					isCorrect = true;
					this.keyvalue=ids;
				}
				
				if(!"A01".equalsIgnoreCase(fieldSetId)) {
					String updateSql = "update " + tablename + " set guidkey=? where a0100=? and i9999=?";
					ArrayList<String> paramList = new ArrayList<String>();
					if("insert".equals(type)) {
						paramList.add(insertGuidkey);
					} else {
						paramList.add(keyvalue);
					}
					
					paramList.add(a0100);
					paramList.add(ids);
					dao.update(updateSql, paramList);
				}
				
				if ("insert".equals(type)) {
                    isCorrect = updateRecord(getI9999(tablename, bean.getA0100(), keyvalue), tablename, bean.getA0100(), this.dao);
                }
				
			} else if ("delete".equals(type)) {
				isCorrect = deleteRecord(keyvalue, tablename, bean.getA0100(), this.dao);

			} else if ("update".equals(type)) {
				isCorrect = structureExecSqlString.InfoUpdate("1", tablename, fieldsname,
						fieldcode, bean.getA0100(), getI9999(tablename, bean.getA0100(), keyvalue), this.userview
								.getUserName(), this.conn);
			
			}
			
			List medias = (List)element.getChildren("multimedia");
			if(medias.size()>0){
				String i9999 = getI9999(tablename, bean.getA0100(), keyvalue);
				if("new".equals(type)) {
                    i9999 = this.keyvalue;
                } else if("insert".equals(type)) {
                    i9999 = getI9999(tablename, bean.getA0100(), insertGuidkey);
                }
				
				saveOrDelMultiMedia(this.nbase, this.a0100, fieldSetId, i9999, medias);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			isCorrect = false;
		}
		return isCorrect;
	}
	
	/**
	 * 批准后插入修改纪录i9999
	 * 
	 * @param I9999
	 * @param tablename
	 * @param A0100
	 * @param dao
	 */
	static private boolean updateRecord(String I9999, String tablename,
			String A0100, ContentDAO dao) {
		boolean flag = true;
		String upsql1 = "update " + tablename
				+ " set I9999=I9999+1 where I9999>=" + I9999 + "   and a0100='"
				+ A0100 + "' ";
		String upsql = "update " + tablename + " set I9999=" + I9999
				+ " where I9999=(select max(I9999) from " + tablename
				+ " where a0100='" + A0100 + "')  and a0100='" + A0100 + "'";
		try {
			dao.update(upsql1);
			dao.update(upsql);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 批准后删除纪录
	 * 
	 * @param i9999
	 * @param tableName
	 * @param A0100
	 * @param dao
	 */
	private boolean deleteRecord(String guidkey, String tableName, String A0100,
			ContentDAO dao) {
		boolean flag = true;
		String desql1 = "";
		String i9999 = "";
		if (tableName.length() == 3 && "01".equals(tableName.substring(1, 3))
				|| tableName.length() == 6
				&& "01".equals(tableName.substring(4, 6))) {
			desql1 = "delete  from " + tableName + " where a0100='" + A0100
					+ "' ";
		} else {
			i9999 = getI9999(tableName, A0100, guidkey);
			desql1 = "delete  from " + tableName + " where I9999=" + i9999
					+ "and a0100='" + A0100 + "' ";
		}
		try {
	        //删除附件 2014-05-04 wangrd
            if (tableName.length()==6){
                String nbase = tableName.substring(0,3);
                String setid = tableName.substring(3,6);
                if (i9999==null) {
                    i9999="0";
                }
                MultiMediaBo mediabo= new MultiMediaBo(this.conn,this.userview);
                mediabo.deleteMultimediaFileByA0100("A", setid, nbase, A0100, Integer.parseInt(i9999));
            }
            
			dao.delete(desql1, new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	//批量操作
	public synchronized boolean allapprove(String chg_id,String setid,String state){
		try {
			String xpath="";
			if(!"".equals(setid) && setid!=null){
				xpath = "/root/setid[@name='"+setid+"']/record[@sp_flag='02']";
				XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
				List childlist = reportPath.selectNodes(doc);
				FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
				
				Iterator recite = childlist.iterator();
				while(recite.hasNext()){
					Element recordE = (Element)recite.next();
					String keyid = recordE.getAttributeValue("keyvalue");
					String typeid = recordE.getAttributeValue("type");
					String sequenceid = recordE.getAttributeValue("sequence");
					updateApployMyselfDataApp(chg_id, fieldset, state,
							keyid, typeid, sequenceid);
				}
			}else{
				xpath = "/root/setid";
				XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
				List childlist = reportPath.selectNodes(doc);
				Iterator setite = childlist.iterator();
				while(setite.hasNext()){
					Element setE = (Element)setite.next();
					String setname = setE.getAttributeValue("name");
					FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
					childlist = setE.getChildren();
					Iterator recite = childlist.iterator();
					while(recite.hasNext()){
						Element recordE = (Element)recite.next();
						String sp_flag = recordE.getAttributeValue("sp_flag");
						if(!"02".equals(sp_flag)) {
                            continue;
                        }
						String keyid = recordE.getAttributeValue("keyvalue");
						String typeid = recordE.getAttributeValue("type");
						String sequenceid = recordE.getAttributeValue("sequence");
						updateApployMyselfDataApp(chg_id, fieldset, state,
								keyid, typeid, sequenceid);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public void saveOrDelMultiMedia(String nbase,String a0100,String setid,String i9999,List medias) throws GeneralException{
		MultiMediaBo multiMediaBo = new MultiMediaBo(this.conn,this.userview,
                "A",nbase,setid,a0100,Integer.parseInt(i9999));
		StringBuffer desids = new StringBuffer(",");
		ArrayList changeEle = new ArrayList();
		for(int i=0;i<medias.size();i++){
			Element me= (Element)medias.get(i);
			if("delete".equals(me.getAttributeValue("type"))){
				desids.append(me.getAttributeValue("fileid")).append(",");
			    changeEle.add(me);
			}else{
				HashMap fileInfo = new HashMap();
				fileInfo.put("filetype", me.getAttributeValue("class"));
				fileInfo.put("filetitle", me.getAttributeValue("topic"));
				fileInfo.put("description", me.getAttributeValue("desc"));
				fileInfo.put("path", me.getAttributeValue("path"));
				FormFile file = null;
				multiMediaBo.saveMultimediaFile(fileInfo, file,false);
				changeEle.add(me);
			}
				
		}
		
		ArrayList mediainfolist = multiMediaBo.getMultimediaRecord(desids.toString());
		if (mediainfolist != null && mediainfolist.size() > 0){
			multiMediaBo.deleteMultimediaRecord(mediainfolist);
		}
	}
	
	public FormFile fileToFormFile(String path,String filename){
		Class parentClass;
		FormFile file = null;
        try{
            parentClass = Class.forName("org.apache.struts.upload.CommonsMultipartRequestHandler");
            Class childClass = parentClass.getDeclaredClasses()[0];
            Constructor c = childClass.getConstructors()[0];
            c.setAccessible(true);
            FileItem aitem = createFileItem(path,filename);
            file = (FormFile) c.newInstance(new Object[] {aitem });
        }catch(Exception e){
        	   e.printStackTrace();
        }
        
        return file;
	}
	
	private FileItem createFileItem(String filePath,String filename) throws IOException
    {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "textField";
        int num = filePath.lastIndexOf(".");
        String extFile = filePath.substring(num);
        FileItem item = factory.createItem(textFieldName,null, true,
        		filename + extFile);
        File newfile = new File(filePath);
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        OutputStream os = null;
        try
        {
            fis = new FileInputStream(newfile);
            os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 1024))
                != -1)
            {
                os.write(buffer, 0, bytesRead);
            }
            
            os.flush();
            newfile.delete();
        }catch (Exception e){
            e.printStackTrace();
        }finally{
        	PubFunc.closeResource(os);
        	PubFunc.closeResource(fis);
        }

        return item;
    }
	/**
	 * 信息审核页面ajax查询人员信息
	 * @param setId 人员信息集id
	 * @return
	 */
	public HashMap fieldSetList(String setId){
        HashMap changeMap = new HashMap();
        
        try{
            List setlist =  userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
            ArrayList infolist = new ArrayList();
            List childlist = new ArrayList();
            FieldSet fs = null;
            for(int i = 0; i < setlist.size(); i++) {
                fs = (FieldSet) setlist.get(i);
                String fieldSetId = fs.getFieldsetid();
                if(fieldSetId.equalsIgnoreCase(setId)) {
                    break;
                }
                
            }
            
            if(fs != null){
                String setid=fs.getFieldsetid();
                String setdesc =fs.getCustomdesc();
                ArrayList itemlist=userview.getPrivFieldList(setid.toUpperCase());
                
                if(itemlist == null || itemlist.size()<1) {
                    return changeMap;
                }
                
                changeMap.put("setid", PubFunc.encrypt(setid.toUpperCase()));
                changeMap.put("fieldSetid", setid.toUpperCase());
                changeMap.put("isMainSet", String.valueOf("A01".equalsIgnoreCase(setid)));
                changeMap.put("setdesc", setdesc);
                
                String xpath = "/root/setid[@name='"+setid+"']";
                XPath reportPath = XPath.newInstance(xpath);// 取得子集结点
                Element  fieldsetE= (Element) reportPath.selectSingleNode(doc);
                
                boolean showsp = false;
                
                ArrayList recordlist=new ArrayList();
                HashMap columns=new HashMap();
                for(int i=0;i<itemlist.size();i++){
                    FieldItem fi = (FieldItem)itemlist.get(i);
                    HashMap fiMap = new HashMap();
                    fiMap.put("itemid", fi.getItemid());
                    fiMap.put("itemdesc", fi.getItemdesc());
                    fiMap.put("itemtype", fi.getItemtype());
                    fiMap.put("format", fi.getFormat());
                    fiMap.put("codesetid",fi.getCodesetid());
                    columns.put(fi.getItemid(),fiMap);
                }
                changeMap.put("columns", columns);
                infolist = getinfolist(setid,this.a0100,this.nbase,itemlist);
                
                if((infolist == null || infolist.size()<1) && fieldsetE == null) {
                    return changeMap;
                }
                
                Iterator recite = infolist.iterator();
                /////////////////////////////////////////////////////////////////////////////////////////    库里的数据和 type为update的数据    
                while(recite.hasNext()){
                    LazyDynaBean infoitem = (LazyDynaBean)recite.next();
                    HashMap rldMap=new HashMap();
                    //信息审核如果同时存在已批和新增子集信息时获取不到i9999
                    if(!"A01".equalsIgnoreCase(setid)) {
                        rldMap.put("recordid", infoitem.get("i9999"));
                    }
                    
                    boolean flag = false;
                    if(!"A01".equals(setid) && fieldsetE!=null){
                        xpath="record[@keyvalue='"+infoitem.get("i9999")+"' and (@type='update' or @sp_flag='03')]";
                        reportPath = XPath.newInstance(xpath);
                        childlist = reportPath.selectNodes(fieldsetE);
                    }else if(fieldsetE!=null) {
                        childlist = fieldsetE.getChildren();
                    } else {
                        childlist = null;
                    }
                    
                    if(childlist!=null && childlist.size()>0){
                        flag = true;
                        showsp = true;
                        HashMap changevalue = new HashMap();
                        ArrayList splist = new ArrayList();
                        for(int i=0;i<childlist.size();i++){
                            Element recordEs = (Element)childlist.get(i);
                            String type = recordEs.getAttributeValue("type");
                            rldMap.put("type", type);
                            rldMap.put("recordid", PubFunc.encrypt(recordEs.getAttributeValue("keyvalue")));
                            rldMap.put("i9999", recordEs.getAttributeValue("keyvalue"));
                            rldMap.put("sequence", recordEs.getAttributeValue("sequence"));
                            splist.add(recordEs.getAttributeValue("sp_flag"));
                            
                            List mulElList = (List)recordEs.getChildren("multimedia");
                            if(mulElList.size()>0){
                                rldMap.put("multimedia", "true");
                                changeMap.put("multimedia", "true");
                                
                                if("A01".equals(setid)){
                                    String multype;
                                    ArrayList muList = new ArrayList();
                                    ContentDAO dao = new ContentDAO(this.conn);
                                    for(int k=0;k<mulElList.size();k++){
                                        Element mulEl = (Element)mulElList.get(k);
                                        HashMap mldMap=new HashMap();
                                        multype = mulEl.getAttributeValue("type");
                                        mldMap.put("type", multype);
                                        if("delete".equals(multype)){
                                            RecordVo vo = new RecordVo("hr_multimedia_file");
                                            vo.setString("id",mulEl.getAttributeValue("fileid"));
                                            if(!dao.isExistRecordVo(vo)) {
                                                continue;
                                            }
                                            
                                            mldMap.put("topic", SafeCode.encode(vo.getString("topic")));
                                            mldMap.put("desc", SafeCode.encode(vo.getString("description")));
                                            mldMap.put("filename", SafeCode.encode(vo.getString("filename")));
                                            mldMap.put("path", vo.getString("path"));
                                            mldMap.put("srcfilename", vo.getString("srcfilename"));
                                        }else{
                                            mldMap.put("topic", SafeCode.encode(mulEl.getAttributeValue("topic")));
                                            mldMap.put("desc", SafeCode.encode(mulEl.getAttributeValue("description")));
                                            mldMap.put("filename", SafeCode.encode(mulEl.getAttributeValue("filename")));
                                            mldMap.put("path", PubFunc.encrypt(mulEl.getAttributeValue("path")
                                                    + "/" + mulEl.getAttributeValue("filename")));
                                            mldMap.put("srcfilename", mulEl.getAttributeValue("srcfilename"));
                                        }
                                        muList.add(mldMap);
                                    }
                                    changeMap.put("multimedialist", muList);
                                }
                            }
                            
                            List collist = recordEs.getChildren("column");
                            Iterator cite = collist.iterator();
                            while(cite.hasNext()){
                                Element columnE = (Element)cite.next();
                                String itemid = columnE.getAttributeValue("name");
                                String newvalue =  columnE.getAttributeValue("newvalue");
                                String oldvalue = columnE.getAttributeValue("oldvalue");
                                if(!newvalue.equals(oldvalue.toString())){
                                    String[] diff = {newvalue,oldvalue};
                                    changevalue.put(itemid,diff);
                                }
                            }
                            
                        }
                            
                        String spflag = ""; 
                        for(int i=0;i<splist.size();i++){
                            if("02".equals(splist.get(i))){
                                spflag="02";
                                break;
                            }else if("03".equals(splist.get(i))){
                                spflag="03";
                                break;
                            }else{
                                spflag= "07";
                            }
                            
                        }
                        if("03".equals(spflag)) {
                            rldMap.put("type", "select");
                        }
                        rldMap.put("sp_flag", spflag);
                        
                        for(int i=0;i<itemlist.size();i++){
                            FieldItem fi = (FieldItem)itemlist.get(i);
                            String itemid = fi.getItemid();
                            String[] values ={"",""};
                            String oldvalue = "";
                            String newvalue = "";
                            if(changevalue.containsKey(itemid)){
                                values = (String[])changevalue.get(itemid);
                                newvalue = values[0];
                                if(values.length>1) {
                                    oldvalue = values[1];
                                }
                                
                            }else{
                                oldvalue = infoitem.get(itemid).toString();
                            }
                            HashMap cldMap=new HashMap();
                            if(!"0".equals(fi.getCodesetid()) && fi.getCodesetid()!= null){
                                if("UN".equals(fi.getCodesetid()) || "UM".equals(fi.getCodesetid()) || "@K".equals(fi.getCodesetid())){
                                    
                                    newvalue = getcodedesc(null, newvalue);
                                    oldvalue = getcodedesc(null, oldvalue);
                                }else{
                                    newvalue = getcodedesc(fi.getCodesetid(),newvalue);
                                    oldvalue = getcodedesc(fi.getCodesetid(), oldvalue);
                                }
                            }
                            
                            if(changevalue.containsKey(itemid)){
                                if("M".equals(fi.getItemtype())) {
                                    HashMap<String, String> newValueMap = subString(newvalue);
                                    String tipNewValue = newValueMap.get("tipText");
                                    String showNewValue = newValueMap.get("showText");
                                    cldMap.put("newvalue", showNewValue);
                                    if(StringUtils.isNotEmpty(tipNewValue)) {
                                        cldMap.put("tipNewValue", tipNewValue);
                                    }
                                    
                                    HashMap<String, String> oldValueMap = subString(oldvalue);
                                    String tipOldValue = oldValueMap.get("tipText");
                                    String showOldValue = oldValueMap.get("showText");
                                    cldMap.put("oldvalue", showOldValue);
                                    if(StringUtils.isNotEmpty(tipOldValue)) {
                                        cldMap.put("tipOldValue", tipOldValue);
                                    }
                                    
                                } else {
                                    cldMap.put("newvalue", newvalue);
                                    cldMap.put("oldvalue", oldvalue);
                                }
                                
                                cldMap.put("changeflag", "Y");
                            }else{
                                if("M".equals(fi.getItemtype())) {
                                    HashMap<String, String> newValueMap = subString(oldvalue);
                                    String tipNewValue = newValueMap.get("tipText");
                                    String showNewValue = newValueMap.get("showText");
                                    cldMap.put("newvalue", showNewValue);
                                    if(StringUtils.isNotEmpty(tipNewValue)) {
                                        cldMap.put("tipNewValue", tipNewValue);
                                    }
                                    
                                    HashMap<String, String> oldValueMap = subString(oldvalue);
                                    String tipOldValue = oldValueMap.get("tipText");
                                    String showOldValue = oldValueMap.get("showText");
                                    cldMap.put("oldvalue", showOldValue);
                                    if(StringUtils.isNotEmpty(tipOldValue)) {
                                        cldMap.put("tipOldValue", tipOldValue);
                                    }
                                    
                                } else {
                                    cldMap.put("newvalue", oldvalue);
                                    cldMap.put("oldvalue", oldvalue);
                                }
                                cldMap.put("changeflag", "N");
                            }
                            rldMap.put(itemid, cldMap);
                        }
                        
                    }
                            
                    if(!flag){
                        if("A01".equals(setid.toUpperCase())) {
                            rldMap.put("type", "update");
                        } else {
                            rldMap.put("type", "select");
                        }
                        rldMap.put("sp_flag", "03");
                        for(int i=0;i<itemlist.size();i++){
                            HashMap cldMap=new HashMap();
                            FieldItem fi = (FieldItem)itemlist.get(i);
                            String value = infoitem.get(fi.getItemid()).toString();
                            if(fi.getCodesetid()==null || "0".equals((String)fi.getCodesetid())) {
                                if("M".equals(fi.getItemtype())) {
                                    HashMap<String, String> newValueMap = subString(value);
                                    String tipNewValue = newValueMap.get("tipText");
                                    String showNewValue = newValueMap.get("showText");
                                    cldMap.put("newvalue", showNewValue);
                                    if(StringUtils.isNotEmpty(tipNewValue)) {
                                        cldMap.put("tipNewValue", tipNewValue);
                                    }
                                } else {
                                    cldMap.put("newvalue", value);
                                }
                                    
                            }
                            else{
                                
                                if("UN".equals(fi.getCodesetid()) || "UM".equals(fi.getCodesetid()) || "@K".equals(fi.getCodesetid())){
                                    
                                    value = getcodedesc(null, value);
                                }else{
                                    value = getcodedesc(fi.getCodesetid(),value);
                                }
                                cldMap.put("newvalue",value);
                                
                            }
                            cldMap.put("changeflag", "N");
                            rldMap.put(fi.getItemid(),cldMap);
                        }
                        
                    }
                            
                    recordlist.add(rldMap);
                }
                            
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////还没有批准入库的数据  
                if(childlist ==null && infolist.size()>0){
                    changeMap.put("itemlist", recordlist);
                    changeMap.put("showsp", new Boolean(showsp));
                    return changeMap;
                }
                        
                childlist = fieldsetE.getChildren();
                Iterator lastite = childlist.iterator();
                while(lastite.hasNext()){
                    showsp = true;
                    Element recordEs=(Element)lastite.next();
                    HashMap rldMap=new HashMap();
                    String type = recordEs.getAttributeValue("type");
                    String sp_flag0 = recordEs.getAttributeValue("sp_flag");
                    if(( "update".equals(type) || "03".equals(sp_flag0) ) && !"delete".equals(type)) //update 上边已经加载，所以这里过滤掉。删除不进行过滤
                    {
                        continue;
                    }
                    rldMap.put("recordid", recordEs.getAttributeValue("keyvalue"));
                    rldMap.put("type", type);
                    rldMap.put("sp_flag", sp_flag0);
                    rldMap.put("sequence", recordEs.getAttributeValue("sequence"));
                    
                    if(recordEs.getChildren("multimedia").size()>0){
                        rldMap.put("multimedia", "true");
                        changeMap.put("multimedia", "true");
                    }
                    
                    childlist = recordEs.getChildren("column");
                    Iterator cite=childlist.iterator();
                    while(cite.hasNext()){
                        Element columnE = (Element)cite.next();
                        String itemid = columnE.getAttributeValue("name");
                        String newvalue =  columnE.getAttributeValue("newvalue");
                        if("delete".equals(type)) {
                            newvalue = columnE.getAttributeValue("oldvalue");
                        }
                        // String oldvalue = columnE.getAttributeValue("oldvalue");
                        FieldItem fi = getcolumndesc(itemlist,itemid);
                        
                        if(fi==null) {
                            continue;
                        }
                        
                        
                        HashMap cldMap=new HashMap();
                        String codesetid = fi.getCodesetid().toUpperCase();
                        if("0".equals(codesetid)){
                            if("M".equals(fi.getItemtype())) {
                                HashMap<String, String> newValueMap = subString(newvalue);
                                String tipNewValue = newValueMap.get("tipText");
                                String showNewValue = newValueMap.get("showText");
                                cldMap.put("newvalue", showNewValue);
                                if(StringUtils.isNotEmpty(tipNewValue)) {
                                    cldMap.put("tipNewValue", tipNewValue);
                                }
                                
                            } else {
                                cldMap.put("newvalue", newvalue);
                            }
                            
                        }else{
                            if("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)){
                                cldMap.put("newvalue", getcodedesc(null,newvalue));
                            }else{
                                cldMap.put("newvalue", getcodedesc(codesetid,newvalue));
                            }
                        }
                        
                        rldMap.put(itemid, cldMap);
                        
                        
                    }
                    
                    recordlist.add(rldMap);
                    
                }
                changeMap.put("itemlist", recordlist);
                changeMap.put("showsp", new Boolean(showsp));
            }
                        
        }catch (Exception e) {
            e.printStackTrace();               
        }
        
        return changeMap;
    }
	/**
	 * 处理备注类型指标的值
	 * @param value 备注类型指标的值
	 * @return
	 */
    public HashMap<String, String> subString(String value) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(value) && value.length() > 19) {
            String tipText = value.replaceAll("\\\\\"", "“");
            tipText = tipText.replaceAll("\r\n", "<br>");
            tipText = tipText.replaceAll("\r", "<br>");
            tipText = tipText.replaceAll("\n", "<br>");
            map.put("tipText", tipText);

            String showText = value.replaceAll("\\\\\"", "“");
            showText = showText.replaceAll("\r\n", "");
            showText = showText.replaceAll("\r", "");
            showText = showText.replaceAll("\n", "");
            showText = showText.trim().substring(0, 10);
            if (showText.indexOf("<") == 9) {
                showText = showText.replaceAll("<", "");
            }
            
            map.put("showText", showText + "...");
        } else if (StringUtils.isNotEmpty(value) && value.length() > 10 && value.length() < 19) {
            String tipText = value.replaceAll("\\\\\"", "“");
            tipText = tipText.replaceAll("\r\n", "<br>");
            tipText = tipText.replaceAll("\r", "<br>");
            tipText = tipText.replaceAll("\n", "<br>");
            map.put("tipText", tipText);

            String showText = value.replaceAll("\\\\\"", "“");
            showText = showText.replaceAll("\r\n", "");
            showText = showText.replaceAll("\r", "");
            showText = showText.replaceAll("\n", "");
            map.put("showText", showText.trim());
        } else {
            map.put("tipText", "");
            map.put("showText", value);
        }
        return map;
    }
    
    /**
     * 信息驳回发送邮件 
     * @param fieldSet 驳回的子集信息
     * @param nbase 人员库
     * @param a0100 人员编号
     */
    public void sendEmail(FieldSet fieldSet, String nbase, String a0100, String commentValue) {
        RowSet rs = null;
        try {
            String emailValue = ""; 
            String email = ConstantParamter.getEmailField().toLowerCase();
            if(StringUtils.isEmpty(email)) {
                return;
            }
            
            String sql = "select " + email + " from " + nbase + "a01 where a0100=" + a0100;
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            if(rs.next()) {
                emailValue = rs.getString(email);
            }
            
            if(StringUtils.isEmpty(emailValue)) {
                return;
            }
            
            AsyncEmailBo asyncEmailBo = new AsyncEmailBo(this.conn, this.userview);
            ArrayList<LazyDynaBean> emailList = new ArrayList<LazyDynaBean>();
            StringBuffer content = new StringBuffer();
            content.append("您好，<br><div style=\"text-indent:2em;\" >您提交的");
            if(fieldSet == null) {
                content.append("个人信息的修改申请被驳回！</div>");
                if(StringUtils.isNotEmpty(commentValue)) {
                    content.append("<div style=\"text-indent:2em;\" >");
                    content.append(commentValue.replace("\r\n", "<br>"));
                    content.append("</div>");
                }
                
                content.append("");
            } else {
                content.append(fieldSet.getFieldsetdesc());
                content.append("的修改申请被驳回！</div>");
            }
            
            LazyDynaBean bean = new LazyDynaBean();
            bean.set("subject", "信息审核提示");
            bean.set("bodyText", content);
            bean.set("toAddr", emailValue);
            asyncEmailBo.send(bean);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
    }

    public void setBatchReject(boolean isBatchReject) {
        this.isBatchReject = isBatchReject;
    }

	/**
	 * 获取子集数据对应的guidkey
	 * 
	 * @param tableName
	 *            子集
	 * @param a0100
	 *            人员编号
	 * @param guidkey
	 *            guidkey
	 * @return
	 */
	private String getI9999(String tableName, String a0100, String guidkey) {
		RowSet rs = null;
		String i9999 = "0";
		try {
			if (tableName.toUpperCase().endsWith("A01")) {
				return i9999;
			}

			String sql = "select i9999 from " + tableName + " where a0100=? and guidkey=?";
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(a0100);
			paramList.add(guidkey);
			rs = dao.search(sql, paramList);
			if (rs.next()) {
				i9999 = rs.getString("i9999");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return i9999;
	}
}
