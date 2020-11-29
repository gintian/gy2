package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 
 * <p>Title:GetPlanTaskDataTrans.java</p>
 * <p>Description:获得计划任务表数据</p> 
 * <p>Company:hjsj</p> 
 * create time at:2014-7-11 上午09:58:03 
 * @author dengcan
 * @version 6.x
 */
public class GetPlanTaskDataTrans extends IBusiness {
	
	private static String SUBMODULEID_PLAN_DESIGN = "workPlan_position_0001"; // 栏目设置区分：计划制订
	// 排除指标：创建者姓名,创建时间,创建者用户名,项目ID,任务ID,期望完成时间,任务状态,审批状态,开始时间,结束时间,父任务号,任务修改标记,评价标准
	private static String exceptFields=",create_fullname,create_time,create_user,p0700,p0800,p0807,p0809,p0811,p0831,p0833,p0841,";

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		WorkPlanUtil wpUtil = new WorkPlanUtil(frameconn, userView);
		try {
			String p0727 = this.getFormHM().get("p0727") + "";
			String p0729 = this.getFormHM().get("p0729") + "";
			String p0731 = this.getFormHM().get("p0731") + "";
			String p0700=WorkPlanUtil.decryption(this.getFormHM().get("p0700")!=null?(String)this.getFormHM().get("p0700"):"");  //计划id
			String p0723=WorkPlanUtil.decryption(this.getFormHM().get("p0723")!=null?(String)this.getFormHM().get("p0723"):"");  //计划类型 1：人员计划  2：团队计划  3：项目
			String p0725=WorkPlanUtil.decryption(this.getFormHM().get("p0725")!=null?(String)this.getFormHM().get("p0725"):"");  //计划类型 1、年度 2:半年  3：季度  4：月份  5：周
			p0727=!"null".equals(p0727)?p0727:"0";   //计划年
			p0729=!"null".equals(p0729)?p0729:"0";  //计划月份、季度
		    p0731=!"null".equals(p0731)?p0731:"0";  //计划月份、季度
			String object_id=WorkPlanUtil.decryption(this.getFormHM().get("object_id")!=null?(String)this.getFormHM().get("object_id"):""); //对象id
			String showType=this.getFormHM().get("showType")!=null?(String)this.getFormHM().get("showType"):"1";  //任务列表视图类型  1：计划制定   2：计划跟踪
			
			String opt=this.getFormHM().get("opt")!=null?(String)this.getFormHM().get("opt"):"0";  //0得到tree其他数据，1得到tree数据
			
			String shwoSubTask = (String)this.getFormHM().get("exportSubTask");
			
			if(p0700.length()==0)
				p0700="0";
			if(p0725.length()==0)
				p0725="1";
			PlanTaskTreeTableBo planBo=new PlanTaskTreeTableBo(this.getFrameconn(),Integer.parseInt(p0700),this.getUserView());
			if( "0".equals(opt)){
				if ("0".equals(p0700)){
					try {
						planBo.setP0727(Integer.parseInt(p0727));
						planBo.setP0729(Integer.parseInt(p0729));
						planBo.setP0731(Integer.parseInt(p0731));
					} catch (Exception e) {
					}
				}
				HashMap dataMap=planBo.getTreePanelMap(Integer.parseInt(showType),
						Integer.parseInt(p0723),Integer.parseInt(p0725),object_id); //获得生成计划任务的树状表格采用EXT控件需要提供的数据
				
				
				
				this.getFormHM().put("dataModel",SafeCode.encode((String)dataMap.get("dataModel")));
				//this.getFormHM().put("dataJson",SafeCode.encode((String)dataMap.get("dataJson")));
				this.getFormHM().put("panelColumns",SafeCode.encode((String)dataMap.get("panelColumns")));
				int	role = wpUtil.getLoaderRole(Integer.parseInt(p0700));
				this.getFormHM().put("role", role+"");
				// 栏目设置功能
				String submoduleid = SUBMODULEID_PLAN_DESIGN; // 栏目设置区分
				if(planBo.hasPrivateScheme(submoduleid, this.userView.getUserName())){// 私有
					this.schemeSetting(submoduleid, "0");
					
					this.getFormHM().put("p0801ColumnWidth", planBo.p0801ColumnWidth);//任务名称链接的渲染在ie下特殊处理 chent 20171009
				}else if(planBo.hasShareScheme(submoduleid)){// 公有
					this.schemeSetting(submoduleid, "1");
					
					this.getFormHM().put("p0801ColumnWidth", planBo.p0801ColumnWidth);//任务名称链接的渲染在ie下特殊处理 chent 20171009
				}else{
					this.schemeSettingDefalt(showType);//默认方案
				}
			}else if("1".equals(opt)){
				String p0800 = (String) this.getFormHM().get("node");
				ArrayList data = new ArrayList();
				if("root".equals(p0800)){//第一次进入
					data = planBo.getTreePanelData("",Integer.parseInt(showType), Integer.parseInt(p0723),Integer.parseInt(p0725), object_id,shwoSubTask);
				}else{//不是根节点
					p0800 = WorkPlanUtil.decryption(p0800);
					data = planBo.getTreePanelData(p0800,Integer.parseInt(showType), Integer.parseInt(p0723),Integer.parseInt(p0725), object_id,shwoSubTask);
				}
				this.getFormHM().put("dataJson",data);
			}
		}  catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
	}
	/**
	 * 栏目设置,把公有方案放进恢复默认方案中
	 * @param submoduleid 任务列表视图类型 
	 * @param is_share 公有/私有  1/0
	 * 
	 * */
	@SuppressWarnings("unchecked")
	private void schemeSetting(String submoduleid, String is_share){

		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rset=null;
		try{
			ArrayList arr = new ArrayList();
			
			String strsql= "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id as id from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = '"+is_share+"' ";
			//haosl 20170410  私有方案增加username条件
			if("0".equals(is_share)){
            	
            	strsql += " and username='" + this.userView.getUserName() + "'";
            }
            strsql += " )  order by displayorder";
          //haosl 20170410 私有方案增加username条件
			rset=dao.search(strsql);
            
            LinkedHashMap map = new LinkedHashMap();
            while(rset.next()){
            	String columnId = rset.getString("itemid");
        		String desc = rset.getString("itemdesc");
        		if(StringUtils.isEmpty(desc)){
        			FieldItem item = DataDictionary.getFieldItem(columnId, "P08");
        			if(item != null){
        				desc = item.getItemdesc();
        			} else {
        				PlanTaskTreeTableBo planTaskTreeTableBo = new PlanTaskTreeTableBo(this.frameconn);
        				desc = planTaskTreeTableBo.getSchemeColumnDesc(columnId);
        			}
        		}
        		ColumnsInfo info = new ColumnsInfo();
        		info.setColumnId(columnId);
        		info.setColumnDesc(desc);
        		info.setLoadtype(Integer.parseInt(rset.getString("is_display")));
        		info.setColumnWidth(rset.getInt("displaywidth"));
        		boolean is_fromdict = false;
        		if("1".equals(rset.getString("is_fromdict"))){
        			is_fromdict = true;
        		}
        		info.setFromDict(is_fromdict);
        		info.setSortable(false);
        		map.put(columnId, info);
        		arr.add(info);
            	
            }
            
			TableDataConfigCache config = new TableDataConfigCache();
			config.setTableColumns(arr);
			config.setColumnMap(map);
			Integer pagesize = new Integer(20);
			config.setPageSize(pagesize);
			
			userView.getHm().put(submoduleid, config);
		}catch(Exception e){
        	e.printStackTrace();
        }
	}
	/**
	 * 栏目设置（默认）
	 * @param showType 任务列表视图类型  1：计划制定   2：计划跟踪
	 * 
	 * */
	private void schemeSettingDefalt(String showType){
		ArrayList fieldList = DataDictionary.getFieldList("P08",1);
		ArrayList arr = new ArrayList();
		LinkedHashMap map = new LinkedHashMap();
		for(int i=0;i<fieldList.size();i++){
			FieldItem fi = (FieldItem)fieldList.get(i);
			// 去除没有启用的指标
			if(!"1".equals(fi.getUseflag())){
				continue;
			}
			// 去除隐藏的指标
			if(!"1".equals(fi.getState())){
				continue;
			}
			// 去除不需要的指标
			if(exceptFields.indexOf(","+fi.getItemid().toLowerCase()+",") != -1){
				continue;
			}
			if("p0801".equalsIgnoreCase(fi.getItemid())){
				ColumnsInfo info = new ColumnsInfo(fi);
				if(isDefalt(showType,fi.getItemid())){
					info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				}else{
					info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
				}
				info.setColumnWidth(400);
				info.setSortable(false);
		// 计划跟踪页面删掉 chent 2015-08-27 start
				//if("2".equals(showType)){
				//	info.setLocked(true);
				//	info.setColumnWidth(300);
				//}
				map.put(fi.getItemid(), info);
				arr.add(info);
				break;
			}
			
		}
		/** 手动添加的项目 */
		// 负责人
		FieldItem fiAdd = new FieldItem();
		ColumnsInfo info = new ColumnsInfo();
		fiAdd.setItemid("principal");
		fiAdd.setItemdesc("负责人");
		fiAdd.setItemtype("A");
		info = new ColumnsInfo(fiAdd);
		info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
		info.setColumnWidth(200);
		info.setFromDict(false);
		//if("2".equals(showType))
		//	info.setColumnWidth(70);
		info.setSortable(false);
		map.put("principal", info);
		arr.add(info);
		
		// 标准rank
		fiAdd = new FieldItem();
		info = new ColumnsInfo();
		fiAdd.setItemid("rank");
		fiAdd.setItemdesc("权重");
		fiAdd.setItemtype("N");
		info = new ColumnsInfo(fiAdd);
		info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
		info.setColumnWidth(200);
		info.setFromDict(false);
		//if("2".equals(showType))
		//	info.setColumnWidth(70);
		info.setSortable(false);
		map.put("rank", info);
		arr.add(info);
		//if("2".equals(showType)){
		//	// 完成进度
		//	for(int i=0;i<fieldList.size();i++){
		//		FieldItem fi = (FieldItem)fieldList.get(i);
		//		// 去除没有启用的指标
		//		if(!"1".equals(fi.getUseflag())){
		//			continue;
		//		}
		//		// 去除不需要的指标
		//		if(exceptFields.indexOf(","+fi.getItemid().toLowerCase()+",") != -1){
		//			continue;
		//		}
		//		if("p0835".equalsIgnoreCase(fi.getItemid())){
		//			info = new ColumnsInfo(fi);
		//			if(isDefalt(showType,fi.getItemid())){
		//				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
		//			}else{
		//				info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
		//			}
		//			info.setColumnWidth(100);
		//			info.setSortable(false);
		//			info.setColumnType("A");
		//			info.setFromDict(false);
		//			arr.add(info);
		//			break;
		//		}
		//	}
		//	// 甘特图
		//	fiAdd = new FieldItem();
		//	fiAdd.setItemid("gantt");
		//	fiAdd.setItemdesc("任务起止时间-甘特图");
		//	info = new ColumnsInfo(fiAdd);
		//	info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
		//	info.setSortable(false);
		//	info.setFromDict(false);
		//	arr.add(info);
		//}
		// 时间安排
		fiAdd = new FieldItem();
		info = new ColumnsInfo();
		fiAdd.setItemid("timearrange");
		fiAdd.setItemdesc("时间安排");
		fiAdd.setItemtype("A");
		info = new ColumnsInfo(fiAdd);
		info.setFromDict(false);
		//if("1".equals(showType)){
		info.setColumnWidth(220);
		info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
		//}
		//else if("2".equals(showType)){
		//	info.setColumnWidth(200);
		//	info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
		//}
		info.setSortable(false);
		map.put("timearrange", info);
		arr.add(info);
		// 甘特图
		fiAdd = new FieldItem();
		fiAdd.setItemid("gantt");
		fiAdd.setItemdesc("任务起止时间-甘特图");
		fiAdd.setItemtype("A");
		info = new ColumnsInfo(fiAdd);
		info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
		info.setSortable(false);
		info.setFromDict(false);
		map.put("gantt", info);
		arr.add(info);
		// 任务成员
		fiAdd = new FieldItem();
		fiAdd.setItemid("participant");
		fiAdd.setItemdesc("任务成员");
		fiAdd.setItemtype("A");
		info = new ColumnsInfo(fiAdd);
		info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
		info.setSortable(false);
		info.setFromDict(false);
		map.put("participant", info);
		arr.add(info);
		for(int i=0;i<fieldList.size();i++){
			FieldItem fi = (FieldItem)fieldList.get(i);
			// 去除没有启用的指标
			if(!"1".equals(fi.getUseflag())){
				continue;
			}
			// 去除不需要的指标
			if(exceptFields.indexOf(","+fi.getItemid().toLowerCase()+",") != -1){
				continue;
			}
			// 去除隐藏的指标
			if(!"1".equals(fi.getState())){
				continue;
			}
			// p0801不需要，前面已经加入
			if("p0801".equalsIgnoreCase(fi.getItemid())){
				continue;
			}
			
			// 计划跟踪时，完成进度不需要，前面已经加入
			//if("2".equals(showType)){
			//	if("p0835".equalsIgnoreCase(fi.getItemid())){
			//		continue;
			//	}
			//}
			info = new ColumnsInfo(fi);
			//完成进度默认左对齐,解决bug16607
			if("p0835".equalsIgnoreCase(fi.getItemid())){
				info.setTextAlign("left");
			}
			if(isDefalt(showType,fi.getItemid())){
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
			}else{
				info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
			}
			info.setSortable(false);
			info.setColumnType("A");
			map.put(fi.getItemid(), info);
			arr.add(info);
		}
		
		TableDataConfigCache config = new TableDataConfigCache();
		config.setColumnMap(map);
		config.setTableColumns(arr);
		Integer pagesize = new Integer(20);
		config.setPageSize(pagesize);
		//if("1".equals(showType)){
		userView.getHm().put(SUBMODULEID_PLAN_DESIGN, config);
		//}
		//else if("2".equals(showType)){
		//	userView.getHm().put(SUBMODULEID_PLAN_TRACE, config);
		//}
		// 计划跟踪页面删掉 chent 2015-08-27 end
		
	}
	
	/**
	 * 计划制订栏目设置中把默认显示项勾选
	 * 
	 * */
	private boolean isDefalt(String showType,String itemid){
		boolean flag = false;
		// 计划跟踪页面删掉 chent 2015-08-27 start
		//if("1".equals(showType)){
		if(("p0801".equalsIgnoreCase(itemid))
			||("objectid".equalsIgnoreCase(itemid))
			||("rank".equalsIgnoreCase(itemid))
			||("timearrange".equalsIgnoreCase(itemid))){
			flag = true;
		}
		//}
		/*else if("2".equals(showType)){
			if(("p0800".equalsIgnoreCase(itemid))
				||("objectid".equalsIgnoreCase(itemid))
				||("p0723".equalsIgnoreCase(itemid))
				||("p0700".equalsIgnoreCase(itemid))
				||("p0833".equalsIgnoreCase(itemid))
				||("p0809".equalsIgnoreCase(itemid))
				||("seq".equalsIgnoreCase(itemid))
				||("p0801".equalsIgnoreCase(itemid))
				||("principal".equalsIgnoreCase(itemid))
				||("rank".equalsIgnoreCase(itemid))
				||("p0811".equalsIgnoreCase(itemid))
				||("p0835".equalsIgnoreCase(itemid))
				||("gantt".equalsIgnoreCase(itemid))
				||("taskprogresscolor".equalsIgnoreCase(itemid))){
				flag = true;
			}
		}*/
		// 计划跟踪页面删掉 chent 2015-08-27 end
		return flag;
	}
}
