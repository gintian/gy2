package com.hjsj.hrms.transaction.train.station;
/**
 * LiWeichao
 */

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveTrainStationTrans extends IBusiness {

	public void execute() throws GeneralException {
		String flag = (String)this.getFormHM().get("flag");
		if("0".equals(flag)){
			String mess="ok";
			String nbase=(String)this.getFormHM().get("nbase");
			String emp_setid=(String)this.getFormHM().get("emp_setid");
			String emp_coursecloumn=(String)this.getFormHM().get("emp_coursecloumn");
			String post_setid=(String)this.getFormHM().get("post_setid");
			String post_coursecloumn=(String)this.getFormHM().get("post_coursecloumn");
			
			String emp_passcloumn=(String)this.getFormHM().get("emp_passcloumn");
			String emp_passvalues=(String)this.getFormHM().get("emp_passvalues");
			if((emp_coursecloumn==null||emp_coursecloumn.length()<2)&&(post_coursecloumn==null||post_coursecloumn.length()<2))
				mess="ok";
			else if((emp_coursecloumn==null||emp_coursecloumn.length()<2)||(post_coursecloumn==null||post_coursecloumn.length()<2))
				mess="inequality";
			else{
				FieldItem temp1=DataDictionary.getFieldItem(emp_coursecloumn);
				FieldItem temp2=DataDictionary.getFieldItem(post_coursecloumn);
				if(temp1==null||temp2==null)
					mess="errer";
				else if(!temp1.getCodesetid().equalsIgnoreCase(temp2.getCodesetid()))
					mess="inequality";
			}
			if("ok".equalsIgnoreCase(mess)){
				ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
				constantbo.setTextValue("/param/post_traincourse", null);
				constantbo.setTextValue("/param/post_traincourse/nbase", nbase);
				constantbo.setTextValue("/param/post_traincourse/emp_setid", emp_setid);
				constantbo.setTextValue("/param/post_traincourse/emp_coursecloumn", emp_coursecloumn);
				constantbo.setTextValue("/param/post_traincourse/post_setid", post_setid);
				constantbo.setTextValue("/param/post_traincourse/post_coursecloumn", post_coursecloumn);
				constantbo.setTextValue("/param/post_traincourse/emp_passcloumn", emp_passcloumn);
				constantbo.setTextValue("/param/post_traincourse/emp_passvalues", emp_passvalues);
				constantbo.saveStrValue();
			}
			this.getFormHM().put("mess", mess);
			
		}else if("1".equals(flag)){
			String setid=(String)this.getFormHM().get("emp_setid");
			StringBuffer value1=new StringBuffer();
			StringBuffer text1=new StringBuffer();
			value1.append("#,");
			text1.append("请选择...,");
			if(setid!=null&&setid.length()>1){
				ArrayList list = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
				for(int i=0;i<list.size();i++){
					FieldItem field=(FieldItem)list.get(i);
					if("A".equalsIgnoreCase(field.getItemtype())&&!"0".equalsIgnoreCase(field.getCodesetid())){
						value1.append(field.getItemid()+",");
						text1.append(field.getItemdesc()+",");
					}
				}
			}
			value1.setLength(value1.length()-1);
			text1.setLength(text1.length()-1);
			this.getFormHM().put("value1", value1.toString());
			this.getFormHM().put("text1", text1.toString());
		}else if("2".equals(flag)){
			String setid=(String)this.getFormHM().get("post_setid");
			StringBuffer value2=new StringBuffer();
			StringBuffer text2=new StringBuffer();
			value2.append("#,");
			text2.append("请选择...,");
			if(setid!=null&&setid.length()>1){
				ArrayList list = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
				for(int i=0;i<list.size();i++){
					FieldItem field=(FieldItem)list.get(i);
					if("A".equalsIgnoreCase(field.getItemtype())&&!"0".equalsIgnoreCase(field.getCodesetid())){
						value2.append(field.getItemid()+",");
						text2.append(field.getItemdesc()+",");
					}
				}
			}
			value2.setLength(value2.length()-1);
			text2.setLength(text2.length()-1);
			this.getFormHM().put("value2", value2.toString());
			this.getFormHM().put("text2", text2.toString());
		}else if("3".equals(flag))
		{
			String column=(String)this.getFormHM().get("pass_column");
			String checked=(String)this.getFormHM().get("checked");
			String emp_passvalues=(String)this.getFormHM().get("emp_passvalues");
			emp_passvalues=emp_passvalues!=null?emp_passvalues:"";
			StringBuffer html=new StringBuffer();
			if(column!=null&&column.length()>0)
			{
				FieldItem fielditem=DataDictionary.getFieldItem(column);
				if(fielditem!=null&&fielditem.getCodesetid()!=null&&!"0".equals(fielditem.getCodesetid()))
				{
					String codesetid=fielditem.getCodesetid();
					ArrayList list=AdminCode.getCodeItemList(codesetid);
					html.append("<table width=\"100%\" border=\"0\" cellspacing=\"1\" align=\"center\" cellpadding=\"1\">");
					for(int i=0;i<list.size();i++)
					{
						CodeItem item=(CodeItem)list.get(i);
						String itemid=item.getCcodeitem();
						html.append("<tr>");
						html.append("<td align='center' width=\"40\">");
						if("1".equals(checked)&&emp_passvalues.indexOf(itemid)!=-1)
						   html.append("<input type=\"checkbox\" name=\"passitem\" style=\"vertical-align: middle; \" value='"+itemid+"' checked/>");
						else
						   html.append("<input type=\"checkbox\" name=\"passitem\" style=\"vertical-align: middle; \" value='"+itemid+"'/>");
						html.append("<td>");
                        html.append("<td align='left'>&nbsp;");
                    	html.append(item.getCodename());
						html.append("<td>");
						html.append("</tr>");
					}
					html.append("</table>");
				}
			}
			this.getFormHM().put("text3", SafeCode.encode(html.toString()));			
		}
	}
}
