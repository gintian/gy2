package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ParseQueryItemsBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeCenterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchResumeCenterTrans</p>
 * <p>Description:查询简历中心列表</p>
 * <p>Company:hjsj</p>
 * <p>create time:2015-01-22</p>
 * @author wangcq
 * @version 1.0
 * 
 */
public class SearchResumeCenterTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try{
			PositionBo pobo = new PositionBo(this.frameconn,new ContentDAO(this.frameconn),this.getUserView());
			pobo.getCodeItem();
			HashMap reqHM = (HashMap)this.getFormHM().get("requestPamaHM");
			String back = "";   //返回标志
			boolean othModule = false;  //其它模块进入简历中心的标志位
			String positionDesc = (String)this.userView.getHm().get("positionDesc");  //其它模块进入时的条件(拼装一起放入)
			positionDesc = PubFunc.decrypt(positionDesc);
			String from=(String)this.getFormHM().get("from"); 
			String flag = (String)this.getFormHM().get("flag");   //搜索位置标识   1：搜索框   2：查询栏  3：应聘情况栏 
			
			String schemeValues = (String)this.getFormHM().get("schemeValues");     //应聘情况
			ArrayList<MorphDynaBean> items = (ArrayList<MorphDynaBean>)this.getFormHM().get("items");
			
			if(reqHM!=null&&reqHM.get("from")!=null) //从菜单进入
			{
				back = (String)reqHM.get("back");
				from=(String)reqHM.get("from");
				reqHM.remove("from");
				if("resumeCenter".equalsIgnoreCase(from)) //简历中心，默认查询全部信息
				{
					flag="3";
					schemeValues="0,0,0";
				}
				else //人才库
				{
					flag="3";
					schemeValues="0,2,0";
				}
				if(StringUtils.isNotEmpty((String)reqHM.get("schemeValues"))){
					schemeValues = (String)reqHM.get("schemeValues");
					reqHM.remove("schemeValues");
				}
				if(!StringUtils.equalsIgnoreCase(back, "true")){
					HashMap map = new HashMap();
					String zp_pos_id = (String)reqHM.get("zp_pos_id")==null ? "" :(String)reqHM.get("zp_pos_id");
					zp_pos_id = PubFunc.decrypt(zp_pos_id);
					map.put("zp_pos_id", zp_pos_id);
					reqHM.remove("zp_pos_id");
					positionDesc = JSON.toString(map);
					this.userView.getHm().put("positionDesc", PubFunc.encrypt(positionDesc));
				}
			} 
			if(from==null)
				from="resumeCenter";
			
			ResumeCenterBo resumeCenterBo = new ResumeCenterBo(this.frameconn,this.userView,from);
			String positionStr = resumeCenterBo.getPositionStr(positionDesc);    //获取招聘职位进入简历中心的筛选条件
			if(positionStr.length() > 0)
				othModule = true;
			ArrayList queryscheme = resumeCenterBo.getQueryScheme();  //获取查询方案
			ArrayList fielditems = resumeCenterBo.getFieldList(from);   //获取相应指标列表
			
			ArrayList columns = resumeCenterBo.getColumnList(fielditems,othModule,from);   //表头字段列表
			
