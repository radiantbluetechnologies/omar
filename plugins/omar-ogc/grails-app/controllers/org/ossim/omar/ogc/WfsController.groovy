package org.ossim.omar.ogc

import org.apache.commons.collections.map.CaseInsensitiveMap

import groovy.xml.StreamingMarkupBuilder

import geoscript.filter.Filter

class WfsController
{
  def webFeatureService

  def index()
  {
    def results, contentType
    def wfsCommand = new WfsCommand()
    try
    {
      //println request.method

      switch ( request.method.toUpperCase() )
      {
      case "POST":
        def builder = new StreamingMarkupBuilder()

        //println "POST: ${ builder.bind { mkp.yield request.XML } }"

        wfsCommand.service = request.XML.@service
        wfsCommand.version = request.XML.@version
        wfsCommand.request = request.XML.name()

        if ( wfsCommand.request?.toUpperCase() == "DESCRIBEFEATURETYPE" )
        {
          wfsCommand.typeName = request.XML.TypeName.text()
        }
        else if ( wfsCommand.request?.toUpperCase() == "GETFEATURE" )
        {
          def foo = new StreamingMarkupBuilder().bindNode( request.XML ).toString()
          def x = new XmlSlurper().parseText( foo )

          def max =  x.@maxFeatures.text()
          def start =  x.@offset?.text()

          wfsCommand.with {
            service = x.@service?.text() ?: "WFS"
            version = x.@version?.text() ?: "1.0.0"
            request = x.name() ?: request ?: "GetFeature"
            typeName = x.Query.collect { it.@typeName.text() }?.first()
            filter = x.Query.collect { new StreamingMarkupBuilder().bindNode( it.Filter ).toString().trim() }?.first()

            maxFeatures = ( max ) ? max.toInteger() : 1000
            offset = ( start ) ? start.toInteger() : 0
            outputFormat = x.@outputFormat?.text() ?: "GML2"
          }
        }
        break

      case "GET":
          def tempParams = new CaseInsensitiveMap( params )

          def wfsParams = tempParams.subMap(
                  ['service', 'version', 'request', 'resultType', 'typeName', 'filter', 'outputFormat', 'bbox', 'maxFeatures', 'offset','sortBy']
          )
          bindData( wfsCommand, wfsParams )
          if((!wfsCommand.maxFeatures)||(wfsCommand.maxFeatures.toInteger() > 1000))
          {
              wfsCommand.maxFeatures = 1000
          }

          break
      }

      //println wfsCommand

      switch ( wfsCommand.request?.toUpperCase() )
      {
      case "GETCAPABILITIES":
        (results, contentType) = webFeatureService.getCapabilities( wfsCommand )
        break
      case "DESCRIBEFEATURETYPE":
        (results, contentType) = webFeatureService.describeFeatureType( wfsCommand )
        break
      case "GETFEATURE":
        (results, contentType) = webFeatureService.getFeature( wfsCommand );
          if (params.callback){
              results="${params.callback}(${results});";
          }
        break
      default:
        throw new Exception( "Unsupported Operation: ${ wfsCommand.request }" )
      }

      if ( !results )
      {
        throw new Exception( "Unknown Exception: ${ wfsCommand.request }" )
      }

    }
    catch ( Exception e )
    {
      results = new StreamingMarkupBuilder().bind() {
        mkp.xmlDeclaration()
        mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )
        ServiceExceptionReport( version: "1.2.0", xmlns: "http://www.opengis.net/ogc",
            'xsi:schemaLocation': "http://www.opengis.net/ogc http://schemas.opengis.net/wfs/1.0.0/OGC-exception.xsd" ) {
          ServiceException( code: "GeneralException", e.message )
        }
      }.toString()
      //contentType = 'application/vnd.ogc.se_xml'
      contentType = 'application/xml'
    }
    finally
    {
        def attachment ="WFS-${wfsCommand.outputFormat}"
        response.setHeader( "Content-disposition", "attachment; ${attachment}" )
        // println "contentType: ${contentType}, text: ${results}"
        render contentType: contentType, text: results
    }
  }
}
