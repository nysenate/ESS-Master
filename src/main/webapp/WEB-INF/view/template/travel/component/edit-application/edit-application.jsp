<div ng-controller="EditApplicationCtrl as vm">
  <div class="travel-hero">
    <h2>Edit Travel Application</h2>
  </div>
  <div class="content-container content-controls">
    <div class="padding-10 text-align-center">
      <span class="disapproved-text">Editing</span> Travel application for: <span class="bold"
                                                                                  ng-bind="::vm.app.traveler.fullName"></span>
    </div>
  </div>

  <ess-edit-app-breadcrumbs></ess-edit-app-breadcrumbs>

  <div ng-if="vm.app">
    <div ng-if="vm.stateService.isPurposeState()">
      <ess-purpose-edit-form app="vm.app"
                             title="Edit the purpose of travel."
                             positive-callback="vm.savePurpose(app)"
                             negative-callback="vm.doneEditing(app)"
                             negative-label="Done Editing">
      </ess-purpose-edit-form>
    </div>

    <div ng-if="vm.stateService.isOutboundState()">
      <ess-outbound-edit-form app="vm.app"
                              title="Edit the outbound route"
                              positive-callback="vm.saveOutbound(app)"
                              neutral-callback="vm.toPurposeState(app)"
                              negative-callback="vm.doneEditing(app)"
                              negative-label="Done Editing">
      </ess-outbound-edit-form>
    </div>

    <div ng-if="vm.stateService.isReturnState()">
      <ess-return-edit-form app="vm.app"
                            title="Edit the return route"
                            positive-callback="vm.saveRoute(app)"
                            neutral-callback="vm.toOutboundState(app)"
                            negative-callback="vm.doneEditing(app)"
                            negative-label="Done Editing">
      </ess-return-edit-form>
    </div>

    <div ng-if="vm.stateService.isAllowancesState()">
      <ess-allowances-edit-form app="vm.app"
                                title="Edit the expenses"
                                positive-callback="vm.saveAllowances(app)"
                                neutral-callback="vm.toReturnState(app)"
                                negative-callback="vm.doneEditing(app)"
                                negative-label="Done Editing">
      </ess-allowances-edit-form>
    </div>

    <div ng-if="vm.stateService.isOverridesState()">
      <ess-perdiem-overrides-edit-form app="vm.app"
                                       title="Override the calculated expenses."
                                       positive-callback="vm.saveOverrides(app)"
                                       neutral-callback="vm.toAllowancesState(app)"
                                       negative-callback="vm.doneEditing(app)"
                                       negative-label="Done Editing">
      </ess-perdiem-overrides-edit-form>
    </div>

    <div ng-if="vm.stateService.isReviewState()">
      <ess-review-edit-form app="vm.app"
                            title="Here is the full application with your changes."
                            positive-btn-label="Done Editing"
                            positive-callback="vm.doneEditing(app)"
                            neutral-callback="vm.toOverridesState(app)">
      </ess-review-edit-form>
    </div>
  </div>


  <div modal-container>

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
    <modal modal-id="ess-address-county-modal">
      <div ess-address-county-modal></div>
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
            You should now <a class="bold" target="_blank"
                              ng-href="${ctxPath}/travel/application/print?id={{vm.app.id}}&print=true">print</a>,
            sign and deliver your application to your department head.
          </p>
        </div>
      </div>
    </modal>

    <%-- Review detail modals --%>
    <modal modal-id="ess-lodging-details-modal">
      <div ess-lodging-details-modal></div>
    </modal>

    <modal modal-id="ess-meal-details-modal">
      <div ess-meal-details-modal></div>
    </modal>

    <modal modal-id="ess-mileage-details-modal">
      <div ess-mileage-details-modal></div>
    </modal>

    <modal modal-id="external-api-error">
      <div confirm-modal rejectable="true"
           title="Failed to Create Travel Request"
           resolve-button="Go back to ESS"
           reject-button="Log out of ESS">
        <div>
          <p style="text-align: left;">
            ESS is unable to communicate with some 3rd party services required to create the travel estimate.
            Please try submitting your travel application again later. If you continue to get this error please contact
            STS.
          </p>
          <h4>
            What would you like to do next?
          </h4>
        </div>
      </div>
    </modal>

  </div>

</div>