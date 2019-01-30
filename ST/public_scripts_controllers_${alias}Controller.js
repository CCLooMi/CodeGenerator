/**
 * Created by ${author} on ${now().format("yyyy-MM-dd HH:mm:ss")}.
 */
angular.module(module)
    .controller('${alias}Ctrl',['$scope','$http','S_${alias}', function ($scope,$http,S_${alias}) {
        $scope.${alias}s=[];
        $scope.${alias}={};
        $scope.add= function () {
            S_${alias}.add($scope);
        };
        $scope.update= function (${alias}) {
            S_${alias}.update($scope,${alias});
        };
        $scope.remove= function (${alias}) {
            S_${alias}.remove($scope,${alias});
        };
    }]);