app.controller('baseController', function ($scope) {

    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    $scope.paginationConf = {
        currentPage: 1,//当前页,默认为1,点击页码会刷新值
        totalItems: 10,//数据总条数
        itemsPerPage: 10,//当前页数据条数
        perPageOptions: [10, 20, 30, 40, 50],//每页条数可选项
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    $scope.selectIds = [];

    $scope.updateSelect = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1);
        }
    };

    $scope.jsonToString = function(jsonString,key){
        var json = JSON.parse(jsonString);
        var value = '';
        for(var i=0 ; i<json.length;i++){
            if(i==0){
                value += json[i][key];
            }else{
                value += ',' +json[i][key];
            }
        }
        return value;
    }
	
	//遍历数组的属性(key),看该属性是否存在于数组中,常用于查看一个json格式的字符串中是否有某个'属性'
	$scope.searchObjectByKey = function(list,key,value){
		for(var i = 0 ; i<list.length; i++){
			if(list[i][key] == value){
				return list[i];
			}
		}
		return null;
    }

})