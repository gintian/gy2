package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:显示审批状态和人数</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:10 30, 2010</p> 
 *@author xieguiquan
 *@version 4.0
 */
public class ShowApproveState extends IBusiness {

	public void execute() throws GeneralException {
		
				String sql = (String)this.getFormHM().get("sql");
				sql = PubFunc.decrypt(sql);
				HashMap map = new HashMap();
				ContentDAO dao=new ContentDAO(this.frameconn);
				ArrayList list = new ArrayList();
				ArrayList statelist = new ArrayList();
				try {
					this.frowset = dao.search(sql);

					while(this.frowset.next()){
						String sp_flag = this.frowset.getString("sp_flag");
			//			FieldItem fielditem = DataDictionary.getFieldItem("sp_flag");
						if(map!=null&&map.get(sp_flag)!=null){
							int count = Integer.parseInt((String)map.get(sp_flag))+1;
							map.put(sp_flag, ""+count);
						}else{
							map.put(sp_flag, "1");
							list.add(sp_flag);
						}
					}
					if(list.size()>0)
						for(int i=0;i<list.size();i++){
							String sp_flag = (String)list.get(i);
							LazyDynaBean bean = new LazyDynaBean();
							bean.set("personnums", map.get(sp_flag));
							this.frowset = dao.search("select codeitemdesc from codeitem where codesetid='23'and codeitemid='"+sp_flag+"'");
							String desc="";
							if(this.frowset.next())
								desc =this.frowset.getString("codeitemdesc");
								bean.set("itemdesc",desc);
							
							statelist.add(bean);
						}
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
				finally{
					this.getFormHM().put("statelist", statelist);
				}
			
			}
			




}
