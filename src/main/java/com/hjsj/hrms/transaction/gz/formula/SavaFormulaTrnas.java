package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SavaFormulaTrnas extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		String item = (String)hm.get("item");
		item=item!=null&&item.trim().length()>0?item:"";

		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		
		String formula = (String)hm.get("formula");
		formula=formula!=null&&formula.trim().length()>0?formula:"";
		String cstate = (String) hm.get("cstate");
		cstate=cstate!=null&&cstate.trim().length()>0?cstate:"";
		formula=SafeCode.decode(formula);
		formula=PubFunc.keyWord_reback(formula);
		RecordVo vo=new RecordVo("salaryformula");
		vo.setInt("salaryid",Integer.parseInt(salaryid));
		vo.setInt("itemid",Integer.parseInt(item));
		
	//	String sqlstr = "update salaryformula set rexpr='"+formula+"' where salaryid="+salaryid+" and itemid="+item;
		try {
			vo = dao.findByPrimaryKey(vo);	
			String _formula = vo.getString("rexpr");
			_formula = _formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");//库里面查询出来的回车符可能是\n也可能是\r\n 此处统一把二者转成<br>  防止ajax报错以及equals比对错误 zhaoxg update
			String _formula1 = formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
			if(_formula!=null&&!_formula.equals(_formula1)){
				StringBuffer context = new StringBuffer();
				SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
				String name = bo.getSalaryName(salaryid);
				context.append("公式保存："+name+"（"+salaryid+"）修改计算公式表达式<br>");
				context.append("<table>");
				context.append("<tr>");
				context.append("<td>属性名</td>");
				context.append("<td>变化前</td>");
				context.append("<td>变化后</td>");
				context.append("</tr>");
				context.append("<tr>");
				context.append("<td>"+vo.getString("hzname")+"</td>");
				context.append("<td>"+_formula.replaceAll("\"", "＂")+"</td>");
				context.append("<td>"+_formula1.replaceAll("\"", "＂")+"</td>");
				context.append("</tr>");
				context.append("</table>");

				this.getFormHM().put("@eventlog", context.toString());
			}else{
				SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
				String name = bo.getSalaryName(salaryid);
				this.getFormHM().put("@eventlog", "公式保存："+name+"（"+salaryid+"）修改计算公式表达式，但没有修改具体内容");
			}
			vo.setString("cstate", cstate);
			vo.setString("rexpr", formula);
			dao.updateValueObject(vo);
	//		dao.update(sqlstr);
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hm.put("info","ok");
	}

}
