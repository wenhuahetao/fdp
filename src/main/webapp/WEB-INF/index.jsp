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
.navbar-nav button{margin-left:20px}
.navbar .nav > li > a {padding:0px;color: black;font-size:20px}
</style>
<script>
function toCurrent(obj){
	var filename = $(obj).html();
	var dir =  $(obj).parent().find('input:eq(1)').val();
	$("#current_downfile").val(filename);
	$("#current_downdir").val(dir);
	if(filename.indexOf(".txt")>=0){
		$("#file_form_d").attr("action", HdfsUtil.getRootPath() + "/download4txt.xhtml" );
	}else if(filename.indexOf(".doc")>=0){
		$("#file_form_d").attr("action", HdfsUtil.getRootPath() + "/download4doc.xhtml" );
	}
	$("#file_form_d").submit();
}
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
<%@ include file="head.jsp"%>
<div class="action-box">
 <!-- 导入zip -->
<div class="modal fade" id="myModal11" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <form method="post" action="${pageContext.request.contextPath }/uploadZip.xhtml" enctype="multipart/form-data" id="fileForm" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">导入ZIP文件至：<span id="file_import_zip"></span></h4>
                    <div>上传的ZIP文件会被解压至以上路径</div>
                </div>
                <div class="modal-body">
                  <input type="file" name="importzipfilename" />
                  <input type="hidden" name="importzipdirname" id="importzipdirname">
                </div>
                <div style="float:right;margin:10px">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="submit" class="btn btn-primary">保存</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->

 <!-- 导入 -->
<div class="modal fade" id="myModal8" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
     <form method="post" action="${pageContext.request.contextPath }/upload.xhtml" enctype="multipart/form-data" id="fileForm" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">导文件至：<span id="file_import_file"></span></h4>
                </div>
                <div class="modal-body">
                  <input type="file" name="importfilename" />
                  <input type="hidden" name="importdirname" id="importdirname">
                </div>
                <div style="float:right;margin:10px">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="submit" class="btn btn-primary">保存</button>
                </div>
                
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->

<!-- 新建文件夹-->
<div class="modal fade" id="myModal10" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <form method="post" action="${pageContext.request.contextPath }/newDir.xhtml" id="fileForm" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">新建文件夹<span id="file_dn"></span></h4>
                </div>
                <div class="modal-body">
                    <div class="input-group input-group-lg">
					  <span class="input-group-addon" id="sizing-addon1">文件夹名</span>
					  <input type="text" id="newDirname" name="newDirname" class="form-control" placeholder="请输入文件夹名" aria-describedby="sizing-addon1">
					  <input type="hidden" name="mkdirname" id="mkdirname">
					</div>
                </div>
                <div style="float:right;margin:10px">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="submit" class="btn btn-primary">保存</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->
 <!-- 新建文件-->
<div class="modal fade" id="myModal9" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <form method="post" action="${pageContext.request.contextPath }/newFile.xhtml" id="fileForm" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">新建文件<span id="file_dn"></span></h4>
                </div>
                <div class="modal-body">
                    <div class="input-group input-group-lg">
					  <span class="input-group-addon" id="sizing-addon1">文件名</span>
					  <input type="text" id="newfilename" name="newfilename" class="form-control" placeholder="请输入文件名" aria-describedby="sizing-addon1">
					  <input type="hidden" name="newFileDir" id="newFileDir">
					</div>
                </div>
               <div style="float:right;margin:10px">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="submit" class="btn btn-primary">保存</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->

 <!-- 重命名 -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <form method="post" action="${pageContext.request.contextPath }/rename.xhtml" id="fileForm" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">重命名<span id="file_dn"></span></h4>
                </div>
                <div class="modal-body">
                    <div class="input-group input-group-lg">
					  <span class="input-group-addon" id="sizing-addon1">新文件名</span>
					  <input type="text" id="filename" name="filename" class="form-control" placeholder="请输入文件名" aria-describedby="sizing-addon1">
					  <input type="hidden" name="oldname" id="oldname">
					</div>
                </div>
               <div style="float:right;margin:10px">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="submit" class="btn btn-primary">保存</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->
