package org.edgegallery.atp.model.testcase;

import org.edgegallery.atp.constant.Constant;

import lombok.Getter;
import lombok.Setter;;

@Setter
@Getter
public class TestCaseResult {
	/**
	 * test case execute result.The value is enum:success,failed or running.
	 */
	String result;
	
	/**
	 * test case fail reason,it can be empty when the result is not failed.
	 */
	String reason;
	
	public TestCaseResult() {
		this.result = Constant.Result.RUNNING;
		this.reason = Constant.EMPTY;
	}
	
	public TestCaseResult(String result,String reason) {
		this.result = result;
		this.reason = reason;
	}
}
