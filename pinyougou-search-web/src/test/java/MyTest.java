public class MyTest {

    public static void main(String[] args){
        String keywords = "三星 手机";
        String newKeywords = keywords.replace(" ","");
        String replaceAll = keywords.replaceAll(" ","");
        System.out.println(keywords);
        System.out.println(newKeywords);
        System.out.println(replaceAll);
    }
}
