package com.hjsj.hrms.transaction.kq.options.formula;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ConfigFormulaByAjaxTrans extends IBusiness {
	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");
			String chkid=(String)this.getFormHM().get("chkid");
			String salaryid=(String)this.getFormHM().get("salaryid");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			/**修改有效性*/
			if("1".equals(opt))
			{
				String validflag=(String)this.getFormHM().get("validflag");
				String sql="update hrpchkformula set validflag='"+validflag+"' where chkid="+chkid;
			    dao.update(sql);
			} 
			/**delete formula*/
			else if("2".equals(opt))
			{
				RecordVo vo=new RecordVo("hrpchkformula");
				vo.setInt("chkid",Integer.parseInt(chkid));
				vo = dao.findByPrimaryKey(vo);	
				String _formula = vo.getString("name");
				_formula = _formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");//库里面查询出来的回车符可能是\n也可能是\r\n 此处统一把二者转成<br>  防止ajax报错以及equals比对错误 zhaoxg update
				StringBuffer context = new StringBuffer();
				SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
				String name = bo.getSalaryName(salaryid);
				context.append("删除："+name+"（"+salaryid+"）删除审核公式（"+_formula+"）<br>");
				this.getFormHM().put("@eventlog", context.toString());		
				
				String sql = "delete from hrpchkformula where chkid="+chkid;
				dao.delete(sql, new ArrayList());
			}
			else if("3".equals(opt))
			{
				String formula="";
				String sql = "select formula from hrpchkformula where chkid="+chkid;
				this.frowset=dao.search(sql);
				while(this.frowset.next())
				{
					formula=SafeCode.encode(Sql_switcher.readMemo(this.frowset,"formula"));
				}
				this.getFormHM().put("formula", formula);
			}
			else if("4".equals(opt))
			{
				String formula=(String)this.getFormHM().get("formula");
				formula = PubFunc.keyWord_reback(SafeCode.decode(formula));
				RecordVo vo=new RecordVo("hrpchkformula");
				vo.setInt("chkid",Integer.parseInt(chkid));
				vo = dao.findByPrimaryKey(vo);	
				String _formula = vo.getString("formula");
				_formula = _formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");//库里面查询出来的回车符可能是\n也可能是\r\n 此处统一把二者转成<br>  防止ajax报错以及equals比对错误 zhaoxg update
				String _formula1 = formula.replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
				if(_formula!=null&&!_formula.equals(_formula1)){
					StringBuffer context = new StringBuffer();
					SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
					String name = bo.getSalaryName(salaryid);
					context.append("公式保存："+name+"("+salaryid+")修改审核公式表达式<br>");
					context.append("<table>");
					context.append("<tr>");
					context.append("<td>属性名</td>");
					context.append("<td>变化前</td>");
					context.append("<td>变化后</td>");
					context.append("</tr>");
					context.append("<tr>");
					context.append("<td>"+vo.getString("name")+"</td>");
					context.append("<td>"+_formula.replaceAll("\"", "＂")+"</td>");
					context.append("<td>"+_formula1.replaceAll("\"", "＂")+"</td>");
					context.append("</tr>");
					context.append("</table>");
					this.getFormHM().put("@eventlog", context.toString());	
				}else{
					SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
					String name = bo.getSalaryName(salaryid);
					this.getFormHM().put("@eventlog", "修改："+name+"("+salaryid+")修改临审核公式("+vo.getString("name")+")表达式,但没有修改具体内容");	
				}
				
				String sql = "update hrpchkformula set formula=? where chkid="+chkid;				
				ArrayList list = new ArrayList();
				list.add(PubFunc.keyWord_reback(SafeCode.decode(formula)));
				dao.update(sql,list);
			}
			this.getFormHM().put("opt", opt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}

