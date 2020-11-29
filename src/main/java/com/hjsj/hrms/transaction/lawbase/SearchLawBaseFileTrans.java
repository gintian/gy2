package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.param.DocumentParamXML;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 规章制度文件列表
 * 
 * @version 1.0
 * 
 */
public class SearchLawBaseFileTrans extends IBusiness {

	public SearchLawBaseFileTrans() {
		super();
	}

	public void execute() throws GeneralException {
	  try{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ArrayList volist = new ArrayList();
		String status=(String)hm.get("status");
		String a_base_id = "";
		String basetype = (String) getFormHM().get("basetype");
		if (hm.get("a_base_id") != null) {
			a_base_id = (String) hm.get("a_base_id");
		}
		if (getFormHM().get("a_base_id") != null) {
			a_base_id = (String) getFormHM().get("a_base_id");
		}
		String[] str = a_base_id.split("～");
		if(str.length>1)
		{			
			a_base_id = PubFunc.decrypt(SafeCode.decode(a_base_id));
		}
		ArrayList fieldlist = new ArrayList();
		DocumentParamXML documentparamXML = new DocumentParamXML(this.getFrameconn());
		String fileitem = documentparamXML.getValue(DocumentParamXML.FILESET,"fielditem");
		if(fileitem==null|| "".equalsIgnoreCase(fileitem))
			this.getFormHM().put("sign","0");
		else{
			FieldItem fi = (FieldItem)DataDictionary.getFieldItem(fileitem.toUpperCase());
			if(fi!=null&& "1".equals(fi.getUseflag())){
				this.getFormHM().put("sign","1");
			}else{
				this.getFormHM().put("sign","0");
			}
		}
		String flg = "true";// 用来标识当前结点是否有子结点
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String orgId = userView.getUserOrgId();// 用来保存机构ID
		CommonBusiness comnbus = new CommonBusiness(getFrameconn());
		String order_by = " order by displayorder,fileorder desc";//law_base_file.base_id
		String order_name = (String)this.getFormHM().get("order_name");
		String order_type = (String)this.getFormHM().get("order_type");
		if(StringUtils.isNotEmpty(order_name)&&StringUtils.isNotEmpty(order_type))
			order_by = " order by "+order_name+" "+order_type;
		String field_str_item="";
		StringBuffer colums = new StringBuffer();
		try {
			String orgTerm = "basetype=" + basetype;
					/*+ " and (dir = '' or dir = '-1' or dir = '" + orgId
					+ "' or dir is null )";*/
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
			
			String field_str = "";
			
			if (a_base_id == null || "".equals(a_base_id) || "null".equals(a_base_id.trim()) || "root".equals(a_base_id.trim())) {
				String sql = "select * from law_base_struct where field_str is not null and "+orgTerm;
				if(Sql_switcher.searchDbServer() == Constant.MSSQL)
					sql = sql + " AND convert(varchar(200),field_str)<>''";
				this.frecset = dao.search(sql);
				if(this.frecset.next()){
					sql = "select base_id,field_str from law_base_struct where base_id=up_base_id and "+orgTerm+" order by displayorder";
					this.frecset = dao.search(sql);
					if(this.frecset.next()){
						//a_base_id=this.frecset.getString("base_id");
						field_str = this.frecset.getString("field_str");
						if(field_str!=null&&field_str.length()>0){
							ArchiveXml xml = new ArchiveXml();
							field_str_item = xml.getElement("item", field_str);
							String downstr = xml.getElement("listing", field_str);
							//以下对field_str_item和downstr进行过滤
							String upstrNew = "";
							String downstrNew = "";
							String str1 = "";
							String str2 = "";
							HashMap itemMap = new HashMap();
							ArrayList itemList = DataDictionary.getFieldList("LAW_BASE_FILE",Constant.USED_FIELD_SET);
							for(int i=0;i<itemList.size();i++){
								FieldItem item = (FieldItem)itemList.get(i);
								String itemid = item.getItemid();
								itemMap.put(itemid, "1");
							}
							String[] temp1 = field_str_item.split(",");
							String[] temp2 = downstr.split(",");
							for(int j=0;j<temp1.length;j++){
								if("extFile".equalsIgnoreCase(temp1[j].split("`")[0])){
									str1 = temp1[j].split("`")[0]+"`"+temp1[j].split("`")[1];
									continue;
								}
								if(itemMap.get(temp1[j].split("`")[0])!=null){
									upstrNew += temp1[j].split("`")[0]+"`"+temp1[j].split("`")[1]+",";
								}
							}
							for(int k=0;k<temp2.length;k++){
								if("extFile".equalsIgnoreCase(temp2[k].split("`")[0])){
									str2 = temp2[k].split("`")[0]+"`"+temp2[k].split("`")[1];
									continue;
								}
								if(itemMap.get(temp2[k].split("`")[0])!=null){
									downstrNew += temp2[k].split("`")[0]+"`"+temp2[k].split("`")[1]+",";
								}
							}
							if(!"".equals(str1))
								upstrNew+=str1+",";
							if(!"".equals(str2))
								downstrNew+=str2+",";
							field_str_item = upstrNew;
							downstr = downstrNew;
							
							field_str=downstr;
						}else{
							field_str="";
						}
					}	
				}else{
					field_str="";
				}
			}else{
				StringBuffer sb = new StringBuffer();
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
			
			String [] fields_str= field_str.toLowerCase().split(",");
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
				fields_str= field_str_item.toLowerCase().split(",");
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
			//com.hrms.hjsj.sys.DataDictionary.getFieldList("law_base_file", 1);
			//System.out.println(comnbus.findLawbaseFile(a_base_id, orgId, orgTerm));
			StringBuffer sqlBuffer = null;//add 2008-08-12
			TreeHandle treehandle = new TreeHandle(this.frameconn);
			if(colums.length()>0){
				sqlBuffer = new StringBuffer(
						"select file_id,law_base_file.base_id"+colums+ ",keywords,displayorder from law_base_file left join law_base_struct on law_base_file.base_id = law_base_struct.base_id ");
				
			}else{
				sqlBuffer = new StringBuffer(
					"select file_id,law_base_file.base_id,content_type,law_base_file.name,title,type,valid,note_num,"
							+ "issue_org,notes,issue_date,implement_date,valid_date,ext, viewcount,originalext, b0110,keywords,displayorder"
							+ " from law_base_file left join law_base_struct on law_base_file.base_id = law_base_struct.base_id ");
			}
			
			if (a_base_id == null || "".equals(a_base_id)
					|| "null".equals(a_base_id.trim())
					|| "root".equals(a_base_id.trim())) {
				if((this.userView.isSuper_admin()&&!this.userView.isBThreeUser())||!"false".equals(law_file_priv.trim())){
				sqlBuffer.append(" where (law_base_file.base_id in (select base_id from law_base_struct where "
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
					sqlBuffer.append(")");
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
			
			if("5".equalsIgnoreCase(basetype)&&!"false".equals(law_file_priv.trim())){
				//String unitid = userView.getUnit_id();
				String unitid = userView.getUserOrgId();
				//String unitids[] = unitid.split("`");
				String org = " and (b0110 in (";
				String b0110 = "";
				/*for(int i=0;i<unitids.length;i++){
					if(unitids[i].length()>0)
					if(unitids[i].substring(0,2).equalsIgnoreCase("UN")){
						b0110=unitids[i].substring(2);
						org += "'"+b0110+"',";
					}
				}*/
				b0110=unitid;
				if(!"".equalsIgnoreCase(b0110)){
					//org = org.substring(0,org.length()-1)+")";
					org += "'"+b0110+"')";
				}
				if(!this.userView.isSuper_admin()){
					if(!"".equalsIgnoreCase(b0110)){
						frowset = dao.search(sqlBuffer.toString()+org+" or b0110 is null) "+order_by);
					}else{
						frowset = dao.search(sqlBuffer.toString()+" and b0110 is null "+order_by);
					}
				}else
					frowset = dao.search(sqlBuffer.toString()+order_by);
			}else
				frowset = dao.search(sqlBuffer.toString()+order_by);
			
			//sqlBuffer.append(this.doInitOrgUnit("b0110")+") "+order_by);
			//frowset = dao.search(sqlBuffer.toString());
			String temp = "";
			flg = comnbus.findChildNode(a_base_id);
			ArchiveXml archiveXml = new ArchiveXml();
			while (frowset.next())
			{
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
				RecordVo vo = new RecordVo("law_base_file");
				vo.setString("file_id", PubFunc.nullToStr(frowset.getString("file_id")));
				vo.setString("base_id", PubFunc.nullToStr(frowset.getString("base_id")));
				vo.setString("keywords",PubFunc.nullToStr(frowset.getString("keywords")));
				if(colums.length()>0){
					for(int i=0;i<fieldlist.size();i++){
						FieldItem item = (FieldItem)fieldlist.get(i);
						String itemid= item.getItemid();
						String itemtype = item.getItemtype();
						if("extfile".equals(itemid))
							continue;
						if("A".equalsIgnoreCase(itemtype)){
							vo.setString(itemid,PubFunc.nullToStr(frowset.getString(itemid)));
						}else if("D".equalsIgnoreCase(itemtype)){
							String date = "";
							Date datetime = frowset.getDate(itemid);
							if(datetime != null)
								date = OperateDate.dateToStr(datetime, "yyyy-MM-dd");
							date = date == null ? "" : date;
							int data_len = archiveXml.getFiledLength(itemid, dao);//日期型的数据按日期格式显示
							if (date != null && !"".equals(date)) 
							{
								date = date.substring(0, data_len);
							}
							vo.setString(itemid,date);
						}else if("N".equalsIgnoreCase(itemtype)){
							if(item.getDecimalwidth()==0){
								if(frowset.getInt(itemid)!=0)
									vo.setInt(itemid, frowset.getInt(itemid));
							}else{
								if(frowset.getDouble(itemid)!=0)
									vo.setString(itemid, frowset.getString(itemid));
							}
						}
					}
				}else{
					temp = PubFunc.nullToStr(this.frowset.getString("name"));
					// vo.setString("name", temp.substring(0, temp.length() > 20 ?
					// 20
					// : temp.length())
					// + "...");
					vo.setString("name", temp);
					temp = PubFunc.nullToStr(this.frowset.getString("title"));
//					if (temp.length() > 40) {
//						temp = temp.substring(0, 40) + "...";
//					}
					vo.setString("title", temp);
					vo.setString("type", PubFunc.nullToStr(frowset.getString("type")));
					vo.setString("content_type", PubFunc.nullToStr(frowset.getString("content_type")));
					vo.setString("valid", PubFunc.nullToStr(frowset.getString("valid")));
					vo.setString("note_num", PubFunc.nullToStr(frowset.getString("note_num")));
					vo.setString("issue_org", PubFunc.nullToStr(frowset.getString("issue_org")));
					vo.setString("notes", PubFunc.nullToStr(frowset.getString("notes")));
					vo.setString("keywords",PubFunc.nullToStr(frowset.getString("keywords")));
					vo.setDate("issue_date", PubFunc.FormatDate((frowset.getDate("issue_date"))));
					vo.setDate("implement_date", PubFunc.FormatDate((frowset.getDate("implement_date"))));
					vo.setDate("valid_date", PubFunc.FormatDate((frowset.getDate("valid_date"))));
					vo.setString("ext", PubFunc.nullToStr(frowset.getString("ext")).toLowerCase().trim());
					vo.setString("originalext",PubFunc.nullToStr(frowset.getString("originalext")).toLowerCase().trim());
					vo.setString("viewcount", PubFunc.nullToStr(frowset.getString("viewcount")));
				}
					
				volist.add(vo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			getFormHM().put("flg", flg);
			getFormHM().put("filelist", volist);
			getFormHM().put("base_id", PubFunc.encrypt(a_base_id));
			this.getFormHM().put("status",status);
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("field_str_item", field_str_item);
			this.getFormHM().put("colums", colums.length()>0?colums.toString()+",":"");
		}
	  }catch (Exception ex) {
		  ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
	  }

	}
	
	/**
	 * 如果有操作单位走操作单位，否则走管理范围
	 * @return
	 */
	public String doInitOrgUnit(String itemid){
		StringBuffer sql=new StringBuffer();
			if(userView.getStatus()!=0){//自助用户走管理范围
				sql.append(" and (");
				//String itemid=this.getItemid();
				String codesetid=userView.getManagePrivCode();
				String codeitemid=userView.getManagePrivCodeValue();
				if("UN".equalsIgnoreCase(codesetid)){
					sql.append(itemid+"='"+codeitemid+"' and ");
				}else if("UM".equalsIgnoreCase(codesetid)){
					//itemid="e0122";
					sql.append(itemid+"='"+codeitemid+"' and ");
				}else if("@K".equalsIgnoreCase(codesetid)){
					//itemid="e01a1";
					sql.append(itemid+"='"+codeitemid+"' and ");
				}
				sql.append("1=1)");
			}else{//业务用户走操作单位
				String orgunit=userView.getUnit_id();
				orgunit=orgunit.toUpperCase();
				sql.append(" and (");
				if(!"UN".equals(orgunit)){
					String str[]=orgunit.split("`");
					//String itemid=this.getItemid();
					for(int i=0;i<str.length;i++){
						if(str[i].indexOf("UN")!=-1&&str[i].substring(2).length()>0){
							sql.append(itemid+"='"+str[i].substring(2)+"' or ");
						}else if(str[i].indexOf("UM")!=-1){
							//itemid="e0122";
							sql.append(itemid+"='"+str[i].substring(2)+"' or ");
						}else{
							continue;
						}
					}
				}
				sql.append("1=2)");
			}
		return sql.toString();
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
			//以下对upstr和downstr进行过滤，过滤掉数据字典中已经删除的数据
			String upstrNew = "";
			String downstrNew = "";
			String str1 = "";
			String str2 = "";
			HashMap itemMap = new HashMap();
			ArrayList itemList = DataDictionary.getFieldList("LAW_BASE_FILE",Constant.USED_FIELD_SET);
			for(int i=0;i<itemList.size();i++){
				FieldItem item = (FieldItem)itemList.get(i);
				String itemid = item.getItemid();
				itemMap.put(itemid, "1");
			}
			String[] temp1 = upstr.split(",");
			String[] temp2 = downstr.split(",");
			for(int j=0;j<temp1.length;j++){
			    String[] temp3 = temp1[j].split("`");
			    //zxj 20161017 判断数据是否合法
			    if (temp3.length < 2)
			        continue;
			    
				if("extFile".equalsIgnoreCase(temp3[0])){
					str1 = temp3[0]+"`"+temp3[1];
					continue;
				}
				if(itemMap.get(temp3[0])!=null){
					upstrNew += temp3[0]+"`"+temp3[1]+",";
				}
			}
			
			for(int k=0;k<temp2.length;k++){
			    String[] temp4 = temp2[k].split("`");
			    //zxj 20161017 判断数据是否合法
                if (temp4.length < 2)
                    continue;
                
				if("extFile".equalsIgnoreCase(temp4[0])){
					str2 = temp4[0]+"`"+temp4[1];
					continue;
				}
				if(itemMap.get(temp4[0])!=null){
					downstrNew += temp4[0]+"`"+temp4[1]+",";
				}
			}
			if(!"".equals(str1))
				upstrNew+=str1+",";
			if(!"".equals(str2))
				downstrNew+=str2+",";
			sb.append(upstrNew+"``"+downstrNew);
		}else{
			if(!base_id.equals(a_base_id)){
				getFieldStr(base_id,dao,sb);
			}
		}
	}
}