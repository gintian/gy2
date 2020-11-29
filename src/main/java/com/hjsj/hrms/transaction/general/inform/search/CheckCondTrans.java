package com.hjsj.hrms.transaction.general.inform.search;

import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class CheckCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String check="ok";
		ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");  //cmq removed it 
		factorlist=factorlist!=null&&factorlist.size()>0?factorlist:new ArrayList();
		 
        String expression=SafeCode.decode((String)this.getFormHM().get("expression"));
        expression=expression!=null&&expression.trim().length()>0?expression:"";
        expression=PubFunc.keyWord_reback(expression);
        
        String a_code=(String)this.getFormHM().get("a_code");
        a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
        
        String tablename=(String)this.getFormHM().get("tablename");
        tablename=tablename!=null&&tablename.trim().length()>0?tablename:"";
        
        String type=(String)this.getFormHM().get("type");
        type=type!=null&&type.trim().length()>0?type:"";
        
        String flag=(String)this.getFormHM().get("flag");
        flag=flag!=null&&flag.trim().length()>0?flag:"";
        check=expressionvalidate(expression,factorlist);
        
        String checkflag=(String)this.getFormHM().get("checkflag");
        checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"";
        
        String no_manager_priv=(String)this.getFormHM().get("no_manager_priv");
        no_manager_priv=no_manager_priv!=null&&no_manager_priv.trim().length()>0?no_manager_priv:"";
        this.getFormHM().put("check",check);
        this.getFormHM().put("flag",flag);
        this.getFormHM().put("tablename",tablename);
        this.getFormHM().put("type",type);
        //this.getFormHM().put("expression",SafeCode.encode(expression)); 
        this.getFormHM().put("expression",expression); 
        this.getFormHM().put("a_code",a_code);
        this.getFormHM().put("checkflag",checkflag);
        this.getFormHM().put("no_manager_priv",no_manager_priv);
	}
	private String expressionvalidate(String expression,ArrayList factorlist) throws GeneralException{
		String except="ok";
		/**为了分析用*/
	    if(!isHaveExpression(expression,factorlist.size()))
	    	except=ResourceFactory.getProperty("errors.query.notexistfactor");
	    expression=expression.replaceAll("!","-");
	    TSyntax syntax=new TSyntax();
	    if(!syntax.Lexical(expression))
	    	except=ResourceFactory.getProperty("errors.query.expression");
	    if(!syntax.DoWithProgram())
	    	except=ResourceFactory.getProperty("errors.query.expression");
	    return except;
	}
	/**
	 * 分析表达式的合法式
	 * @param expression
	 * @param nmax　最大表达式因子号
	 * @return
	 */
	private boolean isHaveExpression(String expression,int nmax){
		boolean bflag=true;
		String strlastno="";
		int ncurr=0;
		for(int i=0;i<expression.length();i++){
			char v =expression.charAt(i);
			if(((i+1)!=expression.length())&&(v>='0'&&v<='9')){
				strlastno=strlastno+v;
			}else{
				if(v>='0'&&v<='9'){
					strlastno=strlastno+v;
				}
				if(!"".equals(strlastno)){
					ncurr=Integer.parseInt(strlastno);
					if(ncurr>nmax){
						bflag=false;
						break;
					}
				}
				strlastno="";
			}
		}        
		return bflag;
	}		
}
