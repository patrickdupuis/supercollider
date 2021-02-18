TestPprotect : UnitTest {

	test_resetExceptionHandler_onError {
		var routine, stream, success = false, condition = false;
		// Note that this must be a Stream, not a Pattern (x.asStream --> x).
		// If it's a pattern, then we don't have access to the routine
		// to check its exceptionHandler below.
		routine = Routine { 0.0.wait; 1 + nil };  // throw an error
		stream = Pprotect(
			routine,
			{
				success = routine.exceptionHandler.isNil;
				condition = true;
			}
		).asStream;
		// Note that it is necessary to do this asynchronously!
		// Otherwise "routine"'s error will halt the entire test suite.
		stream.play;
		this.wait({ condition }, maxTime: 0.1);

		this.assert(success, "Pprotect should clear the stream's exceptionHandler");
	}

	test_stream_can_be_restarted_after_error {
		var pat, stream;
		var condition = false;

		pat = Pprotect(
			Prout {
				0.01.yield;
				condition = true;
				Error("dummy error").throw
			},
			{ stream.streamError }
		);

		stream = pat.play;
		this.wait({ condition }, maxTime: 0.1);

		condition = false;
		stream.reset;
		stream.play;
		this.wait({ condition }, maxTime: 0.1);

		this.assert(condition, "stream should be resettable after an error");
	}

	test_task_proxy_play_after_error {
		var proxy, redefine, hasRun;
		var condition = false;

		proxy = TaskProxy.new;
		proxy.quant = 0;
		proxy.play;

		redefine = {
			proxy.source = {
				0.01.wait;
				condition = true;
				Error("dummy error").throw
			}
		};

		condition = false;
		redefine.value;
		this.wait({ condition }, maxTime: 0.1);

		condition = false;
		redefine.value;
		this.wait({ condition }, maxTime: 0.1);

		this.assert(condition, "task proxy should play again after an error");
	}

	test_nested_instances {
		var innerHasBeenCalled = false, outerHasBeenCalled = false;
		var condition = false;

		fork {
			var stream;
			stream = Pprotect(
				Pprotect(
					Prout {
						Error("dummy error").throw
					}, {
						condition = true;
						innerHasBeenCalled = true
					}
				),
				{
					condition = true;
					outerHasBeenCalled = true
				}
			).asStream;

			stream.next;

		};

		this.wait({ condition }, maxTime: 0.1);

		this.assert(innerHasBeenCalled, "When nesting Pprotect, inner functions should be called");
		this.assert(outerHasBeenCalled, "When nesting Pprotect, outer functions should be called");
	}

}
