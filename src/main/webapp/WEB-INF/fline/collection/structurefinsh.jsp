<%@ page contentType="text/html; charset=UTF-8" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ page isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>大数据平台</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.1/jquery-1.7.1.min.js"></script>
<script type="text/javascript"src="<%=request.getContextPath()%>/js/jquery-1.7.1/jquery.colorbox-min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css" />
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap_collect.css" rel="stylesheet">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/Jrquest.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/HbaseUtil.js"></script>
</head>
<style>
input::-webkit-input-placeholder {
    color:#0CBB12 !important;
}
input:-moz-placeholder {
    color:#0CBB12 !important;
}
ul.licss li{text-indent:10px; height:34px; line-height:34px;overflow:hidden;} 
/* 高度需要计算好，与布局图片一定关系 */ 
ul.licss li:hover{ background:#EBEBEB} 
/* 为了有动感背景变色换色，对li设置hover伪类 */ 
label{
	font-size: 16px;
}
.form-inline label {
	font-size: 14px;
	display:block !important;
	font-weight:bold;
}
.form-group {
    margin-bottom: 15px;
}
.bkgdEb{background:#EBEBEB}
.left_down_file{padding:20px;}
.left_down_file div{margin-top:10px}
.col-md-6{
	position: relative;
  min-height: 1px;
  padding-right: 15px;
  padding-left: 15px;
	width: 45%;
	
}
.import_err_tip{display: none}
.list-group {
    padding-left: 0;
    margin-bottom: 20px;
}
a.list-group-item, button.list-group-item {
    color: #555;
}
.list-group-item {
    position: relative;
    display: block;
    padding: 10px 15px;
    margin-bottom: -1px;
    background-color: #fff;
    border: 1px solid #ddd;
}
a {
    color: #337ab7;
    text-decoration: none;
}
a {
    background-color: transparent;
}
.list-group a:hover{
	background-color: #eeeeee;
}
td{
 text-align:center !important;
 vertical-align:middle !important;
}
a:link {
text-decoration: none;
}
a:visited {
text-decoration: none;
}
a:hover {
text-decoration: none;
}
a:active {
text-decoration: none;
}
li{
 margin-top:1px !important;
 padding-right:5px !important;
}
.hbase_set_step{font-size:16px;margin-left:20px}
#box1,#box2,#box3,#box4{ display:none;}
.circle {
	border-radius: 50%;
	width: 40px;
	height: 40px; 
	font-size:20px;
	color:#ffffff;
	line-height:40px;
	text-align:center;
	-webkit-box-shadow: 4px 4px 3px 0px rgba(50, 50, 50, 0.75);
	-moz-box-shadow:    4px 4px 5px 0px rgba(50, 50, 50, 0.75);
	box-shadow:         4px 4px 3px 0px rgba(50, 50, 50, 0.75);
	float:left;
}
.active {
	background-color:#43c400;
}
.inactive {
	background-color:#555555;
}
span{
  font-size:14px;
  display:inline;
  vertical-align: middle;
    line-height: 40px;
    margin-left: 10px;
}
.btn{
 margin-left:20px
}
table{
    table-layout:fixed;/* 只有定义了表格的布局算法为fixed，下面td的定义才能起作用。 */
}
td{
	height:100%;
    word-break:keep-all;/* 不换行 */
    white-space:nowrap;/* 不换行 */
    overflow:hidden;/* 内容超出宽度时隐藏超出部分的内容 */
    text-overflow:ellipsis;/* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
}
td div{
	width :90%
	height:100%;
    word-break:keep-all;/* 不换行 */
    white-space:nowrap;/* 不换行 */
    overflow:hidden;/* 内容超出宽度时隐藏超出部分的内容 */
    text-overflow:ellipsis;/* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
}
.maskpb {
   display:none;
   background-color:#dddddd;z-index:999999;position:absolute;width:100%;height:372px;
   opacity: 0.5;
  -ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=50)";
  filter: alpha(opacity=50);
}
</style>
<body>
<%@ include file="../../head.jsp"%>
<div style="margin:10px 20px 0 20px;height:550px">
	<div class="maincontent" style="margin-top:20px;margin-bottom:20px;">
		<div >
			<h2 style="margin:20px 0 0 20px;"><Strong>结构化数据采集</Strong></h2>
		</div>
		<div>
			<div style="margin:10px 10px 5px 20px;float:left">
				<img src="<%=request.getContextPath()%>/images/logo_4.png"/>
			</div>
			<div style="float:left;margin-top:30px;">
				<div style="float:left;">
					<font style="color:#FB8B13;font-size:20px;"><strong>数据采集已完成！</strong></font>
				</div>
				<div style="text-decoration:underline;float:left;margin-top:5px;">
					<font style="color:#0CBB12">共导入</font><font style="color:red">${tablehead[0]}</font><font style="color:#0CBB12">张表,</font><font style="color:red">${tablehead[1]}</font>
					<font style="color:#0CBB12">条数据，耗时</font><font style="color:red">${tablehead[2]}</font><font style="color:#0CBB12">分钟</font>
				</div>
			</div>
		</div>
		<div class="row" style="float:left;width:100%;margin-left:5px ">
			<div   class="col-md-3" >
				<div class="panel panel-default" style="background-color:#f4f4f4" >
				  	<div class="panel-body"  >
				  		<input type="text" class="form-control" id="tablename" placeholder="查找表名......" style="width:70%;">
				  		<a href="#"  id="serach" ><img src="<%=request.getContextPath()%>/images/search.png"  /></a>
				  	</div>
			  	 	<div class="panel-body" style="overflow:auto; height:400px">
			  	 	 	<ul class="licss" id="tablehtml"> 
			        	<c:forEach items = "${tableList}" var = "b" varStatus="status">
							<c:if test="${tablename==b}">
				        	<li style="background:#EBEBEB"><a href="#">${b}</a></li>
				        	</c:if>
				        	<c:if test="${tablename!=b}"><li><a href="#" onclick="ajaxQueryTables(this);return false;">${b}</a></li></c:if>
						</c:forEach>
						</ul> 
				  	</div>
				</div>
			</div>
			<div   class="col-md-9">
				<div class="panel panel-default" style="background-color:#f4f4f4">
					  <div class="panel-body" style="height:50px;border-bottom:1px solid #e5e5e5;">
					  	<font style="color:#FB8B13;font-size:20px;"><strong>${tablename}</strong></font>
			 		 </div>
			 		 <div class="panel-body " style="height:410px">
			 		 	<table class="table table-bordered" id = "hbase_index_table"  >
        				<tr>
        					<td width="5%"><input type="checkbox" name="chall"/></td>
        					<td width="10%">Rowkey</td>
        					<c:forEach items = "${familyColums}" var = "basic" varStatus="status">
        						<td style="padding:0">
	        						  <table width="100%">
	        							<tr><td colspan="${sizeMap[basic.key]}" title="${basic.key}">${basic.key}</td></tr>
	        		 		 		    <tr>
	        							<td style="padding:0" colspan="${sizeMap[basic.key]}" title="">
	        								<hr style="height:1px;border:none;border-top:1px double #e5e5e5; width:100%; padding:0; margin:0"/>
	        							</td>
	        							</tr> 
	        							<tr>
	        								<c:forEach items="${basic.value}" var="colname">
		        								<td width="${width}%" title="${colname}">
		        								${colname}
		        								</td>
		        							</c:forEach>
	        							</tr>
	        						</table>
        					</c:forEach>
        				</tr>
        			
        			<c:forEach items = "${hbaselist}" var = "hbase" varStatus="status">
        				<tr>
        					<td><input type="checkbox" name="ch"/></td>
        					<td title="${hbase.rowkey}">${hbase.rowkey}</td>
        				<c:forEach items="${hbase.family}" var="family">
        					<td style="padding:0">
        						<table class="table" style="padding:0;margin:0">
        							<tr>
	        							<c:forEach items = "${familyColums}" var = "basic1" varStatus="status">
	        							<c:if test="${family.key==basic1.key}">
		        							<c:forEach items="${basic1.value}" var="colname">
		        								<!-- colum  -->
			        								<td width="${width}%"  onclick="switchtdinput(this)">
			        								<c:forEach items="${family.value}" var="colv">
			        									<c:if test="${colv.colum==colname}">
			        										 <div title="${colv.value}">${colv.value}</div>
			        									</c:if>
	        										</c:forEach>
			        								</td>
		        							</c:forEach>
	        							</c:if>
	        							</c:forEach>
        							</tr>
        						</table>
        					</td>
        				</c:forEach>
        				</tr>
        			</c:forEach>
        			</table>
        			<table>
        			<tr>
		        		<td colspan="9">
						<span>共${total}条;<span id='count'>总共${countPage}</span>页;</span>
						<span>第${pageIndex}页;</span>
						<c:if test="${pageIndex==1}">
							前一页
						</c:if>
						<c:if test="${pageIndex!=1}">
							<a href='javascript:void' onclick='PrePage(${countPage},${pageIndex},"/collection/queryList?tableName=${tablename}")' id='aPrePage' >前一页</a>
						</c:if>
						<c:if test="${pageIndex==countPage}">
							下一页
						</c:if>
						<c:if test="${pageIndex<countPage}">
							<a href='javascript:void' onclick='NextPage(${countPage},${pageIndex},"/collection/queryList?tableName=${tablename}")' id='aNextPage'>下一页</a>&nbsp;&nbsp;
						</c:if>
						&nbsp;&nbsp;
						</td>
        			</tr>
        			</table>
			 		 </div>
				</div>
			</div>
		</div>
	</div>
	<div id="hide">
		<input type="hidden" value="${dbInfo.dbtype}" name="dbtype" id="dbtype"/>
		<input type="hidden" value="${dbInfo.dbname}" name="dbname" id="dbname"/>
		<input type="hidden" value="${dbInfo.url}" name="connectionurl" id="connectionurl"/>
		<input type="hidden" value="${dbInfo.username}" name="username" id="username"/>
		<input type="hidden" value="${dbInfo.passwd}" name="password" id="password"/>
		<input type="hidden" value="${tablehead[0]}" name="tablecount" id="tablecount"/>
		<input type="hidden" value="${tablehead[1]}" name="tabledate_c" id="tabledate_c"/>
		<input type="hidden" value="${tablehead[2]}" name="alltime" id="alltime"/>
	</div>
</div>
</body>
<script type="text/javascript">
$(document).ready(function() { 
	$(".head_fx").find(".fx img").each(function(){
		var src = $(this).attr("src");
		var filename = src.substr(src.lastIndexOf("/")+1);
		if(filename=="caiji.png"){
			var newFilename = filename.replace(".png","_1.png");
			var newSrc = src.substr(0,src.lastIndexOf("/")+1)+newFilename;
			$(this).attr("src",newSrc);
		}
	});
});
</script>
<script type="text/javascript">
	var dbtype=$("#dbtype").val();
	var dbname=$("#dbname").val();
	var connectionurl=$("#connectionurl").val();
	var username=$("#username").val();
	var password=$("#password").val();
	var tablecount=$("#tablecount").val();
	var tabledate_c=$("#tabledate_c").val();
	var alltime=$("#alltime").val();
	function PrePage(countPage,pageIndex,pathName){
		if(pageIndex-1>0){
			pageIndex -= 1;
			var url = HbaseUtil.getRootPath()+pathName+"&tablecount="+tablecount+"&tabledate_c="+tabledate_c+"&alltime="+alltime+"&page="+pageIndex+"&rows=8&dbtype="+dbtype+"&dbname="+dbname+"&connectionurl="+connectionurl+"&username="+username+"&password="+password+"";
			$.href(url);
		}else{
			return false;
		}
	}
	function NextPage(countPage,pageIndex,pathName){
		if (pageIndex + 1 <= countPage){
			pageIndex += 1;
			var url = HbaseUtil.getRootPath()+pathName+"&tablecount="+tablecount+"&tabledate_c="+tabledate_c+"&alltime="+alltime+"&page="+pageIndex+"&rows=8&dbtype="+dbtype+"&dbname="+dbname+"&connectionurl="+connectionurl+"&username="+username+"&password="+password+"";
			$.href(url);
		}else{
			return false;
		}
	}
	function ajaxQueryTables(obj){
		var tableName = $(obj).html();
		Pager.pageIndex = 1;
		var url = HbaseUtil.getRootPath() + '/collection/queryList?tableName='+tableName+"&tablecount="+tablecount+"&tabledate_c="+tabledate_c+"&alltime="+alltime+"&dbtype="+dbtype+"&dbname="+dbname+"&connectionurl="+connectionurl+"&username="+username+"&password="+password+"&page="+Pager.pageIndex+"&rows=8";
		$.href(url);
	}
	$(document).ready(function() {  
		$("#serach").click(function(){
			var tablename=$("#tablename").val();
			var parms="tablename="+tablename+"&dbtype="+dbtype+"&dbname="+dbname+"&connectionurl="+connectionurl+"&username="+username+"&password="+password+"";
			var url=HbaseUtil.getRootPath() +"/collection/ajaxgettable";
			$.ajax({
				type : "POST",
				data : parms,
				url : url,
				dataType : "text",
				success : function(msg) {
					 var tablenames=eval("("+msg+")").tablenames;
					 var html="";
					 for(var i=0;i<tablenames.length;i++){
						 html+="<li><a href='#' onclick='ajaxQueryTables(this);return false;'>"+tablenames[i]+"</a></li>";
					 }
					 $("#tablehtml").html(html); 
				},
				error:function(){
					alert("表查找出错");
				}
			});
			/* $.ax("ajaxgettable",parms,"POST","text",function(msg){
				
			},function(msg){
				
			}); */
		});
	});
</script>

</html>