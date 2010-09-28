package org.ossim.omar

import org.springframework.beans.factory.InitializingBean
import grails.converters.XML
import grails.converters.JSON

class RasterSearchController implements InitializingBean
{
  def grailsApplication
  def baseWMS
  def dataWMS

  def authenticateService
  def rasterEntrySearchService

  public List tagHeaderList
  public List tagNameList

  static defaultAction = 'search'

  def search = {
    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

    if ( request.method == 'POST' )
    {
      if ( !params.max )
      {
        params.max = 10
      }

      def user = authenticateService.principal().username
      def starttime = System.currentTimeMillis()
      def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)
      def endtime = System.currentTimeMillis()

      def logData = [
              TYPE: "raster_search",
              START: new Date(starttime),
              END: new Date(endtime),
              ELAPSE_TIME_MILLIS: endtime - starttime,
              USER: user,
              PARAMS: params
      ]

      log.info(logData)
      //println logData

      chain(action: "results", model: [rasterEntries: rasterEntries /*, tags: tags*/], params: params)
    }
    else
    {
      // println queryParams?.toMap()

      return [queryParams: queryParams, baseWMS: baseWMS, dataWMS: dataWMS]
    }
  }

  def results = {
//    println params

    def starttime = System.currentTimeMillis()

    if ( !params.max )
    {
      params.max = 10
    }

    def rasterEntries = null
    def tags = null
    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

    if ( chainModel )
    {
      rasterEntries = chainModel.rasterEntries
      tags = chainModel.tags
    }
    else
    {

      rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)

      def endtime = System.currentTimeMillis()
      def user = authenticateService.principal()?.username

      def logData = [
              TYPE: "raster_search",
              START: new Date(starttime),
              END: new Date(endtime),
              ELAPSE_TIME_MILLIS: endtime - starttime,
              USER: user,
              PARAMS: params
      ]

      log.info(logData)

      println logData
    }

    render(view: 'results', model: [
            rasterEntries: rasterEntries,
            //tags: tags,
            tagNameList: tagNameList,
            tagHeaderList: tagHeaderList,
            queryParams: queryParams
    ])

  }



  public void afterPropertiesSet()
  {
    baseWMS = grailsApplication.config.wms.base.layers
    dataWMS = grailsApplication.config.wms.data.raster
    tagHeaderList = grailsApplication.config.rasterEntry.tagHeaderList
    tagNameList = grailsApplication.config.rasterEntry.tagNameList
  }


  def list = {
    params.max = Math.min(params.max ? params.int('max') : 10, 100)
    params.offset = params.offset ?: 0
    params.sort = params.sort ?: "id"
    params.order = params.order ?: "asc"

    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

//    println "list->queryParams: ${queryParams.toMap()}"

    def initialRequest = g.createLink(action: "query.json", params: queryParams.toMap())


    initialRequest = initialRequest.substring(initialRequest.indexOf('?') + 1)

    def myColumnDefs = [
            [key: 'id', label: 'Id', sortable: true, resizeable: true],
            [key: 'entryId', label: 'Entry Id', sortable: true, resizeable: true],
            [key: 'width', label: 'Width', sortable: true, resizeable: true],
            [key: 'height', label: 'Height', sortable: true, resizeable: true],
            [key: 'numberOfBands', label: 'Num Bands', sortable: true, resizeable: true],
            [key: 'numberOfResLevels', label: 'Num Res Levels', sortable: true, resizeable: true],
            [key: 'bitDepth', label: 'Bit Depth', sortable: true, resizeable: true],
            [key: 'metersPerPixel', label: 'Meters Per Pixel', sortable: false, resizeable: true],
            [key: 'minLon', label: 'Min Lon', sortable: false, resizeable: true],
            [key: 'minLat', label: 'Min Lat', sortable: false, resizeable: true],
            [key: 'maxLon', label: 'Max Lon', sortable: false, resizeable: true],
            [key: 'maxLat', label: 'Max Lat', sortable: false, resizeable: true],
            [key: 'acquisitionDate', label: 'Acquisition Date', sortable: true, resizeable: true],
            [key: 'filename', label: 'Filename', sortable: true, resizeable: true]

    ]

    def fields = [
            [key: "id", parser: "number"],
            [key: "entryId"],
            [key: "width", parser: "number"],
            [key: "height", parser: "number"],
            [key: "numberOfBands", parser: "number"],
            [key: "numberOfResLevels", parser: "number"],
            [key: "bitDepth", parser: "number"],
            [key: "metersPerPixel", parser: "number"],
            [key: "minLon", parser: "number"],
            [key: "minLat", parser: "number"],
            [key: "maxLon", parser: "number"],
            [key: "maxLat", parser: "number"],
            [key: "acquisitionDate"],
            [key: "filename"]
    ]

    for ( i in 0..<tagNameList.size() )
    {
      myColumnDefs << [key: tagNameList[i], label: tagHeaderList[i],
              sortable: true, resizeable: true]

      fields << [key: tagNameList[i]]
    }

    return [
            initialRequest: initialRequest,
            myColumnDefs: myColumnDefs as JSON,
            fields: fields as JSON
    ]
  }


  def query = {

    params.max = Math.min(params.max ? params.int('max') : 10, 100)
    params.offset = params.offset ?: 0
    params.sort = params.sort ?: "id"
    params.order = params.order ?: "asc"

    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

//    println "query->queryParams: ${queryParams.toMap()}"

    def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)
    def rasterEntryTotal = rasterEntrySearchService.getCount(queryParams)


//    println "total: ${rasterEntryTotal}"

    def results = rasterEntries.collect {
      def bounds = it.groundGeom?.bounds

      def records = [
              id: it.id,
              entryId: it.entryId,
              width: it.width,
              height: it.height,
              numberOfBands: it.numberOfBands,
              numberOfResLevels: it.numberOfResLevels,
              bitDepth: it.bitDepth,
              metersPerPixel: it.metersPerPixel,
              minLon: bounds.minLon,
              minLat: bounds.minLat,
              maxLon: bounds.maxLon,
              maxLat: bounds.maxLat,
              acquisitionDate: it.acquisitionDate,
              filename: it.mainFile.name
      ]


      for ( i in 0..<tagNameList.size() )
      {
        records[tagNameList[i]] = it[tagNameList[i]] as String
      }

      return records
    }

    withFormat {
      json {
        def data = [
                totalRecords: rasterEntryTotal,
                results: results
        ]

        render contentType: "application/json", text: data as JSON
      }
      xml {
        def data = [
                totalRecords: rasterEntryTotal,
                results: results
        ]

        render contentType: "application/xml", text: data as XML
      }
    }
  }

}
