package tests;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {

	public DateTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleDateFormat sf2 = new SimpleDateFormat("yyyy.MM.dd HH:MM:SS");
		System.out.println(sf2.format(new Date()));
	}

}
