package com.hjsj.hrms.businessobject.workplan.plan_track;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class PlanTaskTreeHrBo {
    private final String lockedFieldStr="/seq/p0801/";   //默认锁定列
    private final String specialFieldStr="/principal/participant/timearrange/gantt/p0835/taskprogresscolor/"; ///p0835_state/p0835_jd/";   //特殊列获取数据时得单独考虑   
    private Connection conn = null;
    private UserView userView;
    private String planType; //查看的计划类型 1 人员 2 部门
    private String periodType;//期间类型
    private String periodYear;//年
    private String periodMonth;//月 根据期间类型不同代码月份、季度、上半年
    private String periodWeek;//周
    private int P0723; //查看的计划类型 1 人员 2 部门
    private int P0725;//期间类型
    private int P0727;//年
    private int P0729;//月 根据期间类型不同代码月份、季度、上半年
    private int P0731;//周
    private int weekNum;//本月周数
    
    private String returnInfo;//
    private RecordVo p07_vo = null; 
    ArrayList headList=null;

    public PlanTaskTreeHrBo(Connection _conn,UserView userview,String plantype,
            String periodtype,String periodyear,String periodmonth,String periodweek)
    {
        this.conn=_conn;
        this.userView=userview;
        
        this.planType=plantype;
        this.periodType=periodtype;
        this.periodYear=periodyear;
        this.periodMonth=periodmonth;
        this.periodWeek=periodweek;
        try {
            this.P0723= Integer.parseInt(plantype);
            this.P0725=Integer.parseInt(periodtype);
            this.P0727=Integer.parseInt(periodyear);
            this.P0729=Integer.parseInt(periodmonth);
            this.P0731=Integer.parseInt(periodweek);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public RecordVo getP07Vo(int p0700) {
        RecordVo vo = new RecordVo("p07");
        try {
            vo.setInt("p0700",p0700);
            ContentDAO dao = new ContentDAO(this.conn);
            vo = dao.findByPrimaryKey(vo);
                
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }
    
    
    public HashMap getTreePanelMap(ArrayList peopleList)
    {
        HashMap map=new HashMap();
        headList=getHeadList(); //获得计划任务列表表头指标 （1阶段固定表头  后期可配置）
        map.put("dataModel", getDataModel());
        map.put("panelColumns",getPanelColumns());
        if("2".equals(this.planType)){
            map.put("dataJson", getDeptTaskListJson(peopleList));
        }
        else {
            map.put("dataJson", getPeopleTaskListJson(peopleList));
        }
      //  map.put("dataJson", "{text:'.',children:[]}");
        
        return map;
    }
    
    /**
     * 定义EXT需要的数据结构和类型
     * @author dengcan
     * @serialData 2014-07-12
     * @return
     */
    private String getDataModel()
    {
        int p0725= this.P0725;
        StringBuffer _json=new StringBuffer("[{name:'p0800',type:'string'}" +
        		",{name:'objectid',type:'string'},{name:'p0723',type:'string'}" +
        		",{name:'p0700',type:'string'},{name:'p0833',type:'string'},{name:'p0809',type:'string'}");
        _json.append(",{name:'img_path',type:'string'}");
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
            
        }
        _json.append("]");
        return _json.toString();
        
    }
    
    
    /**
     * 获得计划任务列表表头指标 （1阶段固定表头  后期可配置）
     * @param showType 任务列表视图类型  1：计划制定   2：计划跟踪
     * @param p0723 1：人员计划  2：团队计划  3：项目
     * @author dengcan
     * @serialData 2014-07-12
     * @return
     */
    private ArrayList getHeadList()
    {
        FieldItem item=null;
        int p0723=this.P0723;
        ArrayList list=new ArrayList();
        list.add(getFieldItem("p0801","任务名称","A","0",200,0,350));
        if(p0723==2)  //p0723-> 1:人员计划  2：团队计划  3:项目计划
        {
            item=DataDictionary.getFieldItem("p0843");
            if (item!=null){
                list.add(item);//岗位
            }
            list.add(getFieldItem("principal","负责人","A","0",50,0,70));
        }
        if(p0723==1)  
        {
            item=DataDictionary.getFieldItem("p0823");
            if (item!=null){
                list.add(item);//任务分类
            }
        }
        list.add(getFieldItem("participant","参与人","A","0",100,0,100)); 
        if(p0723==2)  
        {
            item=DataDictionary.getFieldItem("p0839");
            item.setFormula("150");
            list.add(item); //协作部门
        }
        /* 权重*/
        list.add(getFieldItem("rank","权重","A","0",10,0,50));     
        item=DataDictionary.getFieldItem("p0835"); //完成进度
        item.setFormula("80");
        list.add(item);
        list.add(getFieldItem("gantt","甘特图","A","0",50,0,200)); //甘特图   
        list.add(getFieldItem("taskprogresscolor","颜色提示图","A","0",50,0,220)); //颜色提示图
        if (p0723==1) {
            return list;
        }
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
     * 获得EXT表格的列描述数组
     * @param headList
     * @author dengcan
     * @serialData 2014-07-12
     * @return
     */
    private String getPanelColumns()
    {//,sortable:true ,hideable:false,hidden: true 
        StringBuffer column_str=new StringBuffer("[");
        String lock_str=",locked: true";
        column_str.append("");
        FieldItem item=null;
        for(int i=0;i<headList.size();i++)
        {
            item = (FieldItem) headList.get(i);
            if("taskprogresscolor".equalsIgnoreCase(item.getItemid()))  //颜色提示图
			{
				continue;
			}
            if("gantt".equalsIgnoreCase(item.getItemid()))  //甘特图
            {
                column_str.append(getGantColumns());
            }
            else
            { 
                if (column_str.toString().length()>1){
                    column_str.append(","); 
                }
                column_str.append("{text: '"+item.getItemdesc()+"',dataIndex: '"+item.getItemid().toLowerCase()+"', sortable: false,width:"+item.getFormula()); //item.getFormula()放的是列的默认宽度，
                if(lockedFieldStr.indexOf("/"+item.getItemid().toLowerCase()+"/")!=-1)  //默认锁定列
                {
                    column_str.append(lock_str+",menuDisabled:true"); 
                }
                if("p0801".equalsIgnoreCase(item.getItemid())) //任务名称,出现展开图标,添加链接
                {
                    column_str.append(",xtype:'treecolumn'");
                    column_str.append(",renderer:addLink }");
                }
                else if("rank".equalsIgnoreCase(item.getItemid())) {
                    column_str.append(" ,renderer:addRankValue,menuDisabled:true}");
                } else if("p0835".equalsIgnoreCase(item.getItemid())) {
                    column_str.append(" ,renderer:drawWarning,menuDisabled:true}");
                } else if("p0823".equalsIgnoreCase(item.getItemid())) {
                    column_str.append(" ,renderer:p0823,menuDisabled:true}");
                } else if("principal".equalsIgnoreCase(item.getItemid())) {
                    column_str.append(" ,renderer:addPrincipalValue,menuDisabled:true}");
                } else if("participant".equalsIgnoreCase(item.getItemid()))//参与人
                {
                    column_str.append(" ,renderer:rendercipant,menuDisabled:true}");
                }
                /*
                else if(item.getItemid().equalsIgnoreCase("p0835_state"))
                    column_str.append(" ,renderer:drawWarning}");
                else if(item.getItemid().equalsIgnoreCase("p0835_jd"))
                    column_str.append(" ,renderer:drawWarningJd}");
                */
                else {
                    column_str.append(",renderer:addGridCss,menuDisabled:true}");
                }
            }
            
        }
        column_str.append("]"); 
        return column_str.toString();
    }
    
    
    /**
     * 获得甘特图columns信息
     * @return
     */
    private String getGantColumns()
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
            String width2="30";
            if (this.P0723==2){
                width="26";
                width2="30";
                
            }
                
            _str.append(",{ text:'1季度',columns:[{text:'1',width: "+width+",renderer:drawGrid_left_right,align:'center', dataIndex: 'one',menuDisabled:true},{text:'2',align:'center',width: "+width+",renderer:drawGrid_right, dataIndex: 'two',menuDisabled:true},{text:'3',align:'center',width: "+width+",renderer:drawGrid_right, dataIndex: 'three',menuDisabled:true} ],menuDisabled:true}");
            _str.append(",{ text:'2季度',columns:[{text:'4',width: "+width+",renderer:drawGrid_right,align:'center', dataIndex: 'four',menuDisabled:true},{text:'5',align:'center',width: "+width+",renderer:drawGrid_right, dataIndex: 'five',menuDisabled:true},{text:'6',align:'center',width: "+width+",renderer:drawGrid_right, dataIndex: 'six',menuDisabled:true} ],menuDisabled:true}");
            _str.append(",{ text:'3季度',columns:[{text:'7',width: "+width+",renderer:drawGrid_right,align:'center', dataIndex: 'seven',menuDisabled:true},{text:'8',align:'center',width: "+width+",renderer:drawGrid_right, dataIndex: 'eight',menuDisabled:true},{text:'9',align:'center',width: "+width+",renderer:drawGrid_right, dataIndex: 'nine',menuDisabled:true} ],menuDisabled:true}");
            _str.append(",{ text:'4季度',columns:[{text:'10',width: "+width2+",renderer:drawGrid_right,align:'center', dataIndex: 'ten',menuDisabled:true},{text:'11',align:'center',width: "+width2+",renderer:drawGrid_right, dataIndex: 'eleven',menuDisabled:true},{text:'12',align:'center',width: "+width2+",renderer:drawGrid_right, dataIndex: 'twelve',menuDisabled:true} ],menuDisabled:true}");
        }
        else if(this.P0725==2) //半年计划
        {
            if(this.P0729==1) //上半年
            {
                _str.append(",{ text:'1季度',columns:[{text:'1',width: 50,renderer:drawGrid_left_right,align:'center', dataIndex: 'one',menuDisabled:true},{text:'2',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'two',menuDisabled:true},{text:'3',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'three',menuDisabled:true} ],menuDisabled:true}");
                _str.append(",{ text:'2季度',columns:[{text:'4',width: 50,renderer:drawGrid_right,align:'center', dataIndex: 'four',menuDisabled:true},{text:'5',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'five',menuDisabled:true},{text:'6',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'six',menuDisabled:true} ],menuDisabled:true}");
            }
            else if(this.P0729==2) //下半年
            {
                _str.append(",{ text:'3季度',columns:[{text:'7',width: 50,renderer:drawGrid_left_right,align:'center', dataIndex: 'seven',menuDisabled:true},{text:'8',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'eight',menuDisabled:true},{text:'9',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'nine',menuDisabled:true} ],menuDisabled:true}");
                _str.append(",{ text:'4季度',columns:[{text:'10',width: 50,renderer:drawGrid_right,align:'center', dataIndex: 'ten',menuDisabled:true},{text:'11',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'eleven',menuDisabled:true},{text:'12',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'twelve',menuDisabled:true} ],menuDisabled:true}");
            }
        }
        else if(this.P0725==3) //季度计划
        { 
            if(this.P0729==1) //一季度
            {
                _str.append(",{ text:'1季度',columns:[{text:'1',width: 50,renderer:drawGrid_left_right,align:'center', dataIndex: 'one',menuDisabled:true},{text:'2',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'two',menuDisabled:true},{text:'3',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'three',menuDisabled:true} ],menuDisabled:true}");
            }
            else if(this.P0729==2) //二季度
            {
                _str.append(",{ text:'2季度',columns:[{text:'4',width: 50,renderer:drawGrid_left_right,align:'center', dataIndex: 'four',menuDisabled:true},{text:'5',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'five',menuDisabled:true},{text:'6',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'six',menuDisabled:true} ],menuDisabled:true}");
            }   
            else if(this.P0729==3) //三季度
            {
                _str.append(",{ text:'3季度',columns:[{text:'7',width: 50,renderer:drawGrid_left_right,align:'center', dataIndex: 'seven',menuDisabled:true},{text:'8',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'eight',menuDisabled:true},{text:'9',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'nine',menuDisabled:true} ],menuDisabled:true}");
            }   
            else if(this.P0729==4) //四季度
            {
                _str.append(",{ text:'4季度',columns:[{text:'10',width: 50,renderer:drawGrid_left_right,align:'center', dataIndex: 'ten',menuDisabled:true},{text:'11',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'eleven',menuDisabled:true},{text:'12',align:'center',width: 50,renderer:drawGrid_right, dataIndex: 'twelve',menuDisabled:true} ],menuDisabled:true}");
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
                    renderer="drawGrid_left_right";
                }
                String colName=getNumberToEn(i);
                _str.append(",{text:'"+colDesc+"',width: 50,renderer:"+renderer
                        +",align:'center', dataIndex: '"+colName+"',menuDisabled:true}");
                
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
                    renderer="drawGrid_left_right";
                }
                _str.append(",{text:'"+colDesc+"',width: 50,renderer:"+renderer
                        +",align:'center', dataIndex: '"+colName+"',menuDisabled:true}");
                calendar.add(Calendar.DATE, 1);
                
            }
        }
        return _str.toString();
    }
    
    
    /**   
     * @Title: getPeopleTaskListJson   
     * @Description: 获取人员任务列表   
     * @param @param peopleList
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getPeopleTaskListJson(ArrayList peopleList)
    {
        StringBuffer _json=new StringBuffer("{text:'.',children:[");
        try{
            DynaBean dynaBean;
            WorkPlanBo planbo= new WorkPlanBo(this.conn,this.userView);
            for(int i=0;i<peopleList.size();i++)
            { 
                dynaBean=(DynaBean)peopleList.get(i);
                String a0101=(String)dynaBean.get("a0101");
                String a0100=(String)dynaBean.get("a0100");
                String nbase=(String)dynaBean.get("nbase");
                String objectid=nbase+a0100;
                //objectid="usr00000009";
                planbo.initPlan(objectid,this.planType,
                        this.periodType,this.periodYear,this.periodMonth,this.periodWeek);
                RecordVo _p07vo=planbo.getP07_vo();
                int p0700=0;
                if (_p07vo!=null){                    
                    p0700=_p07vo.getInt("p0700");
                }
                _json.append("{seq:1,p0800:'0',p0833:'0',p0809:'0'"
                        +",p0700:''"
                        +",objectid:'"+WorkPlanUtil.encryption(objectid)+"'"
                        +",p0723:'"+WorkPlanUtil.encryption(this.planType)+"'"
                        ); 
                String taskjson="";
                returnInfo="0";
                if (p0700>0){
                    this.p07_vo=getP07Vo(p0700);                    
                    taskjson=this.getPersonTaskDataJson();
                }
                String taskNum="("+returnInfo+")";
                _json.append(",p0801:'"+a0101+taskNum+"'");
                String imagePath =new WorkPlanBo(this.conn,this.userView).getPhotoPath(nbase, a0100);
                _json.append(",img_path:'"+imagePath+"'");
                
                addPersonInfoDataJson(_json);
                
                if (taskjson.length()>0){
                    _json.append(",children:[");
                    _json.append(taskjson);
                    _json.append("]");
                }
                else {
                    _json.append(",leaf:true");
                }
                _json.append("},");
                
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } 
   
        if (",".equalsIgnoreCase(_json.substring(_json.length()-1, _json.length()))) {
            _json.setLength(_json.length()-1);
        }
        _json.append("]}");
        return _json.toString();
    } 

    /**   
     * @Title: getDeptTaskListJson   
     * @Description: 获取部门的任务列表   
     * @param @param deptList
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private String getDeptTaskListJson(ArrayList deptList)
    {
        StringBuffer _json=new StringBuffer("{text:'.',children:[");
        try{
            DynaBean dynaBean;
            WorkPlanBo planbo= new WorkPlanBo(this.conn,this.userView);
            WorkPlanUtil planUtil= new WorkPlanUtil(this.conn,this.userView);
            ContentDAO dao= new ContentDAO(this.conn);
            for(int i=0;i<deptList.size();i++)
            { 
                dynaBean=(DynaBean)deptList.get(i);
                String _e0122 = (String)dynaBean.get("b0110");
                String _e01a1 = (String)dynaBean.get("e01a1");
                String _e0122desc = (String)dynaBean.get("codeitemdesc");
                String nbase="";String a0100="";String a0101="";
                String  tablename = planUtil.getPeopleSqlByE01a1(_e01a1);
                if (!"".equals(tablename)) {
                    tablename = "(" + tablename + ") T";
                    String strsql = "select T.* from " + tablename;
                    RowSet rset1 = dao.search(strsql);
                    if (rset1.next()) {// 有人负责
                        nbase = rset1.getString("nbase");
                        a0100 = rset1.getString("a0100");            
                        a0101 = rset1.getString("a0101");
                    }
                };
                String objectid=_e0122;
                planbo.initPlan(objectid,this.planType,
                        this.periodType,this.periodYear,this.periodMonth,this.periodWeek);
                RecordVo _p07vo=planbo.getP07_vo();
                int p0700=0;
                if (_p07vo!=null){                    
                    p0700=_p07vo.getInt("p0700");
                }
                _json.append("{seq:1,p0800:'0',p0833:'0',p0809:'0'"
                        +",p0700:''"
                        +",objectid:'"+WorkPlanUtil.encryption(objectid)+"'"
                        +",p0723:'"+WorkPlanUtil.encryption(this.planType)+"'"
                        ); 
                String taskjson="";
                returnInfo="0";
                if (p0700>0){
                    this.p07_vo=getP07Vo(p0700);                    
                    taskjson=this.getPersonTaskDataJson();
                }
                String taskNum="("+a0101+","+returnInfo+")";
                _json.append(",p0801:'"+_e0122desc+taskNum+"'");
                String imagePath =new WorkPlanBo(this.conn,this.userView).getPhotoPath(nbase, a0100);
                _json.append(",img_path:'"+imagePath+"'");
                
                addPersonInfoDataJson(_json);
                
                if (taskjson.length()>0){
                    _json.append(",children:[");
                    _json.append(taskjson);
                    _json.append("]");
                }
                else {
                    _json.append(",leaf:true");
                }
                _json.append("},");
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } 
   
        if (",".equalsIgnoreCase(_json.substring(_json.length()-1, _json.length()))) {
            _json.setLength(_json.length()-1);
        }
        _json.append("]}");
        return _json.toString();
    } 
    /**
     * 获得计划任务的JSON格式数据
     * @author dengcan
     * @serialData 2014-07-12
     * @return
     */
    private String getPersonTaskDataJson()
    {
        StringBuffer _json=new StringBuffer();
        ArrayList tableDataList=new ArrayList();
        HashMap taskMemberMap=new HashMap();
        HashMap scoreMap = new HashMap();
        PlanTaskTreeTableBo taskTreeBo= new PlanTaskTreeTableBo(this.conn,this.p07_vo.getInt("p0700"),this.userView);
        tableDataList=taskTreeBo.getTableData(""); //获得计划下的任务数据
        
        if(tableDataList.size()>0) {
            taskMemberMap=taskTreeBo.getTaskMemberMap(tableDataList);
        }
        	scoreMap = taskTreeBo.getTaskScoreMap(tableDataList);
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
            if((((String)dynaBean.get("p0800")).equals((String)dynaBean.get("p0831")))||p0800Map.get(p0831)==null){
                seq++;
                taskTreeBo.executeJsonData(_json,headList,dynaBean,String.valueOf(seq),tableDataList,taskMemberMap, scoreMap);
                
            }
        }
        returnInfo=String.valueOf(seq);
        if (_json.toString().length()>0){
            if (",".equalsIgnoreCase(_json.substring(_json.length()-1, _json.length()))) {
                _json.setLength(_json.length()-1);
            }
            
        }
        
        return _json.toString();
    } 
    
    
   /**   
 * @Title: addPersonInfoDataJson   
 * @Description:增加人员所在的行的json    
 * @param @param _json 
 * @return void 
 * @author:wangrd   
 * @throws   
*/
private void addPersonInfoDataJson(StringBuffer _json)
    {
       FieldItem item = null;
        for (int i = 0; i < headList.size(); i++) {            
            item = (FieldItem) headList.get(i);
            if ("p0801".equals(item.getItemid().toLowerCase())){
                continue;
            }
            if (specialFieldStr.indexOf("/" + item.getItemid().toLowerCase() + "/") == -1) {
                if ("N".equalsIgnoreCase(item.getItemtype())) {
                    _json.append("," + item.getItemid().toLowerCase() + ":" 
                            + "");

                } else {
                    _json.append("," + item.getItemid().toLowerCase() + ":'" +"" + "'");
                }
            } else {
                _json.append("," + item.getItemid().toLowerCase() + ":'" +"" + "'");

            }
        }
       
    }
    
    
    
  
    
    
}
