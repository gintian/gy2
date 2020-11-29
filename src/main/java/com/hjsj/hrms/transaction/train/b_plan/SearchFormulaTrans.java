package com.hjsj.hrms.transaction.train.b_plan;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SearchFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		String id = (String)this.getFormHM().get("id");
		id=id!=null&&id.trim().length()>0?id:"";
		
		tableStr(id);
	}
	private void tableStr(String id){
		StringBuffer buf = new StringBuffer();
		
		if(id!=null&&id.length()>0){
			ContentDAO dao = new ContentDAO(this.frameconn);
			RecordVo vo = new RecordVo("LExpr");
			vo.setInt("id",Integer.parseInt(id));
			try {
				vo = dao.findByPrimaryKey(vo);
				String factor = vo.getString("factor");
				String lexpr = vo.getString("lexpr");
				buf.append(lexpr);
				buf.append("::"+factor);
				buf.append("::"+vo.getString("fuzzyflag"));
			} catch (GeneralException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.getFormHM().put("searchstr",SafeCode.decode(buf.toString()));
		}else{
			this.getFormHM().put("searchstr","");
		}
		
	}
	private String factorStr(String factor){
		StringBuffer buf = new StringBuffer();
		String arr[] = factor.split("`");
		if(arr.length>0){
			for(int i=0;i<arr.length;i++){
				String eq = "";
				if(arr[i].indexOf("<>")!=-1){
					eq = "<>";
				}else if(arr[i].indexOf("<=")!=-1){
					eq = "<=";
				}else if(arr[i].indexOf(">=")!=-1){
					eq = ">=";
				}else if(arr[i].indexOf("=")!=-1){
					eq = "=";
				}else if(arr[i].indexOf("<")!=-1){
					eq = "<";
				}else if(arr[i].indexOf("<")!=-1){
					eq = ">";
				}
				String fa = arr[i].replaceAll("<>",":").replaceAll(">=",":").replaceAll("<=",":").replaceAll("=",":");
				fa=fa.replaceAll("<",":").replaceAll(">",":");
				String itemarr[] = fa.split(":");
				if(itemarr.length>0){
					String itemid = itemarr[0];
					if(itemid.length()>1){
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null){
							String desc = itemarr.length==2?itemarr[1]:"";
							if(fielditem.isCode()){
								String code = "";
								if(desc.indexOf("*")==-1)
									code=desc+","+AdminCode.getCodeName(fielditem.getCodesetid(),desc);
								else
									code=desc+","+desc;
								buf.append(itemid+":"+fielditem.getItemdesc()+":");
								buf.append(fielditem.getCodesetid()+":"+fielditem.getItemtype()+":"+eq+":"+code);
								buf.append(":"+fielditem.getFieldsetid()+"`");
							}else{
								buf.append(itemid+":"+fielditem.getItemdesc()+":");
								buf.append(fielditem.getCodesetid()+":"+fielditem.getItemtype()+":"+eq+":"+desc);
								buf.append(":"+fielditem.getFieldsetid()+"`");
							}
						}
					}
				}
			}
		}
		return buf.toString();
	}
}
