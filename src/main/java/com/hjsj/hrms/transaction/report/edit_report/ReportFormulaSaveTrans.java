/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportInnerFormulaOperation;
import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportSpaceFormulaOperation;
import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportSpaceGridFormulaAnalyse;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 16, 2006:9:09:43 AM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportFormulaSaveTrans extends IBusiness {

	public void execute() throws GeneralException { 
		
		String expid = (String)this.getFormHM().get("expid");
		String tabid = (String)this.getFormHM().get("tabid");
		String formulaType = (String) this.getFormHM().get("formulaType");
		String exprName = (String) this.getFormHM().get("exprName");
		String leftExpr = (String) this.getFormHM().get("leftExpr");
		String rightExpr = (String) this.getFormHM().get("rightExpr");
		
		if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		
		
		String reportPrefix= (String)this.getFormHM().get("reportType");
		String percent = (String)this.getFormHM().get("npercent");
		if(reportPrefix!=null){
			reportPrefix=PubFunc.keyWord_reback(reportPrefix);
		}
		if(rightExpr!=null){
			rightExpr=PubFunc.keyWord_reback(rightExpr);
		}
		if(leftExpr!=null){
			leftExpr=PubFunc.keyWord_reback(leftExpr);
		}
		String excludeexpr = (String)this.getFormHM().get("excludeexpr");
		if(percent==null|| "".equals(percent))
			percent="0";	
		int npercent = Integer.parseInt(percent);
		exprName = PubFunc.keyWord_reback(exprName);
		leftExpr = PubFunc.keyWord_reback(leftExpr);
		rightExpr = PubFunc.keyWord_reback(rightExpr);
	/*	
		System.out.println("reportPrefix=" + reportPrefix);
		System.out.println("expid=" + expid );
		System.out.println("tabid=" + tabid);
		System.out.println("formulaType=" + formulaType);
		System.out.println("exprName=" + exprName);
		System.out.println("leftExpr=" + leftExpr);
		System.out.println("rightExpr=" + rightExpr);
		*/
		String info = "ok";
		
		int flag = Integer.parseInt(formulaType);
		String leftExpr2 = leftExpr;
		switch(flag){
			case 0:	//表内行公式
				info = this.reportInnerRowFormulaAnalyse(this.getFrameconn(),tabid,leftExpr,rightExpr,flag+1);
				if(excludeexpr!=null&&excludeexpr.length()>0){
					leftExpr2=leftExpr+"|"+excludeexpr;
				}
				break;
			case 1://表内列公式
				info = this.reportInnerColFormulaAnalyse(this.getFrameconn(),tabid,leftExpr,rightExpr,flag+1);
				if(excludeexpr!=null&&excludeexpr.length()>0){
					leftExpr2=leftExpr+"|"+excludeexpr;
				}
				break;
			case 2://表间行公式
				info = this.reportSpaceRowFormulaAnalyse(this.getFrameconn(),tabid,leftExpr,rightExpr,1,reportPrefix);
				break;
			case 3://表间列公式
				info = this.reportSpaceRowFormulaAnalyse(this.getFrameconn(),tabid,leftExpr,rightExpr,2,reportPrefix);
				break;
			case 4://表间格公式
				info = this.reportSpaceGridFormulaAnalyse(this.getFrameconn(),tabid,leftExpr,rightExpr,reportPrefix);
				break;
			default:
				//错误	
		}
		if("ok".equals(info)){
			if(expid == null || "".equals(expid)){
				//执行DB添加操作
				StringBuffer sql = new StringBuffer();
				//insert into tformula(expid,cname,lexpr,rexpr,colrow,tabid) values(825,'','','',0,1)
				sql.append("insert into tformula(expid,cname,lexpr,rexpr,colrow,tabid) values(");
				sql.append(this.getExprID());
				sql.append(",'");
				sql.append(exprName);
				sql.append("','");
				sql.append(leftExpr2);
				sql.append("','");
				sql.append(rightExpr);
				sql.append("',");
				sql.append(flag);
				sql.append(",");
				sql.append(tabid);
				sql.append(")");
				this.insertFormula(this.getFrameconn(),sql.toString());
				updateTgrid2npercent( this.getFrameconn() , tabid, formulaType, leftExpr, npercent);
			}else{
				//执行DB修改操作
				//update tformula set cname='aa' , lexpr='1' , rexpr='1' ,  colrow=2 , tabid=1401 where expid = 74
				StringBuffer sql = new StringBuffer();
				sql.append(" update tformula set cname='");
				sql.append(exprName);
				sql.append("' , lexpr = '");
				sql.append(leftExpr2);
				sql.append("' , rexpr = '");
				sql.append(rightExpr);
				sql.append("' , colrow =");
				sql.append(flag);
				sql.append(" ,tabid = ");
				sql.append(tabid);
				sql.append(" where expid=");
				sql.append(expid);
				
				//System.out.println(sql.toString());
				
				this.updateFormula(this.getFrameconn(),sql.toString());
				updateTgrid2npercent( this.getFrameconn() , tabid, formulaType, leftExpr, npercent);
			}
		
		}
		//System.out.println("info=" + info);
		this.getFormHM().put("info",info);
		
	}
	public void updateTgrid2npercent(Connection conn ,String tabid,String formulaType,String leftExpr,int npercent){
		TnameBo tbo = new TnameBo(conn,tabid);
		if("1".equals(formulaType)){
		int n = Integer.parseInt((String)tbo.getColMap().get(leftExpr));
		ArrayList list =tbo.getRowInfoBGrid();
		if(list.size()>=n+1){
			RecordVo vo=(RecordVo)list.get(n);
			vo.setInt("npercent",npercent);
			vo.setInt("tabid",Integer.parseInt(tabid));
			ContentDAO dao=new ContentDAO(conn);
			try{	
				dao.updateValueObject(vo);
			}catch(Exception e){
			   e.printStackTrace();
			  
			}	
		}
		}
		else if("0".equals(formulaType)){
			int n = Integer.parseInt((String)tbo.getRowMap().get(leftExpr));
			ArrayList list =tbo.getColInfoBGrid();
			if(list.size()>=n+1){
				RecordVo vo=(RecordVo)list.get(n);
				vo.setInt("npercent",npercent);
				vo.setInt("tabid",Integer.parseInt(tabid));
				ContentDAO dao=new ContentDAO(conn);
				try{	
					dao.updateValueObject(vo);
				}catch(Exception e){
				   e.printStackTrace();
				  
				}	
			}
			}
		
	}
	/**
	 * 表内行公式效验
	 * @param conn   DB连接
	 * @param tabid  报表ID  
	 * @param lExpr  左表达式
	 * @param rExpr  右表达式
	 * @param flag   行列标识
	 * @return
	 */
	public String reportInnerRowFormulaAnalyse(Connection conn ,String tabid,String lExpr ,String rExpr , int flag ) throws GeneralException{	
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		TnameBo tbo = new TnameBo(conn,tabid);
		ReportInnerFormulaOperation rifo = new ReportInnerFormulaOperation(tbo,le,re,flag);
		rifo.setUserView(this.userView);
		rifo.setConn(this.getFrameconn());
		String temp = rifo.reportInnerFormulaCheck();	
		return temp;
	}
	
	
	/**
	 * 表内列公式效验
	 * @param conn   DB连接
	 * @param tabid  报表ID  
	 * @param lExpr  左表达式
	 * @param rExpr  右表达式
	 * @param flag   行列标识
	 * @return
	 */	
	public String reportInnerColFormulaAnalyse(Connection conn ,String tabid,String lExpr ,String rExpr , int flag ) throws GeneralException{
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		//System.out.println("le=" + le + "        re=" + re);
		//System.out.println("tabid=" + tabid);
		TnameBo tbo = new TnameBo(conn,tabid);
		//System.out.println("进入....................");
		
		ReportInnerFormulaOperation rifo = new ReportInnerFormulaOperation(tbo,le,re,flag);
		rifo.setUserView(this.userView);
		rifo.setConn(this.getFrameconn());
		String temp = rifo.reportInnerFormulaCheck();	
		return temp;
	}
	
	/**
	 * 表间行公式分析
	 * @param conn    DB连接
	 * @param tabid   报表ID
	 * @param lExpr   左表达式
	 * @param rExpr   右表达式
	 * @param flag    行列标识
	 * @param reportPrefix tb或tt_
	 * @return
	 * @throws GeneralException
	 */
	public String reportSpaceRowFormulaAnalyse(Connection conn ,String tabid ,String lExpr ,String rExpr, int flag ,String reportPrefix) throws GeneralException{
		/*
		 * 第60表表间行计算公式语法分析：
		 * 	1:ALL = [62:1..3]  ALL代表所有列，ALL不区分大小写，第60表的第1行的所有列 = 第62表的第1行对应列的值+第62表的第1行对应列的值+第62表的第1行对应列的值
		 *  1:1,2,3 = [62:1] 第60表的第1行的1，2，3列 = 第62表的第1行的对应列的值
		 *  1:2 = (C100 + [62:1])+C200 第60表的第1行的2列 = （100 + 第62表的第1行第2列 ） + 200
		 *  
		 *  左表达式：1:ALL 或 1:2,3,4
		 */

		//规范的表达式
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		//System.out.println("tabid=" + tabid);
		int tid = Integer.parseInt(tabid);
		
		ReportSpaceFormulaOperation rsfo = new ReportSpaceFormulaOperation(conn,tid,le,re,flag,reportPrefix,"");
		rsfo.setUserView(this.userView);
		String temp = rsfo.reportSpaceFormulaCheck();
		return temp;
		
	}
	
	
	/**
	 * 表间列公式分析
	 * @param conn    DB连接
	 * @param tabid   报表ID
	 * @param lExpr   左表达式
	 * @param rExpr   右表达式
	 * @param flag    行列标识
	 * @param reportPrefix tb或tt_
	 * @return
	 * @throws GeneralException
	 */
	public String reportSpaceColFormulaAnalyse(Connection conn ,String tabid ,String lExpr ,String rExpr ,int flag ,String reportPrefix) throws GeneralException{
		
		//规范的表达式
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		int tid = Integer.parseInt(tabid);
		
		ReportSpaceFormulaOperation rsfo = new ReportSpaceFormulaOperation(conn,tid,le,re,flag,reportPrefix,"");
		rsfo.setUserView(this.userView);
		String temp = rsfo.reportSpaceFormulaCheck();
		return temp;
	}
	
	
	/**
	 * 表间格公式分析
	 * @param conn    DB连接
	 * @param tabid   报表ID
	 * @param lExpr   左表达式
	 * @param rExpr   右表达式
	 * @param flag    行列标识
	 * @param reportPrefix tb或tt_
	 * @return
	 * @throws GeneralException
	 */
	public String reportSpaceGridFormulaAnalyse(Connection conn , String tabid ,String lExpr ,String rExpr ,String reportPrefix) throws GeneralException{
		/*
		 * 1:2 = [11:2:3]+c100
		 * 1行2列=11表2行3列+100
		 */
		
		//规范的表达式
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		int tid = Integer.parseInt(tabid);
		
		ReportSpaceGridFormulaAnalyse rsgfa = new ReportSpaceGridFormulaAnalyse(conn,tid,le,re ,reportPrefix,"");
		rsgfa.setUserView(this.userView);
		String result =rsgfa.reportSpaceGridFormulaCheck();
		
		return result;
	}
	
	
	
	/**
	 * 增加计算公式
	 * @param conn
	 * @param sql
	 * @throws GeneralException
	 */
	public void insertFormula(Connection conn , String sql) throws GeneralException{
		ContentDAO dao=new ContentDAO(conn);
		try{	
			dao.insert(sql ,new ArrayList());
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		
	}
	
	
	/**
	 * 修改计算公式
	 * @param conn
	 * @param sql
	 * @throws GeneralException
	 */
	public void updateFormula(Connection conn , String sql) throws GeneralException{
		ContentDAO dao=new ContentDAO(conn);
		try{	
			dao.update(sql);
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		
	}
	
	
	/**
	 * 获得公式号
	 */
	public synchronized int getExprID() throws GeneralException{
		int num = 0;  //序号默认为0
		String sql="select max(expid) as num  from tformula";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				num = this.frowset.getInt("num");
			}
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}	
		return num+1;		
	}

}
