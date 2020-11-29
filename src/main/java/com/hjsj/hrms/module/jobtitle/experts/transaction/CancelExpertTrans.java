package com.hjsj.hrms.module.jobtitle.experts.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
* <p>Title:CancelExpertTrans </p>
* <p>Description: 撤销专家</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Nov 25, 2015 9:18:37 AM
 */
public class CancelExpertTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		String idlist  = (String)this.getFormHM().get("idlist");//专家编号
		idlist = idlist.substring(1,idlist.length()-1);
		idlist = idlist.replaceAll("\"", "");
		String [] idarray = idlist.split(",");
		ArrayList nodellist = new ArrayList();//不可删除的专家姓名数组
		ArrayList nodelidlist = new ArrayList();//不可删除的专家编号数组
		ArrayList delidlist = new ArrayList();//可删除的专家编号数组
		RecordVo vo = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rst = null;
		RowSet rst1 = null;
		try {
			for(int i=0;i<idarray.length;i++){
				String w0101 = idarray[i];//专家编号
				String committeesql = "select * from zc_judgingpanel_experts where w0101='"+w0101+"'";
				String subjectsql = "select * from zc_subjectgroup_experts where expertid='"+w0101+"'";
				rst = dao.search(committeesql);
				rst1 = dao.search(subjectsql);
				vo = new RecordVo("W01");
	            vo.setString("w0101", w0101);
	            String w0107 ="";
	            if(dao.isExistRecordVo(vo)){
	            	if(vo!=null){
	            		w0107 = vo.getString("w0107");
	            	}
	            }
				if(rst.next()){//评委会专家表有此专家
					nodellist.add(w0107);
					nodelidlist.add(w0101);
				}
				else if(rst1.next()){//科学组成员表有此专家
					nodellist.add(w0107);
					nodelidlist.add(w0101);
				}
				else{
					delidlist.add(w0101);
				}
			}
			if(nodelidlist.size()>0){//有不可删的专家
				String nodelStr = "";
				//取消在撤销专家时自动设置聘任标识  haosl 20160831
//				ArrayList<RecordVo> volist = new ArrayList<RecordVo>();
//				for(int k=0;k<nodelidlist.size();k++){
//					RecordVo resultVo = new RecordVo("W01");
//					resultVo.setString("w0101", String.valueOf(nodelidlist.get(k)));
//					resultVo.setString("w0109", "2");//设置不可聘任
//					volist.add(resultVo);
//				}
//				dao.updateValueObject(volist);
				if(nodellist.size()<=2&&nodellist.size()>0){
					for(int j=0;j<nodellist.size();j++){
						nodelStr += "\""+nodellist.get(j)+"\""+"、";
					}
					nodelStr = nodelStr.substring(0,nodelStr.length()-1);
				}else{
					for(int j=0;j<2;j++){
						nodelStr += "\""+nodellist.get(j)+"\""+"、";
					}
					nodelStr = nodelStr.substring(0,nodelStr.length()-1);
					nodelStr = nodelStr+"等"+nodellist.size()+"名";
				}
				this.getFormHM().put("flag", "1");
				this.getFormHM().put("nodelStr", nodelStr);
			}else{//可以删除
				ArrayList<RecordVo> volist = new ArrayList<RecordVo>();
				for(int m=0;m<delidlist.size();m++){
					RecordVo resultVo = new RecordVo("W01");
					resultVo.setString("w0101", String.valueOf(delidlist.get(m)));
					volist.add(resultVo);
				}
				dao.deleteValueObject(volist);
				this.getFormHM().put("flag", "0");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
