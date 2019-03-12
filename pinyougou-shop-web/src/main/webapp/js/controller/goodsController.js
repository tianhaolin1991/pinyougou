app.controller('goodsController', function($scope, $controller, $location, goodsService, itemCatService,
	typeTemplateService,
	fileService) {

	$controller('baseController', {
		$scope: $scope
	}); //继承

	//变量部分

	//goods:id,sellerId,goodsName,defaultItemId,auditStatus,brandId,caption
	//goods:category1Id,category2Id,category3Id,price,typeTemplateId,isEnableSpec
	//goodsDesc:goodsId,introduction,specificationItems,customAttributeItems,itemImages(商品的图片)
	//   specificationItems(规格结果集):[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}]
	//   customAttributeItems(扩展属性):[{"text":"内存大小","value":"10M"}]
	//item:id,title,price,num,image,status,isDefault,goodsId,sellerId,category,brand,spec
	//	 
	$scope.abc = [];
	$scope.entity = {
			goods: {},
			goodsDesc: {
				specificationItems: [],
				itemImages: [],
				customAttributeItems: [],
				idEnableSpec: 0
			},
			itemList: []
		},
	//初始化商品规格
	//itemCat:id,name
	$scope.categoryList1 = [];
	$scope.categoryList2 = [];
	$scope.categoryList3 = [];
	//商品所选的模板
	$scope.typeTemplate = {};
	//所选模板对应的品牌
	$scope.brandList = [];
	//对应上传的图片url
	$scope.itemImage = {
		color: null,
		url: null
	};
	//搜索条件
	$scope.searchEntity = {
		auditStatus: null,
		goodsName: null
	};
	//商品管理中的status
	$scope.goodsStatus = ['未审核', '已审核', '审核未通过', '已关闭']


	//<!--  函数部分 -->

	//商品分类选项卡
	$scope.findByParentId = function(parentId) {
		itemCatService.findItemsByParentId(parentId).success(
			function(response) {
				for (var i = 0; i < response.length; i++) {
					$scope.categoryList1 = response;
					$scope.categoryList2 = [];
				}
			}
		)
	}
	$scope.$watch('entity.goods.category1Id', function() {
		$scope.categoryList3 = [];
		itemCatService.findItemsByParentId($scope.entity.goods.category1Id).success(
			function(response) {
				$scope.categoryList2 = response;
			}
		)
	})
	$scope.$watch('entity.goods.category2Id', function() {
		itemCatService.findItemsByParentId($scope.entity.goods.category2Id).success(
			function(response) {
				$scope.categoryList3 = response;
			}
		)
	})
	$scope.$watch('entity.goods.category3Id', function() {
		itemCatService.findOne($scope.entity.goods.category3Id).success(
			function(response) {
				typeTemplateService.findOne(response.typeId).success(
					function(data) {
						$scope.typeTemplate.specIds = JSON.parse(data.specIds);
						$scope.brandList = JSON.parse(data.brandIds);
						$scope.entity.goods.typeTemplateId = data.id;
						if ($location.search()['id'] == null) {
							$scope.entity.goodsDesc.customAttributeItems = JSON.parse(data.customAttributeItems);
						}
						$scope.findSpecList()
					}
				)
			}
		)
	});
	//根据specId查找specOption
	$scope.findSpecList = function() {
		typeTemplateService.findSpecList($scope.entity.goods.typeTemplateId).success(
			function(response) {
				$scope.specificationItems = response;
			})
	}

	//保存商品
	$scope.save = function() {
		console.log($scope.entity.goods.id);
		if ($scope.entity.goods.id) {
			var method = goodsService.update($scope.entity);
		} else {
			var method = goodsService.add($scope.entity);
		}
		$scope.entity.goodsDesc.introduction = editor.html();
		method.success(
			function(response) {
				if (response.success) {
					alert(response.message);
					location.href = "goods.html";
				} else {
					alert(response.message);
				}
			}
		)
	}

	//图片商品列表及上传图片//删除
	$scope.upload = function() {
		fileService.upload().success(
			function(response) {
				$scope.itemImage.url = response.message;
			}
		)
	}
	$scope.saveImage = function() {
		$scope.entity.goodsDesc.itemImages.push($scope.itemImage);
	}
	$scope.removeImage = function($index) {
		$scope.entity.goodsDesc.itemImages.splice($index, 1);
	}

	//更新specificationItems数据
	$scope.updateSpecificationItems = function($event, key, value) {
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', key);

		if (object != null) {
			if ($event.target.checked) {
				object.attributeValue.push(value);
			} else {
				object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
				if (object.attributeValue.length == 0) {
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
				}
			}
		} else {
			$scope.entity.goodsDesc.specificationItems.push({
				"attributeName": key,
				"attributeValue": [value]
			})

		}
	}

	//更新$scope.entity.itemList数据
	$scope.updateItemList = function() {

		$scope.entity.itemList = [{
			'spec': {},
			'price': 0,
			'num': 99999,
			'status': 0,
			'isDefault': 0
		}];
		var item = $scope.entity.goodsDesc.specificationItems;
		for (var i = 0; i < item.length; i++) {
			$scope.entity.itemList = $scope.addColumn($scope.entity.itemList, item[i].attributeName, item[i].attributeValue);

		}
	}
	$scope.addColumn = function(list, optionName, optionValues) {
		var newList = [];
		for (var i = 0; i < list.length; i++) {
			var oldRow = list[i];
			for (var j = 0; j < optionValues.length; j++) {
				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[optionName] = optionValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}


	//<!-----以下是商品管理功能 ----->
	//根据条件查询goodsList
	$scope.search = function(page, row) {
		goodsService.search(page, row, $scope.searchEntity).success(
			function(response) {
				$scope.goodsList = response.rows;
				$scope.paginationConf.totalItems = response.total;
			}
		)
	}

	//查询一个商品的所有信息
	$scope.findOne = function() {
		var id = $location.search()['id'];
		if (id) {
			goodsService.findOne($location.search()['id']).success(
				function(response) {
					$scope.entity = response;
					editor.html(response.goodsDesc.introduction);
					$scope.entity.goodsDesc.itemImages = JSON.parse(response.goodsDesc.itemImages);
					$scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.goodsDesc.customAttributeItems);
					$scope.entity.goodsDesc.specificationItems = JSON.parse(response.goodsDesc.specificationItems)
					for (var i = 0; i < response.itemList.length; i++) {
						$scope.entity.itemList[i].spec = JSON.parse(response.itemList[i].spec);
					}
				})
		}
	}
	//显示checkbox状态
	$scope.checkedItem = function(specName, optionName) {
		var item = $scope.entity.goodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(item, 'attributeName', specName);
		if (object != null) {
			if (object.attributeValue.indexOf(optionName) >= 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
})
