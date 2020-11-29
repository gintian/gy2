package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.AutoGroupPaperBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SaveKnowLedgeDiffTrans extends IBusiness {

	public void execute() throws GeneralException {
		String ids = (String)this.getFormHM().get("ids");
		ids=ids==null?"":ids;
		String values = (String)this.getFormHM().get("values");
		values=values==null?"":values;
		String r5300 = (String)this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String flag="ok";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		AutoGroupPaperBo bo = null;
		try {
			String sql="delete tr_knowledge_diff where r5300="+r5300;
			dao.delete(sql, new ArrayList());//先删才该试卷下知识点比例(每次相应的知识点不一定一样，避免留下多余比例)
			
			String id[] = ids.split("`");
			String value[] = values.split("`");
			if(id.length==value.length){
				for (int i = 0; i < id.length; i++) {
					String tmpstr[] = id[i].split("_");
					if(tmpstr.length==3){
						this.frowset = dao.search("select 1 from tr_knowledge_diff where type_id="+tmpstr[0]+" and know_id='"+tmpstr[1]+"' and r5300="+r5300);
						if(this.frowset.next()){ //  this.frowset.next()报错
							sql="update tr_knowledge_diff set "+getColumn(tmpstr[2])+"="+value[i]+" where know_id='"+tmpstr[1]+"' and type_id="+tmpstr[0]+" and r5300="+r5300;
						}else{
							sql="insert into tr_knowledge_diff(r5300,know_id,type_id,"+getColumn(tmpstr[2])+") values ("+r5300+",'"+tmpstr[1]+"',"+tmpstr[0]+","+value[i]+")";
						}//sqls.add(sql);
						dao.update(sql);
					}
				}
			}
			
		} catch (SQLException e) {
			flag="error";
			e.printStackTrace();
		}
		this.getFormHM().put("flag", flag);
	}
	
	private String getColumn(String c){
		int tmp = Integer.parseInt(c);
		String column="diff"+(tmp+3);
//		switch(tmp){
//			case -2:
//				column="diff1";
//				break;
//			case -1:
//				column="diff2";
//				break;
//			case 0:
//				column="diff3";
//				break;
//			case 1:
//				column="diff4";
//				break;
//			case 2:
//				column="diff5";
//				break;
//			case 3:
//				column="diff6";
//				break;
//			case 4:
//				column="diff7";
//				break;
//			case 5:
//				column="diff8";
//				break;
//		}
		return column;
	}
}