<!-- 移动 -->
<div class="modal fade" id="myModal2" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <form method="post" action="${pageContext.request.contextPath }/move.xhtml"  id="file_Form_m" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">移动</h4>
                </div>
                <div class="modal-body">
                    <div class="input-group input-group-lg">
					  <span class="input-group-addon" id="sizing-addon1">目标文件夹</span>
					  <input type="text" id="name_dir_m" name="movedirname"  class="form-control filenameclass"  readonly="readonly" placeholder="" aria-describedby="sizing-addon1">
					  <button type="button"  data-toggle="modal"  class="btn btn-default showdispalybar" id="showdispaly" >...</button>
					  <input type="hidden" name="movefilename" id="movefilename">
					   <input type="hidden" name="sourcemovedirname" id="sourcemovedirname">
					</div>
                </div>
                <div style="display:none;" id="show_m" class="modal-body">
               	  <div>
              	 	<span id="curent_dir_m"></span>
           		  </div>
           		  <table  id="list_dir_m">
           		  </table>
           		   <div class="modal-footer">
           		     	<button type="button" class="btn btn-primary xz_bar" style="float:left;" id="xz_m" >选择这个文件夹</button>
                    	<button type="button" class="btn btn-default showfilebar" style="float:left;" id="showfile" >创建新的文件夹</button>
           		   </div>
           		    <div class="seachBar clearfix filebar" style="display:none;">
           				 <input type="text" id="dirname_m" class="textStyle fl"/>&nbsp;&nbsp;&nbsp;&nbsp;
           				 <button type="button" class="btn btn-primary dircreate" id="m_dir" >创建文件夹</button>
                    	 <button type="button" class="btn btn-default hidefilebar" id="hidefile" >取消</button>
           			</div>
           		</div>
                 <div style="float:right;margin:10px">
                    <button type="button" class="btn btn-default hidebar" id="hide">取消</button>
                    <button type="button" class="btn btn-primary file_qbar" id="move_file_q">确定</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->
<!-- 复制 -->
<div class="modal fade" id="myModal3" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <form method="post" action="${pageContext.request.contextPath }/copy.xhtml" id="file_Form_c" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">复制</h4>
                </div>
                <div class="modal-body">
                    <div class="input-group input-group-lg">
					  <span class="input-group-addon" id="sizing-addon1">目标文件夹</span>
					  <input type="text" id="name_dir_c" name="copydirname" class="form-control filenameclass" readonly="readonly" aria-describedby="sizing-addon1">
					  <button type="button"  data-toggle="modal"  class="btn btn-default showdispalybar" describe="c" id="showdispaly_copy" >...</button>
					  <input type="hidden" name="copyfilename" id="copyfilename">
					  <input type="hidden" name="sourcecopydirname" id="sourcecopydirname">
					</div>
                </div>
                <div style="display:none;" id="show_c" class="modal-body">
               	  <div>
               	  	<span id="curent_dir_c">
	                </span>
           		  </div>
           		  <table id="list_dir_c">
           		  </table>
           		  
           		   <div class="modal-footer">
           		     	<button type="button" id="xz_c" class="btn btn-primary xz_bar" style="float:left;" >选择这个文件夹</button>
                    	<button type="button" class="btn btn-default showfilebar" style="float:left;" id="showfile_copy" >创建新的文件夹</button>
           		   </div>
           		    <div class="seachBar clearfix filebar" style="display:none;">
           				 <input type="text" id="dirname_c" class="textStyle fl"/>&nbsp;&nbsp;&nbsp;&nbsp;
           				 <button type="button" id="c_dir" class="btn btn-primary dircreate"  >创建文件夹</button>
                    	 <button type="button" class="btn btn-default hidefilebar" id="hidefile_copy" >取消</button>
           			</div>
           		</div>
                <div style="float:right;margin:10px">
                    <button type="button" class="btn btn-default hidebar" id="hide_copy">取消</button>
                    <button type="button" id="file_q_c" class="btn btn-primary file_qbar" >确定</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->
<!--下载-->
<div class="modal fade" id="myModal4" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <form method="post" action="${pageContext.request.contextPath }/download.xhtml" id="fileForm" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">下载</h4>
                </div>
                <div class="modal-body">
                    <div class="input-group input-group-lg">
					  <span class="input-group-addon" id="sizing-addon1">下载到指定目录</span>
					  <input type="text" id="downtodir" value="d:\fxdownload\" name="downtodir" class="form-control" placeholder="请输入本地目录" aria-describedby="sizing-addon1">
					  <input type="hidden" name="downfilename" id="downfilename">
					</div>
                </div>
                <div style="float:right;margin:10px">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" id="down_c" class="btn btn-primary" id="">下载</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->

<div class="modal fade" id="myModal1" tabindex="-1" role="dialog" 
   aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static" style="width:300px"> 
   <div class="modal-dialog">
       <div class="modal-content" >
           <span style="text-align:center;">文件正在下载请勿刷新页面！</span><br />
          
           <div class="progress progress-striped active">
               <div class="bar">
               </div>
           </div>
       </div>
   </div>
