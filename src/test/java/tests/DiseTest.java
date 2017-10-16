package tests;

import java.util.Random;

public class DiseTest {
	
	
	public class CardNumberHolder {
        int firstNum;
        int lastNum;
        
        public CardNumberHolder (int firstNum, int lastNum ){
            this.firstNum = firstNum;
            this.lastNum = lastNum;
        }
        
        public String toString() {
        		return  firstNum + "-" + lastNum ;
        }
    }
	
	public void run() {
		
		Random randomObject = new Random();
		
		 // 홀수 합 모음 : 10% 상품 할인
       java.util.List<CardNumberHolder> list1 = new java.util.ArrayList<CardNumberHolder> ();
       
       list1.add(new CardNumberHolder(1, 2));
       list1.add(new CardNumberHolder(2, 1));
       list1.add(new CardNumberHolder(2, 3));
       list1.add(new CardNumberHolder(3, 2));

       // 짝수 합 모음 : 20% 상품 할인
       java.util.List list2 = new java.util.ArrayList ();
       list2.add(new CardNumberHolder(1, 1));
       list2.add(new CardNumberHolder(1, 3));
       list2.add(new CardNumberHolder(2, 2));
       list2.add(new CardNumberHolder(3, 1));
       list2.add(new CardNumberHolder(3, 3));

       int firstCardNum = 0;
       int lastCardNum = 0;
       
       int count1 = 0 ;
       int count2 = 0 ;
       
       for( int i = 0 ; i < 1000 ; i ++ )
       {
           if( randomObject.nextDouble() < 0.3 ){
               java.util.Collections.shuffle(list1);
               Object CardNumberHolderObject = list1.get(0);
               CardNumberHolder cardNumberHolder = (CardNumberHolder)CardNumberHolderObject;
               firstCardNum = cardNumberHolder.firstNum;
               lastCardNum = cardNumberHolder.lastNum;
           }else{
               java.util.Collections.shuffle(list2);
               Object CardNumberHolderObject = list2.get(0);
               CardNumberHolder cardNumberHolder = (CardNumberHolder)CardNumberHolderObject;
               firstCardNum = cardNumberHolder.firstNum;
               lastCardNum = cardNumberHolder.lastNum;
           }
           int sum = firstCardNum + lastCardNum ;
           System.out.println( firstCardNum + "+" + lastCardNum +  " = " + sum  + ", " + sum % 2);    
           
           if( sum % 2 == 0)
           	    count2 ++ ;
           else 
           		count1 ++ ;
       }
       
       System.out.println( "10-" + count1  );
       System.out.println( "20-" + count2  );		
       
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println( "--------" );
		(new DiseTest()).run();
		System.out.println( "--------" );

	}
}
