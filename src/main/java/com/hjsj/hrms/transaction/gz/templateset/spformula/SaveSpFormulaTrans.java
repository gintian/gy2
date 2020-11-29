package com.hjsj.hrms.transaction.gz.templateset.spformula;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveSpFormulaTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			String salaryid=(String)this.getFormHM().get("salaryid");
			String optType=(String)this.getFormHM().get("optType");
			String spFormulaId=(String)this.getFormHM().get("spFormulaId");
			String spFormulaName=(String)this.getFormHM().get("spFormulaName");
			String spAlert=(String)this.getFormHM().get("spAlert");
			String gz_module=(String)this.getFormHM().get("gz_module");
			StringBuffer sql = new StringBuffer("");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("1".equals(optType))
			{
				int seq=this.getSeq()+1;
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				spFormulaId = idg.getId("hrpchkformula.chkid");
				
				sql.append("insert into hrpchkformula (chkid,name,information,seq,flag,tabid,validflag) values (?,?,?,?,?,?,?)");
				ArrayList list = new ArrayList();
				list.add(spFormulaId);
				list.add(spFormulaName);
				list.add(spAlert);
				list.add(seq+"");
				if(gz_module!=null && "3".equals(gz_module)){
					list.add(gz_module);
				}else{
					list.add("1");
				}
				list.add(salaryid);
				list.add("1");
				dao.insert(sql.toString(), list);
				
				StringBuffer context = new StringBuffer();
				SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
				String name = bo.getSalaryName(salaryid);
				context.append("新增："+name+"（"+salaryid+"）新增审核公式（"+spFormulaName+"）<br>");

				this.getFormHM().put("@eventlog", context.toString());
			}
			else
			{
				RecordVo vo=new RecordVo("hrpchkformula");
				vo.setInt("chkid",Integer.parseInt(spFormulaId));
				vo = dao.findByPrimaryKey(vo);	
				String _formula = vo.getString("name");
				if(_formula!=null&&!_formula.equals(spFormulaName)){
					StringBuffer context = new StringBuffer();
					SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
					String name = bo.getSalaryName(salaryid);
					context.append("编辑："+name+"（"+salaryid+"）修改审核公式名称（"+_formula+"--->"+spFormulaName+"）<br>");

					this.getFormHM().put("@eventlog", context.toString());					
				}else{
					SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
					String name = bo.getSalaryName(salaryid);
					this.getFormHM().put("@eventlog", "编辑："+name+"（"+salaryid+"）修改审核公式名称,但没修改具体内容");	
				}

				sql.append(" update hrpchkformula set name=?,information=? where chkid="+spFormulaId);
				ArrayList list = new ArrayList();
				list.add(spFormulaName);
				list.add(spAlert);
				dao.update(sql.toString(), list);
			}
			int gg=Integer.parseInt(spFormulaId);
			this.getFormHM().put("spFormulaId",gg+"");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public int getSeq()
	{
		int seq = 0;
		try
		{
			String sql = "select max(seq) seq from hrpchkformula ";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				seq=this.frowset.getInt("seq");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return seq;
	}

}
