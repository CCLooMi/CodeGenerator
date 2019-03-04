/**
 * Created by ${author} on ${now().format("yyyy-MM-dd HH:mm:ss")}.
 */
angular.module(module)
    .factory('S_${alias}',['$http','S_dialog', function ($http,S_dialog) {
        var service={
            add: function (scope) {
            	scope.${alias}={};
                S_dialog.dialog('添加${comment}','views/${alias}/add.html',scope, function () {
                    $http.post('${alias}/add.do',scope.${alias})
                        .then(function successCallback(resp) {
                            var data=resp.data;
                            if(data[0]==0){
                                scope.${alias}.id=data[1];
                                scope.${alias}s.push(scope.${alias});
                                refreshScope(scope);
                                S_dialog.alert('添加成功','成功添加${comment}','success');
                            }else{
                                S_dialog.alert('添加失败',data[1],'error');
                            }
                        });
                })
            },
            update: function (scope,${alias}) {
                var cloneObj=cloneFrom(${alias});
                scope.${alias}=${alias};
                S_dialog.dialog('修改${comment}','views/${alias}/add.html',scope, function () {
                    $http.post('${alias}/update.do',scope.${alias})
                        .then(function successCallback(resp) {
                            var data=resp.data;
                            if(data[0]==0){
                                S_dialog.alert('修改成功','成功修改${comment}','success');
                            }else{
                                S_dialog.alert('修改失败',data[1],'error');
                            }
                        });
                }, function () {
                    cloneA2B(cloneObj,${alias});
                    refreshScope(scope);
                })
            },
            remove: function (scope,${alias}) {
                S_dialog.alertRemove('${alias}/remove.do',${alias}, function () {
                    scope.${alias}s.splice(scope.${alias}s.indexOf(${alias}),1);
                    refreshScope(scope);
                })
            }
        };
        return service;
    }])
