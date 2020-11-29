package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.Permission;
import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchDataCollectTrans.java</p>
 * <p>Description:数据采集</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 13:00:00</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SearchDataCollectTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	    
	    
		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		String plan_id = (String) map.get("planId");
		String pointID = (String) map.get("pointID");
		map.remove("planId");
		map.remove("pointID");
		if (pointID == null)
		    pointID = "";
		this.getFormHM().put("planId", plan_id);
	
		String isFromTarget = "0";
		boolean isVisible = true;// 如果是从目标考核中调用要隐藏掉考核对象的字段
		if (map.get("b_query3") != null)
		{
		    isVisible = false;
		    isFromTarget = "1";
		    map.remove("b_query3");
		}
		this.getFormHM().put("isFromTarget", isFromTarget);
	
		DataCollectBo bo = new DataCollectBo(this.getFrameconn(), plan_id, this.userView);
		this.getFormHM().put("onlyname",bo.getOnlyname());
		
		RecordVo vo = bo.getPlanVo();
		String planMethod = vo.getString("method");
		String object_type = vo.getString("object_type");
		String targetTraceItem = "";
		String targetCollectItem = "";
		String isShowTargetTrace = "0";
		if ("2".equals(planMethod))
		{
		    // 取得目标跟踪显示和采集指标
		    // 1.取对应于考核计划的参数设置中定义的 目标跟踪显示和采集指标
		    LoadXml parameter_content = new LoadXml(this.getFrameconn(), plan_id);
		    Hashtable params = parameter_content.getDegreeWhole();
		    String targetTraceEnabled = (String) params.get("TargetTraceEnabled");
		    if ("true".equals(targetTraceEnabled))
		    {
				targetTraceItem = (String) params.get("TargetTraceItem");
				targetCollectItem = (String) params.get("TargetCollectItem");
		    } else
		    // 2.从绩效模块参数配置中取目标跟踪显示和采集指标
		    {
				ConfigParamBo configParamBo = new ConfigParamBo(this.getFrameconn());
				targetTraceItem = configParamBo.getTargetTraceItem();
				targetCollectItem = configParamBo.getTargetCollectItem();
				// targetCollectItem="P0419,P04A3,P04A1,P04A2";
		    }
		}
	
		if (targetCollectItem.length() == 0)
		{
			bo = new DataCollectBo(this.getFrameconn(), plan_id, pointID, this.userView);
			
			/******        情况分析两种下载模板样式  JinChunhai 2011.03.30        begin  ***/
			
			//1.判断是否设置了组织机构考核指标数据;
			Permission p = new Permission(this.frameconn,this.userView);
			HashMap hsMap = new HashMap();
			hsMap = p.getMap();
			String privPoint = "noHave";
			if(hsMap==null || hsMap.size()<=0)		
				privPoint = "noHave";
			else
				privPoint = "Have";
			
			//2.判断计划下的定量统一打分指标(业绩指标)是否超过15个以上; 
			ArrayList list = bo.getPointList();
			String pointNum = "noSuper";
			if(list.size()>=15)
				pointNum = "Super";
				
			//3.判断某指标对应需录入数据的考核对象数为计划下总对象数的50%以下的指标数是否超5个以上, 
			String number = bo.getPrivPointList();
			
			String determine = "false";
			if(("Have".equalsIgnoreCase(privPoint)) && ("Super".equalsIgnoreCase(pointNum)) && ("true".equalsIgnoreCase(number)))
				determine = "true";
			else
				determine = "false";
			
			this.getFormHM().put("determine", determine);
			
			/******                            end                                 ***/
			
		    // 生成表per_gather_xxx per_gather_score_xxx
		    bo.generateTable();
		    String status = bo.getPlanStatus();// 获得当前计划的状态,如果为评估或者结束则不许录入业绩数据，为只读状态
		    if (status != null && ("6".equals(status) || "7".equals(status)))
		    	this.getFormHM().put("isReadOnly", "1");
		    else
		    	this.getFormHM().put("isReadOnly", "0");
	
		    this.getFormHM().put("point", bo.getPoint());	 
		    
		    /******    判断定量统一打分指标是否有计算公式     ***/
//		    String pointFormula = "";
		    this.getFormHM().put("pointFormula", bo.getPointFormula(list));	
		    		    
		    this.getFormHM().put("khPoints", list);
		    bo.setColumnWidth(140);
		    String tableHtml = bo.getTableHtml();
		    this.getFormHM().put("tableHtml", tableHtml);
		    list = bo.getAllItems();
		    this.getFormHM().put("khItems", list);
		    list = bo.getKhObjs();
		    
		    //if(CurrentIndex <0 )CurrentIndex =0;
		    //if(CurrentIndex >PageCount )CurrentIndex = PageCount ;
		    this.getFormHM().put("khObjs", list);
		    String rule = bo.getRule();
		    this.getFormHM().put("rule", rule);
		    String pointype = bo.getTypeOfPoint();
		    this.getFormHM().put("pointype", pointype);
		    ArrayList setlist = bo.getDataList();
		    this.getFormHM().put("setlist", setlist);
		} else
		// 显示目标采集的界面
		{
		    isShowTargetTrace = "1";
		    ArrayList list = DataDictionary.getFieldList("P04", Constant.USED_FIELD_SET);
		    ArrayList fieldlist = new ArrayList();
		    StringBuffer sql = new StringBuffer();
		    sql.append("select ");
		    if ("1".equals(object_type))// 团队 考虑到不管是单位还是部门都存在b0110中
		    {
				// sql.append("(case when P04.e0122 is null ");
				// sql.append(" then (select codeitemdesc from organization
		                // where codeitemid=P04.b0110)");
				// sql.append(" else (select codeitemdesc from organization
		                // where codeitemid=P04.e0122)");
				// sql.append(" end) as khObj,");
		
				sql.append(" (select codeitemdesc from organization where codeitemid=P04.b0110) as khObj,");
				Field field = new Field("khObj");
				field.setLabel("考核对象");
				field.setVisible(isVisible);
				field.setReadonly(true);
				fieldlist.add(field);
		    }
	
		    for (int i = 0; i < list.size(); i++)
		    {
				FieldItem item = (FieldItem) list.get(i);
				Field field = (Field) item.cloneField();
				String itemid = item.getItemid();
				if (targetCollectItem.toLowerCase().indexOf(itemid.toLowerCase()) != -1 || "p0407".equalsIgnoreCase(itemid))
				    field.setVisible(true);
				else
				    field.setVisible(false);
				// 考虑到p04表中不管是单位还是部门都存在b0110字段中，所以对于考核对象不同的情况做如下处理
				if ("3".equals(object_type))// 单位
				{
				    if ("b0110".equalsIgnoreCase(itemid))
				    {
						field.setVisible(isVisible);
						field.setLabel("考核对象");
				    } else if ("e0122,e01a1,nbase,a0100,a0101".indexOf(itemid.toLowerCase()) != -1)
				    	continue;
				} else if ("4".equals(object_type))// 部门
				{
				    if ("b0110".equalsIgnoreCase(itemid))// 考虑到部门也存在b0110字段中
				    {
						field.setVisible(isVisible);
						field.setLabel("考核对象");
						field.setCodesetid("UM");
				    } else if ("e0122,e01a1,nbase,a0100,a0101".indexOf(itemid.toLowerCase()) != -1)
				    	continue;
				}
				if ("1".equals(object_type) && "b0110,e0122,e01a1,nbase,a0100,a0101".indexOf(itemid.toLowerCase()) != -1)// 团队
				    continue;
		
				// 权重  控制小数的显示位数 JinChunhai 2011.05.17
				if("p0415".equalsIgnoreCase(itemid))
				{					
					field.setFormat("##.##");										
				}				
				
				if ("2".equals(object_type) && "a0101".equalsIgnoreCase(itemid))// 人员
				    field.setLabel("考核对象");
				if ("2".equals(object_type) && "b0110".equalsIgnoreCase(itemid))// 单位
				    field.setLabel("单位名称");
				if ("2".equals(object_type) && "e0122".equalsIgnoreCase(itemid))// 部门
				{
					FieldItem fielditem = DataDictionary.getFieldItem("E0122");
				    field.setLabel(fielditem.getItemdesc());
				}
		
				if ("2".equals(object_type) && ("e0122".equalsIgnoreCase(itemid) || "b0110".equalsIgnoreCase(itemid) || "a0101".equalsIgnoreCase(itemid)))
				    field.setVisible(isVisible);
		
				if (targetTraceItem.toLowerCase().indexOf(itemid.toLowerCase()) != -1)
				    field.setReadonly(false);
				else
				    field.setReadonly(true);
		
				fieldlist.add(field);
				sql.append(itemid + ",");
		    }
		    sql.setLength(sql.length() - 1);
		    sql.append(" from P04 where plan_id=" + plan_id + " and (chg_type<>3 or chg_type is null)");
	
		    // 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
		    String operOrg = this.userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
		    String privCode = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();// 管理范围
		    
		    
		    if (this.userView.getUnitIdByBusi("5") != null
                    && this.userView.getUnitIdByBusi("5").length() > 0
                    && !"UN"
                            .equalsIgnoreCase(this.userView.getUnitIdByBusi("5")))// &&(this.plan_vo.getInt("status")!=4&&this.plan_vo.getInt("status")!=6&&this.perObject_vo.getString("sp_flag")!=null&&!this.perObject_vo.getString("sp_flag").equals("")&&!this.perObject_vo.getString("sp_flag").equals("01"))
            {
                String temp = this.userView.getUnitIdByBusi("5"); // 操作单位 5:
                                                                    // 绩效管理
                                                                    // 6：培训管理
                                                                   // 7：招聘管理
                String[] arr = temp.split("`");
                StringBuffer t_buf = new StringBuffer();
                for (int i = 0; i < arr.length; i++) {
                    if (arr[i] == null || "".equals(arr[i]))
                        continue;
                    t_buf.append(" or score_org like '"
                            + arr[i].substring(2) + "%'");
                }
                t_buf.append(" or score_org is null or score_org =''");
                sql.append(" and (" + t_buf.toString().substring(3) + ")");
            } else if("".equals(operOrg.trim()) && !"".equals(privCode.trim())){
                if ("UN".equalsIgnoreCase(privCode))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    sql.append(" and ( score_org is null or score_org ='') ");
                else
                    sql.append(" and ( score_org is null or score_org ='' or score_org like '"+privCode.substring(2)+"%' ) ");
            } else {
                sql.append(" and (UPPER(score_org)='"
                        + this.userView.getUserOrgId()
                        + "' or UPPER(score_org)='"
                        + this.userView.getUserDeptId() + "'");
                sql.append(" or score_org is null or score_org ='')");
            }
		    
		    /*if (operOrg.trim().equals("") && privCode.trim().equals(""))
			  sql.append(" and 1=2 ");
		    else if (operOrg.trim().equals("") && !privCode.trim().equals(""))// 按照管理范围走
		    {
				if (object_type.equals("2"))// 人员
				{
				    if (!this.userView.isSuper_admin())
				    {
						if (privCode != null && privCode.trim().length() > 0)
						{
						    String codesetid = privCode.substring(0, 2);
						    String value = privCode.substring(2);
						    if (value.length() > 0)
						    {
								if (codesetid.equalsIgnoreCase("UN"))
								{
								    sql.append(" and b0110 like '");
								    sql.append(value);
								    sql.append("%' ");
								} else if (codesetid.equalsIgnoreCase("UM"))
								{
								    sql.append(" and e0122 like '");
								    sql.append(value);
								    sql.append("%' ");
								} else if (codesetid.equalsIgnoreCase("@K"))
								{
								    sql.append(" and e01a1 like '");
								    sql.append(value);
								    sql.append("%' ");
								}
						    }
						}
				    }
				} else
				// 非人员
				{
				    if (!this.userView.isSuper_admin())
				    {
						if (privCode != null && privCode.trim().length() > 0)
						{
						    String codesetid = privCode.substring(0, 2);
						    String value = privCode.substring(2);
						    if (value.length() > 0)
						    {
							sql.append(" and b0110 like '");
							sql.append(value);
							sql.append("%' ");
						    }
						}
				    }
				}
		    } else if(!operOrg.trim().equals(""))
		    // 按照操作单位范围走
		    {
				if (object_type.equals("2"))// 人员
				{
				    if (operOrg.length() > 3)
				    {
						StringBuffer tempSql = new StringBuffer("");
						String[] temp = operOrg.split("`");
						for (int i = 0; i < temp.length; i++)
						{
						    if (temp[i].substring(0, 2).equalsIgnoreCase("UN"))
							tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
						    else if (temp[i].substring(0, 2).equalsIgnoreCase("UM"))
							tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
			
						}
						sql.append(" and ( " + tempSql.substring(3) + " ) ");
				    }
				} else
				// 非人员
				{
				    if (operOrg.length() > 3)
				    {
						StringBuffer tempSql = new StringBuffer("");
						String[] temp = operOrg.split("`");
						for (int i = 0; i < temp.length; i++)
						{
						    tempSql.append(" or b0110 like '" + temp[i].substring(2) + "%'");
						}
						sql.append(" and ( " + tempSql.substring(3) + " ) ");
				    }
				}
		    }
	*/
		    this.getFormHM().put("sql", sql.toString());
		    this.getFormHM().put("fieldlist", fieldlist);	
		}
	    this.getFormHM().put("object_type", object_type);
		this.getFormHM().put("isShowTargetTrace", isShowTargetTrace);
    }
}
