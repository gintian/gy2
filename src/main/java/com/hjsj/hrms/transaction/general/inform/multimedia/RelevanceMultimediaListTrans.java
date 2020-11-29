package com.hjsj.hrms.transaction.general.inform.multimedia;

import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 
 * Title:RelevanceMultimediaListTrans.java
 * Description:
 * Company:hjsj
 * Create time:Apr 25, 2014:1:46:55 PM
 * @author zhaogd
 * @version 6.x
 */
public class RelevanceMultimediaListTrans extends IBusiness{

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		HashMap hm=(HashMap) this.getFormHM().get("requestPamaHM");
		String kind = (String)hm.get("kind");
		String setname = (String)hm.get("setname");
		String a0100 = (String)hm.get("a0100");
		String userbase =(String)hm.get("userbase");
		String fieldsetdesc = "";
		ArrayList mullist = new ArrayList();
		if(setname==null)
			setname = "";
		try {
			if(!"".equals(setname)){
				String sql = "select fieldsetdesc from fieldSet where fieldsetid='"+setname+"'";//获取指标集编码对应的名称
				RowSet rs = dao.search(sql);
				if(rs.next()){
					fieldsetdesc=rs.getString("fieldsetdesc");
				}
			}
			mullist = this.getMulList(dao,userbase,a0100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getFormHM().put("fieldsetdesc", fieldsetdesc);
		this.getFormHM().put("a00_mul_list", mullist);
		this.getFormHM().put("setname", setname);
	}

	private ArrayList getMulList(ContentDAO dao, String userbase, String a0100) {
		StringBuffer sql = new StringBuffer();
		ArrayList Alist = new ArrayList();
		try{
			MultiMediaBo multiMediaBo = new MultiMediaBo(this.frameconn,this.userView);
			ArrayList flagList = multiMediaBo.getPowerTypeList(dao, "6", a0100);
			
			//zxj 20140419 按需查字段，不用取ole，否则记录多、ole大时，会有严重的性能问题，甚至内存崩溃
            sql.append("select A0100,I9999,Title,ext,fileid from ");
            sql.append(userbase).append("A00");
            sql.append(" WHERE A0100='").append(a0100).append("'");
            sql.append(" AND Flag in (");
            for (int i = 0; i < flagList.size(); i++) {
                if(i>0)
                    sql.append(",");
                
                sql.append("'").append(flagList.get(i)).append("'");                    
            }
            sql.append(")");
            
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				DynaBean vo=new LazyDynaBean();
				vo.set("a0100",this.getFrowset().getString("A0100"));
				vo.set("i9999",this.getFrowset().getString("I9999"));
				vo.set("title",this.getFrowset().getString("Title"));
				vo.set("ext",this.getFrowset().getString("ext"));
				vo.set("fileid",this.getFrowset().getString("fileid"));
				Alist.add(vo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return Alist;
	}

}
