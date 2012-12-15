package com.vaguehope.cmstoad;

import java.io.PrintStream;

public interface CliAction {

	void run (PrintStream out, PrintStream err) throws Exception;

}
