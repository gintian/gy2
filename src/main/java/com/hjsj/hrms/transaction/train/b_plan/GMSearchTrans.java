package com.hjsj.hrms.transaction.train.b_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class GMSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");

		String id = (String)reqhm.get("id");
		id=id!=null&&id.trim().length()>0?id:"";
		reqhm.remove("id");
		
		String type = (String)reqhm.get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		reqhm.remove("type");
		
		String tablename = (String)reqhm.get("tablename");
		tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
		reqhm.remove("tablename");
		
		ArrayList list=DataDictionary.getFieldList(tablename,Constant.USED_FIELD_SET);
		
		ArrayList fieldsetlist = new ArrayList();
		for(int i=0;i<list.size();i++){
			FieldItem fieldItem = (FieldItem)list.get(i);
			if("M".equals(fieldItem.getItemtype()))
		    	continue;
			if("0".equalsIgnoreCase(fieldItem.getUseflag()))
				continue;
			if(fieldItem.getItemid().equalsIgnoreCase(tablename+"01"))
				continue;	
			//去掉培训计划中通用查询的计划表审批方式查询指标
			if("r2512".equalsIgnoreCase(fieldItem.getItemid())|| "R2512".equalsIgnoreCase(fieldItem.getItemid()))
				continue;
			CommonData obj = new CommonData(fieldItem.getItemid()+":"+fieldItem.getItemtype()
		    		  +":"+fieldItem.getCodesetid()+":"+fieldItem.getFieldsetid(),
		    		  fieldItem.getItemdesc());
			fieldsetlist.add(obj);
		}
		
		this.getFormHM().put("fieldlist",fieldsetlist);
		this.getFormHM().put("tablename",tablename);
		this.getFormHM().put("type",type);
		tableStr(id, list);
	}
	private void tableStr(String id, ArrayList itemList){
		StringBuffer buf = new StringBuffer();
		
		if(id!=null&&id.length()>0){
			ContentDAO dao = new ContentDAO(this.frameconn);
			RecordVo vo = new RecordVo("LExpr");
			vo.setInt("id",Integer.parseInt(id));
			try {
				vo = dao.findByPrimaryKey(vo);
				String factor = vo.getString("factor");
				String lexpr = vo.getString("lexpr");
				buf.append(factorStr(factor, itemList));
				buf.append("||"+lexpr);
				buf.append("||"+vo.getString("history"));
				buf.append("||"+vo.getString("fuzzyflag"));
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.getFormHM().put("searchstr",buf.toString());
		}else{
			this.getFormHM().put("searchstr","");
		}
		
	}
	private String factorStr(String factor, ArrayList itemList){
		StringBuffer buf = new StringBuffer();
		String arr[] = factor.split("`");
		if(arr.length>0){
			for(int i=0;i<arr.length;i++){
				String eq = "";
				if(arr[i].indexOf("<>")!=-1){
					eq = "<>";
				}else if(arr[i].indexOf("<=")!=-1){
					eq = "<=";
				}else if(arr[i].indexOf(">=")!=-1){
					eq = ">=";
				}else if(arr[i].indexOf("=")!=-1){
					eq = "=";
				}else if(arr[i].indexOf("<")!=-1){
					eq = "<";
				}else if(arr[i].indexOf(">")!=-1){
					eq = ">";
				}
				String fa = arr[i].replaceAll("<>",":").replaceAll(">=",":").replaceAll("<=",":").replaceAll("=",":");
				fa=fa.replaceAll("<",":").replaceAll(">",":");
				String itemarr[] = fa.split(":");
				if(itemarr.length>0){
					String itemid = itemarr[0];
					if(itemid.length()>1){
						FieldItem fielditem = getItemById(itemid, itemList);
						if(fielditem!=null){
							String desc = itemarr.length==2?itemarr[1]:"";
							if(fielditem.isCode()){
								String code = "";
								if(desc.indexOf("*")==-1)
									code=desc+","+AdminCode.getCodeName(fielditem.getCodesetid(),desc);
								else
									code=desc+","+desc;
								buf.append(itemid+":"+fielditem.getItemdesc()+":");
								buf.append(fielditem.getCodesetid()+":"+fielditem.getItemtype()+":"+eq+":"+code);
								buf.append(":"+fielditem.getFieldsetid()+"`");
							}else{
								buf.append(itemid+":"+fielditem.getItemdesc()+":");
								buf.append(fielditem.getCodesetid()+":"+fielditem.getItemtype()+":"+eq+":"+desc);
								buf.append(":"+fielditem.getFieldsetid()+"`");
							}
						}
					}
				}
			}
		}
		return buf.toString();
	}
	
	private FieldItem getItemById(String itemid, ArrayList itemList){
		FieldItem item = null;
		for(int i = 0; i < itemList.size(); i++){
			FieldItem aItem = (FieldItem)itemList.get(i);
			if(aItem.getItemid().equalsIgnoreCase(itemid)){
				item = aItem;
				break;
			}
		}
		return item;
	}
}
