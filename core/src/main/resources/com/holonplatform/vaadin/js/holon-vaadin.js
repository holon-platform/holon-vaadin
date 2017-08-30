window.holon_vaadin = {
	getChar : function(event) {
		if (event.which === null) {
			return String.fromCharCode(event.keyCode);
		} else if (event.which !== 0 && event.charCode !== 0) {
			return String.fromCharCode(event.which);
		}
		return null;
	}
};

window.com_holonplatform_vaadin_js_ElementPropertyHandler = function() {

	var el = this.getElement(this.getParentId());

	this.setprp = function(name, value) {
		el[name] = value;
	}
	
	this.rmvprp = function(name) {
		el.removeAttribute(name);
	}

	this.seteh = function(evtname, js) {
		js = eval("(" + js + ")");
		el[evtname] = js;
	};

}
