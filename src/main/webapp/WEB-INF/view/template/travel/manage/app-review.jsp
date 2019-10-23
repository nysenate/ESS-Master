<div ng-controller="AppReviewCtrl">
  <div>
    <div class="travel-hero">
      <h2>Review Applications</h2>
    </div>
    <div class="content-container content-controls">
      <h4 class="travel-content-info travel-text-bold">The following travel applications require your review.</h4>
    </div>
  </div>

  <div ng-if="data.apiRequest.$resolved === true">
    <div ng-if="data.apps.length === 0">
      <div class="content-container">
        <div class="content-info">
          <h2 class="dark-gray">No Applications to Review.</h2>
        </div>
      </div>
    </div>

    <div ng-if="data.apps.length > 0">
      <ess-app-summary-table
          apps="data.apps"
          on-row-click="viewApplicationForm(app)">>
      </ess-app-summary-table>
    </div>
  </div>

  <div modal-container>
    <modal modal-id="app-form-view-modal">
      <div app-form-view-modal></div>
    </modal>
  </div>

</div>
