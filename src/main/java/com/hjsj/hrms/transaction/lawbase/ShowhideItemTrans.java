package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class ShowhideItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		String basetype = (String)this.getFormHM().get("basetype");
		String field_str_item=(String)this.getFormHM().get("field_str_item");
		field_str_item=field_str_item==null?"":field_str_item;
		String viewhide = "";
		if("5".equals(basetype)){//LAW_BASE_DOCTYPE
			RecordVo vo  = ConstantParamter.getConstantVo("LAW_BASE_DOCTYPE", this.frameconn);
			if(vo!=null){
				viewhide = vo.getString("str_value");
			}
		}else if("1".equals(basetype)){//LAW_BASE_LAWRULE
			RecordVo vo  = ConstantParamter.getConstantVo("LAW_BASE_LAWRULE", this.frameconn);
			if(vo!=null){
				viewhide = vo.getString("str_value");
			}
		}else if("4".equals(basetype)){//LAW_BASE_KNOWTYPE
			RecordVo vo  = ConstantParamter.getConstantVo("LAW_BASE_KNOWTYPE", this.frameconn);
			if(vo!=null){
				viewhide = vo.getString("str_value");
			}
		}
		viewhide = viewhide==null?"":viewhide;
		ArrayList itemlist = new ArrayList();
		CommonData cd = null;
		if(field_str_item.length()<=0){
		//cd = new CommonData("title",ResourceFactory.getProperty("column.law_base.title"));//标题
		//itemlist.add(cd);
			if(!"5".equals(basetype)){
				cd = new CommonData("type",ResourceFactory.getProperty("lable.lawfile.typenum"));//分类号（文档没有）
				itemlist.add(cd);
			}
			cd = new CommonData("content_type",ResourceFactory.getProperty("lable.lawfile.contenttype"));//内容分类
			itemlist.add(cd);
			if(!"5".equals(basetype)){
				cd = new CommonData("valid",ResourceFactory.getProperty("lable.lawfile.valid"));//时效性（文档没有）
				itemlist.add(cd);
			}
			cd = new CommonData("note_num",ResourceFactory.getProperty("lable.lawfile.notenum"));//文号
			itemlist.add(cd);
			if("5".equals(basetype)){
				cd = new CommonData("b0110",ResourceFactory.getProperty("lable.lawfile.ascriptionunit"));//归属单位（只文档有）
				itemlist.add(cd);
			}
			if(!"5".equals(basetype)){
				cd = new CommonData("issue_org",ResourceFactory.getProperty("lable.lawfile.issue_org"));//颁布单位（文档没有）
				itemlist.add(cd);
			
				cd = new CommonData("notes",ResourceFactory.getProperty("lable.lawfile.note"));//题注（文档没有）
				itemlist.add(cd);
			}
			//cd = new CommonData("issue_date",ResourceFactory.getProperty("lable.lawfile.printmandate"));//颁布日期
			//itemlist.add(cd);
			if(!"5".equals(basetype)){
				cd = new CommonData("implement_date",ResourceFactory.getProperty("lable.lawfile.actualizedate"));//实施日期（文档没有）
				itemlist.add(cd);
				cd = new CommonData("valid_date",ResourceFactory.getProperty("lable.lawfile.invalidationdate"));//失效日期（文档没有）
				itemlist.add(cd);
			}
			cd = new CommonData("name",ResourceFactory.getProperty("column.law_base.filename"));//文件名称
			itemlist.add(cd);
			//cd = new CommonData("content",ResourceFactory.getProperty("lable.lawfile.upfile"));//上传文件
			//itemlist.add(cd);
			if("5".equals(basetype)){
				cd = new CommonData("originalfile",ResourceFactory.getProperty("lable.lawfile.upmanuscript"));//上传原件（只文档有）
				itemlist.add(cd);
			}
			if(!"5".equals(basetype)){
				cd = new CommonData("digest",ResourceFactory.getProperty("label.law_base.affixdigest"));//附件描述（文档没有）
				itemlist.add(cd);
			}
			cd = new CommonData("viewcount",ResourceFactory.getProperty("lable.lawfile.viewcount"));//浏览次数
			itemlist.add(cd);
			cd = new CommonData("extfile",ResourceFactory.getProperty("conlumn.resource_list.name"));//附件
			itemlist.add(cd);
		}else{
			String[] items = field_str_item.split(",");
	        for(int i=0;i<items.length;i++)
	        {
	        	String[] itemss = items[i].split("`");
	        	if(itemss.length==2){
	        		 CommonData dataobj = new CommonData(itemss[0],itemss[1]);
	        		 itemlist.add(dataobj);
	        	}
	        }
		}
		StringBuffer sb = new StringBuffer(); 
		for(int i=0;i<itemlist.size();i++){
			cd = (CommonData)itemlist.get(i);
			sb.append(this.trStr(cd.getDataValue(), cd.getDataName(), viewhide, i));
		}
		this.getFormHM().put("viewhide", sb.toString());
	}

	private String trStr(String itemid,String itemdesc,String viewhide,int m){
		StringBuffer tableview = new StringBuffer();
		if(m%2==0){
			tableview.append("<tr class='trShallow'>");
		}else{
			tableview.append("<tr class='trDeep'>");
		}
		tableview.append("<td align='center' class='RecordRow' style='border-left: 0px;' nowrap>&nbsp;");
		tableview.append(itemdesc);
		tableview.append("</td><td align='center' style='border-right: 0px;' class='RecordRow' nowrap>");
		tableview.append("<select name='");
		tableview.append(itemid);
		tableview.append("'>");
		tableview.append("<option value='' ");
		tableview.append(">");
		tableview.append(ResourceFactory.getProperty("lable.channel.visible"));
		tableview.append("</option>");
		tableview.append("<option value='"+itemid+"' ");
		if(viewhide.indexOf(","+itemid+",")!=-1)
			tableview.append("selected");
		tableview.append(">");
		tableview.append(ResourceFactory.getProperty("lable.channel.hide"));
		tableview.append("</option>");
		tableview.append("</select></td></tr>");
		
		return tableview.toString();
	}

}
