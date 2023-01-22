import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new ConcurrentHashMap<>();
    //ConcurrentHashMap — это потокобезопасная версия класса HashMap.
    public static void main(String[] args) throws InterruptedException {

        Thread maxOutThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    int maxCount =0;
                    int maxCountKey = 0;
                    for(Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
                        if (entry.getValue() > maxCount) {
                            maxCount = entry.getValue();
                            maxCountKey = entry.getKey();
                        }
                    }
                    System.out.println("Текущий лидер среди частот: " + maxCountKey + " (встретилось " + maxCount + " раз)");
                }
            }
        });
        maxOutThread.start();

        int numberThread = 1000; //Количество потоков равно количеству генерируемых маршрутов и равно 1000.
        Thread[] threads = new Thread[numberThread]; //Массив потоков [1000]
        for (int i = 0; i < numberThread; i++) {
            threads[i] = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);// В потоке генерируем текст используя метод generateRoute.
                int countR = countR(route); //Считаем количество команд поворота направо (буквы 'R') используя метод countR.
                synchronized (sizeToFreq) {//Гарантируем, что только один поток может получить доступ к блоку кода. Занимает экран объекта sizeToFreq.
                    if (sizeToFreq.containsKey(countR)) { // Проверяем у МАПЫ есть ли у неё данный ключ (countR)
                        sizeToFreq.put(countR, sizeToFreq.get(countR) + 1); // Если есть, то меняем значение этого ключа добавляя еденицу(key(countR)/value = intV +1)
                    } else {
                        sizeToFreq.put(countR, 1);
                        // Если ещё не было данного ключа (countR), то добавляем в коллекцию ключ-значение (присвоив значению еденицу (valeu = 1))
                    }
                }
            });
            threads[i].start(); // Запускаем поток(нить).
        }
        for (Thread thread : threads) {
            thread.join(); // Ждём каждый поток.
        }
        int maxCount = 0;
        int maxCountKey = 0;
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxCountKey = entry.getKey();
            }
        }
        System.out.println("Самое частое количество повторений " + maxCountKey + " (встретилось " + maxCount + " раз)");
        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getKey() != maxCountKey) {
                System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
            }
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int countR(String route) {
        int countR = 0;
        for (int i = 0; i < route.length(); i++) {
            if (route.charAt(i) == 'R') {
                countR++;
            }
        }
        return countR;
    }
}
