package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AssociateTrans extends IBusiness{

	public void execute() throws GeneralException {
		String nbase = this.getFormHM().get("nbase").toString(); 
		String a0100 = this.getFormHM().get("a0100").toString();
		
		ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
		String src = constantbo.getNodeAttributeValue("/param/teacher_items", "src");
		String subset = constantbo.getNodeAttributeValue("/param/teacher_items", "subset");
		String dest = constantbo.getNodeAttributeValue("/param/teacher_items", "dest");
		String [] srcs = src.split(",");
		String [] subsets = subset.split(",");
		String [] dests = dest.split(",");
		String values = "";
		String value = "";
		String codeitemid  = "";
		String isCheck = "";
		
		if(IsCheck(nbase,a0100)){
		ArrayList list = DataDictionary.getFieldList("r04", Constant.USED_FIELD_SET);
			for(int i = 0 ; i < srcs.length ; i ++){
				for(int j = 0 ; j < list.size() ; j++){
					FieldItem fieldItem = (FieldItem)list.get(j);
					if(fieldItem.getItemid().equals(dests[i])){
					    dest =dest.replaceAll(fieldItem.getItemid(), fieldItem.getItemid()+":"+fieldItem.getState());
                    }
				if(fieldItem.getItemid().equals(dests[i]) && !"0".equals(fieldItem.getCodesetid())){
					codeitemid = getCurrentPeopleInfoById(a0100, srcs[i], nbase, subsets[i]);
					value = codeitemid +"|"+ getCodeItemDescById(fieldItem.getCodesetid(), codeitemid);
					break;
				}else{
					if(fieldItem.getItemid().equals(dests[i]) && "D".equalsIgnoreCase(fieldItem.getItemtype())){
						value = getCurrentPeopleInfoById(a0100, srcs[i], nbase, subsets[i]);
						try {
							if(!"".equals(value) && value != null){								
								Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
								String now = new SimpleDateFormat("yyyy-MM-dd").format(date);
								value = now;
								break;
							}
						} catch (ParseException e) {
							e.printStackTrace();
						} 
					}else{
						value = getCurrentPeopleInfoById(a0100, srcs[i], nbase, subsets[i]);
					}
				}
				}
				values += value+",";	
		}

		this.getFormHM().put("dest", dest);
		this.getFormHM().put("values", values);
	}else{
		isCheck = "当前选择人员已经是培训教师,不能重复选择!";
	}
		this.getFormHM().put("isCheck", isCheck);
	}
	
	private String getCurrentPeopleInfoById(String a0100,String src,String nbase,String subsets){
		String s = "";
		String subset = nbase+subsets;
		if (("A01").equals(subsets)) {//判断是否是主集   
			//取主集数据
			if (!"".equals(a0100) && !"".equals(src) && !"#".equals(src)&& !"".equals(subset) && !"#".equals(subset)) {
				String sql = "select " + src + " from " + subset+ " where a0100 = '" + a0100 + "'";

				ContentDAO dao = new ContentDAO(this.getFrameconn());
				try {
					this.frowset = dao.search(sql);
					if (this.frowset.next()) {
						s = this.frowset.getString(src);
					}

				} catch (Exception e) {
					try {
						s = this.frowset.getDate(src).toString();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		}else {
			//取子集数据
			if (!"".equals(a0100) && !"".equals(src) && !"#".equals(src)&& !"".equals(subset) && !"#".equals(subset)) {
				String sql = "select " + src + " from " + subset+ " where a0100 = '" + a0100 + "' and I9999 = (select MAX(I9999) I9999 from "+ subset+" where a0100 = '" + a0100 + "')";

				ContentDAO dao = new ContentDAO(this.getFrameconn());
				try {
					this.frowset = dao.search(sql);
					if (this.frowset.next()) {
						s = this.frowset.getString(src);
					}

				} catch (Exception e) {
					try {
						s = this.frowset.getDate(src).toString();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		return s;
	}
	
	private String getCodeItemDescById(String codesetid , String codeitemid){
		String s = "";
		String sql = "select codeitemdesc from codeitem where codesetid = '"+codesetid+"' and codeitemid = '"+codeitemid+"'";
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				
				s = this.frowset.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private boolean IsCheck(String usr,String a0100){
        if (usr == null || usr.length() < 1 || a0100 == null || a0100.length() < 1) {
            return true;
        }
        
		boolean flag = true;
		String sql = " select r0401 from r04 where nbase  = '" + usr + "' and a0100 = '" + a0100 +"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				flag = false;
			}else{
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}	
