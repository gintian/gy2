package com.hjsj.hrms.businessobject.workplan.plan_track;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class RelatePerformancePlanBo {
    private Connection conn=null;
    private UserView userView;
    private ContentDAO dao; 
    private WorkPlanUtil workPlanUtil;
    private DbWizard dbw;
    private RowSet frowset;
    
    public RelatePerformancePlanBo(Connection conn,UserView userview) {
        this.conn=conn;
        this.userView=userview;
        dbw = new DbWizard(this.conn);
        dao=new ContentDAO(this.conn);
        workPlanUtil=new WorkPlanUtil(this.conn,userview);
    }
    
    /**   
     * @Title: searchExamPlanList   
     * @Description:返回符合关联要求的计划    
     * @param @param object_type
     * @param @param periodType
     * @param @param year
     * @param @param month
     * @param @param week
     * @param @return
     * @param @throws GeneralException 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList searchExamPlanList(String object_type,
            String periodType,String year,String month,String week) throws GeneralException
    {
     ContentDAO dao = new ContentDAO(this.conn);   
     ExamPlanBo bo = new ExamPlanBo(this.conn);
     ArrayList list = new ArrayList();
     String operOrg = this.userView.getUnitIdByBusi("5");
     String orgWhere = "1=1";
     if (operOrg != null && operOrg.length() > 3) {
         StringBuffer tempSql = new StringBuffer("");
         String[] temp = operOrg.split("`");
         for (int i = 0; i < temp.length; i++) {   
                 tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
         }
         orgWhere = tempSql.substring(3);
     }

     String busitype="0";
     StringBuffer strSql = new StringBuffer("select * from per_plan where  1=1 ");
     strSql.append(" and ( busitype is null  or busitype = 0) ");
     
     StringBuffer whlSql = new StringBuffer();   
     whlSql.append(" and ((B0110 = 'HJSJ' or B0110 = '' ) ");
     whlSql.append(" or ("+ orgWhere);
     whlSql.append(")) ");
     whlSql.append(" and method = 2 ");
         
     if("2".equalsIgnoreCase(object_type.trim())) {
         whlSql.append(" and object_type in(1,3,4) ");
     } else {
         whlSql.append(" and object_type =2" );
     }
     
     try
     {   
         whlSql.append(bo.getPlanWhlByObjTypePriv(this.userView,busitype));
         
         // 时间范围查询   
         whlSql.append(" and cycle="+(String.valueOf(Integer.parseInt(periodType)-1))); 
         whlSql.append(" and theyear="+year); 
         if (!WorkPlanConstant.Cycle.YEAR.equals(periodType)){  
             if (WorkPlanConstant.Cycle.MONTH.equals(periodType)
                     ||WorkPlanConstant.Cycle.WEEK.equals(periodType)){
                 whlSql.append(" and themonth="+month);
             }
             if (WorkPlanConstant.Cycle.QUARTER.equals(periodType)
                     ||WorkPlanConstant.Cycle.HALFYEAR.equals(periodType)){
                 whlSql.append(" and thequarter="+month);
             }
         } 
         
         // 审批状态查询       
         whlSql.append(" and status in (3,4,5,8)");
         strSql.append(whlSql.toString());
         strSql.append(" order by "+Sql_switcher.isnull("a0000", "999999999")
                 +" asc,plan_id desc");
 
         HashMap map =null;
         map=bo.getPlansUserView(this.userView, whlSql.toString());
         RowSet frowset = dao.search(strSql.toString());
         while (frowset.next())
         {     
             if(map.get(frowset.getString("plan_id"))==null) {
                 continue;
             }
             LazyDynaBean abean = new LazyDynaBean();
             abean.set("plan_id", isNull(frowset.getString("plan_id")));
             abean.set("name", isNull(frowset.getString("name")));
             abean.set("object_type", WorkPlanUtil.getKhPlanObjectTypeDesc(
                     isNull(frowset.getString("object_type"))
                     ));
             abean.set("status_desc", WorkPlanUtil.getKhPlanStatusDesc(
                     isNull(frowset.getString("status"))
                     ));
             abean.set("period_desc",WorkPlanUtil.getKhPlanPeriodDesc(
                     isNull(frowset.getString("cycle")),
                     isNull(frowset.getString("theyear")),
                     isNull(frowset.getString("themonth")),
                     isNull(frowset.getString("thequarter"))
                     ));        
             list.add(abean);
         }
 
        
     } catch (Exception e)
     {
         e.printStackTrace();
         throw GeneralExceptionHandler.Handle(e);
     }
     return list;
 }
    
    /**   
     * @Title: getPlanInfo   
     * @Description:返回刚刚新增的计划，为保持前台一致处理 也返回list    
     * @param @param planid
     * @param @return
     * @param @throws GeneralException 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getPlanListById(String planid) throws GeneralException
    {
     ContentDAO dao = new ContentDAO(this.conn);   
     ArrayList list = new ArrayList();
   
     StringBuffer strSql = new StringBuffer("select * from per_plan where  plan_id=?  ");
     try
     {   
         HashMap map =null;
         ArrayList paramlist = new ArrayList();
         paramlist.add(Integer.valueOf(planid));
         RowSet frowset = dao.search(strSql.toString(),paramlist);
         while (frowset.next())
         {     
             LazyDynaBean abean = new LazyDynaBean();
             abean.set("plan_id", isNull(frowset.getString("plan_id")));
             abean.set("name", isNull(frowset.getString("name")));
             abean.set("object_type", WorkPlanUtil.getKhPlanObjectTypeDesc(
                     isNull(frowset.getString("object_type"))
                     ));
             abean.set("status_desc", WorkPlanUtil.getKhPlanStatusDesc(
                     isNull(frowset.getString("status"))
                     ));
             abean.set("period_desc",WorkPlanUtil.getKhPlanPeriodDesc(
                     isNull(frowset.getString("cycle")),
                     isNull(frowset.getString("theyear")),
                     isNull(frowset.getString("themonth")),
                     isNull(frowset.getString("thequarter"))
                     ));        
             list.add(abean);
         }
 
        
     } catch (Exception e)
     {
         e.printStackTrace();
         throw GeneralExceptionHandler.Handle(e);
     }
     return list;
 }
    
    
    /**   
     * @Title: checkHaveSuperBodySet   
     * @Description:检查当前考核计划是否有上级、上上级 返回没有的类别    
     * @param @param plan_id
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String  checkHaveSuperBodySet(String plan_id)
    {
        String info="";
        try
        {   
            boolean bSuper=false; 
            boolean bSuperSuper=false; 
            String strSql ="select * from per_plan_body where plan_id ="+plan_id +" and body_id in ("
                +"select  body_id from per_mainbodyset where ( body_type=0 or body_type is null) "
                +" and ";
            String levelFild = "level";
            if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                levelFild = "level_o";
            }
            strSql=strSql+levelFild+"=";
            RowSet rset = null;
            rset =dao.search(strSql+"0 )");
            if (rset.next()){
                bSuperSuper=true;
            }
            rset =dao.search(strSql+"1) ");
            if (rset.next()){
                bSuper=true;
            }
            if (!bSuper){
                info="上级";
            }
            if (!bSuperSuper){
                if (info.length()>0){
                    info=info+"、";
                }
                info=info+"上上级";
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        
        
        return info;
        
        

    }
    
    /**   
     * @Title: getSuperBodySet   
     * @Description:返回主体类别为上级、上上级的编号    
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String  getSuperBodySet()
    {
        String bodyIds="";
        try
        {   
            String strSql ="select  * from per_mainbodyset where ( body_type=0 or body_type is null) "
                +" and ";
            String levelFild = "level";
            if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                levelFild = "level_o";
            }
            strSql=strSql+levelFild+" in (0,1)";
            RowSet rset = null;
            rset =dao.search(strSql);
            while (rset.next()){
                String bodyid= rset.getString("body_id");
                if (bodyIds.length()>0) {
                    bodyIds=bodyIds+",";
                }
                bodyIds=bodyIds+bodyid;
               
            }
        
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return bodyIds;
    }
    
    /**   
     * @Title: getBodyIdByBodyType   
     * @Description: 根据主体类别分类获取主体类别编号   
     * @param @param body_type
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String  getBodyIdByBodyType(String body_type)
    {
        //0上上级 1 上级
        String bodyid="";
        try
        {   
            String strSql ="select  body_id from per_mainbodyset where ( body_type=0 or body_type is null) "
            +" and ";
            String levelFild = "level";
            if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                levelFild = "level_o";
            }
            strSql=strSql+levelFild+"=";
            RowSet  rset =dao.search(strSql+body_type);
            if (rset.next()){
                bodyid= rset.getString("body_id");
            }
        
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return bodyid;
    }
    
    
    /**   
     * @Title: getBodyIdByBodyType   
     * @Description:  根据主体类别分类获取主体类别编号，并且判断是否在某一计划中存在     
     * @param @param planid
     * @param @param body_type
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getBodyIdByBodyType(String planid,String body_type)
    {
        String bodyid="";
        if (planid.length() == 0) {
            return bodyid;
        }
        try
        {
            ContentDAO dao = new ContentDAO(this.conn);
            StringBuffer strsql = new StringBuffer();
            strsql.append("select body_id from per_plan_body where plan_id=");
            strsql.append(planid);
            strsql.append(" and body_id in (select  body_id from per_mainbodyset where ( body_type=0 or body_type is null) ");
            strsql.append(" and ");
            String levelFild = "level";
            if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
                levelFild = "level_o";
            }
            levelFild =levelFild+"="+body_type+")";
            strsql.append(levelFild);            
            RowSet rset = dao.search(strsql.toString());
            if (rset.next()){
                bodyid= rset.getString("body_id");
            }
            if (rset != null) {
                rset.close();
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return bodyid;
    }
    
    
    /**   
     * @Title: getTemplateId   
     * @Description: 获取计划的模板   
     * @param @param khplan_id
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getTemplateId(String khplan_id)
    {
        String template_id="";
        try
        {
            String strsql="select template_id from per_plan where plan_id =?";
            ArrayList paramList=new ArrayList();
            paramList.add(Integer.valueOf(khplan_id));
            RowSet rset = dao.search(strsql, paramList);
            if (rset.next()){
                template_id=rset.getString("template_id");               
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return template_id;
    }
    
    /**   
     * @Title: getTemplateItemList   
     * @Description: 获取模板项目   
     * @param @param templateID
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getTemplateItemList(String templateID)
    {
        ArrayList list=new ArrayList();
        try
        {
           String strsql="select * from  per_template_item where template_id='"+templateID+"'  order by seq";
           list= dao.searchDynaList(strsql);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }
    
    /**   
     * @Title: addTemplateItem   
     * @Description: 新增模板项目   
     * @param @param templateid
     * @param @param itemdesc 
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public void addTemplateItem(String templateid,String itemdesc)
    {
        try              
        {
            KhTemplateBo bo = new KhTemplateBo(this.conn);
            LazyDynaBean bean =bo.getTemplateInfo("templateid");
            
            IDGenerator idg = new IDGenerator(2, this.conn);
            int newid = new Integer(idg.getId("per_template_item.item_Id")).intValue();
            int seq=bo.getMaxId("per_template_item", "seq");
            RecordVo vo = new RecordVo("per_template_item");
            vo.setInt("kind", 2);
            vo.setInt("item_id",newid);
            vo.setInt("seq",seq);
            if("1".equals((String)bean.get("status")))//权重模版，分值默认为模版总分
            {
                vo.setString("score",(String)bean.get("topscore"));
                vo.setInt("rank", 0);
            }
            else//分值模版权重默认为1
            {
                vo.setInt("score",0);
                vo.setInt("rank", 1);
            }
            vo.setString("template_id",templateid);
            vo.setString("itemdesc",itemdesc);
            dao.addValueObject(vo);
          
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    


    public String getTemplateName(String templateId)
    {
        String name = "";
        if (templateId == null && "".equals(templateId)) {
            return name;
        }

        try
        {
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rset  = dao.search("select template_id,name from per_template where template_id='" + templateId + "'");
            if (rset.next()) {
                name = rset.getString("name");
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return name;
    }

    
    /**   
     * @Title: getTemplateItem   
     * @Description: 根据名称得到模板项目信息   
     * @param @param list
     * @param @param itemdesc
     * @param @return 
     * @return LazyDynaBean 
     * @author:wangrd   
     * @throws   
    */
    public LazyDynaBean getTemplateItem(ArrayList list ,String itemdesc)
    {
        LazyDynaBean bean=null;
        try              
        {
           for (int i=0;i<list.size();i++){
               LazyDynaBean abean= (LazyDynaBean)list.get(i);
               if ("2".equals(abean.get("kind"))){//共性
                   if(itemdesc.equals(abean.get("itemdesc"))){
                       bean=abean;
                       break;
                   }
               }
           }
          
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return bean;
    }
    
    
    public void publishPlan(String planId,String busitype) throws GeneralException
    {
        
        ExamPlanBo khPlanBo = null;
        khPlanBo = new ExamPlanBo(this.conn);
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            if (testMainBodyType(planId))
            {
                // 对于另存得到的计划 做一些补充操作 为了在实施中的数据完整性
                // 有主体没有主体的考核指标权限补充
                String sql = "select * from per_mainbody where plan_id=" + planId;
                this.frowset = dao.search(sql);
                if(this.frowset.next())//主体表有数据
                {
                    if (!dbw.isExistTable("per_pointpriv_" + planId, false))//相应的主体指标权限没有
                    {
                        khPlanBo.cper_pointpriv(planId);
                        khPlanBo.synObjectAndBody(planId, "");
                        khPlanBo.setKhMainbodyDefaultPri(planId);
                    }
                }               
                
                Table table = null;
                // 创建per_table_计划号临时表
                if (!dbw.isExistTable("per_table_" + planId, false))
                {
                    table = cper_table_sql(planId);
                    dbw.createTable(table);
                    
                    // 添加索引
                    dao.update("create index per_table_" + planId+"_idx on per_table_" + planId+" (object_id,mainbody_id)");

                }

                // 创建per_reslut_计划号临时表
                if (!dbw.isExistTable("PER_RESULT_" + planId, false))
                {
                    table = khPlanBo.cper_reslut_sql(planId,busitype);
                    dbw.createTable(table);
                }
                PerEvaluationBo pb = new PerEvaluationBo(this.conn,this.userView);
                // 检查per_result_planid表中有没有调整后的表结构的字段，若没有就创建  JinChunhai 2011.03.08
                pb.editResult(planId);

                // 创建per_pointpriv_计划号临时表
                // 由于另存的时候，当选中复制考核主体的指标权限的复选框时候也会创建该临时表，所以要先判断一下
                if (!dbw.isExistTable("per_pointpriv_" + planId, false))
                {
                    khPlanBo.cper_pointpriv(planId);

                }

                RecordVo vo = new RecordVo("per_plan");
                try
                {
                    vo.setInt("plan_id", Integer.parseInt(planId));
                    vo = dao.findByPrimaryKey(vo);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                String method = vo.getString("method");
                if ("2".equals(method))// 目标管理计划要生成项目权限数据
                {
                    if (!dbw.isExistTable("PER_ITEMPRIV_" + planId, false))
                    {
                        String fieldStr = cper_itempriv_sql(planId, dao);

                        // 对于另存的计划 如果复制了考核对象 却没有复制对象的各主体类别项目权限 在此处按默认有权限设置项目权限
                        setDefaultItemPriv(planId, dao, fieldStr);
                    }
                    //0 数据采集 1 网上打分 目标计划只能是网上打分 这里加如下代码是防止另存360后又改成了目标计划的时候 该参数没有相应改变
                    LoadXml loadXml=new LoadXml(this.conn,planId);
                    Hashtable params = loadXml.getDegreeWhole();                    
                    String scoreWay=(String)params.get("scoreWay");
                    if("0".equals(scoreWay)) {
                        scoreWay="1";
                    }
                    loadXml.saveAttribute("PerPlan_Parameter","ScoreWay",scoreWay);
                }

                // 更新绩效计划状态为发布状态

                dao.update("update per_plan set status='3' where plan_id=" + planId);

            }
           
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void setDefaultItemPriv(String planid, ContentDAO dao, String fieldStr) throws GeneralException
    {
        String sql = "select count(*) from PER_ITEMPRIV_" + planid;
        try
        {
            this.frowset = dao.search(sql);
            if (this.frowset.next()) {
                if (this.frowset.getInt(1) == 0)// 项目权限里没有纪录
                {
                    sql = "select count(*) from per_object where plan_id=" + planid;
                    this.frowset = dao.search(sql);
                    if (this.frowset.next()) {
                        if (this.frowset.getInt(1) > 0)// 有考核对象
                        {
                            sql = "select count(*) from per_plan_body where plan_id=" + planid;
                            this.frowset = dao.search(sql);
                            if (this.frowset.next()) {
                                if (this.frowset.getInt(1) > 0)// 设置了考核主体类别
                                {
                                    String insertSql = "insert into PER_ITEMPRIV_" + planid + "(" + fieldStr + ") ";
                                    StringBuffer buf = new StringBuffer();
                                    buf.append(" select ");
                                    String[] temp = fieldStr.split(",");
                                    for (int i = 0; i < temp.length - 2; i++) {
                                        if (temp[i].trim().length() > 0) {
                                            buf.append("1-" + Sql_switcher.isnull("b.opt", "0") + ","); // 复制的计划发布时根据opt调整项目权限 by 刘蒙
                                        }
                                    }
                                    buf.append("p.object_id,b.body_id from per_object p,per_plan_body b where p.plan_id=" + planid + " and b.plan_id=" + planid);
                                    insertSql += buf.toString();
                                    dao.insert(insertSql, new ArrayList());
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

    public String cper_itempriv_sql(String planid, ContentDAO dao) throws GeneralException  // 修改为：如果模板里共性项目为2级，且一级项目下也有指标，项目权限里把1级项目显示出来  JinChunhai  2011.03.11
    {
        StringBuffer fieldStr = new StringBuffer();
        Table table = new Table("PER_ITEMPRIV_" + planid);

        Field obj = new Field("object_id");
        obj.setDatatype(DataType.STRING);
        obj.setLength(30);
        obj.setNullable(false);
        obj.setKeyable(true);
        table.addField(obj);

        obj = new Field("body_id");
        obj.setDatatype(DataType.INT);
        obj.setLength(10);
        obj.setNullable(false);
        obj.setKeyable(true);
        table.addField(obj);
        
        RowSet rowSet = null;
        try
        {
            String str="select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
            rowSet = dao.search(str);
            HashMap keyMap=new HashMap();
            while (rowSet.next())
            {
                keyMap.put(rowSet.getString("item_id"),"");                            
            }
            
            String strSql="select item_id from per_template_point where item_id in(select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+"))";
            rowSet = dao.search(strSql);
            while (rowSet.next())
            {
                keyMap.put(rowSet.getString("item_id"),"");                            
            }
            StringBuffer buf = new StringBuffer();
            Set keySet=keyMap.keySet();
            java.util.Iterator t=keySet.iterator();
            while(t.hasNext())
            {
                String strKey = (String)t.next();  //键值             
                buf.append("," + strKey);
            }
            String sqlStr = "";
            if(buf!=null && buf.length()>0) 
            {                   
                sqlStr = "select item_id from per_template_item where item_id in("+ buf.substring(1) + ")";
            }else {
                sqlStr = "select item_id from per_template_item where template_id = (select template_id from per_plan where plan_id="+planid+") and child_id is null";
            }

            rowSet = dao.search(sqlStr);
            while (rowSet.next())
            {
                obj = new Field("C_" + rowSet.getString("item_id"));
                obj.setDatatype(DataType.INT);
                obj.setKeyable(false);
                table.addField(obj);
                fieldStr.append("C_" + rowSet.getString("item_id") + ",");
            }
            
            if(rowSet!=null) {
                rowSet.close();
            }
                
        } catch (SQLException e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        dbw.createTable(table);
        fieldStr.append("object_id,body_id");
        return fieldStr.toString();
    }

    /**
     * 是否存在考核主体的指标权限表
     */
    public boolean isExistKhMainbodyPri(String planId)
    {

        boolean flag = false;
        StringBuffer strsql = new StringBuffer();
        strsql.append("SELECT name FROM sysobjects WHERE name = 'per_pointpriv_" + planId + "' AND type = 'U' ");
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            this.frowset = dao.search(strsql.toString());
            if (this.frowset.next()) {
                flag = true;
            }
        } catch (SQLException e)
        {
            flag = false;
        }
        return flag;
    }



    

    public Table cper_table_sql(String planId)
    {

        Table table = new Table("PER_TABLE_" + planId);
        Field obj = new Field("id");
        obj.setDatatype(DataType.INT);
        obj.setNullable(false);
        obj.setKeyable(true);
        table.addField(obj);

        obj = new Field("object_id");
        obj.setDatatype(DataType.STRING);
        obj.setLength(30);
        obj.setKeyable(false);
        table.addField(obj);

        obj = new Field("mainbody_id");
        obj.setDatatype(DataType.STRING);
        obj.setLength(10);
        obj.setKeyable(false);
        table.addField(obj);

        obj = new Field("score");
        obj.setDatatype(DataType.FLOAT);
        obj.setLength(12);
        obj.setDecimalDigits(6);
        obj.setKeyable(false);
        table.addField(obj);

        obj = new Field("amount");
        obj.setDatatype(DataType.FLOAT);
        obj.setLength(12);
        obj.setDecimalDigits(6);
        obj.setKeyable(false);
        table.addField(obj);

        obj = new Field("point_id");
        obj.setDatatype(DataType.STRING);
        obj.setLength(30);
        obj.setKeyable(false);
        table.addField(obj);

        obj = new Field("degree_id");
        obj.setDatatype(DataType.STRING);
        obj.setLength(1);
        obj.setKeyable(false);
        table.addField(obj);
        return table;

        // StringBuffer strSql = new StringBuffer("create table ");
        // strSql.append("PER_TABLE_"+planId + "(");
        // strSql.append("id int primary key,");
        // strSql.append("object_id varchar(30),");
        // strSql.append("mainbody_id varchar(10),");
        // strSql.append("score float,");
        // strSql.append("amount float,");
        // strSql.append("point_id varchar(30),");
        // strSql.append("degree_id varchar(1))");
        // return strSql.toString();
    }

    /**
     * 测试时候定义了考核主体类别,如果未定义则不能发布
     * 
     * @throws GeneralException
     */
    public boolean testMainBodyType(String planId) throws GeneralException
    {

        boolean flag = false;
        ContentDAO dao = new ContentDAO(this.conn);

        StringBuffer strsql = new StringBuffer();
        strsql.append("select * from per_plan_body where plan_id=");
        strsql.append(planId);
        try
        {
            this.frowset = dao.search(strsql.toString());
            if (!this.frowset.next()) {
                throw new GeneralException("计划必须设置考核主体类别，请在计划参数中设置！");
            } else {
                flag = true;
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return flag;
    }

 /**
  * 取得当前的季度
  * 
  * @return
  */
 public int getCurrentJD()
 {
     int jd = 1;
     Calendar calendar = Calendar.getInstance();
     int month = calendar.get(Calendar.MONTH);
     month = month + 1;
     switch (month)
     {
         case 1:
         case 2:
         case 3:
             jd = 1;
             break;
         case 4:
         case 5:
         case 6:
             jd = 2;
             break;
         case 7:
         case 8:
         case 9:
             jd = 3;
             break;
         default:
             jd = 4;
     }
     return jd;
 }

 public String isNull(String str)
 {
     if (str == null) {
         str = "";
     }
     return str;
 }

 public String getDatePart(String mydate, String datepart)
 {
     String str = "";
     if ("y".equalsIgnoreCase(datepart)) {
         str = mydate.substring(0, 4);
     } else if ("m".equalsIgnoreCase(datepart))
     {
         if ("0".equals(mydate.substring(5, 6))) {
             str = mydate.substring(6, 7);
         } else {
             str = mydate.substring(5, 7);
         }
     } else if ("d".equalsIgnoreCase(datepart))
     {
         if ("0".equals(mydate.substring(8, 9))) {
             str = mydate.substring(9, 10);
         } else {
             str = mydate.substring(8, 10);
         }
     }
     return str;
 }
 
 
 
 // 检查per_plan表中有没有A0000字段，若没有就创建  
 public void editArticleA0000() throws GeneralException
 {
     try
     {   
         if (1==1) {
             return ;
         }
         Table table = new Table("per_plan");
         DbWizard dbWizard = new DbWizard(this.conn);
         DBMetaModel dbmodel = new DBMetaModel(this.conn);
         boolean flag = false;
         
         if (!dbWizard.isExistField("per_plan", "A0000",false))
         {
             Field obj = new Field("A0000");
             obj.setDatatype(DataType.INT);
             obj.setKeyable(false);
             table.addField(obj);
             flag = true;                                
         }
         if (!dbWizard.isExistField("per_plan", "execute_user", false))
         {
             Field obj = new Field("execute_user");  
             obj.setDatatype(DataType.STRING);
             obj.setLength(50);
             obj.setKeyable(false);
             table.addField(obj);
             flag = true;
         }
         if (!dbWizard.isExistField("per_plan", "execute_date", false))
         {
             Field obj = new Field("execute_date");  
             obj.setDatatype(DataType.DATE);
             obj.setKeyable(false);
             table.addField(obj);
             flag = true;
         }
         if (!dbWizard.isExistField("per_plan", "distribute_user", false))
         {
             Field obj = new Field("distribute_user");   
             obj.setDatatype(DataType.STRING);
             obj.setLength(50);
             obj.setKeyable(false);
             table.addField(obj);
             flag = true;
         }
         if (!dbWizard.isExistField("per_plan", "distribute_date", false))
         {
             Field obj = new Field("distribute_date");   
             obj.setDatatype(DataType.DATE);
             obj.setKeyable(false);
             table.addField(obj);
             flag = true;
         }
         if (!dbWizard.isExistField("per_plan", "ByModel",false))
         {
             Field obj = new Field("ByModel");
             obj.setDatatype(DataType.INT);
             obj.setKeyable(false);
             table.addField(obj);
             flag = true;                                
         }
         
         if(flag)
         {
             dbWizard.addColumns(table);// 更新列       
             dbmodel.reloadTableModel("per_plan");
         }
         
     }catch(Exception e)
     {
         e.printStackTrace();
     }
 }
 
    
}
