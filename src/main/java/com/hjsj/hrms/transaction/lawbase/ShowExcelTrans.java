package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.lawbase.LawbaseExcel;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:ShowExcelTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jul 19, 2008:3:20:20 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ShowExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String basetype = (String) getFormHM().get("basetype");
		String base_id = (String) getFormHM().get("base_id");
		//base_id = PubFunc.decrypt(base_id);待重现
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		try {
			 list = getShowItem(base_id,dao);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String orgId = userView.getUserOrgId();// 用来保存机构ID
		CommonBusiness comnbus = new CommonBusiness(getFrameconn());
		String order_by = " order by  DisplayOrder,fileorder desc";
		ArrayList infolist = new ArrayList();
		
		try {
			String orgTerm = "basetype=" + basetype;
					/*+ " and (dir = '' or dir = '-1' or dir = '" + orgId
					+ "' or dir is null )";*/
			String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
			if(!"false".equals(law_file_priv.trim())){
			if(!this.userView.isSuper_admin())
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
			//System.out.println(comnbus.findLawbaseFile(a_base_id, orgId, orgTerm));
			if("5".equalsIgnoreCase(basetype)&&!"false".equals(law_file_priv.trim())){
				//String unitid = userView.getUnit_id();
				String unitid = userView.getUserOrgId();
				//String unitids[] = unitid.split("`");
				String org = " and (b0110 in (";
				String b0110 = unitid;
				/*String b0110 = "";
				for(int i=0;i<unitids.length;i++){
					if(unitids[i].length()>0)
					if(unitids[i].substring(0,2).equalsIgnoreCase("UN")){
						b0110=unitids[i].substring(2);
						org += "'"+b0110+"',";
					}
				}*/
				if(!"".equalsIgnoreCase(b0110)){
					//org = org.substring(0,org.length()-1)+")";
					org+="'"+b0110+"')";
				}
				if(!(this.userView.isSuper_admin()&&!this.userView.isBThreeUser())){
					if(!"".equalsIgnoreCase(b0110)){
						frowset = dao.search(comnbus.findLawbaseFile(base_id, orgId,orgTerm,basetype,userView)+org+" or b0110 is null) "+order_by);
					}else{
						frowset = dao.search(comnbus.findLawbaseFile(base_id, orgId,orgTerm,basetype,userView)+" and b0110 is null "+order_by);
					}
				}else
					frowset = dao.search(comnbus.findLawbaseFile(base_id, orgId,orgTerm,basetype,userView)+order_by);
			}else
				frowset = dao.search(comnbus.findLawbaseFile(base_id, orgId,orgTerm,basetype,userView)+order_by);
			while (frowset.next())
			{
				if(!"false".equals(law_file_priv.trim())){
					if (!userView.isHaveResource(IResourceConstant.LAWRULE_FILE, frowset
							.getString("file_id")))//判断当前用户是否有文档权限
					continue;
				}
				LazyDynaBean bean = new LazyDynaBean();
				HashMap extMap = new HashMap();
				extMap = ArchiveXml.getFileExt(base_id, dao);
				if(list == null || list.size() == 0)
					bean = getShowItemValue(frowset);
				else
					bean = getShowItemValue(frowset,list,extMap);
				infolist.add(bean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ArrayList columnlist = new ArrayList();
		ArrayList column = new ArrayList();
		RecordVo vo  = ConstantParamter.getConstantVo("LAW_BASE_DOCTYPE", this.frameconn);
		String viewhide="";
		if(vo!=null){
			viewhide = vo.getString("str_value");
		}
		if(list == null || list.size() == 0){
			viewhide = viewhide==null?"":viewhide;
			columnlist.add("title");
			column.add("标题");
			if(viewhide.indexOf(",note_num,")==-1){
				columnlist.add("note_num");
				column.add("文号");
			}
			if(viewhide.indexOf(",issue_date,")==-1){
				columnlist.add("issue_date");
				column.add("颁布日期");
			}
			columnlist.add("file");
			column.add("文件");
			if(viewhide.indexOf(",originalfile,")==-1){
				columnlist.add("original");
				column.add("原件");
			}
		}else{
			for (int i = 0; i < list.size(); i++) {
				FieldItem item = (FieldItem) list.get(i);
				String itemtype = this.getFieldType(item.getItemid(),dao);
				if ("M".equals(itemtype)) 
				{
					continue;
				}
				if("文件".equalsIgnoreCase(item.getItemdesc())){
					columnlist.add("file");
					column.add("文件");
				}
				else if("originalext".equalsIgnoreCase(item.getItemid())){
					columnlist.add("original");
					column.add(item.getItemdesc());
				}else if("viewcount".equalsIgnoreCase(item.getItemid())){
					continue;
				}
				else if(viewhide.indexOf(","+item.getItemid()+",")==-1){
					columnlist.add(item.getItemid());
					column.add(item.getItemdesc());
				}
			}
		}
		LawbaseExcel exc = new LawbaseExcel(this.userView);
		String excelfile = exc.creatExcel(column,infolist,columnlist);
		excelfile = PubFunc.encrypt(excelfile);
		this.formHM.put("excelfile",excelfile);
	}
	/**
	 * 获取默认导出指标值
	 * @author:Jian Chao
	 * @time: 2012-10-24
	 * @param frowset
	 * @return 
	 */
	private LazyDynaBean getShowItemValue(RowSet frowset){
		LazyDynaBean bean=new LazyDynaBean();
		try {
			bean.set("title", PubFunc.nullToStr(frowset.getString("title")));
			bean.set("note_num", PubFunc.nullToStr(frowset.getString("note_num")));
			bean.set("issue_date",frowset.getDate("issue_date")==null?"":frowset.getDate("issue_date").toString());
		if(PubFunc.nullToStr(frowset.getString("ext")).toLowerCase().trim().length()>0)
			bean.set("file","有");
		else
			bean.set("file","无");
		if(PubFunc.nullToStr(frowset.getString("originalext")).toLowerCase().trim().length()>0)
			bean.set("original","有");
		else
			bean.set("original","无");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 获取设置导出指标值
	 * @author:Jian Chao
	 * @time: 2012-10-29
	 * @param frowset
	 * @param fieldlist
	 * @return 
	 */
	private LazyDynaBean getShowItemValue(RowSet frowset,ArrayList fieldlist,HashMap extMap){
		LazyDynaBean bean=new LazyDynaBean();

		HashMap itemType = new HashMap();
		ArchiveXml archiveXml = new ArchiveXml();
		ContentDAO dao = new ContentDAO(frameconn);
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem item = (FieldItem) fieldlist.get(i);
			itemType.put(item.getItemid(), item.getCodesetid());
			try {
				if("ext".equalsIgnoreCase(item.getItemid())){
					if(PubFunc.nullToStr(frowset.getString("ext")).toLowerCase().trim().length()>0){
						bean.set("file","有");
						bean.set("ext", "有");						
					}
					else{
						bean.set("file","无");
						bean.set("ext", "无");
					}
				}
				else if("extfile".equalsIgnoreCase(item.getItemid())){
					String existExt = (String) extMap.get(frowset.getString("file_id"));
					existExt = existExt == null ? "" : "1";
					if(existExt.toLowerCase().trim().length()>0)
						bean.set("extfile","有");
					else
						bean.set("extfile","无");
				}
				else if("viewcount".equalsIgnoreCase(item.getItemid())){
					continue;
				}
				else if("b0110".equalsIgnoreCase(item.getItemid())){
					String emp = AdminCode.getCodeName("UN",frowset.getString("b0110"));
					if(emp.trim().length()>0)
						bean.set("b0110",emp);
					else
						bean.set("b0110","无");
				}
				else if("originalext".equalsIgnoreCase(item.getItemid())){
					if(PubFunc.nullToStr(frowset.getString("originalext")).toLowerCase().trim().length()>0)
						bean.set("original","有");
					else
						bean.set("original","无");
				}
				else if("issue_date".equalsIgnoreCase(item.getItemid())){
					bean.set("issue_date",frowset.getDate("issue_date")==null?"":frowset.getDate("issue_date").toString());
				}
				else{
					String itemValue = "";
					if (!"0".equals(itemType.get(item.getItemid()))) {
						itemValue = AdminCode.getCodeName((String)itemType.get(item.getItemid()), frowset.getString(item.getItemid()));
					}else if (!"D".equals(item.getItemtype())) 
					{
						itemValue = frowset.getString(item.getItemid());
					}else if ("D".equals(item.getItemtype())) 
					{
						itemValue = frowset.getString(item.getItemid());
						itemValue = itemValue == null ? "" : itemValue;						
						int data_len = archiveXml.getFiledLength(item.getItemid(), dao);
						if (itemValue != null && !"".equals(itemValue)) 
						{
							itemValue = itemValue.substring(0, data_len);
						}
					}
					bean.set(item.getItemid(), PubFunc.nullToStr(itemValue));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return bean;
	}
	
	/**
	 * 获取设置的指标
	 * <p>Create Time:2012-10-30 上午9:53:11</p>
	 * <p>@author:jianc</p>
	 */
	private ArrayList getShowItem(String a_base_id,ContentDAO dao) throws SQLException{
		String field_str = "";
		ArrayList fieldlist = new ArrayList(); 
		if (a_base_id == null || "".equals(a_base_id)
				|| "null".equals(a_base_id.trim())
				|| "root".equals(a_base_id.trim())) {
			String sql = "select * from law_base_struct where field_str is not null";
			this.frecset = dao.search(sql);
			if(this.frecset.next()){
				sql = "select base_id,field_str from law_base_struct where base_id=up_base_id order by displayorder";
				this.frecset = dao.search(sql);
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
		}else{
			StringBuffer sb = new StringBuffer();
			getFieldStr(a_base_id,dao,sb);
			if(sb.length()>2){
				String[] temps= sb.toString().split("``");
				try{
					field_str=temps[1];
				}catch (Exception e) {
					field_str=temps[0];
				}
			}else{
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
				if("extfile".equals(itemid)){
					FieldItem item = new FieldItem("","extfile");
					item.setItemdesc(itemdesc);
					fieldlist.add(item);
				}else{
					FieldItem item = DataDictionary.getFieldItem(itemid);
					if(item!=null){
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
	/**
	 * 获取指标的数据类型
	 * @param field
	 * @return
	 */
	private String getFieldType(String field,ContentDAO dao){
		String type = "";
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ITEMTYPE FROM t_hr_busifield WHERE ITEMID = '" + field + "' AND FIELDSETID = 'LAW_BASE_FILE'");
		try {
			rs = dao.search(sb.toString());
			if (rs.next()) 
			{
				type = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return type;
	}
	
}