			String tablekey = "zp_resume_191130_00001";    //前台表格的唯一标识
			if(!"resumeCenter".equals(from))
			    tablekey = "zp_talent_191130_00001";
			
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tablekey);   //前台表格的相关数据集合
			
			String queryStr = "";
			if(StringUtils.equalsIgnoreCase(back, "true"))
				reqHM.remove("back");
			 
			String searchBoxContent= (String)this.getFormHM().get("searchBoxContent"); //搜索框中内容可以为：姓名、email、职位、专业、学校
			ArrayList searchBar = (ArrayList)this.getFormHM().get("searchBar");
			
			HashMap whrMap = new HashMap();   //查询内容集合
			whrMap.put("searchBox", searchBoxContent);
			whrMap.put("searchBar", searchBar);
			whrMap.put("schemeValues", schemeValues);
			
			
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname="";  //应聘人员库
			if(vo!=null)
				dbname=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
			
			//解决历史数据问题 只显示发布状态的批次
			String batchsql = " and z0301 in(select z0301 from z03 where  z0101 in(select z0101 from z01 where z0129='04') or z0101 is null or z0101 ='')";
			
			if(items!=null){
				String subModuleId = "";
				if("resumeCenter".equals(from)){
					subModuleId = "zp_resume_191130_00001";
				}else if("talents".equals(from)){
					subModuleId = "zp_talent_191130_00001";
				}
				TableDataConfigCache queryCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
				String oldQuerySql = queryCache.getQuerySql();
				ParseQueryItemsBo queryItemsBo = new ParseQueryItemsBo();
				String queryString = queryItemsBo.queryString(items,dbname,"");
				String combineSql = tableCache.getTableSql();
				//替换掉原来的批次条件
				for (MorphDynaBean bean : items) {
					HashMap dynaBeanMap = PubFunc.DynaBean2Map(bean);
					String itemid = (String)dynaBeanMap.get("itemid");
					if("Z0103".equalsIgnoreCase(itemid)){
						combineSql = combineSql.replace(batchsql, queryString.replace("myGridData.", ""));
						combineSql = combineSql.replace("where z0129='04'", "");
						break;
					}else{
						if(StringUtils.isNotEmpty(oldQuerySql))
							combineSql = combineSql.replace(oldQuerySql.replace("myGridData.", "") , batchsql);
					}
				}
				//公共查询去掉批次条件后，拼接原来的批次sql
				if(items.size()==0&&!"0,1,2".equals(schemeValues)) {
					//需要把sql还原
					String querySql = ((String) queryCache.getCustomParamHM().get("pubQuerySql")).replace("myGridData.", "");
					combineSql = combineSql.replace(querySql,batchsql);
				}
				tableCache.setTableSql(combineSql);
				this.userView.getHm().put("hire_sql", combineSql);//查询语句放入userview，查看简历时使用
				this.userView.getHm().put("export_sql", combineSql);//查询语句放入userview，导出简历时使用
				//保存快速查询条件备用
				if(queryCache.getCustomParamHM()==null)
					queryCache.setCustomParamHM(new HashMap<String, String>());
				String fastQuerySql = (String) queryCache.getCustomParamHM().get("fastQuerySql");
				fastQuerySql = StringUtils.isEmpty(fastQuerySql)?"":fastQuerySql;
				queryCache.setQuerySql(queryString+fastQuerySql);
				queryCache.getCustomParamHM().put("pubQuerySql", queryString);
				return;
			}
			StringBuffer sb=this.userView.getDbpriv();//用户人员库权限
			if(!this.userView.isSuper_admin()&&!sb.toString().contains(dbname)){
				throw GeneralExceptionHandler.Handle(new Exception("您没有操作应聘人员库权限!"));
			} 
			String whrstr =resumeCenterBo.getFilterWhl(dbname); //查询范围SQL
			//zhangcq 2016/8/30 如果从其他模块进入简历中心 不拼接我的职位
			String conditionStr = resumeCenterBo.getConditionStr(flag,whrMap,positionDesc,"");     //根据页面选择生成的sql语句条件
			String sqlTemp = resumeCenterBo.getQueryStr(whrMap,fielditems,dbname,conditionStr,whrstr,positionStr,from);  //查询语句
			if("0,1,2".equals(schemeValues))//未应聘职位简历
				queryStr = sqlTemp + " and "+dbname +"a01.a0100 not in (select a0100 from zp_pos_tache)";
			else {
				queryStr = sqlTemp + conditionStr + positionStr;  //因为导出简历excel要获取查询指标所以拼接查询方案和筛选条件改到此处进行
				//只有从简历中心进入时只显示发布状态的批次，不显示未应聘职位简历 
				if("resumeCenter".equals(from)) 
					queryStr += " and z0301 in(select z0301 from z03 where  z0101 is null or z0101 ='' or z0101 in(select z0101 from z01 where z0129='04') )";
			}
			
			queryStr = " select * from ( "+queryStr+" ) temp where 1=1";
			
            String xmlSrc = "recruitment/resumecenter";
            if(!"resumecenter".equalsIgnoreCase(from))
            {
            	xmlSrc = "recruitment/talents";
            }
			this.getFormHM().put("buttonList",resumeCenterBo.getButtonList(othModule));
			this.getFormHM().put("from", from);
			this.getFormHM().put("othModule", othModule);
			this.getFormHM().put("groupcolumns", columns);
			this.getFormHM().put("queryscheme", queryscheme);
			this.getFormHM().put("sqlstr", queryStr);
			this.getFormHM().put("defaultQuery", resumeCenterBo.getDefaultQuery(from));
			this.getFormHM().put("optionalQuery", resumeCenterBo.getOptionalQuery(from));
			this.getFormHM().put("batchQuery", resumeCenterBo.getBatchQuery());
			boolean hasTheFunction = false;
			if("resumeCenter".equalsIgnoreCase(from)){
				hasTheFunction = this.userView.hasTheFunction("3110208");
				this.getFormHM().put("orderbystr", "order by recdate desc");
				this.getFormHM().put("exceptItems", resumeCenterBo.getNoticeField(columns));
				this.getFormHM().put("jsonStr", resumeCenterBo.getSelectField());
			}else{
				hasTheFunction = this.userView.hasTheFunction("3110308");
				this.getFormHM().put("orderbystr", "order by a0101 ASC");
			}
			
			this.getFormHM().put("hasTheFunction", hasTheFunction);
			this.getFormHM().put("constantxml", xmlSrc);
			this.getFormHM().put("schemeValues", schemeValues);
			this.userView.getHm().put("hire_sql", queryStr);//查询语句放入userview，查看简历时使用
			this.userView.getHm().put("export_sql", queryStr);//查询语句放入userview，导出简历时使用
			this.userView.getHm().put("export_sql_field", sqlTemp);//需要查询的指标放入userview，导出简历时使用
			
			if(tableCache != null){
				String querySql = "";
				String fastQuerySql = "";
				if(tableCache.getCustomParamHM()!=null){
					querySql = (String) tableCache.getCustomParamHM().get("pubQuerySql");
					fastQuerySql = (String) tableCache.getCustomParamHM().get("fastQuerySql");
				}
				querySql = StringUtils.isEmpty(querySql) ? "":querySql;
				fastQuerySql = StringUtils.isEmpty(fastQuerySql)?"":fastQuerySql;
				tableCache.setQuerySql(querySql+fastQuerySql);
				tableCache.setTableSql(queryStr);
				this.userView.getHm().put(tablekey, tableCache);
				String sortSql = tableCache.getSortSql();
				sortSql = sortSql==null?"":sortSql;
				this.userView.getHm().put("sortSql", sortSql);//排序语句放入userview，导出简历时使用
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
