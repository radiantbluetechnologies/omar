OMAR.models.BBOX = Backbone.Model.extend(
{
    defaults:{
        "minx":-180.0,
        "miny":-90.0,
        "maxx":180.0,
        "maxy":90.0
    },
    initialize:function(options)
    {
    },
    toWmsString:function()
    {
        return (this.get("minx")+","+this.get("miny")+","+
                this.get("maxx")+","+this.get("maxy"));
    },
    validate:function(attrs)
    {
        if(attrs.minx&&(!OMAR.isFloat(attrs.minx.toString())))
        {
            return ("Minx value is invalid: " + attrs.minx);
        }
        if(attrs.miny&&(!OMAR.isFloat(attrs.miny.toString())))
        {
            return ("Miny value is invalid: " + attrs.miny);
        }
        if(attrs.maxx&&(!OMAR.isFloat(attrs.maxx.toString())))
        {
            return ("Maxx value is invalid: " + attrs.maxx);
        }
        if(attrs.maxy&&(!OMAR.isFloat(attrs.maxy.toString())))
        {
            return ("Maxy value is invalid: " + attrs.maxy);
        }
        return null;
    },
    setFromWmsString:function(s)
    {
        var splitBounds = s.split(",");
        if(splitBounds.length == 4)
        {
            this.minx =parseFloat(splitBounds[0]);
            this.miny =parseFloat(splitBounds[1]);
            this.maxx =parseFloat(splitBounds[2]);
            this.maxy =parseFloat(splitBounds[3]);

        }

        return this;
    },
    toCql:function(columnName)
    {
        var result = "";
        var bad = this.validate(this.attributes);
        if(!bad)
        {
            result = "BBOX("+columnName+","+this.toWmsString()+")";
        }

        return result;
    }
}
);

OMAR.views.BBOX = Backbone.View.extend({
    el:"#BBOXId",
    initialize: function(params)
    {
        // this should create a variable for us called this.$el
        this.setElement(this.el);
        this.model = new OMAR.models.BBOX();
        this.lowerLeftBboxEl = $("#lowerLeftBbox");
        this.upperRightBboxEl = $("#upperRightBbox");
        this.model.on("error",
            function(model,err) {
                alert("BBOX Has errors: " + err);
            });
        this.model.on('change', this.bboxModelChange, this);

    },
    events:{
        "change #lowerLeftBbox" : "llOnChange",
        "change #upperRightBbox": "urOnChange"
    },

    bboxModelChange: function() {
        this.render();
    },

    llOnChange: function(e){
        var v = this.lowerLeftBboxEl.val();
        var values = v.split(",");
        if(values.length ==2)
        {
            this.model.off("change", this.bboxModelChange, this);
            this.model.set({minx:values[1],miny:values[0]});
            this.model.on("change", this.bboxModelChange, this);
        }
        this.render();
    },
    urOnChange: function(e){
        var v = this.upperRightBboxEl.val();
        var values = v.split(",");
        if(values.length ==2)
        {
            this.model.off("change", this.bboxModelChange, this);
            this.model.set({maxx:values[1],maxy:values[0]});
            this.model.on("change", this.bboxModelChange, this);
        }
        this.render();
    },
    bboxError:function(model, err){
    },
    render:function()
    {
        this.lowerLeftBboxEl.val(this.model.get("miny")+","
            +this.model.get("minx"));
        this.upperRightBboxEl.val(this.model.get("maxy")+","
            +this.model.get("maxx"));
    }
});

