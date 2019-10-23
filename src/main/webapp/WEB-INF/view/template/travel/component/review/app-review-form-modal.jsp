<div class="content-container no-top-margin padding-top-5">

  <div>
    <ess-app-form-body app="appReview.travelApplication"></ess-app-form-body>
  </div>

  <div class="travel-button-container">
    <input type="button" class="submit-button" value="Approve Application"
           ng-click="approve()">
    <input type="button" class="reject-button" value="Disapprove Application"
           ng-click="disapprove()">

    <div style="float: right;">
      <a class="margin-10" target="_blank"
         ng-href="${ctxPath}/travel/application/print?id={{appApproval.travelApplication.id}}&print=true">Print</a>
      <input type="button" class="travel-neutral-button" value="Cancel"
             ng-click="exit()">
    </div>
  </div>
</div>
