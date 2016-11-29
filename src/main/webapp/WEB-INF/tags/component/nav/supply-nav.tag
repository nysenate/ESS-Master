<%@tag description="Left navigation menu for Supply screens" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ess-component-nav" tagdir="/WEB-INF/tags/component/nav" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<div class="left-nav-div flex-column-box">
  <section class="left-nav-content flex-header" ess-navigation>
    <ess-component-nav:nav-header topicTitle="Supply Menu" colorClass="blue-purple"/>
    <h3 class="main-topic">My Supply</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic"><a href="${ctxPath}/supply/order">Requisition Form</a></li>
      <li class="sub-topic"><a href="${ctxPath}/supply/order/cart">Cart</a></li>
      <li class="sub-topic"><a href="${ctxPath}/supply/order-history">Order History</a></li>
    </ul>
    <shiro:hasPermission name="supply:employee">
    <h3 class="main-topic">Manage Supply</h3>
    <ul class="sub-topic-list">
      <li class="sub-topic"><a href="${ctxPath}/supply/manage/fulfillment">Fulfillment</a></li>
      <li class="sub-topic"><a href="${ctxPath}/supply/manage/reconciliation">Reconciliation</a></li>
      <li class="sub-topic"><a href="${ctxPath}/supply/history/history">Requisition History</a></li>
    </ul>
    </shiro:hasPermission>
  </section>
  <section class="left-nav-content flex-content flex-column-box margin-top-20"
           style="margin-bottom: 100px; min-height: 0px; min-width: 0px;"
           ng-controller="SupplyNavigationController"
           ng-show="shouldDisplayCategoryFilter()" data-ng-init="init()">
    <ess-component-nav:nav-header topicTitle="Categories" colorClass="blue-purple"/>
    <div class="flex-header padding-10">
      <a style="padding-left: 10px;" ng-click="clearSelections()">
        Clear All
      </a>
    </div>
    <div class="flex-content" style="overflow-y: auto">
      <ul class="">
        <li ng-repeat="cat in getCategories()">
          <input type="checkbox" ng-model="cat.selected" ng-change="onCategoryUpdated()"
                 data-ng-init="updateWithURL(cat)">
          <label>{{cat.name}}</label>
        </li>
      </ul>
    </div>
  </section>
</div>
