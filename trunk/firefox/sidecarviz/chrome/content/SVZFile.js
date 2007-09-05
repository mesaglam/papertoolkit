var SVZFile = {
	init: function () {
		alert(this.getHomeFile());
	},
	
	getHomeFile : function(childFile) {
		return this.getSystemFile("Home", childFile);
	},
	getTempFile : function(childFile) {
		return this.getSystemFile("TmpD", childFile);
	},
	getSystemFile : function(location, childFile) {
		var file = Cc["@mozilla.org/file/directory_service;1"].getService(Ci.nsIProperties).get(location, Ci.nsILocalFile);
  		if (childFile) {
			file.append(childFile);
  		}
		return file;
	}
};
SVZFile.init();