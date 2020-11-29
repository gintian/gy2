package com.hjsj.hrms.transaction.gz.tempvar;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SaveFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		try
		{
			String formula = (String)hm.get("c_expr");
			String salaryid = (String)hm.get("tabid");
			formula=formula!=null&&formula.trim().length()>0?formula:"";
			formula=SafeCode.decode(formula);
			formula=PubFunc.keyWord_reback(formula);
			String nid = (String)hm.get("nid");
			nid=nid!=null&&nid.trim().length()>0?nid:"0";
			
			RecordVo recordvo=new RecordVo("midvariable");
			recordvo.setInt("nid",Integer.parseInt(nid));
			recordvo=dao.findByPrimaryKey(recordvo);
			String _formula = recordvo.getString("cvalue");
			_formula = _formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");//库里面查询出来的回车符可能是\n也可能是\r\n 此处统一把二者转成<br>  防止ajax报错以及equals比对错误 zhaoxg update
			String _formula1 = formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
			if(_formula!=null&&!_formula.equals(_formula1)){
				StringBuffer context = new StringBuffer();
				SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
				String name = bo.getSalaryName(salaryid);
				context.append("公式保存："+name+"（"+salaryid+"）修改临时变量表达式<br>");
				context.append("<table>");
				context.append("<tr>");
				context.append("<td>属性名</td>");
				context.append("<td>变化前</td>");
				context.append("<td>变化后</td>");
				context.append("</tr>");
				context.append("<tr>");
				context.append("<td>"+recordvo.getString("chz")+"</td>");
				context.append("<td>"+_formula.replaceAll("\"", "＂")+"</td>");
				context.append("<td>"+_formula1.replaceAll("\"", "＂")+"</td>");
				context.append("</tr>");
				context.append("</table>");
				this.getFormHM().put("@eventlog", context.toString());

			}else{
				this.getFormHM().put("@eventlog", "公式保存：修改临时变量（"+recordvo.getString("chz")+"）表达式,没有修改具体内容");
			}
			recordvo.setString("cvalue", formula);
			dao.updateValueObject(recordvo);
			/*
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update midvariable set cvalue='");
			sqlstr.append(formula+"' where nid='");
			sqlstr.append(nid+"'");
			
			try {
				dao.update(sqlstr.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}

}
