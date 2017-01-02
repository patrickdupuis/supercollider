+ AbstractGroup {
	asGroup {}
}

+ Nil {
	asGroup {
		^Server.default.defaultGroup
	}
}
+ Server {
	asGroup {
		^defaultGroup
	}
}
+ Synth {
	asGroup {
		^this.group
	}
}
+ Integer {
	asGroup {
		^Group.basicNew(nil, this)
	}
}
