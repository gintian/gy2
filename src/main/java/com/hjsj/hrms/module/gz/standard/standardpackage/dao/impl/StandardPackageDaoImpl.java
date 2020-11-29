/**
 * FileName: StandardPackageDaoImpl
 * Author:   xuchangshun
 * Date:     2019/11/22 15:30
 * Description: 历史沿革数据层实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.hjsj.hrms.module.gz.standard.standardpackage.dao.impl;

import com.hjsj.hrms.module.gz.standard.standardpackage.dao.IStandardPackageDao;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


/**
 * 〈类功能描述〉<br> 
 * 〈历史沿革数据层实现类〉
 *
 * @Author xuchangshun
 * @Date 2019/11/22
 * @since 1.0.0
 */
public class StandardPackageDaoImpl implements IStandardPackageDao {
    /**数据库底层操作类**/
    private ContentDAO contentDAO;
    /**
     * 历史沿革数据层Dao构造方法
     * @Author xuchangshun
     * @param conn : 数据库链接
     * @Date 2019/11/22 15:39
     */
    public StandardPackageDaoImpl(Connection conn){
        contentDAO = new ContentDAO(conn);
    }
    /**
     * 获取历史沿革列表
     * @Author xuchangshun
     * @return List 历史沿革列表数据
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:05
     */
    @Override
    public List<RecordVo> getPackageList(String sql) throws GeneralException {
        List<RecordVo> pkgList = new ArrayList<RecordVo>();
        RowSet rs = null;
        try {
            rs = this.contentDAO.search(sql);
            while (rs.next()) {
                //单个历史沿革信息
                RecordVo pkgVo = new RecordVo("gz_stand_pkg");
                pkgVo.setInt("pkg_id", rs.getInt("pkg_id"));
                pkgVo.setString("b0110", rs.getString("b0110"));
                pkgVo.setString("status", rs.getString("status"));
                pkgList.add(pkgVo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //获取历史沿革数据失败
            throw new GeneralException("gz.standard.pkg.getPkgDataError");
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return pkgList;
    }

    /**
     * 获取单个历史沿革的信息
     * @Author xuchangshun
     * @param pkg_id :历史沿革id
     * @return RecordVo:历史沿革信息集
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:07
     */
    @Override
    public RecordVo getPackageInfor(String pkg_id) throws GeneralException {
        RecordVo vo = new RecordVo("gz_stand_pkg");
        try {
            vo.setString("pkg_id", pkg_id);
            vo = this.contentDAO.findByPrimaryKey(vo);
        } catch (Exception e) {
            e.printStackTrace();
            //"获取历史沿革数据失败！"
            throw new GeneralException("gz.standard.pkg.getPkgDataError");
        }
        return vo;
    }

    /**
     * 批量保存历史沿革数据
     * @Author xuchangshun
     * @param packageList :历史沿革数据列表
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:17
     */
    @Override
    public void batchSavePackageInfor(List<RecordVo> packageList) throws GeneralException {
        try {          
            contentDAO.updateValueObject(packageList);
        } catch (Exception e) {  
            e.printStackTrace();
            throw new GeneralException("gz.standard.saveFail");
        }
    }
    /**
     * 保存历史沿革数据
     * @Author xuchangshun
     * @param vo :历史沿革数据vo
     * @param sqlList:sql语句集合以及init_type
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:23
     */
    @Override
    public void savePackageInfor(RecordVo vo, List sqlList) throws GeneralException {
        try {
            String init_type = (String)sqlList.get(0);
            if(StringUtils.equals(init_type, "create")) {
                contentDAO.addValueObject(vo);
            }else {
                contentDAO.updateValueObject(vo);
                //删除关闭引用的标准表
                for(int i = 1; i < 3; i++) {
                	if(StringUtils.isNotBlank((String) sqlList.get(i))) {
                		contentDAO.update((String) sqlList.get(i));
                	}
                }
            }
            //插入新增的标准表,更新标准表的flag
            for(int i = 3; i < sqlList.size(); i++) {
            	if(StringUtils.isNotBlank((String) sqlList.get(i))) {
            		contentDAO.update((String) sqlList.get(i));
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.saveFail");
        }
    }

    /**
     * 删除历史沿革数据
     * @Author xuchangshun
     * @param vo :历史沿革数据vo
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:26
     */
    @Override
    public void deletePackageInfor(RecordVo vo) throws GeneralException {
        try {
            contentDAO.deleteValueObject(vo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.pkg.deletePkgFail");
        }
    }

    /**
     * 批量删除历史沿革数据
     * @Author xuchangshun
     * @param packageList :历史沿革数据列表
     * @return String 成功返回success 失败返回错误的提示信息
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 15:29
     */
    @Override
    public String batchDeletePackageInfor(List<RecordVo> packageList) throws GeneralException {
        return null;
    }
    
    /**
     * 启用历史沿革
     * @Author qinxx
     * @param sqlList:启用历史沿革sql集合
     * @param init_type：区分历史沿革页面直接修改数据和新建修改页面修改数据
     * @throws GeneralException 异常信息
     * @Date 2019/12/06 10:42
     */
    @Override
    public void enablePackage(List sqlList,String init_type) throws GeneralException {
        try {
        	for(int i = 0; i < sqlList.size(); i++) {
        		if(StringUtils.isNotBlank((String)sqlList.get(i))) {
        			contentDAO.update((String) sqlList.get(i));
        		}
        	}
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.pkg.enableFail");
        } 
    }
    
    /**
     * 获取标准表列表
     * @Author xuchangshun
     * @param pck_id :历史沿革id
     * @return List 标准表列表
     * @throws GeneralException 异常信息
     * @Date 2019/11/22 17:55
     */
    @Override
    public List<RecordVo> getStandTableList(String pck_id) throws GeneralException {
        List<RecordVo> voList = new ArrayList<RecordVo>();
        int id=0;
        String name=null;
        ArrayList list=new ArrayList();
        String  sql="select id,name from gz_stand_history where pkg_id=? order by id";
        list.add(pck_id);
        RowSet rs=null;
        try {
            rs = contentDAO.search(sql,list);
        
            while(rs.next()){
                RecordVo  vo=new RecordVo("gz_stand_history");
                id=rs.getInt("id");
                name=rs.getString("name");
                vo.setInt("id",id);
                vo.setString("name",name);
                voList.add(vo);
            }     
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.pkg.exportStandardError");
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return voList;
    }

    
    /**
     * 获取当前历史沿革中没有使用到的标准表列表
     * @Author qinxx
     * @param noImportSql:未引用标准表的sql
     * @return 当前历史沿革中没有使用到的标准表列表
     * @throws GeneralException 异常信息
     * @Date 2019/12/22 18:29
     */
    @Override
    public List getNoUseInPackageStandList(String noImportSql) throws GeneralException {
        List noImportList = new ArrayList();
        RowSet rs = null;
        try {
            rs = contentDAO.search(noImportSql);
            while(rs.next()) {
                List noImportStandList = new ArrayList();
                String standardId = rs.getString("id");
                String name = rs.getString("name");               
                noImportStandList.add(standardId);
                noImportStandList.add(name);
                noImportStandList.add("0");
                noImportList.add(noImportStandList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.pkg.getPkgDataError");
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return noImportList;
    }
    
    /**
     * 获取顶级机构代码
     * @Author qinxx
     * @return 顶级机构代码
     * @param superOrganizationSql:顶级机构代码sql
     * @throws GeneralException 异常信息
     * @Date 2019/12/13 14:29
     */
    @Override
    public String getSuperOrganization(String superOrganizationSql) throws GeneralException{
        RowSet rs = null;
        String orgCode = "";
        try {
            rs = contentDAO.search(superOrganizationSql);
            if(rs.next()) {
                orgCode = rs.getString("codeitemid"); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.pkg.getPkgDataError");
        } finally {
            PubFunc.closeDbObj(rs);
        }
        return orgCode;
    }
    
    /**
     * 创建历史沿革时获取id
     * @Author qinxx
     * @return 最大id
     * @param maxSql:最大id的sql
     * @throws GeneralException 异常信息
     * @Date 2019/12/14 11:16
     */
    @Override
    public int getMaxPkgId(String maxSql) throws GeneralException{
        RowSet rs = null;
        int pkg_id = 0;
        try {
            rs = contentDAO.search(maxSql);
            while(rs.next()) {
                pkg_id = rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("gz.standard.saveFail");
        } finally {
            PubFunc.closeDbObj(rs);
        }       
        return pkg_id;
    }
}
