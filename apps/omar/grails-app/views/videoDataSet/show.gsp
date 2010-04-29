<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main5"/>
  <title>Show VideoDataSet</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="list" action="list">VideoDataSet List</g:link></span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New VideoDataSet</g:link></span>
  </g:ifAllGranted>
</div>
<div class="body">
  <h1>Show VideoDataSet</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="dialog">
    <table>
      <tbody>

        <tr class="prop">
          <td valign="top" class="name">Id:</td>

          <td valign="top" class="value">${fieldValue(bean: videoDataSet, field: 'id')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Width:</td>

          <td valign="top" class="value">${fieldValue(bean: videoDataSet, field: 'width')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Height:</td>

          <td valign="top" class="value">${fieldValue(bean: videoDataSet, field: 'height')}</td>

        </tr>

<%--
        <tr class="prop">
          <td valign="top" class="name">Ground Geom:</td>

          <td valign="top" class="value">${fieldValue(bean: videoDataSet, field: 'groundGeom')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Start Date:</td>

          <td valign="top" class="value">${fieldValue(bean: videoDataSet, field: 'startDate')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">End Date:</td>

          <td valign="top" class="value">${fieldValue(bean: videoDataSet, field: 'endDate')}</td>

        </tr>
--%>
      
        <tr class="prop">
          <td valign="top" class="name">File Objects:</td>

          <td valign="top" style="text-align:left;" class="value">
            <g:if test="${videoDataSet.fileObjects}">
              <g:link controller="videoFile" action="list" params="${[videoDataSetId: videoDataSet.id]}">Show Video Files</g:link>
            </g:if>
          </td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Repository:</td>

          <td valign="top" class="value"><g:link controller="repository" action="show" id="${videoDataSet?.repository?.id}">${videoDataSet?.repository?.encodeAsHTML()}</g:link></td>

        </tr>

      </tbody>
    </table>
  </div>
  <div class="buttons">
    <g:form>
      <input type="hidden" name="id" value="${videoDataSet?.id}"/>
      <g:ifAllGranted role="ROLE_ADMIN">
        <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
      </g:ifAllGranted>
      <span class="menuButton">
        <a href="${createLink(controller: 'thumbnail', action: 'frame', id: videoDataSet.id, params: [size: 512])}">Show Frame</a>
      </span>
    </g:form>
  </div>
</div>
</body>
</html>
