package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * 试选试题信息
 * @author LiWeichao
 *
 */
public class QuestionsInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		//System.out.println(" 2020070070 提示信息：试选试题");
		String r5200 = (String)this.getFormHM().get("r5200");
		String content = "";
		try {
			String sql = "select R5205,R5207,type_id from r52 where r5200="+r5200;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				int type_id = this.frowset.getInt("type_id");
				String r5205 = this.frowset.getString("r5205");
				String r5207 = this.frowset.getString("r5207");
				content = r5205;
				content = content==null|| "null".equalsIgnoreCase(content)||content.length()<1?"":content;
				if(content.length()>0){
					if(type_id==1||type_id==2){
						content += "<br/>";
						//System.out.println(getSelectHtml(type_id,r5207));
						content += getSelectHtml(type_id,r5207);
					}else if(type_id==3){
						content += "<br/>";
						//System.out.println(getJudgeHtml(type_id,r5207));
						content += getJudgeHtml(type_id,r5207);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			this.getFormHM().put("content", SafeCode.encode(content));
		}
	}
	
	private String getSelectHtml(int type_id,String r5207){
		StringBuffer buffer = new StringBuffer();
		QuestionesBo bo = new QuestionesBo();
		String tmpString = bo.getStrSelection(r5207); 
		String type="radio";
		if(type_id==2)
			type="checkbox";
		buffer.append("<ul style=\"border: 0px;padding: 0px;margin: 0px;\">");
		String tmps[] = tmpString.split("`~&~`");
		for(int i=0;i<tmps.length;i++){
			if(tmps[i]!=null&&tmps[i].length()>0){
				String tmp[] = tmps[i].split("`:`");
			    if(tmp!=null&&tmp.length==2){
			    	buffer.append("<li><input type=\""+type+"\" value=\""+tmp[0]+"\"/>&nbsp;");
			    	buffer.append(tmp[0]+":&nbsp;"+tmp[1]+"</li>");
			    }
			}
		}
		buffer.append("</ul>");
		return buffer.toString();
	}
	
	private String getJudgeHtml(int type_id,String r5207){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<ul style=\"border: 0px;padding: 0px;margin: 0px;\">");
		buffer.append("<li><input type=\"radio\" value=\"A\"/>&nbsp;对</li>");
		buffer.append("<li><input type=\"radio\" value=\"B\"/>&nbsp;错</li>");
		buffer.append("</ul>");
		return buffer.toString();
	}
}