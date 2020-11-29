package com.hjsj.hrms.businessobject.general.deci.leader;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;

public class BuildLeaderStuff {
	private static final String FILD="stuff.txt";
	private Connection conn;
	private UserView userView;
	private String SNameDisplay;
	public Connection getConn() {
		return conn;
	}
	public UserView getUserView() {
		return userView;
	}
	public BuildLeaderStuff() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BuildLeaderStuff(Connection conn, UserView view) {
		super();
		// TODO Auto-generated constructor stub
		this.conn = conn;
		userView = view;
	}
	
	public String buildStuff(int h,ArrayList fieldlist,ArrayList beanlist){
		String fild = this.FILD;
		FileOutputStream fo = null;
		OutputStreamWriter ow = null;
		PrintWriter out = null;
		int index = 1;
		
		try {
			if(h==0) {
                fo=new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fild);
            } else if(h==1) {
                fo=new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fild,true);
            }
			ow = new OutputStreamWriter(fo,"GBK");
			out = new PrintWriter(ow);
			DynaBean bean = new LazyDynaBean();
			if(h==0) {
                out.println("一.班子现状");
            } else if(h==1){
				out.println();
				out.println("三.后备干部");
			}
			for(int i=0;i<beanlist.size();i++){
				out.print("	("+index+").");
				bean=(DynaBean)beanlist.get(i);
				for(int r=0;r<fieldlist.size();r++)
				{
					FieldItem fielditem=(FieldItem)fieldlist.get(r);
					
					if(!fielditem.isVisible()) {
                        continue;
                    }
					String value=(String)bean.get(fielditem.getItemid());
					if(value==null||value.length()<=0|| "null".equalsIgnoreCase(value)) {
                        value="";
                    }
					if("E01A1".equalsIgnoreCase(fielditem.getItemid())){
						out.print(AdminCode.getCodeName("@K",value));
					}
					else if("D".equalsIgnoreCase(fielditem.getItemtype())|| "N".equalsIgnoreCase(fielditem.getItemtype())) {
                        out.print(fielditem.getItemdesc()+"("+value+")");
                    } else {
                        out.print(value);
                    }
					if(r!=(fieldlist.size()-1)) {
                        out.print("，");
                    } else {
                        out.println("。");
                    }
				}
				out.println();
				index++;
			}
			if(h==0) {
                out.println("二.分析");
            } else if(h==1) {
                out.println("四.分析");
            }
		} 
		catch (FileNotFoundException e) {e.printStackTrace();}
		catch(UnsupportedEncodingException e){e.printStackTrace();}
		finally{
				if(out!=null) {
                    out.close();
                }
		}
		return fild;
	}
	
	public String buildDataList(ArrayList datalist){
		String fild = this.FILD;
		FileOutputStream fo = null;
		OutputStreamWriter ow = null;
		PrintWriter out = null;
		
		try {
			fo=new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fild,true);
			ow = new OutputStreamWriter(fo,"GBK");
			out = new PrintWriter(ow);
			out.println("	"+this.getSNameDisplay()+":");
			CommonData vo = new CommonData();
			out.print("		");
			for(int i=0;i<datalist.size();i++){
				vo = (CommonData) datalist.get(i);
//				out.print(vo.getDataName()+": "+vo.getDataValue());
				String value = vo.getDataValue();
				if(Double.parseDouble(value) %1 == 0) {
                    out.print(vo.getDataName()+": "+(int)Double.parseDouble(value)+"人");//生成txt文件内容缺少“人”字 ,人数是整数 bug 37401  wangb 20180704
                } else {
                    out.print(vo.getDataName()+": "+vo.getDataValue()+"人");//生成txt文件内容缺少“人”字  bug 37401  wangb 20180704
                }
				if(i!=(datalist.size()-1)) {
                    out.print("，");
                } else{
					out.println("。");out.println();
				}
			}
		} catch (FileNotFoundException e) {e.printStackTrace();} 
		  catch (UnsupportedEncodingException e) {e.printStackTrace();}
		  finally{
			PubFunc.closeResource(out);
			PubFunc.closeResource(fo);
			PubFunc.closeResource(ow);
		  }
		return fild;
	}
	
	public void buildDoubleDataList(ArrayList varraylist,ArrayList harraylist){
		String fild = this.FILD;
		FileOutputStream fo = null;
		OutputStreamWriter ow = null;
		PrintWriter out = null;
		LazyDynaBean lb = new LazyDynaBean();
		int sum = 0;
		try {
			fo=new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fild,true);
			ow = new OutputStreamWriter(fo,"GBK");
			out = new PrintWriter(ow);
			out.println("	"+this.getSNameDisplay()+":");
			out.print("		");
			for(int i=0;i<varraylist.size();i++){
				CommonData vo = (CommonData)varraylist.get(i);
//				out.print(vo.getDataName()+": "+vo.getDataValue());
				String value = vo.getDataValue();
				if(Double.parseDouble(value) %1 == 0) {
                    out.print(vo.getDataName()+": "+(int)Double.parseDouble(value)+"人");//生成txt文件内容缺少“人”字 ,人数是整数 bug 37401  wangb 20180704
                } else {
                    out.print(vo.getDataName()+": "+vo.getDataValue()+"人");//生成txt文件内容缺少“人”字  bug 37401  wangb 20180704
                }
				if(i!=(varraylist.size()-1)){
					out.print("，");
				}else{
					out.print("。");out.println();out.println();
				}
			}
			out.print("		");
			for(int i=0;i<harraylist.size();i++){
				CommonData vo = (CommonData)harraylist.get(i);
//				out.print(vo.getDataName()+": "+vo.getDataValue());
				String value = vo.getDataValue();
				if(Double.parseDouble(value) %1 == 0) {
                    out.print(vo.getDataName()+": "+(int)Double.parseDouble(value)+"人");//生成txt文件内容缺少“人”字 ,人数是整数 bug 37401  wangb 20180704
                } else {
                    out.print(vo.getDataName()+": "+vo.getDataValue()+"人");//生成txt文件内容缺少“人”字  bug 37401  wangb 20180704
                }
				if(i!=(harraylist.size()-1)){
					out.print("，");
				}else{
					out.print("。");out.println();out.println();
				}
			}
		} catch (FileNotFoundException e) {e.printStackTrace();} 
		  catch (UnsupportedEncodingException e) {e.printStackTrace();}
		  finally{
			PubFunc.closeResource(out);
			PubFunc.closeResource(fo);
			PubFunc.closeResource(ow);
		  }
	}
	public void buildDataList(ArrayList datalist,StringBuffer txtfile,String unit){
		txtfile.append("<li style='list-style-type: square'>"+this.getSNameDisplay()+"：</li><ul><li style='list-style-type: disc'>");
		CommonData vo = null;
		for(int i=0;i<datalist.size();i++){
			vo = (CommonData) datalist.get(i);
			txtfile.append(vo.getDataName()+"：<U>&nbsp;"+vo.getDataValue()+"&nbsp;</U>"+unit);
			if(i!=(datalist.size()-1)) {
                txtfile.append("，");
            } else{
				txtfile.append("。</li></ul>");
			}
		}
	}
	public void buildDoubleDataList(ArrayList varraylist,ArrayList harraylist,StringBuffer txtfile,String unit){
		txtfile.append("<li style='list-style-type: square'>"+this.getSNameDisplay()+"：</li><ul><li style='list-style-type: disc'>");
			for(int i=0;i<varraylist.size();i++){
				CommonData vo = (CommonData)varraylist.get(i);
				txtfile.append(vo.getDataName()+"：<U>&nbsp;"+vo.getDataValue()+"&nbsp;</U>"+unit);
				if(i!=(varraylist.size()-1)){
					txtfile.append("，");
				}else{
					txtfile.append("。</li></ul><ul><li style='list-style-type: disc'>");
				}
			}
			for(int i=0;i<harraylist.size();i++){
				CommonData vo = (CommonData)harraylist.get(i);
				txtfile.append(vo.getDataName()+"：<U>&nbsp;"+vo.getDataValue()+"&nbsp;</U>"+unit);
				if(i!=(harraylist.size()-1)){
					txtfile.append("，");
				}else{
					txtfile.append("。</li></ul>");
				}
			}
	}
	public String getSNameDisplay() {
		return SNameDisplay;
	}
	public void setSNameDisplay(String nameDisplay) {
		SNameDisplay = nameDisplay;
	}
	
}
