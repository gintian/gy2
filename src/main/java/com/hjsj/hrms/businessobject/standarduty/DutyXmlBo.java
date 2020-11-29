package com.hjsj.hrms.businessobject.standarduty;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DutyXmlBo {
	
	 public DutyXmlBo(){
		 
	 }
	
	 public DutyXmlBo(Connection conn){
		 sduty = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.JOB_FIELD_SET);//基准岗位 指标集
		 duty = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.POS_FIELD_SET); //岗位指标集
		 setItemMap(sduty, sduty_flag);
		 setItemMap(duty, duty_flag);  
		 setMap(sduty);
		 this.conn = conn;
	 }
	
	  private  String NO_USED_ITEM_FLAG="0";
	  private  String USED_ITEM_FLAG="1";
      private  String sduty_flag="1"; //代表基准岗位 
      private  String duty_flag="2";  //代表岗位 
	  private HashMap sdutyitem=new HashMap();//基准岗位指标集指标，key为指标集id；value为指标集下指标list
	  private HashMap dutyitem=new HashMap(); //实际岗位指标集指标
	  private ArrayList sduty; //基准岗位指标集
	  private ArrayList duty;  //实际岗位指标集
	  HashMap relevantset=new HashMap();//对应指标集     
	  Connection conn;
	  
	  //生成和基准岗位指标集相对于的信息
	public Map getRelevantsetMap(String xml){
		if(!"#".equalsIgnoreCase(xml) && xml!=null && !"".equals(xml)){
				try{
			        Document doc = PubFunc.generateDom(xml);      //读入xml	
			        Element root=doc.getRootElement();
			        List recs=root.getChildren();
			        for(int i=0;i<recs.size();i++){
			        	Element rec=(Element)recs.get(i);
			        	String setkey=rec.getAttributeValue("source");  
			        	String setvalue=rec.getAttributeValue("target");
			        	String item=rec.getChildText("field");
			        	ArrayList itemOutString=getOutString(item,setkey,setvalue);
			        	LazyDynaBean ldb=new LazyDynaBean();
			        	ldb.set("target", setvalue);
			        	ldb.set("field", itemOutString);
			        	//sm.setTarget(setvalue);
			        	//sm.setField(itemOutString);
			        	relevantset.put(setkey, ldb);//循环rec 将 对应指标集 和 对应指标放进map
			        }
				}catch(Exception e){
					e.printStackTrace();
					GeneralExceptionHandler.Handle(e);
				}
		}
		
		return relevantset;
	}
	
	//将xml形式变成页面需要形式输出
	public ArrayList getOutString(String field,String source,String target){
		String[] items=field.split(",");
		ArrayList arr=new ArrayList();
		
			for(int i=0;i<items.length;i++){
				StringBuffer outstring =new StringBuffer();
				String sdutyid=items[i].split("=")[0];
				String dutyid=items[i].split("=")[1];
				outstring.append(sdutyid);
				String sdutydesc = "";
				String dutydesc = "";
				if("H00".equals(source.toUpperCase())){
				   sdutydesc = getX00desc(sdutyid, source);
				   dutydesc = getX00desc(dutyid, target);
				}
				else{
				   sdutydesc=itemdesc(source,sdutyid,sduty_flag);
				   dutydesc=itemdesc(target,dutyid,duty_flag);
				}
				outstring.append(sdutydesc);
				outstring.append("<=>");
				outstring.append(dutyid);
				
				outstring.append(dutydesc);
				
				if("".equals(sdutydesc) || "".equals(dutydesc))
					continue;
				
				arr.add(outstring.toString());
			}
		return arr;
	}
	
	//查询fielditemid的文字描述
	public String itemdesc(String setid,String itemid,String flag){
		ArrayList itemlist=new ArrayList();
		String itemdesc="";
		if("1".equalsIgnoreCase(flag))
			 itemlist=(ArrayList) sdutyitem.get(setid);
		else{
			if("e01a1".equalsIgnoreCase(itemid)){
				FieldItem fi = DataDictionary.getFieldItem("e01a1");
				itemlist.add(fi);
			}else
			  itemlist=(ArrayList) dutyitem.get(setid);
		}
		
		if(itemlist == null || itemlist.size()<1)
			return itemdesc;
		
		 for(int i=0;i<itemlist.size();i++){
			 FieldItem fi=(FieldItem) itemlist.get(i);
			 if(fi.getItemid().equalsIgnoreCase(itemid)){
			    itemdesc=":"+fi.getItemdesc();
			    break;
			 }
		 }
		return itemdesc;
	}
	
	
	public void setItemMap(ArrayList arr,String flag){
		for(int i=0;i<arr.size();i++){
			FieldSet fs=(FieldSet) arr.get(i);
			if(fs.getFieldsetid().indexOf("00")>=0){
				arr.remove(i);
				i--;
				continue;
			}
				
			ArrayList itemlist=getFieldItem(fs.getFieldsetid(),USED_ITEM_FLAG);
			if("1".equalsIgnoreCase(flag)){
				sdutyitem.put(fs.getFieldsetid(), itemlist);
			}else{
				dutyitem.put(fs.getFieldsetid(), itemlist);
			}
		}
	}
	
	public void setMap(ArrayList para){
		for(int i=0;i<para.size();i++){
			FieldSet fs=(FieldSet)para.get(i);
			LazyDynaBean ldb=new LazyDynaBean();
        	ldb.set("target", "");
        	ldb.set("field", new ArrayList());
			relevantset.put(fs.getFieldsetid(), ldb);
		}
	}

	public ArrayList getFieldItem(String fieldSetId,String useFlag){
		ArrayList fielditem=new ArrayList();
		Connection conn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			 conn=AdminDb.getConnection();
			 String sql="select fieldsetid,itemid,itemdesc,itemtype,codesetid from fielditem where fieldsetid=? and useflag=?";
			 ps=conn.prepareStatement(sql);
			 ps.setString(1, fieldSetId);
			 ps.setString(2, useFlag);
			 rs=ps.executeQuery();
			 
			 while(rs.next()){
				 FieldItem fi=new FieldItem();
				 fi.setFieldsetid(rs.getString("fieldsetid"));
				 fi.setItemid(rs.getString("itemid"));
				 fi.setItemdesc(rs.getString("itemdesc"));
				 fi.setItemtype(rs.getString("itemtype"));
				 fi.setCodesetid(rs.getString("codesetid"));
				 fielditem.add(fi);
				
			 }
			
		} catch (GeneralException e) {
			e.printStackTrace();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(ps);
			PubFunc.closeResource(conn);
				
		}
		
		return fielditem;
	}
	
	
	public ArrayList getSduty() {
		return sduty;
	}

	public void setSduty(ArrayList sduty) {
		this.sduty = sduty;
	}

	public ArrayList getDuty() {
		return duty;
	}

	public void setDuty(ArrayList duty) {
		this.duty = duty;
	}

	public HashMap getSdutyitem() {
		return sdutyitem;
	}

	public void setSdutyitem(HashMap sdutyitem) {
		this.sdutyitem = sdutyitem;
	}

	public HashMap getDutyitem() {
		return dutyitem;
	}

	public void setDutyitem(HashMap dutyitem) {
		this.dutyitem = dutyitem;
	}
	
	public String createXML(Map relevantset,ArrayList sduty,ArrayList targetids){
		
		Element root = new Element("params");
		
		for(int i=0;i<sduty.size();i++){
			FieldSet fs=(FieldSet)sduty.get(i);
			LazyDynaBean ldb=(LazyDynaBean)relevantset.get(fs.getFieldsetid());
			String targetsetid=ldb.get("target").toString();
			ArrayList relerantitem=new ArrayList();
			if(targetsetid.equalsIgnoreCase(targetids.get(i).toString()))
			      relerantitem=(ArrayList)ldb.get("field");
			if(relerantitem.size()>0){
				Element rec = new Element("rec");
				rec.setAttribute("source", fs.getFieldsetid());
				rec.setAttribute("target", targetsetid);
				Element field=new Element("field");
				StringBuffer fieldtext=new StringBuffer();
				for(int j=0;j<relerantitem.size();j++){
					String str=relerantitem.get(j).toString();
					String[] items=str.split("<=>");
					String[] source=items[0].split(":");
					String[] target=items[1].split(":");
					String sourceid=source[0];
					String targetid=target[0];
					fieldtext.append(sourceid+"="+targetid+",");
				}
				field.setText(fieldtext.toString());
				rec.addContent(field);
				root.addContent(rec);
			}
		}
		
		if(relevantset.get("H00")!=null){
			LazyDynaBean ldb=(LazyDynaBean)relevantset.get("H00");
		    Element rec = new Element("rec");
		    rec.setAttribute("source", "H00");
			rec.setAttribute("target", "K00");
			Element field=new Element("field");
			StringBuffer fieldtext=new StringBuffer();
			ArrayList relerantitem = (ArrayList)ldb.get("field");
			for(int i=0;i<relerantitem.size();i++){
				String str=relerantitem.get(i).toString();
				String[] items=str.split("<=>");
				String[] source=items[0].split(":");
				String[] target=items[1].split(":");
				String sourceid=source[0];
				String targetid=target[0];
				fieldtext.append(sourceid+"="+targetid+",");
			}
			
			field.setText(fieldtext.toString());
			rec.addContent(field);
			root.addContent(rec);
		}
		
		int k=root.getContentSize();
		if(k<1){
			return "#";
		}
		Document doc = new Document(root);
    	XMLOutputter outputter = new XMLOutputter();
    	Format format=Format.getPrettyFormat();
    	format.setEncoding("UTF-8");
    	outputter.setFormat(format);
    	String xml=outputter.outputString(doc);
		return xml;
	}
	
	public String getX00desc(String type,String setid){
		String X00desc = "";
		RowSet rs=null;
		try{
		String sql = "select sortname from mediasort where dbflag=? and flag=?" ;
	    ContentDAO dao = new ContentDAO(this.conn);
	    ArrayList values = new ArrayList();
	    if("H00".equals(setid.toUpperCase()))
	    	values.add("4");
	    else
	    	values.add("3");
	    values.add(type);
	    rs = dao.search(sql, values);
	    if(rs.next())
	    	X00desc = rs.getString("sortname");
	    else if("K00".equalsIgnoreCase(setid)){//liuy 2015-4-14 8721：基准岗位，设置对应指标，多媒体分类对应设置，指定上分类后，再看，指标对应关系中的”K：岗位说明书“，就变成”K：“了，不对。
	    	X00desc = "岗位多媒体分类";
	    }
		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			try{
			if(rs!=null)
				rs.close();
			}catch(SQLException exc){
			    exc.printStackTrace();
			}
		}
		
		return ":"+X00desc;
	}
}
