<div ng-controller="TravelApplicationCtrl">
  <div class="travel-hero">
    <h2>Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      Travel application for: <span class="bold">{{data.app.traveler.firstName}} {{data.app.traveler.lastName}}</span>
    </div>
  </div>

  <div class="step-indicator">
    <span class="step completed" ng-click="gotoStep(STATES.PURPOSE)"
          ng-class="{navigable: stepNavigable(STATES.PURPOSE)}">Purpose</span>
    <span class="step" ng-click="gotoStep(STATES.OUTBOUND)"
          ng-class="{completed: highlightStep(STATES.OUTBOUND), navigable: stepNavigable(STATES.OUTBOUND)}">Outbound</span>
    <span class="step" ng-click="gotoStep(STATES.RETURN)"
          ng-class="{completed: highlightStep(STATES.RETURN), navigable: stepNavigable(STATES.RETURN)}">Return</span>
    <span class="step" ng-click="gotoStep(STATES.ALLOWANCES)"
          ng-class="{completed: highlightStep(STATES.ALLOWANCES), navigable: stepNavigable(STATES.ALLOWANCES)}">Expenses</span>
    <span class="step"
          ng-class="{completed: highlightStep(STATES.REVIEW)}">Review</span>
  </div>

  <div loader-indicator class="loader" ng-show="!data.app"></div>

  <div ng-if="data.app">
    <div ng-if="pageState === STATES.PURPOSE">
      <div ng-controller="TravelApplicationPurposeCtrl">
        <ng-include src="'/template/travel/application/travel-application-purpose'"></ng-include>
      </div>
    </div>

    <div ng-if="pageState === STATES.OUTBOUND">
      <div ng-controller="TravelApplicationOutboundCtrl">
        <ng-include src="'/template/travel/application/travel-application-outbound'"></ng-include>
      </div>
    </div>

    <div ng-if="pageState === STATES.RETURN">
      <div ng-controller="TravelApplicationReturnCtrl">
        <ng-include src="'/template/travel/application/travel-application-return'"></ng-include>
      </div>
    </div>

    <div ng-if="pageState === STATES.ALLOWANCES">
      <div ng-controller="TravelApplicationAllowancesCtrl">
        <ng-include src="'/template/travel/application/travel-application-allowances'"></ng-include>
      </div>
    </div>

    <div ng-if="pageState === STATES.REVIEW">
      <div ng-controller="TravelApplicationReviewCtrl">
        <ng-include src="'/template/travel/application/travel-application-review'" onload="init()"></ng-include>
      </div>
    </div>
  </div>

  <div modal-container>

    <%--Continue application modal--%>
    <modal modal-id="travel-continue-application-modal">
      <div travel-continue-application-modal></div>
    </modal>


    <%--Cancel Modal--%>
    <modal modal-id="cancel-application">
      <div confirm-modal rejectable="true"
           title="Cancel Travel Application"
           confirm-message="Are you sure you want to cancel your current application?"
           resolve-button="Yes"
           reject-button="No">
      </div>
    </modal>

    <%--Loading Modal--%>
    <modal modal-id="loading">
      <div progress-modal title="Loading..."></div>
    </modal>

    <%--County information modal--%>
    <modal modal-id="address-county-modal">
      <div address-county-modal></div>
    </modal>

    <modal modal-id="long-trip-warning">
      <div confirm-modal rejectable="true"
           title="Scheduled trip is longer than 7 days"
           confirm-message="Are you sure your travel dates are correct?"
           resolve-button="Yes, my dates are correct"
           reject-button="Let me review">
      </div>
    </modal>

    <%-- Review Modals --%>
    <modal modal-id="submit-confirm">
      <div confirm-modal rejectable="true"
           title="Save Travel Application?"
           confirm-message="Are you sure you want to save this travel application?"
           resolve-button="Save"
           reject-button="Cancel">
      </div>
    </modal>

    <modal modal-id="submit-progress">
      <div progress-modal title="Saving travel application..."></div>
    </modal>

    <modal modal-id="submit-results">
      <div confirm-modal rejectable="true"
           title="Your travel application has been saved."
           <%--confirm-message="What would you like to do next?"--%>
           resolve-button="Go back to ESS"
           reject-button="Log out of ESS">
        <div style="padding-bottom: 20px;">
          <p>
            You should now <a class="bold" target="_blank" ng-href="${ctxPath}/travel/application/travel-application-print?id={{data.app.id}}&print=true">print</a>,
            sign and deliver your application to your department head.
          </p>
        </div>
      </div>
    </modal>

    <%-- Review detail modals --%>
    <modal modal-id="travel-lodging-details-modal">
      <div travel-lodging-details-modal></div>
    </modal>

    <modal modal-id="travel-meal-details-modal">
      <div travel-meal-details-modal></div>
    </modal>

    <modal modal-id="travel-mileage-details-modal">
      <div travel-mileage-details-modal></div>
    </modal>

    <modal modal-id="external-api-error">
      <div confirm-modal rejectable="true"
           title="Failed to Create Travel Request"
           confirm-message=
           "ESS is unable to communicate with some 3rd party services required to create the travel estimate.
           Please try submitting your travel application again later.
           What would you like to do next?"
           resolve-button="Go back to ESS"
           reject-button="Log out of ESS">
      </div>
    </modal>

  </div>

</div>
</div>
