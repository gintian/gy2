package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TableAnalyse;
import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class reportEditViewTrans extends IBusiness {
    
    /**
     * 保存当前登录用户，因为userView会被修改为发起人
     */
    private UserView loginUser;
    
	public void execute() throws GeneralException {
		try
		{
			loginUser = userView;  // 保存当前登录用户
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String tabid=(String)hm.get("code");
			
			//------------------------------------报表上报是否审批  zhaoxg 2013-1-29 --------------------
			TTorganization tt_organization=new TTorganization(this.getFrameconn());
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String isApprove = sysbo.getValue(Sys_Oth_Parameter.ISAPPROVE);
			String relation_id = sysbo.getValue(Sys_Oth_Parameter.APPROVEID);
			RecordVo vo=tt_organization.getSelfUnit(this.getUserView().getUserName());
			String username = SafeCode.decode((String) hm.get("username"));
			String isUpapprove = (String) hm.get("isUpapprove");
			String obj1 = (String) hm.get("obj1");
			String returnType = (String)hm.get("returnType");
			returnType = returnType==null?"":returnType;
			String isSave = "";
			hm.remove("obj1");
			hm.remove("username");
			hm.remove("returnType");
			String isshenpi = "";
			String isPrint = "true";
			boolean isUpapprove1 = false;
			hm.remove("isUpapprove");
			if(isUpapprove==null){
				isUpapprove = "";
			}
			if(obj1==null){
				obj1 = "2";
			}else if("1".equals(obj1)){
				isPrint = "false";
			}
			this.getFormHM().put("isUpapprove", isUpapprove);		
			if(username==null|| "".equals(username)){
				

				if(isApprove1(this.userView.getUserName())){
					if(userView.isHaveResource(IResourceConstant.REPORT,tabid)){
						username = this.userView.getUserName();//如果当前用户是报表负责人，那么优先显示自己的数据
						isshenpi = "1";
					}else{
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
					}
					/* 标识：2383 报表管理：非su用户（汉字的用户），取数，归档后，再取数，手工维护值，保存的时候报保存失败 xiaoyun 2014-6-9 start */
					// 未将上报用户保存到formbean中
					this.getFormHM().put("username", SafeCode.encode(username));
					/* 标识：2383 报表管理：非su用户（汉字的用户），取数，归档后，再取数，手工维护值，保存的时候报保存失败 xiaoyun 2014-6-9 end */
				}else{
					//username=isApprove(tabid);
					username=approve();//不是负责人，找是不是有人报表给他
					if(username!=null&&!"".equals(username)){
						userView = new UserView(username, this.frameconn);
						userView.canLogin();
						isPrint = "false";
					}
					this.getFormHM().put("username", SafeCode.encode(username));//报批时候获取报表负责人
				}
				if("".equals(username)){
					isshenpi = "1";
				}


			}else if(username.equals(this.userView.getUserName())){
				isshenpi = "1";
			}
			if(username==null|| "".equals(username)){
				username = this.userView.getUserName();
				obj1 = "3";
				if(!userView.isHaveResource(IResourceConstant.REPORT,tabid)){
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
				}
			}
			if(username!=null||!"".equals(username)){
				isUpapprove1 = isUpapprove(username,relation_id);
			}
			if("false".equals(isUpapprove)){
				isUpapprove1 = false;
			}else if("true".equals(isUpapprove)){
				isUpapprove1 = true;
			}
			RecordVo a_selfVo=tt_organization.getSelfUnit(username);
			String unitcode1 = "";
			if(a_selfVo!=null){
				unitcode1=a_selfVo.getString("unitcode");//获取当前操作报表的上报人单位
			}	
			
			String status1 = getStatus(username,tabid);
			isUpapprove1 = isUpapprove(username,relation_id);
			if(isUpapprove1&& "4".equals(status1)){
				if(isApp(tabid,unitcode1,username)){
					this.getFormHM().put("isUpapprove", "true");
				}else{
					this.getFormHM().put("isUpapprove", "");
				}
				
			}else if(!isUpapprove1&& "4".equals(status1)){
				if(isApp(tabid,unitcode1,username)){
					this.getFormHM().put("isUpapprove", "false");
				}else{
					this.getFormHM().put("isUpapprove", "");
				}
				
			}
			if("1".equals(isshenpi)){
				this.getFormHM().put("isUpapprove", "");
			}
			if(!username.equals(this.userView.getUserName())){
				userView = new UserView(username, this.frameconn);
				userView.canLogin();
			}
			this.getFormHM().put("unitcode1", unitcode1);
			this.getFormHM().put("username1", username);
			this.getFormHM().put("isPrint", isPrint);
			this.getFormHM().put("obj1", obj1);
//			userView = new UserView(username, this.frameconn);
//			userView.canLogin();
			//------------------------------------------------------------------------------------------
			
			String status=getStatus(username,tabid);
			if("2".equals(status)&& "3".equals(obj1))
				this.getFormHM().put("reboundDescription","true");
			else
				this.getFormHM().put("reboundDescription","false");
			if(hm.get("operates")!=null&& "1".equals((String)hm.get("operates"))) //报表查阅
			{
				status="3";
				hm.remove("operates");
			}
			String reportlisthref =(String)hm.get("reportlisthref");
			if(reportlisthref!=null)
				reportlisthref=reportlisthref.replace("`", "&");
			else
				reportlisthref="";	
	
			this.getFormHM().put("reportlisthref",reportlisthref);
			hm.remove("reportlisthref");
			String operateObject=(String)hm.get("operateObject");
			if(operateObject==null||operateObject.trim().length()==0)
				operateObject="1";
			if(operateObject!=null&& "null".equals(operateObject))
				operateObject="1";
			String usrID=this.getUserView().getUserId();
			String aa = this.getUserView().getUserName();
			TnameBo      tnameBo=new TnameBo(this.getFrameconn(),tabid,usrID,this.getUserView().getUserName(),"view");
			
			TableAnalyse tableAnalyse=new TableAnalyse(this.getFrameconn(),1,Integer.parseInt(tabid),tnameBo);			
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet recset=dao.search("select tsortid,xmlstyle from tname where tabid="+tabid);
			String use_scope_cond="0";
			String scopeid = "0";
			String scopename = "";
			String scopeownerunitid = "";
			String tsortid="";
			ArrayList scopelist = new ArrayList();
			
			DbWizard dbWizard=new DbWizard(this.getFrameconn());
			DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
			TgridBo tgridBo=new TgridBo(this.getFrameconn());
			if(dbWizard.isExistTable("tscope", false)){
				//判断字段units是否存在，不存在就加上字段units
				if(dbWizard.isExistField("tscope", "units",false)){
					
				}else{
					Table table=new Table("tscope");
					table.addField(tgridBo.getField2("units","统计单位","M"));
					dbWizard.addColumns(table);
				}
			if(recset.next())
			{
				tsortid = recset.getString("tsortid");
				tableAnalyse.analyseTable(recset.getString("tsortid"));	
				//<use_scope_cond>0</use_scope_cond>
				String scope_cond = Sql_switcher.readMemo(recset, "xmlstyle");
				if(scope_cond!=null&&scope_cond.indexOf("<use_scope_cond>")!=-1&&scope_cond.indexOf("</use_scope_cond>")!=-1){
					use_scope_cond= scope_cond.substring(scope_cond.indexOf("<use_scope_cond>")+16,scope_cond.indexOf("</use_scope_cond>"));
					//判断是否存在scopeid字段
					   /**版本之间的差异控制，市场考滤*/
			        VersionControl ver_ctrl=new VersionControl();
			        /**版本控制*/
					if(ver_ctrl.searchFunctionId("290207")&&use_scope_cond.length()>0&& "1".equals(use_scope_cond)){
					
					if(dbWizard.isExistField("tb"+tabid, "scopeid",false)){//判断字段是否存在，没则生成，同时付默认值0
					String sql = "select scopeid from tb"+tabid+" where username='"+this.userView.getUserName()+"'";
					this.frowset = dao.search(sql);
					if(this.frowset.next()){
						scopeid= ""+this.frowset.getInt("scopeid");
					}
					}else{
						
						Table table=new Table("tb"+tabid);
						table.addField(tgridBo.getField2("scopeid","统计口径id","I"));
						dbWizard.addColumns(table);
						dao.update(" update tb"+tabid+" set scopeid=0 ");
						
					}
					
					if(!"0".equals(scopeid)){
						String sql = "select * from tscope where scopeid="+scopeid;
						this.frowset = dao.search(sql);
						if(this.frowset.next()){
							scopename = this.frowset.getString("name");	
							scopeownerunitid = this.frowset.getString("owner_unit");
							if(scopeownerunitid.indexOf("UM")!=-1||scopeownerunitid.indexOf("UN")!=-1)
								scopeownerunitid = scopeownerunitid.substring(2,scopeownerunitid.length());
						}else{
							scopeid="0";
						}
					}
					scopelist = getScopeList(tabid,scopeid);
					}else{
						use_scope_cond="0";	
					}
					}else{
						//判断是否存在scopeid 删除scopeid字段
						Table table2=new Table("tb"+tabid);
						if(dbWizard.isExistField("tb"+tabid, "scopeid",false)){
							Field obj=tgridBo.getField2("scopeid","统计口径id","N");
							table2.addField(obj);
							dbWizard.dropColumns(table2);
							dbmodel.reloadTableModel(table2.getName());
						}
					}
					Document doc=null;
					String auto_arch="";
					if(scope_cond!=null&&scope_cond.trim().length()>0){
						
						doc = PubFunc.generateDom(scope_cond);
						if(doc!=null){
							XPath xPath = XPath.newInstance("/param/auto_archive");
							Element auto_archiv = (Element) xPath.selectSingleNode(doc);
							if (auto_archiv != null) {
								auto_arch=auto_archiv.getText();
							}
							if(auto_arch!=null){
								this.getFormHM().put("auto_archive", auto_arch);
							}else{
								this.getFormHM().put("auto_archive",  "0");								
							}
						}
					}else{
						this.getFormHM().put("auto_archive", "0");
					}
				}
				
			}else{
				if(recset.next())
				{
					tsortid = recset.getString("tsortid");
				}
			}
			this.getFormHM().put("scopeid", scopeid);
			this.getFormHM().put("use_scope_cond", use_scope_cond);
			this.getFormHM().put("scopename", scopename);
			this.getFormHM().put("scopeownerunitid",scopeownerunitid);
			this.getFormHM().put("scopelist", scopelist);
			//同步该表页面显示的参数，只同步参数长度变大，只修改字符和数值型
			 HashMap param_map=tnameBo.getParamMap();
			 if(param_map!=null){
					java.util.Iterator it = param_map.entrySet().iterator();
					ResultSetMetaData   rsmd =null;
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						String keys = (String) entry.getKey();
						HashMap map_values = (HashMap)entry.getValue();
						
						 if(map_values!=null&&map_values.get("paramtype")!=null)
						 {
							 if(((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.character"))||((String)map_values.get("paramtype")).equals(ResourceFactory.getProperty("kq.formula.counts")))
							 {
								 if(map_values.get("paramscope")!=null&&map_values.get("paramename")!=null&&map_values.get("paramlen")!=null){
									 //需要同步的表全局参数tp_p表类参数"tp_s"+tsortid表内参数"tp_t"+tabid
									 int paramscope = Integer.parseInt(""+map_values.get("paramscope"));
									 switch(paramscope){
									 case  0:{
										 Table table=new Table("tp_p");
										 Field field=null;
											if(dbWizard.isExistTable("tp_p",false)){ 
												if(!dbWizard.isExistField("tp_p",""+map_values.get("paramename"),false)){ 
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
													this.frowset=dao.search(" select "+map_values.get("paramename")+" from tp_p ");
													   rsmd = this.frowset.getMetaData();
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
											}
											break;
									 }
									 case  1:{
										 Table table=new Table("tp_s"+tsortid);
										 Field field=null;
											if(dbWizard.isExistTable("tp_s"+tsortid,false)){ 
												if(!dbWizard.isExistField("tp_s"+tsortid,""+map_values.get("paramename"),false)){ 
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
													this.frowset=dao.search(" select "+map_values.get("paramename")+" from tp_s"+tsortid);
													   rsmd = this.frowset.getMetaData();
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
											}
										 break;
										 }
									 case  2:{
										 Table table=new Table("tp_t"+tabid);
										 Field field=null;
											if(dbWizard.isExistTable("tp_t"+tabid,false)){ 
												if(!dbWizard.isExistField("tp_t"+tabid,""+map_values.get("paramename"),false)){ 
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
													this.frowset=dao.search(" select "+map_values.get("paramename")+" from tp_t"+tabid);
													   rsmd = this.frowset.getMetaData();
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
											}
										 break;
										 }
									 }
								 }
							 }
							
						 }
					
						}
						
					}
			
			// 自动取数
			if("1".equals(hm.get("fillreport"))){
                hm.remove("fillreport");
                tnameBo.fillReport(userView);
			}
            if("0".equals(hm.get("showpaper"))){
                hm.remove("showpaper");
                tnameBo.setShowReportHtmlPaper(false);
            }
            if("0".equals(hm.get("showbuttons"))){
			    hm.remove("showbuttons");
			    tnameBo.setShowReportHtmlToolbar(false);
			}
			 
			String       htmlCode="";

			if("1".equals(operateObject)){
				if("1".equals(obj1)){
					htmlCode =tnameBo.getReportHtml("0",username,operateObject,"","");	
				}else{
					if(isApp(tabid,unitcode1,username)){
						htmlCode =tnameBo.getReportHtml("0",username,operateObject,"","");	
					}else{
						if(username.equals(this.getUserView().getUserName())){
							htmlCode =tnameBo.getReportHtml(status,username,operateObject,"","");
						}else{
							htmlCode =tnameBo.getReportHtml("3",username,operateObject,"","");
							isSave = "1";
						}
						
					}
						
				}
				
			}
			//TTorganization tt_organization=new TTorganization(this.getFrameconn());
			tt_organization.setValidedateflag("1");
			//RecordVo a_selfVo=tt_organization.getSelfUnit(this.getUserView().getUserName());			
			if("2".equals(operateObject)){
				if(a_selfVo!=null){
				htmlCode =tnameBo.getReportHtml(status,this.userView.getUserName(),operateObject,""+this.getFormHM().get("unitcode"),a_selfVo.getString("unitcode"));
				}
				}
			String rows=String.valueOf(tnameBo.getColInfoBGrid().size());
			String cols=String.valueOf(tnameBo.getRowInfoBGrid().size());
		
			ArrayList paramnameList=tnameBo.getParamenameList();
			StringBuffer param_str=new StringBuffer("");
			for(Iterator t=paramnameList.iterator();t.hasNext();)
			{
				param_str.append("/"+(String)t.next());
			}
			
			if("3".equals(status)|| "4".equals(status)){
				if("1".equals(obj1)){
					this.getFormHM().put("save","true");
				}else{
					this.getFormHM().put("save","false");
				}
				
			}else if("1".equals(isSave)){
				this.getFormHM().put("save","false");
			}else{
				this.getFormHM().put("save","true");
			}
			if("3".equals(status)|| "1".equals(status))
				this.getFormHM().put("appeal","false");
			else
				this.getFormHM().put("appeal","true");
			
			
			if(a_selfVo!=null){
			RecordVo treport_vo=new RecordVo("treport_ctrl");
			treport_vo.setString("unitcode",a_selfVo.getString("unitcode"));
			treport_vo.setInt("tabid", Integer.parseInt(tabid));
			treport_vo=dao.findByPrimaryKey(treport_vo);
			if(treport_vo!=null){
			this.getFormHM().put("status",String.valueOf(treport_vo.getInt("status")));
			}
			this.getFormHM().put("selfUnitcode",a_selfVo.getString("unitcode"));
			this.getFormHM().put("existunicode","1");
			}else{
				this.getFormHM().put("status","0");	
				this.getFormHM().put("existunicode","0");
			}
			//---------------------报表上报是否支持审批  zhaoxg 2013-1-26 -------------------

			String isApproveflag = "";
			if("true".equals(isApprove)&&("00".equals(relation_id))){
				isApproveflag = "1";
			}else if("true".equals(isApprove)&&(!"00".equals(relation_id))){
				if(isMainbody(relation_id,username)&&isApp(tabid,unitcode1,username)&&!isUpapprove1){
					isApproveflag = "2";
					this.getFormHM().put("save","true");
				}else if(!isApp(tabid,unitcode1,username)||isUpapprove1){
					isApproveflag = "5";
				}else{
					isApproveflag = "3";
				}
				
			}else{
				isApproveflag = "4";
			}

			this.getFormHM().put("isApproveflag", isApproveflag);
			//----------------------------------------------------------------------------
			this.getFormHM().put("userName",this.getUserView().getUserName());
			
			this.getFormHM().put("narch",String.valueOf(tnameBo.getTnameVo().getInt("narch")));  //报表类型
			this.getFormHM().put("htmlCode",htmlCode);
			this.getFormHM().put("tabid",tabid);
			this.getFormHM().put("rows",rows);
			this.getFormHM().put("cols",cols);
			this.getFormHM().put("param_str",param_str.toString().length()>0?param_str.substring(1):param_str.toString());
			this.getFormHM().put("operateObject",operateObject);
			
			String isCollectCheck="0";
			if("-1".equals(status)|| "0".equals(status)|| "2".equals(status))
				isCollectCheck="1";
			this.getFormHM().put("isCollectCheck",isCollectCheck );
			String dxt = (String)hm.get("returnvalue");
			if(dxt!=null&&!"dxt".equals(dxt))
				hm.remove("returnvalue");
			if(dxt==null)
				dxt="";
			this.getFormHM().put("returnflag", dxt);
			this.getFormHM().put("returnType", returnType);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 判断审批关系中是否定义用户的审批主体 zhaoxg 2013-1-26
	 * @param relation_id 审批关系id
	 * @return
	 */
	public boolean isMainbody(String relation_id,String username){
		boolean isMainbody = false;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			String sql = "select * from t_wf_mainbody where relation_id = "+relation_id+"";
			RowSet rs = dao.search(sql);
			while(rs.next()){
				if(rs.getString("object_id").equals(username)){
					isMainbody = true;
				}
				
			}
			rs.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return isMainbody;
	}
	/**
	 * 判断当前用户是否是审批人,如果是获取报批人信息  zhaoxg 2013-1-31
	 * @param username
	 * @param tabid
	 * @return
	 */
	public String isApprove(String tabid){
		String username = "";
		String[] appuser = null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			String sql = "select appuser,username from treport_ctrl where tabid = "+tabid+"";
			RowSet rs = dao.search(sql);
			while(rs.next()){
				if(rs.getString("appuser")!=null){
					appuser = rs.getString("appuser").split(";");
					for(int i=0;i<appuser.length;i++){
						if(appuser[i].equals(this.getUserView().getUserFullName())||appuser[i].equals(this.getUserView().getUserName())){
							username=rs.getString("username");
						}
					}

				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return username;
	}

	public String getStatus(String username,String tabid)
	{
		String status="-1";
		ContentDAO dao=new ContentDAO(this.getFrameconn());	
		try
		{
			this.frowset=dao.search("select status from treport_ctrl where  tabid="+tabid+" and unitcode=(select unitcode from operuser where username='"+username+"')");
			if(this.frowset.next())
				status=this.frowset.getString("status");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return status;
	}
	public ArrayList getScopeList(String tabid,String scopeid){
		ArrayList scopelist = new ArrayList();
		CommonData data=new CommonData("","");
		scopelist.add(data);
		//如果用户没有定义操作单位则按管理范围来匹配。
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		StringBuffer  scopeidstr = new StringBuffer();
		try {
			this.frowset = dao.search("select * from tscope ");
			while(this.frowset.next()){
				list.add(this.frowset.getString("scopeid"));
		
			}
			for(int a=0;a<list.size();a++){
				String scopeid2 = (String)list.get(a);
				StringBuffer str = new StringBuffer(" select * from tscope where scopeid ="+scopeid2+" ");
				String temps="";
				
				if (!userView.isSuper_admin())
				{
					String operOrg = userView.getUnit_id();// 操作单位
					StringBuffer tempstr = new StringBuffer();
					if (operOrg.length() > 2)
					{
						String[] temp = operOrg.split("`");
						for (int i = 0; i < temp.length; i++)
						{
							if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))|| "UM".equalsIgnoreCase(temp[i].substring(0, 2))){
								tempstr.append(" or  owner_unit like 'UM" + temp[i].substring(2) + "%'");
								tempstr.append(" or  owner_unit like 'UN" + temp[i].substring(2) + "%'");
							}
						}
						if(tempstr.length()>3){
							temps+=tempstr.toString().substring(3);
						}
					} else
					{	//走管理范围
						
						String code = "-1";
						if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)// 管理范围
						{
							code = userView.getManagePrivCodeValue();
							if (code!=null)
								{
								if(code.indexOf("UN")!=-1||code.indexOf("UM")!=-1){
									tempstr.append(" or  owner_unit like 'UM" + code.substring(2) + "%'");
									tempstr.append(" or  owner_unit like 'UN" + code.substring(2) + "%'");
								}else{
									tempstr.append(" or  owner_unit like 'UN" + code + "%'");
									tempstr.append(" or  owner_unit like 'UM" + code + "%'");
								}
								}else{
									tempstr.append("and  1=2");
								}
								}else{
									tempstr.append("and  1=2");
								}
						if(tempstr.length()>3){
							temps+=tempstr.toString().substring(3);
						}
						}
				
				if(temps.length()>0){
					str.append(" and ("+temps+")");
				}
				this.frowset = dao.search(str.toString());
				if(this.frowset.next()){
					scopeidstr.append(","+scopeid2);
				}
				}else{
					scopeidstr.append(","+scopeid2);
				}
			}
		//判断是否存在scopeid字段
		DbWizard dbWizard=new DbWizard(this.getFrameconn());
		if(dbWizard.isExistField("tb"+tabid, "scopeid",false)){//判断字段是否存在，没则生成，同时付默认值0
		}else{
			TgridBo tgridBo=new TgridBo(this.getFrameconn());
			Table table=new Table("tb"+tabid);
			table.addField(tgridBo.getField2("scopeid","统计口径id","I"));
				dbWizard.addColumns(table);
				dao.update(" update tb"+tabid+" set scopeid=0 ");
			
		}
		//所属机构
		String sql = "";
		if(scopeidstr.toString().length()>0){
			sql = "select * from tscope  where scopeid in("+scopeidstr.substring(1)+") order by displayid";
		}else{
			sql = "select * from tscope where 1=2 order by displayid";
		}
			
			this.frowset = dao.search(sql);
			int count =0;
			while(this.frowset.next()){
				if(count==0){

					if("0".equals(scopeid)){
						scopeid=this.frowset.getString("scopeid");
					}
					 data=new CommonData(this.frowset.getString("scopeid"),this.frowset.getString("name"));
					scopelist.add(data);
				
				}else{
				 data=new CommonData(this.frowset.getString("scopeid"),this.frowset.getString("name"));
				scopelist.add(data);
				}
				count++;
			}
	
	}
	 catch (SQLException e) {
		e.printStackTrace();
	}
	 catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scopelist;
	}
	
	/**
	 * 判断当前用户是不是顶级审批人
	 */
	public boolean isUpapprove(String username,String relation_id){
		boolean isUpapprove = false;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			String sp_grade = "";
			String max = "";
			String sql = "select sp_grade from t_wf_mainbody where relation_id = '"+relation_id+"' and object_id = '"+username+"' and mainbody_id = '"+this.userView.getUserName()+"'";
			String sqll = "select max(sp_grade) as max from t_wf_mainbody where relation_id = '"+relation_id+"' and object_id = '"+username+"'";
			RowSet rs = dao.search(sql);
			RowSet rowset = dao.search(sqll);
			if(rs.next()){
				sp_grade = rs.getString("sp_grade");
			}
			if(rowset.next()){
				max = rowset.getString("max");
				if(max!=null&&!"".equals(sp_grade)){
					if(max.equals(sp_grade)){
						isUpapprove = true;
					}
				}
			}
			
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		
		return isUpapprove;
	}
	/**
	 * 当前登录用户有没有报批权限
	 */
	public boolean isApp(String tabid,String unitcode,String username){
		boolean isapp = false;
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String sql = "select currappuser,status from treport_ctrl where tabid = '"+tabid+"' and unitcode = '"+unitcode+"'";
			RowSet rs = dao.search(sql);
			if(rs.next()){
				String user = rs.getString("currappuser");
				String status = rs.getString("status");
				if(loginUser.getUserName().equals(user)){/*userView*/
					isapp = true;
				}
				if(user==null|| "".equals(user)){
					if(username.equals(loginUser.getUserName())){
						isapp = true;
					}else{
						isapp = false;
					}
				}
				if("1".equals(status)){
					isapp = false;
				}
			}
			
		}catch (Exception e)
        {
            e.printStackTrace();
        }
		return isapp;
	}
	/**
	 * 判断当前用户是否负责报表  zhaoxg 2013-2-17
	 * @param username
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public boolean isApprove1(String username) throws GeneralException, SQLException{
		boolean isapprove = false;
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		ResultSet rs = null;
		try{

			String sql = "select username from operUser,tt_organization  where operUser.unitcode=tt_organization.unitcode";
			rs = dao.search(sql.toString());
			while(rs.next()){
				if(username.equals(rs.getString("username"))){
					isapprove = true;
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return isapprove;
	}
	/**
	 * 获取报批人信息   zhaoxg 2013-2-17
	 * @param userName
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String approve() throws GeneralException, SQLException{
		String approve = "";

		ResultSet rs = null;
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		try{
			String sql = "select appuser,username from treport_ctrl";
			rs = dao.search(sql.toString());
			while(rs.next()){
				String appuser = rs.getString("appuser");
				if(appuser!=null){
					String[] aa = appuser.split(";");
					for(int i=0;i<aa.length;i++){
						if(aa[i].equals(this.userView.getUserFullName())){
							approve = rs.getString("username");
						}
					}
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return approve;
	}
}
