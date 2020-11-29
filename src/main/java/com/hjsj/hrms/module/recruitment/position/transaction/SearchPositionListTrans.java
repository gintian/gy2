package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examinee.businessobject.ExamineeBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.Pageable;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title: SearchPositionListTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-1-15 下午04:48:57</p>
 * @author xiongyy
 * @version 1.0
 */
public class SearchPositionListTrans extends IBusiness{

    @Override
    public void execute() throws GeneralException {
        HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
        UserView userView = this.getUserView();
        //暂时是表示不同的查询
        String searchStr="";
        String flag="";
        String pageDescFro="";//从推荐职位过来的
        String a0100s = PubFunc.keyWord_reback((String)this.getFormHM().get("a0100s")); //推荐职位过来的人员
        String from ="";//从哪进的推荐职位
        from = (String) this.getFormHM().get("from");
        String positionType = "";
        boolean isModule= false;//当从推荐职位过来的 置为true
        try {
        	PositionBo pobo = new PositionBo(this.frameconn,new ContentDAO(this.frameconn),this.getUserView());
      		pobo.getCodeItem();
            ArrayList queryArray = new ArrayList();
            StringBuffer changeIds = new StringBuffer();
            if(hm!=null){
                String z0301 = (String)hm.get("z0301");
                if(z0301!=null&&z0301.length()>0)
                    hm.remove("z0301");//防止其他链接带的z0301 干扰新建职位页面
                searchStr = (String)hm.get("searchStr");
                if(!"true".equals(hm.get("back"))){
                	hm.remove("searchStr");
                }
                hm.remove("back");
                flag = (String)hm.get("flag");
                pageDescFro = (String)hm.get("pageDescFro");
                from =(String) hm.get("from")==null?"":(String) hm.get("from");
                positionType =hm.get("positionType")==null?"":(String)hm.get("positionType");
                hm.remove("positionType");
                hm.remove("pageDescFro");
                hm.remove("flag");
                hm.remove("from");
                // 刷新权限范围内职位的新简历数，候选人数，简历数量，已录用人数
                PositionBo.countA0100(new ContentDAO(this.frameconn),this.userView);
            }
            //表格table的唯一标识 通过这个可以获得存放在userviw里面的sql，list等等
            String tablekey = (String) this.getFormHM().get("tablekey");
            if(tablekey!=null){
            	TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tablekey);
                Pageable pageable = tableCache.getPageable();
                pageable.setPageNumber(1);
                tableCache.setPageable(pageable);
                userView.getHm().put(tablekey,tableCache);
            }
            
            
            
            ArrayList tempList = (ArrayList) this.getFormHM().get("queryArray");  //查询方案返回的信息 根据此信息返回查询的where 条件
            if(!"".equalsIgnoreCase(positionType)&&tempList==null)
            {
            	tempList = new ArrayList();
            	tempList.add("0");//时间
            	tempList.add("0");//状态
            	tempList.add("1");//职位类别
            	flag = "";
            }
            if(tempList!=null){
                queryArray= tempList;
            }else if(searchStr!=null&&searchStr.length()>0&&!"1".equals(flag)){   //返回时走这里
                String[] split = searchStr.split(",");
                for (int i = 0; i < split.length; i++) {
                    queryArray.add(split[i]);
                    if(!"0".equals(split[i])){
                        if (i == 0) {
                            changeIds.append("A" + split[i]+",");
                        } else if (i == 1) {
                            changeIds.append("B" + split[i]+",");
                        } else if (i == 2) {
                            changeIds.append("C" + split[i]+",");
                        }
                    }
                    
                }
                if(changeIds.length()>0)
                    changeIds.setLength(changeIds.length()-1);
            }
            this.getFormHM().put("changeIds", changeIds.toString());
            
            String queryStr = (String) this.getFormHM().get("queryStr");     //快速查询 一键查询的条件
            
            
            ArrayList positionColumn = new ArrayList();  //列表头信息
            ArrayList queryList = new ArrayList();    //查询 方案
            StringBuffer strsql = new StringBuffer(""); //查询职位列表的sql
            String strwhere="";
            if("1".equals(flag))
                from="";
            String max_count = "";
            if(from!=null&&from.length()>0){
            	queryArray = null;
                isModule = true;
                ParameterXMLBo bo = new ParameterXMLBo(this.getFrameconn());
                HashMap map = bo.getAttributeValues();
                max_count = (String) map.get("max_count"); //配置参数得到的岗位申请最大数
                
                if(max_count==null||"".equals(max_count))
                    max_count="3";
            }else{
                a0100s="";
                
            }
            
            pobo.structureRequirement();
            ExamineeBo bo = new ExamineeBo(this.frameconn,this.userView);
            bo.isHasSubjects();
            
            positionColumn = pobo.getColumnList(isModule);
            queryList = pobo.getQueryList();
            strsql.append(pobo.getPositionSql(positionColumn));
            strwhere = pobo.getWhere(flag,queryStr,queryArray,isModule,a0100s);
            
            strsql.append(strwhere);
            
            //查询方案 结束 坐标
            ArrayList endlist = new ArrayList();
            endlist.add("0");
            endlist.add("5");
            endlist.add("1");
            String orSql = " ((z0101 IS NULL or Z0101='') and z0319<>'06'))";
            //当从推荐职位过来的,只显示已发布状态的职位
            if(isModule)
            	orSql = " ((z0101 IS NULL or Z0101='') and z0319='04'))";
            //解决历史数据问题 只显示发布状态的批次
            String batchsql = " and z.z0301 in(select z0301 from z03 where  z0101 in(select z0101 from z01 where z0129='04') or " + orSql;
            //选中结束
            if( tempList != null  &&  tempList.size() ==3 && 
            		(endlist.get(1).equals(tempList.get(1)) || "0".equals(tempList.get(1))))
            	batchsql = " and z.z0301 in(select z0301 from z03 where  z0101 in(select z0101 from z01 where z0129='04') or (z0101 IS NULL or Z0101=''))";
            strsql.append(batchsql);
            this.userView.getHm().put("batchsql_replace", batchsql);
            ArrayList buttonList = new ArrayList();//功能按钮
            buttonList = pobo.getButtonList(isModule);//生成功能按钮
            //如果有已发布职位直接定位到发布
            if(pobo.hasPublicPos())
            	this.getFormHM().put("searchStr", "0,2,1");
            else
            	this.getFormHM().put("searchStr", "0,0,1");
            
            String publishWithApprove = SystemConfig.getPropertyValue("zp_pos_publish_with_approve");
        	if(StringUtils.isEmpty(publishWithApprove))
        		publishWithApprove ="false";
            
            this.getFormHM().put("publishWithApprove",publishWithApprove);
            
            this.getFormHM().put("buttonList",buttonList);
            this.getFormHM().put("strsql", strsql.toString());    
            this.getFormHM().put("positionColumn", positionColumn);
            this.getFormHM().put("queryList", queryList);
            this.getFormHM().put("ordersql", "order by Z0321 ASC, Z0325 ASC,z0301 ASC");
            this.getFormHM().put("a0100s", a0100s);
            this.getFormHM().put("pageDesc", pageDescFro);
            this.getFormHM().put("from", from);
            this.getFormHM().put("max_count", max_count);
            this.getFormHM().put("defaultQuery", pobo.getDefaultQuery());
			this.getFormHM().put("optionalQuery", pobo.getOptionalQuery());
			this.getFormHM().put("batchQuery", pobo.getBatchQuery());
			this.getFormHM().put("hasTheFunction", this.userView.hasTheFunction("3110110"));
			this.getFormHM().put("appStatus", pobo.getApprovalstatus());
            
            if(tablekey!=null&&tablekey.length()>0){   //当查到tablekey说明是走条件查询的
            	TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tablekey);
            	//公共查询条件
    			String querySql = "";
    			String fastQuerySql = "";
    			if(tableCache.getCustomParamHM()!=null){
    				querySql = (String) tableCache.getCustomParamHM().get("pubQuerySql");
    				fastQuerySql = (String) tableCache.getCustomParamHM().get("fastQuerySql");
    			}
            	querySql = StringUtils.isEmpty(querySql)?"":querySql;
            	fastQuerySql = StringUtils.isEmpty(fastQuerySql)?"":fastQuerySql;
            	tableCache.setQuerySql(querySql+fastQuerySql);
            	tableCache.setTableSql(strsql.toString());
                userView.getHm().put(tablekey, tableCache);
                
            }
            if(hm!=null&&hm.get("pendingId")!=null) {
            	String pendingId = (String) hm.get("pendingId");
            	String sql = "update t_hr_pendingtask set Pending_status=1 where pending_id=? and (ext_flag is null or ext_flag='')";
            	ArrayList<String> valueList = new ArrayList<String>();
            	valueList.add(pendingId);
            	ContentDAO dao = new ContentDAO(this.frameconn);
            	dao.update(sql, valueList);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
   
}
