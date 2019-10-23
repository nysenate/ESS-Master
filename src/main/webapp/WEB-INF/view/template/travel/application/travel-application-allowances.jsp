<div class="content-container text-align-center">
  <div class="expenses-container">
    <h1>Meals and Lodging</h1>
    <p>
      You will be reimbursed for the following meals and lodging. Uncheck anything you would not like to be reimbursed for.
    </p>
    <div class="grid">
      <div>
        <div class="col-6-12">
          <h4>Address</h4>
        </div>
        <div class="col-2-12">
          <h4>Date</h4>
        </div>
        <div class="col-2-12">
          <h4>Request Meals</h4>
        </div>
        <div class="col-2-12">
          <h4>Request Lodging</h4>
        </div>
      </div>

      <div ng-repeat="dest in destinations">
        <div class="col-6-12 margin-top-10">
          {{dest.accommodation.address.formattedAddress}}
        </div>
        <div class="col-6-12 margin-top-10">
          &nbsp;
        </div>
        <div ng-repeat="stay in dest.stays" class="">
          <div class="col-6-12">
            &nbsp;
          </div>
          <div class="col-2-12">
            {{stay.date | date: 'shortDate'}}
          </div>
          <div class="col-2-12">
            <label>Meals: </label><input type="checkbox" ng-model="stay.isMealsRequested">
          </div>
           <div class="col-2-12">
             <span ng-if="stay.isLodgingEligible">
               <label>Lodging: </label><input type="checkbox" ng-model="stay.isLodgingRequested">
             </span>
             <span ng-if="!stay.isLodgingEligible">&nbsp;</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="content-container text-align-center"
     ng-if="anyReimbursableTravel()">
  <div class="expenses-container">
    <h1>Mileage</h1>
    <p>
      You will be reimbursed for the following travel. Uncheck anything you do not wish to be reimbursed for.
    </p>
    <div class="grid">
      <div>
        <div class="col-5-12">
          <h4>From</h4>
        </div>
        <div class="col-5-12">
          <h4>To</h4>
        </div>
        <div class="col-2-12">
          <h4>Request Mileage</h4>
        </div>
      </div>
      <div ng-repeat="leg in route.outboundLegs"
           ng-if="isReimbursableLeg(leg)">
        <div class="col-5-12">
          {{leg.from.formattedAddress}}
        </div>
        <div class="col-5-12">
          {{leg.to.formattedAddress}}
        </div>
        <label>Mileage: </label><input type="checkbox" ng-model="leg.isMileageRequested">
      </div>
      <div ng-repeat="leg in route.returnLegs">
        <div class="col-5-12">
          {{leg.from.formattedAddress}}
        </div>
        <div class="col-5-12">
          {{leg.to.formattedAddress}}
        </div>
        <label>Mileage: </label><input type="checkbox" ng-model="leg.isMileageRequested">
      </div>
    </div>
  </div>
</div>


<div class="content-container text-align-center">
  <div class="expenses-container">
    <h1>Miscellaneous Expenses (Optional):</h1>
    <p class="margin-20">
      To request reimbursement for any of the following categories, enter your estimated expenses.
    </p>
    <div class="width-50 margin-top-20" style="margin: auto;">
      <div class="grid" style="min-width: 0;">
        <div class="col-6-12 padding-bottom-10">
          <label class="travel-allowance-label">Tolls: $</label>
          <input ng-model="allowances.tollsAllowance" type="number" step="0.01" min="0" style="width: 5em;">
        </div>
        <div class="col-6-12 padding-bottom-10">
          <label class="travel-allowance-label">Parking: $</label>
          <input ng-model="allowances.parkingAllowance" type="number" step="0.01" min="0" style="width: 5em;">
        </div>
        <div class="col-6-12">
          <label class="travel-allowance-label">Taxi/Bus/Subway: $</label>
          <input ng-model="allowances.alternateAllowance" type="number" step="0.01" min="0" style="width: 5em;">
        </div>
        <div class="col-6-12">
          <label class="travel-allowance-label">Registration Fee: $</label>
          <input ng-model="allowances.registrationAllowance" type="number" step="0.01" min="0" style="width: 5em;">
        </div>
      </div>
    </div>
  </div>

  <div class="travel-button-container">
    <input type="button" class="neutral-button" value="Cancel"
           ng-click="cancelApplication()">
    <input type="button" class="travel-neutral-button" value="Back"
           ng-click="allowancesCallback(destinations, allowances, ACTIONS.BACK)">
    <input type="button" class="submit-button"
           value="Next"
           ng-click="allowancesCallback(destinations, allowances, ACTIONS.NEXT)">
  </div>
</div>