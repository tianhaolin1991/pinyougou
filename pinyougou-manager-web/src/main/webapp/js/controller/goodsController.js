//控制层
app.controller('goodsController', function ($scope, $controller,$location, goodsService ,itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //变量部分

    //goods:id,sellerId,goodsName,defaultItemId,auditStatus,brandId,caption
    //goods:category1Id,category2Id,category3Id,price,typeTemplateId,isEnableSpec
    //goodsDesc:goodsId,introduction,specificationItems,customAttributeItems,itemImages(商品的图片)
    //   specificationItems(规格结果集):[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}]
    //   customAttributeItems(扩展属性):[{"text":"内存大小","value":"10M"}]
    //item:id,title,price,num,image,status,isDefault,goodsId,sellerId,category,brand,spec
    //
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
        $scope.searchEntity = {auditStatus: null, goodsName: null};
        //商品管理中的status
        $scope.goodsStatus = ['未审核', '已审核', '审核未通过', '已关闭'];


        //<!--  函数部分 -->

        //商品分类选项卡
        $scope.findByParentId = function (parentId) {
            itemCatService.findItemsByParentId(parentId).success(
                function (response) {
                    for (var i = 0; i < response.length; i++) {
                        $scope.categoryList1 = response;
                        $scope.categoryList2 = [];
                    }
                }
            )
        }
        $scope.$watch('entity.goods.category1Id', function () {
            $scope.categoryList3 = [];
            itemCatService.findItemsByParentId($scope.entity.goods.category1Id).success(
                function (response) {
                    $scope.categoryList2 = response;
                }
            )
        })
        $scope.$watch('entity.goods.category2Id', function () {
            itemCatService.findItemsByParentId($scope.entity.goods.category2Id).success(
                function (response) {
                    $scope.categoryList3 = response;
                }
            )
        })
        $scope.$watch('entity.goods.category3Id', function () {
            itemCatService.findOne($scope.entity.goods.category3Id).success(
                function (response) {
                    typeTemplateService.findOne(response.typeId).success(
                        function (data) {
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
        $scope.findSpecList = function () {
            typeTemplateService.findSpecList($scope.entity.goods.typeTemplateId).success(
                function (response) {
                    $scope.specificationItems = response;
                })
        }


        //<!-----以下是商品管理功能 ----->
        //根据条件查询goodsList
        $scope.search = function (page, row) {
            goodsService.search(page, row, $scope.searchEntity).success(
                function (response) {
                    $scope.goodsList = response.rows;
                    $scope.paginationConf.totalItems = response.total;
                }
            )
        }

        //查询一个商品的所有信息
        $scope.findOne = function () {
            var id = $location.search()['id'];
            if (id) {
                goodsService.findOne($location.search()['id']).success(
                    function (response) {
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

        //修改category1/2/3Id为名称
        $scope.itemsCategoryList = [];
        $scope.findItemCat = function(){
            itemCatService.findAll().success(
                function(response){

                    for(var i=0;i< response.length;i++){
                        $scope.itemsCategoryList[response[i].id] = response[i].name;
                    }

                }
            )
        }

        //删除商品
        $scope.del = function(){
            goodsService.del($scope.selectIds).success(
                function(response){
                    if(response.success){
                        $scope.reloadList();
                    }else{
                        alert(response.message);
                    }
                }
            )
        }

    $scope.checked = function(){
        goodsService.checked($scope.selectIds).success(
            function(response){
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        )
    }

    $scope.rollBack = function(){
        goodsService.rollBack($scope.selectIds).success(
            function(response){
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        )
    }


});	
