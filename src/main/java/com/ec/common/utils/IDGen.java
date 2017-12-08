package com.ec.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 浜х敓涓�涓敮涓�ID
 */
public class IDGen {


	private static final long SUFFIX=1000;
	private static final int PREFIX=100;
	private static final AtomicLong RANDOM=new AtomicLong(new Random().nextInt(PREFIX));
	
	
	public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyMMdd");
	private static final AtomicLong ID = new AtomicLong (((Long.parseLong(FORMAT.format(new Date()))*PREFIX+ RANDOM.incrementAndGet()) * SUFFIX));
	
	
	
	
	
	/**
	 * 浜х敓鍞竴ID
	 * @return
	 */
	public static long next() {
		return ID.incrementAndGet();
	}
	
	public static long reset() {
		if(RANDOM.get()>PREFIX)
			RANDOM.set(0);
		ID.set(((Long.parseLong(FORMAT.format(new Date()))*PREFIX+ RANDOM.incrementAndGet()) * SUFFIX)) ;
		return ID.get();
	}
	
	
	public static int suffixReqId(Long  reqId){
		if(reqId==null)return 0;
		return Long.valueOf(reqId/getfix()).intValue();
	}
	

	private static long getfix() {
		return SUFFIX*PREFIX;
	}
	
	public static void main(String[] args) throws Exception {
		for(int j=0;j<110;j++){
			System.err.println(IDGen.reset());
		}
		
	}


}