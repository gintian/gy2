package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 试卷暂停/发布 LiWeichao 2011-10-25 17:08:50
 */
public class PubExamPaperTrans extends IBusiness {


	public void execute() throws GeneralException {
		String sels = (String) this.getFormHM().get("sels");
		String state = (String) this.getFormHM().get("state");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList sellist = new ArrayList();
		String id = "";
		int n = 0;
		try {
			if(sels!=null&&sels.length()>0){
			    String[] sel = sels.split(",");
			    for(int i = 0; i<sel.length; i++){
			        if(n > 0)
			            id += ",";
			        id += PubFunc.decrypt(SafeCode.decode(sel[i]));
			        n++;
			        
			        if(n == 1000){
			            sellist.add(id);
			            id = "";
			            n = 0;
			        }
			    }
			    
			    if(id != null && id.length() > 0)
			        sellist.add(id);
			}
			
			if(checkIsDel(dao,sellist,state)){
			    ArrayList sqllist = new ArrayList();
			    for (int i = 0; i < sellist.size(); i++) {
	                String ids = (String) sellist.get(i);
	                String sql = "update r53 set r5311='"+state+"' where r5300 in("+ids+")";
	                sqllist.add(sql);
			    }
				dao.batchUpdate(sqllist);
				this.getFormHM().put("flag", "ok");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkIsDel(ContentDAO dao,ArrayList sellist, String state) throws GeneralException{
		boolean tmpFlag = true;
		StringBuffer tmpstring = new StringBuffer();
		
		try {
            for (int i = 0; i < sellist.size(); i++) {
                String sels = (String) sellist.get(i);
                String sql = "select r5300,r5301,r5311 from r53 where r5300 in (" + sels + ")";
                this.frowset = dao.search(sql);
                while (this.frowset.next()) {
                    String r5311 = this.frowset.getString("r5311");
                    String r5301 = this.frowset.getString("r5301");
                    if ("09".equals(state)) {
                        if ("01".equals(r5311)) {
                            tmpstring.append("\n[" + r5301 + "]");
                            tmpstring.append("为起草状态，不能暂停，只能暂停已发布的记录!\n");
                        }
                        
                        if ("09".equals(r5311)) {
                            tmpstring.append("\n[" + r5301 + "]");
                            tmpstring.append("为暂停状态，不能暂停，只能暂停已发布的记录!\n");
                        }
                    }
                    
                    if ("04".equals(state)) {
                        if ("04".equals(r5311)) {
                            tmpstring.append("\n[" + r5301 + "]");
                            tmpstring.append("为已发布状态，不能发布，只能发布起草或暂停的记录!\n");
                        } else {
                            String verify = verify(this.frowset.getString("r5300"), r5301);
                            if (verify != null && verify.length() > 0)
                                tmpstring.append(verify);
                        }
                    }
                }
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(tmpstring.length()>0){
			this.getFormHM().put("mess", SafeCode.encode(tmpstring.toString()));
			this.getFormHM().put("flag", "error");
			tmpFlag=false;
		}
		return tmpFlag;
	}
	
	private String verify(String r5300,String r5301){
		String flag="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql="select sum(r5213) s from tr_exam_paper t,r52 r where r.r5200=t.r5200 and r5300="+r5300;
		float sum = 0f;
		try {
			this.frecset = dao.search(sql);
			if(this.frecset.next())
				sum = this.frecset.getFloat("s");
			
			sql="select r5304 from r53 where r5300="+r5300;
			this.frecset = dao.search(sql);
			if(this.frecset.next()){
				float f = this.frecset.getFloat("r5304");
				if(f!=sum){
					flag="\n试卷["+r5301+"]满分为："+f+",当前试题总分为："+sum+"。请校验该试卷!\n";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
}

