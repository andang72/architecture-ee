package tests;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.ee.util.Blowfish;

public class BlowfishTest {

	private static Logger log = LoggerFactory.getLogger(BlowfishTest.class);
	
	@Test
	public void encoding () {
		Blowfish bf = new Blowfish();
		String enc = bf.encrypt("hello");
		String dec = bf.decrypt(enc);
		log.debug("{} to {}", enc, dec);
		Assert.assertEquals("hello", dec);
	}
}
