<%@ page import="org.ossim.omar.SecUser" %>
<%@ page import="org.ossim.omar.SecRole" %>
<%@ page import="org.ossim.omar.SecUserSecRole" %>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <g:set var="entityName" value="${message(code: 'secUser.label', default: 'SecUser')}"/>
  <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></span>
  </div>
  <div class="body">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${secUserInstance}">
      <div class="errors">
        <g:renderErrors bean="${secUserInstance}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form method="post">
      <g:hiddenField name="id" value="${secUserInstance?.id}"/>
      <g:hiddenField name="version" value="${secUserInstance?.version}"/>
      <div class="dialog">
        <table>
          <tbody>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="username"><g:message code="secUser.username.label" default="Username"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'username', 'errors')}">
              <g:textField name="username" value="${secUserInstance?.username}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="password"><g:message code="secUser.password.label" default="Password"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'password', 'errors')}">
              <g:passwordField name="password" value="${secUserInstance?.password}"/>
            </td>
          </tr>


          <tr class="prop">
            <td valign="top" class="name">
              <label for="userRealName"><g:message code="secUser.userRealName.label" default="User Real Name"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'userRealName', 'errors')}">
              <g:textField name="userRealName" value="${secUserInstance?.userRealName}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="organization"><g:message code="secUser.organization.label" default="Organization"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'organization', 'errors')}">
              <g:textField name="organization" value="${secUserInstance?.organization}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="phoneNumber"><g:message code="secUser.phoneNumber.label" default="Phone Number"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'phoneNumber', 'errors')}">
              <g:textField name="phoneNumber" value="${secUserInstance?.phoneNumber}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="email"><g:message code="secUser.email.label" default="Email"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'email', 'errors')}">
              <g:textField name="email" value="${secUserInstance?.email}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="enabled"><g:message code="secUser.enabled.label" default="Enabled"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'enabled', 'errors')}">
              <g:checkBox name="enabled" value="${secUserInstance?.enabled}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="accountExpired"><g:message code="secUser.accountExpired.label" default="Account Expired"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'accountExpired', 'errors')}">
              <g:checkBox name="accountExpired" value="${secUserInstance?.accountExpired}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="accountLocked"><g:message code="secUser.accountLocked.label" default="Account Locked"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'accountLocked', 'errors')}">
              <g:checkBox name="accountLocked" value="${secUserInstance?.accountLocked}"/>
            </td>
          </tr>

          <tr class="prop">
            <td valign="top" class="name">
              <label for="passwordExpired"><g:message code="secUser.passwordExpired.label" default="Password Expired"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: secUserInstance, field: 'passwordExpired', 'errors')}">
              <g:checkBox name="passwordExpired" value="${secUserInstance?.passwordExpired}"/>
            </td>
          </tr>
          </tbody>
        </table>

        <hr/>

        <h1>Roles:</h1>

        <g:set var="userRoles" value="${secUserInstance.authorities}"/>
        <g:set var="allRoles" value="${SecRole.list().sort { it.authority } }"/>

        <table>
          <tbody>
          <g:each var="role" in="${allRoles}">
            <tr class="prop">
              <td valign="top" class="name">
                <label for="${role.authority}">${role.authority}</label>
              </td>
              <td valign="top">
                <g:checkBox name="${role.authority}" value="${role.authority in userRoles.authority}"/>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/></span>
        <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
      </div>
    </g:form>
  </div>
</content>
</body>
</html>