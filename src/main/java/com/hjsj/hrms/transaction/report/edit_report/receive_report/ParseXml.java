package com.hjsj.hrms.transaction.report.edit_report.receive_report;

import com.hjsj.hrms.businessobject.report.ReportResultBo;
import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 解析上报盘，将上报的XML文件导入数据库
 * 
 * @author lzy
 * 
 */
public class ParseXml {

	private  String userCode = "";
	private Connection con;
	// 报表id
	private String tabid = "";
	private String unitcode = ""; //上报盘中格式化的单位ID
	// 根节点
	private Element hrp_reports = null;
	private  String operateObject=""; //1:表示编辑报表
	private  String username ="";
	private  String report="";
	private  HashMap map = new HashMap();
	private  String  clew ="";
	private  String strsecid="";//xgq

	/**
	 * 
	 * @param con
	 * @param mydoc  XML文档对象如果此对象不为空则所有数据生成在这个文档中。
	 * @param node_unitcode  填报单位
	 * @param parent_unitcode
	 * @throws SQLException
	 */
	public ParseXml() {
		super();
		
	}
	/**
	 * 
	 * @param con
	 * @param mydoc  XML文档对象如果此对象不为空则所有数据生成在这个文档中。
	 * @param node_unitcode  填报单位
	 * @param parent_unitcode
	 * @throws SQLException
	 */
	public ParseXml(Connection con, Document mydoc, String node_unitcode, String parent_unitcode) throws SQLException {
		super();
		if (con != null) {
			this.con = con;
		}
		Document doc = mydoc;
		NodeList hrp_reports_list = doc.getElementsByTagName("hrp_reports");
		this.hrp_reports = (Element) hrp_reports_list.item(0);
		this.unitcode = node_unitcode;
		// unitcode = hrp_reports.getAttribute("unitcode");
	}

