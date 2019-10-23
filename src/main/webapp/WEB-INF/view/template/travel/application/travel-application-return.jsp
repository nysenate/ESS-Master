<div class="content-container">
  <p class="travel-content-info travel-text-bold">
    Enter your return route of travel.
  </p>

  <form novalidate name="return.form" id="returnForm">

    <div ng-if="return.form.$submitted && !return.form.$valid" class="margin-10">
      <ess-notification level="error" title="Return segments have errors">
        <ul>
          <li ng-if="return.form.$error.dateValidator">One or more travel dates are invalid.
            <span class="icon-help-with-circle" style="padding: 5px;" title="Select a date from the calendar or enter a date in the form mm/dd/yyyy"></span></li>
          </li>
          <li ng-if="return.form.$error.motRequired">Enter a mode of transportation for each segment.</li>
          <li ng-if="return.form.$error.motDescription">Specify how you will travel for segments with mode of transportation of Other.</li>
          <li ng-if="return.form.$error.addressValidator">One or more of your addresses are invalid.
            <span class="icon-help-with-circle" style="padding: 5px;" title="Addresses must be selected from the drop down and contain a zip code. Include a street number to help ensure your selected address will be valid."></span>
          </li>
        </ul>
      </ess-notification>
    </div>

    <div class="travel-inner-container" ng-repeat="leg in route.returnLegs">
      <div class="travel-secondary-background" style="display: inline-block; width: 100%;">
        <h2 class="travel-subheader float-left travel-secondary-background">Return Segment {{$index + 1}}</h2>
        <span class="icon-cross travel-container-cross float-right" style=""
              ng-if="$index > 0 && isLastSegment($index)"
              ng-click="deleteSegment()"></span>
      </div>
      <div class="travel-inner-container-content">

        <div class="itinerary-address">
          <label>From</label><br/>
          <input travel-address-autocomplete
                 name="fromAddress_{{$index}}"
                 ng-model="leg.from.formattedAddress"
                 leg="leg"
                 callback="setFromAddress(leg, address)"
                 autocomplete-address-validator
                 placeholder="From Address"
                 type="text" size="50" required>
        </div>
        <div class="itinerary-date">
          <label>Travel Date</label><br/>
          <input datepicker date-validator type="text" size="13" from-date="fromDate()"
                 name="travelDate_{{$index}}" ng-model="leg.travelDate" autocomplete="new-password" required>
        </div>
        <div class="clear"></div>

        <div class="itinerary-address">
          <label>To</label><br/>
          <input travel-address-autocomplete
                 name="toAddress_{{$index}}"
                 ng-model="leg.to.formattedAddress"
                 leg="leg"
                 callback="setToAddress(leg, address)"
                 autocomplete-address-validator
                 placeholder="To Address"
                 type="text"
                 size="50"
                 required>
        </div>

        <div class="itinerary-mot-container">
          <div class="itinerary-mot">
            <label>Mode of Transportation:</label><br/>
            <select mot-validator name="mot_{{$index}}" ng-model="leg.modeOfTransportation"
                    ng-options="mode.displayName for mode in modesOfTransportation track by mode.methodOfTravel"
                    ng-change="motChange(leg, $index)"
                    required></select>
          </div>
          <div class="itinerary-mot-write-in" ng-if="leg.modeOfTransportation.methodOfTravel === 'OTHER'">
            <label>Please Specify:</label><br/>
            <input mot-description-validator id="returnMotOtherInput_{{$index}}" name="motOther_{{$index}}"
                   type="text" size="17" ng-model="leg.modeOfTransportation.description">
          </div>
        </div>
        <div class="clear"></div>

      </div>
    </div>

    <div>
      <div class="margin-10 travel-secondary-text pointer" style="margin-left:20px;"
           ng-click="addSegment()">
        <span class="icon-circle-with-plus" style="font-size: large; vertical-align: middle;"></span>
        <span style="vertical-align: middle;"> Add Return Segment (optional)</span>
      </div>
    </div>


    <div class="text-align-center">
      <div class="travel-button-container">
        <input type="button" class="neutral-button" value="Cancel"
               ng-click="cancel()">
        <input type="button" class="travel-neutral-button" value="Back"
               ng-click="previousState()">
        <input type="submit" class="submit-button" value="Next"
               ng-click="next()">
      </div>
    </div>

  </form>
</div>
