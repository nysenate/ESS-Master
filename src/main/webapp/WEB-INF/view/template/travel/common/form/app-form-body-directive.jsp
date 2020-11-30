<div>
  <div class="text-align-center">
    <h2 class="margin-5 bold">NEW YORK STATE SENATE</h2>
    <h3 class="margin-5">Secretary of the Senate</h3>
    <h2 class="margin-5">APPLICATION FOR TRAVEL APPROVAL</h2>
    <h3 class="margin-5">Prior Approval for all travel must be obtained from the Secretary of the Senate</h3>
  </div>

  <div class="margin-20">
    <div class="app-form-container">
      <div class="app-form-grid">
        <div class="app-form-label">
          Date:
        </div>
        <div class="app-form-m-col">
          {{(app.submittedDateTime | date:'shortDate') || NOT_AVAILABLE}}
        </div>

        <div class="app-form-label">
          NYS EMPLID#:
        </div>
        <div class="app-form-s-col">
          {{(app.traveler.nid) || NOT_AVAILABLE}}
        </div>
      </div>

      <div class="app-form-grid">
        <div class="app-form-label">
          Name/Title:
        </div>
        <div class="app-form-m-col">
          {{(app.traveler.fullName) || NOT_AVAILABLE}} - {{(app.traveler.jobTitle) || NOT_AVAILABLE}}
        </div>

        <div class="app-form-label">
          Phone:
        </div>
        <div class="app-form-s-col">
          {{(app.traveler.workPhone) || NOT_AVAILABLE}}
        </div>
      </div>

      <div class="app-form-grid">
        <div class="app-form-label">
          Office:
        </div>
        <div class="app-form-m-col">
          {{((app.traveler.respCtr.respCenterHead.name) || NA) || NOT_AVAILABLE}}
        </div>

        <div class="app-form-label">
          Agency Code:
        </div>
        <div class="app-form-s-col">
          {{(app.traveler.respCtr.agencyCode) || NOT_AVAILABLE}}
        </div>
      </div>

      <div class="app-form-grid">
        <div class="app-form-label">
          Office Address:
        </div>
        <div class="app-form-l-col">
          {{(app.traveler.workAddress.formattedAddressWithCounty) || NOT_AVAILABLE}}
        </div>
      </div>

      <div class="app-form-grid" style="border-bottom: 4px solid grey; width: 100%; margin-bottom: 4px;">
      </div>

      <div class="app-form-grid">
        <div class="app-form-label">
          Departure:
        </div>
        <div class="app-form-row-l-col">
          {{(app.activeAmendment.route.origin.formattedAddressWithCounty) || NOT_AVAILABLE}}
        </div>
      </div>

      <div class="app-form-grid" ng-repeat="dest in app.activeAmendment.route.destinations"
           style="font-weight: normal;">
        <div class="app-form-label">
          <span ng-if="$first">Destination:</span>
          <span ng-if="!$first">&nbsp;</span>
        </div>
        <div class="app-form-l-col">
          {{(dest.address.formattedAddressWithCounty) || NOT_AVAILABLE}}
        </div>
      </div>

      <div class="app-form-grid">
        <div class="app-form-label">
          Dates of Travel:
        </div>
        <div class="app-form-l-col">
          {{(app.activeAmendment.startDate | date:'shortDate') || NOT_AVAILABLE}}
          to {{(app.activeAmendment.endDate | date:'shortDate') || NOT_AVAILABLE}}
        </div>
      </div>

      <div class="app-form-grid">
        <div class="app-form-label">
          Purpose:
        </div>
        <div class="app-form-l-col">
          {{app.activeAmendment.purposeOfTravel.eventType.displayName || NOT_AVAILABLE}}
          <span ng-if="app.activeAmendment.purposeOfTravel.eventType.requiresName">
            : {{app.activeAmendment.purposeOfTravel.eventName}}
          </span>
          <br>
          {{app.activeAmendment.purposeOfTravel.additionalPurpose}}
        </div>
      </div>

      <div class="app-form-grid" style="align-items: flex-start;">
        <div class="app-form-mot-box">
          <h4 style="margin: 0px 0px 10px 0px; text-align: center;">Mode of Transportation</h4>
          <div ng-repeat="mode in modeOfTransportations"
               ng-if="app.activeAmendment.route" <%--Only evaluate this once $scope.app has been set by async request--%>
               style="display: inline;">
            <label>{{mode.displayName}} </label><input type="checkbox"
                                                       ng-checked="containsMot(mode)"
                                                       onclick="return false;">
            <span ng-if="!$last"><br/></span>
          </div>
        </div>

        <div class="app-form-allowances-box">
          <h4 style="margin: 0px 0px 10px 0px; text-align: center;">Estimated Travel Costs</h4>
          <label>
            Transportation ({{app.activeAmendment.route.mileagePerDiems.totalMileage}} Miles)
          </label>
          <span>{{(app.activeAmendment.transportationAllowance | currency) || NOT_AVAILABLE}}</span><br/>
          <label>Food</label><span>{{(app.activeAmendment.mealAllowance | currency) || NOT_AVAILABLE}}</span><br/>
          <label>Lodging</label><span>{{(app.activeAmendment.lodgingAllowance | currency) || NOT_AVAILABLE}}</span><br/>
          <label>Parking/Tolls</label><span>{{(app.activeAmendment.tollsAndParkingAllowance | currency) || NOT_AVAILABLE}}</span><br/>
          <label>Taxi/Bus/Subway</label><span>{{(app.activeAmendment.alternateTransportationAllowance | currency) || NOT_AVAILABLE}}</span><br/>
          <label>Registration
            Fee</label><span>{{(app.activeAmendment.registrationAllowance | currency) || NOT_AVAILABLE}}</span><br/>
          <label>TOTAL</label><span>{{(app.activeAmendment.totalAllowance | currency) || NOT_AVAILABLE}}</span><br/>
        </div>
      </div>

      <div class="app-form-grid" style="border-bottom: 4px solid grey; width: 100%; margin-bottom: 4px;">
      </div>

      <div class="app-form-grid"
           ng-if="app.activeAmendment.attachments"
           ng-repeat="attachment in app.activeAmendment.attachments">
        <div class="app-form-label"
             ng-if="$first">
          Attachments:
        </div>
        <div class="app-form-label"
             ng-if="!$first">
          <span> </span>
        </div>
        <div class="app-form-row-l-col">
          <a ng-href="${ctxPath}/api/v1/travel/application/attachment/{{attachment.filename}}" target="_blank">{{attachment.originalName}}</a>
        </div>
      </div>

    </div>
  </div>
</div>
