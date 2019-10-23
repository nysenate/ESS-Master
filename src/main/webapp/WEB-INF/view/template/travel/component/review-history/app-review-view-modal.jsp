<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="gov.nysenate.ess.travel.authorization.permission.TravelPermission" %>
<div class="content-container no-top-margin padding-top-5" style="overflow: hidden;">

  <div>
    <ess-app-review-form-body app-review="appReview"></ess-app-review-form-body>
  </div>

  <div class="travel-button-container" style="margin-top: 5px !important; float: left; width: 100%;">
    <shiro:hasPermission name="<%= TravelPermission.TRAVEL_UI_EDIT_APP.getPermissionString() %>">
      <input type="button" class="reject-button" value="Edit Application"
             ng-click="onEdit(appReview)">
    </shiro:hasPermission>

    <div class="" style="float: right;">
      <a class="margin-10" target="_blank"
         ng-href="${ctxPath}/travel/application/print?id={{app.id}}&print=true">Print</a>
      <input type="button" class="travel-neutral-button" value="Close"
             ng-click="exit()">
    </div>
  </div>
</div>