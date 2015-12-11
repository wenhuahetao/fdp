<%@ page contentType="text/html; charset=gb2312" %>
<!DOCTYPE>
	<html style="overflow:hidden;">
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
			<title>拥堵路况信息</title>
		<link rel="stylesheet" href="css/bootstrap.css"/>
		<link rel="stylesheet" href="js/include/ui-1.10.0/ui-lightness/jquery-ui-1.10.0.custom.min.css" type="text/css" />
		<!--link rel="stylesheet" href="css/jquery-ui-1.10.0.custom.css"/-->
		<!--<link rel="stylesheet" href="js/jquery.ui.timepicker.css"/>-->
		<!--<link rel="stylesheet" href="css/jquery.timepicker.css"/>-->
		
		<script type="text/javascript" src="js/jquery-1.10.2.js"></script>
		<script type="text/javascript" src="js/jquery-ui.js"></script>
		<script type="text/javascript" src="js/jquery-ui-i18n.js"></script>
		<script type="text/javascript" src="js/jquery.ui.timepicker.js"></script>
		<!-- <script type="text/javascript" src="js/theme.js"></script> -->
		<script src="http://33.175.29.33:8090/iserver/iClient/forJavaScript/libs/SuperMap.Include.js"></script>
		<script type="text/javascript">
	$(document).ready(function(){
		init();
		$("#view").val("1");
		getDevice("1","0");
	
	});
	
	var dataCache,style = {
			strokeColor : "#304DBE",
			strokeWidth : 1,
			pointerEvents : "visiblePainted",
			fillColor : "#304DBE",
			fillOpacity : 0.8
		}
	var map, layer,searchMarkerlayer,drawPolygon1,vectorLayer,markerLayer;
	var url = "http://33.175.29.33:8090/iserver/services/map-shaoxing/rest/maps/shaoxing";
	function init() {

		map = new SuperMap.Map("map", {
			controls : [ 
                    new SuperMap.Control.ScaleLine(),//比例尺控件
					new SuperMap.Control.Navigation()//此控件处理伴随鼠标事件（拖拽，双击、鼠标滚轮缩放）的地图浏览
			], 
			units: "m",
			allOverlays:true
		});
		map.addControl(new SuperMap.Control.MousePosition());//该控件显示鼠标移动时，所在点的地理坐标
		layer = new SuperMap.Layer.TiledDynamicRESTLayer("绍兴地图", url, null,
				{
					maxResolution : "auto"
				});
		layer.events.on({
			"layerInitialized" : addLayer
		});
		
		//markerLayer = new SuperMap.Layer.Markers("Markers");
		//searchMarkerlayer = new  SuperMap.Layer.Markers("搜索图层");
		
	}
	
	function addLayer() {
		map.addLayer(layer);
		var url1 = 'http://33.175.29.33:8090/iserver/services/map-shaoxing/rest/maps/TrafficIndex_layer';
		layer_s = new SuperMap.Layer.TiledDynamicRESTLayer("TrafficIndex_layer", url1, {transparent:true,cacheEnable:false},{maxResolution : "auto"});
		layer_s.isBaseLayer=false;
		layer_s.events.on({"layerInitialized" : function(){
			map.addLayer(layer_s);
			var url2='http://33.175.29.33:8090/iserver/services/map-shaoxing/rest/maps/shaoxing_label';
			layer_t = new SuperMap.Layer.TiledDynamicRESTLayer("TrafficIndex_layer", url2, {transparent:true,cacheEnable:true},{maxResolution : "auto"});
			layer_t.isBaseLayer=false;
			layer_t.events.on({"layerInitialized" : function(){
				map.addLayer(layer_t);
			}});
		}});
		map.setCenter(new SuperMap.LonLat(120.58262425281, 30.00030159373), 0);
		var defScale = 0.000007580842517627697;
		map.zoomToScale(defScale,true);
	} 

	
	function viewEntire() {
     	map.zoomToMaxExtent();
    }
    
    function zoomIn() {
    	map.zoomIn();
    }
    
    function zoomOut() {
    	map.zoomOut();
    }
        
        
	function changeView(){
		searchMarkerlayer.clearMarkers();
        map.removeAllPopup();
		if($("#view").val() == "0"){
			changeDeviceType();
		}else if($("#view").val() == "1"){
			changeAlarmType();
		}
	}
	
	function changeDeviceType(){
		$("#type option").remove();
		$("#type").append("<option value =\"0\">1</option>");
		for(var i=0;i<dataCache.length;i++){
    		$("#type").append("<option value =\""+dataCache[i].id+"\">"+dataCache[i].name+"</option>");
    	}
    	getDevice("0","0");
	}
	
	function changeAlarmType(){
		$("#type option").remove();
		$("#type").append("<option value =\"1\">2</option>");
		
	}
	
	function changeType(){
		searchMarkerlayer.clearMarkers();
        map.removeAllPopup();
		getDevice($("#view").val(),$("#type").val());
	}
	
	function getIcon(view,dmt){
		var path = "<c:url value ='/pages/gs/images/gis'/>";
		if(view == "0"){
			path = path+"/"+dmt.deviceTypeCode+"_device.png";
		}else if(view == "1"){
         	path = path+"/lz.png";
		}
		return path;
	}
	
	function getDevice(view,type){
		$.post("<c:url value ='/operation/findGisedDevice'/>",{},function(data){
			for(var i=0;i<data.length;i++){
				var dmt = data[i];
				var size = new SuperMap.Size(44,40);
	       		var offset = new SuperMap.Pixel(-(size.w/2), -size.h);
	       		var icon = new SuperMap.Icon(getIcon(view,dmt), size, offset);
	            var marker = new SuperMap.Marker(new SuperMap.LonLat(dmt[1],dmt[2]),icon) ;
	            var lonlat = marker.getLonLat();
	            marker.dmt = dmt;
	            marker.view = view;
	            marker.events.on({
		           "mouseover":openInfoWin,
		           "mouseout":closeInfoWin,
		           "click":viewDevice,
		           "scope": marker
		        });
	
		        searchMarkerlayer.addMarker(marker);
	        }
		},'json');
	}
	
	//打开对应的信息框
    var infowin = null;
	function viewDevice(){
		closeInfoWin();
		var marker = this;
	    var dmt = marker.dmt;
	    var lonlat = marker.getLonLat();
		map.setCenter(new SuperMap.LonLat(lonlat.lon,lonlat.lat), 0);
		map.zoomToScale(0.00011596125086294817,true); 

	}
	
	function creatContentHTML(dmt){
		 var contentHTML = "<div style=\'font-size:.8em; opacity: 0.8; overflow-y:hidden;\'>";
         contentHTML += "<div>3："+dmt[0]+"</div>";
         contentHTML += "<div>4："+dmt[1]+"</div>";
         contentHTML += "<div>5："+dmt[2]+"</div>";
         contentHTML += "</div>";
         return contentHTML;
	}

	
     function openInfoWin() {
         closeInfoWin();
         var marker = this;
         var dmt = marker.dmt;
         var view = marker.view;
         var lonlat = marker.getLonLat();
         var size = new SuperMap.Size(0, 40);
         var offset = new SuperMap.Pixel(0, -size.h);
         var icon = new SuperMap.Icon(getIcon(view,dmt), size, offset);
         var popup = new SuperMap.Popup.FramedCloud("popwin",
                 new SuperMap.LonLat(lonlat.lon,lonlat.lat),
                 null,
                 creatContentHTML(dmt),
                 icon,
                 false);
         infowin = popup;
         map.addPopup(popup);
     }
     //关闭信息框
     function closeInfoWin(){
         if(infowin){
             try{
                 infowin.hide();
                 infowin.destroy();
             }
             catch(e){}
         }
     }
        
	function expandWin(){
	 	$("#maximizeDiv").hide();
	 	$("#searchDiv").css("width","auto");
	 	$("#minimizeDiv").show();
	}
	
	function unExpandWin(){
		$("#maximizeDiv").show();
		$("#searchDiv").css("width","24px");
		$("#minimizeDiv").hide();
	}
	
	</script>
		
