package com.hjsj.hrms.module.jobtitle.configfile.transaction;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：ConfigTrans 
 * 类描述：配置页面配置窗口
 * 创建人：gaohy
 * 创建时间：Gao 8, 2015 10:20:54 AM 
 * @version
 */
public class ConfigTrans extends IBusiness {
	/**人员分配**/
	private final static String  RENYUANNODE=ResourceFactory.getProperty("gz_new.gz_accounting.renyuanNode");
	
	@Override
    public void execute() throws GeneralException {
//异步加载树节点
		HashMap hm=this.getFormHM();
		//获取树节点
		String note = (String) this.getFormHM().get("node");
		//截取节点中的标识
		note = note.replaceAll("｜", "|").replaceAll("／", "/");
		String[] _note = note.split("\\|");	//截取节点，前半部分为子节点查询条件，后半部分为子节点级别标识
		
		//接收被选中子节点
		Object  value = (Object) this.getFormHM().get("value");//根据已选值判断该子节点的选中状态
		String strValue=value.toString().replaceAll("^.*\\[", "").replaceAll("].*", "");//除掉括号
		String[] value_a=strValue.split(",");//赋值给数组

		for(int i=0;i<value_a.length;i++){//去每个元素"."前的部分，并去掉空格
			String[] value_b=value_a[i].split("\\.");
			value_a[i]=value_b[0].trim();
		}
		//创建数据库链接
		RowSet rs=null;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer str= new StringBuffer();
		HashMap map = new HashMap();
		ArrayList list = new ArrayList();	
	
		try {
			//第一次从根节点进来
			if("root".equals(note)){
				String _static="static";
				if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
					_static="static_o";
				}
				//查询人事异动下的所有一级节点
				rs=dao.search("select * from Operation  where "+_static+"='1' and "+Sql_switcher.datalength("OperationCode")+"=2");
				while(rs.next()){
					map = new HashMap();
					map.put("id", rs.getString("OperationCode")+"|0");//给每个node添加标识
					map.put("text", rs.getString("OperationName"));//只取人员分配业务模版
					list.add(map);
				}
			}
			//第二次进来
			else {
				String flag = _note[1];//节点级别标识，判断是几级节点
				String condition=_note[0];//查询条件
				
			  //点击一级节点，封装二级节点
				if("0".equals(flag)){
					//将条件封装进list，进行预编译查询
					List conditionlist=new ArrayList();
					conditionlist.add(condition+"%");
					//查询：二级的（Operationcode：二级为四位）
					//排除人员调入的（Operationtype=0）
					rs = dao.search("select OperationCode,OperationName from Operation where OperationCode like ? and "+Sql_switcher.datalength("OperationCode")+"=4 and (Operationtype=3 or Operationtype=10)",conditionlist);
				
					while(rs.next()){
						//封装成json串
						map = new HashMap();
						map.put("id", rs.getString("OperationCode")+"|1");
						map.put("text", rs.getString("OperationName"));
						list.add(map);
					}
				}
				//点击二级节点，封装三级节点
				else{
				    rs = dao.search("select TabId,Name from template_table  where OperationCode=?",Arrays.asList(new String[]{condition}));
					String tabid="";
					while(rs.next()){
						tabid=rs.getString("TabId");
						//判断是否有此表
						if(this.userView.isHaveResource(IResourceConstant.RSBD,tabid)){//人事移动
							
							map = new HashMap();
							map.put("id", rs.getString("TabId")+"."+condition+"|1");//给节点id赋值
							map.put("text", rs.getString("TabId")+"."+rs.getString("Name"));//给节点名称赋值
							map.put("leaf", true);//是否是子节点
							map.put("checked",false);
							for(int j=0;j<value_a.length;j++){//如果以前选了该模版，则设为选中状态
								if(value_a[j].equals(rs.getString("TabId"))){
									map.put("checked",true);//是否可选
								}
							}
							list.add(map);
						}
					}
				}
			}
			hm.put("data", list);
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
