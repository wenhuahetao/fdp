package com.fline.hadoop.data.util.rdb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.util.ArrayList;

import java.util.List;

public class Dababase {

private String url="jdbc:oracle:thin:@localhost:1521:orcl";

private String username="hetaotest";

private String pw="hetaotest";

private Connection conn=null;

//用户名字和密码是自己建立的。

public Connection OpenConn(){

try {

Class.forName("oracle.jdbc.driver.OracleDriver");

try {

conn=DriverManager.getConnection(url,username,pw);

} catch (SQLException e) {

// TODO Auto-generated catch block

e.printStackTrace();

}

} catch (ClassNotFoundException e) {

// TODO Auto-generated catch block

e.printStackTrace();

}

return conn;

}

public ResultSet executeQuery(String sql){

Dababase db = new Dababase();

ResultSet  rs = null;

Connection con =db.OpenConn();

    try {

       Statement sm = con.createStatement();

    rs = sm.executeQuery(sql);

    } catch (SQLException e) {

     // TODO Auto-generated catch block

     e.printStackTrace();

    }
    return rs;

}

public void close(){

    try {

     conn.close();

    } catch (SQLException e) {

     // TODO Auto-generated catch block

     e.printStackTrace();

    }
}

// 获取数据库中所有表的表名，并添加到列表结构中。

public List getTableNameList(Connection conn) throws SQLException {

DatabaseMetaData dbmd = conn.getMetaData();


ResultSet rs = dbmd.getTables(conn.getCatalog(), "HETAOTEST", null, new String[] { "TABLE" });

//System.out.println("kkkkkk"+dbmd.getTables("null", "%", "%", new String[] { "TABLE" }));

List tableNameList = new ArrayList();

while (rs.next()) {

tableNameList.add(rs.getString("TABLE_NAME"));

}

return tableNameList;

}

// 获取数据表中所有列的列名，并添加到列表结构中。

public static List getColumnNameList(Connection conn, String tableName)

throws SQLException {

DatabaseMetaData dbmd = conn.getMetaData();

ResultSet rs = dbmd.getColumns(null, "%", tableName, "%");

List columnNameList = new ArrayList();

while (rs.next()) {
columnNameList.add(rs.getString("COLUMN_NAME"));
System.out.println("列类型"+rs.getString("TYPE_NAME"));

}

return columnNameList;

}

public static void main(String s[]) throws SQLException

{

Dababase dbConn = new Dababase();

Connection conn = dbConn.OpenConn();

if(conn==null)

System.out.println("连接失败");

else

System.out.println("连接成功");

try {

List tableList = dbConn.getTableNameList(conn);//取出当前用户的所有表

//List tableList = dbConn.getColumnNameList(conn, "LOGIN");//表名称必须是大写的，取出当前表的所有列

System.out.println(tableList.size());

for (Object object : tableList) {

String ss=(String)object;
List aa = getColumnNameList(conn,ss);
System.out.println(ss);
}
} catch (SQLException e) {

e.printStackTrace();

} finally {

if (conn != null) {

try {

conn.close();

} catch (SQLException e) {

e.printStackTrace();

}
}
}
}
}
