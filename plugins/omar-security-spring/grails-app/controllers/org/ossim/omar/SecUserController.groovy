package org.ossim.omar

import au.com.bytecode.opencsv.CSVWriter

class SecUserController
{

  static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

  def filterService

  def index = {
    redirect(action: "list", params: params)
  }

  def list = {
    if ( !params.max )
    { params.max = 100 }
    params.max = Math.min(params.max ? params.int('max') : 10, 100)
    [secUserInstanceList: SecUser.list(params), secUserInstanceTotal: SecUser.count()]
  }

  def create = {
    def secUserInstance = new SecUser()
    secUserInstance.properties = params
    return [secUserInstance: secUserInstance]
  }

  def save = {
    def secUserInstance = new SecUser(params)
    if ( secUserInstance.save(flush: true) )
    {
      flash.message = "${message(code: 'default.created.message', args: [message(code: 'secUser.label', default: 'SecUser'), secUserInstance.id])}"
      redirect(action: "show", id: secUserInstance.id)
    }
    else
    {
      render(view: "create", model: [secUserInstance: secUserInstance])
    }
  }

  def show = {
    def secUserInstance = SecUser.get(params.id)
    if ( !secUserInstance )
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'secUser.label', default: 'SecUser'), params.id])}"
      redirect(action: "list")
    }
    else
    {
      [secUserInstance: secUserInstance]
    }
  }

  def edit = {
    def secUserInstance = SecUser.get(params.id)
    if ( !secUserInstance )
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'secUser.label', default: 'SecUser'), params.id])}"
      redirect(action: "list")
    }
    else
    {
      return [secUserInstance: secUserInstance]
    }
  }

  def update = {
    def secUserInstance = SecUser.get(params.id)
    if ( secUserInstance )
    {
      if ( params.version )
      {
        def version = params.version.toLong()
        if ( secUserInstance.version > version )
        {

          secUserInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'secUser.label', default: 'SecUser')] as Object[], "Another user has updated this SecUser while you were editing")
          render(view: "edit", model: [secUserInstance: secUserInstance])
          return
        }
      }

      secUserInstance.properties = params

      //println params.sort()

      def roleNames = params.keySet().grep { it.startsWith("ROLE_")}

      SecUserSecRole.removeAll(secUserInstance)
      roleNames?.each { SecUserSecRole.create(secUserInstance, SecRole.findByAuthority(it))}

      if ( !secUserInstance.hasErrors() && secUserInstance.save(flush: true) )
      {
        flash.message = "${message(code: 'default.updated.message', args: [message(code: 'secUser.label', default: 'SecUser'), secUserInstance.id])}"
        redirect(action: "show", id: secUserInstance.id)
      }
      else
      {
        render(view: "edit", model: [secUserInstance: secUserInstance])
      }
    }
    else
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'secUser.label', default: 'SecUser'), params.id])}"
      redirect(action: "list")
    }
  }

  def delete = {
    def secUserInstance = SecUser.get(params.id)
    if ( secUserInstance )
    {
      try
      {
        SecUser.withTransaction {
          SecUserSecRole.removeAll(secUserInstance)
          secUserInstance.delete(flush: true)
        }

        flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'secUser.label', default: 'SecUser'), params.id])}"
        redirect(action: "list")
      }
      catch (org.springframework.dao.DataIntegrityViolationException e)
      {
        flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'secUser.label', default: 'SecUser'), params.id])}"
        redirect(action: "show", id: params.id)
      }
    }
    else
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'secUser.label', default: 'SecUser'), params.id])}"
      redirect(action: "list")
    }
  }

  def export = {

    def newParams = [
            filterBean: params.exportFilterBean,
            filterField: params.exportFilterField,
            filterCriteria: params.exportFilterCriteria,
            filterValue: params.exportFilterValue,
            max: SecUser.count()
    ]

    def useFilter = newParams.filterField && newParams.filterCriteria && newParams.filterValue
    def users = (useFilter) ? filterService.filter(newParams)['secUserInstanceList'] : SecUser.list()

    def labels = ['Id', 'Username', 'Real Name', 'Organization', 'Phone Number', 'E-mail', 'Enabled', 'Account Locked', 'Account Expired', 'Password Expired']
    def fields = ['id', 'username', 'userRealName', 'organization', 'phoneNumber', 'email', 'enabled', 'accountLocked', 'accountExpired', 'passwordExpired']
    def formatters = [:]


    def prefix = "omar-users-"
    def workDir = grailsApplication.config.export.workDir ?: "/tmp"

    def csvFile = File.createTempFile(prefix, ".csv", workDir as File)
    def csvWriter = new CSVWriter(csvFile.newWriter())

    csvWriter.writeNext(labels as String[])


    for ( user in users )
    {
      def data = []
      for ( field in fields )
      {

        if ( formatters[field] )
        {
          data << formatters[field].call(user[field])
        }
        else
        {
          data << user[field]
        }
      }

      csvWriter.writeNext(data as String[])
    }

    csvWriter.close()

    response.setHeader("Content-disposition", "attachment; filename=${csvFile.name}");
    response.contentType = "text/csv"
    response.outputStream << csvFile.newInputStream()
    response.outputStream.flush()
  }

}
