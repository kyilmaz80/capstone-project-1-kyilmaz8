import java.util.Scanner;

public class Menu {
    public static void printHeader() {
        System.out.println("***** Gelişmiş Hesap Makinesi *****");
        System.out.println("Toplama: + | Çıkarma: - | Çarpma: * | Bölme: /");
        System.out.println("Karekök: sqrt(x) | Üs alma: pow(x,y) | Sin: sin(x) | Cos: cos(x) | Tan: tan(x)");
        System.out.println("Çıkmak için: exit");
    }
    public static String getInput() {
        System.out.print("İşlemi girin: ");
        Scanner scanner = new Scanner(System.in);
        String prompt = scanner.nextLine();
        return StringUtils.removeSpaces(prompt.toLowerCase());
    }
    public static void exit() {
        System.out.println("Hesap makinesi kapatılıyor. İyi günler!");
    }

    public static void printOutput() {
        System.out.print("Sonuç:");
    }
}
