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
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.css" rel="stylesheet">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/Jrquest.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/HdfsUtil.js"></script>
</head>
<style>
.left_down_file{padding:20px;}
.left_down_file div{margin-top:10px}
.modal-footer .btn{float:left}
</style>
<script type="text/javascript">
$(document).ready(function() { 
	$(".head_fx").find(".fx img").each(function(){
		var src = $(this).attr("src");
		var filename = src.substr(src.lastIndexOf("/")+1);
		if(filename=="wenjianjia.png"){
			var newFilename = filename.replace(".png","_1.png");
			var newSrc = src.substr(0,src.lastIndexOf("/")+1)+newFilename;
			$(this).attr("src",newSrc);
		}
	});
});
</script>
<body>
<form method="post" action="${pageContext.request.contextPath }/editTxt2Hdfs.xhtml" id="file_form_e">
<div class="action-box">
<!-- 另存为 -->
<div class="modal fade" id="myModal12" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">另存为<span id="file_dn"></span></h4>
                </div>
                <div class="modal-body">
					<div class="input-group-addon" id="sizing-addon1">编辑的文件为</div>
                    <div class="input-group input-group-lg">
					  <input type="text" value="${fileinfo.filepath}" id="editfilename" class="form-control" placeholder="请输入文件名" aria-describedby="sizing-addon1">
					</div>
					<div class="input-group-addon" id="sizing-addon1">输入新的文件保存路径</div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button"  id="editfilename_save" class="btn btn-primary">保存</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<div class="mainbody">
		<%@ include file="head.jsp"%>
		<div class="maincontent">
			<div style="margin:30px 0 30px 0px">
		        <div style="height:20px;font-size:20px;" ><strong>&nbsp;&nbsp;知识库</strong></div>
			</div>
        <div class="seachBar clearfix">
            <div class="locationAdd fl"><a href="#" onClick="WindowOpener.jumpRoot()">Home</a>
            	<span id="into_dir"><c:forEach items = "${dir}" var = "b" varStatus="status">/<a href="#" onClick="WindowOpener.jump4recursion(this)">${b}</a></c:forEach><a href="#">/${downfilename}</a></span>
            </div>
        </div>
        	<input type="hidden" value="${fileinfo.filename}" name="downfilename" id="current_downfile" />
        	<input type="hidden" value="${fileinfo.filepath}" name="downfilepath" id="current_downdir" />
	        <div>
	        	<div>
	        		<div style="border: 1px solid #ababab;background-color:#f5f5f5;padding:20px">
	        			<div style="padding:10px">内容</div>
	        			<textarea rows="13" style="width:98%" name="filecontent">${fileinfo.filecontent}</textarea>
	        			<div class="modal-footer">
		                    <button type="submit" class="btn btn-primary" id="save_down_content">保存</button>
		                    <button type="button" class="btn btn-default" id="save_down_as">另存为</button>
		                </div>
	        		</div>
	        	</div>
	        </div>
    </div>
</div>
</div>
</form>
</body>
</html>
