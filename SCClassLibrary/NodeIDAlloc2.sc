NodeIDAlloc2 {

	var <userID, <lowestTempID, <numUsers;
	var <size, <idOffset, <maxID, <numPerm, <maxPermID;
	var tempCount = -1, permCount = 1, permFreed;

	*new { arg userID = 1, lowestTempID = 1000, numUsers = 32;
		^super.newCopyArgs(userID, lowestTempID, numUsers).reset
	}

	reset {
		// optimize for human readability:
		// 10 ** 8 ids for 1 user, ** 7 for < 10, ** 6 for < 100
		// prefix 20000... is for clientID 2, 120000... for 12 etc
		size = (2 ** 31 / numUsers).round(1);
		size = (10 ** size.asString.size);

		idOffset = size * userID;
		// drop 100000 prefix for first user:
		if (userID == 1) { idOffset = 0 };

		permFreed = IdentitySet.new;
		maxPermID = idOffset + lowestTempID - 1;
		tempCount = -1;
		permCount = 1;
	}

	alloc {
		tempCount = tempCount + 1;
		^(lowestTempID + tempCount).wrap(lowestTempID, size) + idOffset;
	}

	isPerm { |num|
		// 0 and 1 are also permanent
		^num.inclusivelyBetween(idOffset, maxPermID);
	}

	allocPerm {
		var perm;
		if(permFreed.size > 0) {
			perm = permFreed.minItem;
			permFreed.remove(perm);
			^perm
		};

		permCount = (permCount + 1).min(lowestTempID);
		perm = permCount;
		if (perm >= lowestTempID) {
			warn("%: cannot create more than % permanent ids."
				"\nPlease free some permanent ids first,"
				"or set lowestTempID higher."
				.format(thisMethod, perm)
			);
			^nil
		};
		^perm + idOffset
	}

	freePerm { |id|
		if (id.isPerm) { permFreed.add(id) }
	}
}
