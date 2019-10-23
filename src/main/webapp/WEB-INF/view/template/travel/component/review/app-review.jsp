<div ng-controller="AppReviewCtrl as vm">
  <div>
    <div class="travel-hero">
      <h2>Review Applications</h2>
    </div>
    <div class="content-container content-controls">
      <h4 class="travel-content-info travel-text-bold">The following travel applications require your review.</h4>
    </div>
  </div>

  <div ng-if="vm.isLoading === false">
    <div ng-if="vm.apps.length === 0">
      <div class="content-container">
        <div class="content-info">
          <h2 class="dark-gray">No Applications to Review.</h2>
        </div>
      </div>
    </div>

    <div ng-if="vm.apps.length > 0">
      <ess-app-summary-table
          apps="vm.apps"
          on-row-click="vm.onAppRowClick(app)">
      </ess-app-summary-table>
    </div>
  </div>

  <div modal-container>
    <modal modal-id="app-review-action-modal">
      <div app-review-action-modal></div>
    </modal>

    <modal modal-id="app-review-approve-confirm-modal">
      <div app-review-approve-confirm-modal></div>
    </modal>

    <modal modal-id="app-review-disapprove-confirm-modal">
      <div app-review-disapprove-confirm-modal></div>
    </modal>
  </div>

</div>
