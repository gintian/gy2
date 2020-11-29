/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.*;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 *<p>Title:查询薪资表数据交易</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-6-30:上午09:44:45</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchGzTableDataTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		
		
	//	long a=System.currentTimeMillis();
		
		String flow_flag=(String)this.getFormHM().get("flow_flag");
		String salaryid=(String)this.getFormHM().get("salaryid");
		String a_code=(String)this.getFormHM().get("a_code");
		String gz_module=(String)((HashMap)this.getFormHM().get("requestPamaHM")).get("gz_module"); 
		//如果用户没有当前薪资类别的资源权限   20140903  dengcan
		CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
		safeBo.isSalarySetResource(salaryid,gz_module);
		 
	/*	SalaryCtrlParamBo ctrl_par = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
	    String comflag= ctrl_par.getValue(SalaryCtrlParamBo.COMPARE_FIELD);
	    String priv_mode1="0";//是否出现数据比对菜单
	    if(comflag!=null&&!comflag.equals(""))
	    	priv_mode1 = "1";
		if(salaryid==null||salaryid.equalsIgnoreCase("-1"))
		{
			throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
		}
		*/
		String showUnitCodeTree="0";  //是否按操作单位来显示树
		
		
		String flag=(String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		/**人员过滤条件编号，全部为all*/
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String swhere="";
		String condid=(String)this.getFormHM().get("cond_id_str");
		if(condid==null|| "".equalsIgnoreCase(condid))
			condid=(String)this.getFormHM().get("condid");	
		if(condid==null|| "".equalsIgnoreCase(condid))
			condid="all";
		/**项目过滤条件号，全部为all*/
		String itemid=(String)this.getFormHM().get("itemid");
		if(itemid==null|| "".equalsIgnoreCase(itemid))
			itemid="all";
		/**解决当选择项目过滤，进行导入操作，导入或返回后项目过滤为全部问题lizhenwei add*/
		if(((HashMap)this.getFormHM().get("requestPamaHM")).get("import")!=null)
		{
			itemid=(String)(((HashMap)this.getFormHM().get("requestPamaHM")).get("import"));
			((HashMap)this.getFormHM().get("requestPamaHM")).remove("import");
		}
		
		try
		{
			//暂时保留系统自建字段
//			DbWizard dbWizard=new DbWizard(this.getFrameconn());
//			Table table=new Table("gzitem_filter");	
//			if(!dbWizard.isExistField("gzitem_filter", "scope")){//判断字段是否存在，没则生成，同时付默认值0
//		
//				Field obj=new Field("scope","使用范围");
//				obj.setDatatype(DataType.INT);
//				obj.setKeyable(false);			
//				obj.setVisible(false);	
//				table.addField(obj);
//				obj=new Field("username","创建用户");
//				obj.setDatatype(DataType.STRING);
//				obj.setKeyable(false);			
//				obj.setVisible(false);
//				obj.setLength(255);
//				obj.setAlign("left");
//				table.addField(obj);
//				dbWizard.addColumns(table);
//			}
			//自建字段
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			showUnitCodeTree=gzbo.getControlByUnitcode();
			 
			this.getFormHM().put("ff_setname", ("0".equals(gz_module)?"薪资类别:":"保险类别:")+gzbo.getTemplatevo().getString("cname"));
			String comflag= gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.COMPARE_FIELD);
		    String priv_mode1="0";//是否出现数据比对菜单
		    if(comflag!=null&&!"".equals(comflag))
		    	priv_mode1 = "1";
			if(salaryid==null|| "-1".equalsIgnoreCase(salaryid))
			{
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
			}
	        if (!"soufang".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))){//20150603  搜房网基于效率问题,
	       // 	gzbo.synchronismSalarySet();
              
	        	  gzbo.SalarySet();
	        	  gzbo.syncGzTableStruct();
	        }
	        else {
	        	gzbo.SalarySet();
	            DbWizard dbw=new DbWizard(this.frameconn);
	            if(!dbw.isExistTable(gzbo.getGz_tablename(), false))
	            {
	                gzbo.createGzDataTable();
	            }
	        }		
			//因为新建的项目过滤条件不起作用，所以将下面三行行代码移动至此，wangrd 2013-12-04
            String filterid=gzbo.getFiltersIds(salaryid);
            ArrayList itemfilterlist=gzbo.getItemFilterList(filterid);
            this.getFormHM().put("itemlist", itemfilterlist);

            if ("default".equals(itemid)){
                itemid =gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.FLOW_CTRL,"default_filterid");
                if ((","+filterid+",").indexOf(","+itemid+",")<0){//此过滤项目已删除
                    itemid=""; 
                }
                String _str=gzbo.getFiterItem();
                if ((","+_str+",").indexOf(","+itemid+",")<0){//此过滤项目已改成私有
                    itemid=""; 
                }
            }
            if(itemid==null|| "".equalsIgnoreCase(itemid))
                itemid="all";
            ArrayList fieldlist=gzbo.getFieldlist();
            ArrayList salary_fieldlist=(ArrayList)fieldlist.clone();
	
			String pro_field = (String)this.getFormHM().get("proright_str");
			this.getFormHM().put("proright_str",pro_field);
			if(!(pro_field==null || "".equalsIgnoreCase(pro_field)))
			{
				fieldlist=gzbo.filterItemsList(fieldlist,pro_field);
			}else if("new".equalsIgnoreCase(itemid) && "".equalsIgnoreCase(pro_field)){
				fieldlist=gzbo.getFieldlist();
			}
			else {
				if(!"all".equalsIgnoreCase(itemid))
					fieldlist=gzbo.filterItemList(fieldlist,itemid);	
			}
			if(!"all".equalsIgnoreCase(condid)&&!"new".equalsIgnoreCase(condid))
			swhere=gzbo.getFilterWhere(condid,gzbo.getGz_tablename());
			
			if(gzbo.getManager()!=null&&gzbo.getManager().trim().length()>0)
				gzbo.update("update "+gzbo.getGz_tablename()+" set sp_flag2='01' where sp_flag2 is null");
			//当表类的条件和权限在建完数据后改动了,需进行权限及条件过滤(需在变动比对中去掉)
		//	gzbo.delNoConditionData2(gzbo.getGz_tablename());

			/**项目过滤*/
			
            /**必须显示审批标识 用于控制其他记录是否可编辑 wangrd */
			/* 2013-11-25 dengc  不管薪资帐套是否审批，都必须包含审批标识指标
			if (!gzbo.isApprove()){
    			Field field=new Field("sp_flag",ResourceFactory.getProperty("label.gz.sp"));
                field.setLength(50);
                field.setCodesetid("23");
                field.setDatatype(DataType.STRING);
                field.setReadonly(true);
                field.setVisible(false);
                fieldlist.add(field); 
			}*/
            
  			//xieguiquan  2010-08-25
			this.getFormHM().put("hidenflag", "0");
			String _str=",SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,A00Z0,A00Z1,NBASE,";
			RecordVo vo =gzbo.getRealConstantVo(this.getFrameconn(),Integer.parseInt(salaryid));
			if(vo!=null&&vo.getString("lprogram")!=null&&vo.getString("lprogram").indexOf("hidden_items")!=-1
				&&vo.getString("lprogram").indexOf("hidden_item")!=-1){
		    		Document doc = PubFunc.generateDom(vo.getString("lprogram"));
		    		Element root = doc.getRootElement();
					Element hidden_items = root.getChild("hidden_items");
					if(hidden_items!=null){
						List list = hidden_items.getChildren();
						if(list.size()>0){
							for(int i =0;i<list.size();i++){
								Element temp = (Element)list.get(i);
								if(temp.getAttributeValue("user_name")!=null&&temp.getAttributeValue("user_name").toString().equalsIgnoreCase(this.userView.getUserName()))
									{
									String str = temp.getText();//隐藏指标
//									if(str.length()>0){  //zhaoxg  2013-5-24 如果没有隐藏指标  那么界面显示错误  所以不能这么限制
									str = ","+str+",";
									ArrayList alist = new ArrayList();
									for(int j =0;j<fieldlist.size();j++){
										Field field = (Field)fieldlist.get(j);
										Field cfield = (Field)field.clone();
										if(str.indexOf(","+field.getName()+",")!=-1){
											cfield.setVisible(false);
											
										}else{
											if("a01z0".equalsIgnoreCase(field.getName())){//初试化隐藏
												
												if(gzbo.getCtrlparam()!=null)
												{
													String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
													if(a01z0Flag==null|| "0".equals(a01z0Flag))
													{

													}
													else
														cfield.setVisible(true);
												}
												else
													cfield.setVisible(true);
												
												//停发标识
											}
											else if("add_flag".equalsIgnoreCase(field.getName())){
												//追加
											}else if("a0000".equalsIgnoreCase(field.getName())|| "a0100".equalsIgnoreCase(field.getName())){
												//系统默认不显示指标
											}
											else if(_str.indexOf(","+field.getName().toUpperCase()+",")==-1&& "0".equalsIgnoreCase(this.userView.analyseFieldPriv(field.getName())))
											{
												 
											}
											else
												cfield.setVisible(true);
										}
										alist.add(cfield);
									}
									if(alist.size()>0)
										fieldlist =alist;
//									}
									this.getFormHM().put("hidenflag", "1");
									break;
									}
								
								
							}
						}
					}
			}
			
			//dengcan 北京移动自动隐藏 审批状态和过程
			if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
			{
			 	for(int i=0;i<fieldlist.size();i++)
				{
					Field field=(Field)fieldlist.get(i);
				 	if("sp_flag".equalsIgnoreCase(field.getName())|| "appprocess".equalsIgnoreCase(field.getName())|| "a00z1".equalsIgnoreCase(field.getName()))
				 		field.setVisible(false);
				} 
			}
			SalaryPropertyBo bo=new SalaryPropertyBo(this.getFrameconn(),salaryid,Integer.parseInt(gz_module),this.getUserView());
			String priv_mode=bo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");			
			if(priv_mode==null|| "".equals(priv_mode))
			{
				priv_mode = "0";
			}
			//因为新建的项目过滤条件不起作用，所以将下面三行行代码移动至此，wangrd 2013-12-04
