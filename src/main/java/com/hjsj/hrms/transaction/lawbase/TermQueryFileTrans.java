package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TermQueryFileTrans extends IBusiness {

	public TermQueryFileTrans() {
		super();
	}

	public void execute() throws GeneralException {
		String basetype = (String) getFormHM().get("basetype");
		String orgId = userView.getUserOrgId();
		ArrayList myList = new ArrayList();
		StringBuffer sqlText = new StringBuffer("");
		
		String name = ((String) getFormHM().get("name"))==null?"":((String) getFormHM().get("name")).trim();
		name = name.replaceAll("\\[", "%").replaceAll("\\]", "%");
		
		String title = ((String) getFormHM().get("title"))==null?"":((String) getFormHM().get("title")).trim();
		title = title.replaceAll("\\[", "%").replaceAll("\\]", "%");
		
		String type = ((String) getFormHM().get("type"))==null?"":((String) getFormHM().get("type")).trim();
		String content_type = ((String) getFormHM().get("content_type"))==null?"":((String) getFormHM().get("content_type")).trim();
		String valid = ((String) getFormHM().get("valid"))==null?"":((String) getFormHM().get("valid")).trim();
		
		String note_num = ((String) getFormHM().get("note_num"))==null?"":((String) getFormHM().get("note_num")).trim();
		note_num = note_num.replaceAll("\\[", "%").replaceAll("\\]", "%");
		
		String issue_org = ((String) getFormHM().get("issue_org"))==null?"":((String) getFormHM().get("issue_org")).trim();
		String notes = ((String) getFormHM().get("notes"))==null?"":((String) getFormHM().get("notes")).trim();
		
		name = PubFunc.getStr(name);
		title = PubFunc.getStr(title);
		type = PubFunc.getStr(type);
		content_type = PubFunc.getStr(content_type);
		valid = PubFunc.getStr(valid);
		note_num = PubFunc.getStr(note_num);
		issue_org = PubFunc.getStr(issue_org);
		notes = PubFunc.getStr(notes);
		
		String issue_date_start = ((String) getFormHM().get("issue_date_start"));
		if (issue_date_start != null) {
			issue_date_start.trim();
		}
		
		String issue_date_end = ((String) getFormHM().get("issue_date_end"));
		if (issue_date_end != null) {
			issue_date_end.trim();
		}
		
		String implement_date_start = "";
		String implement_date_end="";
		String valid_date_start ="";
		String valid_date_end ="";
		if(!"5".equalsIgnoreCase(basetype)){
			implement_date_start = ((String) getFormHM().get(
					"implement_date_start"));
			implement_date_start=implement_date_start==null?"":implement_date_start.trim();
			
			implement_date_end = ((String) getFormHM().get(
					"implement_date_end"));
			implement_date_end=implement_date_end==null?"":implement_date_end.trim();
	
			valid_date_start = ((String) getFormHM().get("valid_date_start"));
			valid_date_start=valid_date_start==null?"":valid_date_start.trim();
			
			valid_date_end = ((String) getFormHM().get("valid_date_end"));
			valid_date_end=valid_date_end==null?"":valid_date_end.trim();
		}
		
		if (!CommonBusiness.judgeNull(name)) {
			sqlText.append(" and law_base_file.name like '%" + name + "%'");
		}
		
		if (!CommonBusiness.judgeNull(title)) {
			sqlText.append(" and law_base_file.title like '%" + title + "%'");
		}
		
		if (!CommonBusiness.judgeNull(type)) {
			sqlText.append(" and type = '" + type + "'");
		}
		
		if (!CommonBusiness.judgeNull(content_type)) {
			sqlText.append(" and content_type = '" + content_type + "'");
		}
		
		if (!CommonBusiness.judgeNull(valid)) {
			sqlText.append(" and valid = '" + valid + "'");
		}
		
		if (!CommonBusiness.judgeNull(note_num)) {
			sqlText.append(" and note_num like  '%" + note_num + "%'");
		}
		if (!CommonBusiness.judgeNull(issue_org)) {
			sqlText.append(" and issue_org = '" + issue_org + "'");
		}
		
		if (!CommonBusiness.judgeNull(notes)) {
			sqlText.append(" and notes = '" + notes + "'");
		}
		
		if (!CommonBusiness.judgeNull(issue_date_start)
				&& CommonBusiness.judgeNull(issue_date_end)) {
			sqlText.append(" and issue_date >= " + Sql_switcher.dateValue(issue_date_start));
		}
		
		if (CommonBusiness.judgeNull(issue_date_start)
				&& !CommonBusiness.judgeNull(issue_date_end)) {
			sqlText.append(" and issue_date <= " + Sql_switcher.dateValue(issue_date_end));
		}
		
		if (!CommonBusiness.judgeNull(issue_date_start)
				&& !CommonBusiness.judgeNull(issue_date_end)) {
			sqlText.append(" and issue_date between "
					+ Sql_switcher.dateValue(issue_date_start) + " and "
					+ Sql_switcher.dateValue(issue_date_end));
		}
		
		if (!CommonBusiness.judgeNull(implement_date_start)
				&& CommonBusiness.judgeNull(implement_date_end)) {
			sqlText.append(" and implement_date >= " + Sql_switcher.dateValue(implement_date_start));
		}
		
		if (CommonBusiness.judgeNull(implement_date_start)
				&& !CommonBusiness.judgeNull(implement_date_end)) {
			sqlText.append(" and implement_date <= " + Sql_switcher.dateValue(implement_date_end));
		}
		
		if (!CommonBusiness.judgeNull(implement_date_start)
				&& !CommonBusiness.judgeNull(implement_date_end)) {
			sqlText.append(" and implement_date between "
					+ Sql_switcher.dateValue(implement_date_start) + " and "
					+ Sql_switcher.dateValue(implement_date_end));
		}
		
		if (!CommonBusiness.judgeNull(valid_date_start)
				&& CommonBusiness.judgeNull(valid_date_end)) {
			sqlText.append(" and valid_date >= " + Sql_switcher.dateValue(valid_date_start));
		}else if (CommonBusiness.judgeNull(valid_date_start)
				&& !CommonBusiness.judgeNull(valid_date_end)) {
			sqlText.append(" and valid_date <= " + Sql_switcher.dateValue(valid_date_end));
		}else if (!CommonBusiness.judgeNull(valid_date_start)
				&& !CommonBusiness.judgeNull(valid_date_end)) {
			sqlText.append(" and valid_date between "
					+ Sql_switcher.dateValue(valid_date_start) + " and "
					+ Sql_switcher.dateValue(valid_date_end));
		}else {
			//【60533】V77：文档管理：制度浏览/特征检索，可以检索到已经失效的制度，见附件
			String cur_d=PubFunc.getStringDate("yyyy-MM-dd");
			sqlText.append(" and (valid_date>="+Sql_switcher.dateValue(cur_d)+" or  valid_date is null )");
		}
		
		String itemid = (String)this.getFormHM().get("itemid");
		String itemdesc = (String)this.getFormHM().get("itemdesc");
		String itemtype = (String)this.getFormHM().get("itemtype");
		String valueStr = (String)this.getFormHM().get("itemvalue");
		String itemcodeid = (String)this.getFormHM().get("itemCodeid");
		if (itemid != null && !"".equals(itemid) && valueStr.length() > 0) {
			String[] itemidArr = itemid.split("`");
			String[] itemtypeArr = itemtype.split("`");
			String[] valueArr = valueStr.split("`");
			String[] itemCodeidArr = itemcodeid.split("`");
			
			if (valueArr.length > 0) {
				for (int i = 0; i < itemtypeArr.length; i++) {
					String typeValue = itemtypeArr[i];
					String itemidValue = itemidArr[i];
					String itemValue = valueArr[i];
					String itemCodesetid = itemCodeidArr[i];
					
					if("A".equalsIgnoreCase(typeValue) || "M".equalsIgnoreCase(typeValue)){
						if (!CommonBusiness.judgeNull(itemValue)) {
							if(!"0".equals(itemCodesetid)){
								sqlText.append(" and " + itemidValue + " in (");
								sqlText.append("select codeitemid from codeitem");
								sqlText.append(" where codesetid = '" + itemCodesetid + "'");
								sqlText.append(" and codeitemdesc like '%" + itemValue + "%')");
							}else {
								sqlText.append(" and " + itemidValue + " like '%" + itemValue + "%'");
							}
						}
					}else if("D".equalsIgnoreCase(typeValue)){
						String start = itemValue.substring(0, itemValue.indexOf("＋"));
						String end = itemValue.substring(itemValue.indexOf("＋")+1, itemValue.length());
						if (!CommonBusiness.judgeNull(start)) {
							sqlText.append(" and " + itemidValue + " >= " + Sql_switcher.dateValue(start));
						}
						
						if (!CommonBusiness.judgeNull(end)) {
							sqlText.append(" and " + itemidValue + " <= " + Sql_switcher.dateValue(end));
						}
					}else if("N".equalsIgnoreCase(typeValue)){
						if (!CommonBusiness.judgeNull(itemValue)) {
							sqlText.append(" and " + itemidValue + " = " + itemValue);
						}
					}
				}
			}
		}
		
		try {
			String orgTerm = " basetype= " + basetype + "";
			String law_file_priv = SystemConfig.getPropertyValue("law_file_priv");
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
    					orgTerm=orgTerm+ " and (dir = '-1' or dir in (" + orgsrt + ") or dir is null )";	
    				}
    			}
			}
			
			CommonBusiness comnbus = new CommonBusiness(getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			
			String a_base_id=(String) this.getFormHM().get("base_id");
			a_base_id = PubFunc.decrypt(a_base_id);
			
			String sql = comnbus.findLawbaseFile(a_base_id, orgId, orgTerm,basetype,this.userView);
			ArrayList fieldlist = new ArrayList();
			StringBuffer colums = new StringBuffer();
			String field_str_item="";
			StringBuffer sb = new StringBuffer();
				String field_str = "";
				
				if (a_base_id == null || "".equals(a_base_id)
						|| "null".equals(a_base_id.trim())
						|| "root".equals(a_base_id.trim())) {
					String sql1 = "select * from law_base_struct where field_str is not null and basetype="+basetype;
					this.frecset = dao.search(sql1);
					if(this.frecset.next()){
						sql1 = "select base_id,field_str from law_base_struct where base_id=up_base_id and basetype="+basetype+" order by displayorder";
						this.frecset = dao.search(sql1);
						if(this.frecset.next()){
							a_base_id=this.frecset.getString("base_id");
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
						itemid = tmps[0];
						itemdesc=tmps[1];
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
							itemid = tmps[0];
							itemdesc=tmps[1];
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
			
			if("5".equalsIgnoreCase(this.getFormHM().get("basetype").toString())&&!"false".equals(law_file_priv.trim())){
				String unitid = userView.getUserOrgId();
				String org = " and (b0110 in (";
				String b0110 = unitid;
				
				if(!"".equalsIgnoreCase(b0110)){
					org +="'"+b0110+"')"; 
				}
				
				if(!(this.userView.isSuper_admin()&&!userView.isBThreeUser())){
					if(!"".equalsIgnoreCase(b0110)){
						sqlText.append(org+" or b0110 is null) ");
					}else{
						sqlText.append(" and b0110 is null ");
					}
				}
			}
			
			HashMap extMap = new HashMap();
			extMap = ArchiveXml.getFileExtMap(dao);
			String orderText=" order by DisplayOrder , fileorder desc";
			this.frowset = dao.search(sql+" and status=1" + sqlText.toString()+orderText);
			String cur_d=PubFunc.getStringDate("yyyy-MM-dd");
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
			String days=sysoth.getValue(Sys_Oth_Parameter.LAWRULE_FILE_DAYS);
			if(days==null||days.length()<=0)
				days="5";
			
			int days_int=Integer.parseInt(days);
			Date cur_date=DateUtils.getDate(cur_d,"yyyy-MM-dd");
			String images_bir="<img src='/images/new0.gif' border=0>";
			while (frowset.next()) {
				if(!"false".equals(law_file_priv.trim())){
					if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, frowset.getString("file_id")))
					continue;
				}
				
				RecordVo vo = new RecordVo("law_base_file");
				vo.setString("file_id", frowset.getString("file_id"));
				if(fieldlist.size()==0){
				String temp = frowset.getString("name");
				temp = temp == null ? "" : temp;
				if (temp.length() > 40) {
					temp = temp.substring(0, 40) + "...";
				}
				vo.setString("name", temp);
				temp = frowset.getString("title");
				temp = temp == null ? "" : temp;
				if (temp.length() > 40) {
					temp = temp.substring(0, 40) + "...";
				}
				
				/**颁布日期为空，则设置为当前系统日期*/
				Date issue_date=frowset.getDate("issue_date")==null?cur_date:frowset.getDate("issue_date");
				int diff=DateUtils.dayDiff(issue_date,cur_date);
				if(diff<=days_int){
					vo.setString("title", temp+"&nbsp;&nbsp;"+images_bir);
				}else{
					vo.setString("title",temp);
				}
				
				vo.setString("type", frowset.getString("type"));
				vo.setString("content_type", frowset.getString("content_type"));
				vo.setString("valid", frowset.getString("valid"));
				vo.setString("note_num", frowset.getString("note_num"));
				vo.setString("issue_org", frowset.getString("issue_org"));
				vo.setString("notes", frowset.getString("notes"));
				vo.setString("ext",frowset.getString("ext"));
				vo.setString("originalext",frowset.getString("originalext"));
				String s1 = PubFunc.FormatDate(frowset.getDate("issue_date"), "yyyy-MM-dd");
				vo.setDate("issue_date", s1);
				String s = PubFunc.FormatDate(frowset.getDate("valid_date"), "yyyy-MM-dd");
				vo.setDate("valid_date", s);
				vo.setDate("implement_date", PubFunc.FormatDate(frowset.getDate("implement_date"), "yyyy-MM-dd"));
				
				String existExt = (String) extMap.get(frowset.getString("file_id"));
				existExt = existExt == null ? "" : "1";
				vo.setString("digest",existExt);
				vo.setString("viewcount", frowset.getString("viewcount"));
				}else{
						for(int i=0;i<fieldlist.size();i++){
							FieldItem item = (FieldItem)fieldlist.get(i);
							itemid= item.getItemid();
							itemtype = item.getItemtype();
							if("A".equalsIgnoreCase(itemtype)){
								if("title".equalsIgnoreCase(itemid)){
									String temp = PubFunc.nullToStr(this.frowset.getString("title"));
									if (temp.length() > 40) {
										temp = temp.substring(0, 40) + "...";
									}
									if(colums.toString().toLowerCase().indexOf("issue_date")!=-1){
										/**颁布日期为空，则设置为当前系统日期*/
										Date issue_date=frowset.getDate("issue_date")==null?cur_date:frowset.getDate("issue_date");
										int diff=DateUtils.dayDiff(issue_date,cur_date);
										if(diff<=days_int){
											vo.setString("title", temp+"&nbsp;&nbsp;"+images_bir);
										}else{
											vo.setString("title",temp);
										}
									}else{					
										vo.setString("title",temp);
									}
								}else{
									if("ext".equalsIgnoreCase(itemid)){
										vo.setString(itemid,PubFunc.nullToStr(frowset.
												getString(itemid)).trim());
									}else{
										vo.setString(itemid,PubFunc.nullToStr(frowset.
												getString(itemid)));
									}
								}
							}else if("D".equalsIgnoreCase(itemtype)){
								vo.setDate(itemid, PubFunc.FormatDate((frowset
										.getDate(itemid))));
							}else if("N".equalsIgnoreCase(itemtype)){
								if(item.getDecimalwidth()==0){
									if(frowset.getInt(itemid)!=0)
										vo.setInt(itemid, frowset.getInt(itemid));
								}else{
									if(frowset.getDouble(itemid)!=0)
										vo.setDouble(itemid, frowset.getDouble(itemid));
								}
							}
						}
						String existExt = (String) extMap.get(frowset.getString("file_id"));
						existExt = existExt == null ? "" : "1";
						vo.setString("digest",existExt);
				}
				myList.add(vo);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}

		getFormHM().put("myList", myList);
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
