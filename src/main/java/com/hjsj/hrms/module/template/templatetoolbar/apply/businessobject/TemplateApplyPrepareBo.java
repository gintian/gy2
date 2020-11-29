package com.hjsj.hrms.module.template.templatetoolbar.apply.businessobject;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings("all")
public class TemplateApplyPrepareBo {

	private UserView userView;
	private Connection conn=null;
	
	private TemplateFrontProperty frontProperty;
	private TemplateParam paramBo = null;
	private ContentDAO dao;
	
	public TemplateApplyPrepareBo(Connection conn,UserView userview,TemplateParam paramBo,TemplateFrontProperty frontProperty){
		this.conn = conn;
		this.userView=userview;
		this.frontProperty=frontProperty; 
        this.paramBo = paramBo;
        dao = new ContentDAO(conn);   
        
	}
	


    
    /**   
     * @Title: validateIsSelect   
     * @Description:  校验是否选中数据  
     * @param flag :1 报批 ; 2 驳回 ;  3 ：批准 ; 4:手动审批 0:提交 10 上会
     * @param @throws GeneralException 
     * @return String 
     * @throws   
    */
    public String validateIsSelect(String applyFlag)throws GeneralException
    {
    	RowSet rSet= null;
        String info="";
        String desc="报批";
        if ("0".equals(applyFlag)){
            desc="提交";
        }
        else if ("10".equals(applyFlag)) {
           desc="上会"; 
        }
        try
        {
            String selectFlag="1";  // 0:有未选择的记录（不是全选）: 1:全选  2:表中没有数据 4：一条也没有选中（全不选）
            TemplateUtilBo utilBo= new TemplateUtilBo(conn,this.userView);            
            String tableName=utilBo.getTableName(this.frontProperty.getModuleId(),
                    Integer.parseInt(this.frontProperty.getTabId()), this.frontProperty.getTaskId());
            if("0".equals(this.frontProperty.getTaskId()))//起草
            {
                int count=0;//未选中的数据条数
                StringBuffer sb = new StringBuffer("");
    
                sb.append("select count(*) from "+tableName+" where  "+Sql_switcher.isnull("submitflag", "0")+"=1 ");
                rSet=dao.search(sb.toString());
                if(rSet.next())
                {
                    count=rSet.getInt(1);
                    if(count<1){
                        selectFlag="4";
                        info ="请选中需要"+desc+"的记录！";
                    }
                }
                
            }
            else
            {
                
                String sqlstr ="";
                if(this.frontProperty.isBatchApprove())//如果是批量处理
                { 
                    info=validateBatchTasks(desc,tableName,applyFlag);
                }
                else
                {
                    /*String sql="select count(seqnum) from t_wf_task_objlink where tab_id="+this.frontProperty.getTabId()+" and task_id="+this.frontProperty.getTaskId()+" and "+Sql_switcher.isnull("submitflag", "0")+"=0 ";
                    sql+=" and  "+Sql_switcher.isnull("state","0")+"!=3   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";
                    RowSet rSet=dao.search(sql);
                    if(rSet.next())
                    {
                        if(rSet.getInt(1)>0){
                        	if(!"10".equals(applyFlag)){
                        		selectFlag="0";
                                info ="请选中所有记录！";
                        	}
                        }
                    }*/
                    sqlstr ="select count(seqnum) from t_wf_task_objlink where tab_id="+this.frontProperty.getTabId()+" and task_id="+this.frontProperty.getTaskId()+" ";
                    sqlstr+=" and  "+Sql_switcher.isnull("state","0")+"!=3   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";
                    //if("10".equals(applyFlag)){
                    	sqlstr+=" and "+Sql_switcher.isnull("submitflag", "0")+"=1 ";
                    //}
                    rSet=dao.search(sqlstr);
                    if(rSet.next())
                    {
                        if(rSet.getInt(1)==0){
                            selectFlag="2";
                            //info ="请选中所有记录！";
                            //if("10".equals(applyFlag)){
                            	info ="请选中需要"+desc+"的记录！";
                            //}
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }finally {
        	PubFunc.closeDbObj(rSet);
        }
        return info;
    }
    
    
    /**
     * @Title: validateBatchTasks   
     * @Description: 判断批量审批的单据是否全选中   
     * @param @param desc
     * @param @param tabname
     * @param applyFlag 
     * @param @return
     * @param @throws GeneralException 
     * @return String 
     * @throws   
    */
    private String validateBatchTasks(String desc,String tabname, String applyFlag)throws GeneralException
    {
        StringBuffer strHint= new StringBuffer();
        try
        {
            String a0101="a0101_1"; 
            String keyTitle="人员";
            if (this.paramBo.getInfor_type()==1){
                if(this.paramBo.getOperationType()==0)//调入
                {
                    a0101="a0101_2";
                }
            }
            else if(this.paramBo.getInfor_type()==2 || this.paramBo.getInfor_type()==3)
            {
                if (this.paramBo.getInfor_type()==2){
                    keyTitle="机构" ;
                }
                else {
                    keyTitle="岗位" ;
                }
                a0101="codeitemdesc_1";
                if(this.paramBo.getOperationType()==5)
                    a0101="codeitemdesc_2";
            }
            
            String[] temps=this.frontProperty.getTaskId().split(",");
            StringBuffer ss=new StringBuffer("");
            for(int i=0;i<temps.length;i++)
            {
                if(temps[i]!=null&&temps[i].trim().length()>0)
                    ss.append(","+temps[i]);
            }
            //批量审批，多个单据没必要同时提交，只要来自同一单据的人员被全选中了即可。
            String sql="select distinct task_id from t_wf_task_objlink where tab_id="
                +this.frontProperty.getTabId()+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id in ("+ss.substring(1)
                +") and "+Sql_switcher.isnull("submitflag", "0")+"=1  and ( "
                +Sql_switcher.isnull("special_node","0")+"=0 or ("
                +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  )";
            ArrayList selectTaskList= dao.searchDynaList(sql);
            /*if(!"10".equals(applyFlag)){//提交或者报批的时候才验证批量必须全选
	            strHint.append("来源于同一单据的"+keyTitle+"，不支持部分审批！");
	            boolean bHave=false;//有未选中记录
	            for (int i=0;i<selectTaskList.size();i++){//分析选中的记录
	                LazyDynaBean bean = (LazyDynaBean)selectTaskList.get(i);
	                String task_id= (String)bean.get("task_id");
	                
	                RecordVo task_vo=new RecordVo("t_wf_task");
	                task_vo.setInt("task_id",Integer.parseInt(task_id));
	                task_vo=dao.findByPrimaryKey(task_vo);
	                if(task_vo==null)
	                    throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));  
	                String task_topic= task_vo.getString("task_topic");
	                //未选中人员                
	                sql="select * from "+tabname +" where exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and "+tabname+".ins_id=t_wf_task_objlink.ins_id"
	                    +" and tab_id="
	                    +this.frontProperty.getTabId()+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id ="+task_id
	                    +" and "+Sql_switcher.isnull("submitflag", "0")+"=0  and ( "
	                    +Sql_switcher.isnull("special_node","0")+"=0 or ("
	                    +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  )"
	                    +")";
	                RowSet rSet=dao.search(sql);
	                if (rSet.next()){
	                    bHave=true;
	                    strHint.append("<br />");
	                    strHint.append(task_topic+":");
	                    strHint.append("未选中"+keyTitle+":");
	                    String a0101s="";
	                    rSet.beforeFirst();                    
	                    while (rSet.next()){
	                        String a0101Value=rSet.getString(a0101);
	                        if (a0101s.length()>0 ) a0101s=a0101s+"、";
	                        a0101s=a0101s+a0101Value;
	                    }
	                    strHint.append(a0101s); 
	                  //已选中人员
	                    sql="select * from "+tabname +" where exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and "+tabname+".ins_id=t_wf_task_objlink.ins_id"
	                        +" and tab_id="
	                        +this.frontProperty.getTabId()+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id ="+task_id
	                        +" and "+Sql_switcher.isnull("submitflag", "0")+"=1  and ( "
	                        +Sql_switcher.isnull("special_node","0")+"=0 or ("
	                        +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  )"
	                        +")";   
	                    strHint.append("    已选中"+keyTitle+":");
	                    a0101s="";
	                    rSet=dao.search(sql);
	                    while (rSet.next()){
	                        String a0101Value=rSet.getString(a0101);
	                        if (a0101s.length()>0 ) a0101s=a0101s+"、";
	                        a0101s=a0101s+a0101Value;
	                    }
	                    strHint.append(a0101s); 
	                }
	                if (!bHave) strHint.setLength(0);
	            }
            }*/
            if (selectTaskList.size()<1){//没有选中记录
                strHint.append("请选中需要"+desc+"的记录！");
            }
           
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return strHint.toString();
    }
    
    private String  getSql(String dataTabName,String task_ids) throws GeneralException {
        try
        {
            boolean bInProcess= (!"0".equals(task_ids));//审批中
            StringBuffer sql=new StringBuffer();
            if (bInProcess) {//待办，我的申请，我的已办 任务监控                    
                ArrayList taskList =getTaskList(task_ids);              
                StringBuffer strTaskIds=new StringBuffer();                                  
              
                for(int i=0;i<taskList.size();i++)
                {
                   // String ins_id=((RecordVo)taskList.get(i)).getString("ins_id");
                    String task_id=((RecordVo)taskList.get(i)).getString("task_id");
                    if(i!=0)
                        strTaskIds.append(",");
                    strTaskIds.append(task_id);
                }
                sql.append("select T.*,O.task_id as realtask_id from ");
                sql.append(dataTabName+" T");                   
                sql.append(",t_wf_task_objlink O  where T.seqnum=O.seqnum  and T.ins_id=O.ins_id");
                 sql.append(" and ( "+Sql_switcher.isnull("O.special_node","0")+"=0 or ("+Sql_switcher.isnull("O.special_node","0")+"=1 and (lower(O.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(O.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ");
                sql.append("  and O.task_id in ("+strTaskIds.toString()+") and O.tab_id="+this.frontProperty.getTabId()
                        +" and ( "+Sql_switcher.isnull("O.state","0")+"<>3 ) ");//state is null or  state=0
              
           
            }
            else {
                sql.append("select * from ");
                sql.append(dataTabName);
            }
        
            String strsql=sql.toString();       
            
            return sql.toString();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }

    
    public String judgeBusinessRule()
    {
        String judgeisllexpr = "";
        RowSet rset = null;
        try {
            boolean bInProcess = (!"0".equals(this.frontProperty.getTaskId()));// 审批中
            HashMap hm = new HashMap();
            ArrayList a0100lists = new ArrayList();
            String first_base = null;
            String a0100 = null;
            StringBuffer strTaskIds = new StringBuffer();
            if (bInProcess) {// 待办，我的申请，我的已办 任务监控
                try {
                    ArrayList taskList = getTaskList(this.frontProperty.getTaskId());
                    for (int i = 0; i < taskList.size(); i++) {
                        String task_id = ((RecordVo) taskList.get(i)).getString("task_id");
                        if (i != 0)
                            strTaskIds.append(",");
                        strTaskIds.append(task_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            String infor_type = String.valueOf(this.paramBo.getInfor_type());
            String llexpr = this.paramBo.getLlexpr();
            boolean bSelfApply = this.frontProperty.isSelfApply();
            TemplateUtilBo utilBo = new TemplateUtilBo(conn, this.userView);
            String dataTabName = utilBo.getTableName(this.frontProperty.getModuleId(), Integer.parseInt(this.frontProperty.getTabId()), this.frontProperty.getTaskId());

            StringBuffer sql = new StringBuffer();
            if (bSelfApply) {
            	sql.append("select  ");
                if ("1".equals(infor_type))
                    sql.append(" basepre,a0100");
                else if ("2".equals(infor_type))
                    sql.append(" b0110");
                else if ("3".equals(infor_type))
                    sql.append(" e01a1");
                sql.append(" from ");
                sql.append(dataTabName);
                sql.append(" where ");
                sql.append(" basepre='");
                sql.append(this.userView.getDbname());
                sql.append("' and a0100='");
                sql.append(this.userView.getA0100());
                sql.append("'");
            } else {
                if (bInProcess) {//                    
                	sql.append("select ");
                    if ("1".equals(infor_type))
                        sql.append(" T.basepre,T.a0100");
                    else if ("2".equals(infor_type))
                        sql.append(" T.b0110");
                    else if ("3".equals(infor_type))
                        sql.append(" T.e01a1");
                    sql.append(",O.task_id as realtask_id from ");
                    sql.append(dataTabName + " T");
                    sql.append(",t_wf_task_objlink O  where T.seqnum=O.seqnum  and T.ins_id=O.ins_id");
                    sql.append(" and ( " + Sql_switcher.isnull("O.special_node", "0") + "=0 or (" + Sql_switcher.isnull("O.special_node", "0") + "=1 and (lower(O.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(O.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ");
                    sql.append("  and O.task_id in (" + strTaskIds.toString() + ") and O.tab_id=" + this.frontProperty.getTabId() + " and ( " + Sql_switcher.isnull("O.state", "0") + "<>3 ) ");
                    sql.append(" and o.submitflag=1 ");
                } else {
                	sql.append("select ");
                    if ("1".equals(infor_type))
                        sql.append(" basepre,a0100");
                    else if ("2".equals(infor_type))
                        sql.append(" b0110");
                    else if ("3".equals(infor_type))
                        sql.append(" e01a1");
                    sql.append(" from ");
                    sql.append(dataTabName);
                    sql.append(" where submitflag=1 ");
                }

                if ("1".equals(infor_type))
                    sql.append(" order by a0100");
                else if ("2".equals(infor_type))
                    sql.append(" order by b0110");
                else if ("3".equals(infor_type))
                    sql.append(" order by e01a1");
            }

            rset = dao.search(sql.toString());
            while (rset.next()) {
                if ("1".equals(infor_type)) {
                    String pre = rset.getString("basepre").toLowerCase();
                    /** 按人员库进行分类 */
                    if (!hm.containsKey(pre)) {
                        a0100lists = new ArrayList();
                    } else {
                        a0100lists = (ArrayList) hm.get(pre);
                    }
                    a0100lists.add(rset.getString("a0100"));
                    hm.put(pre, a0100lists);
                } else if ("2".equals(infor_type)) {
                    a0100lists.add(rset.getString("b0110"));
                } else if ("3".equals(infor_type)) {
                    a0100lists.add(rset.getString("e01a1"));
                }

            }// for i loop end.

            if ("2".equals(infor_type) || "3".equals(infor_type))
                hm.put("B", a0100lists);

            /** 加规则过滤 */
            ArrayList alUsedFields = null;
            String temptable = null;
            if (llexpr != null && llexpr.trim().length() > 0) {
                alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                temptable = createSearchTempTable(this.conn, infor_type);
            }

            Iterator iterator = hm.entrySet().iterator();
            StringBuffer judgeSql = new StringBuffer();
            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                String pre = entry.getKey().toString();
                ArrayList a0100list = (ArrayList) entry.getValue();
                String key = "a0100";
                if ("1".equals(infor_type)) {
                    judgeSql.append("select A0101 from ");
                    judgeSql.append(pre);
                    judgeSql.append("A01 where ");
                } else if ("2".equals(infor_type)) {
                    judgeSql.append("select B0110 from B01 where ");
                    key = "b0110";
                } else if ("3".equals(infor_type)) {
                    judgeSql.append("select E01A1 from K01 where ");
                    key = "e01a1";
                }

                if (!bSelfApply) {
                    sql.setLength(0);
                    if (bInProcess) {//                    
                        sql.append("select T." + key + " from ");
                        sql.append(dataTabName + " T");
                        sql.append(",t_wf_task_objlink O  where T.seqnum=O.seqnum  and T.ins_id=O.ins_id");
                        sql.append(" and ( " + Sql_switcher.isnull("O.special_node", "0") + "=0 or (" + Sql_switcher.isnull("O.special_node", "0") + "=1 and (lower(O.username)='"+this.userView.getUserName().toLowerCase()+"' or lower(O.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ");
                        sql.append("  and O.task_id in (" + strTaskIds.toString() + ") and O.tab_id=" + this.frontProperty.getTabId() + " and ( " + Sql_switcher.isnull("O.state", "0") + "<>3 ) ");
                        sql.append(" and o.submitflag=1 ");
                    } else {
                        sql.append("select " + key + " from ");
                        sql.append(dataTabName);
                        sql.append(" where submitflag=1 ");
                    }
                    if ("1".equals(infor_type))
                        sql.append(" and lower(basepre)='" + pre.toLowerCase() + "'");

                    judgeSql.append(" " + key + " in ( " + sql.toString() + " ) ");

                } else {
                    judgeSql.append(" " + key + " in(''");
                    for (int i = 0; i < a0100list.size(); i++) {
                        judgeSql.append(",'");
                        judgeSql.append(a0100list.get(i));
                        judgeSql.append("'");
                    }
                    judgeSql.append(")");
                }

                judgeSql.append(" and ");
                judgeSql.append(getFilterSQL(temptable, pre, alUsedFields, llexpr, infor_type));
                judgeSql.append(" UNION ");
                // System.out.println(a0100list + pre);
            }
            if (judgeSql.length() > 7) {
                judgeSql.setLength(judgeSql.length() - 7);
                ContentDAO dao = new ContentDAO(this.conn);
                String judgedesc = "";
                boolean bl = false;

                try {
                    // System.out.println(judgeSql.toString() + llexpr);
                    rset = dao.search(judgeSql.toString());

                    while (rset.next()) {
                        if ("1".equals(infor_type)) {
                            if (bl == false) {
                                judgedesc = rset.getString("a0101");
                                bl = true;
                            } else {
                                judgedesc += "," + rset.getString("a0101");
                            }
                        } else if ("2".equals(infor_type)) {
                            String codeitemid = rset.getString("b0110");
                            String codeitemdesc = AdminCode.getCodeName("UN", codeitemid);
                            if (codeitemdesc == null || codeitemdesc.trim().length() == 0)
                                codeitemdesc = AdminCode.getCodeName("UM", codeitemid);
                            if (bl == false) {
                                judgedesc = codeitemdesc;
                                bl = true;
                            } else {
                                judgedesc += "," + codeitemdesc;
                            }
                        } else if ("3".equals(infor_type)) {
                            String codeitemid = rset.getString("E01A1");
                            String codeitemdesc = AdminCode.getCodeName("@K", codeitemid);
                            if (bl == false) {
                                judgedesc = codeitemdesc;
                                bl = true;
                            } else {
                                judgedesc += "," + codeitemdesc;
                            }
                        }
                    }
                    if (bl) {
                        // this.getFormHM().put("sp_flag","3");
                        judgeisllexpr = judgedesc + ResourceFactory.getProperty("general.template.ishavenotjudge");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeDbObj(rset);
        }

        return judgeisllexpr;
    
    }
    private String getFilterSQL(String temptable,String BasePre,ArrayList alUsedFields,String llexpr,String infor_type)
    {
        String sql=" (1=2)";
        try{
        if(llexpr!=null && llexpr.length()>0)
        {
            ContentDAO dao=new ContentDAO(this.conn);
            StringBuffer inserSql=new StringBuffer();
            if("1".equals(infor_type))
            {
                inserSql.append("insert into ");
                inserSql.append(temptable);
                inserSql.append("(a0100) select '");
                inserSql.append(BasePre);
                inserSql.append("'"+Sql_switcher.concat()+"a0100 from ");
                
                // this.filterfactor="性别 <> '1'";
                int infoGroup = 0; // forPerson 人员
                int varType = 8; // logic
                String whereIN=InfoUtils.getWhereINSql(this.userView,BasePre);
                whereIN="select a0100 "+whereIN;                            
                YksjParser yp = new YksjParser( this.userView ,alUsedFields,
                        YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
                YearMonthCount ymc=null;                            
                yp.run_Where(llexpr, ymc,"","", dao, whereIN,this.conn,"A", null);
                String tempTableName = yp.getTempTableName();
                sql="('" + BasePre + "'"+Sql_switcher.concat()+  BasePre +"A01.a0100 in  (select distinct a0100 from " + temptable + "))";
                inserSql.append(tempTableName);
                inserSql.append(" where " + yp.getSQL());
            }
            else if("2".equals(infor_type))
            {
                inserSql.append("insert into ");
                inserSql.append(temptable);
                inserSql.append("(b0110) select b0110 from ");
                
                //this.filterfactor="性别 <> '1'";
                int infoGroup = 0; // forPerson 人员
                int varType = 8; // logic                               
                String whereIN=InfoUtils.getWhereInOrgSql(this.userView,"2");
                whereIN="select b0110 "+whereIN;                            
                YksjParser yp = new YksjParser( this.userView ,alUsedFields,
                        YksjParser.forSearch, varType, YksjParser.forUnit, "Ht", BasePre);
                YearMonthCount ymc=null;                            
                yp.run_Where(llexpr, ymc,"","", dao, whereIN,this.conn,"A", null);
                String tempTableName = yp.getTempTableName();
                sql="( B01.b0110 in  (select distinct b0110 from " + temptable + "))";
                inserSql.append(tempTableName);
                inserSql.append(" where " + yp.getSQL());
                
            }
            else if("3".equals(infor_type))
            {
                inserSql.append("insert into ");
                inserSql.append(temptable);
                inserSql.append("(e01a1) select e01a1 from ");
                
                //this.filterfactor="性别 <> '1'";
                int infoGroup = 0; // forPerson 人员
                int varType = 8; // logic                               
                String whereIN=InfoUtils.getWhereInOrgSql(this.userView,"3");
                whereIN="select e01a1 "+whereIN;                            
                YksjParser yp = new YksjParser( this.userView ,alUsedFields,
                        YksjParser.forSearch, varType, YksjParser.forPosition, "Ht", BasePre);
                YearMonthCount ymc=null;                            
                yp.run_Where(llexpr, ymc,"","", dao, whereIN,this.conn,"A", null);
                String tempTableName = yp.getTempTableName();
                sql="( K01.e01a1 in  (select distinct e01a1 from " + temptable + "))";
                inserSql.append(tempTableName);
                inserSql.append(" where " + yp.getSQL());
            } 
            dao.insert(inserSql.toString(),new ArrayList());    
        }   
        }catch(Exception e)
        {
            e.printStackTrace();
        }   
        return sql;
    }
    private String createSearchTempTable(Connection conn,String infor_type)
    {
        String temptable="temp_search_xry_01";
        try{
            StringBuffer sql=new StringBuffer();
            sql.delete(0,sql.length());
            sql.append("drop table ");
            sql.append(temptable);
            try{
              ExecuteSQL.createTable(sql.toString(),conn);
            }catch(Exception e)
            {
                //e.printStackTrace();
            }
            sql.delete(0,sql.length());
            sql.append("CREATE TABLE ");
            sql.append(temptable);
            if("1".equals(infor_type))
                sql.append("(a0100  varchar (100) )");
            else if("2".equals(infor_type))
                sql.append("(b0110  varchar (100) )");
            else if("3".equals(infor_type))
                sql.append("(e01a1  varchar (100) )");
            try{
                  ExecuteSQL.createTable(sql.toString(),conn);                            
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return temptable;
    }
    
    /**   
     * @Title: validateHeadCount   
     * @Description: 验证编制 
     * @param @return 
     * @return HashMap 
     * @throws   
    */
    public HashMap validateHeadCount()  {
        HashMap map = new HashMap();
        try
        {
            String msgs="";
            String flag="ok";
            /**员工自助申请标识
             *＝1员工
             *＝0业务员 
             */
            boolean bSelfApply = this.frontProperty.isSelfApply();
            String tabid =this.frontProperty.getTabId();
            String task_ids=this.frontProperty.getTaskId();
            TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(tabid),this.userView);
            if(tablebo.getInfor_type()==1)
            {
                if("0".equals(task_ids)&&tablebo.isHeadCountControl(0))
                { 
                        String srcTab=this.userView.getUserName()+"templet_"+Integer.parseInt(tabid);
                        if(bSelfApply)
                        {
                            srcTab="g_templet_"+tabid;
                            tablebo.setBEmploy(true);
                        }
                
                        /**编制控制*/
                        StringBuffer sbsql = new StringBuffer("");
                        sbsql.append("select * from "+srcTab+" where");
                        if(bSelfApply)//员工通过自助平台发动申请
                            sbsql.append(" a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
                        else
                            sbsql.append(" submitflag=1");
                        
                        ///源库与目标库
                        String srcDb = "";//源库
                        String destinationDb = "";//目标库
                        srcDb = tablebo.getDestinationDb(sbsql.toString());
                        destinationDb = tablebo.getDestBase(); //移库模板的目标库
                        if("".equals(destinationDb)){//说明不是移库模板
                            destinationDb = srcDb;
                        }
                        ///对那些库进行编制控制
                        PosparameXML pos = new PosparameXML(this.conn);//得到constant表中UNIT_WORKOUT对应的参数
                        String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");//对哪个库进行编制控制
                        dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
                        if(dbs.length()>0){
                            if(dbs.charAt(0)!=','){
                                dbs=","+dbs;
                            }
                            if(dbs.charAt(dbs.length()-1)!=','){
                                dbs=dbs+",";
                            }
                        }
                        int currentOperation = tablebo.getOperationtype();//模板类型
                        if(currentOperation==4) {//模板类型为系统内部调动时，人员为当前人员库内调动，目标库应与源库相同
                        	destinationDb=srcDb;
                        
                        }
                        String addFlag = tablebo.getAddFlag(currentOperation,dbs,srcDb,destinationDb);//1:新增。0：修改 2：不控制
                        ScanFormationBo scanFormationBo=new ScanFormationBo(this.conn,this.userView);
                            
                        if(("1".equals(addFlag) || "0".equals(addFlag)) && scanFormationBo.doScan()){//如果需要编制控制
                            
                            ///得到所有变化后的指标（包含子集中的指标）
                            StringBuffer allFields = new StringBuffer("");//所有的指标（如果是子集，也要把所包含的指标提取出来）
                            ArrayList allFieldsList = new ArrayList();//list(0)为变化后的指标。list(1)为map.键为变化后的子集，键值为该子集中涉及到的指标
                            allFieldsList = tablebo.getAllFields(tabid);
                            if(allFieldsList.size()>0){
                                ArrayList afterFields = (ArrayList)allFieldsList.get(0);//变化后的指标（仅仅是指标）
                                ArrayList temp_list = new ArrayList();//所有的指标（包括子集中的）
                                temp_list.addAll(afterFields);
                                HashMap temp_map = (HashMap)allFieldsList.get(1);//所有子集的map(包括兼职子集)。键全为小写
                                
                                Set key = temp_map.keySet();
                                for (Iterator it = key.iterator(); it.hasNext();) {
                                    String s = (String) it.next();
                                    ArrayList innerlist = (ArrayList)temp_map.get(s);
                                    temp_list.addAll(innerlist);
                                }
                                
                                //将所有变化后的指标（包括子集中的指标）变为字符串的形式
                                for(int i=0;i<temp_list.size();i++){
                                    String str = (String)temp_list.get(i);
                                    allFields.append(str+",");
                                }
                                if(allFields.length()>0 && allFields.charAt(allFields.length()-1)==','){
                                    allFields.setLength(allFields.length()-1);
                                }
                                String itemids = "all";
                                if(currentOperation!=1 && currentOperation!=2){//不是移库型
                                    itemids = allFields.toString();
                                }
                                if(scanFormationBo.needDoScan(destinationDb,itemids)){//当前模板的指标是否能引起编制检查
                                    ArrayList beanlist = new ArrayList();
                                    //开始传list。list中存放着bean.
                                    String partType = "0";//兼职指标以何种方式维护。=0：以罗列指标的形式   =1：在兼职子集中
                                    partType = tablebo.getPartType(temp_map);
                                    if("0".equals(partType)){
                                        beanlist = tablebo.getBeanList_1(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map,tabid);
                                    }else{
                                        beanlist = tablebo.getBeanList_2(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map);
                                    }
                                    scanFormationBo.execDate2TmpTable(beanlist);
                                    String mess=scanFormationBo.isOverstaffs();
                                    if(!"ok".equals(mess)){
                                        if("warn".equals(scanFormationBo.getMode())){//提示，继续。
                                            msgs += mess;
                                            flag="warn";
                                        }else{
                                            msgs += mess;
                                            flag="error";
                                        //  throw GeneralExceptionHandler.Handle(new Exception(mess));
                                        }
                                    }
                                } //当前模板的指标能引起编制检查 结束
                            } //allFieldsList.size()>0 结束
                        } //需要编制控制 结束 
                     
                }
                else if(!"0".equals(task_ids))
                {
                    /**批量审批*/
                    ArrayList tasklist=null;//所有要处理的任务
                    String srcTab="templet_"+Integer.parseInt(tabid);                    
                    tasklist=getTaskList(task_ids);                    
                    
                    for(int i=0;i<tasklist.size();i++)
                    {
                        String ins_id=((RecordVo)tasklist.get(i)).getString("ins_id");
                        String task_id=((RecordVo)tasklist.get(i)).getString("task_id");
                       
                          
                            /**编制控制*/
                            if(tablebo.isHeadCountControl(Integer.parseInt(task_id))){//报批和提交时验证超编
                                StringBuffer sbsql = new StringBuffer("");//查询的sql语句
                                sbsql.append("select * from ");
                                sbsql.append(srcTab);
                                sbsql.append(" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum and "+srcTab+".ins_id=t_wf_task_objlink.ins_id ");
                                sbsql.append("  and task_id="+task_id+"  and submitflag=1  and (state is null or  state=0 ) and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
                                ///源库与目标库
                                String srcDb = "";//源库
                                String destinationDb = "";//目标库
                                srcDb = tablebo.getDestinationDb(sbsql.toString());
                                destinationDb = tablebo.getDestBase(); //移库模板的目标库
                                if("".equals(destinationDb)){//说明不是移库模板
                                    destinationDb = srcDb;
                                }
                                ///对那些库进行编制控制
                                PosparameXML pos = new PosparameXML(this.conn);//得到constant表中UNIT_WORKOUT对应的参数
                                String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");//对哪个库进行编制控制
                                dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
                                if(dbs.length()>0){
                                    if(dbs.charAt(0)!=','){
                                        dbs=","+dbs;
                                    }
                                    if(dbs.charAt(dbs.length()-1)!=','){
                                        dbs=dbs+",";
                                    }
                                }
                                int currentOperation = tablebo.getOperationtype();//模板类型
                                String addFlag = tablebo.getAddFlag(currentOperation,dbs,srcDb,destinationDb);//1:新增。0：修改 2：不控制
                                
                                ScanFormationBo scanFormationBo=new ScanFormationBo(this.conn,this.userView);
                                    
                                if(("1".equals(addFlag) || "0".equals(addFlag)) && scanFormationBo.doScan()){//如果需要编制控制
                                    
                                    ///得到所有变化后的指标（包含子集中的指标）
                                    StringBuffer allFields = new StringBuffer("");//所有的指标（如果是子集，也要把所包含的指标提取出来）
                                    ArrayList allFieldsList = new ArrayList();//list(0)为变化后的指标。list(1)为map.键为变化后的子集，键值为该子集中涉及到的指标
                                    allFieldsList = tablebo.getAllFields(tabid);
                                    if(allFieldsList.size()>0){
                                        ArrayList afterFields = (ArrayList)allFieldsList.get(0);//变化后的指标（仅仅是指标）
                                        ArrayList temp_list = new ArrayList();//所有的指标（包括子集中的）
                                        temp_list.addAll(afterFields);
                                        HashMap temp_map = (HashMap)allFieldsList.get(1);//所有子集的map(包括兼职子集)。键全为小写
                                        
                                        Set key = temp_map.keySet();
                                        for (Iterator it = key.iterator(); it.hasNext();) {
                                            String s = (String) it.next();
                                            ArrayList innerlist = (ArrayList)temp_map.get(s);
                                            temp_list.addAll(innerlist);
                                        }
                                        
                                        //将所有变化后的指标（包括子集中的指标）变为字符串的形式
                                        for(int m=0;m<temp_list.size();m++){
                                            String str = (String)temp_list.get(m);
                                            allFields.append(str+",");
                                        }
                                        if(allFields.length()>0 && allFields.charAt(allFields.length()-1)==','){
                                            allFields.setLength(allFields.length()-1);
                                        }
                                        String itemids = "all";
                                        if(currentOperation!=1 && currentOperation!=2){//不是移库型
                                            itemids = allFields.toString();
                                        }
                                        if(scanFormationBo.needDoScan(destinationDb,itemids)){//当前模板的指标是否能引起编制检查
                                            ArrayList beanlist = new ArrayList();
                                            //开始传list。list中存放着bean.
                                            String partType = "0";//兼职指标以何种方式维护。=0：以罗列指标的形式   =1：在兼职子集中
                                            partType = tablebo.getPartType(temp_map);
                                            if("0".equals(partType)){
                                                beanlist = tablebo.getBeanList_1(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map,tabid);
                                            }else{
                                                beanlist = tablebo.getBeanList_2(currentOperation,addFlag,sbsql.toString(),destinationDb,srcDb,afterFields,temp_map);
                                            }
                                            scanFormationBo.execDate2TmpTable(beanlist);
                                            String mess=scanFormationBo.isOverstaffs();
                                            if(!"ok".equals(mess)){
                                                if("warn".equals(scanFormationBo.getMode())){//提示，继续。
                                                    msgs += mess;
                                                    flag="warn";
                                                }else{
                                                    msgs += mess;
                                                    flag="error";
                                                    //throw GeneralExceptionHandler.Handle(new Exception(mess));
                                                }
                                            }
                                        } //当前模板的指标能引起编制检查 结束
                                    } //allFieldsList.size()>0 结束
                                } //需要编制控制 结束 
                            } 
                        
                    } //tasklit end loop 
                    
                }
                    
            }
            if("warn".equals(flag))
                msgs+=" 是否继续?";
            map.put("flag", flag);
            map.put("msgs", msgs);
             
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return map;

    }

    
    
    public ArrayList getTaskList(String batch_task)throws GeneralException
    {
        ArrayList tasklist=new ArrayList();
        String[] lists=StringUtils.split(batch_task,",");
        StringBuffer strsql=new StringBuffer();
        strsql.append("select * from t_wf_task where task_id in (");
        for(int i=0;i<lists.length;i++)
        {
            if(i!=0)
                strsql.append(",");
            strsql.append(lists[i]);
        }
        strsql.append(")");
        try
        {
            if ("0".equals(batch_task)){
                RecordVo taskvo=new RecordVo("t_wf_task");
                taskvo.setInt("task_id",0);
                taskvo.setInt("ins_id",0);
                tasklist.add(taskvo);
            }
            else {
                RowSet rset=dao.search(strsql.toString());
                while(rset.next())
                {
                    RecordVo taskvo=new RecordVo("t_wf_task");
                    taskvo.setInt("task_id",rset.getInt("task_id"));
                    taskvo.setInt("ins_id",rset.getInt("ins_id"));
                    tasklist.add(taskvo);
                } 
                
            }
           
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return tasklist;
    }
    
    /**校验必填项
     * @param tablebo
     * @param taskList
     * @throws GeneralException
     */
    public void  validateMustFillItem(TemplateTableBo tablebo,ArrayList taskList) throws GeneralException 
    {
        try {
            TemplateUtilBo utilBo= new TemplateUtilBo(conn,this.userView);            
            String tabName=utilBo.getTableName(this.frontProperty.getModuleId(),
                    Integer.parseInt(this.frontProperty.getTabId()), this.frontProperty.getTaskId());
            boolean bSelfApply = this.frontProperty.isSelfApply();
            if (bSelfApply){
                tablebo.setBEmploy(true);
            }
            
            /*
            StringBuffer ins_ids = new StringBuffer("");
            for(int i=0;i<taskList.size();i++)
            {
                String ins_id=((RecordVo)taskList.get(i)).getString("ins_id");
                if(i!=0)
                    ins_ids.append(",");
                ins_ids.append(ins_id);
            } 
            */
            
            ArrayList fieldlist = tablebo.getAllFieldItem("0");//liuyz 30408 手机模版和非手机模版子集有相同子集造成子集必填判断不正确。 
            if(taskList.size()>0)
            {
            	if(taskList.size()==1&& "0".equals(((RecordVo)taskList.get(0)).getString("task_id")))
            		tablebo.checkMustFillItem(tabName, fieldlist,0);
            	else
            	{
            		ArrayList taskidsList=splitTaskByNode(taskList);//获得不同审批节点下的任务号
            		for (int i = 0; i < taskidsList.size(); i++) {
		                 
		                String task_ids= (String)taskidsList.get(i);
		                tablebo.checkMustFillItem_batch(tabName, fieldlist,task_ids);
		            }
            		/*
		            for (int i = 0; i < taskList.size(); i++) {
		                String ins_id=((RecordVo)taskList.get(i)).getString("ins_id");
		                String task_id=((RecordVo)taskList.get(i)).getString("task_id");
		                tablebo.checkMustFillItem(tabName, fieldlist, Integer.parseInt(task_id));
		            }*/
            	}
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        } 
    }
    
    /**
     * 获得不同审批节点下的任务号
     * @param taskList
     * @return
     */
    private ArrayList splitTaskByNode(ArrayList taskList)throws GeneralException 
    {
    	ArrayList taskids=new ArrayList();
    	try {
    		ContentDAO dao=new ContentDAO(this.conn); 
	    	StringBuffer task_ids=new StringBuffer("");
	    	for (int i = 0; i < taskList.size(); i++) { 
	            String task_id=((RecordVo)taskList.get(i)).getString("task_id");
	            task_ids.append(","+task_id);
	        }
	    	RowSet rowSet=dao.search("select node_id,task_id from t_wf_task where task_id in ("+task_ids.substring(1)+") order by node_id");
	    	String oldNode="";
	    	String oldTaskId="";
	    	while(rowSet.next())
	    	{
	    		String node_id=rowSet.getString("node_id");
	    		String task_id=rowSet.getString("task_id");
	    		if(oldNode.length()==0)
	    			oldNode=node_id;
	    		
	    		if(!oldNode.equals(node_id))
	    		{
	    			taskids.add(oldTaskId.substring(1));
	    			oldTaskId="";
	    		}
	    		oldTaskId+=","+task_id;
	    	}
	    	taskids.add(oldTaskId.substring(1));
    	
    	 } catch (Exception ex) {
             ex.printStackTrace();
             throw GeneralExceptionHandler.Handle(ex);
         }
    	 return taskids;
    }
    
    
    
    
    
    
    
    
    /**校验审核公式
     * @param templateBo
     * @param taskList
     * @throws GeneralException
     */
    public void  checkLogicExpress(TemplateBo templateBo,ArrayList taskList) throws GeneralException 
    {
        try {
            TemplateUtilBo utilBo= new TemplateUtilBo(conn,this.userView);            
            String tabName=utilBo.getTableName(this.frontProperty.getModuleId(),
                    Integer.parseInt(this.frontProperty.getTabId()), this.frontProperty.getTaskId());
            boolean bSelfApply = this.frontProperty.isSelfApply();
          /*  StringBuffer ins_ids = new StringBuffer("");
            for(int i=0;i<taskList.size();i++)
            {
                String ins_id=((RecordVo)taskList.get(i)).getString("ins_id");
                if(i!=0)
                    ins_ids.append(",");
                ins_ids.append(ins_id);
            } */
            if(taskList.size()>0)
            {
            	if(taskList.size()==1&&("0".equals(((RecordVo)taskList.get(0)).getString("task_id"))||bSelfApply))
            	{ 
            		 
	                	templateBo.checkLogicExpress(0);
            	}
            	else
            	{
            
            		String taskids="";
		            for (int i = 0; i < taskList.size(); i++) {
		                String ins_id=((RecordVo)taskList.get(i)).getString("ins_id");
		                String task_id=((RecordVo)taskList.get(i)).getString("task_id");
		                
		                String noCheckTemplateIds = SystemConfig.getPropertyValue("noCheckTemplateIds"); //system.properties -->  noCheckTemplateIds=12,88
		                if (noCheckTemplateIds != null && Integer.parseInt(task_id) > 0 && ("," + noCheckTemplateIds + ",").indexOf("," + templateBo.getParamBo().getTabId() + ",") != -1) {
		                    
		                } 
		                else
		                	taskids+=","+task_id;
		                //	templateBo.checkLogicExpress(Integer.parseInt(task_id));
		            }
		            if(taskids.length()>0)
		            	templateBo.checkLogicExpress_batch(taskids.substring(1));
		            
		            
            	}
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        
    }
    /**
     * 校验目标库是否有当前唯一指标值对应记录
     * @return
     */
	public String validateExistOnlyData() {
		String info = "";
		RowSet rSet = null;
		RowSet rSet1 = null;
		try {
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name"); //验证唯一性指标
    		onlyname=onlyname!=null?onlyname:"";
    		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//唯一性验证是否启用
    		uniquenessvalid=uniquenessvalid!=null?uniquenessvalid:"";
    		String dbonly = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","db");//验证唯一性适用的人员库
    		dbonly=dbonly!=null?dbonly:"";
    		DbNameBo dbnamebo = new DbNameBo(this.conn);
    		String fldname = "";
    		if("1".equals(uniquenessvalid)&&onlyname.length()>0){//勾选了检验唯一指标，且选择了唯一指标
    			if (this.paramBo.getDest_base()!=null &&this.paramBo.getDest_base().length()>0 && 
						dbonly.toUpperCase().indexOf(this.paramBo.getDest_base().toUpperCase())!=-1){//目标库在设置的校验库中
    				FieldItem fieldItem=DataDictionary.getFieldItem(onlyname);
					if(fieldItem!=null){
						TemplateUtilBo utilBo= new TemplateUtilBo(conn,this.userView);
						ArrayList cellList = utilBo.getPageCell(this.paramBo.getTabId(),-1);
						for(int i=0;i<cellList.size();i++) {
							TemplateSet setbo = (TemplateSet)cellList.get(i);
							String field_name = setbo.getField_name();
							if(field_name.equalsIgnoreCase(onlyname)) {
								fldname = field_name;
								if(setbo.getSub_domain_id()!=null&&setbo.getSub_domain_id().length()>0){
									fldname = fldname+"_"+setbo.getSub_domain_id();
								}
			                    fldname = fldname+"_"+ String.valueOf(setbo.getChgstate());
			                    break;
							}
						}
						String tableName=utilBo.getTableName(this.frontProperty.getModuleId(),
								Integer.parseInt(this.frontProperty.getTabId()), this.frontProperty.getTaskId());
						if(StringUtils.isNotBlank(fldname)) {//证明表单中有插入此指标
							StringBuffer sb = new StringBuffer("");
							sb.append("select DISTINCT a0101 from "+this.paramBo.getDest_base()+"A01 where "+onlyname+" in(");
							if("0".equals(this.frontProperty.getTaskId())) {
				                sb.append("select "+fldname+" from "+tableName+" where  "+Sql_switcher.isnull("submitflag", "0")+"=1 )");
							}else {//流程中的
								sb.append("select "+fldname+" from t_wf_task_objlink where tab_id="+this.frontProperty.getTabId()+" and task_id="+this.frontProperty.getTaskId());
								sb.append(" and  "+Sql_switcher.isnull("state","0")+"!=3   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and"
			                    		+ " (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )))");
								sb.append(" and "+Sql_switcher.isnull("submitflag", "0")+"=1 )");
							}
							rSet=dao.search(sb.toString());
			                while(rSet.next()){
			                    String a0101 = rSet.getString("a0101");
			                    info += a0101+"的"+fieldItem.getItemdesc()+"在目标库中已存在\n\r";
			                }
						}else {//没有插入的话
							StringBuffer sb = new StringBuffer("");
							if("0".equals(this.frontProperty.getTaskId())) {
				                sb.append("select a0100,basepre from "+tableName+" where  "+Sql_switcher.isnull("submitflag", "0")+"=1 ");
							}else {//流程中的
								sb.append("select a0100,basepre from t_wf_task_objlink where tab_id="+this.frontProperty.getTabId()+" and task_id="+this.frontProperty.getTaskId());
								sb.append(" and  "+Sql_switcher.isnull("state","0")+"!=3   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and"
			                    		+ " (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )))");
								sb.append(" and "+Sql_switcher.isnull("submitflag", "0")+"=1");
							}
							rSet=dao.search(sb.toString());
			                while(rSet.next()){
			                	sb.setLength(0);
			                	String basepre = rSet.getString("basepre");
			                	String a0100 = rSet.getString("a0100");
			                	sb.append("select DISTINCT a.a0101 from "+this.paramBo.getDest_base()+"A01 a,"+basepre+"A01 b where a."+onlyname+"=b."+onlyname+" and b.a0100='"+a0100+"'");
			                	rSet1 = dao.search(sb.toString());
			                	if(rSet1.next()) {
			                		String a0101 = rSet1.getString("a0101");
				                    info += a0101+"的"+fieldItem.getItemdesc()+"在目标库中已存在\n\r";
			                	}
			                }
						}
					}
				}
			}
    		if(info.length()>0) {
    			info = info.substring(0,info.length()-2);
    			info += ",不允许提交！";
    		}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rSet);
			PubFunc.closeDbObj(rSet1);
		}
		return info;
	}



    /**
     * 校验是否关联了审批关系
     * @return
     */
	public String validateApplyRelation() {
		RowSet rset = null;
		String info = "";
		try {
			String sql = "select 1 from t_wf_node a,t_wf_actor b,t_sys_role c where a.node_id=b.node_id "
					+ "and b.actorid=c.role_id and a.tabid=? and a.nodetype=2 and c.role_property in(9,10,11,12)"; 
			ArrayList list = new ArrayList();
			list.add(this.paramBo.getTabId());
			rset = dao.search(sql,list);
			if(rset.next()) {
				String relationid = this.paramBo.getRelation_id();
				if(StringUtils.isBlank(relationid)) {
					info = ResourceFactory.getProperty("general.template.workflowbo.info2");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return info;
	}


}