//            /**人员条件过滤，及项目过滤*/
//            String filterid=gzbo.getFiltersIds(salaryid);
//            ArrayList itemfilterlist=gzbo.getItemFilterList(filterid);
            ArrayList manfilterlist=gzbo.getManFilterList();
//
//			String pro_field = (String)this.getFormHM().get("proright_str");
//			this.getFormHM().put("proright_str",pro_field);
//			if(!(pro_field==null || pro_field.equalsIgnoreCase("")))
//			{
//				fieldlist=gzbo.filterItemsList(gzbo.getFieldlist(),pro_field);
//			}else if(itemid.equalsIgnoreCase("new") && pro_field.equalsIgnoreCase("")){
//				fieldlist=gzbo.getFieldlist();
//			}else{
//				if(!itemid.equalsIgnoreCase("all"))
//					fieldlist=gzbo.filterItemList(fieldlist,itemid);	
//			}
//			
//			if(!condid.equalsIgnoreCase("all")&&!condid.equalsIgnoreCase("new"))
//				swhere=gzbo.getFilterWhere(condid,gzbo.getGz_tablename());
//			
//			if(gzbo.isApprove()){
//				fieldlist=gzbo.filterList(fieldlist,flag);
//			}
			 
			
			StringBuffer filterWhl=new StringBuffer("");   //过滤条件--sql
			
			String salaryIsSubed="true";  //薪资是否为已提交状态
			if(gzbo.isSalaryPayed2())
				salaryIsSubed="false";
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			this.getFormHM().put("manager",manager!=null?manager:"");
			
			 
			/** 如果为操作用户同时薪资已确认的数据不让修改 */
			if(manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName())&& "true".equals(salaryIsSubed))
			{
				gzbo.setFieldlist_read(fieldlist);
				this.getFormHM().put("isEditDate","false");
			}
			else
				this.getFormHM().put("isEditDate", "true");
			
			//system.properties  salaryitem=false前台计算项不能编辑
			if(SystemConfig.getPropertyValue("salaryitem")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
				gzbo.setFieldlist_read2(fieldlist);
	
			isSpField(fieldlist); //2013-11-25  邓灿  处理设置的项目过滤不包括 审批标识指标，会造成已报批的记录仍能编辑问题。
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("subFlag", gzbo.getSubFlag());
			this.getFormHM().put("falg", flag);		
			/**数据过滤*/
			StringBuffer buf=new StringBuffer();
			buf.append("select * from ");
			buf.append(gzbo.getGz_tablename());
			buf.append(" where 1=1 ");   //dengcan
			/**是否是管理员参数*/
			String isSalaryManager="Y";
			if(!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId())&&(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName()))&& "1".equals(priv_mode))
			{
				isSalaryManager="N";
			}
			if(!(a_code==null|| "".equalsIgnoreCase(a_code)))
			{
//					/*
//					if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName())&&showUnitCodeTree.equals("0"))
//					{
//						if(a_code.substring(2).length()==0&&priv_mode.equals("1"))
//						{
//							if(this.userView.getManagePrivCode().length()==0)
//								a_code="UN";
//							else if(this.userView.getManagePrivCode().equals("@K"))
//								a_code=gzbo.getUnByPosition(this.userView.getManagePrivCodeValue());
//							else
//								a_code=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
//						}
//					}*/
					String codesetid=a_code.substring(0, 2);
					String value=a_code.substring(2);
					if("UN".equalsIgnoreCase(codesetid)&&value.length()>0)
					{
						if("1".equals(showUnitCodeTree))
						{
							String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
							orgid = orgid != null ? orgid : "";
							String deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
							deptid = deptid != null ? deptid : "";
							StringBuffer tempBuf=new StringBuffer("");
							tempBuf.append("");
							if(orgid.length()>0)
							{
								tempBuf.append(" or "+orgid+" like '"+value+"%' ");
							}
							if(deptid.length()>0)
							{
								tempBuf.append(" or "+deptid+" like '"+value+"%' ");
							}
							if(tempBuf.length()>0)
							{
								buf.append(" and ( "+tempBuf.substring(3)+" )");
								filterWhl.append(" and ( "+tempBuf.substring(3)+" )");
							}
						}
						else
						{
							buf.append(" and (B0110 like '");
							buf.append(value);
							buf.append("%'");	
							filterWhl.append(" and ("+gzbo.getGz_tablename()+".B0110 like '"+value+"%'");
							if("".equalsIgnoreCase(value))
							{
								buf.append(" or B0110 is null");
								filterWhl.append(" or "+gzbo.getGz_tablename()+".B0110 is null");
							}
							buf.append(")");
							filterWhl.append(")");
						}
						
					}
					if("UM".equalsIgnoreCase(codesetid)&&value.length()>0)
					{
						if("1".equals(showUnitCodeTree))
						{
							String deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
							deptid = deptid != null ? deptid : "";
							StringBuffer tempBuf=new StringBuffer("");
							tempBuf.append("");
							if(deptid.length()>0)
							{
								tempBuf.append(" or "+deptid+" like '"+value+"%' ");
							}
							if(tempBuf.length()>0)
							{
								buf.append(" and ( "+tempBuf.substring(3)+" )");
								filterWhl.append(" and ( "+tempBuf.substring(3)+" )");
							}
						}
						else
						{
							buf.append(" and E0122 like '");
							buf.append(value);
							buf.append("%'");
							filterWhl.append(" and "+gzbo.getGz_tablename()+".E0122 like '"+value+"%'");
						}
						
					}
			}
	
			String noManagerFilterSql="";//非管理员权限控制，用于薪资报表中打开任何项只要一切换，高级就不起作用了  bug 0035031
			if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName())&&!this.userView.isSuper_admin()){
				String unitIdByBusiOutofPriv = SystemConfig.getPropertyValue("unitIdByBusiOutofPriv");
				String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
				if(b_units.length()==0&&(unitIdByBusiOutofPriv!=null&& "1".equals(unitIdByBusiOutofPriv))){
					/**导入数据*/
					String dbpres=gzbo.getTemplatevo().getString("cbase");
					/**应用库前缀*/
					String[] dbarr=StringUtils.split(dbpres, ",");
					StringBuffer sub_str=new StringBuffer("");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						if(!this.userView.isSuper_admin()&&this.userView.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
						{
							sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"' and 1=2 )");
						}
						else
						{
							sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"' and upper(" + gzbo.getGz_tablename() + ".a0100) in (select a0100 "+this.userView.getPrivSQLExpression(pre, false)+" ) )");
						}
					}
					if(sub_str.length()>0)
					{
						noManagerFilterSql=" and ( "+sub_str.substring(3)+" )";
						buf.append(" and ( "+sub_str.substring(3)+" )");
						filterWhl.append(" and ( "+sub_str.substring(3)+" )");
					}
				}else{
					String privsql = gzbo.getPrivSQL("", "", salaryid, b_units);
					buf.append(" and ("+privsql+")");
					filterWhl.append(" and ("+privsql+")");
					noManagerFilterSql=" and ("+privsql+")";
				}
			}
			//-------------------------------------------------
			
