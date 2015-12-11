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
a{color:#43c400}
.main{}
.unstruct_ul{margin:20px 20px 0 20px}
.unstruct_upload{display:none}
.unstruct_source{}
.unstruct_upload{}
.left_mz{color:#339900;width:10%;font-size:16px}
.btn-lg{background-color:#ff9000;border:#ff9000 }
.form-control{width:200px}
.maskpb {
   display: none;  width: 100%; height: 100%;  opacity: 0.6;  filter: alpha(opacity=60);
   background:black; position: absolute; 
   top: 0;  left: 0;  z-index: 20000;
}
</style>
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
	if($("#type_error").val()==1){
		$(".unstruct_source").hide();
    	$(".unstruct_upload").show();
    	$('#unstruct_ul_li_source').removeClass('active');
    	$('#unstruct_ul_li_source_li').addClass('active');
    	$("#unstruct_ul_li_source").find("img").attr("src","<%=request.getContextPath()%>/images/resource_no.png");
    	$("#unstruct_ul_li_upload").find("img").attr("src","<%=request.getContextPath()%>/images/upload_yes.png");
	}
});
$(function(){
	$('li').bind('click',function(){
        $(this).addClass('active');
        $('li').not($(this)).removeClass('active');
	});
	$("#unstruct_ul_li_source").click(function(){
		$(".unstruct_source").show();
    	$(".unstruct_upload").hide();
    	$(this).find("img").attr("src","<%=request.getContextPath()%>/images/resource_yes.png");
    	$("#unstruct_ul_li_upload").find("img").attr("src","<%=request.getContextPath()%>/images/upload_no.png");
	});
	$("#unstruct_ul_li_upload").click(function(){
		$(".unstruct_source").hide();
    	$(".unstruct_upload").show();
    	$("#unstruct_ul_li_source").find("img").attr("src","<%=request.getContextPath()%>/images/resource_no.png");
    	$(this).find("img").attr("src","<%=request.getContextPath()%>/images/upload_yes.png");
	});
	$("#unstruct_source_upload").click(function(){
		$(".maskpb").show();
		var url = CollectionUtil.getRootPath()+'/collection/uploadRemoteFile';
		$("#uploadRemoteFileForm").attr("action",url);
		$("#uploadRemoteFileForm").submit();
	});
	$("#unstruct_file_upload").click(function(){
		if($("#importfilename").val()==''){
			alert("请选择文件");
		}else{
			$(".maskpb").show();
		   // var checkCode= $('input:radio[name="checkCode"]:checked').val();
			var url = CollectionUtil.getRootPath()+'/collection/uploadFile';
			$("#uploadlocalFileForm").attr("action",url);
			$("#uploadlocalFileForm").submit();
		}
		
	});
});



</script>
<body>
<input type="hidden" id="type_error" value="${type}" />
<div class="main">
	<div class="maskpb" id="progressBar" >
		<div style="width:100%; margin-top:20%;vertical-align:middle;text-align:center;">
	    	<img src="/fline_hdfs/images/ajax-loader-progress.gif">
	    </div>
	</div>
	<div class="head" style="border-bottom:1px solid #e5e5e5"  >
		<%@ include file="../../head.jsp"%>
	</div>
	<div>
		<h2 style="margin:20px;font-size:25px;font-family: 黑体">非结构化数据采集</h2>
	</div>
	<div class="unstruct_ul">
		<ul class="nav nav-tabs">
			<li class="active" id="unstruct_ul_li_source"><a href="#"><img src="<%=request.getContextPath()%>/images/resource_yes.png" alt="" />&nbsp;指定数据源</a></li>
			<li id="unstruct_ul_li_source_li"><a href="#" id="unstruct_ul_li_upload"><img src="<%=request.getContextPath()%>/images/upload_no.png" alt="" />&nbsp;上传文件</a></li>
		</ul>
	</div>


<div class="unstruct_source" align="center">
	
	<form class="form-horizontal" role="form" id="uploadRemoteFileForm" method="post" >
	   <div class="form-group">
	   		<div class="col-sm-12" style="margin:50px 0 10px 10px;font-size:20px;font-weight: bold;">配置数据源所在服务器及路径</div>
	   </div>
	   <div class="form-group">
	      <div class="col-sm-12">
	      	<label class="left_mz">服务器</label>
	         <input type="text" class="form-control" id="remoteip" name="remoteip"  value="112.33.1.203"
	            placeholder="请输入服务器IP">
	      </div>
	   </div>
	   <div class="form-group">
	      <div class="col-sm-12"><label class="left_mz">用户名</label>
	         <input type="text" class="form-control" id="remoteusername" name="remoteusername" value="root"
	            placeholder="请输入用户名">
	      </div>
	   </div>
	    <div class="form-group">
	      <div class="col-sm-12"><label class="left_mz">密&nbsp;&nbsp;码</label>
	         <input type="text" class="form-control" id="remotepassword" name="remotepassword" value="Feixian2015"
	            placeholder="请输入密码">
	      </div>
	   </div>
	    <div class="form-group">
	      <div class="col-sm-12"><label class="left_mz">文件路径&nbsp;&nbsp;&nbsp;&nbsp;</label>
	         <input type="text" class="form-control" id="remotefilepath" name="remotefilepath" value="/root/test09/"
	            placeholder="请输入文件路径">
	      </div>
	   </div>
	   <div class="form-group">
	      <div class="col-sm-12">
	       	<button type="button" id="unstruct_source_upload" class="btn btn-primary btn-lg btn-block" style="width:200px;margin-left:30px;padding:6px 8px;">开&nbsp;&nbsp;&nbsp;&nbsp;始</button>
	      </div>
	   </div>
	</form>
</div>

<div class="unstruct_upload" align="center">
	<form role="form" class="form-horizontal" method="post" id="uploadlocalFileForm" enctype="multipart/form-data">
	   <div class="form-group">
	   		<div class="col-sm-12" style="margin:50px 0 10px 10px;font-size:20px;font-weight: bold;">上传文件</div>
	   </div>
	   <div class="form-group">
	      <div class="col-sm-12">
	         <label for="firstname"><span class="left_mz" style="margin-left:100px;padding-right:20px">文件路径</span></label>
	         <label><input type="file" id="importfilename" name="importfilename"></label>
	      </div>
	   </div>
	 
	   <div class="form-group">
	   	  <div class="col-sm-12"><label style="margin-right:198px"><span class="left_mz">文件类型</span></label></div>
	      <div class="col-sm-12">
	         <div id="checkcode_radio"  style="margin-left:280px">
				   <label class="checkbox-inline">
				      <input type="radio" id="checkCode3" value="1" name="checkCode"
				         value="option1" checked> 常规(.doc或.txt后缀文件)
				   </label>
				   <label class="checkbox-inline">
				      <input type="radio" id="checkCode4" value="3" name="checkCode"
				         value="option2"> CSV文件(.csv后缀文件)
				   </label>
				    <label class="checkbox-inline">
				      <input type="radio" id="checkCode4" value="2" name="checkCode"
				         value="option2"> 日志文件(.log后缀文件)
				   </label>
		 	</div>
	      </div>
	    </div>
	    <div class="form-group" align="center" style="width:50%">
	      <div class="col-sm-12">
	       	<button type="button" id="unstruct_file_upload" class="btn btn-primary btn-lg btn-block" style="width:200px;margin-left:20px;padding:6px 8px;">开&nbsp;&nbsp;&nbsp;&nbsp;始</button>
	      </div>
	    </div>
	</form>
	 
</div>
</div>

</body>
</html>