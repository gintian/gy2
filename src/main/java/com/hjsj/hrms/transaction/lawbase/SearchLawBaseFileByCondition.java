package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.param.DocumentParamXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 
 *<p>Title:SearchLawBaseFileByCondition.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 11, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SearchLawBaseFileByCondition extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String a_base_id = "";
		if (hm.get("a_base_id") != null) {
			a_base_id = (String) hm.get("a_base_id");
		}
		if (getFormHM().get("base_id") != null) {
			a_base_id = (String) getFormHM().get("base_id");
		}
		DocumentParamXML documentparamXML = new DocumentParamXML(this.getFrameconn());
		String fileitem = documentparamXML.getValue(DocumentParamXML.FILESET,"fielditem");
		String setid = documentparamXML.getValue(DocumentParamXML.FILESET,"setid");
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
		String note_num = (String)this.getFormHM().get("note_nums");
		note_num = note_num.replaceAll("\\[", "%").replaceAll("\\]", "%");
		String contenttype = (String)this.getFormHM().get("contenttype");
		contenttype = contenttype.replaceAll("\\[", "%").replaceAll("\\]", "%");
		String issuedate = (String)this.getFormHM().get("issuedate");
		String enddate = (String)this.getFormHM().get("enddate");
		String name = (String)this.getFormHM().get("username");
		String basetype = (String)this.getFormHM().get("basetype");
		String keyword = (String)this.getFormHM().get("keyword");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		if(name==null|| "".equalsIgnoreCase(name)){
			name="";
		}
		ArrayList dblist = new ArrayList();
		ArrayList userlist = new ArrayList();
		ArrayList stufflist = new ArrayList();
		String dbsql = "select Pre from dbname ";
		ArrayList list = new ArrayList();
		try {
			this.frowset = dao.search(dbsql);
			while(this.frowset.next()){
				dblist.add(this.frowset.getString("Pre"));
			}
		
		//StringBuffer usersql = new StringBuffer();
			if(!"".equalsIgnoreCase(name)){
				name = PubFunc.getStr(name);
				for(int i=0;i<dblist.size();i++){
					String usersql = "select A0100 from "+dblist.get(i)+"A01 where A0101 like '"+name+"%'";
					if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) ))
						usersql = "select A0100 from "+dblist.get(i)+"A01 where (A0101 like '"+name+"%' or "+pinyin_field+" like '"+name+"%')"  ;
					this.frowset = dao.search(usersql);
					while(this.frowset.next()){
						userlist.add(dblist.get(i)+this.frowset.getString("A0100"));
					}
				}
			}
			if(userlist!=null&&userlist.size()>0)
			for(int i=0;i<dblist.size();i++){
				StringBuffer usersql = new StringBuffer();
				usersql.append("select "+fileitem+" from "+dblist.get(i)+setid+" where a0100 in (");
				int index = 0;
				for(int j=0;j<userlist.size();j++){
					if(userlist.get(j).toString().substring(0,3).equalsIgnoreCase(dblist.get(i).toString())){
						usersql.append("'"+userlist.get(j).toString().substring(3,userlist.get(j).toString().length())+"',");
						index++;
					}
				}
				if(index!=0){
					usersql.setLength(usersql.length()-1);
					usersql.append(")");
					this.frowset = dao.search(usersql.toString());
					while(this.frowset.next()){
						stufflist.add(this.frowset.getString(fileitem));
					}
				}
			}
		
		String orgId = userView.getUserOrgId();
		String orgTerm = "status = 1 and basetype="+ this.getFormHM().get("basetype");
		String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
		if(!"false".equals(law_file_priv.trim())){
		if(!(this.userView.isSuper_admin()&&!this.userView.isBThreeUser()))
		{
			if(orgId==null||orgId.length()<=0)
			{
				orgTerm=orgTerm+ " and (dir = '' or dir = '-1' or dir is null)";
				
			}else
			{
				LawDirectory lawDirectory=new LawDirectory();
				String orgsrt=lawDirectory.getOrgStrs(orgId,"UN",this.getFrameconn());
				orgTerm=orgTerm+ " and (dir = '' or dir = '-1' or dir in (" + orgsrt
				+ ") or dir is null )";
				//orgTerm=orgTerm+ " and (dir = '"+orgId+"')";
			}
			
		}
		}
		
		ArrayList fieldlist = new ArrayList();
		StringBuffer colums = new StringBuffer();
		String field_str_item="";
		StringBuffer sb = new StringBuffer();
			String field_str = "";
			
			if (a_base_id == null || "".equals(a_base_id)
					|| "null".equals(a_base_id.trim())
					|| "root".equals(a_base_id.trim())) {
				String sql = "select * from law_base_struct where field_str is not null and basetype="+basetype;
				this.frecset = dao.search(sql);
				if(this.frecset.next()){
					sql = "select base_id,field_str from law_base_struct where base_id=up_base_id and basetype="+basetype+" order by displayorder";
					this.frecset = dao.search(sql);
					if(this.frecset.next()){
						//a_base_id=this.frecset.getString("base_id");   //zhangcq 2016/8/20  base_id 不可从sql查询获取 否则机构树的父节点查询数据不正确 次Id是通过form获取的
						field_str = this.frecset.getString("field_str");
						if(field_str!=null&&field_str.length()>0){
							ArchiveXml xml = new ArchiveXml();
							field_str_item = xml.getElement("item", field_str);
							String downstr = xml.getElement("listing", field_str);
							field_str=downstr;
						}else{
							field_str="";
						}
					}	
				}else{
					field_str="";
				}
			}else{
				getFieldStr(a_base_id,dao,sb);
				if(sb.length()>2){
					String[] temps= sb.toString().split("``");
					if(temps.length>1){
						field_str_item= temps[0];
						field_str=temps[1];
					}else if(temps.length>0){
						field_str_item= temps[0];
						field_str="";
					}else{
						field_str_item="";
						field_str="";
					}
				}else{
					field_str_item="";
					field_str="";
				}
				
			}
			
			String [] fields_str= field_str.split(",");
			for(int i=0;i<fields_str.length;i++){
				String field_desc=fields_str[i];
				if(field_desc.length()>0&&field_desc.indexOf("`")!=-1){
					String[] tmps=field_desc.split("`");
					String itemid = tmps[0];
					String itemdesc=tmps[1];
					if("digest".equalsIgnoreCase(itemid))
						continue;
					if("extfile".equals(itemid)){
						FieldItem item = new FieldItem("","extfile");
						item.setItemdesc(itemdesc);
						fieldlist.add(item);
					}else{
						FieldItem item = DataDictionary.getFieldItem(itemid);
						if(item!=null){
							if("M".equalsIgnoreCase(item.getItemtype()))
								continue;
							if("base_id".equalsIgnoreCase(itemid)){
								
							}else{
								colums.append(",law_base_file."+itemid+" "+itemid);
							}
							item = (FieldItem)item.cloneItem();
							item.setItemdesc(itemdesc);
							fieldlist.add(item);
						}
					}
				}
				
			}
			if(colums.length()<=0){
				fields_str= field_str_item.split(",");
				for(int i=0;i<fields_str.length;i++){
					String field_desc=fields_str[i];
					if(field_desc.length()>0&&field_desc.indexOf("`")!=-1){
						String[] tmps=field_desc.split("`");
						String itemid = tmps[0];
						String itemdesc=tmps[1];
						if("digest".equalsIgnoreCase(itemid))
							continue;
						if("extfile".equals(itemid)){
							FieldItem item = new FieldItem("","extfile");
							item.setItemdesc(itemdesc);
							fieldlist.add(item);
						}else{
							FieldItem item = DataDictionary.getFieldItem(itemid);
							if(item!=null){
								if("M".equalsIgnoreCase(item.getItemtype()))
									continue;
								if("base_id".equalsIgnoreCase(itemid)){
									
								}else{
									colums.append(",law_base_file."+itemid+" "+itemid);
								}
								item = (FieldItem)item.cloneItem();
								item.setItemdesc(itemdesc);
								fieldlist.add(item);
							}
						}
					}
					
				}
			}
			
		StringBuffer sqlBuffer = null;//add 2008-08-12
		TreeHandle treehandle = new TreeHandle(this.frameconn);
		
		if(colums.length()>0){
			sqlBuffer = new StringBuffer(
					"select file_id,law_base_file.base_id"+colums+ ",keywords,displayorder from law_base_file left join law_base_struct on law_base_file.base_id = law_base_struct.base_id ");
			
		}else{
		sqlBuffer = new StringBuffer(
				"select file_id,law_base_file.base_id,content_type,law_base_file.name,title,type,valid,note_num,"
						+ "issue_org,notes,issue_date,implement_date,valid_date,ext, viewcount,originalext, b0110,keywords,displayorder"
						+ " from law_base_file");
		}
		if (a_base_id == null || "".equals(a_base_id)
				|| "null".equals(a_base_id.trim())
				|| "root".equals(a_base_id.trim())) {
			if((this.userView.isSuper_admin()&&!this.userView.isBThreeUser())||!"false".equals(law_file_priv.trim())){
			sqlBuffer
					.append(" where (law_base_file.base_id in (select base_id from law_base_struct where "
							+ orgTerm + ") or UPPER(law_base_file.base_id)='ALL')");
			}else{
				sqlBuffer.append(" where (1=2");
				ArrayList base_ids=(ArrayList)this.getBaseids(basetype,dao);
				for(int i=0;i<base_ids.size();i++){
					String _base_id=(String)base_ids.get(i);
					sqlBuffer.append(" or law_base_file.base_id='" + _base_id + "'");
					String temp = treehandle.selectAllParentStr("law_base_struct",
							"up_base_id", "base_id", _base_id, null, false);
					if (temp != null && !"".equals(temp)) {
						temp = temp.replaceAll("base_id","law_base_file.base_id");
						sqlBuffer.append(" or " + temp);
					}
				}
				sqlBuffer
				.append(")");
			}
		} else {
			String temp = treehandle.selectAllParentStr("law_base_struct",
					"up_base_id", "base_id", a_base_id, null, false);
			sqlBuffer.append(" where (law_base_file.base_id='" + a_base_id + "'");
			if (temp != null && !"".equals(temp)) {
				temp = temp.replaceAll("base_id","law_base_file.base_id");
				sqlBuffer.append(" or " + temp);
			}
			sqlBuffer.append(" or UPPER(law_base_file.base_id)='ALL' ");
			sqlBuffer.append(")");
		}
		
		StringBuffer ss = new StringBuffer();
		if(colums.length()>0){
			ss.append("select distinct(law_base_file.file_id),law_base_file.base_id"+colums+ ",viewcount,keywords,displayorder,law_ext_file.file_id a,fileorder from law_base_file left join law_ext_file on law_base_file.file_id = law_ext_file.file_id left join law_base_struct on law_base_file.base_id = law_base_struct.base_id ");
		}else{
			ss.append("select distinct(law_base_file.file_id),law_base_file.base_id,content_type,law_base_file.name,title,type,valid,note_num,issue_org,notes,issue_date,implement_date,valid_date,law_base_file.ext, viewcount ,fileorder,originalext,law_ext_file.file_id a,displayorder from law_base_file left join law_ext_file on law_base_file.file_id = law_ext_file.file_id left join law_base_struct on law_base_file.base_id = law_base_struct.base_id ");
		}
		CommonBusiness comnbus = new CommonBusiness(getFrameconn());
		String sqlText = sqlBuffer.toString();// comnbus.findLawbaseFile(a_base_id, orgId, orgTerm);
		StringBuffer whereIN=new StringBuffer();
		String cur_d=PubFunc.getStringDate("yyyy-MM-dd");
		whereIN.append("where valid = '1'");
		whereIN.append(" and (implement_date<="+Sql_switcher.dateValue(cur_d)+" or implement_date is null)");
		whereIN.append(" and (valid_date>="+Sql_switcher.dateValue(cur_d)+" or  valid_date is null )");
		
		if(note_num!=null&&!"".equals(note_num)){
			note_num = PubFunc.getStr(note_num);
			whereIN.append(" and note_num like '%"+note_num+"%'");
		}
		if(contenttype!=null&&!"".equals(contenttype)){
			contenttype = PubFunc.getStr(contenttype);
			whereIN.append(" and content_type='"+contenttype+"'");
		}
		
		if(keyword!=null&&!"".equals(keyword)){
			keyword = PubFunc.getStr(keyword);
			if(field_str_item.length()<5){
				whereIN.append(" and (law_base_file.name like '%"+keyword+"%'");
				whereIN.append(" or law_base_file.title like '%"+keyword+"%'");
				whereIN.append(" or law_base_file.Content_type like '%"+keyword+"%'");
				whereIN.append(" or law_base_file.type like '%"+keyword+"%'");
				whereIN.append(" or law_base_file.Note_num like '%"+keyword+"%'");
				whereIN.append(" or law_base_file.Issue_org like '%"+keyword+"%'");
				whereIN.append(" or law_base_file.Notes like '%"+keyword+"%'");
				whereIN.append(" or law_base_file.keywords like '%"+keyword+"%'");
				whereIN.append(" or law_base_file.digest like '%"+keyword+"%')");
			}else{
				whereIN.append(" and (law_base_file.title like '%"+keyword+"%'");
				String[] fieldstrs = field_str_item.split(",");
				String str="title,file_id,valid,ext,base_id,originalext,b0110,";
				for(int i=0;i<fieldstrs.length;i++){
					String fieldstr = fieldstrs[i];
					String tmps[]=fieldstr.split("`");
					if(tmps.length==2){
						String field = tmps[0].toLowerCase();
						if(str.indexOf(field+",")==-1){
							FieldItem item = com.hrms.hjsj.sys.DataDictionary.getFieldItem(field);
							if(item!=null&&(("A".equals(item.getItemtype())&&("0".equals(item.getCodesetid())||item.getCodesetid().length()<1))|| "M".equals(item.getItemtype())))
								whereIN.append(" or law_base_file."+field+" like '%"+keyword+"%'");
						}
					}
				}
				whereIN.append(")");
			}
			//whereIN.append(" and keywords like '%"+keyword+"%'");
		}
		if(issuedate!=null&&!"".equals(issuedate)){
			java.sql.Date date = java.sql.Date.valueOf(issuedate.replaceAll("\\.", "-"));
			whereIN.append(" and issue_date>="+Sql_switcher.dateValue(date.toString())+"");
		}
		if(enddate!=null&&!"".equals(enddate)){
			java.sql.Date date = java.sql.Date.valueOf(enddate.replaceAll("\\.", "-"));
			whereIN.append(" and issue_date<="+Sql_switcher.dateValue(date.toString())+"");
		}
		
		StringBuffer stuffsql = new StringBuffer();
		if(name!=null&&!"".equalsIgnoreCase(name)){
			if(stufflist!=null&&stufflist.size()>0){
				stuffsql.append(" and law_base_file.file_id in (");
				for(int i=0;i<stufflist.size();i++){
					//if(stufflist.get(i).)
					stuffsql.append("'"+stufflist.get(i)+"',");
				}
				stuffsql.setLength(stuffsql.length()-1);
				stuffsql.append(")");
			}
			else{
				stuffsql.append(" and 1=2");
			}
		}
		whereIN.append(stuffsql.toString());
		whereIN.append(" and ");
		String s = sqlText.substring(sqlText.indexOf("where"),sqlText.length());
