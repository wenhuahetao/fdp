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
</head>
<style>
.maskpb {
   display: none;  width: 100%; height: 100%;  opacity: 0.6;  filter: alpha(opacity=60);
   background:black; position: absolute; 
   top: 0;  left: 0;  z-index: 20000;
}
.btn-lg{background-color:#ff9000;border:#ff9000 }
</style>
<body>
<div class="maskpb" id="progressBar" >
	<div style="width:100%; margin-top:20%;vertical-align:middle;text-align:center;">
    	<img src="/fline_hdfs/images/ajax-loader-progress.gif">
    </div>
</div>
<%@ include file="../../head.jsp"%>
<div class="maincontent" style="margin-top:20px">
	<div>
		<h2 style="margin:20px"><Strong>结构化数据采集</Strong></h2>
	</div>
	<form class="form-horizontal" method="post" action="${pageContext.request.contextPath }/collection/structurefinshView.xhtml" id="hbaseImportForm">
		<div class="content" style="border:1px solid #d7d7d7;margin-top:30px;margin-right: auto; margin-left: auto; width:98%;background-color:#f4f4f4 ">
			<div style="margin-top:30px;margin-right: auto; margin-left: auto; width:40%;">
				<h2 style="text-align:center"><Strong>数据源配置</Strong></h2>
				<div style="margin-top:40px" class="form-group">
				 	<label  class="col-sm-3 control-label"><font style="color:#0CBB12;font-size:16px"><Strong>数据库类型</Strong></font></label>
				 	<div class="col-sm-9">
						<select  id="dbtype" name="dbtype" class="form-control"  style="width:70%" >
							<option>mysql</option>
							<option>oracle</option>
							<option>sqlserver</option>
						</select>
					</div>
				</div>
				<div style="margin-top:30px" class="form-group">
					<label  class="col-sm-3 control-label"><font style="color:#0CBB12;font-size:16px"><Strong>数据库名</Strong></font></label>
					<div class="col-sm-9">
					 	<input type="text" value="flinebigdata" class="form-control" id="dbname" name="dbname"  style="width:70%"/>
					</div>
				</div>
				<div style="margin-top:30px" class="form-group">
					<label  class="col-sm-3 control-label"><font style="color:#0CBB12;font-size:16px"><Strong>数据连接</Strong></font></label>
					<div class="col-sm-9">
						<input type="text" value="121.40.19.144" class="form-control" id="url" name="connectionurl" style="width:70%"/>
					</div>
				</div>
				<div style="margin-top:30px" class="form-group">
					<label  class="col-sm-3 control-label"><font style="color:#0CBB12;font-size:16px"><Strong>用户名</Strong></font></label>
					<div class="col-sm-9">
					 	<input type="text" value="zhangyue" class="form-control" id="username" name="username" style="width:70%"/>
					</div>
				</div>
				<div style="margin-top:30px;margin-bottom:30px" class="form-group">
					<label  class="col-sm-3 control-label"><font style="color:#0CBB12;font-size:16px"><Strong>密&nbsp;&nbsp;&nbsp;&nbsp;码</Strong></font></label>
					<div class="col-sm-9">
					 	<input type="password" value="123456" class="form-control" id="password" name="password" style="width:70%" />
				    </div>
				</div>
				<div class="form-group" style="margin-right: auto; margin-left: auto;margin-bottom:50px;" >
					<div class="col-sm-3"></div>
					<div class="col-sm-9">
						<button type="button" id="import" class="btn btn-primary btn-lg btn-block" style="width:200px;padding:6px 8px;">开&nbsp;&nbsp;&nbsp;&nbsp;始</button>
					</div>
				</div>
			</div>
		</div>
	</form>
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
	$(document).ready(function() {  
		$("#import").click(function(){
			<%-- $.ajax({type:'POST', url: '<%=request.getContextPath()%>/collection/transRDBIncre2HBASE.xhtml', 
				data:$('#hbaseImportForm').serialize(), 
				success: function(response) {
					alert(1231);
				},
				error: function(response, errorCode) {
					//display error message
					alert(11);
				}
			}); --%>
			$(".maskpb").show();
			$("#hbaseImportForm").submit();
		});
	});
</script>


</html>