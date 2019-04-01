app.service('userService', function ($http) {

    this.register = function (user,checkCode) {
        return $http.post("../user/add.do?checkCode="+ checkCode, user);
    }

    this.createCheckCode = function (phone) {
        return $http.get("../user/createCheckCode.do?phone=" + phone);
    }
})