//		s = s.replaceFirst("base_id","law_base_file.base_id");
		ss.append(s);
		//ss = ss.append(whereIN.toString());
		sqlText = ss.toString();
		sqlText = sqlText.replaceFirst("where", whereIN.toString());
		String orderText=" order by displayorder,fileorder  desc";
		if("5".equalsIgnoreCase(this.getFormHM().get("basetype").toString())&&!"false".equals(law_file_priv.trim())){
			//String unitid = userView.getUnit_id();
			String unitid = userView.getUserOrgId();
			//String unitids[] = unitid.split("`");
			String org = " and (b0110 in (";
			String b0110 = unitid;
			//String b0110 = "";
			/*for(int i=0;i<unitids.length;i++){
				if(unitids[i].length()>0)
				if(unitids[i].substring(0,2).equalsIgnoreCase("UN")){
					b0110=unitids[i].substring(2);
					org += "'"+b0110+"',";
				}
			}*/
			if(!"".equalsIgnoreCase(b0110)){
				//org = org.substring(0,org.length()-1)+")";
				org+="'"+unitid+"')";
			}
			if(!(this.userView.isSuper_admin()&&!this.userView.isBThreeUser())){
				if(!"".equalsIgnoreCase(b0110)){
					sqlText=sqlText+org+" or b0110 is null) "+orderText;
				}else{
					sqlText=sqlText+" and b0110 is null "+orderText;
				}
			}else
				sqlText=sqlText+orderText;
		}else
			sqlText=sqlText+orderText;
		//System.out.println(ss);
		
		String temp = "";
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		String days=sysoth.getValue(Sys_Oth_Parameter.LAWRULE_FILE_DAYS);
		if(days==null||days.length()<=0)
			days="5";
		int days_int=Integer.parseInt(days);
		Date cur_date=DateUtils.getDate(cur_d,"yyyy-MM-dd");
			String images_bir="<img src='/images/new0.gif' border=0>";
			this.frowset = dao.search(sqlText);
			while (this.frowset.next()) {
				/*if(basetype.equalsIgnoreCase("1"))
				{
					if (!userView.isHaveResource(IResourceConstant.LAWRULE, frowset.getString("base_id")))
						continue;
				}
				if(basetype.equalsIgnoreCase("5"))
				{*/
				if(!"false".equals(law_file_priv.trim())){
					if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, frowset.getString("file_id")))
					continue;
				}	
				//}

				LazyDynaBean bean = new LazyDynaBean();
				bean.set("file_id", PubFunc.encrypt(PubFunc.nullToStr(frowset.getString("file_id"))));
				bean.set("base_id", PubFunc.encrypt(PubFunc.nullToStr(frowset.getString("base_id"))));
				bean.set("viewcount", PubFunc.nullToStr(frowset.getString("viewcount")));
				if(frowset.getString("a")!=null)
					bean.set("choice","1");
				else
					bean.set("choice","0");
				if(colums.length()>0){
					for(int i=0;i<fieldlist.size();i++){
						FieldItem item = (FieldItem)fieldlist.get(i);
						String itemid= item.getItemid();
						String itemtype = item.getItemtype();
						if("A".equalsIgnoreCase(itemtype)){
							if("title".equalsIgnoreCase(itemid)){
								temp = PubFunc.nullToStr(this.frowset.getString("title"));
								if (temp.length() > 40) {
									temp = temp.substring(0, 40) + "...";
								}
								if(colums.toString().toLowerCase().indexOf("issue_date")!=-1){
									/**颁布日期为空，则设置为当前系统日期*/
									Date issue_date=frowset.getDate("issue_date")==null?cur_date:frowset.getDate("issue_date");
									int diff=DateUtils.dayDiff(issue_date,cur_date);
									if(diff<=days_int){
										bean.set("title", temp+"&nbsp;&nbsp;"+images_bir);
									}else{
										bean.set("title",temp);
									}
								}else{					
								    bean.set("title",temp);
								}
							}else{
								if("ext".equalsIgnoreCase(itemid)){
									bean.set(itemid,PubFunc.nullToStr(frowset.
											getString(itemid)).trim());
								}else{
									bean.set(itemid,PubFunc.nullToStr(frowset.
											getString(itemid)));
								}
							}
						}else if("D".equalsIgnoreCase(itemtype)){
							bean.set(itemid, PubFunc.FormatDate((frowset
									.getDate(itemid))));
						}else if("N".equalsIgnoreCase(itemtype)){
							if(item.getDecimalwidth()==0)
								if(frowset.getInt(itemid)==0)
									bean.set(itemid, "");
								else
									bean.set(itemid, String.valueOf(frowset.getInt(itemid)));
							else
								if(frowset.getDouble(itemid)==0)
									bean.set(itemid, "");
								else
									bean.set(itemid, String.valueOf(frowset.getDouble(itemid)));
						}
						
					}
				}else{
					temp = PubFunc.nullToStr(this.frowset.getString("name"));
					// vo.setString("name", temp.substring(0, temp.length() > 20 ?
					// 20
					// : temp.length())
					// + "...");
					//vo.setString("name", temp);
					bean.set("name",temp);
					temp = PubFunc.nullToStr(this.frowset.getString("title"));
					if (temp.length() > 40) {
						temp = temp.substring(0, 40) + "...";
					}
					/**颁布日期为空，则设置为当前系统日期*/
					Date issue_date=frowset.getDate("issue_date")==null?cur_date:frowset.getDate("issue_date");
					int diff=DateUtils.dayDiff(issue_date,cur_date);
					if(diff<=days_int){
						//vo.setString("title", temp+"&nbsp;&nbsp;"+images_bir);
						bean.set("title", temp+"&nbsp;&nbsp;"+images_bir);
					}
					else{					
					 //vo.setString("title", temp);
					 bean.set("title",temp);
					}
					/*vo.setString("type", PubFunc.nullToStr(frowset
							.getString("type")));
					vo.setString("content_type", PubFunc.nullToStr(frowset
							.getString("content_type")));
					vo.setString("valid", PubFunc.nullToStr(frowset
							.getString("valid")));
					vo.setString("note_num", PubFunc.nullToStr(frowset
							.getString("note_num")));
					vo.setString("issue_org", PubFunc.nullToStr(frowset
							.getString("issue_org")));
					vo.setString("notes", PubFunc.nullToStr(frowset						.getString("notes")));
					
					vo.setDate("issue_date", PubFunc.FormatDate((frowset
							.getDate("issue_date"))));
					vo.setDate("implement_date", PubFunc.FormatDate((frowset
							.getDate("implement_date"))));
					vo.setDate("valid_date", PubFunc.FormatDate((frowset
							.getDate("valid_date"))));
					vo
							.setString("ext", PubFunc.nullToStr(frowset
									.getString("ext")));
					vo.setString("base_id", PubFunc.nullToStr(frowset
							.getString("base_id")));
					vo.setString("viewcount", PubFunc.nullToStr(frowset
							.getString("viewcount")));*/
					
					bean.set("type", PubFunc.nullToStr(frowset
							.getString("type")));
					bean.set("content_type", PubFunc.nullToStr(frowset
							.getString("content_type")));
					bean.set("valid", PubFunc.nullToStr(frowset
							.getString("valid")));
					bean.set("note_num", PubFunc.nullToStr(frowset
							.getString("note_num")));
					bean.set("issue_org", PubFunc.nullToStr(frowset
							.getString("issue_org")));
					bean.set("notes", PubFunc.nullToStr(frowset.getString("notes")));
					
					bean.set("issue_date", PubFunc.FormatDate((frowset
							.getDate("issue_date"))));
					bean.set("implement_date", PubFunc.FormatDate((frowset
							.getDate("implement_date"))));
					bean.set("valid_date", PubFunc.FormatDate((frowset
							.getDate("valid_date"))));
					bean.set("ext", PubFunc.nullToStr(frowset
									.getString("ext")).toLowerCase().trim());
					bean.set("originalext",PubFunc.nullToStr(frowset
							.getString("originalext")).toLowerCase().trim());
				}
				
				/*LazyDynaBean bean = new LazyDynaBean();
				bean.set("file_id", PubFunc.nullToStr(frowset
						.getString("file_id")));
				temp = PubFunc.nullToStr(this.frowset.getString("name"));
				// vo.setString("name", temp.substring(0, temp.length() > 20 ?
				// 20
				// : temp.length())
				// + "...");
				bean.set("name",temp);
				temp = PubFunc.nullToStr(this.frowset.getString("title"));
				if (temp.length() > 40) {
					temp = temp.substring(0, 40) + "...";
				}
				Date issue_date=frowset.getDate("issue_date");
				if(issue_date==null)
					issue_date=DateUtils.getDate("2001-01-01","yyyy-MM-dd");
				int diff=DateUtils.dayDiff(issue_date,cur_date);
				if(diff<=days_int){
					bean.set("title", temp+"&nbsp;&nbsp;"+images_bir);
				}
				else{					
				 bean.set("title",temp);
				}
				
				bean.set("type", PubFunc.nullToStr(frowset
						.getString("type")));
				bean.set("content_type", PubFunc.nullToStr(frowset
						.getString("content_type")));
				bean.set("valid", PubFunc.nullToStr(frowset
						.getString("valid")));
				bean.set("note_num", PubFunc.nullToStr(frowset
						.getString("note_num")));
				bean.set("issue_org", PubFunc.nullToStr(frowset
						.getString("issue_org")));
				bean.set("notes", PubFunc.nullToStr(frowset.getString("notes")));
				
				bean.set("issue_date", PubFunc.FormatDate((frowset
						.getDate("issue_date"))));
				bean.set("implement_date", PubFunc.FormatDate((frowset
						.getDate("implement_date"))));
				bean.set("valid_date", PubFunc.FormatDate((frowset
						.getDate("valid_date"))));
				bean.set("ext", PubFunc.nullToStr(frowset
								.getString("ext")).toLowerCase().trim());
				bean.set("base_id", PubFunc.nullToStr(frowset
						.getString("base_id")));
				bean.set("viewcount", PubFunc.nullToStr(frowset
						.getString("viewcount")));
				if(frowset.getString("a")!=null)
					bean.set("choice","1");
				else
					bean.set("choice","0");
				bean.set("originalext",PubFunc.nullToStr(frowset.getString("originalext")).toLowerCase().trim());*/
				list.add(bean);				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			getFormHM().put("filelist", list);
			//getFormHM().put("note_nums","");
			//getFormHM().put("contenttype","");
			//getFormHM().put("issuedate","");
			//getFormHM().put("username","");
		}
	}
	private ArrayList getBaseids(String basetype,ContentDAO dao) throws SQLException{
		ArrayList baseids = new ArrayList();
		String sql ="select base_id from law_base_struct where basetype=" + basetype;
		this.frecset = dao.search(sql);
		while(this.frecset.next()){
			String base_id = this.frecset.getString("base_id");
			if("1".equalsIgnoreCase(basetype))
			{
				if (!userView.isHaveResource(IResourceConstant.LAWRULE, base_id))
					continue;
			}
			if("5".equalsIgnoreCase(basetype))
			{
				if (!userView.isHaveResource(IResourceConstant.DOCTYPE, base_id))
					continue;
			}
			if("4".equalsIgnoreCase(basetype))
			{
				if (!userView.isHaveResource(IResourceConstant.KNOWTYPE, base_id))
					continue;
			}
			baseids.add(base_id);
		}
		return baseids;
	}
	
	private void getFieldStr(String a_base_id,ContentDAO dao,StringBuffer sb) throws SQLException{
		String sql = "select up_base_id,field_str from law_base_struct where base_id='"+a_base_id+"'";
		this.frecset=dao.search(sql);
		String base_id="";
		String field_str = "";
		if(this.frecset.next()){
			base_id=this.frecset.getString("up_base_id");
			field_str=this.frecset.getString("field_str");
		}
		if(field_str!=null&&field_str.length()>0){
			ArchiveXml xml = new ArchiveXml();
			String upstr = xml.getElement("item", field_str);
			String downstr = xml.getElement("listing", field_str);
			sb.append(upstr+"``"+downstr);
		}else{
			if(!base_id.equals(a_base_id)){
				getFieldStr(base_id,dao,sb);
			}
		}
	}
}