</div>

 <!--删除-->
<div class="modal fade" id="myModal5" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <form method="post" action="${pageContext.request.contextPath }/delete.xhtml" id="fileForm" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">确定删除</h4>
                </div>
                <div class="modal-body">
                	<h4 >你确定要删除该文件吗？</h4>
                	<input type="text" name="deletename" id="deletename">
                	<input type="hidden" id="deletdir">
                </div>
                <div style="float:right;margin:10px">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="submit" class="btn btn-primary" id="delete_s">删除</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->
<div class="mainbody">
		<div class="maincontent">
			<div style="margin:30px 0 30px 0px">
		        <div style="height:20px;font-size:20px;" ><strong>&nbsp;&nbsp;知识库</strong></div>
			</div>
        <form method="post" action="${pageContext.request.contextPath }/queryByfilename.xhtml" id="qfileForm" class="form-inline" style="background-color: #f4f4f4;border: 1px solid #d7d7d7;padding:10px"  >
        <div>
            <div style="float:left">
            <input type="text" name="qfilename"  id="qfilename" placeholder="搜索文件名" class="textStyle fl" value="${qfilename}"/>
            <input type="hidden" name="currentDir" id="currentDir_search"/>
            <a href="#" id="q_file_name" onClick="WindowOpener.jumpRoot()"><img src="<%=request.getContextPath()%>/images/sousuo.png" alt="" /></a>
          	</div>
          
            <div style="margin-left:40px;float:left">
	             <button class="btn" data-toggle="modal" id="rename_b" style="background-color: #ebebeb;border: 1px solid #d2d2d2;">
        		A重命名
	            </button>
	        	 <button class="btn" data-toggle="modal" id="move_b">
	        		移动
	            </button>
	             <button class="btn" data-toggle="modal" id="copy_b">
	        		复制
	            </button>
	            <button class="btn" data-toggle="modal"  id="delete_b">
	        		删除
	            </button>
			</div>
			<div style="float:right;margin-left:10px">
			    <div class="btn-group">
					  <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
					  	  导入
					    <span class="caret"></span>
					  </a>
					  <ul class="dropdown-menu">
					     <li><a href="#"  data-toggle="modal"  id="importfile_b"><!-- img src="<%=request.getContextPath()%>/images/sLogo_07.png" width="20px"alt="" class="slogo"/> --> &nbsp;文件</a></li>
			               <li><a href="#"  data-toggle="modal"  id="importzipfile_b"><!-- img src="<%=request.getContextPath()%>/images/sLogo_07.png" width="20px" class="slogo"/> -->&nbsp;zip文件</a></li>
					  </ul>
				 </div>
		   </div>
		   <div style="float:right;">
				<div class="btn-group">
					  <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
					  	  新建
					    <span class="caret"></span>
					  </a>
					  <ul class="dropdown-menu">
					     <li><a href="#" data-toggle="modal"  id="newfile_b"><!-- img src="<%=request.getContextPath()%>/images/sLogo_07.png" width="20px" style="line-height: 18px;"/> --> &nbsp;文件</a></li>
										               <li><a href="#" data-toggle="modal"  id="newdir_b"><!-- img src="<%=request.getContextPath()%>/images/forden.jpg" width="20px" style="line-height: 18px;"/> -->&nbsp;文件夹</a></li>
					  </ul>
				 </div>
			</div>
		   <div style="clear: both"></div>
        </div>
        </form>
        <c:if  test="${successedCount>0}">
        <div>
			<div class="alert alert-success" style="float:left width:80%;margin:10px">
				共导入<font style="color:#ffa500">${successedCount}</font>个文件至${dirname}目录中
			</div>
		</div>
		</c:if>
        <div class="seachBar clearfix">
            <div class="locationAdd fl"><img src="images/home.png" style='width:16px;height:16px;' alt="" class="sLogo fl"/><a href="#"  onClick="WindowOpener.jumpRoot()" >Home</a>&nbsp;&nbsp;
            	<span id="into_dir"><c:forEach items = "${dir}" var = "b" varStatus="status">/<a href="#" onClick="WindowOpener.jump4recursion(this)">${b}</a></c:forEach></span>
                <!-- a href="">Home</a><span>/<a href="">user</a>/<a href="">lee</a>/<a href="">text</a></span> -->
            </div>
            
        </div>
        
        <form method="post" action="${pageContext.request.contextPath }/download.xhtml" id="file_form_d" >
        	<input type="hidden" value="" name="downfilename" id="current_downfile" />
        	<input type="hidden" value="" name="downfiledir" id="current_downdir" />
        </form>
        
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
</div>
</body>
</html>
