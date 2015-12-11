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
.clear{clear: both}
</style>
<body>
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
<div style="border:1px solid #e5e5e5;margin:10px 20px 0 20px;height:650px">
	<div class="head" style="border-bottom:1px solid #e5e5e5"  >
		<div style="width:100px;margin:5px 10px 5px 20px;float:left">
			<img src="<%=request.getContextPath()%>/images/logo_03.png" style="width:100px"/>
		</div>
		<div >
			<h2 style="display:inline-block;"><strong>大数据云服务平台</strong></h2>
		</div>
	</div>
	<div class="maincontent" style="margin-top:20px;margin-bottom:30px;">
		<div style="border-bottom:1px solid #e5e5e5;" >
			<h2 style="margin:20px">非结构化数据采集</h2>
		</div>
		<div>
			<div class="alert alert-success" style="float:left width:80%;margin:10px">
				共导入<font style="color:#ffa500">${successedCount}</font>个文件至${dirname}目录中
			</div>
		</div>
		   <div class="seachBar clearfix">
            <div class="locationAdd fl"><img src="images/home.png" style='width:16px;height:16px;' alt="" class="sLogo fl"/><a href="#"  onClick="WindowOpener.jumpRoot()" >Home</a>&nbsp;&nbsp;
            	<span id="into_dir"><c:forEach items = "${dir}" var = "b" varStatus="status">/<a href="#" onClick="WindowOpener.jump4recursion(this)">${b}</a></c:forEach></span>
                <!-- a href="">Home</a><span>/<a href="">user</a>/<a href="">lee</a>/<a href="">text</a></span> -->
            </div>
            
        </div>
		<table class="filesTable">
            <tr>
                <th><input type="checkbox" id="checkedall"/></th>
                <th>类型</th>
                <th>名称</th>
                <th>大小</th>
                <th>用户</th>
                <th>组</th>
                <th>权限</th>
                <th>创建日期</th>
                <th>创建方式</th>
            </tr>
           <c:forEach items = "${fileinfos}" var = "b" varStatus="status">
            <tr>
                <td><input type="checkbox" name="file_c"/></td>
                <td>
                	<c:if test="${b.filetype=='file'}"><img src="<%=request.getContextPath()%>/images/file.png" alt="" class="slogo"/></c:if>
                	<c:if test="${b.filetype=='dir'}"><img src="<%=request.getContextPath()%>/images/forden.png" alt="" class="slogo"/></c:if>
                	
                </td>
                <td class="nameTd">
                	<input type="hidden" value="${b.filename}" name="" id="nametd_${status.index}" />
                	<input type="hidden" value="${b.filedir}" name="" id="nametddir_${status.index}" />
                	<input type="hidden" value="${b.filetype}" name="" id="filetype_${status.index}" />
                	<c:if test="${b.filetype=='dir'}"><a href="#" onclick="WindowOpener.list(this)">${b.filename}</a></c:if><c:if test="${b.filetype=='file'}"><a href='#' onclick=toCurrent(this)>${b.filename}</a></c:if><c:if test="${!empty qflag}">(<a href="#" onClick="WindowOpener.jumpDetail(this)">${b.filedir}</a>)</c:if>
                </td>
                <td>${b.filesize} kb</td>
                <td>${b.username}</td>
                <td>${b.group}</td>
                <td>${b.authority}</td>
                <td>${b.createDate}</td>
                <td></td>
            </tr>
          </c:forEach>
            <tr>
				<td colspan="9">
				<span>共${total}条&nbsp;<span id='count'>总共${countPage}</span>页&nbsp;</span>
				<span>第${pageIndex}页&nbsp;</span>
				<c:if test="${pageIndex==1}">
					前一页
				</c:if>
				<c:if test="${pageIndex!=1}">
					<a href='javascript:void' onclick='Pager.GoToFirstFormPage(${countPage},${pageIndex})' id='aPrePage' >前一页</a>
				</c:if>
				
				<c:if test="${pageIndex==countPage}">
					下一页
				</c:if>
				<c:if test="${pageIndex<countPage}">
					<a href='javascript:void' onclick='Pager.GoToNextFormPage(${countPage},${pageIndex})' id='aNextPage'>下一页</a>&nbsp;&nbsp;
				</c:if>
				
				</td>
			</tr>
        </table>
	</div>
</div>
</body>



</html>