package com.hjsj.hrms.module.muster.showmuster.businessobject.impl;

import com.hjsj.hrms.businessobject.general.muster.ExecuteExcel;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hjsj.hrms.module.muster.mustermanage.businessobject.MusterManageService;
import com.hjsj.hrms.module.muster.mustermanage.businessobject.impl.MusterManageServiceImpl;
import com.hjsj.hrms.module.muster.showmuster.businessobject.ShowManageService;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.jfree.util.Log;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ShowManageServiceImpl implements ShowManageService{
	private Connection conn;
	private UserView userView;
	public  ShowManageServiceImpl(Connection conn, UserView userView) {
		super();
		this.conn = conn;
		this.userView = userView;
	}

	@Override
	public Map getMusterPageConfig(String tabid) throws GeneralException {
		Map map=new HashMap();
		Document doc;
		ResultSet rs = null;
		try{
			/**
			 * 初始化Document
			 */
			String lhead="";
			String mhead="";
			String rhead="";
			String lfoot="";
			String mfoot="";
			String rfoot="";
			
			String xml = "";
			ContentDAO dao  = new ContentDAO(conn);
			String sql = "select lhead,mhead,rhead,lfoot,mfoot,rfoot,xml_style from LName where Tabid=?";
			ArrayList<String> values = new ArrayList<String>();
			values.add(tabid);
			rs = dao.search(sql,values);
			while(rs.next()){
				xml = Sql_switcher.readMemo(rs,"xml_style");
				lhead=rs.getString("lhead");
				mhead= rs.getString("mhead");
				rhead= rs.getString("rhead");
				lfoot= rs.getString("lfoot");
				mfoot= rs.getString("mfoot");
				rfoot= rs.getString("rfoot");
			}
			if(xml==null||"".equals(xml)){
    			xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>  <report> </report>  ";
    		}
			ReportParseXml readXml=new ReportParseXml();
			ReportParseVo reportParseVo=readXml.ReadOutParseXml(xml,"/report");
			 // 页面设置
			 map.put("reportParseVo",reportParseVo);
			 map.put("lhead", lhead==null?"":lhead);
			 map.put("mhead", mhead==null?"":mhead);
			 map.put("rhead", rhead==null?"":rhead);
			 map.put("lfoot", lfoot==null?"":lfoot);
			 map.put("mfoot", mfoot==null?"":mfoot);
			 map.put("rfoot", rfoot==null?"":rfoot);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		return map;
	}

	@Override
	public Map saveMusterPageConfig(Map data) throws GeneralException {
		Map result=new HashMap();
		ByteArrayInputStream input = null;
		try {
			String xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?> <report></report>";
			byte[] bt =xml.getBytes();
			input=new ByteArrayInputStream(bt);
			Document doc = PubFunc.generateDom(input);
			
			/**
			 * 页面设置标签数据
			 */
			Element report = doc.getRootElement();
			report.setAttribute("name",userView==null?"":userView.getUserName());
			report.setAttribute("pagetype",data.get("pagetype")==null?"":data.get("pagetype").toString());
			report.setAttribute("width",data.get("page_width")==null?"":data.get("page_width").toString());
			report.setAttribute("height",data.get("page_height")==null?"":data.get("page_height").toString());
			report.setAttribute("orientation",data.get("page_range")==null?"":data.get("page_range").toString());
			report.setAttribute("top",data.get("pagemargin_top")==null?"":data.get("pagemargin_top").toString());
			report.setAttribute("bottom",data.get("pagemargin_bottom")==null?"":data.get("pagemargin_bottom").toString());
			report.setAttribute("left",data.get("pagemargin_left")==null?"":data.get("pagemargin_left").toString());
			report.setAttribute("right",data.get("pagemargin_right")==null?"":data.get("pagemargin_right").toString());
			
			/**
			 * 标题数据
			 */
			Element title = new Element("title");
			title.setAttribute("content",(String) data.get("title"));
			report.addContent(title);
			
			/**
             * 页头数据
             */
			Element head = new Element("head");
			head.setAttribute("content",data.get("head").toString());
			report.addContent(head);
			
			/**
             * 页尾数据
             */
			Element tile = new Element("tile");
			tile.setAttribute("content",data.get("tile").toString());
			report.addContent(tile);
			
			/**
             * 正文数据
             */
			Element body = new Element("body");
			body.setAttribute("content",data.get("body").toString());
			report.addContent(body);
			
			XMLOutputter outputter = new XMLOutputter();
        	Format format=Format.getPrettyFormat();
        	format.setEncoding("UTF-8");
        	outputter.setFormat(format);
        	String newXML=outputter.outputString(doc);
        	
			ContentDAO dao = new ContentDAO(this.conn);
			String sql = "update LName set Lhead=?,Mhead=?,Rhead=?,Lfoot=?,Mfoot=?,Rfoot=?,xml_style=? where tabid=?";
			ArrayList values=new ArrayList();
			values.add(data.get("lhead"));
			values.add(data.get("mhead"));
			values.add(data.get("rhead"));
			values.add(data.get("lfoot"));
			values.add(data.get("mfoot"));
			values.add(data.get("rfoot"));
			values.add(newXML);
			values.add(data.get("tabid").toString());
			
			int res=dao.update(sql,values);
			if(res==1) {
				result.put("result", "sucess");
				return result; 
			}else {
				result.put("result", "fail");
				return result; 
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(input);
		}
		return null;
	}
	@Override
	public ArrayList getButtonList(String musterType) {
		ArrayList buttonList  = new ArrayList();
		try {
		    if (userView.isSuper_admin()) {//超级用户
                ButtonInfo buttonInfo2 = new ButtonInfo(ResourceFactory.getProperty("muster.exportExcel"), "showMuster.exportExcelOrPdf('2')");
                buttonInfo2.setId("exportExcel");
                buttonList.add(buttonInfo2);
                ButtonInfo buttonInfo3 = new ButtonInfo(ResourceFactory.getProperty("muster.exportPdf"), "showMuster.exportExcelOrPdf('1')");
                buttonInfo3.setId("exportPdf");
                buttonList.add(buttonInfo3);
                ButtonInfo buttonInfo4 = new ButtonInfo(ResourceFactory.getProperty("muster.dataRange"), "showMuster.dataRangeFunc");
                buttonInfo4.setId("dataRange");
                buttonList.add(buttonInfo4);
            }else if ("1".equals(musterType)) {//员工花名册
                if (userView.hasTheFunction("2603107")||userView.hasTheFunction("030907")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.exportExcel"), "showMuster.exportExcelOrPdf('2')");
                    buttonInfo.setId("exportExcel");
                    buttonList.add(buttonInfo);
                }
                if (userView.hasTheFunction("2603108")||userView.hasTheFunction("030908")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.exportPdf"), "showMuster.exportExcelOrPdf('1')");
                    buttonInfo.setId("exportPdf");
                    buttonList.add(buttonInfo);
                }
                if (userView.hasTheFunction("2603109")||userView.hasTheFunction("030909")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.dataRange"), "showMuster.dataRangeFunc");
                    buttonInfo.setId("dataRange");
                    buttonList.add(buttonInfo);
                }
            }else if ("2".equals(musterType)) {//单位花名册
                if (userView.hasTheFunction("2303107")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.exportExcel"), "showMuster.exportExcelOrPdf('2')");
                    buttonInfo.setId("exportExcel");
                    buttonList.add(buttonInfo);
                }
                if (userView.hasTheFunction("2303108")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.exportPdf"), "showMuster.exportExcelOrPdf('1')");
                    buttonInfo.setId("exportPdf");
                    buttonList.add(buttonInfo);
                }
                if (userView.hasTheFunction("2303109")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.dataRange"), "showMuster.dataRangeFunc");
                    buttonInfo.setId("dataRange");
                    buttonList.add(buttonInfo);
                }
            }else if ("3".equals(musterType)) {//岗位花名册
                if (userView.hasTheFunction("2503107")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.exportExcel"), "showMuster.exportExcelOrPdf('2')");
                    buttonInfo.setId("exportExcel");
                    buttonList.add(buttonInfo);
                }
                if (userView.hasTheFunction("2503108")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.exportPdf"), "showMuster.exportExcelOrPdf('1')");
                    buttonInfo.setId("exportPdf");
                    buttonList.add(buttonInfo);
                }
                if (userView.hasTheFunction("2503109")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.dataRange"), "showMuster.dataRangeFunc");
                    buttonInfo.setId("dataRange");
                    buttonList.add(buttonInfo);
                }
            }else if ("4".equals(musterType)) {//基准岗位花名册
                if (userView.hasTheFunction("2503107")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.exportExcel"), "showMuster.exportExcelOrPdf('2')");
                    buttonInfo.setId("exportExcel");
                    buttonList.add(buttonInfo);
                }
                if (userView.hasTheFunction("2503108")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.exportPdf"), "showMuster.exportExcelOrPdf('1')");
                    buttonInfo.setId("exportPdf");
                    buttonList.add(buttonInfo);
                }
                if (userView.hasTheFunction("2503109")){
                    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.dataRange"), "showMuster.dataRangeFunc");
                    buttonInfo.setId("dataRange");
                    buttonList.add(buttonInfo);
                }
            }
		    if (userView.isSuper_admin()) {//超级用户
                ButtonInfo buttonInfo1 = new ButtonInfo(ResourceFactory.getProperty("muster.pageset"), "showMuster.pageSetup");
                buttonInfo1.setId("pageset");
                buttonList.add(buttonInfo1);
            }else if ("1".equals(musterType)) {//员工花名册
                if (userView.hasTheFunction("2603105")||userView.hasTheFunction("030905")){
                    ButtonInfo buttonInfo  = new ButtonInfo(ResourceFactory.getProperty("muster.pageset"), "showMuster.pageSetup");
                    buttonInfo.setId("pageset");
                    buttonList.add(buttonInfo);
                }
            }else if ("2".equals(musterType)) {//单位花名册
               
                if (userView.hasTheFunction("2303105")){
                    ButtonInfo buttonInfo  = new ButtonInfo(ResourceFactory.getProperty("muster.pageset"), "showMuster.pageSetup");
                    buttonInfo.setId("pageset");
                    buttonList.add(buttonInfo);
                }
            }else if ("3".equals(musterType)) {//岗位花名册
                if (userView.hasTheFunction("2503105")){
                    ButtonInfo buttonInfo  = new ButtonInfo(ResourceFactory.getProperty("muster.pageset"), "showMuster.pageSetup");
                    buttonInfo.setId("pageset");
                    buttonList.add(buttonInfo);
                }
            }else if ("4".equals(musterType)) {//基准岗位花名册
                if (userView.hasTheFunction("2503105")){
                    ButtonInfo buttonInfo  = new ButtonInfo(ResourceFactory.getProperty("muster.pageset"), "showMuster.pageSetup");
                    buttonInfo.setId("pageset");
                    buttonList.add(buttonInfo);
                }
            }
		    ButtonInfo buttonInfo = new ButtonInfo(ResourceFactory.getProperty("muster.back"), "showMuster.returnMusterManage");
            buttonInfo.setId("back");
            buttonList.add(buttonInfo);
			ButtonInfo queryBox = new ButtonInfo();
			queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
			queryBox.setText(ResourceFactory.getProperty("muster.pleasetext"));
			queryBox.setId("pleasetext");
			queryBox.setFunctionId("MM01010002");
			buttonList.add(queryBox);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}

	@Override
	public ArrayList<ColumnsInfo> getColumnList(String tabid,String musterType) {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("");
			sql.append("select baseid,Field_name,ColHz,Align,field_type from Lbase where tabid = ");
			sql.append(tabid);
			sql.append(" order by baseid");
			rowSet=dao.search(sql.toString());
			ArrayList<String> baseidList = new ArrayList<String>();
			ArrayList<String> fieldItemList = new ArrayList<String>();
			while (rowSet.next()) {
				String[] fielditemid = rowSet.getString("Field_name").split("\\.");//指标
				String baseid = rowSet.getString("baseid");
				String fielditemdesc = rowSet.getString("ColHz");//指标名称
				String field_type = rowSet.getString("field_type");//指标类型
				fieldItemList.add(fielditemid[1]);
				FieldItem fielditem = DataDictionary.getFieldItem(fielditemid[1]);
				if (null==fielditem) {
					baseidList.add(baseid);
				}
				//无需再设置这些描述，直接从数据字典中读取就好了
				if (fielditem!=null) {
				    ContentDAO contentDAO = new ContentDAO(conn);
	                ArrayList<String> fieldlist =  getPrivFieldList(contentDAO, userView, musterType);
	                boolean flag = false;
	                String itemid = fielditemid[1].toUpperCase();
                	FieldItem fieldItem2 = DataDictionary.getFieldItem(itemid);
                	if (StringUtils.equals(itemid, "E01A1")&&"K01".equalsIgnoreCase(fieldItem2.getFieldsetid())) {
						flag = true;
					}
	                if (fieldlist.contains(fielditemid[1].toLowerCase())||flag) {
	                    ColumnsInfo info = new ColumnsInfo(fielditem);
	                    String itemtype = fielditem.getItemtype();
	                    info.setLocked(false);
	                    info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
	                    if ("UN".equals(fielditem.getCodesetid())||"UM".equals(fielditem.getCodesetid())
	                        ||"@K".equals(fielditem.getCodesetid())||"A0177".equals(fielditem.getCodesetid())) {//如果是代码型指标将自定义列宽
	                        info.setColumnWidth(200);
	                    }
	                    list.add(info);
	                    
	                }
				}
			}
			
			if (baseidList.size()>0) {
				StringBuffer delBuffer = new StringBuffer("");
				delBuffer.append("delete from lbase where tabid = ");
				delBuffer.append(tabid);
				delBuffer.append(" and baseid in ");
				delBuffer.append("(");
				for (int i = 0; i < baseidList.size(); i++) {
					if (i!=0) {
						delBuffer.append(",");
					}
					delBuffer.append(baseidList.get(i));
				}
				delBuffer.append(")");
				dao.delete(delBuffer.toString(), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return list;
	}

	@Override
	public String getDataSql(String tabid ,String moduleId,String musterType,HashMap<String, Object>  parMap) {
		StringBuffer sql = new StringBuffer("");
		StringBuffer sqlbuf = new StringBuffer("");;
		RowSet rowSet = null;
		HashMap<String, Object>  map = null;
		String flag = "";
		try {
			ContentDAO dao = new ContentDAO(conn);
			if (parMap==null) {
				map =getSqlCondition(dao, tabid,userView,musterType);
			}else {
				 flag = (String) parMap.get("flag");
				 if ("preview".equals(flag)) {//预览
					map = parMap;
				 }else {
					map =getSqlCondition(dao, tabid,userView,musterType);
				 }
			}
			boolean parttimejobvalue = (Boolean) map.get("parttimejobvalue");//是否显示兼职人员
			String sortField = (String) map.get("sortField");//排序指标
			String Nbases =  (String) map.get("Nbases");//人员库
			//String DataRange =  (String) map.get("DataRange");//数据范围
			String filterid = (String) map.get("filterid");//员工管理，模块常用查询的查询条件
			String range_type = (String) map.get("range_type");//0 当前记录 1 年月 2 指标条件
			String condition = (String) map.get("condition");//条件表达式
			if ("dataRang".equals(flag)) {//数据范围
				Nbases =  (String) parMap.get("Nbases");//人员库
				filterid =  (String) parMap.get("filterid");//员工管理，模块常用查询的查询条件
				range_type =  (String) parMap.get("range_type");//0 当前记录 1 年月 2 指标条件
				condition =  (String) parMap.get("condition");//条件表达式
				parttimejobvalue = (Boolean) parMap.get("parttimejobvalue");
			}
			boolean flag01 = (Boolean) map.get("flag01");//子集中是否有01
			ArrayList<String> fieldsetList = (ArrayList<String>) map.get("fieldsetList");//已选指标的子集 
			ArrayList<String> fieldlist = (ArrayList<String>) map.get("fieldlist");//已选择的指标
			String[] sortFieldArray = null;
			if (null!=sortField&&!StringUtils.equals(sortField, "")) {
				sortFieldArray =sortField.split(",");//排序的指标
			}
			if(null==Nbases) {
				Nbases = "Usr";
			}
			String[] nbseArray = Nbases.split(",");
			nbseArray=removaPriDb(userView, nbseArray);
			//sql.append(" select * from ( ");
			if ("1".equals(musterType)) {
				for (int i = 0; i < nbseArray.length; i++) {//分人员库 union all//只有人员才分库
					String nbase = nbseArray[i];
					if (i!=0) {
						sql.append(" union all ");
					}
					if(null==range_type) {
						range_type = "0";
					}
					String sqlByRangeType = getSqlbyRangeType(nbase, flag01, moduleId, filterid, conn,sortFieldArray, fieldsetList, fieldlist,range_type,condition,userView,musterType,parttimejobvalue,null);
				    if (StringUtils.isNotBlank(sqlByRangeType)) {
                        sql.append(sqlByRangeType);
                    }else {
                        sql.append("select a0100 from usra01 where 1=2 ");
                    }
				}
			}else {
				if(null==range_type) {
					range_type = "0";
				}
				String sqlByRangeType = getSqlbyRangeType("", flag01, moduleId, filterid, conn,sortFieldArray, fieldsetList, fieldlist,range_type,condition,userView,musterType,parttimejobvalue,null);
                if (StringUtils.isNotBlank(sqlByRangeType)) {
                    sql.append(sqlByRangeType);
                }else {
                    sql.append("select ");
                    sql.append(getSqlX0100("", musterType, ""));
                    sql.append(" from ");
                    sql.append(getSqlSet(musterType, ""));
                    sql.append(" where 1=2");
                }
                
			}
			sqlbuf.append("select * from (");
			sqlbuf.append("select ROW_NUMBER() OVER (ORDER BY ");
			sqlbuf.append(orderBySqlFormat(fieldlist,sortField,"1",flag01,musterType,"0"));
			sqlbuf.append(") AS xuhao,");
			sqlbuf.append(orderBySqlFormat(fieldlist,sortField,"0",flag01,musterType,"0"));
			sqlbuf.append(" from (");
			sqlbuf.append(sql);
			sqlbuf.append(") hmctable ) fianlly");
		//	sql.append(") hmctable");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return sqlbuf.toString();
	}

	@Override
	public String getOrderBySql(String tabid,String sortField,String musterType) {
		StringBuffer sql = new StringBuffer("");
		try {
			ContentDAO dao = new ContentDAO(conn);
			if (sortField==null||"".equals(sortField)) {
				HashMap<String, Object>  map =getSqlCondition(dao, tabid,userView,musterType);
				sortField = (String) map.get("sortField");
			}
			if (StringUtils.isNotBlank(sortField)) {
				String[] sortFieldArray = sortField.split(",");
				ContentDAO contentDAO = new ContentDAO(conn);
                ArrayList<String> fieldlist =  getPrivFieldList(contentDAO, userView, musterType);
                ArrayList<String> sortFieldList = new ArrayList<String>(Arrays.asList(sortFieldArray));
                for (int i = sortFieldList.size()-1; i >-1; i--) {
                    if (!fieldlist.contains(sortFieldList.get(i).split("\\.")[1].substring(0, sortFieldList.get(i).split("\\.")[1].length()-1).toLowerCase())) {
                        sortFieldList.remove(i);
                    }
                }
				if (sortFieldList.size()>0) {
					sql.append(" order by ");
					for (int j = 0; j < sortFieldList.size(); j++) {
						if (j!=0) {
							sql.append(",");
						}
						String itemlength = sortFieldList.get(j);
						String flag = itemlength.substring(itemlength.length()-1, itemlength.length());
						String itemid = itemlength.substring(0,itemlength.length()-1);
						sql.append(itemid.split("\\.")[1]);
						if ("1".equals(flag)) {
							sql.append(" desc");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql.toString();
	}

	@Override
	public String getMusterName(String tabid) {
		String name =null;
		RowSet rowSet = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sqlbuff = new StringBuffer("");
			sqlbuff.append("select hzname from lname where tabid = ");
			sqlbuff.append(tabid);
			rowSet=dao.search(sqlbuff.toString());
			if (rowSet.next()) {
				name = rowSet.getString("hzname");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return name;
	}
	/**
	 * 查询花名册权限对应的人员
	 * @param moduleId 模块号 =0: 员工管理  =1:组织机构  参照t_hr_subsys表，没有则按顺序添加，默认为0
	 * @param nbase 人员库
	 * @return
	 * @throws GeneralException 
	 */
	private String getPriWhere(UserView userView,String moduleId,String nbase){
		String codePriv = "";
		try {
			if(userView.isSuper_admin())
				return " select A0100 from "+nbase+"A01 where 1=1 ";
			if("0".equalsIgnoreCase(moduleId)){
				codePriv = this.getEmpWhere(userView,nbase);
			}else{
				codePriv = this.getUnitIdByBusi(userView,"4",nbase);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codePriv;
	}
	/**
	 * 获取人员范围与人员范围中高级权限
	 * @param userView
	 * @param nbase 人员库
	 * @return
	 * @throws GeneralException 
	 */
	private String getEmpWhere(UserView userView,String nbase) {
		String sql = null;
		try {
			//String unitId = userView.getManagePrivCodeValue();
			String condPriv = userView.getPrivExpression();
			if(condPriv ==null || condPriv.trim().length()==0)
				return " select A0100 from "+nbase+"A01 where 1=2 ";
			String[] tmps =condPriv.split("\\|");
			if(tmps.length==2){
				FactorList factorslist=new FactorList(tmps[0],tmps[1].toUpperCase(),nbase,false,false,true,1,userView.getUserId());
				try {
					sql = factorslist.getSqlExpression();
				} catch (GeneralException e) {
					e.printStackTrace();
					//获取人员范围权限出错
					Log.error(e);
					throw new GeneralException("");
				}
			}
			sql = " select a0100 " + sql +" ";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}
	
	/**
	 * 获取组织机构权限范围内人员过滤
	 * @param busiID
	 * @return
	 */
	private String getUnitIdByBusi(UserView userView,String busiID,String nbase){
		String orgPriv = null;
		StringBuffer priv = new StringBuffer();
		try {
			orgPriv = userView.getUnitIdByBusi(busiID);
			if(orgPriv == null || orgPriv.trim().length() == 0)
				return " select A0100 from "+nbase+"A01 where 1=2 ";
			priv.append(" select A0100 from "+nbase+"A01 where ");
			orgPriv = orgPriv.replaceAll("`",",");
			String[] orgPrivs = orgPriv.split(",");
			for(int i = 0 ; i < orgPrivs.length ; i++){
				if(orgPrivs[i].toUpperCase().startsWith("UN"))
					priv.append(" B0110 like "+orgPrivs[i].substring(0, 2)+"% or ");
				if(orgPrivs[i].toUpperCase().startsWith("UM"))
					priv.append(" E0122 like "+orgPrivs[i].substring(0, 2)+"% or ");
			}
			priv.setLength(priv.length()-3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return priv.toString();
	}
	/**
	 * 通过id获得常用查询条件中条件表达式
	 * @param idString
	 * @return
	 */
	private String getFactorById(String idString,Connection connection,UserView userView,String nbase) {
		RowSet rowSet = null;
		String sqlStr ="";
		try {
			String sql ="select lexpr,factor from lexpr where id = "+ Integer.parseInt(idString);
			ContentDAO dao = new ContentDAO(connection);
			rowSet = dao.search(sql);
			if (rowSet.next()) {
				String lexpr = rowSet.getString("lexpr");
				String factor=rowSet.getString("factor");
				FactorList factorslist=new FactorList(lexpr,factor.toUpperCase(),nbase,false,false,true,1,userView.getUserId());
				sqlStr = factorslist.getPrivSqlExpression();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return sqlStr;
	}
	/**
	 * 获取花名册展现sql的条件
	 * @param dao
	 * @return
	 */
	private HashMap<String, Object> getSqlCondition(ContentDAO dao,String tabid,UserView userView,String musterType) {
		HashMap<String, Object>  map = new HashMap<String, Object>();
		RowSet rowSet = null;
		try {
			String sortField = null;//排序指标
			String Nbases = null;//人员库
			String DataRange = null;//数据范围
			String filterid = null;//员工管理，模块常用查询的查询条件
			String range_type = null;//0 当前记录 1 年月 2 指标条件
			String condition = null;//条件表达式
			String field_name = null;
			boolean flag01 = false;//子集中是否 有A01
			ArrayList<String> fieldsetList = new ArrayList<String>();//已选指标的子集 
			ArrayList<String> fieldlist = new ArrayList<String>();//已选择的指标
			//String B0110 = null;
			StringBuffer sqlbuff = new StringBuffer("");
			sqlbuff.append("select B0110,SortField,Nbases,DataRange,field_name,baseid from lbase left join lname on lname.tabid = lbase.tabid where lbase.tabid = ");
			sqlbuff.append(tabid );
			sqlbuff.append(" order by lbase.baseid");
			rowSet = dao.search(sqlbuff.toString());
			while(rowSet.next()) {
				 sortField = rowSet.getString("SortField");
				 Nbases = rowSet.getString("Nbases");
				 DataRange = rowSet.getString("DataRange");
				 field_name = rowSet.getString("field_name");
				 fieldlist.add(field_name);
				 //B0110 = rowSet.getString("B0110");
			}
			//获取老版花名册常用查询ID
			String commonQueryId=this.getParamValue(1, "usual_query",tabid);
			//判断老版花名册是否设置取数条件，若设置将数据迁移至新字段 DataRange
			DataRange = this.operateDataRange(DataRange,commonQueryId,tabid);

			//如果指标不存在在业务字典里面则移除
			for (int i =fieldlist.size()-1; i >-1 ; i--) {
                String fielditemid = fieldlist.get(i);
                FieldItem fieldItem = DataDictionary.getFieldItem(fielditemid.split("\\.")[1]);
                if (null==fieldItem) {
                    fieldlist.remove(i);
                }
            }
			//获得当前登录用户权限范围内的指标 
			ArrayList<String> privFieldList = getPrivFieldList(dao, userView, musterType);
			//如果选择的指标不在权限范围内则移除该指标
			for (int i =  fieldlist.size()-1; i >-1; i--) {
				boolean flag = false;
				if ("K01.E01A1".equalsIgnoreCase(fieldlist.get(i))&&privFieldList.size()==0) {
					flag = true;
				}
				for(int j=0;j<privFieldList.size();j++) {
					if (privFieldList.get(j).equalsIgnoreCase(fieldlist.get(i).split("\\.")[1])||"K01.E01A1".equalsIgnoreCase(fieldlist.get(i))) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					fieldlist.remove(i);
				}
			}
		    Set<String> fieldsetSet = new HashSet<String>();
		    for (int i = 0; i < fieldlist.size(); i++) {
		    	if (fieldsetSet.add(fieldlist.get(i).split("\\.")[0])) {
		    		fieldsetList.add(fieldlist.get(i).split("\\.")[0]);
				}
			}
		    Collections.sort(fieldsetList);
		    if (fieldsetList.size()>0) {
				if (fieldsetList.get(0).indexOf("01")!=-1) {
					flag01 = true;
				}
			}
		    boolean parttimejobvalue = false;
			if (null!=DataRange&&!"".equals(DataRange)) {
				JSONObject dataRange = JSONObject.fromObject(DataRange);
				filterid = dataRange.getString("filter");
				range_type = dataRange.getString("range_type");
				condition = dataRange.getString("condition");
                if (dataRange.containsKey("parttimejobvalue")) {
                    parttimejobvalue = dataRange.getBoolean("parttimejobvalue");
                }
			}
			map.put("Nbases", Nbases);
			map.put("parttimejobvalue", parttimejobvalue);
			map.put("sortField", sortField);
			map.put("DataRange", DataRange);
			map.put("filterid", filterid);
			map.put("range_type", range_type);
			map.put("condition", condition);
			map.put("flag01", flag01);
			map.put("fieldsetList", fieldsetList);
			map.put("fieldlist", fieldlist);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rowSet);
		}
		return map;
	}

	/**
	 * 老版取数条件迁移至新字段
	 * @param dataRange
	 * @param commonQueryId
	 * @param tabid
	 * @return
	 */
	private String operateDataRange(String dataRange, String commonQueryId, String tabid) {
		try {
			ContentDAO dao = new ContentDAO(conn);
			if(StringUtils.isBlank(dataRange)){
				if(!StringUtils.isBlank(commonQueryId)){
					dataRange = "{\"filter\":\""+commonQueryId+"\",\"condition\":null,\"range_type\":\"0\"}";
					RecordVo vo=new RecordVo("lname");
					vo.setString("tabid",tabid);
					vo.setString("datarange",dataRange);
					dao.updateValueObject(vo);
				}
			}else{
				JSONObject dataRangeJson = JSONObject.fromObject(dataRange);
				String filteridTemp = dataRangeJson.getString("filter");
				String conditionTemp = dataRangeJson.getString("condition");
				String rangeTypeTemp = dataRangeJson.getString("range_type");
				boolean parttimejobvalueTemp = false;
				if (dataRangeJson.containsKey("parttimejobvalue")) {
					parttimejobvalueTemp = dataRangeJson.getBoolean("parttimejobvalue");
				}
				if(!StringUtils.isBlank(commonQueryId) && (StringUtils.isBlank(filteridTemp) || StringUtils.equalsIgnoreCase(filteridTemp,"null"))){
					dataRange = "{\"filter\":\""+commonQueryId+"\",";
					if(!StringUtils.isBlank(conditionTemp) && !StringUtils.equalsIgnoreCase(conditionTemp,"null")){
						dataRange += "\"condition\":\""+conditionTemp+"\",";
					}else{
						dataRange += "\"condition\":null,";
					}
					dataRange += "\"parttimejobvalue\":"+parttimejobvalueTemp+",";
					if(!StringUtils.isBlank(rangeTypeTemp) && !StringUtils.equalsIgnoreCase(rangeTypeTemp,"null")){
						dataRange += "\"range_type\":\""+rangeTypeTemp+"\"}";
					}else {
						dataRange += "\"range_type\":\"0\"}";
					}
					RecordVo vo=new RecordVo("lname");
					vo.setString("tabid",tabid);
					vo.setString("datarange",dataRange);
					dao.updateValueObject(vo);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return dataRange;
	}

	/**
	 *
	 * @param type 节点类型
	 * @param property 节点名称
	 * @param tabid 花名册id
	 * @return
	 */
	private String getParamValue(int type, String property, String tabid) {
		String paramvalue = "";
		String path = "/report/param";
		switch(type){
			case 1:
				path = "/report/param";
				break;
			case 2:
				path = "/report";
				break;
		}
		XPath xpath;
		try {
			Document doc = getDocumentValue(tabid);
			xpath = XPath.newInstance(path);
			Element element=(Element)xpath.selectSingleNode(doc);
			if(element!=null){
				paramvalue = element.getAttributeValue(property);
				paramvalue=paramvalue!=null&&paramvalue.trim().length()>0?paramvalue:"";
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return paramvalue;
	}

	/**
	 * 获取老版花名册取数规则xml
	 * @param tabid 花名册id
	 * @return
	 */
	private Document getDocumentValue(String tabid) {
		Document doc = null;
		String xml = "";
		RecordVo vo=new RecordVo("lname");
		vo.setString("tabid",tabid);
		StringBuffer temp_xml=new StringBuffer();
		temp_xml.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>");
		temp_xml.append("<report>");
		temp_xml.append("</report>");
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			if(dao.isExistRecordVo(vo))
			{
				vo=dao.findByPrimaryKey(vo);
				if(vo!=null)
					xml=vo.getString("xml_style");
			}
			if(StringUtils.isEmpty(xml)){
				xml=temp_xml.toString();
			}

			doc=PubFunc.generateDom(xml.toString());

		}catch(Exception ex){
			xml=temp_xml.toString();
		}
		return doc;
	}

	/**
	 * 
	 * @param contentDAO
	 * @param userView
	 * @param musterType =1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；
	 * @return
	 */
	public ArrayList<String> getPrivFieldList(ContentDAO contentDAO,UserView userView,String musterType) {
		ArrayList<String> privFieldList = new ArrayList<String>();
		ArrayList<FieldSet> privFieldSetList = new ArrayList<FieldSet>();
		if ("1".equals(musterType)) {
			privFieldSetList = userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
		}else if ("2".equals(musterType)) {
			privFieldSetList = userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
		}else if ("3".equals(musterType)) {
			privFieldSetList = userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
		}else if ("4".equals(musterType)) {
			privFieldSetList = userView.getPrivFieldSetList(Constant.JOB_FIELD_SET);
		}
		//遍历privFieldSetList 移除未构库的子集
		for (int i = privFieldSetList.size()-1; i >-1; i--) {
			FieldSet fieldset = (FieldSet) privFieldSetList.get(i);
			if("0".equalsIgnoreCase(fieldset.getUseflag())) {
				privFieldSetList.remove(i);
			}
		}
		for (int i = privFieldSetList.size()-1; i >-1; i--) {
			FieldSet fieldset = (FieldSet) privFieldSetList.get(i);
			ArrayList<FieldItem> fieldItemList = userView.getPrivFieldList(fieldset.getFieldsetid());
			if (fieldItemList!=null) {
				for (int j = 0; j <fieldItemList.size(); j++) {
					FieldItem fieldItem = fieldItemList.get(j);
					if (!"0".equalsIgnoreCase(fieldItem.getUseflag())) {
						privFieldList.add(fieldItem.getItemid());
					}
				}
			}
		}
		return privFieldList;
	}
	/**
	 * 删除指定位置上的内容
	 * @param index
	 * @param array
	 * @return
	 */
	private String[] delete(int index, String[] array) {
		String[] arrNew = null;
		try {
			//数组的删除其实就是覆盖前一位
			arrNew = new String[array.length - 1];
	        for (int i = index; i < array.length - 1; i++) {
	            array[i] = array[i + 1];
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return arrNew;
    }
    /**
     * 此方法不允许再增加参数，有需要使用到其他参数的放到extendParam中
     * 根据range_type不同拼装sql
     * @param nbase 人员库
     * @param flag01 是否有主集
     * @param moduleId 模块id =0：员工管理；=1：组织机构；
     * @param filterid 过滤条件的id
     * @param connection 数据库连接
     * @param sortFieldArray 排序指标
     * @param fieldsetList  已选子集
     * @param fieldlist  已选指标
     * @param range_type 0 当前记录； 1 年月 ；2 指标条件
     * @param condition 过滤条件
     * @param userView 登录人员的信息，为啥要传这个玩意？这个类在实例化的时候不是有了么？
     * @param musterType =1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；
     * @param parttimejobvalue 是否显示兼职人员
     * @param extendParam 扩展参数 Map格式，存放自己所需要的参数
     * @return
     */
	private String getSqlbyRangeType(String nbase,boolean flag01,String moduleId,String filterid,Connection connection,
			String[] sortFieldArray,ArrayList<String> fieldsetList,ArrayList<String> fieldlist,String range_type,String condition,
            UserView userView,String musterType,boolean parttimejobvalue,Map extendParam) {
		StringBuffer sql = new StringBuffer("");
		try {   
		        if (fieldsetList.size()==0||fieldlist.size()==0) {
                    return null;
                }
		        if (!StringUtils.equals(musterType, "1")) {
                    nbase = "";
                }
		        if (flag01&&"1".equals(musterType)&&!fieldlist.contains("A01.A0000")) {
		        	fieldlist.add(0,"A01.A0000");
				}
			    Collections.sort(fieldsetList);
			    boolean flag = false;
			    for (int k=0;k<fieldlist.size();k++) {
			    	String itemid = fieldlist.get(k);
			    	if (itemid.equalsIgnoreCase(fieldsetList.get(0)+getSqlX0100(".", musterType, ""))) {
						flag =true;
					}
			    } 
			    if (!flag) {
					fieldlist.add(0, fieldsetList.get(0)+getSqlX0100(".", musterType, ""));
				}
				boolean X0100Flag = false;
				if (sortFieldArray!=null&&sortFieldArray.length>0) {
				    sql.append("select ROW_NUMBER() OVER (ORDER BY ");
				    for (int i = 0; i < sortFieldArray.length; i++) {
                        String itemdid = sortFieldArray[i];
                        if (i!=0) {
                            sql.append(",");
                        }
                        sql.append(nbase);
                        sql.append(itemdid.substring(0, itemdid.length()-1));
                        if (itemdid.endsWith("1")) {
                            sql.append(" desc");
                        }
                    }
	                sql.append(") AS xuhao,");
                }else {
                    sql.append("select ROW_NUMBER() OVER (ORDER BY ");
                    sql.append(nbase);
                    sql.append(fieldsetList.get(0));
                    if (flag01&&"1".equals(musterType)) {
                        sql.append(".A0000");
                    }else {
                        sql.append(getSqlX0100(".", musterType, ""));
                    }
                    sql.append(" ASC) AS xuhao,");
                }
				for (int k=0;k<fieldlist.size();k++) {
					if (k!=0) {
						sql.append(",");
					}
					String fielditemid = fieldlist.get(k);
					String itemid = fielditemid.split("\\.")[1];
					FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
					if(fieldItem != null && !StringUtils.equalsIgnoreCase("a0100",itemid) &&!StringUtils.equalsIgnoreCase("a0000", itemid)) {
						if ("N".equals(fieldItem.getItemtype())) {
							sql.append(Sql_switcher.isnull(nbase + fielditemid, "0"));
							sql.append(" " + itemid);
						} else {
							sql.append(nbase);
							sql.append(fielditemid);
						}
					}
					if(StringUtils.equalsIgnoreCase("a0000",itemid)||StringUtils.equalsIgnoreCase("a0100",itemid)){
						sql.append(nbase);
						sql.append(fielditemid);
					}
					if (fielditemid.equalsIgnoreCase(fieldsetList.get(0)+getSqlX0100(".", musterType,""))) {
						X0100Flag =true;
					}
				}
				sql.append(" from ");
				sql.append(nbase);
				sql.append(fieldsetList.get(0));
				for(int g=1;g<fieldsetList.size();g++) {
					String fieldsetid = fieldsetList.get(g);
				    if (flag01) {
				        sql.append(" left join ");
                    }else {
                        sql.append(" full join ");
                    }
				    if (StringUtils.equals("0", range_type)) {
				        //(select * from (select *,row_number() over (partition by a0100 order by i9999 desc) as rownum from usra04) temp where rownum = 1)b on a.a0100 = b.a0100
				        sql.append(" (select * from (select ");
                        sql.append(nbase);
                        sql.append(fieldsetid);
                        sql.append(getSqlX0100(".", musterType, ""));
                        for (int k=0;k<fieldlist.size();k++) {
                            String fielditemid = fieldlist.get(k);
                            if (fieldsetid.equalsIgnoreCase(fielditemid.split("\\.")[0])) {
                                sql.append(",");
                                sql.append(nbase);
                                sql.append(fielditemid);
                            }
                        }
				        sql.append(",row_number() over (partition by ");
				        sql.append(getSqlX0100("", musterType,""));
				        sql.append(" order by i9999 desc)rownumber from ");
				        sql.append(nbase);
                        sql.append(fieldsetid);
                        sql.append(")temp where rownumber = 1 )");
                        sql.append(nbase);
                        sql.append(fieldsetid);
                        sql.append(" on ");
                        sql.append(nbase);
                        sql.append(fieldsetid);
                        sql.append(getSqlX0100(".", musterType, "="));
                        sql.append(nbase);
                        sql.append(fieldsetList.get(0));
                        sql.append(getSqlX0100(".", musterType, ""));
				       }else {
						sql.append(nbase);
						sql.append(fieldsetid);
						sql.append(" on ");
						sql.append(nbase);
						if (flag01) {
							sql.append(getSqlSet(musterType, "."));
							sql.append(getSqlX0100("", musterType, " = "));
						}else {
							sql.append(fieldsetList.get(0));
							sql.append(getSqlX0100(".", musterType, " = "));
						}
						sql.append(nbase);
						sql.append(fieldsetid);
						sql.append(getSqlX0100(".", musterType, " "));
					}
				}
				sql.append(" where ");
				//显示的人员在当前用户的权限范围内
				if ("1".equals(musterType)) {
					sql.append(" EXISTS ");
					sql.append(" ( SELECT 1 FROM (");
					sql.append(getPriWhere(userView, moduleId, nbase));
					//显示兼职人员开始
					if ("1".equals(musterType)&&parttimejobvalue) {//是人员花名册并且设置了显示兼职人员
					    sql.append(getParttimejobPeopleSql(nbase,fieldsetList.get(0),fieldlist));
                    }
					//显示兼职人员结束
					sql.append(") a2 where a2.A0100= ");
					sql.append(nbase);
					sql.append(fieldsetList.get(0));
					sql.append(".A0100 )");
				}else if (StringUtils.equals("2",musterType)||StringUtils.equals("3",musterType)) {
                    MusterManageService musterManageService = new MusterManageServiceImpl(connection,userView);
                    String priv = musterManageService.getMusterPriv(moduleId);//当前用户的权限
                    if (!"|".equals(priv)&&priv.indexOf("|")!=-1) {
                        String[] unidarray = priv.split("\\|")[0].split(",");
                        sql.append(" (");
                        for (int i = 0; i < unidarray.length; i++) {
                            if (i!=0) {
                                sql.append(" or ");
                            }
                            if (flag01) {
                                sql.append(getSqlSet(musterType, "."));
                                sql.append(getSqlX0100("", musterType, ""));
                            }else {
                                sql.append(fieldsetList.get(0));
                                sql.append(getSqlX0100(".", musterType, " "));
                            }
                            sql.append(" like ");
                            sql.append("'");
                            sql.append(unidarray[i]);
                            sql.append("%' ");
                        }
                        sql.append(") ");
                    }else {
                        if ("UN".equals(priv)) {
                            sql.append(" 1=1 ");
                        }else {
                            sql.append(" 1=2 ");
                        }
                    }
                }else {
                    sql.append(fieldsetList.get(0));
                    sql.append(getSqlX0100(".", musterType, " "));
					sql.append(" is not null ");
				}
				if (fieldsetList.size()>1&&!StringUtils.equals("0", range_type)) {
					if ("0".equals(range_type)) {//当前记录
						int k = 0;
						if (flag01) {
                            k=1;
                        }
						for(int g=k;g<fieldsetList.size();g++) {
                            sql.append(getSqlFieldsetList(fieldsetList, g, musterType, nbase));
                        }
					}else if("1".equals(range_type)){//年月变化
						if (!"".equals(condition)&&null!=condition) {
							String[] conditionArray = condition.split(",");
							String[] conditionarray = conditionArray[1].split("\\|");
							sql.append(" and ");
							sql.append(Sql_switcher.dateToChar(nbase+conditionArray[0]+"."+conditionArray[0]+"Z0", "yyyy-MM-dd"));
							sql.append(" >='");
							sql.append(conditionArray[1].split("\\|")[0]);
							sql.append("' and ");
							sql.append(Sql_switcher.dateToChar(nbase+conditionArray[0]+"."+conditionArray[0]+"Z0", "yyyy-MM-dd"));
							sql.append(" <'");
							sql.append(conditionArray[1].split("\\|")[1]);
							sql.append("' ");
							if (conditionarray.length==3) {//次数
                                sql.append(" and ");
                                sql.append(nbase+conditionArray[0]+"."+conditionArray[0]+"Z1 = '");
                                sql.append(conditionarray[2]+"' ");
                            }
						}
					}else if("2".equals(range_type)){//多个指标条件
						if (!"".equals(condition)&&null!=condition) {
							String lexpr = condition.split("\\|")[0];
							String factor = condition.split("\\|")[1];
							FactorList  factorslist=new FactorList(lexpr,factor.toUpperCase(),nbase,true,false,true,Integer.parseInt(musterType),userView.getUserId());
							//String sqlSub = factorslist.getPrivSqlExpression();
							String sqlSub = factorslist.getSqlExpression();
							sql.append(" and EXISTS (");
						    sql.append("select 1 from (select ");
						    if (StringUtils.isNotBlank(nbase)) {
                                sql.append(nbase);
                            }
						    sql.append(fieldsetList.get(0));
						    sql.append(getSqlX0100(".", musterType, " "));
						   if (sqlSub.indexOf("LEFT JOIN")!=-1) {
						        sql.append(",");
	                            if (StringUtils.isNotBlank(nbase)) {
	                                sql.append(nbase);
	                            }
	                            sql.append(fieldsetList.get(1));
	                            sql.append(".i9999 ");
                            }
						    sql.append(sqlSub);
					        sql.append(") lexpertable where lexpertable");
					        sql.append(getSqlX0100(".", musterType, " = "));
					        if (StringUtils.isNotBlank(nbase)) {
					            sql.append(nbase);
                            }
					        sql.append(fieldsetList.get(0));
					        sql.append(getSqlX0100(".", musterType, " "));
					        if (sqlSub.indexOf("LEFT JOIN")!=-1) {
					            sql.append(" and lexpertable.i9999 = ");
	                            if (StringUtils.isNotBlank(nbase)) {
	                                sql.append(nbase);
	                            }
	                            sql.append(fieldsetList.get(1));
	                            sql.append(".i9999 ");
	                            if (!addI9999(sqlSub)) {//如果i9999 都不存在
	                                sql.append("or ");
	                                sql.append(nbase);
	                                sql.append(fieldsetList.get(1));
	                                sql.append(".i9999 is null");
                                }
					        }
					        sql.append(")");
						}
					}else if("3".equals(range_type)){//单个指标条件
						if (!"".equals(condition)&&null!=condition) {
							String[] conditionArray = condition.split(",");
							FieldItem fieldItem = DataDictionary.getFieldItem(conditionArray[0].split("\\.")[1]);
							String fieldType = fieldItem.getItemtype();
							sql.append(" and ");
							if ("D".equals(fieldType)) {
								sql.append(Sql_switcher.dateToChar(nbase+conditionArray[0], "yyyy-MM-dd"));
							}else if("M".equals(fieldType)){
                                sql.append(Sql_switcher.sqlToChar(nbase+conditionArray[0]));
                            }else {
								sql.append(nbase);
								sql.append(conditionArray[0]);
							}
							sql.append(" >= ");
							if (!"N".equals(fieldType)) {
								sql.append("'");
							}
							sql.append(conditionArray[1].split("\\|")[0]);
							if (!"N".equals(fieldType)) {
								sql.append("'");
							}
							sql.append(" and ");
							if ("D".equals(fieldType)) {
								sql.append(Sql_switcher.dateToChar(nbase+conditionArray[0], "yyyy-MM-dd"));
							}else if("M".equals(fieldType)){
                                sql.append(Sql_switcher.sqlToChar(nbase+conditionArray[0]));
							}else {
							    sql.append(nbase);
                                sql.append(conditionArray[0]);
                            }
							sql.append(" <= ");
							if (!"N".equals(fieldType)) {
								sql.append("'");
							}
							sql.append(conditionArray[1].split("\\|")[1]);
							if (!"N".equals(fieldType)) {
								sql.append("'");
							}
						}
					}
				}
				if(filterid!=null&&!"".equals(filterid)&&!"null".equalsIgnoreCase(filterid)) {
					 String sqlSub = getFactorById(filterid, connection,userView, nbase);
					 sql.append(" and EXISTS (");
				     sql.append("select 1 from (select ");
				     sql.append(getSqlX0100("", musterType, " "));
				     sql.append(sqlSub);
			         sql.append(") lexpertable where lexpertable");
			         sql.append(getSqlX0100(".", musterType, " = "));
			         sql.append(nbase);
			         sql.append(fieldsetList.get(0));
			         sql.append(getSqlX0100(".", musterType, " "));
			         sql.append(")");
				}
				//扩展参数不为空
				if(MapUtils.isNotEmpty(extendParam)){
                    String otherCtrl = (String) extendParam.get("otherCtrl");
                    if("true".equalsIgnoreCase(otherCtrl)){
                        String otherSql = (String) extendParam.get("otherSql");
                        sql.append("and EXISTS (");
                        sql.append("select 1 from (select ");
                        sql.append(getSqlX0100("", musterType, " "));
                        sql.append(" from ");//此处暂时只支持从员工调用。其余地方的扩展还需要修改下面的这个语句,等待需要时在扩展
                        sql.append(nbase).append("A01 ").append(otherSql);
                        sql.append(") a3 where a3");
                        sql.append(getSqlX0100(".", musterType, " = "));
                        sql.append(nbase);
                        sql.append(fieldsetList.get(0));
                        sql.append(getSqlX0100(".", musterType, " "));
                        sql.append(")");
                    }
                }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql.toString();
	}
	/**
	 * 获得兼职人员的sql
	 * @return
	 */
	private String getParttimejobPeopleSql(String nbase,String fieldsetid,ArrayList<String> fieldlist) {
        for (int i = 0; i < fieldlist.size(); i++) {
            String[] itemArray = fieldlist.get(i).split("\\.");
            String itemdesc = DataDictionary.getFieldItem(itemArray[1]).getItemdesc();
            if (itemdesc.contains("薪资")||(itemdesc.contains("工资")&&!itemdesc.contains("工资归属"))||itemdesc.contains("奖金")||itemdesc.contains("费")||itemdesc.contains("钱")
                    ||itemdesc.contains("薪点")||itemdesc.contains("绩效")||itemdesc.contains("薪级")) {
                return "";
            }
        }
	    StringBuffer sqlbuff = new StringBuffer();
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
        String flag = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "flag");
        //兼职单位字段
        String unitField = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "unit");
        // 兼职部门
        String deptField = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "dept");
        // 兼职职位
        String posField = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
        //任免标识字段
        String appointField = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "appoint");
        //兼职子集
        String setid = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "setid");
        if (StringUtils.isEmpty(flag) || "false".equalsIgnoreCase(flag)) {
            return "";
        }
        if (StringUtils.isEmpty(setid) || DataDictionary.getFieldSetVo(setid) == null
                || "0".equalsIgnoreCase(DataDictionary.getFieldSetVo(setid).getUseflag())) {
            return "";
        }
        FieldItem unitFieldItem = DataDictionary.getFieldItem(unitField);
        if (unitFieldItem == null || "0".equalsIgnoreCase(unitFieldItem.getUseflag())) {
            return "";
        }
        FieldItem appointFieldItem = DataDictionary.getFieldItem(appointField);
        if (appointFieldItem == null || "0".equalsIgnoreCase(appointFieldItem.getUseflag())) {
            return "";
        }
        String condPriv = userView.getPrivExpression();
        if(condPriv ==null || condPriv.trim().length()==0){
            return "";
        }
        
        if(userView.isSuper_admin()) {
            return "";
        }
      /*String tmps[]=condPriv.split("\\|");
        if(tmps.length==2){
            FactorList factorslist=new FactorList(tmps[0],tmps[1].toUpperCase(),nbase,false,false,true,1,userView.getUserId());
            try {
                sql = factorslist.getSqlExpression();
            } catch (GeneralException e) {
                e.printStackTrace();
            }
        }*/
        String privCodeValue = userView.getManagePrivCodeValue();
        if(privCodeValue ==null || privCodeValue.trim().length()==0){
            return "";
        }
        String tablename = nbase+setid;//兼职子集
        String mainset = nbase+fieldsetid;//主集
        sqlbuff.append(" or exists( select ");
        sqlbuff.append(tablename);
        sqlbuff.append(".A0100 from ");
        sqlbuff.append(tablename);
        sqlbuff.append(" where ");
        sqlbuff.append(tablename);
        sqlbuff.append(".A0100 in (select ");
        sqlbuff.append(mainset);
        sqlbuff.append(".A0100 from ");
        sqlbuff.append(mainset);
        sqlbuff.append(" left join ");
        sqlbuff.append(tablename);
        sqlbuff.append(" on ");
        sqlbuff.append(mainset);
        sqlbuff.append(".A0100 = ");
        sqlbuff.append(tablename);
        sqlbuff.append(".A0100 where (");
        sqlbuff.append(tablename);
        sqlbuff.append(".");
        sqlbuff.append(unitField);
        sqlbuff.append(" like '");
        sqlbuff.append(privCodeValue);
        sqlbuff.append("%' ");
        if (StringUtils.isNotEmpty(deptField)) {
            sqlbuff.append(" or ");
            sqlbuff.append(tablename);
            sqlbuff.append(".");
            sqlbuff.append(deptField);
            sqlbuff.append(" like '");
            sqlbuff.append(privCodeValue);
            sqlbuff.append("%' ");
        }
        if (StringUtils.isNotEmpty(posField)) {
            sqlbuff.append(" or ");
            sqlbuff.append(tablename);
            sqlbuff.append(".");
            sqlbuff.append(posField);
            sqlbuff.append(" like '");
            sqlbuff.append(privCodeValue);
            sqlbuff.append("%' ");
        }
        sqlbuff.append(") ");
        sqlbuff.append(" and ");
        sqlbuff.append(tablename);
        sqlbuff.append(".");
        sqlbuff.append(appointField);
        sqlbuff.append(" = '0') and ");
        sqlbuff.append(mainset);
        sqlbuff.append(".A0100 = ");
        sqlbuff.append(tablename);
        sqlbuff.append(".A0100 ) ");
        return sqlbuff.toString();
    }
	/**
	 * 获取权限范围内的人员库。如果设置的人员库不在权限范围内则移除
	 * @param userView
	 * @param nbseArray 设置的人员库
	 * @return
	 */
    private String[] removaPriDb(UserView userView,String [] nbseArray) {
    	 try {
 			for(int i = nbseArray.length-1; i >-1 ; i--) {
 				String nbase = nbseArray[i];
 				boolean flag = false;
 				for (int j = 0; j < userView.getPrivDbList().size(); j++) {
 					String privDbname =  (String) userView.getPrivDbList().get(j);
 					if (privDbname.equalsIgnoreCase(nbase)) {
 						flag = true;
 						break;
 					}
 				}
 				if (!flag) {
 					delete(i, nbseArray);
 				}
 			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nbseArray;
	}
    /**
     * 
     * @param prefix 前缀
     * @param musterType 
     * @param suffix 后缀
     * @return
     */
    public String getSqlX0100(String prefix,String musterType,String suffix) {
    	StringBuffer sql = new StringBuffer("");
    	try {
    		sql.append(prefix);
        	if ("1".equals(musterType)) {
    			sql.append("A0100");
    		}else if ("2".equals(musterType)) {
    			sql.append("B0110");
    		}else if ("3".equals(musterType)) {
    			sql.append("E01A1");
    		}else if ("4".equals(musterType)) {
    			sql.append("H0100");
    		}
        	sql.append(suffix);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql.toString();
    }
    /**
     * 
     * @param musterType
     * @return
     */
    private String getSqlSet(String musterType,String suffix) {
    	StringBuffer sql = new StringBuffer("");
    	try {
        	if ("1".equals(musterType)) {
    			sql.append("A01");
    		}else if ("2".equals(musterType)) {
    			sql.append("B01");
    		}else if ("3".equals(musterType)) {
    			sql.append("K01");
    		}else if ("4".equals(musterType)) {
    			sql.append("H01");
    		}
        	sql.append(suffix);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql.toString();
    }
    /**
     * 
     * @param fieldsetList
     * @param g
     * @param musterType
     * @param nbase
     * @return
     */
    private String  getSqlFieldsetList(ArrayList<String> fieldsetList,int g,String musterType,String nbase) {
    	StringBuffer sql = new StringBuffer("");
    	RowSet rowSet = null;
    	try {
    	    ContentDAO dao = new ContentDAO(conn);
    	    StringBuffer sqlbuff = new StringBuffer("select ");
    	    sqlbuff.append(getSqlX0100("", musterType, ""));
    	    sqlbuff.append(",MAX (i9999) AS i9999 from ");
    	    sqlbuff.append(nbase);
    	    sqlbuff.append(fieldsetList.get(g));
    	    sqlbuff.append(" group by ");
    	    sqlbuff.append(getSqlX0100("", musterType, ""));
    	    rowSet = dao.search(sqlbuff.toString());
    	    if (rowSet.next()) {
    	        sql.append(" and EXISTS(select 1 from ( select ");
                sql.append(getSqlX0100("", musterType, " "));
                sql.append(",MAX (i9999) AS i9999 from ");
                sql.append(nbase);
                sql.append(fieldsetList.get(g));
                sql.append(" group by ");
                sql.append(getSqlX0100("", musterType, " "));
                sql.append(" ) a");
                sql.append(g);
                sql.append(" where a");
                sql.append(g);
                sql.append(getSqlX0100(".", musterType, " = "));
                sql.append(nbase);
                sql.append(fieldsetList.get(g));
                sql.append(getSqlX0100(".", musterType, " "));
                sql.append(" and a");
                sql.append(g);
                sql.append(".i9999 = ");
                sql.append(nbase);
                sql.append(fieldsetList.get(g));
                sql.append(".i9999");
                sql.append(" or ");
                sql.append(nbase);
                sql.append(fieldsetList.get(g));
                sql.append(getSqlX0100(".", musterType, ""));
                sql.append(" is null) ");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
            PubFunc.closeDbObj(rowSet);
        }
    	return sql.toString();
	}
    /**
     * 是否添加 or i9999 is null 
     * @param sql
     * @return
     */
    private boolean addI9999(String sql) {
        boolean flag = false;
        RowSet rowSet = null;
        try {
            StringBuffer sqlBuffer = new StringBuffer("select i9999 ");
            sqlBuffer.append(sql);
            ContentDAO dao = new ContentDAO(conn);
            rowSet = dao.search(sqlBuffer.toString());
            while (rowSet.next()) {
                String value = rowSet.getString(1);
                if (value!=null) {
                    flag = true;
                    break;
                }
            }
        } catch (Exception e) {
            PubFunc.closeResource(rowSet);
        }
        return flag;
    }
    /**
	  * 获取人员范围的权限  和 上级机构权限   
	  * @param userView 当前登录用户
	  * @return 超级管理管返回UN  非超级管理员 返回  xxx,xxx | xxx,xxx  权限内机构 | 上级机构    
	  */
	 public String getMusterPriv(UserView userView){
		  String codePriv = "";
		  try {
			  if(userView.isSuper_admin()) {
				  return "UN";
			  }
			  String unitCode = userView.getManagePrivCode();
			  if(unitCode == null || unitCode.trim().length() == 0) {
				  return "";
			  }
			  if("UN".equalsIgnoreCase(unitCode)) {
				  codePriv = userView.getManagePrivCodeValue();
			  }
			  String unitValue = userView.getManagePrivCodeValue();
			  codePriv = codePriv + "|";
			  for(int i = 1 ; i < unitValue.length() ; i++){
			      codePriv = codePriv + unitValue.subSequence(0,i) +",";
			  }
			  codePriv = codePriv.substring(0, codePriv.length()-1);
		  } catch (Exception e) {
			 e.printStackTrace();
		  }
		  return codePriv;
	 }

	/**
	 * 格式化处理ROW_NUMBER()函数排序条件语句
	 * @param fieldlist 已选指标
	 * @param sortField 排序指标
	 * @param sortFlag 是否排序标志，0：不排序；1：排序
	 * @return
	 */
	@Override
    public String orderBySqlFormat(ArrayList<String> fieldlist, String sortField, String sortFlag, boolean flag01, String musterType, String readType){
		StringBuffer sqlbuf = new StringBuffer();
		ArrayList<String> realFieldList = new ArrayList<String>();
        String[] sortFieldArr = null;
        if (StringUtils.isNotEmpty(sortField)){
            sortFieldArr = sortField.split(",");
        }


		if(StringUtils.equalsIgnoreCase(readType,"0")){
			for (String field :fieldlist){
				realFieldList.add(field.substring(field.indexOf(".")+1));
			}
		}else if (StringUtils.equalsIgnoreCase(readType,"1")){
			realFieldList.addAll(fieldlist);
		}
		if(StringUtils.equalsIgnoreCase(sortFlag,"1")){
			for (String field :realFieldList){
				if (StringUtils.equalsIgnoreCase(field,"XUHAO")){
					continue;
				}
				if(StringUtils.isNotEmpty(sortField)){
					for (String sortItem : sortFieldArr){
						if (StringUtils.contains(sortItem.toLowerCase(),field.toLowerCase())){
							if(sortItem.endsWith("1")){
								sqlbuf.append("hmctable.").append(field).append(" desc,");
							}else{
								sqlbuf.append("hmctable.").append(field).append(",");
							}
						}
					}
				}else {
					if (flag01&&"1".equals(musterType)) {
						sqlbuf.append("hmctable.A0000,");
					}else {
						sqlbuf.append("hmctable.").append(getSqlX0100(musterType)).append(",");
					}
					//sqlbuf.append("hmctable.").append(field).append(",");
					break;
				}
			}

			if(StringUtils.isNotEmpty(sqlbuf.toString())){
				sqlbuf.setLength(sqlbuf.length()-1);
			}
		}else if (StringUtils.equalsIgnoreCase(sortFlag,"0")){
			for (String field :realFieldList){
				if (StringUtils.equalsIgnoreCase(field,"XUHAO")){
					continue;
				}
				sqlbuf.append("hmctable.").append(field).append(",");
			}
			if(StringUtils.isNotEmpty(sqlbuf.toString())){
				sqlbuf.setLength(sqlbuf.length()-1);
			}
		}

		return sqlbuf.toString();
	}

	/**
	 *
	 * @param musterType
	 * @return
	 */
	@Override
    public String getSqlX0100(String musterType) {
		StringBuffer sql = new StringBuffer("");
		try {
			if ("1".equals(musterType)) {
				sql.append("A0100");
			}else if ("2".equals(musterType)) {
				sql.append("B0110");
			}else if ("3".equals(musterType)) {
				sql.append("E01A1");
			}else if ("4".equals(musterType)) {
				sql.append("H0100");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql.toString();
	}
    /**
     * 导出常用花名册(简单花名册Excel）
     * 此方法为对其他模块提供的接口，
     * 调用时只控制子集的条件，其他的条件由whereSql控制，由其他模块传递
     * @Author xuchangshun
     * @param tabid 花名册模版id
     * @param musterType 花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；
     * @param moduleId 模块id =0：员工管理；=1：组织机构；
     * @param nbase 人员库，目前考虑只传递一个
     * @param whereSql 过滤条件（人员过滤条件）举例 “ where A01......”
     * @param sortField 排序字段 不传递（人员花名册使用A0000(输出字段含主集),A0100(输出字段不含主集字段),单位部门使用B0100,岗位使用E01A1）
     * @return java.lang.String
     * @throws
     * @Date 2020/2/4 15:47
     */
    @Override
    public String exportExcel(String tabid,String musterType,String moduleId,String nbase,String whereSql,String sortField) throws GeneralException {
	    //根据花名册tabid得到花名册sql语句，默认导出该花名册的所有字段
        ContentDAO dao = new ContentDAO(this.conn);
        Map map =getSqlCondition(dao, tabid,userView,musterType);

        boolean parttimejobvalue = true;//是否显示兼职人员  默认显示
        String range_type = (String) map.get("range_type");//0 当前记录 1 年月 2 指标条件
        String condition = (String) map.get("condition");//条件表达式
        boolean flag01 = (Boolean) map.get("flag01");//子集中是否有01
        ArrayList<String> fieldsetList = (ArrayList<String>) map.get("fieldsetList");//已选指标的子集
        ArrayList<String> fieldlist = (ArrayList<String>) map.get("fieldlist");//已选择的指标
        StringBuffer itemids = new StringBuffer();
        for (String field :fieldlist){
            String fileldItemid =  field.substring(field.indexOf(".")+1);
            itemids.append(fileldItemid).append(",");
        }
        Map extendMap = new HashMap();
        extendMap.put("otherCtrl","true");
        extendMap.put("otherSql",whereSql);
        //获取到导出使用的sql语句，使用增加的扩展
        String sql = getSqlbyRangeType(nbase, flag01, moduleId, "", conn,null, fieldsetList, fieldlist,range_type,condition,userView,musterType,parttimejobvalue,extendMap);
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select * from (");
        sqlbuf.append("select ROW_NUMBER() OVER (ORDER BY ");
        sqlbuf.append(orderBySqlFormat(fieldlist,sortField,"1",flag01,musterType,"0"));
        sqlbuf.append(") AS xuhao,");
        sqlbuf.append(orderBySqlFormat(fieldlist,sortField,"0",flag01,musterType,"0"));
        sqlbuf.append(" from (");
        sqlbuf.append(sql);
        sqlbuf.append(") hmctable ) fianlly");
        StringBuffer selectCountBuffer = new StringBuffer();
        selectCountBuffer.append("select count(1) from (");
        selectCountBuffer.append(sql).append(") countTable");
        int totalCount = 0;
        RowSet rowSet = null;
        try {
            rowSet = dao.search(selectCountBuffer.toString());
            if(rowSet.next()){
                totalCount = rowSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GeneralException("导出Excel数据失败，请联系管理员");
        }finally {
            PubFunc.closeResource(rowSet);
        }
        String musterName = getMusterName(tabid);
        HashMap<String, Object>  paramMap = new HashMap<String, Object>();
        paramMap.put("tabid", tabid);
        paramMap.put("musterName", musterName);
        paramMap.put("userName", userView.getUserName());
        paramMap.put("showMusterSql", sqlbuf.toString());
        paramMap.put("orderBySql", "");
        paramMap.put("moduleID", moduleId);
        paramMap.put("totalCount", totalCount);
        paramMap.put("musterType", musterType);
        paramMap.put("itemids",itemids.toString().toUpperCase());
        ExecuteExcel executeExcel=new ExecuteExcel(conn);
        executeExcel.setUserview(userView);
        executeExcel.setTabid(tabid);
        String outName = executeExcel.createExcel(userView.getUserName(),paramMap);
        return outName;
    }
}
