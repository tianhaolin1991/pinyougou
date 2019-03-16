app.controller('searchController', function ($scope, $controller, searchService) {
    $controller('baseController', { $scope, $scope });//继承

    //变量声明
    $scope.searchMap = { category: '', brand: '', spec: {}, currentPage: 1, pageSize: 30 }//keywords,搜索条件
    $scope.resultMap = {}//搜索结果

    //方法,函数
    $scope.search = function () {
        $scope.searchMap.currentPage = parseInt($scope.searchMap.currentPage)
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                $scope.buildPageLabel();
            }
        )
    }


    $scope.addSearchItem = function (itemName, itemValue) {
        if (itemName == 'category' || itemName == 'brand') {
            $scope.searchMap[itemName] = itemValue;
        } else {
            $scope.searchMap.spec[itemName] = itemValue;
        }
        $scope.search();
    }

    $scope.removeSearchItem = function (itemName) {
        if (itemName == 'category' || itemName == 'brand') {
            $scope.searchMap[itemName] = '';
        } else {
            delete $scope.searchMap.spec[itemName];
        }
        $scope.search();
    }


    $scope.buildPageLabel = function () {
        $scope.pageLabel = [];

        var start = $scope.searchMap.currentPage - 2;
        var end = $scope.searchMap.currentPage + 2;
        if(start < 1){
            start = 1;
            end = 5;
        }
        if (end > $scope.resultMap.totalPages) {
            end = $scope.resultMap.totalPages;
            start = end - 4;
            if(start < 1){
                start = 1;
            }
        }
        for (var i = start; i <= end; i++) {
            $scope.pageLabel.push(i);
        }

    }

    $scope.updateCurrentPage = function (currentPage) {
        $scope.searchMap.currentPage = currentPage;
        $scope.search();
    }

    $scope.keywordsIsBrand = function(){
        for(var i= 0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }

    $scope.addPrice = function(price){
        $scope.searchMap.price = price;
        $scope.search();
    }
    $scope.updateSort = function(sortType,sortField){
        $scope.searchMap.sortType=sortType;
        $scope.searchMap.sortField=sortField;
        $scope.search();
    }
})