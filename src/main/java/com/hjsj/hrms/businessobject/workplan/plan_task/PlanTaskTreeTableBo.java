package com.hjsj.hrms.businessobject.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * <p>Title:PlanTaskTreeTableBo.java</p>
 * <p>Description:生成计划任务列表</p> 
 * <p>Company:hjsj</p> 
 * create time at:2014-7-11 上午10:29:54 
 * @author dengcan
 * @version 6.x
 */
@SuppressWarnings("all")
public class PlanTaskTreeTableBo {
	private Connection conn = null;
	private UserView userView;
	private RecordVo p07_vo = null; 
    private int P0723; //查看的计划类型 1 人员 2 部门
    private int P0725;//期间类型
    private int P0727;//年
    private int P0729;//月 根据期间类型不同代码月份、季度、上半年
    private int P0731;//周
    private int showType;//
    public ArrayList sortItem = new ArrayList();
    private String nbase = "";
    private String a0100 = "";
	
	private final String lockedFieldStr="/seq/p0801/";   //默认锁定列
	private final String specialFieldStr="/principal/participant/timearrange/gantt/p0835/p0823/taskprogresscolor/p0811/"; ///p0835_state/p0835_jd/";   //特殊列获取数据时得单独考虑
	
	private static String SUBMODULEID_PLAN_DESIGN = "workPlan_position_0001"; // 栏目设置区分：计划制订
	private static String SUBMODULEID_PLAN_TRACE = "workPlan_position_0002"; // 栏目设置区分：计划跟踪
	
	public static String p0801ColumnWidth = "300";
	/**
	 * 
	 * @param _conn
	 * @param _p0700
	 */
	public PlanTaskTreeTableBo(Connection _conn,int _p0700)
	{
		this.conn=_conn;
		if(_p0700!=0) {
            this.p07_vo=getP07Vo(_p0700);
        }
	}
	
	
	public PlanTaskTreeTableBo(Connection _conn)
	{
		this.conn=_conn;
	 
	}
	
