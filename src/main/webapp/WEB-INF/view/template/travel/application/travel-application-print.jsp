<div ng-controller="TravelApplicationPrintCtrl">
  <div class="content-container padding-10 clearfix" style="width: 700px;">
    <div class="text-align-center">
      <h2 class="margin-5 bold">NEW YORK STATE SENATE</h2>
      <h3 class="margin-5">Secretary of the Senate</h3>
      <h2 class="margin-5">APPLICATION FOR TRAVEL APPROVAL</h2>
    </div>

    <div class="margin-20 travel-print-info-container">
      <div class="grid">

        <div class="col-2-12 travel-print-label">
          Date:
        </div>
        <div class="col-6-12">
           {{app.submittedDateTime | date:'shortDate'}}
        </div>

        <div class="col-2-12 travel-print-label">
          NYS EMPLID#:
        </div>
        <div class="col-2-12">
          {{app.traveler.nid}}
        </div>

        <div class="col-2-12 travel-print-label">
          Name:
        </div>
        <div class="col-6-12">
           {{app.traveler.fullName}}
        </div>

        <div class="col-2-12 travel-print-label">
          Phone:
        </div>
        <div class="col-2-12">
          {{app.traveler.workPhone}}
        </div>

        <div class="col-2-12 travel-print-label">
          Office:
        </div>
        <div class="col-6-12">
           {{app.traveler.respCtr.respCenterHead.name}}
        </div>

        <div class="col-2-12 travel-print-label">
          Agency Code:
        </div>
        <div class="col-2-12">
          {{app.traveler.respCtr.agencyCode}}
        </div>

        <div class="col-2-12 travel-print-label">
          Office Address:
        </div>
        <div class="col-10-12">
          {{app.traveler.workAddress.formattedAddress}}
        </div>

        <div class="col-12-12 width-100" style="border-bottom: 4px solid grey; width: 100%;">
        </div>

        <div class="col-2-12 travel-print-label">
          Departure:
        </div>
        <div class="col-10-12">
          {{app.origin.formattedAddress}}
        </div>

        <span ng-repeat="acc in app.accommodations" style="font-weight: normal;">
          <div class="col-2-12 travel-print-label">
            <span ng-if="$first">Destination:</span>
            <span ng-if="!$first">&nbsp;</span>
          </div>
          <div class="col-10-12 float-left">
            {{acc.address.formattedAddress}}
          </div>
        </span>

        <div class="col-2-12 travel-print-label">
          Dates of Travel:
        </div>
        <div class="col-10-12">
          {{app.startDate | date:'shortDate'}} to {{app.endDate | date:'shortDate'}}
        </div>

        <div class="col-2-12 travel-print-label">
          Purpose:
        </div>
        <div class="col-10-12">
          {{app.purposeOfTravel}}
        </div>

      </div>
    </div>

    <div style="overflow: auto;">
      <div class="travel-print-mot-box">
        <h4 style="margin: 0px 0px 5px 0px;">Mode of Transportation</h4>
        <div ng-repeat="mode in modeOfTransportations" style="display: inline;">
          <label>{{mode}} </label><input type="checkbox" ng-checked="containsMot(mode)" onclick="return false;">
          <span ng-if="!$last"><br/></span>
        </div>
      </div>

      <div class="travel-print-allowances-box">
        <h4 style="margin: 0px 0px 5px 0px;">Estimated Travel Costs</h4>
        <label>Transportation</label><span>{{app.mileageAllowance | currency}}</span><br/>
        <label>Food</label><span>{{app.mealAllowance | currency}}</span><br/>
        <label>Lodging</label><span>{{app.lodgingAllowance | currency}}</span><br/>
        <label>Parking/Tolls</label><span>{{tollsAndParking() | currency}}</span><br/>
        <label>Taxi/Bus/Subway</label><span>{{app.alternateAllowance | currency}}</span><br/>
        <label>Registration Fee</label><span>{{app.registrationAllowance | currency}}</span><br/>
        <label>TOTAL</label><span>{{app.totalAllowance | currency}}</span><br/>
      </div>
    </div>

    <div class="margin-20">
      <div class="width-100" style="border-bottom: 4px solid grey; width: 100%;">
      </div>
    </div>

    <div>
      <div id="traveler-signature" class="print-signature">
        <span class="signature-name">Signature of Traveler</span>
        <span class="signature-date">Date</span>
      </div>

      <div id="depthead-signature" class="print-signature">
        <span class="signature-name">Member/Department Head</span>
        <span class="signature-date">Date</span>
      </div>

      <br/>

      <div id="secretary-signature" class="print-signature">
        <span class="signature-name">Secretary of the Senate</span>
        <span class="signature-date">Date</span>
      </div>
    </div>

    <%--<div class="margin-20">--%>
      <%--<div class="grid">--%>
        <%--<div class="col-6-12">--%>
          <%--_______________________________________--%>
        <%--</div>--%>
         <%--<div class="col-6-12">--%>
          <%--_______________________________________--%>
        <%--</div>--%>
      <%--</div>--%>
    <%--</div>--%>

  </div>
  <div modal-container>
  </div>
</div>
