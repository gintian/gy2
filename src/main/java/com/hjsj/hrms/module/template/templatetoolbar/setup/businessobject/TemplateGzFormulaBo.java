package  com.hjsj.hrms.module.template.templatetoolbar.setup.businessobject;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* <p>Title: TemplateGzFormulaBo</p>
* <p>Description: 人事异动-计算公式，获取公式组列表</p>
* <p>Company: HJSOFT</p> 
* @author gaohy
* @date 2015-12-24 下午03:30:08
 */
public class TemplateGzFormulaBo {
	private ContentDAO dao=null;
	/**登录用户*/
	private UserView userview;
	public TemplateGzFormulaBo(ContentDAO dao,UserView userView){
		this.dao=dao;
		this.userview=userView;
	}
	public  ArrayList getGzFormula(String tableid) throws GeneralException{
		
		/**判断用户是否拥有该模版资源的权限**/
        /*boolean isCorrect=false; //不需要判断权限 lis 20160902
        if(this.userview.isHaveResource(IResourceConstant.RSBD,tableid))//人事移动
            isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.ORG_BD,tableid))//组织变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.POS_BD,tableid))//岗位变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.GZBD,tableid))//工资变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.INS_BD,tableid))//保险变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_FG,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_GX,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_JCG,tableid))
                isCorrect=true;
        if(!isCorrect){
            throw new GeneralException("当前用户不具有相应的权限");
        }*/
		
		ArrayList gzList = new ArrayList();
		HashMap gzMap = new HashMap();
		//ArrayList gzMap = new ArrayList();
		
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select id,flag,chz,nSort ");
		sqlstr.append("from gzAdj_formula ");
		sqlstr.append(" where tabid='"+tableid+"'");
		sqlstr.append(" order by nSort");
		ResultSet rs=null;
		try {
			rs=this.dao.search(sqlstr.toString());
			while(rs.next()){
				gzMap=new HashMap();
				gzMap.put("groupName", rs.getString("chz"));
				gzMap.put("groupId", rs.getString("id"));
				
//				gzMap=new ArrayList();
//				gzMap.add("'"+rs.getString("chz")+"'");
//				gzMap.add("'"+rs.getString("id")+"'");
				String flag = rs.getString("flag");//状态是否启用 lis 20160808
				if("1".equals(flag))
					gzMap.put("groupStat", true);
				else
					gzMap.put("groupStat", false);
					
				gzMap.put("nSort", rs.getString("nSort"));
				//gzMap.add(bo);
				gzList.add(gzMap);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gzList;
	}
}