	public PlanTaskTreeTableBo(Connection _conn,int _p0700,UserView userView)
	{
		this.conn=_conn;
		if(_p0700!=0) {
            this.p07_vo=getP07Vo(_p0700);
        }
		this.userView=userView;
	 
	}
	
	
	public RecordVo getP07Vo(int p0700) {
		RecordVo vo = new RecordVo("p07");
		try {
			vo.setInt("p0700",p0700);
			ContentDAO dao = new ContentDAO(this.conn);
			vo = dao.findByPrimaryKey(vo);
			this.P0723=vo.getInt("p0723");
			this.P0725=vo.getInt("p0725");
			this.P0727=vo.getInt("p0727");
			this.P0729=vo.getInt("p0729");
			this.P0731=vo.getInt("p0731");
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
	
	
	/**
	 * 获得生成计划任务的树状表格采用EXT控件需要提供的数据
	 * @param showType 任务列表视图类型  1：计划制定   2：计划跟踪
	 * @author dengcan
	 * @serialData 2014-07-12
	 * @return dataModel dataJson
	 */

	public HashMap getTreePanelMap(int showType,int p0723,int p0725,String objectid)
	{
	    setP0723(p0723);
	    setP0725(p0725);
	    setShowType(showType);
		HashMap map=new HashMap();
		ArrayList headList=getHeadList(showType,p0723); //获得计划任务列表表头指标 （1阶段固定表头  后期可配置）
		map.put("dataModel", getDataModel(headList,p0725));
		//map.put("dataJson", getDataJson(headList));
		map.put("panelColumns",getPanelColumns(headList,showType,objectid,p0723));
		return map;
	}
	
	/**
	 * @author lis
	 * @Description: 获得生成计划任务的树状表格采用EXT控件需要提供的数据
	 * @date 2016-3-10
	 * @param p0800 任务id
	 * @param showType 任务列表视图类型  1：计划制定   2：计划跟踪
	 * @param p0723 项目类型；1：人员计划，2：团队计划
	 * @param p0725 周期；年、月......
	 * @param objectid 是人员计划时：人员库+人员编号；是团队计划时：是组织机构编号
	 * @return ArrayList
	 * @throws GeneralException 
	 */

	public ArrayList getTreePanelData(String p0800,int showType,int p0723,int p0725,String objectid,String shwoSubTask) throws GeneralException
	{
		try {
			setP0723(p0723);
			setP0725(p0725);
			setShowType(showType);
			ArrayList headList=getHeadList(showType,p0723); //获得计划任务列表表头指标 （1阶段固定表头  后期可配置）
			ArrayList dataList = getDataList(headList,p0800,shwoSubTask);
			
			// 基于栏目设置重新排序，因为数据是拼出来的，没办法用sql直接排序，只能自己排了。 chent 20170113 start
			Object[] dest = dataList.toArray();
			for (int i=0; i<dest.length; i++){
                for (int j=i; j>0 && this.compare(dest[j-1], dest[j])>0; j--) {
                    swap(dest, j, j-1);
                }
			}
			
			ArrayList list = new ArrayList();
			for(int i=0; i<dest.length; i++){
				list.add(dest[i]);
			}
			// 基于栏目设置重新排序，因为数据是拼出来的，没办法用sql直接排序，只能自己排了。 chent 20170113 end
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	 /**
	  * 交换数组位置
	 * @param x
	 * @param a
	 * @param b
	 */
	private void swap(Object[] x, int a, int b) {
	        Object t = x[a];
	        x[a] = x[b];
	        x[b] = t;
	    }
	
	 /**
	  * 自定义排序规则
	 * @param _o1 对象1
	 * @param _o2 对象2
	 * @return 0/-1/1
	 */
	public int compare(Object _o1, Object _o2) {
			
			HashMap<Object, Object> o1 = (HashMap<Object, Object>)_o1;
			HashMap<Object, Object> o2 = (HashMap<Object, Object>)_o2;
			
			if("合计".equals(o1.get("p0801"))){
				return 1;
			}
			if("合计".equals(o2.get("p0801"))){
				return -1;
			}
			
			int flag = 0;
			for(int i=0; i<this.sortItem.size(); i++){
				if(flag != 0){
					return flag;
				}
				String[] array = this.sortItem.get(i).toString().split(":");
				String itemid = array[0];
				String type = array[1];
				boolean isAsc = Integer.parseInt(array[2])==1?true:false;
				
				String v1 = (String)o1.get(itemid);
				String v2 = (String)o2.get(itemid);
				Collator instance = Collator.getInstance(Locale.CHINA);
				if("A".equalsIgnoreCase(type) || "M".equalsIgnoreCase(type) || "D".equalsIgnoreCase(type)){
					//flag = isAsc ? (v1.compareTo(v2)) : (v2.compareTo(v1));
					if("M".equalsIgnoreCase(type)){
						v1 = SafeCode.decode(v1);
						v2 = SafeCode.decode(v2);
					}
					if(StringUtils.isEmpty(v1)) {
                        v1="";
                    }
					if(StringUtils.isEmpty(v2)) {
                        v2="";
                    }
					flag =  isAsc ? (instance.compare(v1, v2)) : (instance.compare(v2, v1));
					
				} else {
					if(StringUtils.isEmpty(v1)){
						v1 = "0";
					}
					if(StringUtils.isEmpty(v2)){
						v2 = "0";
					}
					if (Double.parseDouble(v1) > Double.parseDouble(v2)) {
						flag = isAsc ? 1 : -1;
					}else if(Double.parseDouble(v1) < Double.parseDouble(v2)) {
						flag = isAsc ? -1 : 1;
					}else {
						flag = 0;
					}
				}
				
			}
			return flag;
		
		}
	/**
	 * 获得时间范围内对象工作计划下的任务
	 * @param object_type 对象类型  1：人员计划  2：团队计划
	 * @param object_id 对象id：  人员：nbase+a0100    部门:id
	 * @param startDate  yyyy-MM-dd
	 * @param endDate  yyyy-MM-dd
	 * @param cycle 周期：1、年度 2、半年  3、季度  4、月份  5、周
	 * @param month 月
	 * @param cycleIndex 周、半年、季度
	 * @param scope 任务范围  0：所有   1：我负责的任务  2：我参与的任务
	 * @return
	 */

	public ArrayList getTaskByCycle(int object_type,String object_id,int cycle,int year,int month,int cycleIndex,int scope) throws Exception
	{
		ArrayList list=new ArrayList();
		try{

			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sql_str=new StringBuffer("select p08.*,p07.Nbase,p07.a0100,p07.p0707,p07.p0723  from p08,per_task_map ptm,p07 where ptm.p0700=p07.p0700 and ptm.p0800=p08.p0800  and p07.p0723 in (1,2) ");
			if(object_type==2)  {
				sql_str.append(" and ptm.org_id='"+object_id+"'"); 
				//指派到部门wusy
			    sql_str.append(" and (ptm.flag=5  or ((ptm.flag=1 or ptm.flag=2) and dispatchFlag=1))" ); 
			}else {
                sql_str.append(" and ptm.nbase='"+object_id.substring(0,3)+"' and ptm.a0100='"+object_id.substring(3)+"' ");
            }
			/* 只列出自建任务和别人分配给我已批的任务 */
			sql_str.append(" and (( ptm.flag<>5 and p08.p0811='03' ) or ptm.flag=5  ) ");
			if(scope==1) //我负责的任务
			{
				sql_str.append(" and ( ptm.flag=1 or ptm.flag=5 )");
			}
			else if(scope==2) //我参与的任务
			{
				sql_str.append(" and  ptm.flag=2 ");
			}
			/* 同期计划 */
			sql_str.append(" and p07.p0725="+cycle);
			
			sql_str.append(" and p07.p0727="+String.valueOf(year));  
           
			if(cycle==2 ||cycle==3 )//半年、季度
            {
                sql_str.append(" and p07.p0729="+String.valueOf(cycleIndex));
            } else if (cycle==4 || cycle==5)//月 周
            {
                sql_str.append(" and p07.p0729="+String.valueOf(month));
            }
            if(cycle==5)//周
            {
                sql_str.append(" and p07.p0731="+String.valueOf(cycleIndex));
            }
			sql_str.append(" order by ptm.seq");
			list=dao.searchDynaList(sql_str.toString()); 
			list =getOrderedTableList(list);
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
		} 
		return list;
	}
	
	
	/**
	 * 获得时间范围内对象工作计划下的任务
	 * @param object_type 对象类型  1：人员计划  2：团队计划
	 * @param object_id 对象id：  人员：nbase+a0100    部门:id
	 * @param startDate  yyyy-MM-dd
	 * @param endDate  yyyy-MM-dd
	 * @param cycle 周期：1、年度 2、半年  3、季度  4、月份  5、周
	 * @param scope 任务范围  0：所有   1：我负责的任务  2：我参与的任务
	 * @return
	 */
	public ArrayList getTaskByTime(int object_type,String object_id,String startDate,String endDate,int cycle,int scope) throws Exception
	{
	    ArrayList list=new ArrayList();
	    try
	    {
	        if(startDate.length()==0||endDate.length()==0) {
                return list;
            }
	        ContentDAO dao = new ContentDAO(this.conn);
	        StringBuffer sql_str=new StringBuffer("select p08.*,p07.Nbase,p07.a0100,p07.p0707,p07.p0723  from p08,per_task_map ptm,p07 where ptm.p0700=p07.p0700 and ptm.p0800=p08.p0800  and p07.p0723 in (1,2) ");
	        if(object_type==2) { 
	            sql_str.append(" and ptm.org_id='"+object_id+"'"); 
	            //指派到部门wusy
	        	sql_str.append(" and (ptm.flag=5  or ((ptm.flag=1 or ptm.flag=2) and dispatchFlag=1))" );
	        } else {
                sql_str.append(" and ptm.nbase='"+object_id.substring(0,3)+"' and ptm.a0100='"+object_id.substring(3)+"' ");
            }
	        /* 只列出自建任务和别人分配给我已批的任务 */
	        sql_str.append(" and (( ptm.flag<>5 and p08.p0811='03' ) or ptm.flag=5  ) ");
	        sql_str.append(" and (( "+Sql_switcher.dateValue(startDate) +" < p0815");
	        sql_str.append(" and "+Sql_switcher.dateValue(endDate) +"  > p0813 ) ");		
	        sql_str.append(" or (p0815 is null and "+Sql_switcher.dateValue(endDate) +"  > p0813  )");		
	        sql_str.append(" or (p0813 is null and "+Sql_switcher.dateValue(startDate) +" < p0815  ))");		
	        if(scope==1) //我负责的任务
	        {
	            sql_str.append(" and ( ptm.flag=1 or ptm.flag=5 )");
	        }
	        else if(scope==2) //我参与的任务
	        {
	            sql_str.append(" and  ptm.flag=2 ");
	        }
	        /* 同期计划 */
	        sql_str.append(" and p07.p0725="+cycle);
	        sql_str.append(" order by ptm.seq");
	        list=dao.searchDynaList(sql_str.toString()); 
	        list =getOrderedTableList(list);
	        
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace(); 
	    } 
	    return list;
	}
	
	

    /**   
     * @Title: getOrderedTableList   
     * @Description: 对数据排序   
     * @param @param list
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getOrderedTableList(ArrayList tableDataList)
    {
        DynaBean dynaBean=null;
        HashMap p0800Map=new HashMap();
        ArrayList newDataList = new ArrayList();
        for(int i=0;i<tableDataList.size();i++)
        {
            dynaBean=(DynaBean)tableDataList.get(i);
            p0800Map.put((String)dynaBean.get("p0800"), "1");
        }
        int seq=0;
        for(int i=0;i<tableDataList.size();i++)
        {
            dynaBean=(DynaBean)tableDataList.get(i);
            String p0831=(String)dynaBean.get("p0831");
            if((((String)dynaBean.get("p0800")).equals((String)dynaBean.get("p0831")))
                    ||p0800Map.get(p0831)==null){
                seq++;
                orderTableList(dynaBean,1,seq+"",tableDataList,newDataList); 
                
                
            }
        }
        return newDataList;
    } 
    /**   
     * @Description: 协作任务：没有审批的协作任务不显示
     * @param @param list
     * @param @return 
     * @return ArrayList 
     * chent   
     * @throws   
     */
    public ArrayList getListExceptCooperation(ArrayList tableDataList)
    {
    	ArrayList newDataList = new ArrayList();

    	for(int i=0; i<tableDataList.size(); i++)
    	{
    		boolean flg = true;
    		DynaBean dynaBean = (DynaBean)tableDataList.get(i);
    		String p0800 = (String)dynaBean.get("p0800");
    		WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
    		if(util.isCooperationTask(Integer.parseInt(p0800), false)){// 启用协作任务并且是协作任务
    			WorkPlanBo workPlanBo = new WorkPlanBo(this.conn, this.userView);
    			String status = workPlanBo.getCooperationTaskStatus(Integer.parseInt(p0800));
    			if("01".equals(status)){// 没有审批 01: 待批
    				flg = false;
    			}
    		}
    		
    		if(flg){
    			newDataList.add(dynaBean);
    		}
    	}
    	return newDataList;
    } 
    
    /**   
     * @Title: orderTableList   
     * @Description:    
     * @param @param dynaBean
     * @param @param currentLevel 当前层级
     * @param @param tableDataList 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public void orderTableList(DynaBean dynaBean,int currentLevel,String num,
            ArrayList oldDataList,ArrayList newDataList)
    {
        dynaBean.set("_level", currentLevel+"");
        dynaBean.set("_seq", num);
        newDataList.add(dynaBean);
        String p0800=(String)dynaBean.get("p0800");    
        DynaBean _dynaBean=null;
        int _seq=0;
        for(int j=0;j<oldDataList.size();j++) {
            _dynaBean=(DynaBean)oldDataList.get(j);
            String _p0800=(String)_dynaBean.get("p0800");
            String _p0831=(String)_dynaBean.get("p0831");
            if(!p0800.equalsIgnoreCase(_p0800)&&_p0831.equalsIgnoreCase(p0800))
            {
                int level =  currentLevel+1; 
                _seq++;
                String seq =num +"."+_seq;
                orderTableList(_dynaBean,level,seq,oldDataList,newDataList); 
            }
        }
    }
     
    /**
	 * 获得计划任务的JSON格式数据
	 * @author dengcan
	 * @serialData 2014-07-12
	 * @return
	 */
	/*public String getDataJson(ArrayList headList)
	{
		StringBuffer _json=new StringBuffer("{text:'.',children:[");
		ArrayList tableDataList=new ArrayList();
		HashMap taskMemberMap=new HashMap();
		//上级评分(取平均分)
		HashMap scoreMap = new HashMap();
		if(this.p07_vo!=null)
			tableDataList=getTableData(""); //获得计划下的任务数据
		
		if(tableDataList.size()>0){
			taskMemberMap=getTaskMemberMap(tableDataList);
			scoreMap = getTaskScoreMap(tableDataList);
		}
		DynaBean dynaBean=null;
		int seq=0;
		HashMap p0800Map=new HashMap();
		for(int i=0;i<tableDataList.size();i++)
		{
			dynaBean=(DynaBean)tableDataList.get(i);
			p0800Map.put((String)dynaBean.get("p0800"), "1");
		}
		for(int i=0;i<tableDataList.size();i++)
		{
			dynaBean=(DynaBean)tableDataList.get(i);
			String p0831=(String)dynaBean.get("p0831");
			String p0817 = (String)dynaBean.get("p0817");
			if(StringUtils.isNotBlank(p0817)){
				if("0.0".equals(p0817) || "0.00".equals(p0817)){
					dynaBean.set("p0817", "");
				}else{
					int k = Math.round(Float.parseFloat(p0817));
					dynaBean.set("p0817", k+"");
				}
			}
			if((((String)dynaBean.get("p0800")).equals((String)dynaBean.get("p0831")))||p0800Map.get(p0831)==null){
				seq++;
				executeJsonData(_json,headList,dynaBean,String.valueOf(seq),tableDataList,taskMemberMap,scoreMap);
				
				
			}
		}
       if (true){
           //addSummaryJson(headList,_json);
       }
		if (",".equalsIgnoreCase(_json.substring(_json.length()-1, _json.length())))
		    _json.setLength(_json.length()-1);
		_json.append("]}");
		return _json.toString();
	} */
	
	
	/**
	 * @author lis
	 * @Description: 获得计划任务的数据
	 * @date 2016-3-10
	 * @param headList 表头
	 * @param p0800 任务id
	 * @return ArrayList
	 * @throws GeneralException 
	 */
	public ArrayList getDataList(ArrayList headList,String p0800,String showSubTask) throws GeneralException
	{
		ArrayList list = new ArrayList();	
		try {
			ArrayList tableDataList=new ArrayList();
			HashMap taskMemberMap=new HashMap();
			//上级评分(取平均分)
			HashMap scoreMap = new HashMap();
			if(this.p07_vo!=null) {
                tableDataList=getTableDataAsync(p0800); //获得计划下的任务数据
            }
			
			if(tableDataList.size()>0){
				taskMemberMap=getTaskMemberMap(tableDataList);//计划所有相关人员
				scoreMap = getTaskScoreMap(tableDataList);
			}
			DynaBean dynaBean=null;
			int seq=0;
			HashMap p0800Map=new HashMap();

			for(int i=0;i<tableDataList.size();i++)
			{
				dynaBean=(DynaBean)tableDataList.get(i);
				String p0831=(String)dynaBean.get("p0831");
				String p0817 = (String)dynaBean.get("p0817");
				if(StringUtils.isNotBlank(p0817)){
					if("0.0".equals(p0817) || "0.00".equals(p0817)){
						dynaBean.set("p0817", "");
					}else{
						int k = Math.round(Float.parseFloat(p0817));
						dynaBean.set("p0817", k+"");
					}
				}
				String curP0800 = (String)dynaBean.get("p0800");
				if(p0800Map.get(curP0800)==null){
					p0800Map.put(curP0800, "1");
					seq++;
					HashMap map = executeMapData(headList,dynaBean,String.valueOf(seq),tableDataList,taskMemberMap,scoreMap,p0800,showSubTask);
					list.add(map);
				}
			}
	       if (StringUtils.isBlank(p0800)){
	           addSummaryJson(list,headList);
	       }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	} 
	 
	
	/**
	 * 获得计划下各任务的参与人|负责人信息
	 * @param dataList
	 * @author dengcan
	 * @serialData 2014-07-12
	 * @return
	 */
	public HashMap getTaskMemberMap(ArrayList dataList)
	{
		HashMap map=new HashMap();
		HashMap p0700Map=new HashMap();
		RowSet rowSet = null;
		RowSet rowSet2 = null;
		try
		{
			//lis 2016-03-16 start
			/* 同期计划 */
			int period_type = this.p07_vo.getInt("p0725");
			int period_year = this.p07_vo.getInt("p0727");
			int period_month = 0;
			int period_week = 0;
			if(period_type==2||period_type==3||period_type==4||period_type==5) {
                period_month = this.p07_vo.getInt("p0729");
            }
			if(period_type==5) {
                period_week = this.p07_vo.getInt("p0731");
            }
			WorkPlanBo planBo = new WorkPlanBo(this.conn, this.userView);
			
			//lis 2016-03-16 end
			ContentDAO dao = new ContentDAO(this.conn);
			DynaBean dynaBean=null;
			StringBuffer p0800_str=new StringBuffer("");
			for(int i=0;i<dataList.size();i++)
			{
				dynaBean=(DynaBean)dataList.get(i);
				p0800_str.append(","+(String)dynaBean.get("p0800"));
			}
			String _p0800_str = "''";
			if(p0800_str.length() > 1){// 有可能只为“，”
				_p0800_str = p0800_str.toString().substring(1);
			}
			//linbz okr优化复制任务
//			HashMap existPersonMap=new HashMap(); //存在的人
//			rowSet=dao.search("select distinct Nbase as nbase from p09 where p0901=2 and p0903 in ("+_p0800_str+") ");
//			while(rowSet.next())
//			{
//				String nbase=rowSet.getString("nbase");
//				rowSet2=dao.search("select a0100 from "+nbase+"A01 where a0100 in (select a0100 from p09 where p0901=2 and nbase='"+nbase+"' and p0903 in ("+_p0800_str+") )");
//				while(rowSet2.next())
//				{
//					existPersonMap.put(nbase+rowSet2.getString("a0100"),"1");
//				}
//			}
			
			rowSet=dao.search("select * from p09 where p0901=2 and p0903 in ("+_p0800_str+") order by p0900");
			while(rowSet.next())
			{
				String p0903=rowSet.getString("p0903"); //任务ID
				String p0905=rowSet.getString("p0905"); // 1、负责人 2、参与人(协办人) 3、关注人
				String P0913=rowSet.getString("P0913"); //姓名
				String nbase=rowSet.getString("Nbase"); //人员编号
				String a0100=rowSet.getString("A0100"); //人员编号
				
				/*linbz
		         * 查询复制的任务之前先校验所选计划的任务中是否有人员信息变动
		         * 任务负责人的信息变动，则改为当前人员信息
		         * 任务成员人的信息变动，则直接删除
		         * 这里先注释掉
		         * */
				//过滤掉在职人员库中不存在的人员
//				if(existPersonMap.get(nbase+a0100)!=null){
					String nbasea0100_e = PubFunc.encrypt(nbase + a0100);
					String _str = "";
					if("2".equals(p0905)){//任务成员 前台需要人员编号
						_str = nbasea0100_e+":"+P0913;
					}else{
						_str = P0913;
					}
					if(map.get(p0903+"/"+p0905)!=null)//任务成员
					{
						if("2".equals(p0905)){
							_str=((String)map.get(p0903+"/"+p0905))+"、"+nbasea0100_e+":"+P0913;
						} else{
							_str=((String)map.get(p0903+"/"+p0905))+"、"+P0913;
						}
					}
					map.put(p0903+"/"+p0905, _str); 

					if("1".equals(p0905)){//负责人
						//计划表id
						int p0700 = planBo.getPeoplePlanId(nbase, a0100, String.valueOf(period_type), String.valueOf(period_year), String.valueOf(period_month), String.valueOf(period_week));
						p0700Map.put(p0903, p0700); 
					}
//				}
			}
			map.put("p0700", p0700Map);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rowSet2);
			PubFunc.closeDbObj(rowSet);
		}
		return map;
	}
	
	/**
	 * 获取计划下每个任务的上级平均分分值
	 * @param dataList
	 * @return
	 */
	public HashMap getTaskScoreMap(ArrayList dataList)
	{
		WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			DynaBean dynaBean=null;
			StringBuffer p0800_str=new StringBuffer("");
			for(int i=0;i<dataList.size();i++)
			{
				dynaBean=(DynaBean)dataList.get(i);
				p0800_str.append(","+(String)dynaBean.get("p0800"));
			}
			String planOwnerId = "";
			String planOwnerSupId = "";
			String planOwnerSupE01a1 = "";
			String planOwnerSupNbase = "";
			String planOwnerSupA0100 = "";
			int p0723 = p07_vo.getInt("p0723");
			String e01a1 = "";
			//Sql_switcher
			//直接上级针对计划所有者计划下每条任务评分的平均分
			StringBuffer sql = new StringBuffer();
			if(p0723 == 1){
				planOwnerId = p07_vo.getString("nbase")+p07_vo.getString("a0100");
				planOwnerSupE01a1 = wpUtil.getDirectSuperE01a1(p07_vo.getString("e01a1"));
				planOwnerSupId = wpUtil.getFirstE01a1Leaders(planOwnerSupE01a1);
				if(planOwnerSupId.length()>0){
					planOwnerSupNbase = planOwnerSupId.substring(0, 3);
					planOwnerSupA0100 = planOwnerSupId.substring(3);
				}		
				//sql.append("select p0800, AVG(score) as 'score' from per_task_evaluation where (evaluator_nbase+evaluator_a0100)='"+planOwnerSupId+"' and (nbase+a0100)='"+planOwnerId+"' and p0800 in ("+p0800_str.toString().substring(1)+") group by p0800");
				String _p0800_str = "''";
				if(p0800_str.length() > 1){// 有可能只为“，”
					_p0800_str = p0800_str.toString().substring(1);
				}
				sql.append("select p0800, AVG(score) as score from per_task_evaluation where evaluator_nbase='"+planOwnerSupNbase+"' and evaluator_a0100='"+planOwnerSupA0100+"' and nbase = '"+planOwnerId.substring(0, 3)+"' and a0100='"+planOwnerId.substring(3)+"' and p0800 in ("+_p0800_str+") group by p0800");
			}else if(p0723 == 2){
				planOwnerId = p07_vo.getString("p0707");
				planOwnerSupE01a1 = wpUtil.getDirectSuperE01a1(wpUtil.getDeptLeaderE01a1(p07_vo.getString("p0707")));
				planOwnerSupId = wpUtil.getFirstE01a1Leaders(planOwnerSupE01a1);
				if(planOwnerSupId.length() > 3){
					planOwnerSupNbase = planOwnerSupId.substring(0, 3);
					planOwnerSupA0100 = planOwnerSupId.substring(3);
				}
				sql.append("select p0800, AVG(score) as score from per_task_evaluation where evaluator_nbase='"+planOwnerSupNbase+"' and evaluator_a0100='"+planOwnerSupA0100+"' and org_id = '"+planOwnerId+"' and  p0800 in ("+p0800_str.toString().substring(1)+") group by p0800");
			}
			if(StringUtils.isBlank(planOwnerSupE01a1) || StringUtils.isBlank(planOwnerSupId)){
				return map;
			}
			RowSet rowSet=dao.search(sql.toString());
			while(rowSet.next()){
				String p0800 = rowSet.getString("p0800");
				int scor = rowSet.getInt("score");
				map.put(p0800+"/"+"score", scor); 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		return map;
	}
	
	/**
	 * @author lis
	 * @Description: 获取任务的上级平均分分值
	 * @date 2016-3-22
	 * @param p0800 任务id
	 * @return
	 */
	public int getTaskScore(String p0800,RecordVo p07_vo)
	{
		WorkPlanUtil wpUtil = new WorkPlanUtil(conn, userView);
		HashMap map=new HashMap();
		int scor = 0;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			DynaBean dynaBean=null;
			String planOwnerId = "";
			String planOwnerSupId = "";
			String planOwnerSupE01a1 = "";
			String planOwnerSupNbase = "";
			String planOwnerSupA0100 = "";
			int p0723 = p07_vo.getInt("p0723");
			String e01a1 = "";
			//Sql_switcher
			//直接上级针对计划所有者计划下每条任务评分的平均分
			StringBuffer sql = new StringBuffer();
			if(p0723 == 1){
				planOwnerId = p07_vo.getString("nbase")+p07_vo.getString("a0100");
				planOwnerSupE01a1 = wpUtil.getDirectSuperE01a1(p07_vo.getString("e01a1"));
				planOwnerSupId = wpUtil.getFirstE01a1Leaders(planOwnerSupE01a1);
				if(planOwnerSupId.length()>0){
					planOwnerSupNbase = planOwnerSupId.substring(0, 3);
					planOwnerSupA0100 = planOwnerSupId.substring(3);
				}	
				sql.append("select p0800, AVG(score) as score from per_task_evaluation ");
				sql.append(" where evaluator_nbase='"+planOwnerSupNbase +"' and evaluator_a0100='"+planOwnerSupA0100+"'");
				sql.append(" and nbase = '"+planOwnerId.substring(0, 3)+"' and a0100='"+planOwnerId.substring(3)+"'");
				sql.append(" and p0800 in ("+p0800+") group by p0800");
			}else if(p0723 == 2){
				planOwnerId = p07_vo.getString("p0707");
				planOwnerSupE01a1 = wpUtil.getDirectSuperE01a1(wpUtil.getDeptLeaderE01a1(p07_vo.getString("p0707")));
				planOwnerSupId = wpUtil.getFirstE01a1Leaders(planOwnerSupE01a1);
				if(planOwnerSupId.length() > 3){
					planOwnerSupNbase = planOwnerSupId.substring(0, 3);
					planOwnerSupA0100 = planOwnerSupId.substring(3);
				}
				sql.append("select p0800, AVG(score) as score from per_task_evaluation");
				sql.append(" where evaluator_nbase='"+planOwnerSupNbase+"' and evaluator_a0100='"+planOwnerSupA0100+"'");
				sql.append(" and org_id = '"+planOwnerId+"' and  p0800 in ("+p0800+") group by p0800");
			}
			if(StringUtils.isBlank(planOwnerSupE01a1) || StringUtils.isBlank(planOwnerSupId)){
				return scor;
			}
			RowSet rowSet=dao.search(sql.toString());
			while(rowSet.next()){
				scor = rowSet.getInt("score");
				map.put(p0800+"/"+"score", scor); 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		return scor;
	}
	
	/**   
	 * @Title: addSummaryJson   
	 * @Description: 增加合计行   
	 * @param @param headList
	 * @param @param _json 
	 * @return void 
	 * @author:wangrd   
	 * @throws   
	*/
	private void addSummaryJson(ArrayList list,ArrayList headList)
    {
		HashMap map = new HashMap();
		map.put("othertask", 0);
		map.put("id","");
		map.put("seq", "");
		map.put("p0800", "");
		map.put("p0833", "");
		map.put("p0809", "");
		map.put("p0700", "");
		map.put("objectid", WorkPlanUtil.encryption(""));
		map.put("p0723", WorkPlanUtil.encryption(""));
		map.put("p0801", "合计");
       FieldItem item = null;
        for (int i = 0; i < headList.size(); i++) {            
            item = (FieldItem) headList.get(i);
            if ("p0801".equals(item.getItemid().toLowerCase()) || "seq".equals(item.getItemid().toLowerCase())){
                continue;
            }
            if ("rank".equals(item.getItemid().toLowerCase())){
                String value=getSumRank()+"";
                map.put(item.getItemid().toLowerCase(), value);
                continue;
            }
            if (specialFieldStr.indexOf("/" + item.getItemid().toLowerCase() + "/") == -1) {
                if ("N".equalsIgnoreCase(item.getItemtype())) {
                	map.put(item.getItemid().toLowerCase(), "");

                } else {
                	map.put(item.getItemid().toLowerCase(), "");
                }
            } else {
            	map.put(item.getItemid().toLowerCase(), "");
            }
        }
        map.put("leaf", "true");
        list.add(map);
    }
	
	   
    /**   
     * @Title: getSumRank   
     * @Description: 返回rank合计  
     * @param @param headList
     * @param @param _json 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public String getSumRank()
    {
        double sum=0;
        String value="";
        try {
            String sql=getTableDatasql("");
            /*
            int index= sql.indexOf("from");
            if (index>0)
                sql=sql.substring(index);
            index= sql.indexOf("order");
            if (index>0)
                sql=sql.substring(0,index-1);
          
            sql="select sum(ptm.rank) as rank "+sql;
            */
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rSet=dao.search(sql);            
            while (rSet.next()) {
                int p0833 = rSet.getInt("p0833"); // 任务变更状态
                String p0809 = rSet.getString("p0809"); // 任务执行状态       
                String p0800 = rSet.getString("p0800"); // 任务执行状态       
                if (WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809)
                        || WorkPlanConstant.TaskChangedStatus.Cancel==p0833) { // 已取消
                    continue;
                }
                
                WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
                if(util.isOpenCooperationTask()){// 启用协办任务
    				
    				// 过滤掉协办任务中（发起人不是自己）并且（协办任务状态不是已批准）的任务
					if(util.isCooperationTask(Integer.parseInt(p0800), false)){//是协办任务
						String startPerson = util.getTaskRolePersionList(Integer.parseInt(p0800), 5).get(0);//任务发起人
						String startPersonNbs = startPerson.substring(0, 3);
						String startPersonA0100 = startPerson.substring(3);
						String planStartPersonNbs = "";
						String planStartPersonA0100 = "";
						if(this.p07_vo.getInt("p0723") == 1){// 个人计划
							planStartPersonNbs = this.p07_vo.getString("nbase");
							planStartPersonA0100 = this.p07_vo.getString("a0100");
						} else {// 团队计划
							String org_id = this.p07_vo.getString("p0707");
							if(!StringUtils.isEmpty(org_id)){
								String leader = util.getFirstDeptLeaders(org_id);
								if(!StringUtils.isEmpty(leader)){
									planStartPersonNbs = leader.substring(0, 3);
									planStartPersonA0100 = leader.substring(3);
								}
							}
						}
						if(!startPersonNbs.equalsIgnoreCase(planStartPersonNbs) || !startPersonA0100.equalsIgnoreCase(planStartPersonA0100)){// 发起人不是自己
							
							WorkPlanBo workPlanBo = new WorkPlanBo(this.conn, this.userView);
							String status = workPlanBo.getCooperationTaskStatus(Integer.parseInt(p0800));
							if(!"02".equals(status)){// 协办任务状态不是已批准
								continue;
							}
						}
					}
    				
    			}
                sum= sum+rSet.getDouble("rank");
            }
            sum=sum*100;
            if (sum==0.0){
                value="0"; 
            }
            else {
                value = new DecimalFormat("###.###").format(sum);
            }
           // sum=PubFunc.parseDouble(value)*100;
           // value=String.valueOf(sum);
            
        } catch (Exception e) {
           e.printStackTrace(); 
        }
        return value;
    }
    
    
	
	
	/**
	 * 获得任务JSON格式数据
	 * @param _json
	 * @param headList  表头列
	 * @param dynaBean
	 * @param currentSeq 序号
	 * @param tableDataList 计划下的任务记录
	 * @param taskMemberMap 计划下各任务的参与人|负责人信息
	 * @author dengcan
	 * @serialData 2014-07-12
	 */
	public void executeJsonData(StringBuffer _json,ArrayList headList,DynaBean dynaBean,String currentSeq,ArrayList tableDataList,HashMap taskMemberMap, HashMap scoreMap)
	{
		WorkPlanUtil ut = new WorkPlanUtil(conn, userView);
		int role = -1;
		if(p07_vo!=null && userView!=null){
			role = ut.getLoaderRole(p07_vo.getInt("p0700"));
		}
		String objectid="";
		String p0800=(String)dynaBean.get("p0800");
		if(p07_vo.getInt("p0723")==2)  //p0723-> 1:人员计划  2：团队计划  3:项目计划 
        {
            objectid=this.p07_vo.getString("p0707");
        } else {
            objectid=this.p07_vo.getString("nbase")+this.p07_vo.getString("a0100");
        }
		//othertask:dynaBean.get("othertask")为空时，从计划详情页面添加子任务的时候，计划列表页面不能正常刷新 haosl update 2018年7月9日
		_json.append("{othertask:'"+(dynaBean.get("othertask")==null?"0":(String)dynaBean.get("othertask"))+"',seq:'"+currentSeq+"',p0800:'"+WorkPlanUtil.encryption(p0800)+"',p0700:'"+WorkPlanUtil.encryption(String.valueOf(this.p07_vo.getInt("p0700")))+"'");
		_json.append(",objectid:'"+WorkPlanUtil.encryption(objectid)
		        +"',p0723:'"+WorkPlanUtil.encryption(String.valueOf(p07_vo.getInt("p0723")))
		        +"',p0809:'"+(String)dynaBean.get("p0809")+"'"
		        +",p0811:'"+(String)dynaBean.get("p0811")+"'"
		        +",id:'"+WorkPlanUtil.encryption(p0800)+"'"
		        +",p0833:'"+(String)dynaBean.get("p0833")+"'");
//		if(currentSeq.indexOf(".")==-1)
//			_json.append(",expanded: true");
		FieldItem item=null;
		for(int i=0;i<headList.size();i++)
		{
			item = (FieldItem) headList.get(i); 
			if(specialFieldStr.indexOf("/"+item.getItemid().toLowerCase()+"/")==-1)
			{
				if(dynaBean.get(item.getItemid().toLowerCase())==null) {
                    continue;
                }
				if("rank".equalsIgnoreCase(item.getItemid()))
				{ 
				    String value=(String)dynaBean.get(item.getItemid().toLowerCase());
				    if (value==null) {
                        value="";
                    }
				    if (value.length()>0){ 
	                    double dValue=PubFunc.parseDouble(value)*100;   
	                    if (dValue==0.0){
	                        value="0"; 
	                    }
	                    else {
	                        value=new DecimalFormat("###.###").format(dValue);
	                    }
	                   // dValue=PubFunc.parseDouble(value)*100;
	                   // value=String.valueOf(dValue);
	                }
						 
					_json.append(","+item.getItemid().toLowerCase()+":'"+value+"'");
					 
				}
				else if("N".equalsIgnoreCase(item.getItemtype()))
				{ 
				    _json.append(","+item.getItemid().toLowerCase()+":'"+(String)dynaBean.get(item.getItemid().toLowerCase())+"'");
				    
				}
				else if("M".equalsIgnoreCase(item.getItemtype()))
				{ 
					_json.append(","+item.getItemid().toLowerCase()+":'"+SafeCode.encode((String)dynaBean.get(item.getItemid().toLowerCase()))+"'");
					
				}
				else
				{
				    String codeSetid =WorkPlanUtil.nvl(item.getCodesetid(),"");
				    String value= WorkPlanUtil.nvl((String)dynaBean.get(item.getItemid().toLowerCase()),"");
				    if("D".equalsIgnoreCase(item.getItemtype()) && StringUtils.isNotBlank(value)){
				    	value = value.replaceAll("-", "/");
				    }
				    if (!"0".equals(codeSetid)&&codeSetid.length()>0){
				        value=AdminCode.getCodeName(codeSetid, value);
				    }
				
					_json.append(","+item.getItemid().toLowerCase()+":'"+value+"'");
				}
			}
			else
			{
				 getSpecialColumnJson(_json,item,dynaBean,taskMemberMap,scoreMap);
				
			} 
		}
		if(role>-1){//上级评分
			_json.append(","+"superiorEvaluation"+":'"+scoreMap.get(p0800+"/score")+"'");
		}
		DynaBean _dynaBean=null;
		int n=0;
		for(int j=0;j<tableDataList.size();j++)
		{
			_dynaBean=(DynaBean)tableDataList.get(j);
			String _p0800=(String)_dynaBean.get("p0800");
			String _p0831=(String)_dynaBean.get("p0831");
			if(!p0800.equalsIgnoreCase(_p0800)&&_p0831.equalsIgnoreCase(p0800))//有子节点
			{
				n++;
				if(n==1) {
                    _json.append(",children:[");
                }
				executeJsonData(_json,headList,_dynaBean,currentSeq+"."+n,tableDataList,taskMemberMap,scoreMap);
			}
		}
		if(n>0)
		{
			_json.setLength(_json.length()-1);
			_json.append("]");
		
		}
		else
		if(n==0) {
            _json.append(",leaf:true");
        }
		_json.append("},");
	}
	
	/**
	 * 获得任务JSON格式数据
	 * @param _json
	 * @param headList  表头列
	 * @param dynaBean 行数据
	 * @param currentSeq 序号
	 * @param tableDataList 计划下的任务记录
	 * @param taskMemberMap 计划下各任务的参与人|负责人信息
	 * @author dengcan
	 * @throws GeneralException 
	 * @serialData 2014-07-12
	 */
	public HashMap executeMapData(ArrayList headList,DynaBean dynaBean,String currentSeq,ArrayList tableDataList,HashMap taskMemberMap, HashMap scoreMap,String P0800,String showSubTask) throws GeneralException
	{
		HashMap map = new HashMap();
		RowSet rs = null;
		try {
			String othertask = (String)dynaBean.get("othertask");//1：是穿透任务
			String p0800=(String)dynaBean.get("p0800");
			HashMap p0700Map = (HashMap)taskMemberMap.get("p0700");//p0800=p0700
			if("1".equals(othertask)){
				if(p0700Map.get(p0800) != null) {
                    p07_vo = getP07Vo((Integer)p0700Map.get(p0800));
                }
			}
			
			WorkPlanUtil ut = new WorkPlanUtil(conn, userView);
			int role = -1;
			if(p07_vo!=null && userView!=null){
				role = ut.getLoaderRole(p07_vo.getInt("p0700"));
			}
			map.put("role", ""+role);//上下级关系  lis 20160322
			
			String objectid="";
			if(p07_vo.getInt("p0723")==2)  //p0723-> 1:人员计划  2：团队计划  3:项目计划 
            {
                objectid=this.p07_vo.getString("p0707");
            } else {
                objectid=this.p07_vo.getString("nbase")+this.p07_vo.getString("a0100");
            }
			map.put("othertask", othertask);
			map.put("id",WorkPlanUtil.encryption(p0800));
			map.put("seq", currentSeq);
			map.put("p0800", WorkPlanUtil.encryption(p0800));
			map.put("p0700", WorkPlanUtil.encryption(String.valueOf(this.p07_vo.getInt("p0700"))));
			map.put("objectid", WorkPlanUtil.encryption(objectid));
			map.put("p0723", WorkPlanUtil.encryption(String.valueOf(p07_vo.getInt("p0723"))));
			map.put("p0809", (String)dynaBean.get("p0809"));
			map.put("p0811", (String)dynaBean.get("p0811"));
			map.put("p0833", (String)dynaBean.get("p0833"));
			
			FieldItem item=null;
			for(int i=0;i<headList.size();i++)
			{
				item = (FieldItem) headList.get(i); 
				if(specialFieldStr.indexOf("/"+item.getItemid().toLowerCase()+"/")==-1)
				{
					if(dynaBean.get(item.getItemid().toLowerCase())==null) {
                        continue;
                    }
					if("rank".equalsIgnoreCase(item.getItemid()))
					{ 
					    String value=(String)dynaBean.get(item.getItemid().toLowerCase());
					    if (value==null) {
                            value="";
                        }
					    if (value.length()>0){ 
		                    double dValue=PubFunc.parseDouble(value)*100;   
		                    if (dValue==0.0){
		                        value="0"; 
		                    }
		                    else {
		                        value= new DecimalFormat("###.###").format(dValue);
		                    }
		                }
					    map.put(item.getItemid().toLowerCase(),value);	 
						 
					}
					else if("N".equalsIgnoreCase(item.getItemtype()))
					{ 
						map.put(item.getItemid().toLowerCase(), (String)dynaBean.get(item.getItemid().toLowerCase()));
					    
					}
					else if("M".equalsIgnoreCase(item.getItemtype()))
					{ 
						map.put(item.getItemid().toLowerCase(), SafeCode.encode((String)dynaBean.get(item.getItemid().toLowerCase())));
						
					}
					else
					{
					    String codeSetid =WorkPlanUtil.nvl(item.getCodesetid(),"");
					    String value= WorkPlanUtil.nvl((String)dynaBean.get(item.getItemid().toLowerCase()),"");
					    if("D".equalsIgnoreCase(item.getItemtype()) && StringUtils.isNotBlank(value)){
					    	value = value.replaceAll("-", "/");
					    }
					    if (!"0".equals(codeSetid)&&codeSetid.length()>0){
					        value=AdminCode.getCodeName(codeSetid, value);
					    }
					
					    map.put(item.getItemid().toLowerCase(), value);
					}
				}
				else
				{
					 getSpecialColumnMap(map, item, dynaBean, taskMemberMap, scoreMap);
					
				} 
			}
			if(role>-1){//上级评分
				if("1".equals(othertask)){//是穿透任务  lis 20160322
					int taskScore = this.getTaskScore(p0800, p07_vo);//当前任务的上级评价平均值
					map.put("superiorEvaluation", ""+taskScore);
				}else {
                    map.put("superiorEvaluation", ""+scoreMap.get(p0800+"/score"));
                }
			}
			
			// 判断该任务下是否有子节点 chent 20160426 start
			// 任务下是否能看到子任务的条件：
			//(创建人是自己) 或 (任务负责人是自己 且 是报批/已批状态)
			//排除顶级节点，p800=p0831 ,lis 20160620
			boolean leaf = false;
			/*StringBuffer sql = new StringBuffer();
			if("0".equals(othertask)){
				sql.append("select count(p08.p0800) as countNum  from p08,p09,per_task_map ptm where p08.p0800=ptm.p0800  and p08.p0800!=p08.p0831  and p08.p0831=");
				sql.append(p0800);
				sql.append(" and ((ptm.flag=5  ");
				if(this.p07_vo.getInt("p0723") == 1){//人员计划
					sql.append(" and ptm.nbase='"+this.p07_vo.getString("nbase")+"' and ptm.a0100='"+this.p07_vo.getString("a0100")+"' ");
				} else if(this.p07_vo.getInt("p0723") == 2){//团队计划 haosl 20160921 p0723 = 2 时为团队计划
					sql.append(" and ptm.org_id='"+this.p07_vo.getString("p0707")+"' ");
				}
				if("1".equals(showSubTask)) {//计划页面显示穿透任务时，才需要统计穿透任务
					sql.append(") or (p09.P0903=p08.p0800 and p09.Nbase=ptm.Nbase and p09.a0100=ptm.a0100");
					sql.append(" and p09.p0905=1 and p08.p0811 in ('02','03'))");
					sql.append(")");
				}else {
					sql.append("))");
				}
			}else{//当前节点是穿透任务，判断其子任务，只要是发布的就可以看到 lis 20160801
				sql.append("select count(p08.p0800) as countNum  from p08 where  p08.p0800!=p08.p0831  and p08.p0831=");
				sql.append(p0800);
				sql.append(" and p08.P0811 in ('02','03')");
			}*/
			ContentDAO dao = new ContentDAO(this.conn); 
			rs = dao.search(getTableDatasqlAsync(p0800));
			//先判断任务下有没有非穿透的子任务
			if(!rs.next()){
				//没有非穿透的继续判断有无穿透任务
//				int count = rs.getInt("countNum");
//				if(count == 0){
				if("0".equals(showSubTask)) {
					leaf = true;//是子节点
				}else{
					if(StringUtils.isNotBlank(p0800)) {
						rs = dao.search(getTableDatasqlOther(p0800));
						if(!rs.next()) {//任务下面既没有非穿透任务也没有穿透任务，便是叶子结点
							leaf = true;//是子节点
						}
					}
				}
			}
			map.put("leaf", leaf);
			// 判断该任务下是否有子节点 chent 20160426 end
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return map;
	}
	
	/**
	 * 
	 * @param _json
	 * @param item
	 * @param dynaBean
	 * @param taskMemberMap
	 * @author dengcan
	 * @serialData 2014-07-12
	 */
	private void getSpecialColumnJson(StringBuffer _json,FieldItem item,DynaBean dynaBean,HashMap taskMemberMap,HashMap scoreMap)
	{
		String p0800=(String)dynaBean.get("p0800");
		String itemid=item.getItemid();
		if("principal".equalsIgnoreCase(itemid)) //负责人
		{
			if(taskMemberMap.get(p0800+"/1")!=null) {
                _json.append(","+item.getItemid().toLowerCase()+":'"+(String)taskMemberMap.get(p0800+"/1")+"'");
            }
		}
		else if("participant".equalsIgnoreCase(itemid)) //任务成员
		{
			if(taskMemberMap.get(p0800+"/2")!=null) {
                _json.append(","+item.getItemid().toLowerCase()+":'"+(String)taskMemberMap.get(p0800+"/2")+"'");
            }
		}
		else if("timeArrange".equalsIgnoreCase(itemid)) //时间安排
		{
			_json.append(","+item.getItemid().toLowerCase()+":'"+getTimeArrangeText(dynaBean)+"'");
		}
		else if("gantt".equalsIgnoreCase(itemid)) //甘特图
		{
			//haosl 甘特图数据实时显示  2018-3-21
			HashMap<String,Integer> map = new HashMap<String,Integer>();
			getGanttJson(map, (String)dynaBean.get("p0813"),(String)dynaBean.get("p0815"));
			for(Map.Entry<String, Integer> entry:map.entrySet()) {
				_json.append(","+entry.getKey()+":'"+entry.getValue()+"'");
			}
		}
		
		else if("p0835".equalsIgnoreCase(itemid)) //任务状态
		{
			_json.append(","+item.getItemid().toLowerCase()+":'"+(String)dynaBean.get("p0835")+"'");
		}
		else if("p0811".equalsIgnoreCase(itemid)) //任务报批状态
		{
			_json.append(","+item.getItemid().toLowerCase()+":'"+(String)dynaBean.get("p0811")+"'");
		}
		else if("taskprogresscolor".equalsIgnoreCase(itemid)) //红黄绿灯
		{
			String image_name=getWarningImage(dynaBean);
			_json.append(","+item.getItemid().toLowerCase()+":'"+image_name+"'");
		}
		
		/*
		else if(itemid.equalsIgnoreCase("p0835_state")) //任务状态 红黄绿灯
		{
			String image_name=getWarningImage(dynaBean);
			_json.append(","+item.getItemid().toLowerCase()+":'"+image_name+"_"+(String)dynaBean.get("p0835")+"'");
		}
		else if(itemid.equalsIgnoreCase("p0835_jd")) //任务进度 红黄绿灯
		{
			String image_name=getWarningImage(dynaBean);
			_json.append(","+item.getItemid().toLowerCase()+":'"+image_name+"_"+(String)dynaBean.get("p0835")+"'");
		}
		*/
		else
		{
			if(!"".equals(item.getCodesetid())&&!"0".equals(item.getCodesetid())){//代码型指标
	            _json.append(","+item.getItemid().toLowerCase()+":'"
	                    + (String)dynaBean.get(itemid)+"`"+AdminCode.getCodeName(item.getCodesetid(), (String)dynaBean.get(itemid.toLowerCase()))+"'");
	        }else if(dynaBean.get(item.getItemid().toLowerCase())!=null) {
                _json.append(","+item.getItemid().toLowerCase()+":'"+(String)dynaBean.get(item.getItemid().toLowerCase())+"'");
            }
		 
		}
	}
	
	/**
	 * @author lis
	 * @Description: 得到特殊列
	 * @date 2016-3-10
	 * @param map 存放列map
	 * @param item  某一列
	 * @param dynaBean 行数据
	 * @param taskMemberMap 任务相关成员
	 * @param scoreMap 任务评价分数
	 */
	private void getSpecialColumnMap(HashMap map,FieldItem item,DynaBean dynaBean,HashMap taskMemberMap,HashMap scoreMap)
	{
		String p0800=(String)dynaBean.get("p0800");
		String itemid=item.getItemid();
		if("principal".equalsIgnoreCase(itemid)) //负责人
		{
			if(taskMemberMap.get(p0800+"/1")!=null) {
                map.put(item.getItemid().toLowerCase(),(String)taskMemberMap.get(p0800 + "/1"));
            }
		}
		else if("participant".equalsIgnoreCase(itemid)) //任务成员
		{
			if(taskMemberMap.get(p0800+"/2")!=null){
				map.put(item.getItemid().toLowerCase(), (String)taskMemberMap.get(p0800 + "/2"));
			}else{
				map.put(item.getItemid().toLowerCase(), "");
			}
		}
		else if("timeArrange".equalsIgnoreCase(itemid)) //时间安排
		{
			map.put(item.getItemid().toLowerCase(), getTimeArrangeText(dynaBean));
		}
		else if("gantt".equalsIgnoreCase(itemid)) //甘特图
		{
			getGanttJson(map,(String)dynaBean.get("p0813"),(String)dynaBean.get("p0815"));
		}
		else if("p0835".equalsIgnoreCase(itemid)) //任务状态
		{
			map.put(item.getItemid().toLowerCase(), (String)dynaBean.get("p0835"));
		}
		else if("p0811".equalsIgnoreCase(itemid)) //任务报批状态
		{
			map.put(item.getItemid().toLowerCase(), (String)dynaBean.get("p0811"));
		}
		else if("taskprogresscolor".equalsIgnoreCase(itemid)) //红黄绿灯
		{
			String image_name=getWarningImage(dynaBean);
			map.put(item.getItemid().toLowerCase(), image_name);
		}
		else
		{
			if(!"".equals(item.getCodesetid())&&!"0".equals(item.getCodesetid())){//代码型指标
				map.put(item.getItemid().toLowerCase(), (String)dynaBean.get(itemid)+"`"+AdminCode.getCodeName(item.getCodesetid(), (String)dynaBean.get(itemid.toLowerCase())));
	        }else if(dynaBean.get(item.getItemid().toLowerCase())!=null) {
                map.put(item.getItemid().toLowerCase(), (String)dynaBean.get(item.getItemid().toLowerCase()));
            }
		}
	}
	
	/**
	 * 得到红、黄、绿灯图标名称
	 * @param dynaBean
	 * @return
	 */
	private String getWarningImage(DynaBean dynaBean)
	{
		
		String image_name="green";
		try
		{
			String startDate=dynaBean.get("p0813")!=null?(String)dynaBean.get("p0813"):"";
			String endDate=dynaBean.get("p0815")!=null?(String)dynaBean.get("p0815"):"";
			String p0800=dynaBean.get("p0800")!=null?(String)dynaBean.get("p0800"):"";
			PlanTaskBo taskBo=new PlanTaskBo(conn, userView);
			DbWizard dbw=new DbWizard(conn);
			String finishDate="";
			if(dbw.isExistField("P08","finishDate" ,false)){//判断当前表中是否存在该字段
				RowSet rset= taskBo.getTask(p0800);
				while(rset.next()){
					Date d = rset.getDate("finishDate");
					if(d != null){
						if(Sql_switcher.searchDbServer() == 2){
							d = new Date(rset.getTimestamp("finishDate").getTime());
						}
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						finishDate = df.format(d);
					}else{
						finishDate = "";
					}
					
				}
			}
			int p0835=dynaBean.get("p0835")!=null&&((String)dynaBean.get("p0835")).length()>0?Integer.parseInt((String)dynaBean.get("p0835")):0;
			
			Date _startDate=null;
			Date _endDate=null;
			Date _finishDate=null;
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			if(startDate.length()>0) {
                _startDate=dateFormat.parse(startDate);
            }
			if(endDate.length()>0) {
                _endDate=dateFormat.parse(endDate);
            }
			if(finishDate.length()>0) {
                _finishDate=dateFormat.parse(finishDate);
            }
			Date _sysDate=new Date();  
			if("".equals(finishDate)){//完成时间为空的情况
				if(_endDate!=null&&p0835!=100&&_sysDate.getTime()>_endDate.getTime())  //红灯  逾期未完成的任务，以红灯显示
				{
					image_name="red";
				}
				else if(_startDate!=null&&_endDate!=null)
				{
					if (_endDate.getTime()-_startDate.getTime()>0){
					    float value1 =_sysDate.getTime()-_startDate.getTime();
					    float value2 =_endDate.getTime()-_startDate.getTime();
					    float value=(value1/value2)*100;
					    if(p0835<value) {
                            image_name="yellow";
                        }
					}
				}
			}else{
				//逾期未完成的任务以红灯显示,按期完成的红灯显示
				if(_endDate!=null&&_finishDate.getTime()>_endDate.getTime())  //红灯  
				{
					image_name="red";
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return image_name;
	}
	
	
	/**
	 * 获得甘特图的数据
	 * @return
	 */
	private void getGanttJson(HashMap map,String startDate,String endDate)
	{
		if(startDate==null) {
            startDate="";
        }
		if(endDate==null) {
            endDate="";
        }
		int p0727=p07_vo.getInt("p0727");  //计划的年份
		int p0729=p07_vo.getInt("p0729");  //计划的月份
		int from=0;
		int end=0;
		
		if(startDate.length()>0 || endDate.length()>0){
	        if(p07_vo.getInt("p0725")==1) //年度计划
	        {
	            from=1;
	            end=12;
	            if(startDate.length()>0)
	            {
                    String[] temps1=startDate.split("-");
                    if(Integer.parseInt(temps1[0])<p0727) //起始时间小于计划年份
                    {
                        from=1;
                    } else if(Integer.parseInt(temps1[0])==p0727) //起始时间等于计划年份
                    {
                        from=Integer.parseInt(temps1[1]);
                    } else if(Integer.parseInt(temps1[0])>p0727) //起始时间大于计划年份
                    {
                        from=13;
                    }
	            }
	            if(endDate.length()>0)
	            {
                    String[] temps2=endDate.split("-"); 
                    if(Integer.parseInt(temps2[0])<p0727) //结束时间小于计划年份
                    {
                        end=0;
                    } else if(Integer.parseInt(temps2[0])==p0727) //结束时间等于计划年份
                    {
                        end=Integer.parseInt(temps2[1]);
                    } else if(Integer.parseInt(temps2[0])>p0727) //结束时间大于计划年份
                    {
                        end=12;
                    }
	            } 
	        }
	        else  if(p07_vo.getInt("p0725")==Integer.parseInt(WorkPlanConstant.Cycle.HALFYEAR)) //半年
	        {
                if (p07_vo.getInt("p0729")==1){
                    from=1;
                    end=6;
                }
                else {
                    from=7;
                    end=12;                 
                }
                if(startDate.length()>0)
                {
                    String[] temps1=startDate.split("-");
                    if(Integer.parseInt(temps1[0])<p0727) //起始时间小于计划年份
                    {
                        from=1;
                    } else if(Integer.parseInt(temps1[0])==p0727) //起始时间等于计划年份
                    {
                        from=Integer.parseInt(temps1[1]);
                    } else if(Integer.parseInt(temps1[0])>p0727) //起始时间大于计划年份
                    {
                        from=0;
                    }
                }
                if(endDate.length()>0)
                {
                    String[] temps2=endDate.split("-"); 
                    if(Integer.parseInt(temps2[0])<p0727) //结束时间小于计划年份
                    {
                        end=0;
                    } else if(Integer.parseInt(temps2[0])==p0727) //结束时间等于计划年份
                    {
                        end=Integer.parseInt(temps2[1]);
                    } else if(Integer.parseInt(temps2[0])>p0727) //结束时间大于计划年份
                    {
                        end=12;
                    }
                }  
	        }
	        else  if(p07_vo.getInt("p0725")==Integer.parseInt(WorkPlanConstant.Cycle.QUARTER)) 
	        {
	            if (p07_vo.getInt("p0729")==1){
	                from=1;
	                end=3;
	            }
	            else if (p07_vo.getInt("p0729")==2){
	                from=4;
	                end=6;                 
	            }
	            else if (p07_vo.getInt("p0729")==3){
	                from=7;
	                end=9;                 
	            }
	            else if (p07_vo.getInt("p0729")==4){
	                from=10;
	                end=12;                 
	            }
	            if(startDate.length()>0)
	            {
	                String[] temps1=startDate.split("-");
	                if(Integer.parseInt(temps1[0])<p0727) //起始时间小于计划年份
                    {
                        from=1;
                    } else if(Integer.parseInt(temps1[0])==p0727) //起始时间等于计划年份
                    {
                        from=Integer.parseInt(temps1[1]);
                    } else if(Integer.parseInt(temps1[0])>p0727) //起始时间大于计划年份
                    {
                        from=0;
                    }
	            }
	            if(endDate.length()>0)
	            {
	                String[] temps2=endDate.split("-"); 
	                if(Integer.parseInt(temps2[0])<p0727) //结束时间小于计划年份
                    {
                        end=0;
                    } else if(Integer.parseInt(temps2[0])==p0727) //结束时间等于计划年份
                    {
                        end=Integer.parseInt(temps2[1]);
                    } else if(Integer.parseInt(temps2[0])>p0727) //结束时间大于计划年份
                    {
                        end=12;
                    }
	            }  
	        }		
    		else  if(p07_vo.getInt("p0725")==Integer.parseInt(WorkPlanConstant.Cycle.MONTH)) 
    		{
    		    int weeknum=new WorkPlanUtil(this.conn,null).getWeekNum(p0727, p0729);	    
		        from=1;
		        end=weeknum;    	
    		   
		        WorkPlanUtil planUtil=new WorkPlanUtil(this.conn,null);
    		    if(startDate.length()>0)
    		    {
    		        Date date1= DateUtils.getDate(startDate, "yyyy-MM-dd");
    		        int[] weeks=planUtil.getWhichWeekInMonth(date1);
    		        int inYear=weeks[0];
    		        int inMonth=weeks[1];
    		        int inWeek=weeks[2];
    		        if ((inYear*100+inMonth)==(p0727*100+p0729)){
    		            from =inWeek;   		            
    		        }
    		        else if ((inYear*100+inMonth)>(p0727*100+p0729)){
    		            from=6;
    		        }
    		    }
    		    if(endDate.length()>0)
    		    {
                    Date date1= DateUtils.getDate(endDate, "yyyy-MM-dd");
                    int[] weeks=planUtil.getWhichWeekInMonth(date1);
                    int inYear=weeks[0];
                    int inMonth=weeks[1];
                    int inWeek=weeks[2];
                    if ((inYear*100+inMonth)==(p0727*100+p0729)){
                        end =inWeek;                       
                    }              
                    else if ((inYear*100+inMonth)<(p0727*100+p0729)){
                        end=0;
                    }
    		    }  
    		}
    		else  if(p07_vo.getInt("p0725")==Integer.parseInt(WorkPlanConstant.Cycle.WEEK)) 
            {
    		    from=1;
                end=7; 
                WorkPlanUtil planUtil=new WorkPlanUtil(this.conn,null);
                String[] summaryDates = planUtil.getBeginEndDates(WorkPlanConstant.Cycle.WEEK,
                        String.valueOf(this.p07_vo.getInt("p0727")), 
                        String.valueOf(this.p07_vo.getInt("p0729")), this.p07_vo.getInt("p0731"));
                String firstday = summaryDates[0];
                Date firstDay1=DateUtils.getDate(firstday, "yyyy-MM-dd");
                
                if(startDate.length()>0)
                {
                    String[] temps1=startDate.split("-");
                    Date startDate1= DateUtils.getDate(startDate, "yyyy-MM-dd");
                    if (startDate1.before(firstDay1)){//在第一天之前
                        from=1; 
                    }
                    else {
                        from=8;
                        Calendar calendar =DateUtils.getCalendar(firstDay1);
                        for (int i=1;i<=7;i++){                
                            int year=DateUtils.getYear(calendar.getTime()); 
                            int month=DateUtils.getMonth(calendar.getTime()); 
                            int day=calendar.get(Calendar.DATE);
                            if ((Integer.parseInt(temps1[0])==year)
                                    &&(Integer.parseInt(temps1[1])==month)
                                    &&(Integer.parseInt(temps1[2])==day)){
                                from=i;    
                                break;
                            }
                            calendar.add(Calendar.DATE, 1);
                        }  
                    }
                }
                if(endDate.length()>0)
                {
                    String[] temps1=endDate.split("-");
                    Date endDate1= DateUtils.getDate(endDate, "yyyy-MM-dd");
                    if (endDate1.before(firstDay1)){//在第一天之前
                        end=0; 
                    }
                    else {
                        end=7;
                        Calendar calendar =DateUtils.getCalendar(firstDay1);
                        for (int i=1;i<=7;i++){                
                            int year=DateUtils.getYear(calendar.getTime()); 
                            int month=DateUtils.getMonth(calendar.getTime()); 
                            int day=calendar.get(Calendar.DATE);
                            if ((Integer.parseInt(temps1[0])==year)
                                    &&(Integer.parseInt(temps1[1])==month)
                                    &&(Integer.parseInt(temps1[2])==day)){
                                end=i;    
                                break;
                            }
                            calendar.add(Calendar.DATE, 1);
                        }  
                    }
                }  
           
            }
		}
		getGanttJsonDesc(map,from,end);
	}
	
	/**
	 * @author lis
	 * @Description: 甘特图
	 * @date 2016-3-10
	 * @param map
	 * @param from_index
	 * @param end_index
	 */
	private void getGanttJsonDesc(HashMap map,int from_index,int end_index)
	{
		StringBuffer _json=new StringBuffer("");
		int start=1,end=12; 
		if(this.p07_vo.getInt("p0725")==2) //半年计划
		{
			if(this.p07_vo.getInt("p0729")==1)
			{
				start=1;
				end=6;
			}
			else if(this.p07_vo.getInt("p0729")==2)
			{
				start=7;
				end=12;
			}  
		}
		else if(this.p07_vo.getInt("p0725")==3) //季度计划
		{
			if(this.p07_vo.getInt("p0729")==1)
			{
				start=1; end=3;
			}
			else if(this.p07_vo.getInt("p0729")==2)
			{
				start=4; end=6;
			}
			else if(this.p07_vo.getInt("p0729")==3)
			{
				start=7; end=9;
			}  
			else if(this.p07_vo.getInt("p0729")==4)
			{
				start=10; end=12;
			}  
		}
		else if(this.p07_vo.getInt("p0725")==4) //月份计划
		{
			start=1; end=5;
		}
		else if(this.p07_vo.getInt("p0725")==5) //周计划
		{
			start=1; end=7;
		}
		for(int i=start;i<=end;i++)
		{
			if(i<from_index||i>end_index) {
                map.put(getNumberToEn(i), 0);
            } else {
                map.put(getNumberToEn(i), 1);
            }
		} 
	}
	
	
	private String getNumberToEn(int i)
	{
		String str="";
		switch(i)
		{
			case 1: str="one";  break;
			case 2: str="two";  break;
			case 3: str="three";  break;
			case 4: str="four";  break;
			case 5: str="five";  break;
			case 6: str="six";  break;
			case 7: str="seven";  break;
			case 8: str="eight";  break;
			case 9: str="nine";  break;
			case 10: str="ten";  break;
			case 11: str="eleven";  break;
			case 12: str="twelve";  break;
		}
		return str;
	}
	
	
	/**
	 * 取得任务的时间安排
	 * @param dynaBean
	 * @return
	 */
	public String getTimeArrangeText(DynaBean dynaBean)
	{
		StringBuffer text=new StringBuffer("");
		String p0813=dynaBean.get("p0813")!=null?(String)dynaBean.get("p0813"):"";  //起始时间
		String p0815=dynaBean.get("p0815")!=null?(String)dynaBean.get("p0815"):"";  //结束时间
		String p0727=String.valueOf(p07_vo.getInt("p0727"));  //计划的年份
		if(p0813.length()>0&&p0815.length()>0)
		{
			String[] temps1=p0813.split("-");
			String[] temps2=p0815.split("-");
			if(!temps1[0].equalsIgnoreCase(p0727)) {
                text.append(temps1[0]+"年");
            }
			text.append(temps1[1]+"月"+temps1[2]+"日"); 
			text.append(" 至 "); 
			if(!temps2[0].equalsIgnoreCase(p0727)) {
                text.append(temps2[0]+"年");
            }
			text.append(temps2[1]+"月"+temps2[2]+"日"); 
		}
		else if(p0813.length()>0)
		{
			text.append("自 ");
			String[] temps1=p0813.split("-"); 
			if(!temps1[0].equalsIgnoreCase(p0727)) {
                text.append(temps1[0]+"年");
            }
			text.append(temps1[1]+"月"+temps1[2]+"日  开始");  
		}
		else if(p0815.length()>0)
		{
			text.append("截止 ");
			String[] temps1=p0815.split("-"); 
			if(!temps1[0].equalsIgnoreCase(p0727)) {
                text.append(temps1[0]+"年");
            }
			text.append(temps1[1]+"月"+temps1[2]+"日");  
		}
		return text.toString();
	}



	/**
	 * 获得相关人员（ object_id）任务在映射表中的排序号
	 * @param object_id 
	 * @param parent_p0800  父任务id  
	 * @param p0723 1:人员计划  2：团队计划
	 * @return
	 */
	public int getSeq(String object_id,int parent_p0800,int p0723)
	{
		int seq=1;
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn); 
			String sql_str=getSeqlSql(object_id, 0,p0723);
			ArrayList dataList=dao.searchDynaList(sql_str); 
			HashMap p0800Map=new HashMap();
			DynaBean dynaBean=null;
			for(int i=0;i<dataList.size();i++)
			{
				dynaBean=(DynaBean)dataList.get(i);
				p0800Map.put((String)dynaBean.get("p0800"), (String)dynaBean.get("seq"));
			} 
			
			if(parent_p0800==0||p0800Map.get(String.valueOf(parent_p0800))==null)
			{
				int max_seq=0;
				for(int i=0;i<dataList.size();i++)
				{
					dynaBean=(DynaBean)dataList.get(i);
					String p0800=(String)dynaBean.get("p0800");
					String p0831=(String)dynaBean.get("p0831");
					int temp_seq=Integer.parseInt((String)dynaBean.get("seq"));
					if(p0800.equals(p0831)||p0800Map.get(p0831)==null)
					{
						if(temp_seq>max_seq) {
                            max_seq=temp_seq;
                        }
					}
				}
				seq=max_seq+1;
			}
			else
			{
			    sql_str=getSeqlSql(object_id, parent_p0800,p0723);
				rowSet=dao.search(sql_str);
				if(rowSet.next())
				{
					if(rowSet.getInt(1)!=0) {
                        seq=rowSet.getInt(1)+1;
                    }
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
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception ee)
			{
				rowSet=null;
			}
		}
		return seq;
	}
	
	


    /**   
     * @Title: resetChildTaskSeq   
     * @Description: 如果删除任务后，子任务不删除，则子任务会上移一级，有新的父任务，子任务的顺序会从最大往后排   
     * @param @param childId 子任务
     * @param @param parentId 新父任务 没有父任务传0
     * @param @return 
     * @author:wangrd   
     * @throws   
    */
	/*
    public void resetChildTaskSeq(int childId, int parentId)
    {
        try
        {
            ContentDAO dao = new ContentDAO(this.conn); 
            String  strsql="select * from per_task_map where P0800 = " +String.valueOf(childId); 
            RowSet rset=dao.search(strsql);                
            while (rset.next()){
                String id=rset.getString("id");
                String _nbase=rset.getString("nbase");
                String _a0100=rset.getString("a0100"); 
                String org_id=rset.getString("org_id"); 
                int seq=rset.getInt("seq"); 
                
                if (_nbase!=null && _nbase.length()>0){
                    seq =getSeq(_nbase+_a0100,parentId,1); 
                }
                else {
                    seq =getSeq(org_id,parentId,2); 
                }
                strsql="update per_task_map set seq =" +String.valueOf(seq)+" where id="+id;
                dao.update(strsql);
            }
           
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } 
        finally
        {
            
        }
       
    }
    
*/
    /**   
     * @Title: resetChildTaskSeq   
     * @Description: 如果删除任务后，子任务不删除，则子任务会上移一级，有新的父任务，子任务的顺序会从最大往后排   
     * @param @param delId 要删除的子任务
     * @param @param parentId 新父任务 没有父任务传0
     * @param @return 
     * @author:wangrd   
     * @throws   
    */
    public void resetChildTaskSeq(int delId, int parentId)
    {
        try
        {  
            if (delId==parentId) {
                parentId=0;
            }
            ContentDAO dao = new ContentDAO(this.conn); 
            String  strsql="select * from per_task_map where P0800 in " +
            		"(select p0800 from p08 where p0831 = " +String.valueOf(delId)
            		+" and p0800 <> "+String.valueOf(delId)+")"
            		+" order by  nbase,A0100,org_id,seq"; 
            RowSet rP08Set=dao.search(strsql);                
            while (rP08Set.next()){
                int childId=rP08Set.getInt("p0800");
                strsql="select * from per_task_map where P0800 = " 
                    +String.valueOf(childId); 
                RowSet rset=dao.search(strsql);
                while (rset.next()){
                    String id=rset.getString("id");
                    String _nbase=rset.getString("nbase");
                    String _a0100=rset.getString("a0100"); 
                    String org_id=rset.getString("org_id"); 
                    int seq=rset.getInt("seq"); 
                    
                    if (_nbase!=null && _nbase.length()>0){
                        seq =getSeq(_nbase+_a0100,parentId,1); 
                    }
                    else {
                        seq =getSeq(org_id,parentId,2); 
                    }
                    strsql="update per_task_map set seq =" +String.valueOf(seq)+" where id="+id;
                    dao.update(strsql);
                    
                }
            }
           
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } 
        finally
        {
            
        }
       
    }
     
	

    /**   
     * @Title: getSeqlSql   
     * @Description: 获取最大序号用到的sql   
     * @param @param p0831 为空 标识取所有记录
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getSeqlSql(String object_id,int p0831,int p0723)
    {
        StringBuffer sql_str=new StringBuffer();
        try
        {
            if (p0831>0){//查二级任务
                sql_str.append("select max(ptm.seq)  ");
            }
            else {
                sql_str.append("select p08.*,"+Sql_switcher.isnull("ptm.seq","0")+" as seq ");
            }
            sql_str.append(" from p08,per_task_map ptm,p07");   
            sql_str.append(" where ptm.p0700=p07.p0700 and ptm.p0800=p08.p0800  and p07.p0723 in (1,2) ");
            if(p0723==2)  {
                sql_str.append(" and ptm.org_id='"+object_id+"'");   
                //指派到部门wusy
                sql_str.append(" and (ptm.flag=5  or ((ptm.flag=1 or ptm.flag=2) and dispatchFlag=1))" );
            }
            else {  
                String nbase=object_id.substring(0,3);
                String a0100=object_id.substring(3);
                sql_str.append(" and ptm.nbase='"+nbase+"' and ptm.a0100='"+a0100+"' ");  
            }            
  
            /* 同期计划 */
            sql_str.append(" and p07.p0725="+this.p07_vo.getInt("p0725")+" and p07.p0727="+this.p07_vo.getInt("p0727"));  
            if(this.p07_vo.getInt("p0725")==2||this.p07_vo.getInt("p0725")==3||this.p07_vo.getInt("p0725")==4||this.p07_vo.getInt("p0725")==5) {
                sql_str.append(" and p07.p0729="+this.p07_vo.getInt("p0729"));
            }
            if(this.p07_vo.getInt("p0725")==5) {
                sql_str.append(" and p07.p0731="+this.p07_vo.getInt("p0731"));
            }
            //
            if (p0831>0){
                sql_str.append(" and  p08.p0831="+p0831);
            }
            else {
                sql_str.append(" order by ptm.seq");
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } 
        return sql_str.toString();
    }
    
	/**
	 * 获得计划下的任务数据
	 * @param headList
	 * @author dengcan
	 * * @param p0800 获取某一任务，为空值表示取所有任务
	 * @serialData 2014-07-12
	 * @return
	 */
	public ArrayList getTableData(String p0800)
	{
		ArrayList dataList=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql_str=getTableDatasql(p0800);			
			dataList=dao.searchDynaList(sql_str.toString()); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		return dataList;
	}
	
	/**
	 * 异步加载获得计划下的任务数据
	 * @param headList
	 * @author lis
	 * * @param p0800 获取某一任务，为空值表示取所有任务
	 * @serialData 2016-03-5
	 * @return
	 */
	private ArrayList getTableDataAsync(String p0800)
	{
		ArrayList dataList=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql_str = getTableDatasqlAsync(p0800);			
			dataList=dao.searchDynaList(sql_str.toString()); 
						
			if(StringUtils.isNotBlank(p0800)){//上级可以同时看到穿透任务和自己的任务
				sql_str = getTableDatasqlOther(p0800);//穿透任务
				ArrayList otherTaskDataList = dao.searchDynaList(sql_str.toString()); 
				dataList.addAll(otherTaskDataList);
			}
			
			WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
			if(util.isOpenCooperationTask()){// 启用协办任务
				ArrayList newDatalist = new ArrayList();
				
				// 过滤掉协办任务中（发起人不是自己）并且（协办任务状态不是已批准）的任务
				for(int i=0; i<dataList.size(); i++) {
					DynaBean bean = (DynaBean)dataList.get(i);
					String _p0800 = (String)bean.get("p0800");
					if(util.isCooperationTask(Integer.parseInt(_p0800), false)){//是协办任务
						String startPerson = util.getTaskRolePersionList(Integer.parseInt(_p0800), 5).get(0);//任务发起人
						String startPersonNbs = startPerson.substring(0, 3);
						String startPersonA0100 = startPerson.substring(3);
						String planStartPersonNbs = "";
						String planStartPersonA0100 = "";
						if(this.p07_vo.getInt("p0723") == 1){// 个人计划
							planStartPersonNbs = this.p07_vo.getString("nbase");
							planStartPersonA0100 = this.p07_vo.getString("a0100");
						} else {// 团队计划
							String org_id = this.p07_vo.getString("p0707");
							if(!StringUtils.isEmpty(org_id)){
								String leader = util.getFirstDeptLeaders(org_id);
								if(!StringUtils.isEmpty(leader)){
									planStartPersonNbs = leader.substring(0, 3);
									planStartPersonA0100 = leader.substring(3);
								}
							}
						}
						if(!startPersonNbs.equalsIgnoreCase(planStartPersonNbs) || !startPersonA0100.equalsIgnoreCase(planStartPersonA0100)){// 发起人不是自己
							
							WorkPlanBo workPlanBo = new WorkPlanBo(this.conn, this.userView);
							String status = workPlanBo.getCooperationTaskStatus(Integer.parseInt(_p0800));
							if(!"02".equals(status)){// 协办任务状态不是已批准
								continue;
							}
						}
					}
					newDatalist.add(bean);
				}
				
				dataList = newDatalist;
			}
			
			// 过滤掉，父任务中负责人都不是自己的穿透任务 chent 20160927 start
			ArrayList new2Datalist = new ArrayList();
			for(int i=0; i<dataList.size(); i++) {
				DynaBean bean = (DynaBean)dataList.get(i);
				String _p0800 = (String)bean.get("p0800");
				String othertask = (String)bean.get("othertask");
				
                if(p07_vo.getInt("p0723") == 1)  {//个人计划
                	this.nbase = this.p07_vo.getString("nbase");// 当前计划人
                	this.a0100 = this.p07_vo.getString("a0100");
                }else if(p07_vo.getInt("p0723") == 2) {//部门计划
                	String leader = util.getFirstDeptLeaders(this.p07_vo.getString("p0707"));
                	if(!StringUtils.isEmpty(leader)){
                		this.nbase = leader.substring(0, 3);
                		this.a0100 = leader.substring(3);
					}
                }
				if("1".equals(othertask) && !this.inParentTaskHaveManageByMe(_p0800)){//穿透任务 且 父任务中没有自己负责的任务
					continue ;
				}
				new2Datalist.add(bean);
			}
			dataList = new2Datalist;
			// 过滤掉，父任务中负责人都不是自己的穿透任务 chent 20160927 end
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} 
		return dataList;
	}
	
	/**   
	 * @Title: getTableDatasql   
	 * @Description:返回取任务数据sql    
	 * @param @param p0800
	 * @param @return 
	 * @return String 
	 * @author:wangrd   
	 * @throws   
	*/
	public String getTableDatasql(String p0800)
	    {
	        StringBuffer sql_str=new StringBuffer();
	        try
	        {
	            sql_str.append("select p08.*,ptm.rank as rank ");
	            sql_str.append(" from p08,per_task_map ptm,p07");   
	            sql_str.append(" where ptm.p0700=p07.p0700 and ptm.p0800=p08.p0800  and p07.p0723 in (1,2) ");
	            if (p07_vo==null){
	                sql_str.append(" and  p08.p0800=0");
	            }
	            else {
	                if(p07_vo.getInt("p0723")==2)  {//查看部门任务
	                    sql_str.append(" and ptm.org_id='"+this.p07_vo.getString("p0707")+"'"); 
	                    //查看部门计划时 ,加上指派给部门的任务    wusy          
	                    sql_str.append(" and (ptm.flag=5  or ((ptm.flag=1 or ptm.flag=2) and dispatchFlag=1 and  p08.p0811 in ('02','03')))" );
	                    
	                }
	                else {//查看个人任务：我创建、负责、参与的个人计划任务，我负责、参与的部门计划任务      
	                    sql_str.append(" and ptm.nbase='"+this.p07_vo.getString("nbase")+"' and ptm.a0100='"+this.p07_vo.getString("a0100")+"' ");    
	                    sql_str.append("  ");
	                    sql_str.append(" and (( p07.p0723=1 and (( ptm.flag<>5 and p08.p0811 in ('02','03')) or ptm.flag=5)");
	                    sql_str.append(" ) or ( ");
	                    sql_str.append("p07.p0723=2 and ( ptm.flag<>5 and  p08.p0811 in ('02','03'))");
	                    sql_str.append("))");
	                    
	                }
	                /* 同期计划 */
	                sql_str.append(" and p07.p0725="+this.p07_vo.getInt("p0725")+" and p07.p0727="+this.p07_vo.getInt("p0727"));  
	                if(this.p07_vo.getInt("p0725")==2||this.p07_vo.getInt("p0725")==3||this.p07_vo.getInt("p0725")==4||this.p07_vo.getInt("p0725")==5) {
                        sql_str.append(" and p07.p0729="+this.p07_vo.getInt("p0729"));
                    }
	                if(this.p07_vo.getInt("p0725")==5) {
                        sql_str.append(" and p07.p0731="+this.p07_vo.getInt("p0731"));
                    }
	                if (p0800!=null && p0800.length()>0){//只查看某一条任务记录时，新增任务时使用
	                    sql_str.append(" and  p08.p0800="+p0800);
	                }
	                
	            }
	            sql_str.append(" order by ptm.seq");
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        } 
	        return sql_str.toString();
	    }
	
    
	/**
	 * @author lis
	 * @Description: 获取任务数据sql
	 * @date 2016-3-17
	 * @param p0800
	 * @return String
	 */
	public String getTableDatasqlAsync(String p0800)
    {
        StringBuffer sql_str=new StringBuffer();
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try
        {
        	String sql_where = this.getSqlWhere(p0800);
        	StringBuffer sql = new StringBuffer();
        	String sql_from = " from p08,per_task_map ptm,p07"; 
        	
        	// sql性能优化，子查询改为拼接字符串 chent 20160628 start
        	String notInStr = "";
        	String notInSql =  " select p08.p0800 "+sql_from+" where "+sql_where;
            rs = dao.search(notInSql);
            while(rs.next()) {
            	String p0800Str = rs.getString("p0800");
            	notInStr +=  ("'" + p0800Str + "',");
            }
        	if(!StringUtils.isEmpty(notInStr)){
        		int len = notInStr.length();
        		notInStr = notInStr.substring(0, len-1);
        	}else {
        		notInStr = "''";
        	}
        	// sql性能优化，子查询改为拼接字符串 chent 20160628 end
        	
            sql.append(sql_from);
            sql.append(" where" + sql_where);
            sql.append(" and (p08.P0800=p08.P0831 ");
            sql.append(" or");
            sql.append(" p08.p0831 not in(");
            sql.append(notInStr);
            sql.append(" )");
            sql.append(" )");
            
//            sql_str.append("select * from (");
            sql_str.append("select ptm.seq,0 as othertask,p08.*,ptm.rank as rank ");
            sql_str.append(sql);
            sql_str.append(" order by ptm.seq");
            
          //union很慢，导致任务加载不出来 chent delete 20160608 start
//            sql_str.append(" ) t1");
//            sql_str.append(" left join");
//            sql_str.append(" (");
//            sql_str.append(" select p08.P0831,COUNT(p08.P0831) countP0831");
//            sql_str.append(sql_from);
//            sql_str.append(" where " + sql_where);
//            sql_str.append(" and p08.p0800<>p08.p0831 and p0831 in(");
//            sql_str.append(" select p08.p0800");
//            sql_str.append(sql);
//            sql_str.append(" ) group by p08.P0831 ");
/*            sql_str.append(" union ");//联合查询
            sql_str.append(" select p08.P0831,COUNT(p08.P0831) countP0831");
            sql_str.append(" from p08,p09,per_task_map ptm");
            sql_str.append(" where p09.P0903=p08.p0800 and p09.Nbase=ptm.Nbase and p09.a0100=ptm.a0100");
            sql_str.append(" and p09.p0905=1 and ptm.p0800=p08.p0800  and p08.p0811 in ('02','03')");
            sql_str.append(" and p08.p0800<>p08.p0831 and p0831 in(");
            sql_str.append(" select p08.p0800");
            sql_str.append(sql);
            sql_str.append(" ) group by p08.P0831");
            sql_str.append(" ) t2");*/
            //sql_str.append(" on t1.P0800=t2.p0831 ");
//            sql_str.append(" order by t1.seq");
          //union很慢，导致任务加载不出来 chent delete 20160608 end
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } finally {
        	PubFunc.closeDbObj(rs);
        }
        return sql_str.toString();
    }
	
	/**
	 * @author lis
	 * @Description: 获得当前任务节点数据sql的where条件
	 * @date 2016-3-11
	 * @param p0800 任务id
	 * @return String
	 * @throws GeneralException
	 */
	private String getSqlWhere(String p0800) throws GeneralException{
		 StringBuffer sql_where=new StringBuffer();
		try {
			sql_where.append(" ptm.p0700=p07.p0700 and ptm.p0800=p08.p0800  and p07.p0723 in (1,2) ");
            if (p07_vo==null){
            	sql_where.append(" and  p08.p0800=0");
            }
            else {
                if(p07_vo.getInt("p0723")==2)  {//查看部门任务
                	sql_where.append(" and ptm.org_id='"+this.p07_vo.getString("p0707")+"'"); 
                    //查看部门计划时 ,加上指派给部门的任务    wusy          
                	sql_where.append(" and (ptm.flag=5  or ((ptm.flag=1 or ptm.flag=2) and dispatchFlag=1 and  p08.p0811 in ('02','03')))" );
                    
                } else {//查看个人任务：我创建、负责、参与的个人计划任务，我负责、参与的部门计划任务      
                	sql_where.append(" and ptm.nbase='"+this.p07_vo.getString("nbase")+"' and ptm.a0100='"+this.p07_vo.getString("a0100")+"' ");    
                	sql_where.append(" and (( p07.p0723=1 and (( ptm.flag<>5 and p08.p0811 in ('02','03')) or ptm.flag=5)");
                	sql_where.append(" ) or ( ");
                	sql_where.append("p07.p0723=2 and ( ptm.flag<>5 and  p08.p0811 in ('02','03'))"); 
                	sql_where.append("))");
                	// 协办任务：不是协办任务直接显示;是协办任务：如本人是发起人，则显示；如是协办人，则协办状态应是已批状态 chent add 20160608 start
                	WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
                	if(util.isOpenCooperationTask()){//启用协作任务
                		sql_where.append(" and (");
                		sql_where.append(" "+Sql_switcher.isnull("p0845", "0")+"<>1 ");//非协办任务
                		sql_where.append(" or (p0845=1 and ptm.flag=1 and p08.p0800 in (select p10.p0800 from p10 where P1019='02')) ");//协办人、已批状态
                		sql_where.append(" or (p0845=1 and ptm.flag=5) ");//发起人
                		sql_where.append(")");
                	}
                	// 协办任务：不是协办任务直接显示;是协办任务：如本人是发起人，则显示；如是协办人，则协办状态应是已批状态 chent add 20160608 end
                }
                /* 同期计划 */
                sql_where.append(" and p07.p0725="+this.p07_vo.getInt("p0725")+" and p07.p0727="+this.p07_vo.getInt("p0727"));  
                if(this.p07_vo.getInt("p0725")==2||this.p07_vo.getInt("p0725")==3||this.p07_vo.getInt("p0725")==4||this.p07_vo.getInt("p0725")==5) {
                    sql_where.append(" and p07.p0729="+this.p07_vo.getInt("p0729"));
                }
                if(this.p07_vo.getInt("p0725")==5) {
                    sql_where.append(" and p07.p0731="+this.p07_vo.getInt("p0731"));
                }
                if (p0800!=null && p0800.length()>0){//查看某一条任务记录的子任务
                	sql_where.append(" and p08.p0800<>p08.p0831 and  p08.p0831="+p0800);
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return sql_where.toString();
	}
	
	/**   
	 * @Title: getTableDatasql   
	 * @Description:返回取任务数据sql    
	 * @param @param p0800
	 * @param @return 
	 * @return String 
	 * @author:wangrd   
	 * @throws   
	*/
	public String getTableDatasqlOther(String p0800)
    {
        StringBuffer sql_str=new StringBuffer();
        StringBuffer sql = new StringBuffer();
        try
        {
        	sql.append(" from p08,p09,per_task_map ptm ");
        	sql.append(" where p09.P0903=p08.p0800 and p09.Nbase=ptm.Nbase and p09.a0100=ptm.a0100 ");
        	sql.append(" and p09.p0905=1 and ptm.p0800=p08.p0800  and p08.p0811 in ('02','03')");
        	if (p0800!=null && p0800.length()>0){//只查看某一条任务记录的子任务
        		sql.append(" and p08.p0800<>p08.p0831 and  p08.p0831="+p0800);
        	}
        	
        	//sql_str.append("select * from (");
            sql_str.append("select ptm.seq,1 as othertask,p08.*,ptm.rank as rank ");
            
            sql_str.append(sql);
            //sql_str.append(" ) t1");
/*            sql_str.append(" left join");
            sql_str.append(" (");
            sql_str.append(" select p08.P0831 p0831s,COUNT(p08.P0831) countP0831 from P08 where  p0831 in(");
            sql_str.append(" select p08.p0800");
            sql_str.append(sql);
            sql_str.append(" ) and p08.p0811 in ('02','03') group by p08.p0831) t2");
            sql_str.append(" on t1.P0800=t2.p0831s ");*/
            sql_str.append(" order by ptm.seq");//haosl 20170410 update 穿透任务跟随创建的任务排序方式显示
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } 
        return sql_str.toString();
    }
	/**
	 * 获得EXT表格的列描述数组
	 * @param headList
	 * @author dengcan
	 * @serialData 2014-07-12
	 * @return
	 */
	private String getPanelColumns(ArrayList headList,int showType,String object_id,int p0723)
	{
		// 栏目设置是否有保存过的私有记录、公有记录
		String submoduleid = SUBMODULEID_PLAN_DESIGN;
		if(showType==2){
			submoduleid = SUBMODULEID_PLAN_TRACE;
		}
		String column_str = "";
		//是否公有方案
		WorkPlanUtil ut = new WorkPlanUtil(conn, userView);
		int role = -1;
		if(p07_vo!=null){
			role = ut.getLoaderRole(p07_vo.getInt("p0700"));
		}
		if (hasPrivateScheme(submoduleid,this.userView.getUserName())){
			column_str = getPanelColumnsSetting(headList, showType, object_id, p0723, "1", role);
		}else if (hasShareScheme(submoduleid))
		{
			column_str = getPanelColumnsSetting(headList, showType, object_id, p0723, "2", role);
		}else{
			column_str = getPanelColumnsDefalt(headList, showType, object_id, p0723,role);
		}
		
		return column_str;
		
		
	}
	
	
	/**
	 * 获得甘特图columns信息
	 * @return
	 */
	private String getGantColumns(String lock_str)
	{
		StringBuffer _str=new StringBuffer("");
		if(this.P0725==1) //年计划
		{
		    /*
			_str.append(",{ text:'1季度',columns:[{text:'1月',width: 35,renderer:drawGrid_left_right,align:'center', dataIndex: 'one',menuDisabled:true},{text:'2月',align:'center',width: 35,renderer:drawGrid_right, dataIndex: 'two',menuDisabled:true},{text:'3月',align:'center',width: 35,renderer:drawGrid_right, dataIndex: 'three',menuDisabled:true} ]}");
			_str.append(",{ text:'2季度',columns:[{text:'4月',width: 35,renderer:drawGrid_right,align:'center', dataIndex: 'four',menuDisabled:true},{text:'5月',align:'center',width: 35,renderer:drawGrid_right, dataIndex: 'five',menuDisabled:true},{text:'6月',align:'center',width: 35,renderer:drawGrid_right, dataIndex: 'six',menuDisabled:true} ]}");
			_str.append(",{ text:'3季度',columns:[{text:'7月',width: 35,renderer:drawGrid_right,align:'center', dataIndex: 'seven',menuDisabled:true},{text:'8月',align:'center',width: 35,renderer:drawGrid_right, dataIndex: 'eight',menuDisabled:true},{text:'9月',align:'center',width: 35,renderer:drawGrid_right, dataIndex: 'nine',menuDisabled:true} ]}");
			_str.append(",{ text:'4季度',columns:[{text:'10月',width: 35,renderer:drawGrid_right,align:'center', dataIndex: 'ten',menuDisabled:true},{text:'11月',align:'center',width: 35,renderer:drawGrid_right, dataIndex: 'eleven',menuDisabled:true},{text:'12月',align:'center',width: 35,renderer:drawGrid_right, dataIndex: 'twelve',menuDisabled:true} ]}");
			*/
		    String width="30";
		    String width2="50";//30->50 ie9下30不够显示会出现省略号  haosl update 2018-2-7
		    if (this.P0723==2 && showType==2){
		        width="24";
		        width2="50";
		        
		    }
		        
			_str.append(",{ text:'1季度'"+lock_str+",draggable:false,columns:[{text:'1',width: "+width+",renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'one',menuDisabled:true},{text:'2',align:'center',width: "+width+",renderer:drawGrid_right, draggable:false,dataIndex: 'two',menuDisabled:true},{text:'3',align:'center',width: "+width+",renderer:drawGrid_right, draggable:false,dataIndex: 'three',menuDisabled:true} ],menuDisabled:true}");
			_str.append(",{ text:'2季度'"+lock_str+",draggable:false,columns:[{text:'4',width: "+width+",renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'four',menuDisabled:true},{text:'5',align:'center',width: "+width+",renderer:drawGrid_right, draggable:false,dataIndex: 'five',menuDisabled:true},{text:'6',align:'center',width: "+width+",renderer:drawGrid_right, draggable:false,dataIndex: 'six',menuDisabled:true} ],menuDisabled:true}");
			_str.append(",{ text:'3季度'"+lock_str+",draggable:false,columns:[{text:'7',width: "+width+",renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'seven',menuDisabled:true},{text:'8',align:'center',width: "+width+",renderer:drawGrid_right, draggable:false,dataIndex: 'eight',menuDisabled:true},{text:'9',align:'center',width: "+width+",renderer:drawGrid_right, draggable:false,dataIndex: 'nine',menuDisabled:true} ],menuDisabled:true}");
			_str.append(",{ text:'4季度'"+lock_str+",draggable:false,columns:[{text:'10',width: "+width2+",renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'ten',menuDisabled:true},{text:'11',align:'center',width: "+width2+",renderer:drawGrid_right, draggable:false,dataIndex: 'eleven',menuDisabled:true},{text:'12',align:'center',width: "+width2+",renderer:drawGrid_right, draggable:false,dataIndex: 'twelve',menuDisabled:true} ],menuDisabled:true}");
		}
		else if(this.P0725==2) //半年计划
		{
			if(this.P0729==1) //上半年
			{
			    _str.append(",{ text:'1季度'"+lock_str+",draggable:false,columns:[{text:'1',width: 50,renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'one',menuDisabled:true},{text:'2',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'two',menuDisabled:true},{text:'3',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'three',menuDisabled:true} ],menuDisabled:true}");
	            _str.append(",{ text:'2季度'"+lock_str+",draggable:false,columns:[{text:'4',width: 50,renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'four',menuDisabled:true},{text:'5',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'five',menuDisabled:true},{text:'6',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'six',menuDisabled:true} ],menuDisabled:true}");
			}
			else if(this.P0729==2) //下半年
			{
	            _str.append(",{ text:'3季度'"+lock_str+",draggable:false,columns:[{text:'7',width: 50,renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'seven',menuDisabled:true},{text:'8',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'eight',menuDisabled:true},{text:'9',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'nine',menuDisabled:true} ],menuDisabled:true}");
	            _str.append(",{ text:'4季度'"+lock_str+",draggable:false,columns:[{text:'10',width: 50,renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'ten',menuDisabled:true},{text:'11',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'eleven',menuDisabled:true},{text:'12',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'twelve',menuDisabled:true} ],menuDisabled:true}");
			}
		}
		else if(this.P0725==3) //季度计划
		{ 
            if(this.P0729==1) //一季度
            {
                _str.append(",{ text:'1季度'"+lock_str+",draggable:false,columns:[{text:'1',width: 50,renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'one',menuDisabled:true},{text:'2',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'two',menuDisabled:true},{text:'3',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'three',menuDisabled:true} ],menuDisabled:true}");
            }
            else if(this.P0729==2) //二季度
            {
                _str.append(",{ text:'2季度'"+lock_str+",draggable:false,columns:[{text:'4',width: 50,renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'four',menuDisabled:true},{text:'5',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'five',menuDisabled:true},{text:'6',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'six',menuDisabled:true} ],menuDisabled:true}");
            }	
            else if(this.P0729==3) //三季度
            {
                _str.append(",{ text:'3季度'"+lock_str+",draggable:false,columns:[{text:'7',width: 50,renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'seven',menuDisabled:true},{text:'8',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'eight',menuDisabled:true},{text:'9',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'nine',menuDisabled:true} ],menuDisabled:true}");
            }	
            else if(this.P0729==4) //四季度
            {
                _str.append(",{ text:'4季度'"+lock_str+",draggable:false,columns:[{text:'10',width: 50,renderer:drawGrid_right,draggable:false,align:'center', dataIndex: 'ten',menuDisabled:true},{text:'11',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'eleven',menuDisabled:true},{text:'12',align:'center',width: 50,renderer:drawGrid_right, draggable:false,dataIndex: 'twelve',menuDisabled:true} ],menuDisabled:true}");
            }	
		}
		else if(this.P0725==4) //月计划
		{ 
		    int weekNum = new WorkPlanUtil(this.conn,null)
		            .getWeekNum(this.P0727, this.P0729); 		    
		    for (int i=1;i<=weekNum;i++){
		        String colDesc="第"+i+"周";
		        String renderer="drawGrid_right";
		        if (i==1) {
                    renderer="drawGrid_right";
                }
		        String colName=getNumberToEn(i);
		        _str.append(",{text:'"+colDesc+"',width: 50"+lock_str+",renderer:"+renderer
		                +",align:'center', dataIndex: '"+colName+"',sortable: false,draggable:false,menuDisabled:true}");
		        
		    }
		}
		else if(this.P0725==5) //周计划
		{ 
	        String[] summaryDates = new WorkPlanUtil(this.conn,null)
	                .getBeginEndDates(WorkPlanConstant.Cycle.WEEK,
	                String.valueOf(this.P0727), 
	                String.valueOf(this.P0729), this.P0731);
	        String firstday = summaryDates[0];
	        Date date=DateUtils.getDate(firstday, "yyyy-MM-dd");
	        Calendar calendar =DateUtils.getCalendar(date);
            for (int i=1;i<=7;i++){                
                int month=DateUtils.getMonth(calendar.getTime()); 
                int day=calendar.get(Calendar.DATE);
                String colDesc=String.valueOf(month)+"."+String.valueOf(day);
                String renderer="drawGrid_right";
                String colName=getNumberToEn(i);
                if (i==1) {
                    renderer="drawGrid_right";
                }
                _str.append(",{text:'"+colDesc+"',width: 50"+lock_str+",renderer:"+renderer
                        +",align:'center', dataIndex: '"+colName+"',sortable: false,draggable:false,menuDisabled:true}");
                calendar.add(Calendar.DATE, 1);
                
            }
		}
		return _str.toString();
	}
	
	
	/**
	 * 定义EXT需要的数据结构和类型
	 * @author dengcan
	 * @serialData 2014-07-12
	 * @return
	 */
	private String getDataModel(ArrayList headList,int p0725)
	{
		
		StringBuffer _json=new StringBuffer("[{name:'p0800',type:'string'},{name:'id',type:'string'},{name:'othertask',type:'int'},"
		        +"{name:'objectid',type:'string'},{name:'p0723',type:'string'},"
		        +"{name:'p0700',type:'string'},{name:'p0833',type:'string'},{name:'p0811',type:'string'}"
		        +",{name:'p0809',type:'string'},{name:'superiorEvaluation',type:'string'}"
		        );
		FieldItem item=null;
		for(int i=0;i<headList.size();i++)
		{
			item = (FieldItem) headList.get(i);
			String item_id=item.getItemid();
			
			if("gantt".equalsIgnoreCase(item_id))
			{
				if(p0725==1) //年计划
				{
					for(int y=1;y<=12;y++) {
                        _json.append(",{name:'"+getNumberToEn(y)+"',type:'int'}");
                    }
				}
				else if(p0725==2) //半年计划
                {
				    int start=1;
				    int end=6;
				    if (this.P0729==2){
				        start=7;
				        end=12;
				    }
                    for(int y=start;y<=end;y++) {
                        _json.append(",{name:'"+getNumberToEn(y)+"',type:'int'}");
                    }
                }
				else if(p0725==3) //季度计划
                {
				    int start=1;
                    int end=3;
                    if (this.P0729==2){
                        start=4;
                        end=6;
                    }
                    else if (this.P0729==3){
                        start=7;
                        end=9;
                    }
                    else if (this.P0729==4){
                        start=10;
                        end=12;
                    }
                    for(int y=start;y<=end;y++) {
                        _json.append(",{name:'"+getNumberToEn(y)+"',type:'int'}");
                    }
                }
				else if(p0725==4) //月计划
                {
				    int weeknum =  new WorkPlanUtil(this.conn,null).getWeekNum(this.P0727, this.P0729);			        
                    for(int y=1;y<=weeknum;y++) {
                        _json.append(",{name:'"+getNumberToEn(y)+"',type:'int'}");
                    }
                }
				else if(p0725==5) //周计划
				{
				    for(int y=1;y<=7;y++) {
                        _json.append(",{name:'"+getNumberToEn(y)+"',type:'int'}");
                    }
				}
			}
			else
			{
				String type=item.getItemtype();
				if("A".equalsIgnoreCase(type)|| "M".equalsIgnoreCase(type)|| "p0835".equalsIgnoreCase(item_id)|| "rank".equalsIgnoreCase(item_id)|| "p0817".equalsIgnoreCase(item_id))  //p0835:进度  ||item_id.equalsIgnoreCase("p0835_jd")||item_id.equalsIgnoreCase("p0835_state")
                {
                    type="string";
                } else if("N".equalsIgnoreCase(type))
				{
					if(item.getDecimalwidth()==0) {
                        type="int";
                    } else {
                        type="float";
                    }
				}
				else if("D".equalsIgnoreCase(type)) {
                    type="date";
                }
				_json.append(",{name:'"+item_id.toLowerCase()+"',type:'"+type+"'}");
			}
			
		}
		_json.append("]");
		return _json.toString();
		
	}
	
	/**
	 * @author lis
	 * @Description: 获得列id集合
	 * @date 2016-3-10
	 * @param headList 列头对象
	 * @return ArrayList
	 */
	public ArrayList getColumnList(ArrayList headList)
	{
		ArrayList headFieldList = new ArrayList();
		headFieldList.add("p0800");
		// 栏目设置是否有保存过的私有记录、公有记录
		String submoduleid = SUBMODULEID_PLAN_DESIGN;
		if(showType==2){
			submoduleid = SUBMODULEID_PLAN_TRACE;
		}
		String column_str = "";
		//是否公有方案
		WorkPlanUtil ut = new WorkPlanUtil(conn, userView);
		int role = -1;
		if(p07_vo!=null){
			role = ut.getLoaderRole(p07_vo.getInt("p0700"));
		}
		if (hasPrivateScheme(submoduleid,this.userView.getUserName())){
			headFieldList = getColumnsSetting(headList, role);
		}else if (hasShareScheme(submoduleid))
		{
			headFieldList = getColumnsSetting(headList, role);
		}else{
			headFieldList = getColumnsDefalt(headList,role);
		}
		
		return headFieldList;
		
		
	}
	
	/**
	 * 获得计划任务列表表头指标 （1阶段固定表头  后期可配置）
	 * @param showType 任务列表视图类型  1：计划制定   2：计划跟踪
	 * @param p0723 1：人员计划  2：团队计划  3：项目
	 * @author dengcan
	 * @serialData 2014-07-12
	 * @return
	 */
	public ArrayList getHeadList(int showType,int p0723)
	{
		ArrayList list=new ArrayList();
		
		// 栏目设置是否有保存过的私有记录、公有记录
		String submoduleid = SUBMODULEID_PLAN_DESIGN;
		if(showType==2){
			submoduleid = SUBMODULEID_PLAN_TRACE;
		}
		//是否私有方案
		if (hasPrivateScheme(submoduleid,this.userView.getUserName())){
			list = getHeadListSetting(false,  submoduleid);
		}else if (hasShareScheme(submoduleid))
		{
			list = getHeadListSetting(true, submoduleid);	
		}else{
			list = getHeadListDefalt(showType, p0723);
		}
		/*
		if(!recordPrivate && !recordShare){// 栏目设置表中没有曾保存的记录，显示默认方案
			list = getHeadListDefalt(showType, p0723);
		}else{// 栏目设置表中有曾经保存的记录
			list = getHeadListSetting(recordPrivate, recordShare, submoduleid);
		}
		*/
		return list;
	}
	
	/**
	 * 获得FieldItem对象
	 * @param itemid 
	 * @param desc
	 * @param type N|M|A|D
	 * @param length
	 * @param decimalWidth
	 * @author dengcan
	 * @serialData 2014-07-12
	 * @return
	 */
	private FieldItem getFieldItem(String itemid,String desc,String type,String codesetid,int length,int decimalWidth,int columnWidth)
	{
		FieldItem item=new FieldItem();
		item.setItemid(itemid);
		item.setFieldsetid("oth");//其它
		item.setItemdesc(desc);
		item.setItemlength(length);
		item.setDecimalwidth(decimalWidth);	
		item.setCodesetid(codesetid);
		item.setItemtype(type);
		item.setFormula(String.valueOf(columnWidth));
		return item;
	}
	
	/**   
	 * @Title: getRowJson   
	 * @Description:只获取某一行任务记录的json格式    
	 * @param @param p0800
	 * @param @param seq
	 * @param @return 
	 * @return String 
	 * @author:wangrd   
	 * @throws   
	*/
	public String  getRowJson(String p0800,String seq,int p0723)
    {
	   setShowType(1);
       ArrayList headList=getHeadList(Integer.parseInt(WorkPlanConstant.PlanFlowStatus.DESIGN),p0723); 
       StringBuffer _json=new StringBuffer("");
       ArrayList tableDataList=getTableData(p0800); //获得计划下的任务数据
       HashMap taskMemberMap=getTaskMemberMap(tableDataList);
       HashMap scoreMap = getTaskScoreMap(tableDataList);
       DynaBean dynaBean=null;
       for(int i=0;i<tableDataList.size();i++)
       {
           dynaBean=(DynaBean)tableDataList.get(i);
               executeJsonData(_json,headList,dynaBean,seq,tableDataList,taskMemberMap,scoreMap);
   
       }

       if (",".equalsIgnoreCase(_json.substring(_json.length()-1, _json.length()))) {
           _json.setLength(_json.length()-1);
       }
       return _json.toString();
    }
	
	/**
	 * @author lis
	 * @Description: 获得行数据
	 * @date 2016-3-10
	 * @param columnList 列
	 * @param p0800 任务id
	 * @param p0723 1是个人，2是团队
	 * @return ArrayList
	 * @throws GeneralException 
	 */
	public ArrayList getRowData(ArrayList columnList,String p0800,int p0723,String showSubTask) throws GeneralException
    {
		ArrayList<CommonData> dataList = new ArrayList<CommonData>();
		try {
			HashMap map = new HashMap();
			setShowType(1);
			ArrayList<HashMap> rowData = new ArrayList<HashMap>();
			ArrayList headList=getHeadList(Integer.parseInt(WorkPlanConstant.PlanFlowStatus.DESIGN),p0723); 
			StringBuffer _json=new StringBuffer("");
			ArrayList tableDataList=getTableData(p0800); //获得计划下的任务数据
			HashMap taskMemberMap=getTaskMemberMap(tableDataList);
			HashMap scoreMap = getTaskScoreMap(tableDataList);
			DynaBean dynaBean=null;
			for(int i=0;i<tableDataList.size();i++)
			{
				dynaBean=(DynaBean)tableDataList.get(i);
				map = executeMapData(headList, dynaBean, "0", tableDataList, taskMemberMap, scoreMap,p0800,showSubTask);
				
			}
			
			CommonData data = null;
			for(Object key:map.keySet()){
				String itemid = (String)key;
				if(columnList.contains(itemid)){
					String value = (String)map.get((String)key);
					data = new CommonData(value,itemid);
					dataList.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
       return dataList;
    }
	
   	/**   
	 * @Title: getRowJson   
	 * @Description:只获取某一行任务记录的json格式    
	 * @param @param p0800
	 * @param @param seq
	 * @param @return 
	 * @return String 
	 * @author:wangrd   
	 * @throws   
	*/
	public String  getRowJson(String p0800,String seq,int p0723,String othertask)
    {
	   setShowType(1);
       ArrayList headList=getHeadList(Integer.parseInt(WorkPlanConstant.PlanFlowStatus.DESIGN),p0723); 
       StringBuffer _json=new StringBuffer("");
       ArrayList tableDataList=getTableData(p0800); //获得计划下的任务数据
       HashMap taskMemberMap=getTaskMemberMap(tableDataList);
       HashMap scoreMap = getTaskScoreMap(tableDataList);
       DynaBean dynaBean=null;
       for(int i=0;i<tableDataList.size();i++)
       {
           dynaBean=(DynaBean)tableDataList.get(i);
           dynaBean.set("othertask", othertask);
               executeJsonData(_json,headList,dynaBean,seq,tableDataList,taskMemberMap,scoreMap);
   
       }

       if (",".equalsIgnoreCase(_json.substring(_json.length()-1, _json.length()))) {
           _json.setLength(_json.length()-1);
       }
       return _json.toString();
    }
    
	/**   
     * @Title: taskNameIsRepeated   
     * @Description:   检查任务名称是否重复 
     * @param @param parent_id 上级任务id 顶级节点传空值
     * @param @param task_id 当前任务id 更改名称时需要传
     * @param @param task_name 要检查的任务名称
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean  taskNameIsRepeated(String parent_id,String task_id,String task_name)
    {
       boolean b=false;
       ArrayList tableDataList=getTableData(""); //获得计划下的任务数据
       DynaBean dynaBean=null;   
       boolean bUpdate=false;//更新名称
       boolean bTop=false;//是否一级任务
       if(task_id!=null && task_id.length()>0 ){//更改名称
           bUpdate=true;
           //判断是不是一级任务
           HashMap p0800Map=new HashMap();
           for(int i=0;i<tableDataList.size();i++)
           {
               dynaBean=(DynaBean)tableDataList.get(i);
               p0800Map.put((String)dynaBean.get("p0800"), "1");
           }
          
           for(int i=0;i<tableDataList.size();i++)
           {
               dynaBean=(DynaBean)tableDataList.get(i);
               String p0831=(String)dynaBean.get("p0831");
               String p0800=(String)dynaBean.get("p0800");
               if (task_id.equals(p0800) &&(p0800.equals(p0831)|| p0800Map.get(p0831)==null)){
                   bTop=true;
                   break;
               }
           }           
       }
       else {//新增
           if (parent_id==null || parent_id.length()<1){
               bTop=true;
           }
       }
       
       //判断是否重名
       if (bTop){//一级任务
           HashMap p0800Map=new HashMap();
           for(int i=0;i<tableDataList.size();i++)
           {
               dynaBean=(DynaBean)tableDataList.get(i);
               p0800Map.put((String)dynaBean.get("p0800"), "1");
           }
           for(int i=0;i<tableDataList.size();i++)
           {
               dynaBean=(DynaBean)tableDataList.get(i);
               String p0831=(String)dynaBean.get("p0831");
               String p0800=(String)dynaBean.get("p0800");
               if(p0800.equals(p0831)|| p0800Map.get(p0831)==null){//比较一级任务
                   if (bUpdate){
                       if (p0800.equals(task_id)) {
                           continue;//当前id
                       }
                   }
                   String p0801=(String)dynaBean.get("p0801");
                   if (task_name.equals(p0801)){
                       b=true;
                       break;
                   }
               }
           }
       }
       else {//下级任务
           for(int i=0;i<tableDataList.size();i++)
           {
               dynaBean=(DynaBean)tableDataList.get(i);
               String p0831=(String)dynaBean.get("p0831");
               String p0800=(String)dynaBean.get("p0800");
               if(p0831.equals(parent_id) && (!p0800.equals(parent_id))){
                   if (bUpdate){
                       if (p0800.equals(task_id)) {
                           continue;//当前id
                       }
                   }
                   String p0801=(String)dynaBean.get("p0801");
                   if (task_name.equals(p0801)){
                       b=true;
                       break;
                   }
               }
           }
       }
   return b ;
  }
    
	/**   
     * @Title: taskNameIsRepeated   
     * @Description:   检查任务名称是否重复 
     * @param @param parent_id 上级任务id 顶级节点传空值
     * @param @param task_id 当前任务id 更改名称时需要传
     * @param @param task_name 要检查的任务名称
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public String  getTaskNameRepeatedId(String parent_id,String task_id,String task_name)
    {
       String repeatedId="";
       try{
	       ArrayList tableDataList=getTableData(""); //获得计划下的任务数据
	       DynaBean dynaBean=null;   
	       boolean bUpdate=false;//更新名称
	       boolean bTop=false;//是否一级任务
	       if(task_id!=null && task_id.length()>0 ){//更改名称
	           bUpdate=true;
	           //判断是不是一级任务
	           HashMap p0800Map=new HashMap();
	           for(int i=0;i<tableDataList.size();i++)
	           {
	               dynaBean=(DynaBean)tableDataList.get(i);
	               p0800Map.put((String)dynaBean.get("p0800"), "1");
	           }
	          
	           for(int i=0;i<tableDataList.size();i++)
	           {
	               dynaBean=(DynaBean)tableDataList.get(i);
	               String p0831=(String)dynaBean.get("p0831");
	               String p0800=(String)dynaBean.get("p0800");
	               if (task_id.equals(p0800) &&(p0800.equals(p0831)|| p0800Map.get(p0831)==null)){
	                   bTop=true;
	                   break;
	               }
	           }           
	       }
	       else {//新增
	           if (parent_id==null || parent_id.length()<1){
	               bTop=true;
	           }
	       }
	       
	       //判断是否重名
	       if (bTop){//一级任务
	           HashMap p0800Map=new HashMap();
	           for(int i=0;i<tableDataList.size();i++)
	           {
	               dynaBean=(DynaBean)tableDataList.get(i);
	               p0800Map.put((String)dynaBean.get("p0800"), "1");
	           }
	           for(int i=0;i<tableDataList.size();i++)
	           {
	               dynaBean=(DynaBean)tableDataList.get(i);
	               String p0831=(String)dynaBean.get("p0831");
	               String p0800=(String)dynaBean.get("p0800");
	               if(p0800.equals(p0831)|| p0800Map.get(p0831)==null){//比较一级任务
	                   if (bUpdate){
	                       if (p0800.equals(task_id)) {
                               continue;//当前id
                           }
	                   }
	                   String p0801=(String)dynaBean.get("p0801");
	                   if (task_name.equals(p0801)){
	                	   repeatedId=p0800;
	                       break;
	                   }
	               }
	           }
	       }
	       else {//下级任务
	           for(int i=0;i<tableDataList.size();i++)
	           {
	               dynaBean=(DynaBean)tableDataList.get(i);
	               String p0831=(String)dynaBean.get("p0831");
	               String p0800=(String)dynaBean.get("p0800");
	               if(p0831.equals(parent_id) && (!p0800.equals(parent_id))){
	                   if (bUpdate){
	                       if (p0800.equals(task_id)) {
                               continue;//当前id
                           }
	                   }
	                   String p0801=(String)dynaBean.get("p0801");
	                   if (task_name.equals(p0801)){
	                	   repeatedId=p0800;
	                       break;
	                   }
	               }
	           }
	       }
     }
     catch(Exception e){
    	 e.printStackTrace();
     }  
    return repeatedId ;
  }
    
    
    
    
    /**   
     * @Title: getSameBranchTaskIds   
     * @Description:获得相同分支下的任务编号 以逗号分隔   
     * @param @return 
     * @return HashMap  两个map值 parentIds:父任务  childIds:子孙任务
     * @author:wangrd   
     * @throws   
    */
    public HashMap getSameBranchTaskIds(String task_id)
    {
        HashMap map = new HashMap();
        map.put("parentIds","");
        map.put("childIds", "");
        if(this.p07_vo==null){
            return map;
        }
        ArrayList tableDataList=new ArrayList();
        tableDataList=getTableData(""); //获得计划下的任务数据

        DynaBean dynaBean=null;
        HashMap p0800Map=new HashMap();
        for(int i=0;i<tableDataList.size();i++)
        {
            dynaBean=(DynaBean)tableDataList.get(i);
            p0800Map.put((String)dynaBean.get("p0800"), "1");
        }
        boolean currentHaveRank=false;//当前任务是否有权重
        boolean parentHaveRank=false;//父任务是否有权重
        boolean childHaveRank=false;//子任务是否有权重
        boolean bTop=false;//是否顶级节点
        ArrayList topDataList= new ArrayList();
        for(int i=0;i<tableDataList.size();i++)
        {
            dynaBean=(DynaBean)tableDataList.get(i);
            String p0831=(String)dynaBean.get("p0831");
            String p0800=(String)dynaBean.get("p0800");
            if (task_id.equals(p0800)){
                String _rank=(String)dynaBean.get("rank"); 
                if (_rank!=null && _rank.length()>0){
                    currentHaveRank=true; 
                }
            }
            if(((p0800).equals(p0831)|| p0800Map.get(p0831)==null)){
                if (task_id.equals(p0800)){
                    bTop=true;
                    break;
                }
                topDataList.add(dynaBean);
            }
            
        }
        StringBuffer sameBranchIds= new StringBuffer();
       //取父任务
        if (!bTop){
            parentHaveRank=getParentTaskIds(tableDataList,topDataList,task_id,sameBranchIds);
        }
        map.put("parentIds", sameBranchIds.toString());
        //取子任务
        sameBranchIds.setLength(0);
        childHaveRank=getChildrenTaskIds(tableDataList,task_id,sameBranchIds);
        map.put("childIds", sameBranchIds.toString());
        map.put("parentHaveRank",parentHaveRank?"true":"false");
        map.put("childHaveRank",childHaveRank?"true":"false");
        map.put("currentHaveRank",currentHaveRank?"true":"false");
        return map;
    } 
    

    /**   
     * @Title: getParentTaskIds   
     * @Description: 获取某一任务的所有祖先任务   
     * @param @param topDataList 当前人员的顶级任务列表
     * @param @param task_id
     * @param @param parentIds 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public boolean getParentTaskIds(ArrayList tableDataList,ArrayList topDataList,String task_id,StringBuffer parentIds)
    {
        boolean b=false;
        RowSet rset=null;
        DynaBean dynaBean=null;
        ContentDAO dao = new ContentDAO(this.conn);
        String strsql="select * from p08 where p0800="+task_id;
        try{
            rset=dao.search(strsql);
            if (rset.next()){
                String parentId= rset.getString("p0831");
                parentIds.append(","+parentId);
                if (parentId.equals(task_id)){
                    return b;
                }
                
                for(int i=0;i<tableDataList.size();i++)
                {//判断是否有权重
                    dynaBean=(DynaBean)tableDataList.get(i);
                    String p0800=(String)dynaBean.get("p0800");
                    if (parentId.equals(p0800)){
                        String _rank=(String)dynaBean.get("rank"); 
                        if (_rank!=null && _rank.length()>0){
                        	float rank = Float.valueOf(_rank);//lis 20160628
                        	if(rank > 0){
                        		b=true; 
                        		break;
                        	}
                        }
                    }
                    
                }
                
                boolean bTop=false;
                for(int i=0;i<topDataList.size();i++)
                {
                    dynaBean=(DynaBean)topDataList.get(i);
                    String p0800=(String)dynaBean.get("p0800");                  
                    if (parentId.equals(p0800)){
                        bTop=true;
                        break;
                    }
                    
                }
                if (!bTop){
                    boolean bHaveRank=getParentTaskIds(tableDataList,topDataList,parentId,parentIds);   
                    if (bHaveRank){
                        b=true;
                    }
                    
                }
            }
            rset.close();
            rset=null;
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            WorkPlanUtil.closeDBResource(rset);
        }
        return b;
    }
    
    
    /**   
     * @Title: getChildrenTaskIds   
     * @Description:获取某一任务的所有子孙任务    
     * @param @param tableDataList
     * @param @param task_id
     * @param @param childIds 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public boolean getChildrenTaskIds(ArrayList tableDataList,String task_id,StringBuffer childIds)
    {
        boolean b=false;
        DynaBean _dynaBean=null;
        for(int j=0;j<tableDataList.size();j++)
        {
            _dynaBean=(DynaBean)tableDataList.get(j);
            String _p0800=(String)_dynaBean.get("p0800");
            String _p0831=(String)_dynaBean.get("p0831");
            if(!task_id.equalsIgnoreCase(_p0800)&&_p0831.equalsIgnoreCase(task_id))
            {
                String _rank=(String)_dynaBean.get("rank"); 
                if (_rank!=null && _rank.length()>0){
                    b=true;  
                }
                childIds.append(","+_p0800) ;   
                boolean bHaveRank= getChildrenTaskIds(tableDataList,_p0800,childIds); 
                if (bHaveRank){
                    b=true;
                }
            }
        }
        return b; 
    }
    /**
	 * 查看当前用户名在设置方案中是否存在私有方案
	 * @param username
	 * @return boolean 
	 * */
	public boolean hasPrivateScheme(String submoduleid, String username){
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.conn);
		// 是否存在私有记录
		String sqlForPrivate= "select * from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = 0 and username = '" + username + "'";
		RowSet rset=null;
	    try{
        	rset=dao.search(sqlForPrivate);
        	if(rset.next()){
        		flag = true;
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        return flag;
	}
	 /**
	 * 查看栏目设置方案中是否存在公有方案
	 * @return boolean
	 * */
	public boolean hasShareScheme(String submoduleid){
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.conn);
        // 是否存在公有记录
		String sqlForShare= "select * from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = 1";
		RowSet rset=null;
	    try{
        	rset=dao.search(sqlForShare);
        	if(rset.next()){
        		flag = true;
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }
	            
        return flag;
	}
	
	/**
	 * 获得计划任务列表表头指标(默认)
	 * @param showType 任务列表视图类型  1：计划制定   2：计划跟踪
	 * @param p0723 1：人员计划  2：团队计划  3：项目
	 * @return ArrayList 列表表头
	 */
	private ArrayList getHeadListDefalt(int showType, int p0723){
		ArrayList list=new ArrayList();
		list.add(getFieldItem("seq","序号","A","0",50,0,60));
		FieldItem item=DataDictionary.getFieldItem("p0801", 1);
		// 计划跟踪页面删掉 chent 2015-08-27 start
		//if (showType==1)
	    item.setFormula("400");
		//else 
		//    item.setFormula("300");
		list.add(item); //任务名称
 
		/* 去掉任务描述 郭建文提 20114-8-14 wangrd
		if(showType==2) 
		{
			item=DataDictionary.getFieldItem("p0803");
			item.setFormula("250");
			list.add(item); //任务描述 
		} */
		if (WorkPlanUtil.isClient("ZGLX")){
		    item=DataDictionary.getFieldItem("p0825", 1);
		    if (item!=null){
		        list.add(item);//任务来源
		    }
		}
        if(p0723==2)  //p0723-> 1:人员计划  2：团队计划  3:项目计划
        {
            item=DataDictionary.getFieldItem("p0843", 1);
            if (item!=null){
            //    list.add(item);//岗位
            }
        }
    
        //if (showType==1)
    	list.add(getFieldItem("principal","负责人","A","0",50,0,200));
        //else
        //	list.add(getFieldItem("principal","负责人","A","0",50,0,70));
		/* 参与人 */
	/*	if(showType==2)
			list.add(getFieldItem("participant","参与人","A","0",200,0,150)); */
//		if(p0723==2)  //p0723-> 1:人员计划  2：团队计划  3:项目计划
//		{
//			item=DataDictionary.getFieldItem("p0839", 1);
//			item.setFormula("100");
//			list.add(item); //协作部门
//	
//		}
		/* 权重*/
		list.add(getFieldItem("rank","权重","N","0",50,0,200)); //时间安排 
		//任务报批状态
		item=DataDictionary.getFieldItem("p0811", 1);
		item.setFormula("100");
		list.add(item);
		
		//if(showType==1){ 
		list.add(getFieldItem("timeArrange","时间安排","A","0",50,0,220)); //时间安排 
		//}else{
		/*item=DataDictionary.getFieldItem("p0835", 1);
		item.setFormula("80");
		list.add(item); //完成进度
		
			FieldItem _item=(FieldItem)item.clone();
			_item.setItemid("p0835_state");
			_item.setItemdesc("完成状态");
			_item.setFormula("70");
			list.add(_item); //完成状态
			FieldItem _item2=(FieldItem)item.clone();
			_item2.setItemid("p0835_jd");
			_item2.setFormula("80");
			list.add(_item2); //完成进度
			
		
		list.add(getFieldItem("gantt","任务起止时间-甘特图","A","0",50,0,220)); //甘特图
		list.add(getFieldItem("taskprogresscolor","颜色提示图","A","0",50,0,220)); //颜色提示图*/
			
		//} 
		// 计划跟踪页面删掉 chent 2015-08-27 end
		return list;
	}
	
	/**
	 * 获得计划任务列表表头指标(手动设置)
	 * @param recordPrivate 栏目设置是否有保存过的私有记录
	 * @param recordShare  栏目设置是否有保存过的公有记录
	 * @param submoduleid  栏目设置区分
	 * @return ArrayList 列表表头
	 */
	private ArrayList getHeadListSetting(boolean recordPrivate, boolean recordShare, String submoduleid){
		ArrayList list=new ArrayList();
		list.add(getFieldItem("seq","序号","A","0",50,0,60));
		
		ContentDAO dao = new ContentDAO(this.conn);
		String strsql= "";
		if(recordPrivate){
			strsql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = '0' and username = '" + this.userView.getUserName() + "') and is_display = '1' order by displayorder";
		}else if(recordShare){
			strsql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = '1') and is_display = '1' order by displayorder";
		}
		RowSet rset=null;
		try{
            rset=dao.search(strsql);
            while(rset.next()){
            	String itemid = "";
            	String desc = "";
            	String type = "";
            	String codesetid = "";
            	int length = 0;
            	int decimalWidth = 0;
            	int columnWidth = 0;
                itemid= rset.getString("itemid");
                desc = rset.getString("displaydesc");
            	type = "A";
            	codesetid = "0";
            	length = 50;
            	decimalWidth = 0;
            	columnWidth = Integer.parseInt(rset.getString("displaywidth"));
            	list.add(getFieldItem(itemid,desc,type,codesetid,length,decimalWidth,columnWidth));
            }
        }catch(Exception e){
        	e.printStackTrace();
        }

		
		return list;
	}
	
	
	private ArrayList getHeadListSetting(boolean ishare, String submoduleid){
		ArrayList list=new ArrayList();
		list.add(getFieldItem("seq","序号","A","0",50,0,60));
		
		ContentDAO dao = new ContentDAO(this.conn);
		String strsql= "";
		if(!ishare){
			strsql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = '0' and username = '" + this.userView.getUserName() + "') and is_display = '1' order by displayorder";
		}else {
			strsql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = '1') and is_display = '1' order by displayorder";
		}
		RowSet rset=null;
		try{
            rset=dao.search(strsql);
            while(rset.next()){
            	
            	RowSet rsetFromBusifield = null;
            	String is_order = rset.getString("is_order");
            	String sql = "select * from t_hr_busifield where FieldSetId = 'P08' and LOWER(itemid) = '" + rset.getString("itemid") + "'";
            	rsetFromBusifield = dao.search(sql);
            	// 完成进度颜色提示图
            	if("P0835".equalsIgnoreCase(rset.getString("itemid"))){
            		list.add(getFieldItem("taskprogresscolor","颜色提示图","A","0",50,0,220)); 
            	}
            	if(rsetFromBusifield.next()){
            		String itemid = rset.getString("itemid");
            		String desc = rset.getString("displaydesc");
            		FieldItem item = DataDictionary.getFieldItem(itemid, "P08");
            		// 栏目设置的名称为空则显示业务字典的名称；不为空则显示栏目设置的 chent 20171205 modify start
            		if(StringUtils.isEmpty(desc) && item != null){
            			desc = item.getItemdesc();
            		} else {
            			String specalDesc = this.getSchemeColumnDesc(itemid);// 特殊列显示规则
            			if(StringUtils.isNotEmpty(specalDesc)) {
            				desc = specalDesc;
            			}
            		}
            		// 栏目设置的名称为空则显示业务字典的名称；不为空则显示栏目设置的 chent 20171205 modify end
                	String type = rsetFromBusifield.getString("itemtype");
                	String codesetid = rsetFromBusifield.getString("codesetid");
                	int length = Integer.parseInt(rsetFromBusifield.getString("itemlength"));
                	int decimalWidth = 0;
                	if(!StringUtils.isEmpty(rsetFromBusifield.getString("decimalwidth"))){
                		decimalWidth = Integer.parseInt(rsetFromBusifield.getString("decimalwidth"));
                	}
                	int columnWidth = Integer.parseInt(rset.getString("displaywidth"));
                	list.add(getFieldItem(itemid,desc,type,codesetid,length,decimalWidth,columnWidth));
                	if(StringUtils.isNotBlank(is_order) && !"0".equals(is_order)){
                		this.sortItem.add(itemid+":"+type+":"+is_order);
                	}
                	
            	}else{
            		String itemid = "";
            		String desc = "";
            		String type = "";
            		String codesetid = "";
            		int length = 0;
            		int decimalWidth = 0;
            		int columnWidth = 0;
            		if("rank".equalsIgnoreCase(rset.getString("itemid"))){
            			itemid = rset.getString("itemid");
                    	desc = this.getSchemeColumnDesc(itemid);
                    	type = "N";
                    	codesetid = "0";
                    	length = 3;
                    	decimalWidth = 3;
                    	columnWidth = Integer.parseInt(rset.getString("displaywidth"));
            		}else if("principal".equalsIgnoreCase(rset.getString("itemid"))){
            			itemid = rset.getString("itemid");
                    	desc = this.getSchemeColumnDesc(itemid);
                    	type = "A";
                    	codesetid = "0";
                    	length = 50;
                    	decimalWidth = 0;
                    	columnWidth = Integer.parseInt(rset.getString("displaywidth"));
            		}else if("timearrange".equalsIgnoreCase(rset.getString("itemid"))){
            			itemid = rset.getString("itemid");
                    	desc = this.getSchemeColumnDesc(itemid);
                    	type = "A";
                    	codesetid = "0";
                    	length = 50;
                    	decimalWidth = 0;
                    	columnWidth = Integer.parseInt(rset.getString("displaywidth"));
            		}
            		else if("gantt".equalsIgnoreCase(rset.getString("itemid"))){
            			itemid = rset.getString("itemid");
                    	desc = this.getSchemeColumnDesc(itemid);
                    	type = "A";
                    	codesetid = "0";
                    	length = 50;
                    	decimalWidth = 0;
                    	columnWidth = Integer.parseInt(rset.getString("displaywidth"));
            		}
            		else if("participant".equalsIgnoreCase(rset.getString("itemid"))){
            			itemid = rset.getString("itemid");
                    	desc = this.getSchemeColumnDesc(itemid);
                    	type = "A";
                    	codesetid = "0";
                    	length = 50;
                    	decimalWidth = 0;
                    	columnWidth = Integer.parseInt(rset.getString("displaywidth"));
            		}
            		list.add(getFieldItem(itemid,desc,type,codesetid,length,decimalWidth,columnWidth));
            		if(StringUtils.isNotBlank(is_order) && !"0".equals(is_order)){
                		this.sortItem.add(itemid+":"+type+":"+is_order);
                	}
            	}
            }
        }catch(Exception e){
        	e.printStackTrace();
        }

		
		return list;
	}
	
	private String getPanelColumnsDefalt(ArrayList headList,int showType,String object_id,int p0723,int role){
		//,sortable:true ,hideable:false,hidden: true 
		StringBuffer column_str=new StringBuffer("[");
		String lock_str=",locked: true";
		
		WorkPlanUtil planUtil= new WorkPlanUtil(this.conn,this.userView);
		String nbase="";
		String a0100="";
		if(p0723==1&&!"".equals(object_id)){
			nbase=object_id.substring(0,3);
			a0100=object_id.substring(3,object_id.length());
		}
		if(showType==1) {
            lock_str="";
        }
		//column_str.append("{xtype: 'checkboxmodel',align:'center',dataIndex: 'selected',width: 40"+lock_str+",menuDisabled:true,sortable: false }");
		FieldItem item=null;
		for(int i=0;i<headList.size();i++)
		{
			item = (FieldItem) headList.get(i);
			//在计划跟踪界面不出现颜色提示图页
			if("taskprogresscolor".equalsIgnoreCase(item.getItemid()))  //颜色提示图
			{
				continue;
			}
			//在计划指定页面不出现任务审批状态列
			if("p0811".equalsIgnoreCase(item.getItemid()))  //任务审批状态
			{
				continue;
			}
			if("gantt".equalsIgnoreCase(item.getItemid())/*&&this.p07_vo!=null*/)  //甘特图
			{
				column_str.append(getGantColumns(""));
			}
			else
			{ 
				column_str.append(",{text: '"+item.getItemdesc()+"',dataIndex: '"+item.getItemid().toLowerCase()+"', sortable: false,width:"+item.getFormula()+",minWidth:20"); //item.getFormula()放的是列的默认宽度，
				if(lockedFieldStr.indexOf("/"+item.getItemid().toLowerCase()+"/")!=-1)  //默认锁定列
				{
					column_str.append(lock_str+",menuDisabled:true"); 
				}
				if("p0801".equalsIgnoreCase(item.getItemid())) //任务名称,出现展开图标,添加链接
				{
					column_str.append(",xtype:'treecolumn'");
					column_str.append(" ,editor: 'bigtextfield'");
					column_str.append(",renderer:addLink }");
				}
				else if("rank".equalsIgnoreCase(item.getItemid())){
					column_str.append(" ,renderer:addRankValue,menuDisabled:true");
					if (showType==1)//在计划制定页面，权重值是可以编辑的
                    {
                        column_str.append(" ,editor: 'textfield'");
                    }
					column_str.append(" }");
				}
				else if("p0835".equalsIgnoreCase(item.getItemid())){
					column_str.append(" ,renderer:drawWarning,menuDisabled:true");
					if(planUtil.isLogonUser(nbase, a0100)||planUtil.isMyDept(object_id))//当前登录人是本人才可以编辑自己任务和自己部门任务的进度
                    {
                        column_str.append(" ,editor: 'textfield'");
                    }
					column_str.append(" }");
			    }
				else if("principal".equalsIgnoreCase(item.getItemid())) {
                    column_str.append(" ,renderer:addPrincipalValue,menuDisabled:true}");
                } else if("timeArrange".equalsIgnoreCase(item.getItemid())) {
					//haosl 默认方案添加了上级评价列，所以不能无条件将timeArrange 作为最后一列
				    if(role > -1) {
                        column_str.append(" ,renderer:addTimeArrageCss,menuDisabled:true}");
                    } else {
                        column_str.append(" ,renderer:addTimeArrageCssForEnd,menuDisabled:true}");
                    }
				}
				/*
				else if(item.getItemid().equalsIgnoreCase("p0835_state"))
					column_str.append(" ,renderer:drawWarning}");
				else if(item.getItemid().equalsIgnoreCase("p0835_jd"))
					column_str.append(" ,renderer:drawWarningJd}");
				*/				
				else{
					if ("M".equalsIgnoreCase(item.getItemtype())){
						column_str.append(" ,editor: 'bigtextfield'");
						column_str.append(",renderer:addGridCss,menuDisabled:true}");
					}
					else{ 
						if("p0817".equalsIgnoreCase(item.getItemid())){
							column_str.append(",renderer:addGridCssa,editor: 'textfield',menuDisabled:true}");
						}else{
							column_str.append(",renderer:addGridCss,editor: 'textfield',menuDisabled:true}");
						}
					}
				}
			}
			
		}
		if(role > -1){
			column_str.append(",{text: '上级评价',dataIndex:'superiorEvaluation',width:180,renderer:addGridScore,menuDisabled:true,sortable: false}");
		}
		column_str.append("]"); 
		column_str.deleteCharAt(1);//去掉优化后产生的多余逗号  haosl 20170615
		return column_str.toString();
	}

	private String getPanelColumnsSetting(ArrayList headList,int showType,String object_id,int p0723,String privateOrShare, int role){
		String lockedFieldsStr = ",seq,";// 锁列
		String leftFieldsStr = ",";// 居左
		String centerFieldsStr = ",";// 居中
		String rightFieldsStr = ",";// 居右
		String submoduleid = SUBMODULEID_PLAN_DESIGN; // 栏目设置区分
		if(showType==2){
			submoduleid = SUBMODULEID_PLAN_TRACE;
		}
		String sql = "";
		if("1".equals(privateOrShare)){
			sql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = '0' and username = '" + this.userView.getUserName() + "') and is_display = '1' order by displayorder";
		}else if("2".equals(privateOrShare)){
			sql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '" + submoduleid + "' and is_share = '1') and is_display = '1' order by displayorder";
		}
		String lockFlg = "false";
		RowSet rset=null;
		try{
			ContentDAO dao = new ContentDAO(this.conn);
	        rset=dao.search(sql);
	        while(rset.next()){
	        	String scheme_id= rset.getString("itemid");
	            if("1".equals(rset.getString("is_lock"))){
	            	lockFlg = "true";
		        	lockedFieldsStr += (scheme_id+",");
	            }
	            if("1".equals(rset.getString("align"))){// 居左
	            	leftFieldsStr += (scheme_id+",");
	            }else if("2".equals(rset.getString("align"))){// 居中
	            	centerFieldsStr += (scheme_id+",");
	            }else if("3".equals(rset.getString("align"))){// 居右
	            	rightFieldsStr += (scheme_id+",");
	            }
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//,sortable:true ,hideable:false,hidden: true 
		StringBuffer column_str=new StringBuffer("[");
		String lock_str=",locked: true";
		
		WorkPlanUtil planUtil= new WorkPlanUtil(this.conn,this.userView);
		String nbase="";
		String a0100="";
		if(p0723==1&&!"".equals(object_id)){
			nbase=object_id.substring(0,3);
			a0100=object_id.substring(3,object_id.length());
		}
//		if("true".equals(lockFlg)){
//			column_str.append("{xtype: 'checkcolumn',header: '选择',align:'center',dataIndex: 'selected',width: 40,stopSelection: false"+lock_str+",menuDisabled:true,renderer:addCheckBoxGridCss,sortable: false }");
//		}else{
//			column_str.append("{xtype: 'checkcolumn',header: '选择',align:'center',dataIndex: 'selected',width: 40,stopSelection: false,menuDisabled:true,renderer:addCheckBoxGridCss,sortable: false }");
//		}
		
		FieldItem item=null;
		int num = headList.size()-1;
		if(role>-1){
			num = num+1;
		}
		for(int i=0;i<headList.size();i++)
		{
			item = (FieldItem) headList.get(i);
			//在计划跟踪界面不出现颜色提示图页
			if("taskprogresscolor".equalsIgnoreCase(item.getItemid()))  //颜色提示图
			{
				continue;
			}
			//在计划指定页面不出现任务审批状态列
			if("p0811".equalsIgnoreCase(item.getItemid()))  //任务审批状态
			{
				continue;
			}
			if("gantt".equalsIgnoreCase(item.getItemid())/*&&this.p07_vo!=null*/)  //甘特图
			{
				if(lockedFieldsStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1) {
					column_str.append(getGantColumns(lock_str));
				}else{
					column_str.append(getGantColumns(""));
				}
			}
			else
			{ 
				String alignStr = "";
				if(leftFieldsStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1){
					alignStr = ",align:'left'";
				}else if(centerFieldsStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1){
					alignStr = ",align:'center'";
				}
				else if(rightFieldsStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1){
					alignStr = ",align:'right'";
				}
				//String css ="addTimeArrageCss";
				
				if((i!=num)){
					//这里不知道为什么任务序号列排掉被排掉了（是在1.112版本被lis 排掉，注释是环球的任务穿透），这里为了解决中交一公局提出的任务页面没有序号的问题，删除此代码   haosl 2017-11-20
//					if(item.getItemid().equalsIgnoreCase("seq"))
//						continue;
					column_str.append(",{text: '"+item.getItemdesc()+"',dataIndex: '"+item.getItemid().toLowerCase()+"', sortable: false"+alignStr+",width:"+item.getFormula()+",minWidth:20"); //item.getFormula()放的是列的默认宽度，
					if("true".equals(lockFlg)){
						if(lockedFieldsStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1)  
						{
							column_str.append(lock_str+",menuDisabled:true"); 
						}
					}
					if("p0801".equalsIgnoreCase(item.getItemid())) //任务名称,出现展开图标,添加链接
					{
						column_str.append(",xtype:'treecolumn'");
						column_str.append(" ,editor: 'bigtextfield'");
						column_str.append(",renderer:addLink,menuDisabled:true }");
						
						this.p0801ColumnWidth = item.getFormula();
					}
					else if("rank".equalsIgnoreCase(item.getItemid())){
						column_str.append(" ,renderer:addRankValue,menuDisabled:true");
						if (showType==1)//在计划制定页面，权重值是可以编辑的
                        {
                            column_str.append(" ,editor: 'textfield'");
                        }
						column_str.append(" }");
					}
					else if("p0835".equalsIgnoreCase(item.getItemid())){
						column_str.append(" ,renderer:drawWarning,menuDisabled:true");
						//if(planUtil.isLogonUser(nbase, a0100)||planUtil.isMyDept(object_id))//当前登录人是本人才可以编辑自己任务和自己部门任务的进度     做判断不在此做，删除 chent 20150929
					       column_str.append(" ,editor: 'textfield'");
						column_str.append(" }");
				    }
					else if("principal".equalsIgnoreCase(item.getItemid())){
						column_str.append(" ,renderer:addPrincipalValue,menuDisabled:true}");
					}
					else if("timeArrange".equalsIgnoreCase(item.getItemid())) {
                        column_str.append(" ,renderer:addGridCss,menuDisabled:true}");
                    } else if("participant".equalsIgnoreCase(item.getItemid()))//任务成员
                    {
                        column_str.append(" ,renderer:addParticipant,menuDisabled:true}");
                    }
					//else if(item.getItemid().equalsIgnoreCase("p0823") && StringUtils.isNotBlank(item.getCodesetid())){//任务分类
					else if(!"".equals(item.getCodesetid())&&!"0".equals(item.getCodesetid())){//任务分类
						column_str.append(" ,editor:{xtype:'codecomboxfield',codesetid:'"+item.getCodesetid()+"',config:{maxPickerWidth:50}}");
						column_str.append(" ,renderer:addGridCssP0823,menuDisabled:true}");
					}
					/*
					else if(item.getItemid().equalsIgnoreCase("p0835_state"))
						column_str.append(" ,renderer:drawWarning}");
					else if(item.getItemid().equalsIgnoreCase("p0835_jd"))
						column_str.append(" ,renderer:drawWarningJd}");
					*/
					else{
						if ("M".equalsIgnoreCase(item.getItemtype())){
							column_str.append(" ,editor: 'bigtextfield'");
							column_str.append(",renderer:addGridCssMemo,menuDisabled:true}");
						}
						else {
							if("p0813".equalsIgnoreCase(item.getItemid())){
								column_str.append(" ,editor: {xtype:'datetimefield',format:'Y/m/d'} ");
								column_str.append(",renderer:editTaskStartTime,menuDisabled:true}");
							}else if("p0815".equalsIgnoreCase(item.getItemid())){
								column_str.append(" ,editor: {xtype:'datetimefield',format:'Y/m/d'} ");
								column_str.append(",renderer:editTaskEndTime,menuDisabled:true}");
							}else if("p0819".equalsIgnoreCase(item.getItemid())){
								column_str.append(",renderer:addGridCssa,menuDisabled:true}");
							}else{
								if("p0817".equalsIgnoreCase(item.getItemid())){
									column_str.append(",renderer:addGridCssa,editor: 'textfield',menuDisabled:true}");
								}else{
									column_str.append(",renderer:addGridCss,editor: 'textfield',menuDisabled:true}");
								}
							}
						}
					}
				}else{
					column_str.append(",{text: '"+item.getItemdesc()+"',dataIndex: '"+item.getItemid().toLowerCase()+"', sortable: false"+alignStr+",width:"+item.getFormula()+",minWidth:20"); //item.getFormula()放的是列的默认宽度，
					if(lockedFieldsStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1)  //默认锁定列
					{
						column_str.append(lock_str+",menuDisabled:true"); 
					}
					if("p0801".equalsIgnoreCase(item.getItemid())) //任务名称,出现展开图标,添加链接
					{
						column_str.append(",xtype:'treecolumn'");
						column_str.append(" ,editor: 'bigtextfield'");
						column_str.append(",renderer:addLinkForEnd,menuDisabled:true}");
						
						this.p0801ColumnWidth = item.getFormula();
					}
					else if("rank".equalsIgnoreCase(item.getItemid())){
						column_str.append(" ,renderer:addRankValueEnd,menuDisabled:true");
						if (showType==1)//在计划制定页面，权重值是可以编辑的
                        {
                            column_str.append(" ,editor: 'textfield'");
                        }
						column_str.append(" }");
					}
					else if("p0835".equalsIgnoreCase(item.getItemid())){
						column_str.append(" ,renderer:drawWarningEnd,menuDisabled:true");
						//if(planUtil.isLogonUser(nbase, a0100)||planUtil.isMyDept(object_id))//当前登录人是本人才可以编辑自己任务和自己部门任务的进度        做判断不在此做，删除 chent 20150929
					       column_str.append(" ,editor: 'textfield'");
						column_str.append(" }");
				    }
					else if("principal".equalsIgnoreCase(item.getItemid())) {
                        column_str.append(" ,renderer:addPrincipalValueEnd,menuDisabled:true}");
                    } else if("timeArrange".equalsIgnoreCase(item.getItemid())) {
                        column_str.append(" ,renderer:addTimeArrageCssForEnd,menuDisabled:true}");
                    } else if("participant".equalsIgnoreCase(item.getItemid()))//任务成员
                    {
                        column_str.append(" ,renderer:addParticipantEnd,menuDisabled:true}");
                    } else if(!"".equals(item.getCodesetid())&&!"0".equals(item.getCodesetid())){//代码型
						column_str.append(" ,editor:{xtype:'codecomboxfield',codesetid:'"+item.getCodesetid()+"',config:{maxPickerWidth:50}}");
						column_str.append(" ,renderer:addGridCssP0823end,menuDisabled:true}");
					}
					else{
						if ("M".equalsIgnoreCase(item.getItemtype())){
							column_str.append(" ,editor: 'bigtextfield'");
							column_str.append(",renderer:addGridCssMemoEnd,menuDisabled:true}");
						}else if("p0813".equalsIgnoreCase(item.getItemid())){
							column_str.append(" ,editor: {xtype:'datetimefield',format:'Y/m/d'} ");
							column_str.append(",renderer:editTaskStartTimeLast,menuDisabled:true}");
						}else if("p0815".equalsIgnoreCase(item.getItemid())){
							column_str.append(" ,editor: {xtype:'datetimefield',format:'Y/m/d'} ");
							column_str.append(",renderer:editTaskEndTimeLast,menuDisabled:true}");
						}else {
							if("p0817".equalsIgnoreCase(item.getItemid())){
								column_str.append(",renderer:addGridCssEnda,editor: 'textfield',menuDisabled:true}");
							}else if("p0819".equalsIgnoreCase(item.getItemid())){
								column_str.append(",renderer:addGridCssEnda,menuDisabled:true}");
							}else{
								column_str.append(",renderer:addGridCssEnd,editor: 'textfield',menuDisabled:true}");
							}
						}
					}
				}
			}
		}
		if(role > -1){
			column_str.append(",{text: '上级评价',dataIndex:'superiorEvaluation',width:180,renderer:addGridScore,menuDisabled:true,sortable: false}");
		}
		column_str.append("]"); 
		column_str.deleteCharAt(1);//去掉优化后产生的多余逗号  haosl 20170615
		return column_str.toString();
	}
	
	/**
	 * @author lis
	 * @Description: 获得列id数据集合
	 * @date 2016-3-10
	 * @param headList 列对象集合
	 * @param role 角色
	 * @return
	 */
	private ArrayList getColumnsSetting(ArrayList headList, int role){
		ArrayList headFieldList = new ArrayList();
		
		FieldItem item=null;
		int num = headList.size()-1;
		if(role>-1){
			num = num+1;
		}
		for(int i=0;i<headList.size();i++)
		{
			item = (FieldItem) headList.get(i);
			//在计划跟踪界面不出现颜色提示图页
			if("taskprogresscolor".equalsIgnoreCase(item.getItemid()))  //颜色提示图
			{
				continue;
			}
			//在计划指定页面不出现任务审批状态列
			if("p0811".equalsIgnoreCase(item.getItemid()))  //任务审批状态
			{
				continue;
			}
			if((i!=num)){
				//这里不知道为什么任务序号列排掉被排掉了（是在1.112版本被lis 排掉，注释是环球的任务穿透），这里为了解决中交一公局提出的任务页面没有序号的问题，删除此代码   haosl 2017-11-20
//				if(item.getItemid().equalsIgnoreCase("seq"))
//					continue;
				headFieldList.add(item.getItemid().toLowerCase());
			}else{
				headFieldList.add(item.getItemid().toLowerCase());
			}
		}
		if(role > -1){
			headFieldList.add("superiorEvaluation");
		}
		return headFieldList;
	}
	
	private ArrayList getColumnsDefalt(ArrayList headList, int role){
		ArrayList headFieldList = new ArrayList();
		FieldItem item=null;
		for(int i=0;i<headList.size();i++)
		{
			item = (FieldItem) headList.get(i);
			//在计划跟踪界面不出现颜色提示图页
			if("taskprogresscolor".equalsIgnoreCase(item.getItemid()))  //颜色提示图
			{
				continue;
			}
			//在计划指定页面不出现任务审批状态列
			if("p0811".equalsIgnoreCase(item.getItemid()))  //任务审批状态
			{
				continue;
			}
			if("gantt".equalsIgnoreCase(item.getItemid())/*&&this.p07_vo!=null*/)  //甘特图
			{
				//column_str.append(getGantColumns(""));
			}
			else
			{ 
				headFieldList.add(item.getItemid().toLowerCase());
			}
		}
		if(role > -1){
			headFieldList.add("superiorEvaluation");
		}
		return headFieldList;
	}
	/**   
     * @Description: 获取某一任务的所有祖先任务   
     * @param @param topDataList 当前人员的顶级任务列表
     * @param @param task_id
     * @param @param parentIds 
     * @return void 
     * chent   
    */
    /**
     * 判断是否存在的父任务中负责人为自己的任务
     * @param p0800
     * @return
     * chent
     */
    public boolean inParentTaskHaveManageByMe(String p0800)
    {
    	boolean flag = false;
        RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try{
        	PlanTaskBo taskBo=new PlanTaskBo(this.conn, this.userView);
        	rs = taskBo.getTask(p0800);
            if (rs.next()){
                String parentId = rs.getString("p0831");
                if (StringUtils.isEmpty(parentId) || parentId.equals(p0800)){//查不到父任务 或 已是顶级任务 返回
                	return flag;
                }
                
                int p0700 = rs.getInt("p0700");
                //RecordVo p07_vo = getP07Vo(p0700);
                WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
                
				List<String> personList = util.getTaskRolePersionList(Integer.parseInt(parentId), 1);
				String person = personList.size()>0?personList.get(0):"";
				String taskNbase ="";
				String taskA0100 = "";
				if(StringUtils.isNotBlank(person)){
					taskNbase = person.substring(0, 3);// 任务负责人
					taskA0100 = person.substring(3);
				}
				List<String> startPersonList = util.getTaskRolePersionList(Integer.parseInt(parentId), 5);
				String startperson = startPersonList.size()>0?startPersonList.get(0):"";
				String taskstartNbase = "";
				String taskstartA0100 = "";
				if(StringUtils.isNotBlank(startperson)){
					taskstartNbase = startperson.substring(0, 3);// 任务负责人
					taskstartA0100 = startperson.substring(3);
				}
				if((this.nbase.equalsIgnoreCase(taskNbase) && this.a0100.equalsIgnoreCase(taskA0100)) || (this.nbase.equalsIgnoreCase(taskstartNbase) && this.a0100.equalsIgnoreCase(taskstartA0100))){//负责人或发起人是自己
					flag = true;
				} else {
					flag = inParentTaskHaveManageByMe(parentId);   
				}
            }
        } catch(Exception e) {           
            e.printStackTrace();  
        } finally {
        	PubFunc.closeDbObj(rs);
        }
        return flag;
    }
    /**
     * 通过列头id获取列名
     * @param columnItemId
     * @return
     */
    public String getSchemeColumnDesc(String columnItemId){
    	String desc = "";
    	
    	if("gantt".equalsIgnoreCase(columnItemId)){
    		desc = "任务起止时间-甘特图";
    		
    	} else if("participant".equalsIgnoreCase(columnItemId)) {
    		desc = "任务成员";
    		
    	} else if("rank".equalsIgnoreCase(columnItemId)) {
    		desc = "权重";
    		
    	} else if("timearrange".equalsIgnoreCase(columnItemId)) {
    		desc = "时间安排";
    		
    	} else if("principal".equalsIgnoreCase(columnItemId)) {
    		desc = "负责人";
    	}
    	
    	return desc;
    }
	
public RecordVo getP07_vo() {
	return p07_vo;
}


public void setP07_vo(RecordVo p07_vo) {
	this.p07_vo = p07_vo;
}


public int getP0729() {
    return P0729;
}


public void setP0729(int p0729) {
    P0729 = p0729;
}


public int getP0727() {
    return P0727;
}


public void setP0727(int p0727) {
    P0727 = p0727;
}


public int getP0723() {
    return P0723;
}


public void setP0723(int p0723) {
    P0723 = p0723;
}


public int getP0725() {
    return P0725;
}


public void setP0725(int p0725) {
    P0725 = p0725;
}


public int getP0731() {
    return P0731;
}


public void setP0731(int p0731) {
    P0731 = p0731;
}


public int getShowType() {
    return showType;
}


public void setShowType(int showType) {
    this.showType = showType;
}
	    
}
