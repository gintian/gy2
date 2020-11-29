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
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 全文检索
 * <p>Title:GlobalSearchTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Nov 13, 2006 10:40:11 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class GlobalSearchTrans extends IBusiness {

	public GlobalSearchTrans() {
		super();
	}

	public void execute() throws GeneralException {
		ResultSet rs = null;
		// 当前选择目录
		String base_id = (String) getFormHM().get("base_id");
		base_id = PubFunc.decrypt(base_id);
		
		// 当前用户所在机构ID
		String orgId = userView.getUserOrgId();
		if (orgId == null || "".equals(orgId))
			orgId = "-1";
		// 索引文件所在路径
		String indexPath =  LawDirectory.getLawbaseDir();
		ArrayList myList = new ArrayList();
		String term = (String) getFormHM().get("term");		
		String basetype = (String)this.getFormHM().get("basetype");
		try {
			if (IndexReader.indexExists(indexPath)) {
				Searcher searcher = new IndexSearcher(indexPath);
				// 如果条件为空的话，查询会出现异常。
				if ("".equals(term.trim()))
					return;
				
				// 查询解析器：使用和索引同样的语言分析器
				Query query = QueryParser.parse(term, "body", new ChineseAnalyzer());
				// 搜索结果使用Hits存储
				Hits hits = searcher.search(query);
				if (hits.length() > 0) {
					CommonBusiness comnbus = new CommonBusiness(getFrameconn());
					String catalogTerm = "basetype=" + this.getFormHM().get("basetype") ;//+ " and (dir = '-1' or dir = '" + orgId+ "' or dir is null)";
					String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
					if(!"false".equals(law_file_priv.trim())){
					if(!this.userView.isSuper_admin())
					{
						if(orgId==null||orgId.length()<=0)
						{
							catalogTerm=catalogTerm+ " and (dir = '' or dir = '-1' or dir is null)";
						}else
						{
							LawDirectory lawDirectory=new LawDirectory();
							String orgsrt=lawDirectory.getOrgStrs(orgId,"UN",this.getFrameconn());
							catalogTerm=catalogTerm+ " and (dir = '' or dir = '-1' or dir in (" + orgsrt + ") or dir is null )";
						}
					}
			    }
					
					ContentDAO dao = new ContentDAO(getFrameconn());
					StringBuffer sb = new StringBuffer(	"select * from law_base_file ");
					sb.append(" where file_id in (");
					String childSql = comnbus.findLawbaseFileId(base_id, orgId,	catalogTerm);
					sb.append(childSql);
					sb.append(")");
					sb.append(" and file_id in (");
					for (int i = 0; i < hits.length(); i++) {
						if (i != 0) {
							sb.append(",");
						}
						sb.append("'" + hits.doc(i).get("id") + "','" + hits.doc(i).get("ide") + "'");
					}
					sb.append(")");					
					sb.append(" and valid='1' ");
					String cur_d = PubFunc.getStringDate("yyyy-MM-dd");
					//sb.append(" and implement_date<="+Sql_switcher.dateValue(cur_d));
					sb.append(" and (valid_date>="+Sql_switcher.dateValue(cur_d)+" or  valid_date is null )");
					if("5".equalsIgnoreCase(this.getFormHM().get("basetype").toString())&&!"false".equals(law_file_priv.trim())){
						//String unitid = userView.getUnit_id();
						String unitid = userView.getUserOrgId();
						//String unitids[] = unitid.split("`");
						String org = " and (b0110 in (";
						//String b0110 = "";
						String b0110 = unitid;
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
						
						if(!this.userView.isSuper_admin()){
							if(!"".equalsIgnoreCase(b0110)){
								sb.append(org+" or b0110 is null) ");
							}else{
								sb.append(" and b0110 is null ");
							}
						}
					}
					
					HashMap extMap = ArchiveXml.getFileExtMap(dao);
					this.frowset = dao.search(sb.toString());
					Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
					String days=sysoth.getValue(Sys_Oth_Parameter.LAWRULE_FILE_DAYS);
					if(days==null||days.length()<=0)
						days="5";
					
					int days_int=Integer.parseInt(days);
					Date cur_date=DateUtils.getDate(cur_d,"yyyy-MM-dd");
					String images_bir="<img src='/images/new0.gif' border=0>";
					String colums = "";
					ArrayList fieldlist = this.getFieldList(base_id,basetype,colums);
					while (frowset.next()) {
						/*if(basetype.equalsIgnoreCase("1"))
						{
							if (!userView.isHaveResource(IResourceConstant.LAWRULE, frowset.getString("base_id")))
								continue;
						}
						if(basetype.equalsIgnoreCase("5"))
						{*/
						if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, frowset.getString("file_id")))
						    continue;
						
						RecordVo vo = new RecordVo("law_base_file");
						vo.setString("file_id", frowset.getString("file_id"));
						if(fieldlist.size()==0){
							String name = frowset.getString("name");
							name = name == null ? "" : name;
							vo.setString("name", name.length()<=40 ? name : name.substring(0,40)+"...");
							
							String title = frowset.getString("title");
							title = title == null ? "" : title;
							vo.setString("title", title.length()<=40 ? title : title.substring(0,40)+"...");
							
							vo.setString("type", frowset.getString("type"));
							vo.setString("content_type", frowset.getString("content_type"));
							vo.setString("valid", frowset.getString("valid"));
							vo.setString("note_num", frowset.getString("note_num"));
							vo.setString("issue_org", frowset.getString("issue_org"));
							vo.setString("notes", frowset.getString("notes"));
							vo.setDate("issue_date", PubFunc.FormatDate(frowset.getDate("issue_date"), "yyyy-MM-dd"));
							vo.setDate("valid_date", PubFunc.FormatDate(frowset.getDate("valid_date"), "yyyy-MM-dd"));
							vo.setDate("implement_date", PubFunc.FormatDate(frowset.getDate("implement_date"), "yyyy-MM-dd"));
							vo.setString("originalext", PubFunc.nullToStr(frowset.getString("originalext")).toLowerCase().trim());
							vo.setString("viewcount", frowset.getString("viewcount"));
							String existExt = (String) extMap.get(frowset.getString("file_id"));
							existExt = existExt == null ? "" : "1";
							vo.setString("digest",existExt);
						} else {
							for(int i=0;i<fieldlist.size();i++){
								FieldItem item = (FieldItem)fieldlist.get(i);
								String itemid= item.getItemid();
								String itemtype = item.getItemtype();
								if("A".equalsIgnoreCase(itemtype)){
									if("title".equalsIgnoreCase(itemid)){
										String temp = PubFunc.nullToStr(this.frowset.getString("title"));
										if (temp.length() > 40) {
											temp = temp.substring(0, 40) + "...";
										}
										if(colums.toLowerCase().indexOf("issue_date")!=-1){
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
											vo.setString(itemid,PubFunc.nullToStr(frowset.getString(itemid)).trim());
										}else{
											vo.setString(itemid,PubFunc.nullToStr(frowset.getString(itemid)));
										}
									}
								}else if("D".equalsIgnoreCase(itemtype)){
									vo.setDate(itemid, PubFunc.FormatDate((frowset.getDate(itemid))));
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
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			getFormHM().put("myList", myList);
			getFormHM().put("term", term);
		}
	}
	
	private ArrayList getFieldList(String a_base_id,String basetype,String colums){
		ArrayList fieldlist = new ArrayList();
		ContentDAO dao = new ContentDAO(frameconn);
		StringBuffer sb = new StringBuffer();
		String field_str = "";
		
		if (a_base_id == null || "".equals(a_base_id)
				|| "null".equals(a_base_id.trim())
				|| "root".equals(a_base_id.trim())) {
			String sql1 = "select * from law_base_struct where field_str is not null and basetype="+basetype;
			try {
				this.frecset = dao.search(sql1);
				if(this.frecset.next()){
					sql1 = "select base_id,field_str from law_base_struct where base_id=up_base_id and basetype="+basetype+" order by displayorder";
					this.frecset = dao.search(sql1);
					if(this.frecset.next()){
						a_base_id=this.frecset.getString("base_id");
						field_str = this.frecset.getString("field_str");
						if(field_str!=null&&field_str.length()>0){
							ArchiveXml xml = new ArchiveXml();
							String downstr = xml.getElement("listing", field_str);
							field_str=downstr;
						}else{
							field_str="";
						}
					}	
				}else{
					field_str="";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			try {
				getFieldStr(a_base_id,dao,sb);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(sb.length()>2){
				String[] temps= sb.toString().split("``");
				if(temps.length>1){
					field_str=temps[1];
				}else if(temps.length>0){
					field_str="";
				}else{
					field_str="";
				}
			}else{
				field_str="";
			}
		}
		
		String [] fields_str= field_str.split(",");
		String itemid = "";
		String itemdesc = "";
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
							colums = colums + (",law_base_file."+itemid+" "+itemid);
						}
						item = (FieldItem)item.cloneItem();
						item.setItemdesc(itemdesc);
						fieldlist.add(item);
					}
				}
			}
		}
		return fieldlist;
	}
	
	private void getFieldStr(String a_base_id,ContentDAO dao,StringBuffer sb) throws SQLException{
		String base_id="";
		String field_str = "";
		
		String sql = "select up_base_id,field_str from law_base_struct where base_id='"+a_base_id+"'";
		this.frecset = dao.search(sql);
		if(this.frecset.next()){
			base_id = this.frecset.getString("up_base_id");
			field_str = this.frecset.getString("field_str");
		}
		
		if(field_str != null && field_str.length() > 0){
			ArchiveXml xml = new ArchiveXml();
			String upstr = xml.getElement("item", field_str);
			String downstr = xml.getElement("listing", field_str);
			sb.append(upstr + "``" + downstr);
		}else{
			if(!base_id.equals(a_base_id)){
				getFieldStr(base_id,dao,sb);
			}
		}
	}
}
