<div class="content-container no-top-margin padding-top-5">

  <div>
    <ess-app-review-form-body app-review="appReview"></ess-app-review-form-body>
  </div>

  <div class="travel-button-container" style="margin-top: 5px !important;">
    <input type="button" class="submit-button" value="Approve Application"
           ng-click="approve()">
    <input type="button" class="reject-button" value="Disapprove Application"
           ng-click="disapprove()">

    <a ng-click="vm.onEdit()">Edit</a>

    <div style="float: right;">
      <a class="margin-10" target="_blank"
         ng-href="${ctxPath}/travel/application/print?id={{appReview.travelApplication.id}}&print=true">Print</a>
      <input type="button" class="travel-neutral-button" value="Cancel"
             ng-click="exit()">
    </div>
  </div>
</div>
