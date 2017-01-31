module.exports = function(grunt) {
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        properties: grunt.file.readJSON('grunt.properties.json'),

        /** Path locations to be used as templates */
        cssRoot: 'assets/css',
        cssSource: '<%= cssRoot %>/src',
        lessSource: '<%= cssRoot %>/less',
        cssVendor: '<%= cssRoot %>/vendor',
        cssDest: '<%= cssRoot %>/dest',
        jsRoot: 'assets/js',
        jsSource: '<%= jsRoot %>/src',
        jsVendor: '<%= jsRoot %>/vendor',
        jspSource: 'WEB-INF/view',
        tagSource: 'WEB-INF/tags',
        bowerRoot: 'bower_components',
        jsDest: '<%= jsRoot %>/dest',
        tomcatWeb: '<%= properties.deployDirectory %>',

        /** Compile LESS into css and place it into the css source directory */
        less: {
            dev: {
                options: {
                    sourceMap: true,
                },
                files: {
                    '<%= cssSource %>/main.css': ['<%= lessSource %>/main.less']
                }
            }
        },

        /** Minify all css into one file */
        cssmin: {
            options: {
                sourceMap: true
            },
            combine: {
                src: ['<%= cssSource %>/*.css', '<%= cssVendor %>/*.css'],
                dest: '<%= cssDest %>/app.min.css'
            }
        },

        /** Compress all js into dev and prod files */
        uglify: {
            vendor: {
                options: {
                    beautify: true,
                    mangle: false,
                    preserveComments: 'some',
                    sourceMap: true
                },
                files: {
                    '<%= jsDest %>/ess-vendor.min.js': [
                        // JQuery
                        '<%= bowerRoot %>/jquery/dist/jquery.min.js',
                        '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.core.min.js',
                        '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.widget.min.js',
                        '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.button.min.js',
                        '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.position.min.js',
                        '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.dialog.min.js',
                        '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.datepicker.min.js',
                        '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.menu.min.js',
                        '<%= bowerRoot %>/jquery-ui/ui/minified/jquery.ui.autocomplete.min.js',
                        // AngularJs
                        '<%= bowerRoot %>/angular/angular.min.js',
                        '<%= bowerRoot %>/angular-route/angular-route.min.js',
                        '<%= bowerRoot %>/angular-resource/angular-resource.min.js',
                        '<%= bowerRoot %>/angular-animate/angular-animate.min.js',
                        '<%= bowerRoot %>/angular-utils-pagination/dirPagination.js',
                        '<%= bowerRoot %>/angular-cookies/angular-cookies.min.js',

                        '<%= bowerRoot %>/odometer/odometer.min.js',
                        '<%= bowerRoot %>/moment/min/moment.min.js',
                        '<%= bowerRoot %>/floatThead/dist/jquery.floatThead.min.js',
                        '<%= bowerRoot %>/angular-float-thead/angular-floatThead.js',
                        '<%= bowerRoot %>/underscore/underscore-min.js',
                        '<%= bowerRoot %>/ui-autocomplete/autocomplete.js',
                        '<%= bowerRoot %>/nsPopover/src/nsPopover.js'
                        ],
                    '<%= jsDest %>/ess-vendor-ie.min.js':
                        ['<%= bowerRoot %>/json2/json2.js']
                }
            },
            dev: {},
            prod: {
                options: {
                    beautify: true,
                    mangle: false,
                    compress: {
                        drop_console: true
                    },
                    preserveComments: 'some', /** Preserve licensing comments */
                    banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' +'<%= grunt.template.today("yyyy-mm-dd") %> */',
                    sourceMap: true
                },
                files: {
                    // main
                    '<%= jsDest %>/ess.min.js': [
                        '<%= jsSource %>/ess-app.js',
                        '<%= jsSource %>/ess-api.js',
                        //<!-- Navigation -->
                        '<%= jsSource %>/nav/ess-nav.js',
                        '<%= jsSource %>/nav/ess-routes.js',
                        '<%= jsSource %>/nav/home.js',
                        //<!-- Common Directives -->
                        '<%= jsSource %>/common/ai-filter.js',
                        '<%= jsSource %>/common/badge-directive.js',
                        '<%= jsSource %>/common/badge-service.js',
                        '<%= jsSource %>/common/common-modals.js',
                        '<%= jsSource %>/common/datepicker-directive.js',
                        '<%= jsSource %>/common/debounce-function.js',
                        '<%= jsSource %>/common/err-src-directive.js',
                        '<%= jsSource %>/common/ess-notifications.js',
                        '<%= jsSource %>/common/internal-error-modal.js',
                        '<%= jsSource %>/common/loader-indicator-directive.js',
                        '<%= jsSource %>/common/location-service.js',
                        '<%= jsSource %>/common/modal-directive.js',
                        '<%= jsSource %>/common/modal-service.js',
                        '<%= jsSource %>/common/moment-filter.js',
                        '<%= jsSource %>/common/not-filter.js',
                        '<%= jsSource %>/common/odometer-directive.js',
                        '<%= jsSource %>/common/pagination-model.js',
                        '<%= jsSource %>/common/promise-utils.js',
                        '<%= jsSource %>/common/round-filter.js',
                        '<%= jsSource %>/common/text-auto-height-directive.js',
                        '<%= jsSource %>/common/throttle-function.js',
                        '<%= jsSource %>/common/timeout-checker.js',
                        '<%= jsSource %>/common/timeout-modal.js',
                        '<%= jsSource %>/common/toggle-panel-directive.js',
                        '<%= jsSource %>/common/zero-pad-filter.js',
                        '<%= jsSource %>/common/ess-storage-service.js',
                        // <!-- Testing Code -->
                        '<%= jsSource %>/test/error-modal-test.js'
                    ],
                    //help
                    '<%= jsDest %>/ess-help.min.js': ['<%= jsSource %>/help/help.js'],
                    //login
                    '<%= jsDest %>/ess-login.min.js': ['<%= jsSource %>/auth/login.js'],
                    //myinfo
                    '<%= jsDest %>/ess-myinfo.min.js': [
                        //        <!-- Personnel -->
                        '<%= jsSource %>/myinfo/personnel/summary-ctrl.js',
                        '<%= jsSource %>/myinfo/personnel/transaction-history-ctrl.js',
                        '<%= jsSource %>/myinfo/personnel/transaction-history-filters.js',
                        //        <!-- Payroll -->
                        '<%= jsSource %>/myinfo/payroll/check-history-ctrl.js',
                        '<%= jsSource %>/myinfo/payroll/check-history-filters.js'
                    ],
                    //supply
                    '<%= jsDest %>/ess-supply.min.js': [
                        //        <%-- Supply Entry --%>
                        '<%= jsSource %>/supply/nav/supply-category-nav-ctrl.js',
                        '<%= jsSource %>/supply/service/supply-inventory-service.js',
                        '<%= jsSource %>/supply/service/supply-category-service.js',
                        '<%= jsSource %>/supply/location/supply-location-autocomplete-service.js',
                        '<%= jsSource %>/supply/items/supply-item-autocomplete-service.js',
                        '<%= jsSource %>/supply/api/supply-item-api.js',
                        //        <%-- History --%>
                        '<%= jsSource %>/supply/history/supply-history-ctrl.js',
                        '<%= jsSource %>/supply/history/supply-order-history-ctrl.js',
                        //        <%-- Manage --%>
                        '<%= jsSource %>/supply/manage/fulfillment/supply-fulfillment-ctrl.js',
                        '<%= jsSource %>/supply/manage/supply-reconciliation-ctrl.js',
                        '<%= jsSource %>/supply/manage/fulfillment/modal/fulfillment-editing-modal.js',
                        '<%= jsSource %>/supply/manage/fulfillment/modal/fulfillment-immutable-modal.js',
                        //        <%-- Order --%>
                        '<%= jsSource %>/supply/order/supply-order-ctrl.js',
                        '<%= jsSource %>/supply/order/supply-quantity-selector.js',
                        '<%= jsSource %>/supply/order/order-quantity-validator.js',
                        '<%= jsSource %>/supply/order/order-destination-service.js',
                        '<%= jsSource %>/supply/order/order-more-prompt-modal.js',
                        '<%= jsSource %>/supply/order/order-canceling-modal.js',
                        '<%= jsSource %>/supply/order/special-order-item-modal.js',
                        '<%= jsSource %>/supply/order/supply-line-item-service.js',
                        '<%= jsSource %>/supply/order/supply-item-filter-service.js',
                        '<%= jsSource %>/supply/order/order-page-state-service.js',
                        //        <%-- Cart --%>
                        '<%= jsSource %>/supply/order/cart/supply-cart-service.js',
                        '<%= jsSource %>/supply/order/cart/supply-cart-ctrl.js',
                        '<%= jsSource %>/supply/order/cart/supply-cart-directives.js',
                        '<%= jsSource %>/supply/order/cart/supply-cart-empty-modal.js',
                        //        <%-- Requisition --%>
                        '<%= jsSource %>/supply/requisition/supply-view-ctrl.js',
                        //        <%-- Utilities --%>
                        '<%= jsSource %>/supply/util/supply-utils-service.js',
                        '<%= jsSource %>/supply/service/supply-location-statistics-service.js'
                    ],
                    //time
                    '<%= jsDest %>/ess-time.min.js': [
                        //        <!-- Time Entry -->
                        '<%= jsSource %>/time/time.js',
                        '<%= jsSource %>/time/record/record-filters.js',
                        '<%= jsSource %>/time/record/record-directives.js',
                        '<%= jsSource %>/time/record/record-utils.js',
                        '<%= jsSource %>/time/record/record-entry-ctrl.js',
                        '<%= jsSource %>/time/record/record-history-ctrl.js',
                        '<%= jsSource %>/time/record/record-manage-ctrl.js',
                        '<%= jsSource %>/time/record/supervisor-record-list.js',
                        '<%= jsSource %>/time/record/record-review-modals.js',
                        '<%= jsSource %>/time/record/record-emp-history-ctrl.js',
                        '<%= jsSource %>/time/record/record-validation.js',
                        '<%= jsSource %>/time/record/record-entry-modals.js',
                        //              <!-- Time Off Requests -->
                        '<%= jsSource %>/time/timeoff/new-request-ctrl.js',
                        //                <!-- Pay Period Viewer -->
                        '<%= jsSource %>/time/period/pay-period-view-ctrl.js',
                        //        <!-- Accruals -->
                        '<%= jsSource %>/time/accrual/accrual-history-ctrl.js',
                        '<%= jsSource %>/time/accrual/accrual-projection-ctrl.js',
                        '<%= jsSource %>/time/accrual/accrual-utils.js',
                        '<%= jsSource %>/time/accrual/accrual-bar-directive.js',
                        //            <!-- Grants -->
                        '<%= jsSource %>/time/grant/grant-ctrl.js'
                    ]
                }
            }
        },

        /** Automatically run certain tasks based on file changes */
        watch: {
            less: {
                files: ['<%= lessSource %>/**.less', '<%= lessSource %>/common/**.less'],
                tasks: ['less', 'cssmin', 'copy:css', '<%= properties.lessBeep %>']
            },
            cssVendor: {
                files: ['<%= cssVendor %>/**/*.css'],
                tasks: ['cssmin', 'copy:css', '<%= properties.cssBeep %>']
            },
            jsVendor: {
                files: ['<%= bowerRoot %>/**.js'],
                tasks: ['uglify:vendor', 'copy:js', '<%= properties.jsVendorBeep %>']
            },
            jsSource: {
                files: ['<%= jsSource %>/**/*.js'],
                tasks: ['uglify:dev', 'uglify:prod', 'copy:js', '<%= properties.jsSourceBeep %>']
            },
            jsp: {
                files: ['<%= jspSource %>/**/*.jsp', '<%= tagSource %>/**/*.tag'],
                tasks: ['copy:jsp', '<%= properties.jspBeep %>']
            }
        },

        copy: {
            css: {
                files: [{
                    expand:true, cwd: '<%= cssDest %>/', src: ['**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>/assets/css/dest/'
                }]
            },
            js: {
                files: [{
                    expand:true, src: ['<%= jsSource %>/**', '<%= jsDest %>/**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>'}]
            },
            jsp : {
                files: [{
                    expand:true, src: ['<%= jspSource %>/**', '<%= tagSource %>/**'], filter: 'isFile',
                    dest: '<%= tomcatWeb %>'
                }]
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-beep');

    grunt.registerTask('default', ['less', 'cssmin', 'uglify', 'copy', 'beep:*-*---*-**-**-*-']);
};