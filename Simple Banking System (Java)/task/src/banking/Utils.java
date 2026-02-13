package banking;

import java.util.Random;

public class Utils {

    public static String formatString(int number, int digits){
        String formatted = String.format("%0" + digits + "d", number);
        return formatted;
    }

    public static String randomCard(){
        Random random = new Random();
        int min = 100000000; // Минимальное шестизначное число
        int max = 999999999; // Максимальное шестизначное число
        int nineDigitNumber = random.nextInt(max - min + 1) + min;
        return formatString(nineDigitNumber, 9);
    }
    public static String randomPin(){
        Random random = new Random();
        int min = 1000; // Минимальное шестизначное число
        int max = 9999; // Максимальное шестизначное число
        int nineDigitNumber = random.nextInt(max - min + 1) + min;
        return formatString(nineDigitNumber, 4);
    }

    public static String calculateLuhnDigit(String base){


        String[] doble = new String[15];

        char[] card = base.toCharArray();
        int sum = 0;
        int member;
        int numberLuhn;
        for(int i=0; i< card.length; i++ ) {
            if((i)%2==0){
                doble[i]=String.valueOf((card[i]-'0')*2);
            }else {
                doble[i]=String.valueOf((card[i]-'0'));
            }
        }
        for(int i=0; i< doble.length; i++ ) {
            member = Integer.parseInt(doble[i]);
            if(member>9) {
                member -=9;
                doble[i]= String.valueOf(Integer.parseInt(doble[i])-9);
                sum+=member;
            }else {
                sum+=member;
            }
        }
        numberLuhn = (10 - sum%10)%10;
        return String.valueOf(numberLuhn);
    }
}
