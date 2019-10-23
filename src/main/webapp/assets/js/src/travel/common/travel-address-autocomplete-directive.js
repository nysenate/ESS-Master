var essTravel = angular.module('essTravel');

/**
 * Adds google maps autocomplete functionality to an input element.
 *
 * Example:
 * <input travel-address-autocomplete leg="leg" callback="setAddress(address)" placeholder="Enter Origin Address" type="text" size="30">
 *
 * Notes:
 *     - The callback function is required to have the 'leg' and 'address' param.
 *          - Callback function should set the address to the leg.
 *     - Only works on text input elements.
 */
essTravel.directive('travelAddressAutocomplete', ['appProps', '$q', function (appProps, $q) {
    return {
        require: 'ngModel',
        restrict: 'A',
        scope: {
            callback: '&', // callback function
            leg: '='
        },
        link: function ($scope, $elem, $attrs, $ctrl) {

            var element = $elem[0];
            var autocomplete = new google.maps.places.Autocomplete(
                element, { types: ['address'] });

            var address = {};
            autocomplete.addListener('place_changed', function() {
                var place = autocomplete.getPlace();

                // Convert place result into address object
                address.formattedAddress = place.formatted_address;
                address.addr1 = parseAddress1(place);
                address.city = parseCity(place);
                address.county = parseCounty(place);
                address.state = parseState(place);
                address.zip5 = parseZip5(place);

                // Call $apply here because angular does not seem to realize when $scope vars are updated in the callback function.
                $scope.$apply(function () {
                    $scope.callback({leg: $scope.leg, address: address});
                });
            });

            if ($attrs.address) {
                // If a default address is given, initialize with it.
                element.value = $attrs.address;
            }

            function parseAddress1(place) {
                return getTypeName(place, 'street_number') + ' ' + getTypeName(place, 'route');
            }

            function parseCity(place) {
                var city = getTypeName(place, 'locality');
                return city === null ? getTypeName(place, 'administrative_area_level_3'): city;
            }

            function parseCounty(place) {
                return getTypeName(place, 'administrative_area_level_2');
            }

            function parseState(place) {
                return getTypeName(place, 'administrative_area_level_1');
            }

            function parseZip5(place) {
                return getTypeName(place, 'postal_code');
            }

            /**
             * Returns the value associated with the given place and type.
             * @param place A place object from google autocomplete api.
             * @param type A address type. i.e. 'street_number', 'postal_code'
             *  - Types are documented here: https://developers.google.com/places/web-service/autocomplete#place_types
             * @return {*}
             */
            function getTypeName(place, type) {
                for (var i = 0; i < place.address_components.length; i++) {
                    var component = place.address_components[i];
                    if (component.types[0] ===  type) {
                        return component.long_name;
                    }
                }
                return '';
            }

            /**
             * Address autocomplete validation.
             */
            // TODO Implement validation
            // $ctrl.$validators.address = function(modelValue, viewValue) {
            //     return address.formattedAddress === modelValue;
            // }
        }
    }
}]);
