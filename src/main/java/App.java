import com.google.gson.*;

import java.io.*;
import java.util.*;

public class App {
    private Scanner scanner = new Scanner(System.in);
    private Map<Integer, WiseSaying> sayings = new HashMap<>();
    private static final String DIRECTORY_PATH = "D:/Coding/wiseSaying/db/wiseSaying"; // 기본 경로
    private int lastId = 1; // ID 초기값

    public void run() {
        loadAllFiles();
        System.out.println("== 명언 앱 ==");
        loadAllFiles(); // 시작 시 JSON 파일 로드
        while (true) {
            System.out.print("명령) ");
            String input = scanner.nextLine().trim();

            if (input.startsWith("삭제") || input.startsWith("수정")) {
                processModifyCommand(input);
                continue;
            }

            switch (input) {
                case "등록":
                    addSaying();
                    break;
                case "목록":
                    showSayings();
                    break;
                case "종료":
                    System.exit(0);
                    break;
                default:
                    System.out.println("알 수 없는 명령어입니다. 다시 입력해주세요.");
            }
        }
    }

    private void processModifyCommand(String command) {
        String action = command.startsWith("삭제") ? "삭제" : "수정";
        String[] parts = command.split("\\?");
        boolean isValidCommand = parts.length > 1 && parts[1].startsWith("id=");

        if (!isValidCommand) {
            System.out.println("잘못된 명령어 형식입니다. 명령어 예시 : " + action + "?id=1");
            return;
        }

        int id = Integer.parseInt(parts[1].substring(3));

        if (!sayings.containsKey(id)) {
            System.out.println(id + "번 명언은 존재하지 않습니다.");
            return;
        }

        if (action.equals("삭제")) {
            sayings.remove(id);
            System.out.println(id + "번 명언이 삭제되었습니다.");
        } else {
            updateSaying(id);
        }
    }

    private void updateSaying(int id) {
        WiseSaying targetSaying = sayings.get(id);

        System.out.println("명언(기존) : " + targetSaying.getQuote() + "\n명언 : ");
        targetSaying.setQuote(scanner.nextLine().trim());

        System.out.println("작가(기존) : " + targetSaying.getAuthor() + "\n작가 : ");
        targetSaying.setAuthor(scanner.nextLine().trim());

        try {
            saveFile(targetSaying);
        } catch (IOException e) {
            System.err.println("명언 수정 중 파일 저장 오류: " + e.getMessage());
        }
    }

    private void addSaying() {
        System.out.print("명언 : ");
        String quote = scanner.nextLine().trim();

        System.out.print("작가 : ");
        String author = scanner.nextLine().trim();

        WiseSaying newSaying = new WiseSaying(quote, author, lastId);

        try {
            saveFile(newSaying);
        } catch (IOException e) {
            System.err.println("명언 등록 중 파일 저장 오류: " + e.getMessage());
        }

        sayings.put(newSaying.getId(), newSaying);
        System.out.println(newSaying.getId() + "번 명언이 등록되었습니다.");
        lastId++; // ID 증가
    }

    private void showSayings() {
        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");

        if (sayings.isEmpty()) {
            System.out.println("등록된 명언이 없습니다.");
        } else {
            for (WiseSaying saying : sayings.values()) {
                saying.display();
            }
        }
    }

    private void saveFile(WiseSaying wiseSaying) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (!isCreated) {
                throw new IOException("디렉토리 생성 실패");
            }
        }

        String filePath = DIRECTORY_PATH + File.separator + wiseSaying.getId() + ".json";

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(wiseSaying, writer);
        }
    }

    void loadAllFiles() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File directory = new File(DIRECTORY_PATH);

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("저장된 JSON 파일이 없습니다.");
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            System.out.println("저장된 JSON 파일이 없습니다.");
            return;
        }

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                WiseSaying wiseSaying = gson.fromJson(reader, WiseSaying.class);
                sayings.put(wiseSaying.getId(), wiseSaying);
                lastId = Math.max(lastId, wiseSaying.getId() + 1); // 마지막 ID 갱신
            } catch (IOException e) {
                System.err.println("파일 읽기 오류: " + file.getName() + " - " + e.getMessage());
            }
        }
    }
}

class WiseSaying {
    private int id;
    private String quote;
    private String author;

    public WiseSaying(String quote, String author, int id) {
        this.id = id;
        this.quote = quote;
        this.author = author;
    }

    public void display() {
        System.out.println(id + " / " + quote + " / " + author);
    }

    public int getId() {
        return id;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
