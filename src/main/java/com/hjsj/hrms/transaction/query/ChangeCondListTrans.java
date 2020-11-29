package com.hjsj.hrms.transaction.query;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class ChangeCondListTrans extends IBusiness {

	public void execute() throws GeneralException {
		String ids = (String)this.getFormHM().get("ids");
		ids=com.hjsj.hrms.utils.PubFunc.keyWord_reback(ids);
		String categories = (String)this.getFormHM().get("categories");
		categories=categories==null?"":categories;
		String oldCategories = (String)this.getFormHM().get("oldCategories");
		oldCategories = oldCategories == null ? "" : oldCategories;
		
		String type = (String)this.getFormHM().get("type");
		type=type==null||type.length()==0?"1":type;
		ArrayList condlist = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer strsql = new StringBuffer();
			if(ids!=null&&ids.length()>2){
				strsql.append("update lexpr set categories='"+categories+"' where id in("+ids.substring(2)+"')");
				dao.update(strsql.toString());
			}
			//判断旧分类下是否还有查询条件，如果有则置空，没有就传到前台，再分类下拉选项中删除
			if(StringUtils.isNotEmpty(oldCategories)) {
	        	strsql.setLength(0);
	        	strsql.append("select id,name,type,categories from lexpr where type=?");
	        	strsql.append(" and categories=? order by norder");
	        	ArrayList<String> paramList = new ArrayList<String>();
	        	paramList.add(type);
	        	paramList.add(oldCategories);
	        	this.frowset = dao.search(strsql.toString(), paramList);
	        	if(this.frowset.next())
	        		oldCategories = "";
	        	
	        }
			
			strsql.setLength(0);
			strsql.append("select id,name,type,categories from lexpr where type='");
	        strsql.append(type);
	        if(categories.length()==0){
	        	strsql.append("' and (categories='' or categories is null) order by norder");
	        }else
	        	strsql.append("' and categories='"+categories+"' order by norder");
	        this.frowset = dao.search(strsql.toString());
	        int i=1;
	        while(this.frowset.next()){
	        	 if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
	                	continue;
	        	CommonData cd = new CommonData(this.frowset.getString("id"),i+"."+this.getFrowset().getString("name"));
                condlist.add(cd);
                ++i;
	        }
	        
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}finally{
			this.getFormHM().put("condlist", condlist);
			this.getFormHM().put("oldCategories", oldCategories);
		}
	}

}
