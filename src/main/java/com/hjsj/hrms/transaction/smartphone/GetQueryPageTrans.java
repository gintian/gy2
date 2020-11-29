package com.hjsj.hrms.transaction.smartphone;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class GetQueryPageTrans extends IBusiness {

	public void execute() throws GeneralException {

		/**
		 * <div data-role="content" id=\"qmain\" data-inset=\"true\">
				<div data-role="fieldcontain">
				    <label for="qname">姓名：</label>
				    <input type="text" id="qname" value=""  />
				</div>
				<div data-role="fieldcontain">
					<label for="qnumber">年龄：</label> 
					<fieldset data-role="controlgroup" data-type="horizontal"> 
					      <input type="radio" name="radio-choice-b" id="radio-choice-c" value="on" checked="checked" /> 
					      <label for="radio-choice-c">&gt;</label> 
					      <input type="radio" name="radio-choice-b" id="radio-choice-d" value="off" /> 
					      <label for="radio-choice-d">=</label> 
					      <input type="radio" name="radio-choice-b" id="radio-choice-e" value="other" /> 
					      <label for="radio-choice-e">&lt;</label> 
					</fieldset> 
		         	<input type="number" id="qnumber" value=""  /> 
				</div>
				<div data-role="fieldcontain">
				    <label for="qbirthday">出生日期：</label>
				    <input type="text" id="qbirthdaystar" value=""  />至
				    <input type="text" id="qbirthdayend" value=""  />
				</div>
				<div data-role="fieldcontain">
					<label for="select-choice-1" class="select">性别：</label>
					<select id="select-choice-1">
						<option value="0">男</option>
						<option value="1">女</option>
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="search">籍贯：</label>
				    <input type="search" id="search" value="" onchange="searchCodeItem(this);"/>
				</div>
				<button type="button" data-theme="a" onclick="">确定</button> 
			</div>
		 */
		String selectField = (String)this.userView.getHm().get("selectField");
		StringBuffer fieldtype = new StringBuffer();
		StringBuffer html = new StringBuffer();
		html.append("<div data-role=\"page\" id=\"query\" data-inset=\"true\">");
		html.append("<div data-role=\"header\" data-position=\"fixed\" data-position=\"inline\">");
		html.append("<a href=\"#mainbar\" data-role=\"button\" data-icon=\"forward\">返回</a>");
		html.append("<h1>快速查询&nbsp;</h1>");
		html.append("</div>");
		html.append("<div data-role=\"content\" id=\"qmain\">");
		try{
			String[] selectFields = selectField.toUpperCase().split(",");
			ContentDAO dao = new ContentDAO(this.frameconn);
			for(int i=0;i<selectFields.length;i++){
				String itemid = selectFields[i];
				FieldItem fielditem = DataDictionary
				.getFieldItem(itemid);
				if(fielditem!=null){
					String itemtype = fielditem.getItemtype();
					fieldtype.append(","+itemtype);
					String itemdesc = fielditem.getItemdesc();
					if("A".equals(itemtype)){
						String codesetid = fielditem.getCodesetid();
						if(codesetid!=null&&!"0".equals(codesetid)){
							String sql = "select codeitemid,codeitemdesc from codeitem where codeitemid<>childid and codesetid='"+codesetid+"'";
							if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)){
								sql = "select codeitemid,codeitemdesc from organization where codeitemid<>childid and codesetid='"+codesetid+"' union select codeitemid,codeitemdesc from vorganization where codeitemid<>childid and codesetid='"+codesetid+"'";
							}
							this.frowset = dao.search(sql);
							if(this.frowset.next()){//多层
								html.append("<div data-role=\"fieldcontain\">");
								html.append("<label for=\"q"+itemid+"\">"+itemdesc+"：</label>");
								html.append("<input type=\"search\" id=\"q"+itemid+"view\" value=\"\" onchange=\"searchCodeItem('"+itemid+"',this.value);\"/>");
								html.append("<input type=\"hidden\" id=\"q"+itemid+"\" value=\"\" />");
								html.append("</div>");
							}else{
								sql = "select codeitemid,codeitemdesc,(select count(codeitemid) from codeitem where codesetid='"+codesetid+"') as count from codeitem where codesetid='"+codesetid+"'";
								if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)){
									sql = "select codeitemid,codeitemdesc,(select count(codeitemid) from (select codeitemid from organization where codesetid='"+codesetid+"' union select codeitemid from vorganization where codesetid='"+codesetid+"') tt) as count from (select codeitemid,codeitemdesc from organization where codesetid='"+codesetid+"' union select codeitemid,codeitemdesc from vorganization where codesetid='"+codesetid+"')ttt";
								}
								this.frowset = dao.search(sql);
								if(this.frowset.next()){
									int count = this.frowset.getInt("count");
									if(count>10){
										html.append("<div data-role=\"fieldcontain\">");
										html.append("<label for=\"q"+itemid+"\">"+itemdesc+"：</label>");
										html.append("<input type=\"search\" id=\"q"+itemid+"view\" value=\"\" onchange=\"searchCodeItem('"+itemid+"',this.value);\"/>");
										html.append("<input type=\"hidden\" id=\"q"+itemid+"\" value=\"\" />");
										html.append("</div>");
									}else{
										html.append("<div data-role=\"fieldcontain\">");
										html.append("<label for=\"q"+itemid+"\" class=\"select\">"+itemdesc+"：</label>");
										html.append("<select id=\"q"+itemid+"\">");
										html.append("<option value=\"\" selected>&nbsp;</option>");
										String codeitemid = this.frowset.getString("codeitemid");
										String codeitemdesc = this.frowset.getString("codeitemdesc");
										html.append("<option value=\""+codeitemid+"\">"+codeitemdesc+"</option>");
										while(this.frowset.next()){
											codeitemid = this.frowset.getString("codeitemid");
											codeitemdesc = this.frowset.getString("codeitemdesc");
											html.append("<option value=\""+codeitemid+"\">"+codeitemdesc+"</option>");
										}
										html.append("</select>");
										html.append("</div>");
									}
								}
							}
						}else{
							html.append("<div data-role=\"fieldcontain\"><label for=\"q"+itemid+"\">"+itemdesc+"：</label><input type=\"text\" id=\"q"+itemid+"\" value=\"\"  /></div>");
						}
					}else if("N".equals(itemtype)){
						html.append("<div data-role=\"fieldcontain\">");
						html.append("<label for=\"q"+itemid+"\">"+itemdesc+"：</label>"); 
						html.append("<fieldset data-role=\"controlgroup\" data-type=\"horizontal\">"); 
						html.append("<input type=\"radio\" name=\"q"+itemid+"c\" id=\"radio-choice-c\" value=\"0\" checked=\"checked\" />"); 
						html.append("<label for=\"radio-choice-c\">&gt;</label>"); 
						html.append("<input type=\"radio\" name=\"q"+itemid+"c\" id=\"radio-choice-d\" value=\"1\" />"); 
						html.append("<label for=\"radio-choice-d\">=</label>"); 
						html.append("<input type=\"radio\" name=\"q"+itemid+"c\" id=\"radio-choice-e\" value=\"2\" />"); 
						html.append("<label for=\"radio-choice-e\">&lt;</label>");
						html.append("</fieldset>"); 
						html.append("<input type=\"number\" id=\"q"+itemid+"\" value=\"\"  />"); 
						html.append("</div>");
					}else if("D".equals(itemtype)){
						html.append("<div data-role=\"fieldcontain\">");
						html.append("<label for=\"q"+itemid+"\">"+itemdesc+"：</label>");
						html.append("<input type=\"text\" id=\"q"+itemid+"star\" value=\"\"  />至");
						html.append("<input type=\"text\" id=\"q"+itemid+"end\" value=\"\"  />");
						html.append("</div>");
					}
				}
			}
			ArrayList dbprelist = this.userView.getPrivDbList();
			StringBuffer sql = new StringBuffer("select pre,dbname from dbname where pre in('#'");
			for(int i=0;i<dbprelist.size();i++){
				sql.append(",'"+dbprelist.get(i)+"'");
			}
			sql.append(")");
			this.frowset = dao.search(sql.toString());
			html.append("<div data-role=\"fieldcontain\">");
			html.append("<label for=\"qnbase\" class=\"select\">人员类别：</label>");
			html.append("<select id=\"qnbase\" name=\"nbase\">");
			while(this.frowset.next()){
				html.append("<option value=\""+this.getFrowset().getString("pre")+"\">"+this.getFrowset().getString("dbname")+"</option>");
			}
			if(dbprelist.size()>1){
				html.append("<option value=\"all\">全部人员库</option>");
			}
			html.append("</select>");
			html.append("</div>");
			html.append("<button type=\"button\" data-theme=\"a\" onclick=\"javascript:queryP();\">确定</button>");
			html.append("</div></div>");
			html.append("<input type='hidden' id='fieldtype' value='"+fieldtype.substring(1)+"' />");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.formHM.put("html", html.toString());
		}
	}

}
