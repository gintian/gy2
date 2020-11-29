package com.hjsj.hrms.businessobject.gz.gz_analyse;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryLProgramBo;
import com.hjsj.hrms.businessobject.hire.zp_options.stat.positionstat.PositionStatBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * <p>
 * Title:GzAnalyseBo.java
 * </p>
 * <p>
 * Description:工资分析
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2007-11-8 上午11:41:37
 * </p>
 * 
 * @author LiZhenWei
 * @version 4.0
 */
public class GzAnalyseBo {
	private Connection conn;
    public static HashMap condmap;
    private UserView view;
    private String tableName;
	private Document doc;
    /** 3:包含审批过程中数据(不考虑sp_flag值)<br> 
     *  1或默认: 只包含审批结束数据(sp_flag=06) 
     */
    private String spFlag;
    public String id="00";
	public GzAnalyseBo(Connection conn) {
		this.conn = conn;
	}
	public GzAnalyseBo(Connection conn,UserView view) {
		this.conn = conn;
		this.view=view;
	}
	public GzAnalyseBo() {

	}
	public String getAnalyseTable()
	{
		if(this.getTableName()==null|| "".equals(this.getTableName().trim()))
			return "salaryhistory";
		else
			return this.getTableName();
	}
	/**
	 * 初始化xml  按部门各月工资汇总表专用  zhaoxg 2014-3-27
	 */
	public void initXML() {
		StringBuffer temp_xml = new StringBuffer();
		String xml="";
		temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
		temp_xml.append("<param>");
		temp_xml.append("<report>");
		temp_xml.append("</report>");
		temp_xml.append("</param>");
		try {
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search("select CtrlParam from reportdetail where RSID='9' and RSDTLID='0'");
			if (rs.next())
				xml = Sql_switcher.readMemo(rs,"CtrlParam");
			if (xml == null || "".equals(xml)) {
				xml = temp_xml.toString();
			}
			doc = PubFunc.generateDom(xml.toString());
			rs.close();
		} catch (Exception ex) {
			xml = temp_xml.toString();
		}
	}
	/**
	 * 设置xml属性  
	 * @param attributeName
	 * @param attributeValue
	 * @param username  当前用户名
	 * @param temp  新增和修改的标记  修改时为id号
	 * @param id  本人当前存在的最大id   没有是0
	 */
	public void setAttributeValue( String attributeName,
			String attributeValue,String username,String temp,int id) {
		try {
			String str_path="/param/report";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			boolean flag = true;
			if(childlist.size()!=0)
			{
				for(int i = 0;i<childlist.size();i++){
					List child=null;
					element=(Element)childlist.get(i);
					if (element != null) {
						child = element.getChildren("r_user");
						if (child != null) {
							for(int j=0;j<child.size();j++){
								Element children = (Element) child.get(j);
								if(children.getAttributeValue("name").equals(username)||(this.view.isSuper_admin()&&!"add".equals(temp))) {//超级用户可以修改所有人的共享方案
									if(!"add".equals(temp)){
										List nextchild=children.getChildren("classify");
										if(nextchild!=null){
											for(int t=0;t<nextchild.size();t++){
												Element nextchildren = (Element) nextchild.get(t);
												if(nextchildren.getAttributeValue("id").equals(temp)){
													nextchildren.setAttribute(attributeName, attributeValue);
												}
											}
										}
									}else if("add".equals(temp)&&children.getAttributeValue("name").equals(username)){
										List nextchild=children.getChildren("classify");
										boolean str=true;
										if(nextchild!=null){
											for(int t=0;t<nextchild.size();t++){
												Element nextchildren = (Element) nextchild.get(t);
												if(nextchildren.getAttributeValue("id").equals(id+1+"")){
													nextchildren.setAttribute(attributeName, attributeValue);
													str=false;
												}
											}
										}
										if(str){
											Element addelement = new Element("classify");
											addelement.setAttribute("id",id+1+"");
											addelement.setAttribute(attributeName,attributeValue);
											children.addContent(addelement);	
										}
									}
									flag=false;
								}	
							}
							
						}
					}
				}
				if(flag){
					Element bbElement = (Element) xpath.selectSingleNode(doc);
					element = null;
					element = new Element("r_user");
					element.setAttribute("name",this.view.getUserName());
					String sup="";
					if(this.view.isSuper_admin()){
						sup="0";
					}else{
						sup="1";
					}
					element.setAttribute("super",sup);
					Element _element=new Element("classify");
					_element.setAttribute("id",id+1+"");
					_element.setAttribute(attributeName,attributeValue);
					element.addContent(_element);
					bbElement.addContent(element);									
				} 
			}		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public void delFangAn(String username,String id){
		try{	

			String str_path="/param/report";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				for (int i = 0; i < childlist.size(); i++) {
					List child = null;
					element = (Element) childlist.get(i);
					if (element != null) {
						child = element.getChildren("r_user");
						if (child != null) {
							for (int j = 0; j < child.size(); j++) {
								Element children = (Element) child.get(j);
								if (children.getAttributeValue("name").equals(username)||this.view.isSuper_admin()) {//超级用户可以删除所有人的共享方案
										List nextchild = children.getChildren("classify");
										if (nextchild != null) {
											for (int t = 0; t < nextchild.size(); t++) {
												Element nextchildren = (Element) nextchild.get(t);
												if (nextchildren.getAttributeValue("id").equals(id)) {
													children.removeContent(nextchildren);
												}
											}
										}								
								}
							}
						}
					}
				}
			}				
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 获取方案名
	 * @param list
	 * @param username
	 */
	public void getFaname(ArrayList list,String username,StringBuffer str_value1){
		try{	
			String str_path="/param/report";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			boolean flag = true;
			if(childlist.size()!=0)
			{
				for(int i = 0;i<childlist.size();i++){
					List child=null;
					element=(Element)childlist.get(i);
					if (element != null) {
						child = element.getChildren("r_user");
						if (child != null) {
							for(int j=0;j<child.size();j++){
								Element children = (Element) child.get(j);
								if(children.getAttributeValue("name").equals(username)){
										List nextchild=children.getChildren("classify");
										if(nextchild!=null){
											for(int t=0;t<nextchild.size();t++){
												Element nextchildren = (Element) nextchild.get(t);
												String str="(私有)";
												if("0".equals(nextchildren.getAttributeValue("scope"))){
													str="("+children.getAttributeValue("name")+"_共享)";
													CommonData dataobj = new CommonData(children.getAttributeValue("name")+":"+nextchildren.getAttributeValue("id")+":"+"s."+nextchildren.getAttributeValue("name"), nextchildren.getAttributeValue("name")+str);
													str_value1.append("`"+children.getAttributeValue("name")+":"+nextchildren.getAttributeValue("id")+":"+"s."+nextchildren.getAttributeValue("name")+"~"+nextchildren.getAttributeValue("name")+str+"");
													list.add(dataobj);
												}else{
													str="("+children.getAttributeValue("name")+"_私有)";
													CommonData dataobj = new CommonData(children.getAttributeValue("name")+":"+nextchildren.getAttributeValue("id")+":"+"s."+nextchildren.getAttributeValue("name"), nextchildren.getAttributeValue("name")+str);
													str_value1.append("`"+children.getAttributeValue("name")+":"+nextchildren.getAttributeValue("id")+":"+"s."+nextchildren.getAttributeValue("name")+"~"+nextchildren.getAttributeValue("name")+str+"");
													list.add(dataobj);
												}
											}
										}									
								}else{
									List nextchild=children.getChildren("classify");
									if(nextchild!=null){
										for(int t=0;t<nextchild.size();t++){
											Element nextchildren = (Element) nextchild.get(t);
											String str="(私有)";
											if("0".equals(nextchildren.getAttributeValue("scope"))){
												str="("+children.getAttributeValue("name")+"_共享)";
												CommonData dataobj = new CommonData(children.getAttributeValue("name")+":"+nextchildren.getAttributeValue("id")+":"+"g."+nextchildren.getAttributeValue("name"), nextchildren.getAttributeValue("name")+str);
												str_value1.append("`"+children.getAttributeValue("name")+":"+nextchildren.getAttributeValue("id")+":"+"g."+nextchildren.getAttributeValue("name")+"~"+nextchildren.getAttributeValue("name")+str+"");
												list.add(dataobj);
											}
										}
									}	
								}
							}
							
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public Vector getFameVetor(String username){
		Vector vector = new Vector();
		try{	
			String str_path="/param/report";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;	
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("value", "00");
			bean.set("name", " ");
			vector.addElement(bean);
			if(childlist.size()!=0)
			{
				for(int i = 0;i<childlist.size();i++){
					List child=null;
					element=(Element)childlist.get(i);
					if (element != null) {
						child = element.getChildren("r_user");
						if (child != null) {
							for(int j=0;j<child.size();j++){
								Element children = (Element) child.get(j);
								if(children.getAttributeValue("name").equals(username)){
										List nextchild=children.getChildren("classify");
										if(nextchild!=null){
											for(int t=0;t<nextchild.size();t++){
												Element nextchildren = (Element) nextchild.get(t);
												String str="(私有)";
												if("0".equals(nextchildren.getAttributeValue("scope"))){
													str="("+children.getAttributeValue("name")+"_共享)";
													bean = new LazyDynaBean();
													bean.set("value", children.getAttributeValue("name")+":"+nextchildren.getAttributeValue("id")+":"+"s."+nextchildren.getAttributeValue("name"));
													bean.set("name", nextchildren.getAttributeValue("name")+str);
													vector.addElement(bean);
												}else{
													str="("+children.getAttributeValue("name")+"_私有)";
													bean = new LazyDynaBean();
													bean.set("value", children.getAttributeValue("name")+":"+nextchildren.getAttributeValue("id")+":"+"s."+nextchildren.getAttributeValue("name"));
													bean.set("name", nextchildren.getAttributeValue("name")+str);
													vector.addElement(bean);
												}
											}
										}									
								}else{
									List nextchild=children.getChildren("classify");
									if(nextchild!=null){
										for(int t=0;t<nextchild.size();t++){
											Element nextchildren = (Element) nextchild.get(t);
											String str="(私有)";
											if("0".equals(nextchildren.getAttributeValue("scope"))){
												str="("+children.getAttributeValue("name")+"_共享)";
												bean = new LazyDynaBean();
												bean.set("value", children.getAttributeValue("name")+":"+nextchildren.getAttributeValue("id")+":"+"g."+nextchildren.getAttributeValue("name"));
												bean.set("name", nextchildren.getAttributeValue("name")+str);
												vector.addElement(bean);
											}
										}
									}	
								}
							}
							
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return vector;
	}
	/**
	 * 获取方案的详细信息
	 * @param faName
	 * @return
	 */
	public HashMap getFullFaName(String faName){
		HashMap map=new HashMap();
		try{	
			String[] name=faName.split(":");
			String str_path="/param/report";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i = 0;i<childlist.size();i++){
					List child=null;
					element=(Element)childlist.get(i);
					if (element != null) {
						child = element.getChildren("r_user");
						if (child != null) {
							for(int j=0;j<child.size();j++){
								Element children = (Element) child.get(j);
								if(children.getAttributeValue("name").equals(name[0])){
										List nextchild=children.getChildren("classify");
										if(nextchild!=null){
											for(int t=0;t<nextchild.size();t++){
												Element nextchildren = (Element) nextchild.get(t);
												if(nextchildren.getAttributeValue("id").equals(name[1])){
													map.put("id", nextchildren.getAttributeValue("id"));
													map.put("item", nextchildren.getAttributeValue("item"));
													map.put("name", nextchildren.getAttributeValue("name"));
													map.put("scope", nextchildren.getAttributeValue("scope"));
													map.put("values", nextchildren.getAttributeValue("values"));
												}
											}
										}									
								}
							}
							
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return map;
	}
	/**
	 * 获取具体指标
	 * @param itemid
	 * @param codeitemid
	 * @return
	 */
	public String getItemCode(String itemid,String codeitemid){
		String desc="";
		try{
			FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
			String codesetid ="";
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				if(fielditem.isCode()||codesetid.trim().length()>0){
					if(codesetid!=null||codesetid.trim().length()>0){
						StringBuffer sqlstr = new StringBuffer();
						if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
							sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' and codeitemid = '"+codeitemid+"'");
							sqlstr.append(" order by codeitemid");
						}else if("@@".equalsIgnoreCase(codesetid)){
							sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
							sqlstr.append(" where Pre = '"+codeitemid+"'");
						}else
						{
							
							sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' and codeitemid = '"+codeitemid+"'");
							sqlstr.append(" order by codeitemid");
						}
						ContentDAO dao = new ContentDAO(this.conn);
						RowSet rs=dao.search(sqlstr.toString());
						if(rs.next()){
							desc=rs.getString("codeitemdesc");
						}
					}
				}
			}	
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return desc;
	}
	/**
	 * 获取当前用户的样式下的最大id
	 * @param username
	 * @return
	 */
	public int getMaxId(String username){
		int id=0;
		try{
			String str_path="/param/report";
			XPath xpath=XPath.newInstance(str_path);
			List childlist=xpath.selectNodes(doc);
			Element element=null;
			if(childlist.size()!=0)
			{
				for(int i = 0;i<childlist.size();i++){
					List child=null;
					element=(Element)childlist.get(i);
					if (element != null) {
						child = element.getChildren("r_user");
						if (child != null) {
							for(int j=0;j<child.size();j++){
								Element children = (Element) child.get(j);

									List nextchild=children.getChildren("classify");
									if(nextchild!=null){
										for(int t=0;t<nextchild.size();t++){
											Element nextchildren = (Element) nextchild.get(t);
												int temp= Integer.parseInt(nextchildren.getAttributeValue("id"));
											if(temp>id){
												id=temp;
											}
										}
									}
									
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return id;
	}
	/**
	 * 返回要保存的xml内容
	 * 
	 */
	public String saveStrValue() {
		StringBuffer buf = new StringBuffer();
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		buf.append(outputter.outputString(doc));
		return buf.toString();
	}
	/**
	 * 取得应用库列表
	 * 
	 * @return
	 */
	public ArrayList getDbList(ArrayList privDbList) {
		ArrayList list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer("");
			for(int i=0;i<privDbList.size();i++)
			{
				buf.append(",'");
				buf.append(privDbList.get(i).toString().toUpperCase());
				buf.append("'");
			}
			StringBuffer sql = new StringBuffer("select pre,dbname from dbname");
			if(buf.toString().length()>0)
			{
				sql.append(" where upper(pre) in("+buf.toString().substring(1)+")");
			}else
			{
				sql.append(" where 1=2");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString()+" order by dbid");
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("pre", rs.getString("pre").toUpperCase());
				bean.set("dbname", rs.getString("dbname"));
				list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getDbList3(String privdb) {
		ArrayList list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer("select pre,dbname from dbname");
			if(privdb==null|| "".equals(privdb))
			{
				sql.append(" where 1=2 ");
			}
			else
			{
				String[]  temp = privdb.split("#");
				StringBuffer t_buf = new StringBuffer();
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i]==null|| "".equals(temp[i]))
						continue;
					t_buf.append("'");
					t_buf.append(temp[i]);
					t_buf.append("',");
				}
				t_buf.setLength(t_buf.length()-1);
				sql.append(" WHERE UPPER(PRE) IN ("+t_buf.toString()+") ");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("pre", rs.getString("pre").toUpperCase());
				bean.set("dbname", rs.getString("dbname"));
				list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * get reportdetail max id
	 * 
	 * @return
	 */
	public int getMaxid() {
		int n = 0;
		try {
			String sql = "select max(rsdtlid) as id  from reportdetail";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				n = rs.getInt("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return n + 1;
	}

	/**
	 * 取所有薪资列别
	 * 
	 * @return
	 */
	public ArrayList getSalarySetList(String gz_module,UserView userView) {
		ArrayList list = new ArrayList();
		try {
			String sql = "";
			if("0".equalsIgnoreCase(gz_module))
			{
				sql="select salaryid,cname from salarytemplate where (cstate is null or cstate='')  order by seq ";
			}
			if("1".equalsIgnoreCase(gz_module))
			{
				sql = "select salaryid,cname from salarytemplate where cstate='1' order by seq";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				if("0".equals(gz_module))
				{
					if(!userView.isHaveResource(IResourceConstant.GZ_SET, rs.getString("salaryid")))
						continue;
				}
				else
				{
					if(!userView.isHaveResource(IResourceConstant.INS_SET, rs.getString("salaryid")))
						continue;
				}
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("salaryid", rs.getString("salaryid"));
				bean.set("cname", rs.getString("cname"));
				list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getSalarySetCommonDataList(String gz_module,UserView userView) {
		ArrayList list = new ArrayList();
		try {
			String sql = "";
			if("0".equalsIgnoreCase(gz_module))
			{
				sql="select salaryid,cname from salarytemplate where (cstate is null or cstate='')  order by seq ";
			}
			if("1".equalsIgnoreCase(gz_module))
			{
				sql = "select salaryid,cname from salarytemplate where cstate='1' order by seq";
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				if("0".equals(gz_module))
				{
					if(!userView.isHaveResource(IResourceConstant.GZ_SET, rs.getString("salaryid")))
						continue;
				}
				else
				{
					if(!userView.isHaveResource(IResourceConstant.INS_SET, rs.getString("salaryid")))
						continue;
				}
				CommonData bean = new CommonData(rs.getString("salaryid"),rs.getString("cname"));
				list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public void deleteReportDetail(String rsdtlid, String rsid) {
		try {
			String sql = "delete from reportdetail where rsid=" + rsid
					+ " and rsdtlid=" + rsdtlid;
			ContentDAO dao = new ContentDAO(this.conn);
			dao.delete(sql, new ArrayList());
			sql = "delete from reportitem where rsdtlid="+rsdtlid;
			dao.delete(sql, new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList getGzProjectList(int type, HashMap map,String rsid) {
		ArrayList list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT S.itemid,max(S.itemtype),max(S.Itemlength),max(S.DecWidth),max(S.nWidth),");
			sql.append(" F.CodeSetID,F.itemdesc FROM salaryset S, Fielditem F where S.itemid=F.itemid and ");
			//sql.append(" S.itemid in (select itemid  from fielditem where useflag='1'");
			sql.append(" F.useflag='1'");
			/**第7表类，可选择其他型的指标*/
			if(!"7".equals(rsid)&&!"16".equals(rsid))
	    		sql.append(" and F.itemtype='N'");
			else
				sql.append(" and UPPER(F.itemid)<>'A0101' ");
			sql.append("  and S.itemid in (select itemid from salaryset  WHERE  (CSTATE IS NULL OR CSTATE=''))");
			sql.append(" group by S.itemid,F.CodeSetID,F.itemdesc ORDER BY S.itemid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				if (type == 1)// new
				{
					bean.set("itemid", rs.getString("itemid"));
					bean.set("itemdesc", rs.getString("itemdesc"));
					bean.set("isSelected", "0");
				} else// edit
				{
					bean.set("itemid", rs.getString("itemid"));
					bean.set("itemdesc", rs.getString("itemdesc"));
					if (map.get(rs.getString("itemid").toLowerCase()) != null)
						bean.set("isSelected", "1");
					else
						bean.set("isSelected", "0");
				}
				list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**新建分析表时，，分工资套来选择项目，该方法是取得某工资套下面，，还没有选择过的项目*/
	public ArrayList getGzProjectList(String selectedid,String salaryid,String rsid) {
		ArrayList list = new ArrayList();
		try {
			if(selectedid==null||selectedid.trim().length()<=0)
				selectedid="1=2";
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT S.itemid,max(S.itemtype),max(S.Itemlength),max(S.DecWidth),max(S.nWidth),");
			sql.append(" F.CodeSetID,F.itemdesc FROM salaryset S, Fielditem F where S.itemid=F.itemid and ");
			//sql.append(" S.itemid in (select itemid  from fielditem where useflag='1'");
			sql.append(" F.useflag='1' ");
			/**第7表类，可选择其他型的指标*/
			if(!"7".equals(rsid))
	    		sql.append(" and F.itemtype='N'");
			else
				sql.append(" and UPPER(F.itemid)<>'A0101' ");
			sql.append(" and S.itemid in (select itemid from salaryset  WHERE  salaryid="+salaryid+" and ("+selectedid.toUpperCase()+")) ");
			sql.append(" group by S.itemid,F.CodeSetID,F.itemdesc ORDER BY S.itemid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				if(this.view!=null&& "0".equals(this.view.analyseFieldPriv(rs.getString("itemid"))))
					continue;
				CommonData bean = new CommonData(rs.getString("itemid"),rs.getString("itemdesc"));
				list.add(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
public ArrayList getSelectedItemList(String rsdtlid)
{
	ArrayList list = new ArrayList();
	try {
		String sql = "select * from reportitem where rsdtlid=" + rsdtlid+" order by sortid";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		rs = dao.search(sql);
		while (rs.next()) {
		  if(this.view!=null&& "0".equals(this.view.analyseFieldPriv(rs.getString("itemid"))))
			  continue;
		  CommonData data =new CommonData(rs.getString("itemid"),rs.getString("itemdesc"));
		  list.add(data);
		}
		rs.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return list;
}
	public HashMap getSelectedGzProjectList(String rsdtlid) {
		HashMap map = new HashMap();
		try {
			String sql = "select * from reportitem where rsdtlid=" + rsdtlid+" order by sortid";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				map.put(rs.getString("itemid").toLowerCase(), rs.getString("itemid"));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap getName(String rsid, String rsdtlid) {
		HashMap map = new HashMap();
		try {
			String sql = "select rsdtlname,bgroup,ctrlparam from reportdetail where rsid="+ rsid + " and rsdtlid=" + rsdtlid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				map.put("name", rs.getString("rsdtlname"));
				map.put("bgroup", rs.getString("bgroup"));
				String ctrlparam = Sql_switcher.readMemo(rs, "ctrlparam");
				map.put("ownertype", this.analyseXML(ctrlparam));
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	 public String analyseXML(String xml)
	    {
	    	String ownertype="0";
	    	try
	    	{
	    		if(xml==null|| "".equals(xml))
	    		{
	    			xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <param> </param>  ";
	    		}
	    		Document doc = PubFunc.generateDom(xml);
				Element root=doc.getRootElement();
		    	XPath xpath=XPath.newInstance("/"+root.getName()+"/owner");
	    		Element element=(Element)xpath.selectSingleNode(doc);
	    		if(element==null)
	    			return ownertype;
	    		else{
	    			ownertype = element.getAttributeValue("type");
	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return ownertype;
	    }
	public ArrayList getItemProperty(String[] items) {
		ArrayList list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < items.length; i++) {
				buf.append(",'");
				buf.append(items[i]);
				buf.append("'");
			}
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT S.itemid,max(S.itemtype) as itemtype,max(S.Itemlength) as itemlength,max(S.DecWidth) as DecWidth,");
			sql.append("max(S.nWidth) as nWidth,  F.CodeSetID,F.itemdesc FROM salaryset S, Fielditem F ");
			sql.append("where S.itemid=F.itemid and S.itemid in (");
			sql.append(buf.toString().substring(1));
			sql.append(") group by S.itemid,F.CodeSetID,F.itemdesc ORDER BY S.itemid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			int i = 0;
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemid", rs.getString("itemid"));
				bean.set("itemtype", rs.getString("itemtype"));
				bean.set("decwidth", rs.getString("decwidth"));
				bean.set("sortid", String.valueOf(i));
				bean.set("itemdesc", rs.getString("itemdesc"));
				// bean.set("codesetid",rs.getString("codesetid"));
				bean.set("nwidth", rs.getString("nwidth"));
				list.add(bean);
				i++;
			}
			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getItemProperty2(String items) {
		ArrayList list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT S.itemid,max(S.itemtype) as itemtype,max(S.Itemlength) as itemlength,max(S.DecWidth) as DecWidth,");
			sql.append("max(S.nWidth) as nWidth,  F.CodeSetID,F.itemdesc FROM salaryset S, Fielditem F ");
			sql.append("where S.itemid=F.itemid and S.itemid in (");
			sql.append(items);
			sql.append(") group by S.itemid,F.CodeSetID,F.itemdesc ORDER BY S.itemid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			int i = 0;
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemid", rs.getString("itemid"));
				bean.set("itemtype", rs.getString("itemtype"));
				bean.set("decwidth", rs.getString("decwidth"));
				bean.set("sortid", String.valueOf(i));
				bean.set("itemdesc", rs.getString("itemdesc"));
				// bean.set("codesetid",rs.getString("codesetid"));
				bean.set("nwidth", rs.getString("nwidth"));
				list.add(bean);
				i++;
			}
			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String updateOrInsert(String rsid, String rsdtlid, String rsname,
			String itemids, String bgroup,String ownerType) {
		String a_rsdtlid = "";
		try {
			String new_rsdtlid = "";
			boolean flag = false;
			if (rsdtlid == null || "".equals(rsdtlid)) {
				flag = true;
				new_rsdtlid = String.valueOf(getMaxid());
			}
			ContentDAO dao = new ContentDAO(this.conn);
			itemids=itemids.substring(1).replaceAll("/","'");
			ArrayList itemproperty = getItemProperty2(itemids);
			ArrayList vo_list = new ArrayList();
			for (int i = 0; i < itemproperty.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) itemproperty.get(i);
				RecordVo item_vo = new RecordVo("reportitem");
				String type = (String) bean.get("itemtype");
				int decwidth = Integer.parseInt((String) bean.get("decwidth"));
				if (flag)
					item_vo.setInt("rsdtlid", Integer.parseInt(new_rsdtlid));
				else
					item_vo.setInt("rsdtlid", Integer.parseInt(rsdtlid));
				item_vo.setInt("stid", 0);
				item_vo.setInt("sortid", Integer.parseInt((String) bean.get("sortid")));
				item_vo.setString("itemid", (String) bean.get("itemid"));
				item_vo.setString("itemdesc", (String) bean.get("itemdesc"));
				item_vo.setInt("nwidth", Integer.parseInt((String) bean.get("nwidth")));
				String itemfmt = "";
				int align = 0;
				if ("D".equals(type)) {
					align = 2;
					itemfmt = "yyyy.mm.dd";
				}
				if ("N".equals(type)) {
					align = 2;
					itemfmt = "0";
					if (decwidth > 0) {
						itemfmt += ".";
						for (int j = 0; j < decwidth; j++)
							itemfmt += "0";
					}

				}
				item_vo.setInt("align", align);
				item_vo.setString("itemfmt", itemfmt);
				vo_list.add(item_vo);
			}
			if (rsdtlid == null || "".equals(rsdtlid))// new
			{
				/** reportdetail */

				RecordVo dvo = new RecordVo("reportdetail");
				dvo.setInt("rsdtlid", Integer.parseInt(new_rsdtlid));
				dvo.setInt("rsid", Integer.parseInt(rsid));
				dvo.setString("rsdtlname", rsname);
				dvo.setInt("stid", 0);
				dvo.setInt("leftmargin", 20);
				dvo.setInt("rightmargin", 20);
				dvo.setInt("topmargin", 20);
				dvo.setInt("bottommargin", 20);
				dvo.setInt("bgroup", Integer.parseInt(bgroup));
				Element param = new Element("param");
				Element onwerT=new Element("owner");
				onwerT.setAttribute("type", ownerType);
				onwerT.setText(this.view.getUserName());
				param.addContent(onwerT);
				Document myDocument = new Document(param);
				XMLOutputter outputter = new XMLOutputter();
				Format format = Format.getPrettyFormat();
				format.setEncoding("UTF-8");
				outputter.setFormat(format);
				String temp= outputter.outputString(myDocument);
				dvo.setString("ctrlparam", temp);
				dao.addValueObject(dvo);
				/** reportstyle */

			} else// update
			{
				/** reportdetail */
				String xml = this.getCtrlParam(rsdtlid, rsid, ownerType);
				String sql = "update reportdetail set rsdtlname=?,bgroup="+ bgroup + ",ctrlparam=? where rsdtlid=" + rsdtlid + " and rsid="	+ rsid;
				ArrayList a_list = new ArrayList();
				a_list.add(rsname);
				a_list.add(xml);
				dao.update(sql, a_list);
				a_list.clear();
				String del_sql = "delete from reportitem where rsdtlid="+ rsdtlid;
				dao.delete(del_sql, a_list);
			}
			dao.addValueObject(vo_list);
			if (flag)
				a_rsdtlid = new_rsdtlid;
			else
				a_rsdtlid = rsdtlid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a_rsdtlid;
	}

	public String getCtrlParam(String rsdtlid,String rsid,String ownerType)
	{
		RowSet rs = null;
		String xx = "";
		try
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			rs = dao.search("select ctrlparam from reportdetail where rsdtlid=" + rsdtlid + " and rsid="	+ rsid);
			String xml="";
			while(rs.next())
			{
				xml= Sql_switcher.readMemo(rs, "ctrlparam");
			}
			if(xml==null|| "".equals(xml.trim()))
				xml="<?xml version=\"1.0\" encoding=\"GB2312\"?><param></param>";
			Document doc = PubFunc.generateDom(xml);
			XPath xpath=XPath.newInstance("/param/owner");
			Element element = (Element)xpath.selectSingleNode(doc);
			if(element==null)
			{
				element = new Element("owner");
				element.setAttribute("type", ownerType);
				element.setText(view.getUserName());
				doc.getRootElement().addContent(element);
			}
			else
			{
				element.setAttribute("type", ownerType);
				element.setText(view.getUserName());
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xx= outputter.outputString(doc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return xx;
	}
	public ArrayList getTableHeadList5() {
		ArrayList list = new ArrayList();
		for (int i = 1; i < 13; i++) {
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("itemid", String.valueOf(i));
			bean.set("itemdesc", this.getUpperMonth(i));
			bean.set("itemtype", "N");
			bean.set("codesetid", "0");
			bean.set("itemfmt", "0.00");
			bean.set("nwidth", "8");
			bean.set("align", "1");
			list.add(bean);
		}
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("itemid", "avgmonth");
		bean.set("itemdesc", "月平均");
		bean.set("itemtype", "N");
		bean.set("codesetid", "0");
		bean.set("itemfmt", "0.00");
		bean.set("nwidth", "8");
		bean.set("align", "1");
		list.add(bean);
		bean = new LazyDynaBean();
		bean.set("itemid", "total");
		bean.set("itemdesc", "合计");
		bean.set("itemtype", "N");
		bean.set("codesetid", "0");
		bean.set("itemfmt", "0.00");
		bean.set("nwidth", "8");
		bean.set("align", "1");
		list.add(bean);
		return list;
	}

	/**
	 * 得到表头列表
	 * 
	 * @param rsdtld
	 * @return
	 */
	public ArrayList getTableHeadlist(String rsdtld, String rsid, String itemid,String visibleMonth) {
		ArrayList list = new ArrayList();
		try {
			if ("5".equals(rsid) || "6".equals(rsid) || "7".equals(rsid)|| "10".equals(rsid)|| "11".equals(rsid)|| "14".equals(rsid)|| "15".equals(rsid)|| "16".equals(rsid)) {
				String sql = " select a.nwidth,a.align,a.itemdesc,a.itemid,a.itemfmt,b.itemtype,b.codesetid from reportitem a,fielditem b,(select distinct itemid from salaryset) s where a.itemid=b.itemid and s.itemid=a.itemid and s.itemid=b.itemid and a.rsdtlid="
						+ rsdtld + " order by a.sortid";
				LazyDynaBean bean = null;
				ContentDAO dao = new ContentDAO(this.conn);
				RowSet rs = null;
				rs = dao.search(sql);
				while (rs.next()) {
					bean = new LazyDynaBean();
					if(this.view!=null&& "0".equals(this.view.analyseFieldPriv(rs.getString("itemid"))))//this.view.analyseFieldPriv(rs.getString("itemid"),0)
						continue;
					bean.set("itemid", rs.getString("itemid"));
					bean.set("itemdesc", rs.getString("itemdesc"));
					bean.set("align", rs.getString("align"));
					bean.set("nwidth", rs.getString("nwidth"));
					String itemtype = "A";
					String codesetid = "0";
					String itemfmt = "";
					if (rs.getString("itemfmt") != null)
						itemfmt = rs.getString("itemfmt");
					if (rs.getString("itemtype") != null) {
						itemtype = rs.getString("itemtype");
						codesetid = rs.getString("codesetid");
					}

					if ("a00z0".equals(rs.getString("itemid").toLowerCase()))
						bean.set("itemtype", "D");
					else
						bean.set("itemtype", itemtype);
					bean.set("codesetid", codesetid);
					bean.set("itemfmt", itemfmt);
					list.add(bean);
					if ("10".equals(rsid)) {
						bean = new LazyDynaBean();
						bean.set("itemid", "avg" + rs.getString("itemid"));
						bean.set("itemdesc", "人均" + rs.getString("itemdesc"));
						bean.set("align", rs.getString("align"));
						bean.set("nwidth", rs.getString("nwidth"));
						String avgitemtype = "A";
						String avgcodesetid = "0";
						String avgitemfmt = "";
						if (rs.getString("itemfmt") != null)
							avgitemfmt = rs.getString("itemfmt");
						if (rs.getString("itemtype") != null) {
							avgitemtype = rs.getString("itemtype");
							avgcodesetid = rs.getString("codesetid");
						}

						if ("a00z0".equals(rs.getString("itemid").toLowerCase()))
							bean.set("itemtype", "D");
						else
							bean.set("itemtype", avgitemtype);
						bean.set("codesetid", avgcodesetid);
						bean.set("itemfmt", avgitemfmt);
						list.add(bean);
					}
				}
				rs.close();
			} else if ("8".equals(rsid)|| "17".equals(rsid)) {
				for (int i = 0; i < 15; i++) {
					LazyDynaBean bean = new LazyDynaBean();
					//bean.set("itemtype", "N");
					bean.set("codesetid", "0");
					bean.set("itemfmt", "");
					if (i == 0 || i == 13 || i == 14) {
						if (i == 0) {
							bean.set("itemid", "a0101");
							bean.set("itemdesc", "姓名");
							bean.set("itemtype", "A");
						} else if (i == 13) {
							bean.set("itemid", "sum");
							bean.set("itemtype", "N");
							bean.set("itemdesc", "合计");
						}

						else {
							bean.set("itemid", "avg");
							bean.set("itemdesc", "月平均");
							bean.set("itemtype", "N");
						}
					} else {
						bean.set("itemid", String.valueOf(i));
						bean.set("itemdesc", this.getUpperMonth(i));
						bean.set("itemtype", "N");
					}
					list.add(bean);
				}
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("itemtype", "A");
				bean.set("codesetid", "UM");
				bean.set("itemfmt", "");
				bean.set("itemid", "e0122");
				bean.set("itemdesc", "部门");
				list.add(1,bean);
				bean = new LazyDynaBean();
				bean.set("itemtype", "A");
				bean.set("codesetid", "UN");
				bean.set("itemfmt", "");
				bean.set("itemid", "b0110");
				bean.set("itemdesc", "单位");
				list.add(1,bean);
			}

			else if ("9".equals(rsid)) {
				FieldItem item = DataDictionary.getFieldItem(itemid);
				String id=this.id;
				for (int i = 0; i < 17; i++) {
					LazyDynaBean bean = new LazyDynaBean();

					if (i == 0 || i == 14 || i == 15 || i == 16) {
						if (i == 0) {
							bean.set("itemtype", "A");
							bean.set("codesetid", "0");
							bean.set("itemfmt", "");
							bean.set("itemid", "a0101");
							bean.set("itemdesc", item == null ? "" : item.getItemdesc());
							list.add(bean);
						} else if (i == 14) {
							bean.set("itemtype", "N");
							bean.set("codesetid", "0");
							bean.set("itemfmt", "");
							bean.set("itemid", "avgperson");
							bean.set("itemdesc", "平均人数");
							list.add(bean);
						}

						else if (i == 15) {
							bean.set("itemtype", "N");
							bean.set("codesetid", "0");
							bean.set("itemfmt", "0.00");
							bean.set("itemid", "avgpersonvalue");
							bean.set("itemdesc", "人均值");
							list.add(bean);
						} else {
							bean.set("itemtype", "N");
							bean.set("codesetid", "0");
							bean.set("itemfmt", "0.00");
							bean.set("itemid", "total");
							bean.set("itemdesc", "合计");
							list.add(bean);
						}
					} else {
						if(!"00".equals(id)){
							initXML();
							HashMap map=getFullFaName(id);
							String _item=(String) map.get("item");
							String value=(String) map.get("values");
							if(_item==null||value==null){
								continue;
							}
							String[] values=value.split(",");
							for(int j=0;j<values.length;j++){
								bean = new LazyDynaBean();
								bean.set("itemtype", "N");
								bean.set("codesetid", "0");
								bean.set("itemfmt", "0.00");
								if(values[j].indexOf(":")!=-1){
									bean.set("itemid", String.valueOf(i)+"|"+values[j].split(":")[1]);									
									bean.set("itemdesc", values[j].split(":")[1]);
								}else{
									bean.set("itemid", String.valueOf(i)+"|"+values[j]);
									bean.set("itemdesc", this.getItemCode(_item,values[j]));
								}
								list.add(bean);
							}
						}else{
							bean.set("itemtype", "N");
							bean.set("codesetid", "0");
							bean.set("itemfmt", "0.00");
							bean.set("itemid", String.valueOf(i));
							if(i==13){
								bean.set("itemdesc", "全年");
							}else{
								bean.set("itemdesc", this.getUpperMonth(i));
							}
							
							list.add(bean);
						}

						if("1".equals(visibleMonth))
						{
			    		    bean = new LazyDynaBean();
			    		    bean.set("itemtype", "N");
							bean.set("codesetid", "0");
							bean.set("itemfmt", "0");
							bean.set("itemid", String.valueOf(i)+"p");
							if(i==13){
								bean.set("itemdesc", "全年人数");
							}else{
								bean.set("itemdesc", this.getUpperMonth(i)+"人数");
							}
							
							list.add(bean);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public HashMap getBeforeYear(String rsid, String rsdtlid, int year,
			String dbSql, String salarySql, String itemid, String itemvalue,
			int endmonth, ArrayList itemlist,String isAll,String role,String privCode,String privCodeValue,String salaryid) {
		HashMap map = new HashMap();
		try {

			/*
			 * select sum(a5805) as a5805 ,month(a00z0) as amonth , count(*) as
			 * a0100 from salaryhistory s where year(s.a00z0)=2006 and
			 * a0107='2'group by month(s.a00z0)
			 */
			StringBuffer buf = new StringBuffer();
			String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,b_units);
			for (int i = 0; i < itemlist.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) itemlist.get(i);
				if (((String) bean.get("itemid")).startsWith("avg"))
					continue;
				/*buf.append(",sum(ISNULL(");
				buf.append((String) bean.get("itemid"));
				buf.append(",0)) as ");*/
				buf.append(", sum("+Sql_switcher.isnull((String) bean.get("itemid"), "0")+") as ");
				buf.append((String) bean.get("itemid"));
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select count(distinct nbase"+Sql_switcher.concat()+"A0100) as personNum,");
			sql.append(Sql_switcher.month("a00z0"));
			sql.append(" as amonth ");
			sql.append(buf.toString());
			sql.append(" FROM "+this.getAnalyseTable()+" WHERE ");
			sql.append(Sql_switcher.year("a00z0"));
			sql.append("=");
			sql.append(year);
			if (itemid != null && !"".equals(itemid) && itemvalue != null&& !"".equals(itemvalue)&& "1".equals(isAll)) {
				sql.append(" and ");
				sql.append(itemid);
				sql.append("='");
				sql.append(itemvalue);
				sql.append("'");
			}
			if (endmonth > 0) {
				sql.append(" and ");
				sql.append(Sql_switcher.month("a00z0"));
				sql.append(" <= ");
				sql.append(endmonth);
			}
			sql.append(" and ");
			sql.append(dbSql);
			sql.append(" and ");
			sql.append(salarySql);
			if(privSql!=null&&!"".equals(privSql))
			{
	    		sql.append(" and "+privSql);
			}
	     	sql.append(" group by ");
			sql.append(Sql_switcher.month("a00z0"));
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			int amonth = 0;
			while (rs.next()) {
				String month = rs.getString("amonth");
				for (int i = 0; i < itemlist.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) itemlist.get(i);
					String item = (String) bean.get("itemid");
					if (item.startsWith("avg"))
						continue;
					String sum=rs.getString(item);
					if(sum==null)
						sum="0.00";
					else
						sum = GzAnalyseBo.div(sum,"1",2);
					map.put(month.toLowerCase() + item.toLowerCase(),sum);
				}
				map.put(month, rs.getString("personNum") == null ? "" : rs.getString("personNum"));
				amonth++;
			}
			rs.close();
			map.put("monthcount", String.valueOf(amonth));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 
	 * @param year
	 *            年份
	 * @param pre
	 *            应用库
	 * @param salaryid
	 *            薪资类别
	 * @param value
	 *            加限制的值
	 * @param fielditemid
	 *            加限制的列
	 * @param endmonth
	 *            截止月份
	 * @return
	 */
	public ArrayList getRecordList10(String rsid, String rsdtld, int year,
			String pre, String salaryid, String itemid, String itemvalue,
			int endmonth, String isAll,String role,String privCode,String privCodeValue,String XiaJiFlagCheck) {
		ArrayList list = new ArrayList();
		try {
			String dbSql = this.getDbSQL(pre,salaryid);
			String salarySql = this.getSalarysetSQL(salaryid);
			ArrayList itemlist = this.getTableHeadlist(rsdtld, rsid, "","0");
			HashMap beforemap = this.getBeforeYear(rsid, rsdtld, year - 1,
					dbSql, salarySql, itemid, itemvalue, endmonth, itemlist,isAll,role,privCode,privCodeValue,salaryid);
			HashMap yearmap = new HashMap();
			/*
			 * select sum(a5805) as a5805 ,month(a00z0) as amonth ,count(*) as
			 * a0100 from salaryhistory s where year(s.a00z0)=2007 group by
			 * month(s.a00z0)
			 */
			StringBuffer buf = new StringBuffer();
			String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,b_units);
			for (int i = 0; i < itemlist.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) itemlist.get(i);
				if (((String) bean.get("itemid")).startsWith("avg"))
					continue;
				/*buf.append(",sum(ISNULL(");
				buf.append((String) bean.get("itemid"));
				buf.append(",0)) as ");*/
			//	Sql_switcher.round(arg0, arg1)
				buf.append(", sum("+Sql_switcher.isnull((String) bean.get("itemid"), "0")+") as ");
				buf.append((String) bean.get("itemid"));
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select count(distinct nbase"+Sql_switcher.concat()+"A0100) as personNum,");
			sql.append(Sql_switcher.month("a00z0"));
			sql.append(" as amonth ");
			sql.append(buf.toString());
			sql.append(" FROM "+this.getAnalyseTable()+" WHERE ");
			sql.append(Sql_switcher.year("a00z0"));
			sql.append("=");
			sql.append(year);
			if (itemid != null && !"".equals(itemid) && itemvalue != null&& !"".equals(itemvalue)&& "1".equals(isAll)) {
					sql.append(" and ");
					sql.append(itemid);
					sql.append(" like '");
					sql.append(itemvalue);
					sql.append("%'");				
//				sql.append(" and ");
//				sql.append(itemid);
//				sql.append("='");
//				sql.append(itemvalue);
//				sql.append("'");
			}
			if (endmonth > 0) {
				sql.append(" and ");
				sql.append(Sql_switcher.month("a00z0"));
				sql.append(" <= ");
				sql.append(endmonth);
			}
			sql.append(" and ");
			sql.append(dbSql);
			sql.append(" and ");
			sql.append(salarySql);
			if(privSql!=null&&!"".equals(privSql))
			{
	    		sql.append(" and ");
	    		sql.append(privSql);
			}
			sql.append(" group by ");
			sql.append(Sql_switcher.month("a00z0"));
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			int amonth = 0;
			rs = dao.search(sql.toString());
			while (rs.next()) {
				String month = rs.getString("amonth");
				for (int i = 0; i < itemlist.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) itemlist.get(i);
					String item = (String) bean.get("itemid");
					if (item.startsWith("avg"))
						continue;
					yearmap.put(month.toLowerCase() + item.toLowerCase(), rs.getString(item) == null ? "" : rs.getString(item));
				}
				yearmap.put(month, rs.getString("personNum") == null ? "" : rs.getString("personNum"));
				amonth++;
			}
			rs.close();
			int beforemonth = Integer.parseInt((String) beforemap.get("monthcount"));
			int countmonth = 0;
			if (amonth > beforemonth)
				countmonth = amonth;
			else
				countmonth = beforemonth;
			LazyDynaBean itembean = null;
			for (int i = 1; i < 13; i++) {
				LazyDynaBean bean = new LazyDynaBean();
				String month = String.valueOf(i);
				bean.set("month", month);
				bean.set("monthcount", countmonth + "");
				bean.set("yearmonth", amonth + "");
				for (int j = 0; j < itemlist.size(); j++) {
					itembean = (LazyDynaBean) itemlist.get(j);
					String item = ((String) itembean.get("itemid")).toLowerCase();
					if (item.startsWith("avg"))
						continue;
					String before ="0.00";
					if(beforemap.get(month.toLowerCase()+ item)!=null)
						before=(String)beforemap.get(month.toLowerCase()+ item);
					if("".equals(before)|| "0".equals(before))
						before="0.00";
					bean.set(item + "before", GzAnalyseBo.div(before, "1", 2));
					
					String ayear ="0.00";
					if(yearmap.get(month.toLowerCase()+ item)!=null)
						ayear=(String)yearmap.get(month.toLowerCase()+ item);
					bean.set(item + "year", ayear == null ? "0.00" : GzAnalyseBo.div(ayear, "1", 2));
					//
					String add = GzAnalyseBo.sub(ayear,before, 2);
					bean.set(item + "adde", add == null ? "0.00" : GzAnalyseBo.div(add, "1", 2));
					//System.out.println(before+"------"+add);
					if((before==null|| "".equalsIgnoreCase(before)||Float.parseFloat(before)==0)/*&&(add!=null&&!add.equals("")&&Float.parseFloat(add)!=0)*/)
					{
						bean.set(item + "addl","0.00");
					}
					else
					{
		    			bean.set(item + "addl",GzAnalyseBo.div(""+(Double.parseDouble(GzAnalyseBo.div(add, before, 4)) * 100), "1", 2));
					}
					String beforeavg = GzAnalyseBo.div(before,(String) beforemap.get(month.toLowerCase()), 2);
					bean.set("avg" + item + "before", beforeavg == null ? "0.00": beforeavg);
					String yearavg = GzAnalyseBo.div(ayear, (String) yearmap.get(month.toLowerCase()), 2);
					bean.set("avg" + item + "year", yearavg == null ? "0.00": yearavg);
					String addavg = GzAnalyseBo.sub(yearavg == null ? "0.00": yearavg, beforeavg == null ? "00.00" : beforeavg, 2);
					bean.set("avg" + item + "adde", addavg);
					if((beforeavg==null|| "".equalsIgnoreCase(beforeavg)||Float.parseFloat(beforeavg)==0)/*&&(add!=null&&!add.equals("")&&Float.parseFloat(add)!=0)*/)
					{
						bean.set("avg" + item + "addl","0.00");
					}
					else
					{
				    	String addeavg = ""+ Double.parseDouble(GzAnalyseBo.div(addavg,beforeavg, 4)) * 100;
				    	bean.set("avg" + item + "addl", GzAnalyseBo.div(addeavg, "1", 2));
					}
				}
				bean.set("avgperson", (String)(yearmap.get(month.toLowerCase()) == null ? "0.00" : GzAnalyseBo.div((String)yearmap.get(month.toLowerCase()), "1", 0)));
				bean.set("month", month);
				list.add(bean);
			}
            this.recordNums=list.size();
			
			/*  for(int j=0;j<list.size();j++) { LazyDynaBean bean =
			  (LazyDynaBean)list.get(j);
			  System.out.println("--------------------------month="+bean.get("month"));
			  for(int i=0;i<itemlist.size();i++) { LazyDynaBean abean =
			  (LazyDynaBean)itemlist.get(i); String item =
			  ((String)abean.get("itemid")).toLowerCase();
			  
			  System.out.println("before="+bean.get(item+"before"));
			  System.out.println("year="+bean.get(item+"year"));
			  System.out.println("add="+bean.get(item+"add"));
			  System.out.println("adde="+bean.get(item+"adde"));
			  System.out.println("avgbefore="+bean.get("avg"+item+"before"));
			  System.out.println("avgyear="+bean.get("avg"+item+"year"));
			  System.out.println("avgadd="+bean.get("avg"+item+"add"));
			  System.out.println("avgadde="+bean.get("avg"+item+"adde"));
			  System.out.println("avgaddl="+bean.get("avg"+item+"addl"));
			  System.out.println("month="+bean.get("monthcount")); } }*/
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public void createIndexArchive()
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			if("salaryarchive".equalsIgnoreCase(this.getAnalyseTable()))
			{
				RowSet rs = null;
				String sql="";
				if(Sql_switcher.searchDbServer()==Constant.MSSQL)
				{
					sql="select COUNT(name) from sysindexes where name='S_ARCHIVE_INDEX'";
				}
				else if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql="select COUNT(index_name) from dba_indexes where  index_name='S_ARCHIVE_INDEX'";
				}
				rs=dao.search(sql);
				if(rs.next())
				{
					if(rs.getInt(1)==0)
						dao.update("create index  S_ARCHIVE_INDEX on salaryarchive (NBASE,A00Z0,A00Z1,SALARYID)");
				}
				if(Sql_switcher.searchDbServer()==Constant.MSSQL)
				{
					sql="select COUNT(name) from sysindexes where name='S_ARCHIVE_INDEX_ONE'";
				}
				else if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql="select COUNT(index_name) from dba_indexes where  index_name='S_ARCHIVE_INDEX_ONE'";
				}
				rs=dao.search(sql);
				if(rs.next())
				{
					if(rs.getInt(1)==0)
						dao.update("create index  S_ARCHIVE_INDEX_ONE on salaryarchive (NBASE,A00Z0,A00Z1,SALARYID,a0100)");
				}
				if(Sql_switcher.searchDbServer()==Constant.MSSQL)
				{
					sql="select COUNT(name) from sysindexes where name='S_ARCHIVE_INDEX_TWO'";
				}
				else if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				{
					sql="select COUNT(index_name) from dba_indexes where  index_name='S_ARCHIVE_INDEX_TWO'";
				}
				rs=dao.search(sql);
				if(rs.next())
				{
					if(rs.getInt(1)==0)
						dao.update("create index  S_ARCHIVE_INDEX_TWO on salaryarchive (NBASE,A00Z0,A0100,SALARYID)");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private int recordNums=0;
	public int getRecordNums()
	{
		return this.recordNums;
	}
public void getHeBean(String[] values,LazyDynaBean bean,HashMap map,ArrayList monthlist){
	try{
		for(int t=0;t<monthlist.size();t++){
			for(int i=0;i<values.length;i++){
				if(values[i].indexOf(":")!=-1){//合并
					if(values[i].split(":")[0].indexOf("`")!=-1){
						String[] aa=values[i].split(":")[0].split("`");
						String value="";
						for(int j=0;j<aa.length;j++){
							value=GzAnalyseBo.add(value, (String) map.get(monthlist.get(t)+"|"+aa[j])==null?"":(String) map.get(monthlist.get(t)+"|"+aa[j]), 2);
						}
						bean.set(monthlist.get(t)+"|"+values[i].split(":")[1], value);
						String temp=(String) bean.get("13|"+values[i].split(":")[1]);
						bean.set("13|"+values[i].split(":")[1], GzAnalyseBo.add(temp,value,2));
					}else{//总计
						String value="";
						
						Set key = map.keySet();
						for (Iterator it = key.iterator(); it.hasNext();) 
						{
							String s = (String) it.next();
							if(s.split("\\|")[0].equals(monthlist.get(t))){
								value=GzAnalyseBo.add(value, (String) (map.get(s)==null?"":map.get(s)), 2);
							}
						}		
						for (Iterator it = key.iterator(); it.hasNext();) 
						{
							String s = (String) it.next();

							if(s.split("\\|")[0].equals(monthlist.get(t))&&s.indexOf("all")!=-1){
								value="";
								value=GzAnalyseBo.add(value, (String) (map.get(s)==null?"":map.get(s)), 2);
							}
						}						
						bean.set(monthlist.get(t)+"|"+values[i].split(":")[1], value);
						String temp=(String) bean.get("13|"+values[i].split(":")[1]);
						bean.set("13|"+values[i].split(":")[1], GzAnalyseBo.add(temp,value,2));
					}					
				}
			}
		}

	}catch(Exception e)
	{
		e.printStackTrace();
	}
}
	/**
	 * 个月工资构成分析
	 * @param year
	 * @param pre
	 * @param salaryid
	 * @param itemid
	 * @param groupitemid
	 * @param codesetid
	 * @param role
	 * @param privCode
	 * @param privCodeValue
	 * @return
	 */
	public ArrayList getRecordList9(int year, String pre, String salaryid,
									String itemid, String groupitemid, String codesetid,String tree_codeitemid,String tree_codesetid,String role,String privCode,String privCodeValue,String level,String visibleMonth) {
		ArrayList list = new ArrayList();
		String tableName=(this.view==null?"salaryanalysedata":("T#"+this.view.getUserName()+"_gz_a"));
		Table table = new Table(tableName);
		DbWizard dbw=new DbWizard(this.conn);
		RowSet rs = null;
		try {
			String dbSql = this.getDbSQL(pre,salaryid);
			String salarySql = this.getSalarysetSQL(salaryid);
			String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,b_units);
			String privsql =this.getPrivSQL("",this.getAnalyseTable()+".",salaryid,tree_codesetid+tree_codeitemid); //getSalaryIdSQL(salaryid,tree_codeitemid,tree_codesetid);
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			HashMap totalProject =null;
			String id=this.id;

			if(!"00".equals(id)){
				initXML();
				HashMap map=getFullFaName(id);
				String item=(String) map.get("item");
				String value=(String) map.get("values");
				if(item==null||item.length()==0||value==null||value.length()==0){
					return new ArrayList();
				}
				String[] values=value.split(",");

				Field field = new Field(itemid,itemid);
				field.setDatatype(DataType.FLOAT);
				field.setLength(30);
				field.setDecimalDigits(4);
				table.addField(field);
				field =new Field(groupitemid,groupitemid);
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table.addField(field);
				field = new Field("num","num");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				field = new Field("amonth","amonth");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);

				field = new Field("temp","temp");
				field.setDatatype(DataType.STRING);
				field.setLength(50);
				table.addField(field);

				if(dbw.isExistTable(tableName,false))
				{
					dbw.dropTable(table);
				}
				dbw.createTable(table);
				String tablename="codeitem";
				if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
					tablename="organization";
				if("-1".equals(level)){

					buf.append("insert into "+tableName+"("+itemid+","+groupitemid+",num,amonth,temp) select "+itemid+","+groupitemid+",num,amonth,temp from (");
					buf.append("select sum(");
					buf.append(itemid);
					buf.append(") as ");
					buf.append(itemid);
					buf.append(",");
					if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
						buf.append(" decode(grouping(" + groupitemid + ") ,1, 'k', "+ groupitemid + ") " + groupitemid + ",");
					} else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
						buf.append(" case when("+groupitemid+" is null or "+groupitemid+" = '') then 'k'  else "+groupitemid+"  end as "+groupitemid+" ,");
					}
					buf.append(" count(distinct A0100"+Sql_switcher.concat()+"NBASE) as num,");
					buf.append(Sql_switcher.month("a00z0"));
					buf.append(" as amonth,"+item+" as temp from "+this.getAnalyseTable()+" where ");
					buf.append(Sql_switcher.year("a00z0"));
					buf.append("=");
					buf.append(year);
					buf.append(" and ");
					buf.append(dbSql);
					buf.append(" and ");
					buf.append(salarySql);
					if(privSql!=null&&!"".equals(privSql))
					{
						buf.append(" and ");
						buf.append(privSql);
					}
					buf.append(" group by ");
					buf.append(groupitemid);
					buf.append(",");
					buf.append(item);
					buf.append(",");
					buf.append(Sql_switcher.month("a00z0"));
					buf.append(") a");
					dao.insert(buf.toString(), new ArrayList());
					buf.setLength(0);
				}else{

					int layer=Integer.parseInt(level);
					StringBuffer condSQL = new StringBuffer();
					if("1".equals(role))
					{
						condSQL.append(" 1=1 ");
					}else
					{
						StringBuffer str = new StringBuffer();
						String unitarr[] =b_units.split("`");
						for(int i=0;i<unitarr.length;i++)
						{
							String codeid=unitarr[i];
							if(codeid.length()>2){
								privCode = codeid.substring(0,2);
								privCodeValue = codeid.substring(2);
								str.append("or  codeitemid like '"+(privCodeValue==null?"":privCodeValue)+"%' ");
							}
						}
						condSQL.append("("+str.substring(2)+")");
//						if(privCode!=null&&!privCode.equals(""))
//						{
//
//							condSQL.append("  codeitemid like '"+(privCodeValue==null?"":privCodeValue)+"%'");
//						}
//						else
//						{
//							condSQL.append( "  1=2 ");
//						}
					}
					ArrayList alist = this.getCodeItemidByLayer(tablename, codesetid, layer,condSQL);
					String sql="insert into "+tableName+"("+itemid+","+groupitemid+",num,amonth,temp) values (?,?,?,?,?) ";
					ArrayList sqlList=new ArrayList();
					for(int j=0;j<alist.size();j++)
					{
						String codeitemid=(String)alist.get(j);
						buf.append("select "+itemid+","+groupitemid+",num,amonth,temp from (");
						buf.append("select sum(");
						buf.append(itemid);
						buf.append(") as ");
						buf.append(itemid);
						buf.append(",'");
						buf.append(codeitemid+"' as "+groupitemid);
						buf.append(",");
						buf.append(" count(distinct A0100"+Sql_switcher.concat()+"NBASE) as num,");
						buf.append(Sql_switcher.month("a00z0"));
						buf.append(" as amonth,"+item+" as temp from "+this.getAnalyseTable()+" where ");
						buf.append(Sql_switcher.year("a00z0"));
						buf.append("=");
						buf.append(year);
						buf.append(" and ");
						buf.append(dbSql);
						buf.append(" and ");
						buf.append(salarySql);
						if(privSql!=null&&!"".equals(privSql))
						{
							buf.append(" and ");
							buf.append(privSql);
						}
						buf.append(" and "+groupitemid+" like '"+codeitemid+"%'");
						buf.append(" group by ");

						buf.append(item);
						buf.append(",");
						buf.append(Sql_switcher.month("a00z0"));
						buf.append(") a");
						RowSet rs1=dao.search(buf.toString());

						while(rs1.next()){
							ArrayList _list=new ArrayList();
							_list.add(rs1.getString(itemid));
							_list.add(rs1.getString(groupitemid));
							_list.add(rs1.getString("num"));
							_list.add(rs1.getString("amonth"));
							_list.add(rs1.getString("temp"));
							sqlList.add(_list);
						}
						buf.setLength(0);
					}
					dao.batchInsert(sql, sqlList);
				}
				buf.setLength(0);
				buf.append("select "+itemid+","+groupitemid+",num,amonth,temp from "+tableName);//+" order by "+groupitemid
				//------------------------------关联organization 按照a0000排序  zhaoxg add 2015-2-6-------
				StringBuffer _sql = new StringBuffer();
				_sql.append("select a.* from ("+buf+") a left join "+tablename+" b on ");
				_sql.append(" a."+groupitemid+"=b.codeitemid and b.codesetid ='"+codesetid+"'");
				_sql.append(" order by b.a0000 ");
				//--------------------------------end---------------------------------------------------
				rs = dao.search(_sql.toString());
				String temp_groupitemid = "";
				int init = 1;
				LazyDynaBean bean = new LazyDynaBean();
				int init2 = 1;// 月数
				String totalMoney = "0";// 每个分类的总额
				String allTotalMoney = "0";
				String personAmount = "0";
				String allPersonAmount = "0";
				String avgPersonAmountTotal = "0";
				boolean flag = false;
				int[] pp=new int[13];
				for(int length=0;length<pp.length;length++)
				{
					pp[length]=0;
				}
				HashMap he=new HashMap();//所有的值的集合
				HashMap num=new HashMap();//每月人数
				ArrayList monthlist=new ArrayList();
				String month="";
				HashMap allmap=new HashMap();//加个全年的统计结果  zhaoxg add 2014-8-15
				while (rs.next()) {
					if(rs.getString(groupitemid)==null)
						continue;
					if (init == 1) {
						flag = true;
						temp_groupitemid = rs.getString(groupitemid);
					}
					if (temp_groupitemid.equalsIgnoreCase(rs.getString(groupitemid))) {
						bean.set("groupitemid",rs.getString(groupitemid));
						bean.set(rs.getString("amonth")+"|"+rs.getString("temp"),rs.getString(itemid) == null ? "" : rs.getString(itemid));//月+|+itemid 的形式，表头也如此
						String temp=(String) (allmap.get(rs.getString("temp")) == null ? "" :allmap.get(rs.getString("temp")));
						allmap.put(rs.getString("temp"), GzAnalyseBo.add(temp, rs.getString(itemid) == null ? "" : rs.getString(itemid), 2));
						month=rs.getString("amonth");//列的月份
						String bb=rs.getString("temp")==null?"all":rs.getString("temp");
						he.put(month+"|"+bb, rs.getString(itemid) == null ? "" : rs.getString(itemid));

						monthlist.add(month);
						String desc=AdminCode.getCodeName(codesetid, rs.getString(groupitemid));
						if("UM".equalsIgnoreCase(codesetid)&&(desc==null||desc.trim().length()==0))
							desc=AdminCode.getCodeName("UN", rs.getString(groupitemid));
						num.put(month+"p", GzAnalyseBo.add((String) num.get(month+"p")==null?"":(String) num.get(month+"p"), rs.getString("num"), 2));
						bean.set("a0101", desc);
						bean.set(month+"p", (String) num.get(month+"p")==null?"":(String) num.get(month+"p"));
						String p=(String) (allmap.get("p")==null?"":allmap.get("p"));
						allmap.put("p", GzAnalyseBo.add(rs.getString("num"), p, 2));
						pp[Integer.parseInt(rs.getString("amonth"))-1]+=Integer.parseInt((rs.getString("num")==null?"0":rs.getString("num")));
						pp[12]+=Integer.parseInt((rs.getString("num")==null?"0":rs.getString("num")));
						init2++;
						totalMoney = GzAnalyseBo.add(totalMoney, rs.getString(itemid), 2);
						allTotalMoney = GzAnalyseBo.add(allTotalMoney, rs.getString(itemid), 2);
						personAmount = GzAnalyseBo.add(personAmount, rs.getString("num"), 2);
						allPersonAmount = GzAnalyseBo.add(allPersonAmount, rs.getString("num"), 2);
					} else {
						String avgPersonAmount = GzAnalyseBo.div(personAmount,String.valueOf(num.size()), 2);
						num=new HashMap();
						bean.set("avgperson", avgPersonAmount);
						avgPersonAmountTotal = GzAnalyseBo.add(avgPersonAmountTotal, avgPersonAmount, 2);
						String avgpersonvalue = GzAnalyseBo.div(totalMoney,personAmount, 2);
						bean.set("avgpersonvalue", avgpersonvalue);
						bean.set("total", totalMoney);
						this.getHeBean(values, bean, he,monthlist);
						he=new HashMap();
						Iterator iter = allmap.entrySet().iterator();
						while (iter.hasNext())
						{
							Entry entry = (Entry) iter.next();
							Object key = entry.getKey();
							Object val = entry.getValue();
							if(key!=null&&"p".equals(key.toString())){
								bean.set("13p", val);
							}else{
								bean.set("13|"+key, val);
							}

						}
						list.add(bean);
						monthlist=new ArrayList();
						totalMoney = "0";
						personAmount = "0";
						init2 = 1;
						bean = new LazyDynaBean();
						bean.set("groupitemid",rs.getString(groupitemid));
						allmap=new HashMap();
						bean.set(rs.getString("amonth")+"|"+rs.getString("temp"),rs.getString(itemid) == null ? "" : rs.getString(itemid));//月+|+itemid 的形式，表头也如此
						String temp=(String) (allmap.get(rs.getString("temp")) == null ? "" :allmap.get(rs.getString("temp")));
						allmap.put(rs.getString("temp"), GzAnalyseBo.add(temp, rs.getString(itemid) == null ? "" : rs.getString(itemid), 2));
						month=rs.getString("amonth");//列的月份
						monthlist.add(month);
						String bb=rs.getString("temp")==null?"all":rs.getString("temp");
						he.put(month+"|"+bb, rs.getString(itemid) == null ? "" : rs.getString(itemid));//键是itemid   方便查找
						String desc=AdminCode.getCodeName(codesetid, rs.getString(groupitemid));
						if("UM".equalsIgnoreCase(codesetid)&&(desc==null||desc.trim().length()==0))
							desc=AdminCode.getCodeName("UN", rs.getString(groupitemid));
						bean.set("a0101", desc);
						num.put(month+"p", GzAnalyseBo.add((String) num.get(month+"p")==null?"":(String) num.get(month+"p"), rs.getString("num"), 2));
						bean.set(month+"p", (String) num.get(month+"p")==null?"":(String) num.get(month+"p"));
						String p=(String) (allmap.get("p")==null?"":allmap.get("p"));
						allmap.put("p", GzAnalyseBo.add(rs.getString("num"), p, 2));
//							bean.set(rs.getString("amonth")+"p", rs.getString("num"));
						pp[Integer.parseInt(rs.getString("amonth"))-1]+=Integer.parseInt((rs.getString("num")==null?"0":rs.getString("num")));
						pp[12]+=Integer.parseInt((rs.getString("num")==null?"0":rs.getString("num")));
						totalMoney = GzAnalyseBo.add(totalMoney, rs.getString(itemid), 2);
						personAmount = GzAnalyseBo.add(personAmount, rs.getString("num"), 2);
						allPersonAmount = GzAnalyseBo.add(allPersonAmount, rs.getString("num"), 2);
						allTotalMoney = GzAnalyseBo.add(allTotalMoney, rs.getString(itemid), 2);
						init2++;
						temp_groupitemid = rs.getString(groupitemid);
					}

					init++;
				}
				rs.close();
				if (flag) {
					String avgPersonAmount = GzAnalyseBo.div(personAmount, String.valueOf(num.size()), 2);
					avgPersonAmountTotal = GzAnalyseBo.add(avgPersonAmountTotal,avgPersonAmount, 2);
					bean.set("avgperson", avgPersonAmount);
					String avgpersonvalue = GzAnalyseBo.div(totalMoney,personAmount, 2);
					bean.set("avgpersonvalue", avgpersonvalue);
					bean.set("total", totalMoney);
					this.getHeBean(values, bean, he,monthlist);
					he=new HashMap();
					Iterator iter = allmap.entrySet().iterator();
					while (iter.hasNext())
					{
						Entry entry = (Entry) iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						if(key!=null&&"p".equals(key.toString())){
							bean.set("13p", val);
						}else{
							bean.set("13|"+key, val);
						}
					}
					list.add(bean);
					monthlist=new ArrayList();
					bean = new LazyDynaBean();
					totalProject=this.getTotalProject(tableName, itemid);
					bean.set("a0101", (String) totalProject.get("a0101"));
					{

						for (int i = 1; i < 14; i++) {
							for(int j=0;j<values.length;j++){
								if(values[j].indexOf(":")!=-1){
									bean.set(String.valueOf(i)+"|"+values[j].split(":")[1], totalProject.get(String.valueOf(i)+"|all") == null ? "" : (String) totalProject.get(String.valueOf(i)+"|all"));
									he.put(String.valueOf(i)+"|all", totalProject.get(String.valueOf(i)+"|all") == null ? "" : (String) totalProject.get(String.valueOf(i)+"|all"));//键是itemid   方便查找
								}else{
									bean.set(String.valueOf(i)+"|"+values[j], totalProject.get(String.valueOf(i)+"|"+values[j]) == null ? "" : totalProject.get(String.valueOf(i)+"|"+values[j]));
									he.put(String.valueOf(i)+"|"+values[j], totalProject.get(String.valueOf(i)+"|"+values[j]) == null ? "" : totalProject.get(String.valueOf(i)+"|"+values[j]));//键是itemid   方便查找
								}

								if("1".equals(visibleMonth))
								{
									bean.set(String.valueOf(i)+"p",pp[i-1]+"");
								}
								monthlist.add(String.valueOf(i));
							}

						}
						bean.set("avgpersonvalue", GzAnalyseBo.div(allTotalMoney,allPersonAmount, 2));
						bean.set("avgperson", avgPersonAmountTotal);
						bean.set("total", allTotalMoney);
						this.getHeBean(values, bean, he,monthlist);
						he=new HashMap();
						list.add(bean);
					}

					this.recordNums=list.size()-1;
				}
				else
				{
					this.recordNums=list.size();
				}

			}else{
				String tablename="codeitem";
				if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
					tablename="organization";
				if("-1".equals(level))
				{
					totalProject=this.getTotalProject(itemid, String.valueOf(year), dbSql, salarySql,tree_codeitemid,tree_codesetid,role,privCode,privCodeValue,privsql,salaryid);
					buf.append("select sum(");
					buf.append(itemid);
					buf.append(") as ");
					buf.append(itemid);
					buf.append(",");
					if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
						buf.append(" decode(grouping(" + groupitemid + ") ,1, 'k', "+ groupitemid + ") " + groupitemid + ",");
					} else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
						buf.append(" case when("+groupitemid+" is null or "+groupitemid+" = '') then 'k'  else "+groupitemid+"  end as "+groupitemid+" ,");
					}
					buf.append(" count(distinct A0100"+Sql_switcher.concat()+"NBASE) as num,");
					buf.append(Sql_switcher.month("a00z0"));
					buf.append(" as amonth from "+this.getAnalyseTable()+" where ");
					buf.append(Sql_switcher.year("a00z0"));
					buf.append("=");
					buf.append(year);
					buf.append(" and ");
					buf.append(dbSql);
					buf.append(" and ");
					buf.append(salarySql);
					if(privSql!=null&&!"".equals(privSql))
					{
						buf.append(" and ");
						buf.append(privSql);
					}
					buf.append(" group by ");
					buf.append(groupitemid);
					buf.append(",");
					buf.append(Sql_switcher.month("a00z0"));
//		         	buf.append(" order by "+groupitemid);
				}
				else
				{

					Field field = new Field(itemid,itemid);
					field.setDatatype(DataType.FLOAT);
					field.setLength(30);
					field.setDecimalDigits(4);
					table.addField(field);
					field =new Field(groupitemid,groupitemid);
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					table.addField(field);
					field = new Field("num","num");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					table.addField(field);
					field = new Field("amonth","amonth");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					table.addField(field);
					if(dbw.isExistTable(tableName,false))
					{
						dbw.dropTable(table);
					}
					dbw.createTable(table);

					int layer=Integer.parseInt(level);
					StringBuffer condSQL = new StringBuffer();
					if("1".equals(role))
					{
						condSQL.append(" 1=1 ");
					}else
					{
						StringBuffer str = new StringBuffer();
						String unitarr[] =b_units.split("`");
						for(int i=0;i<unitarr.length;i++)
						{
							String codeid=unitarr[i];
							if(codeid.length()>2){
								privCode = codeid.substring(0,2);
								privCodeValue = codeid.substring(2);
								str.append("or  codeitemid like '"+(privCodeValue==null?"":privCodeValue)+"%' ");
							}
						}
						condSQL.append("("+str.substring(2)+")");
//						if(privCode!=null&&!privCode.equals(""))
//						{
//
//							condSQL.append("  codeitemid like '"+(privCodeValue==null?"":privCodeValue)+"%'");
//						}
//						else
//						{
//							condSQL.append( "  1=2 ");
//						}
					}
					ArrayList alist = this.getCodeItemidByLayer(tablename, codesetid, layer,condSQL);
					StringBuffer sql = new StringBuffer();
					for(int j=0;j<alist.size();j++)
					{
						String codeitemid=(String)alist.get(j);
						if(j!=0)
							sql.append(" or ");
						sql.append("UPPER("+groupitemid+") like '"+codeitemid.toUpperCase()+"%'");
						buf.setLength(0);
						buf.append("insert into "+tableName+"("+itemid+","+groupitemid+",num,amonth) select "+itemid+","+groupitemid+",num,amonth from (");
						buf.append(" select sum("+itemid+") as "+itemid+",'"+codeitemid+"' as "+groupitemid+",count(distinct a0100"+Sql_switcher.concat()+"nbase) as num,"+Sql_switcher.month("a00z0")+" as amonth from ");
						buf.append(this.getAnalyseTable()+"");
						buf.append(" where ");
						buf.append(Sql_switcher.year("a00z0"));
						buf.append("=");
						buf.append(year);
						buf.append(" and ");
						buf.append(dbSql);
						buf.append(" and ");
						buf.append(salarySql);
						if(privSql!=null&&!"".equals(privSql))
						{
							buf.append(" and ");
							buf.append(privSql);
						}
						buf.append(" and "+groupitemid+" like '"+codeitemid+"%'");
						buf.append(" group by ");
						buf.append(Sql_switcher.month("a00z0")+") a");
						dao.insert(buf.toString(), new ArrayList());
					}
					totalProject=this.getTotalProjectLevel(itemid, String.valueOf(year), dbSql, salarySql,tree_codeitemid,tree_codesetid,role,privCode,privCodeValue,sql.toString(),level,tablename,groupitemid,privsql,salaryid);
					buf.setLength(0);
					buf.append("select "+itemid+","+groupitemid+",num,amonth from "+tableName);
				}
				//------------------------------关联organization 按照a0000排序  zhaoxg add 2015-2-6-------
				StringBuffer _sql = new StringBuffer();
				_sql.append("select a.* from ("+buf+") a left join "+tablename+" b on ");
				_sql.append(" a."+groupitemid+"=b.codeitemid and b.codesetid ='"+codesetid+"'");
				_sql.append(" order by b.a0000 ");
				//--------------------------------end---------------------------------------------------
				rs = dao.search(_sql.toString());
				String temp_groupitemid = "";
				int init = 1;
				LazyDynaBean bean = new LazyDynaBean();
				HashMap allmap=new HashMap();
				int init2 = 1;// 月数
				String totalMoney = "0";// 每个分类的总额
				String allTotalMoney = "0";
				String personAmount = "0";
				String allPersonAmount = "0";
				String avgPersonAmountTotal = "0";
				boolean flag = false;
				int[] pp=new int[13];
				for(int length=0;length<pp.length;length++)
				{
					pp[length]=0;
				}
				while (rs.next()) {
					if(rs.getString(groupitemid)==null)
						continue;
					if (init == 1) {
						flag = true;
						temp_groupitemid = rs.getString(groupitemid);
					}
					if (temp_groupitemid.equalsIgnoreCase(rs.getString(groupitemid))) {
						bean.set(rs.getString("amonth"),rs.getString(itemid) == null ? "" : rs.getString(itemid));
						String temp=(String) (allmap.get("13") == null ? "" :allmap.get("13"));
						allmap.put("13", GzAnalyseBo.add(temp, rs.getString(itemid) == null ? "" : rs.getString(itemid), 2));
						String desc=AdminCode.getCodeName(codesetid, rs.getString(groupitemid));
						if("UM".equalsIgnoreCase(codesetid)&&(desc==null||desc.trim().length()==0))
							desc=AdminCode.getCodeName("UN", rs.getString(groupitemid));
						bean.set("groupitemid",rs.getString(groupitemid));
						bean.set("a0101", desc);
						bean.set(rs.getString("amonth")+"p", rs.getString("num"));
						String p=(String) (allmap.get("p")==null?"":allmap.get("p"));
						allmap.put("p", GzAnalyseBo.add(p, (String) rs.getString("num")==null?"":(String) rs.getString("num"), 2));
						pp[Integer.parseInt(rs.getString("amonth"))-1]+=Integer.parseInt((rs.getString("num")==null?"0":rs.getString("num")));
						pp[12]+=Integer.parseInt((rs.getString("num")==null?"0":rs.getString("num")));
						init2++;
						totalMoney = GzAnalyseBo.add(totalMoney, rs.getString(itemid), 2);
						allTotalMoney = GzAnalyseBo.add(allTotalMoney, rs.getString(itemid), 2);
						personAmount = GzAnalyseBo.add(personAmount, rs.getString("num"), 2);
						allPersonAmount = GzAnalyseBo.add(allPersonAmount, rs.getString("num"), 2);
					} else {
						String avgPersonAmount = GzAnalyseBo.div(personAmount,String.valueOf(init2 - 1), 2);
						bean.set("avgperson", avgPersonAmount);
						avgPersonAmountTotal = GzAnalyseBo.add(avgPersonAmountTotal, avgPersonAmount, 2);
						String avgpersonvalue = GzAnalyseBo.div(totalMoney,personAmount, 2);
						bean.set("avgpersonvalue", avgpersonvalue);
						bean.set("total", totalMoney);
						Iterator iter = allmap.entrySet().iterator();
						while (iter.hasNext())
						{
							Entry entry = (Entry) iter.next();
							Object key = entry.getKey();
							Object val = entry.getValue();
							if(key!=null&&"p".equals(key.toString())){
								bean.set("13p", val);
							}else{
								bean.set("13", val);
							}
						}
						list.add(bean);
						totalMoney = "0";
						personAmount = "0";
						init2 = 1;
						bean = new LazyDynaBean();
						bean.set("groupitemid",rs.getString(groupitemid));
						allmap=new HashMap();
						bean.set(rs.getString("amonth"),rs.getString(itemid) == null ? "" : rs.getString(itemid));
						String temp=(String) (allmap.get("13") == null ? "" :allmap.get("13"));
						allmap.put("13", GzAnalyseBo.add(temp, rs.getString(itemid) == null ? "" : rs.getString(itemid), 2));
						String desc=AdminCode.getCodeName(codesetid, rs.getString(groupitemid));
						if("UM".equalsIgnoreCase(codesetid)&&(desc==null||desc.trim().length()==0))
							desc=AdminCode.getCodeName("UN", rs.getString(groupitemid));
						bean.set("a0101", desc);
						bean.set(rs.getString("amonth")+"p", rs.getString("num"));
						String p=(String) (allmap.get("p")==null?"":allmap.get("p"));
						allmap.put("p", GzAnalyseBo.add(p, (String) rs.getString("num")==null?"":(String) rs.getString("num"), 2));
						pp[Integer.parseInt(rs.getString("amonth"))-1]+=Integer.parseInt((rs.getString("num")==null?"0":rs.getString("num")));
						pp[12]+=Integer.parseInt((rs.getString("num")==null?"0":rs.getString("num")));
						totalMoney = GzAnalyseBo.add(totalMoney, rs.getString(itemid), 2);
						personAmount = GzAnalyseBo.add(personAmount, rs.getString("num"), 2);
						allPersonAmount = GzAnalyseBo.add(allPersonAmount, rs.getString("num"), 2);
						allTotalMoney = GzAnalyseBo.add(allTotalMoney, rs.getString(itemid), 2);
						init2++;
						temp_groupitemid = rs.getString(groupitemid);
					}

					init++;
				}
				rs.close();
				if (flag) {
					String avgPersonAmount = GzAnalyseBo.div(personAmount, String.valueOf(init2 - 1), 2);
					avgPersonAmountTotal = GzAnalyseBo.add(avgPersonAmountTotal,avgPersonAmount, 2);
					bean.set("avgperson", avgPersonAmount);
					String avgpersonvalue = GzAnalyseBo.div(totalMoney,personAmount, 2);
					bean.set("avgpersonvalue", avgpersonvalue);
					bean.set("total", totalMoney);
					Iterator iter = allmap.entrySet().iterator();
					while (iter.hasNext())
					{
						Entry entry = (Entry) iter.next();
						Object key = entry.getKey();
						Object val = entry.getValue();
						if(key!=null&&"p".equals(key.toString())){
							bean.set("13p", val);
						}else{
							bean.set("13", val);
						}
					}
					list.add(bean);
					bean = new LazyDynaBean();
					bean.set("a0101", (String) totalProject.get("a0101"));
					if(!"-1".equals(level))
					{
						for (int i = 1; i < 13; i++) {
							bean.set(String.valueOf(i), totalProject.get(String.valueOf(i)) == null ? "" : (String) totalProject.get(String.valueOf(i)));
							if("1".equals(visibleMonth))
							{
								bean.set(String.valueOf(i)+"p", totalProject.get((String.valueOf(i)+"p")) == null ? "" : (String) totalProject.get((String.valueOf(i)+"p")));
							}
						}
						if(totalProject.get("amounttotal")!=null)
						{
							allTotalMoney=(String)totalProject.get("amounttotal");
						}else
						{
							allTotalMoney="0";
						}
						if(totalProject.get("total")!=null)
						{
							allPersonAmount=(String)totalProject.get("total");
						}
						else
						{
							allPersonAmount="0";
						}
						String totalmonth="0";
						if(totalProject.get("totalmonth")!=null)
						{
							totalmonth=(String)totalProject.get("totalmonth");
						}

						bean.set("avgpersonvalue", GzAnalyseBo.div(allTotalMoney,allPersonAmount, 2));
						bean.set("avgperson", GzAnalyseBo.div(allPersonAmount, totalmonth, 2));
						bean.set("total", allTotalMoney);
						list.add(bean);
					}
					else
					{
						for (int i = 1; i < 14; i++) {
							bean.set(String.valueOf(i), totalProject.get(String.valueOf(i)) == null ? "" : (String) totalProject.get(String.valueOf(i)));
							if("1".equals(visibleMonth))
							{
								bean.set(String.valueOf(i)+"p",pp[i-1]+"");
							}
						}
						bean.set("avgpersonvalue", GzAnalyseBo.div(allTotalMoney,allPersonAmount, 2));
						bean.set("avgperson", avgPersonAmountTotal);
						bean.set("total", allTotalMoney);
						list.add(bean);
					}

					this.recordNums=list.size()-1;
				}
				else
				{
					this.recordNums=list.size();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try
			{
				if(dbw.isExistTable(tableName,false))
				{
					dbw.dropTable(table);
				}
				if(rs!=null)
				{
					rs.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}
		return list;
	}
	public ArrayList getCodeItemidByLayer(String tableName,String codesetid,int layer,StringBuffer condSQL)
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select codeitemid from "+tableName+" where UPPER(codesetid)='"+codesetid.toUpperCase()+"'";
			sql+=" and layer<="+layer;
			if("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid))
				sql+=" and "+condSQL+" order by a0000";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String codeitemid=rs.getString("codeitemid");
				list.add(codeitemid);
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList getRecordList8(String pre, String salaryid, int year,
			String itemid, String tree_codeitemid, String tree_codesetid,String role,String privCode,String privCodeValue) {
		ArrayList list = new ArrayList();
		try {
			if(pre==null|| "".equals(pre)||itemid==null|| "".equals(itemid))
			{
				return list;
			}
			String dbSql = this.getDbSQL(pre,salaryid);//逗号分割
			String salarySql = this.getSalarysetSQL(salaryid);
			String privsql = this.getPrivSQL("",this.getAnalyseTable()+".",salaryid,tree_codesetid+tree_codeitemid);//getSalaryIdSQL(salaryid,tree_codeitemid,tree_codesetid);
			HashMap totalProject = this.getTotalProject(itemid, String.valueOf(year), dbSql, salarySql,tree_codeitemid,tree_codesetid,role,privCode,privCodeValue,privsql,salaryid);
			HashMap sumProject = this.getSumProject(itemid, String.valueOf(year), dbSql, salarySql,tree_codeitemid,tree_codesetid,role,privCode,privCodeValue,privsql,salaryid);
			StringBuffer sql = new StringBuffer();
			StringBuffer s_sql=new StringBuffer();
			String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,b_units);
			String[] tt = pre.split(",");
			for(int j=0;j<tt.length;j++)
			{
				//xiegh 20170516 bug26258 
				//sql.append(" select a.a0000,"+this.getAnalyseTable()+".b0110,"+this.getAnalyseTable()+".e0122,"+this.getAnalyseTable()+".a0101,"+this.getAnalyseTable()+".a0100,"+this.getAnalyseTable()+".nbase,sum("+this.getAnalyseTable()+".");.
				sql.append(" select a.a0000,"+this.getAnalyseTable()+".b0110,"+this.getAnalyseTable()+".e0122,a.a0101,a.a0100,"+this.getAnalyseTable()+".nbase,sum("+this.getAnalyseTable()+".");
	     		sql.append(itemid);
	    		sql.append(") as "+itemid+",");
	    		sql.append(Sql_switcher.month(this.getAnalyseTable()+".a00z0"));
	    		sql.append(" as amonth from "+this.getAnalyseTable()+","+tt[j]+"a01 a where ");
	    		sql.append(this.getAnalyseTable()+".a0100=a.a0100 and ");
	    		sql.append(Sql_switcher.year(this.getAnalyseTable()+".a00z0"));
	    		/**高级授权*/
	    		/*StringBuffer prihighlevel=new StringBuffer("");
	    		String priStrSql = InfoUtils.getWhereINSql(view, tt[j]);
	    		prihighlevel.append("select "+tt[j]+"a01.A0100 ");
				if (priStrSql.length() > 0)
					prihighlevel.append(priStrSql);
				else
					prihighlevel.append(" from "+tt[j]+"a01");*/
				
	    		sql.append("=");
	    		sql.append(year);
	    		//sql.append(" and s.a0100 in ("+prihighlevel.toString()+") ");
	    		sql.append(" and "+dbSql);
		    	if (!"-1".equals(tree_codeitemid)) {
		    		sql.append(" and "+privsql+"");
//		    		if (tree_codesetid.equalsIgnoreCase("UN")) {
//		    			sql.append(" and "+this.getAnalyseTable()+".b0110 like '");
//		      			sql.append(tree_codeitemid);
//		    			sql.append("%' ");
//		     		} else if (tree_codesetid.equalsIgnoreCase("UM")) {
//			    		sql.append(" and "+this.getAnalyseTable()+".e0122 like '");
//			    		sql.append(tree_codeitemid);
//			    		sql.append("%' ");
//	    			}
	    		}
	    		sql.append(" and ");
    			sql.append(salarySql);
    			if(privSql!=null&&!"".equals(privSql))
    			{
    	   		    sql.append(" and ");
    		     	sql.append(privSql);
    			}
    			sql.append(" and UPPER("+this.getAnalyseTable()+".nbase)='"+tt[j].toUpperCase()+"'");
    			//sql.append(" group by "+this.getAnalyseTable()+".a0100,"+this.getAnalyseTable()+".b0110,"+this.getAnalyseTable()+".e0122,a.a0000,"+this.getAnalyseTable()+".nbase,"+this.getAnalyseTable()+".a0101,");
				sql.append(" group by a.a0100,"+this.getAnalyseTable()+".b0110,"+this.getAnalyseTable()+".e0122,a.a0000,"+this.getAnalyseTable()+".nbase,a.a0101,");
			    sql.append(Sql_switcher.month(this.getAnalyseTable()+".a00z0"));
			    if(j!=tt.length-1)
			    {
			    	sql.append(" union ");
			    }
			}
			/**显示了单位和部门，所以部门调换的人要显示两条，一个部门一条*/
			//s_sql.append("select a.* from (");
			s_sql.append(" select  t.b0110,"+Sql_switcher.isnull("e0122","''")+" AS e0122 ,t.a0000, t.a0100,t.nbase,t.a0101,SUM(t." + itemid+ ") AS "+itemid+",amonth from (");
			s_sql.append(sql.toString()+") t group by t.b0110,"+Sql_switcher.isnull("e0122","''")+",t.a0000, t.a0100,t.nbase,t.a0101,amonth order by t.b0110,"+Sql_switcher.isnull("E0122","''")+", Nbase Desc, A0000 ASC");
			//s_sql.append(") a left join dbname on a.nbase=dbname.pre order by dbname.dbid,a.a0000,a.b0110,a.e0122");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(s_sql.toString());
			String a0100 = "";
			String nbase = "";
			int init = 1;
			LazyDynaBean bean = new LazyDynaBean();
			int init2 = 0;
			String total = "0";
			String totalavg = "0";
			String b0110="";
			String e0122="";
			String _b0110="";
			String _e0122="";
			boolean flag=true;
			while (rs.next()) {
				flag=false;
				b0110=rs.getString("b0110");
			    e0122=rs.getString("e0122");
				if (init == 1) {
					a0100 = rs.getString("a0100");
					nbase = rs.getString("nbase");
					_b0110=rs.getString("b0110")==null?"":rs.getString("b0110");
					_e0122=rs.getString("e0122")==null?"":rs.getString("e0122");
				}
				if (a0100.equalsIgnoreCase(rs.getString("a0100"))&& nbase.equalsIgnoreCase(rs.getString("nbase"))&&_b0110.equalsIgnoreCase((rs.getString("b0110")==null?"":rs.getString("b0110")))&&_e0122.equalsIgnoreCase((rs.getString("e0122")==null?"":rs.getString("e0122")))) {
					bean.set(rs.getString("amonth"),rs.getString(itemid) == null ? "0.00" : rs.getString(itemid));
					String a0101 = rs.getString("a0101");
					if(rs.getString("a0101")==null){//zhaoxg 2013-5-22 null转“”  否则死都不知道咋死的  神人啊
						a0101 = "";
					}
					bean.set("a0101",a0101);
					init2++;
				} else {
					String sum = (String) (sumProject.get((a0100+ nbase+(_b0110==null?"":_b0110)+(_e0122==null?"":_e0122)).toUpperCase()) == null ? "0" : sumProject.get((a0100 + nbase+(_b0110==null?"":_b0110)+(_e0122==null?"":_e0122)).toUpperCase()));
					String avg = "";
					if (init2 == 0)
						avg = "0";
					else
						avg = GzAnalyseBo.div(sum, String.valueOf(init2), 2);
					total = GzAnalyseBo.add(total, sum, 2);
					totalavg = GzAnalyseBo.add(totalavg, avg, 2);
					bean.set("sum", sum);
					bean.set("avg", avg);
					bean.set("b0110", AdminCode.getCodeName("UN",_b0110));
					String desc=AdminCode.getCodeName("UM",_e0122);
					if(desc==null||desc.trim().length()==0)
						desc=AdminCode.getCodeName("UN",_e0122); 
					bean.set("e0122",desc);
					list.add(bean);
					init2 = 0;
					bean = new LazyDynaBean();
					bean.set(rs.getString("amonth"),rs.getString(itemid) == null ? "0.00" : rs.getString(itemid));
					String a0101 = rs.getString("a0101");
					if(rs.getString("a0101")==null){//zhaoxg 2013-5-22 null转“”  否则死都不知道咋死的  神人啊
						a0101 = "";
					}
					bean.set("a0101",a0101);
					a0100 = rs.getString("a0100");
					nbase = rs.getString("nbase");
					_b0110=rs.getString("b0110")==null?"":rs.getString("b0110");
					_e0122=rs.getString("e0122")==null?"":rs.getString("e0122");
					init2++;
				}

				init++;
			}
			rs.close();
			if(!flag)
			{
	    		String sum = (String) (sumProject.get((a0100 + nbase+(b0110==null?"":b0110)+(e0122==null?"":e0122)).toUpperCase()) == null ? "": sumProject.get((a0100 + nbase+(b0110==null?"":b0110)+(e0122==null?"":e0122)).toUpperCase()));
		    	String avg = GzAnalyseBo.div(sum, String.valueOf(init2), 2);
		    	total = GzAnalyseBo.add(total, sum, 2);
		    	totalavg = GzAnalyseBo.add(totalavg, avg, 2);
		    	bean.set("sum", sum);
	    		bean.set("avg", avg);
		    	bean.set("b0110", AdminCode.getCodeName("UN",b0110));
		    	String desc=AdminCode.getCodeName("UM",e0122);
				if(desc==null||desc.trim().length()==0)
					desc=AdminCode.getCodeName("UN",e0122); 
	    		bean.set("e0122",desc);
	    		list.add(bean);
		    	bean = new LazyDynaBean();
		    	bean.set("a0101", (String) totalProject.get("a0101"));
		    	for (int i = 1; i < 13; i++) {
		    		bean.set(String.valueOf(i),totalProject.get(String.valueOf(i)) == null ? "": (String) totalProject.get(String.valueOf(i)));
		    	}
	    		bean.set("b0110","");
	    		bean.set("e0122", "");
		    	bean.set("sum", total);
		    	bean.set("avg", GzAnalyseBo.div(totalavg, String.valueOf(list.size()), 2));
		    	list.add(bean);
		    	this.recordNums=list.size()-1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public ArrayList getRecordList5ChangeClo_Row(String rsid, String rsdtlid,
			String pre, String salaryid, int year, String a0100) {
		ArrayList list = new ArrayList();
		try {
			ArrayList headList = getTableHeadlist(rsdtlid, rsid, "","0");
			StringBuffer buf = new StringBuffer();
			StringBuffer sql = new StringBuffer();

			String dbSql = "";
			String salarySql = "";
			for (int i = 0; i < headList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) headList.get(i);
				/*buf.append(" sum(ISNULL(");
				buf.append((String) bean.get("itemid"));
				buf.append(",0)) as ");*/
				buf.append(" sum("+Sql_switcher.isnull((String) bean.get("itemid"), "0")+") as ");
				buf.append((String) bean.get("itemid"));
				buf.append(",");
			}
			buf.setLength(buf.length() - 1);
			dbSql = getDbSQL(pre,salaryid);
			salarySql = getSalarysetSQL(salaryid);

			sql.append("select ");
			sql.append(buf.toString());
			sql.append(",");
			sql.append(Sql_switcher.month("A00Z0"));
			sql.append(" as aMonth ");
			sql.append(" from "+this.getAnalyseTable()+" where ");
			if(a0100==null|| "".equals(a0100))
			{
				sql.append(" 1=2 and ");
			}
			else
			{
		    	sql.append("A0100='" + a0100.substring(3) + "' AND ");
	    		sql.append(" upper(nbase) ='" + a0100.substring(0, 3).toUpperCase() + "' and ");
			}
			sql.append(dbSql);
			sql.append(" and ");
			sql.append(Sql_switcher.year("A00Z0"));
			sql.append("=" + year);
			sql.append(" and ");
			sql.append(salarySql);
			sql.append(" group by ");
			sql.append(Sql_switcher.month("A00Z0"));
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql.toString());
			ArrayList templist = new ArrayList();
			LazyDynaBean abean = null;
			while (rs.next()) {
				abean = new LazyDynaBean();
				for (int i = 0; i < headList.size(); i++) {
					LazyDynaBean tbean = (LazyDynaBean) headList.get(i);
					String itemid = (String) tbean.get("itemid");
					abean.set(itemid, rs.getString(itemid) == null ? "" : rs.getString(itemid));
				}
				abean.set("month", rs.getString("amonth"));
				templist.add(abean);
			}
			rs.close();
			LazyDynaBean bean = null;
			HashMap totalMap = null;
			for (int i = 0; i < headList.size(); i++) {
				totalMap = new HashMap();
				bean = new LazyDynaBean();
				LazyDynaBean tbean = (LazyDynaBean) headList.get(i);
				String itemid = (String) tbean.get("itemid");
				String itemdesc = (String) tbean.get("itemdesc");
				int t = 0;
				for (int j = 0; j < templist.size(); j++) {
					LazyDynaBean ttbean = (LazyDynaBean) templist.get(j);
					String month = (String) ttbean.get("month");
					String value = (String) ttbean.get(itemid);
					totalMap = this.getMap(itemid, value, totalMap);
					bean.set(month, value);
					t++;
				}
				bean.set("desc", itemdesc);
				bean.set("avgmonth", GzAnalyseBo.div((String) totalMap.get(itemid.toLowerCase()), String.valueOf(t == 0 ? 1: t), 2));
				bean.set("total", (String) totalMap.get(itemid.toLowerCase()));
				list.add(bean);
			}
			this.recordNums=headList.size();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 
	 * @param rsid
	 * @param rsdtlid
	 * @param pre
	 * @param salaryid
	 * @param year
	 * @param month
	 * @param a0100
	 * @param fielditemid
	 * @param fielditemvalue
	 * @param tree_codeitemid
	 * @param tree_codesetid
	 * @param classFlag
	 * @param orderSql
	 * @param starttime
	 * @param endtime
	 * @param statflag
	 * @param role
	 * @param privCode
	 * @param privCodeValue
	 * @param isShowUnitData=1部门=0单位
	 * @return
	 */
	public ArrayList getRecordList11(String rsid, String rsdtlid, String pre,
			String salaryid, int year,String month, String a0100, String fielditemid,
			String fielditemvalue, String tree_codeitemid,
			String tree_codesetid, String classFlag, String orderSql,
			String starttime, String endtime, String statflag,String role,String privCode,String privCodeValue,String isShowUnitData)
	{
		ArrayList list = new ArrayList();
		try
		{
			ArrayList headList = getTableHeadlist(rsdtlid, rsid, "","0");
			String dbSql = getDbSQL(pre,salaryid);
			String salarySql =getSalarysetSQL(salaryid);
			String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,b_units);
			String privsql = this.getPrivSQL("",this.getAnalyseTable()+".",salaryid,tree_codesetid+tree_codeitemid);
			StringBuffer buf = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer monthCountBuffer = new StringBuffer();
			monthCountBuffer.append("select "+Sql_switcher.year("a00z0")+" as ayear,");
			monthCountBuffer.append(Sql_switcher.month("a00z0")+" as amonth,b0110,e0122 from "+this.getAnalyseTable()+" where 1=1 ");
			for (int i = 0; i < headList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean) headList.get(i);
				buf.append(", sum("+Sql_switcher.isnull((String) bean.get("itemid"), "0")+") as ");
				buf.append((String) bean.get("itemid"));
				
			}
			buf.append(" from "+this.getAnalyseTable()+" where 1=1 ");
			if (!"-1".equals(tree_codeitemid)) {
				buf.append(" and "+privsql+"");
				monthCountBuffer.append(" and "+privsql+"");
//				if (tree_codesetid.equalsIgnoreCase("UN")) {
//					buf.append(" and b0110 like '");
//					buf.append(tree_codeitemid);
//					buf.append("%' ");
//					
//					monthCountBuffer.append(" and b0110 like '");
//					monthCountBuffer.append(tree_codeitemid);
//					monthCountBuffer.append("%' ");
//				} else if (tree_codesetid.equalsIgnoreCase("UM")) {
//					buf.append(" and e0122 like '");
//					buf.append(tree_codeitemid);
//					buf.append("%' ");
//					
//					monthCountBuffer.append(" and e0122 like '");
//					monthCountBuffer.append(tree_codeitemid);
//					monthCountBuffer.append("%' ");
//				}
			}
		    buf.append(" and "+dbSql);
			buf.append(" and "+salarySql);
			if(privSql!=null&&!"".equals(privSql.trim()))
		    	buf.append(" and "+privSql);	
			monthCountBuffer.append(" and "+dbSql);
			monthCountBuffer.append(" and "+salarySql);
			if(privSql!=null&&!"".equals(privSql.trim()))
	    		monthCountBuffer.append(" and "+privSql);
			HashMap monthCount=null;
			if ("1".equals(statflag)) {
				buf.append(" and "+Sql_switcher.year("a00z0"));
				buf.append("=");
				buf.append(year);
				buf.append(" and ");
				buf.append(Sql_switcher.month("a00z0"));
				buf.append("="+month);
			} else {
				String newStarttime = changeFormat(starttime, ".");
				String newEndtime = changeFormat(endtime, ".");
				PositionStatBo psb = new PositionStatBo(this.conn);
				buf.append(" and ");
				buf.append(psb.getDateSql(">=", "a00z0",newStarttime));
				buf.append(" and ");
				buf.append(psb.getDateSql("<=","a00z0",newEndtime));
				monthCountBuffer.append(" and ");
				monthCountBuffer.append(psb.getDateSql(">=", "a00z0",newStarttime));
				monthCountBuffer.append(" and ");
				monthCountBuffer.append(psb.getDateSql("<=","a00z0",newEndtime));
				monthCountBuffer.append(" group by "+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0")+",b0110,e0122");
		        monthCount=this.getMonthCount(monthCountBuffer.toString(), dao, isShowUnitData);
			}
			
			String sql ="";
			String msql="";
			if("1".equals(isShowUnitData))
			{
	     		sql=" select count(distinct a0100) as a0100,b0110,e0122 "+buf.toString()+" group by b0110,e0122 ";
	     		/*if(!statflag.equals("1"))
	     		{
	     			sql+=","+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0");
	     		}*/
//	     		if(orderSql==null||orderSql.equals(""))
//	     		{
//	     			sql+=" order by e0122";
//	     		}
//	     		else
//	     		{
//	     			/**当选择按人数排序的时候，，转换为a0100列*/
//	     			if(orderSql.indexOf("count")!=-1)
//	     			{
//	     				if(orderSql.indexOf("desc")!=-1)
//	     				   orderSql=" order by a0100 desc";
//	     				else
//	     					orderSql=" order by a0100 asc";
//	     			}
//	     			sql+=orderSql;
//	     		}
	     		
	     	    msql = " select b0110,e0122"+buf.toString()+" group by b0110,e0122 order by b0110,e0122";
			}
			else
			{
				sql=" select count(distinct a0100) as a0100,b0110 "+buf.toString()+" group by b0110 ";
				/*if(!statflag.equals("1"))
	     		{
	     			sql+=","+Sql_switcher.year("a00z0")+","+Sql_switcher.month("a00z0");
	     		}*/
//				if(orderSql==null||orderSql.equals(""))
//	     		{
//	     			sql+=" order by b0110";
//	     		}
//	     		else
//	     		{
//	     			/**当选择按人数排序的时候，，转换为a0100列*/
//	     			/**当选择按人数排序的时候，，转换为a0100列*/
//	     			if(orderSql.indexOf("count")!=-1)
//	     			{
//	     				if(orderSql.indexOf("desc")!=-1)
//	     				   orderSql=" order by a0100 desc";
//	     				else
//	     					orderSql=" order by a0100 asc";
//	     			}
//	     			sql+=orderSql;
//	     		}
	     	    msql = " select b0110"+buf.toString()+" group by b0110 order by b0110";
			}
			HashMap unmmap=this.getUMMap(msql, headList,isShowUnitData);
			//------------------------------关联organization 按照a0000排序  zhaoxg add 2015-2-6-------
			StringBuffer _sql = new StringBuffer();
			_sql.append("select a.* from ("+sql+") a left join organization b on ");
			if("1".equals(isShowUnitData))
			{
				_sql.append(" a.e0122=b.codeitemid and b.codesetid ='UM'");
			}else{
				_sql.append(" a.b0110=b.codeitemid and b.codesetid ='UN'");
			}
			_sql.append(" order by b.a0000 ");
			if(orderSql==null|| "".equals(orderSql))
     		{
//				_sql.append(",b.b0110 ");
     		}
     		else
     		{
     			/**当选择按人数排序的时候，，转换为a0100列*/
     			/**当选择按人数排序的时候，，转换为a0100列*/
     			if(orderSql.indexOf("count")!=-1)
     			{
     				if(orderSql.indexOf("desc")!=-1)
     				   orderSql="a0100 desc";
     				else
     					orderSql="a0100 asc";
     			}
     			_sql.append(",b."+orderSql);
     		}
			//--------------------------------end---------------------------------------------------
			RowSet rs = dao.search(_sql.toString());
			int count=0;
			int init=0;
			String e0122="";
			String b0110="";
			while(rs.next())
			{
				if("1".equals(isShowUnitData))
				{
	    			String ae0122=rs.getString("e0122")==null?"":rs.getString("e0122");
	    			String ab0110=rs.getString("b0110");
	    			/*if(init==0)
	    			{
	    				e0122=ae0122;
	    				b0110=ab0110;
	    			}
	    			init++;
		    		if(e0122.equals(ae0122))
		    		{
		    			count++;
		    		}
		    		else
		    		{*/
		    			LazyDynaBean bean = (LazyDynaBean)unmmap.get(ab0110+ae0122);
		    			if(bean!=null)
			    		{
		    				if("1".equals(statflag))
			    	        	bean.set("count",rs.getString("a0100")+"");
		    				else
		    				{
		    					String totalMonth=(String)monthCount.get(ab0110+ae0122);
		    					if(totalMonth==null|| "".equals(totalMonth))
		    						totalMonth="1";
		    					String a=rs.getString("a0100");
		    					if(a==null|| "".equals(a))
		    						a="0";
		    					bean.set("count",Integer.parseInt(a)+"");///Integer.parseInt(totalMonth)   此处原来是除以几个月的，改为count(distinct a0100) zhaoxg 2014-12-12
		    				}
			    	    	bean.set("flag","0");
			         		list.add(bean);
			    		}
			    		/*count=1;
			    		e0122=ae0122;
			    		b0110=ab0110;*/
		    		/*}*/
				}
				/**显示单位数据*/
				else
				{
					String ab0110=rs.getString("b0110");
					/*if(init==0)
	    			{
	    				b0110=ab0110;
	    			}
	    			init++;
	    			if(b0110.equals(ab0110))
		    		{
		    			count++;
		    		}
		    		else
		    		{*/
		    			LazyDynaBean bean = (LazyDynaBean)unmmap.get(ab0110);
		    			if(bean!=null)
			    		{
		    				if("1".equals(statflag))
			    	        	bean.set("count",rs.getString("a0100")+"");
		    				else
		    				{
		    					String totalMonth=(String)monthCount.get(ab0110);
		    					if(totalMonth==null|| "".equals(totalMonth))
		    						totalMonth="1";
		    					String a=rs.getString("a0100");
		    					if(a==null|| "".equals(a))
		    						a="0";
		    					bean.set("count",Integer.parseInt(a)+"");///Integer.parseInt(totalMonth)  同上
		    				}
			    	    	bean.set("flag","0");
			         		list.add(bean);
			    		}
			    		/*count=1;
			    		b0110=ab0110;*/
		    		}
				}
			this.recordNums=list.size();
			rs.close();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String getCondSql(String condid)
	{
		String strwhere="";
		  try
		  {
			/**
			 * 表达式|因子
			 */
			String value=(String)condmap.get(condid);
			if(value==null|| "".equalsIgnoreCase(value))
				return "";
//			value = PubFunc.keyWord_reback(value);
			value = value.replaceAll("＊", "*");
			value = value.replaceAll("？", "?");
			int idx=0;
			idx=value.indexOf("|");
			String expr=value.substring(0, idx);
			String factor=value.substring(idx+1);
			FactorList factorlist=new FactorList(expr,factor,"");
			strwhere=factorlist.getSingleTableSqlExpression(this.getAnalyseTable());
		  }
		  catch(Exception ex)
		  {
			  ex.printStackTrace();
			  strwhere=" 1=2 ";
		  }
		  return strwhere;
	}
	/**
	 * 取得各部门的和
	 * @param sql
	 * @param headList
	 * @return
	 */
	public HashMap getUMMap(String sql,ArrayList headList,String isShowUnitData)
	{
		HashMap map =new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			LazyDynaBean abean= null;
			while(rs.next())
			{
				abean = new LazyDynaBean();
	    		for(int i = 0; i < headList.size();i++) 
	    		{
		     		LazyDynaBean bean = (LazyDynaBean) headList.get(i);
				    String itemid=(String)bean.get("itemid");
				    abean.set(itemid.toUpperCase(),rs.getString(itemid));
	    		}
	    		if("1".equals(isShowUnitData))
	    		{
	        		String b0110=rs.getString("b0110")==null?"":rs.getString("b0110");
	        		String e0122=rs.getString("e0122")==null?"":rs.getString("e0122");
	        		abean.set("b0110",b0110);
	        		String desc=AdminCode.getCodeName("UM",e0122);
					if(desc==null||desc.trim().length()==0)
						desc=AdminCode.getCodeName("UN",e0122); 
	        		abean.set("e0122",desc);
		    		map.put(b0110+e0122,abean);
	    		}
	    		else
	    		{
	    			String b0110=rs.getString("b0110")==null?"":rs.getString("b0110");
	    			abean.set("b0110",b0110);
	        		abean.set("e0122",AdminCode.getCodeName("UN",b0110));
	        		map.put(b0110,abean);
	    		}
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public Vector getCondVector(String rsid)
	{
		Vector vector=new Vector();
		try
		{
			condmap= new HashMap();
			String sql = "select salaryid,lprogram from salarytemplate where ";
			if("14".equals(rsid)|| "15".equals(rsid)|| "16".equals(rsid)|| "17".equals(rsid))
				sql+=" cstate='1'";//险种类别
			else
				sql+=" cstate ='' or cstate is null";
			ContentDAO dao = new ContentDAO(this.conn);
			LazyDynaBean abean=new LazyDynaBean();
			String str_path="/Params/Serive/SeiveItem";
			abean.set("value","all");
			abean.set("name", ResourceFactory.getProperty("label.gz.allman"));
			vector.addElement(abean);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				String lpro=rs.getString("lprogram");
				String salaryid = rs.getString("salaryid");
				if(!(lpro==null|| "".equalsIgnoreCase(lpro)))
				{
					SalaryLProgramBo lprgbo=new SalaryLProgramBo(lpro,this.view); //xieguiquan 增加参数this.view
					ArrayList templist=lprgbo.getServiceItemList();
					for(int i=0;i<templist.size();i++)
					{
						CommonData data=(CommonData)templist.get(i);
						abean=new LazyDynaBean();
						abean.set("value", data.getDataValue()+"-"+salaryid);
						abean.set("name", data.getDataName());
						vector.addElement(abean);
					}
					Document doc=PubFunc.generateDom(lpro);	
					XPath xpath=XPath.newInstance(str_path);
					List childlist=xpath.selectNodes(doc);
					Element element=null;
					if(childlist.size()!=0)
					{
						for(int i=0;i<childlist.size();i++)
						{
							element=(Element)childlist.get(i);
							condmap.put(element.getAttributeValue("ID")+"-"+salaryid, element.getAttributeValue("Expr")+"|"+element.getAttributeValue("Factor"));
						}//for end.
					}
		    	}
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vector;
	}
	/**
	 * 人员工资台帐:数据列表
	 * @param rsid
	 * @param rsdtlid
	 * @param pre
	 * @param salaryid
	 * @param year
	 * @param a0100
	 * @param fielditemid
	 * @param fielditemvalue
	 * @param tree_codeitemid
	 * @param tree_codesetid
	 * @param classFlag
	 * @param orderSql
	 * @param starttime
	 * @param endtime
	 * @param statflag
	 * @param role
	 * @param privCode
	 * @param privCodeValue
	 * @return
	 */
	public ArrayList getRecordList(String rsid, String rsdtlid, String pre,
			String salaryid, int year, String a0100, String fielditemid,
			String fielditemvalue, String tree_codeitemid,
			String tree_codesetid, String classFlag, String orderSql,
			String starttime, String endtime, String statflag,String role,String privCode,String privCodeValue,String condid,String zxgcombox,String zxgfrom,String zxgto,String TotalFlagCheckBox,String XiaJiFlagCheck) {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList headList = getTableHeadlist(rsdtlid, rsid, "","");
			StringBuffer buf = new StringBuffer("");
			StringBuffer sql = new StringBuffer();
			StringBuffer buf7 = new StringBuffer();
			String condsql = "";
			String dbSql = "";
			String salarySql = "";
			/**privSql前有个and*/
			String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,b_units);
			String privsql = this.getPrivSQL("",this.getAnalyseTable()+".",salaryid,tree_codesetid+tree_codeitemid);
			if ("5".equals(rsid) || "6".equals(rsid)|| "14".equals(rsid)|| "15".equals(rsid)) {
				for (int i = 0; i < headList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) headList.get(i);
					buf.append(" sum("+Sql_switcher.isnull((String) bean.get("itemid"), "0")+") as ");
					buf.append((String) bean.get("itemid"));
					buf.append(",");
				}
				if(buf.toString().length()>0)
			    	buf.setLength(buf.length() - 1);
				/**dbSql 和salarySql前都没有and*/
				dbSql = getDbSQL(pre,salaryid);
				salarySql = getSalarysetSQL(salaryid);
				sql.append("select ");
				if ("6".equals(rsid)|| "15".equals(rsid)) {
					sql.append(" count(distinct nbase"+Sql_switcher.concat()+"A0100) as a0100,");
				}
				sql.append(buf.toString());
				if(buf.toString().length()>0)
		    		sql.append(",");
				sql.append(Sql_switcher.month("A00Z0"));
				sql.append(" as aMonth ");
				sql.append(" from "+this.getAnalyseTable()+" where ");
				if((!"".equals(zxgcombox))&&(!"".equals(zxgfrom))&&(!"".equals(zxgto))){
					sql.append(" "+zxgcombox+" between "+zxgfrom+" and "+zxgto+" and ");
				}
				if (("5".equalsIgnoreCase(rsid)|| "14".equals(rsid))&&a0100.trim().length()>0) {
					Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
					String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
					String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
					if(!"0".equals(uniquenessvalid)&&onlyname!=null&&!"".equals(onlyname)){
						String[] pretemp=pre.split(",");
						for(int i=0;i<pretemp.length;i++){
							String sql1 = "select a0100 from "+pretemp[i]+"a01 where "+onlyname+"='"+a0100+"'";
							RowSet rs1 = dao.search(sql1);
							if(rs1.next()){
								a0100 = pretemp[i]+rs1.getString("a0100");
								break;
							}
						}

					}
//					else if(onlyname==null||onlyname.equals("")){
//						sql.append(" A0100='" + a0100.substring(3) + "' AND ");
//						sql.append(" UPPER(nbase) ='" + a0100.substring(0, 3).toUpperCase() + "' and ");
//					}else{
//						sql.append(""+onlyname+" = '"+a0100+"' and ");
//					}
					sql.append(" A0100='" + a0100.substring(3) + "'  and "+privSql+" and ");
					sql.append(" UPPER(nbase) ='" + a0100.substring(0, 3).toUpperCase() + "' and ");
				}
				if ("6".equalsIgnoreCase(rsid)|| "15".equals(rsid)) {
					if ("1".equals(classFlag)) {
						if("-1".equals(fielditemvalue))
						{
							sql.append("("+fielditemid+" is null or "+fielditemid+"='') and");
						}else
						{
							if("1".equals(XiaJiFlagCheck)){//是否包含下级单位，zhaoxg 2013-5-9
								sql.append(fielditemid + " like '");
								sql.append(fielditemvalue + "%' and");
							}else if("0".equals(XiaJiFlagCheck)){
								sql.append(fielditemid + " = '");
								sql.append(fielditemvalue + "' and");
							}
			    			
			    			
						}
						if(privSql!=null&&!"".equals(privSql))
						{
					    	sql.append(privSql);
					    	sql.append(" and ");
						}
					}else
					{
						if(privSql!=null&&!"".equals(privSql))
						{
		    	    		sql.append(privSql);
		    	    		sql.append(" and ");
						}
					}
				}
				if(dbSql!=null&&!"".equals(dbSql)){
					sql.append(dbSql);					
				}else{
					sql.append(" 1=1 ");
				}
				sql.append(" and ");
				sql.append(Sql_switcher.year("A00Z0"));
				sql.append("=" + year+" and ");
				sql.append(salarySql);
//				sql.append(" and "+privsql+" ");//有待商量
				sql.append(" group by ");
				sql.append(Sql_switcher.month("A00Z0"));
			} else if ("7".equals(rsid)|| "16".equals(rsid)) {
				/**查非数值指标值的sql*/
				StringBuffer buf_2 = new StringBuffer();
				/**非数值指标列*/
				StringBuffer buf_1 = new StringBuffer("");
				/**限制不是数值型指标，取时间范围内的最后一条记录*/
				StringBuffer buf_max= new StringBuffer();
				StringBuffer view_buf = new StringBuffer();
				boolean flag=false;
				for (int i = 0; i < headList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) headList.get(i);
					String itemtype=(String)bean.get("itemtype");
					String itemid=(String)bean.get("itemid");
					if("a0101".equalsIgnoreCase(itemid)|| "b0110".equalsIgnoreCase(itemid)|| "e0122".equalsIgnoreCase(itemid))
						continue;
					if("nbase".equalsIgnoreCase(itemid))
					{
						flag=true;
					}
					if("N".equalsIgnoreCase(itemtype))
					{
			    		/*buf.append(", sum(ISNULL(");
			    		buf.append((String) bean.get("itemid"));
			    		buf.append(",0)) as ");*/
						buf.append(", sum("+Sql_switcher.isnull((String) bean.get("itemid"), "0")+") as ");
			    		buf.append((String) bean.get("itemid"));
					}
					else
					{
						buf_1.append(",max("+itemid+") "+itemid);
					}
				}
				buf_max.append("select max(a00z0) from "+this.getAnalyseTable()+" where ");
				buf_2.append("select max(a0100) a0100");
				if(!flag)
					buf_2.append(",max(nbase) nbase ");
				buf_2.append(buf_1+" from "+this.getAnalyseTable()+" where ");
				view_buf.append(" select MAX("+Sql_switcher.isnull("b0110", "'UN'")+") as b0110,"+Sql_switcher.isnull("e0122", "'UM'")+" as e0122");
				view_buf.append(buf.toString());
				view_buf.append(",a0101,nbase,a0100,max(a0000) as a0000,max(dbid) as dbid ");
				view_buf.append(" from "+this.getAnalyseTable()+" where ");
				dbSql = getDbSQL(pre,salaryid);
				salarySql = getSalarysetSQL(salaryid);
				buf_2.append(dbSql);
				buf_max.append(dbSql);
				view_buf.append(dbSql);
				buf_2.append(" and "+salarySql);
				buf_max.append(" and "+salarySql);
				view_buf.append(" and "+salarySql);
				if(condid!=null&&!"all".equalsIgnoreCase(condid))
				{
					condsql = this.getCondSql(condid);
					if(condsql!=null&&!"".equals(condsql))
					{
						buf_2.append(" and (");
						buf_2.append(condsql);
						buf_2.append(") ");
						
						buf_max.append(" and (");
						buf_max.append(condsql);
						buf_max.append(") ");
						
						view_buf.append(" and (");
						view_buf.append(condsql);
						view_buf.append(") ");
					}
				}
				if(privSql!=null&&!"".equals(privSql))
				{
	    			view_buf.append(" and "+privSql);
	    			buf_2.append(" and "+privSql);
	    			buf_max.append(" and "+privSql);
				}
				if (!"-1".equals(tree_codeitemid)) {
					
					buf_2.append(" and "+privsql+"");
					buf_max.append(" and "+privsql+"");
					view_buf.append(" and "+privsql+"");
//					if (tree_codesetid.equalsIgnoreCase("UN")) {
//						buf_2.append(" and b0110 like '");
//						buf_2.append(tree_codeitemid);
//						buf_2.append("%' ");
//						
//						buf_max.append(" and b0110 like '");
//						buf_max.append(tree_codeitemid);
//						buf_max.append("%' ");
//						
//						view_buf.append(" and b0110 like '");
//						view_buf.append(tree_codeitemid);
//						view_buf.append("%' ");
//					} else if (tree_codesetid.equalsIgnoreCase("UM")) {
//						buf_2.append(" and e0122 like '");
//						buf_2.append(tree_codeitemid);
//						buf_2.append("%' ");
//						
//						buf_max.append(" and e0122 like '");
//						buf_max.append(tree_codeitemid);
//						buf_max.append("%' ");
//						
//						view_buf.append(" and e0122 like '");
//						view_buf.append(tree_codeitemid);
//						view_buf.append("%' ");
//					}
				}
				view_buf.append(" and ");
				buf_2.append(" and ");
				buf_max.append(" and ");
				if ("1".equals(statflag)) {
					buf_2.append(Sql_switcher.year("a00z0"));
					buf_2.append("=");
					buf_2.append(year);
					
					buf_max.append(Sql_switcher.year("a00z0"));
					buf_max.append("=");
					buf_max.append(year);
					
					view_buf.append(Sql_switcher.year("a00z0"));
					view_buf.append("=");
					view_buf.append(year);
				} else {
					String newStarttime = changeFormat(starttime, ".");
					String newEndtime = changeFormat(endtime, ".");
					PositionStatBo psb = new PositionStatBo(this.conn);
					buf_2.append(psb.getDateSql(">=", "a00z0",newStarttime));
					buf_2.append(" and ");
					buf_2.append(psb.getDateSql("<=","a00z0",newEndtime));
					
					buf_max.append(psb.getDateSql(">=", "a00z0",newStarttime));
					buf_max.append(" and ");
					buf_max.append(psb.getDateSql("<=","a00z0",newEndtime));
					
					view_buf.append(psb.getDateSql(">=", "a00z0",newStarttime));
					view_buf.append(" and ");
					view_buf.append(psb.getDateSql("<=","a00z0",newEndtime));
				}
				view_buf.append(" group by a0100,nbase,a0101,e0122 ");
				/**查询上面创建的视图，本次查询按单位分组*/
				sql.append("select a0101,");
				if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
					sql.append(" decode(grouping(b0110),1,'un_total',b0110) b0110,");
					sql.append(" decode(grouping(e0122),1,'um_total',e0122) e0122");
				} else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
					sql.append(" coalesce(e0122,'um_total') e0122,coalesce(b0110,'un_total') b0110");
				}
				sql.append(",a0100,nbase,a0000,dbid");
				sql.append(buf.toString());
				sql.append(" from ( ");
				sql.append(view_buf.toString()+") t group by b0110,e0122,a0100,a0000,nbase,a0101,dbid");
				//sql.append(") temp ");
//				buf_2.append(" and a00z0=("+buf_max+") group by a0100,nbase");
				buf_2.append(" group by a0100,nbase");
				buf7.append(" select a.*,b.* from ");
				buf7.append("("+sql.toString()+") a left join ("+buf_2.toString()+") b on a.a0100=b.a0100 and a.nbase=b.nbase");
				if((!"".equals(zxgcombox))&&(!"".equals(zxgfrom))&&(!"".equals(zxgto))){
					buf7.append(" where  a."+zxgcombox+" between "+zxgfrom+" and "+zxgto+"");
				}
				buf7.append(" order by");
				buf7.append(" b0110,e0122,");
				if(orderSql!=null&&!"".equals(orderSql))
					buf7.append(orderSql.substring(9));
				else
					buf7.append("dbid,a0000");
			}
			ArrayList un_um_list = null;
			if ("7".equalsIgnoreCase(rsid)|| "16".equals(rsid)) {
				if("1".equals(TotalFlagCheckBox)){//是否显示合计  默认不显示  zhaoxg 2013-5-9
					un_um_list = this.getUN_UM_Total(buf.toString(), dbSql,
							salarySql, rsid, rsdtlid, pre, salaryid, year, a0100,
							fielditemid, fielditemvalue, starttime, endtime,statflag,privSql,zxgcombox,zxgfrom,zxgto,condsql);
				}
			}
			RowSet rs = null;
			if("7".equals(rsid)|| "16".equals(rsid)){
				rs=dao.search(buf7.toString());
			}else
	    		rs = dao.search(sql.toString());
			//System.out.println(sql);
			String b0110 = "";
			String e0122 = "";
			int init = 0;
			int init2 = 0;
			SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
			while (rs.next()) {

				LazyDynaBean bean = new LazyDynaBean();

				if ("7".equalsIgnoreCase(rsid)|| "16".equals(rsid)) {
					if (init == 0 || init2 > 0) {
						b0110 = rs.getString("b0110");
						e0122 = rs.getString("e0122");
					}
					if (!"un_total".equalsIgnoreCase(b0110)) {
						init2 = 0;
						if (b0110.equalsIgnoreCase(rs.getString("b0110"))) {
							if (e0122.equalsIgnoreCase(rs.getString("e0122"))) {
							} else {
								if(un_um_list!=null){
									for (int i = 0; i < un_um_list.size(); i++) {
										LazyDynaBean temp_bean = (LazyDynaBean) un_um_list.get(i);
										String temp_e0122 = (String) temp_bean.get("e0122");
										String temp_b0110 = (String) temp_bean.get("b0110");
										String code = (String) temp_bean.get("code");
										if (temp_b0110.equalsIgnoreCase(b0110)&& temp_e0122.equalsIgnoreCase(e0122)&& "UM".equalsIgnoreCase(code)) {
											list.add(temp_bean);
										}
									}
								}

								e0122 = rs.getString("e0122");
							}
						} else {
							if(un_um_list!=null){
								for (int i = 0; i < un_um_list.size(); i++) {
									LazyDynaBean temp_bean = (LazyDynaBean) un_um_list.get(i);
									String temp_e0122 = (String) temp_bean.get("e0122");
									String temp_b0110 = (String) temp_bean.get("b0110");
									String code = (String) temp_bean.get("code");
									if (temp_b0110.equalsIgnoreCase(b0110)&& temp_e0122.equalsIgnoreCase(e0122)&& "UM".equalsIgnoreCase(code)) {
										list.add(temp_bean);
									}
								}
							}
							if(un_um_list!=null){
								for (int i = 0; i < un_um_list.size(); i++) {
									LazyDynaBean temp_bean = (LazyDynaBean) un_um_list.get(i);
									String temp_e0122 = (String) temp_bean.get("e0122");
									String temp_b0110 = (String) temp_bean.get("b0110");
									String code = (String) temp_bean.get("code");
									if (temp_b0110.equalsIgnoreCase(b0110)&& "um_total".equalsIgnoreCase(temp_e0122)&& "UN".equalsIgnoreCase(code)) {
										list.add(temp_bean);
									}
								}
							}
							b0110 = rs.getString("b0110");
							e0122 = rs.getString("e0122");
						}
						bean.set("a0101", rs.getString("a0101")==null?"":rs.getString("a0101"));
						for (int i = 0; i < headList.size(); i++) {
							LazyDynaBean abean = (LazyDynaBean) headList.get(i);
							String itemid = (String) abean.get("itemid");
							String itemtype=(String)abean.get("itemtype");
							String itemfmt=(String)abean.get("itemfmt");
							String codesetid=(String)abean.get("codesetid");
							if("a".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid))
							{
								String desc=AdminCode.getCodeName(codesetid, rs.getString(itemid));
								if("UM".equalsIgnoreCase(codesetid)&&(desc==null||desc.trim().length()==0))
									desc=AdminCode.getCodeName("UN", rs.getString(itemid)); 
								bean.set(itemid,desc);
							}
							else if("d".equalsIgnoreCase(itemtype))
							{
								format = new SimpleDateFormat(itemfmt);
								bean.set(itemid,rs.getDate(itemid)==null?"":format.format(rs.getDate(itemid)));
							}
							else
						    	bean.set(itemid, rs.getString(itemid)==null?"":rs.getString(itemid));
						}
						this.recordNums++;
						bean.set("flag", "0");// 显示序号
						init++;
						list.add(bean);
					} else {
						bean.set("a0101", rs.getString("a0101")==null?"":rs.getString("a0101"));
						for (int i = 0; i < headList.size(); i++) {
							LazyDynaBean abean = (LazyDynaBean) headList.get(i);
							String itemid = (String) abean.get("itemid");
							String itemtype=(String)abean.get("itemtype");
							String itemfmt=(String)abean.get("itemfmt");
							String codesetid=(String)abean.get("codesetid");
							if("a".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid))
							{
								String desc=AdminCode.getCodeName(codesetid, rs.getString(itemid));
								if("UM".equalsIgnoreCase(codesetid)&&(desc==null||desc.trim().length()==0))
									desc=AdminCode.getCodeName("UN", rs.getString(itemid)); 
								bean.set(itemid,desc);
							}
							else if("d".equalsIgnoreCase(itemtype))
							{
								format = new SimpleDateFormat(itemfmt);
								bean.set(itemid,rs.getDate(itemid)==null?"":format.format(rs.getDate(itemid)));
							}
							else
						    	bean.set(itemid, rs.getString(itemid));
						}
						bean.set("flag", "0");// 显示序号
						init++;
						list.add(bean);
						
			    		bean = new LazyDynaBean();
			    		bean.set("a0101", "总计");
			    		for (int i = 0; i < headList.size(); i++) {
				    		LazyDynaBean abean = (LazyDynaBean) headList.get(i);
				    		String itemid = (String) abean.get("itemid");
				    		String itemtype=(String)abean.get("itemtype");
							String itemfmt=(String)abean.get("itemfmt");
							String codesetid=(String)abean.get("codesetid");
							if("a".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid))
							{
								String desc=AdminCode.getCodeName(codesetid, rs.getString(itemid));
								if("UM".equalsIgnoreCase(codesetid)&&(desc==null||desc.trim().length()==0))
									desc=AdminCode.getCodeName("UN", rs.getString(itemid)); 
								bean.set(itemid,desc);
							}
							else if("d".equalsIgnoreCase(itemtype))
							{
								format = new SimpleDateFormat(itemfmt);
								bean.set(itemid,rs.getDate(itemid)==null?"":format.format(rs.getDate(itemid)));
							}
							else
						    	bean.set(itemid, rs.getString(itemid));
				    	}
						 bean.set("flag", "1");// 显示序号
						// init++;
						init2++;
				    	list.add(bean);
					}
				} else if ("5".equalsIgnoreCase(rsid)|| "6".equalsIgnoreCase(rsid)|| "14".equals(rsid)|| "15".equals(rsid)) {
					for (int j = 0; j < headList.size(); j++) {
						LazyDynaBean abean = (LazyDynaBean) headList.get(j);
						if ("6".equals(rsid)|| "15".equals(rsid)) {
							bean.set((String) abean.get("itemid"), String.valueOf(rs.getDouble((String) abean.get("itemid"))));
						} else {
							bean.set((String) abean.get("itemid"),rs.getString((String) abean.get("itemid")) == null ? "": rs.getString((String) abean.get("itemid")));
						}
					}

					if ("6".equals(rsid)|| "15".equals(rsid)) {
						bean.set("personcount", rs.getString("a0100"));
					}
					if ("5".equals(rsid) || "6".equals(rsid)|| "14".equals(rsid)|| "15".equals(rsid)) {
						bean.set("amonth", rs.getString("amonth"));
					}
					list.add(bean);
				}
			}
			rs.close();
			if ("7".equalsIgnoreCase(rsid)|| "16".equals(rsid)) {
				if(un_um_list!=null){
					for (int i = 0; i < un_um_list.size(); i++) {
						LazyDynaBean temp_bean = (LazyDynaBean) un_um_list.get(i);
						String temp_e0122 = (String) temp_bean.get("e0122");
						String temp_b0110 = (String) temp_bean.get("b0110");
						String code = (String) temp_bean.get("code");
						if (temp_b0110.equalsIgnoreCase(b0110)&& temp_e0122.equalsIgnoreCase(e0122)&& "UM".equalsIgnoreCase(code)) {
							list.add(temp_bean);
						}
					}
				}
				if(un_um_list!=null){
					for (int i = 0; i < un_um_list.size(); i++) {
						LazyDynaBean temp_bean = (LazyDynaBean) un_um_list.get(i);
						String temp_e0122 = (String) temp_bean.get("e0122");
						String temp_b0110 = (String) temp_bean.get("b0110");
						String code = (String) temp_bean.get("code");
						if (temp_b0110.equalsIgnoreCase(b0110)&& "um_total".equalsIgnoreCase(temp_e0122)&& "UN".equalsIgnoreCase(code)) {
							list.add(temp_bean);
						}
					}
				}
			}
			if("7".equals(rsid)&& "1".equals(TotalFlagCheckBox))//选择部门节点时如果显示了合计行，那么去掉最后一行的总计行  zhaoxg add 2013-12-23 建研院需求遗留bug
			{
				if(list.size()>0)
				{
					if("UM".equalsIgnoreCase(tree_codesetid))
					{
						list.remove(list.size()-1);
					}
				}
			}
			if("5".equals(rsid)|| "6".equals(rsid)|| "14".equals(rsid)|| "15".equals(rsid))
				this.recordNums=5;
			/*for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)list.get(i);
				System.out.println("count===="+bean.get("personcount"));
				for (int j = 0; j < headList.size(); j++) {
					LazyDynaBean abean = (LazyDynaBean) headList.get(j);
					System.out.println(bean.get((String)abean.get("itemid")));
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 求分析项目每个月所有人的总合
	 * 
	 * @param itemid
	 * @param year
	 * @param dbSql
	 * @param salarySql
	 * @return
	 */
	public HashMap getTotalProject(String itemid, String year, String dbSql,
			String salarySql,String tree_codeitemid,String tree_codesetid,String role,String privCode,String privCodeValue,String privsql,String salaryid) {
		HashMap map = new HashMap();
		try {
			StringBuffer buf = new StringBuffer();
			String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,b_units);
			buf.append("select sum(");
			buf.append(itemid);
			buf.append(") as ");
			buf.append(itemid);
			buf.append(",");
			buf.append(Sql_switcher.month("a00z0"));
			buf.append(" as amonth from "+this.getAnalyseTable()+" where ");
			buf.append(Sql_switcher.year("a00z0"));
			buf.append("=");
			buf.append(year);
			buf.append(" and ");
			buf.append(dbSql);
			buf.append(" and ");
			buf.append(salarySql);
			if(privSql!=null&&!"".equals(privSql))
	    		buf.append(" and "+privSql);
			if (!"-1".equals(tree_codeitemid)) {
				buf.append(" and "+privsql+"");
//	    		if (tree_codesetid.equalsIgnoreCase("UN")) {
//	    			buf.append(" and b0110 like '");
//	    			buf.append(tree_codeitemid);
//	    			buf.append("%' ");
//	     		} else if (tree_codesetid.equalsIgnoreCase("UM")) {
//	     			buf.append(" and e0122 like '");
//	     			buf.append(tree_codeitemid);
//	     			buf.append("%' ");
//    			}
    		}
			if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
				buf.append(" group by rollup(" + Sql_switcher.month("a00z0")+ ")");
			} else {
				buf.append(" group by " + Sql_switcher.month("a00z0")+ " with rollup ");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			String all="";
			rs = dao.search(buf.toString());
			while (rs.next()) {
				if (rs.getString("amonth") == null)
					continue;
				map.put(rs.getString("amonth"), rs.getString(itemid));
		    	all=GzAnalyseBo.add(all,rs.getString(itemid),2);
			}
			rs.close();
			map.put("13", all);
			map.put("a0101", "总计");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 求分析项目每个月所有人的总合
	 * 
	 * @param itemid
	 * @param year
	 * @param dbSql
	 * @param salarySql
	 * @return
	 */
	public HashMap getTotalProject(String tablename,String itemid) {
		HashMap map = new HashMap();
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("select sum(");
			buf.append(itemid);
			buf.append(") as ");
			buf.append(itemid);
			buf.append(",");
			buf.append("temp, amonth from "+tablename+" ");

			if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
				buf.append(" group by rollup(amonth,temp)");
			} else {
				buf.append(" group by amonth,temp  with rollup ");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			HashMap _map = new HashMap();
			while (rs.next()) {
				String aa=rs.getString("amonth");
				String bb=rs.getString("temp")==null?"all":rs.getString("temp");
				if (rs.getString("amonth") == null)
					continue;
				map.put(aa+"|"+bb, rs.getString(itemid));
				_map.put(bb, GzAnalyseBo.add((String) (_map.get(bb)==null?"":_map.get(bb)),rs.getString(itemid), 2));
			}
			Iterator iter = _map.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Entry entry = (Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				map.put("13|"+key, val);
			}
			rs.close();
			map.put("a0101", "总计");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public HashMap getTotalProjectLevel(String itemid, String year, String dbSql,
			String salarySql,String tree_codeitemid,String tree_codesetid,String role,String privCode,String privCodeValue,String cond,String level,String tablename,String groupitemid,String privsql,String salaryid) {
		HashMap map = new HashMap();
		try {
			StringBuffer buf = new StringBuffer();
			String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,b_units);
			buf.append("select sum(");
			buf.append(itemid);
			buf.append(") as ");
			buf.append(itemid);
			buf.append(",count(a0100) as total");
			buf.append(",");
			buf.append(Sql_switcher.month("a00z0"));
			buf.append(" as amonth from "+this.getAnalyseTable());
			//buf.append(" left join "+tablename+" on "+this.getAnalyseTable()+"."+groupitemid+"="+tablename+".codeitemid ");
			buf.append(" where ");
			//buf.append(tablename+".layer<="+level+" and ");
			buf.append(Sql_switcher.year("a00z0"));
			buf.append("=");
			buf.append(year);
			buf.append(" and ");
			buf.append(dbSql);
			buf.append(" and ");
			buf.append(salarySql);
			if(privSql!=null&&!"".equals(privSql))
	    		buf.append(" and "+privSql);
			if (!"-1".equals(tree_codeitemid)) {
				buf.append(" and "+privsql+"");
//	    		if (tree_codesetid.equalsIgnoreCase("UN")) {
//	    			buf.append(" and b0110 like '");
//	    			buf.append(tree_codeitemid);
//	    			buf.append("%' ");
//	     		} else if (tree_codesetid.equalsIgnoreCase("UM")) {
//	     			buf.append(" and e0122 like '");
//	     			buf.append(tree_codeitemid);
//	     			buf.append("%' ");
//    			}
    		}
			if(cond!=null&&cond.trim().length()>0)
				buf.append(" and ("+cond+")");
			if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
				buf.append(" group by rollup(" + Sql_switcher.month("a00z0")+ ")");
			} else {
				buf.append(" group by " + Sql_switcher.month("a00z0")+ " with rollup ");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			int i=0;
			String all="";
			while (rs.next()) {
				if (rs.getString("amonth") == null)
				{
					map.put("total", rs.getString("total")==null?"0":rs.getString("total"));
					map.put("amounttotal", rs.getString(itemid)==null?"0":rs.getString(itemid));
				}else{
					i++;
			    	map.put(rs.getString("amonth"), rs.getString(itemid));
			    	map.put(rs.getString("amonth")+"p", rs.getString("total")==null?"0":rs.getString("total"));
			    	all=GzAnalyseBo.add(all,rs.getString(itemid),2);
				}
			}
			rs.close();
			map.put("13", all);
			map.put("a0101", "总计");
			map.put("totalmonth", i+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 求给定的分析项目每个人的各月的总和
	 * 
	 * @param itemid
	 * @param year
	 * @return
	 */
	public HashMap getSumProject(String itemid, String year, String dbSql,
			String salarySql,String tree_codeitemid,String tree_codesetid,String role,String privCode,String privCodeValue,String privsql,String salaryid) {
		HashMap map = new HashMap();
		try {
			StringBuffer buf = new StringBuffer();
			String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,b_units);

			buf.append("select a0100,nbase,b0110,e0122,sum(");
			buf.append(itemid);
			buf.append(") as ");
			buf.append(itemid);
			buf.append(" from "+this.getAnalyseTable()+" where ");
			buf.append(Sql_switcher.year("a00z0") + "=");
			buf.append(year);
			buf.append(" and ");
			buf.append(dbSql);
			buf.append(" and ");
			buf.append(salarySql);
			if(privSql!=null&&!"".equals(privSql))
	    		buf.append(" and "+privSql);
			if (!"-1".equals(tree_codeitemid)) {
				buf.append(" and "+privsql+"");
//	    		if (tree_codesetid.equalsIgnoreCase("UN")) {
//	    			buf.append(" and b0110 like '");
//	    			buf.append(tree_codeitemid);
//	    			buf.append("%' ");
//	     		} else if (tree_codesetid.equalsIgnoreCase("UM")) {
//	     			buf.append(" and e0122 like '");
//		    		buf.append(tree_codeitemid);
//		    		buf.append("%' ");
//    			}
    		}
			buf.append(" group by a0100,a00z0,nbase,b0110,e0122,a0101");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			while (rs.next()) {
				String dbname = rs.getString("nbase").toLowerCase();
				String a0100 = rs.getString("a0100");
				String b0110=rs.getString("b0110")==null?"":rs.getString("b0110");
				String e0122=rs.getString("e0122")==null|| "".equals(rs.getString("e0122").trim())?"":rs.getString("e0122");
				String sum = rs.getString(itemid)==null?"0":rs.getString(itemid);
				if(map.get((a0100 + dbname+b0110+e0122).toUpperCase())!=null)
				{
					String dd=(String)map.get((a0100 + dbname+b0110+e0122).toUpperCase());
					BigDecimal bd = new BigDecimal(dd);
					map.put((a0100 + dbname+b0110+e0122).toUpperCase(), bd.add(new BigDecimal(sum)).toString());
				}
				else
				{
				   map.put((a0100 + dbname+b0110+e0122).toUpperCase(), sum);
				}
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap getMap(String itemid, String value, HashMap t_map) {
		HashMap map = t_map;
		try {
			if (map.get(itemid.toLowerCase()) != null) {

				String f = (String) map.get(itemid.toLowerCase());
				BigDecimal b1 = new BigDecimal(f);
				BigDecimal b2 = new BigDecimal(value);
				BigDecimal s = b1.add(b2);
				BigDecimal one = new BigDecimal("1");
				// double
				// newf=b1.divide(b2,2,BigDecimal.ROUND_HALF_UP).doubleValue();
				String newf = s.divide(one, 2, BigDecimal.ROUND_HALF_UP)
						.toString();
				map.remove(itemid.toLowerCase());
				map.put(itemid.toLowerCase(), newf);
			} else {
				map.put(itemid.toLowerCase(), value);
			}
		} catch (Exception e) {

		}
		return map;
	}

	/**
	 * SQL语句人员库限制部分（高级授权）
	 * 
	 * @param pre
	 * @return
	 */
	public String getDbSQL(String pre,String salaryid) {
		StringBuffer dbSql = new StringBuffer();
//		boolean isBhighPriv=view.isBhighPriv();//是否定义了高级权限
		try 
		{
			dbSql.append(" 1=1 ");//废掉此方法，全走操作单位  zhaoxg update 2014-12-4
			if(pre!=null&&pre.length()>0){
				if (pre.indexOf(",")==-1) {
					dbSql.append(" and (upper("+this.getAnalyseTable()+".nbase)='");
			    	dbSql.append(pre.toUpperCase()+"')");
				}else{
					String[] temp = pre.split(",");
					for (int i = 0; i < temp.length; i++) {
			    		if (i == 0) {
				    		dbSql.append(" and (");
				    	}
	   			    	dbSql.append("upper(nbase)='");
			     		dbSql.append(temp[i].toUpperCase()+"'");
			     		if (i != temp.length - 1) {
		    		    	   dbSql.append(" OR ");
	    		    		} else
	     				    	dbSql.append(")");
					}
				}
			}
//			//采用下面这种方式，防止有人员移库现象导致的看不见这个人问题
//			if(pre!=null&&pre.length()>0)
//			{
//		    	if (pre.indexOf(",") == -1) {
//
//		    		/**加入高级授权*/
//		    		StringBuffer sql = new StringBuffer("");
//					String priStrSql = InfoUtils.getWhereINSql(view, pre);
//					sql.append("select "+pre+"a01.A0100 ");
//					if (priStrSql.length() > 0)
//						sql.append(priStrSql);
//					else
//						sql.append(" from "+pre+"a01");
//					
//					String sql1=""+this.getAnalyseTable()+".A0100 in ("+sql+")";
//		    		if(view.isSuper_admin()||view.getGroupId().equals("1"))
//		    		{
//		    			dbSql.append("(upper("+this.getAnalyseTable()+".nbase)='");
//	    		    	dbSql.append(pre.toUpperCase()+"')");
//		    		}
//		    		else
//		    		{
//		    			if(isBhighPriv){
//			    			dbSql.append("(upper("+this.getAnalyseTable()+".nbase)='");
//		    		    	dbSql.append(pre.toUpperCase()+"'");		    		    	
//			    			dbSql.append(" and "+sql1+")");//走高级了   zhaoxg 2013-7-18
//		    			}else{
//
//		    				StringBuffer _sql=new StringBuffer();
//		    				String b_units=this.view.getUnitIdByBusi("1");
//							if(b_units!=null&&b_units.length()>0&&!b_units.equalsIgnoreCase("UN")) //模块操作单位
//							{
//								String unitarr[] =b_units.split("`");
//			    				for(int i=0;i<unitarr.length;i++)
//			    				{
//				    				String codeid=unitarr[i];
//				    				if(codeid==null||codeid.equals(""))
//				    					continue;
//					    			if(codeid!=null&&codeid.trim().length()>2)
//				    				{
//
//
//					    			//-----
//					    			String b0110_item="b0110";
//									String e0122_item="e0122";
//									
//									if (i == 0) {
////										_sql.append("(");
//									}else{
//										_sql.append(" or ");
//									}
//									
//										String[] temp = salaryid.split(",");
//										for (int j = 0; j < temp.length; j++) {
//											SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(temp[j])); 
//											String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
//											String deptid =ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
//											if(orgid!=null&&orgid.trim().length()>0)
//											{ 
//												 b0110_item=orgid;
//												if(deptid!=null&&deptid.trim().length()>0)
//													e0122_item=deptid;
//												else
//													e0122_item="";  
//											}
//											else if(deptid!=null&&deptid.trim().length()>0)
//											{ 
//												e0122_item=deptid;
//												b0110_item="";
//											}
//											  String codesetid = codeid.substring(0,2);
//											  String value = codeid.substring(2);
//											   
//
//											_sql.append("("+this.getAnalyseTable()+".salaryid=");
//											_sql.append(temp[j]);
//											
//											
//											 if (codesetid.equalsIgnoreCase("UN"))
//											    {
//											    	
//											    	if(b0110_item.length()>0)	
//											    	{
//											    		_sql.append(" and ("+this.getAnalyseTable()+"."+b0110_item+" like '");
//											    		_sql.append(value);
//											    		_sql.append("%'");
//														if (value.equalsIgnoreCase(""))
//														{
//															_sql.append(" or "+this.getAnalyseTable()+"."+b0110_item+" is null");
//														}
//														_sql.append(")");
//											    	}
//											    	else 
//											    	{
//											    		_sql.append(" and ("+this.getAnalyseTable()+"."+e0122_item+" like '");
//											    		_sql.append(value);
//											    		_sql.append("%'");
//														if (value.equalsIgnoreCase(""))
//														{
//															_sql.append(" or "+this.getAnalyseTable()+"."+e0122_item+" is null");
//														}
//														_sql.append(")");
//											    	}
//											    }
//											    if (codesetid.equalsIgnoreCase("UM"))
//											    {
//											    	if(e0122_item.length()>0)	
//											    	{
//											    		_sql.append(" and ("+this.getAnalyseTable()+"."+e0122_item+" like '");
//											    		_sql.append(value);
//											    		_sql.append("%')");
//											    	}
//											    }
//											
//											    _sql.append(")");
//											if (j != temp.length - 1){
//												_sql.append(" OR ");
//											}											
//
//										}
//					    			//-----
////										_sql.append(")");
//				                 	}
//					    			else if(codeid!=null&&codeid.equalsIgnoreCase("UN"))
//					    			{
//					    				_sql.append("  1=1 ");
//				                 	}	
//					    		}
//								
//							}
////							System.out.println(_sql);
//			    			dbSql.append("(upper("+this.getAnalyseTable()+".nbase)='");
//		    		    	dbSql.append(pre.toUpperCase()+"'");		    		    	
//			    			dbSql.append(" and ("+_sql+"))");	    			
//    					}
//		    		}
//	    		 
//    			}
//    			else
//    			{
//    				if(view.isSuper_admin()||view.getGroupId().equals("1"))
//		    		{
//    					String[] temp = pre.split(",");
//    					for (int i = 0; i < temp.length; i++) {
//    			    		if (i == 0) {
//    				    		dbSql.append("(");
//	    			    	}
//   	    			    	dbSql.append("upper(nbase)='");
//	    		     		dbSql.append(temp[i].toUpperCase()+"'");
//	    		     		if (i != temp.length - 1) {
//			    		    	   dbSql.append(" OR ");
//		    		    		} else
//		     				    	dbSql.append(")");
//    					}
//		    		}
//    				else
//    				{
//    					if(isBhighPriv){
//    	        			String[] temp = pre.split(",");
//        		    		for (int i = 0; i < temp.length; i++) {
//        			    		if (i == 0) {
//        				    		dbSql.append("(");
//    	    			    	}
//        			    		StringBuffer sql = new StringBuffer("");
//        						String priStrSql = InfoUtils.getWhereINSql(view, temp[i]);
//        						sql.append("select "+temp[i]+"a01.A0100 ");
//        						if (priStrSql.length() > 0)
//        							sql.append(priStrSql);
//        						else
//        							sql.append(" from "+temp[i]+"a01");
//        						String sql1=""+this.getAnalyseTable()+".A0100 in ("+sql+")";
//       	    			    	dbSql.append("(upper(nbase)='");
//    	    		     		dbSql.append(temp[i].toUpperCase()+"'");
//    	    		     		dbSql.append(" and "+sql1+")");
//    	    		    		if (i != temp.length - 1) {
//    		    		    	   dbSql.append(" OR ");
//    	    		    		} else
//    	     				    	dbSql.append(")");
//        		    		}
//    					}else{
//
//		    				StringBuffer _sql=new StringBuffer();
//		    				String b_units=this.view.getUnitIdByBusi("1");
//							if(b_units!=null&&b_units.length()>0&&!b_units.equalsIgnoreCase("UN")) //模块操作单位
//							{
//								String unitarr[] =b_units.split("`");
//			    				for(int i=0;i<unitarr.length;i++)
//			    				{
//				    				String codeid=unitarr[i];
//				    				if(codeid==null||codeid.equals(""))
//				    					continue;
//					    			if(codeid!=null&&codeid.trim().length()>2)
//				    				{
//
//
//					    			//-----
//					    			String b0110_item="b0110";
//									String e0122_item="e0122";
//									
//									if (i == 0) {
////										_sql.append("(");
//									}else{
//										_sql.append(" or ");
//									}
//									
//										String[] temp = salaryid.split(",");
//										for (int j = 0; j < temp.length; j++) {
//											SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(temp[j])); 
//											String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
//											String deptid =ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
//											if(orgid!=null&&orgid.trim().length()>0)
//											{ 
//												 b0110_item=orgid;
//												if(deptid!=null&&deptid.trim().length()>0)
//													e0122_item=deptid;
//												else
//													e0122_item="";  
//											}
//											else if(deptid!=null&&deptid.trim().length()>0)
//											{ 
//												e0122_item=deptid;
//												b0110_item="";
//											}
//											  String codesetid = codeid.substring(0,2);
//											  String value = codeid.substring(2);
//											   
//
//											_sql.append("("+this.getAnalyseTable()+".salaryid=");
//											_sql.append(temp[j]);
//											
//											
//											 if (codesetid.equalsIgnoreCase("UN"))
//											    {
//											    	
//											    	if(b0110_item.length()>0)	
//											    	{
//											    		_sql.append(" and ("+this.getAnalyseTable()+"."+b0110_item+" like '");
//											    		_sql.append(value);
//											    		_sql.append("%'");
//														if (value.equalsIgnoreCase(""))
//														{
//															_sql.append(" or "+this.getAnalyseTable()+"."+b0110_item+" is null");
//														}
//														_sql.append(")");
//											    	}
//											    	else 
//											    	{
//											    		_sql.append(" and ("+this.getAnalyseTable()+"."+e0122_item+" like '");
//											    		_sql.append(value);
//											    		_sql.append("%'");
//														if (value.equalsIgnoreCase(""))
//														{
//															_sql.append(" or "+this.getAnalyseTable()+"."+e0122_item+" is null");
//														}
//														_sql.append(")");
//											    	}
//											    }
//											    if (codesetid.equalsIgnoreCase("UM"))
//											    {
//											    	if(e0122_item.length()>0)	
//											    	{
//											    		_sql.append(" and ("+this.getAnalyseTable()+"."+e0122_item+" like '");
//											    		_sql.append(value);
//											    		_sql.append("%')");
//											    	}
//											    }
//											
//											    _sql.append(")");
//											if (j != temp.length - 1){
//												_sql.append(" OR ");
//											}											
//
//										}
//					    			//-----
////										_sql.append(")");
//				                 	}
//					    			else if(codeid!=null&&codeid.equalsIgnoreCase("UN"))
//					    			{
//					    				_sql.append("  1=1 ");
//				                 	}	
//					    		}
//								
//							}
////							System.out.println(_sql);
//    	        			String[] temp = pre.split(",");
//        		    		for (int i = 0; i < temp.length; i++) {
//        			    		if (i == 0) {
//        				    		dbSql.append("((");
//    	    			    	}
//       	    			    	dbSql.append("upper(nbase)='");
//    	    		     		dbSql.append(temp[i].toUpperCase()+"'");
//    	    		    		if (i != temp.length - 1) {
//    		    		    	   dbSql.append(" OR ");
//    	    		    		} else
//    	     				    	dbSql.append(")");
//        		    		}	    		    	
//			    			dbSql.append(" and ("+_sql+"))");	    			
//    					}
//
//    	    		}
//    			}
//			}
//			else
//			{
//				dbSql.append(" 1=2 ");
//			}
			if("3".equals(this.spFlag)){
				//System.out.println("包含审批");
            }else if("1".equals(this.spFlag)){  // 
                dbSql.append(" and "+getAnalyseTable()+".sp_flag = '06'");  // 支持表别名
			}else if(this.getAnalyseTable()!=null&& "salaryhistory".equalsIgnoreCase(this.getAnalyseTable())){
				dbSql.append(" and salaryhistory.sp_flag = '06'");//06:结束
				//System.out.println("不包含审批");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbSql.toString();
	}

	public String getSalarysetSQL(String salaryid) {
		StringBuffer salarySQL = new StringBuffer();
		try {
			if (salaryid.indexOf(",") == -1) {
				salarySQL.append("( salaryid=");
				salarySQL.append(salaryid);
				salarySQL.append(")");
			} else {
				String[] temp = salaryid.split(",");
				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						salarySQL.append("(");
					}
					salarySQL.append("salaryid=");
					salarySQL.append(temp[i]);
					if (i != temp.length - 1) {
						salarySQL.append(" OR ");
					} else
						salarySQL.append(")");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salarySQL.toString();
	}
	/**
	 * 根据薪资类别以及是否用归属单位做权限限制 
	 * @param salaryid
	 * @return
	 */
	public String getSalaryIdSQL(String salaryid,String tree_codeitemid,String tree_codesetid) {
		StringBuffer salarySQL = new StringBuffer();
		try {
			String b0110_item="b0110";
			String e0122_item="e0122";

				String[] temp = salaryid.split(",");
				for (int i = 0; i < temp.length; i++) {
					SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(temp[i])); 
					String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
					String deptid =ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
					if(orgid!=null&&orgid.trim().length()>0)
					{ 
						 b0110_item=orgid;
						if(deptid!=null&&deptid.trim().length()>0)
							e0122_item=deptid;
						else
							e0122_item="";  
					}
					else if(deptid!=null&&deptid.trim().length()>0)
					{ 
						e0122_item=deptid;
						b0110_item="";
					}
					  String codesetid = tree_codesetid;
					  String value = tree_codeitemid;
					   
					if (i == 0) {
						salarySQL.append("(");
					}
					salarySQL.append("("+this.getAnalyseTable()+".salaryid=");
					salarySQL.append(temp[i]);
					
					
					 if ("UN".equalsIgnoreCase(codesetid))
					    {
					    	
					    	if(b0110_item.length()>0)	
					    	{
					    		salarySQL.append(" and ("+this.getAnalyseTable()+"."+b0110_item+" like '");
								salarySQL.append(value);
								salarySQL.append("%'");
								if ("".equalsIgnoreCase(value))
								{
									salarySQL.append(" or "+this.getAnalyseTable()+"."+b0110_item+" is null");
								}
								salarySQL.append(")");
					    	}
					    	else 
					    	{
					    		salarySQL.append(" and ("+this.getAnalyseTable()+"."+e0122_item+" like '");
					    		salarySQL.append(value);
								salarySQL.append("%'");
								if ("".equalsIgnoreCase(value))
								{
									salarySQL.append(" or "+this.getAnalyseTable()+"."+e0122_item+" is null");
								}
								salarySQL.append(")");
					    	}
					    }
					    if ("UM".equalsIgnoreCase(codesetid))
					    {
					    	if(e0122_item.length()>0)	
					    	{
					    		salarySQL.append(" and ("+this.getAnalyseTable()+"."+e0122_item+" like '");
								salarySQL.append(value);
								salarySQL.append("%')");
					    	}
					    }
					
					    salarySQL.append(")");
					if (i != temp.length - 1) {
						salarySQL.append(" OR ");
					} else
						salarySQL.append(")");
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salarySQL.toString();
	}
	/**
	 * 人员工资台帐:人员列表
	 * 
	 * @param pre
	 * @param year
	 * @return
	 */
	public Vector getPersonList(String pre, String year,String role,String privCode,String privCodeValue,String salaryid,String tree_codeitemid,String tree_codeset) {
		Vector vector = new Vector();
		try {
              /**
               * select nbase,a0100,max(a0101) a0101,max(e0122) e0122,max(a0000) a0000 ,max(a00z0) a00z0 from SalaryHistory 
where YEAR(A00Z0)=2008 and (upper(nbase)='USR') and (salaryid=24 OR salaryid=18) group by nbase,a0100
               */
			String[] pretemp = pre.split(",");
			for(int i=0;i<pretemp.length;i++){
				pre = pretemp[i];

			StringBuffer buf = new StringBuffer();
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
			String only = ","+pre+"a01."+onlyname+"";
			String only1 = ","+onlyname+"";
			if(onlyname==null|| "".equals(onlyname)|| "a0100".equals(onlyname)|| "a0101".equals(onlyname)|| "e0122".equals(onlyname)|| "a0000".equals(onlyname)){
				only = "";
				only1 = "";
			}
			String dbSql = this.getDbSQL(pre,salaryid);
			String salarySql = this.getSalarysetSQL(salaryid);
			buf.append("select DISTINCT nbase,a0100,a0101,e0122,a0000"+only1+",dbid from ");
			buf.append("(select nbase,"+this.getAnalyseTable()+".a0100,max("+this.getAnalyseTable()+".a0101) a0101,max("+this.getAnalyseTable()+".e0122) e0122,max("+this.getAnalyseTable()+".a0000) a0000"+only+",max(dbid) as dbid from  "+this.getAnalyseTable());
			buf.append(" LEFT join "+pre+"a01 on "+this.getAnalyseTable()+".A0100="+pre+"a01.A0100 where upper(nbase)='"+pre.toUpperCase()+"' and ");
			buf.append(Sql_switcher.year("A00Z0"));
			buf.append("=");
			buf.append(year);
			buf.append(" and ");
			buf.append(dbSql);
			buf.append(" and ");
			buf.append(salarySql);
			if("1".equals(role))
			{
			}else
			{
	    		String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
	    		String sql = this.getPrivSQL(role, this.getAnalyseTable()+".", salaryid,b_units);
	    		buf.append(" and "+sql);
//				if(privCode!=null&&!privCode.equals(""))
//				{
//					String privSql=this.getPrivSQL(role,this.getAnalyseTable()+".",salaryid,privCode+privCodeValue);
//					buf.append(" and "+privSql);
//				}
//				else
//				{
//					buf.append( " and 1=2 ");
//				}
			}
			if(!"-1".equals(tree_codeitemid))
			{
				String privSql=this.getPrivSQL("",this.getAnalyseTable()+".",salaryid,tree_codeset+tree_codeitemid);
				buf.append(" and "+privSql);
//				if(tree_codeset.equalsIgnoreCase("UN"))
//				{
//					buf.append(" and "+this.getAnalyseTable()+".b0110 like '"+tree_codeitemid+"%'");
//				}
//				if(tree_codeset.equalsIgnoreCase("UM"))
//				{
//					buf.append(" and "+this.getAnalyseTable()+".e0122 like '"+tree_codeitemid+"%'");
//				}
			}
			buf.append("  group by nbase,"+this.getAnalyseTable()+".a0100"+only+" )");
			buf.append(" s ");
			
			/*String order_str=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER);
			if(order_str!=null&&order_str.trim().length()>0)
				buf.append(" order by "+order_str);
			else*/
			buf.append(" order by e0122,dbid,a0000");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("value", rs.getString("nbase")+ rs.getString("a0100"));
				bean.set("name", AdminCode.getCodeName("UM", rs.getString("e0122") == null ? "" : rs.getString("e0122"))+ ":" + rs.getString("a0101"));
				if("0".equals(uniquenessvalid)){
					bean.set("zxgflag", "no");
				}else if(onlyname==null|| "".equals(onlyname)){
					bean.set("zxgflag", "no");
				}else if(rs.getString(onlyname)==null|| "".equals(rs.getString(onlyname))){
					bean.set("zxgflag", "");
				}else{
					bean.set("zxgflag", rs.getString(onlyname));
				}
				
				vector.addElement(bean);
			}
			rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vector;
	}

	/**
	 * 统计分析图的各指标
	 * 
	 * @return
	 */
	public Vector getJFCChartItemVector(String rsdtlid) {
		Vector vector = new Vector();
		try {
			String sql = "select itemid,itemdesc from reportitem where rsdtlid="+ rsdtlid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("value", rs.getString("itemid"));
				bean.set("name", rs.getString("itemdesc"));
				vector.addElement(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vector;
	}

	/**
	 * 工资项目分类统计台帐:分类指标列表
	 * 
	 * @param salaryid
	 * @return
	 */
	public Vector getClassFieldVector(String salaryid) {
		Vector vector = new Vector();
		try {
			/*
			 * SELECT
			 * S.itemid,max(S.itemtype),max(S.Itemlength),max(S.DecWidth),max(S.nWidth),
			 * F.CodeSetID,F.itemdesc FROM salaryset S, Fielditem F where
			 * S.itemid=F.itemid and S.itemid in (select itemid from fielditem
			 * where useflag='1') and S.itemid in (select itemid from salaryset
			 * WHERE (CSTATE IS NULL OR CSTATE='')) group by S.itemid,
			 * F.CodeSetID,F.itemdesc ORDER BY S.itemid
			 */

			StringBuffer buf = new StringBuffer();
			//String salarySql = this.getSalarysetSQL(salaryid);
			buf.append("select f.itemid,f.itemdesc,f.codesetid from salaryset s,fielditem f ");
			buf.append(" where f.itemid=s.itemid and upper(f.itemtype)='A' and f.codesetid<>'0' ");
			buf.append(" and s.salaryid in("+salaryid+")");
			buf.append(" group by f.itemid,f.itemdesc,f.codesetid,s.sortid order by s.sortid");
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
		    boolean flag=false;
		    HashMap existMap=new HashMap();
			while (rs.next()) {
				if("b0110".equalsIgnoreCase(rs.getString("itemid")))
					flag=true;
				if(this.view!=null&& "0".equals(this.view.analyseFieldPriv(rs.getString("itemid"))))
					continue;
				LazyDynaBean bean = new LazyDynaBean();
				
				if(existMap.get(rs.getString("itemid"))!=null)
				{ 
					continue;
				}
				existMap.put(rs.getString("itemid"), "1");
				bean.set("value", rs.getString("itemid"));
				bean.set("name", rs.getString("itemdesc"));
				bean.set("codesetid", rs.getString("codesetid"));
				vector.addElement(bean);
			}
			rs.close();
			if(!flag)
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("value", "b0110");
				bean.set("name", ResourceFactory.getProperty("tree.unroot.undesc"));
				bean.set("codesetid", "UN");
				vector.addElement(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vector;
	}
    public Vector getSalarySetVector(String salaryid)
    {
	   Vector vector = new Vector();
	   try
	   {
		   String salarySql = this.getSalarysetSQL(salaryid);
		   StringBuffer sql = new StringBuffer();
		   sql.append(" select salaryid,cname  from salarytemplate where  "+salarySql+" order by seq");
		   ContentDAO dao = new ContentDAO(this.conn);
		   RowSet rs = dao.search(sql.toString());
		   LazyDynaBean bean =null;
		   bean = new LazyDynaBean();
		   bean.set("value","-1");
		   bean.set("name","全部");
		   vector.addElement(bean);
		   while(rs.next())
		   {
			   bean = new LazyDynaBean();
			   bean.set("value", rs.getString("salaryid"));
			   bean.set("name",rs.getString("cname"));
			   vector.addElement(bean);
		   }
		   rs.close();
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   return vector;
    }
	/**
	 * 工资项目分类统计台帐:分类指标值列表
	 * 
	 * @param codesetid
	 * @return
	 */
	public Vector getClassFielditemVector(String codesetid,UserView view) {
		Vector vector = new Vector();
		try {
			StringBuffer buf = new StringBuffer();
			if ("UN".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid)) {
				buf.append("select codeitemid,codeitemdesc from organization where UPPER(codesetid)='");
				buf.append(codesetid.toUpperCase()+"' ");
				if(view!=null&&(view.isSuper_admin()|| "1".equals(view.getGroupId())))
				{
					
				}
				else
				{

						buf.append(" and (");
			    		String b_units=this.view.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
						if(b_units!=null&&b_units.length()>0&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
						{
							String unitarr[] =b_units.split("`");	
							for(int i=0;i<unitarr.length;i++)
							{
			    				String codeid=unitarr[i];
			    				if(codeid==null|| "".equals(codeid))
			    					continue;
				    			if(codeid!=null&&codeid.trim().length()>2)
			    				{
				    				String privCode = codeid.substring(0,2);
				    				String privCodeValue = codeid.substring(2);	
				    				if(privCode!=null&&!"".equals(privCode))
									{
							     		buf.append(" (codeitemid like '"+(privCodeValue==null?"":privCodeValue)+"%'");
							    		if(privCodeValue==null)
							    			buf.append(" or codeitemid is null ");
							    		buf.append(") or");
									}
			    				}
							}
						}else if("UN`".equalsIgnoreCase(b_units)){
							buf.append(" 1=1 or");
						}else{
							buf.append(" 1=2 or");
						}
						String _str = buf.toString();
						buf.setLength(0);
						buf.append(_str.substring(0, _str.length()-3));
						buf.append(")");
				}
				String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
				buf.append(" and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
				buf.append(" order by a0000");
			} else {
				buf.append("select codeitemid,codeitemdesc from codeitem where UPPER(codesetid)='");
				buf.append(codesetid.toUpperCase());
				String bosdate=DateStyle.dateformat(new Date(),"yyyy-MM-dd");
				buf.append("' and "+Sql_switcher.dateValue(bosdate)+" between start_date and end_date ");
				buf.append(" order by codeitemid");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			LazyDynaBean  temp=new LazyDynaBean();
			temp.set("value","-1");
			temp.set("name"," ");
			vector.addElement(temp);
			rs = dao.search(buf.toString());
			int x = 0;
			String y = "";
			String z = "";
			int t = 1;
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				if(x==0){
					x=rs.getString("codeitemid").length();
					z=rs.getString("codeitemdesc");
					y=rs.getString("codeitemid");
					bean.set("value", rs.getString("codeitemid"));
					bean.set("name", z);
				}else if(rs.getString("codeitemid").length()==x){//为了按层级显示部门，zhaoxg 2013-5-9
					x=rs.getString("codeitemid").length();
					z=rs.getString("codeitemdesc");
					for(int k=1;k<=t-1;k++){
						z = "  "+z;
					}
					y=rs.getString("codeitemid");
					bean.set("value", rs.getString("codeitemid"));
					bean.set("name", z);
				}else if(rs.getString("codeitemid").length()>x&&rs.getString("codeitemid").substring(0, x).equals(y)){
					x=rs.getString("codeitemid").length();
					z=rs.getString("codeitemdesc");
					for(int k=1;k<=t;k++){
						z = "  "+z;
					}
					y=rs.getString("codeitemid");
					bean.set("value", rs.getString("codeitemid"));
					bean.set("name", z);
					t++;
				}else{
					x=rs.getString("codeitemid").length();
					z=rs.getString("codeitemdesc");
					y=rs.getString("codeitemid");
					bean.set("value", rs.getString("codeitemid"));
					bean.set("name", z);
					t=1;
				}

				vector.addElement(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vector;
	}

	/**
	 * 取单位和部门的总计
	 * 
	 * @param rsid
	 * @param rsdtlid
	 * @param pre
	 * @param salaryid
	 * @param year
	 * @param a0100
	 * @param fielditemid
	 * @param fielditemvalue
	 * @return
	 */
	public ArrayList getUN_UM_Total(String columns, String dbSql,
			String salarySql, String rsid, String rsdtlid, String pre,
			String salaryid, int year, String a0100, String fielditemid,
			String fielditemvalue, String starttime, String endtime,String statflag,String privSql,String zxgcombox,String zxgfrom,String zxgto,String condsql) {
		ArrayList list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			StringBuffer view_buf = new StringBuffer();
			view_buf.append(" select MAX("+Sql_switcher.isnull("b0110", "'UN'")+") as b0110,"+Sql_switcher.isnull("e0122", "'UM'")+" as e0122");
			view_buf.append(columns);
			view_buf.append(",a0101,nbase,a0100,max(a0000) as a0000");
			view_buf.append(" from "+this.getAnalyseTable()+" where ");
			dbSql = getDbSQL(pre,salaryid);
			salarySql = getSalarysetSQL(salaryid);
			view_buf.append(dbSql);
			view_buf.append(" and "+salarySql);
			view_buf.append(" and ");
			if ("1".equals(statflag)) {
				view_buf.append(Sql_switcher.year("a00z0"));
				view_buf.append("=");
				view_buf.append(year);
			} else {
				String newStarttime = changeFormat(starttime, ".");
				String newEndtime = changeFormat(endtime, ".");
				PositionStatBo psb = new PositionStatBo(this.conn);
				view_buf.append(psb.getDateSql(">=", "a00z0",newStarttime));
				view_buf.append(" and ");
				view_buf.append(psb.getDateSql("<=","a00z0",newEndtime));
			}
			if(privSql!=null&&!"".equals(privSql))
	    		view_buf.append(" and "+privSql);
				if(condsql==null|| "".equals(condsql)){
					view_buf.append("  group by a0100,nbase,a0101,e0122 ");
				}else{
					view_buf.append(" and "+condsql+" group by a0100,nbase,a0101,e0122 ");
				}
				
			
			
			buf.append("select a0100,a0101,");
			if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
				buf.append(" decode(grouping(b0110),1,'un_total',b0110) b0110,");
				buf.append(" decode(grouping(e0122),1,'um_total',e0122) e0122");
			} else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
				buf.append(" coalesce(e0122,'um_total') e0122,coalesce(b0110,'un_total') b0110");
			}
			buf.append(columns);
			buf.append(" from ("+view_buf.toString()+") t ");
			if (Sql_switcher.searchDbServer() == Constant.ORACEL
					|| Sql_switcher.searchDbServer() == Constant.DB2) {
				buf.append(" group by rollup( b0110,e0122,a0101,a0100) ");
			} else {
				buf.append(" group by b0110,e0122,a0101,a0100 with rollup ");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			StringBuffer heji = new StringBuffer();
			if((!"".equals(zxgcombox))&&(!"".equals(zxgfrom))&&(!"".equals(zxgto))){//查询后是否显示合计 zhaoxg 2013-5-13
				heji.append("select a0100,a0101,");
				if (Sql_switcher.searchDbServer() == Constant.ORACEL|| Sql_switcher.searchDbServer() == Constant.DB2) {
					heji.append(" decode(grouping(b0110),1,'un_total',b0110) b0110,");
					heji.append(" decode(grouping(e0122),1,'um_total',e0122) e0122");
				} else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
					heji.append(" coalesce(e0122,'um_total') e0122,coalesce(b0110,'un_total') b0110");
				}
				heji.append(columns);

				heji.append(" from ("+buf.toString()+" having nullif(A0101,'') is not null and nullif(A0100,'') is not null and sum("+Sql_switcher.isnull(zxgcombox, "0")+")  between "+zxgfrom+" and "+zxgto+")  zz ");
				if (Sql_switcher.searchDbServer() == Constant.ORACEL
						|| Sql_switcher.searchDbServer() == Constant.DB2) {
					heji.append(" group by rollup( b0110,e0122,a0101,a0100) ");
				} else {
					heji.append(" group by b0110,e0122,a0101,a0100 with rollup ");
				}
			}else{
				heji = buf;
			}
			rs = dao.search(heji.toString());
			ArrayList headList = getTableHeadlist(rsdtlid, rsid, "","0");
			int init = 0;
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				if ("un_total".equalsIgnoreCase(rs.getString("b0110"))) {
					continue;
				}
				if(rs.getString("a0100") == null&&rs.getString("a0101") != null&&rs.getString("e0122") != null&&rs.getString("b0110") != null){

				}else{
				if (rs.getString("a0101") == null) {

					init++;
					if (!"um_total".equalsIgnoreCase(rs.getString("e0122"))
							|| init == 1) {

						bean.set("a0101", "合计");
						for (int i = 0; i < headList.size(); i++) {
							LazyDynaBean abean = (LazyDynaBean) headList.get(i);
							String itemid = (String) abean.get("itemid");
							String itemtype=(String)abean.get("itemtype");
							if("N".equalsIgnoreCase(itemtype))
				   		    	bean.set(itemid, rs.getString(itemid));
							else{
							    bean.set(itemid, "");
							}
						}
						bean.set("flag", "1");// 不显示序号
						bean.set("code", "UM");// 是部门合计
					}

					if ("um_total".equalsIgnoreCase(rs.getString("e0122"))&& init > 1) {
						bean.set("a0101", "总计");
						for (int i = 0; i < headList.size(); i++) {
							LazyDynaBean abean = (LazyDynaBean) headList.get(i);
							String itemid = (String) abean.get("itemid");
							String itemtype=(String)abean.get("itemtype");
							if("n".equalsIgnoreCase(itemtype))
					 		{
				    			bean.set(itemid, rs.getString(itemid));
					 		}else
					 		{
					 			bean.set(itemid,"");
					 		}
						}
						bean.set("flag", "1");// 不显示序号
						bean.set("code", "UN");// 是单位总计

					}
					bean.set("e0122", rs.getString("e0122"));
					bean.set("b0110", rs.getString("b0110"));
					list.add(bean);

				} else {
					init = 0;
				}
				}
			}
            rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 取得分析项目列表8
	 * 
	 * @param salaryid
	 * @return
	 */
	public Vector getAnalyseProjectVector(String salaryid) {
		Vector v = new Vector();
		try {

			StringBuffer buf = new StringBuffer();
			buf.append("SELECT S.itemid,S.itemtype,max(S.itemlength) as itemlength,max(S.decwidth) as decwidth,max(nWidth) as nwidth,F.codesetid,f.itemdesc from salaryset S,");
			buf.append("fielditem F where S.itemid=F.itemid and S.salaryid in("+salaryid+") and S.itemid in (select itemid  from fielditem where useflag='1') and ");
			buf.append(" S.itemid in (select itemid from salaryset WHERE itemtype='N') group by S.itemid,F.CodeSetID,F.itemdesc,s.itemtype "
							+ " ORDER BY S.itemid");//(CSTATE IS NULL OR CSTATE='') and
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(buf.toString());
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				if(this.view!=null&& "0".equals(this.view.analyseFieldPriv(rs.getString("itemid"))))
					continue;
				bean.set("name", rs.getString("itemdesc"));
				bean.set("value", rs.getString("itemid"));
				v.addElement(bean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	public String getUpperMonth(int month) {
		String mon = "";
		switch (month) {
		case 1: {
			mon = "一月";
			break;
		}
		case 2: {
			mon = "二月";
			break;
		}
		case 3: {
			mon = "三月";
			break;
		}
		case 4: {
			mon = "四月";
			break;
		}
		case 5: {
			mon = "五月";
			break;
		}
		case 6: {
			mon = "六月";
			break;
		}
		case 7: {
			mon = "七月";
			break;
		}
		case 8: {
			mon = "八月";
			break;
		}
		case 9: {
			mon = "九月";
			break;
		}
		case 10: {
			mon = "十月";
			break;
		}
		case 11: {
			mon = "十一月";
			break;
		}
		case 12: {
			mon = "十二月";
			break;
		}
		}
		return mon;
	}

	/**
	 * 更改列标题
	 * 
	 * @param rsid
	 * @param rsdtlid
	 * @param itemid
	 * @param newItemdesc
	 */
	public void editColumnTitle(String rsid, String rsdtlid, String itemid,
			String newItemdesc, String opt) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("update reportitem set ");
			// itemdesc=? where rsdtlid="+rsdtlid+" and itemid='"+itemid+"'";
			if ("4".equals(opt)) {
				sql.append(" itemdesc='");
			}
			if ("2".equals(opt)) {
				sql.append(" itemfmt='");
			}
			if ("1".equals(opt)) {
				sql.append(" align='");
			}
			sql.append(newItemdesc + "'");
			sql.append(" where rsdtlid= ");
			sql.append(rsdtlid);
			sql.append(" and itemid='");
			sql.append(itemid);
			sql.append("'");
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 重置分析表项目顺序
	 * 
	 * @param tableHeadList
	 * @param currentList
	 * @param rsdtlid
	 */
	public void reSetSort(ArrayList tableHeadList, ArrayList currentList,
			String rsdtlid) {
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			for (int i = 0; i < currentList.size(); i++) {
				String aitemdesc = (String) currentList.get(i);
				for (int j = 0; j < tableHeadList.size(); j++) {
					LazyDynaBean bean = (LazyDynaBean) tableHeadList.get(j);
					String itemid = (String) bean.get("itemid");
					String itemdesc = (String) bean.get("itemdesc");
					if (itemdesc.equals(aitemdesc)) {
						dao.update("update reportitem set sortid=" + i+ " where rsdtlid=" + rsdtlid + " and UPPER(itemid)='"+ itemid.toUpperCase() + "'");
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetWidth(ArrayList currentColWidthList,
			ArrayList currentcolList, ArrayList tableHeadList, String rsdtlid) {
		try {
			ContentDAO dao = new ContentDAO(this.conn);

			for (int i = 0; i < currentcolList.size(); i++) {
				String aitemdesc = (String) currentcolList.get(i);
				String nwidth = (String) currentColWidthList.get(i);
				for (int j = 0; j < tableHeadList.size(); j++) {
					LazyDynaBean abean = (LazyDynaBean) tableHeadList.get(j);
					String itemid = (String) abean.get("itemid");
					String itemdesc = (String) abean.get("itemdesc");
					if (itemdesc.equals(aitemdesc)) {
						dao.update("update reportitem set nwidth=" + nwidth+ " where rsdtlid=" + rsdtlid + " and UPPER(itemid)='"+ itemid.toUpperCase() + "'");
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提供相对精确的除法运算
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 * @return
	 */
	public static String div(String v1, String v2, int scale) {
		if (scale < 0) {
			scale = 2;
		}
		if (v2 == null || "".equals(v2) ||Float.parseFloat(v2)==0) {
			v2 = "1";
		}
		if (v1 == null || "0".equals(v1)|| "".equals(v1)||v1.trim().length()==0) {
			return "0";
		}
		BigDecimal b1 = new BigDecimal(v1);

		BigDecimal b2 = new BigDecimal(v2);

		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString();

	}

	/**
	 * 加法运算
	 * 
	 * @param v1
	 * @param v2
	 * @param scale
	 * @return
	 */
	public static String add(String v1, String v2, int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		if (v2 == null || "".equals(v2)||Float.parseFloat(v2)==0) {
			v2 = "0";
		}
		if (v1 == null || "".equals(v1)||Float.parseFloat(v1)==0) {
			v1="0";
		}
		BigDecimal a = new BigDecimal(v1);
		BigDecimal b = new BigDecimal(v2);
		BigDecimal s = a.add(b);
		BigDecimal one = new BigDecimal("1");
		return s.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();

	}

	/**
	 * 减法运算
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static String sub(String v1, String v2, int scale) {
		if (v1 == null || "".equals(v1))
			v1 = "0";
		if (v2 == null || "".equals(v2))
			v2 = "0";
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		BigDecimal one = new BigDecimal("1");
		BigDecimal r = b1.subtract(b2);
		return r.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();

	}

	public String changeFormat(String value, String sep) {
		String str = "";
		try {
			if (value == null || "".equals(value))
				return str;
			//String year= String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(0,2);
			
			String[] temp = value.split("-");
			if (temp.length == 3)
				str = temp[0] + sep  + temp[1]+sep + temp[2];

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public String getBgroup(String rsdtlid, String rsid) {
		String bgroup = "0";
		try {
			String sql = "select bgroup from reportdetail where rsdtlid="+ rsdtlid + " and rsid=" + rsid;
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			rs = dao.search(sql);
			while (rs.next()) {
				bgroup = rs.getString("bgroup");
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bgroup;
	}
	/**
	 * 权限限制sql语句
	 * @param role
	 * @param privCode
	 * @param privCodeValue
	 * @param tablename
	 * @return
	 */
	public String getPrivSQL(String role,String tablename,String salaryid,String b_units)
	{
		StringBuffer buf = new StringBuffer("");
		String[] temp = salaryid.split(",");
		if("1".equals(role))//如果是树节点传进来的，那么此处role可传空  role=1 代表超级用户 if(this.userView.isSuper_admin()||this.userView.getGroupId().equals("1"))
		{
			buf.append( "  1=1 ");
		}else
		{			
			
	     	HashMap map = new HashMap();
			for (int j= 0; j < temp.length; j++){
				String b0110_item="b0110";
				String e0122_item="e0122";
				SalaryCtrlParamBo ctrlparam=new SalaryCtrlParamBo(this.conn,Integer.parseInt(temp[j])); 
				String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
				String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");//归属部门
				if(deptid!=null&&deptid.trim().length()>0)//设置了归属部门
				{ 
					e0122_item=deptid;
					if(orgid!=null&&orgid.length()>0)
						b0110_item=orgid;
				}else if(orgid!=null&&orgid.trim().length()>0)//没设置归属部门，只设置了归属单位，走归属单位
				{ 
					b0110_item=orgid;
				}
				String item = (String) map.get(e0122_item+"/"+b0110_item);
		    	if(item!=null&&item.length()>0){
		    		map.put(e0122_item+"/"+b0110_item, item+",'"+temp[j]+"'");
		    	}else{
		    		map.put(e0122_item+"/"+b0110_item, "'"+temp[j]+"'");
		    	}	

			}			
			if(b_units!=null&&b_units.length()>2&&!"UN".equalsIgnoreCase(b_units)&&!"UN`".equalsIgnoreCase(b_units)) //模块操作单位
			{
				String unitarr[] =b_units.split("`");				
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					String[] str = key.toString().split("/");
					buf.append("((");
					for(int i=0;i<unitarr.length;i++)
					{
	    				String codeid=unitarr[i];
	    				if(codeid==null|| "".equals(codeid))
	    					continue;
		    			if(codeid!=null&&codeid.trim().length()>2)
	    				{
		    				String privCode = codeid.substring(0,2);
		    				String privCodeValue = codeid.substring(2);							  
							if(privCode!=null&&!"".equals(privCode))
							{		
								buf.append(" ( case");
								if(!"e0122".equalsIgnoreCase(str[0])&&!"b0110".equalsIgnoreCase(str[1])){//归属单位和部门均设置了
									buf.append("  when  nullif("+tablename+str[0]+",'') is not null  then "+tablename+str[0]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null ) and nullif("+tablename+str[1]+",'') is not null then "+tablename+str[1]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null ) and (nullif("+tablename+str[1]+",'') is null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if(!"e0122".equalsIgnoreCase(str[0])&& "b0110".equalsIgnoreCase(str[1])){//设置了归属部门，没设置归属单位
									buf.append("  when nullif("+tablename+str[0]+",'') is not null then "+tablename+str[0]+" ");
									buf.append("  when (nullif("+tablename+str[0]+",'') is  null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if("e0122".equalsIgnoreCase(str[0])&&!"b0110".equalsIgnoreCase(str[1])){//没设置归属部门，设置了归属单位
									buf.append("  when nullif("+tablename+str[1]+",'') is not null then "+tablename+str[1]+" ");
									buf.append("  when (nullif("+tablename+str[1]+",'') is null) and nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}else if("e0122".equalsIgnoreCase(str[0])&& "b0110".equalsIgnoreCase(str[1])){//啥都没设置
									buf.append("  when nullif("+tablename+"e0122,'') is not null then "+tablename+"e0122 ");
									buf.append(" else "+tablename+"b0110 end ");
									buf.append(" like '"+privCodeValue+"%' ");
									buf.append(") or");
								}
							}
	    				}
					}

					if(this.view.isSuper_admin()|| "1".equals(this.view.getGroupId())){
						String _str = buf.toString();
						buf.setLength(0);
						buf.append(_str.substring(0, _str.length()-3));
						buf.append(" ) and "+tablename+"salaryid in ("+val.toString()+")) or");
					}else{
						String _str = buf.toString();
						buf.setLength(0);
						buf.append(_str.substring(0, _str.length()-3));
						buf.append(") and "+tablename+"salaryid in ("+val.toString()+")) or");
					}									
				}
				String str = buf.toString();
				buf.setLength(0);
				buf.append("("+str.substring(0, str.length()-3)+")");
			}else if("UN`".equalsIgnoreCase(b_units)){
				buf.append( "  1=1 ");
			}
			else
			{
				if(this.view.isSuper_admin()|| "1".equals(this.view.getGroupId())){
					buf.append( "  1=1 ");
				}else{
					buf.append( "  1=2 ");
				}
			}
		}
		buf.append(" and (");
		for (int i = 0; i < temp.length; i++){
			if(i==0){
				buf.append("  (salaryid = ");
			}else{
				buf.append(" or (salaryid = ");
			}
			
		    buf.append(temp[i]);
		    buf.append(")");
		}
		buf.append(")");
		return buf.toString();
	}
	/**
	 * 
	 * @param sql
	 * @param dao
	 * @param isShowUnitData =1 不显示单位数据
	 * @return
	 */
	public HashMap getMonthCount(String sql,ContentDAO dao,String isShowUnitData)
	{
		HashMap map = new HashMap();
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append(" select count(T.ayear) as  total,b0110");
			if("1".equals(isShowUnitData))
		    	buf.append(",e0122");
			buf.append(" from (");
			buf.append(sql+") T group by b0110");
			if("1".equals(isShowUnitData))
	    		buf.append(",e0122 ");
			buf.append(" order by b0110");
			if("1".equals(isShowUnitData))
	    		buf.append(",e0122");
			RowSet rs=dao.search(buf.toString());
			while(rs.next())
			{
				if("1".equals(isShowUnitData))
				{
					String b0110=rs.getString("b0110");
					String e0122=rs.getString("e0122");
					String total=rs.getString("total");
					if(total==null|| "0".equals(total))
					{
						total="1";
					}
					map.put(b0110+e0122, total);
				}
				else
				{
					String b0110=rs.getString("b0110");
					String total=rs.getString("total");
					if(total==null|| "0".equals(total))
					{
						total="1";
					}
					map.put(b0110, total);
				}
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}	
	public ArrayList getRecordListBacth(String rsid, String rsdtlid, String pre,
			String salaryid, int year, String a0100) {
		ArrayList list = new ArrayList();
        RowSet rs = null;
		try {
			ArrayList headList = getTableHeadlist(rsdtlid, rsid, "","0");
			StringBuffer buf = new StringBuffer();
			StringBuffer sql = new StringBuffer();
			String dbSql = "";
			String salarySql = "";
				for (int i = 0; i < headList.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) headList.get(i);
					buf.append(" sum("+Sql_switcher.isnull((String) bean.get("itemid"), "0")+") as ");
					buf.append((String) bean.get("itemid"));
					buf.append(",");
				}
				buf.setLength(buf.length() - 1);
				/**dbSql 和salarySql前都没有and*/
				//dbSql = getDbSQL(pre);
				salarySql = getSalarysetSQL(salaryid);
				sql.append("select ");
				sql.append(buf.toString());
				sql.append(",");
				sql.append(Sql_switcher.month("A00Z0"));
				sql.append(" as aMonth ");
				sql.append(" from "+this.getAnalyseTable()+" where ");
			    sql.append(" A0100='" + a0100.substring(3) + "' AND ");
			    sql.append(" UPPER(nbase) ='" + a0100.substring(0, 3).toUpperCase() + "' ");
				//sql.append(dbSql);
				sql.append(" and ");
				sql.append(Sql_switcher.year("A00Z0"));
				sql.append("=" + year+" and ");
				sql.append(salarySql);
				sql.append(" group by ");
				sql.append(Sql_switcher.month("A00Z0"));	
			ContentDAO dao = new ContentDAO(this.conn);

	        rs = dao.search(sql.toString());
			while (rs.next()) {

				LazyDynaBean bean = new LazyDynaBean();
					for (int j = 0; j < headList.size(); j++) {
						LazyDynaBean abean = (LazyDynaBean) headList.get(j);
						bean.set((String) abean.get("itemid"),rs.getString((String) abean.get("itemid")) == null ? "0.00": rs.getString((String) abean.get("itemid")));

					}
					bean.set("amonth", rs.getString("amonth"));
					list.add(bean);
				}

		} catch (Exception e) {
			e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
		return list;
	}
	/**
	 * 获取项目名称   按部门各月工资构成分析表
	 * @param salaryid
	 * @return
	 */
	public ArrayList gzProjectList(String salaryid,StringBuffer str_value2){
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
        RowSet rs = null;
		try{
			StringBuffer buf = new StringBuffer("select itemid,itemdesc from salaryset where CODESETID <> '0' and salaryid in ("+salaryid+") group by itemid,itemdesc");
			rs=dao.search(buf.toString());
			while(rs.next()){
				CommonData data = new CommonData(rs.getString("itemid"), rs.getString("itemdesc"));
				str_value2.append("`"+rs.getString("itemid")+"~"+rs.getString("itemdesc")+"");
				list.add(data);
			}
			
		}catch(Exception e){
			e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
		return list;
	}
	/**
	 * 针对使用多层级代码的数据进行排序
	 * @param recordList
	 * @param codeSetId
	 * @return
	 * @author ZhangHua
	 * @date 16:53 2018/1/4
	 */
	public ArrayList sorckRecordList(ArrayList recordList,String codeSetId){
		try{
			if("um".equalsIgnoreCase(codeSetId)|| "un".equalsIgnoreCase(codeSetId))
				return recordList;
			LinkedHashMap<String,Integer> codeMap=this.getCodeItemSortNum(codeSetId);
			if(codeMap==null||codeMap.size()==0)
				return recordList;
			for (Object object : recordList) {
				if(((LazyDynaBean)object).get("groupitemid")!=null){
					String codeId=(String)((LazyDynaBean)object).get("groupitemid");
					if(codeMap.containsKey(codeId))
						((LazyDynaBean)object).set("sortNum",codeMap.get(codeId));
					else
						((LazyDynaBean)object).set("sortNum",10000);
				}
			}
			LazyDynaBean totalBean=(LazyDynaBean)recordList.get(recordList.size()-1);
			if(totalBean.get("total")==null)
				totalBean=null;
			else
				recordList.remove(recordList.size()-1);
			sortClass sort=new sortClass();
			Collections.sort(recordList,sort);
			recordList.add(totalBean);

		}catch(Exception e){
			e.printStackTrace();
		}
		return recordList;
	}
	public class sortClass implements Comparator{
		@Override
        public int compare(Object arg0, Object arg1){
			try {
				int dt1 = Integer.parseInt(((LazyDynaBean)arg0).get("sortNum").toString());
				int dt2 = Integer.parseInt(((LazyDynaBean)arg1).get("sortNum").toString());
				if (dt1 > dt2) {
					return 1;
				} else if (dt1 < dt2) {
					return -1;
				} else {
					return 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}

	/**
	 * 获取待排序的数据
	 * @param codeSetId
	 * @author ZhangHua
	 * @date 16:54 2018/1/4
	 * @return
	 */
	private LinkedHashMap<String,Integer> getCodeItemSortNum(String codeSetId) {
		LinkedHashMap<String,Integer> codeItemSortMap=new LinkedHashMap<String, Integer>();
        RowSet rs=null;
		try{

			ContentDAO dao = new ContentDAO(this.conn);

			String strSql="select codeitemid,parentid,childid,layer,a0000" +
					" from codeitem where upper(codesetid)=? and "+Sql_switcher.sqlNow()+" between start_date and end_date order by layer,a0000";//重点是按照层级和a0000排序，递归时以此数据为基础
			rs=dao.search(strSql,Arrays.asList(new Object[] {codeSetId.toUpperCase() }));
			ArrayList<HashMap<String,String>> codeList=new ArrayList<HashMap<String, String>>();
			int layer=1;
			while (rs.next()){
				HashMap<String,String> map=new HashMap<String, String>();
				String codeItemId=rs.getString("codeitemid");
				map.put("codeitemid",codeItemId);
				map.put("parentid",rs.getString("parentid"));
				map.put("childid",rs.getString("childid"));
				if(rs.getInt("layer")>layer)
					layer=rs.getInt("layer");
				map.put("layer",rs.getString("layer"));
				map.put("a0000",rs.getString("a0000"));
				codeList.add(map);
			}
			if(layer==1)
				return null;
			strSql="select codeitemid " +
					" from codeitem where upper(codesetid)=? and "+Sql_switcher.sqlNow()+" between start_date and end_date and codeitemid not in " +
					"(select parentid from  codeitem where upper(codesetid)=?  and "+Sql_switcher.sqlNow()+" between start_date and end_date  AND parentid<>codeitemid )";
			rs=dao.search(strSql,Arrays.asList(new Object[] {codeSetId.toUpperCase() ,codeSetId.toUpperCase()}));
			HashMap<String,String> endCodeMap=new HashMap<String, String>();
			while (rs.next()){
				endCodeMap.put(rs.getString("codeitemid"),"1");
			}
			this.recurisveCount=0;
			this.doSortCodeMap(codeList,codeItemSortMap,null,endCodeMap);
		}catch (Exception e){
			e.printStackTrace();
		}finally {
		    PubFunc.closeDbObj(rs);
        }
		return codeItemSortMap;
	}
	private int recurisveCount=0;
	/**
	 * 递归对代码树进行排序
	 * @param codeList 原代码数组
	 * @param codeItemSortMap 排序结果数组
	 * @param codeMap 指针
	 * @param endCodeMap 叶子节点
	 * @author ZhangHua
	 * @date 16:55 2018/1/4
	 */
	private void doSortCodeMap (ArrayList<HashMap<String,String>> codeList, LinkedHashMap<String,Integer> codeItemSortMap, HashMap<String, String> codeMap, HashMap<String,String> endCodeMap){
		try{
			if(this.recurisveCount>=50000)//防止死循环
				return;
			this.recurisveCount++;
			if(codeMap==null) {
				for (HashMap<String, String> map : codeList) {//首次进入取顶级节点
					if (map.get("codeitemid").equalsIgnoreCase(map.get("parentid"))) {
						codeItemSortMap.put(map.get("codeitemid"), codeItemSortMap.size());
						this.doSortCodeMap(codeList, codeItemSortMap, map,endCodeMap);
					}
				}
			}else{
				if(endCodeMap.containsKey(codeMap.get("codeitemid")))//递归到底级节点时结束
					return;
				for (HashMap<String, String> map : codeList) {
					if (codeMap.get("codeitemid").equalsIgnoreCase(map.get("parentid"))&&!codeItemSortMap.containsKey(map.get("codeitemid"))) {//找到下级节点
						codeItemSortMap.put(map.get("codeitemid"), codeItemSortMap.size());
						this.doSortCodeMap(codeList, codeItemSortMap, map,endCodeMap);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}



	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSpFlag() {
		return spFlag;
	}
	public void setSpFlag(String spFlag) {
		this.spFlag = spFlag;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
