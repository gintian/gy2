package com.hjsj.hrms.transaction.smartphone;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ShowCodeItemPageTrans extends IBusiness {

	/**
	 * <div data-role="page" id="codeitempage"> 
		<div data-role="header">
			<a href="#query" data-role="button" data-icon="forward">返回</a>
			<h1>&nbsp;</h1>
		</div>
		<div data-role="content"> 
			<ul data-role="listview"> 
					<li> 
						<img src="/images/icon_wsx.gif"  class="ui-li-icon"/> 
						<a href="#query">Broken Bells</a>
						<a href="index.html">Broken Bells</a>  
					</li> 
					<li> 
						<img src="/images/icon_wsx.gif"  class="ui-li-icon"/> 
						<a href="#query">Warning</a>
					</li> 
			</ul> 
		</div>	
	</div>
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String type = (String)this.getFormHM().get("type");
		String pageid = (String)this.getFormHM().get("pageid");
		StringBuffer html = new StringBuffer();
		if("codesetid".equals(type)){
			html.append("<div data-role=\"page\" id=\"codeitempage\"  data-inset=\"true\">");
		}else if("codeitemid".equals(type)){
			html.append("<div data-role=\"page\" id=\""+pageid+"\"  data-inset=\"true\">");
		}else if("back".equals(type)){
			html.append("<div data-role=\"page\" id=\""+pageid+"\"  data-inset=\"true\">");
		}
		try{
			String value = (String)this.getFormHM().get("value");
			String itemid = (String)this.getFormHM().get("itemid");
			FieldItem fielditem = DataDictionary
			.getFieldItem(itemid);
			ContentDAO dao = new ContentDAO(this.frameconn);
			String codeitemid="";
			if(fielditem!=null){
				String codesetid = fielditem.getCodesetid();
				String codesetdesc="";
				String sql="";
				if("codesetid".equals(type)){
					if("UN".equals(codesetid)){
						codesetdesc="单位";
					}else if("UM".equals(codesetid)){
						codesetdesc="部门";
					}else if("@K".equals(codesetid)){
						codesetdesc="岗位";
					}else{
						sql = "select codesetdesc from codeset where codesetid='"+codesetid+"'";
						this.frowset = dao.search(sql);
						if(this.frowset.next()){
							codesetdesc=this.frowset.getString("codesetdesc");
						}
					}
				}
				String parentid="";
				sql = "select parentid from codeitem where codeitemid='"+value+"' and codeitemid<>parentid";
				if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)){
					sql = "select parentid from organization where codeitemid='"+value+"' and codeitemid<>parentid union select parentid from vorganization where codeitemid='"+value+"' and codeitemid<>parentid";
				}
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					parentid=this.frowset.getString("parentid");
				}
				html.append("<div data-role=\"header\">");
					if("codesetid".equals(type)){	
						html.append("<a href=\"#query\" data-role=\"button\" data-icon=\"forward\">返回</a>");
						html.append("<h1>"+codesetdesc+"&nbsp;</h1>");
					}else if("codeitemid".equals(type)){
						if("childcodeitempage".equals(pageid)){
							html.append("<a href=\"javascript:getParentCode('"+itemid+"','"+parentid+"','childcodeitempage1');\" data-role=\"button\" data-icon=\"forward\">返回</a>");
						}else
							html.append("<a href=\"javascript:getParentCode('"+itemid+"','"+parentid+"','childcodeitempage');\" data-role=\"button\" data-icon=\"forward\">返回</a>");
						html.append("<h1>"+AdminCode.getCodeName(codesetid, value)+"&nbsp;</h1>");
					}else if("back".equals(type)){
						if("childcodeitempage".equals(pageid)){
							html.append("<a href=\"javascript:getParentCode('"+itemid+"','"+parentid+"','childcodeitempage1');\" data-role=\"button\" data-icon=\"forward\">返回</a>");
						}else
							html.append("<a href=\"javascript:getParentCode('"+itemid+"','"+parentid+"','childcodeitempage');\" data-role=\"button\" data-icon=\"forward\">返回</a>");
						html.append("<h1>"+AdminCode.getCodeName(codesetid, value)+"&nbsp;</h1>");
					}
					html.append("</div>");
					if("codesetid".equals(type)){
						sql = "select codeitemid,codeitemdesc,childid from codeitem where codesetid='"+codesetid+"' and codeitemdesc like '%"+value+"%' and "+Sql_switcher.length("codeitemid")+"=(select distinct min("+Sql_switcher.length("codeitemid")+") from codeitem where codesetid='"+codesetid+"' and codeitemdesc like '%"+value+"%')";
						if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)){
							sql = "select codeitemid,codeitemdesc,childid from organization where codesetid='"+codesetid+"' and codeitemdesc like '%"+value+"%' and "+Sql_switcher.length("codeitemid")+"=(select distinct min("+Sql_switcher.length("codeitemid")+") from organization where codesetid='"+codesetid+"' and codeitemdesc like '%"+value+"%')";
							sql+=" union select codeitemid,codeitemdesc,childid from vorganization where codesetid='"+codesetid+"' and codeitemdesc like '%"+value+"%' and "+Sql_switcher.length("codeitemid")+"=(select distinct min("+Sql_switcher.length("codeitemid")+") from vorganization where codesetid='"+codesetid+"' and codeitemdesc like '%"+value+"%')";
						}
					}else if("codeitemid".equals(type)){
						sql = "select codeitemid,codeitemdesc,childid from codeitem where codesetid='"+codesetid+"' and parentid='"+value+"' and codeitemid<>parentid";
						if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)){
							sql = "select codeitemid,codeitemdesc,childid from organization where codesetid='"+codesetid+"' and parentid='"+value+"' and codeitemid<>parentid";
							sql+=" union select codeitemid,codeitemdesc,childid from vorganization where codesetid='"+codesetid+"' and parentid='"+value+"' and codeitemid<>parentid";
						}
					}else if("back".equals(type)){
						sql = "select codeitemid,codeitemdesc,childid from codeitem where codesetid='"+codesetid+"' and parentid='"+value+"' and codeitemid<>parentid";
						if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)){
							sql = "select codeitemid,codeitemdesc,childid from organization where codesetid='"+codesetid+"' and parentid='"+value+"' and codeitemid<>parentid";
							sql+=" union select codeitemid,codeitemdesc,childid from vorganization where codesetid='"+codesetid+"' and parentid='"+value+"' and codeitemid<>parentid";
						}
					}
					this.frowset = dao.search(sql);
					html.append("<div data-role=\"content\">"); 
					html.append("<ul data-role=\"listview\">"); 
					while(this.frowset.next()){
						codeitemid = this.frowset.getString("codeitemid");
						String codeitemdesc = this.frowset.getString("codeitemdesc");
						String childid = this.frowset.getString("childid");
						if(codeitemid.equals(childid)){
							html.append("<li>"); 
							html.append("<img src=\"/images/icon_wsx.gif\"  class=\"ui-li-icon\"/>"); 
							html.append("<a href=\"javascript:setCodevalue('"+itemid+"','"+codeitemid+"','"+codeitemdesc+"');\">"+codeitemdesc+"</a>");
							html.append("</li>"); 
						}else{
							if("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)){
								sql = "select codeitemid from organization where codesetid='"+codesetid+"' and parentid='"+codeitemid+"' and parentid<>codeitemid";
								this.frecset = dao.search(sql);
								if(this.frecset.next()){
									html.append("<li>"); 
									html.append("<img src=\"/images/icon_wsx.gif\"  class=\"ui-li-icon\"/>"); 
									html.append("<a href=\"javascript:setCodevalue('"+itemid+"','"+codeitemid+"','"+codeitemdesc+"');\">"+codeitemdesc+"</a>");
									if("childcodeitempage".equals(pageid))
										html.append("<a href=\"javascript:getChildCode('"+itemid+"','"+codeitemid+"','childcodeitempage1');\" rel=\"external\">更多</a>");  
									else
										html.append("<a href=\"javascript:getChildCode('"+itemid+"','"+codeitemid+"','childcodeitempage');\" rel=\"external\">更多</a>");  
									html.append("</li>"); 
								}else{
									html.append("<li>"); 
									html.append("<img src=\"/images/icon_wsx.gif\"  class=\"ui-li-icon\"/>"); 
									html.append("<a href=\"javascript:setCodevalue('"+itemid+"','"+codeitemid+"','"+codeitemdesc+"');\">"+codeitemdesc+"</a>");
									html.append("</li>"); 
								}
							}else{
								html.append("<li>"); 
								html.append("<img src=\"/images/icon_wsx.gif\"  class=\"ui-li-icon\"/>"); 
								html.append("<a href=\"javascript:setCodevalue('"+itemid+"','"+codeitemid+"','"+codeitemdesc+"');\">"+codeitemdesc+"</a>");
								if("childcodeitempage".equals(pageid))
									html.append("<a href=\"javascript:getChildCode('"+itemid+"','"+codeitemid+"','childcodeitempage1');\" rel=\"external\">更多</a>");  
								else
									html.append("<a href=\"javascript:getChildCode('"+itemid+"','"+codeitemid+"','childcodeitempage');\" rel=\"external\">更多</a>");  
								html.append("</li>"); 
							}
						}
					}
					html.append("</ul>"); 
					html.append("</div>");
				
			}
			if("codesetid".equals(type)){
				html.append("<input type='hidden' id='backflag' value='"+codeitemid.length()+"' />");
			}
			html.append("</div>");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.formHM.put("html", html.toString());
		}
		
	}

}
