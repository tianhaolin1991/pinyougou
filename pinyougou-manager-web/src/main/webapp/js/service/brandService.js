app.service('brandService', function ($http) {
    //查找所有,不分页
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    }
    //查找所有,分页
    this.findPage = function (pageNum, pageSize) {
        return $http.get('../brand/findPage.do?pageNum=' + pageNum + '&pageSize=' + pageSize);
    }
    //索索,分页
    this.search = function (pageNum, pageSize, searchEntity) {
        return $http.post('../brand/search.do?pageNum=' + pageNum + '&pageSize=' + pageSize, searchEntity);
    }

    //添加brand
    this.add = function (entity) {
        return $http.post('../brand/add.do', entity);
    }
    //修改brand
    this.update = function (entity) {
        return $http.post('../brand/update.do', entity);
    }
    //删除brand
    this.del = function (ids) {
        return $http.get('../brand/delete.do?ids=' + ids);
    }

    //findOne
    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id);
    }

});