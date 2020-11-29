package com.hjsj.hrms.transaction.gz.gz_amount.tax;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SaveHireViewTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
		String hirecontent = (String)hm.get("hirecontent");
		hirecontent=hirecontent!=null&&hirecontent.trim().length()>0?hirecontent.substring(0,hirecontent.length()-1):"";
		
		String viewcontent = (String)hm.get("viewcontent");
		viewcontent=viewcontent!=null&&viewcontent.trim().length()>0?viewcontent.substring(0,viewcontent.length()-1):"";

		hirecontent=hirecontent.replaceAll(",","','");
//		if(updateHide(hirecontent,salaryid)){
//			if(updateView(viewcontent,salaryid)){
//				hm.put("info","ok");
//			}
//		}
		if(updateLprogram(hirecontent.replace("'", ""),salaryid)){
			hm.put("info","ok");
		}
		else{
			hm.put("info","no");
		}
		
	}
	public boolean updateHide(String hirecontent,String salaryid){
		boolean check = false;
		if(hirecontent.length()>0){
			StringBuffer updatesql = new StringBuffer();
			updatesql.append("update salaryset set ");
			updatesql.append("nwidth=0 ");
			updatesql.append(" where salaryid="+salaryid);
			updatesql.append(" and itemid in('"+hirecontent);
			updatesql.append("')");
			ContentDAO dao=new ContentDAO(this.frameconn);		
			try {
				dao.update(updatesql.toString());
				check=true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			check=true;
		}
		
		return check;
	}
	public boolean updateView(String viewcontent,String salaryid){
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		try {
			gzbo.syncGzTableStruct();
		} catch (GeneralException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList profilterlist=gzbo.getFieldlist();
		boolean check = false;
		String[] arr = viewcontent.split(",");
		for(int i=0;i<arr.length;i++){
			int nwidths = 0;
			if("NBASE".equalsIgnoreCase(arr[i])){
				nwidths=5;
			}else if("b0100".equalsIgnoreCase(arr[i])){
				nwidths=17;
			}else if("a00z0".equalsIgnoreCase(arr[i])){
				nwidths=7;
			}else if("a00z1".equalsIgnoreCase(arr[i])){
				nwidths=5;
			}else if("sp_flag".equalsIgnoreCase(arr[i])){
				nwidths=10;
			}else if("appprocess".equalsIgnoreCase(arr[i])){
				nwidths=10;
			}else{
				FieldItem fielditem = DataDictionary.getFieldItem(arr[i]);
				if(fielditem!=null){
					nwidths=fielditem.getItemlength();
				}else{
					for(int j=0;j<profilterlist.size();j++){
						Field field = (Field)profilterlist.get(j);
						if(field.getName().equalsIgnoreCase(arr[i])){
							nwidths=field.getLength();
							break;
						}
					}
				}
			}
			StringBuffer updatesql = new StringBuffer();
			updatesql.append("update salaryset set ");
			updatesql.append("nwidth=");
			updatesql.append(nwidths);
			updatesql.append(" where salaryid="+salaryid);
			updatesql.append(" and itemid='"+arr[i]+"'");
			ContentDAO dao=new ContentDAO(this.frameconn);		
			try {
				dao.update(updatesql.toString());
				check=true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				check=false;
				e.printStackTrace();
			}
		}
		return check;
	}
	public boolean updateLprogram(String hirecontent,String salaryid){
		boolean flag = false;
		SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn());
		RecordVo vo =gzbo.getRealConstantVo(this.getFrameconn(),Integer.parseInt(salaryid));
		try {
		if(vo!=null&&vo.getString("lprogram")!=null){
			String lprogram= vo.getString("lprogram");
			if(lprogram==null||lprogram.trim().length()==0)
				lprogram="<?xml version=\"1.0\" encoding=\"GB2312\"?><Params></Params>";
    		Document doc;
			if(vo.getString("lprogram").indexOf("hidden_items")!=-1){
				if(vo.getString("lprogram").indexOf("hidden_item")!=-1){
					
						doc = PubFunc.generateDom(lprogram);
						Element root = doc.getRootElement();
						Element hidden_items = root.getChild("hidden_items") ;
						List list = hidden_items.getChildren("hidden_item");
						if(list.size()>0)
							for(int i=0;i<list.size();i++){
								Element child =(Element)list.get(i);
								if(child!=null&&child.getAttributeValue("user_name")!=null&&child.getAttributeValue("user_name").equals(this.getUserView().getUserName()))
								{
									child.setText(hirecontent);
					        		 XMLOutputter outputter=new XMLOutputter();
					     			Format format=Format.getPrettyFormat();
					     			format.setEncoding("UTF-8");
					     			outputter.setFormat(format);
					     			String xml =outputter.outputString(doc);
					        		  BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
					      		    bo.updateLprogram(salaryid,xml);
									flag =true;
									break;
								}
				        		
							}
						if(!flag){//新增一个用户
							Element child = new Element("hidden_item"); 
			        		 child.setAttribute("user_name",this.getUserView().getUserName());
			        		 child.setText(hirecontent);
			        		 hidden_items.addContent(child);
			        		 XMLOutputter outputter=new XMLOutputter();
			     			Format format=Format.getPrettyFormat();
			     			format.setEncoding("UTF-8");
			     			outputter.setFormat(format);
			     			String xml =outputter.outputString(doc);
			        		  BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
			      		    bo.updateLprogram(salaryid,xml);
							
						}
				}else{
					doc = PubFunc.generateDom(lprogram);
					Element root = doc.getRootElement();
					Element hidden_items = root.getChild("hidden_items") ;
					Element child = new Element("hidden_item"); 
	        		 child.setAttribute("user_name",this.getUserView().getUserName());
	        		 child.setText(hirecontent);
	        		 hidden_items.addContent(child);
	        		 XMLOutputter outputter=new XMLOutputter();
	     			Format format=Format.getPrettyFormat();
	     			format.setEncoding("UTF-8");
	     			outputter.setFormat(format);
	     			String xml =outputter.outputString(doc);
	        		  BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
	      		    bo.updateLprogram(salaryid,xml);
			
				}
			}else{
				//建元素
				
				
					doc = PubFunc.generateDom(lprogram);
					Element root = doc.getRootElement();
					Element hidden_items = new Element("hidden_items"); 
					root.addContent(hidden_items);
					Element child = new Element("hidden_item"); 
	        		 child.setAttribute("user_name",this.getUserView().getUserName());
	        		 child.setText(hirecontent);
	        		 hidden_items.addContent(child);
	        		 XMLOutputter outputter=new XMLOutputter();
	     			Format format=Format.getPrettyFormat();
	     			format.setEncoding("UTF-8");
	     			outputter.setFormat(format);
	     			String xml =outputter.outputString(doc);
	        		  BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
	      		    bo.updateLprogram(salaryid,xml);
					
				
	    		
			}
			flag=true;
		}else{
			
		}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
