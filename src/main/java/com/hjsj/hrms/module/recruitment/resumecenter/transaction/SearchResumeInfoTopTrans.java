package com.hjsj.hrms.module.recruitment.resumecenter.transaction;

import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/****
 *	查看简历详情时相关信息
 * <p>Title: SearchResumeInfoTopTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2015-12-23 下午05:19:18</p>
 * @author xiexd
 * @version 1.0
 */
public class SearchResumeInfoTopTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String zp_pos_id = (String) this.getFormHM().get("zp_pos_id");
		zp_pos_id = PubFunc.decrypt(SafeCode.decode(zp_pos_id));
		RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
		String nbase="";  //应聘人员库
		if(vo!=null)
			nbase=vo.getString("str_value");
		else
			throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
		String a0100 = (String) this.getFormHM().get("a0100");
		a0100 = PubFunc.decrypt(SafeCode.decode(a0100));
		String link_id = (String) this.getFormHM().get("link_id");
		String z0381 = (String) this.getFormHM().get("z0381");
		String from = (String) this.getFormHM().get("from");
		ResumeBo rbo = new ResumeBo(this.frameconn,this.userView);
		HashMap map = new HashMap();
		map.put("link_id", link_id);
		map.put("flowId", PubFunc.decrypt(z0381));
		LazyDynaBean resumeInfo = rbo.getResumeInfo(nbase, a0100, zp_pos_id, map);
		if("process".equals(from)){
			int current = 0; 
			int pagesize = 0; 
			int rowindex = 0; 
			if(this.getFormHM().get("current") instanceof Integer){
				current = Integer.valueOf((Integer)this.getFormHM().get("current"));
				pagesize = Integer.valueOf((Integer)this.getFormHM().get("pagesize"));
				rowindex = Integer.valueOf((Integer) (this.getFormHM().get("rowindex")==null?0:(Integer)this.getFormHM().get("rowindex")));
			}else{
				current = Integer.valueOf((String)this.getFormHM().get("current"));
				pagesize = Integer.valueOf((String)this.getFormHM().get("pagesize"));
				rowindex = Integer.valueOf((String)(this.getFormHM().get("rowindex")==null?"0":this.getFormHM().get("rowindex")));
			}
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("zp_recruit_00001");
			String tableSql = "";
			String sortSql = "";
			String querySql = "";
			String filterSql = "";
			if(tableCache!=null){
				tableSql = tableCache.getTableSql();
				sortSql = tableCache.getSortSql();
				querySql = tableCache.getQuerySql()==null?"":tableCache.getQuerySql();
				filterSql = tableCache.getFilterSql()==null?"":tableCache.getFilterSql();
			}
			ArrayList<HashMap> candidate = rbo.getCandidate(tableSql, querySql, filterSql, sortSql, zp_pos_id, a0100, current, pagesize, rowindex);
			HashMap nextCandidate = candidate.get(0);
			HashMap lastCandidate = candidate.get(1);
			nextCandidate.put("link_id", link_id);
			nextCandidate.put("z0381", z0381);
			lastCandidate.put("link_id", link_id);
			lastCandidate.put("z0381", z0381);
			this.getFormHM().put("nextCandidate", nextCandidate);
			this.getFormHM().put("lastCandidate", lastCandidate);
		}
		resumeInfo.set("rootPath", "");
		this.getFormHM().put("resumeInfo", resumeInfo);
	}
	
}
