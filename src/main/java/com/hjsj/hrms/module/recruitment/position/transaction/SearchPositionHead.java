package com.hjsj.hrms.module.recruitment.position.transaction;

import com.hjsj.hrms.businessobject.hire.HireTemplateBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchPositionHead extends IBusiness {

	/**
	 * @param args
	 */
@Override
public void execute() throws GeneralException {
	RowSet rs = null;
	RowSet rowSet2 = null;
	try {
			//得到当前职位序号
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String z0301 = PubFunc.decrypt(SafeCode.decode((String)hm.get("z0301")));
			String z0381 = SafeCode.decode((String)hm.get("z0381"));
			if("undefined".equalsIgnoreCase(z0381) || PubFunc.decrypt(z0381)==null||"".equals(PubFunc.decrypt(z0381)))
			{
				 z0381 = PubFunc.encrypt(this.getLink_id(z0301));
			}
			String node_id = SafeCode.decode((String)hm.get("node_id"));
			String link_id = SafeCode.decode((String)hm.get("link_id"));
			String page = SafeCode.decode((String)hm.get("page"));
			String from = (String) hm.get("from");
			String back = (String) hm.get("back");
			hm.remove("from");
			hm.remove("page");
			hm.remove("back");
			userView.getHm().put("isback", back);//职位候选人进入简历详情，返回加入查询条件使用
			String pageNum = SafeCode.decode((String)hm.get("pageNum"));
			String searchStr = SafeCode.decode((String)hm.get("searchStr"));
			String pagesize = SafeCode.decode((String)hm.get("pagesize"));
			String sign = (String)hm.get("sign");
			
			UserView userView = this.getUserView();
			HireTemplateBo bo = new HireTemplateBo(this.frameconn);
			String b0110 = bo.getB0110(userView);
			//查询职位信息
			ArrayList list = new ArrayList();
			StringBuffer sql = new StringBuffer();
			sql.append("select z0301,Z0351,Z0333,codeitemdesc,z0315," +
			"z0375,z0319 from Z03 z03 left join organization org " +
			"on z03.Z0325=org.codeitemid where Z0301=? ");
			list.add(z0301);
			ContentDAO dao =  new ContentDAO(frameconn);
			if(link_id==null|| "".equals(link_id))
			{
				String link_sql = "select id from zp_flow_links where flow_id='"+PubFunc.decrypt(z0381)+"'  order by seq";
				RowSet link_rs = dao.search(link_sql);
				if(link_rs.next())
				{
					link_id = link_rs.getString("id");
				}
			}
			String statusSql = "select * from  zp_flow_status where link_id=? and valid=1 order by seq";
			ArrayList link = new ArrayList();
			link.add(link_id);
			rowSet2 = dao.search(statusSql,link);
			String status_id="";
			if(rowSet2.next()){
				status_id=rowSet2.getString("status");
			}
			LazyDynaBean bean = new LazyDynaBean();
			ArrayList positionInfo = new ArrayList();
			rs=dao.search(sql.toString(), list);
			if(rs.next())
			{
				StringBuffer position=new StringBuffer(rs.getString("Z0351")!=null?rs.getString("Z0351"):"");
				if(rs.getString("Z0333")!=null&&!"".equals(rs.getString("Z0333")))
				{
					position.append("-");
					position.append(rs.getString("Z0333")!=null?rs.getString("Z0333"):"");
				}
			//	position.append(rs.getString("Z0301")!=null?rs.getString("Z0301"):"");
				bean.set("z0301", PubFunc.encrypt(rs.getString("z0301")));
				bean.set("position", position.toString());
				bean.set("department", rs.getString("codeitemdesc")!=null?rs.getString("codeitemdesc"):"");
				bean.set("number", rs.getString("z0315")==null? "0" : rs.getString("z0315"));
				if(rs.getDate("z0375")!=null&&!"".equals(rs.getDate("z0375")))
				{		
					RecruitUtilsBo reBo = new RecruitUtilsBo(this.frameconn);
					String dateFormat = "";
					dateFormat = reBo.getDateFormat("z0375");
					SimpleDateFormat df = null;
					Timestamp z0375Time = null;
				    if (StringUtils.isEmpty(dateFormat))
                        df = new SimpleDateFormat("yyyy-MM-dd");
                    else
                        df = new SimpleDateFormat(dateFormat);
				    
				    z0375Time = rs.getTimestamp("z0375");
					bean.set("endTime",df.format(z0375Time));
				}else{
					bean.set("endTime","未设置");
				}
				if(!StringUtils.equalsIgnoreCase(from, "process"))
				{
					this.userView.getHm().put("from", from);
				}
				if(from==null||StringUtils.equalsIgnoreCase(from, "process")){
					from = (String)this.userView.getHm().get("from");
				}
				bean.set("status", rs.getString("z0319"));
				bean.set("statu",getStatuByZ0319(dao,rs.getString("z0319")));
				bean.set("z0381", z0381);
				bean.set("pageNum", pageNum);
				bean.set("searchStr", searchStr);
				bean.set("pagesize", pagesize);
				bean.set("from", from);
				bean.set("page", page);
				bean.set("node_id", node_id);
				bean.set("link_id", link_id);
				bean.set("status_id", status_id);
				positionInfo.add(bean);
				this.getFormHM().put("positionInfo", positionInfo);
				this.getFormHM().put("sign",sign);
			}else{
				throw new GeneralException(ResourceFactory.getProperty("train.info.import.error.codenor"));
			}
		} catch (Exception e) {
		    e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(rowSet2);
		}
	}

    private String getStatuByZ0319(ContentDAO dao, String z0319) throws GeneralException {
        String statu = "";
        try {
            FieldItem item = DataDictionary.getFieldItem("z0319");
            String codesetid = item.getCodesetid();
            String sql ="select codeitemdesc from codeitem where codesetid = ? and codeitemid = ?";
            ArrayList list = new ArrayList();
            list.add(codesetid);
            list.add(z0319);
            RowSet rs = dao.search(sql,list);
            while (rs.next()) {
                statu=rs.getString("codeitemdesc");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return statu;
    }
    
    private String getLink_id(String z0301)
	{
		String link_id = "";
		try {
			StringBuffer sql = new StringBuffer("select z0381 from z03 where z0301='"+z0301+"'");
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = dao.search(sql.toString());
			if(rs.next())
			{
				link_id = rs.getString("z0381").toString();
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return link_id;
	}
}
