import Model.WiseSaying;
import com.google.gson.*;

import java.io.*;
import java.util.*;

public class App {
    private final Scanner scanner = new Scanner(System.in);
    private final Map<Integer, WiseSaying> sayings = new HashMap<>();
    private static final String DIRECTORY_PATH = "D:/Coding/wiseSaying/db/wiseSaying"; // 기본 경로
    private int lastId = 0; // ID 초기값

    public void run() {
        loadAllFiles();

        System.out.println("== 명언 앱 ==");
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
                    lastFileSave();
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
            deleteFile(id);
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
            saveFileAsJson(targetSaying);
        } catch (IOException e) {
            System.err.println("명언 수정 중 파일 저장 오류: " + e.getMessage());
        }
    }

    private void addSaying() {
        System.out.print("명언 : ");
        String quote = scanner.nextLine().trim();

        System.out.print("작가 : ");
        String author = scanner.nextLine().trim();

        WiseSaying newSaying = new WiseSaying(quote, author, lastId + 1);

        try {
            saveFileAsJson(newSaying);
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

    private void saveFileAsJson(WiseSaying wiseSaying) throws IOException {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("디렉토리 생성 실패");
        }

        String filePathJson = DIRECTORY_PATH + File.separator + wiseSaying.getId() + ".json";

        try (FileWriter writer = new FileWriter(filePathJson)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(wiseSaying, writer);
        }
    }

    private void lastFileSave() {
        // lastId와 동일한 객체를 가져옴
        WiseSaying targetSaying = sayings.get(lastId);
        if (targetSaying == null) {
            System.out.println("lastId에 해당하는 데이터가 존재하지 않습니다.");
            return;
        }

        // 텍스트 파일로 저장
        String filePathTxt = DIRECTORY_PATH + File.separator + lastId + ".txt";
        try (FileWriter writer = new FileWriter(filePathTxt)) {
            writer.write("id: " + targetSaying.getId() + "\n");
            writer.write("명언: " + targetSaying.getQuote() + "\n");
            writer.write("작가: " + targetSaying.getAuthor() + "\n");
            System.out.println("텍스트 파일 생성: " + filePathTxt);
        } catch (IOException e) {
            System.err.println("텍스트 파일 생성 중 오류: " + e.getMessage());
            return;
        }

        // 기존 JSON 파일 삭제
        System.out.println("삭제 대상 파일: " + lastId + ".json");
        deleteFile(lastId);

        System.out.println("lastId 데이터가 텍스트 파일로 변환되고 JSON 파일이 삭제되었습니다.");
    }


    private void deleteFile(int id) {
        String filePathJson = DIRECTORY_PATH + File.separator + id + ".json";
        File fileJson = new File(filePathJson);

        if (fileJson.exists() && fileJson.delete()) {
            System.out.println(id + "번 명언 파일(.json)이 삭제되었습니다.");
        }
    }

    private void loadAllFiles() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File directory = new File(DIRECTORY_PATH);

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json") || name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("저장된 명언이 없습니다.");
            return;
        }

        for (File file : files) {
            try {
                if (file.getName().endsWith(".json")) {
                    try (FileReader reader = new FileReader(file)) {
                        WiseSaying wiseSaying = gson.fromJson(reader, WiseSaying.class);
                        sayings.put(wiseSaying.getId(), wiseSaying);
                        lastId = Math.max(lastId, wiseSaying.getId()); // 마지막 ID 갱신
                    }
                } else if (file.getName().endsWith(".txt")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String idLine = reader.readLine();
                        String quoteLine = reader.readLine();
                        String authorLine = reader.readLine();

                        if (idLine == null || quoteLine == null || authorLine == null) {
                            System.err.println("텍스트 파일 형식이 잘못되었습니다: " + file.getName());
                            continue;
                        }

                        int id = Integer.parseInt(idLine.replace("id: ", "").trim());
                        String quote = quoteLine.replace("명언: ", "").trim();
                        String author = authorLine.replace("작가: ", "").trim();

                        WiseSaying wiseSaying = new WiseSaying(quote, author, id);
                        sayings.put(id, wiseSaying);
                        lastId = Math.max(lastId, id); // 마지막 ID 갱신
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("파일 처리 중 오류: " + file.getName() + " - " + e.getMessage());
            }
        }
    }
}
