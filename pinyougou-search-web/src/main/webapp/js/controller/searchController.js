app.controller('searchController',function($scope,$controller,searchService){
    $controller('baseController',{$scope,$scope});//继承

    //变量声明
    $scope.searchMap = {}//keywords,搜索条件
    $scope.resultMap = {}//搜索结果

    //方法,函数
    $scope.search = function(){
        searchService.search($scope.searchMap).success(
            function(response){
                $scope.resultMap = response;
            }
        )
    }
})