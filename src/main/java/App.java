import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
    public void run() {

        Scanner sc = new Scanner(System.in);
        Map<Integer, WiseSaying> catalog = new HashMap<>();

        System.out.println("== 명언 앱 ==");
        while (true) {

            System.out.print("명령) ");
            String command = sc.nextLine().trim();;


            if(command.startsWith("삭제")) {
                //"삭제?id=___"에서 id값 추출
                String[] parts = command.split("//?");  // "삭제"와 "id=_" 분리
                if(parts[1].startsWith("id=")) {    // id=__의 명령어 형태가 올바른지 확인
                    int id = Integer.parseInt(parts[1].substring(3));    // id값 추출

                    if(catalog.containsKey(id)) {       // 카탈로그에 해당 id를 가진 명언이 존재하는지 확인
                        catalog.remove(id);             // id에 맞는 명언 삭제
                        System.out.println(id + "번 명언이 삭제되었습니다.");
                    } else {                            // 존재하지 않는 명언 예외처리
                        System.out.println(id + "번 명언은 존재하지 습니다.");
                    }
                } else {                                //잘못된 명령어 예외처리
                    System.out.println("잘못된 명령어 형식입니다. 명령어 예시 : 삭제?id=1");
                }

                continue;
            }

            switch (command) {
                case "등록":
                    System.out.print("명언 : ");
                    String 명언 = sc.nextLine().trim();

                    System.out.print("작가 : ");
                    String 작가 = sc.nextLine().trim();

                    WiseSaying wiseSaying = new WiseSaying(명언, 작가);
                    catalog.put(wiseSaying.getId(), wiseSaying);

                    System.out.println(wiseSaying.getId() + "번 명령이 등록되었습니다.");
                    break;

                case "목록":
                    System.out.println("번호 / 작가 / 명언");
                    System.out.println("----------------------");

                    if (catalog.isEmpty()) {
                        System.out.println("등록된 명언이 없습니다.");
                    } else {
                        for (WiseSaying ws : catalog.values()) {
                            ws.getCatalog();
                        }
                    }
                    break;

                case "종료":
                    System.exit(0);
                    break;

                default: // 잘못된 명령 처리
                    System.out.println("알 수 없는 명령어입니다. 다시 입력해주세요.");
                    break;
            }
        }
    }
}

class WiseSaying {
    private static int idCounter = 0;
    private int id;
    private String 명언;
    private String 작가;

    public WiseSaying() {
    }

    // 생성자
    public WiseSaying(String 명언, String 작가) {
        id = ++idCounter;
        this.명언 = 명언;
        this.작가 = 작가;
    }

    // id getter
    public int getId() {
        return id;
    }

    // 명언 gtter/setter
    public String get명언() {
        return 명언;
    }

    public void set명언(String 명언) {
        this.명언 = 명언;
    }

    // 작가 getter/setter
    public String get작가() {
        return 작가;
    }

    public void set작가(String 작가) {
        this.작가 = 작가;
    }

    public void getCatalog() {
        System.out.println(id + " / " + 명언 + " / " + 작가);
    }
}

