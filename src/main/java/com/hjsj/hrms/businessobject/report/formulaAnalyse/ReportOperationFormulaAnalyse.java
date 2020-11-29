package com.hjsj.hrms.businessobject.report.formulaAnalyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ExprUtil;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/*
vo.setInt("expid",Integer.parseInt(recorder[0]));	
vo.setInt("tabid",Integer.parseInt(recorder[5]));
vo.setString("cname",recorder[1]);
vo.setString("lexpr",recorder[2]);
vo.setString("rexpr",recorder[3]);
vo.setInt("colrow",Integer.parseInt(recorder[4]));
 
=3,表间列公式
=2,表间行公式
=0,表内行公式
=1,表内列公式
=5,汇总公式
=4,表间格公式
*/

/**
 * <p>Title:计算公式分析</p>
 * <p>Description:表内/表间/汇总</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportOperationFormulaAnalyse {
	
	private Connection conn; //DB连接
	private String tabid;	 //报表表号
	private List formulaList;//公式列表
	private TnameBo tbo ;    //报表类
	private String unitCode ; //汇总公式使用
	
	private ArrayList unitCodeList = new ArrayList(); //汇总公式使用
	
	//tb或tt_表操作
	private String reportPrefix; //操作报表的前缀
	private String whereSQL;     //操作报表的限制条件
	private UserView userView; 
	private String operate;
	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	/**
	 * 构造器 
	 * @param conn DB连接
	 * @param tabid  表号
	 * @param formulaList 公式列表-集合形式出现 公式封装到RecordVo类中
	 * @param reportFlag  报表标识1 tb  2 tt_
	 * @param sqlFlag     针对报表标识传入 用户名或填报单位编码
	 */
	public ReportOperationFormulaAnalyse(Connection conn ,String tabid , List formulaList  ,int reportFlag , String sqlFlag){
		this.conn = conn;
		this.tabid = tabid;
		this.formulaList = formulaList;
		if(reportFlag == 1){
			this.reportPrefix = "tb";
			this.whereSQL = " and username = '" + sqlFlag + "' ";
		}else if(reportFlag == 2){
			this.reportPrefix = "tt_";
			this.whereSQL = " and unitcode = '" + sqlFlag + "' ";
		}
	}
	
	
	
	/**
	 * 构造器 
	 * @param conn DB连接
	 * @param tabid  表号
	 * @param formulaList 公式列表-集合形式出现 公式封装到RecordVo类中
	 * @param reportFlag  报表标识1 tb  2 tt_
	 * @param sqlFlag     针对报表标识传入 用户名或填报单位编码
	 */
	public ReportOperationFormulaAnalyse(Connection conn ,String tabid , List formulaList  ,int reportFlag , ArrayList unitList){
		this.conn = conn;
		this.tabid = tabid;
		this.formulaList = formulaList;
		if(reportFlag == 2){
			this.reportPrefix = "tt_";
			StringBuffer str=new StringBuffer("");
			for(int i=0;i<unitList.size();i++) {
                str.append(",'"+(String)unitList.get(i)+"'");
            }
			if(str.length()>0) {
                this.whereSQL = " and unitcode in ("+str.substring(1)+") ";
            }
		}
	}
	
	
	
	/**
	 * 汇总公式计算使用一个填表单位
	 * @param conn
	 * @param unitCode
	 */
	public ReportOperationFormulaAnalyse(Connection conn , String unitCode){
		this.conn = conn;
		this.unitCode = unitCode;
	}
	
	
	/**
	 * 汇总公式计算使用一批填表单位
	 * @param conn
	 * @param unitCode
	 */
	public ReportOperationFormulaAnalyse(Connection conn , ArrayList unitCodeList){
		this.conn = conn;
		this.unitCodeList = unitCodeList;		
	}
	
	/**
	 * 汇总公式分析运算
	 *   公式描述@错误信息描述#公式描述@错误信息描述
	 *	 如果没有错误信息返回“null”和先前的计算公式一致！
	 * @param tabid
	 * @return
	 * @throws GeneralException
	 */
	public String reportCollectFormulaAnalyse(String tabid) throws GeneralException{
		ReportCollectFormulaAnalyse rcfa ;
		if(this.unitCodeList.size() == 0){
			rcfa = new ReportCollectFormulaAnalyse(this.conn , tabid , this.unitCode);
		}else{
			rcfa = new ReportCollectFormulaAnalyse(this.conn , tabid , this.unitCodeList);
			rcfa.setUnitCode(this.unitCode);
		}
		rcfa.setOperate(this.operate);
		String temp = rcfa.reportCollectFormulaAnalyse();
		if(temp == null || "".equals(temp)){
			return  "null";
		}
		String tt =temp;
		if(tt.charAt(tt.length()-1) == '#'){
			tt = tt.substring(0,tt.length()-2);
		}
		return tt;
	}
	
	
	/**
	 * 计算公式分析控制器
	 * @return
	 * @throws GeneralException 
	 */
	public String reportFormulaAnalyse() throws GeneralException{		
		StringBuffer result = new StringBuffer();
		//计算公式为空
		if(this.formulaList == null){
			//错误
		}else{
			//遍例所有公式
			for(int i=0; i< this.formulaList.size(); i++){						
				RecordVo vo = (RecordVo)this.formulaList.get(i);				
				int flag = vo.getInt("colrow");	//公式类型				
				switch(flag){
					case 0:	//表内行公式
						String temp = this.reportInnerRowFormulaAnalyse(vo);
						if("null".equals(temp)){
						}else{
							result.append(this.reportFormulaInfo(vo));
							result.append("\\n");
							result.append("  ");
							result.append(temp);
							result.append("#");
						}				
						break;
					case 1:	//表内列公式
						String temp1 = this.reportInnerColFormulaAnalyse(vo);
						if("null".equals(temp1)){
						}else{
							result.append(this.reportFormulaInfo(vo));
							result.append("\\n");
							result.append("  ");
							result.append(temp1);
							result.append("#");
						}		
						break;
					case 2:	//表间行公式
						String temp2 = this.reportSpaceRowFormulaAnalyse(vo);
						if("null".equals(temp2)){
						}else{
							result.append(this.reportFormulaInfo(vo));
							result.append("\\n");
							result.append("  ");
							result.append(temp2);
							result.append("#");
						}		
						break;
					case 3:	//表间列公式
						String temp3 = this.reportSpaceColFormulaAnalyse(vo);
						if("null".equals(temp3)){
						}else{
							result.append(this.reportFormulaInfo(vo));
							result.append("\\n");
							result.append("  ");
							result.append(temp3);
							result.append("#");
						}		
						break;
					case 4:	//表间格公式
						String temp4 = this.reportSpaceGridFormulaAnalyse(vo);
						if("null".equals(temp4)){
						}else{
							result.append(this.reportSpaceGridFormulaAnalyse(vo));
							result.append("\\n");
							result.append("  ");
							result.append(temp4);
							result.append("#");
						}		
						break;
					default:
						//错误
				}
				
			}
		}
		
		if(result == null || "".equals(result.toString())){
			return  "null";
		}
		String tt = result.toString();
		if(tt.charAt(tt.length()-1) == '#'){
			tt = tt.substring(0,tt.length()-2);
		}
		return tt;
	}
	
	/**
	 * 表内行公式分析
	 * @param vo 公式信息
	 * @return
	 */
	public String reportInnerRowFormulaAnalyse(RecordVo vo) throws GeneralException{
		
		/*
		 * 左表达式只允许为数值
		 * 表内行公式说明：1=2+3 每一列中的第一行=第2行+第3行 
		 * 左表达式行为一个数值，右表达式可以多个
		 * SQL模板：
		 * update tb11 set c2= (select cn from tb11 where secid =3)+
		 * (select cn from tbN where secid =4)  where secid = 2
		 */
		
		//原始表达式
		String lExpr = vo.getString("lexpr");
		String rExpr = vo.getString("rexpr");
		
		//规范的表达式
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		this.tbo = new TnameBo(this.conn , this.tabid );
		String excludeexpr ="";
		if(lExpr!=null&&lExpr.indexOf("|")!=-1){
			excludeexpr = lExpr.substring(lExpr.indexOf("|")+1,lExpr.length());
		}
		if(excludeexpr.indexOf(",")!=-1){
		String expr[] = excludeexpr.split(",");
		for(int m=0;m<expr.length;m++){
			if(!"".equals(expr[m].trim())&&!",".equals(expr[m].trim())){
				this.tbo.getColMap().remove(expr[m].trim());
			}
		}
		}else{
			if(excludeexpr.trim().length()>0&&!"".equals(excludeexpr.trim())&&!",".equals(excludeexpr.trim())){
				this.tbo.getColMap().remove(excludeexpr.trim());
			}
		}
		ReportInnerFormulaOperation rifo = new ReportInnerFormulaOperation(this.conn ,le , re ,this.tbo , 1 ,this.reportPrefix ,this.whereSQL);
		rifo.setUserView(this.userView);
		String result = rifo.reportInnerFormulaAnalyse();
		if(result == null || "".equals(result)){
			return "null";
		}else{
			return result.toString();
		}
	}
	
	
	/**
	 * 表内列公式分析
	 * @param vo 公式信息
	 * @return
	 */		
	public String reportInnerColFormulaAnalyse(RecordVo vo) throws GeneralException{
		/*
		 * 左表达式只允许为数值
		 * 表内列公式说明：1=2+3 每一行的第1列=第2列+第3列
		 * 左表达式行为一个数值，右表达式可以多个
		 * SQL模板：
		 * update tab11 set c2 = c3+c4 where secid not in (甲信息)
		 */
		//原始表达式
		String lExpr = vo.getString("lexpr");
		String rExpr = vo.getString("rexpr");
		
		//规范的表达式
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		this.tbo = new TnameBo(this.conn , this.tabid );
		String excludeexpr ="";
		if(lExpr!=null&&lExpr.indexOf("|")!=-1){
			excludeexpr = lExpr.substring(lExpr.indexOf("|")+1,lExpr.length());
		}
		
		ReportInnerFormulaOperation rifo = new ReportInnerFormulaOperation(this.conn ,le , re ,this.tbo , 2 ,this.reportPrefix ,this.whereSQL);
		rifo.setExcludeexpr(excludeexpr);
		rifo.setUserView(this.userView);
		String result = rifo.reportInnerFormulaAnalyse();
		if(result == null || "".equals(result)){
			return "null";
		}else{
			return result.toString();
		}
	}
	
	/**
	 * 表间行公式分析
	 * @param vo 公式信息
	 * @return
	 * @throws GeneralException 
	 */
	public String reportSpaceRowFormulaAnalyse(RecordVo vo) throws GeneralException{
		/*
		 * 第60表表间行计算公式语法分析：
		 * 	1:ALL = [62:1..3]  ALL代表所有列，ALL不区分大小写，第60表的第1行的所有列 = 第62表的第1行对应列的值+第62表的第1行对应列的值+第62表的第1行对应列的值
		 *  1:1,2,3 = [62:1] 第60表的第1行的1，2，3列 = 第62表的第1行的对应列的值
		 *  1:2 = (C100 + [62:1])+C200 第60表的第1行的2列 = （100 + 第62表的第1行第2列 ） + 200
		 *  
		 *  左表达式：1:ALL 或 1:2,3,4
		 */
		
		//原始表达式
		String lExpr = vo.getString("lexpr");
		String rExpr = vo.getString("rexpr");
		int tabid = vo.getInt("tabid");
		
		//规范的表达式
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		
		//表间计算公式分析类
		ReportSpaceFormulaOperation rsfo = new ReportSpaceFormulaOperation(this.conn, tabid ,le,re,1,this.reportPrefix,this.whereSQL);
		//校验结果
		rsfo.setUserView(this.userView);
		String result = rsfo.reportSpaceFormulaOperation();	
		return result;
	}
	
	
	/**
	 * 表间列公式分析
	 * @param vo 公式信息
	 * @return
	 * @throws GeneralException 
	 */		
	public String reportSpaceColFormulaAnalyse(RecordVo vo) throws GeneralException{
		//原始表达式
		String lExpr = vo.getString("lexpr");
		String rExpr = vo.getString("rexpr");
		int tabid = vo.getInt("tabid");
		
		//规范的表达式
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		// FIXME 提速
		ReportSpaceFormulaOperation rsfo = new ReportSpaceFormulaOperation(this.conn, tabid ,le,re,2,this.reportPrefix ,this.whereSQL);
		rsfo.setUserView(this.userView);
		String result = rsfo.reportSpaceFormulaOperation();	
		return result;
	}
	
	/**
	 * 表间格公式分析
	 * @param vo 公式信息
	 * @return
	 * @throws GeneralException 
	 */
	public String reportSpaceGridFormulaAnalyse(RecordVo vo) throws GeneralException{
		/*
		 * 1:2 = [11:2:3]+c100
		 * 1行2列=11表2行3列+100
		 */
		//原始表达式
		String lExpr = vo.getString("lexpr");
		String rExpr = vo.getString("rexpr");
		int tabid = vo.getInt("tabid");
		
		//规范的表达式
		String le = ExprUtil.getExpr(lExpr);
		String re = ExprUtil.getExpr(rExpr);
		
		ReportSpaceGridFormulaAnalyse reportSpaceGridFormulaAnalyse = new ReportSpaceGridFormulaAnalyse(this.conn,tabid,le,re ,this.reportPrefix,this.whereSQL);
		reportSpaceGridFormulaAnalyse.setUserView(this.userView);
		String result =reportSpaceGridFormulaAnalyse.reportSpaceGridFormulaAnalyse();

		return result;
	}
	
	/**
	 * 报表公式描述信息
	 * @param vo 封装一条公式信息
	 * @return
	 */
	public String reportFormulaInfo(RecordVo vo ){

		StringBuffer result = new StringBuffer();
		result.append(vo.getInt("expid"));
		result.append(":");
		result.append(vo.getString("cname"));
		result.append(":");
		result.append(vo.getString("lexpr"));
		result.append("=");
		result.append(vo.getString("rexpr"));
		int flag = vo.getInt("colrow");
		switch(flag){
			case 0:	//表内行公式
				result.append("("+ResourceFactory.getProperty("edit_report.tableRowFormula")+")");
				break;
			case 1:	//表内列公式
				result.append("("+ResourceFactory.getProperty("edit_report.tableColFormula")+")");
				break;
			case 2:	//表间行公式
				result.append("("+ResourceFactory.getProperty("edit_report.tablesRowFormula")+")");
				break;
			case 3:	//表间列公式
				result.append("("+ResourceFactory.getProperty("edit_report.tablesColFormula")+")");
				break;	
			case 4:	//表间格公式
				result.append("("+ResourceFactory.getProperty("edit_report.tablesGridFormula")+")");
				break;
			default:
				//错误
		}
		
		return result.toString();
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}
	

}
