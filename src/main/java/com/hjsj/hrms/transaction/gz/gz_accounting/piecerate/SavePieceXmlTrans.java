package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SavePieceXmlTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String tabid = SafeCode.decode((String)this.getFormHM().get("tabid"));//预算表分类
			tabid = PubFunc.keyWord_reback(tabid);
			String codeitemid = SafeCode.decode((String)this.getFormHM().get("codeitemid"));//人员类别代码类
			codeitemid = PubFunc.keyWord_reback(codeitemid);
			ConstantXml xml = new ConstantXml(this.frameconn, "PIECE_PAY", "param"); 
			String[] id1 = tabid.split("/");
			String[] id2 = codeitemid.split("/");
			int t = 0;
			String tabid1 = "";
			String tabid2 = "";
			String codeitemid1 = ""; 
			  
			ArrayList list=new ArrayList();
			for(int i=1;i<=id2.length;i++){
				tabid1=id1[2*i-1];
				tabid2=id1[t];
				codeitemid1=id2[i-1]; 
				LazyDynaBean nodeBean=new LazyDynaBean();
				nodeBean.set("name","item");
				nodeBean.set("content", "");
				HashMap attr_map=new HashMap(); 
				attr_map.put("code",codeitemid1);
				attr_map.put("signtable",tabid1);
				attr_map.put("jobtable",tabid2); 
				nodeBean.set("attributes",attr_map); 
				list.add(nodeBean);
				t=2*i;
			}
			xml.addElement("/param/items",list); 
			xml.saveStrValue();
 
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
