package com.hjsj.hrms.transaction.mobileapp.utils;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.FieldItem;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 
 * <p>Title: FieldTypeSwitch </p>
 * <p>Description: 数据库中未知字段格式转换</p>
 * <p>Company: hjsj</p>
 * <p>create time  2013-11-18 下午05:50:17</p>
 * @author tiany
 * @version 1.0
 */
public class FieldTypeSwitch {
    /**
     * @param rset
     * @param rsetmd
     * @param index
     * @return
     * @throws SQLException
     */
    public static String getValueByFieldType(ResultSet rset, ResultSetMetaData rsetmd, int index, FieldItem filedItem) throws SQLException {
        String temp=null;
        switch(rsetmd.getColumnType(index))
        {
        
        case Types.DATE:
                temp=PubFunc.FormatDate(rset.getTimestamp(index),"yyyy-MM-dd HH:mm:ss");
                temp = temp.substring(0,filedItem.getItemlength());
                break;          
        case Types.TIMESTAMP:
                temp=PubFunc.FormatDate(rset.getTimestamp(index),"yyyy-MM-dd HH:mm:ss"); 
                temp = temp.substring(0,filedItem.getItemlength());
                break;
        case Types.BLOB:
                temp="二进制文件";                           
                break;      
        case Types.NUMERIC:
              int preci=filedItem.getDecimalwidth();
              temp=String.valueOf(rset.getDouble(index));             
              temp=PubFunc.DoFormatDecimal(temp, preci);
              break;

        default:        
                temp=rset.getString(index);
                break;
        }
        return temp;
    }
}
