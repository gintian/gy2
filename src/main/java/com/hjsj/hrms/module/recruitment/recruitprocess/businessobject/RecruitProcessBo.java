package com.hjsj.hrms.module.recruitment.recruitprocess.businessobject;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitFlowLink;
import com.hjsj.hrms.module.recruitment.recruitflow.businessobject.RecruitflowBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecruitProcessBo {
	private Connection conn=null;
    private UserView userview;
    
    public RecruitProcessBo(Connection conn, UserView userview)
    {
    	 this.conn=conn;
    	 this.userview=userview;
    }
    
    
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
          return true;
        }
        return false;
      }
    
	/**
	 * 获取流程阶段信息
	 * @param Z0301 职位id
	 * @return
	 * 根据传过来的职位id进行查询当前职位下所有流程状态
	 */
	public ArrayList getStageInfo(String Z0301,String Z0381,int flag){
		RecruitflowBo bo =new RecruitflowBo(conn, userview);
		ArrayList list = new ArrayList();
		try {
			list=(ArrayList)bo.getLinkList(Z0381, Z0301, flag);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获得招聘流程下一阶段id
	 * @param link_id   环节id(阶段id)
	 * @param stateList 招聘流程包含的阶段
	 * @return
	 */
	public String getNextLinkId(String link_id,ArrayList stageList)
	{
		String next_linkId="";
		LazyDynaBean abean=null;
		boolean isMatching=false;
		for(Iterator t=stageList.iterator();t.hasNext();)
		{
			abean=(LazyDynaBean)t.next();
			String a_link_id=(String)abean.get("link_id");
			if(isMatching)
			{
				next_linkId=a_link_id;
				break;
			}
			if(a_link_id!=null&&a_link_id.equalsIgnoreCase(link_id))
			{
				isMatching=true;
			}
		}
		return next_linkId;
	}
	
	/**
	 * 获得招聘流程下一阶段id
	 * @param link_id   环节id(阶段id)
	 * @param stateList 招聘流程包含的阶段
	 * @return
	 */
	public String getNextNodeId(String link_id,ArrayList stageList)
	{
		String next_nodeId="";
		LazyDynaBean abean=null;
		boolean isMatching=false;
		for(Iterator t=stageList.iterator();t.hasNext();)
		{
			abean=(LazyDynaBean)t.next();
			String a_node_id=(String)abean.get("node_id");
			String a_link_id=(String)abean.get("link_id");
			if(isMatching)
			{
				next_nodeId=a_node_id;
				break;
			}
			if(a_link_id!=null&&a_link_id.equalsIgnoreCase(link_id))
			{
				isMatching=true;
			}
		}
		return next_nodeId;
	}
	
	
	
	/**
	 * 拼接查询列表sql
	 * @param columnList 前台显示列
	 * @param link_id 招聘环节id
	 * @param nbase 人员库前缀
	 * @return
	 */
	public String getListSql(ArrayList columnList,String link_id,String z0301){
		
		RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
		String nbase="";  //应聘人员库
		if(vo!=null)
			nbase=vo.getString("str_value"); 
		StringBuffer listSql = new StringBuffer("select ");
		listSql.append(nbase+"A01.guidkey,");
		HashSet fieldset=new HashSet();
		boolean isArrangeStage=false; //是否是面试环节，有是否通知标识
		for(int i=0;i<columnList.size();i++)
		{
	        String infoId = "";
	        Object o = columnList.get(i);
        	if(o instanceof HashMap)
        	{
        		HashMap hm = (HashMap) columnList.get(i);
        		ArrayList list = (ArrayList)hm.get("items");
        		for(int j=0;j<list.size();j++)
        		{
        			ColumnsInfo info = (ColumnsInfo) list.get(j);
        			infoId = info.getColumnId();
        			isArrangeStage = getSql(nbase, listSql, fieldset, isArrangeStage, infoId);
        		}
        	}else{
        		ColumnsInfo info = (ColumnsInfo) columnList.get(i);
        		infoId = info.getColumnId();
        		if("description".equalsIgnoreCase(infoId))
        		    continue;
        		
        		isArrangeStage = getSql(nbase, listSql, fieldset, isArrangeStage, infoId);
        	}
		}
		
		listSql.append("case  when zp_pos_tache.description is null or zp_pos_tache.description like '' then '0' else '1' end description");
		//循环输出字段列，最后一个时将“,”号去掉
//		listSql.setLength(listSql.length()-1);
		listSql.append(" from zp_pos_tache left join "+nbase+"a01 on "+nbase+"a01.a0100=zp_pos_tache.a0100 ");
		for(Iterator t=fieldset.iterator();t.hasNext();)
		{
			String temp=nbase+(String)t.next();
			listSql.append(" left join (select a.* from "+temp+" a where a.i9999=(select max(b.i9999) from "+temp+" b where a.a0100=b.a0100  ) ) "+temp);
			listSql.append(" on zp_pos_tache.a0100="+temp+".a0100 ");
		} 
		//面试阶段需显示是否通知信息
		if(isArrangeStage)
		{
			listSql.append(" left join ( select '1' as arrange_notice,a0100,nbase from z05 where (Z0527='1' or  Z0529='1' or  Z0531='1') and link_id='"+link_id+"'  ) interview ");
			listSql.append(" on  zp_pos_tache.a0100=interview.a0100 and zp_pos_tache.nbase=interview.nbase ");
		}
		
		listSql.append("  left join (select count(a0100_object) num,a0100_object,sum(score)/count(a0100_object) score from zp_evaluation where score!=0 group by a0100_object) e1 on "+nbase+"a01.a0100 = e1.a0100_object");
		listSql.append(" left join zp_flow_status on zp_flow_status.link_id = zp_pos_tache.link_id");
		listSql.append(" where ");
		listSql.append(" zp_pos_tache.zp_pos_id='"+z0301+"'");
		listSql.append(" and zp_pos_tache.status='1' ");
		listSql.append(" and zp_pos_tache.resume_flag = zp_flow_status.status ");
		return listSql.toString();
	}
	private boolean getSql(String nbase, StringBuffer listSql, HashSet fieldset, boolean isArrangeStage,
			String infoId) {
		FieldItem item=DataDictionary.getFieldItem(infoId.toLowerCase());
		if(item!=null&&!"A01".equalsIgnoreCase(item.getFieldsetid())&& "A".equalsIgnoreCase(item.getFieldsetid().substring(0,1)))
			fieldset.add(item.getFieldsetid());
		if("arrange_notice".equalsIgnoreCase(infoId)) //有面试通知标识
		{
			isArrangeStage=true;
			listSql.append(Sql_switcher.isnull(infoId,"'2'")+" as arrange_notice,");
		}
		else if("nbase".equalsIgnoreCase(infoId))
		{
			listSql.append("zp_pos_tache."+infoId+",");
		}
		else if("a0100".equalsIgnoreCase(infoId))
		{
			listSql.append(nbase+"A01."+infoId+",");
		}else if("score".equalsIgnoreCase(infoId)|| "num".equalsIgnoreCase(infoId))//新增分数显示
		{
			listSql.append("case  when e1."+infoId+" is null then 0 else e1."+infoId+" end "+infoId+",");
		}else if("resume_flag1".equalsIgnoreCase(infoId))
		{
			listSql.append("custom_name "+infoId+",");
		}else if("suitable".equalsIgnoreCase(infoId))
		{
			listSql.append("case  when zp_pos_tache."+infoId+"='1' then '符合' else '不符合' end "+infoId+",");
		}else if("rank_num".equalsIgnoreCase(infoId))
			listSql.append("case  when "+infoId+" is null or "+infoId+" = 0 then 999999 else "+infoId+" end "+infoId+",");
		else
			listSql.append(infoId+",");
		return isArrangeStage;
	}
	/**
	 * 获取邮件地址指标
	* @Title:getEmailItemId
	* @Description：
	* @author xiexd
	* @return
	 */
	public String getEmailItemId()
	{
		String emailId = "";
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("select Str_Value from constant where constant='SS_EMAIL'");
			RowSet rs = dao.search(sql.toString());
			if(rs.next())
			{
				emailId = rs.getString("Str_Value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailId;
	}
	/**
	 * 获取查询显示列
	 * @param link_id  面试环节
	 * @return
	 */
	public ArrayList getColumnList(String link_id)
	{
		ArrayList list = new ArrayList(); 
		ArrayList columnList = new ArrayList();
		ArrayList<CommonData> operationData = getNodeList(link_id);
		try
		{
			TableFactoryBO tableBo = new TableFactoryBO("zp_recruit_00001", this.userview, conn);
			HashMap scheme = tableBo.getTableLayoutConfig();
			if(scheme!=null)
	        {
	        	Integer scheme_str = (Integer)scheme.get("schemeId");
	        	int schemeId = scheme_str.intValue();;
	        	ArrayList columnConfigLst = tableBo.searchCombineColumnsConfigs(schemeId, null);
	        	list = columnConfigLst;
	        	list.add("a0100");
	        	list.add("nbase");
	        }else{
	        	list.add("a0100");//人员编号
	        	list.add("resume_flag1");//状态
	        	list.add("nbase");//人员库
	        	list.add("a0101");//人员姓名
	        	list.add("thenumber");//志愿号
	        	list.add("a0107");//性别
	        	list.add("a0122");//年龄
	        	list.add("a0405");//最高学历
	        	list.add("a0410");//专业
	        	list.add("a0435");//学校
	        	list.add("a1915");//所在单位
	        	list.add("score");//总分  
	        	list.add("c0104");//电话
	        	list.add(this.getEmailItemId());//邮箱
	        }
			String mergedesc = "";
        	int mergedescIndex = 0;
            int num = 0;
			for(int i=0;i<list.size();i++)
			{
				boolean infoFlag = true;
				FieldItem item= null;
				String itemId = "";
				ColumnsInfo info = new ColumnsInfo();
	        	if(scheme!=null)
	            {
	        		if(!"a0100".equals(list.get(i))&&!"nbase".equals(list.get(i)))
		        	{
	        			//当前用户有自定义栏目设置时
	        			ColumnConfig column = (ColumnConfig)list.get(i);
	        			itemId = column.getItemid();
	        			item = DataDictionary.getFieldItem(itemId);
	        			if(item!=null)
	        			{
	        				info = new ColumnsInfo(item);
	        				info.setColumnDesc(column.getDisplaydesc());
	        				if("1".equals(column.getIs_lock()))
	        				{
	        					info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
	        				}else if("0".equals(column.getIs_lock()))
	        				{
	        					info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
	        				}
		    				info.setLoadtype(Integer.parseInt(("".equals((String)column.getIs_lock())||(String)column.getIs_lock()==null)?"0":(String)column.getIs_lock()));
	        				info.setColumnWidth(column.getDisplaywidth());
	        				info.setTextAlign(column.getAlign()+"");
	        				String order = "";
		    				if("1".equalsIgnoreCase(column.getIs_order()))
		    				{
		    					order = "true";
		    				}else{
		    					order = "false";
		    				}
		    				info.setSortable(Boolean.parseBoolean(order));
	        				if("1".equals(column.getIs_sum()))
	        				{
	        					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
	        				}else if("2".equals(column.getIs_sum())){
	        					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_AVERAGE);
	        				}else if("3".equals(column.getIs_sum())){
	        					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MIN);
	        				}else if("4".equals(column.getIs_sum())){
	        					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MAX);
	        				}
	        				if("0".equalsIgnoreCase(column.getIs_fromdict()))
		    				{	    					
		    					info.setFromDict(Boolean.parseBoolean("false"));
		    				}
	        				if(column.getMergedesc()!=null&&column.getMergedesc().length()>0){
	        					if(mergedesc.equalsIgnoreCase(column.getMergedesc())&&mergedescIndex==i-1)
	        					{
	        						infoFlag = false;
	        					}else{	        						
	        						mergedesc = column.getMergedesc();
	        						mergedescIndex = i;
	        					}
	        				}
	        			}else{
	        				if(column.getMergedesc()!=null && column.getMergedesc().length()>0){
	        					if(mergedesc.equalsIgnoreCase(column.getMergedesc())&&mergedescIndex==i-1)
	        					{
	        						infoFlag = false;
	        					}else{	        						
	        						mergedesc = column.getMergedesc();
	        						mergedescIndex = i;
	        					}
	        				}
	        			}
		        	}else{
		        		itemId = (String) list.get(i);
		            	item = DataDictionary.getFieldItem(itemId);	
		            	info = new ColumnsInfo(item);
		        	}
	            }else{
	            	itemId = (String) list.get(i);
	            	item = DataDictionary.getFieldItem(itemId);
	            }
	        	if("resume_flag1".equalsIgnoreCase(itemId))
				{
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("resume_flag1");
					item.setCodesetid("36");
//					item.setCodesetid("0");
					item.setItemdesc("状态");
					info = new ColumnsInfo(item);
					info.setColumnWidth(90);
					info.setFromDict(false);
					info.setLocked(true);
					info.setOperationData(operationData);
					info.setRendererFunc("Global.feedBackShow");
				}else if("thenumber".equalsIgnoreCase(itemId))
				{
					item = new FieldItem();
					item.setItemtype("N");
					item.setItemid("thenumber");
					item.setItemdesc("志愿号");
					info = new ColumnsInfo(item);
					info.setColumnWidth(60);
					info.setTextAlign("right");
				}else if("score".equalsIgnoreCase(itemId))
				{
					item = new FieldItem();
					item.setItemtype("A");
					item.setItemid("score");
					item.setItemdesc("简历评价");
					info = new ColumnsInfo(item);
					info.setColumnWidth(90);
					info.setTextAlign("center");
					info.setFromDict(false);
					info.setRendererFunc("Global.evaluation");
        			//info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				}else{				
					item = DataDictionary.getFieldItem(itemId);
					if(item==null|| "".equals(item))
					{
						continue;
					}
					info = new ColumnsInfo(item);
					if("A".equals(info.getColumnType()))
    					info.setCodeSetValid(false);
					if("N".equalsIgnoreCase(item.getItemtype())|| "a0107".equalsIgnoreCase(itemId))
						info.setColumnWidth(50);
					else if("A".equalsIgnoreCase(item.getItemtype()))
						info.setColumnWidth(100);	
				}
				if("a0100".equals(itemId)|| "nbase".equals(itemId))
				{
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
					info.setEncrypted(true);
				}else if("a0101".equals(itemId))
				{
					info.setRendererFunc("Global.queryResume");
				}
				
				if(!"1".equals(item.getUseflag())&&!"resume_flag1".equals(itemId)&&!"thenumber".equals(itemId)&&!"score".equals(itemId)&&!"num".equals(itemId))
                {
                    continue;
                }
				info.setEditableValidFunc("Global.editable");
				if(infoFlag)
					columnList.add(info);
				else{
					ArrayList tableheadlist = new ArrayList( );
					tableheadlist.add(columnList.get(mergedescIndex-num));
					tableheadlist.add(info);
					HashMap topHead = new HashMap();
					topHead.put("text",mergedesc);
					topHead.put("items", tableheadlist);
					columnList.remove(mergedescIndex-num);//当合并时移除最后一列
					columnList.add(topHead);
					num+=1;
				}
			}
			
			//如果是面试环节，列表表需显示面试通知标识
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("zp_flow_links");
			vo.setString("id",link_id);
			vo=dao.findByPrimaryKey(vo); 
			String node_id=vo.getString("node_id");
			if(true)
			{
				ColumnsInfo info = new ColumnsInfo();
				info.setColumnType("A");
				info.setColumnId("resume_flag");
				info.setColumnDesc("状态");
				info.setCodesetId("36");
				info.setColumnWidth(90);
				info.setFromDict(false);
				info.setLocked(true);
				info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				info.setEditableValidFunc("Global.editable");
				columnList.add(info);
				
				ColumnsInfo desc = new ColumnsInfo();
				desc.setColumnType("A");
				desc.setColumnId("description");
				desc.setColumnDesc("反馈信息");
				desc.setCodesetId("0");
				desc.setColumnWidth(90);
				desc.setFromDict(false);
				desc.setLocked(true);
				desc.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				desc.setEditableValidFunc("Global.editable");
                columnList.add(desc);
			}
			if("04".equals(node_id))
			{
				ColumnsInfo info = new ColumnsInfo();
				info.setColumnId("arrange_notice");
				info.setColumnDesc("面试通知");
				info.setCodesetId("45");
				info.setColumnType("A");
				info.setColumnLength(1);
				info.setDecimalWidth(0);
				info.setReadOnly(true);
				info.setColumnWidth(100);
				info.setEditableValidFunc("Global.editable");
				columnList.add(info);
			}
				/***将评价人数在列中固定***/
				ColumnsInfo info2 = new ColumnsInfo();
				info2.setColumnId("num");
				info2.setColumnDesc("人数");
				info2.setColumnType("N");
				info2.setColumnLength(1);
				info2.setDecimalWidth(0);
				info2.setReadOnly(true);
				info2.setColumnWidth(50);
				info2.setFromDict(false);
				info2.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				info2.setEditableValidFunc("Global.editable");
				columnList.add(info2);
			   /***将简历筛选在列中固定***/
				ColumnsInfo info3 = new ColumnsInfo();
				info3.setColumnId("suitable");
				info3.setColumnDesc("简历筛选");
				info3.setColumnType("A");
				info3.setColumnLength(1);
				info3.setDecimalWidth(0);
				info3.setReadOnly(true);
				info3.setColumnWidth(100);
				info3.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
				info3.setEditableValidFunc("Global.editable");
				columnList.add(info3);
				
				ColumnsInfo info4 = new ColumnsInfo();
				info4.setColumnId("email_confirm");
				info4.setColumnDesc("是否参加");
				info4.setColumnType("A");
				info4.setCodesetId("45");
				info4.setColumnLength(1);
				info4.setDecimalWidth(0);
				info4.setColumnWidth(80);
				info4.setEditableValidFunc("Global.editable");
				columnList.add(info4);
				
				ColumnsInfo info5 = new ColumnsInfo();
				info5.setColumnId("rank_num");
				info5.setColumnDesc("推荐排名");
				info5.setColumnType("N");
				info5.setCodesetId("0");
				info5.setDecimalWidth(0);
				info5.setColumnWidth(80);
				info5.setRendererFunc("Global.showNum");
				
				columnList.add(info5);
				
		}
		catch (Exception e) {
            e.printStackTrace(); 
        }
		return columnList;
	}
	/**
	 * @param link_id
	 * @return
	 */
	private ArrayList<CommonData> getNodeList(String link_id) {
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet search = null;
		ArrayList<CommonData> operationData = new ArrayList<CommonData>();
		//状态自定义过滤类型
		try {
			String nodesql="select codeitemid ,custom_name from zp_flow_status left join codeitem on status=codeitemid and codesetid='36' where  link_id=? order by seq asc";
			ArrayList nodeList = new ArrayList();
			nodeList.add(link_id);
			search = dao.search(nodesql, nodeList);
			CommonData date = new CommonData();
			while(search.next()){
				date = new CommonData(search.getString("custom_name"),search.getString("custom_name"));
				operationData.add(date);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(search);
		}
		return operationData;
	}
	
	
	/**
	 * 获取查询方案列表
	 * @param stageId 当前流程阶段Id
	 * @return
	 */
	public ArrayList getProjectList(String link_id,String z0301,int flag)
	{
		RecruitFlowLink rfl = new RecruitFlowLink(link_id, z0301, conn);
		ArrayList projectList = new ArrayList();
		projectList = (ArrayList)rfl.getStatusList();
		return projectList;
	}
	/**
	 * 拼接条件查询语句
	 * @param stageId 招聘流程Id
	 * @param projectId 查询方案Id
	 * @return
	 */
	public String get_where(String link_id,String resume_flag,String queryStr)
	{
		StringBuffer sql_where = new StringBuffer();
		if(link_id!=null&&!"".equals(link_id))
		{
			//当stageId为0时，查询为所有阶段的人员
			if(!"0".equals(link_id))
			{
				//此处拼接招聘流程条件
				sql_where.append(" and zp_pos_tache.link_id='"+link_id+"'");
			}
		}
		if(resume_flag!=null&&!"".equals(resume_flag))
		{	
			if(resume_flag.length()>2){				
				//此处拼接查询方案条件
				sql_where.append(" and resume_flag='"+resume_flag+"'");
			}else{
				sql_where.append(" and resume_flag like '%"+resume_flag+"%'");
			}
		}
		
		RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
		String nbase="";  //应聘人员库
		if(vo!=null)
			nbase=vo.getString("str_value"); 
		
		//此处拼接招聘人员库条件
		sql_where.append(" and zp_pos_tache.nbase='"+nbase+"'");
		
		if(queryStr!=null&&!"".equalsIgnoreCase(queryStr))
		{			
			sql_where.append(" and ("+nbase+"a01.A0101 like '%"+queryStr+"%' ");
			FieldItem C0103 = DataDictionary.getFieldItem("C0103");
            if(C0103!=null&&!"".equals(C0103))
			{
            	sql_where.append(" or "+nbase+"a01.C0103 like '%"+queryStr+"%' ");
			}
            String emailField = ConstantParamter.getEmailField();
            FieldItem C0102 = DataDictionary.getFieldItem(emailField);
            if(C0102!=null&&!"".equals(C0102))
			{
            	sql_where.append(" or "+nbase+"a01."+emailField+" C0102 like '%"+queryStr+"%'  ");
			}
			FieldItem A0435 = DataDictionary.getFieldItem("A0435");
            if(A0435!=null&&!"".equals(A0435))
			{
            	sql_where.append(" or "+nbase+"a04.A0435 like '%"+queryStr+"%' ");
			}
            sql_where.append(" )");
		}
			
		return sql_where.toString();
	}
	/***
	 * 获取当前环节下的所有操作状态
	 * @param flow_id 流程序号
	 * @param Node_id 环节序号
	 * @return
	 */
	public ArrayList getOperationList(String z0301, String z0381,String link_id,String pageNum ,String searchStr,String pagesize)
	{
		RecruitFlowLink rfl = new RecruitFlowLink(this.userview, z0301, z0381, link_id, z0301, conn);
		ArrayList menuList = new ArrayList();
		RecruitUtilsBo ubo = new RecruitUtilsBo(conn);
		ArrayList operationList = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			operationList=rfl.getFunctionList();
			
			RecordVo z03Vo=new RecordVo("z03");
			z03Vo.setString("z0301",z0301);
			z03Vo=dao.findByPrimaryKey(z03Vo);
			if("06".equalsIgnoreCase(z03Vo.getString("z0319"))|| "09".equalsIgnoreCase(z03Vo.getString("z0319"))|| "01".equalsIgnoreCase(z03Vo.getString("z0319")))
			{
				//职位为结束或暂停时终止招聘流程中的一切功能 
				operationList.clear();
			}
			//当前操作人员不在
//			if(!this.getMembers(z0301))
//			{
//				operationList.clear();
//			}
			if(userview.isSuper_admin()||userview.hasTheFunction("311011701")){
				LazyDynaBean bean = RecruitUtilsBo.getMenuBean("导出Excel", "Global.exportWin()", "/images/export.gif", null, null ,new ArrayList());
				menuList.add(bean);
			}
			if(userview.isSuper_admin()||userview.hasTheFunction("311011704")){
				LazyDynaBean bean = RecruitUtilsBo.getMenuBean("导出简历PDF", "Global.exportResumePDF()", "/images/export.gif", null, null ,new ArrayList());
				menuList.add(bean);
			}
			if(userview.isSuper_admin()||userview.hasTheFunction("311011707")){
				LazyDynaBean bean = RecruitUtilsBo.getMenuBean("导出简历WORD", "Global.exportResumeWORD()", "/images/export.gif", null, null ,new ArrayList());
				menuList.add(bean);
			}
			if(userview.isSuper_admin()||userview.hasTheFunction("311011705")){
				LazyDynaBean bean = RecruitUtilsBo.getMenuBean("打印简历", "Global.printAX()", "/images/export.gif", null, null ,new ArrayList());
				bean.set("id", "printAXId");
				menuList.add(bean);
			}
			LazyDynaBean bean = new LazyDynaBean();
			if(userview.isSuper_admin()||userview.hasTheFunction("311011706")){
				bean.set("text", "导入推荐排名");
				bean.set("handler", "Global.importRank()");
				menuList.add(bean);
			}
			for(int i = 0;i<operationList.size();i++) {
				if(operationList.get(i) instanceof ButtonInfo) {
					ButtonInfo button = (ButtonInfo) operationList.get(i);
					String function = (String) button.getParameterMap().get("functions");
					bean = new LazyDynaBean();
					if("toTalents".equals(function)) {
						bean.set("text", button.getText());
						bean.set("handler", "Global.toTalentFunc()");
						menuList.add(bean);
						operationList.remove(i);
						i--;
					}else if("Global.recommendOtherPosition".equals(function)) {
						bean.set("text", button.getText());
						bean.set("handler", "Global.recommendOtherPos()");
						menuList.add(bean);
						operationList.remove(i);
						i--;
					}else if("invitationEvaluation".equals(function)) {
						bean.set("text", button.getText());
						bean.set("handler", "Global.invitationEvaluation()");
						menuList.add(bean);
						operationList.remove(i);
						i--;
					}
				}else {
					if(operationList.size()>0&&"-".equals(operationList.get(i-1))) {
						operationList.remove(i);
						i--;
					}
				}
			}
			String menuStr = RecruitUtilsBo.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"), "recruit_process_menu", menuList);
			operationList.add(0,menuStr);
			ButtonInfo queryBox = new ButtonInfo();
			queryBox.setFunctionId("ZP0000002300");
			queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
			queryBox.setText("请输入姓名、邮箱、学校...");
			operationList.add(queryBox);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return operationList;
	}
	
	 
	/**
	 * 员工入职操作
	 * 1：移库操作 2：终止已录用人员应聘的其它职位
	 * @param employeeList   【0】link_id  【1】resume_flag  【2】zp_pos_id  【3】a0100  【4】Nbase
	 */
	public void StaffEntry(ArrayList employeeList)throws GeneralException
	{
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String resume_state_field="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.conn,"1");
			HashMap map=bo2.getAttributeValues();
			if(map!=null&&map.get("resume_state")!=null)
				resume_state_field=(String)map.get("resume_state");
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");  
			StringBuffer a0010s=new StringBuffer("");
			for(Iterator t=employeeList.iterator();t.hasNext();)
			{
				ArrayList values=(ArrayList)t.next();
				a0010s.append(",'"+(String)values.get(3)+"'");
			}
			//将人员信息库的状态置为已录用
			if(resume_state_field!=null&&resume_state_field.length()>0)
			{
				FieldItem item=DataDictionary.getFieldItem(resume_state_field.toLowerCase());
				if(item!=null&&item.getItemlength()>=4)
					dao.update("update "+dbname+"A01 set "+resume_state_field+"='0903' where A0100 in ("+a0010s.substring(1)+")");
			}
			
			String destNbase = (String) map.get("destNbase");
			importDbase(dbname,destNbase,employeeList);  //移库
			stopOthPosState(employeeList);//终止已入职人员应聘的其它职位
	 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 已录用人员应聘的其它职位设置状态为终止
	 * @param employeeList   【0】link_id  【1】resume_flag  【2】zp_pos_id  【3】a0100  【4】Nbase
	 * @throws GeneralException
	 */
	private void stopOthPosState(ArrayList employeeList)throws GeneralException
	{
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=null;
			ArrayList valueList=new ArrayList();
			for(Iterator t=employeeList.iterator();t.hasNext();)
			{
				ArrayList values=(ArrayList)t.next();
				String nbase=(String)values.get(4);
				String a0100=(String)values.get(3);
				String zp_pos_id=(String)values.get(2);
				//应聘了其它职位非候选人时直接设置为拒绝
				String sql="update zp_pos_tache set status='2' where nbase=? and a0100=? and status='0'";
				dao.update(sql,Arrays.asList(new String[]{nbase,a0100}));
				rowSet=dao.search("select * from zp_pos_tache where nbase=? and a0100=?  and status='1' and zp_pos_id<>? ", Arrays.asList(new String[]{nbase,a0100,zp_pos_id}));
				while(rowSet.next())
				{
					
					String resume_flag=rowSet.getString("resume_flag")!=null?rowSet.getString("resume_flag").trim():"";
					if(resume_flag.length()==4)
					{
						ArrayList tempList=new ArrayList();
						String oth_zp_pos_id=rowSet.getString("zp_pos_id");
						tempList.add(getStopStatus(resume_flag));
						tempList.add(nbase);
						tempList.add(a0100);
						tempList.add(oth_zp_pos_id);
						valueList.add(tempList);
					}
				}
			}
			dao.batchUpdate("update zp_pos_tache set resume_flag=? where nbase=? and a0100=? and zp_pos_id=?",valueList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	/**
	 * 获得各阶段的终止状态
	 * @param status
	 * @return
	 */
	private String getStopStatus(String status)
	{
		String _status="";
		String _stage=status.substring(0,2);
		if("01".equalsIgnoreCase(_stage))
			_status="0106";
		else if("02".equalsIgnoreCase(_stage))
			_status="0206";
		else if("03".equalsIgnoreCase(_stage))
			_status="0308";
		else if("04".equalsIgnoreCase(_stage))
			_status="0408";
		else if("05".equalsIgnoreCase(_stage))
			_status="0508";
		else if("06".equalsIgnoreCase(_stage))
			_status="0604";
		else if("07".equalsIgnoreCase(_stage))
			_status="0704"; 
		else if("08".equalsIgnoreCase(_stage))
			_status="0906";
		else if("10".equalsIgnoreCase(_stage))
			_status="1005";
		return _status;
	}
	

	 /**
	  * 移库
	  * @param dbname
	  * @param toDbname
	  * @param infoSetList
	  * @param list  【0】link_id  【1】resume_flag  【2】zp_pos_id  【3】a0100  【4】Nbase
	  */
	private void importDbase(String dbname,String toDbname,ArrayList list) throws  GeneralException 
	{
		 InputStream in = null;
		  try
		  {
			  	List infoSetList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
			    StringBuffer strsql=new StringBuffer();
			    ContentDAO dao = new ContentDAO(this.conn);
				StringBuffer fieldstr=new StringBuffer();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				if(!list.isEmpty()){
					RowSet rowSet=null;
					for(int i=0;i<list.size();i++)
					{				
						ArrayList rec=(ArrayList)list.get(i);
						String nbase = (String)rec.get(4);
						String A0100=(String)rec.get(3); 
						String zp_pos_id=(String)rec.get(2);
						String toTable=toDbname+"A01";
						String toA0100 =DbNameBo.insertMainSetA0100(toTable,this.conn); 
						ResumeFileBo resumeFileBo = new ResumeFileBo(conn, userview);
						if(!infoSetList.isEmpty()){
								for(int j=0;j<infoSetList.size();j++)
								{
									FieldSet fieldset=(FieldSet)infoSetList.get(j);
									List fields=DataDictionary.getFieldList(fieldset.getFieldsetid(),Constant.EMPLOY_FIELD_SET);
									fieldstr.setLength(0);
									if(fields!=null&&!fields.isEmpty())
									{
									  for(int n=0;n<fields.size();n++)
									  {
									  	FieldItem fielditem=(FieldItem)fields.get(n);
									  	fieldstr.append("," + fielditem.getItemid());
									  }
									 }
								
									strsql=transferInformation(dbname+ fieldset.getFieldsetid(),toDbname + fieldset.getFieldsetid(),A0100,toA0100,fieldset.getFieldsetid(),fieldstr.toString());								
									dao.update(strsql.toString());
									strsql.setLength(0);
								
								}
								rowSet = dao.search("select b0110,e01a1,e0122 from "+nbase+"A01 where a0100='"+A0100+"'");
								if(rowSet.next())
								{
									String b0110 = rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
									String e0122 = rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
									String e01a1 = rowSet.getString("e01a1")!=null?rowSet.getString("e01a1"):"";
									dao.update("update "+toDbname+"A01 set b0110='"+b0110+"', e01a1='"+e01a1+"', e0122='"+e0122+"' where a0100='"+toA0100+"'");	
								}
								ArrayList<LazyDynaBean> resume = resumeFileBo .getFiles(dbname, A0100, "1");//简历
								FieldSet a01Set = DataDictionary.getFieldSetVo("A01");
								MultiMediaBo multimediabo=new MultiMediaBo(conn, userview);
								boolean isSavaAttachToMinSet = false;
								//有多媒体路径并且支持子集附件
					    		if(StringUtils.isNotEmpty(multimediabo.getRootDir()) && "1".equals(a01Set.getMultimedia_file_flag()))
									isSavaAttachToMinSet = true;
					    		String guidKey = this.getGuidKey(toTable, "", toA0100, false);
								for (int num = 0; num<resume.size(); num++) {
									LazyDynaBean obj = resume.get(num);
									String filePath = PubFunc.decrypt((String) obj.get("path"));
									File file = new File(filePath);
									if(isSavaAttachToMinSet){//支持附件
					                    multimediabo.setDbFlag("A");
					                    multimediabo.setNbase(toDbname);
					                    multimediabo.setSetId("A01");
					                    multimediabo.setA0100(toA0100);
					                    multimediabo.setI9999(num+1);
					                    HashMap hmap = new HashMap();
					    				hmap.put("mainguid", guidKey);//会在后面获取
					    				hmap.put("childguid", "");
					    				hmap.put("i9999", multimediabo.getI9999());
					    				hmap.put("dbflag", "A");
					    				hmap.put("srcfilename", (String) obj.get("fileName")+"."+(String) obj.get("fileType"));
					    				hmap.put("description", (String) obj.get("title"));
					    				hmap.put("filetitle", (String) obj.get("fileName"));
					    				hmap.put("ext", "."+(String) obj.get("fileType"));
					    				hmap.put("filetype", "F");
					    				multimediabo.saveMultimediaFile(hmap, file,true);
									}else{
										RecordVo tempvo=new RecordVo(toDbname+"A00");
										tempvo.setString("a0100", toA0100);
										tempvo.setInt("i9999", num+1);
										tempvo.setString("title", (String) obj.get("fileName"));
										tempvo.setString("flag", "N");
										tempvo.setString("ext", "."+(String) obj.get("fileType"));
										tempvo.setDate("createtime",sdf.parse((String) obj.get("createTime")));
										tempvo.setString("createusername", (String) obj.get("createUser"));
										in = new FileInputStream(file);
										switch (Sql_switcher.searchDbServer()) {
										case Constant.ORACEL:
											break;
										default:
											ByteArrayOutputStream out = new ByteArrayOutputStream();
											byte[] b = new byte[1024];  
											int len;  
											while ((len = in.read(b)) != -1) {  
												out.write(b, 0, len);  
											}
											tempvo.setObject("ole", out.toByteArray());
											break;
										}
										dao.addValueObject(tempvo);
										if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
											RecordVo updatevo = new RecordVo(toDbname + "A00");
											updatevo.setString("a0100", toA0100);
											updatevo.setInt("i9999", num+1);
											Blob blob = getOracleBlob(file, toDbname , toA0100, num+1);
											if (blob != null) {
												updatevo.setObject("ole", blob);
												dao.updateValueObject(updatevo);
											}
										}
										PubFunc.closeResource(in);
									}
				                    
								}
								
						}
					}	
				}
		      }catch(Exception e)
			  {
		      		e.printStackTrace();
		      		throw GeneralExceptionHandler.Handle(e);
		      }finally {
		    	  PubFunc.closeResource(in);
		      }
	}
	
	private Blob getOracleBlob(File file, String userbase, String userid, int recid) throws FileNotFoundException, IOException {
		InputStream in = null;
		Blob blob = null;
		try {
			StringBuffer strSearch=new StringBuffer();
			 strSearch.append("select ole from ");
	         strSearch.append(userbase);
	         strSearch.append("a00 where a0100='");
	         strSearch.append(userid);
	         strSearch.append("' and i9999=");
	         strSearch.append(recid);
	         strSearch.append(" FOR UPDATE");
			 
			StringBuffer strInsert=new StringBuffer();
			strInsert.append("update  ");
	        strInsert.append(userbase);
	        strInsert.append("a00 set ole=EMPTY_BLOB() where a0100='");
	        strInsert.append(userid);
	        strInsert.append("' and i9999=");
	        strInsert.append(recid);
		    OracleBlobUtils blobutils=new OracleBlobUtils(conn);
		    
	    	String fname = file.getName();
	         int indexInt = fname.lastIndexOf(".");
	         String ext = fname.substring(indexInt, fname.length());
		    in = new FileInputStream(file);
		    if (this.isImageFile(ext))
		    	in = ImageBO.imgStream(file, ext.substring(1)); // 复制图片，过滤掉木马程序
			blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),in);
	    }catch (Exception e) {
			e.printStackTrace();
		} finally {
	    	PubFunc.closeIoResource(in);
	    }
		return blob;
	}
	/**
     * 是否是图片文件（jbp,gif,bmp)
     * 
     * @Title: isImageFile
     * @Description:
     * @param fileExt
     *            文件扩展名
     * @return
     */
    private boolean isImageFile(String fileExt) {
        String ext = fileExt;
        if (ext.startsWith("."))
            ext = ext.substring(1);

        return "jpg".equalsIgnoreCase(fileExt.substring(1))
                || "jpeg".equalsIgnoreCase(fileExt.substring(1))
                || "gif".equalsIgnoreCase(fileExt.substring(1))
                || "bmp".equalsIgnoreCase(fileExt.substring(1))
                || "png".equalsIgnoreCase(fileExt.substring(1));
    }
	/**
	 * 生成信息集复制SQL语句
	 * @param fromTable 源表
	 * @param toTable  目标表
	 * @param fromNumber 招聘库中的A0100
	 * @param toNumber  目标库中的A0100
	 * @param setid 子集id
	 * @param fieldstr 子集指标 
	 */
	private StringBuffer transferInformation(String fromTable,String toTable,String fromNumber,String toNumber,String setid,String fieldstr) throws  GeneralException 
	{
		boolean flag = false;
		StringBuffer strsql =new  StringBuffer();
		try {
			if ("A01".equals(setid)) {
				 
				List fields=DataDictionary.getFieldList(setid,Constant.EMPLOY_FIELD_SET);
				String to_fieldstr="";
				String from_fieldstr="";
				if(fields!=null&&!fields.isEmpty())
				{
				  for(int n=0;n<fields.size();n++)
				  {
					    FieldItem fielditem=(FieldItem)fields.get(n);
					    String itemid=fielditem.getItemid(); 
					    switch (Sql_switcher.searchDbServer()) {
						case 1: // MSSQL 
							to_fieldstr+=","+toTable+"."+itemid+"="+fromTable+"."+itemid;
							break;
						case 2:// oracle
							to_fieldstr+=","+toTable+"."+itemid;
							from_fieldstr+=","+fromTable+"."+itemid;
							break;
					    }
				  }
				}
				
				String strA0000 = getA0000(toTable,conn);
				if(Sql_switcher.searchDbServer()==1) //MSSQL
				{
					to_fieldstr+=","+toTable+".A0000='"+strA0000+"',"+toTable+".A0100='"+toNumber+"',"+toTable+".State="+fromTable+".State,"+toTable+".CreateUserName="+fromTable+".CreateUserName";
					to_fieldstr+=","+toTable+".CreateTime="+fromTable+".CreateTime,"+toTable+".ModUserName="+fromTable+".ModUserName,"+toTable+".ModTime="+fromTable+".ModTime,"+toTable+".UserName="+fromTable+".UserName,"+toTable+".UserPassword="+fromTable+".UserPassword" ;
				
					strsql.append("update "+toTable+" set "+to_fieldstr.substring(1)+" from "+toTable+","+fromTable+" where "+toTable+".a0100='"+toNumber+"' and "+fromTable+".a0100='"+fromNumber+"'");
				
				}
				else if(Sql_switcher.searchDbServer()==2) // oracle
				{
					to_fieldstr+=","+toTable+".A0000,"+toTable+".A0100,"+toTable+".State,"+toTable+".CreateUserName,"+toTable+".CreateTime,"+toTable+".ModUserName,"+toTable+".ModTime,"+toTable+".UserName,"+toTable+".UserPassword";
					from_fieldstr+=",'"+strA0000+"','"+toNumber+"',"+fromTable+".State,"+fromTable+".CreateUserName,"+fromTable+".CreateTime,"+fromTable+".ModUserName,"+fromTable+".ModTime,"+fromTable+".UserName,"+fromTable+".UserPassword";
					
					strsql.append("update "+toTable+" set ("+to_fieldstr.substring(1)+")=(select "+from_fieldstr.substring(1)+"  from "+fromTable+" where  "+toTable+".a0100='"+toNumber+"' and "+fromTable+".a0100='"+fromNumber+"' )");
					strsql.append(" where "+toTable+".a0100='"+toNumber+"'");
				} 
			} else if ("A00".equals(setid)) {
				try{
				    new ExecuteSQL().execUpdate("delete from " + toTable + " where A0100='" + toNumber + "'",conn);
					}
				catch(Exception e)
				{
				}					
				strsql.append("insert into ");
				strsql.append(toTable);
				strsql.append("(A0100,I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName) select '");
				strsql.append(toNumber);
				strsql.append("',I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName from ");
				strsql.append(fromTable);
				strsql.append(" where A0100='" + fromNumber + "'");			
			} else {
				try{
				    new ExecuteSQL().execUpdate("delete from " + toTable + " where A0100='" + toNumber + "'",conn);
					}
				catch(Exception e)
				{
				}	
				strsql.append("insert into ");
				strsql.append(toTable);
				strsql.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
				strsql.append(fieldstr);
				strsql.append(") select '");
				strsql.append(toNumber);
				strsql.append("',I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
				strsql.append(fieldstr);
				strsql.append(" from ");
				strsql.append(fromTable);
				strsql.append(" where A0100='" + fromNumber + "'");
				
			}				
			flag = true;
		} catch (Exception e) { 
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return strsql;
	}
	 

	private String getA0000(String toTable,Connection conn) {
		String strsql = "select max(A0000) as a0000 from " + toTable;
		int userId=10;			
		try
		{
			List rs=ExecuteSQL.executeMyQuery(strsql,conn);
			if(!rs.isEmpty())
			{
				DynaBean rec=(DynaBean)rs.get(0); 
				
				if(rec.get("a0000")!=null&&!"".equals(rec.get("a0000")))
			    	userId=Integer.parseInt(rec.get("a0000").toString()) + 10;
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
		return String.valueOf(userId);		
	}
	/**
	 * 判断当前人员是否为招聘人员
	 * @param z0301岗位编号
	 * @return
	 */
	private boolean getMembers(String z0301)
	{
		boolean flag = false;
		try {
			if(userview.isSuper_admin())
			{
				return true;
			}
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer();
			sql.append("select * from zp_members where z0301=? and ((nbase=? and a0100=?) or (a0101=? or a0101=?))");// and member_type<>'4' 职位创建人能否对职位候选人进行操作？
			ArrayList list = new ArrayList();
			list.add(z0301);
			list.add(userview.getDbname());
			list.add(userview.getA0100());
			list.add(userview.getUserFullName());
			list.add(userview.getUserName());
			RowSet rs = dao.search(sql.toString(), list);
			if (rs.next()) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	/***
	 * 获取查询入职人员信息
	 * @param rzColumnList 显示列信息
	 * @param a0100List 人员集合
	 * @param z0301 职位id
	 * @param nbase 人员库信息
	 * @return
	 */
	public String getRzRegister(ArrayList a0100List,String z0301,String nbase)
	{
		StringBuffer jsonInfo = new StringBuffer("[");
		try {
			StringBuffer reRegister = new StringBuffer("select ");
			ArrayList value = new ArrayList();
			reRegister.append(" a01.A0100, a0101, z0321, z0325, '' as job");
			reRegister.append(",(select codeitemdesc from organization where codeitemid = (select z0321 from Z03 where Z0301=?)) UN ");
			reRegister.append(",(select codeitemdesc from organization where codeitemid = (select z0325 from Z03 where Z0301=?)) UM ");
			reRegister.append(" from ");
			reRegister.append(" zp_pos_tache zp,z03 z,"+nbase+"A01 a01 where ");
			reRegister.append(" zp.zp_pos_id = z.z0301 and zp.a0100=a01.a0100 ");
			reRegister.append(" and z.z0301 = ? ");
			value.add(z0301);
			value.add(z0301);
			value.add(z0301);
			reRegister.append(" and zp.a0100 in(");
			for(int i=0;i<a0100List.size();i++)
			{
				reRegister.append("?,");
				value.add(a0100List.get(i));
			}
			//去掉最后一个“，”号
			reRegister.setLength(reRegister.length()-1);
			reRegister.append(")");
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(reRegister.toString(), value);
			while(rs.next())
			{
				String a0100 = rs.getString("a0100");
				String a0101 = rs.getString("a0101");
				String z0321 = rs.getString("z0321");
				String z0325 = rs.getString("z0325")==null?"":rs.getString("z0325");
				String UN = rs.getString("UN");
				String UM = rs.getString("UM")==null?"":rs.getString("UM");
				jsonInfo.append("{'a0100':'"+a0100+"','a0101':'"+a0101+"','z0321':'"+z0321+"`"+UN+"','z0325':'"+z0325+"`"+UM+"','job':'`'},");
			}
			jsonInfo.setLength(jsonInfo.length()-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		jsonInfo.append("]");
		return jsonInfo.toString();
	}
	/***
	 * 入职登记列头
	 * @return
	 */
	public String getRzColumn()
	{
		StringBuffer rzColumn = new StringBuffer("[");
		try {
			//rzColumn.append("{'text':'编号','width':90,'align':'center','dataIndex':'a0100','editablevalidfunc':null,'renderer':null},");
			rzColumn.append("{'text':'姓名','width':90,'align':'center','dataIndex':'a0101','editablevalidfunc':null,'renderer':null},");
			rzColumn.append("{'xtype':'codecolumn','editor':{'maxLength':40,'afterCodeSelectFn':'getUNParentid',xtype:'codecomboxfield',ctrltype:'3',nmodule:'7',codesetid:'UN','allowBlank':true,'validator':null},'text':'单位','width':110,'align':'center','dataIndex':'z0321','editablevalidfunc':null,'renderer':null},");
			rzColumn.append("{'xtype':'codecolumn','editor':{afterCodeSelectFn:'Global.setValue','maxLength':40,'parentidFn':'getUMParentid',xtype:'codecomboxfield',ctrltype:'3',nmodule:'7',codesetid:'UM','allowBlank':true,'validator':null},'text':'部门','width':110,'align':'center','dataIndex':'z0325','editablevalidfunc':null,'renderer':null},");
			rzColumn.append("{'xtype':'codecolumn','editor':{afterCodeSelectFn:'Global.setValue','maxLength':40,'parentidFn':'getKParentid',xtype:'codecomboxfield',ctrltype:'3',nmodule:'7',codesetid:'@K','allowBlank':true,'validator':null},'text':'职位','width':120,'align':'center','dataIndex':'job','editablevalidfunc':null,'renderer':null}");
		} catch (Exception e) {
			e.printStackTrace();
		}
		rzColumn.append("]");
		return rzColumn.toString();
	}
	/**
	 * 改变人员库状态
	 * @param infoList
	 * @return
	 */
	public void updateA01(ArrayList infoList)
	{
		try {
			ContentDAO dao = new ContentDAO(conn);
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");  
			StringBuffer sql = new StringBuffer("update "+dbname+"A01 set b0110=?,e0122=?,e01a1=? where a0100=?");
			for(int i=0;i<infoList.size();i++)
			{
				ArrayList value = new ArrayList();
				MorphDynaBean infoBean = (MorphDynaBean)infoList.get(i);
				String z0321 = ("`".equalsIgnoreCase((String)infoBean.get("z0321"))?" ` ":(String)infoBean.get("z0321")).split("`")[0];
				String z0325 = ("`".equalsIgnoreCase((String)infoBean.get("z0325"))?" ` ":(String)infoBean.get("z0325")).split("`")[0];
				String job = ("`".equalsIgnoreCase((String)infoBean.get("job"))?" ` ":(String)infoBean.get("job")).split("`")[0];
				String a0100 = (String)infoBean.get("a0100");
				value.add(z0321.trim());
				value.add(z0325.trim());
				value.add(job.trim());
				value.add(a0100);
				dao.update(sql.toString(), value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取流程是否必须按顺序进行
	 * @param flow_id 流程id
	 * @return
	 */
	public String getSkipFlag(String flow_id){
		ContentDAO dao = new ContentDAO(conn);
		RowSet search = null;
		String sql = "select seq_flag from zp_flow_definition where flow_id='"+flow_id+"'";
		String skipflag = "1";
		try {
			search = dao.search(sql);
			if(search.next())
				skipflag = search.getString("seq_flag");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(search);
		}
		return skipflag;
	}
	
	/**
	 * 获得招聘流程上一阶段id
	 * @param link_id   环节id(阶段id)
	 * @param stateList 招聘流程包含的阶段
	 * @return
	 */
	public String getLastLinkId(String link_id,ArrayList stageList)
	{
		String Last_linkId="";
		LazyDynaBean abean=null;
		boolean isMatching=false;
		for (int i=0; i<stageList.size();i++) {
			abean=(LazyDynaBean)stageList.get(i);
			String a_link_id=(String)abean.get("link_id");
			if(a_link_id!=null&&a_link_id.equalsIgnoreCase(link_id))
			{
				isMatching=true;
			}
			if(isMatching)
			{
				if(i>=1){
					abean = (LazyDynaBean)stageList.get(i-1);
					Last_linkId=(String)abean.get("link_id");
				}
				break;
			}
		}
		return Last_linkId;
	}
	
	/**
	 * @param z0301
	 * @param a0100s
	 * @param rankNums
	 * 保存排名
	 */
	public void saveRankNum(String z0301, String a0100_es, String rank_nums){
		try {
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value"); 
			
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("update zp_pos_tache ");
			sql.append(" set rank_num=? ");
			sql.append(" where a0100=? and nbase=? and zp_pos_id=?");
			
			String[] a0100s = a0100_es.split(",");
			String[] rankNums = rank_nums.split(",");
			z0301 = PubFunc.decrypt(z0301);
			ArrayList values = new ArrayList();
			for (int i = 0; i<a0100s.length; i++) {
				String nums = rankNums[i];
				if(nums.length()>6)
					nums = nums.substring(0, 6);
				ArrayList row = new ArrayList();
				row.add(nums);
				row.add(PubFunc.decrypt(a0100s[i]));
				row.add(dbname);
				row.add(z0301);
				values.add(row);
			}
			dao.batchUpdate(sql.toString(), values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public boolean changeConfirmState(String confirm, String a0100, String dbname, String zp_pos_id, String link_id, String status) {
		boolean flag= true;
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer upSql = new StringBuffer("update zp_pos_tache set email_confirm=? ");
			StringBuffer searchSql = new StringBuffer("select email_confirm from zp_pos_tache ");
			StringBuffer sql = new StringBuffer(" where a0100=? and nbase=? and zp_pos_id=?");
			ArrayList values = new ArrayList();
			values.add(a0100);
			values.add(dbname);
			values.add(zp_pos_id);
			if(StringUtils.isNotEmpty(link_id)&&StringUtils.isNotEmpty(status)) {//邮件确认
				values.add(link_id);
				values.add(status);
				searchSql.append(sql);
				searchSql.append(" and link_id=? and resume_flag=?");
				rs = dao.search(searchSql.toString(),values);
				String email_confirm = "";
				if(rs.next()) {
					email_confirm = rs.getString("email_confirm");
					if(StringUtils.isNotEmpty(email_confirm)&&!"-1".equals(email_confirm))
						flag = false;
				}
				if(flag) {
					values.add(0,confirm);
					upSql.append(sql);
					upSql.append(" and link_id=? and resume_flag=?");
					dao.update(upSql.toString(), values);
				}
			}else {//转环节置为0
				values.add(0,confirm);
				upSql.append(sql);
				dao.update(upSql.toString(), values);
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
		
	}
	
	
	/**
     * 获取Excel 工作簿
     * @return 返回工作簿的名称
     */
    public String createExcel(String linkId, String z0301, String a0100json) throws Exception {
    	RowSet frowset=null;
    	ContentDAO dao = new ContentDAO(conn);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("推荐排名导入模版");
		HSSFPatriarch patr = sheet.createDrawingPatriarch();
		HSSFRow row = sheet.createRow((short) 0);
		HSSFCell cell = null;
		HSSFComment comm = null;
		String z0351 = null;
		
		HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 10);
        HSSFCellStyle style = dataStyle(wb);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("text")); // 文本格式
		
		
		RecordVo vo = ConstantParamter.getConstantVo("ZP_ONLY_FIELD");
		String onlyField= vo.getString("str_value");
		boolean isOnlyEmpty = false;
		if(StringUtils.isEmpty(onlyField)){ 
			onlyField = "A0100";
			isOnlyEmpty = true;
		}else
			onlyField = onlyField.substring(4);
		FieldItem item = DataDictionary.getFieldItem(onlyField);
        String itemdesc = item.getItemdesc();
        String[] a0100jsons= a0100json.split(",");
		
        vo = new RecordVo("z03");
        z0301 = PubFunc.decrypt(z0301);
        vo.setString("z0301", z0301);
        vo = dao.findByPrimaryKey(vo);
        if (vo.getString("z0351") != null ) {
        	z0351 = vo.getString("z0351");
        }
        
		int num=0;
		if(isOnlyEmpty)
			sheet.setColumnWidth(num, 0);
		else
		 	sheet.setColumnWidth(num, 5000);
		
		cell = row.createCell(num++);
		cell.setCellValue(itemdesc);
		comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (num + 1), 0, (short) (num + 2), 1));
		comm.setString(new HSSFRichTextString(onlyField));
        cell.setCellComment(comm);
        sheet.setColumnWidth(num, 5000);
		cell = row.createCell(num++);
		cell.setCellValue("姓名");
		comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (num + 1), 0, (short) (num + 2), 1));
		comm.setString(new HSSFRichTextString("A0101"));
        cell.setCellComment(comm);
        sheet.setColumnWidth(num, 5000);
		cell = row.createCell(num++);
		cell.setCellValue("应聘职位");
		comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (num + 1), 0, (short) (num + 2), 1));
		comm.setString(new HSSFRichTextString("z0351"));
        cell.setCellComment(comm);
        sheet.setColumnWidth(num, 5000);
		cell = row.createCell(num++);
		cell.setCellValue("排名");
		comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (num + 1), 0, (short) (num + 2), 1));
		comm.setString(new HSSFRichTextString("rank_num"));
        cell.setCellComment(comm);
        
        vo=ConstantParamter.getConstantVo("ZP_DBNAME");
		String dbname="";  //应聘人员库
		if(vo!=null)
			dbname=vo.getString("str_value"); 
		
        ArrayList list = new ArrayList();
        StringBuffer sql = new StringBuffer("select  A0100 , rank_num from zp_pos_tache WHERE ");
    	TableDataConfigCache tableCache = null;
        String scheme_id = "zp_recruit_00001";//职位候选人简历导出
        tableCache = (TableDataConfigCache) this.userview.getHm().get(scheme_id);
        String sortSql = (String) tableCache.get("sortSql");
    	sql = new StringBuffer((String) tableCache.get("combineSql")); 
    	sql.append(sortSql);
		frowset = dao.search(sql.toString());
        	
		ArrayList rankNums = new ArrayList();
		ArrayList a0100s = new ArrayList();
		ArrayList a0101s = new ArrayList();
		ArrayList onlyList = new ArrayList();
		while (frowset.next()) {
			String rankNum = frowset.getString("rank_num")==null?"":frowset.getString("rank_num");
			String a0100 = frowset.getString("A0100")==null?"":frowset.getString("A0100");
			if("".equalsIgnoreCase(a0100))
				continue;
			
			rankNums.add(rankNum);
			a0100s.add(a0100);
			
		}
           
		sql = new StringBuffer("select A0100, A0101,"+onlyField+" from "+dbname+"A01  WHERE A0100 IN ( ");
		String newjson = "";
		String json = "";
    	for (int a = 0; a < a0100s.size(); a++) {
            if(a !=0 && a % 1000 == 0){
            	 json =  newjson.substring(0,newjson.length()-2);
                 sql.append (json);
                 sql.append (")  or  A0100 IN ( ");
                 newjson = a0100s.get(a) + ", ";
            }else
            	newjson = newjson + a0100s.get(a) + ", ";
           
            if(a == a0100s.size()-1){
            	json =  newjson.substring(0,newjson.length()-2);
            	sql.append (json);
            }
         }
    	sql.append (")  ");
    	frowset = dao.search(sql.toString());
		HashMap map=new HashMap();
		HashMap onlyMap=new HashMap();
		while(frowset.next()){
			map.put(frowset.getString("A0100"),frowset.getString("A0101"));
			onlyMap.put(frowset.getString("A0100"),frowset.getString(onlyField));
		}
        
        int rowCount = 1;
        while (rowCount < 1001) {
            row = sheet.getRow(rowCount);
            if (row == null) {
                row = sheet.createRow(rowCount);
            }
            
            for (int i = 0; i < 4; i++) {

                cell = row.getCell(i);
                if (cell == null) 
                    cell = row.createCell(i);
                if ( i == 3) {
                    cell.setCellStyle(style);
                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    cell.setCellValue("");
                } else {
                    cell.setCellStyle(style);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                }
                
                if ( i == 0) {
                	
                }

            }
            rowCount++;
        }
        
        if(!"false".equalsIgnoreCase(a0100json)){
        	a0100s.clear();
        	for (int a = 0; a < a0100jsons.length; a++) {
                a0100jsons[a] = PubFunc.decrypt(a0100jsons[a]);
                if(StringUtils.isEmpty(a0100jsons[a]))
                	continue;
                
                a0100s.add(a0100jsons[a]);
             }
        }
        
        for (int i = 0; i<a0100s.size(); i++) {
        	  row = sheet.getRow(i+1);
    		  cell = row.getCell(0);
    		  cell.setCellValue((String)onlyMap.get(a0100s.get(i))); 
    		  cell = row.getCell(1);
    		  cell.setCellValue((String)map.get(a0100s.get(i))); 
    		  cell = row.getCell(2);
    		  cell.setCellValue(z0351); 
    		  cell = row.getCell(3);
    		  if("999999".equalsIgnoreCase((String) rankNums.get(i)))
    			  cell.setCellValue("");
    		  else
    			  cell.setCellValue((String) rankNums.get(i));
    		  
		}
        
	    String userName = this.userview.getUserName();
        if(isContainChinese(userName)){
        	userName =  PubFunc.getStrg();
        	userName = userName.substring(0,userName.length() - 3);
		}
        
        String outName =  userName + "_zp_rank.xls";
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
            wb.write(fileOut);
        } catch(Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(fileOut);
            PubFunc.closeResource(frowset);
            wb = null;
        }
        return outName;
    }
	
    public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBottomBorderColor((short) 8);
        style.setLeftBorderColor((short) 8);
        style.setRightBorderColor((short) 8);
        style.setTopBorderColor((short) 8);
        style.setWrapText(true);//设置自动换行
        return style;
    }
    
    /**
     * @param tablename
     * @param i9999
     * @param toA0100
     * @param bMain
     * @return
     */
    private String getGuidKey(String tablename,String i9999, String toA0100, boolean bMain)
    {
        String guid="";
        RowSet frowset=null;
        try{
        	ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer sb = new StringBuffer();
            StringBuffer sWhere  = new StringBuffer();
            
            sWhere.append(" where a0100 ='");
            sWhere.append(toA0100);
            sWhere.append("'");
            if(bMain) {
            	sWhere.append(" and i9999 =");
            	sWhere.append(i9999); 
            }
            
            sb.append("select GUIDKEY from ");
            sb.append(tablename);     
            sb.append(sWhere.toString());   
            
            frowset = dao.search(sb.toString());
            if (frowset.next()) {
                guid = frowset.getString("guidkey");
                if (StringUtils.isEmpty(guid)){
                    UUID uuid = UUID.randomUUID();
                    String tmpid = uuid.toString(); 
                    StringBuffer stmp = new StringBuffer();
                    stmp.append("update  ");
                    stmp.append(tablename);   
                    stmp.append(" set GUIDKEY ='");
                    stmp.append(tmpid.toUpperCase());
                    stmp.append("'");                    
                    stmp.append(sWhere.toString());
                    stmp.append(" and guidkey is null ");   
                    dao.update(stmp.toString());                
                    PubFunc.closeResource(frowset);
                    frowset = dao.search(sb.toString());
                    if (frowset.next()) {
                        guid = frowset.getString("guidkey");             
                    }
                }
            }
        } catch (Exception e ){
           e.printStackTrace();             
        } finally {
        	PubFunc.closeResource(frowset);
        }
        return guid;
     }
    
    @Deprecated
    public ArrayList <Object> importRanktion(String path, String filename, String z0301){
            //存放错误信息
            ArrayList < Object > msg = new ArrayList < Object > (); 
            ArrayList < Object > msgList = new ArrayList < Object > ();
            RowSet frowset=null;
            String z0351 = null;
            ContentDAO dao = new ContentDAO(conn);
            try {
				RecordVo vo = ConstantParamter.getConstantVo("ZP_ONLY_FIELD");
				String onlyField= vo.getString("str_value");
				if(StringUtils.isEmpty(onlyField)){ 
				//	msg.add("请设置简历唯一性指标");
				//    msgList.add(msg);
				//    msgList.add("false");
				//    return msgList;
					onlyField="A0100";
				}else
					onlyField = onlyField.substring(4);
				
				RecordVo z03Vo=new RecordVo("z03");
				z0301 = PubFunc.decrypt(z0301);
				z03Vo.setString("z0301",z0301);
				z03Vo=dao.findByPrimaryKey(z03Vo);
			    if (z03Vo.getString("z0351") != null ) {
		        	z0351 = z03Vo.getString("z0351");
		        }
				
				int onlyNumber = 0;
				int rankNumber = 0;
				int z0351Number = 0;
				Sheet sheet = this.getSheet(path, filename);
				Row headRow = sheet.getRow(0); // 获取表头
				if (headRow == null) {
				    msg.add("请用导出的Excel模板来导入数据");
				    msgList.add(msg);
				    msgList.add("false");
				    return msgList;
				}

				int headCols = headRow.getPhysicalNumberOfCells();
				int rows = sheet.getPhysicalNumberOfRows();
				if (headCols >= 2 && rows >= 1) {
				    // 用来判断是不是存在职位名称
				    boolean isOnlyField = false;
				    boolean isRankField = false;
				    boolean isZ0351Field = false;
	            	
				    Cell cell = null;
				    Comment comment = null;
				    // 拿到要添加的指标
				    for (int c = 0; c < headCols; c++) {
				        cell = headRow.getCell(c);
				        String field = "";
				        String title = "";

				        if (cell != null) {
				            comment = cell.getCellComment();
				            // 表头存在，批注为空
				            if (comment == null) 
				            	continue;

				            //拿到标注
				            field = comment.getString().toString().trim();
				            if ("rank_num".equalsIgnoreCase(field)) {
				            	isRankField = true;
				            	rankNumber = c;
				                continue;
				            }

				            
				            if ("Z0351".equalsIgnoreCase(field) ) {
				            	isZ0351Field = true;
				            	z0351Number = c;
				                continue;
				            }
				           
				            
				            
				            if (onlyField.equalsIgnoreCase(field)) {
				            	isOnlyField = true;
				            	onlyNumber = c;
				                continue;
				            }
				        }
				    }
				    
				    if (!isOnlyField || !isRankField) {
				        msg.add("请用导出的Excel模板来导入数据！");
				        msgList.add(msg);
				        msgList.add("false");
				        return msgList;
				    }

				}
      
				HSSFRow row = null;
				HSSFCell cell = null;
				Iterator it = sheet.iterator();
				ArrayList rankNums = new ArrayList();
				ArrayList onlyList = new ArrayList();
				ArrayList a0100s = new ArrayList();
				String onlyName =  sheet.getRow(0).getCell(onlyNumber).getStringCellValue();
				String rankName =  sheet.getRow(0).getCell(rankNumber).getStringCellValue();
				vo=ConstantParamter.getConstantVo("ZP_DBNAME");
				String dbname="";  //应聘人员库
				if(vo!=null)
					dbname=vo.getString("str_value"); 
				
				int numberOfRows = sheet.getPhysicalNumberOfRows();  
				while(it.hasNext()){
					row = (HSSFRow)it.next();
					String a0100 = null;
					String onlyValue = null;
					String rankNum = null;
					String z0351Name = null;
					if(row.getRowNum()==0)
						continue;//第一行不要
		                
	                boolean as =isRowEmpty(row);
	                if(isRowEmpty(row))
	                    continue;
					
				//	ArrayList data = new ArrayList();
					cell = row.getCell(onlyNumber);
					if(cell==null){
						int number = msg.size();
				        msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中的数据不能为空  ");
						continue;
					}else{
						 if(cell.getStringCellValue() != null) 
				         {
							 onlyValue = cell.getStringCellValue();
							 if(onlyList.contains(onlyValue)){
								 int number = msg.size();
				                 msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中有重复的数据  ");
								 continue;
							 }
							 
							 StringBuffer sql = new StringBuffer("select A0100  from "+dbname+"A01  WHERE ");
							 sql.append(onlyField);
							 sql.append(" = ?");
							 ArrayList list = new ArrayList();
							 list.add(onlyValue);
							 frowset = dao.search(sql.toString(),list);
							 int a0100number = 0;
							 while(frowset.next()){
								 a0100 = frowset.getString("A0100");
								 a0100number++;
							 }
							 if(a0100number > 1){
								 int number = msg.size();
				                 msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中的数据有多名对应候选人  ");
								 continue;
							 }else if(a0100number == 0){
								 int number = msg.size();
				                 msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中的数据没有对应候选人  ");
								 continue;
							 }
							 
							 
				         }else{
							 int number = msg.size();
				             msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中的数据不能为空  ");
							 continue;
				         }
						
					}
					
					cell = row.getCell(z0351Number);
					if(cell==null){
						int number = msg.size();
				        msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行应聘职位列中的数据不能为空  ");
						continue;
					}else{
						 if (StringUtils.isNotEmpty(cell.getStringCellValue())) 
				         {
							 z0351Name = cell.getStringCellValue();
							 if (StringUtils.isNotEmpty(z0351) && !z0351.equalsIgnoreCase(z0351Name) ) {
								 int number = msg.size();
							     msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行应聘职位与当前职位不符合 ");
								 continue;
							 }
				         }
					}
					
					
					cell = row.getCell(rankNumber);
					if(cell==null){
						int number = msg.size();
				        msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行排名列中的数据不能为空  ");
						continue;
					}else{
						 if (StringUtils.isNotEmpty(cell.getStringCellValue())) 
				         {
							 rankNum = cell.getStringCellValue();
							 boolean isNum = rankNum.matches("[0-9]+"); 
							 if(!isNum){
								 int number = msg.size();
					             msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行排名列中的数据不合法  ");
								 continue;
							 }
								
							 
				         }else{
							 int number = msg.size();
				             msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行排名列中的数据不能为空  ");
							 continue;
				         }
						
					}
					
					a0100s.add(a0100);
					rankNums.add(rankNum);
					onlyList.add(onlyValue);
				}
				
				if (msg.size() > 0) {               
				    userview.getHm().put("rankNums", rankNums);
				    userview.getHm().put("a0100s", a0100s);
				    msgList.add(msg);
				    msgList.add("true");
				    
				} else {
				    String message = this.importExcel(rankNums, a0100s, z0301);
				    msgList.add(msg);
				    msgList.add(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PubFunc.closeResource(frowset);
			}
            
		return msgList;
    }
    
    
    /**
     * 获取导入的sheet
     * @return 拿到要导入的Excel
     * @throws GeneralException
     */
    @Deprecated
    public Sheet getSheet(String path, String filename) throws GeneralException {
        File file = new File(path + "/" + filename);
        InputStream input = null;
        Workbook work = null;
        Sheet sheet = null;
        try {
            // 判断是否为文件
            if (!FileTypeUtil.isFileTypeEqual(file))
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));

            input = new FileInputStream(file);
            work = WorkbookFactory.create(input);
            sheet = (Sheet) work.getSheetAt(0);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(input);
            PubFunc.closeResource(work);
        }
        return sheet;
    }
    
    
    /**
     * 判断excel本行数据是否为空
     * 
     * @param file
     * @param dao 
     * @param hashMap
     * @return
     * @throws Exception
     */
    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
        Cell cell = row.getCell(c);
        String cellValue = cell.getStringCellValue();
        if (StringUtils.isNotEmpty(cellValue))
            return false;
        }
            return true;
    }
    

    /**
     * 获取Excel 工作簿
     * @return 返回工作簿的名称
     */
    public String importExcel(ArrayList rankNums,ArrayList a0100s,String z0301) throws Exception {
    	RowSet rset = null;
		try {
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value"); 
			
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer sql = new StringBuffer("update zp_pos_tache ");
			sql.append(" set rank_num=? ");
			sql.append(" where a0100=? and nbase=? and zp_pos_id=?");
			
	//		z0301 = PubFunc.decrypt(z0301);
			ArrayList values = new ArrayList();
			for (int i = 0; i<a0100s.size(); i++) {
				String nums =(String) rankNums.get(i);
				String a0101 = "";
				if(nums.length()>6) {
					String strsql="select a0101 from "+dbname+"A01 where a0100='"+a0100s.get(i)+"'";
		            rset=dao.search(strsql);
		            if (rset.next()){
		            	a0101 = rset.getString("a0101");
		            }
					
					return  a0101+"的推荐排名数据长度超过6位的限制，请重新调整后再导入" ;
				}
					
				ArrayList row = new ArrayList();
				row.add(nums);
				row.add(a0100s.get(i));
				row.add(dbname);
				row.add(z0301);
				values.add(row);
			}
			dao.batchUpdate(sql.toString(), values);
			return  "成功导入" + a0100s.size() + "条数据" ;
		} catch (SQLException e) {
			e.printStackTrace();
			 return "导入数据出错";
		} finally {
			PubFunc.closeDbObj(rset);
        }
		
    }
    
    public ArrayList <Object> importRanktion(String fileId, String z0301){
        //存放错误信息
        ArrayList < Object > msg = new ArrayList < Object > (); 
        ArrayList < Object > msgList = new ArrayList < Object > ();
        RowSet frowset=null;
        String z0351 = null;
        ContentDAO dao = new ContentDAO(conn);
        try {
			RecordVo vo = ConstantParamter.getConstantVo("ZP_ONLY_FIELD");
			String onlyField= vo.getString("str_value");
			if(StringUtils.isEmpty(onlyField)){ 
				onlyField="A0100";
			}else
				onlyField = onlyField.substring(4);
			
			RecordVo z03Vo=new RecordVo("z03");
			z0301 = PubFunc.decrypt(z0301);
			z03Vo.setString("z0301",z0301);
			z03Vo=dao.findByPrimaryKey(z03Vo);
		    if (z03Vo.getString("z0351") != null ) {
	        	z0351 = z03Vo.getString("z0351");
	        }
			
			int onlyNumber = 0;
			int rankNumber = 0;
			int z0351Number = 0;
			Sheet sheet = this.getSheet(fileId);
			Row headRow = sheet.getRow(0); // 获取表头
			if (headRow == null) {
			    msg.add("请用导出的Excel模板来导入数据");
			    msgList.add(msg);
			    msgList.add("false");
			    return msgList;
			}

			int headCols = headRow.getPhysicalNumberOfCells();
			int rows = sheet.getPhysicalNumberOfRows();
			if (headCols >= 2 && rows >= 1) {
			    // 用来判断是不是存在职位名称
			    boolean isOnlyField = false;
			    boolean isRankField = false;
			    boolean isZ0351Field = false;
            	
			    Cell cell = null;
			    Comment comment = null;
			    // 拿到要添加的指标
			    for (int c = 0; c < headCols; c++) {
			        cell = headRow.getCell(c);
			        String field = "";
			        String title = "";

			        if (cell != null) {
			            comment = cell.getCellComment();
			            // 表头存在，批注为空
			            if (comment == null) 
			            	continue;

			            //拿到标注
			            field = comment.getString().toString().trim();
			            if ("rank_num".equalsIgnoreCase(field)) {
			            	isRankField = true;
			            	rankNumber = c;
			                continue;
			            }

			            
			            if ("Z0351".equalsIgnoreCase(field) ) {
			            	isZ0351Field = true;
			            	z0351Number = c;
			                continue;
			            }
			            
			            if (onlyField.equalsIgnoreCase(field)) {
			            	isOnlyField = true;
			            	onlyNumber = c;
			                continue;
			            }
			        }
			    }
			    
			    if (!isOnlyField || !isRankField) {
			        msg.add("请用导出的Excel模板来导入数据！");
			        msgList.add(msg);
			        msgList.add("false");
			        return msgList;
			    }

			}
  
			HSSFRow row = null;
			HSSFCell cell = null;
			Iterator it = sheet.iterator();
			ArrayList rankNums = new ArrayList();
			ArrayList onlyList = new ArrayList();
			ArrayList a0100s = new ArrayList();
			String onlyName =  sheet.getRow(0).getCell(onlyNumber).getStringCellValue();
			String rankName =  sheet.getRow(0).getCell(rankNumber).getStringCellValue();
			vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value"); 
			
			int numberOfRows = sheet.getPhysicalNumberOfRows();  
			while(it.hasNext()){
				row = (HSSFRow)it.next();
				String a0100 = null;
				String onlyValue = null;
				String rankNum = null;
				String z0351Name = null;
				if(row.getRowNum()==0)
					continue;//第一行不要
	                
                boolean as =isRowEmpty(row);
                if(isRowEmpty(row))
                    continue;
				
			//	ArrayList data = new ArrayList();
				cell = row.getCell(onlyNumber);
				if(cell==null){
					int number = msg.size();
			        msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中的数据不能为空  ");
					continue;
				}else{
					 if(cell.getStringCellValue() != null) 
			         {
						 onlyValue = cell.getStringCellValue();
						 if(onlyList.contains(onlyValue)){
							 int number = msg.size();
			                 msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中有重复的数据  ");
							 continue;
						 }
						 
						 StringBuffer sql = new StringBuffer("select A0100  from "+dbname+"A01  WHERE ");
						 sql.append(onlyField);
						 sql.append(" = ?");
						 ArrayList list = new ArrayList();
						 list.add(onlyValue);
						 frowset = dao.search(sql.toString(),list);
						 int a0100number = 0;
						 while(frowset.next()){
							 a0100 = frowset.getString("A0100");
							 a0100number++;
						 }
						 if(a0100number > 1){
							 int number = msg.size();
			                 msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中的数据有多名对应候选人  ");
							 continue;
						 }else if(a0100number == 0){
							 int number = msg.size();
			                 msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中的数据没有对应候选人  ");
							 continue;
						 }
						 
			         }else{
						 int number = msg.size();
			             msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行" +onlyName+"列中的数据不能为空  ");
						 continue;
			         }
					
				}
				
				cell = row.getCell(z0351Number);
				if(cell==null){
					int number = msg.size();
			        msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行应聘职位列中的数据不能为空  ");
					continue;
				}else{
					 if (StringUtils.isNotEmpty(cell.getStringCellValue())) 
			         {
						 z0351Name = cell.getStringCellValue();
						 if (StringUtils.isNotEmpty(z0351) && !z0351.equalsIgnoreCase(z0351Name) ) {
							 int number = msg.size();
						     msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行应聘职位与当前职位不符合 ");
							 continue;
						 }
			         }
				}
				
				
				cell = row.getCell(rankNumber);
				if(cell==null){
					int number = msg.size();
			        msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行排名列中的数据不能为空  ");
					continue;
				}else{
					 if (StringUtils.isNotEmpty(cell.getStringCellValue())) 
			         {
						 rankNum = cell.getStringCellValue();
						 boolean isNum = rankNum.matches("[0-9]+"); 
						 if(!isNum){
							 int number = msg.size();
				             msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行排名列中的数据不合法  ");
							 continue;
						 }
						 
			         }else{
						 int number = msg.size();
			             msg.add((number + 1) + ". 第" + (row.getRowNum() + 1) + "行排名列中的数据不能为空  ");
						 continue;
			         }
					
				}
				
				a0100s.add(a0100);
				rankNums.add(rankNum);
				onlyList.add(onlyValue);
			}
			
			if (msg.size() > 0) {               
			    userview.getHm().put("rankNums", rankNums);
			    userview.getHm().put("a0100s", a0100s);
			    msgList.add(msg);
			    msgList.add("true");
			    
			} else {
			    String message = this.importExcel(rankNums, a0100s, z0301);
			    msgList.add(msg);
			    msgList.add(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(frowset);
		}
        
	return msgList;
}


	private Sheet getSheet(String fileId) {
		InputStream input = null;
        Workbook work = null;
        Sheet sheet = null;
        try {
            input = VfsService.getFile(fileId);
            work = WorkbookFactory.create(input);
            sheet = work.getSheetAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(input);
            PubFunc.closeResource(work);
        }
        return sheet;
	}
    
    
}
