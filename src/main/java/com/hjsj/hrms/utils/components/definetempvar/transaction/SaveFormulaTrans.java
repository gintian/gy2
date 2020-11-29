package com.hjsj.hrms.utils.components.definetempvar.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.definetempvar.businessobject.DefineTempVarBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 项目名称 ：ehr7.x
 * 类名称：SaveFormulaTrans
 * 类描述：保存临时变量公司表达式
 * 创建人： lis
 * 创建时间：2015-10-31
 */
public class SaveFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		try
		{
			String formula = (String)hm.get("c_expr");//公式表达式
			String salaryid = (String)hm.get("salaryid");//薪资类别id
			salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
			
			formula=formula!=null&&formula.trim().length()>0?formula:"";
			formula=SafeCode.decode(formula);
			formula=PubFunc.keyWord_reback(formula);
			
			String nid = (String)hm.get("nid");//当前临时变量id
			nid=nid!=null&&nid.trim().length()>0?nid:"0";
			
			String type = (String)hm.get("type");//1是薪资，3是人事异动
			type=type!=null&&type.trim().length()>0?type:"";
			
			RecordVo recordvo=new RecordVo("midvariable");
			recordvo.setInt("nid",Integer.parseInt(nid));
			recordvo=dao.findByPrimaryKey(recordvo);
			String _formula = recordvo.getString("cvalue");
			_formula = _formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");//库里面查询出来的回车符可能是\n也可能是\r\n 此处统一把二者转成<br>  防止ajax报错以及equals比对错误 zhaoxg update
			String _formula1 = formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
			String formulaSave = ResourceFactory.getProperty("org.maip.formula.preservation");//公式保存
			if(_formula!=null&&!_formula.equals(_formula1)){
				String editTempVar = ResourceFactory.getProperty("gz_new.gz_editTempVarStr");//修改临时变量表达式
				StringBuffer context = new StringBuffer();
				DefineTempVarBo bo = new DefineTempVarBo(this.frameconn,this.userView);
				String name="";
				if("1".equals(type))//获得薪资类别名称
					name = bo.getSalaryName(salaryid);
				if("3".equals(type))//获得人事异动模版名称
					name = bo.getTempName(salaryid);
				context.append(formulaSave+"："+name+"（"+salaryid+"）"+editTempVar+"<br>");
				context.append("<table>");
				context.append("<tr>");
				context.append("<td>"+ResourceFactory.getProperty("gz_new.gz_propertyName")+"</td>");
				context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.before")+"</td>");
				context.append("<td>"+ResourceFactory.getProperty("gz.gz_acounting.change.affter")+"</td>");
				context.append("</tr>");
				context.append("<tr>");
				context.append("<td>"+recordvo.getString("chz")+"</td>");
				context.append("<td>"+_formula.replaceAll("\"", "＂")+"</td>");
				context.append("<td>"+_formula1.replaceAll("\"", "＂")+"</td>");
				context.append("</tr>");
				context.append("</table>");
				this.getFormHM().put("@eventlog", context.toString());

			}else{
				this.getFormHM().put("@eventlog", formulaSave+"："+ResourceFactory.getProperty("gz_new.gz_editTempVar")+"（"+recordvo.getString("chz")+"）"+ResourceFactory.getProperty("gz_new.gz_noContent"));
			}
			recordvo.setString("cvalue", formula);
			dao.updateValueObject(recordvo);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			GeneralExceptionHandler.Handle(ee);
		}
	}

}
