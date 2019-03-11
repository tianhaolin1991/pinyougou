//控制层 
 app.controller('goodsController', function($scope, $controller,$location,goodsService, fileService , itemCatService,typeTemplateService) {

 	$controller('baseController', {
 		$scope: $scope
 	}); //继承

 	//读取列表数据绑定到表单中  
 	$scope.findAll = function() {
 		goodsService.findAll().success(
 			function(response) {
 				$scope.list = response;
 			}
 		);
 	}

 	//分页
 	$scope.findPage = function(page, rows) {
 		goodsService.findPage(page, rows).success(
 			function(response) {
 				$scope.list = response.rows;
 				$scope.paginationConf.totalItems = response.total; //更新总记录数
 			}
 		);
 	}

 	//查询实体 
 	$scope.findOne = function() {
		var id = $location.search()['id'];
 		goodsService.findOne(id).success(
 			function(response) {
				$scope.goods = response;
				editor.html(response.tbGoodsDesc.introduction);
				$scope.goods.tbGoodsDesc.itemImages = JSON.parse($scope.goods.tbGoodsDesc.itemImages);
				$scope.typeTemplate.customAttribute = JSON.parse($scope.goods.tbGoodsDesc.customAttributeItems);
				$scope.goods.tbGoodsDesc.specificationItems = JSON.parse($scope.goods.tbGoodsDesc.specificationItems);
				for(var i =0 ; i < $scope.goods.tbItems.length; i++){
					$scope.goods.tbItems[i].spec = JSON.parse($scope.goods.tbItems[i].spec)
				}
 			}
 		);
 	}

     
 	//保存 
 	$scope.add = function() {

 		$scope.goods.tbGoodsDesc.introduction = editor.html();
		$scope.goods.tbGoodsDesc.customAttributeItems = $scope.typeTemplate.customAttribute;
 		goodsService.add($scope.goods).success(
 			function(response) {
 				if (response.success) {
 					//重新查询 
 					alert('添加成功')
 					$scope.goods = {tbGoods:{},tbGoodsDesc:{itemImages:[]}}; //重新加载
					$scope.typeTemplate={customAttribute:[],brandIds:[]};
 					editor.html('');
					
 				} else {
 					alert('添加失败!');
 				}
 			}
 		);
 	}


 	//批量删除 
 	$scope.dele = function() {
 		//获取选中的复选框			
 		goodsService.dele($scope.selectIds).success(
 			function(response) {
 				if (response.success) {
 					$scope.reloadList(); //刷新列表
 					$scope.selectIds = [];
 				}
 			}
 		);
 	}

 	$scope.searchEntity = {}; //定义搜索对象 

 	//搜索
 	$scope.search = function(page, rows) {
 		goodsService.search(page, rows, $scope.searchEntity).success(
 			function(response) {
 				$scope.list = response.rows;
 				$scope.paginationConf.totalItems = response.total; //更新总记录数
 			}
 		);
 	}
	
	//为category设置名称
	$scope.categoryList = [];
	$scope.findItemsCatList = function(){
		itemCatService.findAll().success(
			function(response){
				for(var i=0;i<response.length;i++){
					$scope.categoryList[response[i].id] = response[i].name;
				}
			}
		)
	}

	//$scope.img_entity={};
 	$scope.upload = function() {
		fileService.upload().success(
			function(response) {
				if (response.success) {
					$scope.img_entity.url = response.message;
				} else {
					alert(response.message);
				}
		})
 	}

	$scope.goods = {tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[],tbItems:[]}};
	$scope.updateImgList= function(){
		$scope.goods.tbGoodsDesc.itemImages.push({color:$scope.img_entity.color,url:$scope.img_entity.url});
	}
	$scope.spliceImgList = function(index){
		$scope.goods.tbGoodsDesc.itemImages.splice(index,1);
	}
	
	
	

	//实现3级下拉框同步更新:1级变2级可选内容就变,选择2级3级可选内容就变
	$scope.grade1List = [];
	$scope.grade2List = [];
	$scope.grade3List = [];
	$scope.findItemsByParentId = function(){
		itemCatService.findItemsByParentId(0).success(
			function(response){
				$scope.grade1List = response;
				$scope.grade2List = [];
				$scope.grade3List = [];
			}
		)
	}
	
	$scope.$watch('goods.tbGoods.category1Id',function(newValue,oldValue){
		
		itemCatService.findItemsByParentId(newValue).success(
			function(response){
				$scope.grade2List = response;
				$scope.grade3List = [];
			}
		)
	})
	
	$scope.$watch('goods.tbGoods.category2Id',function(newValue,oldValue){
		itemCatService.findItemsByParentId(newValue).success(
			function(response){
				$scope.grade3List = response;
			}
		)
	})
	
	$scope.typeTemplate = {customAttribute:[],brandIds:[]}
	$scope.$watch('goods.tbGoods.category3Id',function(newValue,oldValue){
		itemCatService.findOne(newValue).success(
		function(response){
			$scope.goods.tbGoods.typeTemplateId = response.typeId;
			typeTemplateService.findOne(response.typeId).success(
				function(data){
					$scope.typeTemplate.brandIds = JSON.parse(data.brandIds);
					if(data.customAttributeItems){
						if($location.search()['id']==null){
							$scope.typeTemplate.customAttribute = JSON.parse(data.customAttributeItems);
						}
					}
				}
			)
			$scope.findSpecList()
		})
	})
	
	$scope.findSpecList = function(){
		typeTemplateService.findSpecList($scope.goods.tbGoods.typeTemplateId).success(
			function(response){
				$scope.specificationItems = response;
			}
		)
	}
	
	$scope.goods.tbGoodsDesc.specificationItems=[];
	$scope.updateSpecItem = function($event,key,value){
		var object = $scope.searchObjectByKey($scope.goods.tbGoodsDesc.specificationItems,'attributeName',key);
		if(object != null){
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);
				if(object.attributeValue.length==0){
					$scope.goods.tbGoodsDesc.specificationItems.splice($scope.goods.tbGoodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.goods.tbGoodsDesc.specificationItems.push({'attributeName':key,'attributeValue':[value]});
		}
	}
	
	
	$scope.createItemList = function(){
		//初始化
		$scope.goods.tbItems = [{'spec':{},'price':0,'num':99999,'status':0,'isDefault':0}];
		var items = $scope.goods.tbGoodsDesc.specificationItems;
		
		//每个columName都调用addColumn方法,不断更新tbItems
		for(var i = 0;i<items.length;i++){
			$scope.goods.tbItems = addColumn($scope.goods.tbItems,items[i].attributeName,items[i].attributeValue);
		}
	}
	//增加一列,并增加所有的对应值,返回newList
	addColumn = function(list,columnName,columnValues){
		var newList=[];
		for(var i = 0 ; i < list.length ; i++){
			var oldRow = list[i];
			for(var j = 0;j < columnValues.length;j++){
				//深克隆
				newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columnName] = columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
	
	$scope.checkOption = function(specName,optionName){
		var object = $scope.searchObjectByKey($scope.goods.tbGoodsDesc.specificationItems,"attributeName",specName);
		//alert(JSON.stringify(object))
		if(object !=null){
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
 });
