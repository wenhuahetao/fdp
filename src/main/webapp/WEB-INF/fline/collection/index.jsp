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
<script type="text/javascript" src="<%=request.getContextPath()%>/js/CollectionUtil.js"></script>
</head>
<style>
.fl1{float:left;height:500px;margin:20px;}
.fr1{float:right;height:500px;margin:20px;padding-right:100px}
.clear{clear: both}
.btn-primary,.btn-lg{background-color: #43c400}


.fa {
position:absolute;
top:50%;
left:50%;
margin:-250px 0 0 -400px;
}
</style>
<script>
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
<body>
<div class="action-box">
<%@ include file="../../head.jsp"%>
<div class="fa" style="width:420px;height:400px;">
	<div align="center" style="margin:30px;font-weight: bold;font-size:25px;font-family: 黑体">非结构化数据采集</div>
	<div style="width:100%;height:300px;" align="center">
		<img alt="" src="<%=request.getContextPath()%>/images/unstructure.png" width="300" height="300" style="border: #d7d7d7 solid 3px"">
	</div>
	<div align="center" style="margin:30px;"><button type="button" class="btn btn-primary btn-lg btn-block unstructured_begin" style="width:300px;padding:6px 8px">开始</button></div>

</div>
<div class="fa" style="width:420px;height:400px;margin:-250px 0 0 0px;">
	<div align="center" style="margin:30px;font-weight: bold;font-size:25px;font-family: 黑体">结构化数据采集</div>
		<div style="width:100%;height:300px;" align="center">
			<img alt="" src="<%=request.getContextPath()%>/images/structure.png" width="300" height="300">
		</div>
		<div align="center" style="margin:30px;"><button type="button" class="btn btn-primary btn-lg btn-block structured_begin" style="width:300px;padding:6px 8px;background-color:#fff;color:#43c400;border:#43c400 2px solid; ">开始</button></div>
</div>

</div>
</body>
</html>