</head>


		
<body style="overflow:hidden;">				
	<div class="row">
		<div id="map" style="height:700px">
			<a id="viewEntire" onClick="viewEntire()" title="全幅显示"></a>
			<a id="zoomIn" onClick="zoomIn()" title="放大"></a>
			<a id="zoomOut" onClick="zoomOut()" title="缩小"></a>
		</div>											
	</div>
		

		<script type="text/javascript">	
			var themeUrl = "http://33.175.29.33:8090/iserver/services/map-shaoxing/rest/maps/merge_R@shaoxing2014120210";	
			//function initmap(){
				
			//}
			
			$.ajax({
				url: 'http://33.175.29.24/interface/services/induced/getCongestionTime?jsonpName=callback',
				type: 'GET',
				dataType: 'jsonp',
				jsonpCallback:'callback',
				success: function(result) {
				
				
				
				//createTheme(result,'','');	

					},
				error:function(jqXHR, textStatus, errorThrown){   
				if(textStatus=="timeout"){  

						$("#tips").html("加载超时，请重试"); 
				  
					 }
					 else{  
	
						alert(textStatus);
						  
					 }  
				 },  

				timeout:20000

				});

						
			//createTheme('2015-04-27 16:37:39','','');
			

			
		</script>			
</body>

</html>