//			if(showUnitCodeTree.equals("1"))
//			{
//				String unitcodes=this.userView.getUnit_id();  //UM010101`UM010105`
//				String whl_str=gzbo.getWhlByUnits();
//				noManagerFilterSql=whl_str;
//				buf.append(whl_str);
//				filterWhl.append(whl_str); 
//			}
//			else if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName())&&!this.userView.isSuper_admin())
//			{
//				/**导入数据*/
//				String dbpres=gzbo.getTemplatevo().getString("cbase");
//				/**应用库前缀*/
//				String[] dbarr=StringUtils.split(dbpres, ",");
//				StringBuffer sub_str=new StringBuffer("");
//				for(int i=0;i<dbarr.length;i++)
//				{
//					String pre=dbarr[i];
//					if(!this.userView.isSuper_admin()&&this.userView.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
//					{
//						sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"' and 1=2 )");
//					}
//					else
//					{
//						sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"' and upper(" + gzbo.getGz_tablename() + ".a0100) in (select a0100 "+this.userView.getPrivSQLExpression(pre, false)+" ) )");
//					}
//				}
//				if(sub_str.length()>0)
//				{
//					noManagerFilterSql=" and ( "+sub_str.substring(3)+" )";
//					buf.append(" and ( "+sub_str.substring(3)+" )");
//					filterWhl.append(" and ( "+sub_str.substring(3)+" )");
//				}
//			}
			
			//------------------------end--------------------------
			if(swhere.length()>0)
			{
				buf.append(" and ");
				buf.append("("+swhere+")");
				filterWhl.append(" and ("+swhere+")");
			} 
			/**人员筛选*/
			String empfiltersql =SafeCode.decode((String)this.getFormHM().get("empfiltersql"));
			empfiltersql=PubFunc.decrypt(empfiltersql);
			
			// gby,根据人员筛选，限制'重新导入'范围
			HashMap hashmap = (HashMap)this.getFormHM().get("requestPamaHM");
			String signSaveEmpfilterSql = (String)hashmap.get("signSaveEmpfilterSql");
			String saveEmpfilterSql = (String)this.getFormHM().get("filterWhl");
			saveEmpfilterSql = PubFunc.decrypt(saveEmpfilterSql);
			if("1".equalsIgnoreCase(signSaveEmpfilterSql) && !(saveEmpfilterSql == null || "".equals(saveEmpfilterSql))){
				saveEmpfilterSql = "1=1 " + saveEmpfilterSql;
				empfiltersql = saveEmpfilterSql;
			}
			hashmap.remove("signSaveEmpfilterSql");
			
			if(!(empfiltersql==null || "".equals(empfiltersql)))
			{
				buf.append(" and ("+empfiltersql+") ");
				filterWhl.append(" and ("+empfiltersql+")");
			}
			else
			{
				if("new".equalsIgnoreCase(condid))
				{
					condid="all";
				}
			}
			ArrayList queryFieldList=gzbo.getQueryFieldList(salaryid);
			if(((HashMap)this.getFormHM().get("requestPamaHM")).get("query")!=null)
			{
				((HashMap)this.getFormHM().get("requestPamaHM")).remove("query");
				queryFieldList=(ArrayList)this.getFormHM().get("queryFieldList");
				String sql = gzbo.getQuerySql(gzbo.getGz_tablename(), queryFieldList);//身份证也支持 %数字% 式的模糊查询 zhaoxg 2013-6-20
				if(sql!=null&&!"".equals(sql))
				{
					buf.append(sql);
					filterWhl.append(sql);
				}
				
			}
			this.getFormHM().put("queryFieldList", queryFieldList);
			this.userView.getHm().put("gz_filterWhl",filterWhl.toString());
			this.userView.getHm().put("noManagerFilterSql",noManagerFilterSql);
			this.getFormHM().put("filterWhl",PubFunc.encrypt(filterWhl.toString()));
			this.getFormHM().put("empfiltersql","");

			/**人员排序*/
			String sort_fields = (String)this.getFormHM().get("sort_table_detail");
			this.getFormHM().put("sort_table_detail","");
			if(sort_fields!=null && sort_fields.length()>0)
			{
				String orderby = gzbo.getorderbystr(sort_fields);
				buf.append(" order by "+orderby);
				gzbo.sortemp(sort_fields,this.userView.getUserName(),salaryid);	
				this.getFormHM().put("order_by",PubFunc.encrypt(orderby));
			}else{
				String orderby = (String)this.getFormHM().get("order_by");
				if(orderby!=null && orderby.length()>0)
				{
					/* 选择排序指标，点击【临时排序】后，在薪资表页面中进行项目筛选，系统报错 xiaoyun 2014-9-23 start */
					orderby = PubFunc.decrypt(SafeCode.decode(orderby));
					/* 选择排序指标，点击【临时排序】后，在薪资表页面中进行项目筛选，系统报错 xiaoyun 2014-9-23 end */
					if("sync".equalsIgnoreCase(orderby))
					{
						//buf.append(" order by nbase, A0000, A00Z0, A00Z1");
						String order_str=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView);
						if(order_str!=null&&order_str.trim().length()>0&&isExistErrorItem(order_str,gzbo))
							buf.append(" order by "+order_str);
						else
							buf.append(" order by  dbid,a0000, A00Z0, A00Z1");
					}
					else
						buf.append(" order by "+orderby);
					
				}else
				{
					
					//buf.append(" order by  b0110,e0122,dbname.dbid,a0000, A00Z0, A00Z1");
					String order_str=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER,this.userView); 
					if(order_str!=null&&order_str.trim().length()>0&&isExistErrorItem(order_str,gzbo))
						buf.append(" order by "+order_str);
					else
						buf.append(" order by  dbid,a0000, A00Z0, A00Z1");
				}
			}
			
			
			String relation_id=gzbo.getSpRelationId();
			String  sp_actor_str="";
			if(relation_id.length()>0)
				sp_actor_str=gzbo.getSpActorStr(relation_id);
			if(sp_actor_str.length()>0)
			{
				String[] temps=sp_actor_str.split("`");
				if(temps.length==1)
				{
					temps=temps[0].split("##");
					this.getFormHM().put("spActorName",temps[1]);
				}
				else
					this.getFormHM().put("spActorName", "");
			}
			else
				this.getFormHM().put("spActorName", "");
			this.getFormHM().put("sp_actor_str", SafeCode.encode(sp_actor_str));
			this.getFormHM().put("relation_id",relation_id);
			
			
			
			this.getFormHM().put("sql",PubFunc.encrypt(buf.toString()));
			this.getFormHM().put("tablename",gzbo.getGz_tablename());
			if(gzbo.isApprove())
			{
				if(relation_id.length()>0)
				{
					if(sp_actor_str.length()>0)
						this.getFormHM().put("appflag", "true");
					else
						this.getFormHM().put("appflag", "false");
				}
				else
					this.getFormHM().put("appflag", "true");
			}
			else
				this.getFormHM().put("appflag", "false");
			
			/**分析当前薪资表处于什么状态，结束*/
			if(!gzbo.isApprove())
				this.getFormHM().put("bedit", "true");
			else if(gzbo.isApprove()&&gzbo.isSubCondition())
				this.getFormHM().put("bedit", "true");
			else
				this.getFormHM().put("bedit", "false"); 			
            
            // 参数“允许修改发放结束已提交数据” 控制已提交的数据是否能编辑  wangrd 2013-11-15
            String gzRowCanEditStatus=",01,07,";
            if((!gzbo.isApprove()) && gzbo.isAllowEditSubdata()){
                gzRowCanEditStatus=",01,07,06,";        
            }                
            this.getFormHM().put("gzRowCanEditStatus",gzRowCanEditStatus);
			
		    this.getFormHM().put("salaryIsSubed", salaryIsSubed);

			/**删除垃圾数据*/
			gzbo.deleteSpareData();
			
			String  verify_ctrl=bo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL);
			if(verify_ctrl==null||verify_ctrl.trim().length()==0) ////是否按审核条件控制
				verify_ctrl="0";
			if("1".equals(verify_ctrl))
			{
				String verify_ctrl_ff=bo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_ctrl_ff");
				if(verify_ctrl_ff!=null&&verify_ctrl_ff.length()>0)
					verify_ctrl=verify_ctrl_ff;
			}
			String isControl=bo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
			if("1".equals(isControl))
			{
				String amount_ctrl_ff=bo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_ff");
				if(amount_ctrl_ff!=null&&amount_ctrl_ff.trim().length()>0)
					isControl=amount_ctrl_ff;
				
				
			}
			
			String  royalty_valid=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			if(royalty_valid==null)
				royalty_valid="0";
			this.getFormHM().put("royalty_valid", royalty_valid);
			this.getFormHM().put("isTotalControl", isControl);
			this.getFormHM().put("verify_ctrl",verify_ctrl);
			this.getFormHM().put("itemlist", itemfilterlist);
			//if(condid==null||condid.equalsIgnoreCase(""))
			this.getFormHM().put("condid",condid);
			this.getFormHM().put("condlist", manfilterlist);
			//if(itemid==null||itemid.equalsIgnoreCase(""))
			if("all".equalsIgnoreCase(itemid)&&pro_field!=null&&pro_field.trim().length()>1)
				itemid="new";
			this.getFormHM().put("itemid",itemid);
			/**lizhenwei add*/
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("a_code",a_code==null?"":a_code);
			/** lizhenwei add end*/
			
			this.getFormHM().put("userid",this.getUserView().getUserId());
			this.getFormHM().put("isAppealData",gzbo.getIsAppealData());
			this.getFormHM().put("flow_flag",flow_flag);
			this.getFormHM().put("priv_mode",priv_mode1);
			this.getFormHM().put("nbase", gzbo.getTemplatevo().getString("cbase"));
			
			this.getFormHM().put("isNotSpFlag2Records", gzbo.getIsNotSpFlag2Records());
			/**fanzhiguo add*/
			String isImportBonus="0";
			SalaryPropertyBo bo1=new SalaryPropertyBo(this.getFrameconn(),salaryid,Integer.parseInt(gz_module),this.getUserView());
			String  bonus=bo1.getCtrlparam().getValue(SalaryCtrlParamBo.BONUS);
			if(bonus!=null && !"".equals(bonus))
			    isImportBonus=bonus;
			/**wangrd add*/
			String isImportPiece="0";
			String  Piecevalid=bo1.getCtrlparam().getValue(SalaryCtrlParamBo.PIECEPAY,"valid");
			if(Piecevalid!=null && !"".equals(Piecevalid))
				isImportPiece=Piecevalid;
			
			
			String subNoShowUpdateFashion=gzbo.getLprogramAttri("no_show",SalaryLProgramBo.CONFIRM_TYPE);
			if(subNoShowUpdateFashion==null||subNoShowUpdateFashion.trim().length()==0)
				subNoShowUpdateFashion="0";
			this.getFormHM().put("isHistory",gzbo.isHistory2()==true?"1":"0");
			this.getFormHM().put("subNoShowUpdateFashion",subNoShowUpdateFashion);
			this.getFormHM().put("isImportBonus", isImportBonus);
			this.getFormHM().put("isImportPiece", isImportPiece);
			this.getFormHM().put("isSalaryManager",isSalaryManager);
			//returnFlag 0：返回薪资发放的类别界面 1：返回部门月奖金界面
			HashMap requestPamaHM = (HashMap)this.getFormHM().get("requestPamaHM");
			
			String ff_bosdate="";
			String ff_count="";
			if(requestPamaHM.get("ff_bosdate")!=null&&((String)requestPamaHM.get("ff_bosdate")).trim().length()>0)
			{
				ff_bosdate=(String)requestPamaHM.get("ff_bosdate");
				ff_count=(String)requestPamaHM.get("ff_count");
				this.getFormHM().put("ff_bosdate","业务日期:"+(String)requestPamaHM.get("ff_bosdate"));
				requestPamaHM.remove("ff_bosdate");
			}
			else
			{
				SalaryPkgBo pkb=new SalaryPkgBo(this.frameconn,this.userView);
				LazyDynaBean abean=null;
				if(manager.length()==0)
					abean=pkb.searchCurrentDate3(salaryid,this.userView.getUserName());
				else
					abean=pkb.searchCurrentDate3(salaryid,manager);
				
				String strYm=abean.get("strYm")!=null?(String)abean.get("strYm"):"";
				if(strYm.length()>0)
				{
					this.getFormHM().put("ff_bosdate","业务日期:"+strYm.substring(0, 7)); 
					ff_bosdate=strYm;
					ff_count=abean.get("strC")!=null?(String)abean.get("strC"):"";
				}
			}
			
			String isShowManagerFunction="0";
			String isRedo="0";
			if(manager==null||manager.length()==0||this.userView.getUserName().equals(manager))
			{
				isShowManagerFunction="1";
				if(ff_bosdate!=null&&ff_bosdate.trim().length()>0&&ff_count!=null&&ff_count.trim().length()>0)
				{ 
					if(getIsRedo(ff_bosdate,ff_count,this.userView.getUserName(),salaryid)==1)
						isRedo="1";
				}
			} 
			this.getFormHM().put("isRedo",isRedo);
			if("1".equals(isRedo))
				this.getFormHM().put("subNoShowUpdateFashion","1");
		    this.getFormHM().put("isShowManagerFunction",isShowManagerFunction); 
			
			
			String returnFlag=(String)requestPamaHM.get("returnFlag");
			requestPamaHM.remove("returnFlag");
			returnFlag=returnFlag==null?"0":returnFlag;
			this.getFormHM().put("returnFlag",returnFlag);		
			
			if("1".equals(returnFlag))
			{
			    String year  = (String)requestPamaHM.get("theyear");
			    String month  = (String)requestPamaHM.get("themonth");
			    String operOrg = (String)requestPamaHM.get("operOrg");
			    requestPamaHM.remove("theyear");
			    requestPamaHM.remove("themonth");
			    requestPamaHM.remove("orgcode");
			    this.getFormHM().put("theyear",year);	
			    this.getFormHM().put("themonth",month);
			    this.getFormHM().put("operOrg",operOrg);	
			}			
			/** fanzhiguo add end*/
			String isVisibleItem = gzbo.isVisibleItem();
			this.getFormHM().put("isVisibleItem", isVisibleItem);
			
			
			this.getFormHM().put("showUnitCodeTree",showUnitCodeTree);
			
			LazyDynaBean bean=new LazyDynaBean();
			String[] temp=ff_bosdate.split("\\-");
			String zjjt=(String)hm.get("zjjt");
			hm.remove("zjjt");
			if(ff_bosdate.length()>0&&ff_count!=null&&"1".equals(zjjt)){
				bean.set("year", temp[0]);//年
				bean.set("month", temp[1]);//月
				bean.set("count", ff_count);//次数
				LazyDynaBean _bean=SalaryTemplateBo.updatePendingTask(this.getFrameconn(), this.userView, this.userView.getUserName(),salaryid,bean,"4");
				PendingTask pt = new PendingTask();
				if("update".equals(_bean.get("flag"))){
					pt.updatePending("G", "G"+_bean.get("pending_id"), 2, "薪资审批", this.userView);
				}			
				if("update".equals(_bean.get("selfflag"))){
					pt.updatePending("G", "G"+_bean.get("selfpending_id"), 1, "薪资审批", this.userView);
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 如果排序指标是否有效
	 * @param sort_str
	 * @param gzbo
	 * @return
	 */
	private boolean isExistErrorItem(String sort_str,SalaryTemplateBo gzbo)
	{
		boolean flag=true;
		String[] temps=sort_str.toUpperCase().split(",");
		String zgItemStr=gzbo.getStandardGzItemStr();
		for(int i=0;i<temps.length;i++)
		{
			if(temps[i].length()>0)
			{
				String _str=temps[i].replaceAll("ASC", "");
				_str=_str.replaceAll("DESC", "");
				_str=_str.trim();
				if(DataDictionary.getFieldItem(_str.toLowerCase())!=null&&zgItemStr.indexOf(_str+"/")==-1)
				{
					flag=false;
					break;
				}
			}
			
			
		}
		return flag;
	}
	
	
	
	
	/**
	 * 判断是否有审批标识字段，没有则追加
	 * @param list
	 */
	private void isSpField(ArrayList list)
	{
		try
		{
			boolean isFlag=false;
			for(int i=0;i<list.size();i++)
			{
				Field field=(Field)list.get(i);
				String fieldname=field.getName();
				if("sp_flag".equalsIgnoreCase(fieldname))
				{
					isFlag=true;
				}
			}//for i loop end.
			if(!isFlag)
			{
				Field field=new Field("sp_flag",ResourceFactory.getProperty("label.gz.sp"));
                field.setLength(50);
                field.setCodesetid("23");
                field.setDatatype(DataType.STRING);
                field.setReadonly(true);
                field.setVisible(false);
                list.add(field);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private int getIsRedo(String ff_bosdate,String ff_count,String username,String salaryid)
	{
		int isRedo=0;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String[] temps=ff_bosdate.split("-");
			String sql="select * from gz_extend_log where salaryid="+salaryid+" and lower(username)='"+username.toLowerCase()+"' and "+Sql_switcher.year("a00z2")+"="+temps[0]+" and "+Sql_switcher.month("a00z2")+"="+temps[1]+" and a00z3="+ff_count;
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				if(this.frowset.getInt("isredo")==1)
					isRedo=1;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isRedo;
	}

}
