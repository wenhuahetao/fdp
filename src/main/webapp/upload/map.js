	var dataCache,style = {
			strokeColor : "#304DBE",
			strokeWidth : 3,
			pointerEvents : "visiblePainted",
			fillColor : "#304DBE",
			fillOpacity : 0.8
	};
	var local, map, layer, vectorLayer, markerLayer, drawPoint, nodeArray = [], pathTime, i = 0, j = 0,isMap=0;
	var searchMarkerlayer,drawPolygon1;
	var url = "http://33.175.29.33:8090/iserver/services/map-shaoxing/rest/maps/shaoxing";
	var url2 = "http://33.175.29.33:8090/iserver/services/transportationAnalyst-shaoxing/rest/networkanalyst/BuildNetwork@shaoxing20141202";
	function init() {
		map = new SuperMap.Map("map", {
			controls : [ 
                    new SuperMap.Control.ScaleLine(),//比例尺控件
					new SuperMap.Control.Navigation()]//此控件处理伴随鼠标事件（拖拽，双击、鼠标滚轮缩放）的地图浏览
		});
		map.addControl(new SuperMap.Control.MousePosition());//该控件显示鼠标移动时，所在点的地理坐标
		layer = new SuperMap.Layer.TiledDynamicRESTLayer("绍兴地图", url, null,
				{
					maxResolution : "auto"
				});
		layer.events.on({
			"layerInitialized" : addLayer
		});
		 //初始化图层
	    vectorLayer = new SuperMap.Layer.Vector();
		markerLayer = new SuperMap.Layer.Markers("Markers");
		searchMarkerlayer = new  SuperMap.Layer.Markers("搜索图层");
	}
	function mouseClickHandler(event) {
        closeInfoWin();
        var marker = this;
        var dtm = marker.dmt;
        var lonlat = marker.getLonLat();
        
        var contentHTML=creatContentHTML(dtm);
      //初始化FramedCloud类
		framedCloud = new SuperMap.Popup.FramedCloud("chicken", marker
				.getLonLat(), null, contentHTML, icon, true, null, true);

		infowin = framedCloud;
		map.addPopup(framedCloud);
    }
	function addLayer() {
		map.addLayers([layer,vectorLayer]);
		map.addLayers([layer,searchMarkerlayer,markerLayer]);
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
	
	//打开对应的信息框
    var infowin = null;
	
	function viewDevice(event){
		closeInfoWin();
		var marker = this;
	    var dmt = marker.dmt;
	    var lonlat = marker.getLonLat();
		map.setCenter(new SuperMap.LonLat(lonlat.lon,lonlat.lat), 0);
		map.zoomToScale(0.00011596125086294817,true); 

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
	 function findPath() {
	        var findPathService, parameter, analystParameter, resultSetting;
	        resultSetting = new SuperMap.REST.TransportationAnalystResultSetting({
	            returnEdgeFeatures: true,
	            returnEdgeGeometry: true,
	            returnEdgeIDs: true,
	            returnNodeFeatures: true,
	            returnNodeGeometry: true,
	            returnNodeIDs: true,
	            returnPathGuides: true,
	            returnRoutes: true
	        });
	        analystParameter = new SuperMap.REST.TransportationAnalystParameter({
	            resultSetting: resultSetting,
	            weightFieldName: "SmLength"
	        });
	        parameter = new SuperMap.REST.FindPathParameters({
	            isAnalyzeById: false,
	            nodes: nodeArray,
	            hasLeastEdgeCount: false,
	            parameter: analystParameter
	        });
	        
	        findPathService = new SuperMap.REST.FindPathService(url2, {
	            eventListeners: { "processCompleted": processCompleted }
	        });

	        findPathService.processAsync(parameter);
	    }
		
	    function processCompleted(findPathEventArgs) {
	    	
	    	var result = findPathEventArgs.result;
	        allScheme(result);
	    }
	    function allScheme(result) {
	        if (i < result.pathList.length) {
	            addPath(result);
	        } else {
	            i = 0;
	            //线绘制完成后会绘制关于路径指引点的信息
	            addPathGuideItems(result);
	        }
	    }
	    
	  //以动画效果显示分析结果
	    function addPath(result) {
	        if (j < result.pathList[i].route.components.length) {
	            var pathFeature = new SuperMap.Feature.Vector();
	            var points = [];
	            for (var k = 0; k < 2; k++) {
	                if (result.pathList[i].route.components[j + k]) {
	                    points.push(new SuperMap.Geometry.Point(result.pathList[i].route.components[j + k].x, result.pathList[i].route.components[j + k].y));
	                }
	            }
	            var curLine = new SuperMap.Geometry.LinearRing(points);
	            pathFeature.geometry = curLine;
	            pathFeature.style = style;
	            vectorLayer.addFeatures(pathFeature);
	            //每隔0.001毫秒加载一条弧段
	            pathTime = setTimeout(function () { addPath(result); }, 0.00000001);
	            j++;
	        } else {
	            clearTimeout(pathTime);
	            j = 0;
	            i++;
	            allScheme(result);
	        }
	    }