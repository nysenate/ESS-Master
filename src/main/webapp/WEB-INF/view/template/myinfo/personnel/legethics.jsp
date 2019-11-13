<section ng-controller="LegEthicsCtrl">
  <div class="my-info-hero">
    <h2>Legislative Ethics Training</h2>
  </div>

  <div class="content-container">

    <%--  Show during loading  --%>
    <div loader-indicator class="loader" ng-show="state.loading"></div>

    <%--  Hide during loading  --%>
    <div ng-hide="state.loading">
      <%-- If the task was not loaded successfully --%>
      <ess-notification ng-hide="state.task"
                        level="error"
                        title="LegEthics Personnel Task Assignment Not Found">
        The personnel task assignment associated with the Legislative Ethics course could not be retrieved.<br>
        Please contact the helpline to report this issue.
      </ess-notification>

      <%-- If the task is loaded --%>
      <div ng-if="state.task">

        <%-- Instruction header --%>
        <p class="content-info personnel-todo-instructions">
          <span ng-show="state.task.completed">
            Records indicate you completed Legislative Ethics training on or before
            {{state.task.timestamp | moment:'LL'}}
          </span>
          <span ng-hide="state.task.completed">
            As mandated by law, all new employees are required to complete an interactive Ethics Orientation.
            <br>
            Please follow all instructions below to complete the course.
          </span>
        </p>

        <div class="legethics-instruction-container">
          <a ng-href="{{todoPageUrl}}">
            Return to Personnel To-Do List
          </a>

          <%-- If the task is not yet completed --%>
          <div ng-hide="state.task.completed">
            <h2>LegEthics Training Instructions</h2>
            <ul>
              <li>The interactive course can be accessed using the link below.</li>
              <li>
                You will need to create a new user account for the course.
                <br>
                <span class="bold-text">
                You must use your Senate email address for account registration.
              </span>
              </li>
              <li>
                The online course includes questions which all need to be answered correctly to proceed through the
                material.
              </li>
            </ul>
            <p><a ng-href="{{state.task.getCourseUrl()}}">Legislative Ethics Training Course</a></p>
          </div>
        </div>

      </div>
    </div>

  </div>
</section>
