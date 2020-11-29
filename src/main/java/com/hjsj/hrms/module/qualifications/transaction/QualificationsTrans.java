package com.hjsj.hrms.module.qualifications.transaction;

import com.hjsj.hrms.module.qualifications.businessobject.QuanlificationsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 *
 * 
 * 类描述：评审条件  页面初始化
 * 创建人：liubq
 * 创建时间： 11, 2015 11:01:54 AM 
 * 
 * 
 **/

public class QualificationsTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		//区分请求模块 module_type为空 或者"1" 职称评审，module_type为"2" 证照管理
		String module_type = (String) this.getFormHM().get("module_type");
		boolean fromUrl = this.getFormHM().get("fromUrl")==null?false:(Boolean) this.getFormHM().get("fromUrl");
		//从链接进来的参数是加密的，需要解密
		/**
		 * 58754
		 * 经查前台参数fromUrl固定为true
		 * 兼容工作大厅直接配置menu菜单链接 故做以下模块号兼容
		 */
		if(fromUrl && !",1,2,".contains(","+module_type+",")) {
			module_type = PubFunc.decryption(module_type);
			if(!StringUtils.isEmpty(module_type)&&module_type.indexOf("module_type")>-1)
				module_type = module_type.substring(module_type.indexOf("=")+1);
		}
		
		QuanlificationsBo quanlificationsBo = new QuanlificationsBo(this.getFrameconn(),this.getUserView());
		int totalPageNum =0;
		int pageNum =1;
		int paid = 0;
		int width = 1416; 
		String conditionitemsid = null;
		String conditionid = null;
		if((Integer)this.getFormHM().get("width")!=0)
			width =(Integer)this.getFormHM().get("width");
		if((Integer)this.getFormHM().get("totalPageNum")!=0)
			totalPageNum =(Integer)this.getFormHM().get("totalPageNum");
		if((Integer)this.getFormHM().get("pageNum")!=0)
			pageNum =(Integer)this.getFormHM().get("pageNum");
		if(!StringUtils.isEmpty((String)this.getFormHM().get("conditionitemsid")))
			conditionitemsid = (String)this.getFormHM().get("conditionitemsid");
		if(!StringUtils.isEmpty((String)this.getFormHM().get("conditionid")))
			conditionid = (String)this.getFormHM().get("conditionid");
		ContentDAO  dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		ArrayList list = new ArrayList();
		ArrayList values = new ArrayList();
		String busiId = "9";
		if("2".equals(module_type))
			busiId = "10";
		String wheresql = quanlificationsBo.getWhereSql(busiId);
		StringBuffer sql = new StringBuffer("select * from zc_condition ");
		sql.append(wheresql);
		if("1".equals(module_type)||StringUtils.isEmpty(module_type))
			sql.append(" and "+ Sql_switcher.isnull("module_type", "1") +"=1 ");
		else {
			sql.append(" and module_type=? ");
			values.add(module_type);
		}
		sql.append(" order by condition_id");
		try {
			rs=dao.search(sql.toString(), values);
			while(rs.next()){
				LazyDynaBean ldb= new LazyDynaBean();
				String unit = rs.getString("b0110");
				if(org.apache.commons.lang.StringUtils.isEmpty(unit))
					unit = "";
				String conid = rs.getString("condition_id");
				ldb.set("condition_id", PubFunc.encrypt(conid));
				ldb.set("zc_series", rs.getString("zc_series"));
				ldb.set("description", StringUtils.isEmpty(rs.getString("description")) ? "" : rs.getString("description"));
				ldb.set("attachmentlist",quanlificationsBo.getAttachmentList(conid));//附件列表
				ldb.set("create_time", String.valueOf(rs.getDate("create_time")));
				ldb.set("create_user", rs.getString("create_user"));
				ldb.set("create_fullname", rs.getString("create_fullname"));
				ldb.set("modify_time", String.valueOf(rs.getDate("modify_time")));
				ldb.set("b0110", unit);
				if(quanlificationsBo.canOper(this.userView.getUnitIdByBusi("9"),unit)){
					ldb.set("flag", "true");//是否可以操作
				}else{
					ldb.set("flag", "false");
				}
				list.add(ldb);
			}
			int geshu = width/80;
			if(list.size()>(geshu-1)){
				paid =(pageNum-1)*(geshu-2);
			}else{
				paid = 0;
			}
			totalPageNum =(list.size()%(geshu-2))==0?(list.size()/(geshu-2)):(list.size()/(geshu-2))+1;
			if(conditionid==null|| "".equals(conditionid)){
				conditionitemsid = "conditions"+paid;
				try{
					conditionid =(String) (list.size()==0?"":((LazyDynaBean)list.get(paid)).get("condition_id"));
				}catch(Exception e){
					conditionid =(String) (list.size()==0?"":((LazyDynaBean)list.get(paid-1)).get("condition_id"));
					conditionitemsid = "conditions"+(paid-1);
					pageNum -= 1;
				}
			}else{
				if(conditionitemsid==null){
					this.getFormHM().put("isAdd","T");
					conditionitemsid = "conditions"+(list.size()-1);
					pageNum =totalPageNum;
				}
			}
			this.getFormHM().put("geshu",geshu-2);
			this.getFormHM().put("totalPageNum",totalPageNum);
			this.getFormHM().put("conditionitemsid",conditionitemsid);
			this.getFormHM().put("conditionid",conditionid);
			this.getFormHM().put("conditions", list);
			this.getFormHM().put("attchments", new ArrayList());
			this.getFormHM().put("pageNum", pageNum);
			boolean addVersion = this.userView.hasTheFunction("3800101");//创建权限
			boolean editVersion = this.userView.hasTheFunction("3800102");//创建权限
			boolean deleteVersion = this.userView.hasTheFunction("3800103");//创建权限
			if("2".equalsIgnoreCase(module_type)) {//获取证照管理权限
				addVersion = this.userView.hasTheFunction("40001");
				editVersion = this.userView.hasTheFunction("40001");
				deleteVersion= this.userView.hasTheFunction("40001");
			}
			this.getFormHM().put("addVersion", addVersion);
			this.getFormHM().put("editVersion", editVersion);
			this.getFormHM().put("deleteVersion", deleteVersion);
			this.getFormHM().put("module_type",module_type);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
