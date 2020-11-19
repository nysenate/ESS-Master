<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="gov.nysenate.ess.travel.authorization.permission.SimpleTravelPermission" %>

<div class="content-container no-top-margin padding-top-5">

  <div>
    <ess-app-review-form-body app-review="appReview"></ess-app-review-form-body>
  </div>

  <div class="travel-button-container" style="margin-top: 5px !important;">
    <input type="button" class="submit-button" value="Approve Application"
           ng-click="approve()">
    <input type="button" class="reject-button" value="Disapprove Application"
           ng-click="disapprove()">

    <shiro:hasPermission name="<%= SimpleTravelPermission.TRAVEL_UI_EDIT_APP.getPermissionString() %>">
      <input type="button" class="neutral-button" value="Edit Application"
             ng-click="vm.onEdit()">
    </shiro:hasPermission>

    <div style="float: right;">
      <a class="margin-10" target="_blank"
         ng-href="${ctxPath}/api/v1/travel/application/{{appReview.travelApplication.id}}.pdf">Print</a>
      <input type="button" class="travel-neutral-button" value="Close"
             ng-click="exit()">
    </div>
  </div>
</div>