	/**
	 * 上报盘分析
	 * @throws Exception
	 */
	public void parseXml() throws Exception {

		NodeList reportlist = hrp_reports.getElementsByTagName("report");
		ArrayList updaterowlimit= new ArrayList();

		if(operateObject!=null&& "1".equals(operateObject)){
			for (int i = 0; i < reportlist.getLength(); i++) {
				
				Element report = (Element) reportlist.item(i);
				//上报的表名
				tabid = report.getAttribute("tabid");
				ContentDAO dao = new ContentDAO(con);
				TnameBo      namebo=new TnameBo(con,tabid,"",this.username,"view");
				String columns = report.getAttribute("columns");
				String rows = report.getAttribute("rows");
				if(this.report.indexOf(","+tabid+",")==-1){
					if(this.map!=null&&this.map.get(tabid)!=null){
						this.clew+="没有表:"+this.map.get(tabid)+"的权限\\n";
						continue;
					}
				}				
				//优化begin 即使两个表结构不完全一样则只接收报盘在表中存在的字段 dml2012年2月22日15:36:17
				//判断报表是否与表样式一致 
//				if (!namebo.checkRowColumnNumber(Integer.parseInt(rows), Integer.parseInt(columns))) {
//					if(this.map!=null&&this.map.get(tabid)!=null){
//						this.clew+="上报盘中的表"+tabid+":"+this.map.get(tabid)+"与表结构不一致！\\n";
//						continue;
//					//throw new GeneralException("上报盘中的表:"+this.map.get(tabid)+"与表结构不一致！");
//					}
//				}
				// end
				//优化 维护参数表和创建数据结果表 dml2012年2月22日15:36:17
				this.analyzeTable("0", null, operateObject, tabid, namebo);
				this.analyzeTable("1", null, operateObject, tabid, namebo);
				this.analyzeTable("2", report, operateObject, tabid, namebo);
				this.analyzeTable("3", report, operateObject, tabid, namebo);
				
				try {
					int num =insertReportCtrl(con, unitcode, tabid, "0");//设置填报单位对应报表上报标识为编辑状态
					if(num==1)
						continue;
					String str = this.getRowLimit(namebo, tabid);
					strsecid= str;//xgq
					if(str.length()>1)
						str = " and secid not in ("+str+") ";
					String delsql="delete from tb" + tabid + " where username = '"+ this.username + "' "+str+"";
				//	 updaterowlimit = getUpTbxxByRowLimit(namebo,tabid);//xgq
					
				//	System.out.println(delsql);
					//删除上报表中原有该填报单位对应的数据
					dao.update(delsql);
				} catch (Exception ex) {
					//ReceiveReportTrans.clew = "id为" + tabid + "的报表不存在！";
					ex.printStackTrace();
					//throw new Exception(ReceiveReportTrans.clew);
				}

				 
				
				parseParemElement("0", null);	//全局参数
				parseParemElement("1", null);   //表类参数
				parseParemElement("2", report); //表参数
				parseParemElement("3", report); //表数据
//				if(updaterowlimit.size()>0){			//xgq
//					dao.batchUpdate( updaterowlimit);
//				}
				namebo.insertByRowLimit( tabid, dao, operateObject, username, unitcode);//补齐数据
				//处理小数位
				namebo.autoUpdateDigitalResults(operateObject, "7", "", "", this.tabid,this.username,unitcode);
			}
		}else{
			TnameExtendBo nameExtendBo = new TnameExtendBo(con);
			DbWizard mywizard = new DbWizard(con);
			nameExtendBo.isExistAppealParamTable(1, "", "", mywizard);

			for (int i = 0; i < reportlist.getLength(); i++) {
				Element report = (Element) reportlist.item(i);
				//上报的表名
				tabid = report.getAttribute("tabid");
				TnameBo      namebo=new TnameBo(con,tabid,"",this.username,"view");
				// 注释掉 begin dml 2012年2月22日17:33:02 如果执行下面代码，如果编辑报表的参数结果表也不存在则会出错，可以直接创建。
				//nameExtendBo.isExistAppealParamTable(2, tabid, getSortId(con, tabid), mywizard);
				//nameExtendBo.isExistAppealParamTable(3, tabid, "", mywizard);
				// end
				this.analyzeTable("0", null, operateObject, tabid, namebo);
				this.analyzeTable("1", null, operateObject, tabid, namebo);
				this.analyzeTable("2", report, operateObject, tabid, namebo);
				this.analyzeTable("3", report, operateObject, tabid, namebo);
				ContentDAO dao = new ContentDAO(con);
				try {
					String str = this.getRowLimit(namebo, tabid);
					strsecid= str;
					if(str.length()>1)
						str = " and secid not in ("+str+") ";
					String delsql="delete from tt_" + tabid + " where unitcode = '"+ unitcode + "' "+str+"";
					
				//	 updaterowlimit = getUpTbxxByRowLimit(namebo,tabid); //xgq
				//	System.out.println(delsql);
					//删除上报表中原有该填报单位对应的数据
					dao.update(delsql);
				} catch (Exception ex) {
					//ReceiveReportTrans.clew = "id为" + tabid + "的报表不存在！";
					ex.printStackTrace();
					//throw new Exception(ReceiveReportTrans.clew);
				}

				deleteRreport_ctrl(con, unitcode, tabid); //清空填报单位对应报表上报标识
				insertReportCtrl(con, unitcode, tabid, "1");//设置填报单位对应报表上报标识为已上报状态
				updateSortIdPurview(con, getSortId(con, tabid), unitcode); //设置填报单位负责的报表类别(reporttypes) 
				
				String columns = report.getAttribute("columns");
				String rows = report.getAttribute("rows");
				//判断报表是否与表样式一致
				if (!namebo.checkRowColumnNumber(Integer.parseInt(rows), Integer.parseInt(columns))) {
					throw new Exception();
				}
				
				parseParemElement("0", null);	//全局参数
				parseParemElement("1", null);   //表类参数
				parseParemElement("2", report); //表参数
				parseParemElement("3", report); //表数据
//				if(updaterowlimit.size()>0){		//xgq
//					dao.batchUpdate( updaterowlimit);
//				}
				namebo.insertByRowLimit( tabid, dao, operateObject, username, unitcode);//补齐数据
				//处理小数位
				namebo.autoUpdateDigitalResults("2", "7", "", "", this.tabid,this.username,unitcode);
			}
		}
		
	}
	public boolean analyzeTable(String paramscope, Element element,String operateObject,String tabid,TnameBo namebo){
		boolean flag=false;
		NodeList list = null;
		String t_index="";
		try {
			if ("0".equals(paramscope)) {
				list = hrp_reports.getElementsByTagName("tp_global");
				if("1".equalsIgnoreCase(operateObject)){
					t_index="tp_p";
				}else{
					t_index="tt_p";
				}
			}
			if ("1".equals(paramscope)) {
				list = hrp_reports.getElementsByTagName("tp_style");
				if("1".equalsIgnoreCase(operateObject)){
					t_index="tp_s";
				}else{
					t_index="tt_s";
				}
			}
			if ("2".equals(paramscope)) {
				list = element.getElementsByTagName("tp_table");
				if("1".equalsIgnoreCase(operateObject)){
					t_index="tp_t";
				}else{
					t_index="tt_t";
				}
			}
			if ("3".equals(paramscope)) {
				list = element.getElementsByTagName("records");
			}
			if(this.report.indexOf(","+tabid+",")==-1){
				if(this.map!=null&&this.map.get(tabid)!=null){
					this.clew+="没有表:"+this.map.get(tabid)+"的权限\\n";
				}
			}
			DbWizard dbWizard=new DbWizard(this.con);
			RowSet rs=null;
			ContentDAO dao = new ContentDAO(con);
			StringBuffer sql=new StringBuffer();
			Element node = (Element) list.item(0);
			NodeList paremeterList = node.getChildNodes();
			if (paremeterList == null){
				return flag;
			}
			rs=dao.search("select tsortid from tname where tabid="+tabid);
			String sortid="-1";
			if(rs.next())
				sortid=rs.getString("tsortid");
			HashMap param_map=namebo.getParamMap();
			if ("3".equals(paramscope)) {
				if(namebo.getRowInfoBGrid()!=null&&namebo.getRowInfoBGrid().size()!=0){
					if(operateObject!=null&& "1".equals(operateObject)){
						if(namebo.getRowMap().size()!=0&&namebo.getColMap().size()!=0){
							// 到tname和tgrid2中查找是否又该表的对应信息如不存在不创建，存在就按照tgrid2中的信息创建之后再通过对比报盘和穿件的表数据更新数据（这里只是创建表不作数据的更新）
							namebo.isExistTable(namebo.getTabid(),namebo.getRowInfoBGrid().size());
						}
					}else{
						TgridBo tgridBo=new TgridBo(this.con);
						tgridBo.execute_TT_table(namebo.getTabid(),namebo.getRowInfoBGrid().size());
					}
				}else{
					this.clew+="上报盘中的表"+tabid+":"+this.map.get(tabid)+"在本地不存在相关信息无法更新，请选择正确的上班盘！\\n";
				}				
			}else{
				if(param_map!=null){
					java.util.Iterator it = param_map.entrySet().iterator();
					ResultSetMetaData   rsmd =null;
					Table table=null;
					 switch(Integer.parseInt(paramscope)){
						 case  0:{
							 table=new Table(t_index);
							 break;
						 }
						 case  1:{
							 table=new Table(t_index+sortid);
							 break;
						 }
						 case  2:{
							 table=new Table(t_index+tabid);
						 }
					 }
					while (it.hasNext()){
						Map.Entry entry = (Map.Entry) it.next();
						String keys = (String) entry.getKey();
						HashMap map_values = (HashMap)entry.getValue();
						if(map_values!=null&&map_values.get("paramtype")!=null){
							if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))||((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.counts"))){
								if(map_values.get("paramscope")!=null&&map_values.get("paramename")!=null&&map_values.get("paramlen")!=null){
									int paramscope1 = Integer.parseInt(""+map_values.get("paramscope"));
									if(paramscope1==Integer.parseInt(paramscope)){
										
									}else{
										continue;
									}
									 switch(paramscope1){
										 case  0:{		
											 table=new Table(t_index);
											 Field field=null;
												if(dbWizard.isExistTable(t_index,false)){ 
													if(!dbWizard.isExistField(t_index,""+map_values.get("paramename"),false)){ 
														if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.STRING);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}else{
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.INT);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															 table.addField(field);
														}
														dbWizard.addColumns(table);
													}else{
														rs=dao.search(" select "+map_values.get("paramename")+" from "+t_index+"");
														   rsmd = rs.getMetaData();
														   int nlen=0;
														   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
									    		 				nlen=rsmd.getPrecision(1);
									    		 			else
									    		 				nlen=rsmd.getColumnDisplaySize(1);
														   if(nlen<Integer.parseInt(""+map_values.get("paramlen"))){
															   if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.STRING);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}else{
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.INT);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}
															   dbWizard.alterColumns(table);
														   }
													}
												}else{
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.STRING);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.counts"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.INT);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("report.parse.text"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.CLOB);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("report.parse.d"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.DATE);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.item.code"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.STRING);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
												}
												break;
										 }
										 case  1:{
											 table=new Table(t_index+sortid);
											 Field field=null;
												if(dbWizard.isExistTable(t_index+sortid,false)){ 
													if(!dbWizard.isExistField(t_index+sortid,""+map_values.get("paramename"),false)){ 
														if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.STRING);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}else{
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.INT);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															 table.addField(field);
														}
														dbWizard.addColumns(table);
													}else{
														rs=dao.search(" select "+map_values.get("paramename")+" from "+t_index+sortid);
														   rsmd = rs.getMetaData();
														   int nlen=0;
														   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
									    		 				nlen=rsmd.getPrecision(1);
									    		 			else
									    		 				nlen=rsmd.getColumnDisplaySize(1);
														   if(nlen<Integer.parseInt(""+map_values.get("paramlen"))){
															   if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.STRING);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}else{
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.INT);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}
															   dbWizard.alterColumns(table);
														   }
													}
												}else{
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.STRING);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.counts"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.INT);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("report.parse.text"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.CLOB);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("report.parse.d"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.DATE);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.item.code"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.STRING);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
												}
												break;
											 }
										 case  2:{
											 table=new Table(t_index+tabid);
											 Field field=null;
												if(!dbWizard.isExistTable(t_index+tabid,false)){ 
													if(!dbWizard.isExistField(t_index+tabid,""+map_values.get("paramename"),false)){ 
														if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.STRING);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}else{
															 field=new Field(""+map_values.get("paramename"));
															 field.setDatatype(DataType.INT);
															 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
															table.addField(field);
														}
														 dbWizard.addColumns(table);
													}else{
														   rs=dao.search(" select "+map_values.get("paramename")+" from "+t_index+tabid);
														   rsmd =rs.getMetaData();
														   int nlen=0;
														   if(Sql_switcher.searchDbServer()==Constant.MSSQL)
									    		 				nlen=rsmd.getPrecision(1);
									    		 			else
									    		 				nlen=rsmd.getColumnDisplaySize(1);
														   if(nlen<Integer.parseInt(""+map_values.get("paramlen"))){
															   if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.STRING);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}else{
																	 field=new Field(""+map_values.get("paramename"));
																	 field.setDatatype(DataType.INT);
																	 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
																	table.addField(field);
																}
															   dbWizard.alterColumns(table);
														   }
													}
												}else{
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.STRING);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.counts"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.INT);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("report.parse.text"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.CLOB);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("report.parse.d"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.DATE);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
													if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.item.code"))){
														 field=new Field(""+map_values.get("paramename"));
														 field.setDatatype(DataType.STRING);
														 field.setLength(Integer.parseInt(""+map_values.get("paramlen")));
														 table.addField(field);
													}
												}
												break;
											}
									 }
								}
							}
						}						
					}
					DBMetaModel dbmodel=new DBMetaModel(this.con);
					switch(Integer.parseInt(paramscope)){
					 case  0:{
						 if(!dbWizard.isExistTable(t_index,false)){
							 if(table.isEmpty()){
								 
							 }else{
								 dbWizard.createTable(table);
								 dbmodel.reloadTableModel(t_index);
							 }
						 }						
						 break;
					 }
					 case  1:{
						 if(!dbWizard.isExistTable(t_index+sortid,false)){
							 if(table.isEmpty()){
								 
							 }else{
								 dbWizard.createTable(table);
								 dbmodel.reloadTableModel(t_index+sortid);
							 }
						 }
						 break;
					 }
					 case  2:{
						 if(!dbWizard.isExistTable(t_index+tabid,false)){
							 if(table.isEmpty()){
								 
							 }else{
								 dbWizard.createTable(table);
								 dbmodel.reloadTableModel(t_index+tabid);
							 }
						 }
						 break;
					 }
				 }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 
	 * @param con
	 * @param tabid
	 *            报表ID
	 * @return 表类ID
	 */
	public   String getSortId(Connection con, String tabid) {
		String sortid = "";
		ContentDAO dao = null;
        dao = new ContentDAO(con);
		ResultSet rs = null;
		try {
			rs = dao.search("select tsortid from tname where tabid = "
					+ tabid);
			if (rs.next()) {
				sortid = rs.getString(1);
			}
			return sortid;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
		return null;
	}
	
	
	
	/**
	 * 删除treport_ctrl填报单位为unitcode的记录
	 * 
	 * @param con
	 * @param unitcode
	 *            填报单位编码
	 * @return
	 */
	public  int deleteRreport_ctrl(Connection con, String unitcode,
			String tabid) throws SQLException {
		int num = 0;
		String sql ="delete from treport_ctrl where unitcode = '"+ unitcode + "' and tabid = " + tabid;
		//System.out.println(sql);
		ContentDAO dao = new ContentDAO(con);
		num = dao.update(sql);
		return num;
	}

	/**
	 * 删除填表单位
	 * @param con
	 * @param unitcode
	 *            填报单位编码
	 */
	public int deleteUnitcode(Connection con, String unitcode)
			throws SQLException {
		int num = 0;
		//String sql ="delete from tt_organization where unitcode = '"+ unitcode + "'";
		String sql ="delete from tt_organization where unitcode like '"+ unitcode + "%'";
		//System.out.println(sql);
		ContentDAO dao = new ContentDAO(con);
		num = dao.update(sql);
		return num;
	}
	
	/**
	 * 删除填表单位
	 * @param con
	 * @param unitcode
	 *            填报单位编码
	 */
	public   int deleteUnitcode2(Connection con, String unitcode)
			throws SQLException {
		int num = 0;
		String sql ="delete from tt_organization where unitcode = '"+ unitcode + "'";
		//System.out.println(sql);
		ContentDAO dao = new ContentDAO(con);
		num = dao.update(sql);
		return num;
	}

	/**
	 * 添加权限
	 * 
	 * @param con
	 * @param sortid
	 *            报表类别
	 * @return
	 * @throws SQLException
	 */
	public   int updateSortIdPurview(Connection con, String sortid,
			String unitcode) throws SQLException {
		int num = 0;
		ContentDAO dao = new ContentDAO(con);
		RowSet rs = dao
				.search("select reporttypes from tt_organization where unitcode = '"+ unitcode + "'");
		if (rs != null && rs.next()) {
			String reporttypes = Sql_switcher.readMemo(rs, "reporttypes");
			String temp=reporttypes + sortid + ",";
			String[] temps=temp.split(",");
			HashSet set=new HashSet();
			for(int i=0;i<temps.length;i++)
			{	
				if(temps[i]!=null&&temps[i].trim().length()>0)
					set.add(temps[i]);
			}
			StringBuffer reportTypes=new StringBuffer("");
			for(Iterator t=set.iterator();t.hasNext();)
				reportTypes.append((String)t.next()+",");
			num = dao.update("update tt_organization set reporttypes = '"
					+ reportTypes.toString()+ "' where unitcode='" + unitcode
					+ "'");
		}
		return num;
	}


	public   boolean isUnitCode(Connection con,String unitcode)throws SQLException {
			boolean flag=false;
			int num = 0;
			ContentDAO dao = new ContentDAO(con);
			String sql="select * from  tt_organization where unitcode='"+ unitcode+ "'";
			RowSet rowSet=dao.search(sql);
			if(rowSet.next())
				flag=true;
			return flag;
		
	}
	
	
	/**
	 * 追加填报单位
	 * @param con
	 * @param unitcode
	 * @param parent_unitcode
	 * @param unitname
	 * @return
	 * @throws SQLException
	 */
	public   int insertUnitcode(Connection con, String unitcode,
			String parent_unitcode, String unitname) throws SQLException {
		int num = 0;
		ContentDAO dao = new ContentDAO(con);
		String sql="insert into tt_organization(unitcode,unitname,parentid, unitid) values('"
			+ unitcode
			+ "','"
			+ unitname
			+ "','"
			+ parent_unitcode
			+ "'," + getUnitID(con) + ")";
		
		//System.out.println(sql);
		
		num = dao.update(sql);
		return num;
	}

	/**
	 * 更新tt_organization unitcode值
	 * 
	 * @param con
	 * @param unitcode
	 * @param old_unitcode
	 * @return
	 * @throws SQLException
	 */
	public   int updateUnitcode(Connection con, String unitcode,
			String old_unitcode) throws SQLException {
		int num = 0;
		ContentDAO dao = new ContentDAO(con);
		num = dao.update("update tt_organization set unitcode = '" + unitcode
				+ "' where unitcode = '" + old_unitcode + "'");
		return num;
	}

	/**
	 * tt_organization 添加时需要维护unitid自动加1该方法为得到新unitid
	 * 
	 * @param con
	 * @return 得到新的unitid即表中最大unitid加1
	 * @throws GeneralException
	 */
	public static synchronized String getUnitID(Connection con)
			throws SQLException {
		int num = 1;
		String sql = "select max(unitid) as num  from tt_organization";
		ContentDAO dao = new ContentDAO(con);
		RowSet rs = null;
		rs = dao.search(sql.toString());
		if (rs.next()) {
			num = rs.getInt("num");
		}
		return String.valueOf(num + 1);
	}

	/**
	 * 向treport_ctrl表添加unitcode,tabid和status值
	 * 
	 * @param con
	 * @param unitcode
	 * @param tabid
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	public   int insertReportCtrl(Connection con, String unitcode,String tabid, String status) throws SQLException {
		int num = 0;
		ContentDAO dao = new ContentDAO(con);
		RowSet rs = dao.search(" select status from treport_ctrl where unitcode='"+unitcode+"' and tabid="+tabid);
		if(!rs.next()){
			dao.update("insert into treport_ctrl(unitcode, tabid, status) values('"+ unitcode + "'," + tabid + "," + status + ")");
		}else{
			if("1".equals(rs.getString("status"))){
				  this.setClew(this.getClew()+"上报盘中的表"+tabid+"已上报，不允许接收！\\n");	
				  num =1;
				  return 1;
				}
			if("-1".equals(rs.getString("status"))){
				dao.update("update  treport_ctrl set status=0 where unitcode='"+unitcode+"' and tabid="+tabid);	
			}
		}
		rs.close();
		// System.out
		// .println("insert into treport_ctrl(unitcode, tabid, status) values('"
		// + unitcode + "'," + tabid + "," + status + ")");
		return num;
	}

	/**
	 * 
	 * @return xml文件是否通过效验
	 */
	public boolean checkXml() {
		boolean flg = false;

		return flg;
	}

	
	/**
	 * 更新报表状态
	 * 
	 * @param tabid
	 * @param unitcode
	 * @return
	 * @throws SQLException
	 */
	public boolean updateStatus(String tabid, String unitcode)
			throws SQLException {
		boolean flg = false;
		ContentDAO dao = null;
		try(
				Connection conn = AdminDb.getConnection();
		) {
			dao = new ContentDAO(conn);
			int num = dao.update("update treport_ctrl set status = 1 where unitcode = '"
							+ unitcode + "' and tabid = " + tabid);
			if (num == 1) {
				flg = true;
			} else {
				flg = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flg;
	}

	

	/**
	 * 
	 * @param paramscope
	 *            类型(0 全局， 1 表类 ，2 表内,3 报表数据)0到2为参数类型4为报表数据
	 * @param element
	 *            当paramscope = 2或3时起作用，该参数为表内参数tp_table或报表数据records在xml中的根节点
	 */
	public void parseParemElement(String paramscope, Element element)
			throws GeneralException {
		try {
			NodeList list = null;
			if ("0".equals(paramscope)) {
				list = hrp_reports.getElementsByTagName("tp_global");
			}
			if ("1".equals(paramscope)) {
				list = hrp_reports.getElementsByTagName("tp_style");
			}
			if ("2".equals(paramscope)) {
				list = element.getElementsByTagName("tp_table");
			}
			if ("3".equals(paramscope)) {
				list = element.getElementsByTagName("records");
			}
			
			Element node = (Element) list.item(0);
			
			String record_columns = "";
			if ("3".equals(paramscope)) {
				if (node.hasAttributes()) {
					record_columns = node.getAttributes().getNamedItem("columns").getNodeValue();
				}
			}
			
			NodeList paremeterList = node.getChildNodes();
			
			if (paremeterList == null){
				return;
			}
			
			Hashtable hm = new Hashtable();		
			// hm中存储报表参数包括全局，表类和表内
			hm.put("unitcode", "'" + unitcode + "'");
			
			Hashtable hm_record = new Hashtable();
			// hm_record中存储报表数据
			hm_record.put("unitcode", "'" + unitcode + "'");
			
			TgridBo gridbo = new TgridBo(con);
			String sortid2 = getSortId(con, tabid);
			
			
						
			for (int i = 0; i < paremeterList.getLength(); i++) {
				
				if ("record".equals(paremeterList.item(i).getNodeName().trim())) {//如果是数据
					if ("3".equals(paramscope)) {
						String values = paremeterList.item(i).getFirstChild()
								.getNodeValue();
						String[] col = record_columns.split("`");
						String[] value = values.split("`");
						if(operateObject!=null&& "1".equals(operateObject)){
							gridbo.execute_TB_table(tabid, col.length);
						}else{
							gridbo.execute_TT_table(tabid, col.length);
						}
						boolean flag2=false;
						for (int j = 0; j < col.length; j++) {
							if ("secid".equals(col[j].trim().toLowerCase())) {
								if((","+strsecid+",").indexOf(","+value[j].trim()+",")!=-1){//xgq
									flag2=true;
									break;//xgq
								}
							}
							if ("username".equals(col[j].trim().toLowerCase())) {
								continue;   //xieguiquan  2010/08/16
								}
							if ("unitcode".equals(col[j].trim().toLowerCase())) {
							//	hm_record.put(col[j], "'" + value[j] + "'");   //dengcan  2008/03/26
							} else {
								hm_record.put(col[j], value[j]);
							}
						}
						if(flag2){
							continue;
						}
						saveParameter(unitcode, hm_record, paramscope);
						continue;
					}
				}
				
				if (paremeterList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					if (paremeterList.item(i).getChildNodes().getLength() > 0) {
						
						String name = paremeterList.item(i).getAttributes().getNamedItem("name").getNodeValue();
						String type = paremeterList.item(i).getAttributes().getNamedItem("type").getNodeValue();
						
						
						String value = "";
						if (paremeterList.item(i).getFirstChild() != null){
							value = paremeterList.item(i).getFirstChild().getNodeValue();
						}
						if ("日期".equals(type.trim())){//wangcq 2014-12-02
							if(value.indexOf(" ") != -1)
							    value = value.substring(0, value.indexOf(" "));
							value = Sql_switcher.charToDate("'" + value + "'");
						} else if (!"数值".equals(type.trim())) {
							value = "'" + value + "'";
						}
					//	System.out.println("name=" +  name + " type=" + type + " value= " + value);
						
					
							if ("1".equals(paramscope)) {
								if(paremeterList.item(i).getAttributes().getNamedItem("sortid")!=null){
									String sortid = paremeterList.item(i).getAttributes().getNamedItem("sortid").getNodeValue();
									if(sortid!=null){
										if(sortid.equals(sortid2)){
											hm.put(name, value);
											continue;
										}else{
											continue;
										}										
									}
								}
							}
						
						hm.put(name, value);
					}
				}
			} // end for
			
			//保存参数信息
			if (paremeterList.getLength() > 0 && hm.size() > 1) {
				saveParameter(unitcode, hm, paramscope);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 
	 * @param con
	 *            数据库连接
	 * @param tablename
	 *            表名
	 * @return 数据库中是否存在表名为tablename的表
	 */
	public   boolean tableExists(Connection con, String tablename)
			throws SQLException {
		boolean flg = false;
		String[] type = new String[1];
		type[0] = "table";
		ResultSet rs = null;
		Connection conn=null;
		try
		{
			conn=AdminDb.getConnection();
			rs = conn.getMetaData().getTables(null, null, "law_base_file", type);
			while (rs.next()) {
				if (rs.getString("table_name").trim().equals(tablename.trim())) {
					flg = true;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
				if(conn!=null)
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return flg;

	}

	public void finallize() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param unitcode
	 *            机构ID
	 * @param map
	 *            包括字段名和值的map
	 * @param paramscope
	 *            参数类型(0 全局， 1 表类 ，2表内)
	 */
	public void saveParameter(String unitcode, Hashtable map, String paramscope)
			throws SQLException {
		String tablename = "";
		
		StringBuffer updateSql = new StringBuffer();
		StringBuffer insertSql = new StringBuffer();
		
		Enumeration x = map.keys();
		
		ContentDAO dao = new ContentDAO(con);
		
		
		if(operateObject!=null&& "1".equals(operateObject)){
			if ("0".equals(paramscope)) {
				tablename = "tp_p";
			}
			if ("1".equals(paramscope)) {
				tablename = "tp_s" + getSortId(con, tabid);
			}
			if ("2".equals(paramscope)) {
				tablename = "tp_t" + tabid;
			}
			if ("3".equals(paramscope)) {
				tablename = "tb" + tabid;
			}	
		}else{
			if ("0".equals(paramscope)) {
				tablename = "tt_p";
			}
			if ("1".equals(paramscope)) {
				tablename = "tt_s" + getSortId(con, tabid);
			}
			if ("2".equals(paramscope)) {
				tablename = "tt_t" + tabid;
			}
			if ("3".equals(paramscope)) {
				tablename = "tt_" + tabid;
			}
		}
		RowSet rs=null;
		String existstr=",";
	
		try{
			rs =  dao.search(" select * from "+tablename+" where 1=2");
		}catch(Exception e){
			this.clew =  tablename+" 表不存在！";
			e.printStackTrace();
			if(rs!=null)
			rs.close();
			throw new SQLException(this.clew);
		}
		
		
		
		ResultSetMetaData metaData=rs.getMetaData();
		int c_count=metaData.getColumnCount();
		for(int a=0;a<c_count;a++){
			existstr+=metaData.getColumnName(a+1).toLowerCase()+",";
		}
		
		
		StringBuffer insertvalue = new StringBuffer("");
		
		updateSql.append("update " + tablename + " set ");
		insertSql.append("insert into " + tablename + "(");
		
		boolean flg = false;
		while (x.hasMoreElements()) {
			String key = x.nextElement().toString();
			if (flg) {
				if(existstr.indexOf(","+key.toLowerCase()+",")!=-1|| "unitcode".equalsIgnoreCase(key.toLowerCase())){
				updateSql.append(",");
				insertSql.append(",");
				insertvalue.append(",");
				}
			} 
			String value = "";
			if ("unitcode".equalsIgnoreCase(key)) {
				if(operateObject!=null&& "1".equals(operateObject)){
					value = "'" + this.username + "'";	
				}else
					value = "'" + unitcode + "'";
			} else {
				value = (String) map.get(key);
			}
			if("3".equals(paramscope)&&operateObject!=null&& "1".equals(operateObject)&& "unitcode".equalsIgnoreCase(key)){
				if(existstr.indexOf(","+key.toLowerCase()+",")!=-1|| "unitcode".equalsIgnoreCase(key.toLowerCase())){
				updateSql.append("username =" + value);
				insertSql.append("username");
				flg =true;
				}
			}else{
				if(existstr.indexOf(","+key.toLowerCase()+",")!=-1|| "unitcode".equalsIgnoreCase(key.toLowerCase())){
				updateSql.append(key.toLowerCase() + "=" + value);
				insertSql.append(key.toLowerCase());
				flg = true;
				}
			}
			if(existstr.indexOf(","+key.toLowerCase()+",")!=-1|| "unitcode".equalsIgnoreCase(key.toLowerCase()))
			insertvalue.append(value);
		}
		insertSql.append(") values (" + insertvalue.toString() + ")");
		if(operateObject!=null&& "1".equals(operateObject)){
			if("3".equals(paramscope))
				updateSql.append(" where username = '" + this.username + "'");
			else
				updateSql.append(" where unitcode = '" + this.username + "'");
		}else
			updateSql.append(" where unitcode = '" + unitcode + "'");
		
		if(operateObject!=null&& "1".equals(operateObject)){
			if("3".equals(paramscope))
			 rs = dao.search("select * from " + tablename+ " where username = '" + this.username + "'");
			else
				rs = dao.search("select * from " + tablename+ " where unitcode = '" + this.username + "'");	
		}else
			 rs = dao.search("select * from " + tablename+ " where unitcode = '" + unitcode + "'");
		/*
		System.out.println("参数信息开始..............................");
		System.out.println(updateSql.toString());
		System.out.println(insertSql.toString());
		System.out.println("参数信息结束...........................");
	
		*/
		
	
		
		if ("3".equals(paramscope)) {
		//	System.out.println(insertSql.toString());
			
			dao.update(insertSql.toString());
			//处理小数位
		
		} else {
			if (rs != null && rs.next()) {
				dao.update(updateSql.toString());
			} else {
				dao.update(insertSql.toString());
			}
		}
		if(rs!=null)
			rs.close();
	
	}
	public ArrayList getUpTbxxByRowLimit(TnameBo namebo,String tabid){
		DbWizard dbwizard=new DbWizard(con);
		ArrayList updaterowlimit = new ArrayList();
		if(dbwizard.isExistTable("tb"+tabid, false)){
			ArrayList colInfoList =namebo.getColInfoList();
			ArrayList rowInfoList = namebo.getRowInfoList();
			ReportResultBo resultbo = new ReportResultBo(this.con);
			ArrayList resultlist =resultbo.getTBxxResultList(this.tabid, this.username);
			String datevalue = namebo.getOwnerDate(tabid);
			for(int j=0;j<colInfoList.size();j++)
			{ 
				ArrayList colTermList=(ArrayList)colInfoList.get(j);
				
				int fromIndex=0;
				int toIndex=0;
				String comp="";
				boolean upflag =false;
				for(Iterator t=colTermList.iterator();t.hasNext();)
				{
					String[] temp=(String[])t.next();
					if(temp[5].trim().length()>0){
						if(temp[5].toUpperCase().indexOf("<ROWDATE>")!=-1&&temp[5].toUpperCase().indexOf("</ROWDATE>")!=-1&&temp[5].toUpperCase().indexOf("<ROWDATETYPE>")!=-1&&temp[5].toUpperCase().indexOf("</ROWDATETYPE>")!=-1)
						{
							fromIndex=temp[5].toUpperCase().indexOf("<ROWDATE>");
						 toIndex=temp[5].toUpperCase().indexOf("</ROWDATE>");
						String te1 =temp[5].toUpperCase().substring(fromIndex+9,toIndex).trim();
						 fromIndex=temp[5].toUpperCase().indexOf("<ROWDATETYPE>");
						 toIndex=temp[5].toUpperCase().indexOf("</ROWDATETYPE>");
						String te2 =temp[5].toUpperCase().substring(fromIndex+13,toIndex).trim();
						
							String value=datevalue;
							int app = 0;
//							int start = 0;
							if (value != null && value.length() > 7)
								app = Integer.parseInt(value.substring(5, 7));
							if (te2.indexOf("M")!=-1&&app != 0) {
								if(te1.equals(""+app)){
									upflag=false;
									
								}else{
									upflag=true;
									break;
								}
							}
							if(te2.indexOf("Q")!=-1&&app != 0){
								if(0<app&&app<4)
									comp="1";
								if(3<app&&app<7)
									comp="2";
								if(6<app&&app<10)
									comp="3";
								if(9<app&&app<13)
									comp="4";
								if(te1.equals(""+comp)){
									upflag=false;
									
								}else{
									upflag=true;
									break;
								}
							}	
							if(te2.indexOf("Y")!=-1&&app != 0){
								if(0<app&&app<7)
									comp="1";
								if(6<app&&app<13)
									comp="2";
								if(te1.equals(""+comp)){
									upflag=false;
									
								}else{
									upflag=true;
									break;
								}
							}
					}
					}
				}
				if(upflag&&resultlist.size()>=j){
					String[] temp3=(String[])resultlist.get(j);
					String upstr="";
					for(int a=0;a<rowInfoList.size();a++){
						 upstr = "update tb"+tabid+" set C"+(a+1)+"="+temp3[a]+" where lower(userName)='"+this.username+"' and secid="+(j+1);
						 updaterowlimit.add(upstr);
					}
			  		

			  	}
			}
		}
		return updaterowlimit;
	}
	public String  getRowLimit(TnameBo namebo,String tabid){	//xgq
		StringBuffer strsecid=new StringBuffer();
		DbWizard dbwizard=new DbWizard(con);
		ArrayList updaterowlimit = new ArrayList();
		if(dbwizard.isExistTable("tb"+tabid, false)){
			ArrayList colInfoList =namebo.getColInfoList();
			ArrayList rowInfoList = namebo.getRowInfoList();
			ReportResultBo resultbo = new ReportResultBo(this.con);
			ArrayList resultlist =resultbo.getTBxxResultList(this.tabid, this.username);
			String datevalue = namebo.getOwnerDate(tabid);
			for(int j=0;j<colInfoList.size();j++)
			{ 
				ArrayList colTermList=(ArrayList)colInfoList.get(j);
				
				int fromIndex=0;
				int toIndex=0;
				String comp="";
				boolean upflag =false;
				for(Iterator t=colTermList.iterator();t.hasNext();)
				{
					String[] temp=(String[])t.next();
					if(temp[5].trim().length()>0){
						if(temp[5].toUpperCase().indexOf("<ROWDATE>")!=-1&&temp[5].toUpperCase().indexOf("</ROWDATE>")!=-1&&temp[5].toUpperCase().indexOf("<ROWDATETYPE>")!=-1&&temp[5].toUpperCase().indexOf("</ROWDATETYPE>")!=-1)
						{
							fromIndex=temp[5].toUpperCase().indexOf("<ROWDATE>");
						 toIndex=temp[5].toUpperCase().indexOf("</ROWDATE>");
						String te1 =temp[5].toUpperCase().substring(fromIndex+9,toIndex).trim();
						 fromIndex=temp[5].toUpperCase().indexOf("<ROWDATETYPE>");
						 toIndex=temp[5].toUpperCase().indexOf("</ROWDATETYPE>");
						String te2 =temp[5].toUpperCase().substring(fromIndex+13,toIndex).trim();
						
							String value=datevalue;
							int app = 0;
//							int start = 0;
							if (value != null && value.length() > 7)
								app = Integer.parseInt(value.substring(5, 7));
							if (te2.indexOf("M")!=-1&&app != 0) {
								if(te1.equals(""+app)){
									upflag=false;
									
								}else{
									upflag=true;
									break;
								}
							}
							if(te2.indexOf("Q")!=-1&&app != 0){
								if(0<app&&app<4)
									comp="1";
								if(3<app&&app<7)
									comp="2";
								if(6<app&&app<10)
									comp="3";
								if(9<app&&app<13)
									comp="4";
								if(te1.equals(""+comp)){
									upflag=false;
									
								}else{
									upflag=true;
									break;
								}
							}	
							if(te2.indexOf("Y")!=-1&&app != 0){
								if(0<app&&app<7)
									comp="1";
								if(6<app&&app<13)
									comp="2";
								if(te1.equals(""+comp)){
									upflag=false;
									
								}else{
									upflag=true;
									break;
								}
							}
					}
					}
				}
				if(upflag&&resultlist.size()>=j){
					strsecid.append(j+1);
					strsecid.append(",");
			  	}
			}
		}
		String str="";
			str = strsecid.toString();
			if(str.length()>1)
				str = str.substring(0, str.length()-1);
		return str;
	}
	
	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getOperateObject() {
		return operateObject;
	}

	public void setOperateObject(String operateObject) {
		this.operateObject = operateObject;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public String getReport() {
		return report;
	}
	public void setReport(String report) {
		this.report = report;
	}
	public HashMap getMap() {
		return map;
	}
	public void setMap(HashMap map) {
		this.map = map;
	}
	public Connection getCon() {
		return con;
	}
	public void setCon(Connection con) {
		this.con = con;
	}
	public Element getHrp_reports() {
		return hrp_reports;
	}
	public void setHrp_reports(Element hrp_reports) {
		this.hrp_reports = hrp_reports;
	}
	public String getClew() {
		return clew;
	}
	public void setClew(String clew) {
		this.clew = clew;
	}
}
