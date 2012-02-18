import org.ossim.omar.core.MapPropertyEditor
import org.ossim.omar.core.ISO8601DateParser
import org.ossim.omar.core.CustomEditorRegistrar

class OmarCoreGrailsPlugin
{
  // the plugin version
  def version = "0.1"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "1.2.2 > *"
  // the other plugins this plugin depends on
  def dependsOn = [
          'jodaTime': "1.2 > *"
  ]
  // resources that are excluded from plugin packaging
  def pluginExcludes = [
          "grails-app/views/error.gsp"
  ]

  // TODO Fill in these fields
  def author = "Scott Bortman"
  def authorEmail = "sbortman@radiantblue.com"
  def title = "OMAR core"
  def description = '''\\
This plugin contains OMAR code that can be shared or accessed from other OMAR plugins. 
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/omar-core"

  def doWithWebDescriptor = { xml ->
  }

  def doWithSpring = {
    customEditorRegistrar(CustomEditorRegistrar)
    mapPropertyEditor(MapPropertyEditor)
  }

  def doWithDynamicMethods = { ctx ->
    // TODO Implement registering dynamic methods to classes (optional)
    String.metaClass.toDateTime {->
      ISO8601DateParser.parseDateTime(delegate)
    }
    String.metaClass.toDate {->

      def date = null
      def dateTime = ISO8601DateParser.parseDateTime(delegate.trim())
      if ( dateTime )
      {
        date = new Date(dateTime.millis)
      }

      date
    }
  }

  def doWithApplicationContext = { applicationContext ->
    // TODO Implement post initialization spring config (optional)
  }

  def onChange = { event ->
    // TODO Implement code that is executed when any artefact that this plugin is
    // watching is modified and reloaded. The event contains: event.source,
    // event.application, event.manager, event.ctx, and event.plugin.
  }

  def onConfigChange = { event ->
    // TODO Implement code that is executed when the project configuration changes.
    // The event is the same as for 'onChange'.
  }
}
