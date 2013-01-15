OMAR.models.Wfs = Backbone.Model.extend({
    defaults:{
        "url":"/omar/wfs",
        "service":"WFS",
        "version":"1.1.0",
        "request":"getFeature",
        "typeName":"raster_entry",
        "filter":"",
        "outputFormat":"JSON",
        "maxFeatures":"",
        "offset":"",
        "resultType":"",
        "sort":"",// json formatted array of arrays

        /* These attributes will be set after the fetch */
        "numberOfFeatures":0,
        "getFeatureResult":{}
    },
    initialize:function(params){
    },
    toUrlParams: function(includeNullPropertiesFlag){
        var result = ""

        if(!includeNullPropertiesFlag) includeNullPropertiesFlag = false
        for(var propertyName in this.attributes)
        {
            if(propertyName!="url")
            {
                var value = this.get(propertyName);
                if(value == null)
                {
                    value = "";
                }
                if(((value==="")&&includeNullPropertiesFlag) || !(value===""))
                {

                    if(result)
                    {
                        result = result +"&"+propertyName + "=" + escape(value);
                    }
                    else
                    {
                        result = propertyName + "=" + escape(value);
                    }
                }
            }
        }
        return result;
    },
    parse:function(response){
        //alert("response = " + JSON.stringify(response) );

        var result = this.attributes;

        if(response.numberOfFeatures)
        {
            result.numberOfFeatures = response.numberOfFeatures;
        }
        else{
            result.getFeatureResult = response;
        }
        //alert(result);
        return result;
    },
    toUrl:function(includeNullPropertiesFlag){
        var result =this.toUrlParams(includeNullPropertiesFlag);
        var url = this.get("url");
        if(url&&(url!=""))
        {
            result = (url + "?" + result);
        }

        return result;
    },
    fetchCount:function(){
        if(this.fetchCountAjax&&this.fetchCountAjax.abort)
        {
            this.fetchCountAjax.abort();
        }
        var thisPtr = this;

        var countClone = this.clone();
        countClone.attributes.numberOfFeatures = 0;
        countClone.attributes.getFeatureResult = ""
        countClone.attributes.resultType = "hits";
        countClone.url = countClone.toUrl()+"&callback=?";
        this.fetchCounAjax = countClone.fetch(
            {cache:false,
                "success":function(){
                    thisPtr.attributes.numberOfFeatures = countClone.attributes.numberOfFeatures;
                    thisPtr.trigger("onNumberOfFeaturesChange", thisPtr);
                    thisPtr.fetchCounAjax = null;
                }
            });
    },
    fetchGetFeatureResult:function(){
        if(this.fetchGetFeatureAjax&&this.fetchGetFeatureAjax.abort)
        {
            this.fetchGetFeatureAjax.abort();
        }
        var thisPtr = this;
        var countClone = this.clone();
        countClone.attributes.numberOfFeatures = -1;
        countClone.attributes.getFeatureResult = ""
        countClone.attributes.resultType = "hits";
        countClone.url = countClone.toUrl()+"&callback=?";
        this.fetchGetFeatureAjax = countClone.fetch(
            {cache:false,
                "success":function(){
                    thisPtr.attributes.getFeatureResult = countClone.attributes.getFeatureResult;
                    thisPtr.trigger("onGetFeatureResultChange");
                    thisPtr.fetchGetFeatureAjax = null;
                }
            });
    }
});