package com.hjsj.hrms.businessobject.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:PlanTaskTreeTableBo.java</p>
 * <p>Description:生成计划任务列表</p> 
 * <p>Company:hjsj</p> 
 * create time at:2014-7-11 上午10:29:54 
 * @author dengcan
 * @version 6.x
 */
public class PlanTaskListBo {
	private Connection conn = null;
	private RecordVo p07_vo = null; 
    private int P0723; //查看的计划类型 1 人员 2 部门
    private int P0725;//期间类型
    private int P0727;//年
    private int P0729;//月 根据期间类型不同代码月份、季度、上半年
    private int P0731;//周
    private int P0700;//
	private UserView userView;
	/**
	 * 
	 * @param _conn
	 * @param _p0700
	 */
	public PlanTaskListBo(Connection _conn,int _p0700)
	{
		this.conn=_conn;
		this.P0700= _p0700;
		if(_p0700!=0) {
            this.p07_vo=getP07Vo(_p0700);
        }
	}
	
	public PlanTaskListBo(Connection _conn,int _p0700, UserView userView)
	{
		this.conn=_conn;
		this.P0700= _p0700;
		this.userView = userView;
		if(_p0700!=0) {
            this.p07_vo=getP07Vo(_p0700);
        }
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
 * @Title: getunFinishedTaskListMap   
 * @Description: 获取上期未完成的任务、全部任务列表   
 * @param @return 
 * @return HashMap 
 * @author:wangrd   
 * @throws   
*/
public HashMap getunFinishedTaskListMap()
    {
        HashMap map=new HashMap();
        ArrayList headList=getHeadList(); //获得计划任务列表表头指标 （1阶段固定表头  后期可配置）
        map.put("dataModel", getDataModel(headList));
        
        PlanTaskTreeTableBo taskTreeBo=new PlanTaskTreeTableBo(this.conn,P0700);
        ArrayList tableList =taskTreeBo.getTableData("");
        
       
        WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
		if(util.isOpenCooperationTask()){//启用协作任务
        	tableList = taskTreeBo.getListExceptCooperation(tableList);// 协作任务：没有审批的协作任务不显示 chent
        }
        tableList= taskTreeBo.getOrderedTableList(tableList);  
        //过滤 自己创建的、已批的、
        ArrayList tableListnotFinish=getunFinishedTaskList(tableList, "0"); // 0:上期未完成任务  chent 20160415
        ArrayList tableListAll=getunFinishedTaskList(tableList, "1"); //1： 上期全部任务 chent 20160415
        map.put("dataJson", getTaskListJson(tableListnotFinish));
        map.put("dataJsonAll", getTaskListJson(tableListAll));
        map.put("panelColumns",getPanelColumns(headList));
        return map;
    }


    /**   
     * @Title: getunFinishedTaskList   
     * @Description: 自己创建的 未完成的任务   
     * @param @param tableDataList
     * @param @return 
     * @return ArrayList 
     * @return isAllTaskData  是否复制全部任务
     * @author:szk   
     * @throws   
    */
    public ArrayList getunFinishedTaskList(ArrayList tableDataList, String isAllTaskData)
    {
        ArrayList newDataList = new ArrayList();
        DynaBean dynaBean=null;
        for(int i=0; i<tableDataList.size(); i++)
        {
            dynaBean=(DynaBean)tableDataList.get(i);
            String p0811=(String)dynaBean.get("p0811");
            int p0833=(String)dynaBean.get("p0833")==""?0: Integer.parseInt((String)dynaBean.get("p0833"));
            String p0809=(String)dynaBean.get("p0809");
            //if (String.valueOf(this.P0700).equals(p0700)){//自己创建的
                String p0835 =(String)dynaBean.get("p0835");
                if (p0835==null || p0835.length()<1) {
                    p0835="0";
                }
                //由于下述校验这里若有百分号则替换掉
                p0835 = p0835.replaceAll("%", "");
                //选为完成，未取消的
                if (Integer.parseInt(p0835)<100 || "1".equals(isAllTaskData)){//是否复制全部任务 chent 20160415
                    if ((WorkPlanConstant.TaskStatus.APPROVE.equals(p0811) || WorkPlanConstant.TaskStatus.APPROVED.equals(p0811))
                     && !(WorkPlanConstant.TaskChangedStatus.Cancel == p0833 || WorkPlanConstant.TaskExecuteStatus.CANCEL.equals(p0809)))
                    {
                    	//与父页面任务进度保持一致 加百分号
                    	dynaBean.set("p0835", p0835+"%");
                        newDataList.add(dynaBean);
                    }
                }
            
        }
        return newDataList;
    } 
 
    /**   
     * @Title: getDataModel   
     * @Description: 定义的列模型   
     * @param @param headList
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getDataModel(ArrayList headList)
    {
        StringBuffer _json=new StringBuffer("[{name:'p0800',type:'string'}");
        _json.append(",{name:'_level',type:'int'}");
     
        FieldItem item=null;
        // p0800,p0835,rank,pr
        for(int i=0;i<headList.size();i++)
		{
        	item = (FieldItem) headList.get(i);
			String item_id=item.getItemid();
			String type=item.getItemtype();
			if("A".equalsIgnoreCase(type)|| "M".equalsIgnoreCase(type)|| "p0835".equalsIgnoreCase(item_id)|| "rank".equalsIgnoreCase(item_id))  //p0835:进度  ||item_id.equalsIgnoreCase("p0835_jd")||item_id.equalsIgnoreCase("p0835_state")
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
        _json.append("]");
        return _json.toString();        
    }
    
     
    /**   
     * @Title: getTaskListJson   
     * @Description:表格数据json    
     * @param @param taskList
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getTaskListJson(ArrayList taskList)
    {
        StringBuffer _json=new StringBuffer("[");
        PlanTaskTreeTableBo taskTreeBo=new PlanTaskTreeTableBo(this.conn,P0700, this.userView);
        HashMap taskMemberMap = new HashMap();
		if(taskList.size()>0) {
            taskMemberMap=taskTreeBo.getTaskMemberMap(taskList);
        }
        try{
            DynaBean dynaBean;
            for(int i=0;i<taskList.size();i++)
            { 
                dynaBean=(DynaBean)taskList.get(i);          
                String p0801=(String)dynaBean.get("p0801"); 
                String p0800=(String)dynaBean.get("p0800"); 
                String p0835=(String)dynaBean.get("p0835"); 
                int _level= Integer.parseInt((String)dynaBean.get("_level")); 
                _json.append("{seq:1,");
                _json.append("p0800:'");
                _json.append(WorkPlanUtil.encryption((String)dynaBean.get("p0800")));
                _json.append("',_level:");
                _json.append(_level);
                _json.append(",p0801:'");
                //任务名称当中有换行的换，页面会报错，加密处理  haosl 2018-3-13
                _json.append(SafeCode.encode(p0801));      
                _json.append("',principal:'");
                _json.append((String)taskMemberMap.get(p0800+"/1"));               
                _json.append("',p0835:'");
                _json.append(p0835);               
                _json.append("'");               
        		String value = (String) dynaBean.get("rank");
				if (value == null || "".equals(value)) {
                    value = "0";
                }
				if (value.length() > 0) {
					double dValue = PubFunc.parseDouble(value) * 100;
					if (dValue == 0.0) {
						value = "0";
					}
					else {
						value = dValue+"";
					}
				}
				_json.append(",rank:'" + value );
                _json.append("'},");
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } 
        if (",".equalsIgnoreCase(_json.substring(_json.length()-1, _json.length()))) {
            _json.setLength(_json.length()-1);
        }
        _json.append("]");
        return _json.toString();
    } 
    
    
    private String getBlank(int num) {
        String str="";
        for (int i=0;i<num;i++){
            str=str+"  ";
        }
        return str;
    }
	
    
    /**   
     * @Title: getPanelColumns   
     * @Description: 显示的列   
     * @param @param headList
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getPanelColumns(ArrayList headList)
    {//,sortable:true ,hideable:false,hidden: true 
        StringBuffer column_str=new StringBuffer("[");
        column_str.append("{text:'任务名称',dataIndex: 'p0801',align:'left',flex: 6,sortable: false,menuDisabled:true,renderer:addUnfinishedTaskNameCss  }");
        column_str.append(",{text:'负责人',dataIndex: 'principal',align:'center',flex: 1,sortable: false,menuDisabled:true  }");
        //linbz 32450',renderer:addPrincipalValue'复制任务不需要添加转负责人函数
        column_str.append(",{text:'任务进度',dataIndex: 'p0835',align:'center',flex: 1,sortable: false,menuDisabled:true  }");
        column_str.append("]"); 
        return column_str.toString();
    }
    
	
	
   private ArrayList getHeadList()
    {
        ArrayList list=new ArrayList();
        //list.add(getFieldItem("seq","序号","A","0",50,0,40));
        FieldItem item=DataDictionary.getFieldItem("p0801");
        item.setFormula("300");
        list.add(item); //任务名称     
        list.add(getFieldItem("principal","负责人","A","0",50,0,70));
        
        item=DataDictionary.getFieldItem("p0835");
        item.setFormula("300");
        list.add(item); //完成进度 
        
        FieldItem _item=DataDictionary.getFieldItem("rank");
		item=(FieldItem)_item.clone();
		item.setItemtype("A");
		item.setFormula("70");
		list.add(item); //权重
        return list;
    }
   
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


  //获取生成excel的数据
  	/*public String[][] getExcelData(ArrayList list) 
  	{
  		ContentDAO dao = new ContentDAO(this.conn);
  		int n = list.size();
  		String[][] data = new String[n+1][]; // 定义二维数组
  		for (int i = 0; i <=n; i++) {
  			ArrayList tempArray = new ArrayList();
  			if (i == 0) {
  				tempArray.add(0, "任务名称 ");
  				tempArray.add(1, "开始时间");
  				tempArray.add(2, "结束时间");
  				tempArray.add(3, "任务负责人");
  				tempArray.add(4, "任务描述");
  				tempArray.add(5, "标准权重");
  				tempArray.add(6, "评价标准");
  			} else {
  				DynaBean taskbean=(DynaBean)list.get(i-1);
  				//获取当前负责人信息
  				String principal="";
  				try {
  					StringBuffer p0800_str = new StringBuffer("");
  					p0800_str.append((String) taskbean.get("p0800"));
  					RowSet rowSet = dao.search("select * from p09 where p0901=2 and p0903 in ("
  									+ p0800_str.toString()
  									+ ") order by p0900");
  					while (rowSet.next()) {
  						String p0905 = rowSet.getString("p0905"); // 1、负责人
  						if (p0905.equals("1")) {
  							principal = rowSet.getString("P0913"); // 姓名
  						}

  					}
  				String level=(String)taskbean.get("_level");//获得当前任务的层级
  				String taskname=getAddBlankTaskName(Integer.parseInt(level),(String)taskbean.get("p0801"));
  				tempArray.add(0,taskname);
  				tempArray.add(1,(String)taskbean.get("p0813"));
  				tempArray.add(2,(String)taskbean.get("p0815"));
  				tempArray.add(3, principal);
  				tempArray.add(4,(String)taskbean.get("p0803"));
  				String rank=(String)taskbean.get("rank");
  				if(!"".equals(rank)&&!"0.0".equals(rank)){
  					//处理rank值小数点后位数 
  					float rank1= Float.parseFloat(rank);
  					rank1=(float)(Math.round(rank1*100))/100f;
  					rank=rank1+"";
  				} 
  				tempArray.add(5,rank);
  				tempArray.add(6,(String)taskbean.get("p0841"));
  				} catch (Exception e) {
  					e.printStackTrace();
  				}
  			}
  			String[] s = new String[tempArray.size()];
  			data[i] = (String[]) tempArray.toArray(s);

  		}
  		return data;
  		
  	}*/
  //获取生成excel的数据
	public String[][] getExcelData(ArrayList list) 
  	{
		//组装headlist
		ArrayList Headlist=new ArrayList();
        FieldItem taskp0801=DataDictionary.getFieldItem("p0801");
        Headlist.add(taskp0801);
        FieldItem taskp0813=DataDictionary.getFieldItem("p0813");
        Headlist.add(taskp0813);
        FieldItem taskp0815=DataDictionary.getFieldItem("p0815");
        Headlist.add(taskp0815);
        FieldItem taskprincipal=(FieldItem)taskp0801.clone();
        taskprincipal.setItemid("principal");
        taskprincipal.setItemdesc("任务负责人");
        Headlist.add(taskprincipal);
        FieldItem taskp0803=DataDictionary.getFieldItem("p0803");
        Headlist.add(taskp0803);
        FieldItem taskrank=DataDictionary.getFieldItem("rank");
        Headlist.add(taskrank);
        FieldItem taskp0841=DataDictionary.getFieldItem("p0841");
        Headlist.add(taskp0841);
        
		ContentDAO dao = new ContentDAO(this.conn);
		int n = list.size();
		String[][] data = new String[n + 1][]; // 定义二维数组
		for (int i = 0; i <= n; i++) {
			ArrayList tempArray = new ArrayList();
			for (int j = 0; j < Headlist.size(); j++) {
				FieldItem fieldItem = (FieldItem) Headlist.get(j);
				if (i == 0) {
					String desc = (String) fieldItem.getItemdesc();
					tempArray.add(j, desc);
				} else {
					DynaBean taskbean = (DynaBean) list.get(i - 1);
					String item = (String) fieldItem.getItemid();
					if ("p0801".equals(item)) {// 处理任务层级
						String level = (String) taskbean.get("_level");// 获得当前任务的层级
						String taskname = getAddBlankTaskName(Integer
								.parseInt(level), (String) taskbean.get(item));
						taskbean.set("p0801", taskname);
					} else if ("rank".equals(item)) {// 处理rank值小数点后位数
						String rank = (String) taskbean.get(item);
						if (!"".equals(rank) && !"0.0".equals(rank)) {
							float rank1 = Float.parseFloat(rank);
							rank1 = (float) (Math.round(rank1 * 100)) / 100f;
							rank = rank1 + "";
						}
						taskbean.set("rank", rank);
					} else if ("principal".equals(item)) {// 处理负责人信息
						String principal = "";
						try {
							StringBuffer p0800_str = new StringBuffer("");
							p0800_str.append((String) taskbean.get("p0800"));
							RowSet rowSet = dao
									.search("select * from p09 where p0901=2 and p0903 in ("
											+ p0800_str.toString()
											+ ") order by p0900");
							while (rowSet.next()) {
								String p0905 = rowSet.getString("p0905"); // 1、负责人
								if ("1".equals(p0905)) {
									principal = rowSet.getString("P0913"); // 姓名
								}
								taskbean.set("principal", principal);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					tempArray.add(j, taskbean.get(item));
				}
				String[] s = new String[tempArray.size()];
				data[i] = (String[]) tempArray.toArray(s);
			}
		}
		return data;
  		
  	}
  	//处理任务名称现实层级关系
  	 private String getAddBlankTaskName(int level,String taskname){
  		 String blank="";
  		 while(level>1){
  			 blank=blank+"  ";
  			 level--;
  		 }
  		    
  		return blank+taskname;
  		 
  	 }
 	
  	 
  	private Connection getFrameconn() {
  		// TODO Auto-generated method stub
  		return null;
  	}


  	//解密任务id
  	public String decodeTaskid(String task_ids){
  		  String[] arrids = task_ids.split(",");
            String ids = "";
            for (int i = 0; i < arrids.length; i++) {
                String id = arrids[i];
                if ("".equals(id)) {
                    continue;
                }
                id = SafeCode.decode(id);
                id = WorkPlanUtil.decryption(id);
                if (ids.length() == 0) {
                    ids = id;
                } else {
                    ids = ids + "," + id;
                }
            }
  		return ids;
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
    
}
