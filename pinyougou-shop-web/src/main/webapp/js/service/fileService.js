app.service('fileService',function($http){
	
	this.upload = function(){
		var formdata = new FormData();
		formdata.append('upload',file.files[0]);
		return $http({
			url:'../upload.do',
			method:'post',
			data:formdata,
			headers:{'Content-Type':undefined},
			transformRequest:angular.identity
		})
	}
})